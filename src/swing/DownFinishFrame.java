package swing;

import bean.manager.FrameManager;
import bean.manager.PortManager;
import bean.manager.UserManager;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

/**
 * @author kingfans
 */
public class DownFinishFrame extends JFrame {

    private String version;

    public DownFinishFrame(String version) {
        this.version = version;
        initComponents();
        this.setSize(350, 280);
        this.setTitle("更新提示");
        Image icon = Toolkit.getDefaultToolkit().getImage("resources/dk_logo.png");
        this.setIconImage(icon);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void confirmButtonActionPerformed(ActionEvent e) {
        Runtime mt = Runtime.getRuntime();
        FileSystemView fsv = FileSystemView.getFileSystemView();
        try {
            String filePath = fsv.getHomeDirectory().getPath() + "\\dk_setup";
            String fileName = "dk_setup" + version + ".exe";
            File myfile = new File(filePath, fileName);
            mt.exec(myfile.getAbsolutePath());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        this.dispose();
        try {
            PortManager.removePort();
            UserManager.removeUser();
            FrameManager.removeFrame("recharge");
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        System.exit(0);
    }

    private void laterButtonActionPerformed(ActionEvent e) {
        this.dispose();
    }

    private void initComponents() {

        Container contentPane = this.getContentPane();
        contentPane.setLayout(null);

        JLabel label1 = new JLabel();
        label1.setText("下载完成！");
        label1.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 16));
        label1.setBounds(130, 50, label1.getPreferredSize().width, label1.getPreferredSize().height);
        contentPane.add(label1);

        JLabel label2 = new JLabel();
        label2.setText("安装包以放至桌面的dk_setup文件夹");
        label2.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
        label2.setBounds(52, 80, label2.getPreferredSize().width, label2.getPreferredSize().height);
        contentPane.add(label2);

        JButton confirmButton = new JButton();
        confirmButton.setText("直接覆盖安装");
        confirmButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        confirmButton.addActionListener(this::confirmButtonActionPerformed);
        contentPane.add(confirmButton);
        confirmButton.setBounds(70, 130, 88, confirmButton.getPreferredSize().height);
        confirmButton.setBackground(new Color(180, 205, 205));
        confirmButton.setBorder(BorderFactory.createRaisedBevelBorder());

        JButton laterButton = new JButton();
        laterButton.setText("稍后自行安装");
        laterButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        laterButton.addActionListener(this::laterButtonActionPerformed);
        contentPane.add(laterButton);
        laterButton.setBounds(180, 130, 88, laterButton.getPreferredSize().height);
        laterButton.setBackground(new Color(180, 205, 205));
        laterButton.setBorder(BorderFactory.createRaisedBevelBorder());


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
        new DownFinishFrame("2.0");
    }

}

