import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.driver.DriverFactory
import internal.GlobalVariable as GlobalVariable
import groovy.sql.Sql as Sql
import org.openqa.selenium.By as By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement

def chromePrefs = [:] as HashMap<String, ArrayList>

chromePrefs.put('download.default_directory', System.getProperty('user.dir') + '\\Download')

RunConfiguration.setWebDriverPreferencesProperty('prefs', chromePrefs)

'buka chrome\r\n'
WebUI.openBrowser('')

def driver = DriverFactory.getWebDriver()

'buat flag failed menjadi 0 agar tidak menimpa status failed pada excel'
GlobalVariable.FlagFailed = 0

if (TC != 'IsiSaldo' && TC != 'Tenant' && TC != 'IsiSaldoAuto') {
	
	'buka website APIAAS SIT, data diambil dari TestData Login'
	WebUI.navigateToUrl(findTestData(ExcelPathLogin).getValue(1, 2))
}
else {
	
	if (GlobalVariable.SettingEnvi == 'Trial') {
				
		'buka website billing system Trial, untuk isi saldo'
		WebUI.navigateToUrl(findTestData(ExcelPathLogin).getValue(1, 3))
		
	} 
	else if (GlobalVariable.SettingEnvi == 'Production') {
		
		'buka website billing system Production, untuk isi saldo'
		WebUI.navigateToUrl(findTestData(ExcelPathLogin).getValue(1, 4))
	}
}

def js = (JavascriptExecutor)driver

