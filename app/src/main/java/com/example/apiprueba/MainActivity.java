package com.example.apiprueba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.opencsv.CSVReader;

import org.json.JSONObject;

import java.io.StringReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    Button boton;
    GoogleMap mapView;
    String api = "https://firms.modaps.eosdis.nasa.gov/api/country/csv/692cc45f56bcc891b36d5be7599d8f5c/MODIS_NRT/BOL/1/2023-10-05/";
    List<Coordinate> coordinatesList = new ArrayList<>();
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

                Marker ubicacion = null; // Cambiado de Circle a Marker
                for (Coordinate coordinates : coordinatesList) {
                    ubicacion = mapView.addMarker(new MarkerOptions()
                            .position(new LatLng(coordinates.latitude, coordinates.longitude)));
                    Log.d("coordenadas", coordinates.toString());
                }
                if (ubicacion != null) {
                    mapView.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion.getPosition(), 15));
                }
            }
        });


    }

    private void parseCSVToJSON(String csvData) {
        try {
            CSVReader csvReader = new CSVReader(new StringReader(csvData));
            List<String[]> data = csvReader.readAll();
            csvReader.close();

            coordinatesList.clear();
            String[] headers = data.get(0);


            for (int i = 1; i < data.size(); i++) {
                String[] row = data.get(i);
                String latitude = row[1];
                String longitude = row[2];
                coordinatesList.add(new Coordinate("BOL",
                        Double.parseDouble(latitude),
                        Double.parseDouble(longitude)));
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

        mapView.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for (Coordinate coordinates : coordinatesList) {
                    if (marker.getPosition().latitude == coordinates.latitude &&
                            marker.getPosition().longitude == coordinates.longitude) {
                        openWhatsApp("+59168830275", coordinates.latitude, coordinates.longitude);
                        return true;
                    }
                }
                return false;
            }
        });

    }

    private void openWhatsApp(String phoneNumber, double latitude, double longitude) {
        String mapUrl = "https://www.google.com/maps/place/" + latitude + "," + longitude + " A fire has been reported at this location HELP NEEDED";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String url = "https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + URLEncoder.encode(mapUrl);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

}





