package com.example.applayouts;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class Armas extends AppCompatActivity {

    // Variables para los elementos de la interfaz de usuario
    EditText editTextName, editTextDescription;
    Button buttonAdd, buttonUpdate, buttonDelete;
    ListView listView;
    DatabaseHelper dbHelper;
    ArrayList<String> itemList; // Lista para mostrar los elementos
    ArrayAdapter<String> adapter; // Adaptador para ListView
    int selectedItemID = -1; // ID del elemento seleccionado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_armas);
        // Conectar elementos de la interfaz con las variables
        editTextName = findViewById(R.id.editTextName);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonDelete = findViewById(R.id.buttonDelete);
        listView = findViewById(R.id.listView);
        // Inicializar la base de datos y el adaptador
        dbHelper = new DatabaseHelper(this);
        itemList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemList);
        listView.setAdapter(adapter);
        // Cargar datos iniciales
        loadData();
        // Botón para agregar un nuevo elemento
        buttonAdd.setOnClickListener(v -> {
            String name = editTextName.getText().toString();
            String description = editTextDescription.getText().toString();
            if (name.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                long id = dbHelper.insertItem(name, description);
                if (id != -1) {
                    Toast.makeText(this, "Item agregado", Toast.LENGTH_SHORT).show();
                    loadData();
                    clearFields();
                } else {
                    Toast.makeText(this, "Error al agregar", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Botón para actualizar un elemento
        buttonUpdate.setOnClickListener(v -> {
            String name = editTextName.getText().toString();
            String description = editTextDescription.getText().toString();
            if (selectedItemID == -1) {
                Toast.makeText(this, "Seleccione un item para actualizar", Toast.LENGTH_SHORT).show();
                return;
            }
            if (name.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                int result = dbHelper.updateItem(selectedItemID, name, description);
                if (result > 0) {
                    Toast.makeText(this, "Item actualizado", Toast.LENGTH_SHORT).show();
                    loadData();
                    clearFields();
                    selectedItemID = -1;
                } else {
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Botón para eliminar un elemento
        buttonDelete.setOnClickListener(v -> {
            if (selectedItemID == -1) {
                Toast.makeText(this, "Seleccione un item para eliminar", Toast.LENGTH_SHORT).show();
                return;
            }
            int result = dbHelper.deleteItem(selectedItemID);
            if (result > 0) {
                Toast.makeText(this, "Item eliminado", Toast.LENGTH_SHORT).show();
                loadData();
                clearFields();
                selectedItemID = -1;
            } else {
                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        });
        // Seleccionar un elemento de la lista
        listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            String selectedItem = itemList.get(position);
            String[] parts = selectedItem.split(": ");
            selectedItemID = Integer.parseInt(parts[0]);
            Cursor cursor = dbHelper.getItemById(selectedItemID);
            if (cursor != null && cursor.moveToFirst()) {
                editTextName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)));
                editTextDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION)));
                cursor.close();
            }
        });
    }
    // Cargar datos del ListView desde la base de datos
    private void loadData() {
        itemList.clear();
        Cursor cursor = dbHelper.getAllItems();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));
                itemList.add(id + ": " + name + " - " + description);
            }
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }
    // Limpiar los campos de entrada
    private void clearFields() {
        editTextName.setText("");
        editTextDescription.setText("");
    }
}