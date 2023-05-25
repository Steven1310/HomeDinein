package com.application.homedinein;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import com.application.homedinein.databinding.ActivityRestaurantDetailsBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class RestaurantDetails extends AppCompatActivity {


    ActivityRestaurantDetailsBinding binding;

    private FirestoreUtil firestoreUtilAppetizer,firestoreUtilMains;
    ArrayList<String> Image;
    int imagePosition=0;
    int RestaurantId=0;
    Restaurant restaurant=null;
    RecyclerView recyclerViewAppetizers,recyclerViewMains;
    RecyclerView.LayoutManager layoutManagerAppetizers,layoutManagerMains;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
        View root=binding.getRoot();
        setContentView(root);
        sharedPreferences=getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        Image=bundle.getStringArrayList("images");
        RestaurantId=bundle.getInt("id");
        restaurant=bundle.getParcelable("restaurant");
        Picasso.get().load(Image.get(0)).into(binding.imageViewScroll);
        //firestoreUtil = new FirestoreUtil();
        recyclerViewAppetizers = findViewById(R.id.recyclerViewAppetizers);
        layoutManagerAppetizers = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerViewAppetizers.setLayoutManager(layoutManagerAppetizers);
        firestoreUtilAppetizer = new FirestoreUtil(RestaurantDetails.this,recyclerViewAppetizers);
        recyclerViewMains = findViewById(R.id.recyclerViewMains);
        layoutManagerMains = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerViewMains.setLayoutManager(layoutManagerMains);
        firestoreUtilMains = new FirestoreUtil(RestaurantDetails.this,recyclerViewMains);
        Handler h=new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    imagePosition++;
                    h.post(new Runnable() {
                        public void run() {
                            Picasso.get().load(Image.get(imagePosition%4)).into(binding.imageViewScroll);
                        }
                    });
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
        //floating action button on click
        if (binding.fab != null) {
            binding.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Snackbar.make(view, "Opening Cart", Snackbar.LENGTH_LONG).show();
                    editor.putBoolean("orderedItems",true);
                    editor.commit();
                    Intent intent=null;
                    if(sharedPreferences.getBoolean("skipReservation",true))
                        intent=new Intent(getBaseContext(), Reservation.class);
                    else
                        intent=new Intent(getBaseContext(), Checkout.class);
                    Boolean isModeReservation=sharedPreferences.getString("reservationMode","Takeout").equalsIgnoreCase("Dinein");
                    Cart cart=new Cart(restaurant,firestoreUtilAppetizer.getOrderedAppetizers(),firestoreUtilMains.getOrderedMains(),isModeReservation);
                    Bundle bundle =new Bundle();
                    bundle.putParcelable("cart",cart);
                    intent.putExtras(bundle);
                    startActivityForResult(intent,1);
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firestoreUtilAppetizer.getFoodItems(RestaurantId,"Appetizers");
        firestoreUtilMains.getFoodItems(RestaurantId,"MainCourse");

    }
}