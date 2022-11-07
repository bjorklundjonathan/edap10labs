package actor;

import java.util.LinkedList;

public class ActorThread<M> extends Thread {

    LinkedList<M> queue = new LinkedList<>();

    /** Called by another thread, to send a message to this thread. */
    public synchronized void send(M message) {
        queue.addLast(message);

        notifyAll();
        // TODO: implement this method (one or a few lines)
    }
    
    /** Returns the first message in the queue, or blocks if none available. */
    protected synchronized M receive() throws InterruptedException {
        if(queue.isEmpty()) {
            wait();
        }
        // TODO: implement this method (one or a few lines)
        return queue.poll();
    }
    
    /** Returns the first message in the queue, or blocks up to 'timeout'
        milliseconds if none available. Returns null if no message is obtained
        within 'timeout' milliseconds. */
    protected synchronized M receiveWithTimeout(long timeout) throws InterruptedException {
        // TODO: implement this method (one or a few lines)
        if(queue.isEmpty()) {
            wait(timeout);
        }
        return queue.pollFirst();
    }
}