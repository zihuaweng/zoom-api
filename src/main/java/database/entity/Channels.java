package database.entity;

import database.annotation.AutoIncrement;
import database.annotation.PrimaryKey;
import database.annotation.TableName;

@TableName("channels")
public class Channels {

    @PrimaryKey
    @AutoIncrement
    private int id;
    private String clientId;
    private String channelId;
    private String channelName;
    private long timestamp;

    public Channels() {
    }

    public Channels(String clientId, String channelId, String channelName) {
        super();
        this.clientId = clientId;
        this.channelId = channelId;
        this.channelName = channelName;
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("clientId : %s channelId : %s channelName : %s", clientId, channelId, channelName);
    }

    public boolean equals(Object channel) {
        if (channel instanceof Channels)
            return this.toString().equals(channel.toString());
        return false;
    }
}
