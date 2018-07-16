package swing;

import javax.swing.*;
import java.awt.*;

/**
 * @author kingfans
 */
public class AboutFrame extends JFrame {

    public AboutFrame() {
        initComponents();
        this.setSize(400, 310);
        this.setTitle("关于我们");
        Image icon = Toolkit.getDefaultToolkit().getImage("resources/dk_logo.png");
        System.out.println(icon.toString());
        this.setIconImage(icon);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void initComponents() {
        JLabel photoView = new JLabel();
        JLabel name = new JLabel();
        JLabel version = new JLabel();
        JLabel address = new JLabel();
        JLabel telephone = new JLabel();
        JLabel fax = new JLabel();
        JLabel url = new JLabel();
        JLabel email = new JLabel();

        Container contentPane = this.getContentPane();
        contentPane.setLayout(null);

        ImageIcon ico = new ImageIcon("resources/dk_logo_2.png");
        ico.setImage(ico.getImage().getScaledInstance(94, 26, 1));
        photoView.setBounds(50, 30, 100, 30);
        photoView.setIcon(ico);
        contentPane.add(photoView);

        name.setText("<html><u>杭州得康蓄电池修复仪有限公司</u></html>");
        name.setFont(new Font("Dialog", Font.PLAIN, 12));
        name.setForeground(Color.blue);
        name.setBounds(50, 90, name.getPreferredSize().width, name.getPreferredSize().height);
        contentPane.add(name);

        version.setText("软件版本: version 1.0");
        version.setFont(new Font("Dialog", Font.PLAIN, 12));
        version.setBounds(50, 130, version.getPreferredSize().width, version.getPreferredSize().height);
        contentPane.add(version);

        address.setText("公司地址: 杭州市西湖科技经济园 西园一路12号");
        address.setFont(new Font("Dialog", Font.PLAIN, 12));
        address.setBounds(50, 150, address.getPreferredSize().width, address.getPreferredSize().height);
        contentPane.add(address);

        telephone.setText("联系电话: 0571-89905402");
        telephone.setFont(new Font("Dialog", Font.PLAIN, 12));
        telephone.setBounds(50, 170, telephone.getPreferredSize().width, telephone.getPreferredSize().height);
        contentPane.add(telephone);

        fax.setText("公司传真: 0571-89900087");
        fax.setFont(new Font("Dialog", Font.PLAIN, 12));
        fax.setBounds(50, 190, fax.getPreferredSize().width, fax.getPreferredSize().height);
        contentPane.add(fax);

        url.setText("公司网址: www.hzdk.com");
        url.setFont(new Font("Dialog", Font.PLAIN, 12));
        url.setBounds(50, 210, url.getPreferredSize().width, url.getPreferredSize().height);
        contentPane.add(url);

        email.setText("电子邮箱: HZDK@188.COM");
        email.setFont(new Font("Dialog", Font.PLAIN, 12));
        email.setBounds(50, 230, email.getPreferredSize().width, email.getPreferredSize().height);
        contentPane.add(email);

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

