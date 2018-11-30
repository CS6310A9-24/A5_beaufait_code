
package simulation.ui;

import simulation.Simulation;

import simulation.*;
//import main.java.simulation.Simulation;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BusChangeUI {

    private int bus_id;

    public BusChangeUI(int bus_id) {
        this.bus_id = bus_id;
        //SPEED, ROUTE, CAPACITY
        //String[] changeOptions = { " ", "Speed", "Route", "Capacity"};
        JComboBox optionList = new JComboBox();
        JComboBox routeList = new JComboBox();
        JComboBox stopList = new JComboBox();
        optionList.addItem(" ");
        optionList.addItem("Speed");
        optionList.addItem("Route");
        optionList.addItem("Capacity");
        optionList.setSelectedIndex(0);

        String selection = "";

        routeList.setVisible(false);
        stopList.setVisible(false);

        //JTextField speedChange = new JTextField();


        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JTextField input = new JTextField();
        input.setVisible(false);

        optionList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                event.getSource();

                Object selected = optionList.getSelectedItem();
                if(selected.toString().equals("Speed")) {
                    //System.out.println("Speed");
                    input.setVisible(true);
                    routeList.setVisible(false);
                    stopList.setVisible(false);
                    p.revalidate();
                    p.repaint();
                }
                if(selected.toString().equals("Route")) {
                    //System.out.println("Route");
                    input.setVisible(false);
                    routeList.addItem("");
                    for(int i = 0; i < Simulation.getRouteIDs().size(); i++){
                        routeList.addItem(Simulation.routeIDs.get(i));
                    }
                    routeList.setVisible(true);

                    routeList.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            e.getSource();

                            Object routeSelection = routeList.getSelectedItem();
                            stopList.removeAllItems();
                            for(int i = 0; i < Simulation.getRoutes().get(routeSelection).getListStopIds().size(); i++){
                                stopList.addItem(Simulation.getRoutes().get(routeSelection).getListStopIds().get(i));
                            }
                        }
                    });
                    stopList.setVisible(true);
                }
                if(selected.toString().equals("Capacity")) {
                    //System.out.println("Capacity");
                    input.setVisible(true);
                    routeList.setVisible(false);
                    stopList.setVisible(false);
                }
            }
        });

        p.add(optionList);
        p.add(routeList);
        p.add(stopList);
        p.add(input);

        int result = JOptionPane.showConfirmDialog(null, p,"Change bus # " + this.bus_id, JOptionPane.OK_CANCEL_OPTION);
        //int value = Integer.parseInt(input.getText());
        //System.out.println(value);

        if (result == JOptionPane.OK_OPTION) {
            System.out.println(optionList.getSelectedItem());

            switch (optionList.getSelectedItem().toString()){
                case "Speed":
                    int newSpeed = Integer.parseInt(input.getText());
                    Simulation.addBusSpeedChange(this.bus_id, newSpeed);
                    break;

                case "Capacity":
                    int newCapacity = Integer.parseInt(input.getText());
                    Simulation.addBusCapacityChange(this.bus_id, newCapacity);
                    break;

                case "Route":
                    int newRouteID = Integer.parseInt(routeList.getSelectedItem().toString());
                    int newRteStopIndex = stopList.getSelectedIndex();
                    Simulation.addBusRouteChange(this.bus_id, newRouteID, newRteStopIndex);
                    break;

            }

        }
    }
}