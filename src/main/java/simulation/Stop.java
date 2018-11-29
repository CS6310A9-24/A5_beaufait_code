package simulation;

import java.util.Random;

public class Stop {
    // Declare Stop attributes
    private int id;
    private int ridersArriveLow;
    private int ridersArriveHigh;
    private int ridersOffLow;
    private int ridersOffHigh;
    private int ridersOnLow;
    private int ridersOnHigh;
    private int ridersDepartLow;
    private int ridersDepartHigh;

    private String name;
    private int numPassengersWaiting;
    private int numNewPassengerArrivals;
    private int numPassengerDepartures;
    private Location location;

    private static Random random = new Random(); //let all Stop classes share this one Random object

    // Stop Constructor
    public Stop (int stop_id, String stop_name, int initial_riders, double stop_latitude, double stop_longitude){
        this.id = stop_id;
        this.name = stop_name;
        this.numPassengersWaiting = initial_riders;
        this.location = new Location(stop_latitude, stop_longitude);
        this.numNewPassengerArrivals = 0;
        this.numPassengerDepartures = 0;
    }

    // Access methods
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getNumPassengersWaiting() {
        return numPassengersWaiting;
    }
    public double[] getLocation(){
        double[] stopLocation = new double[2];
        stopLocation[0] = location.getLatitude();
        stopLocation[1] = location.getLongitude();
        return stopLocation;
    }

    public void setStopDistributions(int ridersArriveHigh, int ridersArriveLow, int ridersOffHigh, int ridersOffLow,
                                     int ridersOnHigh, int ridersOnLow, int ridersDepartHigh, int ridersDepartLow) {
        if (ridersArriveLow < 0 || ridersOffLow < 0 || ridersOnLow < 0 || ridersDepartLow < 0 ||
            ridersArriveHigh < ridersArriveLow || ridersOffHigh < ridersOffLow || ridersOnHigh < ridersOffLow || ridersDepartHigh < ridersDepartLow) {
            throw new RuntimeException("Can't have negative passenger probability parameters or samples");
        }
        this.ridersArriveHigh = ridersArriveHigh;
        this.ridersArriveLow = ridersArriveLow;
        this.ridersOffHigh = ridersOffHigh;
        this.ridersOffLow = ridersOffLow;
        this.ridersOnHigh = ridersOnHigh;
        this.ridersOnLow = ridersOnLow;
        this.ridersDepartHigh = ridersDepartHigh;
        this.ridersDepartLow = ridersDepartLow;
    }

    public int getRidersArriveLow() {
        return ridersArriveLow;
    }

    public void setRidersArriveLow(int ridersArriveLow) {
        this.ridersArriveLow = ridersArriveLow;
    }

    public int getRidersArriveHigh() {
        return ridersArriveHigh;
    }

    public void setRidersArriveHigh(int ridersArriveHigh) {
        this.ridersArriveHigh = ridersArriveHigh;
    }

    public int getRidersOffLow() {
        return ridersOffLow;
    }

    public void setRidersOffLow(int ridersOffLow) {
        this.ridersOffLow = ridersOffLow;
    }

    public int getRidersOffHigh() {
        return ridersOffHigh;
    }

    public void setRidersOffHigh(int ridersOffHigh) {
        this.ridersOffHigh = ridersOffHigh;
    }

    public int getRidersOnLow() {
        return ridersOnLow;
    }

    public void setRidersOnLow(int ridersOnLow) {
        this.ridersOnLow = ridersOnLow;
    }

    public int getRidersOnHigh() {
        return ridersOnHigh;
    }

    public void setRidersOnHigh(int ridersOnHigh) {
        this.ridersOnHigh = ridersOnHigh;
    }

    public int getRidersDepartLow() {
        return ridersDepartLow;
    }

    public void setRidersDepartLow(int ridersDepartLow) {
        this.ridersDepartLow = ridersDepartLow;
    }

    public int getRidersDepartHigh() {
        return ridersDepartHigh;
    }

    public void setRidersDepartHigh(int ridersDepartHigh) {
        this.ridersDepartHigh = ridersDepartHigh;
    }

    public void setNumPassengersWaiting(int numPassengersWaiting) {
        this.numPassengersWaiting = numPassengersWaiting;
    }

    public int getNumNewPassengerArrivals() {
        return numNewPassengerArrivals;
    }

    public void setNumNewPassengerArrivals(int numNewPassengerArrivals) {
        this.numNewPassengerArrivals = numNewPassengerArrivals;
    }

    public int getNumPassengerDepartures() {
        return numPassengerDepartures;
    }

    public void setNumPassengerDepartures(int numPassengerDepartures) {
        this.numPassengerDepartures = numPassengerDepartures;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int newPassengersArrive() {
        return random.nextInt((this.ridersArriveHigh - this.ridersArriveLow) + 1) + this.ridersArriveLow;
    }

    public int unloadPassengersfromBus() {
        return random.nextInt((this.ridersOffHigh - this.ridersOffLow) + 1) + this.ridersOffLow;
    }

    public int loadPassengersfromStop() {
        return random.nextInt( (this.ridersOnHigh - this.ridersOnLow) + 1) + this.ridersOnLow;
    }

    public int passengersDepartStop() {
        return random.nextInt( (this.ridersDepartHigh - this.ridersDepartLow) + 1) + this.ridersDepartLow;
    }
}
