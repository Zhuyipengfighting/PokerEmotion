package com.example.pokeremotionapplication.data.pojo;

public class Message {
    private String userName = "测试员";
    private String userNumber = "123456789";
    private String message;
    private String timestamp;
    private boolean isSent; // 是否为发送者
    private boolean isLong; // 是否为长文本

    public Message(String userName, String userNumber, String message, String timestamp, boolean isSent, boolean isLong) {
        this.userName = userName;
        this.userNumber = userNumber;
        this.message = message;
        this.timestamp = timestamp;
        this.isSent = isSent;
        this.isLong = isLong;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public boolean isLong() {
        return isLong;
    }

    public void setLong(boolean aLong) {
        isLong = aLong;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }
}

