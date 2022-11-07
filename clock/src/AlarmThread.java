public class AlarmThread extends Thread {
    CounterMonitor monitor;

    public AlarmThread(CounterMonitor monitor) {
        this.monitor = monitor;
    }

    public void run() {
        while(true) {
            try {
                if(monitor.currentTime() == monitor.getAlarmTime()) {
                    monitor.alarm();   
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
}