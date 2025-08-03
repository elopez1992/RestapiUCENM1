package com.example.restapiucenm1;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.restapiucenm1.config.RestApiMethods;

import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityListPersonas extends AppCompatActivity {

    ListView listView;
    ArrayList<String> listaPersonas;
    ArrayAdapter<String> adapter;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_personas);

        listView = findViewById(R.id.listview);
        listaPersonas = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaPersonas);
        listView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);
        obtenerPersonas();
    }

    private void obtenerPersonas() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, RestApiMethods.EndpointGetPersons, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject persona = response.getJSONObject(i);
                            String nombre = persona.getString("nombres");
                            String apellido = persona.getString("apellidos");
                            listaPersonas.add(nombre + " " + apellido);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show()
        );

        requestQueue.add(request);
    }
}
