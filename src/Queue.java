import java.util.ArrayList;

public class Queue {
    // Declare Queue attributes
    public ArrayList<Evnt> listEvents;
    public ArrayList<RewindEvnt> rewindList;
    public int currentEventId;
    public int numRewindEvents = 0;

    // Queue Constructor
    public Queue() {
        this.currentEventId = -99;
        this.listEvents =  new ArrayList<>();
        this.rewindList = new ArrayList<>();
    }
    // Queue Methods
    public void addEventToPool(int eventIndex, int eventRank, String eventType, int objectId) {
        this.listEvents.add(eventIndex, new Evnt(eventIndex, eventRank, eventType, objectId));
        return;
    }
    public void addRewindEvnt(int id, int rank, String type, int busId, int numPassengersArrive,
                              int numPassengersDepart, int numPassengersOn, int numPassengersOff){
        if (numRewindEvents <= 2) {
            int index = 0;
            this.rewindList.add(index, new RewindEvnt(id, rank, type, busId, numPassengersArrive, numPassengersDepart,
                    numPassengersOn, numPassengersOff));
            numRewindEvents = numRewindEvents + 1;
        } else if (numRewindEvents == 3) {
            int index = 2;
            this.rewindList.remove(index);
            numRewindEvents = numRewindEvents - 1;
            index = 0;
            this.rewindList.add(index, new RewindEvnt(id, rank, type, busId, numPassengersArrive, numPassengersDepart,
                    numPassengersOn, numPassengersOff));
            numRewindEvents = numRewindEvents + 1;
        }
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
        int oldrank = this.listEvents.get(eventIndex).getRank();
        this.listEvents.get(eventIndex).setRank(eventRank);
        int bus_id = this.listEvents.get(eventIndex).getBusId();
        int route_id = Main.buses.get(bus_id).getRouteId();
        if((Main.buses.get(bus_id).getRouteIndex() + 1)>= Main.routes.get(route_id).getListStopIds().size()){
            Main.buses.get(bus_id).setRouteIndex(0);
        } else {
            Main.buses.get(bus_id).setRouteIndex((Main.buses.get(bus_id).getRouteIndex() + 1));
        }
        double efficiency = Main.system_efficiency();
        String type = this.listEvents.get(eventIndex).getType();
        int numPassengersArrive = 0;
        int numPassengersDepart = 0;
        int numPassengersOn = 0;
        int numPassengersOff = 0;
        addRewindEvnt(eventIndex, oldrank, type, bus_id, numPassengersArrive, numPassengersDepart, numPassengersOn,
                      numPassengersOff);

        //DEBUG
        for(int i = 0; i < this.rewindList.size(); i++) {
            int current_bus_processing = this.rewindList.get(i).getBusId();
            int next_stop_id = 0;
            System.out.println("b:" + current_bus_processing + "->s:" + next_stop_id );
        }
        return;
    }
}
