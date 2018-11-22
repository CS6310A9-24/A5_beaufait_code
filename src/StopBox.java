import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class StopBox extends JPanel{

    public static ImageIcon stop_icon = new ImageIcon("bus_stop_img.png");
    public static ImageIcon bus_icon = new ImageIcon("bus_img.png");

    private JPanel panel = new JPanel();
    //private JTextField busTextField = new JTextField();
    private GridBagConstraints c = new GridBagConstraints();
    private JTextField bus_stop_info = new JTextField();
    private JLabel bus_stop_img = new JLabel(stop_icon);
    private JLabel bus_img = new JLabel(bus_icon);
    private JTextField pax_info = new JTextField();

    private static Map<Integer, JTextField> busTextField = new HashMap();//integer is the bus_id

    private String name;


    public StopBox(int stopID, String name){
        panel.setLayout(new GridBagLayout());
        this.name = name;

        //add name to stop
        c.gridx = 0;
        c.gridy = 0;
        bus_stop_info.setFont(new Font("Courier", Font.BOLD,8));
        bus_stop_info.setEditable(false);
        bus_stop_info.setName("bus_stop_info");
        bus_stop_info.setText(this.name);
        panel.add(bus_stop_info, c);

        //add pax text field
        c.gridx = 0;
        c.gridy = 1;
        pax_info.setName("pax_info");
        pax_info.setVisible(true);
        pax_info.setEditable(false);
        panel.add(pax_info, c);

        //add bus stop image
        c.gridx = 0;
        c.gridy = 2;
        bus_stop_img.setName("bus_stop_img");
        bus_stop_img.setVisible(true);
        panel.add(bus_stop_img, c);

        //add bus_icon
        c.gridx = 1;
        c.gridy = 2;
        bus_img.setName("bus_img");
        bus_img.setVisible(false);
        panel.add(bus_img, c);

        panel.validate();

    }

    public void add_busTextField(int bus_id, String s){
        JTextField bus_info = new JTextField(s);
        bus_info.setFont(new Font("Courier", Font.BOLD,8));
        bus_info.setEditable(false);
        bus_info.setName("" + bus_id);

        busTextField.put(bus_id, bus_info);


        //set where it should be, should always at least start at gridy = 3
        c.gridy = 3 + busTextField.size();
        c.gridx = 0;

        panel.add(busTextField.get(bus_id), c);
    }

    public void remove_busTextField(int bus_id){
        panel.remove(busTextField.get(bus_id));
    }

    public void show_buses(){
        if(busTextField.size() > 0)
            this.bus_stop_img.setVisible(true);
    }

    public void setPaxInfo(String s){
        this.pax_info.setText(s);
    }

    public void init_bus(){
        this.bus_stop_img.setVisible(true);
    }
}
