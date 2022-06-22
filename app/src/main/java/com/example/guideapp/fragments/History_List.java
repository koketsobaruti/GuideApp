package com.example.guideapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.guideapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import adapters.HistoryRecycleView;
import models.HistoryInfo;

public class History_List extends Fragment {

    private String userIUD;
    private FirebaseUser currentUser;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private List<HistoryInfo> historyList = new ArrayList<HistoryInfo>();
    private List<HistoryInfo> history = new ArrayList<HistoryInfo>();
    private RecyclerView historyListView;
    private RecyclerView.Adapter mAdapter;
    List<String> keyList = new ArrayList<>();
    RecyclerView.LayoutManager layoutManager;
    public History_List() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_history__list, container, false);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userIUD = currentUser.getUid();
        myRef = database.getReference(userIUD).child("history");
        historyListView = view.findViewById(R.id.historyListView);
        historyListView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        historyListView.setLayoutManager(new LinearLayoutManager(getContext()));

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> children = snapshot.getChildren();

                for(DataSnapshot child:children){
                    HistoryInfo historyInfo = child.getValue(HistoryInfo.class);
                    String push = snapshot.getKey();
                    keyList.add(push);
                    historyList.add(historyInfo);
                }

                int x = 0;
                while(x < historyList.size()){
                    HistoryInfo info = new HistoryInfo();
                    info.setLocation(historyList.get(x).getLocation());
                    info.setAddress(historyList.get(x).getAddress());
                    info.setDescription(historyList.get(x).getDescription());
                    info.setOpeningHours(historyList.get(x).getOpeningHours());
                    info.setRating(historyList.get(x).getRating());

                    history.add(info);
                    x++;
                }

                mAdapter = new HistoryRecycleView(history,getContext());
                historyListView.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }
}