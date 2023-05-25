package com.application.homedinein;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Vibrator;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static android.content.Context.CONNECTIVITY_SERVICE;


public class SigninFragment extends Fragment implements View.OnClickListener {
    ImageView loginShowHidePass;
    EditText login_username,login_password;
    Button registerFrag,login_submit;
    FirebaseAuth firebaseAuth;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextView tvForgotPassword;


    public SigninFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_signin, container, false);
        login_username=(EditText) root.findViewById(R.id.login_username);
        login_password=(EditText) root.findViewById(R.id.login_password);
        login_submit=root.findViewById(R.id.login_submit);
        loginShowHidePass=root.findViewById(R.id.loginShowHidePass);

        registerFrag=root.findViewById(R.id.registerFrag);
        loginShowHidePass.setOnClickListener(this);
        login_submit.setOnClickListener(this);
        registerFrag.setOnClickListener(this);
        tvForgotPassword=root.findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setOnClickListener(this);
        //tvForgotPassword.setVisibility(View.VISIBLE);
        firebaseAuth=FirebaseAuth.getInstance();
        sharedPreferences=root.getContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        return root;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.loginShowHidePass){

            if(loginShowHidePass.getTag()=="show") {
                login_password.setTransformationMethod(null);
                loginShowHidePass.setBackground(getActivity().getDrawable(R.drawable.ic_hide_eye));
                loginShowHidePass.setTag("hide");
            }
            else {
                login_password.setTransformationMethod(new PasswordTransformationMethod());
                loginShowHidePass.setBackground(getActivity().getDrawable(R.drawable.ic_show_eye));
                loginShowHidePass.setTag("show");
            }
        }
        else if(view.getId()==R.id.registerFrag){
            tvForgotPassword.setVisibility(View.INVISIBLE);
            SignUp signUp=(SignUp)getActivity();
            signUp.loadFragmentSignup();
        }
        else if(view.getId()==R.id.login_submit){
            String username,password;
            username=login_username.getText().toString();
            password=login_password.getText().toString();
            Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
            if (TextUtils.isEmpty(username)) {
                Toast.makeText(this.getContext(), "Enter username!", Toast.LENGTH_SHORT).show();
                login_username.setError("Enter username!");
                login_username.setBackgroundResource(R.drawable.custom_error_edittext);
                login_username.startAnimation(shake);
                Vibrator vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(50);
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                Toast.makeText(this.getContext(), "Enter proper email!", Toast.LENGTH_SHORT).show();
                login_username.setError("Enter proper email!");
                login_username.setBackgroundResource(R.drawable.custom_error_edittext);
                login_username.startAnimation(shake);
                Vibrator vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(50);
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this.getContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                login_password.setError("Enter password!");
                login_password.setBackgroundResource(R.drawable.custom_error_edittext);
                login_password.startAnimation(shake);
                Vibrator vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(50);
                return;
            }
            if (password.length() < 6) {
                Toast.makeText(this.getContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                login_password.setError("Password too short, enter minimum 6 characters!");
                login_password.setBackgroundResource(R.drawable.custom_error_edittext);
                login_password.startAnimation(shake);
                Vibrator vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(50);
                return;
            }
            if(isNetworkConnected()) {
                firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            editor.putString("LoginMode", "FIREBASE_EMAIL");
                            editor.commit();
                            Intent i = new Intent(getActivity(), Reservation.class);
                            startActivityForResult(i,0);
                        } else {
                            Toast.makeText(getContext(), "Login failed"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else{
                Toast.makeText(getContext(),"Network error, try again later",Toast.LENGTH_SHORT).show();
            }
        }
        else if(view.getId()==R.id.tvForgotPassword) {
            String username, password;
            username = login_username.getText().toString();
            password = login_password.getText().toString();
            Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
            if (username.isEmpty()) {
                Toast.makeText(getContext(), "Enter username", Toast.LENGTH_SHORT).show();
                login_username.setError("Enter username!");
                login_username.setBackgroundResource(R.drawable.custom_error_edittext);
                login_username.startAnimation(shake);
                Vibrator vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(50);
                return;
            }
            if (isNetworkConnected()) {
                firebaseAuth.sendPasswordResetEmail(username).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(Task task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Check email to reset your password!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Fail to send reset password email!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else{
                Toast.makeText(getContext(),"Network error, try again later",Toast.LENGTH_SHORT).show();
            }
        }
    }


    public boolean isNetworkConnected(){
        ConnectivityManager cm = (ConnectivityManager) this.getActivity().getSystemService(CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo()!=null && cm.getActiveNetworkInfo().isConnected();
    }
}

interface SignIn{
    public void loadFragmentSignin();
}