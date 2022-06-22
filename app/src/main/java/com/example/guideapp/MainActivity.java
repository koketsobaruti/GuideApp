package com.example.guideapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.guideapp.fragments.LoginPage;
import com.example.guideapp.fragments.Sign_Up;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    public static FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        mAuth = FirebaseAuth.getInstance();
        if(findViewById(R.id.fragment) != null){
            if(savedInstanceState != null){
                return;
            }
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //loginFragment homeFragment = new loginFragment();
            fragmentTransaction.add(R.id.fragment,new LoginPage()).commit();
            //fragmentTransaction.add(R.id.firstFragment,homeFragment,null);
            //fragmentTransaction.commit();
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment,new Sign_Up(),null).commit();
        }
    }
}