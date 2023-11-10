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
import java.text.SimpleDateFormat
import java.util.Date

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/1. Login.xlsm')

sheet = 'Link Base Url'

'get base url'
GlobalVariable.BaseUrl =  findTestData('Login/BaseUrl').getValue(2, rowExcel('OCR NPWP'))

'mencari directory excel utama'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

sheet = 'OCR NPWP'

'mendapat jumlah kolom dari sheet Edit Profile'
int countColumnEdit = findTestData(ExcelPathOCRTesting).columnNumbers

'deklarasi variabel untuk konek ke Database eendigo_dev'
Connection conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_public'()

String tanggal = todayDate()

String responseBody, message_ocr, state_ocr, ocr_date, timeOcrhit

int firstRun = 0

'pindah testcase sesuai jumlah di excel'
for (GlobalVariable.NumOfColumn = 2; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++) {
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
		
		if (useCorrectKey != 'Yes') {
			thekey = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Wrong Key'))
		} 
		if (useCorrectTenant != 'Yes') {
			tenantcode = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Wrong TenantCode'))
		}
					
		'lakukan proses HIT api dengan parameter image, key, dan juga tenant'
		response = WS.sendRequest(findTestObject('Object Repository/OCR Testing/New API/NPWP',
		[
			('img'): findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('$IMG')),
			('key'):thekey,
			('tenant'):tenantcode
		]))
		
		'ambil lama waktu yang diperlukan hingga request menerima balikan'
		def elapsedTime = (response.getElapsedTime()) / 1000 + ' second'
		
		'ambil message respon dari HIT tersebut'
		message_ocr = WS.getElementPropertyValue(response, 'message', FailureHandling.CONTINUE_ON_FAILURE)
					
		'ambil status dari respon HIT tersebut'
		state_ocr = WS.getElementPropertyValue(response, 'status', FailureHandling.CONTINUE_ON_FAILURE)
		
		'ambil status dari respon HIT tersebut'
		ocr_date = WS.getElementPropertyValue(response, 'ocr_date', FailureHandling.CONTINUE_ON_FAILURE)
		
		'write to excel response elapsed time'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 1, GlobalVariable.NumOfColumn -
			1, elapsedTime.toString())
			
		if (state_ocr == null || state_ocr == '') {
			
			'write to excel status failed dan reason'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
					'Terdapat unidentified "Nan", tidak bisa tulis respons ke excel')
			
			continue
		}
			
		'Jika status HIT API 200 atau 500 dan tidak menggunakan key atau tenant invalid'
		if (!state_ocr.equalsIgnoreCase('key or tenant invalid') &&
			((WS.verifyResponseStatusCode(response, 200, FailureHandling.OPTIONAL) == true) ||
				(WS.verifyResponseStatusCode(response, 500, FailureHandling.OPTIONAL) == true))) {
			
			'ambil waktu hit untuk sebagai acuan nama file log'
			timeOcrhit = processHourOnly(ocr_date)
			
			'ambil body dari hasil respons'
			responseBody = response.getResponseBodyContent()
			
			'write to excel status'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Status') - 1, GlobalVariable.NumOfColumn -
				1, state_ocr)
			
			'panggil keyword untuk proses beautify dari respon json yang didapat'
			CustomKeywords.'parseJson.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1,
				findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Scenario')))
			
			if (state_ocr.equalsIgnoreCase('Success') && useCorrectKey != 'Yes' && useCorrectTenant != 'Yes') {
				'write to excel status failed dan reason'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
						GlobalVariable.FailedReasonKeyTenantBypass)
				
			} else if (state_ocr.equalsIgnoreCase('Failed')) {
				GlobalVariable.FlagFailed = 1
				'write to excel status failed dan reason'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
						'<' + message_ocr + '>')
			}
			
			'jika perlu cek log dijalankan'
			if (GlobalVariable.checkLog == 'Yes') {
				
				'jika browser belum pernah dibuka'
				if (firstRun == 0) {
					'panggil testcase open browser'
					WebUI.callTestCase(findTestCase('OCR Testing/API Only/OpenBrowserMultiTab'),[:])
					
					firstRun = 1
				}
				'panggil testcase open browser'
				WebUI.callTestCase(findTestCase('OCR Testing/API Only/CheckLog'),[('OCRType') : 'OCR_npwp',
					('Tanggal') : tanggal, ('TenantCode') : tenantcode, ('TimeOCR') : timeOcrhit, ('sheet') : sheet,
					('ExcelPathOCRTesting') : ExcelPathOCRTesting])
			}
		} else {
			'jika param message null'
			if (message_ocr == null) {
				'pindahkan value di status ke message'
				message_ocr = state_ocr
				
				'hardcode status yang kosong'
				state_ocr = 'FAILED'
			}
			
			'Write To Excel GlobalVariable.StatusFailed and errormessage'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				state_ocr, '<' + message_ocr + '>')
		}
	}
}

WebUI.closeBrowser()

def rowExcel(String cellValue) {
	return CustomKeywords.'writeToExcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def todayDate() {
	'ambil tanggal hari ini'
	Date currentDate = new Date()
	
	'buat format menjadi yyyyMMDD'
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd")
	
	'ambil hasil format tadi menjadi string'
	String formattedDate = dateFormat.format(currentDate)
	
	'return hasil format tadi'
	return formattedDate
}

def processHourOnly(String time) {
	
	parts = time.split('T')
	String timePart = parts[1]
	
	String result = timePart.replaceAll("[:+]", "").replace('0700','');
	
	return result
}