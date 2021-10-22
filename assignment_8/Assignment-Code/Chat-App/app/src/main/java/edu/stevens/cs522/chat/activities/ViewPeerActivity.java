package edu.stevens.cs522.chat.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.entities.Message;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.ui.TextAdapter;
import edu.stevens.cs522.chat.viewmodels.PeerViewModel;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends FragmentActivity {

    public static final String TAG = ViewPeerActivity.class.getCanonicalName();

    public static final String PEER_KEY = "peer";

    private PeerViewModel peerViewModel;

    private LiveData<List<Message>> messages;

    private TextAdapter<Message> messagesAdapter;

    private RecyclerView messageList;

    private Peer peer;

    private TextView username;
    private TextView address;
    private TextView timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        peer = getIntent().getParcelableExtra(PEER_KEY);
        if (peer == null) {
            throw new IllegalArgumentException("Expected peer id as intent extra");
        }

        username = findViewById(R.id.view_user_name);
        address = findViewById(R.id.view_address);
        timestamp = findViewById(R.id.view_address);

        username.setText(peer.name);
        address.setText(peer.address.toString());
        timestamp.setText(peer.timestamp.toString());

        // Initialize the recyclerview and adapter for messages
        messageList = findViewById(R.id.view_messages);
        messageList.setLayoutManager(new LinearLayoutManager(this));

        messagesAdapter = new TextAdapter<>(messageList);
        messageList.setAdapter(messagesAdapter);

        peerViewModel = new ViewModelProvider(this).get(PeerViewModel.class);
        peerViewModel.setCurrentPeer(peer);

        messages = peerViewModel.getMessages();
        messages.observe(this, messages -> {
            messagesAdapter.setDataset(messages);
            messagesAdapter.notifyDataSetChanged();
        });
    }

}
