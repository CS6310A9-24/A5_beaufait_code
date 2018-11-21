import java.util.*;
import java.io.File;
import java.lang.*;
import java.util.stream.Collectors;

public class Main {
    public static Map<Integer, Bus> buses = new HashMap();
    public static Map<Integer,Stop> stops = new HashMap();
    public static Map<Integer,Route> routes = new HashMap();
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

            // Step 4: Do passenger exchange at this stop
            Bus bus = buses.get(current_bus_processing);
            passengerExchange(bus, bus.getCurrentStop());
        }
    }

    public static void passengerExchange(Bus bus, Stop stop) {
        //TODO set new passenger capacity here

        int num_waiting = stop.getNumPassengersWaiting();
        System.out.println("num_waiting at station: " + num_waiting);

        // passengers arrive
        num_waiting += stop.newPassengersArrive();
        System.out.println("num_waiting at station after passengers arrive: " + num_waiting);

        // num_transfers passengers get off the bus
        int bus_capacity = bus.getMaxCapacity();
        int num_transfers = stop.unloadPassengersfromBus(); // num that should get off bus
        System.out.println("num should get off bus: " + num_transfers);
        int num_riders = bus.getNumPassengersRiding(); //num actually on bus
        System.out.println("num actually on bus: " + num_riders);
        if (num_transfers >  num_riders) { // only as many riders on the bus can get off
            num_transfers = num_riders;
        }
        if (num_riders - num_transfers > bus_capacity) { // no more than bus_capacity can stay on the bus
            num_transfers  = num_riders - bus_capacity;
        }
        System.out.println("actual num getting off bus: " + num_transfers);
        bus.subtractRiders(num_transfers);

        // passengers get on the bus
        int num_riders_on = stop.loadPassengersfromStop(); //num that should get on the bus
        if (num_riders_on > num_waiting) { // only as many people in the waiting group can get on the bus
            num_riders_on = num_waiting;
        }
        int num_avail_seats = bus.getNumAvailableSeats();
        System.out.println("num_avail_seats: " + num_avail_seats);
        if (num_riders_on > num_avail_seats) { //no more than num_avail_seats can get on the bus
            num_riders_on = num_avail_seats;
        }
        System.out.println("actual num getting on bus: " + num_riders_on);


        num_waiting -= num_riders_on; // subtract from num waiting group
        System.out.println("num waiting at station after passengers get on bus: " + num_waiting);

        bus.addRiders(num_riders_on); // add riders to the bus
        System.out.println("num riders on bus after people get on: " + bus.getNumPassengersRiding());


        // passengers depart station
        int total_people_at_station = num_transfers + num_waiting;
        System.out.println("total people at station: " + total_people_at_station + "\n");

        int num_passengers_depart = stop.passengersDepartStop();
        System.out.println("number of people that should depart: " + num_passengers_depart);

        if (num_passengers_depart > total_people_at_station) { // no more than the number of people at the station can depart the station
            num_passengers_depart = total_people_at_station;
        }
        System.out.println("number of people that depart: " + num_passengers_depart);
        if (num_passengers_depart <= num_transfers) { // transfer group is absorbed by waiting group
            num_transfers -= num_passengers_depart;
            num_waiting += num_transfers;
        } else { // some people in waiting group depart the station
            num_waiting -= num_passengers_depart - num_transfers;
        }

        // set the new number of waiters at the Stop
        if (num_waiting < 0) {
            System.out.println("num_waiting is negative!");
        }
        System.out.println("number of people at station: " + num_waiting);
        stop.setNumPassengersWaiting(num_waiting);
    }
}
