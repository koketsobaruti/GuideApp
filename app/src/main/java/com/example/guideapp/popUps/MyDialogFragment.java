package com.example.guideapp.popUps;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.guideapp.R;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyDialogFragment extends DialogFragment {

    AutoCompleteTextView editText;
    ArrayAdapter<String> adapter, itemAdapter;
    private TextView lvItem;
    Button btnDone;
    private List<String> itemArray;
    ChipGroup chipGroup;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    long x;
    Button btnAdd;
    private FirebaseAuth mAuth;
    private String userIUD;
    FirebaseUser currentUser;
    List<String> fave = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_fragment, container, false);

        String[] types = getResources().getStringArray(R.array.types);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userIUD = currentUser.getUid();
        myRef = database.getReference(userIUD).child("favourites");
        editText = view.findViewById(R.id.collapseActionView);
        btnDone = view.findViewById(R.id.exitDialog);
        adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, types);
        editText.setAdapter(adapter);
        lvItem = view.findViewById(R.id.lv_item);
        itemArray = new ArrayList<>();
        itemAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,itemArray);
        //lvItem.setAdapter(itemAdapter);
        btnAdd = view.findViewById(R.id.addList);
        chipGroup = new ChipGroup(getContext());

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();
                myRef.child(text).setValue(true);
                dismiss();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = editText.getText().toString();
                lvItem.setText(item);
            }
        });

        editText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //parent.getItemAtPosition(position);
                //String item = editText.getText().toString();
                /*Chip chip = new Chip(getContext());
                chip.setText(item);
                chip.setChipBackgroundColorResource(R.color.unselectedOption);
                chip.setCloseIconVisible(true);
                chip.setTextColor(getResources().getColor(R.color.black));
                chip.setTextAppearance(R.style.ChipTextApperance);*/
                //ChipDrawable chip = ChipDrawable.createFromResource(getContext(), R.xml.standalone_chip);

                //chip.setBounds(0, 0, chip.getIntrinsicWidth(), chip.getIntrinsicHeight());
                //ImageSpan span = new ImageSpan(chip);
                //chipGroup.addView(chip);
                //Log.d("ITEM SELECTED: " , item)
;            }
        });

        return view;

    }

    protected void addItemList(AutoCompleteTextView editText) {
        if (isInputValid(this.editText)) {
            itemArray.add(this.editText.getText().toString());
            //this.editText.setText("");
            itemAdapter.notifyDataSetChanged();
        }
    }

    protected boolean isInputValid(EditText etInput2) {
        if (etInput2.getText().toString().trim().length()<1) {
            Toast.makeText(getContext(),"Please Enter Item",Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }



}
