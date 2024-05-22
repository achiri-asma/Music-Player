package com.example.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<String> audioFilesList;

    public RecyclerViewAdapter(Context context, List<String> audioFilesList) {
        this.context = context;
        this.audioFilesList = audioFilesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String audioFilePath = audioFilesList.get(position);
        holder.bind(audioFilePath);
    }

    @Override
    public int getItemCount() {
        return audioFilesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView fileNameTextView;
        String audioFilePath;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileNameTextView = itemView.findViewById(R.id.fileNameTextView);
            itemView.setOnClickListener(this);
        }

        public void bind(String audioFilePath) {
            this.audioFilePath = audioFilePath;
            fileNameTextView.setText(new File(audioFilePath).getName());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(context, MusicActivity.class);
                intent.putExtra("audioFilesList", new ArrayList<>(audioFilesList));
                intent.putExtra("initialIndex", position);
                context.startActivity(intent);
            }
        }
    }
}