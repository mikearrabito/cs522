package edu.stevens.cs522.chatserver.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import edu.stevens.cs522.chatserver.databases.ChatDatabase;
import edu.stevens.cs522.chatserver.entities.Peer;

public class PeersViewModel extends AndroidViewModel {

    private ChatDatabase chatDatabase;

    private LiveData<List<Peer>> peers;

    public PeersViewModel(Application context) {
        super(context);
        chatDatabase = ChatDatabase.getInstance(context);
    }

    public LiveData<List<Peer>> getPeers() {
        if (peers == null) {
            initPeers();
        }
        return peers;
    }

    private void initPeers() {
        peers = chatDatabase.peerDao().fetchAllPeers();
    }

    @Override
    public void onCleared() {
        super.onCleared();
        if (chatDatabase != null && chatDatabase.isOpen()) {
            chatDatabase.close();
            chatDatabase = null;
        }
    }

}
