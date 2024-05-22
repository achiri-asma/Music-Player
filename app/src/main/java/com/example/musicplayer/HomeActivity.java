package com.example.musicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView noMp3TextView;
    ArrayList<String> audioFilesList = new ArrayList<>();

    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.recyclerView);
        noMp3TextView = findViewById(R.id.no_songs_text);
        btn =findViewById(R.id.button);
      displayAudioFiles();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, FavoriteActivity.class);
                startActivity(intent);
            }
        });
    }

    private void displayAudioFiles() {
        File rootDir = Environment.getExternalStorageDirectory();
        searchForAudioFiles(rootDir);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, audioFilesList);
        recyclerView.setAdapter(adapter);
    }

    private void searchForAudioFiles(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    searchForAudioFiles(file);
                } else {
                    if (isAudioFile(file)) {
                        audioFilesList.add(file.getAbsolutePath());
                    }
                }
            }
        }
    }

    private boolean isAudioFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".mp3");
    }


}
