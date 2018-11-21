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
        if (this.ridersArriveHigh - this.ridersArriveLow < 0) {
            System.out.println("can't have negative ridersArrive");
        }
        return this.ridersArriveLow + Math.round( (float) (Math.random() * (this.ridersArriveHigh - this.ridersArriveLow)));
    }

    public int unloadPassengersfromBus() {
        if (this.ridersOffHigh - this.ridersOffLow < 0) {
            System.out.println("can't have negative ridersOff");
        }
        return this.ridersOffLow + Math.round((float) (Math.random() * (this.ridersOffHigh - this.ridersOffLow)));
    }

    public int loadPassengersfromStop() {
        if (this.ridersOnHigh - this.ridersOnLow < 0) {
            System.out.println("can't have negative ridersOn");
        }
        return this.ridersOnLow + Math.round( (float) (Math.random() * (this.ridersOnHigh - this.ridersOnLow)));
    }

    public int passengersDepartStop() {
        if (this.ridersDepartHigh - this.ridersDepartLow < 0) {
            System.out.println("can't have negative ridersDepart");
        }
        return this.ridersDepartLow + Math.round( (float) (Math.random() * (this.ridersDepartHigh - this.ridersDepartLow)));
    }
}
