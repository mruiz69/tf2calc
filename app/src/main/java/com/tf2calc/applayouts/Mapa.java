package com.tf2calc.applayouts;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import androidx.appcompat.widget.AppCompatSpinner;

import org.osmdroid.api.IMapView;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import com.q42.android.scrollingimageview.ScrollingImageView;

public class Mapa extends AppCompatActivity {
    private ImageView imageView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.mapa);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mapa), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressBar = findViewById(R.id.progress_bar);


        //Cargarconfiguracion de mapa predeterminado
        Configuration.getInstance().load(getApplicationContext(),PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        //Obtener referencia a MapView
        MapView mv = findViewById(R.id.mapView);

        //Establece fuente azuejos a MAPNIK
        mv.setTileSource(TileSourceFactory.MAPNIK);

        //Activa controles zoom en mapa
        mv.setBuiltInZoomControls(true);

        //Activa control multitactil
        mv.setMultiTouchControls(true);

        //Coordenadas IP Santo Tomas
        double ipstLatitud = -33.4493141;
        double ipstLongitud = -70.6624069;

        //Creacion ovjetos GeoPoint
        GeoPoint ipstGeoPoint = new GeoPoint(ipstLatitud,ipstLongitud);

        //Confiugurar vista centrada
        mv.getController().setZoom(15.0);

        //Centrar mapa en punto
        mv.getController().setCenter(ipstGeoPoint);

        //creamos marcadaor para el IP
        Marker marcadorST = new Marker(mv);

        //Establecemos posicion del marcador
        marcadorST.setPosition(ipstGeoPoint);

        //Establecemos ancla
        marcadorST.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        //Establecemos Título de marcador
        marcadorST.setTitle("IP Santo Tomás Santiago Centro");
        //Establecemos descripcion
        marcadorST.setSnippet("Instituto Profesional y Centro de formación tecnica, sede Santiago Centro");

        //Agregamos marcadores
        mv.getOverlays().add(marcadorST);



        //Coordenadas Parque
        double parqueLatitud = -33.4625076;
        double parqueLongitud = -70.6600286;

        //Creacion objetos GeoPoint
        GeoPoint parqueGeoPoint = new GeoPoint(parqueLatitud,parqueLongitud);


        //creamos marcadaor para el Parque
        Marker marcadorParque = new Marker(mv);

        //Establecemos posicion del marcador
        marcadorParque.setPosition(parqueGeoPoint);

        //Establecemos ancla
        marcadorParque.setAnchor(0.2f,0.4f);

        //Ajustamos la posicion de titulo y desc
        marcadorParque.setInfoWindowAnchor(0.2f,0.0f);
        //Establecemos Título de marcador
        marcadorParque.setTitle("Parque O'Higgins");
        //Establecemos descripcion
        marcadorParque.setSnippet("Famoso parque en el cual se encuentra Fantasilandia y Movistar Arena");
        //Agregamos Icono
        marcadorParque.setIcon(getResources().getDrawable(R.drawable.tf2logopointer));
        //Agregamos marcadores
        mv.getOverlays().add(marcadorParque);


        //Configuramos el Spinner
        Spinner tipoMapaSpinner = findViewById(R.id.spinnerMapa);
        //Definimos Array con los tipos de mapas
        String[] mapTypes = {"Mapa normal","Mapa de transporte","Mapa Topográfico"};

        //Creamos ArrayAdapter para poblar Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, mapTypes);
        //Establecemos Layout
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        //asignamos adaptador a Spinner
        tipoMapaSpinner.setAdapter(adapter);

        //Listener para detectar cambios en seleccion spinner
        tipoMapaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mv.setTileSource(TileSourceFactory.MAPNIK);
                        progressBar.setVisibility(View.VISIBLE); // Muestra el ProgressBar
                        new LoadImageTask().execute();
                        break;
                    case 1:
                        mv.setTileSource(new XYTileSource(
                                "PublicTransport", 0, 18, 256, ".png", new String[]{
                                "https://tile.memomaps.de/tilegen/"}));
                        progressBar.setVisibility(View.VISIBLE); // Muestra el ProgressBar
                        new LoadImageTask().execute();
                        break;
                    case 2:
                        mv.setTileSource(new XYTileSource(
                                "USGS_Satellite", 0, 18, 256, ".png", new String[]{
                                "https://a.tile.opentopomap.org/",
                                "https://b.tile.opentopomap.org/",
                                "https://c.tile.opentopomap.org/"}));
                        progressBar.setVisibility(View.VISIBLE); // Muestra el ProgressBar
                        new LoadImageTask().execute();
                        break;
                }
            }

            //No se hacen operaciones cuando no se selecciona elemento
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tipoMapaSpinner.setSelection(0);

        Button btVolver = findViewById(R.id.btVolver);
        btVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //Intent intent = new Intent(Mapa.this, MainActivity.class);
                //startActivity(intent);
            }
        });
    }
    private class LoadImageTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Opcional: puedes mostrar un mensaje o animación aquí.
        }
        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                Thread.sleep(3000); // Simula la carga
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 1; // Reemplaza con tu imagen
        }
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            //imageView.setImageResource(result);
            progressBar.setVisibility(View.GONE); // Oculta el ProgressBar
        }
    }

}
