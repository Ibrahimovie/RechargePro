package swing;

import bean.*;
import bean.manager.*;
import commands.*;
import serial.*;
import exception.*;
import gnu.io.*;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

/**
 * @author kingfans
 */
public class LogoutFrame extends JFrame {
    private User user;
    private RechargeFrame rechargeFrame;
    private JComboBox<String> deviceTypeChooser;

    public LogoutFrame(User user, RechargeFrame rechargeFrame) {
        this.initComponents(user);
        this.user = user;
        this.rechargeFrame = rechargeFrame;
        this.setSize(260, 270);
        this.setTitle("设备类型选择");
        Image icon = Toolkit.getDefaultToolkit().getImage("resources/dk_logo.png");
        this.setIconImage(icon);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void confirmButtonActionPerformed(ActionEvent e) throws SerialPortOutputStreamCloseFailure, SendDataToSerialPortFailure {
        int deviceType = this.deviceTypeChooser.getSelectedIndex();
        SerialPort serialPort = PortManager.getSerialPort();
        if (serialPort != null) {
            String systemPassword = this.user.getSystemPassword();
            SerialPortUtils.sendToPort(serialPort, CommandUtils.logoffCommand(systemPassword, deviceType));

        }
        this.rechargeFrame.setEnabled(true);
        this.dispose();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        this.rechargeFrame.setEnabled(true);
        this.dispose();
    }

    private void registerWindowClosing(WindowEvent e) {
        this.rechargeFrame.setEnabled(true);
    }

    private void initComponents(User user) {
        JLabel label1 = new JLabel();
        this.deviceTypeChooser = new JComboBox<String>();
        JButton confirmButton = new JButton();
        JButton cancelButton = new JButton();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LogoutFrame.this.registerWindowClosing(e);
            }
        });
        Container contentPane = this.getContentPane();
        contentPane.setLayout(null);
        label1.setText("\u8bf7\u9009\u62e9\u8bbe\u5907\u7c7b\u578b \uff1a");
        contentPane.add(label1);
        label1.setBounds(new Rectangle(new Point(30, 25), label1.getPreferredSize()));
        this.deviceTypeChooser.setModel(new DefaultComboBoxModel<String>(new String[]{"Q10/CDZ", "Q20"}));
        contentPane.add(this.deviceTypeChooser);
        this.deviceTypeChooser.setBounds(85, 60, 105, this.deviceTypeChooser.getPreferredSize().height);
        confirmButton.setText("\u786e\u5b9a");
        confirmButton.addActionListener(e -> {
            try {
                this.confirmButtonActionPerformed(e);
            } catch (SerialPortOutputStreamCloseFailure | SendDataToSerialPortFailure serialPortOutputStreamCloseFailure) {
                serialPortOutputStreamCloseFailure.printStackTrace();
            }
        });
        contentPane.add(confirmButton);
        confirmButton.setBounds(new Rectangle(new Point(50, 120), confirmButton.getPreferredSize()));
        cancelButton.setText("\u53d6\u6d88");
        cancelButton.addActionListener(this::cancelButtonActionPerformed);
        contentPane.add(cancelButton);
        cancelButton.setBounds(new Rectangle(new Point(135, 120), cancelButton.getPreferredSize()));
        Dimension preferredSize = new Dimension();
        for (int i = 0; i < contentPane.getComponentCount(); i++) {
            final Rectangle bounds = contentPane.getComponent(i).getBounds();
            preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
            preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
        }
        Insets insets = contentPane.getInsets();
        preferredSize.width += insets.right;
        preferredSize.height += insets.bottom;
        contentPane.setMinimumSize(preferredSize);
        contentPane.setPreferredSize(preferredSize);
        this.pack();
        this.setLocationRelativeTo(this.getOwner());
    }
}
