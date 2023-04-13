import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.main.CustomKeywordDelegatingMetaClass as CustomKeywordDelegatingMetaClass
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import com.kms.katalon.entity.global.GlobalVariableEntity as GlobalVariableEntity
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import groovy.sql.Sql as Sql
import org.openqa.selenium.By as By
import org.openqa.selenium.support.ui.Select as Select
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement

HashMap<String, ArrayList> chromePrefs = new HashMap<String, ArrayList>()

chromePrefs.put('download.default_directory', System.getProperty('user.dir') + '\\Download')

RunConfiguration.setWebDriverPreferencesProperty('prefs', chromePrefs)

'buka chrome\r\n'
WebUI.openBrowser('')

def driver = DriverFactory.getWebDriver()

'buat flag failed menjadi 0 agar tidak menimpa status failed pada excel'
GlobalVariable.FlagFailed = 0

'buka website APIAAS SIT, data diambil dari TestData Login'
WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 2))

def js = (JavascriptExecutor)driver

if (TC == 'EditProf')
{
	'input email'
	WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/input_Buat Akun_form-control ng-untouched n_ab9ed8'),
		findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 8))
	
	'input password'
	WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/input_Buat Akun_form-control ng-untouched n_dd86a2'),
		findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 9))
	
	'ceklis pada reCaptcha'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/div_reCAPTCHA_recaptcha-checkbox-border (4)'))
}
else if(TC == 'Regist')
{
	WebUI.maximizeWindow()
	
	WebUI.delay(GlobalVariable.Timeout)
	'klik pada tombol buat akun'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/div_Buat Akun'))
	
	if(findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 18) == 'Yes')
	{
		WebElement linkTerm = driver.findElement(By.cssSelector("#mat-tab-content-0-1 > div > form > div:nth-child(6) > div > div > label > a:nth-child(1)"))
		WebElement linkPrivacy = driver.findElement(By.cssSelector("#mat-tab-content-0-1 > div > form > div:nth-child(6) > div > div > label > a:nth-child(2)"))
		
		js.executeScript("arguments[0].remove()", linkTerm);
		js.executeScript("arguments[0].remove()", linkPrivacy);
		
		'checklist T&C'
		WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/AcceptTnC'))
	}
	
	'input pada field email'
	WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/input_Buat Akun_form-control is-invalid ng-_7788b4'),
		findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 8))
	
	'input pada field nama pengguna'
	WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/input_Buat Akun_form-control is-invalid ng-_7788b4_1'),
		findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 9))
	
	'input pada field kata sandi'
	WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/input_Buat Akun_form-control is-invalid ng-_7788b4_1_2'),
		findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 10))
	
	'input pada field ketik ulang kata sandi'
	WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/input_Buat Akun_form-control is-invalid ng-_7788b4_1_2_3'),
		findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 11))
	
	'bypass captcha langsung masuk verifikasi otp'
	WebElement buttonRegister= driver.findElement(By.cssSelector("#mat-tab-content-0-1 > div > form > button"))
	js.executeScript("arguments[0].removeAttribute('disabled')", buttonRegister);
}
else if (TC == 'Key')
{
	'input data email'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_Buat Akun_form-control ng-untouched n_ab9ed8'),
		findTestData('API_KEY/DataAPIKEY').getValue(GlobalVariable.NumOfColumn, 8))
	
	'input password'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_Buat Akun_form-control ng-untouched n_dd86a2'),
		findTestData('API_KEY/DataAPIKEY').getValue(GlobalVariable.NumOfColumn, 9))
	
	'ceklis pada reCaptcha'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/div_reCAPTCHA_recaptcha-checkbox-border (4)'))
	
}
else if (TC == 'OCR')
{
	'input data email'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_Buat Akun_form-control ng-untouched n_ab9ed8'),
		findTestData(Path).getValue(2, 25))
	
	'input password'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_Buat Akun_form-control ng-untouched n_dd86a2'),
		findTestData(Path).getValue(2, 26))
	
	'ceklis pada reCaptcha'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/div_reCAPTCHA_recaptcha-checkbox-border (4)'))
	
	'pada delay, lakukan captcha secara manual'
	WebUI.delay(10)
	
	'klik pada button login'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))
	
}

