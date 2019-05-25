package com.bsecure.apha.models;

public class MemberModel {

    String id, name, member_number,last_msg,time,read;

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public String getLast_msg() {
        return last_msg;
    }

    public void setLast_msg(String last_msg) {
        this.last_msg = last_msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMember_number() {
        return member_number;
    }

    public void setMember_number(String member_number) {
        this.member_number = member_number;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
