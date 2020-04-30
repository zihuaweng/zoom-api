import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ZoomBuilder implements Cloneable{

    private String endpoint = Zoom.ZOOM_URL;
    private String oauthToken;
    private String jwtToken;
    private int timeout = 10;

    private RateLimiterSingleton rateLimitHandler;

    /**
     * Instantiates a new Zoom builder.
     */
    public ZoomBuilder() {
    }

    /**
     * Create ZoomBuilder from properties
     * @param props Properties with all login information
     * @return ZoomBuilder
     */
    public static ZoomBuilder fromProperties(Properties props) {
        ZoomBuilder self = new ZoomBuilder();
        self.withOAuthToken(props.getProperty("oauth", "None"));
        self.withJwtToken(props.getProperty("jwt", "None"));
        self.withEndpoint(props.getProperty("endpoint", Zoom.ZOOM_URL));
        self.withTimeout(Integer.parseInt(props.getProperty("timeout", "10")));
        return self;
    }

    /**
     * Create ZoomBuilder from properties file
     * @param propertiesFile Property file with all login information
     * @return ZoomBuilder
     */
    public static ZoomBuilder fromPropertiesFile(String propertiesFile) {
        Properties props = new Properties();
        try{
            FileInputStream in = new FileInputStream(propertiesFile);
            props.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fromProperties(props);
    }

    /**
     * Create ZoomBuilder using only OAuthAccessToken
     * @param accessToken OAuthAccessToken retrieve from Zoom
     * @return ZoomBuilder
     */
    public ZoomBuilder setOAuthAccessToken(String accessToken) {
        this.withOAuthToken(accessToken);
        return this;
    }

    /**
     * Create ZoomBuilder using only OAuthAccessToken
     * @param jwtToken jwtToken retrieve from Zoom
     * @return ZoomBuilder
     */
    public ZoomBuilder setJwtToken(String jwtToken) {
        this.withJwtToken(jwtToken);
        return this;
    }

    private ZoomBuilder withEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    private ZoomBuilder withJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
        return this;
    }

    private ZoomBuilder withOAuthToken(String oauthToken) {
        this.oauthToken = oauthToken;
        return this;
    }

    public ZoomBuilder setRateLimitHandler(RateLimiterSingleton rateLimitHandler) {
        this.rateLimitHandler = rateLimitHandler;
        return this;
    }

    private ZoomBuilder withTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * Builds a Zoom instance.
     * @return
     */
    public Zoom build() {
        return new Zoom(endpoint,
                timeout,
                oauthToken,
                jwtToken,
                rateLimitHandler);
    }

    @Override
    public ZoomBuilder clone() {
        try {
            return (ZoomBuilder) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone should be supported", e);
        }
    }

}
