package database.entity;

import database.annotation.AutoIncrement;
import database.annotation.PrimaryKey;
import database.annotation.TableName;

@TableName("channels_membership")
public class ChannelsMembership {

    @PrimaryKey
    @AutoIncrement
    private int id;
    private String channelId;
    private String channelName;
    private String memberName;
    private String email;
    private long timestamp;

    public ChannelsMembership() {}

    public ChannelsMembership(String channelId, String channelName, String memberName, String email) {
        super();
        this.channelId = channelId;
        this.channelName = channelName;
        this.memberName = memberName;
        this.email = email;
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("channelId : %s channelName : %s memberName : %s email : %s", channelId, channelName, memberName, email);
    }

    public boolean equals(Object channelsMembership) {
        if (channelsMembership instanceof ChannelsMembership)
            return this.toString().equals(channelsMembership.toString());
        return false;
    }
}
