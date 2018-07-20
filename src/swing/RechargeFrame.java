package swing;

import bean.*;
import bean.manager.*;
import commands.*;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import serial.*;
//import gnu.io.*;
import service.impl.*;
import utils.*;
import exception.*;

import java.awt.event.*;

import job.*;
import org.quartz.*;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.util.List;

/**
 * @author kingfans
 */
public class RechargeFrame extends JFrame {
    private User user;
    private JComboBox<String> portChooser;
    public JLabel deviceStatusText;
    public JPanel CardInfoPanel;
    public JLabel cardNumText;
    public JLabel cardTypeText;
    public JLabel phoneText;
    public JLabel balanceText;
    public JLabel validTimeText;
    public JLabel lastTimeText;
    public JLabel startTimeText;
    public JLabel chargeTimeText;
    public JLabel payRateText;
    public JLabel powerRateText;
    public JLabel welcomeLabel;
    public JLabel tipLabel;

    public RechargeFrame(User user) {
        this.user = user;
        initComponents();
        System.out.println(user.toString());
        this.setSize(1000, 680);
        Image icon = Toolkit.getDefaultToolkit().getImage("resources/dk_logo.png");
        this.setIconImage(icon);
        this.setTitle("得康充值系统 v1.5");
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        FrameManager.addFrame("recharge", this);
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

    private void addAccountMenuItemActionPerformed(ActionEvent e) {
        new SystemPasswdFrame(user, this, new AddAccountFrame(user));
        this.setEnabled(false);
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


    private void logoutButtonActionPerformed(ActionEvent e) {
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }
        int isLogout = JOptionPane.showConfirmDialog(null, "确认注销？", null, JOptionPane.OK_CANCEL_OPTION);
        if (isLogout == 0) {
            tipLabel.setText("正在准备注销，请将卡片放至充值机");
            SerialPort serialPort = PortManager.getSerialPort();
            if (serialPort != null) {
                String systemPassword = user.getSystemPassword();
                try {
                    SerialPortUtils.sendToPort(serialPort, CommandUtils.logoffCommand(systemPassword, 0));
                } catch (SendDataToSerialPortFailure | SerialPortOutputStreamCloseFailure sendDataToSerialPortFailure) {
                    sendDataToSerialPortFailure.printStackTrace();
                }
            }
        }
    }

    private void queryButtonActionPerformed(ActionEvent e) {
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

    private void rechargeButtonActionPerformed(ActionEvent e) {
        LoginFrame.IS_RECHARGE = 1;
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

    private void registerButtonActionPerformed(ActionEvent e) {
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }
        new RegisterFrame(user, this);
        this.setEnabled(false);
    }

    private void rechargeHisButtonActionPerformed(ActionEvent e) {
        int n = ServiceImpl.getInstance().getRechargeCount();
        List<Map<String, Object>> his = ServiceImpl.getInstance().getRechargeHisAll();
        this.setEnabled(false);
        new HisTableFrame(n, his, this);
    }

    private void aboutMenuItemActionPerformed(ActionEvent e) {
        new AboutFrame();
    }

    private void portChooserItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            deviceStatusText.setText("连接异常");
            deviceStatusText.setForeground(Color.red);
            String selectPort = (String) portChooser.getSelectedItem();
            user.setPortOrder(portChooser.getSelectedIndex());
            ServiceImpl.getInstance().updatePortOrder(user.getUserId(), user.getUserName(), portChooser.getSelectedIndex());
            ServiceImpl.getInstance().updateSubPortOrder(portChooser.getSelectedIndex());
            PortManager.removePort();
            int portRate = Utils.getPortrate(user.getPortrateOrder());
            try {
                SerialPortUtils.openPort(selectPort, portRate, this);
            } catch (NotASerialPort | NoSuchPort | UnsupportedCommOperationException | PortInUse notASerialPort) {
                notASerialPort.printStackTrace();
            }
        }
    }

