package com.armjld.rayashipping.Models;

public class PickUpInfo {
    String user = "";
    String date =  "";
    String statue = "";
    String by = "";
    String uAccepted = "";

    public PickUpInfo() {}

    public PickUpInfo(String user, String date, String statue, String by) {
        this.user = user;
        this.date = date;
        this.statue = statue;
        this.by = by;
    }

    public String getuAccepted() {
        return uAccepted;
    }

    public void setuAccepted(String uAccepted) {
        this.uAccepted = uAccepted;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatue() {
        return statue;
    }

    public void setStatue(String statue) {
        this.statue = statue;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }
}
