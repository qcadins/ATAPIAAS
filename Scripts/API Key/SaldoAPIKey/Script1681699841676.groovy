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

'mendapat jumlah kolom dari sheet Edit Profile'
int CountColumnEdit = findTestData(ExcelPathSaldoAPI).getColumnNumbers()

'deklarasi variabel untuk konek ke Database eendigo_dev'
def conn = CustomKeywords.'dbConnection.connect.connectDBAPIAAS_public'()

'deklarasi koneksi ke Database adins_apiaas_uat'
def connProd = CustomKeywords.'dbConnection.connect.connectDBAPIAAS_uatProduction'()

'panggil fungsi login'
WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'SaldoAPI'], FailureHandling.STOP_ON_FAILURE)

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
String tenantcode = CustomKeywords.'ocrTesting.getParameterfromDB.getTenantCodefromDB'(conn, findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 10))

for(GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn < 3; (GlobalVariable.NumOfColumn)++)
{
	'set penanda error menjadi 0'
	GlobalVariable.FlagFailed = 0
	
	'Lihat status TC di excel'
	StatusTC = findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 1)
	
	'jika data di kolom selanjutnya kosong, lanjutkan loop'
	if(StatusTC == '' || StatusTC == 'Failed' || StatusTC == 'Success')
	{
		continue;
	}
	
	'angka untuk menghitung data mandatory yang tidak terpenuhi'
	int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 4))
	
	int Saldobefore, Saldoafter, JumlahTopUp
	
	'ambil saldo sebelum isi ulang'
	Saldobefore = getSaldoforTransaction(findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 14))
	
	'ubah ke tab billing system'
	WebUI.switchToWindowIndex(currentTab)
	
	'klik pada input tenant'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tenant'))
	
	'panggil fungsi check jumlah tenant di DB dan UI'
	checkTenantcount(connProd)
	
	'input nama tenant yang akan digunakan'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tenant'), findTestData(
		ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 12))
	
	'pencet enter pada textbox'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tenant'), Keys.chord(
		Keys.ENTER))
	
	'klik pada input vendor'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input vendor'))
	
	'check jumlah vendor di DB dan UI'
	checkVendorcount(connProd, tenantcode)
	
	'input nama vendor yang akan digunakan'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input vendor'), findTestData(
		ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 13))
	
	'pencet enter pada textbox'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input vendor'), Keys.chord(
		Keys.ENTER))
	
	'klik pada input tipe saldo'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tipe saldo'))
	
	'panggil fungsi cek banyak tipe saldo yang bisa diisi ulang'
	checkTipeSaldocount(connProd, tenantcode)
	
	'input nama saldo yang akan diisi ulang'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tipe saldo'), findTestData(
		ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 14))
	
	'pencet enter pada textbox'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tipe saldo'), Keys.chord(
		Keys.ENTER))
	
	'input jumlah saldo yang akan ditambahkan'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input_Tambah Saldo_qty'),
		findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 15))
	
	'input nomor tagihan untuk proses isi ulang saldo'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input_Nomor Tagihan_refNo'),
		findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 16))
	
	'input notes/catatan untuk proses isi ulang saldo'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input_Catatan_notes'),
		findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 17))
	
	'input tanggal isi ulang saldo'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input_Tanggal Pembelian_trxDate'),
		findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 18))
	
	'klik di luar textbox agar memunculkan tombol lanjut'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/containerForm'))
	
	'klik pada tombol lanjut'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/button_Lanjut'))
	
	'klik pada tombol proses isi ulang  saldo'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/button_Ya, proses'))
	
	'ubah fokus ke tab eendigo beta'
	WebUI.switchToWindowIndex(currentTab+1)
	
	'refresh laman web untuk ambil saldo baru'
	WebUI.refresh()
	
	'ambil jumlah saldo pada menu trial'
	Saldoafter = getSaldoforTransaction(findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 14))
	
	'ambil jumlah topup yang diinput oleh user dari excel'
	JumlahTopUp = Integer.parseInt(findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 15))
	
	'saldo sekarang harus sama dengan saldo sebelumnya ditambah jumlah topup'
	if(Saldobefore + JumlahTopUp == Saldoafter)
	{
		'tulis status sukses pada excel'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('APIAAS-Saldo', GlobalVariable.NumOfColumn, GlobalVariable.StatusSuccess,
		GlobalVariable.SuccessReason)
	}
	else
	{
		GlobalVariable.FlagFailed = 1
		'tulis kondisi gagal'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('APIAAS-Saldo', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
		GlobalVariable.FailedReasonTopUpFailed)
	}
}

