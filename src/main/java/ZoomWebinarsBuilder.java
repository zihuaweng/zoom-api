import com.google.gson.Gson;

public class ZoomWebinarsBuilder extends ZoomQueryBuilder {

    private final Zoom root;
    private String method;
    private String apiUrlTail;
    private final String[] header;
    private int timeout;

    public ZoomWebinarsBuilder(Zoom root) {
        super(root);
        this.root = root;
        this.header = new String[]{"Authorization", root.getEncodedAuthorization()};
        this.timeout = root.getTimeout();
    }

    public ZoomWebinarsBuilder param(String term, Object data) {
        super.param(term, data);
        return this;
    }

    private void setApiUrlTail(String apiUrlTail) {
        this.apiUrlTail = apiUrlTail;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Response run() {
        Response res = new Gson().fromJson(run(method, apiUrlTail, timeout, header).body(), Response.class);
        return res;
    }

    public ZoomWebinarsBuilder listWebinars(String userId) {
        setApiUrlTail("/users/" + userId + "webinars");
        setMethod("GET");
        return this;
    }

    public ZoomWebinarsBuilder createWebinars(String userId) {
        setApiUrlTail("/users/" + userId + "webinars");
        setMethod("POST");
        return this;
    }

    public ZoomWebinarsBuilder getWebinars(String webinarId) {
        setApiUrlTail("/webinars/" + webinarId);
        setMethod("GET");
        return this;
    }

    public ZoomWebinarsBuilder updateWebinars(String webinarId) {
        setApiUrlTail("/webinars/" + webinarId);
        setMethod("PATCH");
        return this;
    }

    public ZoomWebinarsBuilder deleteWebinars(String webinarId) {
        setApiUrlTail("/webinars/" + webinarId);
        setMethod("DELETE");
        return this;
    }

    public ZoomWebinarsBuilder page_number(int page_number) {
        return param("page_number", page_number);
    }

    public ZoomWebinarsBuilder page_size(int page_size) {
        return param("page_size", page_size);
    }

    public ZoomWebinarsBuilder occurrence_id(String occurrence_id) {
        return param("occurrence_id", occurrence_id);
    }

//    public ZoomWebinarsBuilder role_id(String role_id) {
//        return param("role_id", role_id);
//    }
//
//    public ZoomWebinarsBuilder action(String action) {
//        return param("action", action);
//    }
//
//    public ZoomWebinarsBuilder login_type(int login_type) {
//        return param("login_type", login_type);
//    }
//
//    public ZoomWebinarsBuilder transfer_email(String transfer_email) {
//        return param("transfer_email", transfer_email);
//    }
//
//    public ZoomWebinarsBuilder transfer_meeting(boolean transfer_meeting) {
//        return param("transfer_meeting", transfer_meeting);
//    }
//
//    public ZoomWebinarsBuilder transfer_webinar(boolean transfer_webinar) {
//        return param("transfer_webinar", transfer_webinar);
//    }
//
//    public ZoomWebinarsBuilder transfer_recording(boolean transfer_recording) {
//        return param("transfer_recording", transfer_recording);
//    }
}
