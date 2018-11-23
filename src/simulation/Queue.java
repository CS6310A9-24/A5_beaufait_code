package simulation;

import simulation.ui.UserInterface;

import java.util.ArrayList;

public class Queue {
    // Declare Queue attributes
    public ArrayList<Evnt> listEvents;
    public int currentEventId;
    public UserInterface ui;

    // Queue Constructor
    public Queue(UserInterface ui) {
        this.currentEventId = -99;
        this.listEvents =  new ArrayList<>();
        this.ui = ui;
    }
    // Queue Methods
    public void addEventToPool(int eventIndex, int eventRank, String eventType, int objectId) {
        this.listEvents.add(eventIndex, new Evnt(eventIndex, eventRank, eventType, objectId));
        return;
    }
    public void chooseNextEvent() {
        int lowestRank = 10000000;
        int lowestEventId = Integer.MIN_VALUE;
        int compareRank, compareEventId;
        int lowestBusId, compareBusId;
        double lowestDistance, compareDistance;
        int lowestTime, compareTime;
        for(int j = 0; j < this.listEvents.size(); j++) {
            compareRank = this.listEvents.get(j).getRank();
            compareEventId = this.listEvents.get(j).getId();
            if(compareRank<lowestRank) {
                lowestRank = compareRank;
                lowestEventId = compareEventId;
            } else if (lowestRank == compareRank){
                lowestBusId = this.listEvents.get(lowestEventId).getBusId();
                lowestDistance = ui.buses.get(lowestBusId).calculateDistance();
                lowestTime = ui.buses.get(lowestBusId).calculateTravelTime(lowestDistance);
                compareBusId = this.listEvents.get(compareEventId).getBusId();
                compareDistance = ui.buses.get(compareBusId).calculateDistance();
                compareTime = ui.buses.get(compareBusId).calculateTravelTime(compareDistance);
                if (lowestTime > compareTime) {
                    lowestRank = compareRank;
                    lowestEventId = compareEventId;
                }
            }
        }
        this.currentEventId = lowestEventId;
    }
    public void updateEventExecutionTimes(int eventIndex, int eventRank){
        this.listEvents.get(eventIndex).setRank(eventRank);
        int bus_id = this.listEvents.get(eventIndex).getBusId();
        int route_id = ui.buses.get(bus_id).getRouteId();
        if((ui.buses.get(bus_id).getRouteIndex() + 1)>= ui.routes.get(route_id).getListStopIds().size()){
            ui.buses.get(bus_id).setPrevRouteIndex(ui.buses.get(bus_id).getRouteIndex());
            ui.buses.get(bus_id).setRouteIndex(0);
        } else {
            ui.buses.get(bus_id).setPrevRouteIndex(ui.buses.get(bus_id).getRouteIndex());
            ui.buses.get(bus_id).setRouteIndex((ui.buses.get(bus_id).getRouteIndex() + 1));
        }
    }
}
