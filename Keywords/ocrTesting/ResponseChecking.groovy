package ocrTesting

import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable
import groovy.json.JsonSlurper
import writeToExcel.WriteExcel

public class ResponseChecking {

	WriteExcel needto = new WriteExcel()
	ArrayList reason = []
	String conversion

	@Keyword
	verifyValueDifference(String resp1, String resp2, String sheet, int rowNo) {
		// Your first JSON response
		def response1 = resp1

		// Your second JSON response
		def response2 = resp2

		def jsonSlurper = new JsonSlurper()
		def json1 = jsonSlurper.parseText(response1)
		def json2 = jsonSlurper.parseText(response2)

		def read1 = json1.read
		def read2 = json2.read

		read1.each { key, value1 ->
			if (read2.containsKey(key)) {
				def value2 = read2[key]

				if (value1 != value2) {
					reason.add("Terdapat perbedaan di parameter $key: $value1 != $value2")
					
					GlobalVariable.FlagFailed = 1
				}
			} else {
				reason.add("Parameter '$key' tidak ditemukan di respons dari API")
			}
		}
		//konversi dari array ke string untuk write ke excel
		conversion = reason.join('\n')

		needto.writeToExcel(GlobalVariable.DataFilePath, sheet, rowNo, GlobalVariable.NumOfColumn -
				1, conversion)
	}
}
