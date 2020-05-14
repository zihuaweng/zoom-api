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

public class OAuthTokenHandler {

    private static final String TOKEN_ENDPOINT_URL = "https://zoom.us/oauth/token";
    private static final String AUTHORIZATION_URL = "https://zoom.us/oauth/authorize";

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
            System.out.println("OAuth : Requesting authorize code...");
            OAuthClientRequest request = OAuthClientRequest
                    .authorizationLocation(AUTHORIZATION_URL)
                    .setClientId(clientId)
                    .setRedirectURI(redirectUri)
                    .setResponseType("code")
                    .buildQueryMessage();

            String authorizationUrl = request.getLocationUri();
            System.out.println("OAuth : Open authorization url : " + authorizationUrl);

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
            System.out.println("OAuth : authorizeCode = ***" + authorizeCode + "***");
            System.out.println("OAuth : Requesting access token...");
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
            System.out.println("OAuth : AccessToken " + accessToken);
            System.out.println("OAuth : Expires In " + expiresIn);
            System.out.println("OAuth : =============================================");
            return accessToken;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("OAuth : Request access token failed!");
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

            System.out.println("Local Utils.Server: Waiting for clients to connect...");
            while (code == null) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String line;
                System.out.println("Local Utils.Server: Catch client && reading content...");
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
            System.err.println("Unable to process zoomapi.client request");
            e.printStackTrace();
        }

    }

}