package simulation;

public class Location {
    // Declare Location attributes
    private double latitude;
    private double longitude;

    // Location constructor
    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    // Access methods
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
}
