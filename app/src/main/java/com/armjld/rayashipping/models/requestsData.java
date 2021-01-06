package com.armjld.rayashipping.models;

public class requestsData {
    String id;
    String date;
    String statue;
    String notes = "";
    String owner = "";
    String noti = "";
    String orderId = "";
    String supervisor = "";

    public requestsData(){ }

    public requestsData(String id, String date, String statue, String notes, String owner, String noti, String orderId, String supervisor) {
        this.id = id;
        this.date = date;
        this.statue = statue;
        this.notes = notes;
        this.owner = owner;
        this.noti = noti;
        this.orderId = orderId;
        this.supervisor = supervisor;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getNoti() { return noti; }

    public void setNoti(String noti) {
        this.noti = noti;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getStatue() {
        return statue;
    }

    public void setStatue(String statue) {
        this.statue = statue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
