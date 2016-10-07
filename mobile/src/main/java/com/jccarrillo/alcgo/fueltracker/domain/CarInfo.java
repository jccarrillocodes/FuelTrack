package com.jccarrillo.alcgo.fueltracker.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Juan Carlos on 04/10/2016.
 */

public class CarInfo implements Serializable {

    private String model;
    private String company;
    private String owner;
    private int uid;
    private List<RefuelValue> refuelValues;

    public CarInfo() {
        this.refuelValues = new ArrayList<>();
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<RefuelValue> getRefuelValues() {
        return refuelValues;
    }

    public void setRefuelValues(List<RefuelValue> refuelValues) {
        this.refuelValues = refuelValues;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    private void sortValues(){
        if( refuelValues != null )
            Collections.sort(refuelValues, new Comparator<RefuelValue>() {
                @Override
                public int compare(RefuelValue lhs, RefuelValue rhs) {
                    if( lhs.getDate() != null && rhs.getDate() != null )
                        return rhs.getDate().compareTo( lhs.getDate());
                    if( lhs.getDate() != null )
                        return -1;
                    if( rhs.getDate() != null )
                        return 1;
                    return 0;
                }
            });
    }

    public void addValue(RefuelValue value) {
        this.refuelValues.add( value );
        sortValues();
    }

    public void replaceValue( RefuelValue value, int position ){
        this.refuelValues.remove( position );
        this.refuelValues.add( position, value );
        sortValues();
    }

    public void removeValue(int position) {
        this.refuelValues.remove(position);
    }
}
