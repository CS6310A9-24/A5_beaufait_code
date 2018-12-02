package simulation;

public class BusRouteChange extends BusChange {
    private int new_route_id;
    private int new_route_index;
    public BusRouteChange(ChangeType type, int bus_id, int new_route_id, int new_route_index) {
        super(type, bus_id);
        this.new_route_id = new_route_id;
        this.new_route_index = new_route_index;
    }
    public int getNewRouteId() {
        return this.new_route_id;
    }
    public int getNewRouteIndex() {
        return this.new_route_index;
    }
}
