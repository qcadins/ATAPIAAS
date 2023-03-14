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

def conn = CustomKeywords.'dbConnection.connect.connectDBAPIAAS'()

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.writeExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Edit Profile'
int CountColumnEdit = findTestData(ExcelPathRegisterLogin).getColumnNumbers()

WebUI.openBrowser('')

WebUI.navigateToUrl('http://gdkwebsvr:4100/login')

WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_Buat Akun_form-control ng-untouched n_ab9ed8'), 
    'kvnedgar@adin.com')

WebUI.setEncryptedText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_Buat Akun_form-control ng-untouched n_dd86a2'), 
    'AYp7F1OGEOzMyOyVsOQTMQ==')

WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/div_id(katalon-rec_elementInfoDiv)'))

WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/img_motor_rc-image-tile-33'))

WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/img_motor_rc-image-tile-33'))

WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/img_motor_rc-image-tile-33'))

WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/button_Verifikasi'))

WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))

WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/a_Baru'))

WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Add Api Key/input__apiKeyName'), 'Terserah')

WebUI.selectOptionByValue(findTestObject('Object Repository/API_KEY/Page_Add Api Key/select_Tipe API KeyPRODUCTIONTRIAL'), 
    'PRODUCTION', true)

WebUI.click(findTestObject('Object Repository/API_KEY/Page_Add Api Key/button_Simpan'))

WebUI.click(findTestObject('Object Repository/API_KEY/Page_Add Api Key/button_Ya'))

