package Utils;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuthTokenHandler {

    private static final String TOKEN_ENDPOINT_URL = "https://zoom.us/oauth/token";
    private static final String AUTHORIZATION_URL = "https://zoom.us/oauth/authorize";
    private static final Logger LOGGER = LoggerFactory.getLogger(OAuthTokenHandler.class.getName());

    /**
     * Request authorization code from resource server.
     *
     * @param clientId    Api key for access
     * @param redirectUri Local server url for resource server to redirect (ngrok server url)
     * @param port        Local server port
     * @return authorization code
     */
    private static String authorizeCode(String clientId, String redirectUri, int port) {
        try {
            LOGGER.debug("Requesting authorize code...");
            OAuthClientRequest request = OAuthClientRequest
                    .authorizationLocation(AUTHORIZATION_URL)
                    .setClientId(clientId)
                    .setRedirectURI(redirectUri)
                    .setResponseType("code")
                    .buildQueryMessage();

            String authorizationUrl = request.getLocationUri();
            LOGGER.debug("Open authorization url : {}", authorizationUrl);

            openBrowser(authorizationUrl);
            Server server = new Server(port);
            server.startServer();
            return server.getCode();

        } catch (OAuthSystemException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Request access token from resource server.
     *
     * @param clientId     Api key for access
     * @param clientSecret Api key password for access
     * @param redirectUri  Local server url for resource server to redirect (ngrok server url)
     * @param port         Local server port
     * @return access token
     */
    public static String accessToken(String clientId, String clientSecret, String redirectUri, int port) {
        try {
            String authorizeCode = authorizeCode(clientId, redirectUri, port);
            LOGGER.debug("AuthorizeCode = ***{}***", authorizeCode);
            LOGGER.debug("Requesting access token...");
            OAuthClientRequest request = OAuthClientRequest
                    .tokenLocation(TOKEN_ENDPOINT_URL)
                    .setGrantType(GrantType.AUTHORIZATION_CODE)
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
                    .setRedirectURI(redirectUri)
                    .setCode(authorizeCode)
                    .buildQueryMessage();

            OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
            OAuthAccessTokenResponse oAuthResponse = oAuthClient.accessToken(request);
            String accessToken = oAuthResponse.getAccessToken();
            long expiresIn = oAuthResponse.getExpiresIn();
            LOGGER.debug("AccessToken {}", accessToken);
            LOGGER.debug("Expires In {}", expiresIn);
            LOGGER.info("Receive OAuth accessToken");
            return accessToken;
        } catch (Exception e) {
            LOGGER.error("Request access token failed!", e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Open redirect url using default browser.
     *
     * @param url
     */
    private static void openBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/**
 * Local ServerSocket for receiving authorization code from resource server.
 */
class Server {
    private static final Logger LOGGER = LoggerFactory.getLogger(OAuthTokenHandler.class.getName());

    private final int port;
    private String code = null;

    public Server(int port) {
        this.port = port;
    }

    public String getCode() {
        return code;
    }

    public void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            LOGGER.debug("Waiting for clients to connect...");
            while (code == null) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String line;
                LOGGER.debug("Catch client && reading content...");
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("GET")) {
                        if (line.split("[= ]").length == 4) {
                            code = line.split("[= ]")[2];
                            break;
                        }
                    }
                }
            }
            serverSocket.close();
        } catch (IOException e) {
            LOGGER.error("Unable to process zoomapi.client request", e);
            e.printStackTrace();
        }

    }

}