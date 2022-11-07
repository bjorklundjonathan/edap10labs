package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;
import wash.control.WashingMessage;
import wash.control.WashingMessage.Order;

public class SpinController extends ActorThread<WashingMessage> {

    // TODO: add attributes
    WashingIO io;
    boolean left = true;

    public SpinController(WashingIO io) {
        this.io = io;
    }

    @Override
    public void run() {
        try {

            // ... TODO ...

            while (true) {
                // wait for up to a (simulated) minute for a WashingMessage
                WashingMessage m = receiveWithTimeout(60000 / Settings.SPEEDUP);
                ActorThread<WashingMessage> sender = m.getSender();

                // if m is null, it means a minute passed and no message was received
                
                if (m != null) {
                    System.out.println("got " + m);
                    switch(m.getOrder()) {
                        case SPIN_SLOW:
                            if(left) {
                                io.setSpinMode(WashingIO.SPIN_LEFT);
                                left = false;
                            } else io.setSpinMode(WashingIO.SPIN_RIGHT);
                            break;
                        case SPIN_FAST:
                            io.setSpinMode(WashingIO.SPIN_FAST);
                            break;
                        case SPIN_OFF:
                            io.setSpinMode(WashingIO.SPIN_IDLE);
                            break;
                        default:
                            break;
                    }
                    sender.send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
                }
                
                // ... TODO ...
            }
        } catch (InterruptedException unexpected) {
            // we don't expect this thread to be interrupted,
            // so throw an error if it happens anyway
            throw new Error(unexpected);
        }
    }
}
