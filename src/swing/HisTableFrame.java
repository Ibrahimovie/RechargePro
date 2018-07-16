package swing;

import com.eltima.components.ui.DatePicker;
import excel.ExcelUtils;
import org.apache.poi.ss.usermodel.Workbook;
import service.impl.ServiceImpl;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author kingfans
 */
public class HisTableFrame extends JFrame {
    private RechargeFrame rechargeFrame;
    private int count;
    private List<Map<String, Object>> his;

    public HisTableFrame(int count, List<Map<String, Object>> his, RechargeFrame rechargeFrame) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }
        this.count = count;
        this.his = his;
        this.rechargeFrame = rechargeFrame;
        initComponents(count, his);
        this.setSize(1000, 680);
        this.setTitle("充值记录");
        Image icon = Toolkit.getDefaultToolkit().getImage("resources/dk_logo.png");
        this.setIconImage(icon);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void hisWindowClosing(WindowEvent e) {
        rechargeFrame.setEnabled(true);
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e2) {
            e2.printStackTrace();
        }
    }

    private void initComponents(int n, List<Map<String, Object>> history) {
        DecimalFormat df = new DecimalFormat("0.0");
        JScrollPane scrollPane1 = new JScrollPane();
        String[] columnNames = {"卡号", "手机号", "姓名", "卡类型", "充值时间", "充值金额", "余额", "有效天数", "充电时间", "扣款费率", "最大功率"};
        Object[][] obj = new Object[n][11];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < 11; j++) {
                switch (j) {
                    case 0:
                        String cardNumber = String.valueOf(history.get(i).get("card_number"));
                        obj[i][j] = cardNumber;
                        break;
                    case 1:
                        String phone = String.valueOf(history.get(i).get("phone"));
                        obj[i][j] = phone;
                        break;
                    case 2:
                        String username = String.valueOf(history.get(i).get("username"));
                        obj[i][j] = username;
                        break;
                    case 3:
                        int cardType = (int) history.get(i).get("card_type");
                        switch (cardType) {
                            case 0:
                                obj[i][j] = "Q10卡";
                                break;
                            case 1:
                                obj[i][j] = "Q20电子钱包A";
                                break;
                            case 2:
                                obj[i][j] = "Q20电子钱包B";
                                break;
                            case 3:
                                obj[i][j] = "Q20包月卡";
                                break;
                            default:
                                break;
                        }
                        break;
                    case 4:
                        String nowTime = String.valueOf(history.get(i).get("now_time"));
                        obj[i][j] = nowTime;
                        break;
                    case 5:
                        int topUp = (int) history.get(i).get("top_up");
                        obj[i][j] = df.format(topUp / 10.0f);
                        break;
                    case 6:
                        int balance = (int) history.get(i).get("balance");
                        obj[i][j] = df.format(balance / 10.0f);
                        break;
                    case 7:
                        String validDay = String.valueOf(history.get(i).get("valid_day"));
                        obj[i][j] = validDay;
                        break;
                    case 8:
                        int rechargeTime = (int) history.get(i).get("recharge_time");
                        obj[i][j] = df.format(rechargeTime / 10.0f);
                        break;
                    case 9:
                        int payRate = (int) history.get(i).get("pay_rate");
                        obj[i][j] = df.format(payRate / 10.0f);
                        break;
                    case 10:
                        int powerRate = (int) history.get(i).get("power_rate");
                        obj[i][j] = df.format(powerRate / 100.0f);
                        break;
                    default:
                        break;

                }
            }
        }
