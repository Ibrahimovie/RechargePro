package swing;

import bean.User;
import bean.manager.PortManager;
import commands.CommandUtils;
import exception.SendDataToSerialPortFailure;
import exception.SerialPortOutputStreamCloseFailure;
import gnu.io.SerialPort;
import serial.SerialPortUtils;
import service.impl.ServiceImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @Author: kingfans
 * @Date: 2018/8/30
 */
public class VerifyFrame extends JFrame {

    private User user;
    private String verifyCode;
    private String phone;
    private String name;
    private int deviceType;
    private RechargeFrame rechargeFrame;
    private RegisterFrame registerFrame;
    private JTextField codeText;
    private JButton confirmButton;

    public VerifyFrame(User user, String verifyCode, RechargeFrame rechargeFrame, RegisterFrame registerFrame, String phone, String name, int deviceType) {
        this.user = user;
        this.verifyCode = verifyCode;
        this.phone = phone;
        this.name = name;
        this.deviceType = deviceType;
        this.rechargeFrame = rechargeFrame;
        this.registerFrame = registerFrame;
        initComponents();
        this.setSize(315, 260);
        this.setLocationRelativeTo(null);
        this.getRootPane().setDefaultButton(confirmButton);
        Image icon = Toolkit.getDefaultToolkit().getImage("resources/dk_logo.png");
        this.setIconImage(icon);
        this.setVisible(true);
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        registerFrame.setEnabled(true);
        rechargeFrame.setEnabled(true);
        this.dispose();
    }

    private void confirmButtonActionPerformed(ActionEvent e) {

        String codeInput = codeText.getText();
        if (verifyCode.equals(codeInput)) {
            LoginFrame.REG_PHONE = phone;
            if (name != null && !"".equals(name.trim())) {
                LoginFrame.REG_NAME = name;
            }
            SerialPort serialPort = PortManager.getSerialPort();
            if (serialPort != null) {
                String systemPassword = user.getSystemPassword();
                try {
                    SerialPortUtils.sendToPort(serialPort, CommandUtils.registerCommand(systemPassword, deviceType));
                } catch (SendDataToSerialPortFailure | SerialPortOutputStreamCloseFailure sendDataToSerialPortFailure) {
                    sendDataToSerialPortFailure.printStackTrace();
                }
            }
            rechargeFrame.setEnabled(true);
            rechargeFrame.tipLabel.setText("正在准备注册，请将卡片放至充值机");
            this.dispose();
            registerFrame.dispose();
        } else {
            JOptionPane.showMessageDialog(null, "验证码错误！");
            codeText.setText("");
            codeText.requestFocus();
        }
    }

    private void verifyWindowClosing(WindowEvent e) {
        registerFrame.setEnabled(true);
        rechargeFrame.setEnabled(true);
    }

    private void initComponents() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                VerifyFrame.this.verifyWindowClosing(e);
            }
        });
        Container contentPane = this.getContentPane();
        contentPane.setLayout(null);

        JLabel attentionLabel = new JLabel();
        attentionLabel.setText("请输入短信验证码 : ");
        attentionLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 18));
        contentPane.add(attentionLabel);
        attentionLabel.setBounds(new Rectangle(new Point(15, 50), attentionLabel.getPreferredSize()));

        codeText = new JTextField();
        contentPane.add(codeText);
        codeText.setBounds(70, 110, 210, codeText.getPreferredSize().height);

        confirmButton = new JButton();
        confirmButton.setText("确定");
        confirmButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        confirmButton.setBackground(new Color(180, 205, 205));
        confirmButton.setBorder(BorderFactory.createRaisedBevelBorder());
        confirmButton.addActionListener(this::confirmButtonActionPerformed);
        contentPane.add(confirmButton);
        confirmButton.setBounds(100, 160, 70, 30);

        JButton cancelButton = new JButton();
        cancelButton.setText("取消");
        cancelButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        cancelButton.setBackground(new Color(180, 205, 205));
        cancelButton.setBorder(BorderFactory.createRaisedBevelBorder());
        cancelButton.addActionListener(this::cancelButtonActionPerformed);
        contentPane.add(cancelButton);
        cancelButton.setBounds(180, 160, 70, 30);

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
