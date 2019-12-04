package swing;

import bean.*;
import bean.manager.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import commands.*;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import serial.*;
import service.impl.*;
import utils.*;
import exception.*;

import java.awt.event.*;

import job.*;
import org.quartz.*;

import javax.swing.*;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.awt.*;
import java.util.List;

import static swing.LoginFrame.*;


/**
 * @author kingfans
 */
public class RechargeFrame extends JFrame {
    private User user;
    public JComboBox<String> portChooser;
    public JLabel deviceStatusText;
    public JLabel softwareVersionText;
    public JLabel hardwareVersionText;
    public JLabel serverStatusText;
    public JPanel CardInfoPanel;
    public JLabel cardNumText;
    public JLabel cardTypeText;
    public JLabel phoneText;
    public JLabel balanceText;
    public JLabel validTimeText;
    public JLabel remainTimeText;
    public JLabel lastTimeText;
    public JLabel startTimeText;
    public JLabel chargeTimeText;
    public JLabel payRateText;
    public JLabel powerRateText;
    public JLabel welcomeLabel;
    public JLabel tipLabel;
    public JLabel comunityLabel;

    public JButton logoutButton;
    public JButton registerButton;
    public JButton queryButton;
    public JButton rechargeButton;
    public JButton rechargeHisButton;
    public JButton unclaimedButton;
    public JButton prechargeHisButton;
    public JButton registerHisButton;
    public JButton logoutHisButton;

    private SimpleDateFormat sdf;

    public RechargeFrame(User user) {
        this.user = user;
        this.sdf = new SimpleDateFormat("yyyy-MM-dd");
        initComponents();
        System.out.println(user.toString());
        this.setSize(820, 610);
        Image icon = Toolkit.getDefaultToolkit().getImage("resources/dk_logo.png");
        this.setIconImage(icon);
        this.setTitle("得康充值系统 v" + SOFT_VERSION + "(离线版)");
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        FrameManager.addFrame("recharge", this);

        //检查有没有新版本
//        String response = HttpUtils.toServlet(null, "version", "getSoftwareVersion");
//        JSONObject jsonObject = JSONObject.parseObject(response);
//        String code = jsonObject.getString("code");
//        System.out.println("登录软件更新检查! code : " + code);
//        if ("0".equals(code)) {
//            String resVersion = jsonObject.getString("version");
//            String version = resVersion.substring(8, resVersion.length() - 4);
//            long size = jsonObject.getLong("size") / 1024;
//            System.out.println("有可更新软件版本! software version : " + version + " , size : " + size);
//            if (!version.equals(SOFT_VERSION)) {
//                new LoginUpdateFrame(version, size);
//            }
//        }
    }

    private void sectorMenuItemActionPerformed(ActionEvent e) {
        new SystemPasswdFrame(user, this, new SectorFrame(user));
        this.setEnabled(false);
    }

    private void loginPasswdMenuItemActionPerformed(ActionEvent e) {
        if (user.getIsAdmin() == 1) {
            new SystemPasswdFrame(user, this, new LoginPasswdSettingFrame(user, this));
            this.setEnabled(false);
        } else {
            LoginPasswdSettingFrame frame = new LoginPasswdSettingFrame(user, this);
            frame.setVisible(true);
        }
    }

    private void accountsManageMenuItemActionPerformed(ActionEvent e) {
        int n = ServiceImpl.getInstance().getSubAccountsNum();
        List<Map<String, Object>> subAccounts = ServiceImpl.getInstance().getSubAccountsInfo();
        new SystemPasswdFrame(user, this, new AccountManageFrame(user, n, subAccounts));
        this.setEnabled(false);
    }

    private void systemPasswdMenuItemActionPerformed(ActionEvent e) {
        new SystemPasswdFrame(user, this, new SystemPasswdSettingFrame(user));
        this.setEnabled(false);
    }

    private void portRateMenuItemActionPerformed(ActionEvent e) {
        new SystemPasswdFrame(user, this, new PortrateFrame(user));
        this.setEnabled(false);
    }

    private void communityMenuItemActionPerformed(ActionEvent e) {
//        new SystemPasswdFrame(user, this, new ChangeCommunityFrame(user));
//        this.setEnabled(false);
    }

