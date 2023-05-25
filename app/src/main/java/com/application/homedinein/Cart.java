package com.application.homedinein;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Cart implements Parcelable {
    public Restaurant restaurant;
    public ArrayList<FoodItems> appetizer;
    public ArrayList<FoodItems> mains;
    public double totalPrice;
    public double reservationPrice;

    public void setModeReservation(boolean modeReservation) {
        isModeReservation = modeReservation;
        calulate();
    }

    public boolean isModeReservation=false;

    public String review,orderNo;
    public String name,contact,time,noOfseatings,address1, address2, postalCode,orderDetails;

    public Cart(Restaurant restaurant, ArrayList<FoodItems> appetizer, ArrayList<FoodItems> mains,boolean modeReservation) {
        this.restaurant = restaurant;
        this.appetizer = appetizer;
        this.mains = mains;
        this.totalPrice=0d;
        this.reservationPrice=0d;
        this.isModeReservation=modeReservation;
        calulate();
    }

    public Cart(Restaurant restaurant,boolean modeReservation,String name, String contact,String orderDetails,String review,String orderNo)
    {
        this.restaurant=restaurant;
        this.isModeReservation=modeReservation;
        this.name=name;
        this.contact=contact;
        this.orderDetails=orderDetails;
        this.review=review;
        this.orderNo=orderNo;
    }

    private void calulate() {
        Double total=0.0d;
        for(FoodItems item:appetizer)
            total+=item.Quantity*item.Price;
        for(FoodItems item:mains)
            total+=item.Quantity*item.Price;
        this.totalPrice=Math.round(total);
        if(this.isModeReservation)
            reservationPrice=Math.round(this.totalPrice*0.5);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeTypedList(this.appetizer);
        parcel.writeTypedList(this.mains);
        parcel.writeParcelable(this.restaurant,i);
        parcel.writeDouble(this.totalPrice);
        parcel.writeDouble(this.reservationPrice);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parcel.writeBoolean(this.isModeReservation);
        }
    }

    private Cart(Parcel in){
        ArrayList<FoodItems> itemsAppetizers= new ArrayList<>();
        ArrayList<FoodItems> itemsMains= new ArrayList<>();
        in.readTypedList(itemsAppetizers,FoodItems.CREATOR);
        in.readTypedList(itemsMains,FoodItems.CREATOR);
        this.appetizer=itemsAppetizers;
        this.mains=itemsMains;
        this.restaurant = in.readParcelable(Restaurant.class.getClassLoader());
        this.totalPrice=in.readDouble();
        this.reservationPrice=in.readDouble();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.isModeReservation=in.readBoolean();
        }
        //calulate();
    }

    public static final Parcelable.Creator<Cart> CREATOR = new Parcelable.Creator<Cart>() {
        @Override
        public Cart createFromParcel(Parcel source) {
            return new Cart(source);
        }

        @Override
        public Cart[] newArray(int size) {
            return new Cart[size];
        }
    };
}
