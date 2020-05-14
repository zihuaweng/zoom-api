import Utils.RateLimiterSingleton;

public class Zoom {

    static final String ZOOM_URL = "https://api.zoom.us/v2";

    private int timeout;
    private String apiUrl;
    private String encodedAuthorization;
    private RateLimiterSingleton rateLimitHandler;

    /**
     * Creates a client API root object.
     *
     * <dl>
     * <dt>Log in with OAuth token
     * <dt>Log in with JWT token
     * </dl>
     *
     * @param apiUrl The URL of Zoom API endpoint
     * @param timeout Time out setting for request
     * @param oauthAccessToken Secret OAuth token
     * @param jwtToken Secret jwt token
     * @param rateLimitHandler RateLimit singleton to deal with limited calls
     */
    Zoom(String apiUrl,
         int timeout,
         String oauthAccessToken,
         String jwtToken,
         RateLimiterSingleton rateLimitHandler){
        if (apiUrl.endsWith("/")) {
            apiUrl = apiUrl.substring(0, apiUrl.length()-1);
        }
        this.apiUrl = apiUrl;
        this.timeout = timeout;
        if (oauthAccessToken != null) {
            encodedAuthorization = "Bearer " + oauthAccessToken;
        } // todo with jwtToken
        this.rateLimitHandler = rateLimitHandler;
    }

    public String getEncodedAuthorization() {
        return encodedAuthorization;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public int getTimeout() {
        return timeout;
    }

    public RateLimiterSingleton getRateLimitHandler() {
        return rateLimitHandler;
    }

    public ZoomChatBuilder chat() {
        return new ZoomChatBuilder(this);
    }

    public ZoomMeetingBuilder meeting() {
        return new ZoomMeetingBuilder(this);
    }

    public ZoomRecordingBuilder recording() {
        return new ZoomRecordingBuilder(this);
    }

    public ZoomReportBuilder report() {
        return new ZoomReportBuilder(this);
    }

    public ZoomUsersBuilder users() {
        return new ZoomUsersBuilder(this);
    }

    public ZoomWebinarsBuilder webinars() {
        return new ZoomWebinarsBuilder(this);
    }

    public ZoomWebHook webHook() {
        return new ZoomWebHook(this);
    }
}
