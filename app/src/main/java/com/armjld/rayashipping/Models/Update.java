package com.armjld.rayashipping.Models;

public class Update {

    String msg = "";
    String category = "";
    String orderId = "";
    String date = "";
    String by = "";

    public Update() {}

    public Update(String msg, String category, String date, String orderId, String by) {
        this.msg = msg;
        this.category = category;
        this.date = date;
        this.orderId = orderId;
        this.by = by;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category; }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }
}
