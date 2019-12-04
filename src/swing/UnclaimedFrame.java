package swing;

import bean.Card;
import bean.User;
import bean.manager.CardManager;
import bean.manager.PortManager;
import bean.manager.UserManager;
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
import java.text.DecimalFormat;
import java.util.regex.Pattern;

/**
 * @Author: kingfans
 * @Date: 2018/9/11
 */
public class UnclaimedFrame extends JFrame {

    private RechargeFrame frame;
    private String cardNum;
    private int preBalance;
    private int formerBalance;
    private int deviceType;
    private int lastTime;
    private int startTime;
    private int validDay;
    private int rechargeTime;
    private int payRate;
    private int powerRate;
    private int isReturn;
    //    private JTextField input;
    private JButton confirmButton;
    private DecimalFormat df;

    public UnclaimedFrame(RechargeFrame rechargeFrame, String cardNum, int preBalance, int formerBalance, int deviceType, int lastTime,
                          int startTime, int validDay, int rechargeTime, int payRate, int powerRate, int isReturn) {
        this.frame = rechargeFrame;
        this.cardNum = cardNum;
        this.preBalance = preBalance;
        this.formerBalance = formerBalance;//x10以后
        this.deviceType = deviceType;
        this.lastTime = lastTime;
        this.startTime = startTime;
        this.validDay = validDay;
        this.rechargeTime = rechargeTime;
        this.payRate = payRate;
        this.powerRate = powerRate;
        this.isReturn = isReturn;
        df = new DecimalFormat("0.0");
        initComponents();
        this.setSize(360, 300);
        this.setTitle("圈存领取界面");
        Image icon = Toolkit.getDefaultToolkit().getImage("resources/dk_logo.png");
        this.setIconImage(icon);
        this.setLocationRelativeTo(null);
        this.getRootPane().setDefaultButton(confirmButton);
        this.setVisible(true);
    }

    private void confirmActionPerformed(ActionEvent e) {
        if (preBalance == 0) {
            JOptionPane.showMessageDialog(null, "无可领取金额！");
        } else {
            if (preBalance + (formerBalance / 10.0f) > 5000) {
                JOptionPane.showMessageDialog(null, "卡内余额不得超过5000元！");
            } else {
                SerialPort serialPort = PortManager.getSerialPort();
                User user = UserManager.getUser();
                if (serialPort != null) {
                    String systemPassword = user.getSystemPassword();
                    frame.tipLabel.setText("请再次将卡片放至充值机");
                    try {
                        LoginFrame.PRE_HIS_MARK = 1;
                        SerialPortUtils.sendToPort(serialPort, CommandUtils.rechargeCommand(deviceType, preBalance * 10, lastTime,
                                startTime, validDay, rechargeTime, payRate, powerRate, isReturn, systemPassword));
                    } catch (SendDataToSerialPortFailure | SerialPortOutputStreamCloseFailure sendDataToSerialPortFailure) {
                        sendDataToSerialPortFailure.printStackTrace();
                    }
                }
                int cardType = ServiceImpl.getInstance().getCardTypeByNum(cardNum);
                if (CardManager.getCardByNum(cardNum) != null) {
                    Card card = CardManager.getCardByNum(cardNum);
                    card.setTopUp(preBalance * 10);
                    card.setValidDay(validDay);
                    card.setCardType(cardType);
                    card.setRechargeTime(rechargeTime);
                    card.setPayRate(payRate);
                    card.setPowerRate(powerRate);
                } else {
                    Card card = new Card(cardNum);
                    card.setTopUp(preBalance * 10);
                    card.setValidDay(validDay);
                    card.setCardType(cardType);
                    card.setRechargeTime(rechargeTime);
                    card.setPayRate(payRate);
                    card.setPowerRate(powerRate);
                    CardManager.addCard(cardNum, card);
                }
                frame.setEnabled(true);
                LoginFrame.IS_RECHARGE = 0;
                this.dispose();
            }
        }

    }


    private void cancelActionPerformed(ActionEvent e) {
        frame.tipLabel.setText("欢迎使用得康充值系统");
        frame.setEnabled(true);
        LoginFrame.IS_RECHARGE = 0;
        this.dispose();
    }

    private void unclaimedWindowClosing(WindowEvent e) {
        frame.tipLabel.setText("欢迎使用得康充值系统");
        frame.setEnabled(true);
        LoginFrame.IS_RECHARGE = 0;
    }

    private void initComponents() {

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                unclaimedWindowClosing(e);
            }
        });

        Container contentPane = this.getContentPane();
        contentPane.setLayout(null);

        JLabel formerBalanceLabel = new JLabel();
        formerBalanceLabel.setText("卡内余额 : ");
        formerBalanceLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 20));
        contentPane.add(formerBalanceLabel);
        formerBalanceLabel.setBounds(new Rectangle(new Point(100, 40), formerBalanceLabel.getPreferredSize()));

        JLabel formerBalanceText = new JLabel();
        formerBalanceText.setText(String.valueOf(df.format(formerBalance / 10.0f)));
        formerBalanceText.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 18));
        contentPane.add(formerBalanceText);
        formerBalanceText.setBounds(new Rectangle(new Point(210, 43), formerBalanceText.getPreferredSize()));

        JLabel preBalanceLabel = new JLabel();
        preBalanceLabel.setText("待 领 取  : ");
        preBalanceLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 20));
        contentPane.add(preBalanceLabel);
        preBalanceLabel.setBounds(new Rectangle(new Point(100, 90), preBalanceLabel.getPreferredSize()));

        JLabel preBalanceText = new JLabel();
        preBalanceText.setText(String.valueOf(preBalance));
        preBalanceText.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 18));
        contentPane.add(preBalanceText);
        preBalanceText.setBounds(new Rectangle(new Point(210, 93), preBalanceText.getPreferredSize()));


        JLabel title = new JLabel();
        title.setText("（暂仅支持全额领取）  ");
        title.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
        contentPane.add(title);
        title.setBounds(new Rectangle(new Point(90, 130), title.getPreferredSize()));

//        input = new JTextField();
//        input.setFont(new Font("Dialog", Font.PLAIN, 14));
//        contentPane.add(input);
//        input.setBounds(120, 130, 160, 35);


        confirmButton = new JButton();
        confirmButton.setText("领取");
        confirmButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        confirmButton.addActionListener(this::confirmActionPerformed);
        contentPane.add(confirmButton);
        confirmButton.setBounds(90, 180, 73, 35);
        confirmButton.setBackground(new Color(180, 205, 205));
        confirmButton.setBorder(BorderFactory.createRaisedBevelBorder());

        JButton cancelButton = new JButton();
        cancelButton.setText("取消");
        cancelButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        cancelButton.addActionListener(this::cancelActionPerformed);
        contentPane.add(cancelButton);
        cancelButton.setBounds(180, 180, 73, 35);
        cancelButton.setBackground(new Color(180, 205, 205));
        cancelButton.setBorder(BorderFactory.createRaisedBevelBorder());

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
