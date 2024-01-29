import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable
import org.openqa.selenium.WebDriver
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement

import java.sql.Connection

'siapkan koneksi ke db eendigo'
Connection conn = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_esign'()

'mendapat jumlah kolom dari sheet Edit Profile'
int countColumnEdit = findTestData(ExcelPathRegisterLogin).columnNumbers

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writetoexcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

for (GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn <= countColumnEdit; GlobalVariable.NumOfColumn++) {
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).length() == 0) {	
		break
	} else if (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
		String email, resendotp
	
		'simpan data email dari testdata'
		email = findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, rowExcel('$Email registrasi'))
		
		'status resend otp bila diperlukan'
		resendotp = findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, rowExcel('ResendOTP? (Yes/No)'))
		
		'angka untuk menghitung data mandatory yang tidak terpenuhi'
		int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathRegisterLogin).getValue(
			GlobalVariable.NumOfColumn, rowExcel('Mandatory Complete')))
		
		'banyaknya resend otp yang akan dilakukan'
		int countresend = Integer.parseInt(findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, rowExcel('CountResendOTP')))
		
		'memanggil fungsi untuk login'
		WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'Regist', ('SheetName') : sheet, 
			('Path') : ExcelPathRegisterLogin], FailureHandling.STOP_ON_FAILURE)
		
		if (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, rowExcel('Checklist Recaptcha')) == 'Yes' && isMandatoryComplete == 0) {
			WebDriver driver = DriverFactory.webDriver
			JavascriptExecutor js = (JavascriptExecutor)driver
			
			'bypass captcha langsung masuk verifikasi otp'
			WebElement buttonRegister = driver.findElement(By.cssSelector('#mat-tab-content-0-1 > div > form > button'))
			js.executeScript("arguments[0].removeAttribute('disabled')", buttonRegister)
		}
		
		'cek apakah button register bisa di klik'
		CustomKeywords.'writetoexcel.CheckSaveProcess.checkStatusbtnClickable'(isMandatoryComplete, 
				findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/button_Buat Akun Anda Sekarang'), GlobalVariable.NumOfColumn, sheet)
		
		'pencet enter'
		WebUI.sendKeys(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/input_confirmPassRegist'),
			Keys.chord(Keys.ENTER))
		
		WebUI.delay(5)
		
		if (WebUI.verifyElementPresent(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/input otp'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
			'mengambil otp dari db, disimpan ke iniotp'
			ArrayList<String> iniotp = []
	
			OTP = CustomKeywords.'otp.GetOTPfromDB.getOTPforRegister'(conn, email)
			
			iniotp.add(OTP)
			
			'cek apakah field untuk input otp muncul'
			CustomKeywords.'writetoexcel.CheckSaveProcess.checkStatus'(isMandatoryComplete, 
				findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/input otp'), 
					GlobalVariable.NumOfColumn, sheet)
			
			'fungsi yang menyesuaikan keperluan ambil otp dari DB'
			if (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, rowExcel('AutofillOTP(Yes/No)')) == 'Yes') {
				'input otp dari DB'
				WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/input otp'), iniotp[0])
				
				if (resendotp == 'Yes') {
					for (int i = 0; i < countresend; i++) {
						'tunggu button resend otp'
						WebUI.delay(116)
						
						'klik pada button kirim ulang otp'
						WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/a_Kirim kode lagi'))
						
						WebUI.delay(5)
						
						OTP = CustomKeywords.'otp.GetOTPfromDB.getOTPforRegister'(conn, email)
						
						'mengambil otp dari db, disimpan ke iniotp'
						iniotp.add(OTP)
						
						if (!WebUI.verifyNotMatch(iniotp[i], iniotp[i + 1], false, FailureHandling.CONTINUE_ON_FAILURE)) {
							GlobalVariable.FlagFailed = 1
							
							'tulis gagal resend otp ke excel'
							CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
								GlobalVariable.StatusFailed, (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
									GlobalVariable.FailedReasonOTP)
						}
						
						'input otp lama dari DB'
						WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/input otp'), iniotp[i])
						
						'klik pada button verifikasi otp'
						WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/button_Verifikasi'))
						
						if (WebUI.verifyElementPresent(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/button_OK'), 
								GlobalVariable.Timeout, FailureHandling.CONTINUE_ON_FAILURE)) {
							'klik ok pada verifikasi alert'
							WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/button_OK'))
							
							'input otp baru dari DB'
							WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/input otp'), iniotp[i + 1])
						} else {
							'verifikasi adanya alert otp'
							CustomKeywords.'writetoexcel.CheckSaveProcess.checkStatus'(isMandatoryComplete,
							findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/button_OK'),
								GlobalVariable.NumOfColumn, sheet)
						}
					}
				}
				'klik pada button verifikasi otp'
				WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/button_Verifikasi'))
				
				WebUI.delay(2)
				
				'jika muncul error'
				if (WebUI.verifyElementPresent(findTestObject('RegisterLogin/Page_Login - eendigo Platform/ErrorMsg'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
					'ambil text dan lanjutkan ke testdata selanjutnya'
					CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
						GlobalVariable.StatusFailed, (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') + '<' +
							WebUI.getText(findTestObject('RegisterLogin/SideNotifError')) + '>')
					
					WebUI.closeBrowser()
					
					continue
				}
			} else {
				'input otp dari excel'
				WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/input otp'), findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, rowExcel('ManualOTP')))
				
				if (resendotp == 'Yes') {
					for (int i = 0; i < countresend; i++) {
						'tunggu button resend otp'
						WebUI.delay(116)
						
						'klik pada button kirim ulang otp'
						WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/a_Kirim kode lagi'))

						WebUI.delay(2)
						
						OTP = CustomKeywords.'otp.GetOTPfromDB.getOTPforRegister'(conn, email)
						
						'mengambil otp dari db, disimpan ke iniotp'
						iniotp.add(OTP)

						if (!WebUI.verifyNotMatch(iniotp[i], iniotp[i + 1], false, FailureHandling.CONTINUE_ON_FAILURE)) {
							GlobalVariable.FlagFailed = 1
							
							'tulis gagal resend otp ke excel'
							CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
								GlobalVariable.StatusFailed, (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
									GlobalVariable.FailedReasonOTP)
						}	
						
						'input otp dari DB'
						WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/input otp'), iniotp[i + 1])
					}
				}
					
				'klik pada button verifikasi otp'
				WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/button_Verifikasi'))
				
				WebUI.delay(2)
				
				if (WebUI.verifyElementPresent(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/button_OK'), GlobalVariable.Timeout, FailureHandling.CONTINUE_ON_FAILURE)) {
					'klik ok pada verifikasi alert'
					WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/button_OK'))
					
					'klik tombol x pada verifikasi'
					WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/span_'))
				}
				
				'jika muncul error'
				if (WebUI.verifyElementPresent(findTestObject('RegisterLogin/Page_Login - eendigo Platform/ErrorMsg'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
					'ambil text dan lanjutkan ke testdata selanjutnya'
					CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
						GlobalVariable.StatusFailed, (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') + '<' +
							WebUI.getText(findTestObject('RegisterLogin/SideNotifError')) + '>')
										
					WebUI.closeBrowser()
					
					continue
				}
			}
		}
		
//		'aktifkan nocaptcha by link'
//		WebUI.navigateToUrl('https://config.nocaptchaai.com/?apikey=' + GlobalVariable.APIKEYCaptcha + '')
//		
		'buka website APIAAS SIT, data diambil dari TestData Login'
		WebUI.navigateToUrl(findTestData(ExcelPathLogin).getValue(1, 2))
		
		'klik button submenu Masuk'
		WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/div_Masuk'))
		
		'selama delay bisa fill captcha secara manual'
		WebUI.delay(2)
		
		'input email yang sudah diregist pada field'
		WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/inputemailRegister'),
				findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, rowExcel('$Email registrasi')))
		
		'input password yang sudah diregist ke field'
		WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/input_passRegister'),
				findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, rowExcel('$Pass registrasi')))
	
		WebUI.delay(1)
		
		String idObject = WebUI.getAttribute(findTestObject('RegisterLogin/Page_Login - eendigo Platform/check_Recaptcha'), 'id', FailureHandling.STOP_ON_FAILURE)
		
		modifyObjectCaptcha = WebUI.modifyObjectProperty(findTestObject('RegisterLogin/Page_Login - eendigo Platform/check_Recaptcha'), 'xpath', 'equals',
			'//*[@id="' + idObject + '"]/div/div[2]', true)
		
		WebUI.waitForElementAttributeValue(modifyObjectCaptcha, 'class', 'antigate_solver recaptcha solved', 60, FailureHandling.OPTIONAL)
	
		WebUI.delay(1)
		
		'klik pada button login'
		WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))
		
		'jika berhasil login akan muncul object dibawah'
		if (WebUI.verifyElementPresent(findTestObject('Object Repository/Profile/Page_Balance/dropdownProfile'),
				GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
			'jalankan verifikasi atas proses registrasi yang berhasil'
			verifyregistration(conn, findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, rowExcel('$Email registrasi')))
			
			if (GlobalVariable.FlagFailed == 0) {
				'tulis status sukses pada excel'
				CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet,
					GlobalVariable.NumOfColumn, GlobalVariable.StatusSuccess, '')
			}
		}
		WebUI.closeBrowser()
	}
}

