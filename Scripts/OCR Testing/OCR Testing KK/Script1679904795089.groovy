import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.testobject.ResponseObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import java.awt.Robot
import java.awt.event.KeyEvent
import java.sql.Driver

import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testcase.Variable
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable

import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.writeExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Edit Profile'
int CountColumnEdit = findTestData(ExcelPathOCRTesting).getColumnNumbers()

'deklarasi variabel untuk konek ke Database eendigo_dev'
def conn = CustomKeywords.'dbConnection.connect.connectDBAPIAAS_public'()

'deklarasi koneksi ke Database adins_apiaas_uat'
def connProd = CustomKeywords.'dbConnection.connect.connectDBAPIAAS_uatProduction'()

'deklarasi penggunaan robot untuk testcase'
Robot robot = new Robot()

'buka chrome'
WebUI.openBrowser('')

'buka website APIAAS SIT, data diambil dari TestData Login'
WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 2))

'input data email'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_Buat Akun_form-control ng-untouched n_ab9ed8'),
	findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 11))

'input password'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_Buat Akun_form-control ng-untouched n_dd86a2'),
	findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 12))

'ceklis pada reCaptcha'
WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/div_reCAPTCHA_recaptcha-checkbox-border (4)'))

'pada delay, lakukan captcha secara manual'
WebUI.delay(10)

'klik pada button login'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))

'ambil kode tenant di DB'
ArrayList<String> tenantcode = CustomKeywords.'ocrTesting.getParameterfromDB.getTenantCodefromDB'(conn, findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 11))

'ambil key trial yang aktif dari DB'
ArrayList<String> thekey = CustomKeywords.'ocrTesting.getParameterfromDB.getAPIKeyfromDB'(conn, tenantcode[0])

