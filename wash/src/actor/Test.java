package actor;

public class Test {

    public static void main(String[] args) {
        ActorThread<String> a = new ActorThread<>();
        ActorThread<String> b = new ActorThread<>();

        a.send("1");
        a.send("2");
        b.send("10");

        try {
            System.out.println(a.receive() + a.receive() + b.receive());
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /*
     * TEMP_IDLE
     * wp
     * temp
     *  SPIN_OFF, wp -> spin
        SPIN_SLOW,
        SPIN_FAST,
        TEMP_IDLE, wp -> temp
        TEMP_SET_40,
        TEMP_SET_60,
        WATER_IDLE, wp -> water
        WATER_FILL, 
        WATER_DRAIN,
        ACKNOWLEDGMENT alla -> alla
     * 
     * WATER_DRAIN
     * wp
     * water
     * 
     * WATER_IDLE
     * wp
     * water
     * 
     * SPIN_OFF
     * wp
     * spin
     * 
     * yes they do
     */

}
