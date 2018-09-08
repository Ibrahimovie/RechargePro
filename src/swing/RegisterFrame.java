package swing;

import bean.*;

import java.net.URLEncoder;
import java.util.Objects;
import java.util.regex.*;

import bean.manager.*;
import com.alibaba.fastjson.JSONObject;
import commands.*;
import serial.*;
import exception.*;
import gnu.io.*;
import utils.Constants;
import utils.HttpRequest;
import utils.Utils;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

/**
 * @author kingfans
 */
public class RegisterFrame extends JFrame {
    private User user;
    private RechargeFrame rechargeFrame;
    private JComboBox<String> deviceTypeChooser;
    private JTextField phoneText;
    private JTextField nameText;
    private JButton confirmButton;

    public RegisterFrame(User user, RechargeFrame rechargeFrame) {
        initComponents(user);
        this.user = user;
        this.rechargeFrame = rechargeFrame;
        this.setSize(300, 290);
        this.setTitle("注册");
        this.getRootPane().setDefaultButton(confirmButton);
        Image icon = Toolkit.getDefaultToolkit().getImage("resources/dk_logo.png");
        this.setIconImage(icon);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void confirmButtonActionPerformed(ActionEvent e) {
        int deviceType = deviceTypeChooser.getSelectedIndex();
        String name = nameText.getText();
        String phoneInput = phoneText.getText();
        String PHONE_NUMBER_REG = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$";
        Pattern pattern = Pattern.compile(PHONE_NUMBER_REG);
        if (pattern.matcher(phoneInput).matches()) {
//            String url = "http://47.96.87.126:8654/recharge/SmsVerifyServlet";
//            HttpRequest hr = new HttpRequest();
//            hr.addParam("phone", phoneInput);
//            hr.setMethod("POST");
//            hr.setURL(url);
//            String resp = hr.Send();
//            System.out.println(resp);
//            JSONObject jsonObject = JSONObject.parseObject(resp);
//            if ("0".equals(jsonObject.getString("code"))) {
//                String verifyCode = jsonObject.getString("num");
//                new VerifyFrame(user, verifyCode, rechargeFrame, this, phoneInput, name, deviceType);
                new VerifyFrame(user, "123456", rechargeFrame, this, phoneInput, name, deviceType);
                rechargeFrame.setEnabled(false);
                this.setEnabled(false);
//            }

        } else {
            LoginFrame.REG_PHONE = "-";
            JOptionPane.showMessageDialog(null, "请输入正确的手机号！");
            phoneText.setText("");
            phoneText.requestFocus();
        }
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        rechargeFrame.setEnabled(true);
        this.dispose();
    }

    private void registerWindowClosing(WindowEvent e) {
        rechargeFrame.setEnabled(true);
    }

    private void initComponents(User user) {

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                RegisterFrame.this.registerWindowClosing(e);
            }
        });
        Container contentPane = this.getContentPane();
        contentPane.setLayout(null);

        JLabel label1 = new JLabel();
        label1.setText("  设备类型       ：");
        label1.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        contentPane.add(label1);
        label1.setBounds(new Rectangle(new Point(35, 125), label1.getPreferredSize()));

        deviceTypeChooser = new JComboBox<>();
        deviceTypeChooser.setModel(new DefaultComboBoxModel<>(new String[]{"Q10/CDZ", "Q20"}));
        contentPane.add(deviceTypeChooser);
        deviceTypeChooser.setBounds(150, 120, 105, deviceTypeChooser.getPreferredSize().height);

        confirmButton = new JButton();
        confirmButton.setText("验证");
        confirmButton.addActionListener(this::confirmButtonActionPerformed);
        contentPane.add(confirmButton);
        confirmButton.setBounds(60, 180, 70, 30);
        confirmButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        confirmButton.setBackground(new Color(180, 205, 205));
        confirmButton.setBorder(BorderFactory.createRaisedBevelBorder());

        JButton cancelButton = new JButton();
        cancelButton.setText("取消");
        cancelButton.addActionListener(this::cancelButtonActionPerformed);
        contentPane.add(cancelButton);
        cancelButton.setBounds(155, 180, 70, 30);
        cancelButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        cancelButton.setBackground(new Color(180, 205, 205));
        cancelButton.setBorder(BorderFactory.createRaisedBevelBorder());

        JLabel phoneLabel = new JLabel();
        phoneLabel.setText(" 手机号码 （必填）：");
        phoneLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        contentPane.add(phoneLabel);
        phoneLabel.setBounds(new Rectangle(new Point(25, 80), phoneLabel.getPreferredSize()));

        phoneText = new JTextField();
        contentPane.add(phoneText);
        phoneText.setBounds(150, 75, 110, phoneText.getPreferredSize().height);

        JLabel nameLabel = new JLabel();
        nameLabel.setText("  姓名（选填）     ：");
        nameLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        contentPane.add(nameLabel);
        nameLabel.setBounds(new Rectangle(new Point(25, 35), nameLabel.getPreferredSize()));

        nameText = new JTextField();
        contentPane.add(nameText);
        nameText.setBounds(150, 30, 110, nameText.getPreferredSize().height);

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
