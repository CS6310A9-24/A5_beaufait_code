package simulation;

import simulation.ui.UserInterface;

public class Bus {
    // Declare Bus attributes
    private int id;
    private int routeId;
    private int routeIndex;
    private int numPassengersRiding;
    private int numPassengersUnloading;
    private int numPassengersLoading;
    private int maxCapacity;
    private double avgSpeed;
    private int travelTimeNextStop;
    private double distanceNextStop;
    private boolean isMoving;
    private boolean isLoading;
    private int stopId;
    private int initalFuel;
    private int fuelCapacity;
    private int previousRouteIndex;
    private Simulation simulation;
    private int isFirstStop;

    // Bus Constructor
    public Bus(int bus_id, int route_id, int route_index, int initial_passengers, int max_capacity,
               int initial_fuel, int fuel_capacity, double speed) {
        this.id = bus_id;
        this.routeId = route_id;
        this.routeIndex = route_index;
        this.numPassengersRiding = initial_passengers;
        this.numPassengersUnloading = 0;
        this.numPassengersLoading = 0;
        this.isMoving = false;
        this.isLoading = false;
        this.maxCapacity = max_capacity;
        this.initalFuel = initial_fuel;
        this.fuelCapacity = fuel_capacity;
        this.avgSpeed = speed;
        this.stopId = simulation.routes.get(this.routeId).getStopIdByIndex(this.routeIndex);
        this.previousRouteIndex = 0;
        this.isFirstStop = 1;
    }

    // Access methods
    public int getRouteId() {
        return this.routeId;
    }

    public int getRouteIndex() {
        return this.routeIndex;
    }

    public int getNumPassengersRiding() {
        return this.numPassengersRiding;
    }

    public int getMaxCapacity() {
        return this.maxCapacity;
    }

    public double getAvgSpeed() {
        return this.avgSpeed;
    }

    public int getNextStop() {
        int route_size = simulation.routes.get(this.routeId).getListStopIds().size();
        if (this.routeIndex == (route_size - 1)) {
            return simulation.routes.get(this.routeId).getStopIdByIndex((0));
        } else {
            return simulation.routes.get(this.routeId).getStopIdByIndex((this.routeIndex + 1));
        }
    }

    public void setRouteIndex(int index) {
        this.routeIndex = index;
    }

    public void setPrevRouteIndex(int index) {
        this.previousRouteIndex = index;
    }

    public int getPreviousRouteIndex() {
        if(isFirstStop == 1) {
            this.isFirstStop = 0;
            return this.routeIndex;
        }else if(this.routeIndex == 0){
            this.routeIndex = simulation.routes.get(this.routeId).getListStopIds().size() - 1;
            return this.routeIndex;
        }else{
            return this.routeIndex - 1;
        }
    }

    // Methods
    public double calculateDistance() {
        int current_stopID, next_stopID;
        double[] current_location, next_location;
        int route_size = simulation.routes.get(this.routeId).getListStopIds().size();
        if (this.routeIndex == route_size - 1) {
            current_stopID = simulation.routes.get(this.routeId).getStopIdByIndex(this.routeIndex);
            next_stopID = simulation.routes.get(this.routeId).getStopIdByIndex((0));
        } else {
            current_stopID = simulation.routes.get(this.routeId).getStopIdByIndex(this.routeIndex);
            next_stopID = simulation.routes.get(this.routeId).getStopIdByIndex((this.routeIndex + 1));
        }
        current_location = simulation.stops.get(current_stopID).getLocation();
        next_location = simulation.stops.get(next_stopID).getLocation();
        this.distanceNextStop = 70.0 * Math.sqrt((Math.pow((next_location[0] - current_location[0]), 2) +
                Math.pow((next_location[1] - current_location[1]), 2)));
        return this.distanceNextStop;
    }

    public int calculateTravelTime(double distance) {
        this.travelTimeNextStop = 1 + ((int) distance * 60 / ((int) this.getAvgSpeed()));
        return this.travelTimeNextStop;
    }

    public void setUi(Simulation simulation) {
        this.simulation = simulation;
    }
}
