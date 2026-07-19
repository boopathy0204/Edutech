package edutech.project.util;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.web.multipart.MultipartFile;

public class ExcelHelper {

    public static final String EXCEL_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String EXCEL_XLS = "application/vnd.ms-excel";
    private static final DataFormatter FORMATTER = new DataFormatter();

    public static boolean isExcelFile(MultipartFile file) {
        String type = file.getContentType();
        return EXCEL_XLSX.equals(type) || EXCEL_XLS.equals(type);
    }

    public static String readCell(Row row, int cellIndex) {
        if (row == null || row.getCell(cellIndex) == null) return null;
        String value = FORMATTER.formatCellValue(row.getCell(cellIndex));
        value = value == null ? null : value.trim();
        return (value == null || value.isEmpty()) ? null : value;
    }

}