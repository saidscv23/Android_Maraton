package com.example.maraton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;




import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class MainActivity extends AppCompatActivity {

    private EditText editTextNumCorredores;
    private EditText editTextDistancia;
    private Button buttonIngresarCarrera;
    private Button buttonVerCarreras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextNumCorredores = findViewById(R.id.editTextText);
        editTextDistancia = findViewById(R.id.editTextText2);
        buttonIngresarCarrera = findViewById(R.id.btn_modificar);
        buttonVerCarreras = findViewById(R.id.button2);

        buttonIngresarCarrera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numCorredores = editTextNumCorredores.getText().toString();
                String distancia = editTextDistancia.getText().toString();
                if (!numCorredores.isEmpty() && !distancia.isEmpty()) {
                    iniciarCarrera(numCorredores, distancia);
                } else {
                    Toast.makeText(MainActivity.this, "Por favor, ingrese todos los datos.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonVerCarreras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, carreras.class);
                startActivity(intent);
            }
        });
    }

    private void iniciarCarrera(String numCorredores, String distancia) {
        String url = "http://192.168.101.2:3000/maraton/iniciar?numCorredores=" + numCorredores + "&distancia=" + distancia;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).post(RequestBody.create(null, new byte[0])).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("HTTP Request", "Request failed", e);
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error al iniciar la carrera.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Carrera iniciada correctamente.", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error al iniciar la carrera.", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}