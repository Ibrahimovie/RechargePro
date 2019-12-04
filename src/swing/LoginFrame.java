package swing;

import javax.swing.plaf.*;

import com.alibaba.fastjson.JSONObject;
import service.impl.*;
import bean.*;
import bean.manager.*;
import utils.AESUtils;
import utils.HttpUtils;

import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

/**
 * @author kingfans
 */
public class LoginFrame extends JFrame {
    public static final String SOFT_VERSION = "3.3"; //充值系统软件版本
    public static String LOCAL_HARD_VERSION = "-"; //本地硬件设备版本,稍后根据设备返回确定版本（软件版本）
    public static String SERVER_HARD_VERSION = "-"; //服务器上硬件设备版本
    public static int SERVER_HARDWARE_SIZE = 0; //服务器上硬件设备文件大小
    public static int STATUS_COUNT = 0; //端口状态计数
    public static int SERVER_STATUS_COUNT = 0;  //服务器状态计数
    public static int HARD_UPDATE_STATUS = 0;  //硬件升级状态
    public static int IS_RECHARGE = 0;  // 0：正常查询  1：充值前查询  2：领取圈存前查询  3：注销前查询
    public static int PRE_HIS_MARK = 0;  // 区分正常充值与圈存领取的充值， 0为正常充值，其他为圈存，值即为剩余待领取金额
    public static String REG_PHONE = "-";  //注册的手机号
    public static String REG_NAME = "-";  //注册的姓名
    public static Map<Integer, byte[]> FILE_BYTES_MAP = null;
    public static int SEND_COUNT = 0;
    public static int E1_HANDMADES = 0;
    public static int E1_STATUS = 0;
    public static long LOGIN_TIME = 0;
    public static int PORT_NUM = 0;
    public static final String ERFSXXV = "hzdkkrecharge23";
    public static String FTP_IP = null;
    public static int FTP_PORT = 0;
    public static String FTP_USERNAME = null;
    public static String FTP_PASSWD = null;

    private JTextField userText;
    private JPasswordField passwordText;
    private JButton loginButton;

    private JLayeredPane layeredPane;
    private JPanel backgroundPanel;
    private JLabel backgroundLabel;
    ImageIcon image;