if (TC == 'EditProf') {
	
	'input email'
	WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/inputemail'),
		findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 9))
	
	'input password'
	WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/inputpassword'),
		findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 10))
	
	'ceklis pada reCaptcha'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/'+
		'div_reCAPTCHA_recaptcha-checkbox-border (4)'))
	
	'pada jeda waktu ini, isi captcha secara manual, automation testing dianggap sebagai robot oleh google'
	WebUI.delay(10)

	'focus pada button login'
	WebUI.focus(findTestObject('Object Repository/RegisterLogin/' + 'Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))

	'Klik Login'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/' + 'Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))

	'cek apakah muncul error setelah login'
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/Profile/' + 'Page_Balance/div_Unknown Error'),
		GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		GlobalVariable.FlagFailed = 1

		'tulis adanya error pada sistem web'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Edit Profile', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 2) +
			';') + GlobalVariable.FailedReasonUnknown)
	}
}
else if (TC == 'Regist') {
	
	WebUI.maximizeWindow()
	
	WebUI.delay(GlobalVariable.Timeout)
	
	'klik pada tombol buat akun'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/div_Buat Akun'))
	
	'check apakah mau buka hyperlink atau tidak'
	if (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 20) == 'Yes') {
		'click label syarat dan ketentuan'
		WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/label_KebijakanPrivasi'))
		
		'verify judul halaman == KEBIJAKAN PRIVASI'
		if (!WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/label_KebijakanorSyarat'), FailureHandling.OPTIONAL), 'KEBIJAKAN PRIVASI', false, FailureHandling.CONTINUE_ON_FAILURE)) {
				GlobalVariable.FlagFailed = 1
				
				'tulis gagal resend otp ke excel'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Register', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
						'Gagal membuka halaman KEBIJAKAN PRIVASI')
		}
		
		'kembali ke halaman login'
		WebUI.back()
		
		'klik pada tombol buat akun'
		WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/div_Buat Akun'))
		
		'click label syarat dan ketentuan'
		WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/label_SyaratdanKetentuan'))
		
		'verify judul halaman == SYARAT DAN KETENTUAN PENGGUNAAN PRODUK SOLUSI EENDIGO'
		if (!WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/label_KebijakanorSyarat'), FailureHandling.OPTIONAL), 'SYARAT DAN KETENTUAN PENGGUNAAN PRODUK SOLUSI EENDIGO', false, FailureHandling.CONTINUE_ON_FAILURE)) {
			GlobalVariable.FlagFailed = 1
			
			'tulis gagal resend otp ke excel'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Register', GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
					'Gagal membuka halama SYARAT DAN KETENTUAN PENGGUNAAN PRODUK SOLUSI EENDIGO')
		}
		
		'kembali ke halaman login'
		WebUI.back()
		
		'klik pada tombol buat akun'
		WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/div_Buat Akun'))
		
	}
	
	if (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 19) == 'Yes') {
		WebElement linkTerm = driver.findElement(By.cssSelector("#mat-tab-content-0-1 > div > form >"+
			" div:nth-child(6) > div > div > label > a:nth-child(1)"))
		WebElement linkPrivacy = driver.findElement(By.cssSelector("#mat-tab-content-0-1 > div > form >"+
			" div:nth-child(6) > div > div > label > a:nth-child(2)"))
		
		js.executeScript("arguments[0].remove()", linkTerm);
		js.executeScript("arguments[0].remove()", linkPrivacy);
		
		'checklist T&C'
		WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/AcceptTnC'))
	}
	
	'input pada field email'
	WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/'+
		'input_Buat Akun_form-control is-invalid ng-_7788b4'),
		findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 9))
	
	'ubah ke laman login'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/div_Masuk'))
	
	'klik pada tombol buat akun'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/div_Buat Akun'))
	
	'ambil teks dari field input email'
	if (WebUI.getAttribute(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/'+
		'input_Buat Akun_form-control is-invalid ng-_7788b4'), 'value', FailureHandling.OPTIONAL) 
			!= findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 9)) {
	
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Register', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 2) +
			';') + GlobalVariable.FailedReasonEmailField)
	}
	
	'input pada field nama pengguna'
	WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/'+
		'input_Buat Akun_form-control is-invalid ng-_7788b4_1'),
		findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 10))
	
	'input pada field kata sandi'
	WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/'+
		'input_Buat Akun_form-control is-invalid ng-_7788b4_1_2'),
		findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 11))
	
	'input pada field ketik ulang kata sandi'
	WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/'+
		'input_Buat Akun_form-control is-invalid ng-_7788b4_1_2_3'),
		findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 12))
	
	if (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 21) == 'Yes') {
		'bypass captcha langsung masuk verifikasi otp'
		WebElement buttonRegister= driver.findElement(By.cssSelector("#mat-tab-content-0-1 > div > form > button"))
		js.executeScript("arguments[0].removeAttribute('disabled')", buttonRegister);
	}
}
else if (TC == 'Key') {
	
	'input data email'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'
		+'input_Buat Akun_form-control ng-untouched n_ab9ed8'),
		findTestData('API_KEY/DataAPIKEY').getValue(GlobalVariable.NumOfColumn, 9))
	
	'input password'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'input_Buat Akun_form-control ng-untouched n_dd86a2'),
		findTestData('API_KEY/DataAPIKEY').getValue(GlobalVariable.NumOfColumn, 10))
	
	'ceklis pada reCaptcha'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/'+
		'div_reCAPTCHA_recaptcha-checkbox-border (4)'))
	
}
else if (TC == 'OCR') {	
	'input data email'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'input_Buat Akun_form-control ng-untouched n_ab9ed8'),
		findTestData(Path).getValue(2, 26))
	
	'input password'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'input_Buat Akun_form-control ng-untouched n_dd86a2'),
		findTestData(Path).getValue(2, 27))
	
	'ceklis pada reCaptcha'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/'+
		'div_reCAPTCHA_recaptcha-checkbox-border (4)'))
	
	'pada delay, lakukan captcha secara manual'
	WebUI.delay(10)
	
	'klik pada button login'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'button_Lanjutkan Perjalanan Anda'))
	
}

else if (TC == 'IsiSaldo') {
	
	'input data username'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/inputUsername'),
		findTestData(ExcelPathSaldoAPI).getValue(2, 9))
	
	'input password'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/inputpassword'),
		findTestData(ExcelPathSaldoAPI).getValue(2, 10))
	
	'klik tombol masuk'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/button_Masuk'))
}

