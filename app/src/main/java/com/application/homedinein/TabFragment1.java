package com.application.homedinein;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.application.homedinein.databinding.FragmentTab1Binding;

public class TabFragment1 extends Fragment {
    private FragmentTab1Binding binding;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public TabFragment1() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_tab1, container, false);
        binding = FragmentTab1Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences=getContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        binding.button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String contact, reservee, address1, address2, postalCode;
                contact = binding.etContactNo1.getText().toString();
                reservee = binding.etReservationName1.getText().toString();
                address1 = binding.etAddress1.getText().toString();
                address2 = binding.etAddress2.getText().toString();
                postalCode = binding.etPostalCode.getText().toString();
                Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                if (TextUtils.isEmpty(contact)) {
                    Toast.makeText(getContext(), "Enter Contact!", Toast.LENGTH_SHORT).show();
                    binding.etContactNo1.setError("Enter Contact!");
                    binding.etContactNo1.setBackgroundResource(R.drawable.custom_error_edittext);
                    binding.etContactNo1.startAnimation(shake);
                    Vibrator vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(50);
                    return;
                }
                if (TextUtils.isEmpty(reservee)) {
                    Toast.makeText(getContext(), "Enter Reservation Name!", Toast.LENGTH_SHORT).show();
                    binding.etReservationName1.setError("Enter Reservation Name!");
                    binding.etReservationName1.setBackgroundResource(R.drawable.custom_error_edittext);
                    binding.etReservationName1.startAnimation(shake);
                    Vibrator vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(50);
                    return;
                }
                if (TextUtils.isEmpty(address1)) {
                    Toast.makeText(getContext(), "Enter Address line 1!", Toast.LENGTH_SHORT).show();
                    binding.etAddress1.setError("Enter Address line 1!");
                    binding.etAddress1.setBackgroundResource(R.drawable.custom_error_edittext);
                    binding.etAddress1.startAnimation(shake);
                    Vibrator vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(50);
                    return;
                }
                if (TextUtils.isEmpty(address2)) {
                    Toast.makeText(getContext(), "Enter Address line 2!", Toast.LENGTH_SHORT).show();
                    binding.etAddress2.setError("Enter Address line 2!");
                    binding.etAddress2.setBackgroundResource(R.drawable.custom_error_edittext);
                    binding.etAddress2.startAnimation(shake);
                    Vibrator vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(50);
                    return;
                }
                if (TextUtils.isEmpty(postalCode)) {
                    Toast.makeText(getContext(), "Enter Postal Code!", Toast.LENGTH_SHORT).show();
                    binding.etPostalCode.setError("Enter Postal Code!");
                    binding.etPostalCode.setBackgroundResource(R.drawable.custom_error_edittext);
                    binding.etPostalCode.startAnimation(shake);
                    Vibrator vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(50);
                    return;
                }
                Intent intent =null;
                editor.putBoolean("skipReservation",false);
                editor.putString("reservationMode","Takeout");
                editor.putString("Contact",contact);
                editor.putString("Name",reservee);
                editor.putString("Address1",address1);
                editor.putString("Address2",address2);
                editor.putString("PostalCode",postalCode);
                editor.remove("noOfSeats");
                editor.remove("time");
                editor.commit();
                if(sharedPreferences.getBoolean("orderedItems",false)) {
                    intent = new Intent(getContext(), Checkout.class);
                    Cart cart=((Reservation)getActivity()).getData();
                    Bundle bundle =new Bundle();
                    bundle.putParcelable("cart",cart);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    getActivity().finish();
                }
                else {
                    intent = new Intent(getContext(), Home.class);
                    getContext().startActivity(intent);
                }
            }
        });
    }

}
