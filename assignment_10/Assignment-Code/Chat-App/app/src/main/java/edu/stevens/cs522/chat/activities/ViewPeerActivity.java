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
import edu.stevens.cs522.chat.databases.ChatDatabase;
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
    private TextAdapter<Message> messageAdapter;
    private RecyclerView messageList;
    private Peer peer;
    private TextView viewUserName;
    private TextView viewTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        peer = getIntent().getParcelableExtra(PEER_KEY);
        if (peer == null) {
            throw new IllegalArgumentException("Expected peer id as intent extra");
        }

        viewUserName = findViewById(R.id.view_user_name);
        viewTimestamp = findViewById(R.id.view_timestamp);

        viewUserName.setText(peer.name);
        viewTimestamp.setText(peer.timestamp.toString());

        // Initialize the recyclerview and adapter for messages
        messageList = findViewById(R.id.view_messages);
        messageList.setLayoutManager(new LinearLayoutManager(this));

        messageAdapter = new TextAdapter<>(messageList);
        messageList.setAdapter(messageAdapter);

        peerViewModel = new ViewModelProvider(this).get(PeerViewModel.class);

        messages = peerViewModel.fetchMessagesFromPeer(peer);
        messages.observe(this, (msgs) -> {
            messageAdapter.setDataset(msgs);
            messageAdapter.notifyDataSetChanged();
        });
    }

}
