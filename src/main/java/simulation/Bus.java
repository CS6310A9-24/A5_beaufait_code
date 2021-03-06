package simulation;

import java.util.ArrayList;
import java.util.List;

public class Bus {
    // Declare Bus attributes
    private int id;
    private int routeId;
    private int routeIndex;
    private int newRouteId;
    private int newRouteIndex;
    private int numPassengersRiding;
    private int numPassengersUnloading;
    private int numPassengersLoading;
    private int maxCapacity;
    private double avgSpeed;
    private int travelTimeNextStop;
    private double distanceNextStop;
    private int stopId;
    private int previousRouteIndex;
    private Simulation simulation;
    private boolean isFirstStop;
    public boolean isNewRte;
    public int lastStopLastRte;
    public List previousStops;

    // Bus Constructor
    public Bus(int bus_id, int route_id, int route_index, int max_capacity, double speed) {
        this.id = bus_id;
        this.routeId = route_id;
        this.routeIndex = route_index;
        this.numPassengersUnloading = 0;
        this.numPassengersLoading = 0;
        this.maxCapacity = max_capacity;
        this.avgSpeed = speed;
        this.stopId = simulation.routes.get(this.routeId).getStopIdByIndex(this.routeIndex);
        this.previousRouteIndex = 0;
        this.isFirstStop = true;
        this.previousStops = new ArrayList();
        this.previousStops.add(this.stopId);

        // If client updates bus route and route index, those values will be reflected in these two attributes.
        // The reason for adding these two extra fields (a.o.t. overriding routeId and routeIndex) is that, for calculating distance and time from bus' current stop to next stop, Main looks at routeId and routeIndex to determine current stop.
        // When a bus is moved, Main compares these two fields to routeId and routeIndex. If they're not equal, Main computes distance and time between current stop and stop along new route
        // If they are equal, Main computes distance and time between current stop and the next stop on the same route.
        this.newRouteId = route_id;
        this.newRouteIndex = route_index;
    }

    // Access methods
    public int getRouteId() {
        return this.routeId;
    }
    public void setRouteId(int routeId) {
        this.routeId = routeId;
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
    public void setRouteIndex(int index) {
        this.routeIndex = index;
    }
    public int getPreviousStopID(){
        int prevStopID = 0;
        if(isFirstStop == true){
            prevStopID = Simulation.routes.get(this.routeId).getStopIdByIndex(this.routeIndex);
        }else {
            prevStopID = (Integer) (this.previousStops.get(this.previousStops.size()-2));
        }
        return prevStopID;
    }
    public int getRewindPrevStopID(){
        int prevStopID = (Integer)(this.previousStops.get(this.previousStops.size()-3));
        this.previousStops.remove(this.previousStops.size() -1);
        return prevStopID;
    }
    public Stop getCurrentStop() {
        int current_stop_id = simulation.routes.get(this.routeId).getStopIdByIndex(this.routeIndex);
        return simulation.stops.get(current_stop_id);
    }
    public int getNextStop() {
        if (newRouteId == routeId) {
            // get next stop along the current route
            int route_size = simulation.routes.get(this.routeId).getListStopIds().size();
            if (this.routeIndex == (route_size - 1)) {
                return simulation.routes.get(this.routeId).getStopIdByIndex((0));
            } else {
                return simulation.routes.get(this.routeId).getStopIdByIndex((this.routeIndex + 1));
            }
        } else { //get the designated stop on the new route
            return simulation.routes.get(this.newRouteId).getStopIdByIndex(this.newRouteIndex);
        }
    }
    public void subtractRiders(int num) {
        if (num < 0) {
            throw new RuntimeException("must subtract a nonnegative number of riders");
        }
        if (num > maxCapacity) {
            throw new RuntimeException("requesting more than maxCapacity riders get off the bus");
        }
        this.numPassengersRiding -= num;
    }
    public void addRiders(int num) {
        if (num < 0) {
            throw new RuntimeException("must add a nonnegative number of riders");
        }
        if (num + this.numPassengersRiding > this.maxCapacity) {
            throw new RuntimeException("adding more riders than the bus can hold");
        }
        this.numPassengersRiding += num;
    }
    public int getNumAvailableSeats() {
        return this.maxCapacity - this.numPassengersRiding;
    }

    // Methods
    public double calculateDistance(int next_stopID) {
        int current_stopID;
        double[] current_location, next_location;
        current_stopID = simulation.routes.get(this.routeId).getStopIdByIndex(this.routeIndex);
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

    // Below methods were added to support bus changes capability
    public void setSpeed(double newSpeed) {
        this.avgSpeed = newSpeed;
    }
    public void setCapacity(int newPassengerCapacity) {
        this.maxCapacity = newPassengerCapacity;
    }
    public void changeRoute(int newRouteId, int newRouteIndex) {
        this.newRouteId = newRouteId;
        this.newRouteIndex = newRouteIndex;
    }
    public int getNewRouteId() {
        return newRouteId;
    }
    public int getNewRouteIndex() {
        return newRouteIndex;
    }
    public void setNewRouteIndex(int newRouteIndex) {
        this.newRouteIndex = newRouteIndex;
    }
    public void setNumPassengersRiding(int numPassengersRiding) {
        this.numPassengersRiding = numPassengersRiding;
    }
    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }
    public Simulation getSimulation() {
        return simulation;
    }
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }
    public boolean getIsFirstStop() {
        return this.isFirstStop;
    }
    public void setIsFirstStop(boolean isFirstStop) {
        this.isFirstStop = isFirstStop;
    }
    public void setNewRte(){ this.isNewRte = true;};
    public void setLastStopLastRte(int stop_id){this.lastStopLastRte = stop_id;}
}