if (GlobalVariable.continueTC == 'Yes') {
	'panggil testcase untuk edit profile'
	WebUI.callTestCase(findTestCase('Test Cases/Profile/MAIN_EditProfile'), [:], FailureHandling.STOP_ON_FAILURE)
}

def verifyregistration(Connection conn, String email) {
	'deklarasi arrayindex untuk traversing array'
	int arrayIndex = 0
	
	'simpan data yang diambil dari database'
	ArrayList credential = CustomKeywords.'profile.CheckRegisterProfile.checkDBafterRegister'(conn, email)
	
	'verifikasi data pada WEB dan excel sama'	
	checkVerifyEqualorMatch(WebUI.verifyMatch(credential[arrayIndex++], findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, rowExcel('$Email registrasi')), false, FailureHandling.CONTINUE_ON_FAILURE), 'Email tidak sesuai')
	
	'verifikasi data pada WEB dan excel sama'
	checkVerifyEqualorMatch(WebUI.verifyMatch(credential[arrayIndex++], findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, rowExcel('$Username registrasi')), false, FailureHandling.CONTINUE_ON_FAILURE), 'Nama Pengguna tidak sesuai')
}

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
	if (isMatch == false) {
		GlobalVariable.FlagFailed = 1
		
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
				GlobalVariable.FailedReasonStoreDB + ' ' + reason)
	}
}

def rowExcel(String cellValue) {
	CustomKeywords.'writetoexcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}
