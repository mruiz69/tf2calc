package com.tf2calc.applayouts;

import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static String mqttHost = "tcp://rhinestonebeak446.cloud.shiftr.io:1883";//ip se dervidor mqtt
    private static String idUsuario = "AppAndroid";

    private static String topico = "Mensaje";
    private static String user = "rhinestonebeak446";
    private static String pass = "Z1Okl4w0XPX5dOgW";
    private MqttClient mqttClient;


    private Button btnConvertir;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageAdapter imageAdapter;
    private List<Uri> imageUriList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


        Button btVolver = findViewById(R.id.buttonMap);
        Button btCalcular = findViewById(R.id.buttonConvertir);
        Button btnSelectImage = findViewById(R.id.btnSelectImage);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        // Configurar el RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        imageAdapter = new ImageAdapter(this, imageUriList);
        recyclerView.setAdapter(imageAdapter);
        // Evento para seleccionar imágenes
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        Button botonRegistrar = findViewById(R.id.botonItems);
        botonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Armas.class);
                startActivity(intent);
            }
        });

        btCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText ETcantidad = findViewById(R.id.Llaves);
                EditText ETcompra = findViewById(R.id.ResultadoCompra);
                EditText ETventa = findViewById(R.id.ResultadoVenta);

                String mensaje = ETcantidad.getText()+" Llaves calculadas";
                try {
                    if(mqttClient != null && mqttClient.isConnected()){
                        //publicar mensaje en topico especificado
                        mqttClient.publish(topico, mensaje.getBytes(),0,false);
                        //mostrar mensaje enviado en el textview
                        //textView.append("\n - "+mensaje);
                        Toast.makeText(MainActivity.this,"Mensaje Enviado", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(MainActivity.this,"Error: No se pudo enviar mensaje. La conexion MQTT no esta activa.",Toast.LENGTH_SHORT).show();
                    }
                }
                catch (MqttException e){
                    e.printStackTrace();
                }



                if(ETcantidad.getText().toString().isEmpty())
                {
                    return;
                }
                int cantidad = Integer.parseInt(ETcantidad.getText().toString());
                DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
                df.applyPattern("#.##");

                double compra=0;
                for(int i=0; i<cantidad; i++)
                {
                    compra=compra+69.4444444;
                }

                df.setRoundingMode(RoundingMode.DOWN);
                double compraRedondeada = compra-Math.floor(compra);
                if(compraRedondeada>0.99)
                {
                    compra+=0.01;
                }
                String compraFormato = df.format(compra);
                compra = Double.parseDouble(compraFormato);

                double venta = 0;
                for(int i=0; i<cantidad; i++)
                {
                    venta=venta+69.1111111;
                }

                df.setRoundingMode(RoundingMode.DOWN);
                if(venta-Math.floor(venta)>0.99)
                {
                    venta+=0.01;
                    venta = Double.parseDouble(df.format(venta));
                }
                else{
                    venta = Double.parseDouble(df.format(venta));
                }
                llenarMetal(compra, venta);
                ETcompra.setText(String.valueOf(compra));
                ETventa.setText(String.valueOf(venta));
            }
        });

        btVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Mapa.class);
                startActivity(intent);
            }
        });
    }

    //metodo para llenar los campos que especifican la cantidad de cada metal requerida
    public void llenarMetal(double compra, double venta)
    {
        TextView TVRefCompra = findViewById(R.id.cantidadRefCompra);
        TextView TVRefVenta = findViewById(R.id.cantidadRefVenta);
        TextView TVRecCompra = findViewById(R.id.cantidadRecCompra);
        TextView TVRecVenta = findViewById(R.id.cantidadRecVenta);
        TextView TVScpCompra = findViewById(R.id.cantidadScpCompra);
        TextView TVScpVenta = findViewById(R.id.cantidadScpVenta);

        double c = compra;
        double v = venta;
        //comenzamos calculando valor REF compra y venta
        TVRefCompra.setText(": "+(int)Math.floor(compra));
        TVRefVenta.setText(": "+(int)Math.floor(venta));

        //calculamos valor REC compra y venta, restando el ref
        c -= Math.floor(c);
        int cantidadRecCompra=0;
        v -= Math.floor(v);
        int cantidadRecVenta=0;
        while(c >=0.33)
        {
            cantidadRecCompra++;
            c=c-0.33;
        }
        TVRecCompra.setText(": "+cantidadRecCompra);

        while(v >=0.33)
        {
            cantidadRecVenta++;
            v=v-0.33;
        }
        TVRecVenta.setText(": "+cantidadRecVenta);

        //calculamos valor SCP
        int cantidadScpCompra=0;
        int cantidadScpVenta=0;
        while(c >0)
        {
            cantidadScpCompra++;
            c=c-0.11;
        }
        TVScpCompra.setText(": "+cantidadScpCompra);

        while(v >0)
        {
            cantidadScpVenta++;
            v=v-0.11;
        }
        TVScpVenta.setText(": "+cantidadScpVenta);
    }
    // Método para seleccionar imágenes desde la galería
    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            imageUriList.add(imageUri); // Añadir la imagen seleccionada a la lista
            imageAdapter.notifyDataSetChanged(); // Notificar al adaptador para actualizar el RecyclerView
        }
    }


}