    private void rechargeWindowClosing(WindowEvent e) {
        System.exit(0);
    }

    private void initComponents() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                RechargeFrame.this.rechargeWindowClosing(e);
            }
        });
        this.setTitle("充值系统");
        Container contentPane = this.getContentPane();
        contentPane.setLayout(null);

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
        systemPasswdMenuItem.setText("刷卡机密码设置");
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
        menuBar1.add(settingMenu);
        if (user.getIsAdmin() == 0) {
            portRateMenuItem.setEnabled(false);
        } else {
            portRateMenuItem.setEnabled(true);
        }

        JMenu helpMenu = new JMenu();
        helpMenu.setText("帮助");
        helpMenu.setFont(new Font("Dialog", Font.PLAIN, 12));

        JMenuItem aboutMenuItem = new JMenuItem();
        aboutMenuItem.setText("关于");
        aboutMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
        aboutMenuItem.addActionListener(this::aboutMenuItemActionPerformed);
        helpMenu.add(aboutMenuItem);
        menuBar1.add(helpMenu);
        this.setJMenuBar(menuBar1);

        JLabel portLabel = new JLabel();
        portLabel.setText("COM");
        portLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        contentPane.add(portLabel);
        portLabel.setBounds(new Rectangle(new Point(20, 45), portLabel.getPreferredSize()));

        portChooser = new JComboBox<>();
        ArrayList<String> ports = SerialPortUtils.findPort();
        String[] portList = ports.toArray(new String[0]);
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
        System.out.println("port rate : " + portRate);
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
        portChooser.setBounds(85, 40, 83, 28);

        JLabel deviceStatusLabel = new JLabel();
        deviceStatusLabel.setText("设备状态");
        deviceStatusLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        contentPane.add(deviceStatusLabel);
        deviceStatusLabel.setBounds(new Rectangle(new Point(20, 90), deviceStatusLabel.getPreferredSize()));

        JLabel serverStatusLabel = new JLabel();
        serverStatusLabel.setText("服务器状态");
        serverStatusLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        contentPane.add(serverStatusLabel);
        serverStatusLabel.setBounds(new Rectangle(new Point(20, 135), serverStatusLabel.getPreferredSize()));

        JButton logoutButton = new JButton();
        logoutButton.setText("注销");
        logoutButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        logoutButton.addActionListener(this::logoutButtonActionPerformed);
        contentPane.add(logoutButton);
        logoutButton.setBounds(30, 260, 88, logoutButton.getPreferredSize().height);
        logoutButton.setBackground(new Color(180, 205, 205));
        logoutButton.setBorder(BorderFactory.createRaisedBevelBorder());

        JButton registerButton = new JButton();
        registerButton.setText("注册");
        registerButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        registerButton.addActionListener(this::registerButtonActionPerformed);
        contentPane.add(registerButton);
        registerButton.setBounds(30, 300, 88, registerButton.getPreferredSize().height);
        registerButton.setBackground(new Color(180, 205, 205));
        registerButton.setBorder(BorderFactory.createRaisedBevelBorder());

        JButton queryButton = new JButton();
        queryButton.setText("查询");
        queryButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        queryButton.addActionListener(this::queryButtonActionPerformed);
        contentPane.add(queryButton);
        queryButton.setBounds(30, 340, 88, queryButton.getPreferredSize().height);
        queryButton.setBackground(new Color(180, 205, 205));
        queryButton.setBorder(BorderFactory.createRaisedBevelBorder());

        JButton rechargeButton = new JButton();
        rechargeButton.setText("充值");
        rechargeButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        rechargeButton.addActionListener(this::rechargeButtonActionPerformed);
        contentPane.add(rechargeButton);
        rechargeButton.setBounds(30, 380, 88, rechargeButton.getPreferredSize().height);
        rechargeButton.setBackground(new Color(180, 205, 205));
        rechargeButton.setBorder(BorderFactory.createRaisedBevelBorder());

        JButton rechargeHisButton = new JButton();
        rechargeHisButton.setText("充值历史");
        rechargeHisButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        rechargeHisButton.addActionListener(this::rechargeHisButtonActionPerformed);
        contentPane.add(rechargeHisButton);
        rechargeHisButton.setBounds(30, 420, 88, rechargeHisButton.getPreferredSize().height);
        rechargeHisButton.setBackground(new Color(180, 205, 205));
        rechargeHisButton.setBorder(BorderFactory.createRaisedBevelBorder());

        deviceStatusText = new JLabel();
        deviceStatusText.setText("连接异常");
        deviceStatusText.setForeground(Color.red);
        deviceStatusText.setFont(new Font("Dialog", Font.PLAIN, 12));
        contentPane.add(deviceStatusText);
        deviceStatusText.setBounds(99, 88, deviceStatusText.getPreferredSize().width, 22);

        JLabel serverStatusText = new JLabel();
        serverStatusText.setText("在线");
        serverStatusText.setForeground(Color.blue);
        serverStatusText.setFont(new Font("Dialog", Font.PLAIN, 12));
        contentPane.add(serverStatusText);
        serverStatusText.setBounds(new Rectangle(new Point(105, 135), serverStatusText.getPreferredSize()));

        JSeparator separator1 = new JSeparator();
        JSeparator separator2 = new JSeparator();
        JSeparator separator4 = new JSeparator();
        JSeparator separator5 = new JSeparator();
        contentPane.add(separator1);
        separator1.setBounds(5, 155, separator1.getPreferredSize().width, 2);
        separator2.setForeground(Color.lightGray);
        contentPane.add(separator2);
        separator2.setBounds(0, 175, 190, 5);
        contentPane.add(separator4);
        separator4.setBounds(230, 85, separator4.getPreferredSize().width, 2);
        separator5.setOrientation(SwingConstants.VERTICAL);
        separator5.setForeground(Color.lightGray);
        contentPane.add(separator5);
        separator5.setBounds(190, 20, 5, 155);


        JLabel portAndDeviceLabel = new JLabel();
        portAndDeviceLabel.setText("串口与设备");
        portAndDeviceLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        contentPane.add(portAndDeviceLabel);
        portAndDeviceLabel.setBounds(new Rectangle(new Point(20, 10), portAndDeviceLabel.getPreferredSize()));

        JSeparator separator3 = new JSeparator();
        JSeparator separator6 = new JSeparator();
        separator3.setForeground(Color.lightGray);
        contentPane.add(separator3);
        separator3.setBounds(85, 20, 105, 5);
        separator6.setForeground(Color.lightGray);
        contentPane.add(separator6);
        separator6.setBounds(0, 20, 15, 5);

        JLabel label1 = new JLabel();
        label1.setText("账户");
        label1.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        contentPane.add(label1);
        label1.setBounds(new Rectangle(new Point(20, 215), label1.getPreferredSize()));


        CardInfoPanel = new JPanel();
        CardInfoPanel.setLayout(null);
        CardInfoPanel.setVisible(true);
        JSeparator separator7 = new JSeparator();
        JSeparator separator8 = new JSeparator();
        JSeparator separator9 = new JSeparator();
        JSeparator separator10 = new JSeparator();
        separator7.setForeground(Color.lightGray);
        contentPane.add(separator7);
        separator7.setBounds(0, 225, 15, 5);
        separator8.setForeground(Color.lightGray);
        contentPane.add(separator8);
        separator8.setBounds(50, 225, 105, 5);
        separator9.setForeground(Color.lightGray);
        contentPane.add(separator9);
        separator9.setBounds(0, 475, 155, 5);
        separator10.setOrientation(SwingConstants.VERTICAL);
        separator10.setForeground(Color.lightGray);
        contentPane.add(separator10);
        separator10.setBounds(155, 225, 5, 250);


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

        separator11.setOrientation(SwingConstants.VERTICAL);
        separator11.setForeground(Color.lightGray);
        separator11.setBounds(15, 15, 10, 255);
        CardInfoPanel.add(separator11);

        separator12.setOrientation(SwingConstants.VERTICAL);
        separator12.setForeground(Color.lightGray);
        separator12.setBounds(545, 15, 10, 255);
        CardInfoPanel.add(separator12);

        separator13.setBounds(15, 15, 530, 15);
        separator13.setForeground(Color.lightGray);
        CardInfoPanel.add(separator13);

        separator14.setBounds(15, 270, 530, 15);
        separator14.setForeground(Color.lightGray);
        CardInfoPanel.add(separator14);

        separator15.setOrientation(SwingConstants.VERTICAL);
        separator15.setForeground(Color.lightGray);
        separator15.setBounds(230, 15, 10, 255);
        CardInfoPanel.add(separator15);

        separator16.setBounds(15, 140, 215, 15);
        separator16.setForeground(Color.lightGray);
        CardInfoPanel.add(separator16);

        separator17.setBounds(10, 9, 540, 15);
        separator17.setForeground(Color.lightGray);
        CardInfoPanel.add(separator17);

        separator18.setBounds(10, 275, 540, 15);
        separator18.setForeground(Color.lightGray);
        CardInfoPanel.add(separator18);

        separator19.setOrientation(SwingConstants.VERTICAL);
        separator19.setForeground(Color.lightGray);
        separator19.setBounds(10, 9, 15, 265);
        CardInfoPanel.add(separator19);

        separator20.setOrientation(SwingConstants.VERTICAL);
        separator20.setForeground(Color.lightGray);
        separator20.setBounds(550, 9, 15, 265);
        CardInfoPanel.add(separator20);


        JLabel cardNumLabel = new JLabel();
        cardNumLabel.setText("卡号    ：");
        cardNumLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        CardInfoPanel.add(cardNumLabel);
        cardNumLabel.setBounds(new Rectangle(new Point(45, 165), cardNumLabel.getPreferredSize()));

        cardNumText = new JLabel();
        cardNumText.setText("-");
        cardNumText.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        CardInfoPanel.add(cardNumText);
        cardNumText.setBounds(new Rectangle(new Point(105, 165), cardNumText.getPreferredSize()));
        cardNumText.setBounds(105, 165, 100, cardNumText.getPreferredSize().height);

        JLabel cardTypeLabel = new JLabel();
        cardTypeLabel.setText("卡类型 ： ");
        cardTypeLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        CardInfoPanel.add(cardTypeLabel);
        cardTypeLabel.setBounds(new Rectangle(new Point(45, 195), cardTypeLabel.getPreferredSize()));

        cardTypeText = new JLabel();
        cardTypeText.setText("-");
        cardTypeText.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        CardInfoPanel.add(cardTypeText);
        cardTypeText.setBounds(105, 195, 100, cardTypeText.getPreferredSize().height);

        JLabel phoneLabel = new JLabel();
        phoneLabel.setText("手机号 ： ");
        phoneLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        CardInfoPanel.add(phoneLabel);
        phoneLabel.setBounds(new Rectangle(new Point(45, 225), phoneLabel.getPreferredSize()));

        phoneText = new JLabel();
        phoneText.setText("-");
        phoneText.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        CardInfoPanel.add(phoneText);
        phoneText.setBounds(105, 225, 100, phoneText.getPreferredSize().height);

        JLabel balanceLabel = new JLabel();
        balanceLabel.setText("   余额     ：");
        balanceLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 18));
        CardInfoPanel.add(balanceLabel);
        balanceLabel.setBounds(new Rectangle(new Point(35, 45), balanceLabel.getPreferredSize()));

        JLabel validTimeLabel = new JLabel();
        validTimeLabel.setText("有效天数 ： ");
        validTimeLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 18));
        CardInfoPanel.add(validTimeLabel);
        validTimeLabel.setBounds(new Rectangle(new Point(35, 95), validTimeLabel.getPreferredSize()));

        balanceText = new JLabel();
        balanceText.setText("-");
        balanceText.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        CardInfoPanel.add(balanceText);
        balanceText.setBounds(145, 50, 100, balanceText.getPreferredSize().height);

        validTimeText = new JLabel();
        validTimeText.setText("-");
        validTimeText.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        CardInfoPanel.add(validTimeText);
        validTimeText.setBounds(145, 100, 100, validTimeText.getPreferredSize().height);

        JLabel lastTimeLabel = new JLabel();
        lastTimeLabel.setText("最近刷卡时间    ： ");
        lastTimeLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        CardInfoPanel.add(lastTimeLabel);
        lastTimeLabel.setBounds(new Rectangle(new Point(275, 55), lastTimeLabel.getPreferredSize()));

        JLabel startTimeLabel = new JLabel();
        startTimeLabel.setText("    启用时间      ： ");
        startTimeLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        CardInfoPanel.add(startTimeLabel);
        startTimeLabel.setBounds(new Rectangle(new Point(275, 95), startTimeLabel.getPreferredSize()));

        JLabel chargeTimeLabel = new JLabel();
        chargeTimeLabel.setText("    充电时间      ： ");
        chargeTimeLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        CardInfoPanel.add(chargeTimeLabel);
        chargeTimeLabel.setBounds(new Rectangle(new Point(275, 135), chargeTimeLabel.getPreferredSize()));

        JLabel payRateLabel = new JLabel();
        payRateLabel.setText("    扣款费率      ：");
        payRateLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        CardInfoPanel.add(payRateLabel);
        payRateLabel.setBounds(new Rectangle(new Point(275, 175), payRateLabel.getPreferredSize()));

        JLabel powerRateLabel = new JLabel();
        powerRateLabel.setText("    最大功率      ：");
        powerRateLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        CardInfoPanel.add(powerRateLabel);
        powerRateLabel.setBounds(new Rectangle(new Point(275, 215), powerRateLabel.getPreferredSize()));


        lastTimeText = new JLabel();
        startTimeText = new JLabel();
        chargeTimeText = new JLabel();
        payRateText = new JLabel();
        powerRateText = new JLabel();
        welcomeLabel = new JLabel();
        lastTimeText.setText("-");
        lastTimeText.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        CardInfoPanel.add(lastTimeText);
        lastTimeText.setBounds(380, 55, 100, lastTimeText.getPreferredSize().height);
        startTimeText.setText("-");
        startTimeText.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        CardInfoPanel.add(startTimeText);
        startTimeText.setBounds(380, 95, 100, startTimeText.getPreferredSize().height);
        chargeTimeText.setText("-");
        chargeTimeText.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        CardInfoPanel.add(chargeTimeText);
        chargeTimeText.setBounds(380, 135, 100, chargeTimeText.getPreferredSize().height);
        payRateText.setText("-");
        payRateText.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        CardInfoPanel.add(payRateText);
        payRateText.setBounds(380, 175, 100, payRateText.getPreferredSize().height);
        powerRateText.setText("-");
        powerRateText.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        CardInfoPanel.add(powerRateText);
        powerRateText.setBounds(380, 215, 100, powerRateText.getPreferredSize().height);

        JSeparator separatorButtom = new JSeparator();
        separatorButtom.setOrientation(SwingConstants.HORIZONTAL);
        separatorButtom.setForeground(Color.lightGray);
        contentPane.add(separatorButtom);
        separatorButtom.setBounds(0, 580, 1000, 2);

        tipLabel = new JLabel();
        tipLabel.setText("欢迎使用得康充值系统");
        tipLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        tipLabel.setBounds(10, 590, 200, tipLabel.getPreferredSize().height);
        contentPane.add(tipLabel);
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
        CardInfoPanel.setBounds(310, 80, 560, 280);
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
}
