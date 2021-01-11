package com.armjld.rayashipping.models;

public class UserInFormation {

    private static String userName = "";
    private static String AccountType = "";
    private static String userDate = "";
    private static String userURL = "";
    private static String id = "";
    private static String uPhone = "";
    private static String isConfirm = "";
    private static String email = "";
    private static String pass = "";
    private static String sendGovNoti = "false";
    private static String currentdate = "none";
    private static String userCity = "";
    private static String userState = "";
    private static String walletmoney = "0";
    private static String sendCityNoti = "false";
    private static int rating = 0;
    private static String ordersType = "all";
    private static String trans = "Motor";
    private static String provider = "";


    private static String sup_code = "";
    private static String mySup = "";
    private static String supId = "";

    private static String packMoney = "";

    public UserInFormation() {
    }

    public static String getPackMoney() {
        return packMoney;
    }

    public static void setPackMoney(String packMoney) {
        UserInFormation.packMoney = packMoney;
    }

    public static String getUserCity() {
        return userCity;
    }

    public static void setUserCity(String userCity) {
        UserInFormation.userCity = userCity;
    }

    public static String getUserState() {
        return userState;
    }

    public static void setUserState(String userState) {
        UserInFormation.userState = userState;
    }

    public static String getWalletmoney() {
        return walletmoney;
    }

    public static void setWalletmoney(String walletmoney) {
        UserInFormation.walletmoney = walletmoney;
    }

    public static String getSendCityNoti() {
        return sendCityNoti;
    }

    public static void setSendCityNoti(String sendCityNoti) {
        UserInFormation.sendCityNoti = sendCityNoti;
    }

    public static String getSendGovNoti() {
        return sendGovNoti;
    }

    public static void setSendGovNoti(String sendGovNoti) {
        UserInFormation.sendGovNoti = sendGovNoti;
    }

    public static String getCurrentdate() {
        return currentdate;
    }

    public static void setCurrentdate(String currentdate) {
        UserInFormation.currentdate = currentdate;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        UserInFormation.userName = userName;
    }

    public static String getAccountType() {
        return AccountType;
    }

    public static void setAccountType(String accountType) {
        AccountType = accountType;
    }

    public static String getUserDate() {
        return userDate;
    }

    public static void setUserDate(String userDate) {
        UserInFormation.userDate = userDate;
    }

    public static String getUserURL() {
        return userURL;
    }

    public static void setUserURL(String userURL) {
        UserInFormation.userURL = userURL;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        UserInFormation.id = id;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        UserInFormation.email = email;
    }

    public static String getPass() {
        return pass;
    }

    public static void setPass(String pass) {
        UserInFormation.pass = pass;
    }

    public static int getRating() {
        return rating;
    }

    public static void setRating(int rating) {
        UserInFormation.rating = rating;
    }

    public static String getOrdersType() {
        return ordersType;
    }

    public static void setOrdersType(String ordersType) {
        UserInFormation.ordersType = ordersType;
    }

    public static String getTrans() {
        return trans;
    }

    public static void setTrans(String trans) {
        UserInFormation.trans = trans;
    }

    public static String getuPhone() {
        return uPhone;
    }

    public static void setuPhone(String uPhone) {
        UserInFormation.uPhone = uPhone;
    }

    public static String getIsConfirm() {
        return isConfirm;
    }

    public static void setIsConfirm(String isConfirm) {
        UserInFormation.isConfirm = isConfirm;
    }

    public static String getProvider() {
        return provider;
    }

    public static void setProvider(String provider) {
        UserInFormation.provider = provider;
    }

    public static String getSup_code() {
        return sup_code;
    }

    public static void setSup_code(String sup_code) {
        UserInFormation.sup_code = sup_code;
    }

    public static String getMySup() {
        return mySup;
    }

    public static void setMySup(String mySup) {
        UserInFormation.mySup = mySup;
    }

    public static String getSupId() {
        return supId;
    }

    public static void setSupId(String supId) {
        UserInFormation.supId = supId;
    }

    public static void clearUser() {
        setAccountType("");
        setCurrentdate("");
        setEmail("");
        setId("");
        setIsConfirm("");
        setPass("");
        setuPhone("");
        setRating(0);
        setUserDate("");
        setUserName("");
        setUserURL("");
        setSendGovNoti("false");
        setSendCityNoti("false");
        setTrans("Trans");
        setProvider("");
        setSup_code("");
        setMySup("");
        setSupId("");
    }
}