//        JTable table1 = new JTable(obj, columnNames);
        MyTable table1 = new MyTable(obj,columnNames);

        TableColumn column;
        int columns = table1.getColumnCount();
        for (int i = 0; i < columns; i++) {
            column = table1.getColumnModel().getColumn(i);
            if (i == 4) {
                column.setPreferredWidth(600);
            } else if (i == 7 || i == 8 || i == 9 || i == 10) {
                column.setPreferredWidth(200);
            } else {
                column.setPreferredWidth(300);
            }
        }

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                hisWindowClosing(e);
            }
        });

        Container contentPane = this.getContentPane();
        contentPane.setLayout(null);
        scrollPane1.setViewportView(table1);
        contentPane.add(scrollPane1);

        scrollPane1.setBounds(10, 40, 965, 590);

        JLabel startTimeLabel = new JLabel();
        startTimeLabel.setText("开始时间 : ");
        startTimeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        contentPane.add(startTimeLabel);
        startTimeLabel.setBounds(10, 12, 70, startTimeLabel.getPreferredSize().height);
        final DatePicker startTime = getDatePicker(72);
        contentPane.add(startTime);

        JLabel endTimeLabel = new JLabel();
        endTimeLabel.setText("结束时间 : ");
        endTimeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        contentPane.add(endTimeLabel);
        endTimeLabel.setBounds(172, 12, 70, endTimeLabel.getPreferredSize().height);
        final DatePicker endTime = getDatePicker(234);
        contentPane.add(endTime);

        JLabel phoneLabel = new JLabel();
        phoneLabel.setText("手机号 : ");
        phoneLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        contentPane.add(phoneLabel);
        phoneLabel.setBounds(345, 12, 60, phoneLabel.getPreferredSize().height);

        JTextField phoneText = new JTextField();
        contentPane.add(phoneText);
        phoneText.setBounds(400, 12, 100, phoneText.getPreferredSize().height);

        JButton queryButton = new JButton("筛选");
        queryButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        contentPane.add(queryButton);
        queryButton.setBounds(540, 8, 60, queryButton.getPreferredSize().height);
        queryButton.setBackground(new Color(180, 205, 205));
        queryButton.setBorder(BorderFactory.createRaisedBevelBorder());

        queryButton.addActionListener(e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
            Date date1 = (Date) startTime.getValue();
            String startDate = sdf.format(date1) + " 00:00:00";
            Date date2 = (Date) endTime.getValue();
            String endDate = sdf.format(date2) + " 23:59:59";
            String phone = phoneText.getText();
            if (date2.after(date1) || date1 == date2) {
                if (phone == null || "".equals(phone)) {
                    count = ServiceImpl.getInstance().getRechargeCountRangeWithoutPhone(startDate, endDate);
                    his = ServiceImpl.getInstance().getRechargeHisRangeWithoutPhone(startDate, endDate);
                } else {
                    count = ServiceImpl.getInstance().getRechargeCountRangeWithPhone(startDate, endDate, phone);
                    his = ServiceImpl.getInstance().getRechargeHisRangeWithPhone(startDate, endDate, phone);
                }
                new HisTableFrame(count, his, rechargeFrame);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(null, "请输入正确的时间范围！");
            }
        });

        JButton exportButton = new JButton("导出");
        exportButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        contentPane.add(exportButton);
        exportButton.setBounds(620, 8, 60, exportButton.getPreferredSize().height);
        exportButton.setBackground(new Color(180, 205, 205));
        exportButton.setBorder(BorderFactory.createRaisedBevelBorder());
        exportButton.addActionListener(e -> {
            SimpleDateFormat sdf2 = new SimpleDateFormat("YYYYMMddHHmmss");
            String[] title = new String[]{"卡号", "手机号", "姓名", "卡类型", "充值时间", "充值金额", "余额", "有效天数", "充电时间", "扣款费率", "最大功率"};
            String fileName = "充值记录表" + sdf2.format(new Date()) + ".xls";
            String sheetName = "充值记录表";
            String[][] objects = new String[n][11];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < 11; j++) {
                    switch (j) {
                        case 0:
                            String cardNumber = String.valueOf(history.get(i).get("card_number"));
                            objects[i][j] = cardNumber;
                            break;
                        case 1:
                            String phone = String.valueOf(history.get(i).get("phone"));
                            objects[i][j] = phone;
                            break;
                        case 2:
                            String username = String.valueOf(history.get(i).get("username"));
                            objects[i][j] = username;
                            break;
                        case 3:
                            int cardType = (int) history.get(i).get("card_type");
                            switch (cardType) {
                                case 0:
                                    objects[i][j] = "Q10卡";
                                    break;
                                case 1:
                                    objects[i][j] = "Q20电子钱包A";
                                    break;
                                case 2:
                                    objects[i][j] = "Q20电子钱包B";
                                    break;
                                case 3:
                                    objects[i][j] = "Q20包月卡";
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case 4:
                            String nowTime = String.valueOf(history.get(i).get("now_time"));
                            objects[i][j] = nowTime;
                            break;
                        case 5:
                            int topUp = (int) history.get(i).get("top_up");
                            objects[i][j] = df.format(topUp / 10.0f);
                            break;
                        case 6:
                            int balance = (int) history.get(i).get("balance");
                            objects[i][j] = df.format(balance / 10.0f);
                            break;
                        case 7:
                            String validDay = String.valueOf(history.get(i).get("valid_day"));
                            objects[i][j] = validDay;
                            break;
                        case 8:
                            int rechargeTime = (int) history.get(i).get("recharge_time");
                            objects[i][j] = df.format(rechargeTime / 10.0f);
                            break;
                        case 9:
                            int payRate = (int) history.get(i).get("pay_rate");
                            objects[i][j] = df.format(payRate / 10.0f);
                            break;
                        case 10:
                            int powerRate = (int) history.get(i).get("power_rate");
                            objects[i][j] = df.format(powerRate / 100.0f);
                            break;
                        default:
                            break;
                    }
                }
            }
            Workbook wb = ExcelUtils.getHSSFWorkbook(sheetName, title, objects, null);
            JFileChooser chooser = new JFileChooser("选择保存路径");
            chooser.setApproveButtonText("确定");
            chooser.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
            chooser.setDialogTitle("选择保存路径");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.showOpenDialog(null);
            String path = chooser.getSelectedFile().getPath();
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(path + "\\" + fileName);
                wb.write(fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        Dimension preferredSize = new Dimension();
        for (int i2 = 0; i2 < contentPane.getComponentCount(); i2++) {
            Rectangle bounds = contentPane.getComponent(i2).getBounds();
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

    private static DatePicker getDatePicker(int location) {
        String format = "yyyy-MM-dd";
        Date date = new Date();
        Font font = new Font("Times New Roman", Font.PLAIN, 12);
        Dimension dimension = new Dimension(90, 24);
        DatePicker datePicker = new DatePicker(date, format, font, dimension);
        datePicker.setLocation(location, 10);
        datePicker.setLocale(Locale.CHINA);
        datePicker.setTimePanleVisible(false);
        return datePicker;
    }
}
