import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/i_KEPIN EDGAR_ft-menu font-medium-3'))

WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/span_API Key'))

WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/em_Aksi_align-middle cursor-pointer font-me_8c8f9d'))

WebUI.callTestCase(findTestCase('Test Cases/API_Testing/VerifyDBbeforeEditKey'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/input__apiKeyName'), findTestData(ExcelPathAPIKey).getValue(
        GlobalVariable.NumOfColumn, 12))

WebUI.selectOptionByLabel(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/div_Status API Key Enter activation statusI_e6aed9'), 
    findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 13), false)

WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/input'))

WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/button_Lanjut'))

WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/button_Ya'))

if(WebUI.verifyElementPresent('Object Repository/API_KEY/Page_Edit Api Key/button_OK', GlobalVariable.Timeout))
{
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/button_OK'))
}

WebUI.callTestCase(findTestCase('Test Cases/API_Testing/VerifyDBafterEditKey'), [:], FailureHandling.STOP_ON_FAILURE)

