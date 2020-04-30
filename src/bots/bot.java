import java.util.List;
import java.util.Scanner;

public class bot {

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

        Response response;
        String input;

//        // Testing send message by system input
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

        // Search for default history
        response = zoom.chat().toChannelName("test").history("me");
        for (Message m: response.messages) {
            System.out.println("OAuthBot : " + m.date_time);
            System.out.println("OAuthBot : " + m.sender);
            System.out.println("OAuthBot : " + m.message);
        }

        // Search history in specific days
        List<Message> messages;
        messages = zoom.chat().toChannelName("test").history("2020-4-26", "2020-4-29");

        for (Message m: messages) {
            System.out.println("OAuthBot : " + m.date_time);
            System.out.println("OAuthBot : " + m.sender);
            System.out.println("OAuthBot : " + m.message);
        }

        // Search history with constrains
        messages = zoom.chat().toChannelName("test").searchHistory("2020-4-26", "2020-4-29", x -> x.message.contains("test"));
        for (Message m: messages) {
            System.out.println("OAuthBot : " + m.date_time);
            System.out.println("OAuthBot : " + m.sender);
            System.out.println("OAuthBot : " + m.message);
        }


    }
}
