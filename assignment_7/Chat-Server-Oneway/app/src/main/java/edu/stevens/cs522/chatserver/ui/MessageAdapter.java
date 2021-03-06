package edu.stevens.cs522.chatserver.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.entities.Message;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final String TAG = MessageAdapter.class.getCanonicalName();

    private List<Message> messages;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView senderView;
        private final TextView messageView;

        public ViewHolder(View view) {
            super(view);
            // define click listener for the ViewHolder's View

            senderView = (TextView) view.findViewById(R.id.sender);

            messageView = (TextView) view.findViewById(R.id.message);
        }

        public void setSender(String sender) {
            senderView.setText(sender);
        }

        public void setMessage(String message) {
            messageView.setText(message);
        }
    }

    /**
     * Initialize the dataset of the Adapter
     */
    public MessageAdapter() {
        this.messages = new ArrayList<>();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.message, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.setSender(messages.get(position).sender + ":");
        viewHolder.setMessage(messages.get(position).messageText);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return messages.size();
    }

    /*
     * Invoked by live data observer.
     */
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}

