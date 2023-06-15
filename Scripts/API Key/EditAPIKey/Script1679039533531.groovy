import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'klik pada tombol garis tiga'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/i_KEPIN EDGAR_ft-menu font-medium-3'))

WebUI.delay(1)

'klik pada API KEY'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/span_API Key'))

'klik pada tombol edit API'
WebUI.click(
	findTestObject('Object Repository/API_KEY/Page_Api Key List/em_Aksi_align-middle cursor-pointer font-me_8c8f9d'))

'klik pada panah ddl Status API'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/span_Inactive_ng-arrow-wrapper'))

if (GlobalVariable.KondisiCekDB == 'Yes') {
	
	'verifikasi data ke DB sebelum diedit'
	WebUI.callTestCase(findTestCase('Test Cases/API Key/VerifyDataAPIKey'), [:], FailureHandling.STOP_ON_FAILURE)
}

'input nama API'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/input__apiKeyName'), 
	findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 13))

'cek kondisi status input pada database'
if(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 14) == 'ACTIVE'){
	
	'pilih status active'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/span_Active'))
}
else{
	
	'pilih status inactive'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/span_Inactive'))
}

'klik tombol untuk simpan ubahan'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/button_Lanjut'))

'cek jika adanya tombol "YA" pada popup'
if (WebUI.verifyElementPresent(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/button_Ya'), 
		GlobalVariable.Timeout, FailureHandling.CONTINUE_ON_FAILURE)) {
	
	'klik pada tombol ya'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/button_Ya'))
}

'cek apakah muncul error setelah edit api key'
if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'),
	GlobalVariable.Timeout, FailureHandling.OPTIONAL) == false) {
	
	GlobalVariable.FlagFailed = 1
	
	'tulis adanya error pada sistem web'
	CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('API KEY', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusWarning, (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
			GlobalVariable.FailedReasonUnknown)
}

WebUI.delay(2)

'periksa status edit dan tulis ke excel'
CustomKeywords.'writeToExcel.CheckSaveProcess.checkAlert'(GlobalVariable.NumOfColumn, 'API KEY')

'kondisi jika tidak ada tombol ok, tc masih bisa dilanjutkan'
if (WebUI.verifyElementPresent(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/button_OK_success'), 
		GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
	
	'klik tombol ok'
    WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/button_OK_success'))
	
	'cek ke DB jika memang diperlukan'
	if (GlobalVariable.KondisiCekDB == 'Yes') {
		
		'verifikasi data ke db setelah di edit'
		WebUI.callTestCase(findTestCase('Test Cases/API Key/EditKeyStoreDBVerif'), [:],
			FailureHandling.STOP_ON_FAILURE)
	}
}
//kondisi untuk tombol ok jika edit error
else if(WebUI.verifyElementPresent(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/button_OK_gagal'), 
			GlobalVariable.Timeout, FailureHandling.CONTINUE_ON_FAILURE)){
		
	'klik tombol ok'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/button_OK_gagal'))
}