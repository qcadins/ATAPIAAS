import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.testobject.ResponseObject
import java.sql.Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writetoexcel.WriteExcel.getExcelPath'('/1. Login.xlsm')

sheet = 'Link Base Url'

'get base url'
GlobalVariable.BaseUrl =  findTestData('Login/BaseUrl').getValue(2, rowExcel('dukcapil UAT - Non Biometrik'))

'mencari directory excel utama'
GlobalVariable.DataFilePath = CustomKeywords.'writetoexcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

sheet = 'Dukcapil(NonBiom)'

'mendapat jumlah kolom dari sheet Edit Profile'
int countColumnEdit = findTestData(ExcelPathOCRTesting).columnNumbers

'deklarasi variabel untuk konek ke Database eendigo_dev'
Connection conn = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_public'()

'deklarasi string hasil respons'
String responseBody, message_ocr, state_ocr

'pindah testcase sesuai jumlah di excel'
for(GlobalVariable.NumOfColumn = 2; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++){
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
		response = WS.sendRequest(findTestObject('Object Repository/OCR Testing/dukcapil UAT - Biometrik',
		[('img'): findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('$selfiephoto')),
		('tenant'):tenantcode,
		('key'):thekey,
		('loginId'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('LoginID')),
		('refNum'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('refNumber')),
		('off_code'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('OfficeCode')),
		('off_name'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('OfficeName')),
		('phoneNum'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('$mobilephone')),
		('idNo'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('$idNumber')),
		('fullName'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('$fullName')),
		('DOB'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('$tanggalLahir')),
		('email'):findTestData(ExcelPathOCRTesting).getValue(2, rowExcel('UsernameLogin'))
		]))
			
		'ambil message respon dari HIT tersebut'
		message_ocr = WS.getElementPropertyValue(response, 'message')
		
		'ambil status dari respon HIT tersebut'
		state_ocr = WS.getElementPropertyValue(response, 'status')
		
		'Jika status HIT API 200 OK'
		if (WS.verifyResponseStatusCode(response, 200, FailureHandling.OPTIONAL) == true) {
			
			'ambil body dari hasil respons'
			responseBody = response.getResponseBodyContent()
			
			'write to excel status'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Status') - 1, GlobalVariable.NumOfColumn -
				1, state_ocr)
			
			'write to excel message'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Reason failed') - 1, GlobalVariable.NumOfColumn -
				1, message_ocr)
			
			'panggil keyword untuk proses beautify dari respon json yang didapat'
			CustomKeywords.'parsejson.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1,
				findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Scenario')))
			
			if (state_ocr == '0' && useCorrectKey != 'Yes' && useCorrectTenant != 'Yes') {
				'write to excel status failed dan reason'
				CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
				GlobalVariable.FailedReasonKeyTenantBypass)
					
				continue;
				
			} else if (message_ocr == 'ID has been checked.' && state_ocr == 0 && verifState_ocr == true) {
				'tulis status sukses pada excel'
				CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet,
					GlobalVariable.NumOfColumn, GlobalVariable.StatusSuccess,
						'<' + message_ocr + '>')
			
			} else {
				GlobalVariable.FlagFailed = 1
				'write to excel status failed dan reason'
				CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
				'<' + message_ocr + '>')
			}
		} else {

            'Write To Excel GlobalVariable.StatusFailed and errormessage'
            CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, 
                GlobalVariable.StatusFailed, '<' + message_ocr + '>')
        }
	}
}

def rowExcel(String cellValue) {
	return CustomKeywords.'writetoexcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}