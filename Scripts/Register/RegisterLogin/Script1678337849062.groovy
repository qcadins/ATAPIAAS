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

for (GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn<= CountColumnEdit; GlobalVariable.NumOfColumn++)
{
	'memanggil fungsi untuk login'
	WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), ["TC":'Regist'], FailureHandling.STOP_ON_FAILURE)
	
	//WebUI.click(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/div_reCAPTCHA_recaptcha-checkbox-border (1)'))
	'pada delay ini, bisa melakukan captcha secara manual, karena automation testing dianggap sebagai robot'
	WebUI.delay(20)
	
	'hover pointer ke button buat akun'
	WebUI.focus(findTestObject('Eendigo/Page_Login - eendigo Platform/button_Buat Akun Anda Sekarang'))
	
	'simpan data email dari testdata'
	String email = findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 8)
	
	'kondisi jika button create akun bisa di-klik'
	if (WebUI.verifyElementClickable(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/button_Buat Akun Anda Sekarang'),FailureHandling.OPTIONAL)) 
	{
		'klik pada button buat akun'
		WebUI.click(findTestObject('Eendigo/Page_Login - eendigo Platform/button_Buat Akun Anda Sekarang'))
		
		WebUI.delay(5)
		
		'mengambil otp dari db, disimpan ke iniotp'
		ArrayList<String> iniotp = CustomKeywords.'otp.getOTPfromDB.getOTPforRegister'(conn, email)
		
		'input otp dari DB'
		WebUI.setText(findTestObject('Eendigo/Page_Login - eendigo Platform/input_concat(id(, , otp, , ))_otp'), iniotp[0])
		
		WebUI.delay(5)
		
		'klik pada button verifikasi otp'
		WebUI.click(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/button_Verifikasi'))
		
		WebUI.delay(3)
		
		'klik button Masuk'
		WebUI.click(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/div_Masuk'))
		
		'input email yang sudah diregist pada field'
		WebUI.setText(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/input_Buat Akun_form-control is-invalid ng-_7788b4'),
			findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 8))
		
		'input password yang sudah diregist ke field'
		WebUI.setText(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/input_Buat Akun_form-control is-invalid ng-_7788b4_1_2'),
			findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 10))
		
	//	WebUI.click(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/div_reCAPTCHA_recaptcha-checkbox-border (1)'))
		WebUI.delay(10)
		
		'kondisi jika button login clickable'
		if (WebUI.verifyElementClickable(findTestObject('Eendigo/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'), FailureHandling.OPTIONAL) == true)
		{	
			'klik pada button login'
			WebUI.click(findTestObject('Eendigo/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))
		}
		//kondisi dibawah jika button tidak clickable
		else
		{
			'tulis status dan reason error ke excel'
			CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('Register', GlobalVariable.NumOfColumn, GlobalVariable.Failed,
			(findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.FailedReasonSubmitError)
		}
	} 
	else 
	{
		'buat flag failed menjadi 1 agar tidak menulis status sukses pada excel'
		GlobalVariable.FlagFailed = 1
		
		'tulis error ke excel'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('Register', GlobalVariable.NumOfColumn, GlobalVariable.Failed,
			(findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.FailedReasonSubmitError)
	}
	WebUI.closeBrowser()
}
