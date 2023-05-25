package com.application.homedinein;

import android.content.Context;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static android.content.Context.CONNECTIVITY_SERVICE;


public class SignupFragment extends Fragment implements View.OnClickListener {

    EditText reg_username, reg_password, reg_password_confirm;
    Button loginFrag, reg_submit;
    ImageView regShowHidePass, regShowHidePassConfirm;
    FirebaseAuth firebaseAuth;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_signup, container, false);
        reg_username = (EditText) root.findViewById(R.id.reg_username);
        reg_password = (EditText) root.findViewById(R.id.reg_password);
        reg_password_confirm = (EditText) root.findViewById(R.id.reg_password_confirm);
        regShowHidePass = root.findViewById(R.id.regShowHidePass);
        regShowHidePassConfirm = root.findViewById(R.id.regShowHidePassConfirm);
        loginFrag = root.findViewById(R.id.loginFrag);
        regShowHidePass.setOnClickListener(this);
        regShowHidePassConfirm.setOnClickListener(this);
        loginFrag = root.findViewById(R.id.loginFrag);
        loginFrag.setOnClickListener(this);
        reg_submit = root.findViewById(R.id.reg_submit);
        reg_submit.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = root.getContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        return root;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.regShowHidePass) {

            if (regShowHidePass.getTag() == "show") {
                reg_password.setTransformationMethod(null);
                regShowHidePass.setBackground(getActivity().getDrawable(R.drawable.ic_hide_eye));
                regShowHidePass.setTag("hide");
            } else {
                reg_password.setTransformationMethod(new PasswordTransformationMethod());
                regShowHidePass.setBackground(getActivity().getDrawable(R.drawable.ic_show_eye));
                regShowHidePass.setTag("show");
            }
        } else if (view.getId() == R.id.regShowHidePassConfirm) {
            if (regShowHidePassConfirm.getTag() == "show") {
                reg_password_confirm.setTransformationMethod(null);
                regShowHidePassConfirm.setBackground(getActivity().getDrawable(R.drawable.ic_hide_eye));
                regShowHidePassConfirm.setTag("hide");
            } else {
                reg_password_confirm.setTransformationMethod(new PasswordTransformationMethod());
                regShowHidePassConfirm.setBackground(getActivity().getDrawable(R.drawable.ic_show_eye));
                regShowHidePassConfirm.setTag("show");
            }

        } else if (view.getId() == R.id.loginFrag) {
            SignIn signIn = (SignIn) getActivity();
            signIn.loadFragmentSignin();
        } else if (view.getId() == R.id.reg_submit) {
            String username, password, confirmPassword;
            username = reg_username.getText().toString();
            password = reg_password.getText().toString();
            confirmPassword = reg_password_confirm.getText().toString();
            Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
            if (TextUtils.isEmpty(username)) {
                Toast.makeText(getContext(), "Enter username!", Toast.LENGTH_SHORT).show();
                reg_username.setError("Enter username!");
                reg_username.setBackgroundResource(R.drawable.custom_error_edittext);
                reg_username.startAnimation(shake);
                Vibrator vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(50);
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                Toast.makeText(getContext(), "Enter proper email!", Toast.LENGTH_SHORT).show();
                reg_username.setError("Enter proper email!");
                reg_username.setBackgroundResource(R.drawable.custom_error_edittext);
                reg_username.startAnimation(shake);
                Vibrator vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(50);
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                reg_password.setError("Enter password!");
                reg_password.setBackgroundResource(R.drawable.custom_error_edittext);
                reg_password.startAnimation(shake);
                Vibrator vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(50);
                return;
            }
            if (TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(getContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                reg_password_confirm.setError("Enter password!");
                reg_password_confirm.setBackgroundResource(R.drawable.custom_error_edittext);
                reg_password_confirm.startAnimation(shake);
                Vibrator vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(50);
                return;
            }
            if (password.length() < 6) {
                Toast.makeText(getContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                reg_password.setError("Password too short, enter minimum 6 characters!");
                reg_password.setBackgroundResource(R.drawable.custom_error_edittext);
                reg_password.startAnimation(shake);
                Vibrator vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(50);
                return;
            }
            if (confirmPassword.length() < 6) {
                Toast.makeText(getContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                reg_password_confirm.setError("Password too short, enter minimum 6 characters!");
                reg_password_confirm.setBackgroundResource(R.drawable.custom_error_edittext);
                reg_password_confirm.startAnimation(shake);
                Vibrator vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(50);
                return;
            }
            if (!password.equals(confirmPassword)) {
                reg_password.setError("Invalid passwaord");
                reg_password.setBackgroundResource(R.drawable.custom_error_edittext);
                reg_password.startAnimation(shake);
                reg_password_confirm.setError("Invalid passwaord");
                reg_password_confirm.setBackgroundResource(R.drawable.custom_error_edittext);
                reg_password_confirm.startAnimation(shake);
                Vibrator vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(50);
                Toast.makeText(getContext(), "passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            if(isNetworkConnected()) {
                firebaseAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Registerd Successfully,kindly login", Toast.LENGTH_SHORT).show();
                            SignIn signIn = (SignIn) getActivity();
                            signIn.loadFragmentSignin();
                        } else {
                            Toast.makeText(getContext(), "Error occured " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else {
                Toast.makeText(getContext(), "Network error, try again later", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isNetworkConnected(){
        ConnectivityManager cm = (ConnectivityManager) this.getActivity().getSystemService(CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo()!=null && cm.getActiveNetworkInfo().isConnected();
    }
}

interface SignUp{
    public void loadFragmentSignup();
}