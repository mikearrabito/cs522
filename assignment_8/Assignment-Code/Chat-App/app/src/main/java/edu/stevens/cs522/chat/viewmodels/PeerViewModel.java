package edu.stevens.cs522.chat.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import edu.stevens.cs522.chat.databases.ChatDatabase;
import edu.stevens.cs522.chat.entities.Message;
import edu.stevens.cs522.chat.entities.Peer;

public class PeerViewModel extends AndroidViewModel {

    private ChatDatabase chatDatabase;

    private LiveData<List<Message>> messages;

    private Peer currentPeer;

    public PeerViewModel(Application context) {
        super(context);
        chatDatabase = ChatDatabase.getInstance(context);
    }

    public void setCurrentPeer(Peer currentPeer) {
        this.currentPeer = currentPeer;
    }

    public LiveData<List<Message>> getMessages() {
        if (messages == null) {
            messages = chatDatabase.messageDao().fetchMessagesFromPeer(currentPeer.id);
        }
        return messages;
    }

    public long insert(Peer peer) {
        return chatDatabase.peerDao().insert(peer);
    }

}
