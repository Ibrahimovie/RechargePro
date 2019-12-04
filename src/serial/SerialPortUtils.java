package serial;


import bean.Card;
import bean.LogoutCard;
import bean.manager.*;
import com.alibaba.fastjson.JSONObject;
import commands.CommandUtils;
import exception.*;
import gnu.io.*;
import service.impl.ServiceImpl;
import swing.*;
import utils.FTPUtils;
import utils.HttpUtils;
import utils.Utils;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.*;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static swing.LoginFrame.*;

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

    public static byte[] readFromPort(SerialPort serialPort, RechargeFrame frame) throws ReadDataFromSerialPortFailure, SerialPortInputStreamCloseFailure {
        InputStream in = null;
        byte[] bytes = null;
        try {
            in = serialPort.getInputStream();
            int bufflenth = in.available();
            bytes = new byte[bufflenth];
            in.read(bytes);
        } catch (IOException e3) {
//            JOptionPane.showMessageDialog(null, "串口异常！");
            frame.deviceStatusText.setText("连接异常");
            frame.deviceStatusText.setForeground(Color.red);
            frame.hardwareVersionText.setText("-");
            frame.softwareVersionText.setText("-");
            PortManager.removePort();
            SerialPort port = PortManager.getSerialPort();
            if (port == null) {
                System.out.println("串口已移除！");
            } else {
                System.out.println("串口未移除 ！ port ： " + port.getName());
            }

//            frame.dispose();
//            UserManager.removeUser();
//            try {
//                FrameManager.removeFrame("recharge");
//            } catch (Exception e2) {
//                e2.printStackTrace();
//            }
//            throw new ReadDataFromSerialPortFailure();
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
            this.RX_buf = new byte[1024];
            this.serialPort = serialPort;
            this.frame = frame;
        }

        @Override
        public void serialEvent(SerialPortEvent serialPortEvent) {
            switch (serialPortEvent.getEventType()) {
                case SerialPortEvent.DATA_AVAILABLE:
                    synchronized (SerialListener.class) {
                        byte[] b = null;
                        try {
                            b = SerialPortUtils.readFromPort(serialPort, frame);
//                            System.out.println("收到原始数据 : ");
//                            for (byte aByte : b) {
//                                System.out.print(Integer.toHexString(aByte & 0xFF) + " ");
//                            }
//                            System.out.println();
                        } catch (ReadDataFromSerialPortFailure | SerialPortInputStreamCloseFailure readDataFromSerialPortFailure) {
                            readDataFromSerialPortFailure.printStackTrace();
                            serialPort.close();
                            serialPort = null;
                        }
                        if (b != null) {
                            int length = b.length;
                            try {
                                try {
                                    System.arraycopy(b, 0, RX_buf, rev_app, length);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                this.rev_app += length;
                                int rev_byte_p = 0;
                                int last_p = 0;
                                while (true) {
                                    if ((this.RX_buf[rev_byte_p] == (byte) 0xaa && this.RX_buf[rev_byte_p + 1] == (byte) 0xc3
                                            && this.RX_buf[rev_byte_p + this.RX_buf[rev_byte_p + 2] + 3] == (byte) 0xbb)
                                            || (this.RX_buf[rev_byte_p] == (byte) 0xff && this.RX_buf[rev_byte_p + 1] == (byte) 0x55
                                            && this.RX_buf[rev_byte_p + this.RX_buf[rev_byte_p + 2] + 3] == (byte) 0xbb)) {
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
                                                    int crc = ((bytes[8] & 0xFF) << 8 | (bytes[7] & 0xFF));
                                                    int crcCheck = Utils.crc16(bytes, 3, bytes.length - 6);
                                                    if (crc == crcCheck) {
                                                        int software = (bytes[5] & 0xFF);
                                                        int hardware = (bytes[6] & 0xFF);
                                                        DecimalFormat df = new DecimalFormat("0.0");
                                                        String softVersion = df.format(software / 10.0f);
                                                        String hardVersion = df.format(hardware / 10.0f);
                                                        LOCAL_HARD_VERSION = softVersion;//记录设备版本
                                                        this.frame.deviceStatusText.setText("已连接");
                                                        this.frame.softwareVersionText.setText(softVersion);
                                                        this.frame.hardwareVersionText.setText(hardVersion);
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
                                                            /**
                                                             * 查询操作
                                                             */
                                                            int cardStatus = (bytes[4] & 0xFF);
                                                            if (cardStatus == 1) {
                                                                int balance = ((bytes[8] & 0xFF) << 8 | (bytes[9] & 0xFF));
                                                                DecimalFormat df = new DecimalFormat("0.0");
                                                                String finalBalance = df.format(balance / 10.0f);
                                                                frame.balanceText.setText(finalBalance);
                                                                String cardNum = Integer.toHexString((bytes[23] & 0xFF) << 24 | (bytes[24] & 0xFF) << 16 | (bytes[25] & 0xFF) << 8 | (bytes[26] & 0xFF));
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
                                                                        frame.remainTimeText.setText("     -");
                                                                    } else if (startTime == 65531) {
                                                                        frame.startTimeText.setText("永久有效");
                                                                        startTimeDB = Integer.toHexString(startTime);
                                                                        frame.remainTimeText.setText("     -");
                                                                    } else {
                                                                        int year = (startTime & 0x7E00) >> 9;
                                                                        int month = (startTime & 0x1E0) >> 5;
                                                                        int day = (startTime & 0x1F);
                                                                        String startTimeFinal = "20" + df2.format(year) + "-" + df2.format(month) + "-" + df2.format(day);
                                                                        Calendar calendar = Calendar.getInstance();
                                                                        int nowYear = (calendar.get(Calendar.YEAR)) % 100;
                                                                        if ((year < 5 || year > nowYear) || (month < 1 || month > 12) || (day < 1 || day > 31)) {
                                                                            frame.startTimeText.setText("日期有误");
                                                                            frame.remainTimeText.setText("     -");
                                                                            frame.validTimeText.setText("日期有误");
                                                                        } else {
                                                                            frame.startTimeText.setText(startTimeFinal);
                                                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                                                            Date ago = new Date();
                                                                            try {
                                                                                ago = sdf.parse(startTimeFinal);
                                                                            } catch (ParseException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                            Date now = new Date();
                                                                            int days = Utils.differentDaysByMillisecond(ago, now);
                                                                            System.out.println("距离启用时间已有 " + days + " 天");
                                                                            String endDay = null;
                                                                            try {
                                                                                Date startDate = sdf.parse(startTimeFinal);
                                                                                Date end = Utils.getDateAfter(startDate, validDay);
                                                                                endDay = sdf.format(end);
                                                                            } catch (ParseException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                            if (days >= validDay) {
                                                                                System.out.println("超过有效天数！");
                                                                                frame.remainTimeText.setText(endDay);
                                                                                frame.validTimeText.setText("已过期");
                                                                            } else {
                                                                                int remain = validDay - days;
                                                                                System.out.println("剩余天数 ： " + remain);
                                                                                frame.remainTimeText.setText(endDay);
                                                                                frame.validTimeText.setText(String.valueOf(remain));
                                                                            }

                                                                        }
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

//                                                                try {
//                                                                    Map<String, String> map = new HashMap<>(6);
//                                                                    map.put("card_number", cardNum);
//                                                                    map.put("card_type", URLEncoder.encode(cardType, "utf-8"));
//                                                                    map.put("valid_day", String.valueOf(validDay));
//                                                                    map.put("recharge_time", String.valueOf(chargeTime));
//                                                                    map.put("pay_rate", String.valueOf(payRate));
//                                                                    map.put("power_rate", String.valueOf(powerRate));
//                                                                    String resp = HttpUtils.toServlet(map, "card", "updateCardInfo");
//                                                                } catch (UnsupportedEncodingException e) {
//                                                                    e.printStackTrace();
//                                                                }
                                                            } else if (cardStatus == 2) {
                                                                frame.tipLabel.setText("未检测到IC卡");
                                                            } else if (cardStatus == 3) {
                                                                frame.tipLabel.setText("查询失败！数据错误");
                                                                JOptionPane.showMessageDialog(null, "数据错误！");
                                                            } else if (cardStatus == 4) {
                                                                frame.tipLabel.setText("此卡为新卡");
                                                                JOptionPane.showMessageDialog(null, "此卡为新卡！");
                                                            } else if (cardStatus == 5) {
                                                                frame.tipLabel.setText("查询失败！密码错误");
                                                                JOptionPane.showMessageDialog(null, "密码错误！");
                                                            } else if (cardStatus == 6) {
                                                                frame.tipLabel.setText("非空白卡");
                                                                JOptionPane.showMessageDialog(null, "非空白卡！");
                                                            } else if (cardStatus == 7) {
                                                                frame.tipLabel.setText("空白卡");
                                                                JOptionPane.showMessageDialog(null, "空白卡！");
                                                            }
                                                        } else if (LoginFrame.IS_RECHARGE == 1) {
                                                            /**
                                                             * 充值操作
                                                             */
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
                                                                    int deviceType = 0, lastTime = 0, startTime = 0, isReturn = 0, isMonthUnUse = 0, remainDay = 0;
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
                                                                        lastTime = ((bytes[10] & 0xFF) << 24 | (bytes[11] & 0xFF) << 16 | (bytes[12] & 0xFF) << 8 | (bytes[13] & 0xFF));
                                                                        startTime = ((bytes[14] & 0xFF) << 8 | (bytes[15] & 0xFF));
                                                                        isReturn = 1;
                                                                        if (startTime == 65534) {
                                                                            //如果未激活，isMonthUnUse置1
                                                                            isMonthUnUse = 1;
                                                                            remainDay = formerValidDay;
                                                                        } else {
                                                                            //已激活
                                                                            DecimalFormat df = new DecimalFormat("00");
                                                                            int year = (startTime & 0x7E00) >> 9;
                                                                            int month = (startTime & 0x1E0) >> 5;
                                                                            int day = (startTime & 0x1F);
                                                                            String startTimeFinal = "20" + df.format(year) + "-" + df.format(month) + "-" + df.format(day);
                                                                            System.out.println("startTimeFinal : " + startTimeFinal);
                                                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                                                            Date ago = new Date();
                                                                            try {
                                                                                ago = sdf.parse(startTimeFinal);
                                                                            } catch (ParseException e1) {
                                                                                e1.printStackTrace();
                                                                            }
                                                                            Date now = new Date();
                                                                            int days = Utils.differentDaysByMillisecond(ago, now);
                                                                            System.out.println("当前距离启用日期已" + days + "天");
                                                                            remainDay = formerValidDay - days;
                                                                            System.out.println("remainDay : " + remainDay);
                                                                        }
                                                                    }
                                                                    frame.tipLabel.setText("请输入充值相关参数");
                                                                    new QueryToRechargeFrame(frame, cardNum, cardType, deviceType, lastTime, startTime, isReturn, formerValidDay, formerBalance, formerRechargeTime, formerPayRate, formerPowerRate, isMonthUnUse, remainDay);
                                                                    frame.setEnabled(false);
                                                                }
                                                            } else if (cardStatus == 2) {
                                                                frame.tipLabel.setText("未检测到IC卡");
                                                            } else if (cardStatus == 3) {
                                                                frame.tipLabel.setText("数据错误");
                                                                JOptionPane.showMessageDialog(null, "数据错误！");
                                                                LoginFrame.IS_RECHARGE = 0;
                                                            } else if (cardStatus == 4) {
                                                                String cardNum = Integer.toHexString((bytes[23] & 0xFF) << 24 | (bytes[24] & 0xFF) << 16 | (bytes[25] & 0xFF) << 8 | (bytes[26] & 0xFF));
                                                                int deviceType = (bytes[5] & 0xFF);
                                                                if (deviceType == 0) {
                                                                    int cardType = 0, lastTime = 0, startTime = 0, isReturn = 0, formerValidDay = 0, formerBalance = 0,
                                                                            formerRechargeTime = 0, formerPayRate = 0, formerPowerRate = 0;
                                                                    new QueryToRechargeFrame(this.frame, cardNum, cardType, deviceType, lastTime, startTime, isReturn, formerValidDay, formerBalance
                                                                            , formerRechargeTime, formerPayRate, formerPowerRate, 0, 0);
                                                                    frame.setEnabled(false);
                                                                } else {
                                                                    Object[] obj = new Object[]{"Q20(电子钱包A)", "Q20(电子钱包B)", "Q20(包月卡)"};
                                                                    String cardTypeSelected = (String) JOptionPane.showInputDialog(null, "检测到该卡为新卡，请设置卡的类型:\n", null, JOptionPane.PLAIN_MESSAGE, null, obj, "Q20(电子钱包A)");
                                                                    if (cardTypeSelected != null) {
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
                                                                        new QueryToRechargeFrame(frame, cardNum, cardType, deviceType, lastTime, startTime, isReturn, formerValidDay, formerBalance, formerRechargeTime, formerPayRate, formerPowerRate, 0, 0);
                                                                        frame.setEnabled(false);
                                                                    } else {
                                                                        LoginFrame.IS_RECHARGE = 0;
                                                                    }
                                                                }
                                                            } else if (cardStatus == 5) {
                                                                frame.tipLabel.setText("密码错误");
                                                                JOptionPane.showMessageDialog(null, "密码错误！");
                                                                LoginFrame.IS_RECHARGE = 0;
                                                            } else if (cardStatus == 6) {
                                                                frame.tipLabel.setText("非空白卡");
                                                                JOptionPane.showMessageDialog(null, "非空白卡！");
                                                                LoginFrame.IS_RECHARGE = 0;
                                                            } else if (cardStatus == 7) {
                                                                frame.tipLabel.setText("空白卡");
                                                                JOptionPane.showMessageDialog(null, "空白卡！");
                                                                LoginFrame.IS_RECHARGE = 0;
                                                            }
                                                        } else if (LoginFrame.IS_RECHARGE == 2) {
                                                            /**
                                                             * 圈存领取前查询
                                                             */
                                                            int cardStatus = (bytes[4] & 0xFF);
                                                            if (cardStatus == 1) {
                                                                String cardNum = Integer.toHexString((bytes[23] & 0xFF) << 24 | (bytes[24] & 0xFF) << 16 | (bytes[25] & 0xFF) << 8 | (bytes[26] & 0xFF));
                                                                int formerBalance = ((bytes[8] & 0xFF) << 8 | (bytes[9] & 0xFF));
                                                                int deviceType = (bytes[5] & 0xFF);
                                                                int lastTime = ((bytes[10] & 0xFF) << 8 | (bytes[11] & 0xFF));
                                                                int startTime = ((bytes[14] & 0xFF) << 8 | (bytes[15] & 0xFF));
                                                                int validDay = (bytes[17] & 0xFF);
                                                                int chargeTime = (bytes[18] & 0xFF);
                                                                int payRate = (bytes[19] & 0xFF);
                                                                int powerRate = (bytes[21] & 0xFF);
                                                                int isReturn = (bytes[22] & 0xFF);

                                                                //不需要联网查了

//                                                                Map<String, String> map = new HashMap<>(1);
//                                                                map.put("card_number", cardNum);
//                                                                String resp = HttpUtils.toServlet(map, "card", "getPrechargeBalance");
//                                                                JSONObject jsonObject = JSONObject.parseObject(resp);
//                                                                int preBalance = jsonObject.getIntValue("pre_balance");
//                                                                if (preBalance != -1) {
//                                                                    frame.tipLabel.setText("请输入充值相关参数");
//                                                                    new UnclaimedFrame(frame, cardNum, preBalance, formerBalance, deviceType, lastTime,
//                                                                            startTime, validDay, chargeTime, payRate, powerRate, isReturn);
//                                                                    frame.setEnabled(false);
//                                                                } else {
//                                                                    JOptionPane.showMessageDialog(null, "服务器上无该卡信息！");
//                                                                }

                                                            } else if (cardStatus == 2) {
                                                                frame.tipLabel.setText("未检测到IC卡");
                                                            } else if (cardStatus == 3) {
                                                                frame.tipLabel.setText("数据错误");
                                                                JOptionPane.showMessageDialog(null, "数据错误！");
                                                                LoginFrame.IS_RECHARGE = 0;
                                                            } else if (cardStatus == 4) {
                                                                //新卡
                                                                JOptionPane.showMessageDialog(null, "此卡为新卡！请前往充值界面进行初始化");
                                                                LoginFrame.IS_RECHARGE = 0;
                                                            } else if (cardStatus == 5) {
                                                                frame.tipLabel.setText("密码错误");
                                                                JOptionPane.showMessageDialog(null, "密码错误！");
                                                                LoginFrame.IS_RECHARGE = 0;
                                                            } else if (cardStatus == 6) {
                                                                frame.tipLabel.setText("非空白卡");
                                                                JOptionPane.showMessageDialog(null, "非空白卡！");
                                                                LoginFrame.IS_RECHARGE = 0;
                                                            } else if (cardStatus == 7) {
                                                                frame.tipLabel.setText("空白卡");
                                                                JOptionPane.showMessageDialog(null, "空白卡！");
                                                                LoginFrame.IS_RECHARGE = 0;
                                                            }
                                                        } else if (LoginFrame.IS_RECHARGE == 3) {
                                                            /**
                                                             * 注销前查询
                                                             */
                                                            int cardStatus = (bytes[4] & 0xFF);
                                                            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
                                                            if (cardStatus == 1) {
                                                                String cardNum = Integer.toHexString((bytes[23] & 0xFF) << 24 | (bytes[24] & 0xFF) << 16 | (bytes[25] & 0xFF) << 8 | (bytes[26] & 0xFF));
                                                                int balance = ((bytes[8] & 0xFF) << 8 | (bytes[9] & 0xFF));
                                                                int validDay = (bytes[17] & 0xFF);
                                                                int chargeTime = (bytes[18] & 0xFF);
                                                                int payRate = (bytes[19] & 0xFF);
                                                                int powerRate = (bytes[21] & 0xFF);
                                                                String logoutTime = sdf.format(new Date());
                                                                String operator = UserManager.getUser().getUserName();
                                                                String phone = "-", username = "-";
                                                                int cardType = 0;
                                                                if (ServiceImpl.getInstance().isCardExist(cardNum)) {
                                                                    cardType = ServiceImpl.getInstance().getCardTypeByNum(cardNum);
                                                                    phone = ServiceImpl.getInstance().getCardPhoneByNum(cardNum);
                                                                    username = ServiceImpl.getInstance().getUsernameByCardNum(cardNum);
                                                                }
                                                                String community = UserManager.getUser().getCommunity();
                                                                LogoutCardManager.addCard(new LogoutCard(operator, cardNum, phone, username, cardType,
                                                                        logoutTime, balance, validDay, chargeTime, payRate, powerRate, community));

                                                                int isLogout = JOptionPane.showConfirmDialog(null, "确认注销卡 : " + cardNum + "?", null, JOptionPane.OK_CANCEL_OPTION);
                                                                if (isLogout == 0) {
                                                                    frame.tipLabel.setText("正在准备注销，请将卡片放至充值机");
                                                                    SerialPort serialPort = PortManager.getSerialPort();
                                                                    if (serialPort != null) {
                                                                        String systemPassword = UserManager.getUser().getSystemPassword();
                                                                        try {
                                                                            SerialPortUtils.sendToPort(serialPort, CommandUtils.logoffCommand(systemPassword, 0));
                                                                        } catch (SendDataToSerialPortFailure | SerialPortOutputStreamCloseFailure sendDataToSerialPortFailure) {
                                                                            sendDataToSerialPortFailure.printStackTrace();
                                                                        }
                                                                    }
                                                                }

                                                            } else if (cardStatus == 2) {
                                                                frame.tipLabel.setText("未检测到IC卡");
                                                            } else if (cardStatus == 3) {
                                                                frame.tipLabel.setText("数据错误");
                                                                JOptionPane.showMessageDialog(null, "数据错误！");
                                                                LoginFrame.IS_RECHARGE = 0;
                                                            } else if (cardStatus == 4) {
                                                                //新卡
                                                                String cardNum = Integer.toHexString((bytes[23] & 0xFF) << 24 | (bytes[24] & 0xFF) << 16 | (bytes[25] & 0xFF) << 8 | (bytes[26] & 0xFF));
                                                                int balance = 0;
                                                                int validDay = 0;
                                                                int chargeTime = 0;
                                                                int payRate = 0;
                                                                int powerRate = 0;
                                                                String logoutTime = sdf.format(new Date());
                                                                String operator = UserManager.getUser().getUserName();
                                                                String phone = "-", username = "-";
                                                                int cardType = 4;//标识新卡
                                                                if (ServiceImpl.getInstance().isCardExist(cardNum)) {
                                                                    phone = ServiceImpl.getInstance().getCardPhoneByNum(cardNum);
                                                                    username = ServiceImpl.getInstance().getUsernameByCardNum(cardNum);
                                                                }
                                                                String community = UserManager.getUser().getCommunity();
                                                                LogoutCardManager.addCard(new LogoutCard(operator, cardNum, phone, username, cardType,
                                                                        logoutTime, balance, validDay, chargeTime, payRate, powerRate, community));

                                                                int isLogout = JOptionPane.showConfirmDialog(null, "确认注销卡 : " + cardNum + "?", null, JOptionPane.OK_CANCEL_OPTION);
                                                                if (isLogout == 0) {
                                                                    frame.tipLabel.setText("正在准备注销，请将卡片放至充值机");
                                                                    SerialPort serialPort = PortManager.getSerialPort();
                                                                    if (serialPort != null) {
                                                                        String systemPassword = UserManager.getUser().getSystemPassword();
                                                                        try {
                                                                            SerialPortUtils.sendToPort(serialPort, CommandUtils.logoffCommand(systemPassword, 0));
                                                                        } catch (SendDataToSerialPortFailure | SerialPortOutputStreamCloseFailure sendDataToSerialPortFailure) {
                                                                            sendDataToSerialPortFailure.printStackTrace();
                                                                        }
                                                                    }
                                                                }

                                                            } else if (cardStatus == 5) {
                                                                frame.tipLabel.setText("密码错误");
                                                                JOptionPane.showMessageDialog(null, "密码错误！");
                                                                LoginFrame.IS_RECHARGE = 0;
                                                            } else if (cardStatus == 6) {
                                                                frame.tipLabel.setText("非空白卡");
                                                                JOptionPane.showMessageDialog(null, "非空白卡！");
                                                                LoginFrame.IS_RECHARGE = 0;
                                                            } else if (cardStatus == 7) {
                                                                frame.tipLabel.setText("空白卡");
                                                                JOptionPane.showMessageDialog(null, "空白卡！");
                                                                LoginFrame.IS_RECHARGE = 0;
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

                                                            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
                                                            String regTime = sdf.format(new Date());

//                                                            if (!"-".equals(REG_PHONE) && !"".equals(REG_PHONE) && REG_PHONE != null) {
//                                                                //有手机号注册
//                                                                System.out.println("有手机号注册");
//                                                                Map<String, String> map = new HashMap<>(7);
//                                                                try {
//                                                                    map.put("action", "register");
//                                                                    map.put("phone", LoginFrame.REG_PHONE);
//                                                                    map.put("username", URLEncoder.encode(LoginFrame.REG_NAME, "utf-8"));
//                                                                    map.put("card_number", cardNum);
//                                                                    map.put("reg_time", regTime);
//                                                                    map.put("operator", URLEncoder.encode(UserManager.getUser().getUserName(), "utf-8"));
//                                                                    map.put("community", URLEncoder.encode(UserManager.getUser().getCommunity(), "utf-8"));
//                                                                } catch (UnsupportedEncodingException e) {
//                                                                    e.printStackTrace();
//                                                                }
//                                                                HttpUtils.toServlet(map, "card", "register");
//                                                            } else {
                                                            //无手机号注册
                                                            System.out.println("无手机号注册");
                                                            Map<String, String> map = new HashMap<>(7);
                                                            try {
                                                                map.put("action", "register");
                                                                map.put("phone", LoginFrame.REG_PHONE);
                                                                map.put("username", URLEncoder.encode(LoginFrame.REG_NAME, "utf-8"));
                                                                map.put("card_number", cardNum);
                                                                map.put("reg_time", regTime);
                                                                map.put("operator", URLEncoder.encode(UserManager.getUser().getUserName(), "utf-8"));
                                                                map.put("community", URLEncoder.encode(UserManager.getUser().getCommunity(), "utf-8"));
                                                            } catch (UnsupportedEncodingException e) {
                                                                e.printStackTrace();
                                                            }
//                                                            HttpUtils.toServlet(map, "card", "addRegisterHis");


//                                                            }


                                                            LoginFrame.REG_PHONE = "-";
                                                            LoginFrame.REG_NAME = "-";
                                                        } else if (registerResult == 2) {
                                                            frame.tipLabel.setText("未检测到IC卡");
                                                        } else if (registerResult == 3) {
                                                            frame.tipLabel.setText("注册失败！数据错误");
                                                            JOptionPane.showMessageDialog(null, "数据错误！", "注册提示", JOptionPane.WARNING_MESSAGE);
                                                            LoginFrame.REG_PHONE = "-";
                                                            LoginFrame.REG_NAME = "-";
                                                        } else if (registerResult == 4) {
                                                            frame.tipLabel.setText("此卡为新卡");
                                                            JOptionPane.showMessageDialog(null, "此卡为新卡！", "注册提示", JOptionPane.WARNING_MESSAGE);
                                                            LoginFrame.REG_PHONE = "-";
                                                            LoginFrame.REG_NAME = "-";
                                                        } else if (registerResult == 5) {
                                                            frame.tipLabel.setText("注册失败！密码错误");
                                                            JOptionPane.showMessageDialog(null, "密码错误！", "注册提示", JOptionPane.WARNING_MESSAGE);
                                                            LoginFrame.REG_PHONE = "-";
                                                            LoginFrame.REG_NAME = "-";
                                                        } else if (registerResult == 6) {
                                                            frame.tipLabel.setText("非空白卡");
                                                            JOptionPane.showMessageDialog(null, "非空白卡！", "注册提示", JOptionPane.WARNING_MESSAGE);
                                                            LoginFrame.REG_PHONE = "-";
                                                            LoginFrame.REG_NAME = "-";
                                                        } else if (registerResult == 7) {
                                                            frame.tipLabel.setText("空白卡");
                                                            JOptionPane.showMessageDialog(null, "空白卡！", "注册提示", JOptionPane.WARNING_MESSAGE);
                                                            LoginFrame.REG_PHONE = "-";
                                                            LoginFrame.REG_NAME = "-";
                                                        }

                                                    } else {
                                                        System.out.println("b3 crc校验错误 ");
                                                        System.out.println("b3 crc = " + crc3 + " ,b3 crccheck : " + crcCheck3);
                                                    }
                                                    break;

                                                case 0xb4:
                                                    //注销返回
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
                                                            LogoutCard card = LogoutCardManager.getCard(cardNum);
                                                            String phone = card.getPhone();
                                                            System.out.println("注销成功返回! phone : " + phone);
                                                            if (!"-".equals(phone) && !"".equals(phone) && phone != null) {
                                                                System.out.println("注销有手机号");
                                                                Map<String, String> map = new HashMap<>(13);
                                                                try {
                                                                    map.put("action", "logoff");
                                                                    map.put("card_number", cardNum);
                                                                    map.put("operator", URLEncoder.encode(card.getOperator(), "utf-8"));
                                                                    map.put("phone", card.getPhone());
                                                                    map.put("username", URLEncoder.encode(card.getUsername(), "utf-8"));
                                                                    map.put("cardType", String.valueOf(card.getCardType()));
                                                                    map.put("logoutTime", card.getLogoutTime());
                                                                    map.put("balance", String.valueOf(card.getBalance()));
                                                                    map.put("validDay", String.valueOf(card.getValidDay()));
                                                                    map.put("chargeTime", String.valueOf(card.getChargeTime()));
                                                                    map.put("payRate", String.valueOf(card.getPayRate()));
                                                                    map.put("powerRate", String.valueOf(card.getPowerRate()));
                                                                    map.put("community", URLEncoder.encode(card.getCommunity(), "utf-8"));
                                                                } catch (UnsupportedEncodingException e) {
                                                                    e.printStackTrace();
                                                                }
//                                                                String code = HttpUtils.toServlet(map, "card", "logoff");
//                                                                if ("0".equals(code)) {
                                                                LogoutCardManager.removeCard();
//                                                                }
                                                            } else {
                                                                System.out.println("注销无手机号");
                                                                Map<String, String> map = new HashMap<>(13);
                                                                try {
                                                                    map.put("action", "logoff");
                                                                    map.put("card_number", cardNum);
                                                                    map.put("operator", URLEncoder.encode(card.getOperator(), "utf-8"));
                                                                    map.put("phone", card.getPhone());
                                                                    map.put("username", URLEncoder.encode(card.getUsername(), "utf-8"));
                                                                    map.put("cardType", String.valueOf(card.getCardType()));
                                                                    map.put("logoutTime", card.getLogoutTime());
                                                                    map.put("balance", String.valueOf(card.getBalance()));
                                                                    map.put("validDay", String.valueOf(card.getValidDay()));
                                                                    map.put("chargeTime", String.valueOf(card.getChargeTime()));
                                                                    map.put("payRate", String.valueOf(card.getPayRate()));
                                                                    map.put("powerRate", String.valueOf(card.getPowerRate()));
                                                                    map.put("community", URLEncoder.encode(card.getCommunity(), "utf-8"));
                                                                } catch (UnsupportedEncodingException e) {
                                                                    e.printStackTrace();
                                                                }
//                                                                String code = HttpUtils.toServlet(map, "card", "addLogoutHis");
//                                                                if ("0".equals(code)) {
                                                                LogoutCardManager.removeCard();
//                                                                }
                                                            }

                                                            frame.tipLabel.setText("注销成功");
                                                            JOptionPane.showMessageDialog(null, "注销成功！", "注销提示", JOptionPane.INFORMATION_MESSAGE);
                                                        } else if (logoutResult == 2) {
                                                            frame.tipLabel.setText("未检测到IC卡");
                                                        } else if (logoutResult == 3) {
                                                            frame.tipLabel.setText("注销失败！数据错误");
                                                            JOptionPane.showMessageDialog(null, "数据错误！", "注销提示", JOptionPane.WARNING_MESSAGE);
                                                        } else if (logoutResult == 4) {
                                                            frame.tipLabel.setText("此卡为新卡");
                                                            JOptionPane.showMessageDialog(null, "此卡为新卡！", "注销提示", JOptionPane.WARNING_MESSAGE);
                                                        } else if (logoutResult == 5) {
                                                            frame.tipLabel.setText("注销失败！密码错误");
                                                            JOptionPane.showMessageDialog(null, "密码错误！", "注销提示", JOptionPane.WARNING_MESSAGE);
                                                        } else if (logoutResult == 6) {
                                                            frame.tipLabel.setText("非空白卡");
                                                            JOptionPane.showMessageDialog(null, "非空白卡！", "注销提示", JOptionPane.WARNING_MESSAGE);
                                                        } else if (logoutResult == 7) {
                                                            frame.tipLabel.setText("空白卡");
                                                            JOptionPane.showMessageDialog(null, "空白卡！", "注销提示", JOptionPane.WARNING_MESSAGE);
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
                                                        if (LoginFrame.PRE_HIS_MARK == 0) {
                                                            //充值返回
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
                                                                    int cardType = card.getCardType();
                                                                    int topUp = card.getTopUp();
                                                                    int validDay = card.getValidDay();
                                                                    int rechargeTime = card.getRechargeTime();
                                                                    int payRate = card.getPayRate();
                                                                    int powerRate = card.getPowerRate();
                                                                    String operator = UserManager.getUser().getUserName();
                                                                    //添加充值记录到本地
                                                                    ServiceImpl.getInstance().addRechargeHis(cardNum, username, phone, cardType,
                                                                            balance, topUp, validDay, rechargeTime, payRate, powerRate, nowTime, operator, "");
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
                                                                if (CardManager.getCardByNum(cardNum) != null) {
                                                                    Card card = CardManager.getCardByNum(cardNum);
                                                                    try {
                                                                        Map<String, String> map = new HashMap<>(6);
                                                                        map.put("card_number", cardNum);
                                                                        map.put("card_type", URLEncoder.encode(Utils.getStringCardType(card.getCardType()), "utf-8"));
                                                                        map.put("valid_day", String.valueOf(card.getValidDay()));
                                                                        map.put("recharge_time", String.valueOf(card.getRechargeTime()));
                                                                        map.put("pay_rate", String.valueOf(card.getPayRate()));
                                                                        map.put("power_rate", String.valueOf(card.getPowerRate()));
//                                                                        HttpUtils.toServlet(map, "card", "updateCardInfo");
                                                                    } catch (UnsupportedEncodingException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            } else if (rechargeResult == 3) {
                                                                frame.tipLabel.setText("充值失败");
                                                                JOptionPane.showMessageDialog(null, "充值失败！", "充值提示", JOptionPane.WARNING_MESSAGE);
                                                            }
                                                        } else {
                                                            //圈存领取返回
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
                                                                    try {
                                                                        Map<String, String> map = new HashMap<>(10);
                                                                        map.put("action", "addWithdrawHis");
                                                                        map.put("card_number", cardNum);
                                                                        map.put("username", URLEncoder.encode(username, "utf-8"));
                                                                        map.put("phone", phone);
                                                                        map.put("card_type", String.valueOf(card.getCardType()));
                                                                        map.put("balance", String.valueOf(balance));
                                                                        map.put("top_up", String.valueOf(card.getTopUp()));
                                                                        map.put("now_time", nowTime);
                                                                        map.put("operator", URLEncoder.encode(UserManager.getUser().getUserName(), "utf-8"));
                                                                        map.put("community", URLEncoder.encode(UserManager.getUser().getCommunity(), "utf-8"));
//                                                                        HttpUtils.toServlet(map, "withdraw", "addWithdrawHis");
                                                                    } catch (UnsupportedEncodingException e) {
                                                                        e.printStackTrace();
                                                                    }

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
                                                                frame.tipLabel.setText("领取成功");
                                                                JOptionPane.showMessageDialog(null, "领取成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                                                                if (CardManager.getCardByNum(cardNum) != null) {
                                                                    Card card = CardManager.getCardByNum(cardNum);
                                                                    Map<String, String> map2 = new HashMap<>(4);
                                                                    map2.put("action", "updateBalance");
                                                                    map2.put("card_number", cardNum);
                                                                    map2.put("option", "subtract");
                                                                    map2.put("money", String.valueOf(card.getTopUp() / 10));
//                                                                    HttpUtils.toServlet(map2, "card", "updateBalance");
                                                                }
                                                                LoginFrame.PRE_HIS_MARK = 0;
                                                            } else if (rechargeResult == 3) {
                                                                frame.tipLabel.setText("领取失败");
                                                                JOptionPane.showMessageDialog(null, "领取失败！", "提示", JOptionPane.WARNING_MESSAGE);
                                                                LoginFrame.PRE_HIS_MARK = 0;
                                                            }
                                                        }
                                                    } else {
                                                        System.out.println("b5 crc校验错误 ");
                                                        System.out.println("b5 crc = " + crc5 + " ,b5 crccheck : " + crcCheck5);
                                                    }
                                                    break;


                                                case 0xe1:
                                                    int crcE1 = ((bytes[6] & 0xFF) << 8 | (bytes[5] & 0xFF));
                                                    int crccheckE1 = Utils.crc16(bytes, 3, bytes.length - 6);
                                                    if (crcE1 == crccheckE1) {
                                                        if (E1_STATUS == 0) {
                                                            if (E1_HANDMADES == 0) {
                                                                long now = System.currentTimeMillis() / 1000;
                                                                if (now - LOGIN_TIME > 6) {
                                                                    System.out.println("now : " + now + " , login_time : " + LOGIN_TIME);
                                                                    new Thread(() -> {
                                                                        ProgressBarWithTipFrame progressBarWithTipFrame = new ProgressBarWithTipFrame(SERVER_HARDWARE_SIZE, 4500);
                                                                        ProgressFrameWithTipManager.addFrame("progressWithTipBar", progressBarWithTipFrame);
                                                                    }).start();
                                                                    SerialPort serialPort = PortManager.getSerialPort();
                                                                    if (serialPort != null) {
                                                                        try {
                                                                            SerialPortUtils.sendToPort(serialPort, CommandUtils.F1Command(SERVER_HARDWARE_SIZE));
                                                                            E1_STATUS = 1;
                                                                        } catch (SendDataToSerialPortFailure | SerialPortOutputStreamCloseFailure sendDataToSerialPortFailure) {
                                                                            sendDataToSerialPortFailure.printStackTrace();
                                                                        }
                                                                    }
                                                                }
                                                            } else {
                                                                SerialPort serialPort = PortManager.getSerialPort();
                                                                if (serialPort != null) {
                                                                    try {
                                                                        SerialPortUtils.sendToPort(serialPort, CommandUtils.F1Command(SERVER_HARDWARE_SIZE));
                                                                        E1_STATUS = 1;
                                                                    } catch (SendDataToSerialPortFailure | SerialPortOutputStreamCloseFailure sendDataToSerialPortFailure) {
                                                                        sendDataToSerialPortFailure.printStackTrace();
                                                                    }
                                                                }
                                                            }


                                                        }
                                                    } else {
                                                        System.out.println("e1 crc校验错误！ ");
                                                        System.out.println("e1 crc = " + crcE1 + " , e1 crccheck : " + crccheckE1);
                                                    }
                                                    break;
                                                case 0xe2:
                                                    int crcE2 = ((bytes[6] & 0xFF) << 8 | (bytes[5] & 0xFF));
                                                    int crccheckE2 = Utils.crc16(bytes, 3, bytes.length - 6);
                                                    if (crcE2 == crccheckE2) {
                                                        HARD_UPDATE_STATUS = 0;
                                                        //开始发送数据包
                                                        FTPUtils ftpUtils = new FTPUtils();
                                                        String serverPath = "/home/FTPUser/hardware";
                                                        String fileName = "USB_CARD_RW_V" + SERVER_HARD_VERSION + ".bin";
                                                        FileSystemView fsv = FileSystemView.getFileSystemView();
                                                        String localPath = fsv.getHomeDirectory().getPath() + "\\hardware_bin_temp";
                                                        File file = ftpUtils.downloadFile(serverPath, fileName, localPath);

                                                        byte[] fileBytes = Utils.File2byte(file);
                                                        int count = (int) (file.length() / 1024) + 1;
                                                        Map<Integer, byte[]> map = new HashMap<>(count);
                                                        for (int i = 0; i < count && fileBytes.length - i * 1024 > 1024; i++) {
                                                            byte[] temp = new byte[1024];
                                                            try {
                                                                System.arraycopy(fileBytes, i * 1024, temp, 0, 1024);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                            map.put(i + 1, temp);
                                                        }
                                                        if (map.size() != count) {
                                                            int desc = map.size() + 1;
                                                            int tail = fileBytes.length - (desc - 1) * 1024;
                                                            byte[] leftBytes = new byte[tail];
                                                            System.arraycopy(fileBytes, (desc - 1) * 1024, leftBytes, 0, tail);
                                                            map.put(desc, leftBytes);
                                                        }
                                                        FILE_BYTES_MAP = map;
                                                        SEND_COUNT = 1;
                                                        SerialPort serialPort = PortManager.getSerialPort();
                                                        if (serialPort != null) {
                                                            try {
                                                                SerialPortUtils.sendToPort(serialPort, CommandUtils.updateCommand(SEND_COUNT, FILE_BYTES_MAP.size(), FILE_BYTES_MAP.get(SEND_COUNT)));
                                                            } catch (SendDataToSerialPortFailure | SerialPortOutputStreamCloseFailure sendDataToSerialPortFailure) {
                                                                sendDataToSerialPortFailure.printStackTrace();
                                                            }
                                                        }
                                                    } else {
                                                        System.out.println("e2 crc校验错误！ ");
                                                        System.out.println("e2 crc = " + crcE2 + " , e2 crccheck : " + crccheckE2);
                                                    }
                                                    break;
                                                case 0xe3:
                                                    int crcE3 = ((bytes[6] & 0xFF) << 8 | (bytes[5] & 0xFF));
                                                    int crccheckE3 = Utils.crc16(bytes, 3, bytes.length - 6);
                                                    if (crcE3 == crccheckE3) {
                                                        int status = bytes[4] & 0xFF;
                                                        if (status == 1) {
                                                            System.out.println("接收成功！ 总包数 ： " + FILE_BYTES_MAP.size() + " , 已发送 ： " + SEND_COUNT);
                                                            //发送成功
                                                            if (SEND_COUNT < FILE_BYTES_MAP.size()) {
                                                                SEND_COUNT++;
                                                                SerialPort serialPort = PortManager.getSerialPort();
                                                                if (serialPort != null) {
                                                                    try {
                                                                        SerialPortUtils.sendToPort(serialPort, CommandUtils.updateCommand(SEND_COUNT, FILE_BYTES_MAP.size(), FILE_BYTES_MAP.get(SEND_COUNT)));
                                                                    } catch (SendDataToSerialPortFailure | SerialPortOutputStreamCloseFailure sendDataToSerialPortFailure) {
                                                                        sendDataToSerialPortFailure.printStackTrace();
                                                                    }
                                                                }
                                                            } else if (SEND_COUNT == FILE_BYTES_MAP.size()) {
                                                                ProgressBarFrame progressBarFrame = ProgressFrameManager.getFrameByName("progressBar");
                                                                if (progressBarFrame != null) {
                                                                    progressBarFrame.dispose();
                                                                }
                                                                ProgressBarWithTipFrame progressBarWithTipFrame = ProgressFrameWithTipManager.getFrameByName("progressWithTipBar");
                                                                if (progressBarWithTipFrame != null) {
                                                                    progressBarWithTipFrame.dispose();
                                                                }
                                                                JOptionPane.showMessageDialog(null, "升级成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                                                                FILE_BYTES_MAP = null;
                                                                SEND_COUNT = 0;
                                                                E1_STATUS = 0;
                                                                E1_HANDMADES = 0;
                                                                FileSystemView fsv = FileSystemView.getFileSystemView();
                                                                File file = new File(fsv.getHomeDirectory() + "/hardware_bin_temp");
                                                                Utils.deleteFile(file);
                                                            }
                                                        } else {
                                                            //发送失败
                                                            ProgressBarFrame progressBarFrame = ProgressFrameManager.getFrameByName("progressBar");
                                                            progressBarFrame.dispose();
                                                            JOptionPane.showMessageDialog(null, "升级错误！", "提示", JOptionPane.WARNING_MESSAGE);
                                                            FILE_BYTES_MAP = null;
                                                            SEND_COUNT = 0;
                                                            E1_STATUS = 0;
                                                            E1_HANDMADES = 0;
                                                            FileSystemView fsv = FileSystemView.getFileSystemView();
                                                            File file = new File(fsv.getHomeDirectory() + "/hardware_bin_temp");
                                                            Utils.deleteFile(file);
                                                        }
                                                    } else {
                                                        System.out.println("e3 crc校验错误！ ");
                                                        System.out.println("e3 crc = " + crcE3 + " , e3 crccheck : " + crccheckE3);
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
                    }


                    break;
                default:
                    break;
            }

        }
    }
}




















