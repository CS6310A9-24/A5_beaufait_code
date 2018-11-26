import java.util.*;
import java.io.File;
import java.lang.*;

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

        // Loop for twenty (20) iterations:
        for (int i = 0; i < 20; i++) {
            // Step 2: Determine which bus should be selected for processing(based on lowest arrival time)
            queue.chooseNextEvent();
            current_bus_processing = queue.listEvents.get(queue.currentEventId).getBusId();
            Bus bus = buses.get(current_bus_processing);

            // Step 4: update bus changes (if any)
            evaluateChanges(current_bus_processing);

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

    // process bus changes on the bus with id bus_id requested by the client
    // each bus change is stored in a unique BusChange object
    // the bus that the change is applied to is specified by the bus_id field in the BusChange object
    // delete all the applied BusChange objects so that no change is applied more than once
    public static void evaluateChanges(int bus_id) {
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
                        break;
                }
            }
        }
        // remove BusChange objects applied to bus with id bus_id so they are not processed more than once
        for (BusChange change : bus_changes) {
            if (change.getBus_id() == bus_id) {
                bus_changes.remove(change);
            }
        }
    }
}
