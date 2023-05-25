package com.application.homedinein;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Reservation extends AppCompatActivity{
    Button btn_skip;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Cart cart=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        btn_skip=(Button) findViewById(R.id.btn_skip);
        sharedPreferences=getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        btn_skip.setVisibility(View.VISIBLE);
        ComponentName callingApplication = getCallingActivity();
        //Log.i("Reservation",callingApplication);
        if (callingApplication != null) {
            String message = callingApplication.getShortClassName().toString();
            Log.i("Reservation",message);
            Intent intent = getIntent();
            if (message.equals(".MainActivity")) {

            } else if (message.equals(".RestaurantDetails")) {
                btn_skip.setVisibility(View.GONE);
                Bundle bundle = intent.getExtras();
                cart = bundle.getParcelable("cart");
            }
            //new Intent().putExtra("com.example.flower_sale.MESSAGE", message1);
        }

        // Create an instance of the tab layout from the view.
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        // Set the text for each tab.
        //tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_fragment1));
        //tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_fragment2));

        // Set the tabs to fill the entire layout.
        //tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Use PagerAdapter to manage page views in fragments.
        // Each page is represented by its own fragment.
        final ViewPager2 viewPager = findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), getLifecycle(),viewPager);
        viewPager.setAdapter(adapter);

        ArrayList<String> tablist=new ArrayList<String>();
        tablist.add(getString(R.string.tab_fragment1));
        tablist.add(getString(R.string.tab_fragment2));

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tablist.get(position))
        ).attach();

        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(getApplicationContext(),Home.class);
                editor.putBoolean("skipReservation",true);
                editor.commit();
                startActivity(i);
                finish();
            }
        });


    }

    public Cart getData(){
        return  cart;
    }
}