package edu.stevens.cs522.chatserver.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import edu.stevens.cs522.chatserver.contracts.MessageContract;

/**
 * Created by dduggan.
 */

public class Message implements Parcelable, Persistable {

    public long id;

    public String chatRoom;

    public String messageText;

    public Date timestamp;

    public Double latitude;

    public Double longitude;

    public String sender;

    public long senderId;

    public Message() {
    }

    public Message(Cursor in) {
        id = MessageContract.getId(in);
        chatRoom = MessageContract.getChatRoom(in);
        messageText = MessageContract.getMessageText(in);
        timestamp = MessageContract.getTimestamp(in);
        latitude = MessageContract.getLatitude(in);
        longitude = MessageContract.getLongitude(in);
        sender = MessageContract.getSender(in);
        senderId = MessageContract.getSenderId(in);
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
    public void writeToProvider(ContentValues out) {
        //out.put(MessageContract._ID, id);
        MessageContract.putId(out, id);
        MessageContract.putChatRoom(out, chatRoom);
        MessageContract.putMessageText(out, messageText);
        MessageContract.putTimestamp(out, timestamp);
        MessageContract.putLatitude(out, latitude);
        MessageContract.putLongitude(out, longitude);
        MessageContract.putSender(out, sender);
        MessageContract.putSenderId(out, senderId);
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

