package com.application.homedinein;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class FoodItems implements Parcelable {
    String Name;
    Double Price;
    int Quantity;
    boolean cartItem=false;

    public FoodItems(String name, Double price) {
        Name = name;
        Price = price;
        Quantity=0;
        cartItem=false;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Double getPrice() {
        return Price;
    }

    public void setPrice(Double price) {
        Price = price;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public boolean isCartItem() {
        return cartItem;
    }

    public void setCartItem(boolean cartItem) {
        this.cartItem = cartItem;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(this.Name);
        parcel.writeDouble(this.Price);
        parcel.writeInt(this.Quantity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parcel.writeBoolean(this.cartItem);
        }
    }

    private FoodItems(Parcel in){
        Name = in.readString();
        Price = in.readDouble();
        Quantity=in.readInt();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            cartItem=in.readBoolean();
        }
    }

    public static final Parcelable.Creator<FoodItems> CREATOR = new Parcelable.Creator<FoodItems>() {
        @Override
        public FoodItems createFromParcel(Parcel source) {
            return new FoodItems(source);
        }

        @Override
        public FoodItems[] newArray(int size) {
            return new FoodItems[size];
        }
    };
}
