import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.driver.DriverFactory
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.By as By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement

'panggil fungsi untuk open browser'
JavascriptExecutor js = CustomKeywords.'login.Browser.settingandOpen'(Path, rowExcel('CaptchaEnabled'))

'buat flag failed menjadi 0 agar tidak menimpa status failed pada excel'
GlobalVariable.FlagFailed = 0

if (TC != 'IsiSaldo' && TC != 'Tenant' && TC != 'IsiSaldoAuto') {
	'buka website APIAAS SIT, data diambil dari TestData Login'
	WebUI.navigateToUrl(findTestData(ExcelPathLogin).getValue(1, 2))
} else {
	if (GlobalVariable.SettingEnvi == 'Trial') {
		'buka website billing system Trial, untuk isi saldo'
		WebUI.navigateToUrl(findTestData(ExcelPathLogin).getValue(1, 3))
	} else if (GlobalVariable.SettingEnvi == 'Production') {
		'buka website billing system Production, untuk isi saldo'
		WebUI.navigateToUrl(findTestData(ExcelPathLogin).getValue(1, 4))
	}
}

'cek perlukah tunggu agar recaptcha selesai solving'
if ((findTestData(Path).getValue(GlobalVariable.NumOfColumn, rowExcel('CaptchaEnabled')) == 'Yes' ||
	findTestData(Path).getValue(GlobalVariable.NumOfColumn, rowExcel('CaptchaEnabled')) == '') && 
	TC != 'Tenant' && TC != 'IsiSaldo' && TC != 'IsiSaldoAuto') {
	WebUI.delay(1)

	String idObject = WebUI.getAttribute(findTestObject('RegisterLogin/Page_Login - eendigo Platform/check_Recaptcha'), 'id', FailureHandling.STOP_ON_FAILURE)
	
	modifyObjectCaptcha = WebUI.modifyObjectProperty(findTestObject('RegisterLogin/Page_Login - eendigo Platform/check_Recaptcha'), 'xpath', 'equals', 
		'//*[@id="' + idObject + '"]/div/div[2]', true)
	
	WebUI.waitForElementAttributeValue(modifyObjectCaptcha, 'class', 'antigate_solver recaptcha solved', 60, FailureHandling.OPTIONAL)

	WebUI.delay(1)
//	
//	WebUI.waitForElementAttributeValue(findTestObject('RegisterLogin/Page_Login - eendigo Platform/check_Recaptcha'), 'aria-checked', 'true', 60, FailureHandling.OPTIONAL)
}	
	
if (TC == 'Regist') {
	WebUI.maximizeWindow()
	
	WebUI.delay(GlobalVariable.Timeout)
	
	'klik pada tombol buat akun'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/div_Buat Akun'))
	
	'check apakah mau buka hyperlink atau tidak'
	if (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, rowExcel('Open Hyperlink?')) == 'Yes') {
		'click label syarat dan ketentuan'
		WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/label_KebijakanPrivasi'))
		
		'verify judul halaman == KEBIJAKAN PRIVASI'
		if (!WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/label_KebijakanorSyarat'), FailureHandling.OPTIONAL), 'KEBIJAKAN PRIVASI', false, FailureHandling.CONTINUE_ON_FAILURE)) {
				GlobalVariable.FlagFailed = 1
				
				'tulis gagal membuka halaman kebijakan privasi'
				CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'('Register', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
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
			
			'tulis gagal membuka halaman syarat dan ketentuan'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'('Register', GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
					'Gagal membuka halaman SYARAT DAN KETENTUAN PENGGUNAAN PRODUK SOLUSI EENDIGO')
		}
		
		'kembali ke halaman login'
		WebUI.back()
		
		'klik pada tombol buat akun'
		WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/div_Buat Akun'))
	}
	
	if (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, rowExcel('Checklist T&C?')) == 'Yes') {
		WebElement linkTerm = driver.findElement(By.cssSelector("#mat-tab-content-0-1 > div > form >" +
			" div:nth-child(6) > div > div > label > a:nth-child(1)"))
		WebElement linkPrivacy = driver.findElement(By.cssSelector("#mat-tab-content-0-1 > div > form >" +
			" div:nth-child(6) > div > div > label > a:nth-child(2)"))
		
		js.executeScript("arguments[0].remove()", linkTerm)
		js.executeScript("arguments[0].remove()", linkPrivacy)
		
		'checklist T&C'
		WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/AcceptTnC'))
	}
	
	'input pada field email'
	WebUI.setText(
		findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/inputemailRegister'),
			findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, rowExcel('$Email registrasi')))
	
	'ubah ke laman login'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/div_Masuk'))
	
	'klik pada tombol buat akun'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/div_Buat Akun'))
	
	WebUI.delay(1)
	
	'ambil teks dari field input email'
	if (WebUI.getAttribute(
		findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/inputemailRegister'), 'value', FailureHandling.OPTIONAL)
			!= findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, rowExcel('$Email registrasi'))) {
	
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'('Register', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) +
			';') + GlobalVariable.FailedReasonEmailField)
	}
	
	'input pada field nama pengguna'
	WebUI.setText(
		findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/inputNamaRegister'),
			findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, rowExcel('$Username registrasi')))
	
	'input pada field kata sandi'
	WebUI.setText(
		findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/input_passRegister'),
			findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, rowExcel('$Pass registrasi')))
	
	'input pada field ketik ulang kata sandi'
	WebUI.setText(
		findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/input_confirmPassRegist'),
			findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, rowExcel('$KetikUlang Pass')))
	
