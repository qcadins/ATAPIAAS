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
int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('Mandatory Complete')))

'klik pada tombol garis tiga'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/spanMenu'))

WebUI.delay(1)

'klik pada API KEY'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/span_API Key'))

'cek apakah tombol menu dalam jangkauan web'
if (WebUI.verifyElementVisible(
	findTestObject('Object Repository/User Management-Role/Page_List Roles/tombolX_menu'), FailureHandling.OPTIONAL)) {
	
	'klik pada tombol silang menu'
	WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/tombolX_menu'))
}

'jika merupakan case awal'
if (GlobalVariable.NumOfColumn == 2) {
	
	'klik tombol +Baru'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/a_Baru'))
	
	'klik tombol batal'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Add Api Key/button_Batal'))
}

'klik tombol +Baru'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/a_Baru'))

'verify element field kosong'
checkVerifyEqualOrMatch(WebUI.verifyElementAttributeValue(findTestObject('Object Repository/API_KEY/Page_Add Api Key/input__apiKeyName'),
	'class', 'form-control ng-untouched ng-pristine ng-invalid',
	GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Field add tidak kosong setelah klik cancel')

'input data API KEY name'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Add Api Key/input__apiKeyName'), findTestData(ExcelPathAPIKey).getValue(
        GlobalVariable.NumOfColumn, rowExcel('$Nama API KEY')))

'pilih jenis API KEY'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Add Api Key/select_tipeAPI'), findTestData(ExcelPathAPIKey).getValue(
        GlobalVariable.NumOfColumn, rowExcel('$Tipe API KEY')))

WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Add Api Key/select_tipeAPI'), Keys.chord(Keys.ENTER), FailureHandling.OPTIONAL)

'jika tombol simpan di disabled'
if (WebUI.verifyElementHasAttribute(findTestObject('Object Repository/API_KEY/Page_Add Api Key/button_Simpan'), 'disabled', GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
	
	'Write to excel status failed mandatory'
	CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
	GlobalVariable.StatusFailed, (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') + GlobalVariable.FailedReasonMandatory)
	
	GlobalVariable.FlagFailed = 1
	
} else {
	'klik pada tombol simpan'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Add Api Key/button_Simpan'))
	
	WebUI.delay(3)
	
	'verifikasi tombol "YA" terdapat di layar'
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/API_KEY/Page_Add Api Key/button_Ya'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		'klik pada button YA jika muncul pop-up'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Add Api Key/button_Ya'))
	}
	
	String reason
	
	'kondisi jika tidak ada tombol ok, tc masih bisa dilanjutkan'
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/API_KEY/Page_Add Api Key/button_OK'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		'get failed reason'
		reason = WebUI.getText(findTestObject('Object Repository/API_KEY/Page_Add Api Key/label_FailedReason'))
		
		'klik tombol ok pada success alert'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Add Api Key/button_OK'))
	}
	
	if (reason == 'Success') {
		'cek ke DB jika memang diperlukan'
		if (GlobalVariable.KondisiCekDB == 'Yes') {
			'verifikasi ke database untuk data yang ditambahkan'
			WebUI.callTestCase(findTestCase('Test Cases/API Key/APIKeyStoreDB'), [('Case'): 'Add'], FailureHandling.STOP_ON_FAILURE)
		}
		
		'cek apakah muncul error setelah add api key'
		if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'), GlobalVariable.Timeout,
			FailureHandling.OPTIONAL) == false) {
		
			'tulis adanya error pada sistem web'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, GlobalVariable.StatusWarning,
				(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') + GlobalVariable.FailedReasonUnknown)
		}
		
		searchAPIKEY()
		
		'verify nama api key'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/API_KEY/label_NamaAPIKey')), findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('$Nama API KEY')), false, FailureHandling.CONTINUE_ON_FAILURE), ' nama api key')
	
		'verify nama tipe api key'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/API_KEY/label_TipeAPIKey')), findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('$Tipe API KEY')), false, FailureHandling.CONTINUE_ON_FAILURE), ' tipe api key')
	
	} else {
		
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') + '<' + reason + '>')
		
		GlobalVariable.FlagFailed = 1
	}
}

'fungsi untuk melakukan pengecekan '
def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
	if ((isMatch == false)) {
		
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
			(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') + GlobalVariable.FailedReasonVerifyEqualorMatch + + '<' + reason + '>')

		GlobalVariable.FlagFailed = 1
	}
}

def searchAPIKEY() {
	'input tipe API'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Api Key List/input_tipeapi_list'), findTestData(
			ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('$Tipe API KEY')))
	
	'select tipe API'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Api Key List/input_tipeapi_list'), Keys.chord(
			Keys.ENTER))
	
	'input status API'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Api Key List/input_statusapi_list'), findTestData(
			ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('SearchStatusAPI')))
	
	'select status API'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Api Key List/input_statusapi_list'), Keys.chord(
			Keys.ENTER))
	
	'klik pada button cari'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/button_Cari'))
}

def rowExcel(String cellValue) {
	return CustomKeywords.'writetoexcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}