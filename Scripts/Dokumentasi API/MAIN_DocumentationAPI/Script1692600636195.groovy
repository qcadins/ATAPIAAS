import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import groovy.sql.Sql as Sql

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Edit Profile'
int countColumnEdit = findTestData(ExcelPathAPIDocs).columnNumbers

'panggil fungsi login'
WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'DocAPI', ('SheetName') : 'Dokumentasi API',
	('Path') : ExcelPathAPIDocs], FailureHandling.STOP_ON_FAILURE)

'pindah testcase sesuai jumlah di excel'
for (GlobalVariable.NumOfColumn = 2; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++) {
	
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathAPIDocs).getValue(GlobalVariable.NumOfColumn, 1).length() == 0) {
		
		break
	} else if (findTestData(ExcelPathAPIDocs).getValue(GlobalVariable.NumOfColumn, 1).equalsIgnoreCase('Unexecuted')) {
		
		'angka untuk menghitung data mandatory yang tidak terpenuhi'
		int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathAPIDocs).getValue(GlobalVariable.NumOfColumn, 5))
		
		'tunggu hingga page terload dengan sempurna'
		WebUI.delay(GlobalVariable.Timeout)
		
		'klik pada tombol garis tiga'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/spanMenu'))
		
		WebUI.delay(1)
		
		'klik pada menu dokumentasi API'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/span_Dokumentasi API'))
		
		'cek apakah tombol menu dalam jangkauan web'
		if (WebUI.verifyElementVisible(findTestObject('Object Repository/User Management-Role/Page_List Roles/tombolX_menu'), FailureHandling.OPTIONAL)) {
			
			'klik pada tombol silang menu'
			WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/tombolX_menu'))
		}
		
		'jika perlu, akan memanggil fungsi cek ddl dokumentasi'
		if (GlobalVariable.KondisiCekDB == 'Yes') {
			
			'verifikasi data DDL yang ada di web dengan DB'
			VerifyDocumentListAPI()
		}
		
		'input jenis dokumentasi yang akan didownload'
		WebUI.setText(findTestObject('Object Repository/API_KEY/Page_API Documentation/input'),
			findTestData(ExcelPathAPIDocs).getValue(GlobalVariable.NumOfColumn, 9))
		
		'select status API'
		WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_API Documentation/input'), Keys.chord(Keys.ENTER))
		
		'cek apakah perlu kembalikan ddl ke default'
		if (findTestData(ExcelPathAPIDocs).getValue(GlobalVariable.NumOfColumn, 11) == 'Yes') {
			
			'klik pada tombol silang di ddl'
			WebUI.click(findTestObject('Object Repository/API_KEY/Page_API Documentation/CrossDDL'))
		}
		
		'klik pada tombol unduh'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_API Documentation/button_Unduh'))
		
		WebUI.delay(GlobalVariable.Timeout)
		
		'pengecekan file yang sudah didownload'
		boolean isDownloaded = CustomKeywords.'documentationAPI.CheckDocumentation.isFileDownloaded'(
			findTestData(ExcelPathAPIDocs).getValue(GlobalVariable.NumOfColumn, 10))
		
		'jika file tidak terunduh, tulis gagal'
		if (WebUI.verifyEqual(isDownloaded, true, FailureHandling.OPTIONAL) && isMandatoryComplete == 0 &&
			findTestData(ExcelPathAPIDocs).getValue(GlobalVariable.NumOfColumn, 11) == 'No') {
			
			'tulis status sukses pada excel'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Dokumentasi API',
				GlobalVariable.NumOfColumn, GlobalVariable.StatusSuccess,
					GlobalVariable.SuccessReason)
		}
		else if (WebUI.verifyEqual(isDownloaded, true, FailureHandling.OPTIONAL) &&
			findTestData(ExcelPathAPIDocs).getValue(GlobalVariable.NumOfColumn, 11) == 'Yes') {
			
			'tulis kondisi gagal'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Dokumentasi API',
				GlobalVariable.NumOfColumn, GlobalVariable.StatusSuccess,
					GlobalVariable.FailedReasonDownloadProblem + ' Bypass')
		}
		else if (isMandatoryComplete > 0) {
			
			'tulis kondisi gagal'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Dokumentasi API',
				GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
					GlobalVariable.FailedReasonMandatory)
		}
		else {
			'tulis kondisi gagal'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Dokumentasi API',
				GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
					GlobalVariable.FailedReasonDownloadProblem)
		}
	}
}

'tutup browser'
WebUI.closeBrowser()

def VerifyDocumentListAPI(){
	'deklarasi variabel untuk konek ke Database APIAAS'
	def conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_public'()
	
	'kumpulan string yang menyimpan hasil data dari DB'
	ArrayList hasildb = CustomKeywords.'documentationAPI.CheckDocumentation.getDocumentationAPIName'(conn)
	
	'klik pada panah turun ddl'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_API Documentation/span_panah_Pilih API'))
	
	'ambil text dari UI Web APIAAS'
	ArrayList hasilweb = CustomKeywords.'documentationAPI.CheckDocumentation.getValueDDLDocumentationAPI'()
	
	'klik kembali panah turun ddl'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_API Documentation/span_panah_Pilih API'))
	
	'sortir data pada hasil web'
	Collections.sort(hasilweb)
	
	'sortir data pada hasil db'
	Collections.sort(hasildb)
	
	for (int j = 0; j < hasildb.size ; j++) {
		
		'verifikasi semua opsi pada web sesuai dengan database'
		checkVerifyEqualorMatch(WebUI.verifyEqual(hasilweb[j], hasildb[j], FailureHandling.CONTINUE_ON_FAILURE))
	}
}

def checkVerifyEqualorMatch(Boolean isMatch) {
	if (isMatch == false) {
		
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		GlobalVariable.FlagFailed = 1
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Dokumentasi API', 
			GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
			(findTestData(ExcelPathAPIDocs).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.FailedReasonDDL)
	}
}
