package swing;


import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * @Author: kingfans
 * @Date: 2018/11/3
 */
public class ProgressBarFrame extends JFrame {
    public ProgressBarFrame(long size, int speed) {
        this.setTitle("更新进度");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(250, 100);
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setContentPane(contentPane);
        contentPane.setLayout(null);

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
        progressBar.setBounds(56, 20, 120, 25);

        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        new ProgressBarFrame(864, 128);
    }
}
