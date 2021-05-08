package com.armjld.rayashipping.Models;

import java.util.ArrayList;

public class PickUp {

    ArrayList<Order> orderList = new ArrayList<>();
    UserData user;
    String date;

    public  PickUp() { }

    public PickUp(ArrayList<Order> orderList, UserData user, String date) {
        this.orderList = orderList;
        this.user = user;
        this.date = date;
    }


    public ArrayList<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(ArrayList<Order> orderList) {
        this.orderList = orderList;
    }

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
