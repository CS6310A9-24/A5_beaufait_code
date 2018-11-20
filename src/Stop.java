public class Stop {
    // Declare Stop attributes
    private int id;
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
}
