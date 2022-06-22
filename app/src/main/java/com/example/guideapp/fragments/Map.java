package com.example.guideapp.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guideapp.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import classes.Calculations;
import models.BookmarkInfo;
import models.HistoryInfo;
import classes.NetworkUtility;
import models.Temperature;
import models.PlaceInfo;
import models.PolylineData;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class Map extends Fragment implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener {

    ///declarations
    List<String> openingHrsDays = new ArrayList<String>();
    SupportMapFragment supportMapFragment;
    private LatLng currentLocation;
    private LatLng destinationLatlng;
    private BottomSheetDialog bottomSheetDialog;
    private String location, address, time, date;
    private Button btnDirections, btnZoomIn, btnZoomOut, btnStart;
    //variables for the map
    boolean locationPermissionGranted = false;
    private final int DEFAULT_ZOOM = 20;
    private Location lastKnownLocation;
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private FusedLocationProviderClient client;

    // The entry point to the Places API.
    private PlacesClient placesClient;
    GoogleMap map;
    private CameraPosition cameraPosition;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    TextView searchText;
    private ImageView bookmarkImage;

    TextView contactTxt, ratingTxt, txtOpeningHours, txtAddress, txtLocation;
    private String locationType;
    private String userIUD;
    FirebaseUser currentUser;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    DatabaseReference myRefHist;

    private ArrayList<Temperature> temperatureArrayList = new ArrayList<>();
    private TextView tempTxt;

    private FirebaseAuth mAuth;

    MarkerOptions place1, place2;
    Polyline currentPolyline;
    private GeoApiContext mGeoApiContext;
    private ArrayList<PolylineData> mPolylinesData = new ArrayList<>();
    private Marker mSelectedMarker = null;
    ArrayList<Marker> mTripMarkers = new ArrayList<>();
    private ImageButton resetBtn, cycleBtn, driveBtn, walkBtn, myLocationBtn, zoomIn, zoomOut;
    String destinationName = null;
    String destDuration;
    private TextView txtDuration, txtDistance;
    Marker destMarker;
    String destName, destAddress, destHrs, destRating, destNumber;
    boolean open;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    int PLACE_PICKER_REQUEST = 1234;
    private GoogleApiClient mGoogleApiClient;
    private PlaceInfo mPlace;
    private List<PlaceInfo> placesList = new ArrayList<>();
    private DatabaseReference myRefSettings;
    String temp = "";
    String distMeters = null;
    String distKm = null;
    List<HistoryInfo> historyInfoList = new ArrayList<>();
    private String placeId;
    List<String> keyList = new ArrayList<>();
    String openHrs = "";
    private FusedLocationProviderClient fusedLocationProviderClient;

    //showBottomSheetDialog(name,address,openingHours,  rating,  phoneNumber);
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        getLocationPermission();
        /*fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }else{
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }

        //Build the map
        supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.googleMap);
        supportMapFragment.getMapAsync(this::onMapReady);*/

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userIUD = currentUser.getUid();
        myRef = database.getReference(userIUD).child("bookmarks");
        myRefHist = database.getReference(userIUD).child("history");
        myRefSettings = database.getReference(userIUD).child("settings");

        bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout);
        btnDirections = bottomSheetDialog.findViewById(R.id.btnDirections);
        //btnStart = bottomSheetDialog.findViewById(R.id.btnStartNavigation);

        txtLocation = bottomSheetDialog.findViewById(R.id.locationSelectedTxt);
        txtAddress = bottomSheetDialog.findViewById(R.id.addressSelectedTxt);
        txtOpeningHours = bottomSheetDialog.findViewById(R.id.openingHoursTxt);
        ratingTxt = bottomSheetDialog.findViewById(R.id.ratingTxt);
        contactTxt = bottomSheetDialog.findViewById(R.id.contactTxt);

        txtDuration = view.findViewById(R.id.txtDuration);
        txtDistance = view.findViewById(R.id.txtDistance);
        cycleBtn = view.findViewById(R.id.cycleBtn);
        driveBtn = view.findViewById(R.id.drivingBtn);
        walkBtn = view.findViewById(R.id.walkingBtn);
        myLocationBtn = view.findViewById(R.id.myLocationBtn);
        zoomIn = view.findViewById(R.id.btnZoomIn);
        zoomOut = view.findViewById(R.id.btnZoomOut);

        bookmarkImage = bottomSheetDialog.findViewById(R.id.bookmarkLocation);
        database = FirebaseDatabase.getInstance();

        //google maps directions

        //weather api
        URL weatherUrl = NetworkUtility.buildUrlForWeather();
        new FetchWeatherDetails().execute(weatherUrl);
        tempTxt = view.findViewById(R.id.tempTxtMap);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        bookmarkImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = txtLocation.getText().toString();
                address = txtAddress.getText().toString();
                date = Calculations.getCurrentDate();
                time = Calculations.getCurrentTime();

                BookmarkInfo info = new BookmarkInfo(location, address, locationType, date, time, placeId);
                myRef.push().setValue(info);
                Toast.makeText(getContext(), "Bookmark added.", Toast.LENGTH_SHORT).show();
            }
        });

        Places.initialize(getContext(), "AIzaSyA3mnQFSM39ZwEamw9X_yrDk35M5DX87Rg");
        placesClient = Places.createClient(getContext());

        searchText = view.findViewById(R.id.sv_location);
        //opens the google autocomplete and
        //assigns the value to the location and address
        searchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS,
                        Place.Field.OPENING_HOURS, Place.Field.PHONE_NUMBER, Place.Field.RATING, Place.Field.LAT_LNG, Place.Field.TYPES, Place.Field.ID);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                        fields).build(getContext());
                startActivityForResult(intent, 100);

            }
        });

        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
        }

        //taskLoadedCallback = this;
        btnDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                calculateDirections(destinationLatlng);
                resetBtn.setEnabled(true);
                resetBtn.setVisibility(View.VISIBLE);
                txtDistance.setVisibility(View.VISIBLE);
                txtDuration.setVisibility(View.VISIBLE);
                walkBtn.setEnabled(true);
                cycleBtn.setEnabled(true);
                driveBtn.setEnabled(true);
                walkBtn.setVisibility(View.VISIBLE);
                cycleBtn.setVisibility(View.VISIBLE);
                driveBtn.setVisibility(View.VISIBLE);


            }
        });

        resetBtn = view.findViewById(R.id.resetMapBtn);
        //DISABLE THE RESET BUTTON WHEN THE ACTIVITY LOADS
        resetBtn.setEnabled(false);
        resetBtn.setVisibility(View.GONE);
        txtDistance.setVisibility(View.GONE);
        txtDuration.setVisibility(View.GONE);
        walkBtn.setEnabled(false);
        cycleBtn.setEnabled(false);
        driveBtn.setEnabled(false);
        walkBtn.setVisibility(View.GONE);
        cycleBtn.setVisibility(View.GONE);
        driveBtn.setVisibility(View.GONE);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetMap();
                resetBtn.setEnabled(false);
                resetBtn.setVisibility(View.GONE);
                txtDistance.setVisibility(View.GONE);
                txtDuration.setVisibility(View.GONE);
                walkBtn.setEnabled(false);
                cycleBtn.setEnabled(false);
                driveBtn.setEnabled(false);
                walkBtn.setVisibility(View.GONE);
                cycleBtn.setVisibility(View.GONE);
                driveBtn.setVisibility(View.GONE);
            }
        });

        myLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destMarker.remove();
                zoomIntoCurrentLocation();
            }
        });

        zoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });

        zoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });

        cycleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latitude = String.valueOf(destMarker.getPosition().latitude);
                String longitude = String.valueOf(destMarker.getPosition().longitude);
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude + "&mode=b");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                try{
                    if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Couldn't open map", Toast.LENGTH_SHORT).show();
                }
            }
        });

        driveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latitude = String.valueOf(destMarker.getPosition().latitude);
                String longitude = String.valueOf(destMarker.getPosition().longitude);
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude + "&mode=d");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                try{
                    if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Couldn't open map", Toast.LENGTH_SHORT).show();
                }
            }
        });

        walkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latitude = String.valueOf(destMarker.getPosition().latitude);
                String longitude = String.valueOf(destMarker.getPosition().longitude);
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude + "&mode=w");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                try{
                    if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Couldn't open map", Toast.LENGTH_SHORT).show();
                }
            }
        });

        client = LocationServices.getFusedLocationProviderClient(getActivity());
        return view;
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        this.map = googleMap;

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        map.setOnPolylineClickListener(this);

    }

    //region FIND NEARBY LOCATIONS


    //endregion

    //region DISPLAYING DIRECTIONS ONTO THE EMBEDDED MAP
    private void calculateDirections(LatLng dest)
        {

            com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                    dest.latitude,
                    dest.longitude
            );
            DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

            directions.alternatives(true);
            directions.origin(
                    new com.google.maps.model.LatLng(
                            currentLocation.latitude,
                            currentLocation.longitude
                    )
            );

            //Log.d(TAG3, "calculateDirections: destination: " + destination.toString());
            //Log.d(TAG3, "calculateDirections: origin: " + currentLocation.toString());

            directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
               /* Log.d(TAG3, "onResult: routes: " + result.routes[0].toString());
                Log.d(TAG3, "onResult: duration legs: " + result.routes[0].legs[0].duration);
                Log.d(TAG3, "onResult: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG3, "onResult: geoCodedWayPoints: " + result.geocodedWaypoints[0].toString());
                //destDuration = String.valueOf(result.routes[0].legs[0].distance);*/
                //draw poly lines on the map
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                e.printStackTrace();

            }
        });
    }

    //add polyLines on the map
    private void addPolylinesToMap(final DirectionsResult result) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(mPolylinesData.size()>0){
                    for(PolylineData polylineData: mPolylinesData)
                    {
                        polylineData.getPolyline().remove();
                    }
                }
                double duration = 99999999;
                for (DirectionsRoute route : result.routes) {
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for (com.google.maps.model.LatLng latLng : decodedPath) {

                        //Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = map.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getActivity(), R.color.darkGrey));
                    polyline.setClickable(true);
                    mPolylinesData.add(new PolylineData(polyline,route.legs[0]));

                    //HIGHLIGHT THE SHORTEST ROUTE
                    double tempDuration = route.legs[0].duration.inSeconds;

                    String dur = null;
                    if(tempDuration<duration){
                        duration = tempDuration;
                        onPolylineClick(polyline);
                        zoomRoute(polyline.getPoints());
                        distMeters = String.valueOf(result.routes[0].legs[0].distance.inMeters);
                        distKm = String.valueOf(result.routes[0].legs[0].distance);
                        dur = String.valueOf(route.legs[0].duration);

                        myRefSettings.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String mode = snapshot.getValue(String.class);
                                if(mode.equals("imperial")){
                                    int newDist = Calculations.convertToImpDistance(distMeters);
                                    txtDistance.setText(String.format("%d mi", newDist));
                                }else{
                                    txtDistance.setText(distKm);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        //txtDuration.setText(dist);
                        txtDuration.setText(dur);
                    }
                }

            }
        });
    }

    @Override
    public void onPolylineClick(@NonNull Polyline polyline) {
        for(PolylineData polylineData: mPolylinesData){
            if(polyline.getId().equals(polylineData.getPolyline().getId())){
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.blue1));
                polylineData.getPolyline().setZIndex(1);

                LatLng endLocation = new LatLng(
                        polylineData.getLeg().endLocation.lat,
                        polylineData.getLeg().endLocation.lng
                );

                destMarker.showInfoWindow();
                mTripMarkers.add(destMarker);
            }else{
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.darkGrey));
                polylineData.getPolyline().setZIndex(0);
            }
        }
    }

    //ZOOM MAP TO DISPLAY THE ROUTE
    public void zoomRoute(List<LatLng> lstLatLngRoute){
        if((map == null) || (lstLatLngRoute == null) || (lstLatLngRoute.isEmpty())) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for(LatLng latLngPoint : lstLatLngRoute){
            boundsBuilder.include(latLngPoint);
        }

        int routePadding = 120;
        LatLngBounds latLngBounds = boundsBuilder.build();

        map.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }

    //zoom onto location searched
    public void zoomLocation(LatLng latLngDest, String name){
        removeMarkers();

        /*destMarker = map.addMarker(new MarkerOptions()
                    .position(endLocation)
                    .title("Destination" + polylineData.getLeg().duration)
                );*/

        destMarker = map.addMarker(new MarkerOptions().position(latLngDest).title(name));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(latLngDest.latitude,
                        latLngDest.longitude), DEFAULT_ZOOM));
    }

    public void zoomIntoCurrentLocation(){
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(currentLocation.latitude,
                        currentLocation.longitude), DEFAULT_ZOOM));
    }

    //REMOVE ALL MARKERS FROM THE MAP
    private void removeMarkers(){
        for(Marker marker : mTripMarkers){
            marker.remove();
        }
    }

    private void resetSelectedMarker(){
        if(mSelectedMarker!=null){
            mSelectedMarker.setVisible(true);
            mSelectedMarker = null;
            removeMarkers();
        }
    }

    //GOES BACK TO THE MAIN MAP DISPLAY
    private void resetMap() {
        if(map != null){
            map.clear();

            if(mPolylinesData.size()>0){
                mPolylinesData.clear();
                mPolylinesData = new ArrayList<>();
            }

        }
        boolean granted = true;
        getDeviceLocation();
    }

    //endregion

    //region GET WEATHER DETAILS

    private class FetchWeatherDetails extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL weatherUrl = urls[0];
            String weatherSearchResults = null;

            try{
                //get weather search results
                weatherSearchResults = NetworkUtility.getResponseFromHttpUrl(weatherUrl);

            }catch (IOException e){
                e.printStackTrace();
            }
            return weatherSearchResults;
        }

        @Override
        protected void onPostExecute(String weatherSearchResults) {
            if(weatherSearchResults != null && !weatherSearchResults.equals("")){
                temperatureArrayList = parseJson(weatherSearchResults);

                //set the weather details
                setTodaysWeather(temperatureArrayList);
                //testing
                Iterator itr = temperatureArrayList.iterator();
                while (itr.hasNext()){
                    Temperature temp = (Temperature) itr.next();
                }

            }
            super.onPostExecute(weatherSearchResults);
        }
    }

    public ArrayList<Temperature> parseJson(String weatherSearchResults) {
        if(weatherSearchResults!=null){
            temperatureArrayList.clear();
        }

        if(weatherSearchResults!=null){
            try{
                JSONObject rootObject = new JSONObject(weatherSearchResults);
                JSONArray results = rootObject.getJSONArray("DailyForecasts");

                for(int i =0; i<results.length(); i++){
                    Temperature temperature = new Temperature();

                    JSONObject resultsObj = results.getJSONObject(i);
                    String dateRetrieved = resultsObj.getString("Date");
                    String date ="";
                    date = dateRetrieved.substring(0,10);
                    temperature.setDate(date);

                    JSONObject tempResultsObj = resultsObj.getJSONObject("Temperature");
                    String minTemp = tempResultsObj.getJSONObject("Minimum").getString("Value");
                    temperature.setMinTemp(minTemp);

                    String maxTemp = tempResultsObj.getJSONObject("Minimum").getString("Value");
                    temperature.setMaxTemp(maxTemp);

                    temperatureArrayList.add(temperature);
                }

                return temperatureArrayList;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //get today's weather
    public void setTodaysWeather(ArrayList<Temperature> data){
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH) + 1 ;
        int year = c.get(Calendar.YEAR);

        String todaysDate = year + "-0" + month + "-" + day;
        Temperature temp = new Temperature();
        int x = 0;
        while(x<data.size()){
            if(data.get(x).getDate().equals(todaysDate)){
                temp.setMinTemp(data.get(x+1).getMinTemp());
                temp.setMaxTemp(data.get(x+2).getMaxTemp());
                break;
            }else{
                x++;
            }
        }
        getCurrentTemp(temp);
    }

    //get the temperature for a time of day
    public void getCurrentTemp(Temperature todaysWeather){
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);

        //check the hour of the day
        if(hour>12){
            temp = todaysWeather.getMaxTemp();
        }else{
            temp = todaysWeather.getMinTemp();
        }

        myRefSettings.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String mode = snapshot.getValue(String.class);
                if(mode.equals("metric")){
                    int newTemp = Calculations.convertToMetricTemp(temp);
                    tempTxt.setText(String.format("%d °C", newTemp));
                }else{
                    tempTxt.setText(temp + " F");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        
        /*tempTxt.setText(temp);
        Log.i(TAG2, " TEMPERATURE " + temp);*/
    }
    //endregion

    //region GET THE DETAILS OF THE LOCATION SEARCHED
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            //Log.d(TAG, "Current location is null. Using defaults.");

            destinationLatlng = place.getLatLng();
            destName = place.getName();
            destAddress = place.getAddress();
            placeId = place.getId();
            int x = 0;
            Calendar c = Calendar.getInstance();
            //int day = c.DAY_OF_WEEK - 1;
            int day = c.get(Calendar.DAY_OF_WEEK)-2;

            open  = false;


            try{
                openHrs = place.getOpeningHours().getWeekdayText().get(day);
            } catch (Exception e) {
                openHrs = "N/A";
            }


            zoomLocation(destinationLatlng, place.getName());

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    if(marker.equals(destMarker)){
                        showBottomSheetDialog(place.getName(), place.getAddress(), openHrs, place.getRating(), place.getPhoneNumber());
                    }
                    return false;
                }
            });
            searchText.setText(place.getName());

            List<Place.Type> typeList = new ArrayList<>();
            typeList = place.getTypes();
            locationType = String.valueOf(typeList.get(0));
            addToHistory(place.getName(), place.getAddress(), openHrs, place.getRating(),place.getLatLng(), place.getId());



        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            // TODO: Handle the error.
            Status status = Autocomplete.getStatusFromIntent(data);
            status.getStatusMessage();
            //Log.i("Error", status.getStatusMessage());
        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }

        return;
    }

    //ADD SEARCHED LOCATION TO HISTORY
    private void addToHistory(String name, String address, String openingHours, Double rating,
                              LatLng latLng, String id) {

        myRefHist.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Iterable<DataSnapshot> children = snapshot.getChildren();
                for(DataSnapshot child:children){
                    HistoryInfo locationInfo = child.getValue(HistoryInfo.class);
                    String key = snapshot.getKey();
                    keyList.add(key);
                    historyInfoList.add(locationInfo);
                }

                boolean found = false;
                int x = 0;

                while((!found) && (x<keyList.size())){
                    if(keyList.get(x).equals(id)){
                        found = true;
                    }else{
                        x++;
                    }
                }

                if(!found){
                    String lat = String.valueOf(latLng.latitude);
                    String lon = String.valueOf(latLng.longitude);
                    String r = String.valueOf(rating);

                    HistoryInfo info = new HistoryInfo(name,address,locationType,openingHours,r,lat,lon,id);
                    myRefHist.child(id).setValue(info);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    //DISPLAY DETAILS ABOUT THE PLACE
    private void showBottomSheetDialog(String name, String address, String openingHours, Double rating, String phoneNumber) {
        txtLocation.setText(name);
        txtAddress.setText(address);
        //String hrs = openingHours + " - " + close;
        txtOpeningHours.setText(openingHours);
        ratingTxt.setText(String.format(String.valueOf(rating)));
        if(phoneNumber.equals("") || phoneNumber == null){
            contactTxt.setText("N/A");
        }else{
            contactTxt.setText(phoneNumber);
        }
        removeMarkers();

        bottomSheetDialog.show();
    }
    //endregion

    private void getCurrentLocation() {
        //Log.d("MAP FRAGMENT", "getLocationPermission: getting location permissions");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        try{
            if(locationPermissionGranted){
                @SuppressLint("MissingPermission") Task<Location> location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location curr = task.getResult();
                        //Log.d(TAG3, "CURRENT LOCATION :" + curr);
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curr.getLatitude(), curr.getLongitude()),
                                DEFAULT_ZOOM));
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Log.e(TAG, "Exception: %s" + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom){
        //Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    //region GET CURRENT DEVICE LOCATION & USE TO DISPLAY CURRENT LOCATION ON THE MAP
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        //Log.d(TAG3, "location granted :" + locationPermissionGranted);
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = client.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                LatLng latLng = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());

                                map.addMarker(new MarkerOptions().position(latLng).title("" + "You are here."));

                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                currentLocation = latLng;
                                //Log.d("LOCATION :" , String.valueOf(lastKnownLocation));
                            }
                        } else {
                            //Log.d(TAG, "Current location is null. Using defaults.");
                           // Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
           // Log.d(TAG, "Current location is null. Using defaults.");
            //Log.e(TAG, "Exception: %s", task.getException());
            map.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
            map.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    //GET THE USER TO ALLOW THE APP TO USE THEIR CURRENT LOCATION
    private void getLocationPermission() {
        String [] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION};

        //Log.d(TAG3, "LOCATION PERMISSION GRANTED METHOD :" + ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {
                if(getActivity().checkSelfPermission(COURSE_LOCATION ) == PackageManager.PERMISSION_GRANTED){
                    locationPermissionGranted = true;
                    //initialize map
                    initMap();
                }else{
                    ActivityCompat.requestPermissions(getActivity(),
                            permissions,
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }
            } else {
                //when permission not granted request permission
                //Log.d(TAG3, "PERMISSION REQUEST RUNNING ... ");
                ActivityCompat.requestPermissions(getActivity(),
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION
                                , Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
        //return locationPermissionGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //Log.d(TAG3, "PERMISSION REQUEST : " + requestCode);
        switch(requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION : {
                //if request is cancelled, the result arrays are empty.
                if(grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        //Log.d(TAG3, "PERMISSION REQUEST GRANTED");
                        locationPermissionGranted = true;
                        //initialize map
                        initMap();
                }else{
                   // Log.d(TAG3, "PERMISSION REQUEST DENIED");
                    getLocationPermission();
                    locationPermissionGranted = false;
                }
                return;
                }

            }
        //Log.d(TAG3, "PERMISSION REQUEST RESULT :" + locationPermissionGranted);
        //updateLocationUI();
        }

    //}

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        //Log.d(TAG3, "location granted 2 :" + locationPermissionGranted);
        if(map == null) {
            return;
        }

        try{
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        }catch (SecurityException e)  {
            e.printStackTrace();
            //Log.e("Exception: %s", e.getMessage());
        }
    }

    //endregion

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

}