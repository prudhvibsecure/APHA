package com.bsecure.apha.models;

public class VipModel {

    String vips_id,vip_name,designation,phone_number,information,sector,state,district,image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVips_id() {
        return vips_id;
    }

    public void setVips_id(String vips_id) {
        this.vips_id = vips_id;
    }

    public String getVip_name() {
        return vip_name;
    }

    public void setVip_name(String vip_name) {
        this.vip_name = vip_name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
