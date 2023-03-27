import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.configuration.RunConfiguration as RunConfiguration
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
import org.openqa.selenium.WebDriver as WebDriver
import groovy.sql.Sql as Sql
import org.openqa.selenium.By as By
import org.openqa.selenium.support.ui.Select as Select
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory

'penanda bahwa data yang diperlukan sudah lengkap di excel'
int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathAPIDocs).getValue(GlobalVariable.NumOfColumn, 4))

'nama dokumen yang akan diambil'
String namadokumentasi = findTestData(ExcelPathAPIDocs).getValue(GlobalVariable.NumOfColumn, 8)

'user menentukan apakah file yang didownload langsung dihapus atau tidak lewat excel'
String FlagDelete = findTestData(ExcelPathAPIDocs).getValue(GlobalVariable.NumOfColumn, 9)

'mengambil alamat dari project katalon ini'
String userDir = System.getProperty('user.dir')

'directory tempat file akan didownload'
String filePath = userDir + '\\Download'

'driver chrome untuk pengalihan proses download'
WebDriver driver = DriverFactory.getWebDriver()

'Wait for Some time so that file gets downloaded and Stored in user defined path'
WebUI.delay(5)

'klik pada tombol garis tiga'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/i_KEPIN EDGAR_ft-menu font-medium-3'))

WebUI.delay(1)

'klik pada menu dokumentasi API'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/span_Dokumentasi API'))

'jika perlu, akan memanggil fungsi cek ddl dokumentasi'
if (GlobalVariable.KondisiCekDB == 'Yes')
{
	'verifikasi data DDL yang ada di web dengan DB'
	VerifyDocumentListAPI()
}

'input jenis dokumentasi yang akan didownload'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_API Documentation/input'), namadokumentasi)

'select status API'
WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_API Documentation/input'), Keys.chord(Keys.ENTER))

'klik pada tombol unduh'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_API Documentation/button_Unduh'))

WebUI.delay(GlobalVariable.Timeout)

'pengecekan file yang sudah didownload'
boolean isDownloaded = CustomKeywords.'documentationAPI.checkDocumentation.isFileDownloaded'(FlagDelete)

'jika file tidak terunduh, tulis gagal'
if (WebUI.verifyEqual(isDownloaded, true, FailureHandling.OPTIONAL)) 
{
    'tulis status sukses pada excel'
    CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('Dokumentasi API', GlobalVariable.NumOfColumn, GlobalVariable.StatusSuccess, 
        GlobalVariable.SuccessReason)
}
else 
{
    GlobalVariable.FlagFailed = 1
    'tulis kondisi gagal'
    CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('Dokumentasi API', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed, 
        GlobalVariable.FailedReasonDownloadProblem)
}

def VerifyDocumentListAPI(){
	'deklarasi variabel untuk konek ke Database APIAAS'
	def conn = CustomKeywords.'dbConnection.connect.connectDBAPIAAS_public'()
	
	'kumpulan string yang menyimpan hasil data dari DB'
	ArrayList<String> hasildb = CustomKeywords.'documentationAPI.checkDocumentation.getDocumentationAPIName'(conn)
	
	'klik pada panah turun ddl'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_API Documentation/span_Pilih API_ng-arrow-wrapper'))
	
	'ambil text dari UI Web APIAAS'
	ArrayList<String> hasilweb = CustomKeywords.'documentationAPI.checkDocumentation.getValueDDLDocumentationAPI'()
	
	'klik kembali panah turun ddl'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_API Documentation/span_Pilih API_ng-arrow-wrapper'))
	
	'sortir data pada hasil web'
	Collections.sort(hasilweb)
	
	'sortir data pada hasil db'
	Collections.sort(hasildb)
	
	for (int j = 0; j < hasildb.size ; j++)
	{
		'verifikasi semua opsi pada web sesuai dengan database'
		checkVerifyEqualorMatch(WebUI.verifyEqual(hasilweb[j], hasildb[j], FailureHandling.CONTINUE_ON_FAILURE))
	}
}

def checkVerifyEqualorMatch(Boolean isMatch) {
	if (isMatch == false) {
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		GlobalVariable.FlagFailed = 1
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('Dokumentasi API', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
			(findTestData(ExcelPathAPIDocs).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.FailedReasonDDL)
	}
}
