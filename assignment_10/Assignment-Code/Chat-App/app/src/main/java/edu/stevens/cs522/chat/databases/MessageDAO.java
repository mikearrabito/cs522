package edu.stevens.cs522.chat.databases;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import edu.stevens.cs522.chat.entities.Message;
import edu.stevens.cs522.chat.entities.Peer;

@Dao
public abstract class MessageDAO {
    @Query("SELECT * FROM Message")
    public abstract LiveData<List<Message>> fetchAllMessages();

    @Query("SELECT * FROM Message WHERE senderId=:peerId")
    public abstract LiveData<List<Message>> fetchMessagesFromPeer(long peerId);

    @Insert
    public abstract void persist(Message message);

}
