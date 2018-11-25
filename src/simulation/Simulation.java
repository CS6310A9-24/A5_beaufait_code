package simulation;

import simulation.ui.UserInterface;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Simulation {

    public static Map<Integer, Bus> buses = new HashMap<>();
    public static Map<Integer, Stop> stops = new HashMap<>();
    public static Map<Integer, Route> routes = new HashMap<>();

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
        //String scenarioFile = args[0];
        String scenarioFile = "resources/test_scenario.txt";
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
        System.out.println("b:" + current_bus_processing + "->s:" + next_stop_id + "@" + next_time + "//p:" + next_passengers + "/main_simulation_frame:0");

    }

    public void updateEventExecutionTimes() {
        // Step 6: Update system state and generate new events as needed.
        queue.updateEventExecutionTimes(queue.currentEventId, next_time);

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
