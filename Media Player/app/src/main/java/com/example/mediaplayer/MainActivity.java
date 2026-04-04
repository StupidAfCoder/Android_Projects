package com.example.mediaplayer;

import android.app.AlertDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private boolean isAudioMode = false;
    private Uri currentAudioUri;
    private ActivityResultLauncher<String> pickAudioFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VideoView videoView   = findViewById(R.id.videoView);
        Button OpenFile = findViewById(R.id.btnOpenFile);
        Button OpenUrl = findViewById(R.id.btnOpenUrl);
        Button Play        = findViewById(R.id.btnPlay);
        Button Pause = findViewById(R.id.btnPause);
        Button Stop = findViewById(R.id.btnStop);
        Button Restart = findViewById(R.id.btnRestart);

        // Register the file picker — GetContent() opens system file chooser
        pickAudioFile = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        currentAudioUri = uri;
                        isAudioMode = true;
                        if (mediaPlayer != null) { mediaPlayer.release(); mediaPlayer = null; }
                        videoView.setVisibility(View.GONE);
                        Toast.makeText(this, "Audio loaded! Press Play.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Open File
        OpenFile.setOnClickListener(v -> pickAudioFile.launch("audio/*"));

        // Open URL
        OpenUrl.setOnClickListener(v -> {
            EditText input = new EditText(this);
            input.setHint("Paste video URL");
            input.setText("https://www.w3schools.com/html/mov_bbb.mp4"); // test URL

            new AlertDialog.Builder(this)
                    .setTitle("Enter Video URL")
                    .setView(input)
                    .setPositiveButton("Load", (dialog, which) -> {
                        String url = input.getText().toString().trim();
                        if (!url.isEmpty()) {
                            isAudioMode = false;
                            videoView.setVisibility(View.VISIBLE);
                            videoView.setVideoURI(Uri.parse(url));
                            videoView.requestFocus();
                            Toast.makeText(this, "URL loaded! Press Play.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Play
        Play.setOnClickListener(v -> {
            if (isAudioMode) {
                if (mediaPlayer == null) {
                    // MediaPlayer.create() prepares the player with the file URI
                    mediaPlayer = MediaPlayer.create(this, currentAudioUri);
                }
                if (mediaPlayer != null) mediaPlayer.start();
            } else {
                videoView.start(); // VideoView handles buffering + play internally
            }
        });

        // Pause
        Pause.setOnClickListener(v -> {
            if (isAudioMode) {
                if (mediaPlayer != null) mediaPlayer.pause();
            } else {
                if (videoView.isPlaying()) videoView.pause();
            }
        });

        // STOP
        Stop.setOnClickListener(v -> {
            if (isAudioMode) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            } else {
                videoView.stopPlayback();
            }
        });

        // restart
        Restart.setOnClickListener(v -> {
            if (isAudioMode) {
                if (mediaPlayer != null) { mediaPlayer.seekTo(0); mediaPlayer.start(); }
            } else {
                videoView.seekTo(0); videoView.start();
            }
        });
    }

    // ALWAYS
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) { mediaPlayer.release(); mediaPlayer = null; }
    }
}