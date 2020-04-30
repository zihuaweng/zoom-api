import java.util.Date;
import java.util.List;

/**
 *  Return object for all response
 */
public class Response {
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
}

class Channel {
    String jid;
    String name;
    String id;
    int type;
}

class Member {
    String id;
    String email;
    String first_name;
    String last_name;
    String role;
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
