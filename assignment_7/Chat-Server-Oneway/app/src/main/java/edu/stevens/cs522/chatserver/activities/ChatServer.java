/*********************************************************************

 Chat server: accept chat messages from clients.

 Sender name and GPS coordinates are encoded
 in the messages, and stripped off upon receipt.

 Copyright (c) 2017 Stevens Institute of Technology
 **********************************************************************/
package edu.stevens.cs522.chatserver.activities;

import android.content.Intent;
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

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.stevens.cs522.base.DatagramSendReceive;
import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.databases.ChatDatabase;
import edu.stevens.cs522.chatserver.databases.MessageDAO;
import edu.stevens.cs522.chatserver.databases.PeerDAO;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;
import edu.stevens.cs522.chatserver.ui.MessageAdapter;
import edu.stevens.cs522.chatserver.viewmodels.ChatViewModel;

public class ChatServer extends FragmentActivity implements OnClickListener {

    final static public String TAG = ChatServer.class.getCanonicalName();

    public final static String SENDER_NAME = "name";

    public final static String CHATROOM = "room";

    public final static String MESSAGE_TEXT = "text";

    public final static String TIMESTAMP = "timestamp";

    public final static String LATITUDE = "latitude";

    public final static String LONGITUDE = "longitude";

    /*
     * Socket used both for sending and receiving.
     *
     * This should also be in a view model!
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
    private ChatViewModel chatViewModel;

    private LiveData<List<Message>> messages;

    private RecyclerView messageList;

    private MessageAdapter messagesAdapter;

    private Button next;

    /*
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "(Re)starting ChatServer activity....");

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
        messageList = findViewById(R.id.message_list);
        messageList.setLayoutManager(new LinearLayoutManager(this));

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        messages = chatViewModel.fetchAllMessages();

        messagesAdapter = new MessageAdapter();
        messageList.setAdapter(messagesAdapter);

        messages.observe(this, msgList -> {
            Log.d(TAG, "updating message list");
            messagesAdapter.setMessages(msgList);
            messagesAdapter.notifyDataSetChanged();
        });

        next = findViewById(R.id.next);
        next.setOnClickListener(this);
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

            /*
             * Add the sender to our list of senders
             */
            Peer peer = new Peer();
            peer.name = sender;
            peer.address = address;
            peer.port = port;
            peer.timestamp = timestamp;
            peer.latitude = latitude;
            peer.longitude = longitude;

            Message message = new Message();
            message.messageText = text;
            message.chatRoom = room;
            message.sender = sender;
            message.timestamp = timestamp;
            message.latitude = latitude;
            message.longitude = longitude;

            message.senderId = chatViewModel.upsert(peer);
            chatViewModel.persist(message);
        } catch (Exception e) {

            Log.e(TAG, "Problems receiving packet: " + e.getMessage(), e);
            socketOK = false;
        }

    }

    /*
     * Close the socket before exiting application
     */
    public void closeSocket() {
        if (serverSocket != null) {
            serverSocket.close();
            serverSocket = null;
        }
    }

    /*
     * If the socket is OK, then it's running
     */
    boolean socketIsOK() {
        return socketOK;
    }

    public void onDestroy() {
        super.onDestroy();
        closeSocket();
        Log.i(TAG, "Leaving ChatServer activity....");
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
                break;

            default:
        }
        return false;
    }

}