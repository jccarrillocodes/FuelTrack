package com.jccarrillo.alcgo.fueltracker.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Juan Carlos on 04/10/2016.
 */

public class RefuelValue implements Serializable {

    private Date date;
    private Double quantity;
    private Double cost;
    private Double distance;
    private DrivingType drivingType;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public DrivingType getDrivingType() {
        return drivingType;
    }

    public void setDrivingType(DrivingType drivingType) {
        this.drivingType = drivingType;
    }
}
