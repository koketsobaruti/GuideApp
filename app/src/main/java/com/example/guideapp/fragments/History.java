package com.example.guideapp.fragments;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.guideapp.R;


public class History extends Fragment  {

    private Button suggestionsBtn, histBtn;
    public History() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        History_List hist = new History_List();
        getChildFragmentManager().beginTransaction().replace(R.id.fragmentHistory, hist).commit();
        suggestionsBtn = view.findViewById(R.id.histSuggestionsBtn);
        histBtn = view.findViewById(R.id.searchHistBtn);

        suggestionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUnselectedColour(histBtn);
                changeSelectedColour(suggestionsBtn);

                Suggestions suggestions = new Suggestions();
                getChildFragmentManager().beginTransaction().replace(R.id.fragmentHistory, suggestions).commit();
            }
        });

        histBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSelectedColour(histBtn);
                changeUnselectedColour(suggestionsBtn);
                History_List hist = new History_List();
                getChildFragmentManager().beginTransaction().replace(R.id.fragmentHistory, hist).commit();
            }
        });

        return view;
    }

    private void changeUnselectedColour(Button btn){
        Resources res = getResources();
        int back =  ResourcesCompat.getColor(res, R.color.histUnselected,null);
        int txt = ResourcesCompat.getColor(res, R.color.black,null);

        btn.setBackgroundColor(back);
        btn.setTextColor(txt);
    }

    private void changeSelectedColour(Button btn){
        Resources res = getResources();
        int back =  ResourcesCompat.getColor(res, R.color.histSelected,null);
        int txt = ResourcesCompat.getColor(res, R.color.white,null);

        btn.setBackgroundColor(back);
        btn.setTextColor(txt);
    }
}