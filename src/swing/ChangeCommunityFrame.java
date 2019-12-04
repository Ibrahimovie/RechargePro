package swing;

import bean.User;
import bean.manager.FrameManager;
import com.alibaba.fastjson.JSONObject;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import service.impl.ServiceImpl;
import utils.HttpUtils;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.List;

/**
 * @author kingfans
 */
public class ChangeCommunityFrame extends JFrame {
    private User user;
    private JComboBox<String> communityChooser;
    private JButton button1;
    private JPasswordField passwordText;
    private RechargeFrame frame;

    public ChangeCommunityFrame(User user) {
        initComponents(user);
        this.frame = FrameManager.getFrameByName("recharge");
        this.user = user;
        this.setSize(400, 300);
        this.setTitle("小区管理");
        this.getRootPane().setDefaultButton(button1);
        Image icon = Toolkit.getDefaultToolkit().getImage("resources/dk_logo.png");
        this.setIconImage(icon);
        this.setLocationRelativeTo(null);
    }

    private void confirmButtonActionPerformed(ActionEvent e) {
//        if (communityChooser.getSelectedIndex() != 0) {
//            String community = (String) communityChooser.getSelectedItem();
//            char[] passwd = passwordText.getPassword();
//            String password = String.valueOf(passwd);
//            if (password != null && !"".equals(password)) {
//                Map<String, String> map = new HashMap<>(1);
//                try {
//                    map.put("community", URLEncoder.encode(community, "utf-8"));
//                } catch (UnsupportedEncodingException e1) {
//                    e1.printStackTrace();
//                }
//                String response = HttpUtils.toServlet(map, "community", "getCommunityPassword");
//                JSONObject jsonObject = JSONObject.parseObject(response);
//                String query_password = jsonObject.getString("password");
//                if (query_password.equals(password)) {
//                    //切换小区成功
//                    user.setCommunity(community);
//                    ServiceImpl.getInstance().updateAllCommunity(community);
//                    frame.comunityLabel.setText("当前小区 : " + user.getCommunity());
//                    JOptionPane.showMessageDialog(null, "已切换到小区 : " + user.getCommunity(), "提示", JOptionPane.INFORMATION_MESSAGE);
//                    this.dispose();
//                } else {
//                    JOptionPane.showMessageDialog(null, "密码错误！", "提示", JOptionPane.WARNING_MESSAGE);
//                    passwordText.setText("");
//                    passwordText.requestFocus();
//                }
//
//            } else {
//                JOptionPane.showMessageDialog(null, "请输入密码！", "提示", JOptionPane.WARNING_MESSAGE);
//            }
//
//
//        } else {
//            JOptionPane.showMessageDialog(null, "请选择小区！", "提示", JOptionPane.WARNING_MESSAGE);
//            passwordText.setText("");
//        }

    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        this.dispose();
    }

    private void initComponents(User user) {

        Container contentPane = this.getContentPane();
        contentPane.setLayout(null);

        JLabel communityLabel = new JLabel();
        communityLabel.setText("当前小区 : ");
        communityLabel.setHorizontalAlignment(SwingConstants.LEFT);
        communityLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
        contentPane.add(communityLabel);
        communityLabel.setBounds(80, 30, 100, 30);

        JLabel commnityText = new JLabel();
        commnityText.setText(user.getCommunity());
        commnityText.setForeground(Color.red);
        commnityText.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
        contentPane.add(commnityText);
        commnityText.setBounds(170, 30, 200, 30);


        JLabel changeLabel = new JLabel();
        changeLabel.setText("切换小区 : ");
        changeLabel.setHorizontalAlignment(SwingConstants.LEFT);
        changeLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
        contentPane.add(changeLabel);
        changeLabel.setBounds(80, 80, 100, 30);


//        String resp = HttpUtils.toServlet(null, "community", "getCommunities");
//        JSONObject jsonObject = JSONObject.parseObject(resp);
//        String communities;
//        ArrayList<String> arrayList = null;
//        try {
//            communities = URLDecoder.decode(jsonObject.getString("community"), "utf-8");
//            List<String> list = Arrays.asList(communities.split(","));
//            arrayList = new ArrayList(list);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        arrayList.add(0, "请选择相应小区");
//        String[] communityArray = arrayList.toArray(new String[0]);
//        communityChooser = new JComboBox<>();
//        communityChooser.setModel(new DefaultComboBoxModel<>(communityArray));
//        contentPane.add(communityChooser, JLayeredPane.MODAL_LAYER);
//        communityChooser.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
//        communityChooser.setBounds(170, 80, 130, 30);
//        communityChooser.setOpaque(false);

        JLabel passwordLabel = new JLabel();
        passwordLabel.setText("密       码 : ");
        passwordLabel.setHorizontalAlignment(SwingConstants.LEFT);
        passwordLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
        contentPane.add(passwordLabel);
        passwordLabel.setBounds(80, 130, 100, 30);

        passwordText = new JPasswordField();
        contentPane.add(passwordText);
        passwordText.setBounds(170, 130, 130, 30);


        button1 = new JButton();
        button1.setText("确定");
        button1.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        button1.setBackground(new Color(180, 205, 205));
        button1.setBorder(BorderFactory.createRaisedBevelBorder());
        button1.addActionListener(this::confirmButtonActionPerformed);
        contentPane.add(button1);
        button1.setBounds(80, 190, 100, 30);

        JButton button2 = new JButton();
        button2.setText("取消");
        button2.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        button2.setBackground(new Color(180, 205, 205));
        button2.setBorder(BorderFactory.createRaisedBevelBorder());
        button2.addActionListener(this::cancelButtonActionPerformed);
        contentPane.add(button2);
        button2.setBounds(200, 190, 100, 30);

        Dimension preferredSize = new Dimension();
        for (int i = 0; i < contentPane.getComponentCount(); i++) {
            final Rectangle bounds = contentPane.getComponent(i).getBounds();
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
