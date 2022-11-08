public class CountMainClock extends Thread {
    
    CounterMonitor monitor;

    public CountMainClock(CounterMonitor monitor) {
        this.monitor = monitor;
    }


    public void run() {
        long t0 = System.currentTimeMillis();
        while (true) {
            
            t0 += 1000;
            long diff = t0 - System.currentTimeMillis();
            try {
                Thread.sleep(diff);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                monitor.increment();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
}
