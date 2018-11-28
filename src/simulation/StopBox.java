package simulation;

import simulation.ui.BusChangeUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

public class StopBox extends JPanel {

    public static ImageIcon stop_icon = new ImageIcon("resources/images/bus_stop_img.png");
    public static ImageIcon bus_icon = new ImageIcon("resources/images/bus_img.png");

    private GridBagConstraints c = new GridBagConstraints();
    private JTextField bus_stop_info = new JTextField();
    private JLabel bus_stop_img = new JLabel(stop_icon);
    private JLabel bus_img = new JLabel(bus_icon);
    private JTextField pax_info = new JTextField();

    private Map<Integer, JTextField> busTextFields = new HashMap<>();//integer is the bus_id

    private String name;
    private int stopID;


    public StopBox(int stopID, String name) {
        this.stopID = stopID;
        setLayout(new GridBagLayout());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 1;
        this.name = name;

        //add bus_icon - must be done first since it is the second column
        c.gridx = 1;
        c.gridy = 2;
        c.anchor = GridBagConstraints.WEST;
        bus_img.setName("bus_img");
        bus_img.setVisible(false);
        add(bus_img, c);

        //add name to stop
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.gridwidth = 2;
        bus_stop_info.setFont(new Font("Courier", Font.BOLD, 8));
        bus_stop_info.setEditable(false);
        bus_stop_info.setName("bus_stop_info");
        bus_stop_info.setText(this.name);
        add(bus_stop_info, c);

        //add pax text field
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.gridwidth = 2;
        pax_info.setName("pax_info");
        pax_info.setVisible(true);
        pax_info.setEditable(false);
        add(pax_info, c);

        //add bus stop image
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = 1;
        bus_stop_img.setName("bus_stop_img");
        bus_stop_img.setVisible(true);
        add(bus_stop_img, c);

        setVisible(true);
        resize_box();

    }

    public void add_busTextField(int bus_id, String s) {
        c.anchor = GridBagConstraints.CENTER;
        JTextField bus_info = new JTextField(s);
        bus_info.setFont(new Font("Courier", Font.BOLD, 8));
        bus_info.setEditable(false);
        bus_info.setName(Integer.toString(bus_id));
        bus_info.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                BusChangeUI bc = new BusChangeUI(bus_id);
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        //System.out.println("Add " + bus_id + " to stop " + this.stopID + " with size " + busTextFields.size());
        busTextFields.put(bus_id, bus_info);
        //System.out.println("Added " + bus_id + " to stop " + this.stopID + " with size " + busTextFields.size());
        //set where it should be, should always at least start at gridy = 3
        c.gridy = 3 + busTextFields.size();
        c.gridx = 0;
        c.gridwidth = 2;
        add(busTextFields.get(bus_id), c);
        resize_box();
    }

    public void updateBusInfo(int bus_id) {
        int compID = 1000;
        for(int i = 0; i < getComponentCount(); i++){
            if(getComponent(i).getName().equals(Integer.toString(bus_id))){
                compID = i;
            }
        }

        remove(compID);
        System.out.println("Rem " + bus_id + " from stop " + this.stopID + " with size " + busTextFields.size());
        busTextFields.remove(bus_id);
        System.out.println("Removed " + bus_id + " from stop " + this.stopID + " with size " + busTextFields.size());
        resize_box();
    }

    public void show_buses() {
        if (busTextFields.size() != 0) {
            this.bus_img.setVisible(true);
        } else {
            this.bus_img.setVisible(false);
        }

        resize_box();
    }

    public void setPaxInfo(String s) {
        this.pax_info.setText(s);
        resize_box();
    }

    public void init_bus() {
        this.bus_img.setVisible(true);
        resize_box();
    }

    public void resize_box() {
        setPreferredSize(getPreferredSize());
        revalidate();
        repaint();
    }
}
