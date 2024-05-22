
package com.example.musicplayer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "audios.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "favorite_audios";
    private static final String COLUMN_FILE_PATH = "file_path";
    private static final String COLUMN_FILE_NAME = "file_name";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_FILE_PATH + " TEXT PRIMARY KEY, " +
                COLUMN_FILE_NAME + " TEXT)";
        db.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertAudio(String filePath, String fileName) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES('" + filePath + "', '" + fileName + "')");
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error inserting audio: " + e.getMessage());
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
    public void deleteAudio(String filePath) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_FILE_PATH + "='" + filePath + "'");
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error deleting audio: " + e.getMessage());
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public ArrayList<String> getAllFavoriteAudios() {
        ArrayList<String> favoriteAudios = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT " + COLUMN_FILE_PATH + ", " + COLUMN_FILE_NAME + " FROM " + TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                do {
                    int filePathIndex = cursor.getColumnIndex(COLUMN_FILE_PATH);
                    int fileNameIndex = cursor.getColumnIndex(COLUMN_FILE_NAME);
                    String filePath = cursor.getString(filePathIndex);
                    String fileName = cursor.getString(fileNameIndex);
                    favoriteAudios.add(fileName); // Ajoutez le nom de fichier à la liste
                    Log.d("DatabaseHelper", "Favorite audio file: " + filePath + ", " + fileName);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting all favorite audios: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        Log.d("DatabaseHelper", "List of favorite audio files: " + favoriteAudios.toString());
        return favoriteAudios;
    }
    public ArrayList<String> getAllFavoriteAudiosFav() {
        ArrayList<String> favoriteAudioPaths = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT " + COLUMN_FILE_PATH + " FROM " + TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                do {
                    int filePathIndex = cursor.getColumnIndex(COLUMN_FILE_PATH);
                    String filePath = cursor.getString(filePathIndex);
                    favoriteAudioPaths.add(filePath); // Ajoutez le chemin complet du fichier à la liste
                    Log.d("DatabaseHelper", "Favorite audio file path: " + filePath);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting all favorite audios: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        Log.d("DatabaseHelper", "List of favorite audio file paths: " + favoriteAudioPaths.toString());
        return favoriteAudioPaths;
    }
}
