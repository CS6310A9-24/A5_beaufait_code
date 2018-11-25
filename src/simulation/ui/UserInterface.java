package simulation.ui;

import simulation.Bus;
import simulation.Route;
import simulation.Simulation;
import simulation.Stop;

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
    public static Map<Integer, JPanel> stop_box = new HashMap<>();

    public static ImageIcon stop_icon = new ImageIcon("simulation/bus_stop_img.png");
    public static ImageIcon bus_icon = new ImageIcon("simulation/bus_img.png");

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

        main_simulation_frame.setLayout(null);

        main_simulation_frame.getContentPane().add(world_layout);
        main_simulation_frame.getContentPane().add(button_layout);


        JButton move_bus_button = new JButton("Move Next Bus");
        move_bus_button.setBounds(0, 0, move_bus_button.getPreferredSize().width,
                move_bus_button.getPreferredSize().height);
        move_bus_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                move_bus();
            }
        });


        JButton replay_button = new JButton("Rewind");
        replay_button.setBounds(0, 0, replay_button.getPreferredSize().width, replay_button.getPreferredSize().height);


        JPanel system_efficiency = new JPanel();
        system_efficiency_text = new JTextField("System Efficiency = 14.32");
        system_efficiency.add(system_efficiency_text);

        button_layout.add(move_bus_button);
        button_layout.add(replay_button);
        button_layout.add(system_efficiency);


        main_simulation_frame.validate();
        main_simulation_frame.pack();
        main_simulation_frame.setVisible(true);
        main_simulation_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public void add_bus_GUI(int bus_index) {
        int current_stopID = simulation.getRoutes().get(simulation.getBuses().get(bus_index).getRouteId()).getStopIdByIndex(simulation.getBuses().get(bus_index).getRouteIndex());

        stop_box.get(current_stopID).getComponent(3).setVisible(true);

    }


    public void add_stop_GUI(int stop_index) {

        double[] loc = simulation.getStops().get(stop_index).getLocation();

        int pos_x = (int) (loc[0] * 2400);
        int pos_y = (int) (loc[1] * 1200);

        JLabel bus_stop_img = new JLabel(stop_icon);
        bus_stop_img.setName("bus_stop_img");
        //bus_stop_img.setBounds(pos_x, pos_y, 25, 20);
        JTextField bus_stop_label = new JTextField("Stop#" + simulation.getStops().get(stop_index).getId() + " " + simulation.getStops().get(stop_index).getName());
        bus_stop_label.setFont(new Font("Courier", Font.BOLD, 15));
        bus_stop_label.setEditable(false);
        bus_stop_label.setName("bus_stop_label");


        JTextField bus_info = new JTextField("");
        bus_info.setFont(new Font("Courier", Font.BOLD, 8));
        bus_info.setEditable(false);
        bus_info.setName("bus_info");

        JLabel bus_img = new JLabel(bus_icon);
        bus_img.setName("bus_img");
        bus_img.setVisible(false);

        JPanel sp = new JPanel();
        sp.setLayout(new BorderLayout());
        sp.add(bus_stop_label, "North");
        sp.add(bus_stop_img, "West");
        sp.add(bus_info, "South");
        sp.add(bus_img, "East");
        sp.setName("stop_panel");

        sp.validate();
        sp.setBounds(pos_x, pos_y, sp.getPreferredSize().width, sp.getPreferredSize().height);
        sp.setName("" + simulation.getStops().get(stop_index).getId());

        stop_box.put(stop_index, sp);
        //new_stop.set_stop_box_id(stop_box.size()-1);
        //stops.add(new_stop);

        world_layout.add(stop_box.get(stop_index));
        world_layout.validate();
    }


    public void move_bus() {
        int current_bus_processing = simulation.getCurrent_bus_processing();
        int next_stop_id = simulation.getNext_stop_id();
        int next_time = simulation.getNext_time();
        int next_passengers = simulation.getNext_passengers();

        int current_stopID = simulation.getRoutes().get(simulation.getBuses().get(current_bus_processing).getRouteId()).getStopIdByIndex(simulation.getBuses().get(current_bus_processing).getRouteIndex());
        int previous_stopID = simulation.getRoutes().get(simulation.getBuses().get(current_bus_processing).getRouteId()).getStopIdByIndex(simulation.getBuses().get(current_bus_processing).getPreviousRouteIndex());

        ((JTextField) (stop_box.get(current_stopID).getComponent(2))).setText("b:" + current_bus_processing + "->s:" + next_stop_id + "@" + next_time + "//p:" + next_passengers + "/main_simulation_frame:0");
        stop_box.get(current_stopID).getComponent(3).setVisible(true);//show bus at the new location

        if (current_stopID != previous_stopID) {
            ((JTextField) (stop_box.get(previous_stopID).getComponent(2))).setText("");
            stop_box.get(previous_stopID).getComponent(3).setVisible(false);
        }

        simulation.updateEventExecutionTimes();
    }

    public void updateSystemEfficiency(String text) {
        system_efficiency_text.setText("System Efficiency = " + text);
    }


}
