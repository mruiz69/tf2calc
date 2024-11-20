package com.tf2calc.applayouts;

import android.content.Intent;
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
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.q42.android.scrollingimageview.ScrollingImageView;

public class Inicio extends AppCompatActivity  implements SensorEventListener{

    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private TextView proximityTextView;

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

        proximityTextView = findViewById(R.id.proximityTextView);

        // Initialize the SensorManager and proximity sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (proximitySensor != null) {
            // Register the listener for proximity sensor events
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            // If the device doesn't have a proximity sensor, display a message
            proximityTextView.setText("Proximity sensor not available");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float distance = event.values[0];
            if (distance < proximitySensor.getMaximumRange()) {
                // Object is near, dim the screen
                proximityTextView.setText("HAY ALGO CERCA");
                setScreenBrightness(0.1f); // Dim screen brightness to 10%
            } else {
                // Object is far, restore the brightness
                proximityTextView.setText("NADA CERCA");
                setScreenBrightness(1.0f); // Set screen brightness to 100%
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle sensor accuracy changes (optional)
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the sensor listener to save battery
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Re-register the sensor listener
        if (proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    /**
     * Method to set the screen brightness
     * @param brightness: A value between 0.0f (minimum brightness) and 1.0f (maximum brightness)
     */
    private void setScreenBrightness(float brightness) {
        // Get the current window attributes
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        // Set screen brightness (value between 0 and 1)
        layoutParams.screenBrightness = brightness;
        // Apply the brightness change
        getWindow().setAttributes(layoutParams);
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