    private void exitActionPerformed(ActionEvent e) {
        int n = JOptionPane.showConfirmDialog(null, "确认退出程序？", "", JOptionPane.OK_CANCEL_OPTION);
        if (0 == n) {
            this.dispose();
            PortManager.removePort();
            UserManager.removeUser();
            try {
                FrameManager.removeFrame("recharge");
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            System.exit(0);
        }
    }


    //注销
    private void logoutButtonActionPerformed(ActionEvent e) {
        LoginFrame.IS_RECHARGE = 3;
        balanceText.setText("-");
        validTimeText.setText("-");
        remainTimeText.setText("     -");
        cardNumText.setText("-");
        cardTypeText.setText("-");
        phoneText.setText("-");
        lastTimeText.setText("-");
        startTimeText.setText("-");
        chargeTimeText.setText("-");
        payRateText.setText("-");
        powerRateText.setText("-");
        tipLabel.setText("准备注销，请将卡放至充值机");
        SerialPort serialPort = PortManager.getSerialPort();
        if (serialPort != null) {
            String systemPassword = user.getSystemPassword();
            try {
                SerialPortUtils.sendToPort(serialPort, CommandUtils.queryCommand(systemPassword));
            } catch (SendDataToSerialPortFailure | SerialPortOutputStreamCloseFailure sendDataToSerialPortFailure) {
                sendDataToSerialPortFailure.printStackTrace();
            }
        }
    }

    //查询
    private void queryButtonActionPerformed(ActionEvent e) {
        LoginFrame.IS_RECHARGE = 0;
        balanceText.setText("-");
        validTimeText.setText("-");
        remainTimeText.setText("     -");
        cardNumText.setText("-");
        cardTypeText.setText("-");
        phoneText.setText("-");
        lastTimeText.setText("-");
        startTimeText.setText("-");
        chargeTimeText.setText("-");
        payRateText.setText("-");
        powerRateText.setText("-");
        tipLabel.setText("正在查询，请将卡片放至充值机");
        SerialPort serialPort = PortManager.getSerialPort();
        if (serialPort != null) {
            String systemPassword = user.getSystemPassword();
            try {
                SerialPortUtils.sendToPort(serialPort, CommandUtils.queryCommand(systemPassword));
            } catch (SendDataToSerialPortFailure | SerialPortOutputStreamCloseFailure sendDataToSerialPortFailure) {
                sendDataToSerialPortFailure.printStackTrace();
            }
        }
    }

    //充值
    private void rechargeButtonActionPerformed(ActionEvent e) {
        LoginFrame.IS_RECHARGE = 1;
        balanceText.setText("-");
        validTimeText.setText("-");
        remainTimeText.setText("     -");
        cardNumText.setText("-");
        cardTypeText.setText("-");
        phoneText.setText("-");
        lastTimeText.setText("-");
        startTimeText.setText("-");
        chargeTimeText.setText("-");
        payRateText.setText("-");
        powerRateText.setText("-");
        tipLabel.setText("准备充值，请将卡放至充值机");
        SerialPort serialPort = PortManager.getSerialPort();
        if (serialPort != null) {
            String systemPassword = user.getSystemPassword();
            try {
                SerialPortUtils.sendToPort(serialPort, CommandUtils.queryCommand(systemPassword));
            } catch (SendDataToSerialPortFailure | SerialPortOutputStreamCloseFailure sendDataToSerialPortFailure) {
                sendDataToSerialPortFailure.printStackTrace();
            }
        }
    }

    //圈存领取
    private void unclaimedButtonActionPerformed(ActionEvent e) {
        LoginFrame.IS_RECHARGE = 2;
        balanceText.setText("-");
        validTimeText.setText("-");
        remainTimeText.setText("     -");
        cardNumText.setText("-");
        cardTypeText.setText("-");
        phoneText.setText("-");
        lastTimeText.setText("-");
        startTimeText.setText("-");
        chargeTimeText.setText("-");
        payRateText.setText("-");
        powerRateText.setText("-");
        tipLabel.setText("准备领取圈存，请将卡放至充值机");
        SerialPort serialPort = PortManager.getSerialPort();
        if (serialPort != null) {
            String systemPassword = user.getSystemPassword();
            try {
                SerialPortUtils.sendToPort(serialPort, CommandUtils.queryCommand(systemPassword));
            } catch (SendDataToSerialPortFailure | SerialPortOutputStreamCloseFailure sendDataToSerialPortFailure) {
                sendDataToSerialPortFailure.printStackTrace();
            }
        }
    }

    //注册
    private void registerButtonActionPerformed(ActionEvent e) {
        balanceText.setText("-");
        validTimeText.setText("-");
        remainTimeText.setText("     -");
        cardNumText.setText("-");
        cardTypeText.setText("-");
        phoneText.setText("-");
        lastTimeText.setText("-");
        startTimeText.setText("-");
        chargeTimeText.setText("-");
        payRateText.setText("-");
        powerRateText.setText("-");
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }
        new RegisterFrame(user, this);
        this.setEnabled(false);
    }

