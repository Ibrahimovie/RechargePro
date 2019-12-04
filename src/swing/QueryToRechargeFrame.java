package swing;

import java.text.*;
import java.util.Calendar;
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
    private int isMonthUnUse;
    private int remainDay;
    private String remainDayText;
    private JTextField moneyText;
    private JTextField validText;
    private JTextField rechargeText;
    private JTextField payRateText;
    private JTextField powerRateText;
    private DecimalFormat df;

    public QueryToRechargeFrame(RechargeFrame frame, String cardNum, int cardType, int deviceType, int lastTime, int startTime, int isReturn,
                                int formerValidDay, int formerBalance, int formerRechargeTime, int formerPayRate, int formerPowerRate, int isMonthUnUse, int remainDay) {
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
        this.isMonthUnUse = isMonthUnUse;
        this.remainDay = remainDay;
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
        int is_negative = 0;
        String regEx = "^[0-9]+([.][0-9]{1}){0,1}$";//一位小数或整数
        Pattern pattern = Pattern.compile(regEx);
        if (deviceType == 0) {
            //deviceType为0，Q10卡
            String moneyInput = moneyText.getText();
            if (moneyInput.contains("-")) {
                moneyInput = moneyInput.replace("-", "");
                is_negative = 1;
            }
            if (pattern.matcher(moneyInput).matches()) {
                int money;
                if (is_negative == 0) {
                    money = (int) (Double.parseDouble(moneyInput) * 10.0);
                } else {
                    money = -(int) (Double.parseDouble(moneyInput) * 10.0);
                }
                if (money + formerBalance > 50000) {
                    JOptionPane.showMessageDialog(null, "账户余额不得超过5000元！");
                    moneyText.addFocusListener(new JTextFieldHintListener(moneyText, "卡内余额：" + df.format(formerBalance / 10.0f)));
                    moneyText.requestFocus();
                } else if (money + formerBalance < 0) {
                    JOptionPane.showMessageDialog(null, "账户余额不得少于0元！");
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
            System.out.println("包月卡的充值");
            if (isMonthUnUse == 1) {
                System.out.println("未激活卡");
                String validTextText = validText.getText();
                System.out.println("validText输入值 : " + validTextText);
                if (validTextText == null || "".equals(validTextText.trim()) || validTextText.contains("剩余天数")) {
                    System.out.println("默认充值0天");
                    // 默认充值0天
                    String moneyInput = moneyText.getText();
                    System.out.println("moneyInput : " + moneyInput);
                    if (moneyInput.contains("-")) {
                        moneyInput = moneyInput.replace("-", "");
                        is_negative = 1;
                    }
                    System.out.println("rechargeText.getText : " + rechargeText.getText());
                    System.out.println("payRateText.getText : " + payRateText.getText());
                    System.out.println("powerRateText.getText : " + powerRateText.getText());
                    String validDayInput = String.valueOf(formerValidDay);
                    String rechargeTimeInput = (rechargeText.getText() == null || "".equals(rechargeText.getText().trim()) || rechargeText.getText().contains("当前")) ? df.format(formerRechargeTime / 10.0f) : rechargeText.getText();
                    String payRateInput = (payRateText.getText() == null || "".equals(payRateText.getText().trim()) || payRateText.getText().contains("当前")) ? df.format(formerPayRate / 10.0f) : payRateText.getText();
                    String powerRateInput = (powerRateText.getText() == null || "".equals(powerRateText.getText().trim()) || powerRateText.getText().contains("当前")) ? df.format(formerPowerRate / 100.0f) : powerRateText.getText();
                    int money, validDay, rechargeTime, payRate, powerRate;
                    if (pattern.matcher(moneyInput).matches() && pattern.matcher(rechargeTimeInput).matches() && pattern.matcher(payRateInput).matches()
                            && pattern.matcher(validDayInput).matches() && pattern.matcher(powerRateInput).matches()) {
                        if (is_negative == 0) {
                            money = (int) (Double.parseDouble(moneyInput) * 10.0);
                        } else {
                            money = -(int) (Double.parseDouble(moneyInput) * 10.0);
                        }
                        if (money + formerBalance > 50000) {
                            JOptionPane.showMessageDialog(null, "账户余额不得超过5000元！");
                            moneyText.addFocusListener(new JTextFieldHintListener(moneyText, "卡内余额：" + df.format(formerBalance / 10.0f)));
                            validText.addFocusListener(new JTextFieldHintListener(validText, "剩余天数：" + remainDay));
                            rechargeText.addFocusListener(new JTextFieldHintListener(rechargeText, "当前：" + df.format(formerRechargeTime / 10.0f)));
                            payRateText.addFocusListener(new JTextFieldHintListener(payRateText, "当前：" + df.format(formerPayRate / 10.0f)));
                            powerRateText.addFocusListener(new JTextFieldHintListener(powerRateText, "当前：" + df.format(formerPowerRate / 100.0f)));
                            moneyText.requestFocus();
                        } else if (money + formerBalance < 0) {
                            JOptionPane.showMessageDialog(null, "账户余额不得少于0元！");
                            moneyText.addFocusListener(new JTextFieldHintListener(moneyText, "卡内余额：" + df.format(formerBalance / 10.0f)));
                            validText.addFocusListener(new JTextFieldHintListener(validText, "剩余天数：" + remainDay));
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
                                validText.addFocusListener(new JTextFieldHintListener(validText, "剩余天数：" + remainDay));
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
                        validText.addFocusListener(new JTextFieldHintListener(validText, "剩余天数：" + remainDay));
                        rechargeText.addFocusListener(new JTextFieldHintListener(rechargeText, "当前：" + df.format(formerRechargeTime / 10.0f)));
                        payRateText.addFocusListener(new JTextFieldHintListener(payRateText, "当前：" + df.format(formerPayRate / 10.0f)));
                        powerRateText.addFocusListener(new JTextFieldHintListener(powerRateText, "当前：" + df.format(formerPowerRate / 100.0f)));
                        moneyText.requestFocus();
                    }
                } else {
                    System.out.println("需要充值" + validTextText + "天");
                    String moneyInput = moneyText.getText();
                    System.out.println("moneyInput : " + moneyInput);
                    if (moneyInput.contains("-")) {
                        moneyInput = moneyInput.replace("-", "");
                        is_negative = 1;
                    }
                    int sum = formerValidDay + Integer.parseInt(validTextText);
                    System.out.println("sum : " + sum);
                    String validDayInput = String.valueOf(sum);
                    System.out.println("最后的validDayInput ： " + validDayInput);
                    String rechargeTimeInput = (rechargeText.getText() == null || "".equals(rechargeText.getText().trim()) || rechargeText.getText().contains("当前")) ? df.format(formerRechargeTime / 10.0f) : rechargeText.getText();
                    String payRateInput = (payRateText.getText() == null || "".equals(payRateText.getText().trim()) || payRateText.getText().contains("当前")) ? df.format(formerPayRate / 10.0f) : payRateText.getText();
                    String powerRateInput = (powerRateText.getText() == null || "".equals(powerRateText.getText().trim()) || powerRateText.getText().contains("当前")) ? df.format(formerPowerRate / 100.0f) : powerRateText.getText();
                    int money, validDay, rechargeTime, payRate, powerRate;
                    if (pattern.matcher(moneyInput).matches() && pattern.matcher(rechargeTimeInput).matches() && pattern.matcher(payRateInput).matches()
                            && pattern.matcher(validDayInput).matches() && pattern.matcher(powerRateInput).matches()) {
                        if (is_negative == 0) {
                            money = (int) (Double.parseDouble(moneyInput) * 10.0);
                        } else {
                            money = -(int) (Double.parseDouble(moneyInput) * 10.0);
                        }
                        if (money + formerBalance > 50000) {
                            JOptionPane.showMessageDialog(null, "账户余额不得超过5000元！");
                            moneyText.addFocusListener(new JTextFieldHintListener(moneyText, "卡内余额：" + df.format(formerBalance / 10.0f)));
                            validText.addFocusListener(new JTextFieldHintListener(validText, "剩余天数：" + remainDayText));
                            rechargeText.addFocusListener(new JTextFieldHintListener(rechargeText, "当前：" + df.format(formerRechargeTime / 10.0f)));
                            payRateText.addFocusListener(new JTextFieldHintListener(payRateText, "当前：" + df.format(formerPayRate / 10.0f)));
                            powerRateText.addFocusListener(new JTextFieldHintListener(powerRateText, "当前：" + df.format(formerPowerRate / 100.0f)));
                            moneyText.requestFocus();
                        } else if (money + formerBalance < 0) {
                            JOptionPane.showMessageDialog(null, "账户余额不得少于0元！");
                            moneyText.addFocusListener(new JTextFieldHintListener(moneyText, "卡内余额：" + df.format(formerBalance / 10.0f)));
                            validText.addFocusListener(new JTextFieldHintListener(validText, "剩余天数：" + remainDayText));
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
                                validText.addFocusListener(new JTextFieldHintListener(validText, "剩余天数：" + remainDayText));
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
                        validText.addFocusListener(new JTextFieldHintListener(validText, "剩余天数：" + remainDayText));
                        rechargeText.addFocusListener(new JTextFieldHintListener(rechargeText, "当前：" + df.format(formerRechargeTime / 10.0f)));
                        payRateText.addFocusListener(new JTextFieldHintListener(payRateText, "当前：" + df.format(formerPayRate / 10.0f)));
                        powerRateText.addFocusListener(new JTextFieldHintListener(powerRateText, "当前：" + df.format(formerPowerRate / 100.0f)));
                        moneyText.requestFocus();
                    }
                }
            } else {
                System.out.println("已激活卡");
                System.out.println("剩余天数 : " + remainDay);
                String validTextText = validText.getText();
                System.out.println("validText输入值 : " + validTextText);
                if (validTextText == null || "".equals(validTextText.trim()) || validTextText.contains("剩余天数")) {
                    System.out.println("默认充值0天");
                    //默认充值0天
                    String moneyInput = moneyText.getText();
                    System.out.println("moneyInput : " + moneyInput);
                    if (moneyInput.contains("-")) {
                        moneyInput = moneyInput.replace("-", "");
                        is_negative = 1;
                    }
                    System.out.println("rechargeText.getText : " + rechargeText.getText());
                    System.out.println("payRateText.getText : " + payRateText.getText());
                    System.out.println("powerRateText.getText : " + powerRateText.getText());
                    String validDayInput = String.valueOf(formerValidDay);
                    String rechargeTimeInput = (rechargeText.getText() == null || "".equals(rechargeText.getText().trim()) || rechargeText.getText().contains("当前")) ? df.format(formerRechargeTime / 10.0f) : rechargeText.getText();
                    String payRateInput = (payRateText.getText() == null || "".equals(payRateText.getText().trim()) || payRateText.getText().contains("当前")) ? df.format(formerPayRate / 10.0f) : payRateText.getText();
                    String powerRateInput = (powerRateText.getText() == null || "".equals(powerRateText.getText().trim()) || powerRateText.getText().contains("当前")) ? df.format(formerPowerRate / 100.0f) : powerRateText.getText();
                    int money, validDay, rechargeTime, payRate, powerRate;
                    if (pattern.matcher(moneyInput).matches() && pattern.matcher(rechargeTimeInput).matches() && pattern.matcher(payRateInput).matches()
                            && pattern.matcher(validDayInput).matches() && pattern.matcher(powerRateInput).matches()) {
                        if (is_negative == 0) {
                            money = (int) (Double.parseDouble(moneyInput) * 10.0);
                        } else {
                            money = -(int) (Double.parseDouble(moneyInput) * 10.0);
                        }
                        if (money + formerBalance > 50000) {
                            JOptionPane.showMessageDialog(null, "账户余额不得超过5000元！");
                            moneyText.addFocusListener(new JTextFieldHintListener(moneyText, "卡内余额：" + df.format(formerBalance / 10.0f)));
                            validText.addFocusListener(new JTextFieldHintListener(validText, "剩余天数：" + remainDay));
                            rechargeText.addFocusListener(new JTextFieldHintListener(rechargeText, "当前：" + df.format(formerRechargeTime / 10.0f)));
                            payRateText.addFocusListener(new JTextFieldHintListener(payRateText, "当前：" + df.format(formerPayRate / 10.0f)));
                            powerRateText.addFocusListener(new JTextFieldHintListener(powerRateText, "当前：" + df.format(formerPowerRate / 100.0f)));
                            moneyText.requestFocus();
                        } else if (money + formerBalance < 0) {
                            JOptionPane.showMessageDialog(null, "账户余额不得少于0元！");
                            moneyText.addFocusListener(new JTextFieldHintListener(moneyText, "卡内余额：" + df.format(formerBalance / 10.0f)));
                            validText.addFocusListener(new JTextFieldHintListener(validText, "剩余天数：" + remainDay));
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
                                validText.addFocusListener(new JTextFieldHintListener(validText, "剩余天数：" + remainDay));
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
                        validText.addFocusListener(new JTextFieldHintListener(validText, "剩余天数：" + remainDay));
                        rechargeText.addFocusListener(new JTextFieldHintListener(rechargeText, "当前：" + df.format(formerRechargeTime / 10.0f)));
                        payRateText.addFocusListener(new JTextFieldHintListener(payRateText, "当前：" + df.format(formerPayRate / 10.0f)));
                        powerRateText.addFocusListener(new JTextFieldHintListener(powerRateText, "当前：" + df.format(formerPowerRate / 100.0f)));
                        moneyText.requestFocus();
                    }
                } else {
                    System.out.println("需要充值" + validTextText + "天");
                    if (remainDay <= 0) {
                        System.out.println("卡已到期，remainDay可能为负数");
                        //启用时间置为今天
                        Calendar now = Calendar.getInstance();
                        String yearStr = String.valueOf(now.get(Calendar.YEAR));
                        int year = Integer.parseInt(yearStr.substring(2));
                        int month = now.get(Calendar.MONTH) + 1;
                        int day = now.get(Calendar.DAY_OF_MONTH);
                        int newStartTime = (year << 9) | (month << 5) | day;
                        String moneyInput = moneyText.getText();
                        System.out.println("moneyInput : " + moneyInput);
                        if (moneyInput.contains("-")) {
                            moneyInput = moneyInput.replace("-", "");
                            is_negative = 1;
                        }
                        System.out.println("rechargeText.getText : " + rechargeText.getText());
                        System.out.println("payRateText.getText : " + payRateText.getText());
                        System.out.println("powerRateText.getText : " + powerRateText.getText());
                        String validDayInput = String.valueOf(validTextText);
                        String rechargeTimeInput = (rechargeText.getText() == null || "".equals(rechargeText.getText().trim()) || rechargeText.getText().contains("当前")) ? df.format(formerRechargeTime / 10.0f) : rechargeText.getText();
                        String payRateInput = (payRateText.getText() == null || "".equals(payRateText.getText().trim()) || payRateText.getText().contains("当前")) ? df.format(formerPayRate / 10.0f) : payRateText.getText();
                        String powerRateInput = (powerRateText.getText() == null || "".equals(powerRateText.getText().trim()) || powerRateText.getText().contains("当前")) ? df.format(formerPowerRate / 100.0f) : powerRateText.getText();
                        int money, validDay, rechargeTime, payRate, powerRate;
                        if (pattern.matcher(moneyInput).matches() && pattern.matcher(rechargeTimeInput).matches() && pattern.matcher(payRateInput).matches()
                                && pattern.matcher(validDayInput).matches() && pattern.matcher(powerRateInput).matches()) {
                            if (is_negative == 0) {
                                money = (int) (Double.parseDouble(moneyInput) * 10.0);
                            } else {
                                money = -(int) (Double.parseDouble(moneyInput) * 10.0);
                            }
                            if (money + formerBalance > 50000) {
                                JOptionPane.showMessageDialog(null, "账户余额不得超过5000元！");
                                moneyText.addFocusListener(new JTextFieldHintListener(moneyText, "卡内余额：" + df.format(formerBalance / 10.0f)));
                                validText.addFocusListener(new JTextFieldHintListener(validText, "剩余天数：" + remainDay));
                                rechargeText.addFocusListener(new JTextFieldHintListener(rechargeText, "当前：" + df.format(formerRechargeTime / 10.0f)));
                                payRateText.addFocusListener(new JTextFieldHintListener(payRateText, "当前：" + df.format(formerPayRate / 10.0f)));
                                powerRateText.addFocusListener(new JTextFieldHintListener(powerRateText, "当前：" + df.format(formerPowerRate / 100.0f)));
                                moneyText.requestFocus();
                            } else if (money + formerBalance < 0) {
                                JOptionPane.showMessageDialog(null, "账户余额不得少于0元！");
                                moneyText.addFocusListener(new JTextFieldHintListener(moneyText, "卡内余额：" + df.format(formerBalance / 10.0f)));
                                validText.addFocusListener(new JTextFieldHintListener(validText, "剩余天数：" + remainDay));
                                rechargeText.addFocusListener(new JTextFieldHintListener(rechargeText, "当前：" + df.format(formerRechargeTime / 10.0f)));
                                payRateText.addFocusListener(new JTextFieldHintListener(payRateText, "当前：" + df.format(formerPayRate / 10.0f)));
                                powerRateText.addFocusListener(new JTextFieldHintListener(powerRateText, "当前：" + df.format(formerPowerRate / 100.0f)));
                                moneyText.requestFocus();
                            } else {
                                validDay = (int) Double.parseDouble(validDayInput);
                                System.out.println("valid day : " + validDay);
                                rechargeTime = (int) (Double.parseDouble(rechargeTimeInput) * 10.0);
                                payRate = (int) (Double.parseDouble(payRateInput) * 10.0);
                                powerRate = (int) (Double.parseDouble(powerRateInput) * 100.0);
                                if (validDay > 200 || rechargeTime > 255 || payRate > 255 || powerRate > 200) {
                                    JOptionPane.showMessageDialog(null, "请按照提示输入正确格式，且最多一位小数！");
                                    moneyText.addFocusListener(new JTextFieldHintListener(moneyText, "卡内余额：" + df.format(formerBalance / 10.0f)));
                                    validText.addFocusListener(new JTextFieldHintListener(validText, "剩余天数：" + remainDay));
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
                                            SerialPortUtils.sendToPort(serialPort, CommandUtils.rechargeCommand(deviceType, money, lastTime, newStartTime, validDay, rechargeTime, payRate, powerRate, isReturn, systemPassword));
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
                            validText.addFocusListener(new JTextFieldHintListener(validText, "剩余天数：" + remainDay));
                            rechargeText.addFocusListener(new JTextFieldHintListener(rechargeText, "当前：" + df.format(formerRechargeTime / 10.0f)));
                            payRateText.addFocusListener(new JTextFieldHintListener(payRateText, "当前：" + df.format(formerPayRate / 10.0f)));
                            powerRateText.addFocusListener(new JTextFieldHintListener(powerRateText, "当前：" + df.format(formerPowerRate / 100.0f)));
                            moneyText.requestFocus();
                        }
                    } else {
                        System.out.println("未过期，加天数");
                        //启用时间置为今天
                        Calendar now = Calendar.getInstance();
                        String yearStr = String.valueOf(now.get(Calendar.YEAR));
                        int year = Integer.parseInt(yearStr.substring(2));
                        int month = now.get(Calendar.MONTH) + 1;
                        int day = now.get(Calendar.DAY_OF_MONTH);
                        int newStartTime = (year << 9) | (month << 5) | day;
                        String moneyInput = moneyText.getText();
                        System.out.println("moneyInput : " + moneyInput);
                        if (moneyInput.contains("-")) {
                            moneyInput = moneyInput.replace("-", "");
                            is_negative = 1;
                        }
                        System.out.println("rechargeText.getText : " + rechargeText.getText());
                        System.out.println("payRateText.getText : " + payRateText.getText());
                        System.out.println("powerRateText.getText : " + powerRateText.getText());

                        System.out.println("remainDay : " + remainDay);
                        System.out.println("validTextText : " + Integer.parseInt(validTextText));
                        int sum = remainDay + Integer.parseInt(validTextText);
                        System.out.println("sum : " + sum);
                        String validDayInput = String.valueOf(sum);
                        System.out.println("最后的validDayInput ： " + validDayInput);
                        String rechargeTimeInput = (rechargeText.getText() == null || "".equals(rechargeText.getText().trim()) || rechargeText.getText().contains("当前")) ? df.format(formerRechargeTime / 10.0f) : rechargeText.getText();
                        String payRateInput = (payRateText.getText() == null || "".equals(payRateText.getText().trim()) || payRateText.getText().contains("当前")) ? df.format(formerPayRate / 10.0f) : payRateText.getText();
                        String powerRateInput = (powerRateText.getText() == null || "".equals(powerRateText.getText().trim()) || powerRateText.getText().contains("当前")) ? df.format(formerPowerRate / 100.0f) : powerRateText.getText();
                        int money, validDay, rechargeTime, payRate, powerRate;
                        if (pattern.matcher(moneyInput).matches() && pattern.matcher(rechargeTimeInput).matches() && pattern.matcher(payRateInput).matches()
                                && pattern.matcher(validDayInput).matches() && pattern.matcher(powerRateInput).matches()) {
                            if (is_negative == 0) {
                                money = (int) (Double.parseDouble(moneyInput) * 10.0);
                            } else {
                                money = -(int) (Double.parseDouble(moneyInput) * 10.0);
                            }
                            if (money + formerBalance > 50000) {
                                JOptionPane.showMessageDialog(null, "账户余额不得超过5000元！");
                                moneyText.addFocusListener(new JTextFieldHintListener(moneyText, "卡内余额：" + df.format(formerBalance / 10.0f)));
                                validText.addFocusListener(new JTextFieldHintListener(validText, "剩余天数：" + remainDayText));
                                rechargeText.addFocusListener(new JTextFieldHintListener(rechargeText, "当前：" + df.format(formerRechargeTime / 10.0f)));
                                payRateText.addFocusListener(new JTextFieldHintListener(payRateText, "当前：" + df.format(formerPayRate / 10.0f)));
                                powerRateText.addFocusListener(new JTextFieldHintListener(powerRateText, "当前：" + df.format(formerPowerRate / 100.0f)));
                                moneyText.requestFocus();
                            } else if (money + formerBalance < 0) {
                                JOptionPane.showMessageDialog(null, "账户余额不得少于0元！");
                                moneyText.addFocusListener(new JTextFieldHintListener(moneyText, "卡内余额：" + df.format(formerBalance / 10.0f)));
                                validText.addFocusListener(new JTextFieldHintListener(validText, "剩余天数：" + remainDayText));
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
                                    validText.addFocusListener(new JTextFieldHintListener(validText, "剩余天数：" + remainDayText));
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
                                            SerialPortUtils.sendToPort(serialPort, CommandUtils.rechargeCommand(deviceType, money, lastTime, newStartTime, validDay, rechargeTime, payRate, powerRate, isReturn, systemPassword));
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
                            validText.addFocusListener(new JTextFieldHintListener(validText, "剩余天数：" + remainDayText));
                            rechargeText.addFocusListener(new JTextFieldHintListener(rechargeText, "当前：" + df.format(formerRechargeTime / 10.0f)));
                            payRateText.addFocusListener(new JTextFieldHintListener(payRateText, "当前：" + df.format(formerPayRate / 10.0f)));
                            powerRateText.addFocusListener(new JTextFieldHintListener(powerRateText, "当前：" + df.format(formerPowerRate / 100.0f)));
                            moneyText.requestFocus();
                        }
                    }
                }
            }
        } else {
            String moneyInput = moneyText.getText();
            if (moneyInput.contains("-")) {
                moneyInput = moneyInput.replace("-", "");
                is_negative = 1;
            }
            String rechargeTimeInput = (rechargeText.getText() == null || "".equals(rechargeText.getText().trim()) || rechargeText.getText().contains("当前")) ? df.format(formerRechargeTime / 10.0f) : rechargeText.getText();
            String payRateInput = (payRateText.getText() == null || "".equals(payRateText.getText().trim()) || payRateText.getText().contains("当前")) ? df.format(formerPayRate / 10.0f) : payRateText.getText();
            String powerRateInput = (powerRateText.getText() == null || "".equals(powerRateText.getText().trim()) || powerRateText.getText().contains("当前")) ? df.format(formerPowerRate / 100.0f) : powerRateText.getText();
            if (pattern.matcher(moneyInput).matches() && pattern.matcher(rechargeTimeInput).matches() && pattern.matcher(payRateInput).matches() && pattern.matcher(powerRateInput).matches()) {
                int money;
                if (is_negative == 0) {
                    money = (int) (Double.parseDouble(moneyInput) * 10.0);
                } else {
                    money = -(int) (Double.parseDouble(moneyInput) * 10.0);
                }
                if (money + formerBalance > 50000) {
                    JOptionPane.showMessageDialog(null, "账户余额不得超过5000元！");
                    moneyText.addFocusListener(new JTextFieldHintListener(moneyText, "卡内余额：" + df.format(formerBalance / 10.0f)));
                    rechargeText.addFocusListener(new JTextFieldHintListener(rechargeText, "当前：" + df.format(formerRechargeTime / 10.0f)));
                    payRateText.addFocusListener(new JTextFieldHintListener(payRateText, "当前：" + df.format(formerPayRate / 10.0f)));
                    powerRateText.addFocusListener(new JTextFieldHintListener(powerRateText, "当前：" + df.format(formerPowerRate / 100.0f)));
                    moneyText.requestFocus();
                } else if (money + formerBalance < 0) {
                    JOptionPane.showMessageDialog(null, "账户余额不得少于0元！");
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
        balanceText.setText(df.format(formerBalance / 10.0f));
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
        validLabel.setText("充值天数");
        validLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
        contentPane.add(validLabel);
        validLabel.setBounds(new Rectangle(new Point(55, 170), validLabel.getPreferredSize()));

        validText = new JTextField();
        contentPane.add(validText);
        validText.setBounds(130, 165, 120, 30);
        if (cardType == 0 || cardType == 1 || cardType == 2) {
            validText.setEditable(false);
        } else {

            if (remainDay < 0) {
                remainDayText = "已到期";
            } else {
                remainDayText = String.valueOf(remainDay);
            }
            validText.addFocusListener(new JTextFieldHintListener(validText, "剩余天数：" + remainDayText));
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
