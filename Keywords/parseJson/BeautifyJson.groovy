package parseJson

import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import writeToExcel.WriteExcel
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.annotation.Keyword
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.ss.usermodel.*

import internal.GlobalVariable

public class BeautifyJson {

	WriteExcel needto = new WriteExcel()

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
				needto.writeToExcel(GlobalVariable.DataFilePath, sheet, rowNo, GlobalVariable.NumOfColumn -
						1, beautifiedJson.toString())

			} catch (Exception ex) {

				String beautifiedJsonPath = System.getProperty('user.dir') + '\\Response\\' + fileName + '.json'

				new File(beautifiedJsonPath).text = beautifiedJson

				needto.writeToExcel(GlobalVariable.DataFilePath, sheet, rowNo, GlobalVariable.NumOfColumn -
						1, beautifiedJsonPath)
			}

		} catch (Exception e) {
			println("Failed to beautify the JSON: ${e.getMessage()}")
		}
	}
}
