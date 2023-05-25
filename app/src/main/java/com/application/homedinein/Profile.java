package com.application.homedinein;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Profile extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
        Button logoutBtn;
        TextView userName,userEmail,userId;
        ImageView profileImage;
        private GoogleApiClient googleApiClient;
        private GoogleSignInOptions gso;
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        FirebaseAuth firebaseAuth;
        RecyclerView recyclerView;
        RecyclerView.LayoutManager layoutManager;

        FirestoreUtil firestoreUtil;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_profile);

                logoutBtn=(Button)findViewById(R.id.logoutBtn);
                userName=(TextView)findViewById(R.id.name);
                userEmail=(TextView)findViewById(R.id.email);
                userId=(TextView)findViewById(R.id.userId);
                profileImage=(ImageView)findViewById(R.id.profileImage);

                gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();

                googleApiClient=new GoogleApiClient.Builder(this)
                        .enableAutoManage(this,this)
                        .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                        .build();


                logoutBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                                        new ResultCallback<Status>() {
                                                @Override
                                                public void onResult(Status status) {
                                                        if (status.isSuccess()){
                                                                FirebaseAuth.getInstance().signOut();
                                                                gotoMainActivity();
                                                        }else{
                                                                Toast.makeText(getApplicationContext(),"Session not close",Toast.LENGTH_LONG).show();
                                                        }
                                                }
                                        });
                        }
                });
                sharedPreferences=getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
                editor=sharedPreferences.edit();
                firebaseAuth = FirebaseAuth.getInstance();
                recyclerView = findViewById(R.id.recyclerViewOrders);
                layoutManager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(layoutManager);
                firestoreUtil = new FirestoreUtil(Profile.this,recyclerView);
        }

        @Override
        protected void onStart() {
                super.onStart();
                if(sharedPreferences.getString("LoginMode","").equals("GOOGLE")) {
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
                }
                else{
                        FirebaseUser user=firebaseAuth.getCurrentUser();
                        userName.setText(user.getDisplayName());
                        userEmail.setText(user.getEmail());
                        userId.setText(user.getUid());
                        try{
                                Glide.with(getBaseContext()).load(user.getPhotoUrl()).into(profileImage);
                        }catch (NullPointerException e){
                                Toast.makeText(getApplicationContext(),"image not found",Toast.LENGTH_LONG).show();
                        }
                }
                firestoreUtil.getPastOrders();
        }
        private void handleSignInResult(GoogleSignInResult result){
                if(result.isSuccess()){
                        GoogleSignInAccount account=result.getSignInAccount();
                        userName.setText(account.getDisplayName());
                        userEmail.setText(account.getEmail());
                        userId.setText(account.getId());
                        try{
                                Glide.with(this).load(account.getPhotoUrl()).into(profileImage);
                        }catch (NullPointerException e){
                                Toast.makeText(getApplicationContext(),"image not found",Toast.LENGTH_LONG).show();
                        }

                }else{
                        gotoMainActivity();
                }
        }
        private void gotoMainActivity(){
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
        }
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

        }
}