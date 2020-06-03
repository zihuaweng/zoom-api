import Utils.OAuthTokenHandler;
import Utils.RateLimiterSingleton;
import Utils.Util;
import database.SQLDatabase;
import database.entity.Channels;
import database.entity.ChannelsMembership;
import database.entity.Credential;
import database.entity.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Bot51 {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Bot51.class.getName());

        // Retrieve access token
        Config config = new Config("OAuthConfig1.properties");
        String clientId = config.getProperty("clientId");
        String clientSecret = config.getProperty("clientSecret");
        String ngrokServerUrl = config.getProperty("ngrokServerUrl");
        int port = Integer.parseInt(config.getProperty("port"));
        String redirectUri = Util.getRedirectUrl(ngrokServerUrl);

        SQLDatabase sqlDatabase = new SQLDatabase(config.getProperty("database"));

        sqlDatabase.createTable(Credential.class);
        sqlDatabase.createTable(Channels.class);
        sqlDatabase.createTable(ChannelsMembership.class);
        sqlDatabase.createTable(Messages.class);

        Credential credential = OAuthTokenHandler.accessToken(clientId, clientSecret, redirectUri, port, sqlDatabase);

        Zoom zoom = new ZoomBuilder()
                .setOAuthAccessToken(credential.getOAuthToken())
                .setRateLimitHandler(new RateLimiterSingleton(1))  // Here you could set num of calls per second.
                .build();

        List<Channel> channels = zoom.chat().listChannels(clientId, sqlDatabase);

        List<Message> messages = zoom.chat().toChannelName("mine").history("mine", sqlDatabase);

        List<Member> members = zoom.chat().listMembersByName("mine", sqlDatabase);
    }
}
