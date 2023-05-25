package com.application.homedinein;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.application.homedinein.databinding.ActivityCheckoutBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class Checkout extends AppCompatActivity implements View.OnClickListener {
    ActivityCheckoutBinding binding;
    Cart cart = null;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        cart = bundle.getParcelable("cart");
        String details = "";
        details = "\nRestaurant Name: " + cart.restaurant.getTitle() + "\nOrders:\nAppetizers:\n";
        for (FoodItems item : cart.appetizer)
            details += "" + item.Name + "  $" + item.Price + " X " + item.Quantity + "\n";
        details += "\nMains:\n";
        for (FoodItems item : cart.mains)
            details += "" + item.Name + "  $" + item.Price + " X " + item.Quantity + "\n";
        if (cart.isModeReservation)
            details += "\nTotal(50% reservation fee): $" + cart.reservationPrice;
        else
            details += "\nTotal: $" + cart.totalPrice;
        binding.etTextMultiLine.setText(details);
        binding.etOrderType.setText(cart.isModeReservation ? "Dinein" : "Takeout");
        binding.btnEditOrder.setOnClickListener(this);
        binding.btnPlaceOrder.setOnClickListener(this);
        cart.name = sharedPreferences.getString("Name", "");
        cart.contact = sharedPreferences.getString("Contact", "");
        if (cart.isModeReservation) {
            cart.noOfseatings = "" + sharedPreferences.getInt("noOfSeats", -1);
            cart.time = sharedPreferences.getString("time", "");
        } else {
            cart.address1 = sharedPreferences.getString("Address1", "");
            cart.address2 = sharedPreferences.getString("Address2", "");
            cart.postalCode = sharedPreferences.getString("PostalCode", "");
        }
        cart.orderDetails=details;

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnEditOrder) {
            finish();
        } else if (view.getId() == R.id.btnPlaceOrder) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String docType=cart.isModeReservation ? "D" : "T";
            db.collection("Orders").document(docType + System.currentTimeMillis())
                    .set(cart).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Snackbar.make(view, "Order Placed", Snackbar.LENGTH_LONG).show();
                            Intent i = new Intent(Checkout.this, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(view, "Try again later!", Snackbar.LENGTH_LONG).show();
                        }
                    });
        }
    }
}