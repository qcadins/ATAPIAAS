import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities

import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable

//'mencari directory excel'
//GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')
//
//'mencari directory excel lain'
//GlobalVariable.DataFilePath2 = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Simulasi/Simulasi Hitung Top Up Using Coupon.xlsx')
//
//'mendapat jumlah kolom dari sheet Edit Profile'
//int countColumnEdit = findTestData(ExcelPathTopUp).columnNumbers
//
//'deklarasi koneksi ke Database adins_apiaas_uat'
//Connection conndev = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_esign'()
//
//'panggil fungsi login'
//WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'TopUp', ('SheetName') : 'TopUp',
//	('Path') : ExcelPathTopUp], FailureHandling.STOP_ON_FAILURE)
//
//'klik pada tombol menu'
//WebUI.click(findTestObject('Object Repository/Top Up/Page_Balance/spanMenu'))
//
//'klik pada menu isi saldo'
//WebUI.click(findTestObject('Object Repository/Top Up/Page_Balance/span_Isi Saldo'))
//
//'cek apakah tombol menu dalam jangkauan web'
//if (WebUI.verifyElementVisible(findTestObject('Object Repository/User Management-Role/Page_List Roles/tombolX_menu'), FailureHandling.OPTIONAL)) {
//	
//	'klik pada tombol silang menu'
//	WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/tombolX_menu'))
//}
//
//'ambil nama TipeSaldo dari DB'
//ArrayList<String> namaTipeSaldoDB = CustomKeywords.'topup.TopupVerif.getDDLTipeSaldo'(conndev)
//
//'call function check ddl untuk Tipe Saldo'
//checkDDL(findTestObject('Object Repository/Top Up/Page_Topup Balance/inputtipesaldo'), namaTipeSaldoDB, 'DDL Tipe Saldo')
//
//def checkDDL(TestObject objectDDL, ArrayList<String> listDB, String reason) {
//	'declare array untuk menampung ddl'
//	ArrayList<String> list = []
//
//	'click untuk memunculkan ddl'
//	WebUI.click(objectDDL)
//	
//	'get id ddl'
//	id = WebUI.getAttribute(findTestObject('Object Repository/Top Up/ddlClass'), 'id', FailureHandling.CONTINUE_ON_FAILURE)
//
//	'get row'
//	variable = DriverFactory.webDriver.findElements(By.cssSelector(('#' + id) + '> div > div:nth-child(2) div'))
//	
//	'looping untuk get ddl kedalam array'
//	for (i = 1; i < variable.size(); i++) {
//		'modify object DDL'
//		modifyObjectDDL = WebUI.modifyObjectProperty(findTestObject('Object Repository/User Management-Role/modifyObject'), 'xpath', 'equals', ((('//*[@id=\'' +
//			id) + '-') + i) + '\']', true)
//
//		'add ddl ke array'
//		list.add(WebUI.getText(modifyObjectDDL))
//	}
//	
//	'verify ddl ui = db'
//	checkVerifyEqualOrMatch(listDB.containsAll(list), reason)
//
//	'verify jumlah ddl ui = db'
//	checkVerifyEqualOrMatch(WebUI.verifyEqual(list.size(), listDB.size(), FailureHandling.CONTINUE_ON_FAILURE), ' Jumlah ' + reason)
//	
//	'Input enter untuk tutup ddl'
//	WebUI.sendKeys(objectDDL, Keys.chord(Keys.ENTER))
//}
//
//def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
//	if (isMatch == false) {
//		GlobalVariable.FlagFailed = 1
//	}
//}

//==========================================================================

/*Coba untuk extension otomatis Recaptcha*/

System.setProperty("webdriver.chrome.driver", "Drivers/chromedriver.exe")

ChromeOptions options = new ChromeOptions()

options.addExtensions(new File("Drivers/nocaptchaai_chrome_1.7.6.crx"))

options.addExtensions(new File("Drivers/Smart_Wait.crx"))

DesiredCapabilities caps = new DesiredCapabilities()

caps.setCapability(ChromeOptions.CAPABILITY, options)

WebDriver driver = new ChromeDriver(caps)

DriverFactory.changeWebDriver(driver)

WebUI.navigateToUrl('https://config.nocaptchaai.com/?apikey=kvnedgar9286-35bde35f-e305-699a-0af2-d6fb983c8c4a')

'buka website APIAAS SIT, data diambil dari TestData Login'
WebUI.navigateToUrl(findTestData(ExcelPath).getValue(1, 2))

'input email'
WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/inputemail'),
	'JONAUDRIS23@MAILSAC.COM')

'input password'
WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/inputpassword'),
	'P@ssw0rd123')

'pada jeda waktu ini, isi captcha secara manual, automation testing dianggap sebagai robot oleh google'
WebUI.delay(3)

WebUI.closeBrowser()

driver = new ChromeDriver(caps)

DriverFactory.changeWebDriver(driver)

'buka website APIAAS SIT, data diambil dari TestData Login'
WebUI.navigateToUrl(findTestData(ExcelPath).getValue(1, 2))

'input email'
WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/inputemail'),
	'JONAUDRIS23@MAILSAC.COM')

'input password'
WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/inputpassword'),
	'P@ssw0rd123')

'pada jeda waktu ini, isi captcha secara manual, automation testing dianggap sebagai robot oleh google'
WebUI.delay(10)

'Klik Login'
WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))
