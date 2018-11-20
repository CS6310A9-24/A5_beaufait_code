import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class GUI {

    public static Map<Integer, Bus> buses = new HashMap();
    public static Map<Integer,Stop> stops = new HashMap();
    public static Map<Integer,Route> routes = new HashMap();

    public int event_index = -1;
    public int current_bus_processing, next_stop_id, next_time, next_passengers;
    public double next_distance;
    public Queue queue = new Queue();

    public JFrame f = new JFrame("MTS Simulation Control");
    //public JPanel sim_layout = new JPanel();
    //public JTextArea sim_status = new JTextArea("Status window");
    public static JPanel world_layout = new JPanel();
    public JPanel button_layout = new JPanel();
    public static Map<Integer, JPanel> stop_box = new HashMap();

    public static ImageIcon stop_icon = new ImageIcon("bus_stop_img.png");
    public static ImageIcon bus_icon = new ImageIcon("bus_img.png");

    public GUI(){
        f.setPreferredSize(new Dimension(1200, 850));
        //sim_layout.setLayout(null);
        //sim_layout.setBounds(1000, 0, 200, 800);
        world_layout.setLayout(null);
        world_layout.setBounds(0, 0, 1200, 750);
        world_layout.setBackground(Color.lightGray);
        button_layout.setLayout(null);
        button_layout.setBounds(0, 750, 800, 150);

        f.setLayout(null);

        //f.getContentPane().add(sim_layout);
        f.getContentPane().add(world_layout);
        f.getContentPane().add(button_layout);

        //JScrollPane sim_status_scroll = new JScrollPane(sim_status);
        //sim_status_scroll.setBounds(0, 0, 200, 800);
        //sim_layout.add(sim_status_scroll);

        JButton move_bus = new JButton("Move Next Bus");
        move_bus.setBounds(0, 0, move_bus.getPreferredSize().width, move_bus.getPreferredSize().height);
        button_layout.add(move_bus);

        move_bus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                execute_next();
            }
        });

        f.validate();
        f.pack();
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void build_environment(String[] args){
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
                        add_stop_GUI(stop_index);
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
                        add_bus_GUI(bus_index);
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
    }

    public void add_bus_GUI(int bus_index){
        int current_stopID = routes.get(buses.get(bus_index).getRouteId()).getStopIdByIndex(buses.get(bus_index).getRouteIndex());

        stop_box.get(current_stopID).getComponent(3).setVisible(true);
    }


    public void add_stop_GUI(int stop_index){

        double[] loc = stops.get(stop_index).getLocation();

        int pos_x = (int)(loc[1]*2400);
        int pos_y = (int)(loc[0]*1200);

        JLabel bus_stop_img = new JLabel(stop_icon);
        bus_stop_img.setName("bus_stop_img");
        //bus_stop_img.setBounds(pos_x, pos_y, 25, 20);
        JTextField bus_stop_info = new JTextField("Stop#" + stops.get(stop_index).getId() + " " + stops.get(stop_index).getName());
        bus_stop_info.setFont(new Font("Courier", Font.BOLD,8));
        bus_stop_info.setEditable(false);
        bus_stop_info.setName("bus_stop_info");

        JTextField bus_info = new JTextField("");
        bus_info.setFont(new Font("Courier", Font.BOLD,8));
        bus_info.setEditable(false);
        bus_info.setName("bus_info");

        JLabel bus_img = new JLabel(bus_icon);
        bus_img.setName("bus_img");
        bus_img.setVisible(false);

        JPanel sp = new JPanel();
        sp.setLayout(new BorderLayout());
        sp.add(bus_stop_info, "North");
        sp.add(bus_stop_img, "West");
        sp.add(bus_info, "South");
        sp.add(bus_img, "East");
        sp.setName("stop_panel");

        sp.validate();
        sp.setBounds(pos_x, pos_y, sp.getPreferredSize().width, sp.getPreferredSize().height);
        sp.setName("" + stops.get(stop_index).getId());

        stop_box.put(stop_index, sp);
        //new_stop.set_stop_box_id(stop_box.size()-1);
        //stops.add(new_stop);

        world_layout.add(stop_box.get(stop_index));
        world_layout.validate();
    }

    public void execute_next(){
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
