package per.test.p_emotion_springboot.model;

public class EmotionDataJson {
    private String date;
    private String mainEmotion;
    private float mainClassRatio;
    private String secondaryEmotion;
    private float secondaryClassRatio;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMainEmotion() {
        return mainEmotion;
    }

    public void setMainEmotion(String mainEmotion) {
        this.mainEmotion = mainEmotion;
    }

    public float getMainClassRatio() {
        return mainClassRatio;
    }

    public void setMainClassRatio(float mainClassRatio) {
        this.mainClassRatio = mainClassRatio;
    }

    public String getSecondaryEmotion() {
        return secondaryEmotion;
    }

    public void setSecondaryEmotion(String secondaryEmotion) {
        this.secondaryEmotion = secondaryEmotion;
    }

    public float getSecondaryClassRatio() {
        return secondaryClassRatio;
    }

    public void setSecondaryClassRatio(float secondaryClassRatio) {
        this.secondaryClassRatio = secondaryClassRatio;
    }
}