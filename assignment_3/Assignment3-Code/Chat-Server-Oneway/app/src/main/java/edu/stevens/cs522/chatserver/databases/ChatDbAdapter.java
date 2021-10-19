package edu.stevens.cs522.chatserver.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;

/**
 * Created by dduggan.
 */

public class ChatDbAdapter {

    private static final String DATABASE_NAME = "messages.db";
    private static final String MESSAGE_TABLE = "messages";
    private static final String PEER_TABLE = "peers";
    private static final int DATABASE_VERSION = 1;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    private static final String ID = "_id";
    private static final String NAME = "name";
    private static final String TIMESTAMP = "timestamp";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String ADDRESS = "address";
    private static final String PORT = "port";
    private static final String CHAT_ROOM = "chat_room";
    public static final String MESSAGE_TEXT = "message_text";
    private static final String SENDER = "sender";
    private static final String SENDER_ID = "sender_id";
    private static final String PEER_FK = "peer_fk";

    private static final String[] ALL_PEER_COLUMNS = {ID, NAME, TIMESTAMP, LATITUDE, LONGITUDE, ADDRESS, PORT};
    private static final String[] ALL_MESSAGE_COLUMNS = {ID, CHAT_ROOM, MESSAGE_TEXT, TIMESTAMP, LATITUDE, LONGITUDE, SENDER, SENDER_ID};

    public static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String CREATE_PEER_TABLE = "CREATE TABLE IF NOT EXISTS " + PEER_TABLE + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                NAME + " TEXT NOT NULL," +
                TIMESTAMP + " TEXT NOT NULL," +
                LATITUDE + " REAL NOT NULL," +
                LONGITUDE + " REAL NOT NULL," +
                ADDRESS + " TEXT NOT NULL," +
                PORT + " INTEGER NOT NULL" +
                ")";

        private static final String CREATE_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + MESSAGE_TABLE + "  (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CHAT_ROOM + " TEXT NOT NULL," +
                MESSAGE_TEXT + " TEXT NOT NULL," +
                TIMESTAMP + " TEXT NOT NULL," +
                LATITUDE + " REAL NOT NULL," +
                LONGITUDE + " REAL NOT NULL," +
                SENDER + " TEXT NOT NULL," +
                SENDER_ID + " INTEGER NOT NULL" +
                //"FOREIGN KEY (" + SENDER_ID + ") REFERENCES " + PEER_TABLE + " (" + ID + ") " + "ON DELETE CASCADE" +
                ")";

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_PEER_TABLE);
            db.execSQL(CREATE_MESSAGE_TABLE);
            db.execSQL("CREATE INDEX MessagesPeerIndex ON Messages(sender_id);");
            db.execSQL("CREATE INDEX PeerNameIndex ON Peers(name);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + PEER_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_TABLE);
            onCreate(db);
        }
    }


    public ChatDbAdapter(Context _context) {
        dbHelper = new DatabaseHelper(_context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    public Cursor fetchAllMessages() {
        return db.query(MESSAGE_TABLE, ALL_MESSAGE_COLUMNS,
                null, null, null, null, null, null);
    }

    public Cursor fetchAllPeers() {
        return db.query(PEER_TABLE, ALL_PEER_COLUMNS,
                null, null, null, null, null, null);
    }

    public Peer fetchPeer(long peerId) {
        String selection = ID + "=" + peerId;
        Cursor queryResult = db.query(PEER_TABLE, ALL_PEER_COLUMNS, selection,
                null, null, null, null, null);
        return new Peer(queryResult);
    }

    public Cursor fetchMessagesFromPeer(Peer peer) {
        String selection = SENDER_ID + "=" + peer.id;
        return db.query(MESSAGE_TABLE, ALL_MESSAGE_COLUMNS, selection,
                null, null, null, null, null);
    }

    public long persist(Message message) throws SQLException {
        ContentValues values = new ContentValues();
        message.writeToProvider(values);
        return db.insert(MESSAGE_TABLE, null, values);
    }

    /**
     * Add a peer record if it does not already exist; update information if it is already defined.
     */
    public long persist(Peer peer) throws SQLException {
        ContentValues values = new ContentValues();
        peer.writeToProvider(values);
        String selection = NAME + "=" + '"' + peer.name + '"';
        Cursor queryResult = db.query(PEER_TABLE, ALL_PEER_COLUMNS, selection,
                null, null, null, null, null);

        if (queryResult.getCount() == 0) {
            return db.insert(PEER_TABLE, null, values);
        } else {
            db.update(PEER_TABLE, values, selection, null);
            queryResult.moveToFirst();
            return PeerContract.getId(queryResult);
        }
    }

    public void close() {
        db.close();
    }
}