package writeToExcel

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows

import internal.GlobalVariable

public class writeExcel {
	/**
	 * Write to Excel
	 */
	@Keyword
	def writeToExcel(String filePath, String sheetName, int rowNo, int collNo, String cellValue) {
		FileInputStream file = new FileInputStream (new File(filePath)) //initiate excel repository

		XSSFWorkbook workbook = new XSSFWorkbook(file);
		XSSFSheet sheet = workbook.getSheet(sheetName); //getSheet -> sheet num n (start from index 0)

		'Write data to excel'
		//sheet.createRow(0) //for create clear row (if needed), start from index 0
		sheet.getRow(rowNo).createCell(collNo).setCellValue(cellValue); //getrow = row, dimulai dari 0. create cell = coll, dimulai dari 0, setCellValue = write string to excel

		file.close();
		FileOutputStream outFile =new FileOutputStream(new File(filePath));
		workbook.write(outFile);
		outFile.close();
	}

	@Keyword
	def writeToExcelNumber(String filePath, String sheetName, int rowNo, int collNo, Integer cellValue) {
		FileInputStream file = new FileInputStream (new File(filePath)) //initiate excel repository

		XSSFWorkbook workbook = new XSSFWorkbook(file);
		XSSFSheet sheet = workbook.getSheet(sheetName); //getSheet -> sheet num n (start from index 0)

		'Write data to excel'
		//sheet.createRow(0) //for create clear row (if needed), start from index 0
		sheet.getRow(rowNo).createCell(collNo).setCellValue(cellValue); //getrow = row, dimulai dari 0. create cell = coll, dimulai dari 0, setCellValue = write string to excel

		file.close();
		FileOutputStream outFile =new FileOutputStream(new File(filePath));
		workbook.write(outFile);
		outFile.close();
	}

	@Keyword
	def writeToExcelDecimal(String filePath, String sheetName, int rowNo, int collNo, Double cellValue) {
		FileInputStream file = new FileInputStream (new File(filePath)) //initiate excel repository

		XSSFWorkbook workbook = new XSSFWorkbook(file);
		XSSFSheet sheet = workbook.getSheet(sheetName); //getSheet -> sheet num n (start from index 0)

		'Write data to excel'
		//sheet.createRow(0) //for create clear row (if needed), start from index 0
		sheet.getRow(rowNo).createCell(collNo).setCellValue(cellValue); //getrow = row, dimulai dari 0. create cell = coll, dimulai dari 0, setCellValue = write string to excel

		file.close();
		FileOutputStream outFile =new FileOutputStream(new File(filePath));
		workbook.write(outFile);
		outFile.close();
	}

	// write to excel status and reason
	@Keyword
	public writeToExcelStatusReason (String sheetname, int colm, String status, String reason){

		(new writeToExcel.writeExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
				0, colm - 1, status)
		(new writeToExcel.writeExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
				1, colm - 1, reason)
	}


	@Keyword
	public void writeToExcelFormula(String filePath, String sheetName, int rowNo, int collNo, String cellValue) throws IOException{
		FileInputStream file = new FileInputStream (new File(filePath))
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		XSSFSheet sheet = workbook.getSheet(sheetName);

		XSSFRow row=sheet.createRow(0);

		sheet.getRow(rowNo).createCell(collNo).setCellFormula(cellValue)

		file.close();
		FileOutputStream outFile =new FileOutputStream(new File(filePath));
		workbook.write(outFile);
		outFile.close();

	}

	//keyword getExcelPath
	@Keyword
	public getExcelPath(String Path){
		String userDir = System.getProperty('user.dir')

		String filePath = userDir + Path

		return filePath
	}
}