package com.example.musicplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.IOException;
import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity implements FavoritesAdapter.OnAudioFileClickListener {
    private RecyclerView recyclerView;
    private FavoritesAdapter adapter;
    private ArrayList<String> favoriteAudioPaths;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        recyclerView = findViewById(R.id.listView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialisez la liste des chemins des fichiers audio favoris depuis la base de données
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        favoriteAudioPaths = databaseHelper.getAllFavoriteAudios();

        // Initialisez l'adaptateur avec la liste des chemins des fichiers audio favoris
        adapter = new FavoritesAdapter(favoriteAudioPaths, this);
        recyclerView.setAdapter(adapter);

        mediaPlayer = new MediaPlayer();
    }
    @Override
    public void onAudioFileClicked(String clickedFileName) {
        String audioFilePath = getAudioFilePathFromName(clickedFileName);
        if (audioFilePath != null) {
            Intent intent = new Intent(FavoriteActivity.this, FavMusicActivity.class);
            ArrayList<String> audioFilesList = new ArrayList<>();
            audioFilesList.add(audioFilePath); // Ajouter le fichier audio sélectionné à la liste
            intent.putExtra("audioFilesList", audioFilesList);
            intent.putExtra("initialIndex", 0); // L'index initial est 0 car il n'y a qu'un seul fichier dans la liste
            startActivity(intent);
        }
    }

    private void playAudio(String clickedFileName) {
        // Trouvez le chemin complet du fichier audio à partir de son nom
        String audioFilePath = getAudioFilePathFromName(clickedFileName);

        // Lancez la lecture du fichier audio
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getAudioFilePathFromName(String fileName) {
        // Implémentez la logique pour obtenir le chemin complet du fichier audio à partir de son nom
        // Vous pouvez parcourir la liste favoriteAudioPaths et trouver le chemin correspondant au nom de fichier donné
        // ou utiliser une structure de données plus appropriée pour stocker les noms de fichiers et leurs chemins
        return "/chemin/vers/fichier/" + fileName;
    }
}