//	if (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 21) == 'Yes') {
//		'bypass captcha langsung masuk verifikasi otp'
//		WebElement buttonRegister= driver.findElement(By.cssSelector("#mat-tab-content-0-1 > div > form > button"))
//		js.executeScript("arguments[0].removeAttribute('disabled')", buttonRegister);
//	}
} else if (TC == 'OCR') {
	'input data email'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_username'),
		findTestData(Path).getValue(GlobalVariable.NumOfColumn, Row))
	
	'input password'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_password'),
		findTestData(Path).getValue(GlobalVariable.NumOfColumn, Row + 1))
} else if (TC == 'Tenant') {
	WebUI.maximizeWindow()
	
	'input data username'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/inputUsername'),
		findTestData(ExcelPathLogin).getValue(2, 3))
	
	'input password'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/inputpassword'),
		findTestData(ExcelPathLogin).getValue(3, 3))
		
	'klik tombol masuk'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/button_Masuk'))
} else if (TC == 'ChangePass') {
	'input data email'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_username'),
		findTestData(Path).getValue(GlobalVariable.NumOfColumn, rowExcel('Username Login')))
	
	'input password'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_password'),
		findTestData(Path).getValue(GlobalVariable.NumOfColumn, rowExcel('Password Login')))
} else if (TC == 'IsiSaldoAuto') {
	'input data username'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/inputUsername'),
		findTestData(ExcelPathLogin).getValue(2, 18))
	
	'input password'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/inputpassword'),
		findTestData(ExcelPathLogin).getValue(2, 19))
	
	'klik tombol masuk'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/button_Masuk'))
} else if (TC == 'TranxHist') {
//	'deklarasi penghitungan role yang dipilih'
//	int isSelected = 0
	
	'input data email'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_username'),
		findTestData(Path).getValue(GlobalVariable.NumOfColumn, rowExcel('Username Login')))
	
	'input password'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_password'),
		findTestData(Path).getValue(GlobalVariable.NumOfColumn, rowExcel('Password Login')))
	
//	'jika ada pilihan role'
//	if (WebUI.verifyElementPresent(
//		findTestObject('Object Repository/Change Password/Page_Login - eendigo Platform/Admin Client_3'),
//			GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
//
//		'cari element dengan nama role'
//		def elementRole = DriverFactory.getWebDriver().findElements(By.cssSelector('body > ngb-modal-window > div > div > app-multi-role > div > div.row > div > table tr'))
//
//		'lakukan loop untuk cari nama role yang ditentukan'
//		for (int i = 1; i <= elementRole.size() - 1; i++) {
//
//			'cari nama role yag sesuai di opsi role'
//			def modifyRole = WebUI.modifyObjectProperty(findTestObject('Object Repository/Change Password/modifyobject'), 'xpath', 'equals', "/html/body/ngb-modal-window/div/div/app-multi-role/div/div[2]/div/table/tr["+ (i+1) +"]/td[1]", true)
//
//			'jika nama object sesuai dengan nama role'
//			if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 11).equalsIgnoreCase(
//				WebUI.getAttribute(modifyRole, 'value', FailureHandling.STOP_ON_FAILURE))) {
//
//				'ubah alamat xpath ke role yang dipilih'
//				modifyRole = WebUI.modifyObjectProperty(findTestObject('Object Repository/Change Password/modifyobject'), 'xpath', 'equals', "/html/body/ngb-modal-window/div/div/app-multi-role/div/div[2]/div/table/tr["+ (i+1) +"]/td[2]/a", true)
//
//				'klik role yang dipilih'
//				WebUI.click(findTestObject('Object Repository/Change Password/modifyobject'))
//
//				'penanda adanya role yang dipilih'
//				isSelected = 1
//
//				break;
//			}
//		}
//		'tulis error dan lanjut testcase berikutnya'
//		if (isSelected == 0) {
//
//			'tulis adanya error pada sistem web'
//			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'('RiwayatTransaksi', GlobalVariable.NumOfColumn,
//				GlobalVariable.StatusFailed, (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
//					GlobalVariable.FailedReasonRoleLogin)
//		}
//	}
} else {
	'input email'
	WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/inputemail'),
		findTestData(Path).getValue(GlobalVariable.NumOfColumn, rowExcel(Username)))
	
	'input password'
	WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/inputpassword'),
		findTestData(Path).getValue(GlobalVariable.NumOfColumn, rowExcel(Password)))
}

if (TC != 'IsiSaldo' && TC != 'Tenant' && TC != 'IsiSaldoAuto' && TC != 'Regist') {
	'klik pada button login'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))
	
	'cek apakah berhasil login'
	for (int i = 1; i <= 10; i++) {
		'cek apakah berhasil login'
		if (WebUI.verifyElementPresent(findTestObject('RegisterLogin/Page_Login - eendigo Platform/textSmallError'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
			'write to excel reason failed error login'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(Path).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
					WebUI.getText(findTestObject('RegisterLogin/Page_Login - eendigo Platform/textSmallError')))
			
			GlobalVariable.FlagFailed = 1
			
			break
		} else if (WebUI.verifyElementPresent(findTestObject('Profile/Page_Balance/dropdownProfile'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
			break
		} else {
			if (i == 10) {
				'write to excel reason failed error login'
				CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(Path).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
						GlobalVariable.FailedReasonLoginIssue)
				
				GlobalVariable.FlagFailed = 1
			}
		}
	}
}

if (TC != 'IsiSaldo' && TC != 'Tenant' && TC != 'IsiSaldoAuto') {	
	if (GlobalVariable.SettingEnvi == 'Production' && WebUI.verifyElementPresent(findTestObject('Object Repository/Saldo/Page_Balance/button_Production'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		'click pada production'
		WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/button_Production'))
	}
}

def rowExcel(String cellValue) {
	CustomKeywords.'writetoexcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, SheetName, cellValue)
}
