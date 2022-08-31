import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.opencsv.CSVReader;
public class CsvToExcelConverter {
    public static final char CSV_FILE_DELIMITER = ',';
    public void convertCsvToExcel(String strSource, String strDestination, String extension)
            throws IllegalArgumentException, IOException {

        Workbook workBook = null;
        FileOutputStream fos = null;

        // Check that the source file exists.
        File sourceFile = new File(strSource);
        if (!sourceFile.exists()) {
            throw new IllegalArgumentException("The source CSV file cannot be found at " + sourceFile);
        }

        // Check that the destination folder exists to save the Excel file.
        File destination = new File(strDestination);
        if (!destination.exists()) {
            throw new IllegalArgumentException(
                    "The destination directory " + destination + " for the converted Excel file does not exist.");
        }
        if (!destination.isDirectory()) {
            throw new IllegalArgumentException(
                    "The destination " + destination + " for the Excel file is not a directory/folder.");
        }

        // Getting CSVReader object by passing specified Delimiter
        CSVReader reader = new CSVReader(new FileReader(sourceFile), CSV_FILE_DELIMITER);

        // Getting XSSFWorkbook or HSSFWorkbook object based on excel file format
        if (extension.equals(".xlsx")) {
            workBook = new XSSFWorkbook();
        } else {
            workBook = new HSSFWorkbook();
        }

        Sheet sheet = workBook.createSheet("Sheet");

        String[] nextLine;
        int rowNum = 0;
        while ((nextLine = reader.readNext()) != null) {
            Row currentRow = sheet.createRow(rowNum++);
            for (int i = 0; i < nextLine.length; i++) {
                if (NumberUtils.isDigits(nextLine[i])) {
                    currentRow.createCell(i).setCellValue(Integer.parseInt(nextLine[i]));
                } else if (NumberUtils.isNumber(nextLine[i])) {
                    currentRow.createCell(i).setCellValue(Double.parseDouble(nextLine[i]));
                } else {
                    currentRow.createCell(i).setCellValue(nextLine[i]);
                }
            }
        }
        String filename = sourceFile.getName();
        filename = filename.substring(0, filename.lastIndexOf('.'));
        File generatedExcel = new File(strDestination, filename + extension);
        fos = new FileOutputStream(generatedExcel);
        workBook.write(fos);

        try {
            // Closing workbook, fos, and reader object
            workBook.close();
            fos.close();
            reader.close();

        } catch (IOException e) {
            System.out.println("Exception While Closing I/O Objects");
            e.printStackTrace();
        }

    }

    // Main method
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        boolean converted = true;
        try {
            CsvToExcelConverter converter = new CsvToExcelConverter();
            String strSource = "D:/New folder/csvToExcel/csvFile.csv";
            String strDestination = "D:/New folder/csvToExcel/";
            converter.convertCsvToExcel(strSource, strDestination, ".xls");
        } catch (Exception e) {
            System.out.println("Unexpected exception");
            e.printStackTrace();
            converted = false;
        }
        if (converted) {
            System.out.println("Conversion took " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds");
        }
    }
}