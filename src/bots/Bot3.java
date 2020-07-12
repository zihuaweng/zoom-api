import Utils.OAuthTokenHandler;
import Utils.RateLimiterSingleton;
import Utils.Util;
import database.entity.Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Bot3 {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Bot3.class.getName());

        // Retrieve access token
        Config config = new Config("OAuthConfig.properties");
        String clientId = config.getProperty("clientId");
        String clientSecret = config.getProperty("clientSecret");
        String ngrokServerUrl = config.getProperty("ngrokServerUrl");
        int port = Integer.parseInt(config.getProperty("port"));
        String redirectUri = Util.getRedirectUrl(ngrokServerUrl);

        Credential credential = OAuthTokenHandler.accessToken(clientId, clientSecret, redirectUri, port);

        Zoom zoom = new ZoomBuilder()
                .setOAuthAccessToken(credential.getOAuthToken())
                .setRateLimitHandler(new RateLimiterSingleton(1))  // Here you could set num of calls per second.
                .build();


//        // Testing send message by system input
//        String input;
//        Scanner scanner = new Scanner(System.in);
//        while (true) {
//            input = scanner.nextLine();
//            if (input.toLowerCase().equals("stop")) {
//                break;
//            }
//            // Send Message to specific channel
//            zoom.chat().toChannelName("test").sendMessage(input);
//        }

        // Testing send message
        for (int i = 0; i < 1; i++) {
            zoom.chat().toChannelName("test").sendMessage("test");
        }

        List<Message> messages;

        // Search for default history. Zoom uses GMT so it only return history according to GMT.
        messages = zoom.chat().toChannelName("test").history();
//        for (Message m: messages) {
//            logger.info("OAuthBot : {}", m.date_time);
//            logger.info("OAuthBot : {}", m.sender);
//            logger.info("OAuthBot : {}", m.message);
//        }
        logger.info("OAuthBot : number of messages : {}", messages.size());

        // Search history in specific days. Zoom uses GMT so it only return history according to GMT.
        messages = zoom.chat().toChannelName("test").history("2020-4-27", "2020-4-29");
//        for (Message m: messages) {
//            logger.info("OAuthBot : {}", m.date_time);
//            logger.info("OAuthBot : {}", m.sender);
//            logger.info("OAuthBot : {}", m.message);
//        }
        logger.info("OAuthBot : number of messages : {}", messages.size());

        // Search history with constrains. Zoom uses GMT so it only return history according to GMT.
        messages = zoom.chat().toChannelName("test").searchHistory("2020-4-26", "2020-4-28", x -> x.message.contains("test"));
//        for (Message m: messages) {
//            logger.info("OAuthBot : {}", m.date_time);
//            logger.info("OAuthBot : {}", m.sender);
//            logger.info("OAuthBot : {}", m.message);
//        }
        logger.info("OAuthBot : number of messages : {}", messages.size());
    }
}
