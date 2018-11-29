package simulation;

import java.util.ArrayList;

public class Queue {
    // Declare Queue attributes
    public ArrayList<Event> listEvents;
    public int currentEventId;
    public Simulation simulation;

    // Queue Constructor
    public Queue(Simulation simulation) {
        this.currentEventId = -99;
        this.listEvents = new ArrayList<>();
        this.simulation = simulation;
    }

    // Queue Methods
    public void addEventToPool(int eventIndex, int eventRank, String eventType, int objectId) {
        this.listEvents.add(eventIndex, new Event(eventIndex, eventRank, eventType, objectId));
    }

    public void chooseNextEvent() {
        int lowestRank = 10000000;
        int lowestEventId = Integer.MIN_VALUE;
        int compareRank, compareEventId;
        int lowestBusId, compareBusId;
        double lowestDistance, compareDistance;
        int lowestTime, compareTime;
        for (int j = 0; j < this.listEvents.size(); j++) {
            compareRank = this.listEvents.get(j).getRank();
            compareEventId = this.listEvents.get(j).getId();
            if (compareRank < lowestRank) {
                lowestRank = compareRank;
                lowestEventId = compareEventId;
            } else if (lowestRank == compareRank) {
                lowestBusId = this.listEvents.get(lowestEventId).getBusId();
                lowestDistance = simulation.buses.get(lowestBusId).calculateDistance();
                lowestTime = simulation.buses.get(lowestBusId).calculateTravelTime(lowestDistance);
                compareBusId = this.listEvents.get(compareEventId).getBusId();
                compareDistance = simulation.buses.get(compareBusId).calculateDistance();
                compareTime = simulation.buses.get(compareBusId).calculateTravelTime(compareDistance);
                if (lowestTime > compareTime) {
                    lowestRank = compareRank;
                    lowestEventId = compareEventId;
                }
            }
        }
        this.currentEventId = lowestEventId;
    }

    // final step: make routeId and routeIndex the same as newRouteId and newRouteIndex so that Main can tell if client has changed bus route in future steps
    // pre-condition: routeId and routeIndex may be same or different than newRouteId and newRouteIndex (latter is true if client requested bus route change)
    // post-condition: bus routeId and routeIndex will be equal to newRouteId and newRouteIndex, respectively
    public void updateEventExecutionTimes(int eventIndex, int eventRank) {
        this.listEvents.get(eventIndex).setRank(eventRank);
        int bus_id = this.listEvents.get(eventIndex).getBusId();
        Bus bus = simulation.buses.get(bus_id);
        if (bus.getRouteId() == bus.getNewRouteId()) { //client has not changed route since last move_bus event was processed
            // make routeIndex and newRouteIndex equal to the index of the next stop on the current route
            int route_id = bus.getRouteId();
            if ((bus.getRouteIndex() + 1) >= simulation.routes.get(route_id).getListStopIds().size()) {
                bus.setRouteIndex(0);
                bus.setNewRouteIndex(0);
            } else {
                bus.setRouteIndex((bus.getRouteIndex() + 1));
                bus.setNewRouteIndex((bus.getRouteIndex() + 1));
            }
        } else { //client has changed route since last move_bus event was processed
            // set the bus route and stop index to be newRouteId and newRouteIndex, respectively
            bus.setRouteId(bus.getNewRouteId());
            bus.setRouteIndex(bus.getNewRouteIndex());
        }
    }
}
