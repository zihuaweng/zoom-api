# ZoomApi

## Set Up
- java 11 or above

## How To Run
- Import zoomapi source code as Maven project. (Please check compiler version, java should be 11 or above)
- Change clientId and clientSecret in OAuthConfig.properties file.
````
clientId = your clientId
clientSecret = your clientSecret
port = 4000   # This port is used for ngrok http server
ngrokServerUrl = http://localhost:4040/api/tunnels  
timeout = 15
````
- Open ngrok in your terminal using command. Please use the the same port number set up in OAuthConfig.properties file.
```
ngrok http 4000
```
- Run code using Intellij
    - Run src/bots/Bot3.java (If you fail to build the project, please check your java compiler version, it should be at least 11.)
    - The application will open your default browser and redirect to the sign in page of Zoom. Remember to click sign in and authorize the application.
    
- Rate Limit
    - Zoom Api allows 10 calls per second. The number in this application is 1 (which is controlled in Utils.RateLimiterSingleton class, line 20 in Bot3.java, setRateLimitHandler(new Utils.RateLimiterSingleton(1))). If users try more than 1 request in second, it will show warning and delay request.