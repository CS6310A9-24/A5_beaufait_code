package simulation;

public class RewindEvnt {
    private int id;
    private int busId;
    private int rank;
    private String type;
    private int stopIndex;
    private int oldNumPassengersAtStation;
    private int oldNumPassengersOnBus;
    private int oldCapacity;
    private double oldSpeed;
    // RewindEvnt Constructor
    public RewindEvnt(int id, int busId, int rank, String type, int stopIndex, int oldNumPassengersAtStation,
                      int oldNumPassengersOnBus, int oldCapacity, double oldSpeed) {
        this.id = id;
        this.busId = busId;
        this.rank = rank;
        this.type = type;
        this.stopIndex = stopIndex;
        this.oldNumPassengersAtStation = oldNumPassengersAtStation;
        this.oldNumPassengersOnBus = oldNumPassengersOnBus;
        this.oldCapacity = oldCapacity;
        this.oldSpeed = oldSpeed;
    }
    public int getId() {
        return this.id;
    }
    public int getBusId () {
        return this.busId;
    }
    public int getStopIndex() {
        return this.stopIndex;
    }
    public int getRank() {
        return this.rank;
    }
    public int getOldNumPassengersAtStation() {
        return oldNumPassengersAtStation;
    }
    public int getOldNumPassengersOnBus() {
        return oldNumPassengersOnBus;
    }
    public int getOldCapacity() {
        return oldCapacity;
    }
    public double getOldSpeed() {
        return oldSpeed;
    }
}
