package edu.stevens.cs522.chat.databases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import edu.stevens.cs522.chat.entities.DateConverter;
import edu.stevens.cs522.chat.entities.InetAddressConverter;
import edu.stevens.cs522.chat.entities.Message;
import edu.stevens.cs522.chat.entities.Peer;

/**
 * Created by dduggan.
 * <p>
 * See build.gradle file for app for where schema file is left after processing.
 */

@Database(entities = {Message.class, Peer.class}, version = 1)
@TypeConverters({DateConverter.class, InetAddressConverter.class})
public abstract class ChatDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "messages.db";

    private static ChatDatabase instance;

    public abstract PeerDAO peerDao();

    public abstract MessageDAO messageDao();

    public abstract RequestDAO requestDao();

    public static ChatDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, ChatDatabase.class, DATABASE_NAME).build();
        }
        return instance;
    }

}