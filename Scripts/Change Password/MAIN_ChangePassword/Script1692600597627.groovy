import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.By
import org.openqa.selenium.Keys as Keys

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writetoexcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Edit Profile'
int countColumnEdit = findTestData(ExcelPathChangePass).columnNumbers

'panggil fungsi untuk open browser'
CustomKeywords.'login.Browser.settingandOpen'(ExcelPathChangePass, rowExcel('CaptchaEnabled'))

'buat flag failed menjadi 0 agar tidak menimpa status failed pada excel'
GlobalVariable.FlagFailed = 0

'buka website APIAAS SIT, data diambil dari TestData Login'
WebUI.navigateToUrl(findTestData(ExcelPathLogin).getValue(1, 2))

'lakukan proses login dengan password lama'
loginFunction(rowExcel('Password Login'))

for (GlobalVariable.NumOfColumn = 2; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++) {		
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).length() == 0) {
		break
	} else if (findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
		'set penanda error menjadi 0'
		GlobalVariable.FlagFailed = 0
		
		'klik pada tombol untuk span profile'
		WebUI.click(findTestObject('Object Repository/Change Password/Page_Balance/span_profile'))
		
		'pilih ubah password'
		WebUI.click(findTestObject('Object Repository/Change Password/Page_Balance/changepass'))
		
		if (GlobalVariable.NumOfColumn == 2) {
			'klik pada bagian menu'
			WebUI.click(findTestObject('Object Repository/Change Password/span_Menu'))
			
			'pilih saldo'
			WebUI.click(findTestObject('Object Repository/Change Password/menu_Balance'))
			
			'klik pada tombol untuk span profile'
			WebUI.click(findTestObject('Object Repository/Change Password/Page_Balance/span_profile'))
			
			'pilih ubah password'
			WebUI.click(findTestObject('Object Repository/Change Password/Page_Balance/changepass'))
			
			'periksa field kosong'
			checkVerifyEqualorMatch(WebUI.verifyElementAttributeValue(findTestObject('Object Repository/Change Password/Page_Change Password/input__currentPass'),
				'class', 'form-control ng-untouched ng-pristine ng-invalid', GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Field current pass tidak kosong')
			
			'periksa field kosong'
			checkVerifyEqualorMatch(WebUI.verifyElementAttributeValue(findTestObject('Object Repository/Change Password/Page_Change Password/input__newPass'),
				'class', 'form-control ng-untouched ng-pristine ng-invalid', GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Field New pass tidak kosong')
			
			'periksa field kosong'
			checkVerifyEqualorMatch(WebUI.verifyElementAttributeValue(findTestObject('Object Repository/Change Password/Page_Change Password/input__confirmnewPass'),
				'class', 'form-control ng-untouched ng-pristine ng-invalid', GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Field confirm new pass tidak kosong')
		}
				
		'input password lama'
		setTextEmptyValidation(findTestObject('Object Repository/Change Password/Page_Change Password/input__currentPass'), '$Password Lama')
				
		'input password baru'
		setTextEmptyValidation(findTestObject('Object Repository/Change Password/Page_Change Password/input__newPass'), '$Password Baru')
		
		'input password baru confirm'
		setTextEmptyValidation(findTestObject('Object Repository/Change Password/Page_Change Password/input__confirmnewPass'), '$PasswordBaruConfirm')
		
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
				CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) +
						';') + '<' + notifMsg + '>')
				
				'klik pada tombol OK'
				WebUI.click(findTestObject('Object Repository/Change Password/Page_Balance/button_OK'))
				
				continue
			}
			
			'klik pada tombol OK'
			WebUI.click(findTestObject('Object Repository/Change Password/Page_Balance/button_OK'))
			
			'panggil fungsi logout'
			logoutFunction()
			
			'login menggunakan password yang baru diubah'
			if (loginFunction(rowExcel('$Password Baru')) == 0) {
				continue
			}
			
			'cek apakah muncul error gagal login'
			if (WebUI.verifyElementPresent(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/ErrorMsg'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
				GlobalVariable.FlagFailed = 1
			
				'tulis adanya error pada sistem web'
				CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
						GlobalVariable.FailedReasonLoginIssue)
			}
			
			'verifikasi berhasil login atau tidak'
			if (WebUI.verifyElementPresent(findTestObject('Object Repository/Change Password/Page_Balance/dropdownMenu'), GlobalVariable.Timeout) && GlobalVariable.FlagFailed == 0) {
				'write to excel success'
				CustomKeywords.'writetoexcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Status') - 1,
					GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
			}
		} else {
			'tulis ada error '
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) +
					';') + GlobalVariable.FailedReasonMandatory)
		}
	}
}

'fungsi tutup browser saat testing beres'
WebUI.closeBrowser()

