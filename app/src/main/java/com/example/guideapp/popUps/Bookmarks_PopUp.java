package com.example.guideapp.popUps;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.guideapp.R;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import classes.Calculations;
import models.BookmarkInfo;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class Bookmarks_PopUp extends DialogFragment {

    private TextView searchTxt, locationTxt, addressTxt, descTxt;
    private Button btnDone;
    private LinearLayout display;
    private ImageView exitBtn;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private String userIUD, placeId;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.bookmark_pop_up, container, false);

        searchTxt = view.findViewById(R.id.searchBookmarkTxt);
        locationTxt = view.findViewById(R.id.locationSearchedTxt);
        addressTxt = view.findViewById(R.id.addressSearchedTxt);
        descTxt = view.findViewById(R.id.descSearchedTxt);
        btnDone = view.findViewById(R.id.exitBookmarkDialog);
        display = view.findViewById(R.id.displayLayout);
        exitBtn = view.findViewById(R.id.exitBookmark);

        display.setVisibility(View.INVISIBLE);
        display.setEnabled(false);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userIUD = currentUser.getUid();
        myRef = database.getReference().child(userIUD).child("bookmarks");

        searchTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.TYPES);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                        fields).build(getContext());
                startActivityForResult(intent,100);
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = Calculations.getCurrentTime();
                String date = Calculations.getCurrentDate();
                String location = String.valueOf(locationTxt.getText());
                String address = String.valueOf(addressTxt.getText());
                String description = String.valueOf(descTxt.getText());

                BookmarkInfo info = new BookmarkInfo(location,address,description,date,time,placeId);
                myRef.child(placeId).setValue(info);
                Toast.makeText(getContext(),"Bookmark added.",Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);

            placeId = place.getId();
            List<Place.Type> typeList = new ArrayList<>();
            typeList = place.getTypes();
            String type = String.valueOf(typeList.get(0));

            display.setVisibility(View.VISIBLE);
            display.setEnabled(true);
            //String type = place.getId();
            locationTxt.setText(place.getName());
            addressTxt.setText(place.getAddress());
            descTxt.setText(type);
            // Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
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
}
