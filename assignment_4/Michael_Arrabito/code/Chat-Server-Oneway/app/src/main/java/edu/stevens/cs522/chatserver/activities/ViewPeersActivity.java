package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Peer;


public class ViewPeersActivity extends FragmentActivity implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter peerAdapter;
    private ListView peersList;

    static final int LOADER_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peers);

        peerAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,
                null, new String[]{PeerContract.NAME}, new int[]{android.R.id.text1}, 0);

        peersList = this.findViewById(R.id.peer_list);
        peersList.setAdapter(peerAdapter);

        peersList.setOnItemClickListener(this);
        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = peerAdapter.getCursor();
        if (cursor.moveToPosition(position)) {
            Intent intent = new Intent(this, ViewPeerActivity.class);
            Peer peer = new Peer(cursor);
            intent.putExtra(ViewPeerActivity.PEER_KEY, peer);
            startActivity(intent);
        } else {
            throw new IllegalStateException("Unable to move to position in cursor: " + position);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ID:
                return new CursorLoader(this, PeerContract.CONTENT_URI,
                        null, null, null, null);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        this.peerAdapter.swapCursor(data);
        peerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.peerAdapter.swapCursor(null);
        peerAdapter.notifyDataSetChanged();
    }

}
