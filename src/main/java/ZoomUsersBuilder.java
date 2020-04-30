import com.google.gson.Gson;

public class ZoomUsersBuilder extends ZoomQueryBuilder {

    private final Zoom root;
    private String method;
    private String apiUrlTail;
    private final String[] header;
    private int timeout;

    public ZoomUsersBuilder(Zoom root) {
        super(root);
        this.root = root;
        this.header = new String[]{"Authorization", root.getEncodedAuthorization()};
        this.timeout = root.getTimeout();
    }

    public ZoomUsersBuilder param(String term, Object data) {
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

    public ZoomUsersBuilder listUsers() {
        setApiUrlTail("/users");
        setMethod("GET");
        return this;
    }

    public ZoomUsersBuilder createUsers() {
        setApiUrlTail("/users");
        setMethod("POST");
        return this;
    }

    public ZoomUsersBuilder getUser(String userId) {
        setApiUrlTail("/users/" + userId);
        setMethod("GET");
        return this;
    }

    public ZoomUsersBuilder updateUser(String userId) {
        setApiUrlTail("/users/" + userId);
        setMethod("PATCH");
        return this;
    }

    public ZoomUsersBuilder deleteUser(String userId) {
        setApiUrlTail("/users/" + userId);
        setMethod("DELETE");
        return this;
    }

    public ZoomUsersBuilder page_number(int page_number) {
        return param("page_number", page_number);
    }

    public ZoomUsersBuilder page_size(int page_size) {
        return param("page_size", page_size);
    }

    public ZoomUsersBuilder status(String status) {
        return param("status", status);
    }

    public ZoomUsersBuilder role_id(String role_id) {
        return param("role_id", role_id);
    }

    public ZoomUsersBuilder action(String action) {
        return param("action", action);
    }

    public ZoomUsersBuilder login_type(int login_type) {
        return param("login_type", login_type);
    }

    public ZoomUsersBuilder transfer_email(String transfer_email) {
        return param("transfer_email", transfer_email);
    }

    public ZoomUsersBuilder transfer_meeting(boolean transfer_meeting) {
        return param("transfer_meeting", transfer_meeting);
    }

    public ZoomUsersBuilder transfer_webinar(boolean transfer_webinar) {
        return param("transfer_webinar", transfer_webinar);
    }

    public ZoomUsersBuilder transfer_recording(boolean transfer_recording) {
        return param("transfer_recording", transfer_recording);
    }
}
