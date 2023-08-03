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
import java.sql.Connection

import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Isi Saldo'
int countColumnEdit = findTestData(ExcelPathSaldoAPI).getColumnNumbers()

Connection conn

if(GlobalVariable.SettingEnvi == 'Production') {
	'deklarasi koneksi ke Database eendigo_dev'
	conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_public'()
} else if(GlobalVariable.SettingEnvi == 'Trial') {
	'deklarasi koneksi ke Database eendigo_dev_uat'
	conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_devUat'()
}

'buka chrome\r\n'
WebUI.openBrowser('')

'arahkan tab baru ke url eendigo beta dan lakukan login'
navigatetoeendigoBeta()

'ambil kode tenant di DB'
String tenantcode = CustomKeywords.'ocrTesting.GetParameterfromDB.getTenantCodefromDB'(conn,
	findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 11))

'filter saldo sesuai kebutuhan user'
filterSaldo()

'scroll ke bawah halaman'
WebUI.scrollToElement(findTestObject('Object Repository/API_KEY/Page_Balance/skiptoLast_page'), GlobalVariable.Timeout)

'cek apakah button skip enable atau disable'
if(WebUI.verifyElementVisible(findTestObject('Object Repository/API_KEY/Page_Balance/skiptoLast_page'), FailureHandling.OPTIONAL)){
	
	'klik button skip to last page'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/skiptoLast_page'))
}

verifyTableContent(conn, tenantcode)

def navigatetoeendigoBeta() {
	'buka website APIAAS SIT, data diambil dari TestData Login'
	WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 2))
	
	'isi username dengan email yang terdaftar'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_username'),
		findTestData(ExcelPathSaldoAPI).getValue(2, 11))
	
	'isi password yang sesuai'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_password'),
		findTestData(ExcelPathSaldoAPI).getValue(2, 12))
	
	'ceklis pada reCaptcha'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/check_Recaptcha'))

	'pada delay, lakukan captcha secara manual'
	WebUI.delay(10)
	
	'klik pada button login'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))
	
	'cek apakah muncul error unknown setelah login'
	if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'),
		GlobalVariable.Timeout, FailureHandling.OPTIONAL) == false) {
		
		GlobalVariable.FlagFailed = 1
	}
}

'ambil no. transaksi pada tabel'
def verifyTableContent(connection, String tenant) {
	'ambil alamat trxnumber'
	def variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance-prod > div.ng-star-inserted > app-msx-paging-v2 > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))
	
	'banyaknya row table'
	int lastIndex = variable.size()
	
	'flag jika ada error pada verifikasi'
	int flagError = 1
	
	'ambil data table dari db'
	ArrayList result = CustomKeywords.'apikey.CheckSaldoAPI.getTrialTableContent'(connection, tenant)
	
	'modifikasi object tanggal transaksi'
	def modifytglTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/TglTrx'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (lastIndex) +"]/datatable-body-row/div[2]/datatable-body-cell[1]/div", true)
		
	'modifikasi object kantor'
	def modifyKantor = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/KantorLocation'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (lastIndex) +"]/datatable-body-row/div[2]/datatable-body-cell[2]/div", true)

	'modifikasi object tipe transaksi'
	def modifytipeTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/TipeTrx'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (lastIndex) +"]/datatable-body-row/div[2]/datatable-body-cell[3]/div", true)

	'modifikasi object sumber transaksi'
	def modifysumberTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/sumberTrx'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (lastIndex) +"]/datatable-body-row/div[2]/datatable-body-cell[4]/div", true)

	'modifikasi object tenant transaksi'
	def modifytenantTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/TenantTrx'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (lastIndex) +"]/datatable-body-row/div[2]/datatable-body-cell[5]/div", true)

	'modifikasi object trxnumber'
	def modifytrxnumber = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/TrxNumber'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (lastIndex) +"]/datatable-body-row/div[2]/datatable-body-cell[6]/div", true)
		
	'modifikasi object reference transaksi'
	def modifyrefTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/refTrx'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (lastIndex) +"]/datatable-body-row/div[2]/datatable-body-cell[7]/div", true)
	
	'modifikasi object quantity transaksi'
	def modifyqtyTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/modifyobject'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (lastIndex) +"]/datatable-body-row/div[2]/datatable-body-cell[8]/div", true)

	'modifikasi object hasil proses transaksi'
	def modifyprocTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/procTrx'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (lastIndex) +"]/datatable-body-row/div[2]/datatable-body-cell[10]/div", true)

	'modifikasi object hasil proses transaksi'
	def modifycatatanTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/catatan'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (lastIndex) +"]/datatable-body-row/div[2]/datatable-body-cell[11]/div", true)

	println WebUI.getText(modifytglTrx)
	println WebUI.getText(modifyKantor)
	println WebUI.getText(modifytipeTrx)
	println WebUI.getText(modifyqtyTrx)
	
	'check status semua match data'
	ArrayList arrayMatch = []
		
	'kembalikan nomor transaksi'
	int arrayIndex = 0
	
	'verify tanggal transaksi ui = db'
	checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifytglTrx), result[arrayIndex++].replace('.0',''), false, FailureHandling.CONTINUE_ON_FAILURE)))
	
	'verify kantor ui = db'
	checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyKantor), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE)))
	
	'verify tipe transaksi ui = db'
	checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifytipeTrx), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE)))
	
	'verify sumber transaksi ui = db'
	checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifysumberTrx), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE)))
	
	'verify tenant transaksi ui = db'
	checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifytenantTrx), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE)))
	
	'verify nomor transaksi ui = db'
	checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifytrxnumber), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE)))
	
	'verify reference number transaksi ui = db'
	checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyrefTrx), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE)))
	
	'verify quantity transaksi ui = db'
	checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyqtyTrx) , result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE)))
	
	'verify hasil proses transaksi ui = db'
	checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyprocTrx), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE)))
	
	'verify catatan transaksi ui = db'
	checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifycatatanTrx), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE)))
	
	'jika ada verifikasi yang gagal'
	if (arrayMatch.contains(false)) {
		
		'kembalikan flag error'
		flagError = 0
	}
	return flagError
}

'fungsi untuk melakukan pengecekan '
def checkVerifyEqualOrMatch(Boolean isMatch) {
	if ((isMatch == false)) {

	}
}

'fungsi untuk filter saldo berdasarkan input user'
def filterSaldo() {
	'tunggu webpage load'
	WebUI.delay(4)
	
	'isi field input tipe saldo'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'),
		findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 21))
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'), Keys.chord(Keys.ENTER))
	
	'isi field tipe transaksi'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipetranc'),
		findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 22))
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipetranc'), Keys.chord(Keys.ENTER))
		
	'klik pada button cari'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/button_Cari'))
}