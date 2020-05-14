import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * WebHook for Zoom API.
 * This WebHook is channel oriented. Only listen to events related to channels.
 */
public class ZoomWebHook implements Runnable {

    private final int EVENT_HANDLE_INTERVAL = 5;

    private final Zoom root;
    private Thread thread;
    private boolean isComplete = false;
    private List<ZoomEvent> events;
    private Map<Channel, List<Message>> historyMessage;
    private Map<Channel, List<Message>> historyUpdateMessage;
    private Map<Channel, List<Member>> historyMembers;
    private Map<String, Channel> channels;

    ZoomWebHook(Zoom root) {
        this.root = root;
        events = new CopyOnWriteArrayList<>();
        channels = new HashMap<>();
        historyMessage = new HashMap<>();
        historyMembers = new HashMap<>();
        historyUpdateMessage = new HashMap<>();
        start();
    }

    public Thread getThread() {
        return thread;
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        if (null == thread) {
            return;
        }
        synchronized (events) {
            for (ZoomEvent event : events) {
                unsubscribe(event);
            }
        }
        thread.interrupt();
    }

    /**
     * Subscribe an event to webhook, if the event triggered, result will be sent to application bot.
     *
     * @param event       Within Event list
     * @param channelName Specific channel to listen
     * @param handler     Callback provided by application bot to process results
     * @return ZoomEvent
     */
    public ZoomEvent subscribe(Event event, String channelName, Consumer<List> handler) {
        synchronized (events) {
            EventHandlerActor eventHandlerActor = new EventHandlerActor(handler);
            ZoomEvent zoomEvent = new ZoomEvent(event, channelName, eventHandlerActor);
            events.add(zoomEvent);
            return zoomEvent;
        }
    }

    /**
     * Unsubscribe an event from webhook.
     */
    public void unsubscribe(ZoomEvent zoomEvent) {
        synchronized (events) {
            zoomEvent.unsubscribe();
            events.remove(zoomEvent);
        }
    }

    /**
     * Update channel info
     */
    public void updateChannels() {
        for (Channel channel : root.chat().listChannels()) {
            channels.put(channel.name, channel);
        }
    }

    /**
     * Initiate message history for events.
     *
     * @param channelName Given channel name from application bot
     * @return false if message history is not initiated before
     */
    public boolean checkHistoryMessage(String channelName) {
        Channel channel = channels.get(channelName);
        if (!historyMessage.containsKey(channel)) {
            historyMessage.put(channel, root.chat().toChannelId(channel.id).history());
            return false;
        }
        return true;
    }

    /**
     * Capture new messages for given channel
     *
     * @param channelName Given channel name from application bot
     * @return new message list
     */
    public List<Message> newMessage(String channelName) {
        if (!checkHistoryMessage(channelName)) return null;
        Channel channel = channels.get(channelName);
        List<Message> currentMessage = root.chat().toChannelId(channel.id).history();
        List<Message> newMessage = currentMessage.stream().filter(message -> !historyMessage.get(channel).contains(message)).collect(Collectors.toList());
        historyUpdateMessage.put(channel, currentMessage);
        return newMessage;
    }

    /**
     * Capture updated messages for given channel
     *
     * @param channelName Given channel name from application bot
     * @return updated message list
     */
    public List<Message> updateMessage(String channelName) {
        if (!checkHistoryMessage(channelName)) return null;
        Channel channel = channels.get(channelName);
        List<Message> currentMessage = root.chat().toChannelId(channel.id).history();
        List<Message> newMessage = currentMessage.stream().filter(
                message -> {
                    for (Message m : historyMessage.get(channel)) {
                        if (m.getId().equals(message.getId()) && !m.getMessage().equals(message.getMessage())) {
                            return true;
                        }
                    }
                    return false;
                }
        ).collect(Collectors.toList());
        historyUpdateMessage.put(channel, currentMessage);
        return newMessage;
    }

    /**
     * Update history messages for each iteration.
     */
    public void updateHistoryMessage() {
        historyMessage.putAll(historyUpdateMessage);
        historyUpdateMessage = new HashMap<>();
    }

    /**
     * Initiate member list for events.
     *
     * @param channelName Given channel name from application bot
     * @return false if member list is not initiated before
     */
    public boolean checkHistoryMembers(String channelName) {
        Channel channel = channels.get(channelName);
        if (!historyMembers.containsKey(channel)) {
            historyMembers.put(channel, root.chat().listMembersById(channel.id));
            return false;
        }
        return true;
    }

    /**
     * Capture new members for given channel
     *
     * @param channelName Given channel name from application bot
     * @return new member list
     */
    public List<Member> newMembers(String channelName) {
        if (!checkHistoryMembers(channelName)) return null;
        Channel channel = channels.get(channelName);
        List<Member> currentMembers = root.chat().listMembersById(channel.id);
        List<Member> newMembers = currentMembers.stream().filter(member -> !historyMembers.get(channel).contains(member)).collect(Collectors.toList());
        historyMembers.put(channel, currentMembers);
        return newMembers;
    }


    @Override
    public void run() {
        while (!isComplete) {
            try {
                Thread.sleep(EVENT_HANDLE_INTERVAL * 1000); // Sleep for EVENT_HANDLE_INTERVAL seconds.
                updateChannels();
                synchronized (events) {
                    for (ZoomEvent event : events) {
                        switch (event.getEvent()) {
                            case NEW_MESSAGE:
                                event.getOperator().receive(newMessage(event.getChannelName()));
                                break;
                            case UPDATE_MESSAGE:
                                event.getOperator().receive(updateMessage(event.getChannelName()));
                                break;
                            case NEW_MEMBER:
                                event.getOperator().receive(newMembers(event.getChannelName()));
                                break;
                        }
                    }
                }
                updateHistoryMessage();
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}

