import java.util.ArrayList;

public class Queue {
    // Declare Queue attributes
    public ArrayList<Evnt> listEvents;
    public int currentEventId;

    // Queue Constructor
    public Queue() {
        this.currentEventId = -99;
        this.listEvents =  new ArrayList<>();
    }
    // Queue Methods
    public void addEventToPool(int eventIndex, int eventRank, String eventType, int objectId) {
        this.listEvents.add(eventIndex, new Evnt(eventIndex, eventRank, eventType, objectId));
        return;
    }
    public void chooseNextEvent() {
        int lowestRank = 10000000;
        int lowestEventId = -99;
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
                lowestDistance = Main.buses.get(lowestBusId).calculateDistance();
                lowestTime = Main.buses.get(lowestBusId).calculateTravelTime(lowestDistance);
                compareBusId = this.listEvents.get(compareEventId).getBusId();
                compareDistance = Main.buses.get(compareBusId).calculateDistance();
                compareTime = Main.buses.get(compareBusId).calculateTravelTime(compareDistance);
                if (lowestTime > compareTime) {
                    lowestRank = compareRank;
                    lowestEventId = compareEventId;
                }
            }
        }
        this.currentEventId = lowestEventId;
        return;
    }
    public void updateEventExecutionTimes(int eventIndex, int eventRank){
        this.listEvents.get(eventIndex).setRank(eventRank);
        int bus_id = this.listEvents.get(eventIndex).getBusId();
        int route_id = Main.buses.get(bus_id).getRouteId();
        if((Main.buses.get(bus_id).getRouteIndex() + 1)>= Main.routes.get(route_id).getListStopIds().size()){
            Main.buses.get(bus_id).setRouteIndex(0);
        } else {
            Main.buses.get(bus_id).setRouteIndex((Main.buses.get(bus_id).getRouteIndex() + 1));
        }
        double efficiency = Main.system_efficiency();
        return;
    }
}
