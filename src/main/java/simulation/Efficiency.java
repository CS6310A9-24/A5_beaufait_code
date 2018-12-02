package simulation;

import java.util.Map;

public class Efficiency {
    public double consts_speed;
    public double consts_waiting;
    public double consts_capacity;
    public double consts_buses;
    public double consts_combined;

    // Constructor
    public Efficiency() {
        this.consts_speed = 1.0;
        this.consts_waiting = 1.0;
        this.consts_capacity = 1.0;
        this.consts_buses = 1.0;
        this.consts_combined = 1.0;
    }
    // Access operations
    public void setConstsSpeed(double newConstsSpeed) {
        this.consts_speed = newConstsSpeed;
        return;
    }
    public void setConstsWaiting(double newConstsWaiting) {
        this.consts_waiting = newConstsWaiting;
        return;
    }
    public void setConstsCapacity(double newConstsCapacity) {
        this.consts_capacity = newConstsCapacity;
        return;
    }
    public void setConstsBuses(double newConstsBuses) {
        this.consts_buses = newConstsBuses;
        return;
    }
    public void setConstsCombined(double newConstsCombined) {
        this.consts_combined = newConstsCombined;
        return;
    }
    // methods
    private static int waiting_passengers(){
        int total_passengers = 0;
        int num_passengers;
        for (Map.Entry<Integer, Stop> stopEntry: Simulation.stops.entrySet()){
            num_passengers = Simulation.stops.get(stopEntry.getKey()).getNumPassengersWaiting();
            total_passengers += num_passengers;
        }
        return total_passengers;
    }
    private double bus_cost(){
        double total_cost = 0;
        double cost;
        for (Map.Entry<Integer, Bus> busEntry: Simulation.buses.entrySet()){
            cost = consts_speed *(Simulation.buses.get(busEntry.getKey()).getAvgSpeed()) +
                    consts_capacity * (Simulation.buses.get(busEntry.getKey()).getMaxCapacity());
            total_cost += cost;
        }
        return total_cost;
    }
    public double system_efficiency(){
        int efficiency_passengers = waiting_passengers();
        double efficiency_cost = bus_cost();
        double efficiency = (consts_waiting * efficiency_passengers) + (consts_buses * efficiency_cost) +
                (consts_combined * efficiency_passengers * efficiency_cost);
        return efficiency;
    }
}