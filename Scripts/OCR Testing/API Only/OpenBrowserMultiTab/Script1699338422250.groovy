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
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities
import com.kms.katalon.core.webui.driver.DriverFactory
import org.openqa.selenium.WebDriver

//inisialisasi webdriver
WebDriver driver

System.setProperty("webdriver.chrome.driver", "Drivers/chromedriver.exe")

def chromePrefs = [:] as HashMap<String, ArrayList>

chromePrefs.put('download.default_directory', System.getProperty('user.dir') + '\\Download')

ChromeOptions options = new ChromeOptions()

options.addExtensions(new File("Drivers/nocaptchaai_chrome_1.7.6.crx"))

options.addExtensions(new File("Drivers/Smart_Wait.crx"))

options.setExperimentalOption('prefs', chromePrefs)

DesiredCapabilities caps = new DesiredCapabilities()

caps.setCapability(ChromeOptions.CAPABILITY, options)

//RunConfiguration.setWebDriverPreferencesProperty('prefs', chromePrefs)

driver = new ChromeDriver(caps)

DriverFactory.changeWebDriver(driver)
//end of initialization webdriver

'maximize window browser'
WebUI.maximizeWindow()

'navigasi ke url bucket standard'
WebUI.navigateToUrl(findTestData(ExcelPathLogin).getValue(8, 7))

'login username google'
WebUI.setText(findTestObject('OCR Testing/checkLog/input_username'), 
	findTestData(ExcelPathLogin).getValue(8, 9))

'klik pada tombol next'
WebUI.click(findTestObject('OCR Testing/checkLog/next_username'))

'input password'
WebUI.setText(findTestObject('OCR Testing/checkLog/input_pass'), 
	findTestData(ExcelPathLogin).getValue(8, 10))

'klik pada tombol next'
WebUI.click(findTestObject('OCR Testing/checkLog/next_pass'))

'jika muncul page penawaran google'
if (WebUI.verifyElementPresent(findTestObject('OCR Testing/checkLog/span_NotNow'), GlobalVariable.Timeout,
	FailureHandling.OPTIONAL)) {
	
	'klik pada tombol tolak penawaran google'
	WebUI.click(findTestObject('OCR Testing/checkLog/span_NotNow'))
}

'siapkan js executor'
JavascriptExecutor js = ((driver) as JavascriptExecutor)

'buka tab baru'
js.executeScript('window.open();')

'ganti fokus robot ke tab baru'
WebUI.switchToWindowIndex(1)

'navigasi ke url bucket coldline'
WebUI.navigateToUrl(findTestData(ExcelPathLogin).getValue(8, 8))

'ganti fokus robot ke tab baru'
WebUI.switchToWindowIndex(0)