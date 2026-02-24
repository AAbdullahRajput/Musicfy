package com.example.myapplication;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 100;
    private ListView listView;
    private ArrayList<String> mySongs;
    private ArrayList<String> songPaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        listView = findViewById(R.id.listView);

        // Check and handle permissions based on SDK version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+ (READ_MEDIA_AUDIO)
            requestPermission(Manifest.permission.READ_MEDIA_AUDIO);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10-12 (Scoped Storage)
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            fetchSongsAndDisplay(); // Permissions not required for SDK < 23
        }
    }

    private void requestPermission(String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            fetchSongsAndDisplay();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, STORAGE_PERMISSION_CODE);
        }
    }

    private void fetchSongsAndDisplay() {
        try {
            new Thread(() -> {
                try {
                    mySongs = fetchSongs();
                    songPaths = fetchSongPaths();

                    if (mySongs.isEmpty()) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "No songs found", Toast.LENGTH_SHORT).show());
                        return;
                    }

                    runOnUiThread(() -> {
                        SongAdapter adapter = new SongAdapter(MainActivity.this, mySongs);
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener((parent, view, position, id) -> {
                            Intent intent = new Intent(MainActivity.this, PlaySong.class);
                            intent.putExtra("songPaths", songPaths); // Pass the list of song paths
                            intent.putExtra("currentIndex", position); // Pass the current song index
                            startActivity(intent);
                        });
                    });
                } catch (Exception e) {
                    Log.e("MainActivity", "Error while fetching songs: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }).start();
        } catch (Exception e) {
            Log.e("MainActivity", "Unexpected error: " + e.getMessage());
            Toast.makeText(this, "Unexpected error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<String> fetchSongs() {
        ArrayList<String> songList = new ArrayList<>();
        try {
            ContentResolver contentResolver = getContentResolver();
            Uri collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

            String[] projection = {MediaStore.Audio.Media.TITLE};
            String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

            Cursor cursor = contentResolver.query(collection, projection, selection, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                do {
                    String songTitle = cursor.getString(titleIndex);
                    if (songTitle != null) {
                        songList.add(songTitle);
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error fetching songs: " + e.getMessage());
        }
        return songList;
    }

    private ArrayList<String> fetchSongPaths() {
        ArrayList<String> songPaths = new ArrayList<>();
        try {
            ContentResolver contentResolver = getContentResolver();
            Uri collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

            String[] projection = {MediaStore.Audio.Media.DATA};
            String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

            Cursor cursor = contentResolver.query(collection, projection, selection, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int pathIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                do {
                    String songPath = cursor.getString(pathIndex);
                    if (songPath != null) {
                        songPaths.add(songPath);
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error fetching song paths: " + e.getMessage());
        }
        return songPaths;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                fetchSongsAndDisplay();
            } else {
                Toast.makeText(this, "Permission denied. Cannot access songs.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
