import com.google.gson.Gson;

public class ZoomRecordingBuilder extends ZoomQueryBuilder {

    private final Zoom root;
    private String method;
    private String apiUrlTail;
    private final String[] header;
    private int timeout;

    public ZoomRecordingBuilder(Zoom root) {
        super(root);
        this.root = root;
        this.header = new String[]{"Authorization", root.getEncodedAuthorization()};
        this.timeout = root.getTimeout();
    }

    public ZoomRecordingBuilder param(String term, Object data) {
        super.param(term, data);
        return this;
    }

    private void setApiUrlTail(String apiUrlTail) {
        this.apiUrlTail = apiUrlTail;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public ZoomResponse run() {
        ZoomResponse res = new Gson().fromJson(run(method, apiUrlTail, timeout, header).body(), ZoomResponse.class);
        return res;
    }

    public ZoomRecordingBuilder listRecording(String userId) {
        setApiUrlTail("/users/" + userId + "recordings");
        setMethod("GET");
        return this;
    }

    public ZoomRecordingBuilder getRecording(String meetingId) {
        setApiUrlTail("/meetings/" + meetingId + "recordings");
        setMethod("GET");
        return this;
    }

    public ZoomRecordingBuilder deleteRecording(String meetingId) {
        setApiUrlTail("/meetings/" + meetingId + "recordings");
        setMethod("DELETE");
        return this;
    }

    public ZoomRecordingBuilder next_page_token(String next_page_token) {
        return param("next_page_token", next_page_token);
    }

    public ZoomRecordingBuilder nc(String mc) {
        return param("mc", mc);
    }

    public ZoomRecordingBuilder trash(boolean trash) {
        return param("trash", trash);
    }

    public ZoomRecordingBuilder from(String from) {
        return param("from", from);
    }

    public ZoomRecordingBuilder to(String to) {
        return param("to", to);
    }

    public ZoomRecordingBuilder trash_type(String trash_type) {
        return param("trash_type", trash_type);
    }

    public ZoomRecordingBuilder action(String action) {
        return param("action", action);
    }


}