'tutup browser'
WebUI.closeBrowser()


'cek jumlah tenant di DB dan UI'
def checkTenantcount(connection) {
	'ambil list tenant'
	def elementjumlahlisttenant = DriverFactory.getWebDriver().findElements(By.xpath('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-topup-balance/div[2]/div/div/div/div/form/div[1]/div[1]/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div'))
	
	'ambil hitungan tenant yang ada'
	int countWeb = (elementjumlahlisttenant.size()) - 1
	
	'ambil hitungan tenant dari DB'
	int countDB = CustomKeywords.'apikey.checkSaldoAPI.gettotalTenant'(connection)
	
	'jika hitungan di UI dan DB tidak sesuai'
	if(countWeb != countDB)
	{
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and reason topup failed'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('APIAAS-Saldo', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonVerifyEqualorMatch)
	}
}

'cek jumlah vendor di DB dan UI'
def checkVendorcount(connection, tenantcode) {
	'ambil list vendor'
	def elementjumlahlistvendor = DriverFactory.getWebDriver().findElements(By.xpath('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-topup-balance/div[2]/div/div/div/div/form/div[1]/div[2]/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div'))
	
	'ambil hitungan vendor yang ada'
	int countWeb = (elementjumlahlistvendor.size()) - 1
	
	'flag vendor sesuai'
	int isVendorFound = 0
	
	'ambil hitungan vendor dari DB'
	int countDB = CustomKeywords.'apikey.checkSaldoAPI.gettotalVendor'(connection, tenantcode)
	
	'ambil nama vendor dari DB'
	String namaVendorDB = CustomKeywords.'apikey.checkSaldoAPI.getVendorName'(connection, tenantcode)
	
	'cari nama vendor'
	for(int i=0; i<=countWeb ; i++)
	{
		'ambil object dari ddl'
		def modifyNamaVendor = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/VendorList'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-topup-balance/div[2]/div/div/div/div/form/div[1]/div[2]/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div["+(i+1)+"]/span", true)
		
		'cari nama vendor yang sesuai'
		if(WebUI.getText(modifyNamaVendor) == namaVendorDB)
		{
			'ubah flag menjadi ketemu'
			isVendorFound = 1
		}
	}
	
	'jika tidak ada match pada jumlah count atau nama vendor'
	if(isVendorFound == 0 || countWeb != countDB)
	{
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('APIAAS-Saldo', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonVerifyEqualorMatch)
	}
}

