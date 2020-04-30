import com.google.gson.Gson;
import org.joda.time.LocalDate;

import java.net.http.HttpResponse;
import java.util.*;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public class ZoomChatBuilder extends ZoomQueryBuilder {

    private final Zoom root;
    private String method;
    private String apiUrlTail;
    private final String[] header;
    private int timeout;

    /**
     * Create a chat related builder
     * @param root
     */
    public ZoomChatBuilder(Zoom root) {
        super(root);
        this.root = root;
        this.header = new String[]{"Authorization", root.getEncodedAuthorization()};
        this.timeout = root.getTimeout();
    }

    /**
     * Set http request parameters.
     * @param term parameter key
     * @param data parameter values
     * @return ZoomChatBuilder
     */
    public ZoomChatBuilder param(String term, Object data) {
        super.param(term, data);
        return this;
    }

    private void setApiUrlTail(String apiUrlTail) {
        this.apiUrlTail = apiUrlTail;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Execute http request.
     * @return Response
     */
    public Response run() {
        HttpResponse<String> httpRes = run(method, apiUrlTail, timeout, header);
        System.out.println("ZoomChatBuilder : " + method + "   " + apiUrlTail);
        System.out.println("ZoomChatBuilder : " + httpRes.statusCode());
        Response res = new Gson().fromJson(httpRes.body(), Response.class);
        return res;
    }

    // chat channels
    public Response listChannels() {
        setApiUrlTail("/chat/users/me/channels");
        setMethod("GET");
        return run();
    }

    public Response createChannel() {
        setApiUrlTail("/chat/users/me/channels");
        setMethod("POST");
        return run();
    }

    public Response getChannelById(String channelId) {
        setApiUrlTail("/chat/channels/" + channelId);
        setMethod("POST");
        return run();
    }

    public Response getChannelByName(String channelName) {
        return getChannelById(getChannelId(channelName));
    }

    public Response updateChannelById(String channelId) {
        setApiUrlTail("/chat/channels/" + channelId);
        setMethod("PATCH");
        return run();
    }

    public Response updateChannelByName(String channelName) {
        return updateChannelById(getChannelId(channelName));
    }

    public Response deleteChannelById(String channelId) {
        setApiUrlTail("/chat/channels/" + channelId);
        setMethod("DELETE");
        return run();
    }

    public Response deleteChannelByName(String channelName) {
        return deleteChannelById(getChannelId(channelName));
    }

    public Response listMembersById(String channelId) {
        setApiUrlTail("/chat/channels/" + channelId + "/members");
        setMethod("GET");
        return run();
    }

    public Response listMembersByName(String channelName) {
        return listMembersById(getChannelId(channelName));
    }

    public Response inviteMembers(String channelId) {
        setApiUrlTail("/chat/channels/" + channelId + "/members");
        setMethod("POST");
        return run();
    }

    public Response joinMembers(String channelId) {
        setApiUrlTail("/chat/channels/" + channelId + "/members/me");
        setMethod("POST");
        return run();
    }

    public Response leaveMembers(String channelId) {
        setApiUrlTail("/chat/channels/" + channelId + "/members/me");
        setMethod("DELETE");
        return run();
    }

    public Response removeMembers(String channelId, String memberId) {
        setApiUrlTail("/chat/channels/" + channelId + "/members/" + memberId);
        setMethod("DELETE");
        return run();
    }

    /**
     * Send message to a channel
     */
    public Response sendMessage(String message) {
        setApiUrlTail("/chat/users/me/messages");
        setMethod("POST");
        message(message);
        return run();
    }

    public Response history(String userId) {
        setApiUrlTail("/chat/users/" + userId + "/messages");
        setMethod("GET");
        return run();
    }

    /**
     * Get chat message history with start date and end date.
     * @param from String start date with format: yyyy-mm-dd
     * @param to String end date with format: yyyy-mm-dd
     * @return List of Message object
     */
    public List<Message> history(String from, String to) {
        LocalDate dateStart = new LocalDate(from);
        LocalDate dateEnd = new LocalDate(to);
        return history(dateStart, dateEnd);
    }

    /**
     * Get chat message history with start date and end date.
     * @param dateStart LocalDate start date with format: yyyy-mm-dd
     * @param dateEnd  LocalDate end date with format: yyyy-mm-dd
     * @return List of Message object
     */
    public List<Message> history(LocalDate dateStart, LocalDate dateEnd) {

        List<LocalDate> days = new ArrayList<>();
        while (dateStart.isBefore(dateEnd)) {
            System.out.println("ZoomChatBuilder : LocalDate : " + dateStart);
            dateStart = dateStart.plusDays(1);
            days.add(dateStart);
        }
        List<Message> messages = new ArrayList<>();
        for (LocalDate day : days) {
            String next_page_token = "";
            do {
                Response res = this.date(day.toString()).next_page_token(next_page_token).history("me");
                next_page_token = res.next_page_token;
                messages.addAll(res.messages);
            } while (!next_page_token.equals(""));
        }
        return messages;
    }

    /**
     * Get chat message history with start date and end date and constrain.
     * @param dateStart String start date with format: yyyy-mm-dd
     * @param dateEnd String end date with format: yyyy-mm-dd
     * @param func constrain lambda function
     * @return List of Message object
     */
    public List<Message> searchHistory(String dateStart, String dateEnd, Predicate<Message> func) {
        List<Message> messages = history(dateStart, dateEnd);
        List<Message> filteredMessages = messages.stream().filter(func).collect(toList());
        return filteredMessages;
    }

    public ZoomChatBuilder deleteMessage(String messageId) {
        setApiUrlTail("/chat/users/me/messages/" + messageId);
        setMethod("DELETE");
        return this;
    }


    /**
     * Get channel id with channel name
     * @param channelName String channel name
     * @return channel id
     */
    public String getChannelId(String channelName) {
        Response response = root.chat().listChannels();
        List<Channel> channels = response.channels;
        String channelId = null;
        for (Channel c : channels) {
            if (c.name.equals(channelName)) {
                channelId = c.id;
            }
        }
        return channelId;
    }


    public ZoomChatBuilder toChannelName(String channelName) {
        return toChannelId(getChannelId(channelName));
    }

    public ZoomChatBuilder message(String message) {
        return param("message", message);
    }

    public ZoomChatBuilder toChannelId(String channelId) {
        return param("to_channel", channelId);
    }

    public ZoomChatBuilder toContact(String emailAddress) {
        return param("to_contact", emailAddress);
    }

    public ZoomChatBuilder date(String date) {
        return param("date", date);
    }

    public ZoomChatBuilder channelName(String name) {
        return param("name", name);
    }

    public ZoomChatBuilder type(int type) {
        return param("type", type);
    }

    public ZoomChatBuilder next_page_token(String next_page_token) {
        return param("next_page_token", next_page_token);
    }

    public ZoomChatBuilder page_size(int page_size) {
        return param("page_size", page_size);
    }


}
