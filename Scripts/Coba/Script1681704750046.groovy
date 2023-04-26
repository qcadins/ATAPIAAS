import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.By as By
import org.openqa.selenium.WebDriver as WebDriver
import java.sql.Driver as Driver

WebUI.openBrowser('')

WebUI.navigateToUrl('http://websvr:8000/login')

WebUI.setText(findTestObject('Object Repository/Coba/Page_eSignHub - Adicipta Inovasi Teknologi/input_Selamat datang kembali di Billing Sys_95ee84'), 
    'admesign')

WebUI.click(findTestObject('Object Repository/Coba/Page_eSignHub - Adicipta Inovasi Teknologi/div_Selamat datang kembali di Billing System_input'))

WebUI.setText(findTestObject('Object Repository/Coba/Page_eSignHub - Adicipta Inovasi Teknologi/input_Selamat datang kembali di Billing Sys_768062'), 
    'password')

WebUI.click(findTestObject('Object Repository/Coba/Page_eSignHub - Adicipta Inovasi Teknologi/button_Masuk'))

WebUI.click(findTestObject('Object Repository/Coba/Page_eSignHub - Adicipta Inovasi Teknologi/i_Indonesian_ft-menu font-medium-3'))

WebUI.click(findTestObject('Object Repository/Coba/Page_eSignHub - Adicipta Inovasi Teknologi/span_Tenant'))

WebUI.click(findTestObject('Object Repository/Coba/Page_eSignHub - Adicipta Inovasi Teknologi/a_Baru'))

'get total form'
variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-add-tenant > div.row.match-height > div > div > div > div > form div'))

println(variable.size())

'get total form'
variable2 = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-add-tenant > div.row.match-height > div > div > div > div > form > div div'))

println(variable2.size())

'get total form'
variable3 = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-add-tenant > div.row.match-height > div > div > div > div > form > row'))

println(variable3.size())

