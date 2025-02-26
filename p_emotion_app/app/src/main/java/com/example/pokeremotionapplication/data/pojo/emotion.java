package com.example.pokeremotionapplication.data.pojo;

public class emotion {
    private String main_emotion;
    private double main_class_ratio;
    private String secondary_emotion;
    private double secondary_class_ratio;


    public emotion(String main_emotion, double main_class_ratio, String secondary_emotion, double secondary_class_ratio) {
        this.main_emotion = main_emotion;
        this.main_class_ratio = main_class_ratio;
        this.secondary_emotion = secondary_emotion;
        this.secondary_class_ratio = secondary_class_ratio;
    }

    public String getMain_emotion() {
        return main_emotion;
    }

    public void setMain_emotion(String main_emotion) {
        this.main_emotion = main_emotion;
    }

    public double getMain_class_ratio() {
        return main_class_ratio;
    }

    public void setMain_class_ratio(double main_class_ratio) {
        this.main_class_ratio = main_class_ratio;
    }

    public String getSecondary_emotion() {
        return secondary_emotion;
    }

    public void setSecondary_emotion(String secondary_emotion) {
        this.secondary_emotion = secondary_emotion;
    }

    public double getSecondary_class_ratio() {
        return secondary_class_ratio;
    }

    public void setSecondary_class_ratio(double secondary_class_ratio) {
        this.secondary_class_ratio = secondary_class_ratio;
    }
}
