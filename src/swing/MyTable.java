package swing;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
 * @Author: kingfans
 * @Date: 2018/7/16
 */
public class MyTable extends JTable {
    public MyTable(DefaultTableModel tableModel) {
        super(tableModel);
    }

    public MyTable(Object[][] obj, String[] columnNames) {
        super(obj, columnNames);
    }

    @Override
    public JTableHeader getTableHeader() {
        JTableHeader tableHeader = super.getTableHeader();
        tableHeader.setReorderingAllowed(false);
        DefaultTableCellRenderer hr = (DefaultTableCellRenderer) tableHeader.getDefaultRenderer();
        hr.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        return tableHeader;
    }

    @Override
    public TableCellRenderer getDefaultRenderer(Class<?> columnClass) {
        DefaultTableCellRenderer cr = (DefaultTableCellRenderer) super.getDefaultRenderer(columnClass);
        cr.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        return cr;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