else if (TC == 'Tenant') {
	
	WebUI.maximizeWindow()
	
	'klik tombol masuk'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/button_Masuk'))
	
	'input data username'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/inputUsername'),
		findTestData('Login/Login').getValue(2, 3))
	
	'input password'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/inputpassword'),
		findTestData('Login/Login').getValue(3, 3))
		
	'klik tombol masuk'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/button_Masuk'))
}
else if (TC == 'Saldo') {
	
	'input data email'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'input_Buat Akun_form-control ng-untouched n_ab9ed8'),
		findTestData(ExcelPathSaldo).getValue(2, 25))
	
	'input password'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'input_Buat Akun_form-control ng-untouched n_dd86a2'),
		findTestData(ExcelPathSaldo).getValue(2, 26))
	
	'ceklis pada reCaptcha'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/'+
		'div_reCAPTCHA_recaptcha-checkbox-border (4)'))
	
	'pada delay, lakukan captcha secara manual'
	WebUI.delay(10)
	
	'klik pada button login'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'button_Lanjutkan Perjalanan Anda'))
}
else if (TC == 'Layanan') {
	
	'input data email'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'input_Buat Akun_form-control ng-untouched n_ab9ed8'),
		findTestData(ExcelPathLayanan).getValue(2, 9))
	
	'input password'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'input_Buat Akun_form-control ng-untouched n_dd86a2'),
		findTestData(ExcelPathLayanan).getValue(2, 10))
	
	'ceklis pada reCaptcha'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/'+
		'div_reCAPTCHA_recaptcha-checkbox-border (4)'))
	
	'pada delay, lakukan captcha secara manual'
	WebUI.delay(10)
	
	'klik pada button login'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'button_Lanjutkan Perjalanan Anda'))
}

else if (TC == 'Role') {
	
	'input data email'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'input_Buat Akun_form-control ng-untouched n_ab9ed8'),
		findTestData(ExcelPathRole).getValue(2, 11))
	
	'input password'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'input_Buat Akun_form-control ng-untouched n_dd86a2'),
		findTestData(ExcelPathRole).getValue(2, 12))
	
	'ceklis pada reCaptcha'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/'+
		'div_reCAPTCHA_recaptcha-checkbox-border (4)'))
	
	'pada delay, lakukan captcha secara manual'
	WebUI.delay(10)
	
	'klik pada button login'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'button_Lanjutkan Perjalanan Anda'))
	
	'klik pada bagian admin client'
	WebUI.click(findTestObject('Object Repository/User Management-Role/'+
		'Page_Login - eendigo Platform/i_Admin Client_ft-edit'))
}
else if (TC == 'User') {
	
	'input data email'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'input_Buat Akun_form-control ng-untouched n_ab9ed8'),
		findTestData(Path).getValue(2, 11))
	
	'input password'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'input_Buat Akun_form-control ng-untouched n_dd86a2'),
		findTestData(Path).getValue(2, 12))
	
	'ceklis pada reCaptcha'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/'+
		'div_reCAPTCHA_recaptcha-checkbox-border (4)'))
	
	'pada delay, lakukan captcha secara manual'
	WebUI.delay(10)
	
	'klik pada button login'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'button_Lanjutkan Perjalanan Anda'))
}

else if (TC == 'Coupon') {
	
	'input data email'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'input_Buat Akun_form-control ng-untouched n_ab9ed8'),
		findTestData(Path).getValue(2, 33))
	
	'input password'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'input_Buat Akun_form-control ng-untouched n_dd86a2'),
		findTestData(Path).getValue(2, 34))
	
	'ceklis pada reCaptcha'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/'+
		'div_reCAPTCHA_recaptcha-checkbox-border (4)'))
	
	'pada delay, lakukan captcha secara manual'
	WebUI.delay(10)
	
	'klik pada button login'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'button_Lanjutkan Perjalanan Anda'))
}