    public LoginFrame() {
        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Microsoft YaHei UI", Font.BOLD, 12)));
        UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("Microsoft YaHei UI", Font.BOLD, 13)));
        initComponents();

        this.setLayeredPane(layeredPane);
        this.setSize(image.getIconWidth(), image.getIconHeight());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(image.getIconWidth(), image.getIconHeight());
        this.setVisible(true);
        Image icon = Toolkit.getDefaultToolkit().getImage("resources/dk_logo.png");
        this.setIconImage(icon);
        this.setTitle("得康充值系统 v" + SOFT_VERSION + "(离线版)");
        this.getRootPane().setDefaultButton(loginButton);
        this.setLocationRelativeTo(null);

    }

    private void loginActionPerformed(ActionEvent e) {
        String userName = this.userText.getText();
        char[] passwd = this.passwordText.getPassword();
        String password = String.valueOf(passwd);
        String queryPasswd;
        if (ServiceImpl.getInstance().isUserExist(userName)) {
            Map<String, Object> userInfo = ServiceImpl.getInstance().getUserInfo(userName);
            queryPasswd = (String) userInfo.get("password");
            if (password.equals(queryPasswd)) {
//                String community = ServiceImpl.getInstance().getCommunity(userName);
                User user = new User((int) userInfo.get("user_id"), userName, queryPasswd, (String) userInfo.get("system_password"),
                        (int) userInfo.get("sector_id"), (int) userInfo.get("portrate_id"), (int) userInfo.get("port_id"),
                        (int) userInfo.get("device_id"), 0, 0, (int) userInfo.get("is_admin"), "");
                UserManager.addUser(user);
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
                new RechargeFrame(user);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(null, "密码错误！");
                passwordText.setText("");
                passwordText.requestFocus();
            }
        } else {
            JOptionPane.showMessageDialog(null, "账号不存在！");
            userText.setText("");
            passwordText.setText("");
            userText.requestFocus();
        }

    }

    private void cancelActionPerformed(ActionEvent e) {
        System.exit(0);
    }

    private void loginWindowClosing(WindowEvent e) {
        System.exit(0);
    }


    private void initComponents() {
//        Map<String, Object> map = ServiceImpl.getInstance().getMap();
//        FTP_IP = AESUtils.AESDecode(ERFSXXV, (String) map.get("param1"));
//        FTP_PORT = Integer.parseInt(Objects.requireNonNull(AESUtils.AESDecode(ERFSXXV, (String) map.get("param2"))));
//        FTP_USERNAME = AESUtils.AESDecode(ERFSXXV, (String) map.get("param3"));
//        FTP_PASSWD = AESUtils.AESDecode(ERFSXXV, (String) map.get("param4"));
//        String response = HttpUtils.toServlet(null, "version", "getHardwareVersion");
//        JSONObject jsonObject = JSONObject.parseObject(response);
//        String code = jsonObject.getString("code");
//        if ("0".equals(code)) {
//            String resVersion = jsonObject.getString("version");
//            SERVER_HARD_VERSION = resVersion.substring(13, resVersion.length() - 4);
//            SERVER_HARDWARE_SIZE = Math.toIntExact(jsonObject.getLong("size"));
//        }
//        System.out.println("server hardware version : " + SERVER_HARD_VERSION + " , size : " + SERVER_HARDWARE_SIZE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                loginWindowClosing(e);
            }
        });

        layeredPane = new JLayeredPane();
        image = new ImageIcon("resources/login_bg.png");
        backgroundPanel = new JPanel();
        backgroundPanel.setBounds(0, 0, image.getIconWidth(), image.getIconHeight());

        backgroundLabel = new JLabel(image);
        backgroundPanel.add(backgroundLabel);

        layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

        JLabel userLabel = new JLabel();
        userLabel.setText("账号 ");
        userLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        layeredPane.add(userLabel, JLayeredPane.MODAL_LAYER);
        userLabel.setBounds(170, 140, 78, userLabel.getPreferredSize().height);

        userText = new JTextField();
        userText.setOpaque(false);
        userText.setFont(new Font("Dialog", Font.PLAIN, 12));
        layeredPane.add(userText, JLayeredPane.MODAL_LAYER);
        userText.setBounds(215, 135, 130, 30);

        JLabel passwordLabel = new JLabel();
        passwordLabel.setText("密码 ");
        passwordLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        layeredPane.add(passwordLabel, JLayeredPane.MODAL_LAYER);
        passwordLabel.setBounds(170, 175, 78, passwordLabel.getPreferredSize().height);

        passwordText = new JPasswordField();
        passwordText.setOpaque(false);
        layeredPane.add(passwordText, JLayeredPane.MODAL_LAYER);
        passwordText.setBounds(215, 170, 130, 30);

        ImageIcon icon = new ImageIcon("resources/button_bg.png");
        Image img = icon.getImage().getScaledInstance(85, 35, Image.SCALE_FAST);

        loginButton = new JButton();
        loginButton.setText("登录");
        loginButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        loginButton.setForeground(Color.white);
        loginButton.setIcon(new ImageIcon(img));
        loginButton.setHorizontalTextPosition(SwingConstants.CENTER);
        loginButton.setOpaque(false);
        loginButton.setMargin(new Insets(0, 0, 0, 0));
        loginButton.setFocusPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setBorder(null);
        loginButton.addActionListener(this::loginActionPerformed);
        layeredPane.add(loginButton, JLayeredPane.MODAL_LAYER);
        loginButton.setBounds(260, 215, 85, 35);

        JButton cancelButton = new JButton();
        cancelButton.setText("退出");
        cancelButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        cancelButton.setForeground(Color.white);
        cancelButton.setIcon(new ImageIcon(img));
        cancelButton.setHorizontalTextPosition(SwingConstants.CENTER);
        cancelButton.setOpaque(false);
        cancelButton.setMargin(new Insets(0, 0, 0, 0));
        cancelButton.setFocusPainted(false);
        cancelButton.setContentAreaFilled(false);
        cancelButton.setBorder(null);
        cancelButton.addActionListener(this::cancelActionPerformed);
        layeredPane.add(cancelButton, JLayeredPane.MODAL_LAYER);
        cancelButton.setBounds(170, 215, 85, 35);

        JLabel titleLabel = new JLabel();
        titleLabel.setText("欢迎使用得康充值系统");
        titleLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 22));
        layeredPane.add(titleLabel, JLayeredPane.MODAL_LAYER);
        titleLabel.setBounds(150, 70, 250, 30);

        JLabel url = new JLabel();
        url.setText("杭州得康  www.hzdk.com");
        url.setFont(new Font("Dialog", Font.PLAIN, 12));
        url.setForeground(Color.darkGray);
        url.setBounds(183, 340, url.getPreferredSize().width, url.getPreferredSize().height);
        layeredPane.add(url, JLayeredPane.MODAL_LAYER);

    }

}
