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

int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 4))

String optiontipe

String optionstatus

String totaldata

'klik pada tombol garis tiga'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/i_KEPIN EDGAR_ft-menu font-medium-3'))

WebUI.delay(1)

'klik pada API KEY'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/span_API Key'))

WebUI.delay(2)

'cek apakah perlu klik tombol X'
if (WebUI.verifyElementPresent(findTestObject('Object Repository/API_KEY/Page_Api Key List/i_Keluar_ft-x ng-tns-c133-1'), 
    GlobalVariable.Timeout, FailureHandling.CONTINUE_ON_FAILURE)) {
    'klik tombol x pada menu eendigo'
    WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/i_Keluar_ft-x ng-tns-c133-1'))
}

'klik ddl tipe API KEY'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/span_All_ng-arrow-wrapper'))

'pilih production'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/span_PRODUCTION'))

'klik tombol cari'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/button_Cari'))

'klik ddl Status API KEY'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/span_All_ng-arrow-wrapper_1'))

'pilih active'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/span_Active'))

'klik tombol cari'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/button_Cari'))

'klik ddl status API KEY'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/span_All_ng-arrow-wrapper_1'))

'pilih inactive'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/span_Inactive'))

'klik tombol cari'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/button_Cari'))

'klik pada ddl tipe API KEY'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/span_All_ng-arrow-wrapper'))

'pilih TRIAL'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/span_TRIAL'))

'klik pada ddl Status API KEY'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/span_All_ng-arrow-wrapper_1'))

'pilih ALL'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/span_All'))

'klik tombol cari'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/button_Cari'))

'klik pada ddl Status API KEY'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/span_All_ng-arrow-wrapper_1'))

'pilih active'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/span_Active'))

'klik tombol cari'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/button_Cari'))

'klik pada ddl Status API KEY'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/span_All_ng-arrow-wrapper_1'))

'pilih inactive'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/span_Inactive'))

'klik tombol cari'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/button_Cari'))

'klik tombol set ulang'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/button_Set Ulang'))

'klik pada ddl tipe API KEY'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/span_All_ng-arrow-wrapper'))

'simpan pilihan utama dari tipe API KEY'
optiontipe = WebUI.getAttribute(findTestObject('Object Repository/API_KEY/Page_Api Key List/input tipe'), 'aria-activedescendant')

'klik pada ddl Status API KEY'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/span_All_ng-arrow-wrapper_1'))

'simpan pilihan utama dari status API KEY'
optionstatus = WebUI.getAttribute(findTestObject('Object Repository/API_KEY/Page_Api Key List/input status'), 'aria-activedescendant')

'klik pada ddl Status API KEY'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/span_All_ng-arrow-wrapper_1'))

'jika semua pilihan ddl kembali ke "ALL"'
if (optiontipe.contains('-0') && optionstatus.contains('-0')) {
    'klik tombol cari'
    WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/button_Cari'))

    'tulis kondisi success atau failed'
    CustomKeywords.'writeToExcel.checkSaveProcess.checkStatus'(isMandatoryComplete, findTestObject('Object Repository/API_KEY/Page_Api Key List/p_MAMANK'), 
        GlobalVariable.NumOfColumn, 'API KEY')
}

'verifikasi jumlah baris di DB dan di WEB'
WebUI.callTestCase(findTestCase('Test Cases/API_Testing/VerifyTotalAPIList'), [:], FailureHandling.STOP_ON_FAILURE)

'klik tombol COPY LINK'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/em_Aksi_align-middle cursor-pointer font-medium-3 ft-copy'))

'verifikasi copy berhasil'
CustomKeywords.'writeToExcel.checkSaveProcess.checkStatus'(isMandatoryComplete, findTestObject('Object Repository/API_KEY/Page_Api Key List/div_API Key copied to clipboard'), 
    GlobalVariable.NumOfColumn, 'API KEY')

WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/i_Aksi_datatable-icon-right'))

WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/i_Aksi_datatable-icon-left'))

WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/a_2'))

WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/a_1'))

WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/i_Aksi_datatable-icon-skip'))

WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/i_Aksi_datatable-icon-prev'))

