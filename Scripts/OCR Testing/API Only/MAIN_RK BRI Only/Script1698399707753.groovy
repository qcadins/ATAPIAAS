import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.testobject.ResponseObject
import java.sql.Driver
import java.sql.Connection
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdfparser.PDFParser
import org.apache.pdfbox.io.RandomAccessFile
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
GlobalVariable.BaseUrl =  findTestData('Login/BaseUrl').getValue(2, 15)

'deklarasi string'
String message, state, date, numofpages, readIdentity, readTransactionHistory,
	readTransactionSummary, readConfidenceIdentity, readConfidenceTransactionSummary

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
		String thekey = CustomKeywords.'ocrTesting.GetParameterfromDB.getAPIKeyfromDB'(conn, tenantcode, GlobalVariable.SettingEnvi)
		
		'deklarasi variable response'
		ResponseObject response
		
		'cek apakah perlu tambah API'
		String useCorrectKey = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('UseCorrectKey?(Yes/No)'))
		
		'cek apakah perlu gunakan tenantcode yang salah'
		String useCorrectTenant = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('UseCorrectTenantCode?'))
		
		'set penanda error menjadi 0'
		GlobalVariable.FlagFailed = 0

		'cek apa perlu penggunaan key dan tenant yang benar'
		if (useCorrectKey != 'Yes') {
			thekey = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Wrong Key'))
		}
		if (useCorrectTenant != 'Yes') {
			tenantcode = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Wrong TenantCode'))
		}
					
		'lakukan proses HIT api dengan parameter image, key, dan juga tenant'
		response = WS.sendRequest(findTestObject('Object Repository/OCR Testing/New API/Statement BRI',
		[
			('img'): findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('$IMG')),
			('key'):thekey,
			('tenant'):tenantcode
		]))
						
		'ambil message respon dari HIT tersebut'
		message = WS.getElementPropertyValue(response, 'message')
					
		'ambil status dari respon HIT tersebut'
		state = WS.getElementPropertyValue(response, 'status')
		
		'ambil num of pages dari respons tersebut'
		numofpages = WS.getElementPropertyValue(response, 'num_of_pages')
		
		'ambil ocr date dari respon tersebut'
		date = WS.getElementPropertyValue(response, 'ocr_date')
		
		'jika hasil bacaan tidak null'
		if (WS.getElementPropertyValue(response, 'read') != null) {
			
			'ambil hasil bacaan ocr'
			readIdentity = WS.getElementPropertyValue(response, 'read.Identity')
			
			'ambil hasil bacaan ocr'
			readTransactionHistory = WS.getElementPropertyValue(response, 'read.TransactionHistory')
			
			'ambil hasil bacaan ocr'
			readTransactionSummary = WS.getElementPropertyValue(response, 'read.TransactionSummary')
	
			'ambil tingkat confidence jika hit sukses saja, karena param tidak muncul jika failed'
			if (state.equalsIgnoreCase('Success')) {
				'ambil tingkat confidence hasil bacaan ocr'
				readConfidenceIdentity = WS.getElementPropertyValue(response, 'read_confidence.Identity')
				
				'ambil tingkat confidence hasil bacaan ocr'
				readConfidenceTransactionSummary = WS.getElementPropertyValue(response, 'read_confidence.TransactionSummary')
			}
		}
			
		'Jika status HIT API 200 OK'
		if (WS.verifyResponseStatusCode(response, 200, FailureHandling.OPTIONAL) == true) {
			
			'write to excel status'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Status') - 1, GlobalVariable.NumOfColumn -
				1, state)
			
			'write to excel message'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Message') - 1, GlobalVariable.NumOfColumn -
				1, message)
			
			'write to excel num of pages'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('NumOfPages') - 1, GlobalVariable.NumOfColumn -
				1, numofpages)
			
			'write to excel date ocr'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Date') - 1, GlobalVariable.NumOfColumn -
				1, date)
			
			'jika hasil bacaan tidak null'
			if (WS.getElementPropertyValue(response, 'read') != null) {
				
				'ambil hasil bacaan ocr'
				readIdentity = WS.getElementPropertyValue(response, 'read.Identity')
				
				'ambil hasil bacaan ocr'
				readTransactionHistory = WS.getElementPropertyValue(response, 'read.TransactionHistory')
				
				'ambil hasil bacaan ocr'
				readTransactionSummary = WS.getElementPropertyValue(response, 'read.TransactionSummary')
		
				'ambil tingkat confidence jika hit sukses saja, karena param tidak muncul jika failed'
				if (state.equalsIgnoreCase('Success')) {
					'ambil tingkat confidence hasil bacaan ocr'
					readConfidenceIdentity = WS.getElementPropertyValue(response, 'read_confidence.Identity')
					
					'ambil tingkat confidence hasil bacaan ocr'
					readConfidenceTransactionSummary = WS.getElementPropertyValue(response, 'read_confidence.TransactionSummary')
				}
			}
			
			if (state.equalsIgnoreCase('Success') && useCorrectKey != 'Yes' && useCorrectTenant != 'Yes') {
				'write to excel status failed dan reason'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
				GlobalVariable.FailedReasonKeyTenantBypass)
				
			} else if (state.equalsIgnoreCase('Failed')) {
				GlobalVariable.FlagFailed = 1
				'write to excel status failed dan reason'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
				'<' + message + '>')
			}
		} else {
			'jika param message null'
			if (message == null) {
				'pindahkan value di status ke message'
				message = state
				
				'hardcode status yang kosong'
				state = 'FAILED'
			}
			
			'Write To Excel GlobalVariable.StatusFailed and errormessage'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				state, '<' + message + '>')
		}
	}
}

def rowExcel(String cellValue) {
	return CustomKeywords.'writeToExcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}