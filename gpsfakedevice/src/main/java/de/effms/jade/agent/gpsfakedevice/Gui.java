package de.effms.jade.agent.gpsfakedevice;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class Gui extends JFrame {
    private GpsFakeDevice device;

    private JTextField longitude, latitude;

    public Gui(GpsFakeDevice fakeDevice) {
        super(fakeDevice.getLocalName());

        this.device = fakeDevice;

        JPanel p = new JPanel();
        p.setLayout(new GridLayout(2, 2));
        p.add(new JLabel("Longitude:"));
        longitude = new JTextField(15);
        p.add(longitude);
        p.add(new JLabel("Latitude:"));
        latitude = new JTextField(15);
        p.add(latitude);
        getContentPane().add(p, BorderLayout.CENTER);

        JButton addButton = new JButton("Send");
        addButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                try {
                    String longitude = Gui.this.longitude.getText().trim();
                    String latitude = Gui.this.latitude.getText().trim();
                    device.sendNewCoordinates(longitude, latitude);
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(Gui.this, "Invalid values. " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        p = new JPanel();
        p.add(addButton);
        getContentPane().add(p, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                device.doDelete();
            }
        });

        setResizable(false);
    }

    public void showGui() {
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int)screenSize.getWidth() / 2;
        int centerY = (int)screenSize.getHeight() / 2;
        setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
        super.setVisible(true);
    }
}
