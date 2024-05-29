package com.example.maraton;

import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class carreras extends AppCompatActivity {
    private LinearLayout linearLayoutButtons;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carreras);

        linearLayoutButtons = findViewById(R.id.linearLayoutButtons);
        client = new OkHttpClient();

        obtenerCarreras();




        Button btnRegresar = findViewById(R.id.id_regresar);
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(carreras.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }












    private void obtenerCarreras() {
        String url = "http://192.168.101.2:3000/maraton/carreras"; // Asegúrate de tener una ruta en tu servidor Node.js que devuelva las carreras

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("HTTP Request", "Request failed", e);
                runOnUiThread(() -> Toast.makeText(carreras.this, "Error al obtener las carreras.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    runOnUiThread(() -> crearBotonesCarreras(responseBody));
                } else {
                    runOnUiThread(() -> Toast.makeText(carreras.this, "Error al obtener las carreras.", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void crearBotonesCarreras(String responseBody) {
        try {
            JSONArray carreras = new JSONArray(responseBody);
            for (int i = 0; i < carreras.length(); i++) {
                JSONObject carrera = carreras.getJSONObject(i);
                int carreraId = carrera.getInt("id");

                Button button = new Button(this);
                button.setText("Carrera " + carreraId);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(carreras.this, resultados.class);
                        intent.putExtra("carreraId", carreraId);
                        startActivity(intent);
                    }
                });

                linearLayoutButtons.addView(button);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}