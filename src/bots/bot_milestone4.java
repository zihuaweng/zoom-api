import Utils.OAuthTokenHandler;
import Utils.RateLimiterSingleton;
import Utils.Util;

public class bot_milestone4 {

    public static void main(String[] args) throws InterruptedException {

        // Retrieve access token
        Config config = new Config("OAuthConfig.properties");
        String clientId = config.getProperty("clientId");
        String clientSecret = config.getProperty("clientSecret");
        String ngrokServerUrl = config.getProperty("ngrokServerUrl");
        int port = Integer.parseInt(config.getProperty("port"));
        String redirectUri = Util.getRedirectUrl(ngrokServerUrl);

        String accessToken = OAuthTokenHandler.accessToken(clientId, clientSecret, redirectUri, port);

        Zoom zoom = new ZoomBuilder()
                .setOAuthAccessToken(accessToken)
                .setRateLimitHandler(new RateLimiterSingleton(1))  // Here you could set num of calls per second.
                .build();

        ZoomWebHook zoomWebHook = zoom.webHook();

        String newMessageChannel = "own";
        ZoomEvent newMessageEvent = zoomWebHook.subscribe(Event.NEW_MESSAGE, newMessageChannel, m -> {
            if (m != null || !m.isEmpty()) {
                System.out.println("***==========get new message!==========***");
                System.out.println("Channel name: " + newMessageChannel);
                m.forEach(System.out::println);
                System.out.println("***====================================***");
            }
        });

        ZoomEvent updateMessageEvent = zoomWebHook.subscribe(Event.UPDATE_MESSAGE, newMessageChannel, m -> {
            if (m != null || !m.isEmpty()) {
                System.out.println("***==========get update message!==========***");
                System.out.println("Channel name: " + newMessageChannel);
                m.forEach(System.out::println);
                System.out.println("***====================================***");
            }
        });

        String newMemberChannel = "mine";
        ZoomEvent newMemberEvent = zoomWebHook.subscribe(Event.NEW_MEMBER, newMemberChannel, m -> {
            if (m != null || !m.isEmpty()) {
                System.out.println("***==========get new members!==========***");
                System.out.println("Channel name: " + newMemberChannel);
                m.forEach(System.out::println);
                System.out.println("***====================================***");
            }
        });

        Thread.sleep(60*1000);
        zoomWebHook.unsubscribe(newMessageEvent);
        zoomWebHook.unsubscribe(updateMessageEvent);
        zoomWebHook.unsubscribe(newMemberEvent);
        System.out.println("***==========unsubscribe all events!==========***");
        zoomWebHook.stop();
    }
}
