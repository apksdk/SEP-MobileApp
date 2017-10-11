package com.riversidecorps.rebuy.models;

/**
 * Created by yijun on 2017/10/2.
 */

public class Message {
    private String content;
    private String sender;
    private String datetime;
    private String title;
    private String message_id;
    private String sender_id;

    public Message(String content, String sender, String datetime, String title, String message_id, String sender_id) {
        this.content = content;
        this.sender = sender;
        this.datetime = datetime;
        this.title = title;
        this.message_id = message_id;
        this.sender_id = sender_id;
    }

    public Message() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }
}
