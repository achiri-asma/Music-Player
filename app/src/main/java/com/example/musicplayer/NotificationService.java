package com.example.musicplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class NotificationService extends Service implements MediaPlayer.OnCompletionListener {

    private static final String CHANNEL_ID = "channel_id";
    private static final int NOTIFICATION_ID = 1;

    private MediaPlayer mediaPlayer;
    private int currentIndex = 0;
    private ArrayList<Uri> mp3Files = new ArrayList<>();
    private MusicReceiver musicReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        loadMp3Files();
        musicReceiver = new MusicReceiver(this);
        registerReceiver(musicReceiver, new IntentFilter("PAUSE"));
        registerReceiver(musicReceiver, new IntentFilter("PLAY"));
        registerReceiver(musicReceiver, new IntentFilter("PREVIOUS"));
        registerReceiver(musicReceiver, new IntentFilter("NEXT"));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Music Player", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void loadMp3Files() {
        File rootDir = Environment.getExternalStorageDirectory();
        searchForAudioFiles(rootDir);
    }

    private void searchForAudioFiles(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    searchForAudioFiles(file);
                } else {
                    if (isAudioFile(file)) {
                        mp3Files.add(Uri.fromFile(file));
                    }
                }
            }
        }
    }

    private boolean isAudioFile(File file) {
        String extension = getFileExtension(file.getName());
        return extension != null && extension.equalsIgnoreCase("mp3");
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex >= 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mp3Files.isEmpty()) {
            playCurrentFile();
            showNotification();
        }
        return START_STICKY;
    }

    private void playCurrentFile() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, mp3Files.get(currentIndex));
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }

    void playNext() {
        currentIndex = (currentIndex + 1) % mp3Files.size();
        playCurrentFile();
        showNotification();
    }

    void playPrevious() {
        currentIndex = (currentIndex - 1 + mp3Files.size()) % mp3Files.size();
        playCurrentFile();
        showNotification();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playNext();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public Notification showNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_play)
                .setContentTitle("Lecture en cours")
                .setContentText(mp3Files.get(currentIndex).getLastPathSegment())
                .setContentIntent(pendingIntent)
                .setOngoing(true);


            Intent playIntent = new Intent("MUSIC_ACTIONS");
            playIntent.setAction("PLAY");
            PendingIntent pendingPlayIntent = PendingIntent.getBroadcast(this, 0, playIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.drawable.ic_play, "Play", pendingPlayIntent);

        Intent nextIntent = new Intent("MUSIC_ACTIONS");
        nextIntent.setAction("NEXT");
        PendingIntent pendingNextIntent = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.next, "Next", pendingNextIntent);

        Intent prevIntent = new Intent("MUSIC_ACTIONS");
        prevIntent.setAction("PREVIOUS");
        PendingIntent pendingPrevIntent = PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.next_2, "Previous", pendingPrevIntent);

        Intent pauseIntent = new Intent("MUSIC_ACTIONS");
        pauseIntent.setAction("PAUSE");
        PendingIntent pendingPauseIntent = PendingIntent.getBroadcast(this, 0, pauseIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.pause, "Pause", pendingPauseIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Music Player", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        startForeground(NOTIFICATION_ID, builder.build());
        return builder.build();
    }

    public class MusicReceiver extends BroadcastReceiver {
        private NotificationService notificationService;

        public MusicReceiver(NotificationService service) {
            notificationService = service;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case "PAUSE":
                    if (notificationService.mediaPlayer != null && notificationService.mediaPlayer.isPlaying()) {
                        notificationService.mediaPlayer.pause();
                    }
                    break;
                case "PLAY":
                    if (notificationService.mediaPlayer != null && !notificationService.mediaPlayer.isPlaying()) {
                        notificationService.mediaPlayer.start();
                    }
                    break;
                case "NEXT":
                    notificationService.playNext();
                    break;
                case "PREVIOUS":
                    notificationService.playPrevious();
                    break;
            }
            notificationService.showNotification();
        }

        void cleanup() {
            notificationService = null;
        }}}