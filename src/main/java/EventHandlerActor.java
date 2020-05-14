import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

/**
 * Actor that receive message from ZoomEventHandler and execute application level process.
 * @param <T> Result objects: Message, Members...
 */
public class EventHandlerActor<T> implements Runnable{
    enum STATE {
        alive, dead
    }

    private final BlockingQueue<List<T>> queue;
    private STATE state;
    private Thread thread;
    private Consumer<List<T>> handler;

    EventHandlerActor(Consumer<List<T>> handler) {
        this.queue = new LinkedBlockingQueue<>();
        this.handler = handler;
        start();
    }

    public Thread getThread() {
        return thread;
    }

    public void start() {
        this.state = STATE.alive;
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        if (null == thread) {
            return;
        }
        thread.interrupt();
        this.state = STATE.dead;
    }

    @Override
    public void run() {
        while (state.equals(STATE.alive)) {
            try {
                List<T> resource = queue.take();
                if (resource.isEmpty()) {
                    stop();
                    break;
                }
                handler.accept(resource);
            } catch (InterruptedException e) {
                e.printStackTrace();
                stop();
            }
        }
    }

    /**
     * Receive message from other threads.
     * @param resource List of process object
     */
    public void receive(List<T> resource) {
        if (resource != null && !resource.isEmpty())
            queue.offer(resource);
    }

    /**
     * Receive stop signal from other threads and stop the current thread.
     */
    public void receive() {
        queue.offer(new ArrayList<>());
    }

    public void send(Event event, String channelName, Consumer<List> operator, ZoomWebHook handler) {
        handler.subscribe(event, channelName, operator);
    }
}