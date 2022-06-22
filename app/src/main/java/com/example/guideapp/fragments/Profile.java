package com.example.guideapp.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.example.guideapp.MainActivity;
import com.example.guideapp.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;

import com.example.guideapp.popUps.MyDialogFragment;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import classes.Calculations;
import classes.NetworkUtility;
import models.Temperature;

public class Profile extends Fragment {

    private Button addFavourite;
    private Button logoutBtn;
    private List<String> types = new ArrayList<String>();
    ChipGroup chipGroup;
    Switch unitSwitch;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    DatabaseReference profileRef;
    private FirebaseAuth mAuth;
    private String userIUD;
    private TextView txtUsername, txtWeather;
    FirebaseUser currentUser;
    private ArrayList<Temperature> temperatureArrayList = new ArrayList<>();

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        chipGroup = view.findViewById(R.id.chipGroup);
        addFavourite = view.findViewById(R.id.addFavourite);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userIUD = currentUser.getUid();
        myRef = database.getReference(userIUD).child("favourites");
        profileRef = database.getReference(userIUD).child("settings");
        txtUsername = view.findViewById(R.id.txtUsername);
        String user = mAuth.getCurrentUser().getEmail();
        txtUsername.setText(user);
        unitSwitch = view.findViewById(R.id.unitSwitch);
        txtWeather = view.findViewById(R.id.txtWeather);

        URL weatherUrl = NetworkUtility.buildUrlForWeather();
        new Profile.FetchWeatherDetails().execute(weatherUrl);

        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String state = snapshot.getValue(String.class);
                //Log.d("CHECKED :" , String.valueOf(state));
                //unitSwitch.setChecked(state);
                if(state.equals("metric")){
                    unitSwitch.setChecked(true);
                    unitSwitch.setText("Metric");
                }else{
                    unitSwitch.setChecked(false);
                    unitSwitch.setText("Imperial");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        unitSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean state = unitSwitch.isChecked();
                if(state){
                    profileRef.setValue("metric");
                }else{
                    profileRef.setValue("imperial");
                }


            }
        });

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String item = snapshot.getKey();
                types.add(item);
                addChip(item, chipGroup);

            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        logoutBtn = view.findViewById(R.id.logoutBtn);
        addFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open up pop up dialog
                MyDialogFragment myDialogFragment = new MyDialogFragment();
                myDialogFragment.show(getChildFragmentManager(), "My fragment");
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void addChip(String pItem, ChipGroup pChipGroup) {
        List<String> arr = new ArrayList<String>();
        Chip lChip = new Chip(getContext());
        lChip.setText(pItem);
        lChip.setTextColor(getResources().getColor(R.color.black));
        lChip.setChipBackgroundColor(getResources().getColorStateList(R.color.unselectedOption));
        lChip.setCloseIconResource(R.drawable.ic_baseline_close_24);
        lChip.setCloseIconVisible(true);
        lChip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                types.remove(pItem);
                pChipGroup.removeView(lChip);
                myRef.child(pItem).removeValue();
            }
        });
        pChipGroup.addView(lChip, pChipGroup.getChildCount());
    }

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
            //Log.i("WEATHER", "doInBackground: weatherSearchresults:" + weatherSearchResults);
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
                    //Log.i(TAG2, "parseJson: DATE" + temp.getDate() +
                            //"parseJson: MAX" + temp.getMaxTemp());
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

                    //Log.i(TAG2, " date formatted : " + date);
                    temperature.setDate(date);
                   // Log.i(TAG2, "parseJSON : date : " + dateRetrieved);

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
        //Log.i(TAG2, " date today : " + todaysDate);
        Temperature temp = new Temperature();
        int x = 0;
        while(x<data.size()){
            if(data.get(x).getDate().equals(todaysDate)){
                //Log.i(TAG2, " MATCH FOUND ");
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
        int hour = c.get(Calendar.HOUR_OF_DAY);;
        String temp ="";

        //check the hour of the day
        if(hour>12){
            temp = todaysWeather.getMaxTemp();
        }else{
            temp = todaysWeather.getMinTemp();
        }

        if(unitSwitch.isChecked()){
            int newTemp = Calculations.convertToMetricTemp(temp);
           // Log.i(TAG2, " TEMP CONVERTED : " + newTemp);
            txtWeather.setText(String.valueOf(newTemp) + " °C");
        }else{
            txtWeather.setText(temp + " F");
        }
        //Log.i(TAG2, " TEMPERATURE " + temp);
    }
    //endregion
}