import java.util.Scanner;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.lang.*;

public class Main {
    public static Map<Integer, Bus> buses = new HashMap();
    public static Map<Integer,Stop> stops = new HashMap();
    public static Map<Integer,Route> routes = new HashMap();
    public static double consts_speed = 1.0;
    public static double consts_waiting = 1.0;
    public static double consts_capacity = 1.0;
    public static double consts_buses = 1.0;
    public static double consts_combined = 1.0;

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
                    case "add_depot":
                        int stop_index = Integer.parseInt(tokens[1]);
                        stops.put(stop_index, new Stop(Integer.parseInt(tokens[1]), tokens[2],
                                                       Double.parseDouble(tokens[3]), Double.parseDouble(tokens[4])));
                        break;
                    case "add_stop":
                        stop_index = Integer.parseInt((tokens[1]));
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

        // Loop for twenty (20) iterations:
        for (int i = 0; i < 20; i++) {
            // Step 2: Determine which bus should be selected for processing(based on lowest arrival time)
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
            // Step 6: Update system state and generate new events as needed.
            queue.updateEventExecutionTimes(queue.currentEventId, next_time);
        }
    }
    private static int waiting_passengers(){
        int total_passengers = 0;
        int num_passengers;
        for (Map.Entry<Integer, Stop> stopEntry: stops.entrySet()){
           num_passengers = stops.get(stopEntry.getKey()).getNumPassengersWaiting();
           total_passengers += num_passengers;
        }
        return total_passengers;
    }
    private static double bus_cost(){
        double total_cost = 0;
        double cost;
        for (Map.Entry<Integer, Bus> busEntry: buses.entrySet()){
            cost = consts_speed *(buses.get(busEntry.getKey()).getAvgSpeed()) +
                   consts_capacity * (buses.get(busEntry.getKey()).getMaxCapacity());
            total_cost += cost;
        }
        return total_cost;
    }
    public static double system_efficiency(){
        int efficiency_passengers = waiting_passengers();
        double efficiency_cost = bus_cost();
        double efficiency = (consts_waiting * efficiency_passengers) + (consts_buses * efficiency_cost) +
                (consts_combined * efficiency_passengers * efficiency_cost);
        return efficiency;
    }
}
