package swing;

import bean.*;

import java.awt.event.*;

import service.impl.*;
import bean.manager.*;

import javax.swing.*;
import java.awt.*;

/**
 * @author kingfans
 */
public class LoginPasswdSettingFrame extends JFrame {
    private User user;
    private RechargeFrame rechargeFrame;
    private JPasswordField formerPasswdText;
    private JPasswordField newPasswdText;
    private JPasswordField checkPasswdText;
    private JTextField userText;
    private JButton confirmButton;

    public LoginPasswdSettingFrame(User user, RechargeFrame rechargeFrame) {
        initComponents(user);
        this.user = user;
        this.rechargeFrame = rechargeFrame;
        this.setSize(305, 350);
        this.setTitle("登录帐号设置");
        this.getRootPane().setDefaultButton(confirmButton);
        Image icon = Toolkit.getDefaultToolkit().getImage("resources/dk_logo.png");
        this.setIconImage(icon);
        this.setLocationRelativeTo(null);
    }

    private void confirmButtonActionPerformed(ActionEvent e) {
        String username = this.userText.getText();
        if (formerPasswdText.getPassword() != null) {
            String formerPassword = String.valueOf(formerPasswdText.getPassword());
            if (formerPassword.equals(user.getPassword())) {
                if (newPasswdText.getPassword() != null && checkPasswdText.getPassword() != null) {
                    String newPassword = String.valueOf(newPasswdText.getPassword());
                    String checkPassword = String.valueOf(checkPasswdText.getPassword());
                    if (!"".equals(newPassword.trim()) && !"".equals(checkPassword.trim())) {
                        if (newPassword.equals(checkPassword)) {
                            if (username != null && !"".equals(username.trim())) {
                                user.setUserName(username);
                                ServiceImpl.getInstance().updateUsername(user.getUserId(), username);
                            }
                            user.setPassword(newPassword);
                            ServiceImpl.getInstance().updatePassword(user.getUserId(), user.getUserName(), newPassword);
                            JOptionPane.showMessageDialog(null, "登录密码设置成功！请重新登录");
                            this.dispose();
                            UserManager.removeUser();
                            PortManager.removePort();
                            rechargeFrame.dispose();
                            FrameManager.removeFrame("recharge");
                            new LoginFrame();
                        } else {
                            JOptionPane.showMessageDialog(null, "密码输入不一致！");
                            newPasswdText.setText("");
                            checkPasswdText.setText("");
                            newPasswdText.requestFocus();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "请输入正确的密码格式！");
                        newPasswdText.setText("");
                        checkPasswdText.setText("");
                        newPasswdText.requestFocus();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "请输入新密码！");
                }
            } else {
                JOptionPane.showMessageDialog(null, "原密码错误！");
                formerPasswdText.setText("");
                newPasswdText.setText("");
                checkPasswdText.setText("");
                formerPasswdText.requestFocus();
            }
        } else {
            JOptionPane.showMessageDialog(null, "请输入原密码！");
        }
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        this.dispose();
    }

    private void initComponents(User user) {

        Container contentPane = this.getContentPane();
        contentPane.setLayout(null);

        JLabel label4 = new JLabel();
        label4.setText("设置用户名 : ");
        label4.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        contentPane.add(label4);
        label4.setBounds(new Rectangle(new Point(30, 50), label4.getPreferredSize()));

        userText = new JTextField();
        contentPane.add(userText);
        userText.setBounds(110, 45, 130, userText.getPreferredSize().height);

        JLabel label1 = new JLabel();
        label1.setText("原密码 ：");
        label1.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        contentPane.add(label1);
        label1.setBounds(40, 90, 55, 20);

        JLabel label2 = new JLabel();
        label2.setText("新密码 ：");
        label2.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        contentPane.add(label2);
        label2.setBounds(new Rectangle(new Point(40, 135), label2.getPreferredSize()));

        formerPasswdText = new JPasswordField();
        contentPane.add(formerPasswdText);
        formerPasswdText.setBounds(110, 85, 130, formerPasswdText.getPreferredSize().height);

        newPasswdText = new JPasswordField();
        contentPane.add(this.newPasswdText);
        newPasswdText.setBounds(110, 130, 130, newPasswdText.getPreferredSize().height);

        JLabel label3 = new JLabel();
        label3.setText("确认密码 ：");
        label3.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        contentPane.add(label3);
        label3.setBounds(new Rectangle(new Point(35, 180), label3.getPreferredSize()));

        checkPasswdText = new JPasswordField();
        contentPane.add(checkPasswdText);
        checkPasswdText.setBounds(110, 175, 130, checkPasswdText.getPreferredSize().height);


        confirmButton = new JButton();
        confirmButton.setText("确定");
        confirmButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        confirmButton.addActionListener(this::confirmButtonActionPerformed);
        contentPane.add(confirmButton);
        confirmButton.setBounds(75, 235, 70, 30);
        confirmButton.setBackground(new Color(180, 205, 205));
        confirmButton.setBorder(BorderFactory.createRaisedBevelBorder());

        JButton cancelButton = new JButton();
        cancelButton.setText("取消");
        cancelButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        cancelButton.addActionListener(this::cancelButtonActionPerformed);
        contentPane.add(cancelButton);
        cancelButton.setBounds(165, 235, 70, 30);
        cancelButton.setBackground(new Color(180, 205, 205));
        cancelButton.setBorder(BorderFactory.createRaisedBevelBorder());

        Dimension preferredSize = new Dimension();
        for (int i = 0; i < contentPane.getComponentCount(); i++) {
            final Rectangle bounds = contentPane.getComponent(i).getBounds();
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
