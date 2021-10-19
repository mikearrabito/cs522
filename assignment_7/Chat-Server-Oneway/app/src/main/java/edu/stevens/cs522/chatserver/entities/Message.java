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

@Entity(foreignKeys = @ForeignKey(entity = Peer.class, parentColumns = "id",
        childColumns = "senderId", onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "senderId")})
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
        id = in.readLong();
        chatRoom = in.readString();
        messageText = in.readString();
        timestamp = (Date) in.readSerializable();
        latitude = in.readDouble();
        longitude = in.readDouble();
        sender = in.readString();
        senderId = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(chatRoom);
        dest.writeString(messageText);
        dest.writeSerializable(timestamp);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(sender);
        dest.writeLong(senderId);
    }

    @Override
    public String toString() {
        return messageText;
    }

    @Override
    public int describeContents() {
        return 0;
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

