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

'mendapat jumlah kolom dari sheet Edit Profile'
int countColumnEdit = findTestData(ExcelPathChangePass).getColumnNumbers()

'buka chrome'
WebUI.openBrowser('')

'buka website APIAAS SIT, data diambil dari TestData Login'
WebUI.navigateToUrl(findTestData(ExcelPathLogin).getValue(1, 2))

for (GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++) {
	
	'set penanda error menjadi 0'
	GlobalVariable.FlagFailed = 0
		
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, 1).length() == 0) {
		
		break
	}
	else if (findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, 1).equalsIgnoreCase('Unexecuted')) {
		
		'jika gagal pilih role saat login'
		if (loginFunction(10) == 0) {
			
			continue
		}
		
		'klik pada tombol untuk span profile'
		WebUI.click(findTestObject('Object Repository/Change Password/Page_Balance/span_profile'))
		
		'pilih ubah password'
		WebUI.click(findTestObject('Object Repository/Change Password/Page_Balance/changepass'))
		
		'input password lama'
		WebUI.setText(findTestObject('Object Repository/Change Password/Page_Change Password/input__currentPass'),
			findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, 12))
		
		'input password baru'
		WebUI.setText(findTestObject('Object Repository/Change Password/Page_Change Password/input__newPass'),
			findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, 13))
		
		'input password baru confirm'
		WebUI.setText(findTestObject('Object Repository/Change Password/Page_Change Password/input__confirmnewPass'),
			findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, 14))
		
		'pastikan tombol lanjut tidak disabled'
		if (WebUI.verifyElementNotHasAttribute(findTestObject('Object Repository/Change Password/Page_Change Password/button_Lanjut'),
			'disabled', GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		
			'klik pada tombol lanjut'
			WebUI.click(findTestObject('Object Repository/Change Password/Page_Change Password/button_Lanjut'))
			
			'klik pada tombol ya'
			WebUI.click(findTestObject('Object Repository/Change Password/Page_Change Password/button_Ya'))
			
			'ambil notif message setelah ubah sandi'
			String notifMsg = WebUI.getText(findTestObject('Object Repository/Change Password/Page_Balance/notifSukses'))
			
			'cek muncul error atau tidak'
			if (notifMsg != 'Kode Akses anda berhasil diganti.') {
				
				'tulis ada error '
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('ChangePassword', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, 2) +
						';') + notifMsg)
				
				'klik pada tombol OK'
				WebUI.click(findTestObject('Object Repository/Change Password/Page_Balance/button_OK'))
				
				'panggil fungsi logout'
				logoutFunction()
				
				continue
			}
			
			'klik pada tombol OK'
			WebUI.click(findTestObject('Object Repository/Change Password/Page_Balance/button_OK'))
			
			'panggil fungsi logout'
			logoutFunction()
			
			'jika gagal pilih role saat login'
			if (loginFunction(10) == 0) {
			
				continue
			}
			
			'cek apakah muncul error gagal login'
			if (WebUI.verifyElementPresent(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/ErrorMsg')
				,GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
			
				GlobalVariable.FlagFailed = 1
			
				'tulis adanya error pada sistem web'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('ChangePassword', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
						GlobalVariable.FailedReasonLoginIssue)
			}
			
			'verifikasi berhasil login atau tidak'
			if (WebUI.verifyElementPresent(findTestObject('Object Repository/Change Password/Page_Balance/dropdownMenu'),
				GlobalVariable.Timeout) && GlobalVariable.FlagFailed == 0) {
			
				'write to excel success'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'ChangePassword', 0,
					GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
			}
		}
		else {
			
			'tulis ada error '
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('ChangePassword', GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, 2) +
					';') + GlobalVariable.FailedReasonMandatory)
			
			'panggil fungsi logout'
			logoutFunction()
		}
	}
}

'fungsi tutup browser saat testing beres'
WebUI.closeBrowser()

def logoutFunction() {
	
	'klik pada tombol untuk span profile'
	WebUI.click(findTestObject('Object Repository/Change Password/Page_Balance/span_profile'))
	
	'lakukan logout'
	WebUI.click(findTestObject('Object Repository/Change Password/Page_Balance/span_Logout'))
	
	'verifikasi apakah login dengan google muncul'
	WebUI.verifyElementPresent(findTestObject('Object Repository/RegisterLogin/'+
		'Page_Login - eendigo Platform/div_reCAPTCHA_recaptcha-checkbox-border (4)'), GlobalVariable.Timeout)
	
	'verifikasi apakah captcha muncul'
	WebUI.verifyElementPresent(findTestObject('Object Repository/Saldo/'+
		'Page_Login - eendigo Platform/span_Lanjutkan dengan Google'), GlobalVariable.Timeout)
}

def loginFunction(int row) {
	
	'penentu apakah ada role yang sudah di'
	int isSelected = 0
	
	'input data email'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'input_Buat Akun_form-control ng-untouched n_ab9ed8'),
		findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, 9))
	
	'input password'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'input_Buat Akun_form-control ng-untouched n_dd86a2'),
		findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, row))
	
	'ceklis pada reCaptcha'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/'+
		'div_reCAPTCHA_recaptcha-checkbox-border (4)'))
	
	'pada delay, lakukan captcha secara manual'
	WebUI.delay(10)
	
	'klik pada button login'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'button_Lanjutkan Perjalanan Anda'))
	
	'jika ada pilihan role'
	if (WebUI.verifyElementPresent(
		findTestObject('Object Repository/Change Password/Page_Login - eendigo Platform/Admin Client_3'),
			GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		
		'cari element dengan nama role'
		def elementRole = DriverFactory.getWebDriver().findElements(By.cssSelector('body > ngb-modal-window > div > div > app-multi-role > div > div.row > div > table tr'))
		
		'lakukan loop untuk cari nama role yang ditentukan'
		for (int i = 1; i <= elementRole.size() - 1; i++) {
			
			'cari nama role yag sesuai di opsi role'
			def modifyRole = WebUI.modifyObjectProperty(findTestObject('Object Repository/Change Password/modifyobject'), 'xpath', 'equals', "/html/body/ngb-modal-window/div/div/app-multi-role/div/div[2]/div/table/tr["+ (i+1) +"]/td[1]", true)
	
			'jika nama object sesuai dengan nama role'
			if (findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, 11).equalsIgnoreCase(
				WebUI.getAttribute(modifyRole, 'value', FailureHandling.STOP_ON_FAILURE))) {
				
				'ubah alamat xpath ke role yang dipilih'
				modifyRole = WebUI.modifyObjectProperty(findTestObject('Object Repository/Change Password/modifyobject'), 'xpath', 'equals', "/html/body/ngb-modal-window/div/div/app-multi-role/div/div[2]/div/table/tr["+ (i+1) +"]/td[2]/a", true)
			
				'klik role yang dipilih'
				WebUI.click(findTestObject('Object Repository/Change Password/modifyobject'))
				
				'penanda adanya role yang dipilih'
				isSelected = 1
				
				break;
			}
		}
		'tulis error dan lanjut testcase berikutnya'
		if (isSelected == 0) {
			
			'tulis adanya error pada sistem web'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('ChangePassword', GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
					GlobalVariable.FailedReasonRoleLogin)
		}
	}
	
	'cek apakah muncul error gagal login'
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/ErrorMsg')
		,GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
	
		GlobalVariable.FlagFailed = 1
	
		'tulis adanya error pada sistem web'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('ChangePassword', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
				GlobalVariable.FailedReasonLoginIssue)
	}
	
	return isSelected
}