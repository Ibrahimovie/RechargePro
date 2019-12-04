package swing;

import bean.*;

import java.awt.event.*;

import service.impl.*;

import javax.swing.*;
import java.awt.*;

/**
 * @author kingfans
 */
public class SectorFrame extends JFrame {
    private User user;
    private JComboBox<String> comboBox1;
    private JButton button1;

    public SectorFrame(User user) {
        initComponents(user);
        this.user = user;
        this.setSize(230, 220);
        this.setTitle("扇区");
        this.getRootPane().setDefaultButton(button1);
        Image icon = Toolkit.getDefaultToolkit().getImage("resources/dk_logo.png");
        this.setIconImage(icon);
        this.setLocationRelativeTo(null);
    }

    private void confirmButtonActionPerformed(ActionEvent e) {
        int selectOrder = comboBox1.getSelectedIndex();
        user.setSectorOrder(selectOrder);
        ServiceImpl.getInstance().updateSectorOrder(user.getUserId(), user.getUserName(), selectOrder);
        ServiceImpl.getInstance().updateSubSectorOrder(selectOrder);
        JOptionPane.showMessageDialog(null, "设置成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
        this.dispose();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        this.dispose();
    }

    private void initComponents(User user) {

        Container contentPane = this.getContentPane();
        contentPane.setLayout(null);

        JLabel label = new JLabel();
        label.setText("设置扇区");
        label.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        contentPane.add(label);
        label.setBounds(35, 33, 60, 30);

        comboBox1 = new JComboBox<>();
        comboBox1.setModel(new DefaultComboBoxModel<>(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"}));
        comboBox1.setSelectedIndex(user.getSectorOrder());
        contentPane.add(comboBox1);
        comboBox1.setBounds(105, 35, 70, 25);


        button1 = new JButton();
        button1.setText("确定");
        button1.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        button1.setBackground(new Color(180, 205, 205));
        button1.setBorder(BorderFactory.createRaisedBevelBorder());
        button1.addActionListener(this::confirmButtonActionPerformed);
        contentPane.add(button1);
        button1.setBounds(30, 105, 70, 30);


        JButton button2 = new JButton();
        button2.setText("取消");
        button2.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        button2.setBackground(new Color(180, 205, 205));
        button2.setBorder(BorderFactory.createRaisedBevelBorder());
        button2.addActionListener(this::cancelButtonActionPerformed);
        contentPane.add(button2);
        button2.setBounds(110, 105, 70, 30);


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
