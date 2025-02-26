package com.example.pokeremotionapplication.data.pojo;

public class DataPoint {
    private int index;
    private double channel1;
    private double channel2;
    private double channel3;
    private double channel4;
    private double channel5;
    private double channel6;
    private double channel7;
    private double channel8;
    private double channel9;
    private double channel10;
    private double channel11;
    private double channel12;
    private double channel13;
    private double channel14;
    private double channel15;
    private double channel16;

    public DataPoint(int index, double channel1, double channel2, double channel3, double channel4, double channel5,
                     double channel6, double channel7, double channel8, double channel9, double channel10,
                     double channel11, double channel12, double channel13, double channel14, double channel15,
                     double channel16) {
        this.index = index;
        this.channel1 = channel1;
        this.channel2 = channel2;
        this.channel3 = channel3;
        this.channel4 = channel4;
        this.channel5 = channel5;
        this.channel6 = channel6;
        this.channel7 = channel7;
        this.channel8 = channel8;
        this.channel9 = channel9;
        this.channel10 = channel10;
        this.channel11 = channel11;
        this.channel12 = channel12;
        this.channel13 = channel13;
        this.channel14 = channel14;
        this.channel15 = channel15;
        this.channel16 = channel16;
    }

    // 将对象转换为JSON格式的字符串
    @Override
    public String toString() {
        return "{" +
                "\"Channel_1\":" + channel1 +
                ", \"Channel_2\":" + channel2 +
                ", \"Channel_3\":" + channel3 +
                ", \"Channel_4\":" + channel4 +
                ", \"Channel_5\":" + channel5 +
                ", \"Channel_6\":" + channel6 +
                ", \"Channel_7\":" + channel7 +
                ", \"Channel_8\":" + channel8 +
                ", \"Channel_9\":" + channel9 +
                ", \"Channel_10\":" + channel10 +
                ", \"Channel_11\":" + channel11 +
                ", \"Channel_12\":" + channel12 +
                ", \"Channel_13\":" + channel13 +
                ", \"Channel_14\":" + channel14 +
                ", \"Channel_15\":" + channel15 +
                ", \"Channel_16\":" + channel16 +
                "}";
    }

    public int getIndex() {
        return index;
    }

    public double getChannel2() {
        return channel2;
    }

    public void setChannel2(double channel2) {
        this.channel2 = channel2;
    }

    public double getChannel3() {
        return channel3;
    }

    public void setChannel3(double channel3) {
        this.channel3 = channel3;
    }

    public double getChannel4() {
        return channel4;
    }

    public void setChannel4(double channel4) {
        this.channel4 = channel4;
    }

    public double getChannel5() {
        return channel5;
    }

    public void setChannel5(double channel5) {
        this.channel5 = channel5;
    }

    public double getChannel6() {
        return channel6;
    }

    public void setChannel6(double channel6) {
        this.channel6 = channel6;
    }

    public double getChannel7() {
        return channel7;
    }

    public void setChannel7(double channel7) {
        this.channel7 = channel7;
    }

    public double getChannel8() {
        return channel8;
    }

    public void setChannel8(double channel8) {
        this.channel8 = channel8;
    }

    public double getChannel9() {
        return channel9;
    }

    public void setChannel9(double channel9) {
        this.channel9 = channel9;
    }

    public double getChannel10() {
        return channel10;
    }

    public void setChannel10(double channel10) {
        this.channel10 = channel10;
    }

    public double getChannel11() {
        return channel11;
    }

    public void setChannel11(double channel11) {
        this.channel11 = channel11;
    }

    public double getChannel12() {
        return channel12;
    }

    public void setChannel12(double channel12) {
        this.channel12 = channel12;
    }

    public double getChannel13() {
        return channel13;
    }

    public void setChannel13(double channel13) {
        this.channel13 = channel13;
    }

    public double getChannel14() {
        return channel14;
    }

    public void setChannel14(double channel14) {
        this.channel14 = channel14;
    }

    public double getChannel15() {
        return channel15;
    }

    public void setChannel15(double channel15) {
        this.channel15 = channel15;
    }

    public double getChannel16() {
        return channel16;
    }

    public void setChannel16(double channel16) {
        this.channel16 = channel16;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getChannel1() {
        return channel1;
    }

    public void setChannel1(double channel1) {
        this.channel1 = channel1;
    }

}