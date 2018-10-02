package com.example.bdgasbookingsystem.Class;

import android.support.annotation.NonNull;

import java.util.Comparator;

public class User implements Comparable<User>{

    private String Name;
    private String PhoneNumber;
    private String Location;
    private String ShopName;
    private String UserType;
    private String Address;
    private String Distance;
    private int DistanceValue;


    public User(String Name, String PhoneNumber, String Location, String ShopName, String UserType, String Address, String Distance, int DistanceValue){

        this.Name=Name;
        this.PhoneNumber=PhoneNumber;
        this.Location=Location;
        this.ShopName=ShopName;

        this.UserType=UserType;
        this.Address=Address;
        this.Distance=Distance;
        this.DistanceValue=DistanceValue;
    }

    public User(String Name, String PhoneNumber, String Location, String ShopName, String UserType, String Address){

        this.Name=Name;
        this.PhoneNumber=PhoneNumber;
        this.Location=Location;
        this.ShopName=ShopName;
        this.UserType=UserType;
        this.Address=Address;
    }

    public String getName() {
        return Name;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public String getLocation() {
        return Location;
    }

    public String getShopName() {
        return ShopName;
    }

    public String getUserType() {
        return UserType;
    }

    public String getAddress() {
        return Address;
    }

    public String getDistance() {
        return Distance;
    }

    public int getDistanceValue() {
        return DistanceValue;
    }

    @Override
    public int compareTo(@NonNull User o) {
        return 0;
    }

    public static final Comparator<User> DESCENDING_COMPARATOR = new Comparator<User>() {
        // Overriding the compare method to sort the age
        public int compare(User u1, User u2) {
            return u1.getDistanceValue() - u2.getDistanceValue();
        }
    };
}
