import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.testobject.ResponseObject
import java.sql.Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable
import java.time.format.DateTimeFormatter
import java.time.LocalDate

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writetoexcel.WriteExcel.getExcelPath'('/1. Login.xlsm')

sheet = 'Link Base Url'

'get base url'
GlobalVariable.BaseUrl =  findTestData('Login/BaseUrl').getValue(2, rowExcel('OCR RK BCA'))

'mencari directory excel utama'
GlobalVariable.DataFilePath = CustomKeywords.'writetoexcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

sheet = 'OCR RK BCA'

'mendapat jumlah kolom dari sheet Edit Profile'
int countColumnEdit = findTestData(ExcelPathOCRTesting).columnNumbers

'deklarasi variabel untuk konek ke Database eendigo_dev'
Connection conn = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_public'()

String tanggal = todayDate()

String responseBody, message, state, ocrdate, timeOcrhit

int firstRun = 0

'pindah testcase sesuai jumlah di excel'
for (GlobalVariable.NumOfColumn = 2; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++) {
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).length() == 0) {
		break
	} else if (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
		'ambil kode tenant di DB'
		String tenantcode = CustomKeywords.'ocrtesting.GetParameterfromDB.getTenantCodefromDB'(conn,
			findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('UsernameLogin')))
		
		'ambil key trial yang aktif dari DB'
		String thekey = CustomKeywords.'ocrtesting.GetParameterfromDB.getAPIKeyfromDB'(conn, tenantcode, GlobalVariable.SettingEnvi)
		
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
		response = WS.sendRequest(findTestObject('Object Repository/OCR Testing/New API/Statement BCA',
		[
			('img'): findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('$IMG')),
			('key'):thekey,
			('tenant'):tenantcode,
		]))
		
		'ambil lama waktu yang diperlukan hingga request menerima balikan'
		String elapsedTime = (response.elapsedTime) / 1000 + ' second'
		
		'ambil message respon dari HIT tersebut'
		message = WS.getElementPropertyValue(response, 'message', FailureHandling.CONTINUE_ON_FAILURE)
					
		'ambil status dari respon HIT tersebut'
		state = WS.getElementPropertyValue(response, 'status', FailureHandling.CONTINUE_ON_FAILURE)
		
		'ambil status dari respon HIT tersebut'
		ocrdate = WS.getElementPropertyValue(response, 'ocr_date', FailureHandling.CONTINUE_ON_FAILURE)
		
		'write to excel response elapsed time'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 1, GlobalVariable.NumOfColumn -
			1, elapsedTime.toString())
			
		if (state == null || state == '') {
			'write to excel status failed dan reason'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
					'Terdapat unidentified "Nan", tidak bisa tulis respons ke excel')
			
			continue
		}

		'Jika status HIT API 200 atau 500 dan tidak menggunakan key atau tenant invalid'
		if ((!state.equalsIgnoreCase('key or tenant invalid') && !message.equalsIgnoreCase('Invalid API key or tenant code')) &&
			((WS.verifyResponseStatusCode(response, 200, FailureHandling.OPTIONAL) == true) ||
				(WS.verifyResponseStatusCode(response, 500, FailureHandling.OPTIONAL) == true))) {
			'ambil waktu hit untuk sebagai acuan nama file log'
			timeOcrhit = processHourOnly(ocrdate)
			
			'ambil body dari hasil respons'
			responseBody = response.responseBodyContent
			
			'write to excel status'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Status') - 1, GlobalVariable.NumOfColumn -
				1, state)
			
			'panggil keyword untuk proses beautify dari respon json yang didapat'
			CustomKeywords.'parsejson.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1,
				findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Scenario')))
			
			if (state.equalsIgnoreCase('Success') && useCorrectKey != 'Yes' && useCorrectTenant != 'Yes') {
				'write to excel status failed dan reason'
				CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
						GlobalVariable.FailedReasonKeyTenantBypass)
			} else if (state.equalsIgnoreCase('Failed')) {
				GlobalVariable.FlagFailed = 1
				
				'write to excel status failed dan reason'
				CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
				'<' + message + '>')
			}
			
			'jika perlu cek log dijalankan'
			if (GlobalVariable.checkLog == 'Yes') {
				'jika browser belum pernah dibuka'
				if (firstRun == 0) {
					'panggil testcase open browser'
					WebUI.callTestCase(findTestCase('OCR Testing/API Only/OpenBrowserMultiTab'), [('Path') : ExcelPathOCRTesting,
						('sheet') : sheet,])
					
					firstRun = 1
				}
				'panggil testcase check log'
				WebUI.callTestCase(findTestCase('OCR Testing/API Only/CheckLog'), [('OCRType') : 'BCAExtractor',
					('Tanggal') : tanggal, ('TenantCode') : tenantcode, ('TimeOCR') : timeOcrhit, ('sheet') : sheet,
					('ExcelPathOCRTesting') : ExcelPathOCRTesting,])
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
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				state, '<' + message + '>')
		}
	}
}

WebUI.closeBrowser()

def rowExcel(String cellValue) {
	CustomKeywords.'writetoexcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def todayDate() {
	'ambil tanggal hari ini'
	LocalDate currentDate = LocalDate.now()
	
	'buat format menjadi yyyyMMDD'
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd")

	'Format the LocalDate'
	String formattedDate = currentDate.format(formatter)
	
	'return hasil format tadi'
	formattedDate
}

def processHourOnly(String time) {
	parts = time.split('T')
	String timePart = parts[1]
	
	String result = timePart.replaceAll('[:+]', '').replace('0700', '')
	
	result
}
