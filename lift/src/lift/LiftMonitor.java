package lift;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LiftMonitor {
    
    LiftView view;
    int currentFloor = 0;
    int inLift = 0;
    int entering = 0;
    boolean doorsOpen = false;
    List<Passenger> passengersMoving = new LinkedList<>();
    LinkedList<Integer> floorQueue = new LinkedList<>();
    Map<Integer, Boolean> goingtTo = new HashMap<Integer, Boolean>();

    private int[] toEnter = new int[] {0, 0, 0, 0, 0, 0 , 0};
    private int[] toExit = new int[] {0, 0, 0, 0, 0, 0, 0};

    Lift lift;
    int NBR_FLOORS;

    public LiftMonitor(LiftView view, int NBR_FLOORS) {
        this.view = view;
        view.openDoors(currentFloor);
        this.NBR_FLOORS = NBR_FLOORS;

        for(int i = 0 ; i < NBR_FLOORS ; i++) {
            goingtTo.put(i, false);
        }
    }
    
    public synchronized int current() {
        return currentFloor;
    }

    public synchronized void updateCurret(int n) {
        currentFloor = n;
        notifyAll();
    }

    public synchronized int blockGetNext() throws InterruptedException {
        while(floorQueue.isEmpty()){
            wait();
        }
        int a = floorQueue.removeLast();
        for(int i = 0 ; i < a-currentFloor ; i++) {
            if(goingtTo.get(i)) {
                goingtTo.put(i, false);
                return i;
            }
        }
        return a;
    }

    public synchronized void openDoors(int floor) {
        view.openDoors(floor);
        doorsOpen = true;
    }

    public synchronized void floorAppend(int f) {
        floorQueue.addFirst(f);
        goingtTo.put(f, true);
        notifyAll();
    }

    public synchronized void waitForPassengers(int floor) throws InterruptedException {
        while((toEnter[floor] > 0 && inLift < 4) || toExit[floor] > 0 || entering > 0) {
            //System.out.println("waiting for pass");
            wait();
        }
        //System.out.println("done waiting");
    }

    public synchronized void updateDoors(boolean bool) {
        doorsOpen = bool;
        notifyAll();
    }

    public synchronized void addToEnterExit(Passenger passenger) {
        toEnter[passenger.getStartFloor()]++;
        notifyAll();
    }

    public synchronized void blockLoad(Passenger passenger) throws InterruptedException {
        while(!(doorsOpen && passenger.getStartFloor() == currentFloor && inLift < 4)) {
            wait();
        }
        inLift++;
        entering++;
        //System.out.println(doorsOpen + " " + passenger.getStartFloor() + " " + currentFloor);
    }

    public synchronized void loadPassenger(Passenger passenger) throws InterruptedException {
        toEnter[passenger.getStartFloor()]--;
        toExit[passenger.getDestinationFloor()]++;
        entering--;
        notifyAll();
    }

    public synchronized void checkHalt() throws InterruptedException {
        while(Arrays.stream(toEnter).sum() + Arrays.stream(toExit).sum() == 0) {
            wait();
        }
    }

    public synchronized boolean open(int floor) {
        return (toEnter[floor] > 0 && inLift < 4) || toExit[floor] > 0;
    }

    public synchronized void blockExit(Passenger passenger) throws InterruptedException {
        while(!(doorsOpen && passenger.getDestinationFloor() == currentFloor)) {
            wait();
        }
    }

    public synchronized void exitPassenger(Passenger passenger) throws InterruptedException {
        inLift--;
        toExit[passenger.getDestinationFloor()]--;
        notifyAll();
    }




}
