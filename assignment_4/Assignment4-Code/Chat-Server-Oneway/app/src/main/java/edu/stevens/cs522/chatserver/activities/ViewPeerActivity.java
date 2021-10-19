package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Peer;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String PEER_KEY = "peer";

    private SimpleCursorAdapter messageAdapter;
    private ListView messages;
    private Peer peer;

    private static final int MESSAGE_LOADER_ID = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        peer = getIntent().getParcelableExtra(PEER_KEY);
        if (peer == null) {
            throw new IllegalArgumentException("Expected peer as intent extra");
        }

        TextView peerName = findViewById(R.id.view_user_name);
        peerName.setText(peer.name);

        TextView timestamp = findViewById(R.id.view_timestamp);
        timestamp.setText(peer.timestamp.toString());

        TextView address = findViewById(R.id.view_address);
        address.setText(peer.address.toString());

        messageAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,
                null, new String[]{MessageContract.MESSAGE_TEXT}, new int[]{android.R.id.text1}, 0);

        messages = this.findViewById(R.id.view_messages);
        messages.setAdapter(messageAdapter);

        LoaderManager lm = LoaderManager.getInstance(this);

        lm.initLoader(MESSAGE_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case MESSAGE_LOADER_ID:
                String msgSelection = new String(MessageContract.SENDER_ID + "=?");
                String[] msgSelectionArgs = new String[]{String.valueOf(this.peer.id)};
                return new CursorLoader(this, MessageContract.CONTENT_URI, null,
                        msgSelection, msgSelectionArgs, null);

            default:
                throw new IllegalStateException("Unrecognized loader id");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case MESSAGE_LOADER_ID:
                this.messageAdapter.swapCursor(data);
                messageAdapter.notifyDataSetChanged();
                break;
            default:
                throw new IllegalStateException("Unrecognized loader id");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case MESSAGE_LOADER_ID:
                this.messageAdapter.swapCursor(null);
                messageAdapter.notifyDataSetChanged();
                break;
            default:
                throw new IllegalStateException("Unrecognized loader id");
        }
    }

}
