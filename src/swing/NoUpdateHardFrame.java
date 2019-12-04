package swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author kingfans
 */
public class NoUpdateHardFrame extends JFrame {


    public NoUpdateHardFrame() {
        initComponents();
        this.setSize(350, 280);
        this.setTitle("升级提示");
        Image icon = Toolkit.getDefaultToolkit().getImage("resources/dk_logo.png");
        this.setIconImage(icon);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void confirmButtonActionPerformed(ActionEvent e) {
        this.dispose();
    }

    private void initComponents() {
        JLabel photoView = new JLabel();

        JLabel nowVersion = new JLabel();
        JButton confirmButton = new JButton();

        Container contentPane = this.getContentPane();
        contentPane.setLayout(null);

        ImageIcon ico = new ImageIcon("resources/dk_logo_2.png");
        ico.setImage(ico.getImage().getScaledInstance(94, 26, 1));
        photoView.setBounds(120, 30, 100, 30);
        photoView.setIcon(ico);
        contentPane.add(photoView);


        nowVersion.setText("当前已是最新固件版本！");
        nowVersion.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 16));
        nowVersion.setBounds(85, 100, nowVersion.getPreferredSize().width, nowVersion.getPreferredSize().height);
        contentPane.add(nowVersion);

        confirmButton.setText("确定");
        confirmButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        confirmButton.addActionListener(this::confirmButtonActionPerformed);
        contentPane.add(confirmButton);
        confirmButton.setBounds(120, 160, 88, confirmButton.getPreferredSize().height);
        confirmButton.setBackground(new Color(180, 205, 205));
        confirmButton.setBorder(BorderFactory.createRaisedBevelBorder());


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

