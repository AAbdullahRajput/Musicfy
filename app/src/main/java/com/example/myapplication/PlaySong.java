package com.example.myapplication;

import android.animation.ValueAnimator;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class PlaySong extends AppCompatActivity {

    private static final String TAG = "PlaySongActivity";
    private TextView songTitle, currentTime, totalTime;
    private Button playPauseButton, forwardButton, previousButton;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private ArrayList<String> songPaths;
    private int currentIndex; // Current song index
    private boolean isPlaying = false;
    private final Handler handler = new Handler();
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Initialize UI components
        toolbar = findViewById(R.id.toolbar);
        songTitle = findViewById(R.id.songTitle);
        playPauseButton = findViewById(R.id.playPauseButton);
        seekBar = findViewById(R.id.songSeekBar);
        currentTime = findViewById(R.id.currentTime);
        totalTime = findViewById(R.id.totalTime);
        forwardButton = findViewById(R.id.forwardButton);
        previousButton = findViewById(R.id.previousButton);

        // Setup Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Get data from intent
        songPaths = getIntent().getStringArrayListExtra("songPaths");
        currentIndex = getIntent().getIntExtra("currentIndex", 0);

        if (songPaths == null || songPaths.isEmpty()) {
            Toast.makeText(this, "Error: No songs found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Play the current song
        playSong();

        // Set up buttons
        playPauseButton.setOnClickListener(v -> togglePlayback());
        forwardButton.setOnClickListener(v -> playNextSong());
        previousButton.setOnClickListener(v -> playPreviousSong());

        configureSeekBar();
    }

    private void playSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        String songPath = songPaths.get(currentIndex);
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this, Uri.parse(songPath));
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;
            playPauseButton.setText("Pause");

            // Update UI
            songTitle.setText(new File(songPath).getName());
            totalTime.setText(formatTime(mediaPlayer.getDuration()));
            seekBar.setMax(mediaPlayer.getDuration());
            seekBar.setProgress(0);

            // On song completion, play the next song
            mediaPlayer.setOnCompletionListener(mp -> playNextSong());
        } catch (IOException e) {
            Log.e(TAG, "Error playing song: " + e.getMessage());
            Toast.makeText(this, "Error playing song", Toast.LENGTH_SHORT).show();
        }
    }

    private void playNextSong() {
        currentIndex = (currentIndex + 1) % songPaths.size(); // Loop back to the first song
        playSong();
    }

    private void playPreviousSong() {
        currentIndex = (currentIndex - 1 + songPaths.size()) % songPaths.size(); // Loop back to the last song
        playSong();
    }

    // Format time for display in mm:ss format
    private String formatTime(int timeInMillis) {
        int minutes = (timeInMillis / 1000) / 60;
        int seconds = (timeInMillis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // Runnable to update the SeekBar and current time
    private final Runnable updateSeekBarRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    // Update SeekBar progress
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    // Update current time display
                    currentTime.setText(formatTime(mediaPlayer.getCurrentPosition()));
                    // Schedule the next update
                    handler.postDelayed(this, 1000);
                }
            } catch (IllegalStateException e) {
                // MediaPlayer is no longer valid, stop updating
                handler.removeCallbacks(this);
            }
        }
    };

    // Configure the SeekBar
    private void configureSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Stop the Runnable while user interacts with the SeekBar
                handler.removeCallbacks(updateSeekBarRunnable);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Seek MediaPlayer to the new position
                mediaPlayer.seekTo(seekBar.getProgress());
                // Resume updating SeekBar and time
                handler.post(updateSeekBarRunnable);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // Update MediaPlayer position and current time if the user interacts
                    mediaPlayer.seekTo(progress);
                    currentTime.setText(formatTime(progress));
                }
            }
        });

        // Start updating the SeekBar and time
        handler.postDelayed(updateSeekBarRunnable, 1000);
    }

    // Call this method to initialize the SeekBar
//    private void initializeSeekBar() {
//        seekBar.setMax(mediaPlayer.getDuration());
//        totalTime.setText(formatTime(mediaPlayer.getDuration())); // Set total time
//        configureSeekBar();
//    }


//    private final Runnable updateSeekBarRunnable = new Runnable() {
//        @Override
//        public void run() {
//            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//                seekBar.setProgress(mediaPlayer.getCurrentPosition());
//                currentTime.setText(formatTime(mediaPlayer.getCurrentPosition()));
//                handler.postDelayed(this, 1000);
//            }
//        }
//    };

    private void togglePlayback() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playPauseButton.setText("Play");
            isPlaying = false;
            // Stop updating SeekBar when paused
            handler.removeCallbacks(updateSeekBarRunnable);
        } else {
            mediaPlayer.start();
            playPauseButton.setText("Pause");
            isPlaying = true;
            // Start updating SeekBar when resumed
            handler.postDelayed(updateSeekBarRunnable, 1000);
        }
    }


//    private String formatTime(int timeInMillis) {
//        if (timeInMillis < 0) {
//            timeInMillis = 0; // Handle negative values
//        }
//        int totalSeconds = timeInMillis / 1000;
//        int minutes = totalSeconds / 60;
//        int seconds = totalSeconds % 60;
//        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null; // Clean up the mediaPlayer object reference
        }
        handler.removeCallbacks(updateSeekBarRunnable); // Stop the updates when the activity is destroyed
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
