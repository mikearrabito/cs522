package edu.stevens.cs522.chat.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.os.ResultReceiver;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import edu.stevens.cs522.base.DatagramSendReceive;
import edu.stevens.cs522.base.InetAddressUtils;
import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.databases.ChatDatabase;
import edu.stevens.cs522.chat.entities.Message;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.settings.Settings;

import static android.app.Activity.RESULT_OK;


public class ChatService extends Service implements IChatService {

    protected static final String TAG = ChatService.class.getCanonicalName();

    protected static final String SEND_TAG = "ChatSendThread";

    protected static final String RECEIVE_TAG = "ChatReceiveThread";


    public final static String SENDER_NAME = "name";

    public final static String CHATROOM = "room";

    public final static String MESSAGE_TEXT = "text";

    public final static String TIMESTAMP = "timestamp";

    public final static String LATITUDE = "latitude";

    public final static String LONGITUDE = "longitude";


    protected IBinder binder = new ChatBinder();

    protected SendHandler sendHandler;

    protected Thread receiveThread;

    protected DatagramSendReceive chatSocket;

    protected boolean socketOK = true;

    protected boolean finished = false;

    protected ChatDatabase chatDatabase;

    protected int chatPort;

    @Override
    public void onCreate() {

        chatPort = this.getResources().getInteger(R.integer.app_port);

        chatDatabase = ChatDatabase.getInstance(this);

        try {
            chatSocket = new DatagramSendReceive(chatPort);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to init client socket.", e);
        }

        // TODO initialize the thread that sends messages

        // end TODO

        receiveThread = new Thread(new ReceiverThread());
        receiveThread.start();
    }

    @Override
    public void onDestroy() {
        finished = true;
        sendHandler.getLooper().getThread().interrupt();  // No-op?
        sendHandler.getLooper().quit();
        receiveThread.interrupt();
        chatSocket.close();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public final class ChatBinder extends Binder {

        public IChatService getService() {
            return ChatService.this;
        }

    }

    @Override
    public void send(InetAddress destAddress, int destPort,
                     String chatRoom, String messageText,
                     Date timestamp, double latitude, double longitude, ResultReceiver receiver) {
        android.os.Message message = sendHandler.obtainMessage();
        // TODO send the message to the sending thread (add a bundle with params)

    }


    private final class SendHandler extends Handler {

        public static final String HDLR_CHATROOM = "edu.stevens.cs522.chat.services.extra.CHATROOM";
        public static final String HDLR_MESSAGE_TEXT = "edu.stevens.cs522.chat.services.extra.MESSAGE_TEXT";
        public static final String HDLR_TIMESTAMP = "edu.stevens.cs522.chat.services.extra.TIMESTAMP";
        public static final String HDLR_LATITUDE = "edu.stevens.cs522.chat.services.extra.LATITUDE";
        public static final String HDLR_LONGITUDE = "edu.stevens.cs522.chat.services.extra.LONGITUDE";

        public static final String HDLR_DEST_ADDRESS = "edu.stevens.cs522.chat.services.extra.DEST_ADDRESS";
        public static final String HDLR_DEST_PORT = "edu.stevens.cs522.chat.services.extra.DEST_PORT";
        public static final String HDLR_RECEIVER = "edu.stevens.cs522.chat.services.extra.RECEIVER";

        public SendHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(android.os.Message message) {

            try {
                InetAddress destAddr;

                int destPort;

                String senderName;

                long senderId;

                String chatRoom;

                String messageText;

                Date timestamp;

                Double latitude, longitude;

                ResultReceiver receiver;

                senderName = Settings.getSenderName(ChatService.this);

                senderId = Settings.getSenderId(ChatService.this);

                Bundle data = message.getData();


                destAddr = null;

                destPort = -1;

                chatRoom = null;

                messageText = null;

                timestamp = null;

                latitude = null;

                longitude = null;

                receiver = null;


                // TODO get data from message (including result receiver)



                // End todo

                /*
                 * Insert into the local database
                 */
                Message mesg = new Message();
                mesg.messageText = messageText;
                mesg.chatRoom = chatRoom;
                mesg.timestamp = timestamp;
                mesg.latitude = latitude;
                mesg.longitude = longitude;
                mesg.sender = senderName;
                mesg.senderId = senderId;

                // Okay to do this synchronously because we are on a background thread.
                chatDatabase.messageDao().persist(mesg);

                Log.d(TAG, String.format("Sending data from address %s:%d", chatSocket.getInetAddress(), chatSocket.getPort()));

                StringWriter output = new StringWriter();
                JsonWriter wr = new JsonWriter(output);
                wr.beginObject();
                wr.name(SENDER_NAME).value(senderName);
                wr.name(CHATROOM).value(chatRoom);
                wr.name(MESSAGE_TEXT).value(messageText);
                wr.name(TIMESTAMP).value(timestamp.getTime());
                wr.name(LATITUDE).value(latitude);
                wr.name(LONGITUDE).value(longitude);
                wr.endObject();

                String content = output.toString();

                byte[] sendData = content.getBytes();  // Default encoding is UTF-8

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, destAddr, destPort);

                chatSocket.send(sendPacket);

                Log.i(TAG, "Sent content: " + content);

                receiver.send(RESULT_OK, null);


            } catch (UnknownHostException e) {
                Log.e(TAG, "Unknown host exception", e);
            } catch (IOException e) {
                Log.e(TAG, "IO exception", e);
            }

        }
    }

    private final class ReceiverThread implements Runnable {

        public void run() {

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            while (!finished && socketOK) {

                try {

                    chatSocket.receive(receivePacket);
                    Log.i(TAG, "Received a packet");

                    InetAddress address = receivePacket.getAddress();
                    int port = receivePacket.getPort();
                    Log.d(TAG, "Source IP Address: " + address + " , Port: " + port);

                    String content = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    Log.d(TAG, "Message received: " + content);

                    /*
                     * Parse the JSON object
                     */
                    String sender = null;

                    String room = null;

                    String text = null;

                    Date timestamp = null;

                    Double latitude = null;

                    Double longitude = null;

                    JsonReader rd = new JsonReader(new StringReader(content));

                    rd.beginObject();
                    if (SENDER_NAME.equals(rd.nextName())) {
                        sender = rd.nextString();
                    }
                    if (CHATROOM.equals(rd.nextName())) {
                        room = rd.nextString();
                    }
                    if (MESSAGE_TEXT.equals((rd.nextName()))) {
                        text = rd.nextString();
                    }
                    if (TIMESTAMP.equals(rd.nextName())) {
                        timestamp = new Date(rd.nextLong());
                    }
                    if (LATITUDE.equals(rd.nextName())) {
                        latitude = rd.nextDouble();
                    }
                    if (LONGITUDE.equals((rd.nextName()))) {
                        longitude = rd.nextDouble();
                    }
                    rd.endObject();

                    rd.close();

                    /*
                     * Add the sender to our list of senders
                     */
                    Peer peer = new Peer();
                    peer.name = sender;
                    peer.address = address;
                    peer.port = port;
                    peer.timestamp = timestamp;
                    peer.latitude = latitude;
                    peer.longitude = longitude;

                    Message message = new Message();
                    message.messageText = text;
                    message.chatRoom = room;
                    message.sender = sender;
                    message.timestamp = timestamp;
                    message.latitude = latitude;
                    message.longitude = longitude;

                    /*
                     * TODO upsert peer and insert message into the database
                     */



                } catch (Exception e) {

                    Log.e(TAG, "Problems receiving packet.", e);
                    socketOK = false;
                }

            }

        }

    }

}
