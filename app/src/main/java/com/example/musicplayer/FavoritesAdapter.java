package com.example.musicplayer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
    private ArrayList<String> favoriteAudioPaths;
    private OnAudioFileClickListener onAudioFileClickListener;

    public FavoritesAdapter(ArrayList<String> favoriteAudioPaths, OnAudioFileClickListener onAudioFileClickListener) {
        this.favoriteAudioPaths = favoriteAudioPaths;
        this.onAudioFileClickListener = onAudioFileClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String fileName = favoriteAudioPaths.get(position);
        holder.audioFileNameTextView.setText(fileName);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAudioFileClickListener != null) {
                    TextView textView = holder.audioFileNameTextView;
                    String clickedFileName = textView.getText().toString();
                    onAudioFileClickListener.onAudioFileClicked(clickedFileName);
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return favoriteAudioPaths.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView audioFileNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            audioFileNameTextView = itemView.findViewById(R.id.fileNameTextView);
        }
    }

    public interface OnAudioFileClickListener {
        void onAudioFileClicked(String clickedFileName);
    }
}