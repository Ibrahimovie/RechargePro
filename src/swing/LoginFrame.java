package swing;

import javax.swing.plaf.*;

import service.impl.*;
import bean.*;
import bean.manager.*;

import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

/**
 * @author kingfans
 */
public class LoginFrame extends JFrame {
    public static int STATUS_COUNT = 0;
    public static int IS_RECHARGE = 0;
    public static String REG_PHONE = "-";
    public static String REG_NAME = "-";
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
        this.setTitle("得康充值系统 v1.0");
        this.setLocationRelativeTo(null);
        this.getRootPane().setDefaultButton(loginButton);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        new LoginFrame();
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
            User user = new User((int) userInfo.get("user_id"), userName, queryPasswd, (String) userInfo.get("system_password"),
                    (int) userInfo.get("sector_id"), (int) userInfo.get("portrate_id"), (int) userInfo.get("port_id"),
                    (int) userInfo.get("device_id"), 0, 0, (int) userInfo.get("is_admin"));
            UserManager.addUser(user);
            if (password.equals(queryPasswd)) {
                System.out.println("login success ! username : " + userName + " , passwd : " + password);
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
                new RechargeFrame(user);
                this.dispose();
            } else {
                System.out.println("密码错误！");
                JOptionPane.showMessageDialog(null, "密码错误！");
                passwordText.setText("");
                passwordText.requestFocus();
            }
        } else {
            System.out.println("账号不存在！");
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

        JLabel userLabel = new JLabel();
        userLabel.setText("账号");
        userLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        contentPane.add(userLabel);
        userLabel.setBounds(170, 130, 78, userLabel.getPreferredSize().height);

        userText = new JTextField();
        userText.setFont(new Font("Dialog", Font.PLAIN, 12));
        contentPane.add(userText);
        userText.setBounds(235, 125, 105, 30);

        JLabel passwordLabel = new JLabel();
        passwordLabel.setText("密码");
        passwordLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        contentPane.add(passwordLabel);
        passwordLabel.setBounds(170, 170, 78, passwordLabel.getPreferredSize().height);

        loginButton = new JButton();
        loginButton.setText("登录");
        loginButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        loginButton.addActionListener(this::loginActionPerformed);
        contentPane.add(loginButton);
        loginButton.setBounds(265, 210, 73, 35);
        loginButton.setBackground(new Color(180, 205, 205));
        loginButton.setBorder(BorderFactory.createRaisedBevelBorder());

        JButton cancelButton = new JButton();
        cancelButton.setText("退出");
        cancelButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        cancelButton.addActionListener(this::cancelActionPerformed);
        contentPane.add(cancelButton);
        cancelButton.setBounds(180, 210, 73, 35);
        cancelButton.setBackground(new Color(180, 205, 205));
        cancelButton.setBorder(BorderFactory.createRaisedBevelBorder());

        JLabel titleLabel = new JLabel();
        titleLabel.setText("欢迎使用得康充值系统");
        titleLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 22));
        contentPane.add(titleLabel);
        titleLabel.setBounds(140, 70, 250, 30);

        passwordText = new JPasswordField();
        contentPane.add(passwordText);
        passwordText.setBounds(235, 165, 105, 30);

        JLabel url = new JLabel();
        url.setText("杭州得康  www.hzdk.com");
        url.setFont(new Font("Dialog", Font.PLAIN, 12));
        url.setForeground(Color.darkGray);
        url.setBounds(200, 340, url.getPreferredSize().width, url.getPreferredSize().height);
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
