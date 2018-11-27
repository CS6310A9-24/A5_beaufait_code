public class BusSpeedChange extends BusChange {
    private double new_speed;

    public BusSpeedChange(ChangeType type, int bus_id, double new_speed) {
        super(type, bus_id);
        this.new_speed = new_speed;
    }
    
    public double getNewSpeed() {
        return this.new_speed;
    }
}
