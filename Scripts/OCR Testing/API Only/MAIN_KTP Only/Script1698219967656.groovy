import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.testobject.ResponseObject
import java.sql.Driver
import java.sql.Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Edit Profile'
int countColumnEdit = findTestData(ExcelPathOCRTesting).columnNumbers

'deklarasi variabel untuk konek ke Database eendigo_dev'
Connection conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_public'()

'get base url'
GlobalVariable.BaseUrl =  findTestData('Login/BaseUrl').getValue(2, 6)

String message_ocr, state_ocr, date_ocr, read_ocr, readconfidence_ocr

'pindah testcase sesuai jumlah di excel'
for (GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++) {
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).length() == 0) {	
		break
	} else if (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
		
		'ambil kode tenant di DB'
		String tenantcode = CustomKeywords.'ocrTesting.GetParameterfromDB.getTenantCodefromDB'(conn,
			findTestData(ExcelPathOCRTesting).getValue(2, rowExcel('UsernameLogin')))
		
		'ambil key trial yang aktif dari DB'
		String thekey = CustomKeywords.'ocrTesting.GetParameterfromDB.getAPIKeyfromDB'(conn, tenantcode,  GlobalVariable.SettingEnvi)
		
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
		response = WS.sendRequest(findTestObject('Object Repository/OCR Testing/New API/KTP', 
		[
			('img'): findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('$IMG')),
			('key'):thekey, 
			('tenant'):tenantcode
		]))
			
		'ambil message respon dari HIT tersebut'
		message_ocr = WS.getElementPropertyValue(response, 'message')
					
		'ambil status dari respon HIT tersebut'
		state_ocr = WS.getElementPropertyValue(response, 'status')
		
		'ambil ocr date dari respon tersebut'
		date_ocr = WS.getElementPropertyValue(response, 'ocr_date')
		
		'ambil hasil bacaan ocr'
		read_ocr = WS.getElementPropertyValue(response, 'read')
		
		'ambil tingkat confidence hasil bacaan ocr'
		readconfidence_ocr = WS.getElementPropertyValue(response, 'read_confidence')
			
		'Jika status HIT API 200 OK'
		if (WS.verifyResponseStatusCode(response, 200, FailureHandling.OPTIONAL) == true) {
			
			'write to excel status'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Status') - 1, GlobalVariable.NumOfColumn -
				1, state_ocr)
			
			'write to excel message'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Message') - 1, GlobalVariable.NumOfColumn -
				1, message_ocr)
			
			'write to excel date ocr'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Date') - 1, GlobalVariable.NumOfColumn -
				1, date_ocr)
			
			'write to excel read dari ocr'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Read Result') - 1, GlobalVariable.NumOfColumn -
				1, read_ocr)
			
			'write to excel read confidence ocr'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Read Confidence') - 1, GlobalVariable.NumOfColumn -
				1, readconfidence_ocr)
			
			if (state_ocr.equalsIgnoreCase('Success') && useCorrectKey != 'Yes' && useCorrectTenant != 'Yes') {
				'write to excel status failed dan reason'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
				GlobalVariable.FailedReasonKeyTenantBypass)
				
			} else {
				GlobalVariable.FlagFailed = 1
				'write to excel status failed dan reason'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
				'<' + state_ocr + '>')
			}
		} else {
			'Write To Excel GlobalVariable.StatusFailed and errormessage'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, '<' + message_ocr + '>')
		}
	}
}

def rowExcel(String cellValue) {
	return CustomKeywords.'writeToExcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}