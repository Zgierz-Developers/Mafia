package com.example.mafia;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messageList;
    private Context context;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
        Log.d("MessageAdapter", "Initial message list: " + messageList.toString());
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
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

    private int getProfileNickColorResourceId(int profileNickColorId) {
        switch (profileNickColorId) {
            case 0:
                return R.color.dark_red;
            case 1:
                return R.color.red;
            case 2:
                return R.color.orange;
            case 3:
                return R.color.yellow;
            case 4:
                return R.color.dark_green;
            case 5:
                return R.color.green;
            case 6:
                return R.color.light_green;
            case 7:
                return R.color.blue;
            case 8:
                return R.color.dark_blue;
            case 9:
                return R.color.light_blue;
            case 10:
                return R.color.purple;
            case 11:
                return R.color.pink;
            case 12:
                return R.color.brown;
            case 13:
                return R.color.black;
            case 14:
                return R.color.gray;
            case 15:
                return R.color.white;
            default:
                return R.color.pink;
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);

        holder.usernameTextView.setText(message.getUsername());
        holder.messageTextView.setText(message.getMessage());

        int profileLogoId = message.getClientProfileLogo();
        int resourceId = getProfileLogoResourceId(profileLogoId);
        holder.profileLogoImageView.setImageResource(resourceId);


        int profileNickColorId = message.getClientNickColor();
        int nickColorResourceId = getProfileNickColorResourceId(profileNickColorId);
        int color = ContextCompat.getColor(context, nickColorResourceId);
        holder.usernameTextView.setTextColor(color);
        Log.d("ELO420", "Profile logo ID: " + profileNickColorId);
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