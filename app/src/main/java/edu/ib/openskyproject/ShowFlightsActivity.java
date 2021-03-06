package edu.ib.openskyproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class ShowFlightsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
   // private ActivityShowFlightsBinding binding;
    private boolean isReady;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_flights);
        


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googlemap);

        mapFragment.getMapAsync(this);



    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
       String url = (String) getIntent().getStringExtra("passUrl");


            RequestQueue queue = Volley.newRequestQueue(this);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            res -> {
                                Results results = gson.fromJson(res.toString(), Results.class);

                                ArrayList<Flights> flightsArrayList = new ArrayList<>();
                                ArrayList<ArrayList> statesList = new ArrayList<>();
                                statesList.addAll(results.getStates());
                                ArrayList<String> countriesList = new ArrayList<>();
                                for (int i = 0; i < statesList.size(); i++) {
                                    try {
                                        double latitude = Double.parseDouble(statesList.get(i).get(6).toString());
                                        double longitude = Double.parseDouble(statesList.get(i).get(5).toString());
                                        String country = statesList.get(i).get(2).toString();
                                        flightsArrayList.add(new Flights(longitude, latitude));
                                        countriesList.add(country);
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }

                                }
                                for (int i = 0; i < flightsArrayList.size(); i++) {
                                    LatLng point = new LatLng(flightsArrayList.get(i).getLatitude(),
                                            flightsArrayList.get(i).getLongitude());
                                    MarkerOptions markerOptions = new MarkerOptions().position(point).title("Country: " +
                                            countriesList.get(i));
                                    mMap.addMarker(markerOptions);

                                }

                                //System.out.println(result);
                            }, error -> {
                        System.out.println("Error");
                    });
                    queue.add(stringRequest);

                    handler.postDelayed(this::run, 5000);
                }

            };
            handler.postDelayed(runnable, 5000);
        }




    public void returnToBounds(View view) {
        Intent intent = new Intent(getApplicationContext(),BoundariesActivity.class);
        startActivity(intent);
    }


}
