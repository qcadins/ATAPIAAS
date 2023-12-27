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
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.WebDriver
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import com.kms.katalon.core.configuration.RunConfiguration

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Edit Profile'
int countColumnEdit = findTestData(ExcelPathChangePass).columnNumbers

'untuk line 27 hingga 51 adalah setting untuk chromedriver dan plugin/extension no catpcha'
WebDriver driver

System.setProperty("webdriver.chrome.driver", "Drivers/chromedriver.exe")

ChromeOptions options = new ChromeOptions()

options.addExtensions(new File("Drivers/nocaptchaai_chrome_1.7.6.crx"))

DesiredCapabilities caps = new DesiredCapabilities()

caps.setCapability(ChromeOptions.CAPABILITY, options)

def chromePrefs = [:] as HashMap<String, ArrayList>

chromePrefs.put('download.default_directory', System.getProperty('user.dir') + '\\Download')

RunConfiguration.setWebDriverPreferencesProperty('prefs', chromePrefs)

driver = new ChromeDriver(caps)

DriverFactory.changeWebDriver(driver)

'aktifkan nocaptcha by link'
WebUI.navigateToUrl('https://config.nocaptchaai.com/?apikey=' + GlobalVariable.APIKEYCaptcha + '')

'buka website APIAAS SIT, data diambil dari TestData Login'
WebUI.navigateToUrl(findTestData(ExcelPathLogin).getValue(1, 2))

for (GlobalVariable.NumOfColumn = 2; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++) {
	
	'set penanda error menjadi 0'
	GlobalVariable.FlagFailed = 0
		
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).length() == 0) {
		
		break
		
	} else if (findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
		
		'lakukan proses login dengan password lama'
		loginFunction(rowExcel('Password Login'))
		
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
		WebUI.setText(findTestObject('Object Repository/Change Password/Page_Change Password/input__currentPass'),
			findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, rowExcel('$Password Lama')))
		
		'input password baru'
		WebUI.setText(findTestObject('Object Repository/Change Password/Page_Change Password/input__newPass'),
			findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, rowExcel('$Password Baru')))
		
		'input password baru confirm'
		WebUI.setText(findTestObject('Object Repository/Change Password/Page_Change Password/input__confirmnewPass'),
			findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, rowExcel('$PasswordBaruConfirm')))
		
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
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) +
						';') + '<' + notifMsg + '>')
				
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
			
			'login menggunakan password yang baru diubah'
			if (loginFunction(rowExcel('$Password Baru')) == 0) {
			
				continue
			}
			
			'cek apakah muncul error gagal login'
			if (WebUI.verifyElementPresent(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/ErrorMsg')
				,GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
			
				GlobalVariable.FlagFailed = 1
			
				'tulis adanya error pada sistem web'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
						GlobalVariable.FailedReasonLoginIssue)
			}
			
			'verifikasi berhasil login atau tidak'
			if (WebUI.verifyElementPresent(findTestObject('Object Repository/Change Password/Page_Balance/dropdownMenu'),
				GlobalVariable.Timeout) && GlobalVariable.FlagFailed == 0) {
			
				'write to excel success'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Status') - 1,
					GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
			}
		} else {
			
			'tulis ada error '
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) +
					';') + GlobalVariable.FailedReasonMandatory)
			
			'panggil fungsi logout'
			logoutFunction()
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
	
	'tunggu tombol tidak di disable lagi'
	if (WebUI.waitForElementNotHasAttribute(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'),
		'disabled', 100, FailureHandling.OPTIONAL)) {
	
		'klik pada button login'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))
	}
	
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
			if (findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, rowExcel('Pilih Role')).equalsIgnoreCase(
				WebUI.getAttribute(modifyRole, 'value', FailureHandling.STOP_ON_FAILURE))) {
				
				'ubah alamat xpath ke role yang dipilih'
				modifyRole = WebUI.modifyObjectProperty(findTestObject('Object Repository/Change Password/modifyobject'), 'xpath', 'equals', "/html/body/ngb-modal-window/div/div/app-multi-role/div/div[2]/div/table/tr["+ (i+1) +"]/td[2]/a", true)
			
				'klik role yang dipilih'
				WebUI.click(findTestObject('Object Repository/Change Password/modifyobject'))
				
				break
			}
		}
	}
	
	'cek apakah muncul error gagal login'
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/ErrorMsg')
		,GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
	
		GlobalVariable.FlagFailed = 1
	
		'tulis adanya error pada sistem web'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
				GlobalVariable.FailedReasonLoginIssue)
	}
}

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
	if (isMatch == false) {
		GlobalVariable.FlagFailed = 1
		
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		GlobalVariable.FlagFailed = 1
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathChangePass).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
				GlobalVariable.FailedReasonVerifyEqualorMatch + reason)
	}
}

def rowExcel(String cellValue) {
	return CustomKeywords.'writeToExcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}