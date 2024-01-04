package ocrtesting

import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable
import groovy.json.JsonSlurper
import writetoexcel.WriteExcel

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

		read1.each { key, value1 ->
			if (read2.containsKey(key)) {
				String value2 = read2[key]

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
