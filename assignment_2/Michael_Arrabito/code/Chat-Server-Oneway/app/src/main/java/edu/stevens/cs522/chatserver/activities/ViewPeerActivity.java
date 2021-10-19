package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.entities.Peer;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends Activity {

    private TextView viewUsername;
    private TextView viewTimestamp;
    private TextView viewAddress;
    private TextView viewPort;

    public static final String PEER_KEY = "peer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        Peer peer = getIntent().getParcelableExtra(PEER_KEY);
        if (peer == null) {
            throw new IllegalArgumentException("Expected peer as intent extra");
        }

        viewUsername = this.findViewById(R.id.view_user_name);
        viewTimestamp = this.findViewById(R.id.view_timestamp);
        viewAddress = this.findViewById(R.id.view_address);
        viewPort = this.findViewById(R.id.view_port);

        viewUsername.setText(peer.name);
        viewTimestamp.setText(peer.timestamp.toString());
        viewAddress.setText(peer.address.toString());
        viewPort.setText(String.valueOf(peer.port));
    }

}
