import Utils.OAuthTokenHandler;
import Utils.RateLimiterSingleton;
import Utils.Util;
import database.entity.Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bot4 {

    public static void main(String[] args) throws InterruptedException {
        Logger logger = LoggerFactory.getLogger(Bot4.class.getName());
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

        ZoomWebHook zoomWebHook = zoom.webHook();

        String newMessageChannel = "own";
        ZoomEvent newMessageEvent = zoomWebHook.subscribe(Event.NEW_MESSAGE, newMessageChannel, m -> {
            if (m != null || !m.isEmpty()) {
                logger.info("Channel {} gets new message!", newMessageChannel);
                m.forEach(x -> logger.info(x.toString()));
            }
        });
        logger.info("Subscribe NEW_MESSAGE event");

        ZoomEvent updateMessageEvent = zoomWebHook.subscribe(Event.UPDATE_MESSAGE, newMessageChannel, m -> {
            if (m != null || !m.isEmpty()) {
                logger.info("Channel {} gets update message!", newMessageChannel);
                m.forEach(x -> logger.info(x.toString()));
            }
        });
        logger.info("Subscribe UPDATE_MESSAGE event");

        String newMemberChannel = "mine";
        ZoomEvent newMemberEvent = zoomWebHook.subscribe(Event.NEW_MEMBER, newMemberChannel, m -> {
            if (m != null || !m.isEmpty()) {
                logger.info("Channel {} get new members!", newMemberChannel);
                m.forEach(x -> logger.info(x.toString()));
            }
        });
        logger.info("Subscribe NEW_MEMBER event");

        Thread.sleep(60*1000);
        zoomWebHook.unsubscribe(newMessageEvent);
        zoomWebHook.unsubscribe(updateMessageEvent);
        zoomWebHook.unsubscribe(newMemberEvent);
        logger.info("Unsubscribe all events!");
        zoomWebHook.stop();
    }
}
