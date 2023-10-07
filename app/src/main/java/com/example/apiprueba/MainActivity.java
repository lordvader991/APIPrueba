package com.example.apiprueba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.apiprueba.models.Coordinate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.apiprueba.models.Coordinate;
import com.opencsv.CSVReader;

import org.json.JSONObject;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    Button boton;
    GoogleMap mapView;
    String api = "https://firms.modaps.eosdis.nasa.gov/api/country/csv/95228f5172a6476487cea9ca7348bf58/MODIS_NRT/BOL/1/2023-10-05/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boton = findViewById(R.id.boton);
        getData();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Hola mundo", Toast.LENGTH_SHORT).show();

                Circle ubicacion = mapView.addCircle(new CircleOptions()
                        .center(new LatLng(-16.502656, -68.132009))
                        .radius(100)
                        .strokeColor(Color.argb(128, 255, 0, 0))
                        .fillColor(Color.argb(128, 255, 0, 0)));

                mapView.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion.getCenter(), 15));
            }
        });

    }

    private void parseCSVToJSON(String csvData) {
        try {
            CSVReader csvReader = new CSVReader(new StringReader(csvData));
            List<String[]> data = csvReader.readAll();
            csvReader.close();

            List<Coordinate> coordinatesList = new ArrayList<>();
            String[] headers = data.get(0);

            int latitudeIndex = -1;
            int longitudeIndex = -1;

            for (int i = 0; i < headers.length; i++) {
                if ("latitude".equals(headers[i])) {
                    latitudeIndex = i;
                } else if ("longitude".equals(headers[i])) {
                    longitudeIndex = i;
                }
            }
            for (int i = 1; i < data.size(); i++) {
                String[] row = data.get(i);

                // Check if latitude and longitude indices are found
                if (latitudeIndex != -1 && longitudeIndex != -1) {
                    String latitude = "\"" + row[latitudeIndex] + "\"";
                    String longitude = "\"" + row[longitudeIndex] + "\"";
                    String coordinates = latitude + ", " + longitude;
                    coordinatesList.add(new Coordinate("BOL",
                                                Double.parseDouble(latitude),
                                                Double.parseDouble(longitude)));
                }
            }

            for (Coordinate coordinates : coordinatesList) {
                Log.d("coordenadas", coordinates.toString());
            }

        } catch (Exception e) {
            Log.e("Error", "Error convirtiendo el csv: " + e.getMessage());
        }
    }



    private void getData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, api,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("api", "onResponse: " + response);
                        parseCSVToJSON(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("api", "onErrorResponse: " + error.getLocalizedMessage());
            }
        });
        queue.add(stringRequest);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapView = googleMap;
    }
}