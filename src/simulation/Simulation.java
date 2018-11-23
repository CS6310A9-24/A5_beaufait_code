package simulation;

import ui.UserInterface;

import java.util.HashMap;
import java.util.Map;

public class Simulation {

    public static Map<Integer, Bus> buses = new HashMap();
    public static Map<Integer, Stop> stops = new HashMap();
    public static Map<Integer, Route> routes = new HashMap();

    public Simulation(String[] args) {

        UserInterface g = new UserInterface();
        g.build_environment(args);
    }

    public void setup() {

    }
}
