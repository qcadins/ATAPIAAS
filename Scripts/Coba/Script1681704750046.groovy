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

WebUI.navigateToUrl('http://gdkwebsvr:4100/login')

WebUI.click(findTestObject('null'))

WebUI.setText(findTestObject('null'), 
    'usera@gmail.com')

WebUI.setEncryptedText(findTestObject('null'), 
    'iFGeFYmXIrU6ruIopQUS+w==')

WebUI.click(findTestObject('null'))

WebUI.click(findTestObject('null'))

WebUI.click(findTestObject('null'))

WebUI.click(findTestObject('null'))

WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/input_Tanggal Transaksi Dari_transactionDateStart'))

WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/input_Pengguna_user'))

WebUI.click(findTestObject('null'))

WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/input_Ref Number_referenceNo'))

WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/input_Nama Dokumen_documentName'))

WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/input_Tanggal Transaksi Sampai_transactionDateEnd'))

WebUI.click(findTestObject('null'))

WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/button_Unduh Excel'))

WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/button_Set Ulang'))

WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/button_Cari'))

