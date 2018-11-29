package simulation;

public class Event {
    // Declare Event attributes
    private int id;
    private int rank;
    private String type;
    private int busId;

    // Event Constructor
    public Event(int id, int time, String evnt_type, int object_id) {
        this.id = id;
        this.rank = time;
        this.type = evnt_type;
        this.busId = object_id;
    }
    // Access methods
    public int getId() {
        return id;
    }
    public int getRank() {
        return rank;
    }
    public void setRank(int new_rank){
        this.rank = new_rank;
    }
    public String getType() {
        return type;
    }
    public int getBusId() {
        return busId;
    }
}
