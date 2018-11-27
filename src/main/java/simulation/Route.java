package simulation;

import java.util.ArrayList;
import java.util.List;



public class Route {
    // Declare Route attributes
    private int id;
    private int number;
    private String name;
    private List<Integer> listStopIds;

    // Route Constructor
    public Route (int route_id, int route_number, String route_name) {
        this.id = route_id;
        this.number = route_number;
        this.name = route_name;
        this.listStopIds = new ArrayList<>();
    }
    // Access methods
    public int getId() {
        return id;
    }
    public int getNumber() {
        return number;
    }
    public String getName() {
        return name;
    }
    public List<Integer> getListStopIds() {
        return listStopIds;
    }
    public int getStopIdByIndex(int stop_index) {
        return  listStopIds.get(stop_index);
    }
    // Extend Route
    public void addStopIdtoRoute(int new_stop_id) {
        this.listStopIds.add(new_stop_id);
    }
}
