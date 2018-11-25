import java.util.ArrayList;

public class Queue {
    // Declare Queue attributes
    public ArrayList<Evnt> listEvents;
    public ArrayList<RewindEvnt> rewindList;
    public int currentEventId;
    public int numRewindEvents = 0;
    public boolean replay_flag = false;


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
        if (!replay_flag || this.rewindList.size() < 1) {
            int lowestRank = 10000000;
            int lowestEventId = -99;
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
                    int lowest_stop_id = Main.buses.get(lowestBusId).getNextStop();
                    lowestDistance = Main.buses.get(lowestBusId).calculateDistance(lowest_stop_id);
                    lowestTime = Main.buses.get(lowestBusId).calculateTravelTime(lowestDistance);
                    compareBusId = this.listEvents.get(compareEventId).getBusId();
                    int compare_stop_id = Main.buses.get(compareBusId).getNextStop();
                    compareDistance = Main.buses.get(compareBusId).calculateDistance(compare_stop_id);
                    compareTime = Main.buses.get(compareBusId).calculateTravelTime(compareDistance);
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
    public void updateEventExecutionTimes(int eventIndex, int eventRank){
        int old_rank = this.listEvents.get(eventIndex).getRank();
        this.listEvents.get(eventIndex).setRank(eventRank);
        int bus_id = this.listEvents.get(eventIndex).getBusId();
        int route_id = Main.buses.get(bus_id).getRouteId();
        int stop_index = Main.buses.get(bus_id).getRouteIndex();
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
            int this_route_id = Main.buses.get(current_bus_processing).getRouteId();
            int next_stop_index = this.rewindList.get(i).getStopIndex();
            int next_stop_id = Main.routes.get(this_route_id).getStopIdByIndex(next_stop_index);

            System.out.println(this_route_id + "b:" + current_bus_processing + "->s:" + next_stop_id);
        }
        return;
    }
}
