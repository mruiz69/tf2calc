package com.tf2calc.applayouts;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Firebase extends AppCompatActivity {

    FirebaseFirestore db;
    private EditText txtCodigo,txtNombre,txtDescripcion;
    private ListView lista;
    private Spinner spinnerClase,spinnerRareza;
    String [] Clases = {"Scout","Soldier","Heavy","Demoman","Pyro","Engineer","Spy","Medic","Sniper"};
    String [] Rarezas = {"Normal","Vintage","Strange","Haunted"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_firebase);
        //llamamos al metodo que carga la lista
        cargarListaFirestore();
        //inicializar firestore
        db = FirebaseFirestore.getInstance();

        //Uno las variables con los id del xml
        txtCodigo = findViewById(R.id.txtCodigo);
        txtNombre = findViewById(R.id.txtNombre);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        spinnerClase =findViewById(R.id.spinnerClase);
        spinnerRareza =findViewById(R.id.spinnerRareza);
        lista = findViewById(R.id.listaDeArmas);
        //ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
        //    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        //    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        //    return insets;
        //});
        //poblar Spinners
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, Clases);
        //Establecemos Layout
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        //asignamos adaptador a Spinner
        spinnerClase.setAdapter(adapter);

        adapter = new ArrayAdapter<>(this, R.layout.spinner_item, Rarezas);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerRareza.setAdapter(adapter);
    }
    public void cargarListaFirestore(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("armas")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<String> listaArmas = new ArrayList<>();
                            for(QueryDocumentSnapshot document : task.getResult()){
                                String linea = "||"+document.getString("codigo")+ "||"+
                                        document.getString("nombre")+"||"+
                                        document.getString("descripcion")+"||"+
                                        document.getString("rareza")+"||"+
                                        document.getString("clase")+"||";
                                listaArmas.add(linea);
                            }
                            for (String arma : listaArmas) {
                                Log.d("Firebase", "Arma: " + arma);
                            }
                            ArrayAdapter<String> adaptador = new ArrayAdapter<>(Firebase.this, android.R.layout.simple_list_item_1, listaArmas);
                            lista.setAdapter(adaptador);
                        } else {
                            Log.e("TAG","Error al obtener datos de Firestore",task.getException());
                        }
                    }
                });
    }

    public void enviarDatosFirestore(View view){
        String codigo = txtCodigo.getText().toString();
        String nombre = txtNombre.getText().toString();
        String desc = txtDescripcion.getText().toString();
        String clase = spinnerClase.getSelectedItem().toString();
        String rareza = spinnerRareza.getSelectedItem().toString();

        //creamos mapa con datos a enviar
        Map<String, Object> arma = new HashMap<>();
        arma.put("codigo",codigo);
        arma.put("nombre",nombre);
        arma.put("descripcion",desc);
        arma.put("clase",clase);
        arma.put("rareza",rareza);

        //Enviamos a Firestore
        db.collection("armas")
                .document(codigo)
                .set(arma)
                .addOnSuccessListener(aVoid ->{
                    Toast.makeText(Firebase.this, "Datos enviados correctamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Firebase.this, "Error al enviar datos a Firestore", Toast.LENGTH_SHORT).show();
                });
    }
    public void cargarLista(View view){
        cargarListaFirestore();
    }
}