'cek jumlah vendor di DB dan UI'
def checkTipeSaldocount(connection, tenantcode) {
	'ambil list tipe saldo'
	def elementjumlahtipe = DriverFactory.getWebDriver().findElements(By.xpath('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-topup-balance/div[2]/div/div/div/div/form/div[1]/div[3]/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div'))
	
	'flag data yang match'
	int isDataMatch = 0
	
	'ambil hitungan vendor yang ada'
	int countWeb = (elementjumlahtipe.size()) - 1
	
	'ambil hitungan tenant dari DB'
	int countDB = CustomKeywords.'apikey.checkSaldoAPI.gettotalTipeSaldo'(connection, tenantcode)
	
	'nama-nama tipe saldo yang sedang aktif dari DB'
	ArrayList<String> namaTipefromDB = CustomKeywords.'apikey.checkSaldoAPI.getNamaTipeSaldo'(connection, tenantcode)
	
	'nama-nama tipe saldo sedang aktif dari UI'
	ArrayList<String> namaTipefromUI = new ArrayList<String>()
	
	'loop untuk tambah data ke array from UI'
	for(int i=1; i<=countWeb; i++)
	{
		'ambil object dari ddl'
		def modifyNamaTipe = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/TipeSaldoList'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-topup-balance/div[2]/div/div/div/div/form/div[1]/div[3]/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div["+(i+1)+"]/span", true)
		
		'tambahkan nama tipe saldo ke array'
		String data = WebUI.getText(modifyNamaTipe)
		namaTipefromUI.add(data)
	}

	'cek setiap data di UI dengan data di DB sebagai pembanding'
	for (String tipe : namaTipefromDB) 
	{
		'jika ada data yang tidak terdapat pada arraylist yang lain'
		if (!namaTipefromUI.contains(tipe)) 
		{
			'ada data yang tidak match'
			isDataMatch = 0;
			'berhentikan loop'
			break;
		}
		'kondisi ini bisa ditemui jika data match'
		isDataMatch = 1
	}
	
	'jika hitungan di UI dan DB tidak sesuai atau data tidak match'
	if(countWeb != countDB || isDataMatch == 0)
	{
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('APIAAS-Saldo', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonVerifyEqualorMatch)
	}
}

def navigatetoeendigoBeta() {
	'buka website APIAAS SIT, data diambil dari TestData Login'
	WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 2))
	
	'isi username dengan email yang terdaftar'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_Buat Akun_form-control ng-untouched n_ab9ed8'),
		findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 10))
	
	'isi password yang sesuai'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_Buat Akun_form-control ng-untouched n_dd86a2'),
		findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 11))
	
	'ceklis pada reCaptcha'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/div_reCAPTCHA_recaptcha-checkbox-border (4)'))

	'pada delay, lakukan captcha secara manual'
	WebUI.delay(10)
	
	'klik pada button login'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))
}

'ambil saldo sesuai testing yang dilakukan'
def getSaldoforTransaction(String NamaSaldo) {
	
	'deklarasi jumlah saldo sekarang'
	int saldoNow
	
	'cari element dengan nama saldo'
	def elementNamaSaldo = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance-prod > div.row.match-height > div > lib-balance-summary > div > div'))
	
	'lakukan loop untuk cari nama saldo yang ditentukan'
	for(int i=1; i<=elementNamaSaldo.size(); i++)
	{
		'cari nama saldo yang sesuai di list saldo'
		def modifyNamaSaldo = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Balance/span_OCR KK'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[1]/div/lib-balance-summary/div/div["+ (i) +"]/div/div/div/div/div[1]/span", true)

		'jika nama object sesuai dengan nama saldo'
		if(WebUI.getText(modifyNamaSaldo) == NamaSaldo)
		{
			'ubah alamat jumlah saldo ke kotak saldo yang dipilih'
			def modifySaldoDipilih = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Balance/h3_4,988'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[1]/div/lib-balance-summary/div/div["+ (i) +"]/div/div/div/div/div[1]/h3", true)
			
			'simpan jumlah saldo sekarang di variabel'
			 saldoNow = Integer.parseInt(WebUI.getText(modifySaldoDipilih).replace(',',''))
			 
			 break;
		}
	}
	'pakai saldo IDR jika lainnya tidak ada'
	if(saldoNow == 0)
	{
		'simpan jumlah saldo sekarang di variabel'
		saldoNow = Integer.parseInt(WebUI.getText(findTestObject('Object Repository/API_KEY/Page_Balance/h3_4,988')).replace(',',''))
	}
	'kembalikan nilai saldo sekarang'
	return saldoNow
}