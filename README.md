# ZoomApi
This is a java wrapper around Zoom APIs. Only tested on iOS system locally.

## Set Up Locally
- java 11 or above
- Import zoomapi source code as Maven project. 
- Open ngrok in your terminal using command. Please use the the same port number set up in OAuthConfig.properties file.
```
ngrok http 4000
```
- Run code using Intellij
    - Run src/bots/Bot.java
    - The application will open your default browser and redirect to the sign in page of Zoom. Remember to click sign in and authorize the application.
    
## How to use
- Change clientId and clientSecret in OAuthConfig.properties file.
````
clientId = your clientId
clientSecret = your clientSecret
port = 4000   # This port is used for ngrok http server
ngrokServerUrl = http://localhost:4040/api/tunnels  
timeout = 15
database = zoom
````
- Create a zoom bot
```java
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
```

- Send message
```java
 // Send message
zoom.chat().toChannelName("test").sendMessage("test");
```

- Search history
```java
List<Message> messages;

// Search for default history. Zoom uses GMT so it only return history according to GMT.
messages = zoom.chat().toChannelName("test").history();

// Search history in specific days. Zoom uses GMT so it only return history according to GMT.
messages = zoom.chat().toChannelName("test").history("2020-4-27", "2020-4-29");

// Search history with constrains. Zoom uses GMT so it only return history according to GMT.
messages = zoom.chat().toChannelName("test").searchHistory("2020-4-26", "2020-4-28", x -> x.message.contains("test"));
```

- WebHook
```java
ZoomWebHook zoomWebHook = zoom.webHook();

String newMessageChannel = "own";
ZoomEvent newMessageEvent = zoomWebHook.subscribe(Event.NEW_MESSAGE, newMessageChannel, m -> {
    if (m != null || !m.isEmpty()) {
        logger.info("Channel {} gets new message!", newMessageChannel);
        m.forEach(x -> logger.info(x.toString()));
    }
});

ZoomEvent updateMessageEvent = zoomWebHook.subscribe(Event.UPDATE_MESSAGE, newMessageChannel, m -> {
    if (m != null || !m.isEmpty()) {
        logger.info("Channel {} gets update message!", newMessageChannel);
        m.forEach(x -> logger.info(x.toString()));
    }
});

String newMemberChannel = "mine";
ZoomEvent newMemberEvent = zoomWebHook.subscribe(Event.NEW_MEMBER, newMemberChannel, m -> {
    if (m != null || !m.isEmpty()) {
        logger.info("Channel {} get new members!", newMemberChannel);
        m.forEach(x -> logger.info(x.toString()));
    }
});

zoomWebHook.unsubscribe(newMessageEvent);
zoomWebHook.unsubscribe(updateMessageEvent);
zoomWebHook.unsubscribe(newMemberEvent);
zoomWebHook.stop();
```

- Caching
```java
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
```

- Rate Limit
    - Zoom Api allows 10 calls per second. The number in this application is 1 (new RateLimiterSingleton(1)). If users try more than 1 request in second, it will show warning and delay request.
