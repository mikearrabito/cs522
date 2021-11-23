package edu.stevens.cs522.chat.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import edu.stevens.cs522.chat.databases.ChatDatabase;
import edu.stevens.cs522.chat.entities.Message;
import edu.stevens.cs522.chat.entities.Peer;

public class ChatViewModel extends AndroidViewModel {

    public static final String TAG = ChatViewModel.class.getCanonicalName();

    private ChatDatabase chatDatabase;

    private LiveData<List<Message>> messages;

    public ChatViewModel(Application context) {
        super(context);
        Log.i(TAG, "Getting database in ChatViewModel....");
        chatDatabase = ChatDatabase.getInstance(context);
    }

    public LiveData<List<Message>> fetchAllMessages() {
        if (messages == null) {
            messages = loadMessages();
        }
        return messages;
    }

    private LiveData<List<Message>> loadMessages() {
        return chatDatabase.messageDao().fetchAllMessages();
    }

    @Override
    public void onCleared() {
        super.onCleared();
        Log.i(TAG, "Clearing ChatViewModel....");
        chatDatabase = null;
    }
}
