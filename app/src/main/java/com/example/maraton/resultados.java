    package com.example.maraton;

    import androidx.appcompat.app.AppCompatActivity;

    import android.content.Intent;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.TextView;
    import android.widget.Toast;

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
    import android.os.Bundle;

    public class resultados extends AppCompatActivity {
        private TextView textViewResultados;

        private Button buttonEliminar, buttonModificar;

        private OkHttpClient client;
        private int carreraId;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_resultados);

            textViewResultados = findViewById(R.id.textView);
            buttonEliminar = findViewById(R.id.id_eliminar);
            buttonModificar = findViewById(R.id.button5);
            client = new OkHttpClient();

            carreraId = getIntent().getIntExtra("carreraId", -1);

            if (carreraId != -1) {
                simularCarrera(carreraId);
            } else {
                Toast.makeText(this, "ID de carrera inválido.", Toast.LENGTH_SHORT).show();
            }

            buttonEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eliminarCarrera(carreraId);
                }
            });

            buttonModificar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(resultados.this, Modificar.class);
                    intent.putExtra("carreraId", carreraId);
                    startActivity(intent);
                }
            });
        }

        private void simularCarrera(int carreraId) {
            String urlSimular = "http://192.168.101.2:3000/maraton/simular?id=" + carreraId;


            Request request = new Request.Builder().url(urlSimular).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("HTTP Request", "Request failed", e);
                    runOnUiThread(() -> Toast.makeText(resultados.this, "Error al simular la carrera.", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // Después de simular la carrera, obtenemos el estado actualizado
                        obtenerEstadoCarrera(carreraId);
                    } else {
                        runOnUiThread(() -> Toast.makeText(resultados.this, "Error al simular la carrera.", Toast.LENGTH_SHORT).show());
                    }
                }
            });
        }

        private void obtenerEstadoCarrera(int carreraId) {
            String urlEstado = "http://192.168.101.2:3000/maraton/estado?id=" + carreraId;

            Request request = new Request.Builder().url(urlEstado).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("HTTP Request", "Request failed", e);
                    runOnUiThread(() -> Toast.makeText(resultados.this, "Error al obtener el estado de la carrera.", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        runOnUiThread(() -> mostrarEstadoCarrera(responseBody));
                    } else {
                        runOnUiThread(() -> Toast.makeText(resultados.this, "Error al obtener el estado de la carrera.", Toast.LENGTH_SHORT).show());
                    }
                }
            });
        }

        private void mostrarEstadoCarrera(String responseBody) {
            try {
                JSONObject carrera = new JSONObject(responseBody);
                StringBuilder estado = new StringBuilder();

                estado.append("Carrera ID: ").append(carrera.getInt("id")).append("\n");
                estado.append("Distancia: ").append(carrera.getDouble("distancia")).append(" km\n");
                estado.append("Tiempo Total: ").append(carrera.getInt("tiempo")).append(" horas\n");
                estado.append("Finalizada: ").append(carrera.getBoolean("finalizada") ? "Sí" : "No").append("\n");
                if (carrera.getBoolean("finalizada")) {
                    estado.append("Ganador: Corredor ").append(carrera.getInt("ganador")).append("\n");
                }

                estado.append("\nCorredores:\n");
                JSONArray corredores = carrera.getJSONArray("corredores");
                for (int i = 0; i < corredores.length(); i++) {
                    JSONObject corredor = corredores.getJSONObject(i);
                    estado.append("Corredor ").append(corredor.getInt("id")).append("\n");
                    estado.append("Velocidad: ").append(corredor.getDouble("velocidad")).append(" km/h\n");
                    estado.append("Distancia Recorrida: ").append(corredor.getDouble("distanciaRecorrida")).append(" km\n");

                    estado.append("Tiempos:\n");
                    JSONArray tiempos = corredor.getJSONArray("tiempos");
                    for (int j = 0; j < tiempos.length(); j++) {
                        JSONObject tiempo = tiempos.getJSONObject(j);
                        estado.append("  ").append(tiempo.getString("Tiempo")).append(", ");
                        estado.append("Distancia Recorrida: ").append(tiempo.getString("distanciaRecorrida")).append(" km\n");
                    }
                    estado.append("\n");
                }

                textViewResultados.setText(estado.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        private void eliminarCarrera(int carreraId) {
            String urlEliminar = "http://192.168.101.2:3000/maraton/eliminar?id=" + carreraId;

            Request request = new Request.Builder().url(urlEliminar).delete().build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("HTTP Request", "Request failed", e);
                    runOnUiThread(() -> Toast.makeText(resultados.this, "Error al eliminar la carrera.", Toast.LENGTH_SHORT).show());
                }




                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        runOnUiThread(() -> {
                            Toast.makeText(resultados.this, "Carrera eliminada exitosamente.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(resultados.this, carreras.class);
                            startActivity(intent);
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(resultados.this, "Error al eliminar la carrera.", Toast.LENGTH_SHORT).show());
                    }
                }
            });
        }





    }