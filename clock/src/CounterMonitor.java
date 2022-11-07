import java.util.concurrent.Semaphore;
import clock.io.ClockInput;
import clock.io.ClockInput.UserInput;
import clock.io.ClockOutput;
import java.time.LocalTime;

public class CounterMonitor {

    ClockOutput out;
    int hour;
    int minute;
    int second;
    int aHour;
    int aMinute;
    int aSecond;
    boolean alarmOn = false;
    Semaphore mutex = new Semaphore(1);

    public CounterMonitor(ClockOutput out){
        this.out = out;
        LocalTime now = LocalTime.now();
        hour = now.getHour();
        minute = now.getMinute();
        second = now.getSecond();
    }

    public void increment() throws InterruptedException {
        mutex.acquire();
        second++;
        if(sixty(second)) {
            minute++;
            second = 0;
            if(sixty(minute)) {
                hour++;
                minute = 0;
                if(hour == 24) {
                    hour = 0;
                }
            }
        }
        out.displayTime(hour, minute, second);
        mutex.release();
    }

    private boolean sixty(int t) {
        return t % 60 == 0;
    }

    public int currentTime() throws InterruptedException {
        mutex.acquire();
        String str = String.valueOf(hour) + String.valueOf(minute) + String.valueOf(second);
        mutex.release();
        return Integer.parseInt(str);
        
    }

    public int getAlarmTime() throws InterruptedException {
        mutex.acquire();
        String str = String.valueOf(aHour) + String.valueOf(aMinute) + String.valueOf(aSecond);
        mutex.release();
        return Integer.parseInt(str);
        
    }

    public void setTime(int hour, int minute, int second) throws InterruptedException {
        mutex.acquire();
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        mutex.release();
    }

    public void setAlarmTime(int hour, int minute, int second) throws InterruptedException {
        mutex.acquire();
        aHour = hour;
        aMinute = minute;
        aSecond = second;
        mutex.release();
    }

    public void toggleAlarm() {
        alarmOn = alarmOn ^ true;
        out.setAlarmIndicator(alarmOn);
    }

    public void alarm() {
        out.alarm();
    }
}
