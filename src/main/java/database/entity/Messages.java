package database.entity;

import database.annotation.PrimaryKey;
import database.annotation.TableName;

import java.util.Date;

@TableName("messages")
public class Messages {

    private String channelId;
    @PrimaryKey
    private String messageId;
    private String sender;
    private String message;
    private Date date;
    private long timestamp;

    public Messages() {}

    public Messages(String channelId, String messageId, String sender, String message, Date date) {
        super();
        this.channelId = channelId;
        this.messageId = messageId;
        this.sender = sender;
        this.message = message;
        this.date = date;
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("channelId : %s messageId : %s sender : %s message : %s", channelId, messageId, sender, message);
    }

    public boolean equals(Object messages) {
        if (messages instanceof Messages)
            return this.toString().equals(messages.toString());
        return false;
    }
}
