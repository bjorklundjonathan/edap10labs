package lift;

public class Lift extends Thread {
    

    LiftMonitor monitor;
    LiftView view;
    int current = 0;
    int direction = 1;

    public Lift(LiftMonitor monitor, LiftView view) {
        this.monitor = monitor;
        this.view = view;
    }


    public void run() {
        view.closeDoors();
        monitor.updateDoors(false);
        while(true) {
            try {
                //int next = monitor.blockGetNext();
                monitor.checkHalt();
                if(current == 6) {
                    direction = -1;
                }
                if(current == 0) {
                    direction = 1;
                }
                int next = current + direction;
                view.moveLift(current, next);
                current = next;
                monitor.updateCurret(next);
                if(monitor.open(current)) {
                    view.openDoors(next);
                    monitor.updateDoors(true);
                    monitor.waitForPassengers(current);
                    view.closeDoors();
                    monitor.updateDoors(false);
                }

            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
