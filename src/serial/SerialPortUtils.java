package serial;


import bean.Card;
import bean.manager.CardManager;
import bean.manager.FrameManager;
import bean.manager.PortManager;
import bean.manager.UserManager;
import exception.*;
import gnu.io.*;
import service.impl.ServiceImpl;
import swing.LoginFrame;
import swing.QueryToRechargeFrame;
import swing.RechargeFrame;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.TooManyListenersException;

/**
 * @author kingfans
 */
public class SerialPortUtils {
    private static SerialPort serialPort;
    private SimpleDateFormat sdf;

    public SerialPortUtils() {
        this.sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

    public static ArrayList<String> findPort() {
        Enumeration<CommPortIdentifier> portList = (Enumeration<CommPortIdentifier>) CommPortIdentifier.getPortIdentifiers();
        ArrayList<String> portNameList = new ArrayList<String>();
        while (portList.hasMoreElements()) {
            CommPortIdentifier commPortIdentifier = portList.nextElement();
            String portName = commPortIdentifier.getName();
            System.out.println(commPortIdentifier.getPortType());
            portNameList.add(portName);
        }
        return portNameList;
    }

    public static void closePort(SerialPort serialPort) {
        if (serialPort != null) {
            serialPort.close();
            serialPort = null;
        }
    }

    public static SerialPort openPort(String portName, int portRate, RechargeFrame frame) throws NotASerialPort, NoSuchPort,
            PortInUse, UnsupportedCommOperationException {
        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            CommPort commPort = portIdentifier.open(portName, 2000);
            if (commPort instanceof SerialPort) {
                SerialPortUtils.serialPort = (SerialPort) commPort;
                System.out.println(SerialPortUtils.serialPort.getName() + " connected !");
                PortManager.addPort(SerialPortUtils.serialPort);
                SerialPortUtils.serialPort.setSerialPortParams(portRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);
                try {
                    addListener(SerialPortUtils.serialPort, new SerialListener(SerialPortUtils.serialPort, frame));
                } catch (TooManyListeners tooManyListeners) {
                    tooManyListeners.printStackTrace();
                }
                return SerialPortUtils.serialPort;
            } else {
                // 不是串口
                throw new NotASerialPort();
            }
        } catch (NoSuchPortException e1) {
            throw new NoSuchPort();
        } catch (PortInUseException e2) {
            throw new PortInUse();
        }
    }

    public static void sendToPort(SerialPort serialPort, byte[] order) throws SendDataToSerialPortFailure, SerialPortOutputStreamCloseFailure {
        OutputStream out = null;
        try {
            out = serialPort.getOutputStream();
            out.write(order);
            out.flush();
        } catch (IOException e) {
            throw new SendDataToSerialPortFailure();
        } finally {
            try {
                if (out != null) {
                    out.close();
                    out = null;
                }
            } catch (IOException e2) {
                throw new SerialPortOutputStreamCloseFailure();
            }
        }
    }

    public static byte[] readFromPort(final SerialPort serialPort, final RechargeFrame frame) throws
            ReadDataFromSerialPortFailure, SerialPortInputStreamCloseFailure {
        InputStream in = null;
        byte[] bytes = null;
        try {
            in = serialPort.getInputStream();
            int bufflenth = in.available();
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            bytes = new byte[bufflenth];
            in.read(bytes);
        } catch (IOException e3) {
            JOptionPane.showMessageDialog(null, "串口异常，请重新登录！");
            frame.dispose();
            PortManager.removePort();
            UserManager.removeUser();
            try {
                FrameManager.removeFrame("recharge");
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            throw new ReadDataFromSerialPortFailure();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e4) {
                throw new SerialPortInputStreamCloseFailure();
            }
        }
        return bytes;
    }

    public static void addListener(SerialPort port, SerialPortEventListener listener) throws TooManyListeners {
        try {
            port.addEventListener(listener);
            port.notifyOnDataAvailable(true);
        } catch (TooManyListenersException e) {
            throw new TooManyListeners();
        }
    }

