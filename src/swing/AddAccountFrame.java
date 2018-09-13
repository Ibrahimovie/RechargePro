package swing;

import bean.User;
import bean.manager.FrameManager;
import bean.manager.PortManager;
import bean.manager.UserManager;
import service.impl.ServiceImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author kingfans
 */
public class AddAccountFrame extends JFrame {
    private User user;
    private JPasswordField newPasswdText;
    private JPasswordField checkPasswdText;
    private JTextField userText;
    private JButton confirmButton;

    public AddAccountFrame(User user) {
        initComponents(user);
        this.user = user;
        this.setSize(305, 350);
        this.setTitle("添加账号");
        this.getRootPane().setDefaultButton(confirmButton);
        Image icon = Toolkit.getDefaultToolkit().getImage("resources/dk_logo.png");
        this.setIconImage(icon);
        this.setLocationRelativeTo(null);
    }

    private void confirmButtonActionPerformed(ActionEvent e) {
        String username = userText.getText();
        if (username != null && !"".equals(username.trim())) {
            if (!ServiceImpl.getInstance().isUserExist(username)) {
                if (newPasswdText.getPassword() != null && checkPasswdText.getPassword() != null) {
                    String newPassword = String.valueOf(newPasswdText.getPassword());
                    String checkPassword = String.valueOf(checkPasswdText.getPassword());
                    if (!"".equals(newPassword.trim()) && !"".equals(checkPassword.trim())) {
                        if (newPassword.equals(checkPassword)) {
                            ServiceImpl.getInstance().addSubAccount(username, newPassword, user.getSystemPassword(), 0,
                                    user.getPortrateOrder(), user.getPortOrder(), user.getDeviceOrder(), user.getSectorOrder(), user.getCommunity());
                            JOptionPane.showMessageDialog(null, "添加账号 " + username + " 成功! ");
                            this.dispose();
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
                    JOptionPane.showMessageDialog(null, "请输入密码并确认密码！");
                    newPasswdText.setText("");
                    checkPasswdText.setText("");
                    newPasswdText.requestFocus();
                }
            } else {
                JOptionPane.showMessageDialog(null, "用户名已存在，请选择其他用户名！");
                userText.setText("");
                newPasswdText.setText("");
                checkPasswdText.setText("");
                userText.requestFocus();
            }
        } else {
            JOptionPane.showMessageDialog(null, "请输入正确的用户名！");
            userText.setText("");
            newPasswdText.setText("");
            checkPasswdText.setText("");
            userText.requestFocus();
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
        label4.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
        contentPane.add(label4);
        label4.setBounds(new Rectangle(new Point(30, 50), label4.getPreferredSize()));

        userText = new JTextField();
        contentPane.add(userText);
        userText.setBounds(120, 50, 130, userText.getPreferredSize().height);

        JLabel label2 = new JLabel();
        label2.setText("设置密码  : ");
        label2.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
        contentPane.add(label2);
        label2.setBounds(new Rectangle(new Point(40, 100), label2.getPreferredSize()));

        newPasswdText = new JPasswordField();
        contentPane.add(this.newPasswdText);
        newPasswdText.setBounds(120, 100, 130, newPasswdText.getPreferredSize().height);

        JLabel label3 = new JLabel();
        label3.setText("确认密码  : ");
        label3.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
        contentPane.add(label3);
        label3.setBounds(new Rectangle(new Point(40, 150), label3.getPreferredSize()));

        checkPasswdText = new JPasswordField();
        contentPane.add(checkPasswdText);
        checkPasswdText.setBounds(120, 150, 130, checkPasswdText.getPreferredSize().height);


        confirmButton = new JButton();
        confirmButton.setText("确定");
        confirmButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        confirmButton.addActionListener(this::confirmButtonActionPerformed);
        contentPane.add(confirmButton);
        confirmButton.setBounds(60, 210, 70, 30);
        confirmButton.setBackground(new Color(180, 205, 205));
        confirmButton.setBorder(BorderFactory.createRaisedBevelBorder());

        JButton cancelButton = new JButton();
        cancelButton.setText("取消");
        cancelButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        cancelButton.addActionListener(this::cancelButtonActionPerformed);
        contentPane.add(cancelButton);
        cancelButton.setBounds(150, 210, 70, 30);
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
