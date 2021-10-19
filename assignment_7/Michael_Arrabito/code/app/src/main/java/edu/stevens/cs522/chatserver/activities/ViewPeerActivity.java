package edu.stevens.cs522.chatserver.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;
import edu.stevens.cs522.chatserver.ui.TextAdapter;
import edu.stevens.cs522.chatserver.viewmodels.PeerViewModel;
import edu.stevens.cs522.chatserver.viewmodels.PeersViewModel;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        peer = getIntent().getParcelableExtra(PEER_KEY);
        if (peer == null) {
            throw new IllegalArgumentException("Expected peer id as intent extra");
        }

        TextView username = (TextView) findViewById(R.id.view_user_name);
        username.setText(peer.name);
        TextView timestamp = (TextView) findViewById(R.id.view_timestamp);
        timestamp.setText(peer.timestamp.toString());
        TextView address = (TextView) findViewById(R.id.view_address);
        address.setText(peer.address.toString());

        messageList = findViewById(R.id.view_messages);
        messageList.setLayoutManager(new LinearLayoutManager(this));

        peerViewModel = new ViewModelProvider(this).get(PeerViewModel.class);
        peerViewModel.setCurrentPeer(peer);

        messagesAdapter = new TextAdapter<>(messageList);
        messageList.setAdapter(messagesAdapter);

        messages = peerViewModel.getMessages();
        messages.observe(this, msgs -> {
            messagesAdapter.setDataset(msgs);
            messagesAdapter.notifyDataSetChanged();
        });
    }
}
