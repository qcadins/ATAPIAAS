package ocrtesting

import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable
import groovy.json.JsonSlurper
import writetoexcel.WriteExcel
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

public class ResponseChecking {

	WriteExcel needto = new WriteExcel()

	@Keyword
	verifyValueDifference(String resp1, String resp2, String sheet, int rowNo) {
		ArrayList reason = []
		String conversion

		JsonSlurper jsonSlurper = new JsonSlurper()
		Map json1 = jsonSlurper.parseText(resp1)
		Map json2 = jsonSlurper.parseText(resp2)

		Map read1 = json1.read
		Map read2 = json2.read

		if (!(sheet.contains('Forgery') && sheet.contains('ID'))) {
			println 'aa'
			read2.each { key, value2 ->
				if (read1 == null) {
					read1 = [:]
				}
				if (read1.containsKey(key)) {
					Object value1 = read1[key]

					if (value1 != value2) {
						reason.add("Terdapat perbedaan di parameter $key: $value1 != $value2")
						GlobalVariable.FlagFailed = 1
					}
				} else {
					reason.add("Parameter '$key' tidak ditemukan di respons dari API")
				}
			}
		}
		println 'aa'

		if (sheet.contains('Forgery')) {
			if (sheet.contains('KTP')) {
				read1 = json1.result_forgery
				read2 = json2.result_forgery
			} else if (sheet.contains('ID')) {
				read1 = json1.result
				read2 = json2.result
			}
			read2.each { key, value2 ->
				if (read1 == null) {
					read1 = [:]
				}
				if (read1.containsKey(key)) {
					Object value1 = read1[key]

					if (value1 != value2) {
						reason.add("Terdapat perbedaan di parameter $key: $value1 != $value2")
						GlobalVariable.FlagFailed = 1
					}
				} else {
					reason.add("Parameter '$key' tidak ditemukan di respons dari API")
				}
			}
		}
		//konversi dari array ke string untuk write ke excel
		conversion = reason.join('\n')

		needto.writeToExcel(GlobalVariable.DataFilePath, sheet, rowNo, GlobalVariable.NumOfColumn -
				1, conversion)
	}
}
