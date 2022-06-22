package com.example.guideapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.guideapp.R;
import com.example.guideapp.popUps.Bookmarks_PopUp;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import adapters.BookmarkAdapter;
import models.BookmarkInfo;

public class Bookmarks extends Fragment {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private String userIUD;
    private ListView bookmarksListView;
    private BottomSheetDialog deleteWindow;
    private List<BookmarkInfo> bookmarksList = new ArrayList<BookmarkInfo>();
    private Button deleteBtn, cancelBtn, addBtn;
    BookmarkAdapter adapter;

    public Bookmarks() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userIUD = currentUser.getUid();
        myRef = database.getReference().child(userIUD).child("bookmarks");
        bookmarksListView = view.findViewById(R.id.bookmarksListview);
        deleteWindow = new BottomSheetDialog(getContext());
        deleteWindow.setContentView(R.layout.delete_dialogue);
        deleteBtn = deleteWindow.findViewById(R.id.yesBtn);
        cancelBtn = deleteWindow.findViewById(R.id.noBtn);

        addBtn = view.findViewById(R.id.addNewBookmark);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bookmarks_PopUp myDialogFragment = new Bookmarks_PopUp();
                myDialogFragment.show(getChildFragmentManager(), "My fragment");
             }
        });



       myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                BookmarkInfo locationInfo = snapshot.getValue(BookmarkInfo.class);
                addToListView(locationInfo);
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

        bookmarksListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BookmarkInfo info = (BookmarkInfo) (bookmarksListView.getItemAtPosition(position));
                String key = info.getKey();
                deleteWindow.show();
                showDeleteDialogue(key, position);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteWindow.dismiss();
            }
        });

        return view;
    }

    private void addToListView(BookmarkInfo info) {
        bookmarksList.add(info);
        adapter = new BookmarkAdapter(getContext(),0,bookmarksList);
        bookmarksListView.setAdapter(adapter);
    }

    private void showDeleteDialogue(String k, int pos) {
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.child(k).removeValue();
                bookmarksList.remove(pos);
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(),"Deleted successfully.",Toast.LENGTH_SHORT).show();
                deleteWindow.dismiss();
            }
        });

    }


}