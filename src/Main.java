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
            // Step 4: Do passenger exchange at this stop
            Bus bus = buses.get(current_bus_processing);
            this.passengerExchange(bus, bus.getCurrentStop());
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

    public static void passengerExchange(Bus bus, Stop stop) {
        return;
    }
}
