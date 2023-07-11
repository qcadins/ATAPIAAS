import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'connect DB APIAAS'
Connection conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_public'()

'get colm excel'
int countColmExcel = findTestData(excelPathAPIGetPaymentDetail).columnNumbers

'looping API Get Payment Detail'
for (GlobalVariable.NumOfColumn = 2; GlobalVariable.NumOfColumn <= countColmExcel; (GlobalVariable.NumOfColumn)++) {
    if (findTestData(excelPathAPIGetPaymentDetail).getValue(GlobalVariable.NumOfColumn, 1).length() == 0) {
        break
    } else if (findTestData(excelPathAPIGetPaymentDetail).getValue(GlobalVariable.NumOfColumn, 1).equalsIgnoreCase('Unexecuted')) {
        'HIT API check Get Payment Detail'
        respon = WS.sendRequest(findTestObject('API Get Payment Detail/APIGetPaymentDetail', [('bankCode') : findTestData(
                        excelPathAPIGetPaymentDetail).getValue(GlobalVariable.NumOfColumn, 9)]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
			'get response data'
			code = WS.getElementPropertyValue(respon, 'StatusCode', FailureHandling.OPTIONAL)
			
			if(code == '200') {
	            bankCode = WS.getElementPropertyValue(respon, 'KodeBank', FailureHandling.OPTIONAL)
	
	            bankName = WS.getElementPropertyValue(respon, 'NamaBank', FailureHandling.OPTIONAL)
	
	            noRek = WS.getElementPropertyValue(respon, 'NoRekening', FailureHandling.OPTIONAL)
	
	            namaRek = WS.getElementPropertyValue(respon, 'NamaRekening', FailureHandling.OPTIONAL)
				
				'declare arraylist arraymatch'
				ArrayList<String> arrayMatch = []
				
				if (bankCode != '' && GlobalVariable.KondisiCekDB == 'Yes') {
				
					arrayIndex = 0
					
					'get payment detail from db'
					result = CustomKeywords.'apiGetPaymentDetail.APIGetPaymentDetail.getPaymentDetailDB'(conn, findTestData(
							excelPathAPIGetPaymentDetail).getValue(GlobalVariable.NumOfColumn, 9).replace('"', ''))
		
					'verify bank code'
					arrayMatch.add(WebUI.verifyMatch(bankCode, result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))
					
					'verify bank name'
					arrayMatch.add(WebUI.verifyMatch(bankName, result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))
					
					'verify account number'
					arrayMatch.add(WebUI.verifyMatch(noRek, result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))
					
					'verify account name'
					arrayMatch.add(WebUI.verifyMatch(namaRek, result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))
				}
				
				if (arrayMatch.contains(false)) {
					'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('APIGetPaymentDetail', GlobalVariable.NumOfColumn,
						GlobalVariable.StatusFailed, (findTestData(excelPathAPIGetPaymentDetail).getValue(GlobalVariable.NumOfColumn,
							2) + ';') + GlobalVariable.FailedReasonStoreDB)
				} else {
		            'write to excel success'
		            CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'APIGetPaymentDetail', 0, 
		                GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
				}
			} else {
				'mengambil status code berdasarkan response HIT API'
				message = WS.getElementPropertyValue(respon, 'ErrorMessages.Message', FailureHandling.OPTIONAL).toString()
				
				if(message == 'null') {
					message = WS.getElementPropertyValue(respon, 'Message', FailureHandling.OPTIONAL).toString()
				}
				
				'Write To Excel GlobalVariable.StatusFailed and errormessage'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('APIGetPaymentDetail', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, message)
			}
        } else {
            'mengambil status code berdasarkan response HIT API'
            message = WS.getElementPropertyValue(respon, 'ErrorMessages.Message', FailureHandling.OPTIONAL)
			
            'Write To Excel GlobalVariable.StatusFailed and errormessage'
            CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('APIGetPaymentDetail', GlobalVariable.NumOfColumn, 
                GlobalVariable.StatusFailed, message)
        }
    }
}

