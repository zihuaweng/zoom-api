import com.google.gson.Gson;

public class ZoomMeetingBuilder extends ZoomQueryBuilder {

    private final Zoom root;
    private String method;
    private String apiUrlTail;
    private final String[] header;
    private int timeout;

    public ZoomMeetingBuilder(Zoom root) {
        super(root);
        this.root = root;
        this.header = new String[]{"Authorization", root.getEncodedAuthorization()};
        this.timeout = root.getTimeout();
    }

    public ZoomMeetingBuilder param(String term, Object data) {
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

    // chat channels
    public ZoomMeetingBuilder listMeeting(String userId) {
        setApiUrlTail("/users/" + userId + "meetings");
        setMethod("GET");
        return this;
    }

    public ZoomMeetingBuilder createMeeting(String userId) {
        setApiUrlTail("/users/" + userId + "/meetings");
        setMethod("POST");
        return this;
    }

    public ZoomMeetingBuilder getMeeting(String meetingId) {
        setApiUrlTail("/meetings/" + meetingId);
        setMethod("GET");
        return this;
    }

    public ZoomMeetingBuilder updateMeeting(String meetingId) {
        setApiUrlTail("/meetings/" + meetingId);
        setMethod("PATCH");
        return this;
    }

    public ZoomMeetingBuilder deleteMeeting(String meetingId) {
        setApiUrlTail("/meetings/" + meetingId);
        setMethod("DELETE");
        return this;
    }

    public ZoomMeetingBuilder schedule_for_reminder(boolean schedule_for_reminder) {
        return param("schedule_for_reminder", schedule_for_reminder);
    }

    public ZoomMeetingBuilder occurrence_id(String occurrence_id) {
        return param("occurrence_id", occurrence_id);
    }

    public ZoomMeetingBuilder type(int type) {
        return param("type", type);
    }

    public ZoomMeetingBuilder page_number(String page_number) {
        return param("page_number", page_number);
    }

    public ZoomMeetingBuilder page_size(int page_size) {
        return param("page_size", page_size);
    }


}
