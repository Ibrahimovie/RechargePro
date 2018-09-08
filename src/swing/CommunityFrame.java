package swing;

import bean.manager.PortManager;
import com.alibaba.fastjson.JSONObject;
import exception.NoSuchPort;
import exception.NotASerialPort;
import exception.PortInUse;
import gnu.io.UnsupportedCommOperationException;
import serial.SerialPortUtils;
import service.impl.ServiceImpl;
import utils.HttpUtils;
import utils.Utils;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
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

/**
 * @author kingfans
 */
public class CommunityFrame extends JFrame {

    private JComboBox communityChooser;
    private JPasswordField passwordText;
    private JButton button;


    public CommunityFrame() {
        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Microsoft YaHei UI", Font.BOLD, 12)));
        UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("Microsoft YaHei UI", Font.BOLD, 13)));
        initComponents();
        this.setSize(new Dimension(535, 405));
        Image icon = Toolkit.getDefaultToolkit().getImage("resources/dk_logo.png");
        this.setIconImage(icon);
        this.setTitle("得康充值系统 v1.5");
        this.setLocationRelativeTo(null);
        this.getRootPane().setDefaultButton(button);
        this.setVisible(true);
    }


    private void buttonActionPerformed(ActionEvent e) {
        if (communityChooser.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(null, "请选择小区！");
        } else {
            String community = (String) communityChooser.getSelectedItem();
            Map<String, String> map = new HashMap<>(2);
            map.put("action", "getCommunityPassword");
            try {
                map.put("community", URLEncoder.encode(community, "utf-8"));
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            String response = HttpUtils.toServlet(map, "CommunityServlet");
            JSONObject jsonObject = JSONObject.parseObject(response);
            String query_password = jsonObject.getString("password");
            System.out.println("community password === " + query_password);
            char[] passwd = passwordText.getPassword();
            String password_input = String.valueOf(passwd);
            if (password_input.equals(query_password)) {
                ServiceImpl.getInstance().updateAllCommunity(community);
                System.out.println("小区密码正确！");
                new LoginFrame();
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(null, "小区密码错误！");
            }
        }

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
        Container contentPane = this.getContentPane();
        contentPane.setLayout(null);

        JLabel userLabel = new JLabel();
        userLabel.setText("小区 : ");
        userLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        contentPane.add(userLabel);
        userLabel.setBounds(170, 150, 78, userLabel.getPreferredSize().height);

        communityChooser = new JComboBox();
        Map<String, String> map = new HashMap<>(1);
        map.put("action", "getCommunities");
        String resp = HttpUtils.toServlet(map, "CommunityServlet");
        JSONObject jsonObject = JSONObject.parseObject(resp);
        String communities;
        ArrayList<String> arrayList = null;
        try {
            communities = URLDecoder.decode(jsonObject.getString("community"), "utf-8");
            List<String> lis = Arrays.asList(communities.split(","));
            arrayList = new ArrayList(lis);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        arrayList.add(0, "请选择");
        String[] communityArray = arrayList.toArray(new String[0]);
        communityChooser.setModel(new DefaultComboBoxModel<>(communityArray));
        contentPane.add(communityChooser);
        communityChooser.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        communityChooser.setBounds(235, 145, 105, 30);

        JLabel passwordLabel = new JLabel();
        passwordLabel.setText("密码 : ");
        passwordLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        contentPane.add(passwordLabel);
        passwordLabel.setBounds(170, 190, 78, passwordLabel.getPreferredSize().height);


        JLabel titleLabel = new JLabel();
        titleLabel.setText("欢迎使用得康充值系统");
        titleLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 22));
        contentPane.add(titleLabel);
        titleLabel.setBounds(150, 70, 250, 30);

        passwordText = new JPasswordField();
        contentPane.add(passwordText);
        passwordText.setBounds(235, 185, 105, 30);

        button = new JButton();
        button.setText("进入");
        button.setFont(new Font("Dialog", Font.PLAIN, 12));
        button.addActionListener(this::buttonActionPerformed);
        contentPane.add(button);
        button.setBounds(170, 230, 170, 35);
        button.setBackground(new Color(180, 205, 205));
        button.setBorder(BorderFactory.createRaisedBevelBorder());

        JLabel url = new JLabel();
        url.setText("杭州得康  www.hzdk.com");
        url.setFont(new Font("Dialog", Font.PLAIN, 12));
        url.setForeground(Color.darkGray);
        url.setBounds(200, 340, url.getPreferredSize().width, url.getPreferredSize().height);
        contentPane.add(url);

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
