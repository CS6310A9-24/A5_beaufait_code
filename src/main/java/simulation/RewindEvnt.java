package simulation;

public class RewindEvnt {
    private int id;
    private int busId;
    private int rank;
    private String type;
    private int stopIndex;

    // RewindEvnt Constructor
    public RewindEvnt(int id, int busId, int rank, String type, int stopIndex) {
        this.id = id;
        this.busId = busId;
        this.rank = rank;
        this.type = type;
        this.stopIndex = stopIndex;
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