def logoutFunction() {
	WebUI.delay(1)
	
	'klik pada tombol untuk span profile'
	WebUI.click(findTestObject('Object Repository/Change Password/Page_Balance/span_profile'))
	
	'lakukan logout'
	WebUI.click(findTestObject('Object Repository/Change Password/Page_Balance/span_Logout'))
	
	'verifikasi apakah captcha muncul'
	WebUI.verifyElementPresent(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/check_Recaptcha'), GlobalVariable.Timeout)
	
	'verifikasi apakah login dengan google muncul'
	WebUI.verifyElementPresent(findTestObject('Object Repository/Saldo/Page_Login - eendigo Platform/span_Lanjutkan dengan Google'), GlobalVariable.Timeout)
}

def loginFunction(int row) {
	'input data email'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_username'),
		findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, rowExcel('Username Login')))
	
	'input password'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_password'),
		findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, row))
	
	'cek perlukah tunggu agar recaptcha selesai solving'
	if ((findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, rowExcel('CaptchaEnabled')) == 'Yes' ||
		findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, rowExcel('CaptchaEnabled')) == '')) {
		WebUI.delay(1)
	
		String idObject = WebUI.getAttribute(findTestObject('RegisterLogin/Page_Login - eendigo Platform/check_Recaptcha'), 'id', FailureHandling.STOP_ON_FAILURE)
		
		modifyObjectCaptcha = WebUI.modifyObjectProperty(findTestObject('RegisterLogin/Page_Login - eendigo Platform/check_Recaptcha'), 'xpath', 'equals',
			'//*[@id="' + idObject + '"]/div/div[2]', true)
		
		WebUI.waitForElementAttributeValue(modifyObjectCaptcha, 'class', 'antigate_solver recaptcha solved', 120, FailureHandling.OPTIONAL)
	
		WebUI.delay(1)
	//
	//	WebUI.waitForElementAttributeValue(findTestObject('RegisterLogin/Page_Login - eendigo Platform/check_Recaptcha'), 'aria-checked', 'true', 60, FailureHandling.OPTIONAL)
	}
	
	'klik pada button login'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))
	
	'jika ada pilihan role'
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/Change Password/Page_Login - eendigo Platform/Admin Client_3'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		'cari element dengan nama role'
		elementRole = DriverFactory.webDriver.findElements(By.cssSelector('body > ngb-modal-window > div > div > app-multi-role > div > div.row > div > table tr'))
		
		'lakukan loop untuk cari nama role yang ditentukan'
		for (int i = 1; i <= elementRole.size() - 1; i++) {
			'cari nama role yag sesuai di opsi role'
			modifyRole = WebUI.modifyObjectProperty(findTestObject('Object Repository/Change Password/modifyobject'), 'xpath', 'equals', '/html/body/ngb-modal-window/div/div/app-multi-role/div/div[2]/div/table/tr[' + i + 1 + ']/td[1]', true)
	
			'jika nama object sesuai dengan nama role'
			if (findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, rowExcel('Pilih Role')).equalsIgnoreCase(
				WebUI.getAttribute(modifyRole, 'value', FailureHandling.STOP_ON_FAILURE))) {
				'ubah alamat xpath ke role yang dipilih'
				modifyRole = WebUI.modifyObjectProperty(findTestObject('Object Repository/Change Password/modifyobject'), 'xpath', 'equals', '/html/body/ngb-modal-window/div/div/app-multi-role/div/div[2]/div/table/tr[' + i + 1 + ']/td[2]/a', true)
			
				'klik role yang dipilih'
				WebUI.click(findTestObject('Object Repository/Change Password/modifyobject'))
				
				break
			}
		}
	}
	
	'cek apakah muncul error gagal login'
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/ErrorMsg')
		, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		GlobalVariable.FlagFailed = 1
	
		'tulis adanya error pada sistem web'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
				GlobalVariable.FailedReasonLoginIssue)
	}
}

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
	if (isMatch == false) {
		GlobalVariable.FlagFailed = 1
		
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		GlobalVariable.FlagFailed = 1
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
				GlobalVariable.FailedReasonVerifyEqualorMatch + reason)
	}
}

def setTextEmptyValidation(TestObject object, String testdata) {
	'jika testdata kosong'
	if (findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, rowExcel(testdata)).equalsIgnoreCase('')) {
		'select all text di field tersebut'
		WebUI.sendKeys(object, Keys.chord(Keys.CONTROL + 'a'))
		
		'hapus text tersebut'
		WebUI.sendKeys(object, Keys.chord(Keys.BACK_SPACE))
		
		'input text kosong'
		WebUI.setText(object, '')
	} else {
		'input text sesuai testdata'
		WebUI.setText(object, findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, rowExcel(testdata)))
	}
}

def rowExcel(String cellValue) {
	CustomKeywords.'writetoexcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}
