package com.example.guideapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.guideapp.fragments.Bookmarks;
import com.example.guideapp.fragments.History;
import com.example.guideapp.fragments.Map;
import com.example.guideapp.fragments.Profile;
import com.example.guideapp.fragments.Schedule;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class NavigationActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView navigation;
    public static FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        //initialize fragment
        Fragment fragment = new Map();
        getSupportFragmentManager().beginTransaction().replace(R.id.navigationFragment, fragment).commit();
        //load the map on the fragment
        loadFragment(fragment);


        navigation = findViewById(R.id.bottomNavigationView);
        navigation.setId(R.id.navigation_map);
        navigation.setOnNavigationItemSelectedListener(this);
    }

    /*@Override
    public void onTaskDone(Object... values) {
        if(currentPolyline!=null){currentPolyline.remove();}
        currentPolyline = map.addPolyline((PolylineOptions) values[0]);
    }
*/
    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.navigationFragment, fragment).commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch(item.getItemId()){
            case R.id.navigation_account:
                fragment = new Profile();
                break;
            case R.id.navigation_bookmarks:
                fragment = new Bookmarks();
                break;
            case R.id.navigation_schedule:
                fragment = new Schedule();
                break;
            case R.id.navigation_map:
                fragment = new Map();
                break;
            case R.id.navigation_history:
                fragment = new History();
                break;
        }

        return loadFragment(fragment);
    }


}