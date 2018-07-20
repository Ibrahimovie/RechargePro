package excel;

import org.apache.poi.hssf.usermodel.*;

/**
 * @author kingfans
 */
public class ExcelUtils {
    public static HSSFWorkbook getHSSFWorkbook(String sheetName, String[] title, String[][] values, HSSFWorkbook wb) {
        if (wb == null) {
            wb = new HSSFWorkbook();
        }
        HSSFSheet sheet = wb.createSheet(sheetName);
        sheet.setColumnWidth(0, 3000);
        sheet.setColumnWidth(1, 3000);
        sheet.setColumnWidth(2, 3256);
        sheet.setColumnWidth(3, 3512);
        sheet.setColumnWidth(4, 4024);
        sheet.setColumnWidth(5, 5048);
        sheet.setColumnWidth(6, 2744);
        sheet.setColumnWidth(7, 2744);
        sheet.setColumnWidth(8, 2744);
        sheet.setColumnWidth(9, 2744);
        sheet.setColumnWidth(10, 2744);
        sheet.setColumnWidth(11, 2744);
        HSSFRow row = sheet.createRow(0);
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment((short) 2);
        HSSFCell cell = null;
        for (int i = 0; i < title.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }
        for (int i = 0; i < values.length; i++) {
            row = sheet.createRow(i + 1);
            for (int j = 0; j < values[i].length; j++) {
                row.createCell(j).setCellValue(values[i][j]);
            }
        }
        return wb;
    }
}