    //充值记录
    private void rechargeHisButtonActionPerformed(ActionEvent e) {
        Map<String, String> map = new HashMap<>(1);
        try {
            map.put("community", URLEncoder.encode(user.getCommunity(), "utf-8"));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        int n = ServiceImpl.getInstance().getRechargeCount();
        List<Map<String, Object>> his = ServiceImpl.getInstance().getRechargeHisAll();
        int totalAmount = 0;
        for (int i = 0; i < his.size(); i++) {
            Map<String, Object> map1 = his.get(i);
            int topUp = (int) map1.get("top_up");
            totalAmount += topUp;
        }
        this.setEnabled(false);
        new HisTableFrame(user, n, his, this, totalAmount, "2018-01-01", sdf.format(new Date()));
    }

    //圈存领取记录
    private void prechargeHisButtonActionPerformed(ActionEvent e) {
//        Map<String, String> map = new HashMap<>(1);
//        try {
//            map.put("community", URLEncoder.encode(user.getCommunity(), "utf-8"));
//        } catch (UnsupportedEncodingException e1) {
//            e1.printStackTrace();
//        }
//        String rechargeAllJson = HttpUtils.toServlet(map, "withdraw", "getWithdrawHis");
//        JSONObject jsonObject = JSONObject.parseObject(rechargeAllJson);
//        int n = 0;
//        List<Map<String, Object>> his = null;
//        int totalAmount = 0;
//        try {
//            String hist = URLDecoder.decode(jsonObject.getString("withdraw_his"), "utf-8");
//            JSONObject object = JSONObject.parseObject(hist);
//            JSONArray jsonArray = (JSONArray) object.get("his");
//            n = object.getInteger("count");
//            his = new ArrayList<>();
//            for (int i = 0; i < jsonArray.size(); i++) {
//                Map<String, Object> map1 = (Map<String, Object>) jsonArray.get(i);
//                int a = (int) map1.get("top_up");
//                totalAmount += a;
//                his.add(map1);
//            }
//        } catch (UnsupportedEncodingException e1) {
//            e1.printStackTrace();
//        }
//        this.setEnabled(false);
//        new WithdrawHisFrame(user, n, his, this, totalAmount, "2018-01-01", sdf.format(new Date()));
    }

    //注册记录
    private void registerHisButtonActionPerformed(ActionEvent e) {
//        Map<String, String> map = new HashMap<>(1);
//        try {
//            map.put("community", URLEncoder.encode(user.getCommunity(), "utf-8"));
//        } catch (UnsupportedEncodingException e1) {
//            e1.printStackTrace();
//        }
//        String registerHisJson = HttpUtils.toServlet(map, "card", "getRegisterHis");
//        JSONObject jsonObject = JSONObject.parseObject(registerHisJson);
//        int n = 0;
//        List<Map<String, Object>> his = null;
//        try {
//            String hist = URLDecoder.decode(jsonObject.getString("register_his"), "utf-8");
//            JSONObject object = JSONObject.parseObject(hist);
//            JSONArray jsonArray = (JSONArray) object.get("his");
//            n = object.getInteger("count");
//            his = new ArrayList<>();
//            for (int i = 0; i < jsonArray.size(); i++) {
//                his.add((Map<String, Object>) jsonArray.get(i));
//            }
//        } catch (UnsupportedEncodingException e1) {
//            e1.printStackTrace();
//        }
//        this.setEnabled(false);
//        new RegisterHisFrame(user, n, his, this);
    }

    private void logoutHisButtonActionPerformed(ActionEvent e) {

//        Map<String, String> map = new HashMap<>(1);
//        try {
//            map.put("community", URLEncoder.encode(user.getCommunity(), "utf-8"));
//        } catch (UnsupportedEncodingException e1) {
//            e1.printStackTrace();
//        }
//        String logoutHisJson = HttpUtils.toServlet(map, "card", "getLogoutHis");
//        JSONObject jsonObject = JSONObject.parseObject(logoutHisJson);
//        int n = 0;
//        List<Map<String, Object>> his = null;
//        try {
//            String hist = URLDecoder.decode(jsonObject.getString("logout_his"), "utf-8");
//            JSONObject object = JSONObject.parseObject(hist);
//            JSONArray jsonArray = (JSONArray) object.get("his");
//            n = object.getInteger("count");
//            his = new ArrayList<>();
//            for (int i = 0; i < jsonArray.size(); i++) {
//                his.add((Map<String, Object>) jsonArray.get(i));
//            }
//        } catch (UnsupportedEncodingException e1) {
//            e1.printStackTrace();
//        }
//        this.setEnabled(false);
//        new LogoutHisFrame(user, n, his, this);
    }


    private void aboutMenuItemActionPerformed(ActionEvent e) {
        new AboutFrame();
    }

    private void updateMenuItemActionPerformed(ActionEvent e) {
//        String response = HttpUtils.toServlet(null, "version", "getSoftwareVersion");
//        JSONObject jsonObject = JSONObject.parseObject(response);
//        String code = jsonObject.getString("code");
//        if ("0".equals(code)) {
//            String resVersion = jsonObject.getString("version");
//            String version = resVersion.substring(8, resVersion.length() - 4);
//            long size = jsonObject.getLong("size") / 1024;
//            if (version.equals(SOFT_VERSION)) {
//                new NoUpdateFrame();
//            } else {
//                new UpdateFrame(version, size);
//            }
//        } else {
//            new NoUpdateFrame();
//        }
    }

    private void hardUpdateMenuItemActionPerformed(ActionEvent e) {
        double localVersion = Double.parseDouble(LOCAL_HARD_VERSION);
        if ((!SERVER_HARD_VERSION.equals(LOCAL_HARD_VERSION) && (localVersion >= 0.3)) || "-".equals(LOCAL_HARD_VERSION)) {
            //可升级
            new HardwareUpdateFrame(SERVER_HARD_VERSION, SERVER_HARDWARE_SIZE);
        } else if (SERVER_HARD_VERSION.equals(LOCAL_HARD_VERSION)) {
            //不需要升级
            new NoUpdateHardFrame();
//            new HardwareUpdateFrame(SERVER_HARD_VERSION, SERVER_HARDWARE_SIZE);
        } else if (!"-".equals(LOCAL_HARD_VERSION) && (localVersion < 0.3)) {
            JOptionPane.showMessageDialog(null, "当前版本不支持升级！", "提示", JOptionPane.WARNING_MESSAGE);
        }

    }

    private void portChooserItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            deviceStatusText.setText("连接异常");
            deviceStatusText.setForeground(Color.red);
            hardwareVersionText.setText("-");
            softwareVersionText.setText("-");
            String selectPort = (String) portChooser.getSelectedItem();
            user.setPortOrder(portChooser.getSelectedIndex());
            ServiceImpl.getInstance().updatePortOrder(user.getUserId(), user.getUserName(), portChooser.getSelectedIndex());
            ServiceImpl.getInstance().updateSubPortOrder(portChooser.getSelectedIndex());
            PortManager.removePort();
            int portRate = Utils.getPortrate(user.getPortrateOrder());
            try {
                SerialPortUtils.openPort(selectPort, portRate, this);
                LOGIN_TIME = System.currentTimeMillis() / 1000;
            } catch (NotASerialPort | NoSuchPort | UnsupportedCommOperationException | PortInUse notASerialPort) {
                notASerialPort.printStackTrace();
            }
        }
    }

    private void rechargeWindowClosing(WindowEvent e) {
        int n = JOptionPane.showConfirmDialog(null, "确认退出程序？", "", JOptionPane.OK_CANCEL_OPTION);
        if (0 == n) {
            this.dispose();
            PortManager.removePort();
            UserManager.removeUser();
            try {
                FrameManager.removeFrame("recharge");
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            System.exit(0);
        }
    }

    private void initComponents() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        LOGIN_TIME = System.currentTimeMillis() / 1000;
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                RechargeFrame.this.rechargeWindowClosing(e);
            }
        });
        this.setTitle("充值系统");
        Container contentPane = this.getContentPane();
        contentPane.setLayout(null);


        /**
         * 导航栏
         */
        JMenuBar menuBar1 = new JMenuBar();
        JMenu fileMenu = new JMenu();
        fileMenu.setText("文件");
        fileMenu.setFont(new Font("Dialog", Font.PLAIN, 12));
        JMenuItem exitMenuItem = new JMenuItem();
        exitMenuItem.setText("退出");
        exitMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
        exitMenuItem.addActionListener(this::exitActionPerformed);
        fileMenu.add(exitMenuItem);
        menuBar1.add(fileMenu);

        JMenu settingMenu = new JMenu();
        settingMenu.setText("系统设置");
        settingMenu.setFont(new Font("Dialog", Font.PLAIN, 12));

        JMenuItem accountsManageMenuItem = new JMenuItem();
        accountsManageMenuItem.setText("多账号管理");
        accountsManageMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
        accountsManageMenuItem.addActionListener(this::accountsManageMenuItemActionPerformed);
        settingMenu.add(accountsManageMenuItem);
        if (user.getIsAdmin() == 0) {
            accountsManageMenuItem.setEnabled(false);
        } else {
            accountsManageMenuItem.setEnabled(true);
        }

        JMenuItem loginPasswdMenuItem = new JMenuItem();
        loginPasswdMenuItem.setText("登录账号设置");
        loginPasswdMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
        loginPasswdMenuItem.addActionListener(this::loginPasswdMenuItemActionPerformed);
        settingMenu.add(loginPasswdMenuItem);

        JMenuItem systemPasswdMenuItem = new JMenuItem();
        systemPasswdMenuItem.setText("刷卡器密码");
        systemPasswdMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
        systemPasswdMenuItem.addActionListener(this::systemPasswdMenuItemActionPerformed);
        settingMenu.add(systemPasswdMenuItem);
        if (user.getIsAdmin() == 0) {
            systemPasswdMenuItem.setEnabled(false);
        } else {
            systemPasswdMenuItem.setEnabled(true);
        }

        JMenuItem sectorMenuItem = new JMenuItem();
        sectorMenuItem.setText("扇区设置");
        sectorMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
        sectorMenuItem.addActionListener(this::sectorMenuItemActionPerformed);
        settingMenu.add(sectorMenuItem);
        if (user.getIsAdmin() == 0) {
            sectorMenuItem.setEnabled(false);
        } else {
            sectorMenuItem.setEnabled(true);
        }

        JMenuItem portRateMenuItem = new JMenuItem();
        portRateMenuItem.setText("波特率设置");
        portRateMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
        portRateMenuItem.addActionListener(this::portRateMenuItemActionPerformed);
        settingMenu.add(portRateMenuItem);
        if (user.getIsAdmin() == 0) {
            portRateMenuItem.setEnabled(false);
        } else {
            portRateMenuItem.setEnabled(true);
        }

