package simulation.ui;

import javax.swing.*;

public class BusChangeUI {

    private int bus_id;

    public BusChangeUI(int bus_id) {
        this.bus_id = bus_id;
        //SPEED, ROUTE, CAPACITY
        String[] changeOptions = { "Speed", "Route", "Capacity"};
        JComboBox optionList = new JComboBox(changeOptions);
        optionList.setSelectedIndex(0);

        //JTextField speedChange = new JTextField();


        JPanel p = new JPanel();
        p.add(optionList);


        int result = JOptionPane.showConfirmDialog(null, p,"Change bus # " + this.bus_id, JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            System.out.println(optionList.getSelectedItem());
            //System.out.println("y value: " + yField.getText());
        }
    }
}

