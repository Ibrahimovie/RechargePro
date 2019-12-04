package swing;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * @Author: kingfans
 * @Date: 2018/11/3
 */
public class ProgressBarWithTipFrame extends JFrame {
    public ProgressBarWithTipFrame(long size, int speed) {
        this.setTitle("升级进度");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(250, 150);
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel label = new JLabel();
        label.setText("正在升级固件...");
        label.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
        label.setBounds(70, 20, 150, 20);
        contentPane.add(label);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        int time = (int) (size / speed);
        int sleep = time * 1000 / 99;
        new Thread(() -> {
            for (int i = 0; i <= 99; i++) {
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progressBar.setValue(i);
            }
        }).start();

        contentPane.add(progressBar);
        progressBar.setBounds(56, 60, 120, 25);

        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        new ProgressBarWithTipFrame(864, 128);
    }
}