    private static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        for (int i = begin; i < begin + count; i++) {
            bs[i - begin] = src[i];
        }
        return bs;
    }

    public static class SerialListener implements SerialPortEventListener {
        SerialPort serialPort;
        RechargeFrame frame;
        private int rev_app;
        private byte[] RX_buf;

        public SerialListener(SerialPort serialPort, RechargeFrame frame) {
            this.rev_app = 0;
            this.RX_buf = new byte[20];
            this.serialPort = serialPort;
            this.frame = frame;
        }

        @Override
        public void serialEvent(final SerialPortEvent serialPortEvent) {
            switch (serialPortEvent.getEventType()) {
                case SerialPortEvent.DATA_AVAILABLE:
                    byte[] b = null;
                    try {
                        b = SerialPortUtils.readFromPort(serialPort, frame);
                    } catch (ReadDataFromSerialPortFailure | SerialPortInputStreamCloseFailure readDataFromSerialPortFailure) {
                        readDataFromSerialPortFailure.printStackTrace();
                        serialPort.close();
                        serialPort = null;
                    }
                    if (b != null) {
                        int length = b.length;
                        try {
                            System.arraycopy(b, 0, this.RX_buf, this.rev_app, length);
                            this.rev_app += length;
                            int rev_byte_p = 0;
                            int last_p = 0;
                            while (true) {
                                if (this.RX_buf[rev_byte_p] == (byte) 0xaa && this.RX_buf[rev_byte_p + 1] == (byte) 0xc3 && this.RX_buf[rev_byte_p + this.RX_buf[rev_byte_p + 2] + 3] == (byte) 0xbb) {
                                    final byte[] bytes = subBytes(this.RX_buf, rev_byte_p, this.RX_buf[rev_byte_p + 2] + 4);
                                    if ((bytes[4] & 0xFF) != 0x02) {
                                        System.out.println("收到完整数据 :");
                                        for (byte aByte : bytes) {
                                            System.out.print(Integer.toHexString(aByte & 0xFF) + " ");
                                        }
                                        System.out.println();
                                    }
                                    SwingUtilities.invokeLater(() -> {
                                        int instruction = (bytes[3] & 0xFF);
                                        switch (instruction) {
                                            case 0xb1:
                                                int crc = ((bytes[6] & 0xFF) << 8 | (bytes[5] & 0xFF));
                                                int crcCheck = Utils.crc16(bytes, 3, bytes.length - 6);
                                                if (crc == crcCheck) {
                                                    this.frame.deviceStatusText.setText("已连接");
                                                    this.frame.deviceStatusText.setForeground(Color.blue);
                                                    LoginFrame.STATUS_COUNT = 0;
                                                } else {
                                                    System.out.println("b1 crc校验错误！ ");
                                                    System.out.println("b1 crc = " + crc + " , b1 crccheck : " + crcCheck);
                                                }
                                                break;

                                            case 0xb2:
                                                int crc2 = ((bytes[28] & 0xFF) << 8 | (bytes[27] & 0xFF));
                                                int crcCheck2 = Utils.crc16(bytes, 3, bytes.length - 6);
                                                if (crc2 == crcCheck2) {
                                                    if (LoginFrame.IS_RECHARGE == 0) {
                                                        int cardStatus = (bytes[4] & 0xFF);
                                                        if (cardStatus == 1) {
                                                            int balance = ((bytes[8] & 0xFF) << 8 | (bytes[9] & 0xFF));
                                                            DecimalFormat df = new DecimalFormat("0.0");
                                                            String finalBalance = df.format(balance / 10.0f);
                                                            frame.balanceText.setText(finalBalance);
                                                            String cardNum = Integer.toHexString((bytes[23] & 0xFF) << 24 | (bytes[24] & 0xFF) << 16 | (bytes[25] & 0xFF) << 8 | (bytes[26] & 0xFF));
                                                            System.out.println("卡号==============" + cardNum);
                                                            frame.cardNumText.setText(cardNum);
                                                            int deviceType = (bytes[5] & 0xFF);
                                                            String cardType = "Q10/CDZ";
                                                            if (deviceType == 1) {
                                                                int isBack = (bytes[22] & 0xFF);
                                                                if (isBack == 0) {
                                                                    cardType = "Q20(电子钱包A)";
                                                                } else if (isBack == 1) {
                                                                    int startTime = ((bytes[14] & 0xFF) << 8 | (bytes[15] & 0xFF));
                                                                    if (startTime == 65531) {
                                                                        cardType = "Q20(电子钱包B)";
                                                                    } else {
                                                                        cardType = "Q20(包月卡)";
                                                                    }
                                                                }
                                                            }
                                                            frame.cardTypeText.setText(cardType);
                                                            int validDay;
                                                            if ("Q20(包月卡)".equals(cardType)) {
                                                                validDay = (bytes[17] & 0xFF);
                                                                frame.validTimeText.setText(String.valueOf(validDay));
                                                            } else {
                                                                validDay = 0;
                                                                frame.validTimeText.setText("永久有效");
                                                            }
                                                            String phone = "-";
                                                            if (ServiceImpl.getInstance().isCardExist(cardNum)) {
                                                                phone = ServiceImpl.getInstance().getCardPhoneByNum(cardNum);
                                                                if ("-1".equals(phone)) {
                                                                    phone = "-";
                                                                }
                                                            }
                                                            frame.phoneText.setText(phone);
                                                            DecimalFormat df2 = new DecimalFormat("00");
                                                            String lastTimeDB;
                                                            if (deviceType == 0) {
                                                                frame.lastTimeText.setText("-");
                                                                lastTimeDB = "-";
                                                            } else {
                                                                int lastTime = ((bytes[10] & 0xFF) << 8 | (bytes[11] & 0xFF));
                                                                if (lastTime == 65530) {
                                                                    this.frame.lastTimeText.setText("不限制");
                                                                    lastTimeDB = Integer.toHexString(lastTime);
                                                                } else if (lastTime == 65533) {
                                                                    this.frame.lastTimeText.setText("已完成");
                                                                    lastTimeDB = Integer.toHexString(lastTime);
                                                                } else {
                                                                    int year = (lastTime & 0x7E00) >> 9;
                                                                    int month = (lastTime & 0x1E0) >> 5;
                                                                    int day = (lastTime & 0x1F);
                                                                    int hour = (bytes[12] & 0xFF);
                                                                    int minute = (bytes[13] & 0xFF);
                                                                    String lastTimeFinal = "20" + df2.format(year) + "-" + df2.format(month) + "-" + df2.format(day) + " " + df2.format(hour) + ":" + df2.format(minute);
                                                                    frame.lastTimeText.setText(lastTimeFinal);
                                                                    lastTimeDB = lastTimeFinal;
                                                                }
                                                            }
                                                            String startTimeDB;
                                                            if (deviceType == 0) {
                                                                frame.startTimeText.setText("-");
                                                                startTimeDB = "-";
                                                            } else {
                                                                int startTime = ((bytes[14] & 0xFF) << 8 | (bytes[15] & 0xFF));
                                                                if (startTime == 65534) {
                                                                    frame.startTimeText.setText("未激活");
                                                                    startTimeDB = Integer.toHexString(startTime);
                                                                } else if (startTime == 65531) {
                                                                    frame.startTimeText.setText("永久有效");
                                                                    startTimeDB = Integer.toHexString(startTime);
                                                                } else {
                                                                    int year = (startTime & 0x7E00) >> 9;
                                                                    int month = (startTime & 0x1E0) >> 5;
                                                                    int day = (startTime & 0x1F);
                                                                    String startTimeFinal = "20" + df2.format(year) + "-" + df2.format(month) + "-" + df2.format(day);
                                                                    frame.startTimeText.setText(startTimeFinal);
                                                                    startTimeDB = startTimeFinal;
                                                                }
                                                            }
                                                            int chargeTime;
                                                            if (deviceType == 0) {
                                                                chargeTime = 0;
                                                                frame.chargeTimeText.setText("-");
                                                            } else {
                                                                chargeTime = (bytes[18] & 0xFF);
                                                                frame.chargeTimeText.setText(df.format(chargeTime / 10.0f) + " h");
                                                            }

                                                            int payRate;
                                                            if (deviceType == 0) {
                                                                payRate = 0;
                                                                frame.payRateText.setText("-");
                                                            } else {
                                                                payRate = (bytes[19] & 0xFF);
                                                                frame.payRateText.setText(df.format(payRate / 10.0f) + " 元");
                                                            }

                                                            int powerRate;
                                                            if (deviceType == 0) {
                                                                powerRate = 0;
                                                                frame.powerRateText.setText("-");
                                                            } else {
                                                                powerRate = (bytes[21] & 0xFF);
                                                                frame.powerRateText.setText(df.format(powerRate / 100.0f) + " KW");
                                                            }
                                                            frame.tipLabel.setText("查询完毕");
                                                            if (ServiceImpl.getInstance().isCardExist(cardNum)) {
                                                                if (deviceType == 0) {
                                                                    ServiceImpl.getInstance().updateCardInfoQ10(cardNum, balance);
                                                                } else {
                                                                    ServiceImpl.getInstance().updateCardInfo(cardNum, Utils.getIntCardType(cardType), balance, validDay, lastTimeDB, startTimeDB, chargeTime, payRate, powerRate);
                                                                }
                                                            } else {
                                                                ServiceImpl.getInstance().addCard(cardNum, Utils.getIntCardType(cardType), balance, validDay, lastTimeDB, startTimeDB, chargeTime, payRate, powerRate);
                                                            }
                                                            if (CardManager.getCardByNum(cardNum) != null) {
                                                                Card card = CardManager.getCardByNum(cardNum);
                                                                if (deviceType == 0) {
                                                                    card.setBalance(balance);
                                                                    card.setCardType(Utils.getIntCardType(cardType));
                                                                } else {
                                                                    card.updateCardInfo(Utils.getIntCardType(cardType), balance, validDay, lastTimeDB, startTimeDB, chargeTime, payRate, powerRate);
                                                                }
                                                            } else {
                                                                Card card2 = new Card(cardNum);
                                                                if (deviceType == 0) {
                                                                    card2.setBalance(balance);
                                                                    card2.setCardType(Utils.getIntCardType(cardType));
                                                                } else {
                                                                    card2.updateCardInfo(Utils.getIntCardType(cardType), balance, validDay, lastTimeDB, startTimeDB, chargeTime, payRate, powerRate);
                                                                }
                                                                CardManager.addCard(cardNum, card2);
                                                            }
                                                        } else if (cardStatus == 4) {
                                                            frame.tipLabel.setText("查询完毕，此卡为新卡");
                                                            JOptionPane.showMessageDialog(null, "此卡为新卡！");
                                                        }
                                                    } else if (LoginFrame.IS_RECHARGE == 1) {
                                                        int cardStatus = (bytes[4] & 0xFF);
                                                        if (cardStatus == 1) {
                                                            String cardNum = Integer.toHexString((bytes[23] & 0xFF) << 24 | (bytes[24] & 0xFF) << 16 | (bytes[25] & 0xFF) << 8 | (bytes[26] & 0xFF));
                                                            int formerBalance = ((bytes[8] & 0xFF) << 8 | (bytes[9] & 0xFF));
                                                            int formerValidDay = (bytes[17] & 0xFF);
                                                            int formerRechargeTime = (bytes[18] & 0xFF);
                                                            int formerPayRate = (bytes[19] & 0xFF);
                                                            int formerPowerRate = (bytes[21] & 0xFF);
                                                            if (ServiceImpl.getInstance().isCardExist(cardNum)) {
                                                                int cardType = ServiceImpl.getInstance().getCardTypeByNum(cardNum);
                                                                int deviceType = 0, lastTime = 0, startTime = 0, isReturn = 0;
                                                                if (cardType == 0) {
                                                                    lastTime = ((bytes[10] & 0xFF) << 24 | (bytes[11] & 0xFF) << 16 | (bytes[12] & 0xFF) << 8 | (bytes[13] & 0xFF));
                                                                    startTime = ((bytes[14] & 0xFF) << 8 | (bytes[15] & 0xFF));
                                                                } else if (cardType == 1) {
                                                                    deviceType = 1;
                                                                    lastTime = 65530;
                                                                    startTime = 65531;
                                                                } else if (cardType == 2) {
                                                                    deviceType = 1;
                                                                    lastTime = 65530;
                                                                    startTime = 65531;
                                                                    isReturn = 1;
                                                                } else if (cardType == 3) {
                                                                    deviceType = 1;
                                                                    lastTime = 65533;
                                                                    startTime = 65534;
                                                                    isReturn = 1;
                                                                }
                                                                frame.tipLabel.setText("请输入充值相关参数");
                                                                new QueryToRechargeFrame(frame, cardNum, cardType, deviceType, lastTime, startTime, isReturn, formerValidDay, formerBalance, formerRechargeTime, formerPayRate, formerPowerRate);
                                                                frame.setEnabled(false);
                                                            }
                                                        } else if (cardStatus == 4) {
                                                            String cardNum = Integer.toHexString((bytes[23] & 0xFF) << 24 | (bytes[24] & 0xFF) << 16 | (bytes[25] & 0xFF) << 8 | (bytes[26] & 0xFF));
                                                            int deviceType = (bytes[5] & 0xFF);
                                                            if (deviceType == 0) {
                                                                int cardType = 0, lastTime = 0, startTime = 0, isReturn = 0, formerValidDay = 0, formerBalance = 0,
                                                                        formerRechargeTime = 0, formerPayRate = 0, formerPowerRate = 0;
                                                                new QueryToRechargeFrame(this.frame, cardNum, cardType, deviceType, lastTime, startTime, isReturn, formerValidDay, formerBalance
                                                                        , formerRechargeTime, formerPayRate, formerPowerRate);
                                                                frame.setEnabled(false);
                                                            } else {
                                                                Object[] obj = new Object[]{"Q20(电子钱包A)", "Q20(电子钱包B)", "Q20(包月卡)"};
                                                                String cardTypeSelected = (String) JOptionPane.showInputDialog(null, "检测到该卡为新卡，请设置卡的类型:\n", null, JOptionPane.PLAIN_MESSAGE, null, obj, "Q20(电子钱包A)");
                                                                if (cardTypeSelected != null) {
                                                                    System.out.println("caedTypeSelected : " + cardTypeSelected);
                                                                    int cardType = Utils.getIntCardType(cardTypeSelected), lastTime = 0, startTime = 0, isReturn = 0,
                                                                            formerValidDay = 0, formerBalance = 0, formerRechargeTime = 0, formerPayRate = 0, formerPowerRate = 0;
                                                                    if (cardType == 1) {
                                                                        lastTime = 65530;
                                                                        startTime = 65531;
                                                                    } else if (cardType == 2) {
                                                                        lastTime = 65530;
                                                                        startTime = 65531;
                                                                        isReturn = 1;
                                                                    } else if (cardType == 3) {
                                                                        lastTime = 65533;
                                                                        startTime = 65534;
                                                                        isReturn = 1;
                                                                    }
                                                                    frame.tipLabel.setText("请输入充值相关参数");
                                                                    new QueryToRechargeFrame(frame, cardNum, cardType, deviceType, lastTime, startTime, isReturn, formerValidDay, formerBalance, formerRechargeTime, formerPayRate, formerPowerRate);
                                                                    frame.setEnabled(false);
                                                                } else {
                                                                    LoginFrame.IS_RECHARGE = 0;
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    System.out.println("b2 crc校验错误 ");
                                                    System.out.println("b2 crc = " + crc2 + " ,b2 crccheck : " + crcCheck2);
                                                }
                                                break;

                                            case 0xb3:
                                                int crc3 = ((bytes[10] & 0xFF) << 8 | (bytes[9] & 0xFF));
                                                int crcCheck3 = Utils.crc16(bytes, 3, bytes.length - 6);
                                                if (crc3 == crcCheck3) {
                                                    int registerResult = (bytes[4] & 0xFF);
                                                    if (registerResult == 1) {
                                                        String cardNum = Integer.toHexString((bytes[5] & 0xFF) << 24 | (bytes[6] & 0xFF) << 16 | (bytes[7] & 0xFF) << 8 | (bytes[8] & 0xFF));
                                                        System.out.println("注册成功！卡号为 ：" + cardNum);
                                                        frame.tipLabel.setText("注册成功");
                                                        JOptionPane.showMessageDialog(null, "注册成功！卡号为 : " + cardNum, "注册提示", JOptionPane.PLAIN_MESSAGE);
                                                        if (ServiceImpl.getInstance().isCardExist(cardNum)) {
                                                            ServiceImpl.getInstance().deleteCard(cardNum);
                                                            ServiceImpl.getInstance().addNewCard(cardNum, LoginFrame.REG_NAME, LoginFrame.REG_PHONE);
                                                        } else {
                                                            ServiceImpl.getInstance().addNewCard(cardNum, LoginFrame.REG_NAME, LoginFrame.REG_PHONE);
                                                        }
                                                        Card card = new Card(cardNum);
                                                        card.setPhone(LoginFrame.REG_PHONE);
                                                        card.setUsername(LoginFrame.REG_NAME);
                                                        CardManager.addCard(cardNum, card);
                                                        LoginFrame.REG_PHONE = "-";
                                                        LoginFrame.REG_NAME = "-";
                                                    } else if (registerResult == 3) {
                                                        frame.tipLabel.setText("注册失败");
                                                        JOptionPane.showMessageDialog(null, "注册失败！", "注册提示", 2);
                                                        LoginFrame.REG_PHONE = "-";
                                                        LoginFrame.REG_NAME = "-";
                                                    }

                                                } else {
                                                    System.out.println("b3 crc校验错误 ");
                                                    System.out.println("b3 crc = " + crc3 + " ,b3 crccheck : " + crcCheck3);
                                                }
                                                break;

                                            case 0xb4:
                                                int crc4 = ((bytes[10] & 0xFF) << 8 | (bytes[9] & 0xFF));
                                                int crcCheck4 = Utils.crc16(bytes, 3, bytes.length - 6);
                                                if (crc4 == crcCheck4) {
                                                    int logoutResult = (bytes[4] & 0xFF);
                                                    if (logoutResult == 1) {
                                                        String cardNum = Integer.toHexString((bytes[5] & 0xFF) << 24 | (bytes[6] & 0xFF) << 16 | (bytes[7] & 0xFF) << 8 | (bytes[8] & 0xFF));
                                                        if (ServiceImpl.getInstance().isCardExist(cardNum)) {
                                                            ServiceImpl.getInstance().deleteCard(cardNum);
                                                        }
                                                        CardManager.deleteCard(cardNum);
                                                        frame.tipLabel.setText("注销成功");
                                                        JOptionPane.showMessageDialog(null, "注销成功！", "注销提示", JOptionPane.INFORMATION_MESSAGE);
                                                    } else if (logoutResult == 3) {
                                                        this.frame.tipLabel.setText("注销失败");
                                                        JOptionPane.showMessageDialog(null, "注销失败！", "注销提示", JOptionPane.WARNING_MESSAGE);
                                                    }

                                                } else {
                                                    System.out.println("b4 crc校验错误 ");
                                                    System.out.println("b4 crc = " + crc4 + " ,b4 crccheck : " + crcCheck4);
                                                }
                                                break;

                                            case 0xb5:
                                                DecimalFormat df = new DecimalFormat("0.0");
                                                int crc5 = ((bytes[14] & 0xFF) << 8 | (bytes[13] & 0xFF));
                                                int crcCheck5 = Utils.crc16(bytes, 3, bytes.length - 6);
                                                if (crc5 == crcCheck5) {
                                                    int rechargeResult = (bytes[4] & 0xFF);
                                                    if (rechargeResult == 1) {
                                                        int balance = ((bytes[5] & 0xFF) << 24 | (bytes[6] & 0xFF) << 16 | (bytes[7] & 0xFF) << 8 | (bytes[8] & 0xFF));
                                                        String cardNum = Integer.toHexString((bytes[9] & 0xFF) << 24 | (bytes[10] & 0xFF) << 16 | (bytes[11] & 0xFF) << 8 | (bytes[12] & 0xFF));
                                                        if (CardManager.getCardByNum(cardNum) != null) {
                                                            Card card = CardManager.getCardByNum(cardNum);
                                                            card.setBalance(balance);
                                                            ServiceImpl.getInstance().updateCardInfoExTime(cardNum, card.getCardType(), balance, card.getValidDay(), card.getRechargeTime(), card.getPayRate(), card.getPowerRate());
                                                            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
                                                            String nowTime = sdf.format(new Date());
                                                            String username = ServiceImpl.getInstance().getUsernameByCardNum(cardNum);
                                                            if ("-1".equals(username)) {
                                                                username = "-";
                                                            }
                                                            String phone = ServiceImpl.getInstance().getCardPhoneByNum(cardNum);
                                                            if ("-1".equals(phone)) {
                                                                phone = "-";
                                                            }
                                                            ServiceImpl.getInstance().addRechargeHis(cardNum, username, phone, card.getCardType(), balance, card.getTopUp(), card.getValidDay(), card.getRechargeTime(), card.getPayRate(), card.getPowerRate(), nowTime);
                                                            frame.balanceText.setText(df.format(balance / 10.0f));
                                                            frame.cardNumText.setText(cardNum);
                                                            frame.cardTypeText.setText(Utils.getStringCardType(card.getCardType()));
                                                            frame.phoneText.setText(phone);
                                                            if (card.getCardType() == 0) {
                                                                frame.validTimeText.setText("永久有效");
                                                                frame.lastTimeText.setText("-");
                                                                frame.startTimeText.setText("-");
                                                                frame.chargeTimeText.setText("-");
                                                                frame.payRateText.setText("-");
                                                                frame.powerRateText.setText("-");
                                                            } else if (card.getCardType() == 1) {
                                                                frame.validTimeText.setText("永久有效");
                                                                frame.lastTimeText.setText("不限制");
                                                                frame.startTimeText.setText("永久有效");
                                                                frame.chargeTimeText.setText(df.format(card.getRechargeTime() / 10.0f) + " h");
                                                                frame.payRateText.setText(df.format(card.getPayRate() / 10.0f) + " 元");
                                                                frame.powerRateText.setText(df.format(card.getPowerRate() / 100.0f) + " KW");
                                                            } else if (card.getCardType() == 2) {
                                                                this.frame.validTimeText.setText("永久有效");
                                                                this.frame.lastTimeText.setText("不限制");
                                                                this.frame.startTimeText.setText("永久有效");
                                                                this.frame.chargeTimeText.setText(df.format(card.getRechargeTime() / 10.0f) + " h");
                                                                this.frame.payRateText.setText(df.format(card.getPayRate() / 10.0f) + " \u5143");
                                                                this.frame.powerRateText.setText(df.format(card.getPowerRate() / 100.0f) + " KW");
                                                            } else if (card.getCardType() == 3) {
                                                                frame.validTimeText.setText(String.valueOf(card.getValidDay()));
                                                                String lastTime = ServiceImpl.getInstance().getLastTimeByNum(cardNum);
                                                                if ("fffd".equals(lastTime)) {
                                                                    lastTime = "已完成";
                                                                }
                                                                String startTime = ServiceImpl.getInstance().getStartTimeByNum(cardNum);
                                                                if ("fffe".equals(startTime)) {
                                                                    startTime = "未激活";
                                                                }
                                                                frame.lastTimeText.setText(lastTime);
                                                                frame.startTimeText.setText(startTime);
                                                                frame.chargeTimeText.setText(df.format(card.getRechargeTime() / 10.0f) + " h");
                                                                frame.payRateText.setText(df.format(card.getPayRate() / 10.0f) + " 元");
                                                                frame.powerRateText.setText(df.format(card.getPowerRate() / 100.0f) + " KW");
                                                            }
                                                        }
                                                        frame.tipLabel.setText("充值成功");
                                                        JOptionPane.showMessageDialog(null, "充值成功！卡内余额：" + df.format(balance / 10.0f), "充值提示", JOptionPane.INFORMATION_MESSAGE);
                                                    } else if (rechargeResult == 3) {
                                                        frame.tipLabel.setText("充值失败");
                                                        JOptionPane.showMessageDialog(null, "充值失败！", "充值提示", JOptionPane.WARNING_MESSAGE);
                                                    }

                                                } else {
                                                    System.out.println("b5 crc校验错误 ");
                                                    System.out.println("b5 crc = " + crc5 + " ,b5 crccheck : " + crcCheck5);
                                                }
                                                break;
                                            default:
                                                break;
                                        }

                                    });
                                    RX_buf[rev_byte_p] = 0x00;
                                    RX_buf[rev_byte_p + 1] = 0x00;
                                    RX_buf[rev_byte_p + RX_buf[rev_byte_p + 2] + 3] = 0x00;
                                    rev_byte_p = rev_byte_p + RX_buf[rev_byte_p + 2] + 4;
                                    last_p = rev_byte_p;

                                } else {
                                    rev_byte_p++;
                                }
                                if (rev_byte_p >= rev_app) {
                                    int last_number = rev_app - last_p;
                                    if (last_number != 0) {
                                        for (int i = 0; i < last_number; i++) {
                                            RX_buf[i] = RX_buf[last_p + i];
                                        }
                                    }
                                    for (int j = last_number; j < RX_buf.length; j++) {
                                        RX_buf[j] = 0x00;
                                    }
                                    rev_app = last_number;
                                    break;
                                }

                            }

                        } catch (Exception e) {
                            RX_buf = new byte[512];
                            rev_app = 0;
                        }

                    }

                    break;
                default:
                    break;
            }

        }
    }
}




