'pindah testcase sesuai jumlah di excel'
for(GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn < 3; (GlobalVariable.NumOfColumn)++)
{
	'deklarasi variable response'
	ResponseObject response
	
	'cek apakah perlu tambah API'
	String UseCorrectKey = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 14)
	
	'input key yang salah'
	String WrongKey = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 15)
	
	String emailuser = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 11)
	
	'angka untuk menghitung data mandatory yang tidak terpenuhi'
	int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 4))
	
	'deklarasi variabel angka'
	int isSaldoBerkurang, Saldobefore, Saldoafter, isTrxIncreased, HitAPITrx
	
	'penanda untuk HIT yang berhasil dan gagal'
	HitAPITrx = 1
	
	'set penanda error menjadi 0'
	GlobalVariable.FlagFailed = 0
	
	'panggil fungsi filter saldo berdasarkan input user'
	filterSaldo()
	
	'panggil fungsi skip ke page terakhir'
	SkiptotheLastPages()
	
	'panggil fungsi ambil transaksi terakhir di tabel'
	String no_Trx_before = getTrxNumber()
	
	'variabel yang menyimpan saldo sebelum adanya transaksi'
	Saldobefore = Integer.parseInt(WebUI.getText(findTestObject('Object Repository/API_KEY/Page_Balance/h3_45,649')).replace(',',''))
	
	'jika user ingin menggunakan key yang valid'
	if(UseCorrectKey == 'Yes')
	{
		'lakukan proses HIT api dengan parameter image, key, dan juga tenant'
		response = WS.sendRequest(findTestObject('Object Repository/OCR Testing/OCR KK', [('img'): findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 8),
		('key'):thekey[0], ('tenant'):tenantcode[0]]))
	}
	//jika user ingin mencoba key yang diambil dari excel
	else
	{
		'lakukan proses HIT api dengan parameter image, key, dan juga tenant'
		response = WS.sendRequest(findTestObject('Object Repository/OCR Testing/OCR KK', [('img'): findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 8),
		('key'):WrongKey, ('tenant'):tenantcode[0]]))
		
	}
	'ambil message respon dari HIT tersebut'
	message_ocr = WS.getElementPropertyValue(response, 'message')
	
	'ambil status dari respon HIT tersebut'
	state_ocr = WS.getElementPropertyValue(response, 'status')
	
	'refresh halaman web'
	robot.keyPress(KeyEvent.VK_F5)
	robot.keyRelease(KeyEvent.VK_F5);
	
	'panggil fungsi filter saldo berdasarkan inputan user'
	filterSaldo()
	
	'panggil fungsi untuk pindah ke laman terakhir table'
	SkiptotheLastPages()
	
	'variabel yang diharapkan menyimpan number transaksi sesudah hit'
	String no_Trx_after = getTrxNumber()
	
	'jika user ingin cek ke DB hasil HIT API nya'
	if(GlobalVariable.KondisiCekDB == 'Yes')
	{
		'simpan trx number terbaru dari DB'
		ArrayList<String> LatestMutation= CustomKeywords.'ocrTesting.getParameterfromDB.getLatestMutationfromDB'(connProd, tenantcode[0])
		
		'simpan transaction number dari web'
		ArrayList<String> trxnum = new ArrayList<>()
		
		'data transaction number dari web dimasukkan ke array'
		trxnum.add(no_Trx_after)
		
		for (int i = 0; i<LatestMutation.size; i++)
		{
			'jika data transaction number di web dan DB tidak sesuai'
			if(!WebUI.verifyMatch(LatestMutation[i], trxnum[i], false, FailureHandling.CONTINUE_ON_FAILURE))
			{
				'anggap HIT Api gagal'
				HitAPITrx = 0
			}
		}
	}
	
	'simpan saldo setelah di HIT'
	Saldoafter = Integer.parseInt(WebUI.getText(findTestObject('Object Repository/API_KEY/Page_Balance/h3_45,649')).replace(',',''))
	
	'jika selisih saldo sesuai dengan service price KTP yaitu 500'
	if(Saldobefore - Saldoafter == 500)
	{
		'transaksi dilakukan'
		isSaldoBerkurang = 1
	}
	else
	{
		'transaksi tidak terjadi'
		isSaldoBerkurang = 0
	}
	
	if(no_Trx_after > no_Trx_before)
	{
		'web mencatat transaksi terbaru'
		isTrxIncreased = 1
	}
	else
	{
		'web tidak mencatat transaksi terbaru'
		isTrxIncreased = 0
	}

	println isSaldoBerkurang
	println isTrxIncreased
	println HitAPITrx
	println no_Trx_before
	println no_Trx_after
	
	'jika tidak ada message error dan kondisi lain terpenuhi'
	if(message_ocr == '' && state_ocr == 'SUCCESS' && isTrxIncreased == 1 && isSaldoBerkurang == 1 && HitAPITrx == 1)
	{
		'untuk testcase diatas 3 adalah foto KTP yang tidak sesuai kriteria'
		if(GlobalVariable.NumOfColumn > 3)
		{
			GlobalVariable.FlagFailed = 1
			'tulis kondisi gagal'
			CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('OCR KTP', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
			GlobalVariable.FailedReasonCriteriaBypass)
		}
		'tulis status sukses pada excel'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('OCR KTP', GlobalVariable.NumOfColumn, GlobalVariable.StatusSuccess,
		GlobalVariable.SuccessReason)
	}
	//kondisi jika transaksi berhasil tapi tidak tercatat/tersimpan di DB
	else if(state_ocr == 'SUCCESS' && isTrxIncreased == 0 && isSaldoBerkurang == 1)
	{
		GlobalVariable.FlagFailed = 1
		'tulis kondisi gagal'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('OCR KTP', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
		GlobalVariable.FailedReasonTrxNotinDB)
	}
	//kondisi jika transaksi berhasil tapi saldo tidak berkurang
	else if(state_ocr == 'SUCCESS' && isTrxIncreased == 1 && isSaldoBerkurang == 0)
	{
		GlobalVariable.FlagFailed = 1
		'tulis kondisi gagal'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('OCR KTP', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
		GlobalVariable.FailedReasonBalanceNotChange)
	}
	//kondisi transaksi tidak tampil dan tidak tersimpan di DB
	else if(HitAPITrx == 0 && state_ocr == 'FAILED' && isTrxIncreased == 0 && isSaldoBerkurang == 1)
	{
		GlobalVariable.FlagFailed = 1
		'tulis kondisi gagal'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('OCR KTP', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
		GlobalVariable.FailedReasonSaldoBocor)
	}
	else
	{
		GlobalVariable.FlagFailed = 1
		'write to excel status failed dan reason'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('OCR KTP', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		message_ocr)
	}
}

'fungsi langsung ke laman akhir'
def SkiptotheLastPages() {
	'cari button skip di footer'
	def elementbuttonskip = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance-prod > div.ng-star-inserted > app-msx-paging-v2 > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))
	
	'ambil banyaknya laman footer'
	int lastPage = elementbuttonskip.size()
	
	'ubah path object button skip'
	def modifybuttonskip = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Balance/i_Catatan_datatable-icon-skip'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li["+ (lastPage) +"]", true)

	'klik button skip to last page'
	WebUI.click(modifybuttonskip)
}

def getTrxNumber() {
	'ambil alamat trxnumber'
	def variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance-prod > div.ng-star-inserted > app-msx-paging-v2 > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))
	
	'banyaknya row table'
	int lastIndex = variable.size()
		
	'modifikasi alamat object trxnumber'
	def modifytrxnumber = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/TrxNumber'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (lastIndex) +"]/datatable-body-row/div[2]/datatable-body-cell[6]/div/p", true)
							
	'simpan nomor transaction number ke string'												
	String no_Trx = WebUI.getText(modifytrxnumber)
	
	'kembalikan nomor transaksi'
	return no_Trx
}

def filterSaldo() {
	'tunggu webpage load'
	WebUI.delay(4)
	
	'isi field input tipe saldo'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'), findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 9))
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'), Keys.chord(Keys.ENTER))
	
	'isi field tipe transaksi'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipetranc'), findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 10))
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipetranc'), Keys.chord(Keys.ENTER))
		
	'klik pada button cari'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/button_Cari'))
}