package swing;

import utils.FTPUtils;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author kingfans
 */
public class LoginUpdateFrame extends JFrame {

    private String version;
    private long size;

    public LoginUpdateFrame(String version, long size) {
        this.version = version;
        this.size = size;
        initComponents();
        this.setSize(350, 280);
        this.setTitle("更新提示");
        Image icon = Toolkit.getDefaultToolkit().getImage("resources/dk_logo.png");
        this.setIconImage(icon);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void updateButtonActionPerformed(ActionEvent e) {
        this.dispose();
        int speed = 150;//理论128
        new Thread(() -> {
            ProgressBarFrame progressBarFrame = new ProgressBarFrame(size, speed);
            FTPUtils ftpUtils = new FTPUtils();
            String serverPath = "/home/FTPUser/software";
            String fileName = "dk_setup" + version + ".exe";
            FileSystemView fsv = FileSystemView.getFileSystemView();
            String localPath = fsv.getHomeDirectory().getPath() + "\\dk_setup";
            ftpUtils.downloadFileWithProgress(serverPath, fileName, localPath, progressBarFrame, version);
        }).start();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        this.dispose();
    }

    private void initComponents() {
        JLabel photoView = new JLabel();

        JLabel nowVersion = new JLabel();
        JButton updateButton = new JButton();
        JButton cancelButton = new JButton();

        Container contentPane = this.getContentPane();
        contentPane.setLayout(null);

        ImageIcon ico = new ImageIcon("resources/dk_logo_2.png");
        ico.setImage(ico.getImage().getScaledInstance(94, 26, 1));
        photoView.setBounds(120, 30, 100, 30);
        photoView.setIcon(ico);
        contentPane.add(photoView);


        nowVersion.setText("有可用新版本 :  " + version);
        nowVersion.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 16));
        nowVersion.setBounds(95, 100, nowVersion.getPreferredSize().width, nowVersion.getPreferredSize().height);
        contentPane.add(nowVersion);

        updateButton.setText("更新");
        updateButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        updateButton.addActionListener(this::updateButtonActionPerformed);
        contentPane.add(updateButton);
        updateButton.setBounds(80, 150, 75, updateButton.getPreferredSize().height);
        updateButton.setBackground(new Color(180, 205, 205));
        updateButton.setBorder(BorderFactory.createRaisedBevelBorder());

        cancelButton.setText("取消");
        cancelButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        cancelButton.addActionListener(this::cancelButtonActionPerformed);
        contentPane.add(cancelButton);
        cancelButton.setBounds(170, 150, 75, cancelButton.getPreferredSize().height);
        cancelButton.setBackground(new Color(180, 205, 205));
        cancelButton.setBorder(BorderFactory.createRaisedBevelBorder());


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

    public static void main(String[] args) {
        new LoginUpdateFrame("2.0", 864);
    }
}

