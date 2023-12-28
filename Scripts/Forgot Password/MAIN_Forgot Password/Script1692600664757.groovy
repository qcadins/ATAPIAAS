import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.By

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet User'
int countColumnEdit = findTestData(ExcelPathForgotPass).columnNumbers

'deklarasi koneksi ke Database adins_apiaas_uat'
Connection conndev = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_esign'()

'buka chrome'
WebUI.openBrowser('')

'buka website APIAAS SIT, data diambil dari TestData Login'
WebUI.navigateToUrl(findTestData(ExcelPathLogin).getValue(1, 2))

for (GlobalVariable.NumOfColumn = 2; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++) {
	
	GlobalVariable.FlagFailed = 0
	
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathForgotPass).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).length() == 0) {
		
		break
	} else if (findTestData(ExcelPathForgotPass).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).equalsIgnoreCase('Unexecuted') ||
		findTestData(ExcelPathForgotPass).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).equalsIgnoreCase('Warning')) {
		
		'angka untuk menghitung data mandatory yang tidak terpenuhi'
		int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathForgotPass).getValue(GlobalVariable.NumOfColumn, rowExcel('Mandatory Complete')))
		
		'klik pada link forgot password'
		WebUI.click(findTestObject('Object Repository/Forgot Password/Page_Login - eendigo Platform/a_Lupa Kata Sandi'))
		
		'input email yang akan dilakukan reset pass'
		WebUI.setText(findTestObject('Object Repository/Forgot Password/Page_Forgot Password Page/input__email'),
			findTestData(ExcelPathForgotPass).getValue(GlobalVariable.NumOfColumn, rowExcel('$Username Login')))
		
		'klik pada tombol batal'
		WebUI.click(findTestObject('Object Repository/Forgot Password/Page_Forgot Password Page/button_Batal'))
		
		'klik pada link forgot password'
		WebUI.click(findTestObject('Object Repository/Forgot Password/Page_Login - eendigo Platform/a_Lupa Kata Sandi'))

		'pastikan field email kosong setelah klik cancel'
		checkVerifyEqualorMatch(WebUI.verifyElementAttributeValue(
			findTestObject('Object Repository/Forgot Password/Page_Forgot Password Page/input__email'),
				'class', 'form-control ng-untouched ng-pristine ng-invalid',
				GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Field email tidak kosong setelah klik cancel')
		
		'input email yang akan dilakukan reset pass'
		WebUI.setText(findTestObject('Object Repository/Forgot Password/Page_Forgot Password Page/input__email'),
			findTestData(ExcelPathForgotPass).getValue(GlobalVariable.NumOfColumn, rowExcel('$Username Login')))
		
		'cek apakah tombol lanjut tidak di disable'
		if (WebUI.verifyElementNotHasAttribute(
			findTestObject('Object Repository/Forgot Password/Page_Forgot Password Page/button_Lanjut'), 'disabled',
				GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
			
			'klik pada tombol lanjut'
			WebUI.click(findTestObject('Object Repository/Forgot Password/Page_Forgot Password Page/button_Lanjut'))
			
			'klik pada tombol periksa lagi'
			WebUI.click(findTestObject('Object Repository/Forgot Password/Page_Forgot Password Page/button_Periksa lagi'))
			
			'cek apakah inputan nya masih ada'
			checkVerifyEqualorMatch(WebUI.verifyMatch(
				WebUI.getAttribute(findTestObject('Object Repository/Forgot Password/Page_Forgot Password Page/input__email'),'value', FailureHandling.OPTIONAL),
						findTestData(ExcelPathForgotPass).getValue(GlobalVariable.NumOfColumn, rowExcel('$Username Login')), false, FailureHandling.OPTIONAL),
						'Field email tidak tersimpan setelah klik periksa lagi')
			
			'klik pada tombol lanjut'
			WebUI.click(findTestObject('Object Repository/Forgot Password/Page_Forgot Password Page/button_Lanjut'))
			
			'klik pada tombol ya'
			WebUI.click(findTestObject('Object Repository/Forgot Password/Page_Forgot Password Page/button_Ya'))
			
			'check apakah muncul error'
			if (WebUI.verifyElementPresent(findTestObject('Object Repository/Forgot Password/Page_Reset Password/div_NotifPop'),
				GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
				
				'ambil error dan get text dari error tersebut'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathForgotPass).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) +
						';') + '<' + WebUI.getText(findTestObject('Object Repository/Forgot Password/Page_Reset Password/div_NotifPop')) + '>')
				
				'klik pada tombol OK'
				WebUI.click(findTestObject('Object Repository/Forgot Password/Page_Reset Password/button_OK_notif'))
				
				'klik pada tombol batal'
				WebUI.click(findTestObject('Object Repository/Forgot Password/Page_Forgot Password Page/button_Batal'))
				
				continue
			}
		} else {
			
			'klik pada tombol batal'
			WebUI.click(findTestObject('Object Repository/Forgot Password/Page_Forgot Password Page/button_Batal'))
			
			if (isMandatoryComplete != 0) {
				
				'tulis adanya error pada sistem web'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathForgotPass).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
						GlobalVariable.FailedReasonMandatory)
			}
			
			continue
		}
		
		'ambil reset code dari DB untuk email yang dituju'
		ArrayList resetCodefromDB = []
		
		'hitung jumlah resend yang diperlukan'
		int countResend = Integer.parseInt(findTestData(ExcelPathForgotPass).getValue(GlobalVariable.NumOfColumn, rowExcel('Website')))
		
		'tambahkan reset code dari DB'
		resetCodefromDB.add(CustomKeywords.'forgotPass.ForgotpassVerif.getResetCode'(conndev,
			findTestData(ExcelPathForgotPass).getValue(GlobalVariable.NumOfColumn, rowExcel('$Username Login'))))
		
		'pengecekan untuk pakai code yang salah'
		if (findTestData(ExcelPathForgotPass).getValue(GlobalVariable.NumOfColumn, rowExcel('$Nama Tenant')) == 'No') {
			
			'input reset code dari DB'
			WebUI.setText(findTestObject('Object Repository/Forgot Password/Page_Forgot Password Page/input_resetCode'),
				resetCodefromDB[0])
			
			resendFunction(conndev, countResend, resetCodefromDB)
			
			'panggil fungsi konfirmasi verif'
			if(verifConfirmation() == true) {
				
				continue
			}
		} else {
			
			'input code yang salah dari DB'
			WebUI.setText(findTestObject('Object Repository/Forgot Password/Page_Forgot Password Page/input_resetCode'),
				findTestData(ExcelPathForgotPass).getValue(GlobalVariable.NumOfColumn, rowExcel('$Industry')))
			
			resendFunction(conndev, countResend, resetCodefromDB)
			
			'panggil fungsi konfirmasi verif'
			if(verifConfirmation() == true) {
				
				continue
			}
		}
		
		'input password baru'
		WebUI.setText(findTestObject('Object Repository/Forgot Password/Page_Reset Password/input_newPassword'),
			findTestData(ExcelPathForgotPass).getValue(GlobalVariable.NumOfColumn, rowExcel('$Password Login')))
		
		'input password confirm'
		WebUI.setText(findTestObject('Object Repository/Forgot Password/Page_Reset Password/input_confirmNewPassword'),
			findTestData(ExcelPathForgotPass).getValue(GlobalVariable.NumOfColumn, rowExcel('$Nama Depan')))
		
		'klik button lanjut'
		WebUI.click(findTestObject('Object Repository/Forgot Password/Page_Reset Password/button_Simpan'))
		
		'klik periksa lagi'
		WebUI.click(findTestObject('Object Repository/Forgot Password/Page_Reset Password/button_Periksa lagi'))
		
		'ambil pass baru yang baru diinput di UI'
		String newPass = WebUI.getAttribute(findTestObject('Object Repository/Forgot Password/Page_Reset Password/input_newPassword'),
			'value', FailureHandling.CONTINUE_ON_FAILURE)
		
		'ambil pass confirm baru yang diinput di UI'
		String newPassConfirm = WebUI.getAttribute(findTestObject('Object Repository/Forgot Password/Page_Reset Password/input_confirmNewPassword'),
			'value', FailureHandling.CONTINUE_ON_FAILURE)
				
		'cek apakah inputan nya masih ada'
		checkVerifyEqualorMatch(WebUI.verifyMatch(newPass,
			findTestData(ExcelPathForgotPass).getValue(GlobalVariable.NumOfColumn, rowExcel('$Password Login')),
				false, FailureHandling.OPTIONAL), 'Field new Password tidak tersimpan setelah klik periksa lagi')
			
		'cek apakah inputan nya masih ada'
		checkVerifyEqualorMatch(WebUI.verifyMatch(newPassConfirm,
			findTestData(ExcelPathForgotPass).getValue(GlobalVariable.NumOfColumn, rowExcel('$Nama Depan')),
				false, FailureHandling.OPTIONAL), 'Field new Password Confirm tidak tersimpan setelah klik periksa lagi')
		
		'klik button lanjut'
		WebUI.click(findTestObject('Object Repository/Forgot Password/Page_Reset Password/button_Simpan'))
		
		'klik pada tombol YA'
		WebUI.click(findTestObject('Object Repository/Forgot Password/Page_Reset Password/button_Ya'))
		
		'check apakah muncul error'
		if (WebUI.getText(findTestObject('Object Repository/Forgot Password/Page_Reset Password/div_NotifPop'),
			FailureHandling.OPTIONAL) != 'Kata sandi Anda telah berhasil diganti') {
			
			'ambil error dan get text dari error tersebut'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathForgotPass).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) +
					';') + '<' + WebUI.getText(findTestObject('Object Repository/Forgot Password/Page_Reset Password/div_NotifPop')) + '>')
			
			'klik pada tombol OK'
			WebUI.click(findTestObject('Object Repository/Forgot Password/Page_Reset Password/button_OK_notif'))
			
			'buka website APIAAS SIT, data diambil dari TestData Login'
			WebUI.navigateToUrl(findTestData(ExcelPathLogin).getValue(1, 2))
			
			continue
		} else {
			
			'klik pada tombol OK'
			WebUI.click(findTestObject('Object Repository/Forgot Password/Page_Reset Password/button_OK_notif'))
		}
		
		'input email untuk login'
		WebUI.setText(findTestObject('Object Repository/Forgot Password/Page_Login - eendigo Platform/input_usernameLogin'),
			findTestData(ExcelPathForgotPass).getValue(GlobalVariable.NumOfColumn, rowExcel('$Username Login')))
		
		'input password baru yang direset'
		WebUI.setText(findTestObject('Object Repository/Forgot Password/Page_Login - eendigo Platform/input_passLogin'),
			findTestData(ExcelPathForgotPass).getValue(GlobalVariable.NumOfColumn, rowExcel('$Password Login')))
		
		'klik pada button login'
		WebUI.click(
			findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))
		
		'cek apakah berhasil login'
		if (WebUI.verifyElementPresent(findTestObject('Object Repository/Forgot Password/span_profile'),
			GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
			'klik pada tombol profile'
			WebUI.click(findTestObject('Object Repository/Forgot Password/span_profile'))
			
			'klik pada tombol logout'
			WebUI.click(findTestObject('Object Repository/Forgot Password/span_Logout'))
			
			if (GlobalVariable.FlagFailed == 0 && isMandatoryComplete == 0) {
				'write to excel success'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0,
					GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
			} else {
				'tulis adanya error pada sistem web'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathForgotPass).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
						GlobalVariable.FailedReasonMandatory)
			}
		} else {
			'tulis adanya error pada sistem web'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathForgotPass).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
					GlobalVariable.FailedReasonLoginIssue)
		}
	}
}

