import Utils.OAuthTokenHandler;
import Utils.RateLimiterSingleton;
import Utils.Util;

import java.util.List;

public class bot_milestone3 {

    public static void main(String[] args) {

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


//        // Testing send message by system input
//        String input;
//        Scanner scanner = new Scanner(System.in);
//        while (true) {
//            input = scanner.nextLine();
//            if (input.toLowerCase().equals("stop")) {
//                break;
//            }
//            // Send Utils.Message to specific channel
//            zoom.chat().toChannelName("test").sendMessage(input);
//        }

        // Testing send message
        for (int i = 0; i < 1; i++) {
            zoom.chat().toChannelName("test").sendMessage("test");
        }

        List<Message> messages;

        // Search for default history. Zoom uses GMT so it only return history according to GMT.
        messages = zoom.chat().toChannelName("test").history();
//        for (Utils.Message m: messages) {
//            System.out.println("OAuthBot : " + m.date_time);
//            System.out.println("OAuthBot : " + m.sender);
//            System.out.println("OAuthBot : " + m.message);
//        }
        System.out.println("OAuthBot : number of messages : " + messages.size());

        // Search history in specific days. Zoom uses GMT so it only return history according to GMT.
        messages = zoom.chat().toChannelName("test").history("2020-4-27", "2020-4-29");
//        for (Utils.Message m: messages) {
//            System.out.println("OAuthBot : " + m.date_time);
//            System.out.println("OAuthBot : " + m.sender);
//            System.out.println("OAuthBot : " + m.message);
//        }
        System.out.println("OAuthBot : number of messages : " + messages.size());

        // Search history with constrains. Zoom uses GMT so it only return history according to GMT.
        messages = zoom.chat().toChannelName("test").searchHistory("2020-4-26", "2020-4-28", x -> x.message.contains("test"));
//        for (Utils.Message m: messages) {
//            System.out.println("OAuthBot : " + m.date_time);
//            System.out.println("OAuthBot : " + m.sender);
//            System.out.println("OAuthBot : " + m.message);
//        }
        System.out.println("OAuthBot : number of messages : " + messages.size());


    }
}
