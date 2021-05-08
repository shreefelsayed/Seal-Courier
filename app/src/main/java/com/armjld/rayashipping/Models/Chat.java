package com.armjld.rayashipping.Models;

public class Chat {
    private String senderid;
    private String msgid = "";
    private String newMsg = "false";
    private String reciverid;
    private String msg;
    private String timestamp;
    private String type = "text";
    private String url = "";

    public Chat(String senderid, String reciverid, String msg, String timestamp, String type, String url, String newMsg, String msgid) {
        this.senderid = senderid;
        this.reciverid = reciverid;
        this.msg = msg;
        this.timestamp = timestamp;
        this.newMsg = newMsg;
        this.msgid = msgid;
        this.type = type;
        this.url = url;
    }

    public Chat() {
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getNewMsg() {
        return newMsg;
    }

    public void setNewMsg(String newMsg) {
        this.newMsg = newMsg;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getReciverid() {
        return reciverid;
    }

    public void setReciverid(String reciverid) {
        this.reciverid = reciverid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
