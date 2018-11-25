public class RewindEvnt {
    private int id;
    private int busId;
    private int rank;
    private String type;
    private int stopIndex;
    private int numPassengersArrive;
    private int numPassengersDepart;
    private int numPassengersOn;
    private int numPassengersOff;

    // RewindEvnt Constructor
    public RewindEvnt(int id, int busId, int rank, String type, int stopIndex, int numPassengersArrive,
                      int numPassengersDepart, int numPassengersOn, int numPassengersOff) {
        this.id = id;
        this.busId = busId;
        this.rank = rank;
        this.type = type;
        this.stopIndex = stopIndex;
        this.numPassengersArrive = numPassengersArrive;
        this.numPassengersDepart = numPassengersDepart;
        this.numPassengersOn = numPassengersOn;
        this.numPassengersOff = numPassengersOff;
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
}
