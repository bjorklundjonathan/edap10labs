package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;
import wash.simulation.WashingSimulator;

public class Wash {

    public static void main(String[] args) throws InterruptedException {
        WashingSimulator sim = new WashingSimulator(Settings.SPEEDUP);
        
        WashingIO io = sim.startSimulation();

        TemperatureController temp = new TemperatureController(io);
        WaterController water = new WaterController(io);
        SpinController spin = new SpinController(io);

        temp.start();
        water.start();
        spin.start();

        while (true) {
            int n = io.awaitButton();
            System.out.println("user selected program " + n);
            ActorThread<WashingMessage> program;

            switch(n) {
                case 0:
                    program = new ActorThread<>();
                    temp.send(new WashingMessage(program, WashingMessage.Order.TEMP_IDLE));
                    water.send(new WashingMessage(program, WashingMessage.Order.WATER_IDLE));
                    spin.send(new WashingMessage(program, WashingMessage.Order.SPIN_OFF));
                    break;
                case 1:
                    program = new WashingProgram1(io, temp, water, spin);
                    break;
                case 2:
                    program = new WashingProgram2();
                    break;
                case 3:
                    program = new WashingProgram3(io, temp, water, spin);
                    break;
                default:
                    program = new ActorThread<>();
                    break;
            }

            program.start();


            // TODO:
            // if the user presses buttons 1-3, start a washing program
            // if the user presses button 0, and a program has been started, stop it
        }
    }
};
