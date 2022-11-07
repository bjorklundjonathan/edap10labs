package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class TemperatureController extends ActorThread<WashingMessage> {

    // TODO: add attributes
    WashingIO io;
    int temp = 0;

    public TemperatureController(WashingIO io) {
        this.io = io;
    }

    @Override
    public void run() {
        while(true) {
            try {
                WashingMessage wMessage = receive();
                switch(wMessage.getOrder()) {
                    case TEMP_IDLE:
                        temp = 0;
                        break;
                    case TEMP_SET_40:
                        temp = 40;
                        break;
                    case TEMP_SET_60:
                        temp = 60;
                        break;
                    default:
                        break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            io.heat(temp > io.getTemperature());
        }
    }
}
