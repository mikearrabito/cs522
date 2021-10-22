/*********************************************************************

 Chat server: accept chat messages from clients.

 Sender name and GPS coordinates are encoded
 in the messages, and stripped off upon receipt.

 Copyright (c) 2017 Stevens Institute of Technology
 **********************************************************************/
package edu.stevens.cs522.chat.activities;

import android.content.ComponentName;
import android.content.Context;
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

    private ChatViewModel chatViewModel;

    private LiveData<List<Message>> messages;

    private RecyclerView messageList;

    private MessageAdapter messagesAdapter;

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
            Log.d(TAG, "Updating messages");
            messagesAdapter.setMessages(messages);
            messagesAdapter.notifyDataSetChanged();
        });

        sendButton = findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);

        destinationHost = findViewById(R.id.destination_host);
        destinationPort = findViewById(R.id.destination_port);

        Intent chatServiceIntent = new Intent(this, ChatService.class);
        bindService(chatServiceIntent, this, Context.BIND_AUTO_CREATE);

        sendResultReceiver = new ResultReceiverWrapper(new Handler());
    }

    public void onResume() {
        super.onResume();
        senderName.setText(Settings.getSenderName(this));
        sendResultReceiver.setReceiver(this);
    }

    public void onPause() {
        super.onPause();
        sendResultReceiver.setReceiver(null);
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
            Toast.makeText(this, R.string.register_necessary, Toast.LENGTH_LONG).show();
            return;
        }

        if (chatService != null) {
            /*
             * On the emulator, which does not support WIFI stack, we'll send to
             * (an AVD alias for) the host loopback interface, with the server
             * port on the host redirected to the server port on the server AVD.
             */

            String destAddrString = destinationHost.getText().toString();

            String destPortString = destinationPort.getText().toString();

            String chatRoom = "_default";

            String text = messageText.getText().toString();

            Date timestamp = DateUtils.now();

            Double latitude = 44.523483;

            Double longitude = -89.574814;

            if (destAddrString.isEmpty()) {
                Toast.makeText(this, "Enter an address", Toast.LENGTH_LONG).show();
                return;
            }
            InetAddress destAddr = InetAddressUtils.fromString(destAddrString);

            if (destPortString.isEmpty()) {
                Toast.makeText(this, "Enter a port", Toast.LENGTH_LONG).show();
                return;
            }
            int destPort = Integer.parseInt(destPortString);

            if (text.isEmpty()) {
                Toast.makeText(this, "Enter a message", Toast.LENGTH_LONG).show();
                return;
            }

            chatService.send(destAddr, destPort, chatRoom, text,
                    timestamp, latitude, longitude, sendResultReceiver);

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
                Toast.makeText(this, "Message sent", Toast.LENGTH_LONG).show();
                break;
            case RESULT_CANCELED:
                Toast.makeText(this, "Could not send message", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "Connected to the chat service.");
        ChatService.ChatBinder binder = (ChatService.ChatBinder) service;
        chatService = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "Disconnected from the chat service.");
        chatService = null;
    }
}