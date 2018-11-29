package simulation;

public class BusCapacityChange extends BusChange {
    private int new_capacity;

    public BusCapacityChange(ChangeType type, int bus_id, int new_capacity) {
        super(type, bus_id);
        this.new_capacity = new_capacity;
    }

    public int getNewCapacity() {
        return this.new_capacity;
    }
}
