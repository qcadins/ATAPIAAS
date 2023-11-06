import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.testobject.ResponseObject

import java.sql.Connection
import java.sql.Driver

import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable

import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/1. Login.xlsm')

sheet = 'Link Base Url'

'get base url'
GlobalVariable.BaseUrl =  findTestData('Login/BaseUrl').getValue(2, rowExcel('Liveness'))

'mencari directory excel utama'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

sheet = 'OCR Liveness'

'mendapat jumlah kolom dari sheet Edit Profile'
int countColumnEdit = findTestData(ExcelPathOCRTesting).columnNumbers

'deklarasi variabel untuk konek ke Database eendigo_dev'
Connection conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_public'()

'deklarasi string hasil respons'
String responseBody, message_ocr, state_ocr, error_ocr

'pindah testcase sesuai jumlah di excel'
for(GlobalVariable.NumOfColumn = 2; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++){
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 1).length() == 0) {
		break
	} else if (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 1).equalsIgnoreCase('Unexecuted')) {
		
		'ambil kode tenant di DB'
		String tenantcode = CustomKeywords.'ocrTesting.GetParameterfromDB.getTenantCodefromDB'(conn,
			findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('UsernameLogin')))
		
		'ambil key trial yang aktif dari DB'
		String thekey = CustomKeywords.'ocrTesting.GetParameterfromDB.getAPIKeyfromDB'(conn, tenantcode, GlobalVariable.SettingEnvi)
		
		'deklarasi variable response'
		ResponseObject response
		
		'cek apakah perlu tambah API'
		String useCorrectKey = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('UseCorrectKey?(Yes/No)'))
		
		'cek apakah perlu gunakan tenantcode yang salah'
		String useCorrectTenant = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('UseCorrectTenantCode?'))
				
		'set penanda error menjadi 0'
		GlobalVariable.FlagFailed = 0
		
		if (useCorrectKey != 'Yes') {
			
			thekey = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Wrong Key'))
		}
		if (useCorrectTenant != 'Yes') {
			
			tenantcode = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Wrong TenantCode'))
		}
		
		'lakukan proses HIT api dengan parameter image, key, dan juga tenant'
		response = WS.sendRequest(findTestObject('Object Repository/OCR Testing/Liveness(Prod)',
		[	('img'): findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('$selfiephoto')),
			('key'):thekey,
			('tenant'):tenantcode,
			('refNum'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('refNumber')),
			('source'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('source')),
			('loginid'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('LoginID')),
			('nik'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('NIK')),
			('offcode'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('OfficeCode')),
			('offname'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('OfficeName')),
			('question'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('question')),
		]))
			
		'ambil message respon dari HIT tersebut'
		error_ocr = WS.getElementPropertyValue(response, 'error')
		
		'ambil status dari respon HIT tersebut'
		state_ocr = WS.getElementPropertyValue(response, 'status')

		'ambil verifStatus dari respon HIT'
		message_ocr = WS.getElementPropertyValue(response, 'message')

		'Jika status HIT API 200 OK'
		if (WS.verifyResponseStatusCode(response, 200, FailureHandling.OPTIONAL) == true) {
			
			'ambil body dari hasil respons'
			responseBody = response.getResponseBodyContent()
			
			'write to excel status'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Status') - 1, GlobalVariable.NumOfColumn -
				1, state_ocr)
			
			'write to excel message'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Reason failed') - 1, GlobalVariable.NumOfColumn -
				1, message_ocr)
			
			'write to excel num of pages'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Error') - 1, GlobalVariable.NumOfColumn -
				1, error_ocr)
			
			'panggil keyword untuk proses beautify dari respon json yang didapat'
			CustomKeywords.'parseJson.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1,
				findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Scenario')))
			
			if (state_ocr.equalsIgnoreCase('Success') && useCorrectKey != 'Yes' && useCorrectTenant != 'Yes') {
				'write to excel status failed karena key dan tenant salah tapi HIT berhasil'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
				GlobalVariable.FailedReasonKeyTenantBypass)
				
			} else if (state_ocr.equalsIgnoreCase('Success')) {
				'tulis status sukses pada excel'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet,
					GlobalVariable.NumOfColumn, GlobalVariable.StatusSuccess,
						'<' + error_ocr + '>')
			} else {
				GlobalVariable.FlagFailed = 1
				'write to excel status failed dan reason'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
				'<' + message_ocr + '>')
			}
		} else {

            'Write To Excel GlobalVariable.StatusFailed and errormessage'
            CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, 
                GlobalVariable.StatusFailed, '<' + message_ocr + '>')
        }
	}
}

'tutup browser jika loop sudah selesai'
WebUI.closeBrowser()

def rowExcel(String cellValue) {
	return CustomKeywords.'writeToExcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}