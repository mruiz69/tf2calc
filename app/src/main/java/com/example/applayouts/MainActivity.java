package com.example.applayouts;

import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        btVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Mapa.class);
                startActivity(intent);
            }
        });
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
