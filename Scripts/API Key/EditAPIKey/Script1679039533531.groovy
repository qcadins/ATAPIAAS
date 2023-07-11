import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import org.openqa.selenium.Keys

import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

//'klik pada tombol garis tiga'
//WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/expandMenu'))
//
//WebUI.delay(1)
//
//'klik pada API KEY'
//WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/span_API Key'))
//
//WebUI.delay(2)

'klik pada tombol edit API'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/buttonEditAPI'))

'klik tombol batal'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/button_Batal'))

'search api yang mau di edit'
searchAPIKEY(16)

'klik pada tombol edit API'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/buttonEditAPI'))

'klik pada panah ddl Status API'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/span_Inactive_ng-arrow-wrapper'))

if (GlobalVariable.KondisiCekDB == 'Yes') {
	
	'verifikasi data ke DB sebelum diedit'
	WebUI.callTestCase(findTestCase('Test Cases/API Key/VerifyDataAPIKey'), [:], FailureHandling.STOP_ON_FAILURE)
}

'input nama API'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/input__apiKeyName'), 
	findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 13))

'pilih status'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/input'), findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 14))

'select status'
WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/input'), Keys.chord(
		Keys.ENTER))

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
	
	'search api key yang sudah di edit'
	searchAPIKEY(14)
	
	'verify nama api key'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/API_KEY/label_NamaAPIKey')), findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 11), false, FailureHandling.CONTINUE_ON_FAILURE), ' nama api key')

	'verify nama tipe api key'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/API_KEY/label_TipeAPIKey')), findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 12), false, FailureHandling.CONTINUE_ON_FAILURE), ' tipe api key')
	
//kondisi untuk tombol ok jika edit error
}else if(WebUI.verifyElementPresent(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/button_OK_gagal'), 
			GlobalVariable.Timeout, FailureHandling.CONTINUE_ON_FAILURE)){
		
	'klik tombol ok'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/button_OK_gagal'))
}

'fungsi untuk melakukan pengecekan '
def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
	if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
		
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('API KEY', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
			(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.FailedReasonVerifyEqualorMatch + reason)

		GlobalVariable.FlagFailed = 1
	}
}

def searchAPIKEY(int row) {
	'input tipe API'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Api Key List/input_tipeapi_list'), findTestData(
			ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 15))
	
	'select tipe API'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Api Key List/input_tipeapi_list'), Keys.chord(
			Keys.ENTER))
	
	'input status API'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Api Key List/input_statusapi_list'), findTestData(
			ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, row))
	
	'select status API'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Api Key List/input_statusapi_list'), Keys.chord(
			Keys.ENTER))
	
	'klik pada button cari'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/button_Cari'))
}