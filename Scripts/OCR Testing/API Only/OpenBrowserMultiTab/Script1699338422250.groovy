import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.driver.DriverFactory
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.WebDriver

'deklarasi untuk buka browser dan ambil driver nya'
WebDriver driver = CustomKeywords.'login.Browser.settingandOpen'(Path, rowExcel('CaptchaEnabled'))

'maximize window browser'
WebUI.maximizeWindow()

'navigasi ke url bucket standard'
WebUI.navigateToUrl(findTestData(ExcelPathLoginOCR).getValue(2, 3))

'login username google'
WebUI.setText(findTestObject('OCR Testing/checkLog/input_username'), findTestData(ExcelPathLoginOCR).getValue(2, 5))

'klik pada tombol next'
WebUI.click(findTestObject('OCR Testing/checkLog/next_username'))

'input password'
WebUI.setText(findTestObject('OCR Testing/checkLog/input_pass'), findTestData(ExcelPathLoginOCR).getValue(2, 6))

'klik pada tombol next'
WebUI.click(findTestObject('OCR Testing/checkLog/next_pass'))

'jika muncul page penawaran google'
if (WebUI.verifyElementPresent(findTestObject('OCR Testing/checkLog/span_NotNow'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
	'klik pada tombol tolak penawaran google'
	WebUI.click(findTestObject('OCR Testing/checkLog/span_NotNow'))
}

'siapkan js executor'
JavascriptExecutor js = (JavascriptExecutor)driver

'buka tab baru'
js.executeScript('window.open();')

'ganti fokus robot ke tab baru'
WebUI.switchToWindowIndex(1)

'navigasi ke url bucket coldline'
WebUI.navigateToUrl(findTestData(ExcelPathLoginOCR).getValue(2, 4))

'ganti fokus robot ke tab baru'
WebUI.switchToWindowIndex(0)

def rowExcel(String cellValue) {
	CustomKeywords.'writetoexcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}
