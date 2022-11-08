package lift;

public class PassengerThread extends Thread {
    

    Passenger pass;
    LiftMonitor monitor;

    public PassengerThread(Passenger pass, LiftMonitor monitor) {
        this.pass = pass;
        this.monitor = monitor;
    }

    public void run() {
        pass.begin();
        monitor.addToEnterExit(pass);
        try {
            monitor.blockLoad(pass);
            pass.enterLift();
            monitor.loadPassenger(pass);
            monitor.blockExit(pass);
            pass.exitLift();
            monitor.exitPassenger(pass);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        pass.end();

    }


}
