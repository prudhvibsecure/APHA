package com.bsecure.apha.models;

import java.io.Serializable;

/**
 * Created by Admin on 2018-11-30.
 */
public class MessageObject implements Serializable {

    String message_id;
    String message;
    String message_date;
    String message_delivered;
    String message_viewed;
    String sender_member_id;
    String receiver_member_ids;
    String district_id;
    String forward_ids;
    String sender_member_number;
    String receiver_member_number;
    String sender_name;
    String reply_id;

    public String getUser_me() {
        return user_me;
    }

    public void setUser_me(String user_me) {
        this.user_me = user_me;
    }

    String forward_status;
    String user_me;

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage_date() {
        return message_date;
    }

    public void setMessage_date(String message_date) {
        this.message_date = message_date;
    }

    public String getMessage_delivered() {
        return message_delivered;
    }

    public void setMessage_delivered(String message_delivered) {
        this.message_delivered = message_delivered;
    }

    public String getMessage_viewed() {
        return message_viewed;
    }

    public void setMessage_viewed(String message_viewed) {
        this.message_viewed = message_viewed;
    }

    public String getSender_member_id() {
        return sender_member_id;
    }

    public void setSender_member_id(String sender_member_id) {
        this.sender_member_id = sender_member_id;
    }

    public String getReceiver_member_ids() {
        return receiver_member_ids;
    }

    public void setReceiver_member_ids(String receiver_member_ids) {
        this.receiver_member_ids = receiver_member_ids;
    }

    public String getDistrict_id() {
        return district_id;
    }

    public void setDistrict_id(String district_id) {
        this.district_id = district_id;
    }

    public String getForward_ids() {
        return forward_ids;
    }

    public void setForward_ids(String forward_ids) {
        this.forward_ids = forward_ids;
    }

    public String getSender_member_number() {
        return sender_member_number;
    }

    public void setSender_member_number(String sender_member_number) {
        this.sender_member_number = sender_member_number;
    }

    public String getReceiver_member_number() {
        return receiver_member_number;
    }

    public void setReceiver_member_number(String receiver_member_number) {
        this.receiver_member_number = receiver_member_number;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getReply_id() {
        return reply_id;
    }

    public void setReply_id(String reply_id) {
        this.reply_id = reply_id;
    }

    public String getForward_status() {
        return forward_status;
    }

    public void setForward_status(String forward_status) {
        this.forward_status = forward_status;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
