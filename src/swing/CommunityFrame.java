package swing;

import com.alibaba.fastjson.JSONObject;
import service.impl.ServiceImpl;
import utils.HttpUtils;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.*;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static swing.LoginFrame.SOFT_VERSION;

/**
 * @author kingfans
 */
public class CommunityFrame extends JFrame {

    private JComboBox communityChooser;
    private JPasswordField passwordText;
    private JButton button;

    private JLayeredPane layeredPane;
    private JPanel backgroundPanel;
    private JLabel backgroundLabel;
    ImageIcon image;

    public CommunityFrame() {
        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Microsoft YaHei UI", Font.BOLD, 12)));
        UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("Microsoft YaHei UI", Font.BOLD, 13)));
        initComponents();

        this.setLayeredPane(layeredPane);
        this.setSize(image.getIconWidth(), image.getIconHeight());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(image.getIconWidth(), image.getIconHeight());
        this.setVisible(true);
        Image icon = Toolkit.getDefaultToolkit().getImage("resources/dk_logo.png");
        this.setIconImage(icon);
        this.setTitle("得康充值系统 v" + SOFT_VERSION);
        this.getRootPane().setDefaultButton(button);
        this.setLocationRelativeTo(null);
    }


    private void buttonActionPerformed(ActionEvent e) {
//        if (communityChooser.getSelectedIndex() == 0) {
//            JOptionPane.showMessageDialog(null, "请选择小区！");
//        } else {
//            String community = (String) communityChooser.getSelectedItem();
//            Map<String, String> map = new HashMap<>(1);
//            try {
//                map.put("community", URLEncoder.encode(community, "utf-8"));
//            } catch (UnsupportedEncodingException e1) {
//                e1.printStackTrace();
//            }
//            String response = HttpUtils.toServlet(map, "community","getCommunityPassword");
//            JSONObject jsonObject = JSONObject.parseObject(response);
//            String query_password = jsonObject.getString("password");
//            char[] passwd = passwordText.getPassword();
//            String password_input = String.valueOf(passwd);
//            if (password_input.equals(query_password)) {
//                ServiceImpl.getInstance().updateAllCommunity(community);
//                new LoginFrame();
//                this.dispose();
//            } else {
//                JOptionPane.showMessageDialog(null, "小区密码错误！");
//                passwordText.setText("");
//                passwordText.requestFocus();
//            }
//        }

    }


    private void communityWindowClosing(WindowEvent e) {
        System.exit(0);
    }


    private void initComponents() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                communityWindowClosing(e);
            }
        });

        layeredPane = new JLayeredPane();
        image = new ImageIcon("resources/login_bg.png");
        backgroundPanel = new JPanel();
        backgroundPanel.setBounds(0, 0, image.getIconWidth(), image.getIconHeight());

        backgroundLabel = new JLabel(image);
        backgroundPanel.add(backgroundLabel);

        layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);


//        communityChooser = new JComboBox();
//        String resp = HttpUtils.toServlet(null, "community", "getCommunities");
//        JSONObject jsonObject = JSONObject.parseObject(resp);
//        String communities;
//        ArrayList<String> arrayList = null;
//        try {
//            communities = URLDecoder.decode(jsonObject.getString("community"), "utf-8");
//            List<String> lis = Arrays.asList(communities.split(","));
//            arrayList = new ArrayList(lis);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        arrayList.add(0, "请选择相应小区");
//        String[] communityArray = arrayList.toArray(new String[0]);
//        communityChooser.setModel(new DefaultComboBoxModel<>(communityArray));
//        layeredPane.add(communityChooser, JLayeredPane.MODAL_LAYER);
//        communityChooser.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
//        communityChooser.setBounds(185, 145, 155, 30);
//        communityChooser.setOpaque(false);
//        communityChooser.setUI(new BasicComboBoxUI() {
//            @Override
//            public void installUI(JComponent comboBox) {
//                super.installUI(comboBox);
//                listBox.setOpaque(false);
//            }
//
//            @Override
//            protected JButton createArrowButton() {
//                return super.createArrowButton();
//            }
//        });

        JLabel passwordLabel = new JLabel();
        passwordLabel.setText("密码   ");
        passwordLabel.setForeground(new Color(91, 91, 91));
        passwordLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
        layeredPane.add(passwordLabel, JLayeredPane.MODAL_LAYER);
        passwordLabel.setBounds(185, 190, 78, passwordLabel.getPreferredSize().height);


        JLabel titleLabel = new JLabel();
        titleLabel.setText("欢迎使用得康充值系统");
        titleLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 22));
        layeredPane.add(titleLabel, JLayeredPane.MODAL_LAYER);
        titleLabel.setBounds(150, 70, 250, 30);

        passwordText = new JPasswordField();
        passwordText.setForeground(Color.darkGray);
        passwordText.setOpaque(false);
        layeredPane.add(passwordText, JLayeredPane.MODAL_LAYER);
        passwordText.setBounds(225, 185, 115, 30);


        ImageIcon icon = new ImageIcon("resources/button_bg.png");
        Image img = icon.getImage().getScaledInstance(155, 35, Image.SCALE_FAST);
        button = new JButton();
        button.setText("进入");
        button.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
        button.setForeground(Color.white);
        button.setIcon(new ImageIcon(img));
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setOpaque(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorder(null);
        button.addActionListener(this::buttonActionPerformed);
        layeredPane.add(button, JLayeredPane.MODAL_LAYER);
        button.setBounds(185, 230, 155, 35);


        JLabel url = new JLabel();
        url.setText("杭州得康  www.hzdk.com");
        url.setFont(new Font("Dialog", Font.PLAIN, 12));
        url.setForeground(Color.darkGray);
        url.setBounds(200, 340, url.getPreferredSize().width, url.getPreferredSize().height);
        layeredPane.add(url, JLayeredPane.MODAL_LAYER);

    }

}
