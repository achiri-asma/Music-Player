package com.example.musicplayer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener {
    private MediaPlayer mediaPlayer;
    private static ArrayList<String> audioFilesList;
    private static int currentIndex;
    private ImageButton playPauseButton, prevButton, nextButton;
    private TextView t1;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        Intent serviceIntent = new Intent(getApplicationContext(), NotificationService.class);
        startService(serviceIntent);

        // Récupérer la liste des fichiers audio et l'index initial depuis les extras
        audioFilesList = getIntent().getStringArrayListExtra("audioFilesList");
        currentIndex = getIntent().getIntExtra("initialIndex", 0);

        String filePath = audioFilesList.get(currentIndex);
        String fileName = new File(filePath).getName();
        t1 = findViewById(R.id.text2);
        t1.setText(fileName);

        // Initialiser le MediaPlayer
        mediaPlayer = new MediaPlayer();
        initMediaPlayer(filePath);
        mediaPlayer.setOnCompletionListener(this); // Définir le listener pour la fin de la lecture

        // Configurer les boutons de contrôle
        playPauseButton = findViewById(R.id.button2);
        prevButton = findViewById(R.id.button1);
        nextButton = findViewById(R.id.button3);
        playPauseButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        databaseHelper = new DatabaseHelper(this);

        ImageButton favoriteButton = findViewById(R.id.button4);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filePath = getCurrentFilePath();
                if (filePath != null) {
                    String fileName = new File(filePath).getName();
                    databaseHelper.insertAudio(filePath, fileName);
                    Toast.makeText(MusicActivity.this, "Audio ajouté aux favoris", Toast.LENGTH_SHORT).show();
                }
            }
        });

        favoriteButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String filePath = getCurrentFilePath();
                if (filePath != null) {
                    databaseHelper.deleteAudio(filePath);
                    Toast.makeText(MusicActivity.this, "Audio supprimé des favoris", Toast.LENGTH_SHORT).show();
                }
                return true; // Indique que le long clic est consommé et ne doit pas être traité comme un clic simple
            }
        });
    }

    public static String getCurrentFilePath() {
        if (audioFilesList != null && currentIndex >= 0 && currentIndex < audioFilesList.size()) {
            return audioFilesList.get(currentIndex);
        }
        return null;
    }

    private void initMediaPlayer(String filePath) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        if (mediaPlayer != null) {
            if (v.getId() == R.id.button2) {
                // Gérer la lecture/pause
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    ((ImageButton) v).setImageResource(R.drawable.ic_play);
                } else {
                    mediaPlayer.start();
                    ((ImageButton) v).setImageResource(R.drawable.pause);
                }
            } else if (v.getId() == R.id.button1) {
                // Gérer le passage au fichier audio précédent
                prevAudio();
            } else if (v.getId() == R.id.button3) {
                // Gérer le passage au fichier audio suivant
                nextAudio();
            }
        }
    }

    private void prevAudio() {
        if (currentIndex > 0) {
            currentIndex--;
        } else {
            currentIndex = audioFilesList.size() - 1; // Revenir à la fin de la liste
        }
        playAudio();
    }

    private void nextAudio() {
        if (currentIndex < audioFilesList.size() - 1) {
            currentIndex++;
        } else {
            currentIndex = 0; // Recommencer depuis le début
        }
        playAudio();
    }

    private void playAudio() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        String filePath = audioFilesList.get(currentIndex);
        String fileName = new File(filePath).getName();
        t1.setText(fileName); // Set the TextView text with the current file name
        initMediaPlayer(filePath);
        mediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // Passer automatiquement au fichier audio suivant à la fin de la lecture
        nextAudio();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
