package edu.stevens.cs522.chat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.ui.TextAdapter;
import edu.stevens.cs522.chat.viewmodels.PeersViewModel;


public class ViewPeersActivity extends FragmentActivity implements TextAdapter.OnItemClickListener {

    private PeersViewModel peersViewModel;

    private LiveData<List<Peer>> peers;

    private TextAdapter<Peer> peerAdapter;

    private RecyclerView peersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peers);

        // Initialize the recyclerview and adapter for peers
        peersList = findViewById(R.id.peer_list);
        peersList.setLayoutManager(new LinearLayoutManager(this));

        peerAdapter = new TextAdapter<>(peersList, this);
        peersList.setAdapter(peerAdapter);

        peersViewModel = new ViewModelProvider(this).get(PeersViewModel.class);

        peers = peersViewModel.getPeers();

        peers.observe(this, peers -> {
            peerAdapter.setDataset(peers);
            peerAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /*
     * Callback interface defined in TextAdapter, for responding to clicks on rows.
     */
    @Override
    public void onItemClick(RecyclerView parent, View view, int position) {
        /*
         * Clicking on a peer brings up details
         */
        List<Peer> ps = peers.getValue();
        if (ps != null) {
            Peer peer = ps.get(position);

            Intent intent = new Intent(this, ViewPeerActivity.class);
            intent.putExtra(ViewPeerActivity.PEER_KEY, peer);
            startActivity(intent);
        }


    }
}
