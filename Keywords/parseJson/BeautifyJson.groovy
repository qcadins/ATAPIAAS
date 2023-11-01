package parseJson

import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.annotation.Keyword
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.ss.usermodel.*


import internal.GlobalVariable

public class BeautifyJson {

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

	@Keyword
	def process(String responseBody, String sheet, int rowNo, String fileName) {
		try {
			// Parse the original JSON string
			def slurper = new groovy.json.JsonSlurper()
			def json = slurper.parseText(responseBody)

			// Beautify the JSON
			def builder = new groovy.json.JsonBuilder(json)
			def beautifiedJson = builder.toPrettyString()

			try {
				writeToExcel(GlobalVariable.DataFilePath, sheet, rowNo, GlobalVariable.NumOfColumn -
						1, beautifiedJson.toString())

			} catch (Exception ex) {

				String beautifiedJsonPath = System.getProperty('user.dir') + '\\Response\\' + fileName + '.json'

				new File(beautifiedJsonPath).text = beautifiedJson

				writeToExcel(GlobalVariable.DataFilePath, sheet, rowNo, GlobalVariable.NumOfColumn -
						1, beautifiedJsonPath)
			}

		} catch (Exception e) {
			println("Failed to beautify the JSON: ${e.getMessage()}")
		}
	}

	//	@Keyword
	//	def optionaltoFile(String responseBody) {
	//		try {
	//			// Parse the original JSON string
	//			def slurper = new groovy.json.JsonSlurper()
	//			def json = slurper.parseText(responseBody)
	//
	//			// Beautify the JSON
	//			def builder = new groovy.json.JsonBuilder(json)
	//			def beautifiedJson = builder.toPrettyString()
	//
	//			return beautifiedJson.toString()
	//
	//		} catch (Exception e) {
	//			println("Failed to beautify the JSON: ${e.getMessage()}")
	//		}
	//	}
}
