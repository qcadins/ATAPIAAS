import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.configuration.RunConfiguration as RunConfiguration
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.WebDriver as WebDriver

int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathAPIDocs).getValue(GlobalVariable.NumOfColumn, 4))

String namadokumentasi = findTestData(ExcelPathAPIDocs).getValue(GlobalVariable.NumOfColumn, 8)

String FlagDelete = findTestData(ExcelPathAPIDocs).getValue(GlobalVariable.NumOfColumn, 9)

String userDir = System.getProperty('user.dir')

String filePath = userDir + '\\Download'

WebDriver driver = DriverFactory.getWebDriver()

'Wait for Some time so that file gets downloaded and Stored in user defined path'
WebUI.delay(5)

'klik pada tombol garis tiga'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/i_KEPIN EDGAR_ft-menu font-medium-3'))

WebUI.delay(1)

'klik pada menu dokumentasi API'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/span_Dokumentasi API'))

'input jensi dokumentasi yang akan diupload'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_API Documentation/input'), namadokumentasi)

'select status API'
WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_API Documentation/input'), Keys.chord(Keys.ENTER))

'klik pada tombol unduh'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_API Documentation/button_Unduh'))

WebUI.delay(GlobalVariable.Timeout)

boolean isDownloaded = CustomKeywords.'apikey.checkDownloadedFiles.isFileDownloaded'(FlagDelete)

'jika file tidak terunduh, tulis gagal'
if (WebUI.verifyEqual(isDownloaded, true, FailureHandling.OPTIONAL)) {
    'tulis status sukses pada excel'
    CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('Documentation', GlobalVariable.NumOfColumn, GlobalVariable.StatusSuccess, 
        GlobalVariable.SuccessReason)
} else {
    GlobalVariable.FlagFailed = 1

    'tulis kondisi gagal'
    CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('Documentation', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed, 
        GlobalVariable.FailedReasonDownloadProblem)
}

