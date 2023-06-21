package writeToExcel

import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.ss.usermodel.*

import com.kms.katalon.core.annotation.Keyword

import internal.GlobalVariable

public class WriteExcel {
	/**
	 * Write to Excel
	 */
	//fungsi digunakan untuk menulis data ke dalam excel dalam bentuk string
	@Keyword
	def writeToExcel(String filePath, String sheetName, int rowNo, int collNo, String cellValue) {
		FileInputStream file = new FileInputStream (new File(filePath)) //initiate excel repository

		XSSFWorkbook workbook = new XSSFWorkbook(file)
		XSSFSheet sheet = workbook.getSheet(sheetName) //getSheet -> sheet num n (start from index 0)

		'Write data to excel'
		//sheet.createRow(0) //for create clear row (if needed), start from index 0
		sheet.getRow(rowNo).createCell(collNo).setCellValue(cellValue) //getrow = row, dimulai dari 0. create cell = coll, dimulai dari 0, setCellValue = write string to excel

		file.close()
		FileOutputStream outFile =new FileOutputStream(new File(filePath))
		workbook.write(outFile)
		outFile.close()
	}

	//fungsi digunakan untuk menulis ke dalam excel dengan tipe data angka
	@Keyword
	def writeToExcelNumber(String filePath, String sheetName, int rowNo, int collNo, Integer cellValue) {
		FileInputStream file = new FileInputStream (new File(filePath)) //initiate excel repository

		XSSFWorkbook workbook = new XSSFWorkbook(file)
		XSSFSheet sheet = workbook.getSheet(sheetName) //getSheet -> sheet num n (start from index 0)

		'Write data to excel'
		//sheet.createRow(0) //for create clear row (if needed), start from index 0
		sheet.getRow(rowNo).createCell(collNo).setCellValue(cellValue) //getrow = row, dimulai dari 0. create cell = coll, dimulai dari 0, setCellValue = write string to excel

		file.close()
		FileOutputStream outFile = new FileOutputStream(new File(filePath))
		workbook.write(outFile)
		outFile.close()
	}

	//fungsi digunakan untuk menulis inputan decimal(float) ke excel
	@Keyword
	def writeToExcelDecimal(String filePath, String sheetName, int rowNo, int collNo, Double cellValue) {
		FileInputStream file = new FileInputStream (new File(filePath)) //initiate excel repository

		XSSFWorkbook workbook = new XSSFWorkbook(file)
		XSSFSheet sheet = workbook.getSheet(sheetName) //getSheet -> sheet num n (start from index 0)

		'Write data to excel'
		//sheet.createRow(0) //for create clear row (if needed), start from index 0
		sheet.getRow(rowNo).createCell(collNo).setCellValue(cellValue) //getrow = row, dimulai dari 0. create cell = coll, dimulai dari 0, setCellValue = write string to excel

		file.close()
		FileOutputStream outFile = new FileOutputStream(new File(filePath))
		workbook.write(outFile)
		outFile.close()
	}

	//fungsi digunakan untuk menulis ke dalam excel dengan status dan alasan gagalnya case tersebut
	// write to excel status and reason
	@Keyword
	writeToExcelStatusReason (String sheetname, int colm, String status, String reason){

		(new writeToExcel.WriteExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
				0, colm - 1, status)
		(new writeToExcel.WriteExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
				1, colm - 1, reason)
	}


	//fungsi digunakan untuk menulis rumus ke dalam cell excel
	@Keyword
	void writeToExcelFormula(String filePath, String sheetName, int rowNo, int collNo, String cellValue) throws IOException {
		FileInputStream file = new FileInputStream (new File(filePath))
		XSSFWorkbook workbook = new XSSFWorkbook(file)
		XSSFSheet sheet = workbook.getSheet(sheetName)

		sheet.getRow(rowNo).createCell(collNo).setCellFormula(cellValue)

		file.close()
		FileOutputStream outFile = new FileOutputStream(new File(filePath))
		workbook.write(outFile)
		outFile.close()

	}

	//fungsi digunakan untuk mengambil directory dari file excel yang akan digunakan
	//keyword getExcelPath
	@Keyword
	getExcelPath(String Path) {
		String userDir = System.getProperty('user.dir')

		String filePath = userDir + Path

		filePath
	}

	@Keyword
	void emptyCellRange(String filePath, String sheetName, int startRow, int endRow, int column) throws Exception {
        FileInputStream fis = new FileInputStream(filePath)
        XSSFWorkbook workbook = new XSSFWorkbook(fis)
        Sheet sheet = workbook.getSheet(sheetName)

        for (int row = startRow; row <= endRow; row++) {
            Row currentRow = sheet.getRow(row)
            if (currentRow != null) {
                Cell cell = currentRow.getCell(column)
                if (cell != null) {
                    cell.setCellValue('')
                }
            }
        }

        FileOutputStream fos = new FileOutputStream(filePath)
        workbook.write(fos)
        fos.close()
        workbook.close()
        fis.close()
    }
}

