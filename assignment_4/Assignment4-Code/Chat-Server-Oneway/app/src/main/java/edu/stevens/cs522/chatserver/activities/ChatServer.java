/*********************************************************************

 Chat server: accept chat messages from clients.

 Sender name and GPS coordinates are encoded
 in the messages, and stripped off upon receipt.

 Copyright (c) 2017 Stevens Institute of Technology
 **********************************************************************/
package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;

import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Date;

import edu.stevens.cs522.base.DatagramSendReceive;
import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;
import edu.stevens.cs522.chatserver.providers.ChatProvider;

public class ChatServer extends FragmentActivity implements OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    final static public String TAG = ChatServer.class.getCanonicalName();

    public final static String SENDER_NAME = "name";

    public final static String CHATROOM = "room";

    public final static String MESSAGE_TEXT = "text";

    public final static String TIMESTAMP = "timestamp";

    public final static String LATITUDE = "latitude";

    public final static String LONGITUDE = "longitude";

    /*
     * Socket used both for sending and receiving
     */
    private DatagramSendReceive serverSocket;
//  private DatagramSocket serverSocket;

    /*
     * True as long as we don't get socket errors
     */
    private boolean socketOK = true;

    /*
     * UI for displayed received messages
     */
    private ListView messageList;

    private SimpleCursorAdapter messagesAdapter;

    private Button next;

    static final private int LOADER_ID = 1;

    /*
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Let's be clear, this is a HACK to allow you to do network communication on the messages thread.
         * This WILL cause an ANR, and is only provided to simplify the pedagogy.  We will see how to do
         * this right in a future assignment (using a Service managing background threads).
         */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            /*
             * Get port information from the resources.
             */
            int port = getResources().getInteger(R.integer.app_port);

            // serverSocket = new DatagramSocket(port);

            serverSocket = new DatagramSendReceive(port);

        } catch (Exception e) {
            throw new IllegalStateException("Cannot open socket", e);
        }

        setContentView(R.layout.messages);

        String[] from = new String[]{MessageContract.SENDER, MessageContract.MESSAGE_TEXT};
        int[] to = new int[]{android.R.id.text1, android.R.id.text2};
        messagesAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2,
                null, from, to, 0);

        messageList = this.findViewById(R.id.message_list);
        messageList.setAdapter(messagesAdapter);

        next = this.findViewById(R.id.next);
        next.setOnClickListener(this);

        // Use loader manager to initiate a query of the database
        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);
    }


    public void onClick(View v) {

        byte[] receiveData = new byte[1024];

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        try {

            serverSocket.receive(receivePacket);
            Log.d(TAG, "Received a packet");

            InetAddress address = receivePacket.getAddress();
            int port = receivePacket.getPort();
            Log.d(TAG, "Source IP Address: " + address + " , Port: " + port);

            String content = new String(receivePacket.getData(), 0, receivePacket.getLength());
            Log.d(TAG, "Message received: " + content);

            /*
             * Parse the JSON object
             */
            String sender = null;

            String room = null;

            String text = null;

            Date timestamp = null;

            Double latitude = null;

            Double longitude = null;

            JsonReader rd = new JsonReader(new StringReader(content));

            rd.beginObject();
            if (SENDER_NAME.equals(rd.nextName())) {
                sender = rd.nextString();
            }
            if (CHATROOM.equals(rd.nextName())) {
                room = rd.nextString();
            }
            if (MESSAGE_TEXT.equals((rd.nextName()))) {
                text = rd.nextString();
            }
            if (TIMESTAMP.equals(rd.nextName())) {
                timestamp = new Date(rd.nextLong());
            }
            if (LATITUDE.equals(rd.nextName())) {
                latitude = rd.nextDouble();
            }
            if (LONGITUDE.equals((rd.nextName()))) {
                longitude = rd.nextDouble();
            }
            rd.endObject();

            rd.close();
            
            Peer peer = new Peer();
            peer.name = sender;
            peer.address = address;
            peer.port = port;
            peer.timestamp = timestamp;
            peer.latitude = latitude;
            peer.longitude = longitude;

            final Message message = new Message();
            message.messageText = text;
            message.chatRoom = room;
            message.sender = sender;
            message.timestamp = timestamp;
            message.latitude = latitude;
            message.longitude = longitude;

            ContentResolver resolver = getContentResolver();

            ContentValues peerValues = new ContentValues();
            peer.writeToProvider(peerValues);
            Uri peerUri = resolver.insert(PeerContract.CONTENT_URI, peerValues);

            message.senderId = PeerContract.getId(peerUri);

            ContentValues messageValues = new ContentValues();
            message.writeToProvider(messageValues);
            resolver.insert(MessageContract.CONTENT_URI, messageValues);

        } catch (Exception e) {
            Log.e(TAG, "Problems receiving packet: " + e.getMessage(), e);
            socketOK = false;
        }

    }

    public void closeSocket() {
        if (serverSocket != null) {
            serverSocket.close();
            serverSocket = null;
        }
    }

    boolean socketIsOK() {
        return socketOK;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ID:
                return new CursorLoader(this, MessageContract.CONTENT_URI, null, null, null, null);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        this.messagesAdapter.swapCursor(data);
        messagesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.messagesAdapter.swapCursor(null);
        messagesAdapter.notifyDataSetChanged();
    }

    public void onDestroy() {
        super.onDestroy();
        closeSocket();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.chatserver_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.peers:
                Intent intent = new Intent(this, ViewPeersActivity.class);
                startActivity(intent);
                return true;

            default:
        }
        return false;
    }


}