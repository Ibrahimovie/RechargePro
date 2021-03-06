package swing;

import bean.User;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eltima.components.ui.DatePicker;
import excel.ExcelUtils;
import org.apache.poi.ss.usermodel.Workbook;
import service.impl.ServiceImpl;
import utils.HttpUtils;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * @author kingfans
 */
public class HisTableFrame extends JFrame {
    private RechargeFrame rechargeFrame;
    private int count;
    private List<Map<String, Object>> his;
    private User user;
    private int totalAmount;
    private String dateStart;
    private String dateEnd;

    public HisTableFrame(User user, int count, List<Map<String, Object>> his, RechargeFrame rechargeFrame, int totalAmount, String dateStart, String dateEnd) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }
        this.user = user;
        this.count = count;
        this.his = his;
        this.rechargeFrame = rechargeFrame;
        this.totalAmount = totalAmount;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
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
        String[] columnNames = {"操作工号", "卡号", "手机号", "姓名", "卡类型", "充值时间", "充值金额", "余额", "有效天数", "充电时间", "扣款费率", "最大功率"};
        Object[][] obj = new Object[n][12];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < 12; j++) {
                switch (j) {
                    case 0:
                        String operator = String.valueOf(history.get(i).get("operator"));
                        obj[i][j] = operator;
                        break;
                    case 1:
                        String cardNumber = String.valueOf(history.get(i).get("card_number"));
                        obj[i][j] = cardNumber;
                        break;
                    case 2:
                        String phone = String.valueOf(history.get(i).get("phone"));
                        obj[i][j] = phone;
                        break;
                    case 3:
                        String username = String.valueOf(history.get(i).get("username"));
                        obj[i][j] = username;
                        break;
                    case 4:
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
                    case 5:
                        String nowTime = String.valueOf(history.get(i).get("now_time"));
                        obj[i][j] = nowTime;
                        break;
                    case 6:
                        int topUp = (int) history.get(i).get("top_up");
                        obj[i][j] = df.format(topUp / 10.0f);
                        break;
                    case 7:
                        int balance = (int) history.get(i).get("balance");
                        obj[i][j] = df.format(balance / 10.0f);
                        break;
                    case 8:
                        String validDay = String.valueOf(history.get(i).get("valid_day"));
                        obj[i][j] = validDay;
                        break;
                    case 9:
                        int rechargeTime = (int) history.get(i).get("recharge_time");
                        obj[i][j] = df.format(rechargeTime / 10.0f);
                        break;
                    case 10:
                        int payRate = (int) history.get(i).get("pay_rate");
                        obj[i][j] = df.format(payRate / 10.0f);
                        break;
                    case 11:
                        int powerRate = (int) history.get(i).get("power_rate");
                        obj[i][j] = df.format(powerRate / 100.0f);
                        break;
                    default:
                        break;

                }
            }
        }
        MyTable table1 = new MyTable(obj, columnNames);

        TableColumn column;
        int columns = table1.getColumnCount();
        for (int i = 0; i < columns; i++) {
            column = table1.getColumnModel().getColumn(i);
            if (i == 5) {
                column.setPreferredWidth(560);
            } else if (i == 8 || i == 9 || i == 10 || i == 11) {
                column.setPreferredWidth(230);
            } else {
                column.setPreferredWidth(280);
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
        final DatePicker startTime = getDatePicker(72, dateStart);
        contentPane.add(startTime);

        JLabel endTimeLabel = new JLabel();
        endTimeLabel.setText("结束时间 : ");
        endTimeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        contentPane.add(endTimeLabel);
        endTimeLabel.setBounds(172, 12, 70, endTimeLabel.getPreferredSize().height);
        final DatePicker endTime = getDatePicker(234, dateEnd);
        contentPane.add(endTime);

        JLabel operatorLabel = new JLabel();
        operatorLabel.setText("操作工号 : ");
        operatorLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        contentPane.add(operatorLabel);
        operatorLabel.setBounds(345, 12, 70, operatorLabel.getPreferredSize().height);

        JComboBox operatorChooser = new JComboBox();
        ArrayList<String> operators = ServiceImpl.getInstance().getSubUsername();
        operators.add(0, "默认");
        String[] operatorArray = operators.toArray(new String[0]);
        operatorChooser.setModel(new DefaultComboBoxModel<>(operatorArray));
        contentPane.add(operatorChooser);
        operatorChooser.setBounds(410, 12, 70, operatorChooser.getPreferredSize().height);


        JLabel phoneLabel = new JLabel();
        phoneLabel.setText("手机号 : ");
        phoneLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        contentPane.add(phoneLabel);
        phoneLabel.setBounds(500, 12, 60, phoneLabel.getPreferredSize().height);

        JTextField phoneText = new JTextField();
        phoneText.setEditable(false);
        contentPane.add(phoneText);
        phoneText.setBounds(555, 12, 100, phoneText.getPreferredSize().height);

        JButton queryButton = new JButton("筛选");
        queryButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        contentPane.add(queryButton);
        queryButton.setBounds(680, 8, 60, queryButton.getPreferredSize().height);
        queryButton.setBackground(new Color(180, 205, 205));
        queryButton.setBorder(BorderFactory.createRaisedBevelBorder());

        queryButton.addActionListener(e -> {
            int operatorIndex = operatorChooser.getSelectedIndex();
            String operator = null;
            if (operatorIndex != 0) {
                operator = (String) operatorChooser.getSelectedItem();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = (Date) startTime.getValue();
            String startDate = sdf.format(date1) + " 00:00:00";
            Date date2 = (Date) endTime.getValue();
            String endDate = sdf.format(date2) + " 23:59:59";
            String phone = phoneText.getText();
            if (date2.after(date1) || date1 == date2) {
                if (phone == null || "".equals(phone)) {
                    Map<String, String> map = new HashMap<>(3);
                    map.put("start_time", startDate);
                    map.put("end_time", endDate);
                    try {
                        if (null != operator) {
                            map.put("operator", URLEncoder.encode(operator, "utf-8"));
                        }
                        map.put("community", URLEncoder.encode(user.getCommunity(), "utf-8"));
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }

                    count = ServiceImpl.getInstance().getRechargeCountRangeWithoutPhone(startDate, endDate, operator);
                    his = ServiceImpl.getInstance().getRechargeHisRangeWithoutPhone(startDate, endDate, operator);
                    totalAmount = 0;
                    for (int i = 0; i < his.size(); i++) {
                        Map<String, Object> map1 = his.get(i);
                        int a = (int) map1.get("top_up");
                        totalAmount += a;
                    }


//                    String rechargeAllJson = HttpUtils.toServlet(map, "recharge", "getRechargeHisWithoutPhone");
//                    JSONObject jsonObject = JSONObject.parseObject(rechargeAllJson);
//                    try {
//                        String hist = URLDecoder.decode(jsonObject.getString("recharge_his"), "utf-8");
//                        JSONObject object = JSONObject.parseObject(hist);
//                        JSONArray jsonArray = (JSONArray) object.get("his");
//                        count = object.getInteger("count");
//                        his = new ArrayList<>();
//                        totalAmount = 0;
//                        for (int i = 0; i < jsonArray.size(); i++) {
//                            Map<String, Object> map1 = (Map<String, Object>) jsonArray.get(i);
//                            int a = (int) map1.get("top_up");
//                            totalAmount += a;
//                            his.add(map1);
//                        }
//                    } catch (UnsupportedEncodingException e1) {
//                        e1.printStackTrace();
//                    }
                } else {
                    //进不到这里面
//                    Map<String, String> map = new HashMap<>(4);
//                    map.put("start_time", startDate);
//                    map.put("end_time", endDate);
//                    map.put("phone", phone);
//                    try {
//                        if (null != operator) {
//                            map.put("operator", URLEncoder.encode(operator, "utf-8"));
//                        }
//                        map.put("community", URLEncoder.encode(user.getCommunity(), "utf-8"));
//                    } catch (UnsupportedEncodingException e1) {
//                        e1.printStackTrace();
//                    }
////                    String rechargeAllJson = HttpUtils.toServlet(map, "recharge", "getRechargeHisWithPhone");
////                    JSONObject jsonObject = JSONObject.parseObject(rechargeAllJson);
//
//                    try {
//                        String hist = URLDecoder.decode(jsonObject.getString("recharge_his"), "utf-8");
//                        JSONObject object = JSONObject.parseObject(hist);
//                        JSONArray jsonArray = (JSONArray) object.get("his");
//                        count = object.getInteger("count");
//                        his = new ArrayList<>();
//                        totalAmount = 0;
//                        for (int i = 0; i < jsonArray.size(); i++) {
//                            Map<String, Object> map1 = (Map<String, Object>) jsonArray.get(i);
//                            int topUp = (int) map1.get("top_up");
//                            totalAmount += topUp;
//                            his.add(map1);
//                        }
//                    } catch (UnsupportedEncodingException e1) {
//                        e1.printStackTrace();
//                    }
                }
                new HisTableFrame(user, count, his, rechargeFrame, totalAmount, sdf.format(date1), sdf.format(date2));
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(null, "请输入正确的时间范围！");
            }
        });

        JButton exportButton = new JButton("导出");
        exportButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        contentPane.add(exportButton);
        exportButton.setBounds(755, 8, 60, exportButton.getPreferredSize().height);
        exportButton.setBackground(new Color(180, 205, 205));
        exportButton.setBorder(BorderFactory.createRaisedBevelBorder());
        exportButton.addActionListener(e -> {
            SimpleDateFormat sdf2 = new SimpleDateFormat("YYYYMMddHHmmss");
            String[] title = new String[]{"操作工号", "卡号", "手机号", "姓名", "卡类型", "充值时间", "充值金额", "余额", "有效天数", "充电时间", "扣款费率", "最大功率"};
            String fileName = "充值记录表" + sdf2.format(new Date()) + ".xls";
            String sheetName = "充值记录表";
            String[][] objects = new String[n][12];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < 12; j++) {
                    switch (j) {
                        case 0:
                            String operator = String.valueOf(history.get(i).get("operator"));
                            objects[i][j] = operator;
                            break;
                        case 1:
                            String cardNumber = String.valueOf(history.get(i).get("card_number"));
                            objects[i][j] = cardNumber;
                            break;
                        case 2:
                            String phone = String.valueOf(history.get(i).get("phone"));
                            objects[i][j] = phone;
                            break;
                        case 3:
                            String username = String.valueOf(history.get(i).get("username"));
                            objects[i][j] = username;
                            break;
                        case 4:
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
                        case 5:
                            String nowTime = String.valueOf(history.get(i).get("now_time"));
                            objects[i][j] = nowTime;
                            break;
                        case 6:
                            int topUp = (int) history.get(i).get("top_up");
                            objects[i][j] = df.format(topUp / 10.0f);
                            break;
                        case 7:
                            int balance = (int) history.get(i).get("balance");
                            objects[i][j] = df.format(balance / 10.0f);
                            break;
                        case 8:
                            String validDay = String.valueOf(history.get(i).get("valid_day"));
                            objects[i][j] = validDay;
                            break;
                        case 9:
                            int rechargeTime = (int) history.get(i).get("recharge_time");
                            objects[i][j] = df.format(rechargeTime / 10.0f);
                            break;
                        case 10:
                            int payRate = (int) history.get(i).get("pay_rate");
                            objects[i][j] = df.format(payRate / 10.0f);
                            break;
                        case 11:
                            int powerRate = (int) history.get(i).get("power_rate");
                            objects[i][j] = df.format(powerRate / 100.0f);
                            break;
                        default:
                            break;
                    }
                }
            }
            Workbook wb = ExcelUtils.getHSSFWorkbook(sheetName, title, objects, null);
            FileSystemView fsv = FileSystemView.getFileSystemView();
            JFileChooser chooser = new JFileChooser(fsv.getHomeDirectory());
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

        JLabel totalAmountLabel = new JLabel();
        totalAmountLabel.setText("充值总金额 : ");
        totalAmountLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        contentPane.add(totalAmountLabel);
        totalAmountLabel.setBounds(840, 10, 80, totalAmountLabel.getPreferredSize().height);

        JLabel totalAmountText = new JLabel();
        totalAmountText.setText(df.format(totalAmount / 10.0f));
        totalAmountText.setFont(new Font("Dialog", Font.PLAIN, 12));
        totalAmountText.setForeground(Color.red);
        contentPane.add(totalAmountText);
        totalAmountText.setBounds(915, 10, 60, totalAmountLabel.getPreferredSize().height);

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

    private static DatePicker getDatePicker(int location, String time) {
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = new Date();
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Font font = new Font("Times New Roman", Font.PLAIN, 12);
        Dimension dimension = new Dimension(90, 24);
        DatePicker datePicker = new DatePicker(date, format, font, dimension);
        datePicker.setLocation(location, 10);
        datePicker.setLocale(Locale.CHINA);
        datePicker.setTimePanleVisible(false);
        return datePicker;
    }

}
