package swing;

import bean.User;
import bean.manager.FrameManager;
import bean.manager.PortManager;
import bean.manager.UserManager;
import service.impl.ServiceImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author kingfans
 */
public class EditUserFrame extends JFrame {
    private User user;
    private String formerUsername;
    private String formerPassword;
    private JPasswordField newPasswdText;
    private JPasswordField checkPasswdText;
    private JTextField userText;
    private JButton confirmButton;
    private AccountManageFrame frame;

    public EditUserFrame(User user, String formerUsername, String formerPassword, AccountManageFrame frame) {
        this.formerUsername = formerUsername;
        this.formerPassword = formerPassword;
        this.frame = frame;
        this.user = user;
        initComponents(user);
        this.setSize(305, 350);
        this.setTitle("账号设置");
        this.getRootPane().setDefaultButton(confirmButton);
        Image icon = Toolkit.getDefaultToolkit().getImage("resources/dk_logo.png");
        this.setIconImage(icon);
        this.setLocationRelativeTo(null);
    }

    private void confirmButtonActionPerformed(ActionEvent e) {
        String username = userText.getText();
        if (username.equals(formerUsername)) {
            if (newPasswdText.getPassword() != null && checkPasswdText.getPassword() != null) {
                String newPassword = String.valueOf(newPasswdText.getPassword());
                String checkPassword = String.valueOf(checkPasswdText.getPassword());
                if (!"".equals(newPassword.trim()) && !"".equals(checkPassword.trim())) {
                    if (newPassword.equals(checkPassword)) {
                        ServiceImpl.getInstance().updateSubPassword(username, newPassword);
                        JOptionPane.showMessageDialog(null, "账号设置成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                        frame.setEnabled(true);
                        this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "密码输入不一致！", "提示", JOptionPane.WARNING_MESSAGE);
                        newPasswdText.setText("");
                        checkPasswdText.setText("");
                        newPasswdText.requestFocus();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "请输入正确的密码格式！", "提示", JOptionPane.WARNING_MESSAGE);
                    newPasswdText.setText("");
                    checkPasswdText.setText("");
                    newPasswdText.requestFocus();
                }
            } else {
                JOptionPane.showMessageDialog(null, "请输入新密码！", "提示", JOptionPane.WARNING_MESSAGE);
                newPasswdText.setText("");
                checkPasswdText.setText("");
                newPasswdText.requestFocus();
            }
        } else {
            if (!ServiceImpl.getInstance().isUserExist(username)) {
                if (newPasswdText.getPassword() != null && checkPasswdText.getPassword() != null) {
                    String newPassword = String.valueOf(newPasswdText.getPassword());
                    String checkPassword = String.valueOf(checkPasswdText.getPassword());
                    if (!"".equals(newPassword.trim()) && !"".equals(checkPassword.trim())) {
                        if (newPassword.equals(checkPassword)) {
                            ServiceImpl.getInstance().updateSubUserPasswd(formerUsername, username, newPassword);
                            JOptionPane.showMessageDialog(null, "账号设置成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                            frame.setEnabled(true);
                            this.dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, "密码输入不一致！", "提示", JOptionPane.WARNING_MESSAGE);
                            newPasswdText.setText("");
                            checkPasswdText.setText("");
                            newPasswdText.requestFocus();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "请输入正确的密码格式！", "提示", JOptionPane.WARNING_MESSAGE);
                        newPasswdText.setText("");
                        checkPasswdText.setText("");
                        newPasswdText.requestFocus();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "请输入新密码！", "提示", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "用户名已存在，请选择其他用户名！", "提示", JOptionPane.WARNING_MESSAGE);
                userText.addFocusListener(new JTextFieldHintListener(userText, formerUsername));
                newPasswdText.setText("");
                checkPasswdText.setText("");
                newPasswdText.requestFocus();
            }

        }


    }


    private void cancelButtonActionPerformed(ActionEvent e) {
        frame.setEnabled(true);
        this.dispose();
    }

    private void editUserWindowClosing(WindowEvent e) {
        frame.setEnabled(true);
    }

    private void initComponents(User user) {

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                editUserWindowClosing(e);
            }
        });

        Container contentPane = this.getContentPane();
        contentPane.setLayout(null);

        JLabel label4 = new JLabel();
        label4.setText("用户名  : ");
        label4.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
        contentPane.add(label4);
        label4.setBounds(new Rectangle(new Point(40, 45), label4.getPreferredSize()));

        userText = new JTextField();
        contentPane.add(userText);
        userText.setBounds(110, 45, 130, userText.getPreferredSize().height);
        userText.addFocusListener(new JTextFieldHintListener(userText, formerUsername));

        JLabel label2 = new JLabel();
        label2.setText("新密码  : ");
        label2.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
        contentPane.add(label2);
        label2.setBounds(new Rectangle(new Point(40, 100), label2.getPreferredSize()));

        newPasswdText = new JPasswordField();
        contentPane.add(this.newPasswdText);
        newPasswdText.setBounds(110, 100, 130, newPasswdText.getPreferredSize().height);

        JLabel label3 = new JLabel();
        label3.setText("确认密码 : ");
        label3.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
        contentPane.add(label3);
        label3.setBounds(new Rectangle(new Point(35, 155), label3.getPreferredSize()));

        checkPasswdText = new JPasswordField();
        contentPane.add(checkPasswdText);
        checkPasswdText.setBounds(110, 155, 130, checkPasswdText.getPreferredSize().height);

        confirmButton = new JButton();
        confirmButton.setText("确定");
        confirmButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        confirmButton.addActionListener(this::confirmButtonActionPerformed);
        contentPane.add(confirmButton);
        confirmButton.setBounds(65, 215, 70, 30);
        confirmButton.setBackground(new Color(180, 205, 205));
        confirmButton.setBorder(BorderFactory.createRaisedBevelBorder());

        JButton cancelButton = new JButton();
        cancelButton.setText("取消");
        cancelButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        cancelButton.addActionListener(this::cancelButtonActionPerformed);
        contentPane.add(cancelButton);
        cancelButton.setBounds(155, 215, 70, 30);
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
