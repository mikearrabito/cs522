/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender name and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package edu.stevens.cs522.chat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.entities.Message;
import edu.stevens.cs522.chat.rest.ChatHelper;
import edu.stevens.cs522.chat.services.ResultReceiverWrapper;
import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.ui.MessageAdapter;
import edu.stevens.cs522.chat.viewmodels.ChatViewModel;

public class ChatActivity extends FragmentActivity implements OnClickListener, ResultReceiverWrapper.IReceive {

	final static public String TAG = ChatActivity.class.getCanonicalName();

    private ChatHelper chatHelper;
    private ChatViewModel chatViewModel;
    private MessageAdapter messagesAdapter;
    private TextView senderName;
    private EditText messageText;
    private LiveData<List<Message>> messages;
    private ResultReceiverWrapper sendResultReceiver;
    private Button sendButton;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.messages);

        /*
         * Widgets for message text, list of messages
         */
        RecyclerView messageList = findViewById(R.id.message_list);
        messageList.setLayoutManager(new LinearLayoutManager(this));

        messageText = findViewById(R.id.message_text);

        senderName = findViewById(R.id.sender_name);

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

        chatHelper = new ChatHelper(this);
        sendResultReceiver = new ResultReceiverWrapper(new Handler());

        chatHelper.startMessageSync();
        /*
         * Initialize settings to default values.
         */
        if (!Settings.isRegistered(this)) {
            Settings.getAppId(this);
            // Registration must be done manually
        }

    }

	public void onResume() {
        super.onResume();
        senderName.setText(Settings.getChatName(this));
        sendResultReceiver.setReceiver(this);
    }

    public void onPause() {
        super.onPause();
        sendResultReceiver.setReceiver(null);
    }

    public void onDestroy() {
        super.onDestroy();
        chatHelper.stopMessageSync();
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
        switch(item.getItemId()) {
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

        if (chatHelper != null) {
            String chatRoom = getString(R.string.default_chat_room);
            String text = messageText.getText().toString();
            chatHelper.postMessage(chatRoom,text,sendResultReceiver);
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
            default:
                Toast.makeText(this, "Could not send message", Toast.LENGTH_LONG).show();
                break;
        }
    }

}