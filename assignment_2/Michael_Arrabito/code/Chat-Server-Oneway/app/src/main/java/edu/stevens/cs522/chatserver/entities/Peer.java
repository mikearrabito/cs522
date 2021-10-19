package edu.stevens.cs522.chatserver.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.InetAddress;
import java.util.Date;

/**
 * Created by dduggan.
 */

public class Peer implements Parcelable {

    // Will be database key
    public long id;

    public String name;

    // Last time we heard from this peer.
    public Date timestamp;

    // Where we heard from them
    public Double latitude;
    public Double longitude;

    // Where they sent message from
    public InetAddress address;
    public int port;

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(name);
        out.writeSerializable(timestamp);
        out.writeDouble(latitude);
        out.writeDouble(longitude);
        out.writeSerializable(address);
        out.writeInt(port);
    }

    public Peer() {
    }

    public Peer(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.timestamp = (Date) in.readSerializable();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.address = (InetAddress) in.readSerializable();
        this.port = in.readInt();
    }

    public static final Creator<Peer> CREATOR = new Creator<Peer>() {

        @Override
        public Peer createFromParcel(Parcel source) {
            return new Peer(source);
        }

        @Override
        public Peer[] newArray(int size) {
            return new Peer[size];
        }

    };
}
