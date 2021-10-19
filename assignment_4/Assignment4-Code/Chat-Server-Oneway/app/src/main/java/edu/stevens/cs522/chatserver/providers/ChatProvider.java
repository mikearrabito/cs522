package edu.stevens.cs522.chatserver.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import edu.stevens.cs522.chatserver.contracts.BaseContract;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;

public class ChatProvider extends ContentProvider {

    public ChatProvider() {
    }

    private static final String AUTHORITY = BaseContract.AUTHORITY;

    private static final String MESSAGE_CONTENT_PATH = MessageContract.CONTENT_PATH;

    private static final String MESSAGE_CONTENT_PATH_ITEM = MessageContract.CONTENT_PATH_ITEM;

    private static final String PEER_CONTENT_PATH = PeerContract.CONTENT_PATH;

    private static final String PEER_CONTENT_PATH_ITEM = PeerContract.CONTENT_PATH_ITEM;


    private static final String DATABASE_NAME = "messages.db";

    private static final String MESSAGE_TABLE = "messages";

    private static final String PEER_TABLE = "peers";

    private static final int DATABASE_VERSION = 2;


    // Create the constants used to differentiate between the different URI requests.
    private static final int MESSAGES_ALL_ROWS = 1;
    private static final int MESSAGES_SINGLE_ROW = 2;
    private static final int PEERS_ALL_ROWS = 3;
    private static final int PEERS_SINGLE_ROW = 4;

    public static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        private static final String CREATE_PEER_TABLE = "CREATE TABLE IF NOT EXISTS " + PEER_TABLE + " (" +
                PeerContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PeerContract.NAME + " TEXT NOT NULL," +
                PeerContract.TIMESTAMP + " TEXT NOT NULL," +
                PeerContract.LATITUDE + " REAL NOT NULL," +
                PeerContract.LONGITUDE + " REAL NOT NULL," +
                PeerContract.ADDRESS + " TEXT NOT NULL," +
                PeerContract.PORT + " INTEGER NOT NULL" +
                ")";

        private static final String CREATE_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + MESSAGE_TABLE + "  (" +
                MessageContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MessageContract.CHATROOM + " TEXT NOT NULL," +
                MessageContract.MESSAGE_TEXT + " TEXT NOT NULL," +
                MessageContract.TIMESTAMP + " TEXT NOT NULL," +
                MessageContract.LATITUDE + " REAL NOT NULL," +
                MessageContract.LONGITUDE + " REAL NOT NULL," +
                MessageContract.SENDER + " TEXT NOT NULL," +
                MessageContract.SENDER_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + MessageContract.SENDER_ID + ") REFERENCES " + PEER_TABLE + " (" + MessageContract._ID + ") " + " ON DELETE CASCADE" +
                ")";

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

    private DbHelper dbHelper;

    @Override
    public boolean onCreate() {
        // Initialize your content provider on startup.
        dbHelper = new DbHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        return true;
    }

    // Used to dispatch operation based on URI
    private static final UriMatcher uriMatcher;

    // uriMatcher.addURI(AUTHORITY, CONTENT_PATH, OPCODE)
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH, MESSAGES_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH_ITEM, MESSAGES_SINGLE_ROW);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH, PEERS_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH_ITEM, PEERS_SINGLE_ROW);
    }

    protected String contentType(String content) {
        return "vnd.android.cursor/vnd." + BaseContract.AUTHORITY + "." + content + "s";
    }

    protected String contentItemType(String content) {
        return "vnd.android.cursor.item/vnd." + BaseContract.AUTHORITY + "." + content + "s";
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                return contentType("message");
            case MESSAGES_SINGLE_ROW:
                return contentItemType("message");
            case PEERS_ALL_ROWS:
                return contentType("peer");
            case PEERS_SINGLE_ROW:
                return contentItemType("peer");
            default:
                throw new IllegalStateException("Unrecognized case.");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                long msgId = db.insert(MESSAGE_TABLE, null, values);
                Uri instanceUri = MessageContract.CONTENT_URI(msgId);
                ContentResolver resolver = getContext().getContentResolver();
                resolver.notifyChange(instanceUri, null);
                return instanceUri;

            case PEERS_ALL_ROWS:
                String selection = (PeerContract.NAME + "=?");
                String[] selectionArgs = new String[]{values.getAsString(PeerContract.NAME)};
                Cursor cursor = db.query(PEER_TABLE, null, selection, selectionArgs, null, null, null);
                if (cursor.moveToFirst()) {
                    db.update(PEER_TABLE, values, selection, null);
                    Uri peerUri = PeerContract.CONTENT_URI(PeerContract.getId(cursor)); // get id from cursor, then convert to uri
                    getContext().getContentResolver().notifyChange(peerUri, null);
                    return peerUri;
                } else {
                    long id = db.insert(PEER_TABLE, null, values);
                    Uri peerUri = PeerContract.CONTENT_URI(id); // get id from cursor, then convert to uri
                    getContext().getContentResolver().notifyChange(peerUri, null);
                    return peerUri;
                }

            default:
                throw new IllegalStateException("insert: bad case");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                Cursor cursor = db.query(MESSAGE_TABLE, projection, selection, selectionArgs, null, null, null);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;

            case PEERS_ALL_ROWS:
                Cursor peerAllRowsCursor = db.query(PEER_TABLE, projection, selection, selectionArgs, null, null, null);
                peerAllRowsCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return peerAllRowsCursor;

            case MESSAGES_SINGLE_ROW:
                Cursor messageSingleRowCursor = db.query(MESSAGE_TABLE, projection, selection, selectionArgs, null, null, null);
                messageSingleRowCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return messageSingleRowCursor;

            case PEERS_SINGLE_ROW:
                Cursor peerSingleRowCursor = db.query(PEER_TABLE, projection, selection, selectionArgs, null, null, null);
                peerSingleRowCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return peerSingleRowCursor;

            default:
                throw new IllegalStateException("Query: bad case");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:

            case MESSAGES_SINGLE_ROW:
                return db.update(MESSAGE_TABLE, values, selection, selectionArgs);

            case PEERS_ALL_ROWS:

            case PEERS_SINGLE_ROW:
                return db.update(PEER_TABLE, values, selection, selectionArgs);

            default:
                throw new IllegalStateException("Query: bad case");
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                int messagesDeleteResult = db.delete(MESSAGE_TABLE, selection, selectionArgs);
                return messagesDeleteResult;

            case PEERS_ALL_ROWS:
                int peersDeleteResult = db.delete(PEER_TABLE, selection, selectionArgs);
                return peersDeleteResult;

            default:
                throw new IllegalStateException("Invalid uri for delete");
        }
    }
}


