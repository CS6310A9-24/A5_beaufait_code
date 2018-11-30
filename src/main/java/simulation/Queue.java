package simulation;

import java.util.ArrayList;

public class Queue {
    // Declare Queue attributes
    public ArrayList<Event> listEvents;
    public ArrayList<RewindEvnt> rewindList;
    public int currentEventId;
    public Simulation simulation;
    public int numRewindEvents = 0;
    public boolean replay_flag = false;

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
    public void addRewindEvnt(int id, int busId, int rank, String type, int stopIndex, int numPassengersArrive,
                              int numPassengersDepart, int numPassengersOn, int numPassengersOff){
        if (this.rewindList.size() <= 2) {
            int index = 0;
            this.rewindList.add(index, new RewindEvnt(id, busId, rank, type, stopIndex, numPassengersArrive, numPassengersDepart,
                    numPassengersOn, numPassengersOff));
        } else if (this.rewindList.size() == 3) {
            int index = 2;
            this.rewindList.remove(index);
            index = 0;
            this.rewindList.add(index, new RewindEvnt(id, busId, rank, type, stopIndex, numPassengersArrive, numPassengersDepart,
                    numPassengersOn, numPassengersOff));
        }
        return;
    }

    public void chooseNextEvent() {
        if (!replay_flag || this.rewindList.size()<1) {
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
        } else {
            int event_id = this.rewindList.get(0).getId();
            this.listEvents.get(event_id).setRank(this.rewindList.get(0).getRank());
            this.currentEventId = event_id;
        }
        return;
    }

    // final step: make routeId and routeIndex the same as newRouteId and newRouteIndex so that Main can tell if client has changed bus route in future steps
    // pre-condition: routeId and routeIndex may be same or different than newRouteId and newRouteIndex (latter is true if client requested bus route change)
    // post-condition: bus routeId and routeIndex will be equal to newRouteId and newRouteIndex, respectively
    public void updateEventExecutionTimes(int eventIndex, int eventRank) {
        int old_rank = this.listEvents.get(eventIndex).getRank();
        this.listEvents.get(eventIndex).setRank(eventRank);
        int bus_id = this.listEvents.get(eventIndex).getBusId();
        int route_id = simulation.buses.get(bus_id).getRouteId();
        int stop_index = simulation.buses.get(bus_id).getRouteIndex();
        Bus bus = simulation.buses.get(bus_id);
        if (bus.getRouteId() == bus.getNewRouteId()) { //client has not changed route since last move_bus event was processed
            // make routeIndex and newRouteIndex equal to the index of the next stop on the current route
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
        String type = this.listEvents.get(eventIndex).getType();
        int numPassengersArrive = 0;
        int numPassengersDepart = 0;
        int numPassengersOn = 0;
        int numPassengersOff = 0;
        if (!replay_flag) {
            addRewindEvnt(eventIndex, bus_id, old_rank, type, stop_index, numPassengersArrive, numPassengersDepart, numPassengersOn,
                    numPassengersOff);
        } else {
            numRewindEvents = numRewindEvents - 1;
            int ind = 0;
            this.rewindList.remove(ind);
            if (numRewindEvents == 0) {
                replay_flag = false;
            }
        }
        //DEBUG
        for(int i = 0; i < this.rewindList.size(); i++) {
            int current_bus_processing = this.rewindList.get(i).getBusId();
            int this_route_id = simulation.buses.get(current_bus_processing).getRouteId();
            int next_stop_index = this.rewindList.get(i).getStopIndex();
            int next_stop_id = simulation.routes.get(this_route_id).getStopIdByIndex(next_stop_index);
            System.out.println(this_route_id + "b:" + current_bus_processing + "->s:" + next_stop_id);
        }
        return;
    }
}
