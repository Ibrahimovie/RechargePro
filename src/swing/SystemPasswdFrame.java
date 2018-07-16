package swing;

import bean.*;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

/**
 * @author kingfans
 */
public class SystemPasswdFrame extends JFrame {
    private User user;
    private RechargeFrame rechargeFrame;
    private JFrame waitingFrame;
    private JPasswordField passwordText;
    private JButton confirmButton;

    public SystemPasswdFrame(User user, RechargeFrame rechargeFrame, JFrame waitingFrame) {
        this.user = user;
        this.rechargeFrame = rechargeFrame;
        this.waitingFrame = waitingFrame;
        initComponents();
        this.setSize(315, 260);
        this.setLocationRelativeTo(null);
        this.getRootPane().setDefaultButton(confirmButton);
        Image icon = Toolkit.getDefaultToolkit().getImage("resources/dk_logo.png");
        this.setIconImage(icon);
        this.setVisible(true);
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        rechargeFrame.setEnabled(true);
        waitingFrame.dispose();
        this.dispose();
    }

    private void confirmButtonActionPerformed(ActionEvent e) {
        char[] passwd = this.passwordText.getPassword();
        String password = String.valueOf(passwd);
        System.out.println("system passwd : " + this.user.getSystemPassword());
        if (user.getSystemPassword().equals(password)) {
            try {
                Thread.sleep(200L);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
            waitingFrame.setVisible(true);
            rechargeFrame.setEnabled(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(null, "密码错误！");
            rechargeFrame.setEnabled(true);
            this.dispose();
        }
    }

    private void systemPasswdWindowClosing(WindowEvent e) {
        waitingFrame.dispose();
        rechargeFrame.setEnabled(true);
    }

    private void initComponents() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SystemPasswdFrame.this.systemPasswdWindowClosing(e);
            }
        });
        Container contentPane = this.getContentPane();
        contentPane.setLayout(null);

        JLabel attentionLabel = new JLabel();
        attentionLabel.setText("请输入系统密码 : ");
        attentionLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 18));
        contentPane.add(attentionLabel);
        attentionLabel.setBounds(new Rectangle(new Point(15, 50), attentionLabel.getPreferredSize()));

        passwordText = new JPasswordField();
        contentPane.add(passwordText);
        passwordText.setBounds(70, 95, 210, passwordText.getPreferredSize().height);

        confirmButton = new JButton();
        confirmButton.setText("确定");
        confirmButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        confirmButton.setBackground(new Color(180, 205, 205));
        confirmButton.setBorder(BorderFactory.createRaisedBevelBorder());
        confirmButton.addActionListener(this::confirmButtonActionPerformed);
        contentPane.add(confirmButton);
        confirmButton.setBounds(100, 155, 70, 30);

        JButton cancelButton = new JButton();
        cancelButton.setText("取消");
        cancelButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        cancelButton.setBackground(new Color(180, 205, 205));
        cancelButton.setBorder(BorderFactory.createRaisedBevelBorder());
        cancelButton.addActionListener(this::cancelButtonActionPerformed);
        contentPane.add(cancelButton);
        cancelButton.setBounds(180, 155, 70, 30);

        Dimension preferredSize = new Dimension();
        for (int i = 0; i < contentPane.getComponentCount(); i++) {
            Rectangle bounds = contentPane.getComponent(i).getBounds();
            preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
            preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
        }
        final Insets insets = contentPane.getInsets();
        preferredSize.width += insets.right;
        preferredSize.height += insets.bottom;
        contentPane.setMinimumSize(preferredSize);
        contentPane.setPreferredSize(preferredSize);
        this.pack();
        this.setLocationRelativeTo(this.getOwner());
    }
}
