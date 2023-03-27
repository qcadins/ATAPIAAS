import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.main.CustomKeywordDelegatingMetaClass as CustomKeywordDelegatingMetaClass
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import com.kms.katalon.entity.global.GlobalVariableEntity as GlobalVariableEntity
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.By as By
import org.openqa.selenium.support.ui.Select as Select
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement

'ambil driver chrome'
def driver = DriverFactory.getWebDriver()

'panggil java script executor'
def js = (JavascriptExecutor)driver

'cek apakah mandatory lengkap pada excel'
int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 4))

'klik pada tombol garis tiga'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/i_KEPIN EDGAR_ft-menu font-medium-3'))

WebUI.delay(1)

'klik pada API KEY'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/span_API Key'))

'klik pada tombol edit API'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/em_Aksi_align-middle cursor-pointer font-me_8c8f9d'))

'klik pada panah ddl Status API'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/span_Inactive_ng-arrow-wrapper'))

if(GlobalVariable.KondisiCekDB == 'Yes')
{
	'verifikasi data ke DB sebelum diedit'
	WebUI.callTestCase(findTestCase('Test Cases/API Key/VerifyDataAPIKey'), [:], FailureHandling.STOP_ON_FAILURE)
}

'input nama API'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/input__apiKeyName'), findTestData(ExcelPathAPIKey).getValue(
        GlobalVariable.NumOfColumn, 12))

'cek kondisi status input pada database'
if(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 13) == 'ACTIVE')
{
	'pilih status active'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/span_Active'))
}
else
{
	'pilih status inactive'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/span_Inactive'))
}

'klik tombol untuk simpan ubahan'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/button_Lanjut'))

'cek jika adanya tombol "YA" pada popup'
if (WebUI.verifyElementPresent(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/button_Ya'), GlobalVariable.Timeout, FailureHandling.CONTINUE_ON_FAILURE)) 
{
	'klik pada tombol ya'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/button_Ya'))
	
	'cek ke DB jika memang diperlukan'
	if(GlobalVariable.KondisiCekDB == 'Yes')
	{
		'verifikasi data ke db setelah di edit'
		WebUI.callTestCase(findTestCase('Test Cases/API Key/EditKeyStoreDBVerif'), [:], FailureHandling.STOP_ON_FAILURE)
	}
}

WebUI.delay(2)

'periksa status edit dan tulis ke excel'
CustomKeywords.'writeToExcel.checkSaveProcess.checkAlert'(GlobalVariable.NumOfColumn, 'API KEY')

'kondisi jika tidak ada tombol ok, tc masih bisa dilanjutkan'
if (WebUI.verifyElementPresent(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/button_OK_success'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) 
{
	'klik tombol ok'
    WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/button_OK_success'))
}
//kondisi untuk tombol ok jika edit error
else if(WebUI.verifyElementPresent(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/button_OK_gagal'), GlobalVariable.Timeout, FailureHandling.CONTINUE_ON_FAILURE))
{
	'klik tombol ok'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/button_OK_gagal'))
}