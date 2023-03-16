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
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

def conn = CustomKeywords.'dbConnection.connect.connectDBAPIAAS_esign'()

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.writeExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Edit Profile'
int CountColumnEdit = findTestData(ExcelPathRegisterLogin).getColumnNumbers()

'angka untuk menghitung data mandatory yang tidak terpenuhi'
int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 4))

'angka untuk mendapat otp salah dari excel'
List<Integer> otpmanual = new ArrayList<Integer>()
otpmanual.add(OTPfromExcel)

'banyaknya resend otp yang akan dilakukan'
int countresend = Integer.parseInt(CountResendOTP)

'simpan data email dari testdata'
String email = findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 8)

for (GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn<= CountColumnEdit; GlobalVariable.NumOfColumn++)
{
	'memanggil fungsi untuk login'
	WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), ["TC":'Regist'], FailureHandling.STOP_ON_FAILURE)
	
	CustomKeywords.'writeToExcel.checkSaveProcess.checkStatusbtnClickable'(isMandatoryComplete, findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/button_Buat Akun Anda Sekarang'), GlobalVariable.NumOfColumn, 'Register')
	
	'hover pointer ke button buat akun'
	WebUI.focus(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/button_Buat Akun Anda Sekarang'))
	
	'klik pada button buat akun'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/button_Buat Akun Anda Sekarang'))
		
	WebUI.delay(5)
	
	'mengambil otp dari db, disimpan ke iniotp'
	ArrayList<String> iniotp = CustomKeywords.'otp.getOTPfromDB.getOTPforRegister'(conn, email)
	
	CustomKeywords.'writeToExcel.checkSaveProcess.checkStatus'(isMandatoryComplete, findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/input_concat(id(, , otp, , ))_otp'), GlobalVariable.NumOfColumn, 'Register')
	
	if(AutofillOTP == 'Yes')
	{
		'input otp dari DB'
		WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/input_concat(id(, , otp, , ))_otp'), iniotp[0])
		
		if(ResendOTP == 'Yes')
		{
			for(int i=0; i < countresend; i++)
			{
				'tunggu button resend otp'
				WebUI.delay(123)
				
				'klik pada button kirim ulang otp'
				WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/a_Kirim kode lagi'))
				
				'bersihkan otp dari variabel'
				iniotp.clear()
				
				WebUI.delay(2)
				
				'mengambil otp dari db, disimpan ke iniotp'
				iniotp = CustomKeywords.'otp.getOTPfromDB.getOTPforRegister'(conn, email)
				
				'input otp dari DB'
				WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/input_concat(id(, , otp, , ))_otp'), iniotp[0])
			}
		}
		'klik pada button verifikasi otp'
		WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/button_Verifikasi'))
		
		WebUI.delay(2)
		
		'verifikasi adanya sukses isi otp'
		CustomKeywords.'writeToExcel.checkSaveProcess.checkStatus'(isMandatoryComplete, findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/div_Verifikasi OTP Email berhasil'), GlobalVariable.NumOfColumn, 'Register')
	}
	else
	{
		'input otp dari excel'
		WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/input_concat(id(, , otp, , ))_otp'), otpmanual[0])
		
		if(ResendOTP == 'Yes')
			{
				for(int i=0; i < countresend; i++)
				{
					'tunggu button resend otp'
					WebUI.delay(123)
					
					'klik pada button kirim ulang otp'
					WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/a_Kirim kode lagi'))
					
					'bersihkan otp dari variabel'
					iniotp.clear()
					
					WebUI.delay(2)
					
					'mengambil otp dari db, disimpan ke iniotp'
					iniotp = CustomKeywords.'otp.getOTPfromDB.getOTPforRegister'(conn, email)
					
					'input otp dari DB'
					WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/input_concat(id(, , otp, , ))_otp'), otpmanual[0])
					
				}
			}
			'klik pada button verifikasi otp'
			WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/button_Verifikasi'))
			
			WebUI.delay(2)
			
			'verifikasi adanya alert otp'
			CustomKeywords.'writeToExcel.checkSaveProcess.checkStatus'(isMandatoryComplete, findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/button_OK'), GlobalVariable.NumOfColumn, 'Register')
			
			'klik ok pada verifikasi alert'
			WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/button_OK'))
			
			'klik tombol x pada verifikasi'
			WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/span_'))

	}
		
	WebUI.delay(3)
	
	'verifikasi data hasil registrasi dan yang ada pada db'
	WebUI.callTestCase(findTestCase('Test Cases/Register/VerifyRegistration'), [:], FailureHandling.STOP_ON_FAILURE)
	
	'klik button Masuk'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/div_Masuk'))
	
	'input email yang sudah diregist pada field'
	WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/input_Buat Akun_form-control is-invalid ng-_7788b4'),
		findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 8))
	
	'input password yang sudah diregist ke field'
	WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/input_Buat Akun_form-control is-invalid ng-_7788b4_1_2'),
		findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 10))
	
	'klik pada tombol captcha'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/div_reCAPTCHA_recaptcha-checkbox-border (4)'))
	
	'selama delay bisa fill captcha secara manual'
	WebUI.delay(10)
	
//	'kondisi jika data yang required tidak dipenuhi'
//	if(isMandatoryComplete > 0)
//	{
//		'write status failed karena data mandatory tidak valid'
//		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('Register', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
//			(findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.StatusFailedReasonMandatory)
//	}
	
//	'kondisi jika button login clickable'
//	if (WebUI.verifyElementClickable(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'), FailureHandling.OPTIONAL))
//	{
	
	'verifikasi state dari button login'
	CustomKeywords.'writeToExcel.checkSaveProcess.checkStatusbtnClickable'(isMandatoryComplete, findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'), GlobalVariable.NumOfColumn, 'Register')
	
	'klik pada button login'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))
		
//	}
//	//kondisi dibawah jika button tidak clickable
//	else
//	{
//		'penanda ada error, status sukses tidak akan ditulis'
//		GlobalVariable.FlagFailed = 1
//		
//		'tulis status dan reason error ke excel'
//		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('Register', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
//		(findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.StatusFailedReasonSubmitError)
//		
//	}
	if (GlobalVariable.FlagFailed == 0)
	{
		'tulis status sukses pada excel'
		CustomKeywords.'writeToExcel.checkSaveProcess.checkStatus'(isMandatoryComplete, findTestObject('Object Repository/Profile/Page_Balance/i_LINA_ft-chevron-down'), GlobalVariable.NumOfColumn, 'Register')
	}
	else
	{
		'tulis gagal ke excel'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('Register', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
			GlobalVariable.StatusReasonSystem)
	}
	
	WebUI.closeBrowser()
}
