package swing;

import bean.manager.PortManager;
import bean.manager.ProgressFrameManager;
import commands.CommandUtils;
import exception.SendDataToSerialPortFailure;
import exception.SerialPortOutputStreamCloseFailure;
import gnu.io.SerialPort;
import serial.SerialPortUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static swing.LoginFrame.E1_HANDMADES;

/**
 * @author kingfans
 */
public class HardwareUpdateFrame extends JFrame {

    private String version;
    private int size;

    public HardwareUpdateFrame(String version, int size) {
        this.version = version;
        this.size = size;
        initComponents();
        this.setSize(350, 280);
        this.setTitle("升级提示");
        Image icon = Toolkit.getDefaultToolkit().getImage("resources/dk_logo.png");
        this.setIconImage(icon);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void updateButtonActionPerformed(ActionEvent e) {
        new Thread(() -> {
            ProgressBarFrame progressBarFrame = new ProgressBarFrame(size, 4500);
            ProgressFrameManager.addFrame("progressBar", progressBarFrame);
        }).start();
        SerialPort serialPort = PortManager.getSerialPort();
        if (serialPort != null) {
            try {
                E1_HANDMADES = 1;
                SerialPortUtils.sendToPort(serialPort, CommandUtils.F1Command(size));
            } catch (SendDataToSerialPortFailure | SerialPortOutputStreamCloseFailure sendDataToSerialPortFailure) {
                sendDataToSerialPortFailure.printStackTrace();
            }
        }
        this.dispose();
    }

    private void initComponents() {
        JLabel photoView = new JLabel();

        JLabel nowVersion = new JLabel();
        JButton updateButton = new JButton();

        Container contentPane = this.getContentPane();
        contentPane.setLayout(null);

        ImageIcon ico = new ImageIcon("resources/dk_logo_2.png");
        ico.setImage(ico.getImage().getScaledInstance(94, 26, 1));
        photoView.setBounds(120, 30, 100, 30);
        photoView.setIcon(ico);
        contentPane.add(photoView);


        nowVersion.setText("可升级到新版本 :  " + version);
        nowVersion.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 16));
        nowVersion.setBounds(95, 100, nowVersion.getPreferredSize().width, nowVersion.getPreferredSize().height);
        contentPane.add(nowVersion);

        updateButton.setText("开始升级");
        updateButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        updateButton.addActionListener(this::updateButtonActionPerformed);
        contentPane.add(updateButton);
        updateButton.setBounds(120, 160, 88, updateButton.getPreferredSize().height);
        updateButton.setBackground(new Color(180, 205, 205));
        updateButton.setBorder(BorderFactory.createRaisedBevelBorder());


        Dimension preferredSize = new Dimension();
        for (int i = 0; i < contentPane.getComponentCount(); i++) {
            Rectangle bounds = contentPane.getComponent(i).getBounds();
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

