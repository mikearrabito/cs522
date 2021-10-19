package edu.stevens.cs522.chatserver.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;

import java.util.List;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.databases.ChatDatabase;
import edu.stevens.cs522.chatserver.databases.PeerDAO;
import edu.stevens.cs522.chatserver.entities.Peer;


public class ViewPeersActivity extends FragmentActivity implements AdapterView.OnItemClickListener {

    private ChatDatabase chatDatabase;
    private PeerDAO peerDao;
    private LiveData<List<Peer>> peers;
    private ListView peersList;
    private ArrayAdapter<Peer> peersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peers);

        peersList = findViewById(R.id.peer_list);
        peersList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1));
        peersList.setOnItemClickListener(this);

        chatDatabase = ChatDatabase.getInstance(getApplicationContext());
        peerDao = chatDatabase.peerDao();
        peers = peerDao.fetchAllPeers();

        peers.observe(this, peers -> {
            this.peersAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    peers);
            peersList.setAdapter(peersAdapter);
            peersAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatDatabase.isOpen()) {
            chatDatabase.close();
            chatDatabase = null;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Peer peer = peers.getValue().get(position);

        Intent intent = new Intent(this, ViewPeerActivity.class);
        intent.putExtra(ViewPeerActivity.PEER_KEY, peer);
        startActivity(intent);

    }
}
