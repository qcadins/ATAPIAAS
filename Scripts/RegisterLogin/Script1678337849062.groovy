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

WebUI.openBrowser('')

WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 2))

'mendapat jumlah kolom dari sheet Edit Profile'
int CountColumnEdit = findTestData(ExcelPathRegisterLogin).getColumnNumbers()

WebUI.click(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/div_Buat Akun'))

WebUI.setText(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/input_Buat Akun_form-control is-invalid ng-_7788b4'), 
    findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 8))

WebUI.setText(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/input_Buat Akun_form-control is-invalid ng-_7788b4_1'), 
    findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 9))

WebUI.setText(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/input_Buat Akun_form-control is-invalid ng-_7788b4_1_2'), 
    findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 10))

WebUI.setText(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/input_Buat Akun_form-control is-invalid ng-_7788b4_1_2_3'), 
    findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 11))

//WebUI.click(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/div_reCAPTCHA_recaptcha-checkbox-border (1)'))
WebUI.delay(20)

WebUI.focus(findTestObject('Eendigo/Page_Login - eendigo Platform/button_Buat Akun Anda Sekarang'))

WebUI.click(findTestObject('Eendigo/Page_Login - eendigo Platform/button_Buat Akun Anda Sekarang'))

String email = findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 8)

int iniotp = CustomKeywords.'otp.getDatafromDB.getDBdata'(conn, email)

if(WebUI.verifyElementNotPresent('Eendigo/Page_Login - eendigo Platform/input_concat(id(, , otp, , ))_otp', GlobalVariable.Timeout))
	
{
	CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('Register', GlobalVariable.NumOfColumn, GlobalVariable.Failed,
		(findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.FailedReasonSubmitError)
}
else
{
	WebUI.setText(findTestObject('Eendigo/Page_Login - eendigo Platform/input_concat(id(, , otp, , ))_otp'), iniotp)
}

WebUI.click(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/div_Masuk'))

WebUI.setText(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/input_Buat Akun_form-control is-invalid ng-_7788b4'), 
    findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 12))

WebUI.setText(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/input_Buat Akun_form-control is-invalid ng-_7788b4_1_2'), 
    findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 13))

WebUI.click(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/div_reCAPTCHA_recaptcha-checkbox-border (1)'))

WebUI.click(findTestObject('Eendigo/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))

WebUI.closeBrowser()