WebUI.closeBrowser()

def verifConfirmation() {
	
	boolean shouldContinue = false
	
	'klik pada button verifikasi'
	WebUI.click(findTestObject('Object Repository/Forgot Password/Page_Forgot Password Page/button_Verifikasi'))
	
	'check apakah muncul error'
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/Forgot Password/Page_Reset Password/div_NotifPop'),
		GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		
		'ambil error dan get text dari error tersebut'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathForgotPass).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) +
				';') + '<' + WebUI.getText(findTestObject('Object Repository/Forgot Password/Page_Reset Password/div_NotifPop')) + '>')
		
		'klik pada tombol OK'
		WebUI.click(findTestObject('Object Repository/Forgot Password/Page_Reset Password/button_OK_notif'))
		
		'klik pada tombol silang verifikasi'
		WebUI.click(findTestObject('Object Repository/Forgot Password/Page_Forgot Password Page/tombolX'))
		
		'klik pada tombol batal'
		WebUI.click(findTestObject('Object Repository/Forgot Password/Page_Forgot Password Page/button_Batal'))
		
		shouldContinue = true
	}
	
	return shouldContinue
}

def resendFunction(Connection conndev, int countResend, ArrayList resetCodefromDB) {
	
	'cek apakah perlu resend reset code'
	if (findTestData(ExcelPathForgotPass).getValue(GlobalVariable.NumOfColumn, rowExcel('Jenis Kelamin')) == 'Yes') {
		
		'ulangi sesuai flag dari excel'
		for (int i = 1; i <= countResend; i++) {
			
			WebUI.delay(117)
			
			'klik pada resend code'
			WebUI.click(findTestObject('Object Repository/Forgot Password/Page_Forgot Password Page/a_Kirim kode lagi'))
			
			'tunggu agar data di DB terupdate'
			WebUI.delay(5)
			
			'ambil kembali code dari DB'
			resetCodefromDB.add(CustomKeywords.'forgotPass.ForgotpassVerif.getResetCode'(conndev,
				findTestData(ExcelPathForgotPass).getValue(GlobalVariable.NumOfColumn, rowExcel('$Username Login'))))
			
			'cek apakah resend gagal'
			if (resetCodefromDB[i-1] == resetCodefromDB[i]) {
				
				GlobalVariable.FlagFailed = 1
				
				'tulis adanya error pada resend'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathForgotPass).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
						GlobalVariable.FailedReasonOTP)
			}
			
			'input reset code dari DB'
			WebUI.setText(findTestObject('Object Repository/Forgot Password/Page_Forgot Password Page/input_resetCode'),
				resetCodefromDB[i])
		}
	}
}

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
	if (isMatch == false) {
		GlobalVariable.FlagFailed = 1
		
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		GlobalVariable.FlagFailed = 1
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathForgotPass).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
				GlobalVariable.FailedReasonVerifyEqualorMatch + reason)
	}
}

def rowExcel(String cellValue) {
	return CustomKeywords.'writeToExcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}