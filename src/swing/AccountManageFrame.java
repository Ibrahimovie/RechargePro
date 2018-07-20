package swing;

import bean.User;
import jdk.nashorn.internal.scripts.JO;
import service.impl.ServiceImpl;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * @author kingfans
 */
public class AccountManageFrame extends JFrame {
    private User user;
    private int n;
    private List<Map<String, Object>> list;
    private MyTable table1;

    public AccountManageFrame(User user, int n, List<Map<String, Object>> list) {
        initComponents(user, n, list);
        this.user = user;
        this.n = n;
        this.list = list;
        this.setSize(415, 356);
        this.setTitle("多账号管理");
        Image icon = Toolkit.getDefaultToolkit().getImage("resources/dk_logo.png");
        this.setIconImage(icon);
        this.setLocationRelativeTo(null);
    }

    private void initComponents(User user, int count, List<Map<String, Object>> his) {

        Container contentPane = this.getContentPane();
        contentPane.setLayout(null);

        JButton addButton = new JButton("添加");
        addButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        contentPane.add(addButton);
        addButton.setBounds(10, 10, 60, addButton.getPreferredSize().height);
        addButton.setBackground(new Color(204, 204, 204));
        addButton.setBorder(BorderFactory.createRaisedBevelBorder());
        addButton.addActionListener(e -> new AddAccountFrame(user).setVisible(true));

        JButton editButton = new JButton("编辑");
        editButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        contentPane.add(editButton);
        editButton.setBounds(80, 10, 60, editButton.getPreferredSize().height);
        editButton.setBackground(new Color(204, 204, 204));
        editButton.setBorder(BorderFactory.createRaisedBevelBorder());
        editButton.addActionListener(e -> {
            int selected = table1.getSelectedRow();
            if (selected == -1) {
                JOptionPane.showMessageDialog(null, "请选择要编辑的账号！");
            } else {
                String username = (String) table1.getValueAt(selected, 1);
                String password = (String) table1.getValueAt(selected, 2);
                System.out.println(username + "======" + password);
                new EditUserFrame(user, username, password, this).setVisible(true);
                this.setEnabled(false);
            }
        });


        JButton delButton = new JButton("删除");
        delButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        contentPane.add(delButton);
        delButton.setBounds(150, 10, 60, delButton.getPreferredSize().height);
        delButton.setBackground(new Color(204, 204, 204));
        delButton.setBorder(BorderFactory.createRaisedBevelBorder());
        delButton.addActionListener(e -> {
            int selected = table1.getSelectedRow();
            if (selected == -1) {
                JOptionPane.showMessageDialog(null, "请选择要删除的账号！");
            } else {
                String username = (String) table1.getValueAt(selected, 1);
                int n = JOptionPane.showConfirmDialog(null, "确定删除账号 " + username + " ？", "", JOptionPane.OK_CANCEL_OPTION);
                if (0 == n) {
                    ServiceImpl.getInstance().deleteUser(username);
                    JOptionPane.showMessageDialog(null, "删除账号 " + username + " 成功!");
                }
            }
        });

        JButton refreshButton = new JButton("刷新");
        refreshButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        contentPane.add(refreshButton);
        refreshButton.setBounds(220, 10, 60, refreshButton.getPreferredSize().height);
        refreshButton.setBackground(new Color(204, 204, 204));
        refreshButton.setBorder(BorderFactory.createRaisedBevelBorder());
        refreshButton.addActionListener(e -> {
            n = ServiceImpl.getInstance().getSubAccountsNum();
            list = ServiceImpl.getInstance().getSubAccountsInfo();
            new AccountManageFrame(user, n, list).setVisible(true);
            this.dispose();
        });

        JScrollPane scrollPane1 = new JScrollPane();
        String[] columnNames = {"序号", "用户名", "密码"};
        Object[][] obj = new Object[count][3];
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < 3; j++) {
                switch (j) {
                    case 0:
                        String number = String.valueOf(i + 1);
                        obj[i][j] = number;
                        break;
                    case 1:
                        String username = String.valueOf(his.get(i).get("username"));
                        obj[i][j] = username;
                        break;
                    case 2:
//                        String password = String.valueOf(his.get(i).get("password"));
                        String password = "***";
                        obj[i][j] = password;
                        break;
                    default:
                        break;

                }
            }
        }
        table1 = new MyTable(obj, columnNames);
        TableColumn column;
        int columns = table1.getColumnCount();
        for (int i = 0; i < columns; i++) {
            column = table1.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(200);
            } else {
                column.setPreferredWidth(365);
            }
        }

        scrollPane1.setViewportView(table1);
        contentPane.add(scrollPane1);

        scrollPane1.setBounds(10, 50, 380, 260);


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
