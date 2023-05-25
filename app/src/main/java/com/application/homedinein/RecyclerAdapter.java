package com.application.homedinein;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<Restaurant> restaurantArrayList;
    private ArrayList<FoodItems> foodArrayList;

    private ArrayList<Cart> cartArrayList;
    private Context context;
    String type;
    FirebaseFirestore db;

    public RecyclerAdapter(ArrayList<Restaurant> restaurantArrayList, Context context, String type, Restaurant... restaurant) {
        this.restaurantArrayList = restaurantArrayList;
        this.context = context;
        this.type = type;
    }

    public RecyclerAdapter(ArrayList<FoodItems> foodArrayList, Context context, String type, FoodItems... foodItems) {
        this.foodArrayList = foodArrayList;
        this.context = context;
        this.type = type;
    }

    public RecyclerAdapter(ArrayList<Cart> cartArrayList, Context context, String type) {
        this.cartArrayList = cartArrayList;
        this.context = context;
        this.type = type;
        this.db=FirebaseFirestore.getInstance();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (type.equalsIgnoreCase("Restaurant")) {
            View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
            ViewHolder viewHolder = new ViewHolder(root);
            return viewHolder;
        } else if(type.equalsIgnoreCase("Food")) {
            View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_food_items, parent, false);
            ViewHolder viewHolder = new ViewHolder(root);
            return viewHolder;
        }
        else {
            View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_order, parent, false);
            ViewHolder viewHolder = new ViewHolder(root);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (type.equalsIgnoreCase("Restaurant")) {
            Restaurant restaurant = restaurantArrayList.get(position);

            holder.restaurant_title.setText(restaurant.getTitle());
            holder.restaurant_rating.setText(String.valueOf(restaurant.getRating()));
            Picasso.get().load(restaurant.getPoster(0)).into(holder.image);
            if (restaurantArrayList.get(position).isDisabled()) {
                holder.card_view.setEnabled(false);
                holder.card_view.setCardBackgroundColor(Color.GRAY);
            }
        } else if(type.equalsIgnoreCase("Food")) {
            FoodItems foodItem = foodArrayList.get(position);
            holder.food_Name.setText(foodItem.getName());
            holder.food_Price.setText("$" + String.valueOf(foodItem.getPrice()));
            holder.food_quantity.setText(foodItem.getQuantity() + "");
            holder.linearLayout.setVisibility(foodItem.isCartItem() ? View.VISIBLE : View.GONE);
        } else if (type.equalsIgnoreCase("Orders")) {
            Cart cart=cartArrayList.get(position);
            holder.tvOrderRestName.setText(cart.restaurant.getTitle());
            holder.tvOrderNo.setText(cart.orderNo);
            holder.tvOrderName.setText(cart.name);
            holder.tvOrderContactNo.setText(cart.contact);
            holder.etOrderDetails.setText(cart.orderDetails);
            holder.etOrderReview.setText(cart.review);
            if(cart.review==null) {
                holder.btnPostReview.setVisibility(View.VISIBLE);
                holder.etOrderReview.setEnabled(true);
            }
            else if(cart.review=="" || cart.review.isEmpty()) {
                holder.btnPostReview.setVisibility(View.VISIBLE);
                holder.etOrderReview.setEnabled(true);
            }
            else{
                holder.btnPostReview.setVisibility(View.GONE);
                holder.etOrderReview.setEnabled(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (type.equalsIgnoreCase("Restaurant"))
            return restaurantArrayList.size();
        else if(type.equalsIgnoreCase("Food"))
            return foodArrayList.size();
        else
            return cartArrayList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;
        TextView restaurant_title, restaurant_rating, food_Name, food_Price, food_quantity;
        LinearLayout linearLayout;
        ImageButton add, subtract, remove;

        CardView card_view;

        TextView tvOrderRestName,tvOrderNo,tvOrderName,tvOrderContactNo;
        EditText etOrderDetails,etOrderReview;
        Button btnPostReview;

        public ViewHolder(View itemView) {
            super(itemView);
            if (type.equalsIgnoreCase("Restaurant")) {
                image = itemView.findViewById(R.id.imageView);
                restaurant_title = itemView.findViewById(R.id.restaurant_title);
                restaurant_rating = itemView.findViewById(R.id.restaurant_rating);
                card_view = itemView.findViewById(R.id.card_view);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, RestaurantDetails.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("restaurant", restaurantArrayList.get(getAdapterPosition()));
                        bundle.putStringArrayList("images", restaurantArrayList.get(getAdapterPosition()).getImage());
                        bundle.putInt("id", getAdapterPosition() + 1);
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    }
                });
            } else if(type.equalsIgnoreCase("Food")) {
                food_Name = itemView.findViewById(R.id.food_title);
                food_Price = itemView.findViewById(R.id.food_price);
                linearLayout = itemView.findViewById(R.id.layoutQuantity);
                add = itemView.findViewById(R.id.cartAdd);
                subtract = itemView.findViewById(R.id.cartSubtract);
                remove = itemView.findViewById(R.id.cartRemove);
                food_quantity = itemView.findViewById(R.id.food_quantity);
                add.setOnClickListener(this);
                subtract.setOnClickListener(this);
                remove.setOnClickListener(this);
                itemView.setOnClickListener(this);
            }
            else{
                tvOrderRestName = itemView.findViewById(R.id.tvOrderRestName);
                tvOrderNo = itemView.findViewById(R.id.tvOrderNo);
                tvOrderName = itemView.findViewById(R.id.tvOrderName);
                tvOrderContactNo = itemView.findViewById(R.id.tvOrderContactNo);
                etOrderDetails = itemView.findViewById(R.id.etOrderDetails);
                etOrderReview = itemView.findViewById(R.id.etOrderReview);
                btnPostReview = itemView.findViewById(R.id.btnPostReview);
                btnPostReview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
                        if (etOrderReview.getText().toString().isEmpty()) {
                            Toast.makeText(context, "Enter username", Toast.LENGTH_SHORT).show();
                            etOrderReview.setError("Enter review");
                            etOrderReview.setBackgroundResource(R.drawable.custom_error_edittext);
                            etOrderReview.startAnimation(shake);
                            Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                            vibe.vibrate(50);
                            return;
                        }
                        DocumentReference docRef = db.collection("Orders").document(cartArrayList.get(getAdapterPosition()).orderNo);
                        docRef
                                .update("review", etOrderReview.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                        Toast.makeText(context, "Review Posted!", Toast.LENGTH_SHORT).show();
                                        cartArrayList.get(getAdapterPosition()).review=etOrderReview.getText().toString();
                                        notifyDataSetChanged();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document", e);
                                    }
                                });
                    }
                });
            }
        }

        @Override
        public void onClick(View view) {
            Log.i("OnClick listener", view.getId() + "");
            int quantity = foodArrayList.get(getAdapterPosition()).Quantity;
            Animation animationSlideDown = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.slide_down);
            Animation animationSlideUp = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.slide_up);
            if (linearLayout.getVisibility() == View.GONE) {
                linearLayout.setVisibility(View.VISIBLE);
                foodArrayList.get(getAdapterPosition()).setCartItem(true);
                linearLayout.startAnimation(animationSlideDown);
            } else if (view.getId() == R.id.cartAdd) {
                foodArrayList.get(getAdapterPosition()).setQuantity(quantity < 3 ? ++quantity : 3);
                notifyDataSetChanged();
            } else if (view.getId() == R.id.cartSubtract) {
                foodArrayList.get(getAdapterPosition()).setQuantity(quantity > 0 ? --quantity : 0);
                if (foodArrayList.get(getAdapterPosition()).Quantity == 0) {
                    foodArrayList.get(getAdapterPosition()).setCartItem(false);
                    linearLayout.setVisibility(View.GONE);
                    linearLayout.startAnimation(animationSlideUp);
                }
                notifyDataSetChanged();
            } else if (view.getId() == R.id.cartRemove) {
                foodArrayList.get(getAdapterPosition()).setQuantity(0);
                foodArrayList.get(getAdapterPosition()).setCartItem(false);
                linearLayout.setVisibility(View.GONE);
                linearLayout.startAnimation(animationSlideUp);
                notifyDataSetChanged();
            }
        }
    }
}

