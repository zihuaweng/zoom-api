import java.util.Date;
import java.util.List;

/**
 *  Return object for all response
 */
public class ZoomResponse {
    int total_records;
    int page_size;
    String id;
    String name;
    int type;
    String jid;
    String next_page_token;
    Date added_at;
    int page_count;
    int page_number;
    String topic;
    Date start_time;
    int duration;
    String schedule_for;
    String timezone;
    String password;
    String agenda;
    Date from;
    Date to;
    List<Channel> channels;
    List<Message> messages;
    List<Member> members;
    List<Meeting> meetings;
    List<TrackingField> trackingFields;

}


class Message {
    String id;
    String message;
    String sender;
    Date date_time;
    long timestamp;

    public String getMessage() {
        return message;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return sender + " : " + message;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Message)
            return id.equals(((Message) obj).id);
        return false;
    }

}

class Channel {
    String jid;
    String name;
    String id;
    int type;

    @Override
    public String toString() {
        return jid + " " + name + " " + id + " " + type;
    }
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof Channel)
            return id.equals(((Channel) obj).id);
        return false;
    }
}

class Member {
    String id;
    String email;
    String first_name;
    String last_name;
    String role;

    @Override
    public String toString() {
        return id + " " + email;
    }
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof Member)
            return id.equals(((Member) obj).id);
        return false;
    }
}

class Meeting {
    String uuid;
    String id;
    String host_id;
    String topic;
    int type;
    Date start_time;
    int duration;
    String timezone;
    String create_at;
    String join_url;
    String agenda;
}

class TrackingField {
    String field;
    String value;
}
