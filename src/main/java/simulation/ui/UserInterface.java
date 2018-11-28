package simulation.ui;

import simulation.Simulation;
import simulation.StopBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class UserInterface {

    public JFrame main_simulation_frame = new JFrame("MTS Simulation Control");
    public static JPanel world_layout = new JPanel();
    public static JPanel button_layout = new JPanel();
    public JPanel status_layout = new JPanel();
    public static Map<Integer, StopBox> stop_boxes = new HashMap<>();

    private int APP_WIDTH = 1200;
    private int APP_HEIGHT = 900;
    private JTextField system_efficiency_text;
    private Simulation simulation;

    public UserInterface(Simulation simulation) {
        this.simulation = simulation;

        main_simulation_frame.setPreferredSize(new Dimension(APP_WIDTH, APP_HEIGHT));

        world_layout.setLayout(new GridLayout(5, 5, 15, 20));
        world_layout.setBounds(0, 0, APP_WIDTH, APP_HEIGHT - 150);
        world_layout.setBackground(Color.lightGray);
        button_layout.setLayout(new GridLayout(1, 8, 0, 10));
        button_layout.setBounds(0, 750, APP_WIDTH, 150);

        main_simulation_frame.setLayout(new BorderLayout());

        main_simulation_frame.getContentPane().add(world_layout, BorderLayout.CENTER);
        main_simulation_frame.getContentPane().add(button_layout, BorderLayout.PAGE_END);
        main_simulation_frame.getContentPane().add(status_layout, BorderLayout.PAGE_START);


        JButton move_bus_button = new JButton("Move Next Bus");
        move_bus_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulation.execute_next();
            }
        });


        JButton replay_button = new JButton("Rewind");


        JPanel system_efficiency = new JPanel();
        system_efficiency_text = new JTextField("System Efficiency = 14.32");
        system_efficiency.add(system_efficiency_text);

        button_layout.add(move_bus_button);
        button_layout.add(replay_button);
        status_layout.add(system_efficiency);


        main_simulation_frame.validate();
        main_simulation_frame.pack();
        main_simulation_frame.setVisible(true);
        main_simulation_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public void add_bus_GUI(int bus_index) {
        int current_stopID = simulation.getRoutes().get(simulation.getBuses().get(bus_index).getRouteId()).getStopIdByIndex(simulation.getBuses().get(bus_index).getRouteIndex());

        stop_boxes.get(current_stopID).init_bus();

    }


    public void add_stop_GUI(int stop_index) {

        double[] loc = simulation.getStops().get(stop_index).getLocation();

        int pos_x = (int) (loc[0] * 2400);
        int pos_y = (int) (loc[1] * 1200);

        String name = ("Stop#" + simulation.getStops().get(stop_index).getId() + " " + simulation.getStops().get(stop_index).getName());

        StopBox sb = new StopBox(stop_index, name);

        sb.setBounds(pos_x, pos_y, sb.getPreferredSize().width, sb.getPreferredSize().height);


        stop_boxes.put(stop_index, sb);
        //new_stop.set_stop_box_id(stop_boxes.size()-1);
        //stops.add(new_stop);

        world_layout.add(stop_boxes.get(stop_index));
        world_layout.validate();
    }


    public void move_bus() {
        int current_bus_processing = simulation.getCurrent_bus_processing();
        int next_stop_id = simulation.getNext_stop_id();
        int next_time = simulation.getNext_time();
        int next_passengers = simulation.getNext_passengers();

        int current_stopID = simulation.getRoutes().get(simulation.getBuses().get(current_bus_processing).getRouteId()).getStopIdByIndex(simulation.getBuses().get(current_bus_processing).getRouteIndex());
        int previous_stopID = simulation.getRoutes().get(simulation.getBuses().get(current_bus_processing).getRouteId()).getStopIdByIndex(simulation.getBuses().get(current_bus_processing).getPreviousRouteIndex());
        String s = "b:" + current_bus_processing + "->s:" + next_stop_id + "@" + next_time + "//p:" + next_passengers + "/f:0";


        stop_boxes.get(current_stopID).add_busTextField(current_bus_processing, s);
        stop_boxes.get(current_stopID).show_buses();

        //run logic for previous stop
        if (current_stopID != previous_stopID) {
            stop_boxes.get(previous_stopID).updateBusInfo(current_bus_processing);
        }

        stop_boxes.get(previous_stopID).show_buses();

        stop_boxes.get(current_stopID).revalidate();
        stop_boxes.get(previous_stopID).revalidate();
        stop_boxes.get(current_stopID).repaint();
        stop_boxes.get(previous_stopID).repaint();
    }

    public void updateSystemEfficiency(String text) {
        system_efficiency_text.setText("System Efficiency = " + text);
    }


}
