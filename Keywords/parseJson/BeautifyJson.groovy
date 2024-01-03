package parseJson

import writeToExcel.WriteExcel
import com.kms.katalon.core.annotation.Keyword
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import internal.GlobalVariable

public class BeautifyJson {

	WriteExcel needto = new WriteExcel()

	@Keyword
	def process(String responseBody, String sheet, int rowNo, String fileName) {
		try {
			// Parse the original JSON string
			JsonSlurper slurper = new JsonSlurper()
			Map json = slurper.parseText(responseBody)

			// Beautify the JSON
			JsonBuilder builder = new JsonBuilder(json)
			String beautifiedJson = builder.toPrettyString()

			try {
				needto.writeToExcel(GlobalVariable.DataFilePath, sheet, rowNo, GlobalVariable.NumOfColumn -
						1, beautifiedJson.toString())
			} catch (FileNotFoundException ex) {
				String beautifiedJsonPath = System.getProperty('user.dir') + '\\Response\\' + fileName + '.json'

				new File(beautifiedJsonPath).text = beautifiedJson

				needto.writeToExcel(GlobalVariable.DataFilePath, sheet, rowNo, GlobalVariable.NumOfColumn -
						1, beautifiedJsonPath)
			}
		} catch (FileNotFoundException e) {
			println("Failed to beautify the JSON: ${e.message}")
		}
	}
	
}
