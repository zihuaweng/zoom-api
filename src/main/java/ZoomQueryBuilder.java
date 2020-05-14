import org.json.JSONObject;

import java.net.http.HttpResponse;

public abstract class ZoomQueryBuilder extends ZoomRequest implements Cloneable {
    protected final Zoom root;
    protected final JSONObject requestData;

    /**
     * Instantiates a new Query builder.
     */
    ZoomQueryBuilder(Zoom root) {
        super(root);
        this.root = root;
        this.requestData = new JSONObject();
    }

    /**
     * Add parameters to request
     * @param term parameter key
     * @param data parameter values
     * @return  builder object
     */
    public ZoomQueryBuilder param(String term, Object data) {
        requestData.put(term, data);
        return this;
    }

    /**
     * Run Http request.
     * @param method http method: GET, POST, PUT, PATCH, DELETE
     * @param endpoint request tail url
     * @param timeout time out for request
     * @param header header with access token
     * @return HttpResponse<String> result
     */
    public HttpResponse<String> run(String method, String endpoint, int timeout, String[] header) {
        HttpResponse<String> response;
        switch (method) {
            case "GET":
                response = getRequest(endpoint, timeout, header, requestData);
                break;
            case "POST":
                response = postRequest(endpoint, timeout, header, requestData);
                break;
            case "PUT":
                response = putRequest(endpoint, timeout, header, requestData);
                break;
            case "PATCH":
                response = patchRequest(endpoint, timeout, header, requestData);
                break;
            case "DELETE":
                response = deleteRequest(endpoint, timeout, header, requestData);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + method);
        }
        return response;
    }

    @Override
    public ZoomQueryBuilder clone() {
        try {
            return (ZoomQueryBuilder) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone should be supported", e);
        }
    }

}
