package edu.stevens.cs522.chatserver.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.Date;

import edu.stevens.cs522.base.DateUtils;

/**
 * Created by dduggan.
 */

public class MessageContract implements BaseColumns {

    public static final String CHAT_ROOM = "chat_room";

    public static final String MESSAGE_TEXT = "message_text";

    public static final String TIMESTAMP = "timestamp";

    public static final String LATITUDE = "latitude";

    public static final String LONGITUDE = "longitude";

    public static final String SENDER = "sender";

    public static final String SENDER_ID = "sender_id";


    // TODO remaining columns in Messages table

    // TODO remaining getter and putter operations for other columns

    private static int idColumn = -1;

    public static long getId(Cursor cursor) {
        if (idColumn < 0) {
            idColumn = cursor.getColumnIndexOrThrow(_ID);
        }
        return cursor.getLong(idColumn);
    }

    public static void putId(ContentValues out, long id) {
        if (id > 0) {
            out.put(_ID, id);
        }
    }

    private static int messageTextColumn = -1;

    public static String getMessageText(Cursor cursor) {
        if (messageTextColumn < 0) {
            messageTextColumn = cursor.getColumnIndexOrThrow(MESSAGE_TEXT);
        }
        return cursor.getString(messageTextColumn);
    }

    public static void putMessageText(ContentValues out, String messageText) {
        out.put(MESSAGE_TEXT, messageText);
    }

    // TODO remaining getter and putter operations for other columns

    private static int chatRoomColumn = -1;

    public static String getChatRoom(Cursor cursor) {
        if (chatRoomColumn < 0) {
            chatRoomColumn = cursor.getColumnIndexOrThrow(CHAT_ROOM);
        }
        return cursor.getString(chatRoomColumn);
    }

    public static void putChatRoom(ContentValues out, String messageText) {
        out.put(CHAT_ROOM, messageText);
    }

    private static int timestampColumn = -1;

    public static Date getTimestamp(Cursor cursor) {
        if (timestampColumn < 0) {
            timestampColumn = cursor.getColumnIndexOrThrow(TIMESTAMP);
        }
        return DateUtils.getDate(cursor, timestampColumn);
    }

    public static void putTimestamp(ContentValues out, Date timestamp) {
        DateUtils.putDate(out, TIMESTAMP, timestamp);
    }

    private static int latitudeColumn = -1;

    public static double getLatitude(Cursor cursor) {
        if (latitudeColumn < 0) {
            latitudeColumn = cursor.getColumnIndexOrThrow(LATITUDE);
        }
        return cursor.getDouble(latitudeColumn);
    }

    public static void putLatitude(ContentValues out, double latitude) {
        out.put(LATITUDE, latitude);
    }

    private static int longitudeColumn = -1;

    public static double getLongitude(Cursor cursor) {
        if (longitudeColumn < 0) {
            longitudeColumn = cursor.getColumnIndexOrThrow(LONGITUDE);
        }
        return cursor.getDouble(longitudeColumn);
    }

    public static void putLongitude(ContentValues out, double longitude) {
        out.put(LONGITUDE, longitude);
    }

    private static int senderColumn = -1;

    public static String getSender(Cursor cursor) {
        if (senderColumn < 0) {
            senderColumn = cursor.getColumnIndexOrThrow(SENDER);
        }
        return cursor.getString(senderColumn);
    }

    public static void putSender(ContentValues out, String sender) {
        out.put(SENDER, sender);
    }

    private static int senderIdColumn = -1;

    public static long getSenderId(Cursor cursor) {
        if (senderIdColumn < 0) {
            senderIdColumn = cursor.getColumnIndexOrThrow(SENDER_ID);
        }
        return cursor.getLong(senderIdColumn);
    }

    public static void putSenderId(ContentValues out, long senderId) {
        out.put(SENDER_ID, senderId);
    }


}
