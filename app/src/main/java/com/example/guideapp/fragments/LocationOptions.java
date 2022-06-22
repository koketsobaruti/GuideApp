package com.example.guideapp.fragments;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.guideapp.R;
import com.example.guideapp.popUps.Unit_Popup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import models.LocationTypes;

public class LocationOptions extends Fragment {

    private Button doneBtn, foodBtn, entertainmentBtn, university,
            shoppingBtn, libraryBtn, barsBtn, historicBtn;

    private boolean foodState;
    private boolean entState;
    private boolean hotelState;
    private boolean shoppingState;
    private boolean libState;
    private boolean barsState;
    private boolean histState;
    private Bundle bundle = new Bundle();

    private String userIUD;
    FirebaseUser currentUser;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    FirebaseAuth mAuth;

    private List<String> locations = new ArrayList<String>();

    public LocationOptions() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location_options,container,false);

        doneBtn = view.findViewById(R.id.doneButton);
        foodBtn = view.findViewById(R.id.foodButton);
        entertainmentBtn = view.findViewById(R.id.entertainmentBtn);
        university = view.findViewById(R.id.hotelSpaButton);
        shoppingBtn = view.findViewById(R.id.shoppingBtn);
        libraryBtn = view.findViewById(R.id.libraryBtn);
        barsBtn = view.findViewById(R.id.barsBtn);
        historicBtn = view.findViewById(R.id.historicBtn);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userIUD = currentUser.getUid();
        myRef = database.getReference(userIUD).child("favourites");

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //add the selected buttons to a list
                if(foodBtn.isSelected()){
                    locations.add("food");
                }
                if(entertainmentBtn.isSelected()){
                    locations.add("movie theater");
                }
                if(university.isSelected()){
                    locations.add("university");
                }
                if(shoppingBtn.isSelected()){
                    locations.add("shopping mall");
                }
                if(libraryBtn.isSelected()){
                    locations.add("library");
                }
                if(barsBtn.isSelected()){
                    locations.add("night club");
                }
                if(historicBtn.isSelected()){
                    locations.add("museum");
                }

                Toast.makeText(getContext(),"",Toast.LENGTH_LONG).show();

                //add selected favourite places to firebase
                LocationTypes types = new LocationTypes(locations);
                for(int i = 0; i<locations.size(); i++){
                    myRef.child(String.valueOf(locations.get(i))).setValue(true);
                }

                Toast.makeText(getContext(),"Account created. ",Toast.LENGTH_LONG).show();
                //create an object
                Unit_Popup myDialogFragment = new Unit_Popup();
                myDialogFragment.show(getChildFragmentManager(), "My fragment");
                //Intent intent = new Intent(getContext(), NavigationActivity.class);
                //startActivity(intent);
            }
        });

        foodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get selected state
                foodState = changeState(foodState);
                //change selected state
                changeButtonSelected(foodBtn);
                //change background
                int color = getBackgroundColor(foodState);
                foodBtn.setBackgroundColor(color);
                //foodBtn.setBackgroundResource(R.color.selectedOption);
            }
        });

        entertainmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entState = changeState(entState);

                changeButtonSelected(entertainmentBtn);
                int color = getBackgroundColor(entState);
                entertainmentBtn.setBackgroundColor(color);
            }
        });

        university.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hotelState = changeState(hotelState);
                changeButtonSelected(university);
                int color = getBackgroundColor(hotelState);
                university.setBackgroundColor(color);
            }
        });

        shoppingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shoppingState = changeState(shoppingState);
                changeButtonSelected(shoppingBtn);
                int color = getBackgroundColor(shoppingState);
                shoppingBtn.setBackgroundColor(color);
            }
        });

        libraryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                libState = changeState(libState);
                changeButtonSelected(libraryBtn);
                int color = getBackgroundColor(libState);
                libraryBtn.setBackgroundColor(color);
            }
        });

        barsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barsState = changeState(barsState);
                changeButtonSelected(barsBtn);
                int color = getBackgroundColor(barsState);
                barsBtn.setBackgroundColor(color);
            }
        });

        historicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                histState = changeState(histState);
                changeButtonSelected(historicBtn);
                int color = getBackgroundColor(histState);
                historicBtn.setBackgroundColor(color);
            }
        });

        return view;
    }

    private void changeButtonSelected(Button btn) {
        if(!btn.isSelected()){
            btn.setSelected(true);
        }else{
            btn.setSelected(false);
        }
    }


    //method to check the state of the button (whether it has been selected or not)
    private boolean changeState(boolean state) {
        state = false;
        if(!state){
            state = true;

        }else{
            state = false;
        }
        return state;
    }

    private int getBackgroundColor(Boolean state){
        Resources res = getResources();
        int color;
        if(state){
            color = ResourcesCompat.getColor(res, R.color.selectedOption,null);
        }else{
            color = ResourcesCompat.getColor(res, R.color.unselectedOption,null);
        }
        return color;
    }

}