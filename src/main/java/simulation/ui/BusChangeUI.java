
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
        JComboBox optionList = new JComboBox();
        JComboBox routeList = new JComboBox();
        JComboBox stopList = new JComboBox();
        optionList.addItem(" ");
        optionList.addItem("Speed");
        optionList.addItem("Route");
        optionList.addItem("Capacity");
        optionList.setSelectedIndex(0);
        routeList.setVisible(false);
        stopList.setVisible(false);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JTextField speedInput = new JTextField();
        JTextField capacityInput = new JTextField();
        speedInput.setVisible(false);
        capacityInput.setVisible(false);
        optionList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                event.getSource();
                Object selected = optionList.getSelectedItem();
                if(selected.toString().equals("Speed")) {
                    speedInput.setVisible(true);
                    routeList.setVisible(false);
                    stopList.setVisible(false);
                    capacityInput.setVisible(false);
                    p.revalidate();
                    p.repaint();
                }
                if(selected.toString().equals("Route")) {
                    speedInput.setVisible(false);
                    capacityInput.setVisible(false);
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
                    capacityInput.setVisible(true);
                    speedInput.setVisible(false);
                    routeList.setVisible(false);
                    stopList.setVisible(false);
                    p.revalidate();
                    p.repaint();
                }
            }
        });

        p.add(optionList);
        p.add(routeList);
        p.add(stopList);
        p.add(speedInput);
        p.add(capacityInput);

        int result = JOptionPane.showConfirmDialog(null, p,"Change bus # " + this.bus_id, JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            switch (optionList.getSelectedItem().toString()){
                case "Speed":
                    int newSpeed = Integer.parseInt(speedInput.getText());
                    Simulation.addBusSpeedChange(this.bus_id, newSpeed);
                    break;
                case "Capacity":
                    int newCapacity = Integer.parseInt(capacityInput.getText());
                    Simulation.addBusCapacityChange(this.bus_id, newCapacity);
                    break;
                case "Route":
                    int newRouteID = Integer.parseInt(routeList.getSelectedItem().toString());
                    int newRteStopIndex = stopList.getSelectedIndex();
                    Simulation.buses.get(bus_id).setLastStopLastRte(Simulation.buses.get(bus_id).getCurrentStop().getId());
                    Simulation.buses.get(bus_id).setNewRte();
                    Simulation.addBusRouteChange(this.bus_id, newRouteID, newRteStopIndex);
                    break;
            }
        }
    }
}