package simulation;

abstract class BusChange {
    enum ChangeType {
        SPEED, ROUTE, CAPACITY
    }

    private ChangeType type;
    private int bus_id;

    public BusChange(ChangeType type, int bus_id) {
        this.type = type;
        this.bus_id = bus_id;
    }

    public ChangeType getChangeType() {
        return this.type;
    }

    public int getBus_id() {
        return this.bus_id;
    }

}