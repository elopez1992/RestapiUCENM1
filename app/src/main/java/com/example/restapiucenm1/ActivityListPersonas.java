package com.example.restapiucenm1;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.restapiucenm1.config.Personas;
import com.example.restapiucenm1.config.RestApiMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityListPersonas extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Personas> personaList;
    private ArrayList<String> listaNombres;
    private ArrayAdapter<String> adapter;
    private RequestQueue requestQueue;

    private static final String TAG = "ActivityListPersonas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_personas);

        listView = findViewById(R.id.listview);
        personaList = new ArrayList<>();
        listaNombres = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaNombres);
        listView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);

        obtenerPersonas();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Personas p = personaList.get(position);

            new AlertDialog.Builder(ActivityListPersonas.this)
                    .setTitle("Seleccione una opción")
                    .setItems(new CharSequence[]{"Actualizar", "Eliminar"}, (dialog, which) -> {
                        if (which == 0) {
                            // Abrir ActivityCreate pasando datos para actualizar
                            Intent intent = new Intent(ActivityListPersonas.this, ActivityCreate.class);
                            intent.putExtra("id", p.getId());
                            intent.putExtra("nombres", p.getNombres());
                            intent.putExtra("apellidos", p.getApellidos());
                            intent.putExtra("direccion", p.getDireccion());
                            intent.putExtra("telefono", p.getTelefono());
                            intent.putExtra("fechanac", p.getFechanac());
                            intent.putExtra("foto", p.getFoto());
                            startActivity(intent);
                        } else {
                            eliminarPersona(p.getId());
                        }
                    }).show();
        });
    }

    private void obtenerPersonas() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, RestApiMethods.EndpointGetPersons, null,
                response -> {
                    Log.d(TAG, "Respuesta JSON: " + response.toString());

                    personaList.clear();
                    listaNombres.clear();

                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject persona = response.getJSONObject(i);

                            String id = persona.optString("id", "");
                            String nombres = persona.optString("nombres", "");
                            String apellidos = persona.optString("apellidos", "");
                            String direccion = persona.optString("direccion", "");
                            String telefono = persona.optString("telefono", "");
                            String fechanac = persona.optString("fechanac", ""); // puede que no exista en JSON
                            String foto = persona.optString("foto", "");

                            Personas p = new Personas(id, nombres, apellidos, direccion, telefono, fechanac, foto);
                            personaList.add(p);
                            listaNombres.add(nombres + " " + apellidos);
                        }

                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        Log.e(TAG, "Error al parsear JSON", e);
                        Toast.makeText(this, "Error al interpretar los datos recibidos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String body = new String(error.networkResponse.data);
                        Log.e(TAG, "Error respuesta servidor: " + body);
                    }
                    Log.e(TAG, "Error en la petición", error);
                    Toast.makeText(this, "Error al obtener datos: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
        );

        requestQueue.add(request);
    }

    private void eliminarPersona(String idPersona) {
        String url = RestApiMethods.EndpointDeletePerson;

        JSONObject json = new JSONObject();
        try {
            json.put("id", idPersona);
        } catch (JSONException e) {
            Log.e(TAG, "Error al crear JSON para eliminar", e);
            Toast.makeText(this, "Error interno al eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, json,
                response -> {
                    Toast.makeText(this, "Persona eliminada correctamente", Toast.LENGTH_SHORT).show();
                    obtenerPersonas(); // refrescar lista
                },
                error -> {
                    Log.e(TAG, "Error al eliminar persona", error);
                    Toast.makeText(this, "Error al eliminar persona", Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(request);
    }
}
