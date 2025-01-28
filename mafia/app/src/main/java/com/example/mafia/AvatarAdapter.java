package com.example.mafia;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder> {

    private Context context;
    private List<Integer> avatarDrawables;
    private OnAvatarClickListener listener;
    private int selectedAvatar = -1;
    private int highlightColor = Color.parseColor("#8000FF00"); // Kolor podświetlenia (półprzezroczysty zielony)
    private int defaultColor = Color.TRANSPARENT; // Domyślny kolor tła (przezroczysty)

    public AvatarAdapter(Context context, List<Integer> avatarDrawables, OnAvatarClickListener listener) {
        this.context = context;
        this.avatarDrawables = avatarDrawables;
        this.listener = listener;
    }

    public void setSelectedAvatar(int selectedAvatar) {
        int previousSelected = this.selectedAvatar;
        this.selectedAvatar = selectedAvatar;
        notifyItemChanged(previousSelected);
        notifyItemChanged(selectedAvatar);
    }

    @NonNull
    @Override
    public AvatarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.avatar_item, parent, false);
        return new AvatarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvatarViewHolder holder, int position) {
        int avatarDrawable = avatarDrawables.get(position);
        holder.avatarImageView.setImageResource(avatarDrawable);

        if (position == selectedAvatar) {
            holder.avatarCardView.setCardBackgroundColor(highlightColor);
            holder.avatarCardView.setCardElevation(10);
        } else {
            holder.avatarCardView.setCardBackgroundColor(defaultColor);
            holder.avatarCardView.setCardElevation(0);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAvatarClick(avatarDrawable, position);
                int previousSelected = selectedAvatar;
                selectedAvatar = holder.getAdapterPosition();
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedAvatar);
            }
        });
    }

    @Override
    public int getItemCount() {
        return avatarDrawables.size();
    }

    public static class AvatarViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImageView;
        CardView avatarCardView;

        public AvatarViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            avatarCardView = itemView.findViewById(R.id.avatarCardView);
        }
    }

    public interface OnAvatarClickListener {
        void onAvatarClick(int avatarDrawable, int position);
    }

    public int getSelectedAvatar() {
        return selectedAvatar;
    }
}