package edu.stevens.cs522.chatserver.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;

import org.w3c.dom.Text;

import java.util.List;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.databases.ChatDatabase;
import edu.stevens.cs522.chatserver.databases.MessageDAO;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends FragmentActivity {

    public static final String PEER_KEY = "peer";

    private ChatDatabase chatDatabase;

    private LiveData<List<Message>> messages;

    private MessageDAO messageDAO;
    private ListView messagesList;
    private ArrayAdapter<Message> messagesAdapter;

    private Peer peer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        peer = getIntent().getParcelableExtra(PEER_KEY);
        if (peer == null) {
            throw new IllegalArgumentException("Expected peer id as intent extra");
        }

        TextView username = findViewById(R.id.view_user_name);
        username.setText(peer.name);

        TextView timestamp = findViewById(R.id.view_timestamp);
        timestamp.setText(peer.timestamp.toString());

        TextView address = findViewById(R.id.view_address);
        address.setText(peer.address.toString());

        messagesList = findViewById(R.id.view_messages);
        messagesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        messagesList.setAdapter(messagesAdapter);

        chatDatabase = ChatDatabase.getInstance(getApplicationContext());
        messageDAO = chatDatabase.messageDao();
        messages = messageDAO.fetchMessagesFromPeer(peer.id);
        messages.observe(this, msgs -> {
            messagesAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    msgs);
            messagesList.setAdapter(messagesAdapter);
            messagesAdapter.notifyDataSetChanged();
        });

    }

}
