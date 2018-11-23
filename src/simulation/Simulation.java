package simulation;

import simulation.ui.UserInterface;

import java.util.HashMap;
import java.util.Map;

public class Simulation {

    public static Map<Integer, Bus> buses = new HashMap();
    public static Map<Integer, Stop> stops = new HashMap();
    public static Map<Integer, Route> routes = new HashMap();


    private UserInterface ui;

    public Simulation() {

        ui = new UserInterface();
    }

    public void setup(String[] args) {
        ui.build_environment(args);

    }
}
