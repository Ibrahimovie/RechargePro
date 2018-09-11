package swing;

import java.text.*;
import java.util.regex.*;

import commands.*;
import serial.*;
import exception.*;
import bean.manager.*;
import gnu.io.*;
import bean.*;
import utils.*;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

/**
 * @author kingfans
 */
public class QueryToRechargeFrame extends JFrame {
    private RechargeFrame frame;
    private String cardNum;
    private int cardType;
    private int deviceType;
    private int lastTime;
    private int startTime;
    private int isReturn;
    private int formerValidDay;
    private int formerBalance;
    private int formerRechargeTime;
    private int formerPayRate;
    private int formerPowerRate;
    private JTextField moneyText;
    private JTextField validText;
    private JTextField rechargeText;
    private JTextField payRateText;
    private JTextField powerRateText;
    private DecimalFormat df;

    public QueryToRechargeFrame(RechargeFrame frame, String cardNum, int cardType, int deviceType, int lastTime, int startTime, int isReturn,
                                int formerValidDay, int formerBalance, int formerRechargeTime, int formerPayRate, int formerPowerRate) {
        this.frame = frame;
        this.cardNum = cardNum;
        this.cardType = cardType;
        this.deviceType = deviceType;
        this.lastTime = lastTime;
        this.startTime = startTime;
        this.isReturn = isReturn;
        this.formerValidDay = formerValidDay;
        this.formerBalance = formerBalance;
        this.formerRechargeTime = formerRechargeTime;
        this.formerPayRate = formerPayRate;
        this.formerPowerRate = formerPowerRate;
        df = new DecimalFormat("0.0");
        initComponents(cardNum, cardType);
        this.setSize(510, 390);
        this.setTitle("充值界面");
        Image icon = Toolkit.getDefaultToolkit().getImage("resources/dk_logo.png");
        this.setIconImage(icon);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void confirmButtonActionPerformed(ActionEvent e) {
        String regEx = "^[0-9]+([.][0-9]{1}){0,1}$";//一位小数或整数
        Pattern pattern = Pattern.compile(regEx);
        if (deviceType == 0) {
            String moneyInput = moneyText.getText();
            if (pattern.matcher(moneyInput).matches()) {
                int money = (int) (Double.parseDouble(moneyInput) * 10.0);
                if (money + formerBalance > 50000) {
                    JOptionPane.showMessageDialog(null, "账户余额不得超过5000元！");
                    moneyText.addFocusListener(new JTextFieldHintListener(moneyText, "卡内余额：" + df.format(formerBalance / 10.0f)));
                    moneyText.requestFocus();
                } else {
                    SerialPort serialPort = PortManager.getSerialPort();
                    User user = UserManager.getUser();
                    if (serialPort != null) {
                        String systemPassword = user.getSystemPassword();
                        frame.tipLabel.setText("请再次将卡片放至充值机");
                        try {
                            SerialPortUtils.sendToPort(serialPort, CommandUtils.rechargeCommand(deviceType, money, 0, 0, 0, 0, 0, 0, isReturn, systemPassword));
                        } catch (SendDataToSerialPortFailure | SerialPortOutputStreamCloseFailure sendDataToSerialPortFailure) {
                            sendDataToSerialPortFailure.printStackTrace();
                        }
                    }
                    if (CardManager.getCardByNum(cardNum) != null) {
                        Card card = CardManager.getCardByNum(cardNum);
                        card.setCardType(cardType);
                        card.setTopUp(money);
                    } else {
                        Card card = new Card(cardNum);
                        card.setCardType(cardType);
                        card.setTopUp(money);
                        CardManager.addCard(cardNum, card);
                    }
                    frame.setEnabled(true);
                    LoginFrame.IS_RECHARGE = 0;
                    this.dispose();
                }
            } else {
                JOptionPane.showMessageDialog(null, "请按照正确格式输入充值金额，且最多一位小数！");
                moneyText.addFocusListener(new JTextFieldHintListener(moneyText, "卡内余额：" + df.format(formerBalance / 10.0f)));
                moneyText.requestFocus();
            }
        } else if (cardType == 3) {
            String moneyInput = moneyText.getText();
            String validDayInput = (validText.getText()==null||"".equals(validText.getText().trim())||validText.getText().contains("当前"))? String.valueOf(formerValidDay) :validText.getText();
            String rechargeTimeInput = (rechargeText.getText()==null||"".equals(rechargeText.getText().trim())||rechargeText.getText().contains("当前"))?df.format(formerRechargeTime / 10.0f):rechargeText.getText();
            String payRateInput = (payRateText.getText()==null||"".equals(payRateText.getText().trim())||payRateText.getText().contains("当前"))?df.format(formerPayRate / 10.0f):payRateText.getText();
            String powerRateInput = (powerRateText.getText()==null||"".equals(powerRateText.getText().trim())||powerRateText.getText().contains("当前"))?df.format(formerPowerRate / 100.0f):powerRateText.getText();
            int money, validDay, rechargeTime, payRate, powerRate;
            if (pattern.matcher(moneyInput).matches() && pattern.matcher(rechargeTimeInput).matches() && pattern.matcher(payRateInput).matches()
                    && pattern.matcher(validDayInput).matches() && pattern.matcher(powerRateInput).matches()) {
                money = (int) (Double.parseDouble(moneyInput) * 10.0);
                if (money + formerBalance > 50000) {
                    JOptionPane.showMessageDialog(null, "账户余额不得超过5000元！");
                    moneyText.addFocusListener(new JTextFieldHintListener(moneyText, "卡内余额：" + df.format(formerBalance / 10.0f)));
                    validText.addFocusListener(new JTextFieldHintListener(validText, "当前：" + formerValidDay));
                    rechargeText.addFocusListener(new JTextFieldHintListener(rechargeText, "当前：" + df.format(formerRechargeTime / 10.0f)));
                    payRateText.addFocusListener(new JTextFieldHintListener(payRateText, "当前：" + df.format(formerPayRate / 10.0f)));
                    powerRateText.addFocusListener(new JTextFieldHintListener(powerRateText, "当前：" + df.format(formerPowerRate / 100.0f)));
                    moneyText.requestFocus();
                } else {
                    validDay = (int) Double.parseDouble(validDayInput);
                    rechargeTime = (int) (Double.parseDouble(rechargeTimeInput) * 10.0);
                    payRate = (int) (Double.parseDouble(payRateInput) * 10.0);
                    powerRate = (int) (Double.parseDouble(powerRateInput) * 100.0);
                    if (validDay > 200 || rechargeTime > 255 || payRate > 255 || powerRate > 200) {
                        JOptionPane.showMessageDialog(null, "请按照提示输入正确格式，且最多一位小数！");
                        moneyText.addFocusListener(new JTextFieldHintListener(moneyText, "卡内余额：" + df.format(formerBalance / 10.0f)));
                        validText.addFocusListener(new JTextFieldHintListener(validText, "当前：" + formerValidDay));
                        rechargeText.addFocusListener(new JTextFieldHintListener(rechargeText, "当前：" + df.format(formerRechargeTime / 10.0f)));
                        payRateText.addFocusListener(new JTextFieldHintListener(payRateText, "当前：" + df.format(formerPayRate / 10.0f)));
                        powerRateText.addFocusListener(new JTextFieldHintListener(powerRateText, "当前：" + df.format(formerPowerRate / 100.0f)));
                        moneyText.requestFocus();
                    } else {
                        SerialPort serialPort = PortManager.getSerialPort();
                        User user = UserManager.getUser();
                        if (serialPort != null) {
                            String systemPassword = user.getSystemPassword();
                            frame.tipLabel.setText("请再次将卡片放至充值机");
                            try {
                                SerialPortUtils.sendToPort(serialPort, CommandUtils.rechargeCommand(deviceType, money, lastTime, startTime, validDay, rechargeTime, payRate, powerRate, isReturn, systemPassword));
                            } catch (SendDataToSerialPortFailure | SerialPortOutputStreamCloseFailure sendDataToSerialPortFailure) {
                                sendDataToSerialPortFailure.printStackTrace();
                            }

                        }
                        if (CardManager.getCardByNum(cardNum) != null) {
                            Card card = CardManager.getCardByNum(cardNum);
                            card.setCardType(cardType);
                            card.setValidDay(validDay);
                            card.setRechargeTime(rechargeTime);
                            card.setPayRate(payRate);
                            card.setPowerRate(powerRate);
                            card.setTopUp(money);
                        } else {
                            Card card = new Card(cardNum);
                            card.setCardType(cardType);
                            card.setValidDay(validDay);
                            card.setRechargeTime(rechargeTime);
                            card.setPayRate(payRate);
                            card.setPowerRate(powerRate);
                            card.setTopUp(money);
                            CardManager.addCard(cardNum, card);
                        }
                        frame.setEnabled(true);
                        LoginFrame.IS_RECHARGE = 0;
                        this.dispose();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "请按照提示输入正确格式，且最多一位小数！");
                moneyText.addFocusListener(new JTextFieldHintListener(moneyText, "卡内余额：" + df.format(formerBalance / 10.0f)));
                validText.addFocusListener(new JTextFieldHintListener(validText, "当前：" + formerValidDay));
                rechargeText.addFocusListener(new JTextFieldHintListener(rechargeText, "当前：" + df.format(formerRechargeTime / 10.0f)));
                payRateText.addFocusListener(new JTextFieldHintListener(payRateText, "当前：" + df.format(formerPayRate / 10.0f)));
                powerRateText.addFocusListener(new JTextFieldHintListener(powerRateText, "当前：" + df.format(formerPowerRate / 100.0f)));
                moneyText.requestFocus();
            }
        } else {
            String moneyInput = moneyText.getText();
            String rechargeTimeInput = (rechargeText.getText()==null||"".equals(rechargeText.getText().trim())||rechargeText.getText().contains("当前"))?df.format(formerRechargeTime / 10.0f):rechargeText.getText();
            String payRateInput = (payRateText.getText()==null||"".equals(payRateText.getText().trim())||payRateText.getText().contains("当前"))?df.format(formerPayRate / 10.0f):payRateText.getText();
            String powerRateInput = (powerRateText.getText()==null||"".equals(powerRateText.getText().trim())||powerRateText.getText().contains("当前"))?df.format(formerPowerRate / 100.0f):powerRateText.getText();
            if (pattern.matcher(moneyInput).matches() && pattern.matcher(rechargeTimeInput).matches() && pattern.matcher(payRateInput).matches() && pattern.matcher(powerRateInput).matches()) {
                int money = (int) (Double.parseDouble(moneyInput) * 10.0);
                if (money + formerBalance > 50000) {
                    JOptionPane.showMessageDialog(null, "账户余额不得超过5000元！");
                    moneyText.addFocusListener(new JTextFieldHintListener(moneyText, "卡内余额：" + df.format(formerBalance / 10.0f)));
                    rechargeText.addFocusListener(new JTextFieldHintListener(rechargeText, "当前：" + df.format(formerRechargeTime / 10.0f)));
                    payRateText.addFocusListener(new JTextFieldHintListener(payRateText, "当前：" + df.format(formerPayRate / 10.0f)));
                    powerRateText.addFocusListener(new JTextFieldHintListener(powerRateText, "当前：" + df.format(formerPowerRate / 100.0f)));
                    moneyText.requestFocus();
                } else {
                    int rechargeTime = (int) (Double.parseDouble(rechargeTimeInput) * 10.0);
                    int payRate = (int) (Double.parseDouble(payRateInput) * 10.0);
                    int powerRate = (int) (Double.parseDouble(powerRateInput) * 100.0);
                    if (rechargeTime > 255 || payRate > 255 || powerRate > 200) {
                        JOptionPane.showMessageDialog(null, "请按照提示输入正确格式，且最多一位小数！");
                        moneyText.addFocusListener(new JTextFieldHintListener(moneyText, "卡内余额：" + df.format(formerBalance / 10.0f)));
                        rechargeText.addFocusListener(new JTextFieldHintListener(rechargeText, "当前：" + df.format(formerRechargeTime / 10.0f)));
                        payRateText.addFocusListener(new JTextFieldHintListener(payRateText, "当前：" + df.format(formerPayRate / 10.0f)));
                        powerRateText.addFocusListener(new JTextFieldHintListener(powerRateText, "当前：" + df.format(formerPowerRate / 100.0f)));
                        moneyText.requestFocus();
                    } else {
                        SerialPort serialPort = PortManager.getSerialPort();
                        User user = UserManager.getUser();
                        if (serialPort != null) {
                            String systemPassword = user.getSystemPassword();
                            frame.tipLabel.setText("请再次将卡片放至充值机");
                            try {
                                SerialPortUtils.sendToPort(serialPort, CommandUtils.rechargeCommand(deviceType, money, lastTime, startTime, 0, rechargeTime, payRate, powerRate, isReturn, systemPassword));
                            } catch (SendDataToSerialPortFailure | SerialPortOutputStreamCloseFailure sendDataToSerialPortFailure) {
                                sendDataToSerialPortFailure.printStackTrace();
                            }

                        }
                        if (CardManager.getCardByNum(cardNum) != null) {
                            Card card = CardManager.getCardByNum(cardNum);
                            card.setCardType(cardType);
                            card.setValidDay(0);
                            card.setRechargeTime(rechargeTime);
                            card.setPayRate(payRate);
                            card.setPowerRate(powerRate);
                            card.setTopUp(money);
                        } else {
                            Card card = new Card(cardNum);
                            card.setCardType(cardType);
                            card.setValidDay(0);
                            card.setRechargeTime(rechargeTime);
                            card.setPayRate(payRate);
                            card.setPowerRate(powerRate);
                            card.setTopUp(money);
                            CardManager.addCard(cardNum, card);
                        }
                        frame.setEnabled(true);
                        LoginFrame.IS_RECHARGE = 0;
                        this.dispose();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "请按照提示输入正确格式，且最多一位小数！");
                moneyText.addFocusListener(new JTextFieldHintListener(moneyText, "卡内余额：" + df.format(formerBalance / 10.0f)));
                rechargeText.addFocusListener(new JTextFieldHintListener(rechargeText, "当前：" + df.format(formerRechargeTime / 10.0f)));
                payRateText.addFocusListener(new JTextFieldHintListener(payRateText, "当前：" + df.format(formerPayRate / 10.0f)));
                powerRateText.addFocusListener(new JTextFieldHintListener(powerRateText, "当前：" + df.format(formerPowerRate / 100.0f)));
                moneyText.requestFocus();
            }
        }
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        frame.tipLabel.setText("欢迎使用得康充值系统");
        frame.setEnabled(true);
        LoginFrame.IS_RECHARGE = 0;
        this.dispose();
    }

    private void rechargeWindowClosing(WindowEvent e) {
        frame.tipLabel.setText("欢迎使用得康充值系统");
        frame.setEnabled(true);
        LoginFrame.IS_RECHARGE = 0;
    }

    private void initComponents(String cardNum, int cardType) {
        JButton confirmButton = new JButton();
        JButton cancelButton = new JButton();
        JLabel label1 = new JLabel();
        JLabel label2 = new JLabel();
        JLabel label3 = new JLabel();
        JLabel label4 = new JLabel();
        JLabel label5 = new JLabel();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                rechargeWindowClosing(e);
            }
        });

        Container contentPane = this.getContentPane();
        contentPane.setLayout(null);

        JLabel cardNumLabel = new JLabel();
        cardNumLabel.setText("卡号 ：");
        cardNumLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 18));
        contentPane.add(cardNumLabel);
        cardNumLabel.setBounds(new Rectangle(new Point(20, 40), cardNumLabel.getPreferredSize()));

        JLabel cardNumText = new JLabel();
        cardNumText.setText(cardNum);
        cardNumText.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        contentPane.add(cardNumText);
        cardNumText.setBounds(new Rectangle(new Point(80, 46), cardNumText.getPreferredSize()));

        JLabel balanceLabel = new JLabel();
        balanceLabel.setText("余额 ：");
        balanceLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 18));
        contentPane.add(balanceLabel);
        balanceLabel.setBounds(new Rectangle(new Point(180, 40), balanceLabel.getPreferredSize()));

        JLabel balanceText = new JLabel();
        balanceText.setText(df.format(formerBalance/10.0f));
        balanceText.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        contentPane.add(balanceText);
        balanceText.setBounds(new Rectangle(new Point(240, 46), balanceText.getPreferredSize()));

        JLabel cardTypeLabel = new JLabel();
        cardTypeLabel.setText("类型 ： ");
        cardTypeLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 18));
        contentPane.add(cardTypeLabel);
        cardTypeLabel.setBounds(new Rectangle(new Point(320, 40), cardTypeLabel.getPreferredSize()));

        JLabel cardTypeText = new JLabel();
        cardTypeText.setText(Utils.getStringCardType(cardType));
        cardTypeText.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        contentPane.add(cardTypeText);
        cardTypeText.setBounds(new Rectangle(new Point(380, 45), cardTypeText.getPreferredSize()));

        JLabel pleaseLabel = new JLabel();
        pleaseLabel.setText("请输入 ： ");
        pleaseLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 16));
        contentPane.add(pleaseLabel);
        pleaseLabel.setBounds(new Rectangle(new Point(40, 90), pleaseLabel.getPreferredSize()));

        JLabel moneyLabel = new JLabel();
        moneyLabel.setText("充值金额");
        moneyLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
        contentPane.add(moneyLabel);
        moneyLabel.setBounds(new Rectangle(new Point(55, 130), moneyLabel.getPreferredSize()));

        moneyText = new JTextField();
        contentPane.add(moneyText);
        moneyText.setBounds(130, 125, 120, moneyText.getPreferredSize().height);
        moneyText.addFocusListener(new JTextFieldHintListener(moneyText, "卡内余额：" + df.format(formerBalance / 10.0f)));

        JLabel validLabel = new JLabel();
        validLabel.setText("有效天数");
        validLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
        contentPane.add(validLabel);
        validLabel.setBounds(new Rectangle(new Point(55, 170), validLabel.getPreferredSize()));

        validText = new JTextField();
        contentPane.add(validText);
        validText.setBounds(130, 165, 120, 30);
        if (cardType == 0 || cardType == 1 || cardType == 2) {
            validText.setEditable(false);
        } else {
            validText.addFocusListener(new JTextFieldHintListener(validText, "当前：" + formerValidDay));
            validText.setEditable(true);
        }

        JLabel rechargeLabel = new JLabel();
        rechargeLabel.setText("充电时间");
        rechargeLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
        contentPane.add(rechargeLabel);
        rechargeLabel.setBounds(55, 210, 56, 20);

        JLabel payRateLabel = new JLabel();
        payRateLabel.setText("扣款费率");
        payRateLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
        contentPane.add(payRateLabel);
        payRateLabel.setBounds(55, 250, 56, 20);

        JLabel powerRateLabel = new JLabel();
        powerRateLabel.setText("最大功率");
        powerRateLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
        contentPane.add(powerRateLabel);
        powerRateLabel.setBounds(55, 290, 56, 20);

        rechargeText = new JTextField();
        contentPane.add(rechargeText);
        rechargeText.setBounds(130, 205, 120, 30);
        rechargeText.addFocusListener(new JTextFieldHintListener(rechargeText, "当前：" + df.format(formerRechargeTime / 10.0f)));

        payRateText = new JTextField();
        contentPane.add(payRateText);
        payRateText.setBounds(130, 245, 120, 30);
        payRateText.addFocusListener(new JTextFieldHintListener(payRateText, "当前：" + df.format(formerPayRate / 10.0f)));

        powerRateText = new JTextField();
        contentPane.add(powerRateText);
        powerRateText.setBounds(130, 285, 120, 30);
        powerRateText.addFocusListener(new JTextFieldHintListener(powerRateText, "当前：" + df.format(formerPowerRate / 100.0f)));

        if (cardType == 0) {
            rechargeText.setEditable(false);
            payRateText.setEditable(false);
            powerRateText.setEditable(false);
        }

        confirmButton.setText("充值");
        confirmButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        confirmButton.addActionListener(this::confirmButtonActionPerformed);
        contentPane.add(confirmButton);
        confirmButton.setBounds(385, 245, 70, 30);
        confirmButton.setBackground(new Color(180, 205, 205));
        confirmButton.setBorder(BorderFactory.createRaisedBevelBorder());

        cancelButton.setText("取消");
        cancelButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        cancelButton.addActionListener(this::cancelButtonActionPerformed);
        contentPane.add(cancelButton);
        cancelButton.setBounds(385, 285, 70, 30);
        cancelButton.setBackground(new Color(180, 205, 205));
        cancelButton.setBorder(BorderFactory.createRaisedBevelBorder());

        label1.setText("(0~5000元)");
        label1.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        contentPane.add(label1);
        label1.setBounds(new Rectangle(new Point(265, 130), label1.getPreferredSize()));

        label2.setText("(0~200天)");
        label2.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        contentPane.add(label2);
        label2.setBounds(new Rectangle(new Point(265, 170), label2.getPreferredSize()));

        label3.setText("(0~25.5h)");
        label3.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        contentPane.add(label3);
        label3.setBounds(new Rectangle(new Point(265, 210), label3.getPreferredSize()));

        label4.setText("(0~25.5元)");
        label4.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        contentPane.add(label4);
        label4.setBounds(new Rectangle(new Point(265, 250), label4.getPreferredSize()));

        label5.setText("(0~2.0KW)");
        label5.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        contentPane.add(label5);
        label5.setBounds(new Rectangle(new Point(265, 290), label5.getPreferredSize()));

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
