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

WebUI.openBrowser('')

WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 2))

WebUI.click(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/div_Buat Akun'))

WebUI.setText(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/input_Buat Akun_form-control is-invalid ng-_7788b4'), 
    findTestData('DataRegistLogin').getValue(2, 8))

WebUI.setText(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/input_Buat Akun_form-control is-invalid ng-_7788b4_1'), 
    findTestData('DataRegistLogin').getValue(2, 9))

WebUI.setText(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/input_Buat Akun_form-control is-invalid ng-_7788b4_1_2'), 
    findTestData('DataRegistLogin').getValue(2, 10))

WebUI.setText(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/input_Buat Akun_form-control is-invalid ng-_7788b4_1_2_3'), 
    findTestData('DataRegistLogin').getValue(2, 11))

WebUI.click(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/div_reCAPTCHA_recaptcha-checkbox-border (1)'))

WebUI.delay(2)

WebUI.focus(findTestObject('Eendigo/Page_Login - eendigo Platform/button_Buat Akun Anda Sekarang'))

WebUI.click(findTestObject('Eendigo/Page_Login - eendigo Platform/button_Buat Akun Anda Sekarang'))

WebUI.click(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/div_Masuk'))

WebUI.setText(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/input_Buat Akun_form-control is-invalid ng-_7788b4'), 
    findTestData('DataRegistLogin').getValue(2, 12))

WebUI.setText(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/input_Buat Akun_form-control is-invalid ng-_7788b4_1_2'), 
    findTestData('DataRegistLogin').getValue(2, 13))

WebUI.click(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/div_reCAPTCHA_recaptcha-checkbox-border (1)'))

WebUI.click(findTestObject('Eendigo/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))

WebUI.closeBrowser()

