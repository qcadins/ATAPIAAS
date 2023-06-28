import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

'angka untuk menghitung data mandatory yang tidak terpenuhi'
int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 5))

'klik pada tombol garis tiga'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/i_KEPIN EDGAR_ft-menu font-medium-3'))

WebUI.delay(1)

'klik pada API KEY'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/span_API Key'))

'klik tombol +Baru'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/a_Baru'))

'klik tombol batal'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Add Api Key/button_Batal'))

'klik tombol +Baru'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/a_Baru'))

'input data API KEY name'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Add Api Key/input__apiKeyName'), findTestData(ExcelPathAPIKey).getValue(
        GlobalVariable.NumOfColumn, 11))

'pilih jenis API KEY'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Add Api Key/select_tipeAPI'), findTestData(ExcelPathAPIKey).getValue(
        GlobalVariable.NumOfColumn, 12))

WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Add Api Key/select_tipeAPI'), Keys.chord(Keys.ENTER), FailureHandling.OPTIONAL)

'klik pada tombol simpan'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Add Api Key/button_Simpan'))

WebUI.delay(3)

'verifikasi tombol "YA" terdapat di layar'
if (WebUI.verifyElementPresent(TombolYes, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
	'klik pada button YA jika muncul pop-up'
	WebUI.click(TombolYes)
}

'get failed reason'
reason = WebUI.getText(FailedReason)

if (reason == 'Success') {
	'cek ke DB jika memang diperlukan'
	if (GlobalVariable.KondisiCekDB == 'Yes') {
		'verifikasi ke database untuk data yang ditambahkan'
		WebUI.callTestCase(findTestCase('Test Cases/API Key/AddKeyStoreDBVerif'), [:], FailureHandling.STOP_ON_FAILURE)
	}
	
	'cek apakah muncul error setelah add api key'
	if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'), GlobalVariable.Timeout,
		FailureHandling.OPTIONAL) == false) {
		GlobalVariable.FlagFailed = 1
	
		'tulis adanya error pada sistem web'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('API KEY', GlobalVariable.NumOfColumn, GlobalVariable.StatusWarning,
			(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.FailedReasonUnknown)
	}
} else {
	'get failed reason'
	reason = WebUI.getText(FailedReason)
	
	'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
	CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('API KEY', GlobalVariable.NumOfColumn,
	GlobalVariable.StatusFailed, (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 2) + ';') + reason)
	
	GlobalVariable.FlagFailed = 1
}

//WebUI.delay(GlobalVariable.Timeout)

//'jika button ok muncul, tulis ke excel tidak gagal'
//CustomKeywords.'writeToExcel.CheckSaveProcess.checkStatus'(isMandatoryComplete, findTestObject('Object Repository/API_KEY/Page_Add Api Key/button_OK'), 
//    GlobalVariable.NumOfColumn, 'API KEY')

'kondisi jika tidak ada tombol ok, tc masih bisa dilanjutkan'
if (WebUI.verifyElementPresent(ButtonOK, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
    'klik tombol ok pada success alert'
    WebUI.click(ButtonOK)
}

