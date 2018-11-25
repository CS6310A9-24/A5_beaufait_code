import java.util.*;
import java.io.File;
import java.lang.*;
import java.util.stream.Collectors;

public class Main {
    public static Map<Integer, Bus> buses = new HashMap();
    public static Map<Integer,Stop> stops = new HashMap();
    public static Map<Integer,Route> routes = new HashMap();
    public static List<BusChange> bus_changes = new ArrayList<>();
    public static void main(String[] args) {
        // Initialize variables
        int event_index = -1;
        int current_bus_processing, next_stop_id, next_time, next_passengers;
        double next_distance;
        final String DELIMITER = ",";
        String scenarioFile = args[0];
        Queue queue = new Queue();

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
        // Loop for twenty (20) iterations:
        for (int i = 0; i < 20; i++) {
            // Step 3: Determine which bus should be selected for processing(based on lowest arrival time)
            queue.chooseNextEvent();
            current_bus_processing = queue.listEvents.get(queue.currentEventId).getBusId();
            Bus bus = buses.get(current_bus_processing);

            // Step 4: update bus changes (if any)
            evaluateChanges(bus);

            // Step 5: Do passenger exchange at this stop
            passengerExchange(bus, bus.getCurrentStop());

            // Step 3: Determine which stop the bus will travel to next (based on the current location and route)
            next_stop_id = buses.get(current_bus_processing).getNextStop();
            // Step 4: Calculate the distance and travel time between the current and next stops
            next_distance = buses.get(current_bus_processing).calculateDistance();
            next_time = buses.get(current_bus_processing).calculateTravelTime(next_distance) +
                        queue.listEvents.get(queue.currentEventId).getRank();
            // Step 5: Display the output line of text to the display
            next_passengers = buses.get(current_bus_processing).getNumPassengersRiding();
            System.out.println("b:"+current_bus_processing +"->s:"+next_stop_id+"@"+next_time+"//p:"+next_passengers+"/f:0");
            // Step 6: Update system state (increment bus route index [stop]) and generate new events as needed.
            queue.updateEventExecutionTimes(queue.currentEventId, next_time);

        }
    }

    public static void evaluateChanges(Bus bus) {
        for (BusChange change : bus_changes) {
            BusChange.ChangeType type = change.getChangeType();
            switch (type) {
                case SPEED:
                    BusSpeedChange speedChange = (BusSpeedChange) change;
                    bus.setSpeed(speedChange.getNewSpeed());
                    break;
                case CAPACITY:
                    BusCapacityChange capacityChange = (BusCapacityChange) change;
                    bus.setCapacity(capacityChange.getNewCapacity());
                    break;
                case ROUTE:
                    BusRouteChange routeChange = (BusRouteChange) change;
                    bus.changeRoute(routeChange.getNewRouteId(), routeChange.getNewRouteIndex());
                    break;
            }
        }
    }

    public static void passengerExchange(Bus bus, Stop stop) {
        int numPassengersWaiting = stop.getNumPassengersWaiting();
        //System.out.println("numPassengersWaiting at station: " + numPassengersWaiting);

        // passengers arrive
        numPassengersWaiting += stop.newPassengersArrive();
        //ystem.out.println("numPassengersWaiting at station after passengers arrive: " + numPassengersWaiting);

        // numPassengersTransfers passengers get off the bus
        int bus_capacity = bus.getMaxCapacity();
        int numPassengersTransfers = stop.unloadPassengersfromBus(); // num that should get off bus
        //System.out.println("num should get off bus: " + numPassengersTransfers);
        int numPassengersRiding = bus.getNumPassengersRiding(); //num actually on bus
        //System.out.println("num actually on bus: " + numPassengersRiding);
        if (numPassengersTransfers >  numPassengersRiding) { // only as many riders on the bus can get off
            numPassengersTransfers = numPassengersRiding;
        }
        if (numPassengersRiding - numPassengersTransfers > bus_capacity) { // no more than bus_capacity can stay on the bus
            numPassengersTransfers  = numPassengersRiding - bus_capacity;
        }
        //System.out.println("actual num getting off bus: " + numPassengersTransfers);
        bus.subtractRiders(numPassengersTransfers);

        // passengers get on the bus
        int num_riders_on = stop.loadPassengersfromStop(); //num that should get on the bus
        if (num_riders_on > numPassengersWaiting) { // only as many people in the waiting group can get on the bus
            num_riders_on = numPassengersWaiting;
        }
        int num_avail_seats = bus.getNumAvailableSeats();
        //System.out.println("num_avail_seats: " + num_avail_seats);
        if (num_riders_on > num_avail_seats) { //no more than num_avail_seats can get on the bus
            num_riders_on = num_avail_seats;
        }
        //System.out.println("actual num getting on bus: " + num_riders_on);


        numPassengersWaiting -= num_riders_on; // subtract from num waiting group
        //System.out.println("num waiting at station after passengers get on bus: " + numPassengersWaiting);

        bus.addRiders(num_riders_on); // add riders to the bus
        //System.out.println("num riders on bus after people get on: " + bus.getNumPassengersRiding());


        // passengers depart station
        int total_people_at_station = numPassengersTransfers + numPassengersWaiting;
        //System.out.println("total people at station: " + total_people_at_station);

        int num_passengers_depart = stop.passengersDepartStop();
        //System.out.println("number of people that should depart: " + num_passengers_depart);

        if (num_passengers_depart > total_people_at_station) { // no more than the number of people at the station can depart the station
            num_passengers_depart = total_people_at_station;
        }
//        System.out.println("number of people that depart: " + num_passengers_depart);
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
//        System.out.println("number of people at station: " + numPassengersWaiting + "\n");
        stop.setNumPassengersWaiting(numPassengersWaiting);
    }
}
