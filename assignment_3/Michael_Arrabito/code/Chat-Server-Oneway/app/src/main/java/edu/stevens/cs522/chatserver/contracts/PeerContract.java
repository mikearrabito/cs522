package edu.stevens.cs522.chatserver.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.net.InetAddress;
import java.util.Date;

import edu.stevens.cs522.base.DateUtils;
import edu.stevens.cs522.base.InetAddressUtils;

/**
 * Created by dduggan.
 */

public class PeerContract implements BaseColumns {

    // TODO define column names, getters for cursors, setters for contentvalues

    public static final String ID = "_id";

    public static final String NAME = "name";

    public static final String TIMESTAMP = "timestamp";

    public static final String LATITUDE = "latitude";

    public static final String LONGITUDE = "longitude";

    public static final String ADDRESS = "address";

    public static final String PORT = "port";


    private static int idColumn = -1;

    public static long getId(Cursor cursor) {
        if (idColumn < 0) {
            idColumn = cursor.getColumnIndexOrThrow(ID);
        }
        return cursor.getLong(idColumn);
    }

    public static void putId(ContentValues out, long id) {
        if (id > 0) {
            out.put(ID, id);
        }
    }

    private static int nameColumn = -1;

    public static String getName(Cursor cursor) {
        if (nameColumn < 0) {
            nameColumn = cursor.getColumnIndexOrThrow(NAME);
        }
        return cursor.getString(nameColumn);
    }

    public static void putName(ContentValues out, String name) {
        out.put(NAME, name);
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

    private static int addressColumn = -1;

    public static InetAddress getAddress(Cursor cursor) {
        if (addressColumn < 0) {
            addressColumn = cursor.getColumnIndexOrThrow(ADDRESS);
        }
        return InetAddressUtils.getAddress(cursor, addressColumn);
    }

    public static void putAddress(ContentValues out, InetAddress address) {
        InetAddressUtils.putAddress(out, ADDRESS, address);
    }

    private static int portColumn = -1;

    public static int getPort(Cursor cursor) {
        if (portColumn < 0) {
            portColumn = cursor.getColumnIndexOrThrow(PORT);
        }
        return cursor.getInt(portColumn);
    }

    public static void putPort(ContentValues out, int port) {
        out.put(PORT, port);
    }

}
