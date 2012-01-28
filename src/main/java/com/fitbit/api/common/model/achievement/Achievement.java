package com.fitbit.api.common.model.achievement;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by IntelliJ IDEA.
 * User: Kiryl
 * Date: 10/18/11
 * Time: 12:56 AM
 */
public class Achievement {

    private long caloriesOut;
    private long floors;
    private double distance;
    private long steps;
    private long activeScore;

    public Achievement(long caloriesOut, long floors, double distance, long steps, long activeScore) {
        this.caloriesOut = caloriesOut;
        this.floors = floors;
        this.distance = distance;
        this.steps = steps;
        this.activeScore = activeScore;
    }

    public Achievement(JSONObject jsonObject) throws JSONException {
        this.caloriesOut = jsonObject.getLong("caloriesOut");
        this.floors = jsonObject.optLong("floors");
        this.distance = jsonObject.getDouble("distance");
        this.steps = jsonObject.getLong("steps");
        this.activeScore = jsonObject.getLong("activeScore");
    }

    public long getCaloriesOut() {
        return caloriesOut;
    }

    public long getFloors() {
        return floors;
    }

    public double getDistance() {
        return distance;
    }

    public long getSteps() {
        return steps;
    }

    public long getActiveScore() {
        return activeScore;
    }
}
