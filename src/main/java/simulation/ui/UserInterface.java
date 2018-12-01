package simulation.ui;

import simulation.Simulation;
import simulation.StopBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class UserInterface {

    public JFrame main_simulation_frame = new JFrame("MTS Simulation Control");
    public static JPanel world_layout = new JPanel();
    public static JPanel button_layout = new JPanel();
    public JPanel status_layout = new JPanel(new GridLayout(2, 5, 2, 2));
    public static Map<Integer, StopBox> stop_boxes = new HashMap<>();

    private int APP_WIDTH = 1200;
    private int APP_HEIGHT = 900;
    private JTextField system_efficiency_text= new JTextField();
    private Simulation simulation;

    JTextField k_speed_text = new JTextField("1.0");
    JButton k_speed_button = new JButton("Update Speed");
    JTextField k_capacity_text = new JTextField("1.0");
    JButton k_capacity_button = new JButton("Update Capacity");
    JTextField k_waiting_text = new JTextField("1.0");
    JButton k_waiting_button = new JButton("Update Waiting");
    JTextField k_buses_text = new JTextField("1.0");
    JButton k_buses_button = new JButton("Update Buses");
    JTextField k_combined_text = new JTextField("1.0");
    JButton k_combined_button = new JButton("Update Combined");
    JButton efficiency_button = new JButton("Refresh efficiency");

    JPanel constantPanel = new JPanel(new GridLayout(2, 6, 2, 2));

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
        replay_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulation.rewind();
            }
        });

        button_layout.add(move_bus_button);
        button_layout.add(replay_button);


        //System efficiency section and constants
        status_layout.add(k_speed_text);
        status_layout.add(k_capacity_text);
        status_layout.add(k_waiting_text);
        status_layout.add(k_buses_text);
        status_layout.add(k_combined_text);
        status_layout.add(system_efficiency_text);

        status_layout.add(k_speed_button);
        status_layout.add(k_capacity_button);
        status_layout.add(k_waiting_button);
        status_layout.add(k_buses_button);
        status_layout.add(k_combined_button);
        status_layout.add(efficiency_button);
        system_efficiency_text.setEditable(false);
        system_efficiency_text.setText("System Efficiency");


        setupListenersForButtons();

        //main frame section
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
        //int previous_stopID = simulation.getRoutes().get(simulation.getBuses().get(current_bus_processing).getRouteId()).getStopIdByIndex(simulation.getBuses().get(current_bus_processing).getPreviousRouteIndex());
        int previous_stopID = simulation.buses.get(current_bus_processing).getPreviousStopID();
        String s = "b:" + current_bus_processing + "->s:" + next_stop_id + "@" + next_time + "//p:" + next_passengers + "/f:0";


        System.out.println("current_stopID: " + current_stopID);
        System.out.println("previous_stopID:" + previous_stopID);
        stop_boxes.get(current_stopID).add_busTextField(current_bus_processing, s);
        stop_boxes.get(current_stopID).show_buses();

        //run logic for previous stop
        System.out.println(simulation.buses.get(current_bus_processing).getIsFirstStop());
        if (simulation.buses.get(current_bus_processing).getIsFirstStop() == false) {
            System.out.println("In this shit");
            stop_boxes.get(previous_stopID).updateBusInfo(current_bus_processing);
        }

        simulation.buses.get(current_bus_processing).setIsFirstStop(false);

        stop_boxes.get(previous_stopID).show_buses();

        stop_boxes.get(current_stopID).revalidate();
        stop_boxes.get(previous_stopID).revalidate();
        stop_boxes.get(current_stopID).repaint();
        stop_boxes.get(previous_stopID).repaint();
    }

    public void updateSystemEfficiency(String text) {
        system_efficiency_text.setText(text);
    }


    private void setupListenersForButtons() {
        final String INVALID_VALUE = "Invalid Value!!";

        k_buses_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    simulation.efficiency.setConstsBuses(Double.parseDouble(k_buses_text.getText()));
                    k_buses_button.setText("Saved Buses");
                } catch (NumberFormatException nfe) {
                    System.out.println("Invalid value for Bus constant");
                    k_buses_text.setText(INVALID_VALUE);
                }
            }
        });

        k_buses_text.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                k_buses_button.setText("Update Buses");
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });


        k_speed_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    simulation.efficiency.setConstsSpeed(Double.parseDouble(k_speed_text.getText()));
                    k_speed_button.setText("Saved Speed");

                } catch (NumberFormatException nfe) {
                    System.out.println("Invalid value for Speed constant");
                    k_speed_text.setText(INVALID_VALUE);
                }
            }
        });
        k_speed_text.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                k_speed_button.setText("Update Speed");
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        k_capacity_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    simulation.efficiency.setConstsCapacity(Double.parseDouble(k_capacity_text.getText()));
                    k_capacity_button.setText("Saved Capacity");

                } catch (NumberFormatException nfe) {
                    System.out.println("Invalid value for Capacity constant");
                    k_capacity_text.setText(INVALID_VALUE);
                }
            }
        });

        k_capacity_text.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                k_capacity_button.setText("Update Capacity");
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });


        k_waiting_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    simulation.efficiency.setConstsWaiting(Double.parseDouble(k_waiting_text.getText()));
                    k_waiting_button.setText("Saved Waiting");
                } catch (NumberFormatException nfe) {
                    System.out.println("Invalid value for waiting constant");
                    k_waiting_text.setText(INVALID_VALUE);
                }
            }
        });
        k_waiting_text.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                k_waiting_button.setText("Update Waiting");
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        k_combined_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    simulation.efficiency.setConstsCombined(Double.parseDouble(k_combined_text.getText()));
                    k_combined_button.setText("Saved Combined");

                } catch (NumberFormatException nfe) {
                    System.out.println("Invalid value for Combined constant");
                    k_combined_button.setText(INVALID_VALUE);
                }
            }
        });
        k_combined_text.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                k_combined_button.setText("Update Combined");
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });


        efficiency_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                updateSystemEfficiency(simulation.efficiency.system_efficiency() + "");
            }
        });

    }
}