else if (TC == 'TopUp') {
	
	'input data email'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'input_Buat Akun_form-control ng-untouched n_ab9ed8'),
		findTestData(Path).getValue(2, 19))
	
	'input password'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'input_Buat Akun_form-control ng-untouched n_dd86a2'),
		findTestData(Path).getValue(2, 20))
	
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
	
		'pilih admin client'
		WebUI.click(findTestObject('Object Repository/Change Password/Page_Login - eendigo Platform/Admin Client_3'))
	}
}
else if (TC == 'ChangePass') {
	
	'input data email'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'input_Buat Akun_form-control ng-untouched n_ab9ed8'),
		findTestData(Path).getValue(GlobalVariable.NumOfColumn, 9))
	
	'input password'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'input_Buat Akun_form-control ng-untouched n_dd86a2'),
		findTestData(Path).getValue(GlobalVariable.NumOfColumn, 10))
	
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
	
		'pilih admin client'
		WebUI.click(findTestObject('Object Repository/Change Password/Page_Login - eendigo Platform/Admin Client_3'))
	}
} 

else if (TC == 'IsiSaldoAuto') {
	
	'input data username'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/inputUsername'),
		findTestData(ExcelPathLogin).getValue(2, 18))
	
	'input password'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/inputpassword'),
		findTestData(ExcelPathLogin).getValue(2, 19))
	
	'klik tombol masuk'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/button_Masuk'))
}
else if (TC == 'TranxHist') {
	
	'deklarasi penghitungan role yang dipilih'
	int isSelected = 0
	
	'input data email'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'input_Buat Akun_form-control ng-untouched n_ab9ed8'),
		findTestData(Path).getValue(GlobalVariable.NumOfColumn, 9))
	
	'input password'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'input_Buat Akun_form-control ng-untouched n_dd86a2'),
		findTestData(Path).getValue(GlobalVariable.NumOfColumn, 10))
	
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
			if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 11).equalsIgnoreCase(
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
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('RiwayatTransaksi', GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
					GlobalVariable.FailedReasonRoleLogin)
		}
	}
}
else if (TC == 'TenantCekServices') {
	
	'input data email'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'input_Buat Akun_form-control ng-untouched n_ab9ed8'),
		findTestData(Path).getValue(4, 33))
	
	'input password'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'input_Buat Akun_form-control ng-untouched n_dd86a2'),
		findTestData(Path).getValue(4, 34))
	
	'ceklis pada reCaptcha'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/'+
		'div_reCAPTCHA_recaptcha-checkbox-border (4)'))
	
	'pada delay, lakukan captcha secara manual'
	WebUI.delay(10)
	
	'klik pada button login'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
		'button_Lanjutkan Perjalanan Anda'))
}

if (TC != 'IsiSaldo' && TC != 'Tenant' && TC != 'IsiSaldoAuto' && TC != 'Regist') {	
	
	if (GlobalVariable.SettingEnvi == 'Production') {
		'click pada production'
		WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/button_Production'))
	}
}

//'cek apakah muncul error unknown setelah login'
//if (WebUI.verifyElementPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'), 
//	GlobalVariable.Timeout, FailureHandling.OPTIONAL) == false) {
//	
//	'tulis adanya error pada sistem web'
//	CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumOfColumn,
//		GlobalVariable.StatusWarning, (findTestData(Path).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
//			GlobalVariable.FailedReasonUnknown)
//}
//
//'cek apakah muncul error gagal login'
//if (WebUI.verifyElementPresent(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/ErrorMsg')
//	,GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
//	
//	GlobalVariable.FlagFailed = 1
//	
//	'tulis adanya error pada sistem web'
//	CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumOfColumn,
//		GlobalVariable.StatusFailed, (findTestData(Path).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
//			GlobalVariable.FailedReasonLoginIssue)
//}
