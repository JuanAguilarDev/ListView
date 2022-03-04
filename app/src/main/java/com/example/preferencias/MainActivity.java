package com.example.preferencias;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    // Almacenamiento en el dispositivo, temas, alias y cosas de tamaño pequeño

    EditText et1;
    ArrayList<String> datos;
    ArrayList<String> datosCambiados; // Datos cambiados
    ListView lv1; // Cambio
    ListView lv2; // Inicial
    int posicion = 0;
    boolean bandera = false;
    SharedPreferences preferencias;
    SharedPreferences preferenciasCambiadas;
    ArrayAdapter<String> adaptador;
    ArrayAdapter<String> adaptadorCambiado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et1 = findViewById(R.id.et1);
        lv1 = findViewById(R.id.lv01);
        lv2 = findViewById(R.id.lv02);
        datos = new ArrayList<String>();
        datosCambiados = new ArrayList<String>();
        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datos);
        adaptadorCambiado = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datosCambiados);
        lv1.setAdapter(adaptadorCambiado);
        lv2.setAdapter(adaptador);

        preferencia();
        Toast.makeText(MainActivity.this, "Contenido: "+datosCambiados.size(), Toast.LENGTH_LONG).show();

        lv1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                int posicionCambiada = i;
                AlertDialog.Builder dialogo = new AlertDialog.Builder(MainActivity.this);
                dialogo.setTitle("Aviso");
                dialogo.setMessage("Seguro que quieres eliminar?");
                dialogo.setCancelable(false);
                dialogo.setPositiveButton("si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String dato = datosCambiados.get(posicionCambiada);

                        SharedPreferences.Editor elemento = preferenciasCambiadas.edit();
                        Toast.makeText(MainActivity.this, "elemento: " + dato, Toast.LENGTH_LONG);
                        elemento.remove(dato.trim());
                        elemento.apply();

                        datosCambiados.remove(posicionCambiada);
                        Toast.makeText(MainActivity.this, "Contenido: "+datosCambiados.size(), Toast.LENGTH_LONG).show();
                        // Siempre que se realice un cambio debe actualizar
                        adaptadorCambiado.notifyDataSetChanged();
                    }
                });

                dialogo.setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                });

                dialogo.show();

                return false;
            }
        });

        lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                posicion = i;
                bandera = true;
            }
        });



    }



    public void cambiar(View view){
        // Tambien lo eliminamos de las preferencias anteriores y lo guardamos en las preferencias nuevas
        if(bandera){
            datosCambiados.add(lv2.getItemAtPosition(posicion).toString());
            guardarCambiadas();
            eliminarAnteriores(); // Eliminamos
            datos.remove(posicion);
            adaptador.notifyDataSetChanged();
            adaptadorCambiado.notifyDataSetChanged();
            bandera = false;
            Toast.makeText(this, "Cambiado", Toast.LENGTH_SHORT).show();
        }else if(datos.isEmpty()){
            Toast.makeText(this, "Agregue un elemento", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Seleccione un elemento", Toast.LENGTH_SHORT).show();
        }
    }

    public void guardarCambiadas(){
        SharedPreferences.Editor editor = preferenciasCambiadas.edit();
        editor.putString( datos.get(posicion).trim(), datos.get(posicion).trim());
        editor.apply();
        Toast.makeText(this, "Guardado en cambiadas", Toast.LENGTH_SHORT).show();
    }

    public void eliminarAnteriores(){
        String dato = datos.get(posicion);

        SharedPreferences.Editor elemento = preferencias.edit();
        elemento.remove(dato.trim());
        elemento.apply();

    }

    public void guardar(View view){
        if(et1.getText().toString().isEmpty()){
            Toast.makeText(this, "Ingrese un dato primero", Toast.LENGTH_SHORT).show();
        }else{
            // Agregamos el dato a la lista
            datos.add(et1.getText().toString().trim());
            adaptador.notifyDataSetChanged();

            // Guardarlo en preferencias
            SharedPreferences.Editor editor = preferencias.edit();
            editor.putString( et1.getText().toString(), et1.getText().toString());
            editor.apply();
            Toast.makeText(this, "Guardado", Toast.LENGTH_SHORT).show();

            et1.setText("");
        }
    }

    public void preferencia(){
        preferencias = getSharedPreferences("datos", Context.MODE_PRIVATE);
        preferenciasCambiadas = getSharedPreferences("datosCambiados", Context.MODE_PRIVATE);
        Map<String, ?> claves = preferencias.getAll();
        Map<String, ?> clavesCambiadas = preferenciasCambiadas.getAll();
        // Mostramos ambas preferencias gardadas
        for(Map.Entry<String, ?> elem: clavesCambiadas.entrySet()){
            datosCambiados.add(" "+elem.getValue().toString());
        }
        for(Map.Entry<String, ?> elem: claves.entrySet()){
            datos.add(" "+elem.getValue().toString());
        }
    }


}