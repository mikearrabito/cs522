package edu.stevens.cs522.chat.rest.client;

import java.io.OutputStream;

import edu.stevens.cs522.chat.entities.Message;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/*
 * The API for the chat server.
 */
public interface ServerApi {

    public final static String CHAT_NAME = "chat-name";

    public final static String LAST_SEQ_NUM = "last-seq-num";

    // Make sure that URL paths use this as parameter name.
    public final static String SENDER_ID = "sender-id";

    @POST("chat")
    public Call<Void> register(@Query(CHAT_NAME) String chatName);

    @POST("chat/{sender-id}/messages")
    public Call<Void> postMessage(@Path(SENDER_ID) long senderId, @Body Message chatMessage);

    @POST("chat/{sender-id}/sync")
    public Call<ResponseBody> syncMessages(@Path(SENDER_ID) long senderId,
                                           @Query(LAST_SEQ_NUM) long lastSeqNum,
                                           @Body RequestBody requestBody);

}
