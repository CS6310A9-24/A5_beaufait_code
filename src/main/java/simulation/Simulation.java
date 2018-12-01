package simulation;

import simulation.ui.UserInterface;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Simulation {

    public static Map<Integer, Bus> buses = new HashMap<>();
    public static Map<Integer, Stop> stops = new HashMap<>();
    public static Map<Integer, Route> routes = new HashMap<>();
    public static List<BusChange> bus_changes = new ArrayList<>();
    public static Efficiency efficiency = new Efficiency();

    public int current_bus_processing, next_stop_id, next_time, next_passengers;
    public double next_distance;

    public int event_index = -1;
    public Queue queue;


    private UserInterface ui;

    public Simulation() {

        ui = new UserInterface(this);
        queue = new Queue(this);
    }

    public void setup(String[] args) {

        final String DELIMITER = ",";
        String scenarioFile = args[0];
        // Step 1: Read the data from the provided scenario configuration file.
        try {
            Scanner takeCommand = new Scanner(new File(scenarioFile));
            String[] tokens;
            do {
                String userCommandLine = takeCommand.nextLine();
                tokens = userCommandLine.split(DELIMITER);
                // Set up scenario.
                switch (tokens[0]) {
                    case "add_stop":
                        int stop_index = Integer.parseInt((tokens[1]));
                        stops.put(stop_index, new Stop(Integer.parseInt(tokens[1]), tokens[2],
                                Integer.parseInt(tokens[3]), Double.parseDouble(tokens[4]),
                                Double.parseDouble(tokens[5])));
                        ui.add_stop_GUI(stop_index);
                        break;
                    case "add_route":
                        int route_index = Integer.parseInt(tokens[1]);
                        routes.put(route_index, new Route(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]),
                                tokens[3]));
                        break;
                    case "extend_route":
                        route_index = Integer.parseInt(tokens[1]);
                        routes.get(route_index).addStopIdtoRoute(Integer.parseInt(tokens[2]));
                        break;
                    case "add_bus":
                        int bus_index = Integer.parseInt(tokens[1]);
                        buses.put(bus_index, new Bus(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]),
                                Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]),
                                Integer.parseInt(tokens[5]), Integer.parseInt(tokens[6]),
                                Integer.parseInt(tokens[7]), Double.parseDouble(tokens[8])));
                        ui.add_bus_GUI(bus_index);
                        break;
                    case "add_event":
                        ++event_index;
                        queue.addEventToPool(event_index, Integer.parseInt(tokens[1]), tokens[2],
                                Integer.parseInt(tokens[3]));
                        break;
                    default:
                        System.out.println(" command not recognized");
                        break;
                }
            } while (takeCommand.hasNextLine());
            takeCommand.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println();
        }


        // Step 2: Read the data from the provided passenger probabilities file
        final String PASSENGER_PROBABILITY_DELIMITER = ",";
        String probabilityFile = args[1];
        try {
            Scanner takeCommand = new Scanner(new File(probabilityFile));
            String[] tokens;
            int cnt = 0;
            Set<Integer> unordered_stop_ids = stops.keySet();
            List<Integer> stop_ids_sorted = unordered_stop_ids.stream().sorted().collect(Collectors.toList());
            // read in the passenger probabilities for each stop in this scenario,  O(n = number of lines in prob file) runtime
            while (takeCommand.hasNextLine() && cnt < stop_ids_sorted.size()) {
                String userCommandLine = takeCommand.nextLine();
                tokens = userCommandLine.split(PASSENGER_PROBABILITY_DELIMITER);
                if (stops.containsKey(Integer.parseInt(tokens[0]))) {
                    //Initialize passenger probabilities
                    stops.get(Integer.parseInt(tokens[0])).setStopDistributions(
                            Integer.parseInt(tokens[1]),
                            Integer.parseInt(tokens[2]),
                            Integer.parseInt(tokens[3]),
                            Integer.parseInt(tokens[4]),
                            Integer.parseInt(tokens[5]),
                            Integer.parseInt(tokens[6]),
                            Integer.parseInt(tokens[7]),
                            Integer.parseInt(tokens[8]));
                    cnt = cnt + 1;
                }
            }
            takeCommand.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println();
        }
    }

    // process bus changes on the current moving bus
    // pre:: bus_id is the id of the current moving bus
    // each bus change is stored in a unique BusChange object
    // post:: applies the change to the current moving bus and deletes all the BusChange objects so that no change is applied more than once
    public void evaluateChanges(int bus_id) {
        for (BusChange change : bus_changes) {
            if (change.getBus_id() == bus_id) {
                BusChange.ChangeType type = change.getChangeType();
                switch (type) {
                    case SPEED:
                        BusSpeedChange speedChange = (BusSpeedChange) change;
                        buses.get(bus_id).setSpeed(speedChange.getNewSpeed());
                        break;
                    case CAPACITY:
                        BusCapacityChange capacityChange = (BusCapacityChange) change;
                        buses.get(bus_id).setCapacity(capacityChange.getNewCapacity());
                        break;
                    case ROUTE:
                        BusRouteChange routeChange = (BusRouteChange) change;
                        buses.get(bus_id).changeRoute(routeChange.getNewRouteId(), routeChange.getNewRouteIndex());
                        // DO NOT ALLOW THE CLIENT TO REQUEST REPLAY AFTER A BUS ROUTE CHANGE
                        queue.replay_flag = false;
                        queue.rewindList.clear();
                        break;
                }
            }
        }
        // remove all BusChange objects applied to the current moving bus from the bus_changes list so they are not processed more than once
        for (BusChange change : bus_changes) {
            if (change.getBus_id() == bus_id) {
                bus_changes.remove(change);
            }
        }
    }

    public void create_rewind_event(int eventIndex) {
        int bus_id = queue.listEvents.get(eventIndex).getBusId();
        int old_rank = queue.listEvents.get(eventIndex).getRank();
        String type = queue.listEvents.get(eventIndex).getType();
        int stop_index = buses.get(bus_id).getRouteIndex();
        int route_id = buses.get(bus_id).getRouteId();
        int stop_id = routes.get(route_id).getStopIdByIndex(stop_index);
        Bus bus = buses.get(bus_id);
        Stop stop = stops.get(stop_id);
        queue.addRewindEvnt(eventIndex, bus_id, old_rank, type, stop_index, stop.getNumPassengersWaiting(), bus.getNumPassengersRiding(), bus.getMaxCapacity(),
                bus.getAvgSpeed());

        // Set to true so that rewind() can now be requested by the client
        queue.replay_flag = true;
    }

    public void updateBusStop(Bus bus) {
        if (bus.getRouteId() == bus.getNewRouteId()) { //client has not changed route since last move_bus event was processed
            // make routeIndex and newRouteIndex equal to the index of the next stop on the current route
            if ((bus.getRouteIndex() + 1) >= routes.get(bus.getRouteId()).getListStopIds().size()) {
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
    public void execute_next(){
        queue.chooseNextEvent();
        current_bus_processing = queue.listEvents.get(queue.currentEventId).getBusId();
        Bus bus = buses.get(current_bus_processing);

        // Store the original state of the bus in the rewind list
        create_rewind_event(queue.currentEventId);

        // Step 4: update bus changes (if any)
        evaluateChanges(current_bus_processing);
        // Step 5: Do passenger exchange at this stop
        passengerExchange(bus, bus.getCurrentStop());
        // Step 3: Determine which stop the bus will travel to next (based on the current location and route)
        next_stop_id = buses.get(current_bus_processing).getNextStop();

        // Step 4: Calculate the distance and travel time between the current and next stops
        next_distance = buses.get(current_bus_processing).calculateDistance(next_stop_id);
        next_time = buses.get(current_bus_processing).calculateTravelTime(next_distance) +
                queue.listEvents.get(queue.currentEventId).getRank();
        // Step 5: Display the output line of text to the display
        next_passengers = buses.get(current_bus_processing).getNumPassengersRiding();
        System.out.println("b:"+current_bus_processing +"->s:"+next_stop_id+"@"+next_time+"//p:"+next_passengers+"/f:0");

        // Update the bus route index
        updateBusStop(bus);

        // Step 6: Update system state and generate new events as needed.
        ui.move_bus();
        queue.updateEventExecutionTimes(queue.currentEventId, next_time);
        double efficiency_value = efficiency.system_efficiency();
        String efficiency_value_txt = String.valueOf(efficiency_value);
        ui.updateSystemEfficiency(efficiency_value_txt);
    }

    // pre: bus route has not changed
    public void rewind() {
        if (!queue.replay_flag) {
            System.out.println("Cannot rewind: either you (1) changed the route, (2) tried to rewind more than the number of times you moved buses OR (2) you tried to rewind more than three consecutive times");
            return;
        }
        queue.choosePreviousEvent(); //restore old time
        RewindEvnt rewindEvnt = queue.rewindList.get(0);
        current_bus_processing = rewindEvnt.getBusId();
        Bus bus = buses.get(current_bus_processing);
        Stop stop = bus.getCurrentStop();

        // restore the historic state of the bus and stop
        // undo evaluateChanges
        bus.setAvgSpeed(rewindEvnt.getOldSpeed());
        bus.setCapacity(rewindEvnt.getOldCapacity());

        //undo passengerExchange
        bus.setNumPassengersRiding(rewindEvnt.getOldNumPassengersOnBus());
        stop.setNumPassengersWaiting(rewindEvnt.getOldNumPassengersAtStation());

        // restore previous bus stop
        bus.setRouteIndex(rewindEvnt.getStopIndex());

        //restore the old class attributes so that ui.move_bus() updates the UI to match previous state
        next_stop_id = routes.get(bus.getRouteId()).getStopIdByIndex(rewindEvnt.getStopIndex());
        next_time = rewindEvnt.getRank();
        next_passengers = buses.get(current_bus_processing).getNumPassengersRiding();

        // Step 5: Display the output line of text to the display
        System.out.println("b:"+current_bus_processing +"->s:"+next_stop_id+"@"+next_time+"//p:"+next_passengers+"/f:0");

        // Step 6: Update system state and generate new events as needed.
        ui.move_bus();
        // event's time was already restored to previous event time in call to choosePreviousEvent()
        double efficiency_value = efficiency.system_efficiency();
        String efficiency_value_txt = String.valueOf(efficiency_value);
        ui.updateSystemEfficiency(efficiency_value_txt);


        // remove the just applied RewindEvnt object from the rewind list
        int ind = 0;
        queue.rewindList.remove(ind);
        if (queue.rewindList.isEmpty()) {
            queue.replay_flag = false;
        }
    }

    public void passengerExchange(Bus bus, Stop stop) {
        int numPassengersWaiting = stop.getNumPassengersWaiting();
        System.out.println("numPassengersWaiting at station: " + numPassengersWaiting);

        // passengers arrive
        numPassengersWaiting += stop.newPassengersArrive();
        System.out.println("numPassengersWaiting at station after passengers arrive: " + numPassengersWaiting);

        // numPassengersTransfers passengers get off the bus
        int bus_capacity = bus.getMaxCapacity();
        int numPassengersTransfers = stop.unloadPassengersfromBus(); // num that should get off bus
        System.out.println("num should get off bus: " + numPassengersTransfers);
        int numPassengersRiding = bus.getNumPassengersRiding(); //num actually on bus
        System.out.println("num actually on bus: " + numPassengersRiding);
        if (numPassengersTransfers >  numPassengersRiding) { // only as many riders on the bus can get off
            numPassengersTransfers = numPassengersRiding;
        }
        if (numPassengersRiding - numPassengersTransfers > bus_capacity) { // no more than bus_capacity can stay on the bus
            numPassengersTransfers  = numPassengersRiding - bus_capacity;
        }
        System.out.println("actual num getting off bus: " + numPassengersTransfers);
        bus.subtractRiders(numPassengersTransfers);

        // passengers get on the bus
        int num_riders_on = stop.loadPassengersfromStop(); //num that should get on the bus
        if (num_riders_on > numPassengersWaiting) { // only as many people in the waiting group can get on the bus
            num_riders_on = numPassengersWaiting;
        }
        int num_avail_seats = bus.getNumAvailableSeats();
        System.out.println("num_avail_seats: " + num_avail_seats);
        if (num_riders_on > num_avail_seats) { //no more than num_avail_seats can get on the bus
            num_riders_on = num_avail_seats;
        }
        System.out.println("actual num getting on bus: " + num_riders_on);


        numPassengersWaiting -= num_riders_on; // subtract from num waiting group
        System.out.println("num waiting at station after passengers get on bus: " + numPassengersWaiting);

        bus.addRiders(num_riders_on); // add riders to the bus
        System.out.println("num riders on bus after people get on: " + bus.getNumPassengersRiding());


        // passengers depart station
        int total_people_at_station = numPassengersTransfers + numPassengersWaiting;
        System.out.println("total people at station: " + total_people_at_station);

        int num_passengers_depart = stop.passengersDepartStop();
        System.out.println("number of people that should depart: " + num_passengers_depart);

        if (num_passengers_depart > total_people_at_station) { // no more than the number of people at the station can depart the station
            num_passengers_depart = total_people_at_station;
        }
        System.out.println("number of people that depart: " + num_passengers_depart);
        if (num_passengers_depart <= numPassengersTransfers) { // transfer group is absorbed by waiting group
            numPassengersTransfers -= num_passengers_depart;
            numPassengersWaiting += numPassengersTransfers;
        } else { // some people in waiting group depart the station
            numPassengersWaiting -= num_passengers_depart - numPassengersTransfers;
        }

        // set the new number of waiters at the Stop
        if (numPassengersWaiting < 0) {
            throw new RuntimeException("numPassengersWaiting is negative!");
        }
        System.out.println("number of people at station: " + numPassengersWaiting + "\n");
        stop.setNumPassengersWaiting(numPassengersWaiting);
    }

    public static Map<Integer, Bus> getBuses() {
        return buses;
    }

    public static void setBuses(Map<Integer, Bus> buses) {
        Simulation.buses = buses;
    }

    public static Map<Integer, Stop> getStops() {
        return stops;
    }

    public static void setStops(Map<Integer, Stop> stops) {
        Simulation.stops = stops;
    }

    public static Map<Integer, Route> getRoutes() {
        return routes;
    }

    public static void setRoutes(Map<Integer, Route> routes) {
        Simulation.routes = routes;
    }

    public int getCurrent_bus_processing() {
        return current_bus_processing;
    }

    public void setCurrent_bus_processing(int current_bus_processing) {
        this.current_bus_processing = current_bus_processing;
    }

    public int getNext_stop_id() {
        return next_stop_id;
    }

    public void setNext_stop_id(int next_stop_id) {
        this.next_stop_id = next_stop_id;
    }

    public int getNext_time() {
        return next_time;
    }

    public void setNext_time(int next_time) {
        this.next_time = next_time;
    }

    public int getNext_passengers() {
        return next_passengers;
    }

    public void setNext_passengers(int next_passengers) {
        this.next_passengers = next_passengers;
    }

    public double getNext_distance() {
        return next_distance;
    }

    public void setNext_distance(double next_distance) {
        this.next_distance = next_distance;
    }

    public int getEvent_index() {
        return event_index;
    }

    public void setEvent_index(int event_index) {
        this.event_index = event_index;
    }

    public Queue getQueue() {
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public UserInterface getUi() {
        return ui;
    }

    public void setUi(UserInterface ui) {
        this.ui = ui;
    }
}
