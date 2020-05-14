/**
 * WebHook event object that could subscribe a ZoomWebhook.
 */
public class ZoomEvent {

    private Event event;
    private String channelName;
    private EventHandlerActor operator;

    ZoomEvent(Event event, String channelName, EventHandlerActor operator){
        this.event = event;
        this.channelName = channelName;
        this.operator = operator;
    }

    public Event getEvent() {
        return event;
    }

    public String getChannelName() {
        return channelName;
    }

    public EventHandlerActor getOperator() {
        return operator;
    }

    public void unsubscribe() {
        operator.receive();
    }
}



