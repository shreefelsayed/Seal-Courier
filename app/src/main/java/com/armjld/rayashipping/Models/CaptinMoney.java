package com.armjld.rayashipping.Models;

public class CaptinMoney {

    String orderid; // Order id
    String transType; // delivered / recived / denied / deniedback / ourmoney / bouns / shippingFees
    String date; // Transaction Date
    String trackId; // Order track ID
    String isPaid = "fasle"; // is Transaction Paid
    String money;

    public CaptinMoney() {
    }

    public CaptinMoney(String orderid, String transType, String date, String isPaid, String trackId, String money) {
        this.orderid = orderid;
        this.transType = transType;
        this.date = date;
        this.isPaid = isPaid;
        this.trackId = trackId;
        this.money = money;
    }


    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(String isPaid) {
        this.isPaid = isPaid;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }
}
