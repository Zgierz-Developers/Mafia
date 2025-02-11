package com.example.mafia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {

    private Context context;
    private List<Integer> colorDrawables;
    private OnColorClickListener listener;
    private int selectedColor = -1;

    public ColorAdapter(Context context, List<Integer> colorDrawables, OnColorClickListener listener) {
        this.context = context;
        this.colorDrawables = colorDrawables;
        this.listener = listener;
    }

    public class ColorViewHolder extends RecyclerView.ViewHolder {
        public View colorView;
        public ImageView selectedImageView;

        public ColorViewHolder(View itemView) {
            super(itemView);
            colorView = itemView.findViewById(R.id.colorView);
            selectedImageView = itemView.findViewById(R.id.selectedImageView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    // Tutaj wywołujemy metodę onColorClick z interfejsu
                    listener.onColorClick(colorDrawables.get(position), position);
                    setSelectedColor(position);
                }
            });
        }
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.color_item, parent, false);
        return new ColorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        int colorDrawable = colorDrawables.get(position);
        holder.colorView.setBackgroundColor(context.getResources().getColor(colorDrawable, null));

        if (selectedColor == position) {
            holder.selectedImageView.setVisibility(View.VISIBLE);
        } else {
            holder.selectedImageView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return colorDrawables.size();
    }

    public void setSelectedColor(int position) {
        int previousSelected = selectedColor;
        selectedColor = position;
        notifyItemChanged(previousSelected);
        notifyItemChanged(selectedColor);
    }
    public int getSelectedColor(){
        return selectedColor;
    }
}