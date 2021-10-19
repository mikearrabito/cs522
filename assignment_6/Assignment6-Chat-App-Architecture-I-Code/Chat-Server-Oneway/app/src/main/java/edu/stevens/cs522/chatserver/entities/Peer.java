package edu.stevens.cs522.chatserver.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.net.InetAddress;
import java.util.Date;

/**
 * Created by dduggan.
 */


@Entity(indices = {@Index(value = {"name"}, unique = true)})
public class Peer implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;

    @TypeConverters(DateConverter.class)
    public Date timestamp;

    public Double latitude;
    public Double longitude;

    @TypeConverters(InetAddressConverter.class)
    public InetAddress address;

    public int port;

    @Override
    public String toString() {
        return name;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(this.id);
        out.writeString(this.name);
        out.writeSerializable(this.timestamp);
        out.writeDouble(this.latitude);
        out.writeDouble(this.longitude);
        out.writeSerializable(this.address);
        out.writeInt(this.port);
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
