import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import java.sql.Connection

'siapkan koneksi ke db eendigo'
Connection conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_esign'()

'mendapat jumlah kolom dari sheet Edit Profile'
int countColumnEdit = findTestData(ExcelPathRegisterLogin).getColumnNumbers()

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

for (GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn <= countColumnEdit; GlobalVariable.NumOfColumn++){
	
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 1).length() == 0){
		
		break
	}
	else if (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 1).equalsIgnoreCase('Unexecuted')){
		
		String email, autofillOTP, otpExcel, resendotp, countresendOTP
	
		'simpan data email dari testdata'
		email = findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 9)
		
		'kondisi jika perlu isi OTP langsung dari DB'
		autofillOTP = findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 14)
		
		'simpan data OTP dari excel'
		otpExcel = findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 15)
		
		'status resend otp bila diperlukan'
		resendotp = findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 16)
		
		'berapa kali resend diperlukan'
		countresendOTP = findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 17)
		
		'angka untuk menghitung data mandatory yang tidak terpenuhi'
		int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathRegisterLogin).getValue(
			GlobalVariable.NumOfColumn, 5))
		
		'angka untuk mendapat otp salah dari excel'
		List<Integer> otpmanual = []
		
		'tambahkan data ke dalam list otp manual'
		otpmanual.add(otpExcel)
		
		'banyaknya resend otp yang akan dilakukan'
		int countresend = Integer.parseInt(countresendOTP)
		
		'memanggil fungsi untuk login'
		WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC'):'Regist', ('SheetName') : 'Register', 
			('Path') : ExcelPathRegisterLogin], FailureHandling.STOP_ON_FAILURE)
		
		'cek apakah button register bisa di klik'
		CustomKeywords.'writeToExcel.CheckSaveProcess.checkStatusbtnClickable'(isMandatoryComplete, 
			findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/'+
				'button_Buat Akun Anda Sekarang'), GlobalVariable.NumOfColumn, 'Register')
		
		'pencet enter'
		WebUI.sendKeys(findTestObject('Object Repository/RegisterLogin/'+
			'Page_Login - eendigo Platform/input_Buat Akun_form-control is-invalid ng-_7788b4_1_2_3'), Keys.chord(Keys.ENTER))
		
		WebUI.delay(5)
		
		'mengambil otp dari db, disimpan ke iniotp'
		ArrayList<String> iniotp = CustomKeywords.'otp.GetOTPfromDB.getOTPforRegister'(conn, email)
		
		'cek apakah field untuk input otp muncul'
		CustomKeywords.'writeToExcel.CheckSaveProcess.checkStatus'(isMandatoryComplete, 
			findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/input_concat(id(, , otp, , ))_otp'), 
				GlobalVariable.NumOfColumn, 'Register')
		
		if (autofillOTP == 'Yes') {
			
			'input otp dari DB'
			WebUI.setText(findTestObject('Object Repository/RegisterLogin/'+
				'Page_Login - eendigo Platform/input_concat(id(, , otp, , ))_otp'), iniotp[0])
			
			if (resendotp == 'Yes') {
				
				for (int i=0; i < countresend; i++) {
					
					'tunggu button resend otp'
					WebUI.delay(116)
					
					'klik pada button kirim ulang otp'
					WebUI.click(findTestObject('Object Repository/RegisterLogin/'+
						'Page_Login - eendigo Platform/a_Kirim kode lagi'))
					
					WebUI.delay(2)
					
					'mengambil otp dari db, disimpan ke iniotp'
					iniotp = CustomKeywords.'otp.GetOTPfromDB.getOTPforRegister'(conn, email)
					
					if (WebUI.verifyMatch(iniotp[i], iniotp[i+1], false, FailureHandling.CONTINUE_ON_FAILURE)) {
						
						GlobalVariable.FlagFailed = 1
						
						'tulis gagal resend otp ke excel'
						CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Register', GlobalVariable.NumOfColumn,
							GlobalVariable.StatusFailed, (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
								GlobalVariable.FailedReasonOTP)
					}
					
					'input otp dari DB'
					WebUI.setText(findTestObject('Object Repository/RegisterLogin/'+
						'Page_Login - eendigo Platform/input_concat(id(, , otp, , ))_otp'), iniotp[i])
				}
			}
			'klik pada button verifikasi otp'
			WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/button_Verifikasi'))
			
			WebUI.delay(2)
			
			'verifikasi adanya sukses isi otp'
			CustomKeywords.'writeToExcel.CheckSaveProcess.checkStatus'(isMandatoryComplete, 
				findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/div_Verifikasi OTP Email berhasil'), 
					GlobalVariable.NumOfColumn, 'Register')
		}
		else {
			
			'input otp dari excel'
			WebUI.setText(findTestObject('Object Repository/RegisterLogin/'+
				'Page_Login - eendigo Platform/input_concat(id(, , otp, , ))_otp'), otpmanual[0])
			
			if(resendotp == 'Yes')
				{
					for(int i=0; i < countresend; i++)
					{
						'tunggu button resend otp'
						WebUI.delay(116)
						
						'klik pada button kirim ulang otp'
						WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/a_Kirim kode lagi'))
						
						WebUI.delay(2)
						
						'mengambil otp dari db, disimpan ke iniotp'
						iniotp = CustomKeywords.'otp.GetOTPfromDB.getOTPforRegister'(conn, email)
						
						if (WebUI.verifyMatch(iniotp[i], otpmanual[0], false, FailureHandling.CONTINUE_ON_FAILURE)) {
							
							GlobalVariable.FlagFailed = 1
								
							'tulis gagal resend otp ke excel'
							CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Register', GlobalVariable.NumOfColumn,
								GlobalVariable.StatusFailed, (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
									GlobalVariable.FailedReasonOTP)
						}
						
						'input otp dari DB'
						WebUI.setText(findTestObject('Object Repository/RegisterLogin/'+
							'Page_Login - eendigo Platform/input_concat(id(, , otp, , ))_otp'), otpmanual[0])
					}
				}
				
				'klik pada button verifikasi otp'
				WebUI.click(findTestObject('Object Repository/RegisterLogin/'+
					'Page_Login - eendigo Platform/button_Verifikasi'))
				
				WebUI.delay(2)
				
				if(WebUI.verifyElementPresent(findTestObject('Object Repository/RegisterLogin/'+
					'Page_Login - eendigo Platform/button_OK'), GlobalVariable.Timeout, FailureHandling.CONTINUE_ON_FAILURE)){
				
					'verifikasi adanya alert otp'
					CustomKeywords.'writeToExcel.CheckSaveProcess.checkStatus'(isMandatoryComplete,
					findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/button_OK'),
						GlobalVariable.NumOfColumn, 'Register')
					
					'klik ok pada verifikasi alert'
					WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/button_OK'))
				}
							
				'klik tombol x pada verifikasi'
				WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/span_'))
	
		}
		
		'cek apakah muncul error unknown setelah login'
		if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'),
			GlobalVariable.Timeout, FailureHandling.OPTIONAL) == false) {
			
			GlobalVariable.FlagFailed = 1
			
			'tulis adanya error pada sistem web'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Register', GlobalVariable.NumOfColumn,
				GlobalVariable.StatusWarning, (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
					GlobalVariable.FailedReasonUnknown)
		}
			
		WebUI.delay(3)
		
		'verifikasi data hasil registrasi dan yang ada pada db'
		WebUI.callTestCase(findTestCase('Test Cases/Register/VerifyRegistration'), 
			[:], FailureHandling.STOP_ON_FAILURE)
		
		'klik button submenu Masuk'
		WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/div_Masuk'))
		
		'input email yang sudah diregist pada field'
		WebUI.setText(findTestObject('Object Repository/RegisterLogin/'+
			'Page_Login - eendigo Platform/input_Buat Akun_form-control is-invalid ng-_7788b4'),
				findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 9))
		
		'input password yang sudah diregist ke field'
		WebUI.setText(findTestObject('Object Repository/RegisterLogin/'+
			'Page_Login - eendigo Platform/input_Buat Akun_form-control is-invalid ng-_7788b4_1_2'),
				findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 11))
		
		'klik pada tombol captcha'
		WebUI.click(findTestObject('Object Repository/RegisterLogin/'+
			'Page_Login - eendigo Platform/div_reCAPTCHA_recaptcha-checkbox-border (4)'))
		
		'selama delay bisa fill captcha secara manual'
		WebUI.delay(10)
		
	//	'kondisi jika data yang required tidak dipenuhi'
	//	if(isMandatoryComplete > 0)
	//	{
	//		'write status failed karena data mandatory tidak valid'
	//		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Register', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
	//			(findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.StatusFailedReasonMandatory)
	//	}
		
	//	'kondisi jika button login clickable'
	//	if (WebUI.verifyElementClickable(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'), FailureHandling.OPTIONAL))
	//	{	
		'klik pada button login'
		WebUI.click(findTestObject('Object Repository/RegisterLogin/'+
			'Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))
		
		'cek apakah muncul error unknown setelah login'
		if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'),
			GlobalVariable.Timeout, FailureHandling.OPTIONAL) == false) {
			
			GlobalVariable.FlagFailed = 1
			
			'tulis adanya error pada sistem web'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Register', GlobalVariable.NumOfColumn,
				GlobalVariable.StatusWarning, (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
					GlobalVariable.FailedReasonUnknown)
		}
			
	//	}
	//	//kondisi dibawah jika button tidak clickable
	//	else
	//	{
	//		'penanda ada error, status sukses tidak akan ditulis'
	//		GlobalVariable.FlagFailed = 1
	//		
	//		'tulis status dan reason error ke excel'
	//		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Register', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
	//		(findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.StatusFailedReasonSubmitError)
	//		
	//	}
		if (GlobalVariable.FlagFailed == 0) {
			
			'tulis status sukses pada excel'
			CustomKeywords.'writeToExcel.CheckSaveProcess.checkStatus'(isMandatoryComplete, 
				findTestObject('Object Repository/Profile/Page_Balance/i_LINA_ft-chevron-down'), 
					GlobalVariable.NumOfColumn, 'Register')
		}
		
		WebUI.closeBrowser()
	}
}
