package com.armjld.rayashipping;

public class myCaptReqs {
    String captinId;

    public myCaptReqs(String captinId, String orderId) {
        this.captinId = captinId;
        this.orderId = orderId;
    }

    String orderId;

    public String getCaptinId() {
        return captinId;
    }

    public void setCaptinId(String captinId) {
        this.captinId = captinId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

}
