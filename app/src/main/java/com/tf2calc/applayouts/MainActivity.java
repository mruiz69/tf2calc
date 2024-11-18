package com.tf2calc.applayouts;

import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button btnConvertir;
    private static final int PICK_IMAGE_REQUEST = 1;
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<Uri> imageUriList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btVolver = findViewById(R.id.buttonMap);
        Button btCalcular = findViewById(R.id.buttonConvertir);
        Button btnSelectImage = findViewById(R.id.btnSelectImage);

        recyclerView = findViewById(R.id.recyclerView);
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

                if(ETcantidad.getText().toString().isBlank() || ETcantidad.getText().toString().isEmpty())
                {
                    return;
                }
                int cantidad = Integer.parseInt(ETcantidad.getText().toString());
                DecimalFormat df = new DecimalFormat("#.##");

                double compra=0;
                for(int i=0; i<cantidad; i++)
                {
                    compra=compra+69.4444444;
                }

                df.setRoundingMode(RoundingMode.DOWN);
                if(compra-Math.floor(compra)>0.99)
                {
                    compra+=0.01;
                    compra = Double.parseDouble(df.format(compra));
                }
                else {
                    compra = Double.parseDouble(df.format(compra));
                }

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
