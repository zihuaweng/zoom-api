import com.google.gson.Gson;

import java.util.Date;

public class ZoomReportBuilder extends ZoomQueryBuilder {

    private final Zoom root;
    private String method;
    private String apiUrlTail;
    private final String[] header;
    private int timeout;

    public ZoomReportBuilder(Zoom root) {
        super(root);
        this.root = root;
        this.header = new String[]{"Authorization", root.getEncodedAuthorization()};
        this.timeout = root.getTimeout();
    }

    public ZoomReportBuilder param(String term, Object data) {
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

    public ZoomReportBuilder listUserReport(String userId, Date from, Date to) {
        setApiUrlTail("/report/users/" + userId + "meetings");
        setMethod("GET");
        super.param("from", from);
        super.param("to", to);
        return this;
    }

    public ZoomReportBuilder listAccountReport(Date from, Date to) {
        setApiUrlTail("/report/users/");
        setMethod("GET");
        super.param("from", from);
        super.param("to", to);
        return this;
    }

    public ZoomReportBuilder deleteRecording(String meetingId) {
        setApiUrlTail("/meetings/" + meetingId + "recordings");
        setMethod("DELETE");
        return this;
    }

    public ZoomReportBuilder next_page_token(String next_page_token) {
        return param("next_page_token", next_page_token);
    }

    public ZoomReportBuilder page_size(int page_size) {
        return param("page_size", page_size);
    }

    public ZoomReportBuilder from(String from) {
        return param("from", from);
    }

    public ZoomReportBuilder to(String to) {
        return param("to", to);
    }

    public ZoomReportBuilder type(String type) {
        return param("type", type);
    }


}
