package com.example.maraton;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class Modificar extends AppCompatActivity {
    private EditText editTextNumCorredores;
    private EditText editTextDistancia;
    private Button buttonModificar;
    private int carreraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar);

        editTextNumCorredores = findViewById(R.id.editTextText);
        editTextDistancia = findViewById(R.id.editTextText2);
        buttonModificar = findViewById(R.id.btn_modificar);

        carreraId = getIntent().getIntExtra("carreraId", -1);

        buttonModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numCorredores = editTextNumCorredores.getText().toString();
                String distancia = editTextDistancia.getText().toString();
                if (!numCorredores.isEmpty() && !distancia.isEmpty()) {
                    String url = "http://192.168.101.2:3000/maraton/carrera?id=" + carreraId + "&numCorredores=" + numCorredores + "&distancia=" + distancia;

                    new ModificarCarreraTask().execute(url);
                } else {
                    Toast.makeText(Modificar.this, "Por favor, ingrese todos los datos.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class ModificarCarreraTask extends AsyncTask<String, Void, String> {
        private final OkHttpClient client = new OkHttpClient();

        @Override
        protected String doInBackground(String... urls) {
            try {
                Request request = new Request.Builder()
                        .url(urls[0])
                        .put(RequestBody.create(null, new byte[0]))
                        .build();

                Response response = client.newCall(request).execute();
                return response.isSuccessful() ? "Success" : "Failed";
            } catch (IOException e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String resultado) {
            super.onPostExecute(resultado);

            if (resultado.equals("Success")) {
                Toast.makeText(Modificar.this, "Carrera modificada correctamente.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Modificar.this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(Modificar.this, resultado, Toast.LENGTH_SHORT).show();
            }
        }
    }
}