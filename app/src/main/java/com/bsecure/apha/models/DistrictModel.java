package com.bsecure.apha.models;

public class DistrictModel {

    String dist_id, dist_name;

    public String getDist_id() {
        return dist_id;
    }

    public void setDist_id(String dist_id) {
        this.dist_id = dist_id;
    }

    public String getDist_name() {
        return dist_name;
    }

    public void setDist_name(String dist_name) {
        this.dist_name = dist_name;
    }

    @Override
    public String toString() {
        return dist_name;
    }
}
