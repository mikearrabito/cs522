package edu.stevens.cs522.chatserver.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.util.Date;

/**
 * Created by dduggan.
 */

// TODO annotate (including FK constraints)

@Entity(foreignKeys = @ForeignKey(entity = Peer.class, onDelete = ForeignKey.CASCADE,
        parentColumns = "id", childColumns = "senderId"), indices = {@Index(value = {"senderId"})})
public class Message implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String chatRoom;
    public String messageText;

    @TypeConverters(DateConverter.class)
    public Date timestamp;

    public Double latitude;
    public Double longitude;
    public String sender;
    public long senderId;

    public Message() {
    }

    public Message(Parcel in) {
        this.id = in.readLong();
        this.chatRoom = in.readString();
        this.messageText = in.readString();
        this.timestamp = (Date) in.readSerializable();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.sender = in.readString();
        this.senderId = in.readLong();
    }

    @Override
    public String toString() {
        return sender + ": " + messageText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.chatRoom);
        dest.writeString(this.messageText);
        dest.writeSerializable(this.timestamp);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.sender);
        dest.writeLong(this.senderId);
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {

        @Override
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }

    };

}

