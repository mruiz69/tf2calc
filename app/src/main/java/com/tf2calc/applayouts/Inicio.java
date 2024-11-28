package com.tf2calc.applayouts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.q42.android.scrollingimageview.ScrollingImageView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Inicio extends AppCompatActivity  implements SensorEventListener{

    private static String mqttHost = "tcp://rhinestonebeak446.cloud.shiftr.io:1883";//ip se dervidor mqtt
    private static String idUsuario = "AppAndroid";

    private static String topico = "Mensaje";
    private static String user = "rhinestonebeak446";
    private static String pass = "Z1Okl4w0XPX5dOgW";
    private MqttClient mqttClient;
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private Sensor lightSensor;
    private TextView proximityTextView;
    private TextView lightValueTextView;
    private ExoPlayer player;

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

        try{
            //crea cliente mqtt
            mqttClient = new MqttClient(mqttHost,idUsuario,null);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(user);
            options.setPassword(pass.toCharArray());
            //conexion a mqtt
            mqttClient.connect(options);
            //si se conecta imprime
            Toast.makeText(this,"Aplicacion conectada a servidor MQTT", Toast.LENGTH_SHORT).show();
            //manejo de entrega de datos y perdida de conexion
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d("MQTT","Conexión Perdida");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String payload = new String(message.getPayload());
                    //runOnUiThread(() -> textView.setText(payload));
                }
                //metodo para verificar si envio fue exitoso
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d("MQTT","Entrega Completa");
                }
            });
        }
        catch (MqttException e){
            e.printStackTrace();
        }

        Button btComenzar = findViewById(R.id.btComenzar);
        btComenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mensaje = "INICIO DE APLICACION DETECTADO";
                try {
                    if(mqttClient != null && mqttClient.isConnected()){
                        //publicar mensaje en topico especificado
                        mqttClient.publish(topico, mensaje.getBytes(),0,false);
                        //mostrar mensaje enviado en el textview
                        //textView.append("\n - "+mensaje);
                        Toast.makeText(Inicio.this,"Mensaje Enviado", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(Inicio.this,"Error: No se pudo enviar mensaje. La conexion MQTT no esta activa.",Toast.LENGTH_SHORT).show();
                    }
                }
                catch (MqttException e){
                    e.printStackTrace();
                }
                Intent intent = new Intent(Inicio.this, MainActivity.class);
                startActivity(intent);
            }
        });
        ScrollingImageView bgScroll = findViewById(R.id.bgScroll);
        bgScroll.start();
        playAudio();

        // Initialize the SensorManager and proximity sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximityTextView = findViewById(R.id.proximityTextView);

        // Get Light Sensor
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        // UI Element to Display Light Sensor Data
        lightValueTextView = findViewById(R.id.lightValue);

        // Check if Light Sensor is Available
        if (lightSensor == null) {
            lightValueTextView.setText("No hay sensor de luz disponible");
        }


        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (proximitySensor != null) {
            // Register the listener for proximity sensor events
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            // If the device doesn't have a proximity sensor, display a message
            proximityTextView.setText("No hay sensor de proximidad disponible");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lightLevel = event.values[0];
            lightValueTextView.setText(String.format("Nivel de luz: %.2f lux", lightLevel));
        } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float proximityValue = event.values[0];
            proximityTextView.setText(String.format("Proximidad: %.2f cm", proximityValue));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle sensor accuracy changes (optional)
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Stop audio playback
        stopAudio();
        // Unregister the sensor listener to save battery
        if (proximitySensor != null) {
            sensorManager.unregisterListener(this, proximitySensor);
        }

        if (lightSensor != null) {
            sensorManager.unregisterListener(this, lightSensor);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Resume audio playback
        playAudio();
        // Re-register the sensor listener
        if (proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
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
        if(player==null)
        {
            player = new ExoPlayer.Builder(getApplicationContext()).build();
            MediaItem mediaItem = MediaItem.fromUri("android.resource://"+getPackageName()+"/raw/moregunv3");
            // Set the media item to be played.
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
        }

    }
    public void stopAudio() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

}