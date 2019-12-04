package swing;

import bean.*;

import java.awt.event.*;
import java.util.regex.*;

import service.impl.*;

import javax.swing.*;
import java.awt.*;

/**
 * @author kingfans
 */
public class SystemPasswdSettingFrame extends JFrame {
    private User user;
    private JPasswordField newPasswdText;
    private JPasswordField checkPasswdText;
    private JButton confirmButton;

    public SystemPasswdSettingFrame(User user) {
        initComponents(user);
        this.user = user;
        this.setSize(320, 270);
        this.setTitle("刷卡机密码设置");
        this.getRootPane().setDefaultButton(confirmButton);
        Image icon = Toolkit.getDefaultToolkit().getImage("resources/dk_logo.png");
        this.setIconImage(icon);
        this.setLocationRelativeTo(null);
    }

    private void confirmButtonActionPerformed(ActionEvent e) {
        if (newPasswdText.getPassword() != null && checkPasswdText.getPassword() != null) {
            String newPassword = String.valueOf(newPasswdText.getPassword());
            String checkPassword = String.valueOf(checkPasswdText.getPassword());
            String regEx = "\\d{8}$";
            Pattern pattern = Pattern.compile(regEx);
            if (pattern.matcher(newPassword).matches() && pattern.matcher(checkPassword).matches()) {
                if (newPassword.equals(checkPassword)) {
                    user.setSystemPassword(newPassword);
                    ServiceImpl.getInstance().updateSystemPassword(user.getUserId(), user.getUserName(), newPassword);
                    ServiceImpl.getInstance().updateSubSystemPassword(newPassword);
                    JOptionPane.showMessageDialog(null, "系统密码设置成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "密码输入不一致！", "提示", JOptionPane.WARNING_MESSAGE);
                    newPasswdText.setText("");
                    checkPasswdText.setText("");
                    newPasswdText.requestFocus();
                }
            } else {
                JOptionPane.showMessageDialog(null, "请输入8位数字格式的密码！", "提示", JOptionPane.WARNING_MESSAGE);
                newPasswdText.setText("");
                checkPasswdText.setText("");
                newPasswdText.requestFocus();
            }
        }
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        this.dispose();
    }

    private void initComponents(User user) {

        Container contentPane = this.getContentPane();
        contentPane.setLayout(null);

        JLabel newPasswdLabel = new JLabel();
        newPasswdLabel.setText("新密码   : ");
        newPasswdLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
        contentPane.add(newPasswdLabel);
        newPasswdLabel.setBounds(new Rectangle(new Point(60, 50), newPasswdLabel.getPreferredSize()));

        JLabel checkPasswdLabel = new JLabel();
        checkPasswdLabel.setText("确认密码 : ");
        checkPasswdLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
        contentPane.add(checkPasswdLabel);
        checkPasswdLabel.setBounds(new Rectangle(new Point(55, 105), checkPasswdLabel.getPreferredSize()));

        confirmButton = new JButton();
        confirmButton.setText("确认");
        confirmButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        confirmButton.addActionListener(this::confirmButtonActionPerformed);
        contentPane.add(confirmButton);
        confirmButton.setBounds(65, 160, 70, 30);
        confirmButton.setBackground(new Color(180, 205, 205));
        confirmButton.setBorder(BorderFactory.createRaisedBevelBorder());

        JButton cancelButton = new JButton();
        cancelButton.setText("取消");
        cancelButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        cancelButton.addActionListener(this::cancelButtonActionPerformed);
        contentPane.add(cancelButton);
        cancelButton.setBounds(165, 160, 70, 30);
        cancelButton.setBackground(new Color(180, 205, 205));
        cancelButton.setBorder(BorderFactory.createRaisedBevelBorder());

        newPasswdText = new JPasswordField();
        contentPane.add(newPasswdText);
        newPasswdText.setBounds(125, 50, 120, newPasswdText.getPreferredSize().height);

        checkPasswdText = new JPasswordField();
        contentPane.add(checkPasswdText);
        checkPasswdText.setBounds(125, 105, 120, checkPasswdText.getPreferredSize().height);

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
