package edu.stevens.cs522.chat.databases;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import edu.stevens.cs522.chat.entities.Message;

@Dao
public interface MessageDAO {

    @Query("select * from Message")
    public LiveData<List<Message>> fetchAllMessages();

    @Query("select * from Message where senderId=:peerId")
    public LiveData<List<Message>> fetchMessagesFromPeer(long peerId);

    @Insert
    public void persist(Message message);
}
