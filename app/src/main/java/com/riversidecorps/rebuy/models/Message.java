package com.riversidecorps.rebuy.models;

/**
 * Created by yijun on 2017/10/2.
 */

public class Message {
    private String content;
    private String buyer;
    private String datetime;
    private String title;

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
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

    public Message(String content, String buyer, String datetime, String title) {
        this.content = content;
        this.buyer = buyer;
        this.datetime = datetime;
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
