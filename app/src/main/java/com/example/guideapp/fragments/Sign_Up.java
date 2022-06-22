package com.example.guideapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guideapp.MainActivity;
import com.example.guideapp.NavigationActivity;
import com.example.guideapp.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import classes.Calculations;

public class Sign_Up extends Fragment {

    CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private TextView usernameText;
    private TextView firstPasswordText;
    private TextView secondPasswordText;
    private Button registerButton;
    private Button returnButton;
    private Button googleSignUp;
    private final static int GOOGLE_SIGN_UP=123;
    private final static int FACEBOOK_SIGN_UP = 100;
    private GoogleSignInClient mGoogleSignInClient;
    String username,firstPassword, secondPassword;
    private LoginButton facebookLogin;

    public Sign_Up() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_sign__up, container, false);
        View view = inflater.inflate(R.layout.fragment_sign__up,container,false);
        mAuth = FirebaseAuth.getInstance();

        usernameText = view.findViewById(R.id.newEmailTxt);
        firstPasswordText = view.findViewById(R.id.passwordTxt1);
        secondPasswordText = view.findViewById(R.id.passwordTxt2);
        registerButton= view.findViewById(R.id.registerBtn);
        returnButton = view.findViewById(R.id.returnBtn);
        googleSignUp = view.findViewById(R.id.signUpGoogle);

        callbackManager = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(FacebookSdk.getApplicationContext());
        facebookLogin = view.findViewById(R.id.signUpFacebook);

        facebookLogin.setFragment(this);
        facebookLogin.setPermissions("email", "public_profile");
        facebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Toast.makeText(getContext(),"Login Successful",Toast.LENGTH_SHORT).show();
                //Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(getContext(),"Login Cancel",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(getContext(),"Error : "+ exception.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateInput();
                // RegisterUser();
            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment,new LoginPage(),null).commit();
                MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment,new LoginPage(),null).commit();
            }
        });

        createRequest();
        googleSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        return view;
    }

    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this.getContext(), gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_UP);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_UP) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                e.printStackTrace();
                // Google Sign In failed, update UI appropriately
                //Log.w(TAG, "Google sign up failed", e);
            }
        }else{
            callbackManager.onActivityResult(requestCode,resultCode,data);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signUpWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getContext(),"Google sign in failed.",Toast.LENGTH_LONG).show();
                            //Log.w(TAG, "signUpWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }


    private void ValidateInput() {
        username = usernameText.getText().toString();
        firstPassword = firstPasswordText.getText().toString();
        secondPassword = secondPasswordText.getText().toString();

        //validate passwords
        if(username.equals("") || firstPassword.equals("") ||
                secondPassword.equals("")){
            Toast.makeText(getContext(),"Enter all values.",Toast.LENGTH_SHORT).show();
        }
        else if(!firstPasswordText.getText().toString().matches(secondPasswordText.
                getText().toString())){

            Toast.makeText(getContext(), "Enter matching passwords.", Toast.LENGTH_SHORT).show();
            firstPasswordText.setText("");
            secondPasswordText.setText("");
        }else{
            mAuth.createUserWithEmailAndPassword(username,secondPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "User has been registered successfully!", Toast.LENGTH_SHORT).show();
                                MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment,new LocationOptions(),null).commit();
                            }else{
                                Toast.makeText(getContext(), "Failed to register user",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        //Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getContext(),"Facebook sign in failed.",Toast.LENGTH_LONG).show();
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getContext(), "Sign in failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    public void updateUI(FirebaseUser account){

        if(account != null){
            //Toast.makeText(getContext(),"Signed in successfully user : " + account,Toast.LENGTH_LONG).show();
           // Log.d("SIGN IN ID: " , String.valueOf(account));
            String userId = account.getUid();
            MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment,LocationOptions.class,null).commit();

        }

    }
}

