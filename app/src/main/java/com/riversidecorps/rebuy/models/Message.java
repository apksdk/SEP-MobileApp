package com.riversidecorps.rebuy.models;

/**
 * Created by yijun on 2017/10/2.
 */

public class Message {
    private String content;
    private String sender;
    private String dateTime;
    private String title;
    private String messageId;
    private String senderId;

    public Message(String content, String sender, String dateTime, String title, String messageId, String senderId) {
        this.content = content;
        this.sender = sender;
        this.dateTime = dateTime;
        this.title = title;
        this.messageId = messageId;
        this.senderId = senderId;
    }

    public Message() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
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

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
