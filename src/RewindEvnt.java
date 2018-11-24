public class RewindEvnt {
    private int id;
    private int rank;
    private String type;
    private int busId;
    private int numPassengersArrive;
    private int numPassengersDepart;
    private int numPassengersOn;
    private int numPassengersOff;

    // RewindEvnt Constructor
    public RewindEvnt(int id, int rank, String type, int busId, int numPassengersArrive,
                      int numPassengersDepart, int numPassengersOn, int numPassengersOff) {
        this.id = id;
        this.rank = rank;
        this.type = type;
        this.busId = busId;
        this.numPassengersArrive = numPassengersArrive;
        this.numPassengersDepart = numPassengersDepart;
        this.numPassengersOn = numPassengersOn;
        this.numPassengersOff = numPassengersOff;
    }
    public int getBusId () {
        return this.busId;
    }
}
