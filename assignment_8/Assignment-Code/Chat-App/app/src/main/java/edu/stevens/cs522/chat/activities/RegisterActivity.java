/*********************************************************************

 Chat server: accept chat messages from clients.

 Sender chatName and GPS coordinates are encoded
 in the messages, and stripped off upon receipt.

 Copyright (c) 2017 Stevens Institute of Technology
 **********************************************************************/
package edu.stevens.cs522.chat.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import edu.stevens.cs522.base.DateUtils;
import edu.stevens.cs522.base.InetAddressUtils;
import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.viewmodels.ChatViewModel;
import edu.stevens.cs522.chat.viewmodels.PeerViewModel;

public class RegisterActivity extends FragmentActivity implements OnClickListener {

    final static public String TAG = RegisterActivity.class.getCanonicalName();

    protected PeerViewModel peerViewModel;

    protected Executor executor = Executors.newSingleThreadExecutor();

    protected Handler mainLoop = new Handler(Looper.getMainLooper());

    /*
     * Widgets for server Uri, chat name, register button.
     */
    private EditText userNameText;

    private Button registerButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        peerViewModel = new ViewModelProvider(this).get(PeerViewModel.class);

        setContentView(R.layout.register);

        userNameText = (EditText) findViewById(R.id.chat_name_text);

        registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);

    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    /*
     * Callback for the REGISTER button.
     */
    public void onClick(View v) {

        String userName = userNameText.getText().toString();

        if (userName.isEmpty()) {
            return;
        }

        if (!Settings.isRegistered(this)) {

            Log.d(TAG, "Registering as " + userName);

            final Peer peer = new Peer();

            peer.name = userName;

            peer.timestamp = DateUtils.now();

            peer.latitude = 44.523483;

            peer.longitude = -89.574814;

            peer.address = InetAddressUtils.fromString("127.0.0.1");

            peer.port = getResources().getInteger(R.integer.app_port);

            /*
             * Insert the peer record (on a background thread) and save the data about this peer.
             *
             * The key thing here is not to leak a reference to the activity to a background thread.
             */
            final Context context = getApplicationContext();

            executor.execute(() -> {

                long id = peerViewModel.insert(peer);
                Log.d(TAG, "Inserted peer record for this peer, id =" + id);

                mainLoop.post(() -> {
                    if (id < 0) {
                        Toast.makeText(context, R.string.already_taken, Toast.LENGTH_LONG).show();
                    } else {
                        Settings.register(context, peer.name, id);
                        Log.d(TAG, "Registered " + Settings.getSenderName(RegisterActivity.this));
                        Toast.makeText(context, R.string.register_success, Toast.LENGTH_LONG).show();
                    }
                });
            });

        } else {

            Log.d(TAG, "Already registered!");

            Toast.makeText(this, "Already Registered!", Toast.LENGTH_LONG).show();

        }
    }

}