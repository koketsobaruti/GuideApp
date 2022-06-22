package com.example.guideapp.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.guideapp.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import adapters.SuggestionsAdapter;
import classes.JsonParser;
import models.PlaceInfo;
import retrofit2.http.Url;


public class Suggestions extends Fragment implements OnMapReadyCallback {

    private static final int RESULT_OK = 2;
    public Button findBtn;
    int PLACE_PICKER_REQUEST = 1;
    private PlaceInfo mPlace;
    private List<PlaceInfo> placesList = new ArrayList<>();
    SupportMapFragment supportMapFragment;
    private final int DEFAULT_ZOOM = 15;
    GoogleMap map;
    private GoogleApiClient mGoogleApiClient;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private String userIUD;
    private List<String> types = new ArrayList<>();
    private Spinner spType;
    private ListView sugListView;
    private PlaceInfo placeInfo;
    private FusedLocationProviderClient fusedLocationProviderClient;
    double currentLat = 0;
    double currentLng = 0;

    public Suggestions() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_suggestions, container, false);
        findBtn = view.findViewById(R.id.findButton);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userIUD = currentUser.getUid();
        myRef = database.getReference(userIUD).child("favourites");
        spType = view.findViewById(R.id.sp_type);
       // sugListView = view.findViewById(R.id.suggestionsListView);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }else{
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }
        supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.googleMap);
        supportMapFragment.getMapAsync(this);

       /* mGoogleApiClient = new GoogleApiClient
                .Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();*/

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> children = snapshot.getChildren();
                for (DataSnapshot child : children) {
                    String x = child.getKey();
                    types.add(x);
                }
                //Log.d("TYPES: ", String.valueOf(types));
                spType.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, types));
                //SuggestionsAdapter adapter = new SuggestionsAdapter(getContext(), 0, types);
                //sugListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = spType.getSelectedItemPosition();
                //Log.d("SELECTED: ", types.get(i));
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                        "?location=" + currentLat + "," + currentLng +
                        "&radius=9000" +
                        "&type=" + types.get(i) +
                        //"&sensor=true" +
                        "&key=AIzaSyA3mnQFSM39ZwEamw9X_yrDk35M5DX87Rg" ;

                //execute place taks methos to download json data
                new PlaceTask().execute(url);
            }
        });
        /*sugListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //LocationInfo info = (LocationInfo) (bookmarksListView.getItemAtPosition(position));
                String typeSelected = (String) sugListView.getItemAtPosition(position);
                Log.d("SELECTED: ", typeSelected);

                //LatLng currentLatLng = getCurrentLocation();

                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                        "?location" + currentLat + "," + currentLng +
                        "&radius=5000" +
                        "&types" + typeSelected +
                        "&sensor=true" +
                        "&key" + getResources().getString(R.string.google_api_key);

                //execute place taks methos to download json data
                new PlaceTask().execute(url);
            }
        });*/

        return view;
    }

    private void getCurrentLocation() {
        @SuppressLint("MissingPermission") Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    currentLat = location.getLatitude();
                    currentLng = location.getLongitude();

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(currentLat,
                                    currentLng), DEFAULT_ZOOM));
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 44){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
    }

    private class PlaceTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            //Log.d("TRIGGERED: ", " PLACE TASK RUNNING...");
            String data = null;
            try {
                //initialize data
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            //Log.d("TRIGGERED: ", " ONPOST EXECUTE RUNNING...");
            //Execute parser task
            new ParserTask().execute(s);
        }
    }

    private String downloadUrl(String string) throws IOException {
        URL url = new URL(string);
        //initialize connection
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        //connect conneciton
        connection.connect();
        //initialize input stream
        InputStream stream = connection.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        StringBuilder builder = new StringBuilder();

        String line ="";
        while((line = reader.readLine()) != null){
            //append line
            builder.append(line);
        }

        //get appended data
        String data = builder.toString();
        reader.close();

        return data;
    }


    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{
        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            //create json parser class
            JsonParser jsonParser = new JsonParser();
            //initialize hashmap list
            List<HashMap<String,String>> mapList = null;
            JSONObject object = null;
            //initialize json object
            try {
                object = new JSONObject(strings[0]);
                //parse json
                //Log.d("TRIGGERED: ", " OBJECT : " + object);
                mapList = jsonParser.parseResult(object);
            } catch (JSONException e) {
                e.printStackTrace();
                //Log.i(TAG, " Error4 : " + e.getMessage());
            }
            //Log.d("TRIGGERED: ", " MAP LIST : " + mapList);
            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            //clear map
            map.clear();
            //use for loop
            for(int i = 0; i<hashMaps.size(); i++){
                HashMap<String, String> hashmapList = hashMaps.get(i);
                //get Lat and long
                double lat = Double.parseDouble(hashmapList.get("lat"));
                double lng = Double.parseDouble(hashmapList.get("lng"));

                String name = hashmapList.get("name");
                //Log.d("FOUND: ", name);
                LatLng latLng = new LatLng(lat,lng);
                MarkerOptions options = new MarkerOptions();
                options.position(latLng);
                options.title(name);
                map.addMarker(options);
            }
        }
    }
}