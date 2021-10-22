/*********************************************************************

 Chat server: accept chat messages from clients.

 Sender name and GPS coordinates are encoded
 in the messages, and stripped off upon receipt.

 Copyright (c) 2017 Stevens Institute of Technology
 **********************************************************************/
package edu.stevens.cs522.chat.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.net.InetAddress;
import java.util.Date;
import java.util.List;

import edu.stevens.cs522.base.DateUtils;
import edu.stevens.cs522.base.InetAddressUtils;
import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.databases.ChatDatabase;
import edu.stevens.cs522.chat.databases.MessageDAO;
import edu.stevens.cs522.chat.databases.PeerDAO;
import edu.stevens.cs522.chat.entities.Message;
import edu.stevens.cs522.chat.services.ChatService;
import edu.stevens.cs522.chat.services.IChatService;
import edu.stevens.cs522.chat.services.ResultReceiverWrapper;
import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.ui.MessageAdapter;
import edu.stevens.cs522.chat.viewmodels.ChatViewModel;

public class ChatActivity extends FragmentActivity implements OnClickListener, ServiceConnection, ResultReceiverWrapper.IReceive {
    final static public String TAG = ChatActivity.class.getCanonicalName();

    /*
     * UI for displayed received messages
     */
    private ChatViewModel chatViewModel;

    private LiveData<List<Message>> messages;

    private RecyclerView messageList;

    private MessageAdapter messagesAdapter;

    /*
     * Widgets for dest address, message text, send button.
     */
    private EditText destinationHost;

    private EditText destinationPort;

    private TextView senderName;

    private EditText messageText;

    private Button sendButton;


    /*
     * Reference to the service, for sending a message
     */
    private IChatService chatService;

    /*
     * For receiving ack when message is sent.
     */
    private ResultReceiverWrapper sendResultReceiver;

    /*
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.messages);
        messageList = findViewById(R.id.message_list);
        messageList.setLayoutManager(new LinearLayoutManager(this));

        destinationHost = findViewById(R.id.destination_host);
        destinationPort = findViewById(R.id.destination_port);
        senderName = findViewById(R.id.sender_name);
        messageText = findViewById(R.id.message_text);

        messagesAdapter = new MessageAdapter();
        messageList.setAdapter(messagesAdapter);

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        messages = chatViewModel.fetchAllMessages();
        messages.observe(this, messages -> {
            messagesAdapter.setMessages(messages);
            messagesAdapter.notifyDataSetChanged();
        });

        sendButton = findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);

        // TODO initiate binding to the service


        // TODO initialize sendResultReceiver (for receiving notification of message sent)

    }

    public void onResume() {
        super.onResume();
        senderName.setText(Settings.getSenderName(this));
        // TODO register result receiver
    }

    public void onPause() {
        super.onPause();
        // TODO unregister result receiver
    }

    public void onDestroy() {
        super.onDestroy();
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

            case R.id.register:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;

            case R.id.peers:
                Intent peersIntent = new Intent(this, ViewPeersActivity.class);
                startActivity(peersIntent);
                break;

            default:
        }
        return false;
    }


    /*
     * Callback for the SEND button.
     */
    public void onClick(View v) {

        if (!Settings.isRegistered(this)) {

            Toast.makeText(this, R.string.register_necessary, Toast.LENGTH_LONG);
            return;

        }

        if (chatService != null) {
            /*
             * On the emulator, which does not support WIFI stack, we'll send to
             * (an AVD alias for) the host loopback interface, with the server
             * port on the host redirected to the server port on the server AVD.
             */

            String destAddrString = null;

            String destPortString = null;

            String chatRoom = "_default";

            String text = null;

            Date timestamp = DateUtils.now();

            Double latitude = 44.523483;

            Double longitude = -89.574814;

            // TODO Get destination host and port and message from UI.


            if (destAddrString.isEmpty()) {
                return;
            }
            InetAddress destAddr = InetAddressUtils.fromString(destAddrString);

            if (destPortString.isEmpty()) {
                return;
            }
            int destPort = Integer.parseInt(destPortString);

            if (text.isEmpty()) {
                return;
            }


            // TODO use chatService to send the message

            Log.i(TAG, "Sent message: " + text);

            messageText.setText("");
        }
    }

    @Override
    /**
     * Show a text message when notified that sending a message succeeded or failed
     */
    public void onReceiveResult(int resultCode, Bundle data) {
        switch (resultCode) {
            case RESULT_OK:
                // TODO show a success toast message
                break;
            default:
                // TODO show a failure toast message
                break;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "Connected to the chat service.");
        // TODO initialize chatService


    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "Disconnected from the chat service.");
        chatService = null;
    }
}