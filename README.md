# ZoomApi

## Set Up
- java 11 or above

## How To Run
- Import zoomapi source code as Maven project. 
- Change clientId and clientSecret in OAuthConfig.properties file.
````
clientId = your clientId
clientSecret = your clientSecret
port = 4000   # This port is used for ngrok http server
ngrokServerUrl = http://localhost:4040/api/tunnels  
timeout = 15
database = zoom
````
- Open ngrok in your terminal using command. Please use the the same port number set up in OAuthConfig.properties file.
```
ngrok http 4000
```
- Run code using Intellij
    - Run src/bots/Bot.java
    - The application will open your default browser and redirect to the sign in page of Zoom. Remember to click sign in and authorize the application.
    
- Rate Limit
    - Zoom Api allows 10 calls per second. The number in this application is 1 (new RateLimiterSingleton(1)). If users try more than 1 request in second, it will show warning and delay request.
