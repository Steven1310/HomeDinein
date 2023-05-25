package com.application.homedinein;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.application.homedinein.databinding.FragmentTab2Binding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TabFragment2 extends Fragment {


    private FragmentTab2Binding binding;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    AlertDialog.Builder builder;


    public TabFragment2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_tab2, container, false);
        binding = FragmentTab2Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Date dtStart=new Date(new Date().getTime()+ 30 * 60 * 1000);
        Date dtEnd=new Date(new Date().getTime()+ 210 * 60 * 1000);
        binding.tvTime.setText(dtStart.getHours()+":"+dtStart.getMinutes());
        sharedPreferences=getContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        builder = new AlertDialog.Builder(this.getContext());
        binding.tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        binding.tvTime.setText(hourOfDay+":"+minutes);
                    }
                }, dtStart.getHours(), dtStart.getMinutes(), false);
                timePickerDialog.show();
            }
        });
        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String contact, reservee, noOfSeats;
                contact = binding.etContactNo2.getText().toString();
                reservee = binding.etReservationName2.getText().toString();
                noOfSeats = binding.etNoOfSeats.getText().toString();
                Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                if (TextUtils.isEmpty(contact)) {
                    Toast.makeText(getContext(), "Enter Contact!", Toast.LENGTH_SHORT).show();
                    binding.etContactNo2.setError("Enter Contact!");
                    binding.etContactNo2.setBackgroundResource(R.drawable.custom_error_edittext);
                    binding.etContactNo2.startAnimation(shake);
                    Vibrator vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(50);
                    return;
                }
                if (TextUtils.isEmpty(reservee)) {
                    Toast.makeText(getContext(), "Enter Reservation Name!", Toast.LENGTH_SHORT).show();
                    binding.etReservationName2.setError("Enter Reservation Name!");
                    binding.etReservationName2.setBackgroundResource(R.drawable.custom_error_edittext);
                    binding.etReservationName2.startAnimation(shake);
                    Vibrator vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(50);
                    return;
                }
                if (TextUtils.isEmpty(noOfSeats) ||Integer.parseInt(noOfSeats)<=0) {
                    Toast.makeText(getContext(), "Enter no of seats!", Toast.LENGTH_SHORT).show();
                    binding.etNoOfSeats.setError("Enter no of seats!");
                    binding.etNoOfSeats.setBackgroundResource(R.drawable.custom_error_edittext);
                    binding.etNoOfSeats.startAnimation(shake);
                    Vibrator vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(50);
                    return;
                }
                if(checktimings(binding.tvTime.getText().toString(),dtStart.getHours()+":"+dtStart.getMinutes()) || checktimings(dtEnd.getHours()+":"+dtEnd.getMinutes(),binding.tvTime.getText().toString())) {
                    builder.setMessage("Reservation time should be between "+dtStart.getHours()+":"+dtStart.getMinutes()+" and "+dtEnd.getHours()+":"+dtEnd.getMinutes())
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle("Error!");
                    alert.show();
                    return;
                }
                Intent intent =null;
                editor.putBoolean("skipReservation",false);
                editor.putString("reservationMode","Dinein");
                editor.putString("Contact",binding.etContactNo2.getText().toString());
                editor.putString("Name",binding.etReservationName2.getText().toString());
                editor.putInt("noOfSeats",Integer.parseInt(noOfSeats));
                editor.putString("time",binding.tvTime.getText().toString());
                editor.remove("Address1");
                editor.remove("Address2");
                editor.remove("PostalCode");
                editor.commit();
                if(sharedPreferences.getBoolean("orderedItems",false)) {
                    intent = new Intent(getContext(), Checkout.class);
                    Cart cart=((Reservation)getActivity()).getData();
                    cart.setModeReservation(true);
                    Bundle bundle =new Bundle();
                    bundle.putParcelable("cart",cart);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    getActivity().finish();
                }
                else {
                    intent = new Intent(getContext(), Home.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("noOfSeats", Integer.parseInt(noOfSeats));
                    intent.putExtras(bundle);
                    getContext().startActivity(intent);
                }
            }
        });
    }

    private boolean checktimings(String time, String endtime) {

        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            if(date1.before(date2)) {
                return true;
            } else {

                return false;
            }
        } catch (ParseException e){
            e.printStackTrace();
        }
        return false;
    }
}