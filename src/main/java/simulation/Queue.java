package simulation;

import java.util.ArrayList;

public class Queue {
    // Declare Queue attributes
    public ArrayList<Event> listEvents;
    public ArrayList<RewindEvnt> rewindList;
    public int currentEventId;
    public Simulation simulation;
    public boolean replay_flag = false; //only false if rewindList is empty

    // Queue Constructor
    public Queue(Simulation simulation) {
        this.currentEventId = -99;
        this.listEvents = new ArrayList<>();
        this.rewindList = new ArrayList<>();
        this.simulation = simulation;
    }

    // Queue Methods
    public void addEventToPool(int eventIndex, int eventRank, String eventType, int objectId) {
        this.listEvents.add(eventIndex, new Event(eventIndex, eventRank, eventType, objectId));
    }
    public void addRewindEvnt(int id, int busId, int rank, String type, int stopIndex, int numPassengersAtStation,
                              int numPassengersOnBus, int oldCapacity, double oldSpeed){
        if (this.rewindList.size() <= 2) {
            int index = 0;
            this.rewindList.add(index, new RewindEvnt(id, busId, rank, type, stopIndex, numPassengersAtStation, numPassengersOnBus,
                    oldCapacity, oldSpeed));
        } else if (this.rewindList.size() == 3) {
            int index = 2;
            this.rewindList.remove(index);
            index = 0;
            this.rewindList.add(index, new RewindEvnt(id, busId, rank, type, stopIndex, numPassengersAtStation, numPassengersOnBus,
                    oldCapacity, oldSpeed));
        }
        return;
    }

    public void chooseNextEvent() {
        int lowestRank = 10000000;
        int lowestEventId = Integer.MIN_VALUE;
        int compareRank, compareEventId;
        int lowestBusId, lowestStopId, compareBusId, compareStopId;
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
                lowestStopId = simulation.buses.get(lowestBusId).getNextStop();
                lowestDistance = simulation.buses.get(lowestBusId).calculateDistance(lowestStopId);
                lowestTime = simulation.buses.get(lowestBusId).calculateTravelTime(lowestDistance);
                compareBusId = this.listEvents.get(compareEventId).getBusId();
                compareStopId = simulation.buses.get(compareBusId).getNextStop();
                compareDistance = simulation.buses.get(compareBusId).calculateDistance(compareStopId);
                compareTime = simulation.buses.get(compareBusId).calculateTravelTime(compareDistance);
                if (lowestTime > compareTime) {
                    lowestRank = compareRank;
                    lowestEventId = compareEventId;
                }
            }
        }
        this.currentEventId = lowestEventId;
        return;
    }

    public void choosePreviousEvent() {
        int event_id = this.rewindList.get(0).getId();
        this.listEvents.get(event_id).setRank(this.rewindList.get(0).getRank()); // restore previous time s.t. it has the lowest rank and is the next event picked by choose_event
        this.currentEventId = event_id;
    }

    // final step: make routeId and routeIndex the same as newRouteId and newRouteIndex so that Main can tell if client has changed bus route in future steps
    // pre-condition: routeId and routeIndex may be same or different than newRouteId and newRouteIndex (latter is true if client requested bus route change)
    // post-condition: bus routeId and routeIndex will be equal to newRouteId and newRouteIndex, respectively
    public void updateEventExecutionTimes(int eventIndex, int eventRank) {
        this.listEvents.get(eventIndex).setRank(eventRank);

        //DEBUG
        for(int i = 0; i < this.rewindList.size(); i++) {
            int current_bus_processing = this.rewindList.get(i).getBusId();
            int this_route_id = simulation.buses.get(current_bus_processing).getRouteId();
            int next_stop_index = this.rewindList.get(i).getStopIndex();
            int next_stop_id = simulation.routes.get(this_route_id).getStopIdByIndex(next_stop_index);
        }
        return;
    }


}
