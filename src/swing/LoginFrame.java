package swing;

import javax.swing.plaf.*;

import com.alibaba.fastjson.JSONObject;
import service.impl.*;
import bean.*;
import bean.manager.*;
import utils.HttpUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author kingfans
 */
public class LoginFrame extends JFrame {
    public static int STATUS_COUNT = 0;
    public static int SERVER_STATUS_COUNT = 0;
    public static int IS_RECHARGE = 0;
    public static int IS_UNCLIAMED = 0;
    public static String REG_PHONE = "-";
    public static String REG_NAME = "-";
    public static int IS_FIRSTTIME = 1;
    private JTextField userText;
    private JPasswordField passwordText;
    private JButton loginButton;

    public LoginFrame() {
        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Microsoft YaHei UI", Font.BOLD, 12)));
        UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("Microsoft YaHei UI", Font.BOLD, 13)));
        initComponents();
        this.setSize(new Dimension(535, 405));
        Image icon = Toolkit.getDefaultToolkit().getImage("resources/dk_logo.png");
        this.setIconImage(icon);
        this.setTitle("得康充值系统 v1.5");
        this.setLocationRelativeTo(null);
        this.getRootPane().setDefaultButton(loginButton);
        this.setVisible(true);
    }

    private void loginActionPerformed(ActionEvent e) {
        String userName = this.userText.getText();
        char[] passwd = this.passwordText.getPassword();
        String password = String.valueOf(passwd);
        String queryPasswd;
        if (ServiceImpl.getInstance().isUserExist(userName)) {
            Map<String, Object> userInfo = ServiceImpl.getInstance().getUserInfo(userName);
            System.out.println("user_info : " + userInfo.toString());
            queryPasswd = (String) userInfo.get("password");
            if (password.equals(queryPasswd)) {
                String community = ServiceImpl.getInstance().getCommunity(userName);
                User user = new User((int) userInfo.get("user_id"), userName, queryPasswd, (String) userInfo.get("system_password"),
                        (int) userInfo.get("sector_id"), (int) userInfo.get("portrate_id"), (int) userInfo.get("port_id"),
                        (int) userInfo.get("device_id"), 0, 0, (int) userInfo.get("is_admin"), community);
                UserManager.addUser(user);
                System.out.println("login success ! username : " + userName + " , passwd : " + password);
                System.out.println("user info : " + user.toString());
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
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                loginWindowClosing(e);
            }
        });
        Container contentPane = this.getContentPane();
        contentPane.setLayout(null);

//        JLabel communityLabel = new JLabel();
//        communityLabel.setText("小区 : ");
//        communityLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
//        contentPane.add(communityLabel);
//        communityLabel.setBounds(160, 130, 78, communityLabel.getPreferredSize().height);

//        communityChooser = new JComboBox();
//        Map<String, String> map = new HashMap<>(1);
//        map.put("action", "getCommunities");
//        String resp = HttpUtils.toServlet(map, "CommunityServlet");
//        JSONObject jsonObject = JSONObject.parseObject(resp);
//        String communities;
//        ArrayList<String> arrayList = null;
//        try {
//            communities = URLDecoder.decode(jsonObject.getString("community"), "utf-8");
//            List<String> lis = Arrays.asList(communities.split(","));
//            arrayList = new ArrayList(lis);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        arrayList.add(0, "请选择");
//        String[] communityArray = arrayList.toArray(new String[0]);
//        communityChooser.setModel(new DefaultComboBoxModel<>(communityArray));
//        communityChooser.addItemListener(this::communityChooserItemStateChanged);
//        contentPane.add(communityChooser);
//        communityChooser.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
//        communityChooser.setBounds(225, 125, 130, 30);


        JLabel userLabel = new JLabel();
        userLabel.setText("账号 : ");
        userLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        contentPane.add(userLabel);
        userLabel.setBounds(160, 140, 78, userLabel.getPreferredSize().height);

        userText = new JTextField();
        userText.setFont(new Font("Dialog", Font.PLAIN, 12));
        contentPane.add(userText);
        userText.setBounds(225, 135, 130, 30);

        JLabel passwordLabel = new JLabel();
        passwordLabel.setText("密码 : ");
        passwordLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        contentPane.add(passwordLabel);
        passwordLabel.setBounds(160, 175, 78, passwordLabel.getPreferredSize().height);

        passwordText = new JPasswordField();
        contentPane.add(passwordText);
        passwordText.setBounds(225, 170, 130, 30);


        loginButton = new JButton();
        loginButton.setText("登录");
        loginButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        loginButton.addActionListener(this::loginActionPerformed);
        contentPane.add(loginButton);
        loginButton.setBounds(260, 215, 73, 35);
        loginButton.setBackground(new Color(180, 205, 205));
        loginButton.setBorder(BorderFactory.createRaisedBevelBorder());

        JButton cancelButton = new JButton();
        cancelButton.setText("退出");
        cancelButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        cancelButton.addActionListener(this::cancelActionPerformed);
        contentPane.add(cancelButton);
        cancelButton.setBounds(175, 215, 73, 35);
        cancelButton.setBackground(new Color(180, 205, 205));
        cancelButton.setBorder(BorderFactory.createRaisedBevelBorder());

        JLabel titleLabel = new JLabel();
        titleLabel.setText("欢迎使用得康充值系统");
        titleLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 22));
        contentPane.add(titleLabel);
        titleLabel.setBounds(150, 70, 250, 30);


        JLabel url = new JLabel();
        url.setText("杭州得康  www.hzdk.com");
        url.setFont(new Font("Dialog", Font.PLAIN, 12));
        url.setForeground(Color.darkGray);
        url.setBounds(183, 340, url.getPreferredSize().width, url.getPreferredSize().height);
        contentPane.add(url);

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
