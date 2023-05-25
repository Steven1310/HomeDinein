package com.application.homedinein;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class Home extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    public static final String EXTRA_USER = "com.application.homedinein.extra.USER";
    String userKey = "temp@xyz.com";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    Handler mHandler;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;

    private FirestoreUtil firestoreUtil;
    AutoCompleteTextView acSearchRestaurant;
    int noOfSeats=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ComponentName callingApplication = getCallingActivity();
        //Log.i("HPA", callingApplication.toString());
        Intent intent = getIntent();
        Bundle data= intent.getExtras();
        if(savedInstanceState!=null)
        {
            noOfSeats=savedInstanceState.getInt("noOfSeats",-1);
        }
        if(data !=null){
            noOfSeats=data.getInt("noOfSeats",-1);
        }
        //Getting the instance of AutoCompleteTextView
        acSearchRestaurant = (AutoCompleteTextView) findViewById(R.id.acSearchRestaurant);
        acSearchRestaurant.setThreshold(1);//will start working from first character
        FloatingActionButton fab = findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Opening Profile", Snackbar.LENGTH_LONG).show();
                    //startConversation(Home.this,true);
                    Intent intent = new Intent(getApplicationContext(), Profile.class);
                    startActivity(intent);
                }
            });
        }
        sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        firebaseAuth = FirebaseAuth.getInstance();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        firestoreUtil = new FirestoreUtil(Home.this,recyclerView,acSearchRestaurant,noOfSeats);
        firestoreUtil.getRestaurants();
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("noOfSeats",noOfSeats);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sharedPreferences.getString("LoginMode", "").equals("GOOGLE")) {
            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
            if (opr.isDone()) {
                GoogleSignInResult result = opr.get();
                handleSignInResult(result);
            } else {
                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(GoogleSignInResult googleSignInResult) {
                        handleSignInResult(googleSignInResult);
                    }
                });
            }
        } else {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            userKey = user.getEmail();
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            userKey = account.getEmail();
        }
    }

}