//        JMenuItem communityMenuItem = new JMenuItem();
//        communityMenuItem.setText("小区设置");
//        communityMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
//        communityMenuItem.addActionListener(this::communityMenuItemActionPerformed);
//        settingMenu.add(communityMenuItem);
        menuBar1.add(settingMenu);


        JMenu helpMenu = new JMenu();
        helpMenu.setText("帮助");
        helpMenu.setFont(new Font("Dialog", Font.PLAIN, 12));

        JMenuItem aboutMenuItem = new JMenuItem();
        aboutMenuItem.setText("关于");
        aboutMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
        aboutMenuItem.addActionListener(this::aboutMenuItemActionPerformed);
        helpMenu.add(aboutMenuItem);

//        JMenuItem updateMenuItem = new JMenuItem();
//        updateMenuItem.setText("软件更新");
//        updateMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
//        updateMenuItem.addActionListener(this::updateMenuItemActionPerformed);
//        helpMenu.add(updateMenuItem);
//
//        JMenuItem hardUpdateMenuItem = new JMenuItem();
//        hardUpdateMenuItem.setText("硬件升级");
//        hardUpdateMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
//        hardUpdateMenuItem.addActionListener(this::hardUpdateMenuItemActionPerformed);
//        helpMenu.add(hardUpdateMenuItem);


        menuBar1.add(helpMenu);
        this.setJMenuBar(menuBar1);


        /**
         * 串口与设备
         */
        JLabel portAndDeviceLabel = new JLabel();
        portAndDeviceLabel.setText("串口与设备");
        portAndDeviceLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        contentPane.add(portAndDeviceLabel);
        portAndDeviceLabel.setBounds(new Rectangle(new Point(20, 10), portAndDeviceLabel.getPreferredSize()));

        JSeparator separator6 = new JSeparator();//串口与设备左边
        separator6.setForeground(Color.lightGray);
        contentPane.add(separator6);
        separator6.setBounds(5, 20, 10, 5);

        JSeparator separator3 = new JSeparator();//串口与设备右边
        separator3.setForeground(Color.lightGray);
        contentPane.add(separator3);
        separator3.setBounds(85, 20, 500, 5);

        JSeparator separator55 = new JSeparator();//左竖线
        separator55.setOrientation(SwingConstants.VERTICAL);
        separator55.setForeground(Color.lightGray);
        contentPane.add(separator55);
        separator55.setBounds(5, 20, 5, 70);

        JSeparator separator5 = new JSeparator();//右竖线
        separator5.setOrientation(SwingConstants.VERTICAL);
        separator5.setForeground(Color.lightGray);
        contentPane.add(separator5);
        separator5.setBounds(585, 20, 5, 70);

        JSeparator separator2 = new JSeparator();//下横线
        separator2.setForeground(Color.lightGray);
        contentPane.add(separator2);
        separator2.setBounds(5, 90, 580, 5);

        JLabel portLabel = new JLabel();
        portLabel.setText("端口");
        portLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        contentPane.add(portLabel);
        portLabel.setBounds(new Rectangle(new Point(20, 45), portLabel.getPreferredSize()));

        portChooser = new JComboBox<>();
        ArrayList<String> ports = SerialPortUtils.findPort();
        String[] portList = ports.toArray(new String[0]);
        PORT_NUM = portList.length;
        portChooser.setFont(new Font("Dialog", Font.PLAIN, 12));
        portChooser.setModel(new DefaultComboBoxModel<>(portList));
        try {
            portChooser.setSelectedIndex(user.getPortOrder());
        } catch (Exception e) {
            ServiceImpl.getInstance().updatePortOrder(user.getUserId(), user.getUserName(), 0);
            ServiceImpl.getInstance().updateSubPortOrder(0);
            user.setPortOrder(0);
            portChooser.setSelectedIndex(0);
            e.printStackTrace();
        }

        String selectPort = (String) this.portChooser.getSelectedItem();
        int portRate = Utils.getPortrate(this.user.getPortrateOrder());
        try {
            SerialPortUtils.openPort(selectPort, portRate, this);
        } catch (NotASerialPort | NoSuchPort | UnsupportedCommOperationException | PortInUse notASerialPort) {
            notASerialPort.printStackTrace();
        }
        try {
            Schedule.run();
        } catch (SchedulerException e2) {
            e2.printStackTrace();
        }
        portChooser.addItemListener(this::portChooserItemStateChanged);
        contentPane.add(portChooser);
        portChooser.setBounds(65, 40, 83, 28);

        JLabel deviceStatusLabel = new JLabel();
        deviceStatusLabel.setText("设备状态 :");
        deviceStatusLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        contentPane.add(deviceStatusLabel);
        deviceStatusLabel.setBounds(new Rectangle(new Point(180, 45), deviceStatusLabel.getPreferredSize()));

        deviceStatusText = new JLabel();
        deviceStatusText.setText("连接异常");
        deviceStatusText.setForeground(Color.red);
        deviceStatusText.setFont(new Font("Dialog", Font.PLAIN, 12));
        contentPane.add(deviceStatusText);
        deviceStatusText.setBounds(250, 42, deviceStatusText.getPreferredSize().width, 22);

        JLabel softwareLabel = new JLabel();
        softwareLabel.setText("软件版本 :");
        softwareLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        contentPane.add(softwareLabel);
        softwareLabel.setBounds(new Rectangle(new Point(330, 45), softwareLabel.getPreferredSize()));

        softwareVersionText = new JLabel();
        softwareVersionText.setText("-");
        softwareVersionText.setFont(new Font("Dialog", Font.PLAIN, 12));
        contentPane.add(softwareVersionText);
        softwareVersionText.setBounds(405, 43, 50, 20);


        JLabel hardwareLabel = new JLabel();
        hardwareLabel.setText("硬件版本 :");
        hardwareLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        contentPane.add(hardwareLabel);
        hardwareLabel.setBounds(new Rectangle(new Point(450, 45), hardwareLabel.getPreferredSize()));

        hardwareVersionText = new JLabel();
        hardwareVersionText.setText("-");
        hardwareVersionText.setFont(new Font("Dialog", Font.PLAIN, 12));
        contentPane.add(hardwareVersionText);
        hardwareVersionText.setBounds(525, 43, 50, 20);


        JLabel photoView = new JLabel();
        ImageIcon ico = new ImageIcon("resources/dk_logo_2.png");
        ico.setImage(ico.getImage().getScaledInstance(126, 35, 1));
        photoView.setBounds(625, 25, 200, 60);
        photoView.setIcon(ico);
        contentPane.add(photoView);

        //长分割线
        JSeparator separator1 = new JSeparator();
        separator1.setForeground(Color.GRAY);
        contentPane.add(separator1);
        separator1.setBounds(0, 105, 800, 2);


        /**
         * 账户框
         */
        JLabel label1 = new JLabel();
        label1.setText("账户");
        label1.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        contentPane.add(label1);
        label1.setBounds(new Rectangle(new Point(20, 115), label1.getPreferredSize()));

        JSeparator separator7 = new JSeparator();//账户左边
        separator7.setForeground(Color.lightGray);

        contentPane.add(separator7);
        separator7.setBounds(5, 125, 10, 5);

        JSeparator separator8 = new JSeparator();//账户右边
        separator8.setForeground(Color.lightGray);
        contentPane.add(separator8);
        separator8.setBounds(50, 125, 105, 5);

        JSeparator separator9 = new JSeparator();//底部横线
        separator9.setForeground(Color.lightGray);
        contentPane.add(separator9);
        separator9.setBounds(5, 470, 150, 5);

        JSeparator separatorr = new JSeparator();//左竖线
        separatorr.setOrientation(SwingConstants.VERTICAL);
        separatorr.setForeground(Color.lightGray);
        contentPane.add(separatorr);
        separatorr.setBounds(5, 125, 5, 345);

        JSeparator separator10 = new JSeparator();//右竖线
        separator10.setOrientation(SwingConstants.VERTICAL);
        separator10.setForeground(Color.lightGray);
        contentPane.add(separator10);
        separator10.setBounds(155, 125, 5, 345);


        logoutButton = new JButton();
        logoutButton.setText("注销");
        logoutButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        logoutButton.addActionListener(this::logoutButtonActionPerformed);
        contentPane.add(logoutButton);
        logoutButton.setBounds(30, 160, 88, logoutButton.getPreferredSize().height);
        logoutButton.setBackground(new Color(180, 205, 205));
        logoutButton.setBorder(BorderFactory.createRaisedBevelBorder());

        registerButton = new JButton();
        registerButton.setText("注册");
        registerButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        registerButton.addActionListener(this::registerButtonActionPerformed);
        contentPane.add(registerButton);
        registerButton.setBounds(30, 220, 88, registerButton.getPreferredSize().height);
        registerButton.setBackground(new Color(180, 205, 205));
        registerButton.setBorder(BorderFactory.createRaisedBevelBorder());

        queryButton = new JButton();
        queryButton.setText("查询");
        queryButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        queryButton.addActionListener(this::queryButtonActionPerformed);
        contentPane.add(queryButton);
        queryButton.setBounds(30, 280, 88, queryButton.getPreferredSize().height);
        queryButton.setBackground(new Color(180, 205, 205));
        queryButton.setBorder(BorderFactory.createRaisedBevelBorder());

        rechargeButton = new JButton();
        rechargeButton.setText("充值");
        rechargeButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        rechargeButton.addActionListener(this::rechargeButtonActionPerformed);
        contentPane.add(rechargeButton);
        rechargeButton.setBounds(30, 340, 88, rechargeButton.getPreferredSize().height);
        rechargeButton.setBackground(new Color(180, 205, 205));
        rechargeButton.setBorder(BorderFactory.createRaisedBevelBorder());

        unclaimedButton = new JButton();
        unclaimedButton.setText("圈存领取");
        unclaimedButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        unclaimedButton.addActionListener(this::unclaimedButtonActionPerformed);
        unclaimedButton.setEnabled(false);
        contentPane.add(unclaimedButton);
        unclaimedButton.setBounds(30, 400, 88, unclaimedButton.getPreferredSize().height);
        unclaimedButton.setBackground(new Color(180, 205, 205));
        unclaimedButton.setBorder(BorderFactory.createRaisedBevelBorder());


        /**
         * 记录框
         */
        JLabel recordLabel = new JLabel();
        recordLabel.setText("记 录");
        recordLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        contentPane.add(recordLabel);
        recordLabel.setBounds(250, 110, 50, 30);

        JSeparator jSeparator2 = new JSeparator();//记录左边
        jSeparator2.setForeground(Color.lightGray);
        contentPane.add(jSeparator2);
        jSeparator2.setBounds(200, 125, 40, 5);

        JSeparator jSeparator1 = new JSeparator();//记录右边
        jSeparator1.setForeground(Color.lightGray);
        contentPane.add(jSeparator1);
        jSeparator1.setBounds(290, 125, 470, 5);


        JSeparator jSeparator3 = new JSeparator();//底部横线
        jSeparator3.setForeground(Color.lightGray);
        contentPane.add(jSeparator3);
        jSeparator3.setBounds(200, 188, 560, 5);

        JSeparator jSeparator4 = new JSeparator();//左竖线
        jSeparator4.setOrientation(SwingConstants.VERTICAL);
        jSeparator4.setForeground(Color.lightGray);
        contentPane.add(jSeparator4);
        jSeparator4.setBounds(200, 125, 5, 63);

        JSeparator jSeparator5 = new JSeparator();//右竖线
        jSeparator5.setOrientation(SwingConstants.VERTICAL);
        jSeparator5.setForeground(Color.lightGray);
        contentPane.add(jSeparator5);
        jSeparator5.setBounds(760, 125, 5, 63);


        rechargeHisButton = new JButton();
        rechargeHisButton.setText("充值记录");
        rechargeHisButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        rechargeHisButton.addActionListener(this::rechargeHisButtonActionPerformed);
        contentPane.add(rechargeHisButton);
        rechargeHisButton.setBounds(240, 145, 88, rechargeHisButton.getPreferredSize().height);
        rechargeHisButton.setBorder(BorderFactory.createRaisedBevelBorder());

        prechargeHisButton = new JButton();
        prechargeHisButton.setText("圈领记录");
        prechargeHisButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        prechargeHisButton.addActionListener(this::prechargeHisButtonActionPerformed);
        prechargeHisButton.setEnabled(false);
        contentPane.add(prechargeHisButton);
        prechargeHisButton.setBounds(370, 145, 88, prechargeHisButton.getPreferredSize().height);
        prechargeHisButton.setBorder(BorderFactory.createRaisedBevelBorder());

        registerHisButton = new JButton();
        registerHisButton.setText("注册记录");
        registerHisButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        registerHisButton.addActionListener(this::registerHisButtonActionPerformed);
        registerHisButton.setEnabled(false);
        contentPane.add(registerHisButton);
        registerHisButton.setBounds(500, 145, 88, registerHisButton.getPreferredSize().height);
        registerHisButton.setBorder(BorderFactory.createRaisedBevelBorder());

        logoutHisButton = new JButton();
        logoutHisButton.setText("注销记录");
        logoutHisButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        logoutHisButton.addActionListener(this::logoutHisButtonActionPerformed);
        logoutHisButton.setEnabled(false);
        contentPane.add(logoutHisButton);
        logoutHisButton.setBounds(630, 145, 88, logoutHisButton.getPreferredSize().height);
        logoutHisButton.setBorder(BorderFactory.createRaisedBevelBorder());


        /**
         * 查询信息框
         */

        CardInfoPanel = new JPanel();
        CardInfoPanel.setBackground(Color.white);
        CardInfoPanel.setLayout(null);
        CardInfoPanel.setVisible(true);

        JSeparator separator11 = new JSeparator();
        JSeparator separator12 = new JSeparator();
        JSeparator separator13 = new JSeparator();
        JSeparator separator14 = new JSeparator();
        JSeparator separator15 = new JSeparator();
        JSeparator separator16 = new JSeparator();
        JSeparator separator17 = new JSeparator();
        JSeparator separator18 = new JSeparator();
        JSeparator separator19 = new JSeparator();
        JSeparator separator20 = new JSeparator();

        //外圈左
        separator11.setOrientation(SwingConstants.VERTICAL);
        separator11.setForeground(Color.GRAY);
        separator11.setBounds(0, 0, 10, 280);
        CardInfoPanel.add(separator11);
        //外圈右
        separator20.setOrientation(SwingConstants.VERTICAL);
        separator20.setForeground(Color.GRAY);
        separator20.setBounds(559, 0, 15, 280);
        CardInfoPanel.add(separator20);
        //外圈上
        separator14.setBounds(0, 0, 560, 15);
        separator14.setForeground(Color.GRAY);
        CardInfoPanel.add(separator14);
        //外圈下
        separator18.setBounds(0, 259, 560, 15);
        separator18.setForeground(Color.GRAY);
        CardInfoPanel.add(separator18);


        //内圈竖线左
        separator19.setOrientation(SwingConstants.VERTICAL);
        separator19.setForeground(Color.lightGray);
        separator19.setBounds(10, 9, 15, 242);
        CardInfoPanel.add(separator19);
        //内圈竖线中
        separator15.setOrientation(SwingConstants.VERTICAL);
        separator15.setForeground(Color.lightGray);
        separator15.setBounds(230, 9, 10, 242);
        CardInfoPanel.add(separator15);
        //内圈竖线右
        separator12.setOrientation(SwingConstants.VERTICAL);
        separator12.setForeground(Color.lightGray);
        separator12.setBounds(549, 9, 10, 242);
        CardInfoPanel.add(separator12);


        //内圈横线上
        separator17.setBounds(10, 9, 540, 15);
        separator17.setForeground(Color.lightGray);
        CardInfoPanel.add(separator17);
        //内圈横线中
        separator16.setBounds(10, 130, 220, 15);
        separator16.setForeground(Color.lightGray);
        CardInfoPanel.add(separator16);
        //内圈横线下
        separator13.setBounds(10, 250, 540, 15);
        separator13.setForeground(Color.lightGray);
        CardInfoPanel.add(separator13);

        JLabel balanceLabel = new JLabel();
        balanceLabel.setText("   余额     ：");
        balanceLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
        CardInfoPanel.add(balanceLabel);
        balanceLabel.setBounds(new Rectangle(new Point(35, 23), balanceLabel.getPreferredSize()));

        balanceText = new JLabel();
        balanceText.setText("-");
        balanceText.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        CardInfoPanel.add(balanceText);
        balanceText.setBounds(135, 25, 100, balanceText.getPreferredSize().height);

        JLabel validTimeLabel = new JLabel();
        validTimeLabel.setText("剩余天数 ： ");
        validTimeLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
        CardInfoPanel.add(validTimeLabel);
        validTimeLabel.setBounds(new Rectangle(new Point(35, 60), validTimeLabel.getPreferredSize()));

        validTimeText = new JLabel();
        validTimeText.setText("-");
        validTimeText.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        CardInfoPanel.add(validTimeText);
        validTimeText.setBounds(135, 61, 100, validTimeText.getPreferredSize().height);

        JLabel remainTimeLabel = new JLabel();
        remainTimeLabel.setText("到期日期 ： ");
        remainTimeLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
        CardInfoPanel.add(remainTimeLabel);
        remainTimeLabel.setBounds(new Rectangle(new Point(35, 97), remainTimeLabel.getPreferredSize()));

        remainTimeText = new JLabel();
        remainTimeText.setText("     -");
        remainTimeText.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        CardInfoPanel.add(remainTimeText);
        remainTimeText.setBounds(115, 97, 120, remainTimeText.getPreferredSize().height);


        JLabel cardNumLabel = new JLabel();
        cardNumLabel.setText("卡号    ：");
        cardNumLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        CardInfoPanel.add(cardNumLabel);
        cardNumLabel.setBounds(new Rectangle(new Point(45, 150), cardNumLabel.getPreferredSize()));

        cardNumText = new JLabel();
        cardNumText.setText("-");
        cardNumText.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        CardInfoPanel.add(cardNumText);
        cardNumText.setBounds(new Rectangle(new Point(105, 155), cardNumText.getPreferredSize()));
        cardNumText.setBounds(105, 150, 100, cardNumText.getPreferredSize().height);

        JLabel cardTypeLabel = new JLabel();
        cardTypeLabel.setText("卡类型 ： ");
        cardTypeLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        CardInfoPanel.add(cardTypeLabel);
        cardTypeLabel.setBounds(new Rectangle(new Point(45, 180), cardTypeLabel.getPreferredSize()));

        cardTypeText = new JLabel();
        cardTypeText.setText("-");
        cardTypeText.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        CardInfoPanel.add(cardTypeText);
        cardTypeText.setBounds(105, 180, 100, cardTypeText.getPreferredSize().height);

        JLabel phoneLabel = new JLabel();
        phoneLabel.setText("手机号 ： ");
        phoneLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        CardInfoPanel.add(phoneLabel);
        phoneLabel.setBounds(new Rectangle(new Point(45, 210), phoneLabel.getPreferredSize()));

        phoneText = new JLabel();
        phoneText.setText("-");
        phoneText.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        CardInfoPanel.add(phoneText);
        phoneText.setBounds(105, 210, 100, phoneText.getPreferredSize().height);


        JLabel lastTimeLabel = new JLabel();
        lastTimeLabel.setText("最近刷卡时间    ： ");
        lastTimeLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        CardInfoPanel.add(lastTimeLabel);
        lastTimeLabel.setBounds(new Rectangle(new Point(275, 45), lastTimeLabel.getPreferredSize()));

        JLabel startTimeLabel = new JLabel();
        startTimeLabel.setText("    启用时间      ： ");
        startTimeLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        CardInfoPanel.add(startTimeLabel);
        startTimeLabel.setBounds(new Rectangle(new Point(275, 85), startTimeLabel.getPreferredSize()));

        JLabel chargeTimeLabel = new JLabel();
        chargeTimeLabel.setText("    充电时间      ： ");
        chargeTimeLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        CardInfoPanel.add(chargeTimeLabel);
        chargeTimeLabel.setBounds(new Rectangle(new Point(275, 125), chargeTimeLabel.getPreferredSize()));

        JLabel payRateLabel = new JLabel();
        payRateLabel.setText("    扣款费率      ：");
        payRateLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        CardInfoPanel.add(payRateLabel);
        payRateLabel.setBounds(new Rectangle(new Point(275, 165), payRateLabel.getPreferredSize()));

        JLabel powerRateLabel = new JLabel();
        powerRateLabel.setText("    最大功率      ：");
        powerRateLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        CardInfoPanel.add(powerRateLabel);
        powerRateLabel.setBounds(new Rectangle(new Point(275, 205), powerRateLabel.getPreferredSize()));


        lastTimeText = new JLabel();
        startTimeText = new JLabel();
        chargeTimeText = new JLabel();
        payRateText = new JLabel();
        powerRateText = new JLabel();
        welcomeLabel = new JLabel();
        lastTimeText.setText("-");
        lastTimeText.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        CardInfoPanel.add(lastTimeText);
        lastTimeText.setBounds(380, 45, 200, lastTimeText.getPreferredSize().height);
        startTimeText.setText("-");
        startTimeText.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        CardInfoPanel.add(startTimeText);
        startTimeText.setBounds(380, 85, 100, startTimeText.getPreferredSize().height);
        chargeTimeText.setText("-");
        chargeTimeText.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        CardInfoPanel.add(chargeTimeText);
        chargeTimeText.setBounds(380, 125, 100, chargeTimeText.getPreferredSize().height);
        payRateText.setText("-");
        payRateText.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        CardInfoPanel.add(payRateText);
        payRateText.setBounds(380, 165, 100, payRateText.getPreferredSize().height);
        powerRateText.setText("-");
        powerRateText.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        CardInfoPanel.add(powerRateText);
        powerRateText.setBounds(380, 205, 100, powerRateText.getPreferredSize().height);


        /**
         * 下方栏
         */
        JSeparator separatorButtom = new JSeparator();
        separatorButtom.setOrientation(SwingConstants.HORIZONTAL);
        separatorButtom.setForeground(Color.lightGray);
        contentPane.add(separatorButtom);
        separatorButtom.setBounds(0, 510, 800, 2);

        tipLabel = new JLabel();
        tipLabel.setText("欢迎使用得康充值系统");
        tipLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        tipLabel.setBounds(10, 520, 200, tipLabel.getPreferredSize().height);
        contentPane.add(tipLabel);

