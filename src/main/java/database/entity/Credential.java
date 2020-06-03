package database.entity;

import database.annotation.PrimaryKey;
import database.annotation.TableName;

@TableName("credentials")
public class Credential {

    @PrimaryKey
    private String clientId;
    private String OAuthToken;
    private long expiresIn;
    private long timestamp;

    public Credential() {}

    public Credential(String clientId, String OAuthToken, long expiresIn) {
        super();
        this.clientId = clientId;
        this.OAuthToken = OAuthToken;
        this.expiresIn = expiresIn;
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getOAuthToken() {
        return OAuthToken;
    }

    public void setOAuthToken(String OAuthToken) {
        this.OAuthToken = OAuthToken;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("clientId : %s OAuthToken : %s expiresIn : %s", clientId, OAuthToken, expiresIn);
    }

    public boolean equals(Object credential) {
        if (credential instanceof Credential)
            return this.toString().equals(credential.toString());
        return false;
    }
}

