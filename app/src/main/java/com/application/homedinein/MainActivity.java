package com.application.homedinein;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, SignUp,SignIn{

    private static final String TAG = "MainActivity";
    public final static  String LoginMode="com.application.homedinein.LoginMode";
    private SignInButton signInButton;
    //private GoogleApiClient googleApiClient;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    private static final int RC_GOOGLE_SIGN_IN = 1;
    private static final int RC_FB_SIGN_IN = 2;
    String name, email;
    String idToken;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ImageView loginShowHidePass;
    Fragment fragment;
    EditText login_username,login_password;
    Button registerFrag;
    Fragment signIn,signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragment=getSupportFragmentManager().findFragmentById(R.id.frag1);
        sharedPreferences=getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        firebaseAuth = FirebaseAuth.getInstance();
        configureGoogleClient();
        //AppEventsLogger.activateApp(this);
        /*login_username=(EditText) findViewById(R.id.login_username);
        login_password=(EditText) findViewById(R.id.login_password);

        loginShowHidePass=findViewById(R.id.loginShowHidePass);

        registerFrag=findViewById(R.id.registerFrag);*/






        signIn=new SigninFragment();
        signUp=new SignupFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.frag1,new SigninFragment()).commit();
        editor.putBoolean("orderedItems",false);
        editor.commit();
    }


    private void configureGoogleClient() {
        // Configure Google Sign In
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // for the requestIdToken, this is in the values.xml file that
                // is generated from your google-services.json
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInToGoogle();
            }
        });


    }


    public void signInToGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_GOOGLE_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
            /*GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);*/
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Log.d(TAG, "signInWithCredential:success: currentUser: " + user.getEmail());
                            editor.putString(LoginMode,"GOOGLE");
                            editor.commit();
                            autoLoginProfile();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    private void autoLoginProfile(){
        Intent intent = new Intent(MainActivity.this, Reservation.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivityForResult(intent,0);
        finish();
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "Currently Signed in: " + currentUser.getEmail());
            /*Toast.makeText(this,"Currently Logged in: " + currentUser.getEmail(),Toast.LENGTH_SHORT).show();*/
            autoLoginProfile();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    public boolean isNetworkConnected(){
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo()!=null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onConnectionFailed( @NotNull ConnectionResult connectionResult) {
        if(!isNetworkConnected()){
            Toast.makeText(MainActivity.this,"Bad network connection!",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void loadFragmentSignin() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frag1,new SigninFragment()).addToBackStack(null).commit();
    }

    @Override
    public void loadFragmentSignup() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frag1,new SignupFragment()).addToBackStack(null).commit();
    }
}