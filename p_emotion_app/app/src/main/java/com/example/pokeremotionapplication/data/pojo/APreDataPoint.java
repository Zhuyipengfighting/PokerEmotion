package com.example.pokeremotionapplication.data.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class APreDataPoint {
    private Map<String, List<Double>> channels;

    public APreDataPoint(Map<String, List<Double>> channels) {
        this.channels = channels;
    }

    public Map<String, List<Double>> getChannels() {
        return channels;
    }

    public void setChannels(Map<String, List<Double>> channels) {
        this.channels = channels;
    }
}