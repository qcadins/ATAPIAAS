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
GlobalVariable.BaseUrl =  findTestData('Login/BaseUrl').getValue(2, rowExcel('OCR BPKB'))

'mencari directory excel utama'
GlobalVariable.DataFilePath = CustomKeywords.'writetoexcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

sheet = 'OCR BPKB'

'mendapat jumlah kolom dari sheet Edit Profile'
int countColumnEdit = findTestData(ExcelPathOCRTesting).columnNumbers

'deklarasi variabel untuk konek ke Database eendigo_dev'
Connection conn = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_public'()

String tanggal = todayDate()

String responseBody, messageocr, stateocr, ocrdate, timeOcrhit

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
		
		if (useCorrectKey != 'Yes') {
			thekey = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Wrong Key'))
		}
		if (useCorrectTenant != 'Yes') {
			tenantcode = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Wrong TenantCode'))
		}
		
		'lakukan proses HIT api dengan parameter image, key, dan juga tenant'
		response = WS.sendRequest(findTestObject('Object Repository/OCR Testing/New API/BPKB',
		[
			('page2'): findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('$halaman1')),
			('page3'): findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('$halaman2')),
			('key'):thekey,
			('tenant'):tenantcode,
			('custno'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Customer No.')),
		]))
		
		'ambil lama waktu yang diperlukan hingga request menerima balikan'
		String elapsedTime = (response.elapsedTime) / 1000 + ' second'
		
		'ambil message respon dari HIT tersebut'
		messageocr = WS.getElementPropertyValue(response, 'message', FailureHandling.CONTINUE_ON_FAILURE)
					
		'ambil status dari respon HIT tersebut'
		stateocr = WS.getElementPropertyValue(response, 'status', FailureHandling.CONTINUE_ON_FAILURE)
		
		'ambil status dari respon HIT tersebut'
		ocrdate = WS.getElementPropertyValue(response, 'ocr_date', FailureHandling.CONTINUE_ON_FAILURE)
		
		'write to excel response elapsed time'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 1, GlobalVariable.NumOfColumn -
			1, elapsedTime.toString())
			
		if (stateocr == null || stateocr == '') {
			'write to excel status failed dan reason'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
					'Terdapat unidentified "Nan", tidak bisa tulis respons ke excel')
			
			continue
		}
		
		'Jika status HIT API 200 atau 500 dan tidak menggunakan key atau tenant invalid'
		if (!stateocr.equalsIgnoreCase('key or tenant invalid') &&
			((WS.verifyResponseStatusCode(response, 200, FailureHandling.OPTIONAL) == true) ||
				(WS.verifyResponseStatusCode(response, 500, FailureHandling.OPTIONAL) == true))) {
			'ambil body dari hasil respons'
			responseBody = response.responseBodyContent
			
			'ambil waktu hit untuk sebagai acuan nama file log'
			timeOcrhit = processHourOnly(ocrdate)
			
			'write to excel status'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Status') - 1, GlobalVariable.NumOfColumn -
				1, stateocr)
			
			'panggil keyword untuk proses beautify dari respon json yang didapat'
			CustomKeywords.'parsejson.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1,
				findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Scenario')))
			
			'pengecekan value expected dan respons dari OCR'
			CustomKeywords.'ocrtesting.ResponseChecking.verifyValueDifference'(
				findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Expected Response')),
					findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Respons')),
						sheet, rowExcel('Difference Checking') - 1)
			
			if (stateocr.equalsIgnoreCase('Success') && useCorrectKey != 'Yes' && useCorrectTenant != 'Yes') {
				'write to excel status failed dan reason'
				CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
						GlobalVariable.FailedReasonKeyTenantBypass)
			} else if (stateocr.equalsIgnoreCase('Failed')) {
				GlobalVariable.FlagFailed = 1
				'write to excel status failed dan reason'
				CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
						'<' + messageocr + '>')
			}
			
			'jika perlu cek log dijalankan'
			if (GlobalVariable.checkLog == 'Yes') {
				'jika browser belum pernah dibuka'
				if (firstRun == 0) {
					'panggil testcase open browser'
					WebUI.callTestCase(findTestCase('OCR Testing/API Only/OpenBrowserMultiTab'), [:])
					
					firstRun = 1
				}
				'panggil testcase open browser'
				WebUI.callTestCase(findTestCase('OCR Testing/API Only/CheckLog'), [('OCRType') : 'BPKBExtractor',
					('Tanggal') : tanggal, ('TenantCode') : tenantcode, ('TimeOCR') : timeOcrhit, ('sheet') : sheet,
					('ExcelPathOCRTesting') : ExcelPathOCRTesting,])
			}
			
			'jika expected response tidak sesuai response'
			if (GlobalVariable.FlagFailed != 0) {
				'write to excel status failed dan reason'
				CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
						'Expected tidak sesuai Response yang didapat')
			}
		} else {
			'jika param message null'
			if (messageocr == null) {
				'pindahkan value di status ke message'
				messageocr = stateocr
				
				'hardcode status yang kosong'
				stateocr = 'FAILED'
			}
			
			'Write To Excel GlobalVariable.StatusFailed and errormessage'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				stateocr, '<' + messageocr + '>')
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
