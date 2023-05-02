import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import java.sql.Driver

import org.openqa.selenium.JavascriptExecutor as JavascriptExecutor

import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.writeExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Isi Saldo'
int CountColumnEdit = findTestData(ExcelPathLayanan).getColumnNumbers()

'deklarasi variabel untuk konek ke Database eendigo_dev'
def conn = CustomKeywords.'dbConnection.connect.connectDBAPIAAS_public'()

'deklarasi koneksi ke Database adins_apiaas_uat'
def connProd = CustomKeywords.'dbConnection.connect.connectDBAPIAAS_uatProduction'()

'panggil fungsi login'
WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'Layanan'], FailureHandling.STOP_ON_FAILURE)

'ambil index tab yang sedang dibuka di chrome'
int currentTab = WebUI.getWindowIndex()

'ambil WebDriver untuk menjalankan js executor'
WebDriver driver = DriverFactory.getWebDriver()

'siapkan js executor'
JavascriptExecutor js = ((driver) as JavascriptExecutor)

'buka tab baru'
js.executeScript('window.open();')

'ganti fokus robot ke tab baru'
WebUI.switchToWindowIndex(currentTab + 1)

'arahkan tab baru ke url eendigo beta dan lakukan login'
navigatetoeendigoBeta()

'ambil kode tenant di DB'
String tenantcode = CustomKeywords.'layananSaya.verifLayanan.getTenantCodefromDB'(conn, findTestData(ExcelPathLayanan).getValue(GlobalVariable.NumOfColumn, 10))

for(GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn <= CountColumnEdit; (GlobalVariable.NumOfColumn)++)
{
	'set penanda error menjadi 0'
	GlobalVariable.FlagFailed = 0
		
	'Lihat status TC di excel'
	StatusTC = findTestData(ExcelPathLayanan).getValue(GlobalVariable.NumOfColumn, 1)
		
	'jika data di kolom selanjutnya kosong, lanjutkan loop'
	if(StatusTC != 'Unexecuted')
	{
		continue;
	}
		
	'angka untuk menghitung data mandatory yang tidak terpenuhi'
	int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathLayanan).getValue(GlobalVariable.NumOfColumn, 4))
	
	'ubah ke tab billing system'
	WebUI.switchToWindowIndex(currentTab)
	
	'click menu tenant'
	WebUI.click(findTestObject('Tenant/menu_Tenant'))
	
	'panggil fungsi search tenant'
	searchTenant()
	
	'cek apakah object muncul setelah tenant dicari'
	if(WebUI.verifyElementPresent(findTestObject('Object Repository/LayananSaya/Page_eSignHub - Adicipta Inovasi Teknologi/ServiceSetting'), GlobalVariable.Timeout, FailureHandling.OPTIONAL))
	{
		'klik ke bagian service balance setting'
		WebUI.click(findTestObject('Object Repository/LayananSaya/Page_eSignHub - Adicipta Inovasi Teknologi/ServiceSetting'))
	}
	else
	{
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('LayananSaya', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathLayanan).getValue(GlobalVariable.NumOfColumn, 2) +
				';') + GlobalVariable.FailedReasonSearchFailed)
			
		continue
	}
	
	'deklarasi service yang aktif di UI'
	ArrayList<String> arrayServicesUI = new ArrayList<String>()
	
	//lanjut ambil data dari DB
}
		


def navigatetoeendigoBeta() {
	'buka website APIAAS SIT, data diambil dari TestData Login'
	WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 2))
	
	'isi username dengan email yang terdaftar'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_Buat Akun_form-control ng-untouched n_ab9ed8'),
		findTestData(ExcelPathLayanan).getValue(GlobalVariable.NumOfColumn, 10))
	
	'isi password yang sesuai'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_Buat Akun_form-control ng-untouched n_dd86a2'),
		findTestData(ExcelPathLayanan).getValue(GlobalVariable.NumOfColumn, 11))
	
	'ceklis pada reCaptcha'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/div_reCAPTCHA_recaptcha-checkbox-border (4)'))

	'pada delay, lakukan captcha secara manual'
	WebUI.delay(10)
	
	'klik pada button login'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))
}

def searchTenant() {
	'input nama tenant'
	WebUI.setText(findTestObject('Tenant/input_NamaTenant'), findTestData(ExcelPathLayanan).getValue(GlobalVariable.NumOfColumn,
			15))

	'input status'
	WebUI.setText(findTestObject('Tenant/input_Status'), findTestData(ExcelPathLayanan).getValue(GlobalVariable.NumOfColumn,
			16))

	'click enter untuk input select ddl'
	WebUI.sendKeys(findTestObject('Tenant/input_Status'), Keys.chord(Keys.ENTER))

	'click button cari'
	WebUI.click(findTestObject('Tenant/button_Cari'))
}
