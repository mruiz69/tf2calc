package com.example.applayouts;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import com.q42.android.scrollingimageview.ScrollingImageView;

import java.io.IOException;

public class Inicio extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btComenzar = findViewById(R.id.btComenzar);
        btComenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Inicio.this, MainActivity.class);
                startActivity(intent);
            }
        });
        ScrollingImageView bgScroll = findViewById(R.id.bgScroll);
        bgScroll.start();
        playAudio();
    }

    public void playAudio()
    {
        ExoPlayer player = new ExoPlayer.Builder(getApplicationContext()).build();
        MediaItem mediaItem = MediaItem.fromUri("android.resource://"+getPackageName()+"/raw/moregunv3");
        // Set the media item to be played.
        player.setMediaItem(mediaItem);
        // Prepare the player.
        player.prepare();
        // Start the playback.
        player.play();
        //MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.moregunv3);
        //mediaPlayer.setLooping(true);
        //mediaPlayer.start(); // no need to call prepare(); create() does that for you
    }

}