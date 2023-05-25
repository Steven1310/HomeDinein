package com.application.homedinein;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class FirestoreUtil implements AdapterView.OnItemClickListener, TextWatcher {
    FirebaseFirestore db;
    ArrayList<Restaurant> restaurantArrayList;
    ArrayList<String> restaurantNameArrayList;
    ArrayList<FoodItems> appetizerItemsArrayList;
    ArrayList<FoodItems> mainItemsArrayList;
    ArrayList<Cart> pastOrders;
    private final static String TAG = "FirestoreUtil";

    Context context;
    RecyclerView recyclerView;

    RecyclerView.Adapter adapter;
    ArrayAdapter<String> stringArrayAdapter;
    AutoCompleteTextView acSearchRestaurant;
    int noOfSeats;

    public FirestoreUtil(Context context, RecyclerView recyclerView) {
        this.db = FirebaseFirestore.getInstance();
        this.appetizerItemsArrayList = new ArrayList<FoodItems>();
        this.mainItemsArrayList = new ArrayList<FoodItems>();
        this.pastOrders = new ArrayList<>();
        this.context = context;
        this.recyclerView = recyclerView;
    }

    public FirestoreUtil(Context context, RecyclerView recyclerView, AutoCompleteTextView acSearchRestaurant, int noOfSeats) {
        this.db = FirebaseFirestore.getInstance();
        this.restaurantArrayList = new ArrayList<Restaurant>();
        this.restaurantNameArrayList = new ArrayList<>();
        this.context = context;
        this.recyclerView = recyclerView;
        this.acSearchRestaurant = acSearchRestaurant;
        this.acSearchRestaurant.setOnItemClickListener(this);
        this.acSearchRestaurant.addTextChangedListener(this);
        this.noOfSeats = noOfSeats;
    }

    public void getRestaurants() {

        db.collection("Restaurants")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                try {
                                    String Title, overview;
                                    ArrayList<String> Image;
                                    double Ratings;
                                    int SeatingPerTable, NumberOfTables;
                                    boolean isDisabled = false;
                                    ArrayList<Object> objects = (ArrayList<Object>) document.getData().get("list");
                                    for (int i = 0; i < objects.size(); i++) {
                                        HashMap<String, Object> hashMap = (HashMap<String, Object>) objects.get(i);
                                        Title = hashMap.get("Title").toString();
                                        Image = (ArrayList<String>) hashMap.get("Image");
                                        overview = "";
                                        Ratings = (double) hashMap.get("Ratings");
                                        SeatingPerTable = Integer.parseInt(hashMap.get("SeatingPerTable").toString());
                                        NumberOfTables = Integer.parseInt(hashMap.get("NumberOfTables").toString());
                                        Log.d(TAG, Title + " " + Image.toString() + " " + Ratings);
                                        restaurantNameArrayList.add(Title);
                                        if (noOfSeats > (NumberOfTables * SeatingPerTable))
                                            isDisabled = true;
                                        else
                                            isDisabled = false;
                                        restaurantArrayList.add(new Restaurant(Title, Image, overview, Ratings, SeatingPerTable, NumberOfTables, isDisabled));
                                    }
                                    Log.d(TAG, "Restaurants fetched");
                                    stringArrayAdapter = new ArrayAdapter<String>
                                            (context, android.R.layout.select_dialog_item, restaurantNameArrayList);
                                    acSearchRestaurant.setAdapter(stringArrayAdapter);
                                    adapter = new RecyclerAdapter(restaurantArrayList, context, "Restaurant");
                                    recyclerView.setAdapter(adapter);

                                } catch (Exception e) {
                                    Log.e(TAG, e.getMessage());
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        //return restaurantArrayList;
    }

    public ArrayList<FoodItems> getOrderedAppetizers() {
        ArrayList<FoodItems> orderedItems = new ArrayList<>();
        for (FoodItems item : appetizerItemsArrayList)
            if (item.isCartItem())
                orderedItems.add(item);
        return orderedItems;
    }

    public ArrayList<FoodItems> getOrderedMains() {
        ArrayList<FoodItems> orderedItems = new ArrayList<>();
        for (FoodItems item : mainItemsArrayList)
            if (item.isCartItem())
                orderedItems.add(item);
        return orderedItems;
    }

    public void getPastOrders() {

        db.collection("Orders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                try {
                                    String Name, Contact, OrderDetails, Review,OrderNo;
                                    double TotalPrice, ReservationPrice;
                                    boolean isReservation = false;
                                    Restaurant restaurant = null;
                                    ArrayList<FoodItems> appetizers, mains;
                                    HashMap<String, Object> objects = (HashMap<String, Object>) document.getData();
                                    //Log.d(TAG, "Restaurants fetched " + objects);
                                    //for (int i = 0; i < objects.size(); i++) {
                                    //HashMap<String, Object> hashMap = (HashMap<String, Object>) objects.get(i);
                                    Name = objects.get("name").toString();
                                    Contact = objects.get("contact").toString();
                                    OrderDetails = objects.get("orderDetails").toString();
                                    TotalPrice = (double) objects.get("totalPrice");
                                    ReservationPrice = (double) objects.get("reservationPrice");
                                    appetizers = (ArrayList<FoodItems>) objects.get("appetizer");
                                    mains = (ArrayList<FoodItems>) objects.get("mains");
                                    isReservation = (Boolean) objects.get("isModeReservation");
                                    Review=(String)objects.get("review");
                                    OrderNo=(String)document.getId();
                                    HashMap<String, Object> restHasMap = (HashMap<String, Object>) objects.get("restaurant");
                                    restaurant = new Restaurant(
                                            (String) restHasMap.get("title"),
                                            (ArrayList<String>) restHasMap.get("image"),
                                            "",
                                            (Double) restHasMap.get("rating"),
                                            Integer.parseInt(restHasMap.get("seatingPerTable").toString()),
                                            Integer.parseInt(restHasMap.get("numberOfTables").toString()),
                                            (Boolean) restHasMap.get("disabled"));
                                    Log.d(TAG, "Restaurant " + restaurant);
                                    Log.d(TAG, "Appetizers " + appetizers);
                                    Log.d(TAG, "Mains " + mains);
                                    //restaurantArrayList.add(new Restaurant(Title, Image, overview, Ratings, SeatingPerTable, NumberOfTables,isDisabled));
                                    Cart cart = new Cart(restaurant,isReservation,Name,Contact,OrderDetails,Review,OrderNo);
                                    pastOrders.add(cart);
                                    //}

                                    //recyclerView.setAdapter(adapter);

                                } catch (Exception e) {
                                    Log.e("Eroor", e.getMessage());
                                }
                            }
                            Log.d(TAG, "Orders fetched" + pastOrders);
                            adapter = new RecyclerAdapter(pastOrders, context,"Orders");
                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        //return restaurantArrayList;
    }

    public void getFoodItems(int Restaurant, String type) {

        db.collection("FoodItems")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(Restaurant);
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            try {
                                String Name;
                                double Price;
                                HashMap<String, Object> hashMapDishes = (HashMap<String, Object>) document.getData().get("FoodDishes");
                                ArrayList<Object> objects = (ArrayList<Object>) hashMapDishes.get(type);
                                if (type == "Appetizers") {
                                    for (int i = 0; i < objects.size(); i++) {
                                        HashMap<String, Object> hashMap = (HashMap<String, Object>) objects.get(i);
                                        Name = hashMap.get("Name").toString();
                                        Price = (double) hashMap.get("Price");
                                        //Log.d(TAG, Name + " " + Price);
                                        appetizerItemsArrayList.add(new FoodItems(Name, Price));
                                    }
                                    Log.d(TAG, "Appetizers Fetched" + appetizerItemsArrayList.toString());
                                    adapter = new RecyclerAdapter(appetizerItemsArrayList, context, "Food");
                                    recyclerView.setAdapter(adapter);
                                } else {
                                    for (int i = 0; i < objects.size(); i++) {
                                        HashMap<String, Object> hashMap = (HashMap<String, Object>) objects.get(i);
                                        Name = hashMap.get("Name").toString();
                                        Price = (double) hashMap.get("Price");
                                        //Log.d(TAG, Name + " " + Price);
                                        mainItemsArrayList.add(new FoodItems(Name, Price));
                                    }
                                    Log.d(TAG, "Mains Fetched");
                                    adapter = new RecyclerAdapter(mainItemsArrayList, context, "Food");
                                    recyclerView.setAdapter(adapter);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.i(TAG, "OnItemClicked " + i);
        ArrayList<Restaurant> tempArrayList = new ArrayList<>();
        for (Restaurant restaurant : restaurantArrayList) {
            if (restaurant.getTitle().equals(restaurantNameArrayList.get(i)))
                tempArrayList.add(restaurant);
        }
        adapter = new RecyclerAdapter(tempArrayList, context, "Restaurant");
        recyclerView.setAdapter(adapter);
        //recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String restaurantName = editable.toString();
        if (restaurantName == null || restaurantName.equals("")) {
            adapter = new RecyclerAdapter(restaurantArrayList, context, "Restaurant");
            recyclerView.setAdapter(adapter);
        } else {
            ArrayList<Restaurant> tempArrayList = new ArrayList<>();
            for (Restaurant restaurant : restaurantArrayList) {
                if (restaurant.getTitle().contains(restaurantName))
                    tempArrayList.add(restaurant);
            }
            adapter = new RecyclerAdapter(tempArrayList, context, "Restaurant");
            recyclerView.setAdapter(adapter);
        }
    }
}
