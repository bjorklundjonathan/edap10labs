package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class WaterController extends ActorThread<WashingMessage> {

    // TODO: add attributes
    WashingIO io;
    boolean fill = false;
    boolean drain = false;

    public WaterController(WashingIO io) {
        this.io = io;
    }
    /*  SPIN_OFF,
        SPIN_SLOW,
        SPIN_FAST,
        TEMP_IDLE,
        TEMP_SET_40,
        TEMP_SET_60,
        WATER_IDLE,
        WATER_FILL,
        WATER_DRAIN,
        ACKNOWLEDGMENT */

    @Override
    public void run() {
        while(true) {
            try {
                double wantedLevel;
                WashingMessage wMessage = receive();
                switch(wMessage.getOrder()) {
                    case WATER_IDLE:
                        fill = false;
                        drain = false;
                        break;
                    case WATER_DRAIN:
                        fill = false;
                        drain = true;
                        break;
                    case WATER_FILL:
                        fill = true;
                        drain = false;
                        break;
                    default:
                        break;
                }
                io.drain(drain);
                io.fill(fill);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
        }
    }
}
