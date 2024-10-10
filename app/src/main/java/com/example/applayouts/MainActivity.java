package com.example.applayouts;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;



import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {
    private Button btnConvertir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btVolver = findViewById(R.id.buttonMap);
        btVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Mapa.class);
                startActivity(intent);
            }
        });
    }

}
