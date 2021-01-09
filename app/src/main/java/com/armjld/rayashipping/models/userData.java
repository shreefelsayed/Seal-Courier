package com.armjld.rayashipping.models;

public class userData {

    private String name = "";
    private String phone = "";
    private String email = "";
    private String date = "";
    private String id = "";
    private String accountType = "RayahCaptin";
    private String ppURL = "";
    private String ssnURL = "";
    private String mpass = "";
    private String canceled = "";
    private String ssnNum = "";
    private String device = "";
    private String app_version = "";
    private String unique_id = "";
    private String active = "true";
    private String device_token = "";
    private String userState = "";
    private String userCity = "";
    private String ordersType = "all";
    private String currentDate = "none";
    private String sendOrderNoti = "true";
    private String sendOrderNotiCity = "false";
    private int walletmoney = 0;
    private String isConfirmed = "true";
    private String provider = "Esh7nly";
    private String mySuper = "";
    private String mySuperId = "";
    private String supervisor_code = "";
    private String packMoney = "0";

    private String trackId = "";


    public userData(String name, String phone, String email, String date, String id, String accountType, String ppURL, String ssnURL, String mpass, String canceled, String ssnNum, String device, String app_version, String unique_id, String active, String device_token, String userState, String userCity, String ordersType, String currentDate, String sendOrderNoti, String sendOrderNotiCity, int walletmoney, String isConfirmed, String provider) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.date = date;
        this.id = id;
        this.accountType = accountType;
        this.ppURL = ppURL;
        this.ssnURL = ssnURL;
        this.mpass = mpass;
        this.canceled = canceled;
        this.ssnNum = ssnNum;
        this.device = device;
        this.app_version = app_version;
        this.unique_id = unique_id;
        this.active = active;
        this.device_token = device_token;
        this.userState = userState;
        this.userCity = userCity;
        this.ordersType = ordersType;
        this.currentDate = currentDate;
        this.sendOrderNoti = sendOrderNoti;
        this.sendOrderNotiCity = sendOrderNotiCity;
        this.walletmoney = walletmoney;
        this.isConfirmed = isConfirmed;
        this.provider = provider;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getPpURL() {
        return ppURL;
    }

    public void setPpURL(String ppURL) {
        this.ppURL = ppURL;
    }

    public String getSsnURL() {
        return ssnURL;
    }

    public void setSsnURL(String ssnURL) {
        this.ssnURL = ssnURL;
    }

    public String getMpass() {
        return mpass;
    }

    public void setMpass(String mpass) {
        this.mpass = mpass;
    }

    public String getCanceled() {
        return canceled;
    }

    public void setCanceled(String canceled) {
        this.canceled = canceled;
    }

    public String getSsnNum() {
        return ssnNum;
    }

    public void setSsnNum(String ssnNum) {
        this.ssnNum = ssnNum;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getUserState() {
        return userState;
    }

    public void setUserState(String userState) {
        this.userState = userState;
    }

    public String getUserCity() {
        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }

    public String getOrdersType() {
        return ordersType;
    }

    public void setOrdersType(String ordersType) {
        this.ordersType = ordersType;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getSendOrderNoti() {
        return sendOrderNoti;
    }

    public void setSendOrderNoti(String sendOrderNoti) {
        this.sendOrderNoti = sendOrderNoti;
    }

    public String getSendOrderNotiCity() {
        return sendOrderNotiCity;
    }

    public void setSendOrderNotiCity(String sendOrderNotiCity) {
        this.sendOrderNotiCity = sendOrderNotiCity;
    }

    public int getWalletmoney() {
        return walletmoney;
    }

    public void setWalletmoney(int walletmoney) {
        this.walletmoney = walletmoney;
    }

    public String getIsConfirmed() {
        return isConfirmed;
    }

    public void setIsConfirmed(String isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    public userData(){ }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getMySuper() {
        return mySuper;
    }

    public void setMySuper(String mySuper) {
        this.mySuper = mySuper;
    }

    public String getMySuperId() {
        return mySuperId;
    }

    public void setMySuperId(String mySuperId) {
        this.mySuperId = mySuperId;
    }

    public String getSupervisor_code() {
        return supervisor_code;
    }

    public void setSupervisor_code(String supervisor_code) {
        this.supervisor_code = supervisor_code;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getPackMoney() {
        return packMoney;
    }

    public void setPackMoney(String packMoney) {
        this.packMoney = packMoney;
    }
}
