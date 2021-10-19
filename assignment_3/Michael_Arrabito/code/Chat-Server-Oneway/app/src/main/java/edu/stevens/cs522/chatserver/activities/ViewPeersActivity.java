package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.List;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.databases.ChatDbAdapter;
import edu.stevens.cs522.chatserver.entities.Peer;


public class ViewPeersActivity extends Activity implements AdapterView.OnItemClickListener {

    /*
     * TODO See ChatServer for example of what to do, query peers database instead of messages database.
     */

    private ChatDbAdapter chatDbAdapter;

    private SimpleCursorAdapter peerAdapter;
    private ListView peersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peers);

        // TODO initialize peerAdapter with result of DB query
        chatDbAdapter = new ChatDbAdapter(this);
        chatDbAdapter.open();

        Cursor cursor = chatDbAdapter.fetchAllPeers();

        String[] from = new String[]{PeerContract.NAME};
        int[] to = new int[]{R.id.peer_name};

        peerAdapter = new SimpleCursorAdapter(this, R.layout.peer, cursor, from, to);
        peersList = this.findViewById(R.id.peer_list);
        peersList.setAdapter(peerAdapter);
        peerAdapter.notifyDataSetChanged();
        peersList.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
         * Clicking on a peer brings up details
         */
        Peer peer = chatDbAdapter.fetchPeer(id);
        System.out.println(peer.id);
        System.out.println(peer.name);
        System.out.println(peer.address);

        Intent intent = new Intent(this, ViewPeerActivity.class);
        intent.putExtra(ViewPeerActivity.PEER_KEY, peer);
        startActivity(intent);
    }
}