//        serverStatusText = new JLabel();
//        serverStatusText.setText("离线");
//        serverStatusText.setForeground(Color.red);
//        serverStatusText.setFont(new Font("Dialog", Font.PLAIN, 12));
//        contentPane.add(serverStatusText);
//        serverStatusText.setBounds(new Rectangle(new Point(350, 520), serverStatusText.getPreferredSize()));

//        comunityLabel = new JLabel();
//        comunityLabel.setText("当前小区 : " + user.getCommunity());
//        comunityLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
//        comunityLabel.setBounds(630, 520, 200, comunityLabel.getPreferredSize().height);
//        contentPane.add(comunityLabel);

        Dimension preferredSize = new Dimension();
        for (int i = 0; i < CardInfoPanel.getComponentCount(); i++) {
            Rectangle bounds = CardInfoPanel.getComponent(i).getBounds();
            preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
            preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
        }
        Insets insets = CardInfoPanel.getInsets();
        Dimension dimension = preferredSize;
        dimension.width += insets.right;
        Dimension dimension2 = preferredSize;
        dimension2.height += insets.bottom;

        CardInfoPanel.setMinimumSize(preferredSize);
        CardInfoPanel.setPreferredSize(preferredSize);
        contentPane.add(CardInfoPanel);
        CardInfoPanel.setBounds(200, 210, 560, 260);

        preferredSize = new Dimension();
        for (int i = 0; i < contentPane.getComponentCount(); i++) {
            Rectangle bounds = contentPane.getComponent(i).getBounds();
            preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
            preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
        }
        insets = contentPane.getInsets();
        Dimension dimension3 = preferredSize;
        dimension3.width += insets.right;
        Dimension dimension4 = preferredSize;
        dimension4.height += insets.bottom;
        contentPane.setMinimumSize(preferredSize);
        contentPane.setPreferredSize(preferredSize);
        this.pack();
        this.setLocationRelativeTo(this.getOwner());
    }

    public static void main(String[] args) {
        User user = new User(1, "1", "1", "1", 1, 1, 1, 1, 1, 1, 1, "1");
        new RechargeFrame(user);
    }
}
