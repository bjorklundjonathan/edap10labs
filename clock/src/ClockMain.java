import java.util.concurrent.Semaphore;

import clock.AlarmClockEmulator;
import clock.io.ClockInput;
import clock.io.ClockInput.UserInput;

public class ClockMain {

    public static void main(String[] args) throws InterruptedException {
        AlarmClockEmulator emulator = new AlarmClockEmulator();
        CounterMonitor mainMonitor = new CounterMonitor(emulator.getOutput());
        CountMainClock clock = new CountMainClock(mainMonitor);
        AlarmThread alarm = new AlarmThread(mainMonitor);

        clock.start();
        alarm.start();


        ClockInput  in  = emulator.getInput();
        Semaphore mutex = new Semaphore(1);

        while (true) {
            System.out.println(in.getSemaphore().availablePermits());
            in.getSemaphore().acquire();
            mutex.acquire();
            UserInput userInput = in.getUserInput();
            int choice = userInput.getChoice();
            int h = userInput.getHours();
            int m = userInput.getMinutes();
            int s = userInput.getSeconds();

            switch (choice) {
                case ClockInput.CHOICE_SET_TIME:
                    mainMonitor.setTime(h, m, s);
                    break;
                case ClockInput.CHOICE_SET_ALARM:
                    mainMonitor.setAlarmTime(h, m, s);
                    break;
                case ClockInput.CHOICE_TOGGLE_ALARM:
                    mainMonitor.toggleAlarm();
                    break;
            }

            System.out.println("choice=" + choice + " h=" + h + " m=" + m + " s=" + s);
            mutex.release();
        }
    }
}
