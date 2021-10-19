package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.Date;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.databases.ChatDbAdapter;
import edu.stevens.cs522.chatserver.entities.Peer;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends Activity {

    public static final String PEER_KEY = "peer";

    private ChatDbAdapter chatDbAdapter;
    private Cursor peerMessagesCursor;
    private SimpleCursorAdapter peerMessagesAdapter;

    private ListView messageList;
    private TextView usernameTextView;
    private TextView addressTextView;
    private TextView timestampTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        Peer peer = getIntent().getParcelableExtra(PEER_KEY);
        if (peer == null) {
            throw new IllegalArgumentException("Expected peer id as intent extra");
        }

        usernameTextView = this.findViewById(R.id.view_user_name);
        addressTextView = this.findViewById(R.id.view_address);
        timestampTextView = this.findViewById(R.id.view_timestamp);

        usernameTextView.setText(peer.name);
        addressTextView.setText(peer.address.toString());
        timestampTextView.setText(peer.timestamp.toString());

        chatDbAdapter = new ChatDbAdapter(this);
        chatDbAdapter.open();

        peerMessagesCursor = chatDbAdapter.fetchMessagesFromPeer(peer);
        String[] from = new String[]{MessageContract.TIMESTAMP, MessageContract.MESSAGE_TEXT};
        int[] to = new int[]{R.id.message_timestamp, R.id.message_text};

        peerMessagesAdapter = new SimpleCursorAdapter(this, R.layout.message_from_peer, peerMessagesCursor, from, to) {
            @Override
            public void setViewText(TextView v, String text) {
                super.setViewText(v, convText(v, text));
            }
        };

        messageList = this.findViewById(R.id.view_messages);
        messageList.setAdapter(peerMessagesAdapter);
        peerMessagesAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        peerMessagesCursor.requery();
        peerMessagesAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatDbAdapter.close();
    }


    private String convText(TextView v, String text) {
        switch (v.getId()) {
            case R.id.message_timestamp:
                Date date = new Date(Long.valueOf(text));
                return date.toString();
        }
        return text;
    }
}
