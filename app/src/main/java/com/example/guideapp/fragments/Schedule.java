package com.example.guideapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.guideapp.R;

import java.util.ArrayList;
import java.util.List;

import adapters.ScheduleAdapter;
import models.BookmarkInfo;
import models.LocationInfo;
import models.ScheduleInfo;
import com.example.guideapp.popUps.Schedule_PopUp;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Schedule extends Fragment {

    //on create, check if the firebase has any data to load
    LocationInfo locationInfo = new LocationInfo();
    Button scheduleBtn,cancelBtn, deleteBtn;
    private BottomSheetDialog deleteWindow;
    private boolean alarm;
    private String locationStr, addressStr, dateStr, timeStr, descriptionStr;
    private ArrayList<LocationInfo> locationInfoList = new ArrayList<LocationInfo>();
    private ListView list;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private String userIUD;
    private List<ScheduleInfo> scheduleList = new ArrayList<ScheduleInfo>();
    private List<ScheduleInfo> schedules = new ArrayList<ScheduleInfo>();
    private ListView scheduleListView;
    private List<String> keyList = new ArrayList<>();
    ScheduleAdapter adapter;
    public Schedule() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userIUD = currentUser.getUid();
        myRef = database.getReference().child(userIUD).child("schedules");
        scheduleListView = view.findViewById(R.id.scheduleListView);
        scheduleBtn = view.findViewById(R.id.addSchedule);
        deleteWindow = new BottomSheetDialog(getContext());
        deleteWindow.setContentView(R.layout.delete_dialogue);
        deleteBtn = deleteWindow.findViewById(R.id.yesBtn);
        cancelBtn = deleteWindow.findViewById(R.id.noBtn);

        scheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Schedule_PopUp myDialogFragment = new Schedule_PopUp();
                myDialogFragment.show(getChildFragmentManager(), "My fragment");
            }
        });

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ScheduleInfo scheduleInfo = snapshot.getValue(ScheduleInfo.class);
                String key = snapshot.getKey();
                keyList.add(key);
                addItem(scheduleInfo);
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

        scheduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScheduleInfo info = (ScheduleInfo) (scheduleListView.getItemAtPosition(position));
                String key = keyList.get(position);
                deleteWindow.show();
                showDeleteDialogue(key, position);
            }
        });

        /*myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                scheduleList.clear();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for(DataSnapshot child:children){
                    ScheduleInfo scheduleInfo = child.getValue(ScheduleInfo.class);
                    String push = dataSnapshot.getKey();
                    scheduleList.add(scheduleInfo);
                }
                Log.d("SIZE: ", String.valueOf(scheduleList.size()));
                int x = 0;
                while(x < scheduleList.size()){
                    ScheduleInfo info = new ScheduleInfo();
                    info.setLocation(scheduleList.get(x).getLocation());
                    info.setAddress(scheduleList.get(x).getAddress());
                    info.setDescription(scheduleList.get(x).getDescription());
                    info.setDate(scheduleList.get(x).getDate());
                    info.setTime(scheduleList.get(x).getTime());
                    info.setAlarmState(scheduleList.get(x).getAlarmState());
                    info.setTense(scheduleList.get(x).getTense());
                    //Log.d("BOOKMARK TYPE: ", scheduleList.get(x).getDescription());
                    schedules.add(info);
                    x++;
                }
                ScheduleAdapter adapter = new ScheduleAdapter(getContext(),0,schedules);
                scheduleListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });*/

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteWindow.dismiss();
            }
        });

        return view;
    }

    private void addItem(ScheduleInfo info) {
        scheduleList.add(info);
        adapter = new ScheduleAdapter(getContext(),0,scheduleList);
        scheduleListView.setAdapter(adapter);
    }

    private void showDeleteDialogue(String k, int pos) {
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.child(k).removeValue();
                scheduleList.remove(pos);
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(),"Deleted successfully.",Toast.LENGTH_SHORT).show();
                deleteWindow.dismiss();
            }
        });

    }
}