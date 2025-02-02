package com.example.mafia;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
        Log.d("MessageAdapter", "Initial message list: " + messageList.toString());
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    private int getProfileLogoResourceId(int profileLogoId) {
        switch (profileLogoId) {
            case 0:
                return R.drawable.profile_logo_1;
            case 1:
                return R.drawable.profile_logo_2;
            case 2:
                return R.drawable.profile_logo_3;
            case 3:
                return R.drawable.profile_logo_4;
            default:
                return R.drawable.profile_logo_1;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);

        holder.usernameTextView.setText(message.getUsername());
        holder.messageTextView.setText(message.getMessage());

        int profileLogoId = message.getClientProfileLogo();
        int resourceId = getProfileLogoResourceId(profileLogoId);
        holder.profileLogoImageView.setImageResource(resourceId);

        Log.d("MessageAdapter", "Binding message at position " + position + ": " + message.getUsername() + " - " + message.getMessage() + ", logo ID wiadomość otrzymana");
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView messageTextView;
        ImageView profileLogoImageView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            profileLogoImageView = itemView.findViewById(R.id.avatarImageView);
        }
    }
}