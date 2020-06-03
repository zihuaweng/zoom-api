import com.google.gson.Gson;
import database.SQLDatabase;
import database.entity.Channels;
import database.entity.ChannelsMembership;
import database.entity.Messages;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpResponse;
import java.util.*;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public class ZoomChatBuilder extends ZoomQueryBuilder {

    private long VALID_TIME = 600;

    private final Zoom root;
    private String method;
    private String apiUrlTail;
    private final String[] header;
    private int timeout;
    private final Logger logger = LoggerFactory.getLogger(ZoomChatBuilder.class.getName());

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
    public ZoomResponse run() {
        HttpResponse<String> httpRes = run(method, apiUrlTail, timeout, header);
        logger.debug("method: {} apiUrlTail: {}, statusCode: {}", method, apiUrlTail, httpRes.statusCode());
        ZoomResponse res = new Gson().fromJson(httpRes.body(), ZoomResponse.class);
        return res;
    }

    // chat channels
    public List<Channel> listChannels() {
        setApiUrlTail("/chat/users/me/channels");
        setMethod("GET");
        ZoomResponse res = run();
        List<Channel> channels = new ArrayList<>();
        channels.addAll(res.channels);
        return channels;
    }

    public List<Channel> listChannels(String clientId, SQLDatabase sqlDatabase) {
        List<Channel> channels = listChannels();
        for (Channel channel: channels) {
            Channels c = new Channels(clientId, channel.id, channel.name);
            Channels cFromSQL = sqlDatabase.getData(Channels.class, Arrays.asList("clientId", "channelId"), Arrays.asList(clientId, channel.id));
            if (cFromSQL == null) {
                sqlDatabase.insert(c);
            } else if (!cFromSQL.equals(c) || System.currentTimeMillis() / 1000 - cFromSQL.getTimestamp() > VALID_TIME) {
                sqlDatabase.update(c);
            }
        }
        return channels;
    }

    public ZoomResponse createChannel() {
        setApiUrlTail("/chat/users/me/channels");
        setMethod("POST");
        return run();
    }

    public ZoomResponse createChannel(String channelName) {
        setApiUrlTail("/chat/users/me/channels");
        setMethod("POST");
        param("name", channelName);
        return run();
    }

    public ZoomResponse getChannelById(String channelId) {
        setApiUrlTail("/chat/channels/" + channelId);
        setMethod("POST");
        return run();
    }

    public ZoomResponse getChannelByName(String channelName) {
        return getChannelById(getChannelId(channelName));
    }

    public ZoomResponse updateChannelById(String channelId) {
        setApiUrlTail("/chat/channels/" + channelId);
        setMethod("PATCH");
        return run();
    }

    public ZoomResponse updateChannelByName(String channelName) {
        return updateChannelById(getChannelId(channelName));
    }

    public ZoomResponse deleteChannelById(String channelId) {
        setApiUrlTail("/chat/channels/" + channelId);
        setMethod("DELETE");
        return run();
    }

    public ZoomResponse deleteChannelByName(String channelName) {
        return deleteChannelById(getChannelId(channelName));
    }

    public List<Member> listMembersById(String channelId) {
        setApiUrlTail("/chat/channels/" + channelId + "/members");
        setMethod("GET");
        ZoomResponse res = run();
        List<Member> members = new ArrayList<>();
        members.addAll(res.members);
        return members;
    }

    public List<Member> listMembersByName(String channelName) {
        return listMembersById(getChannelId(channelName));
    }

    public List<Member> listMembersByName(String channelName, SQLDatabase sqlDatabase) {
        String channelId = getChannelId(channelName);
        List<Member> members = listMembersByName(channelName);
        for (Member member: members) {
            ChannelsMembership m = new ChannelsMembership(channelId, channelName, member.first_name + " " + member.last_name, member.email);
            ChannelsMembership mFromSQL = sqlDatabase.getData(ChannelsMembership.class, Arrays.asList("channelId", "email"), Arrays.asList(channelId, member.email));
            if (mFromSQL == null) {
                sqlDatabase.insert(m);
            } else if (!mFromSQL.equals(m) || System.currentTimeMillis() / 1000 - mFromSQL.getTimestamp() > VALID_TIME){
                sqlDatabase.update(m);
            }
        }
        return members;
    }

    public ZoomResponse inviteMembers(String channelId) {
        setApiUrlTail("/chat/channels/" + channelId + "/members");
        setMethod("POST");
        return run();
    }

    public ZoomResponse joinMembers(String channelId) {
        setApiUrlTail("/chat/channels/" + channelId + "/members/me");
        setMethod("POST");
        return run();
    }

    public ZoomResponse leaveMembers(String channelId) {
        setApiUrlTail("/chat/channels/" + channelId + "/members/me");
        setMethod("DELETE");
        return run();
    }

    public ZoomResponse removeMembers(String channelId, String memberId) {
        setApiUrlTail("/chat/channels/" + channelId + "/members/" + memberId);
        setMethod("DELETE");
        return run();
    }

    /**
     * Send message to a channel
     */
    public ZoomResponse sendMessage(String message) {
        setApiUrlTail("/chat/users/me/messages");
        setMethod("POST");
        message(message);
        return run();
    }

    /**
     * Get chat message history with start date and end date. Zoom uses GMT so it only return history according to GMT.
     * @param userId User id
     * @return List of Message object
     */
    public ZoomResponse history(String userId) {
        setApiUrlTail("/chat/users/" + userId + "/messages");
        setMethod("GET");
        return run();
    }

    /**
     * Get chat message history with start date and end date. Zoom uses GMT so it only return history according to GMT.
     * @return List of Message object
     */
    public List<Message> history() {
        setApiUrlTail("/chat/users/me/messages");
        setMethod("GET");
        ZoomResponse res = run();
        List<Message> messages = new ArrayList<>();
        messages.addAll(res.messages);
        Collections.sort(messages, Comparator.comparing(x -> x.date_time));
        return messages;
    }

    public List<Message> history(String channelId, SQLDatabase sqlDatabase) {
        List<Message> messages = history();
        for (Message message: messages) {
            Messages m = new Messages(channelId, message.id, message.sender, message.message, message.date_time);
            Messages mFromSQL = sqlDatabase.getData(Messages.class, message.id);
            if (mFromSQL == null) {
                sqlDatabase.insert(m);
            } else if (!mFromSQL.equals(m) || System.currentTimeMillis() / 1000 - mFromSQL.getTimestamp() > VALID_TIME) {
                sqlDatabase.update(m);
            }
        }
        return messages;
    }

    /**
     * Get chat message history with start date and end date. Zoom uses GMT so it only return history according to GMT.
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
     * Get chat message history with start date and end date. Zoom uses GMT so it only return history according to GMT.
     * @param dateStart LocalDate start date with format: yyyy-mm-dd
     * @param dateEnd  LocalDate end date with format: yyyy-mm-dd
     * @return List of Message object
     */
    public List<Message> history(LocalDate dateStart, LocalDate dateEnd) {

        List<LocalDate> days = new ArrayList<>();
        int gap = Days.daysBetween(dateStart, dateEnd).getDays();
        for (int i = 0; i <= gap; i++) {
            days.add(dateStart);
            dateStart = dateStart.plusDays(1);
        }
        List<Message> messages = new ArrayList<>();
        for (LocalDate day : days) {
            String next_page_token = "";
            do {
                ZoomResponse res = this.date(day.toString()).page_size("50").next_page_token(next_page_token).history("me");
                next_page_token = res.next_page_token;
                messages.addAll(res.messages);
            } while (!next_page_token.equals(""));
        }
        Collections.sort(messages, Comparator.comparing(x -> x.date_time));
        return messages;
    }

    /**
     * Get chat message history with start date and end date and constrain. Zoom uses GMT so it only return history according to GMT.
     * @param dateStart String start date with format: yyyy-mm-dd
     * @param dateEnd String end date with format: yyyy-mm-dd
     * @param func constrain lambda function
     * @return List of Message object
     */
    public List<Message> searchHistory(String dateStart, String dateEnd, Predicate<Message> func) {
        List<Message> messages = history(dateStart, dateEnd);
        List<Message> filteredMessages = messages.stream().filter(func).collect(toList());
        Collections.sort(messages, Comparator.comparing(x -> x.date_time));
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
        List<Channel> channels = root.chat().listChannels();
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

    public ZoomChatBuilder page_size(String page_size) {
        return param("page_size", page_size);
    }


}
