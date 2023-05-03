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
int CountColumnEdit = findTestData(ExcelPathSaldoAPI).getColumnNumbers()

'deklarasi variabel untuk konek ke Database eendigo_dev'
def conn = CustomKeywords.'dbConnection.connect.connectDBAPIAAS_public'()

//'deklarasi koneksi ke Database adins_apiaas_uat'
//def connProd = CustomKeywords.'dbConnection.connect.connectDBAPIAAS_uatProduction'()

'deklarasi koneksi ke Database adins_apiaas_uat'
def conndevUAT = CustomKeywords.'dbConnection.connect.connectDBAPIAAS_devUat'()

'panggil fungsi login'
WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'IsiSaldo'], FailureHandling.STOP_ON_FAILURE)

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

for(GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn < 4; (GlobalVariable.NumOfColumn)++)
{
	'set penanda error menjadi 0'
	GlobalVariable.FlagFailed = 0
	
	'Lihat status TC di excel'
	StatusTC = findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 1)
	
	'jika data di kolom selanjutnya kosong, lanjutkan loop'
	if(StatusTC != 'Unexecuted')
	{
		continue;
	}
	
	'angka untuk menghitung data mandatory yang tidak terpenuhi'
	int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 4))
	
	int Saldobefore, Saldoafter, JumlahTopUp, TopupSaldoCorrectTenant
	
	String no_TrxfromUI, no_TrxfromDB, no_TrxOtherTenant
	
	'flag apakah topup masuk ke tenant yang benar'
	TopupSaldoCorrectTenant = 1
	
	'ambil saldo sebelum isi ulang'
	Saldobefore = getSaldoforTransaction(findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 14))
	
	'ubah ke tab billing system'
	WebUI.switchToWindowIndex(currentTab)
	
	'klik pada input tenant'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tenant'))
	
	'panggil fungsi check jumlah tenant di DB dan UI'
	checkTenantcount(conndevUAT)
	
	'input nama tenant yang akan digunakan'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tenant'), findTestData(
		ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 12))
	
	'pencet enter pada textbox'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tenant'), Keys.chord(
		Keys.ENTER))
	
	'klik pada input vendor'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input vendor'))
	
	'check jumlah vendor di DB dan UI'
	checkVendorcount(conndevUAT, tenantcode)
	
	'input nama vendor yang akan digunakan'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input vendor'), findTestData(
		ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 13))
	
	'pencet enter pada textbox'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input vendor'), Keys.chord(
		Keys.ENTER))
	
	'klik pada input tipe saldo'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tipe saldo'))
	
	'panggil fungsi cek banyak tipe saldo yang bisa diisi ulang'
	checkTipeSaldocount(conndevUAT, tenantcode)
	
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
	
	'verifikasi element bisa diceklis'
	if(WebUI.verifyElementHasAttribute(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/button_Lanjut'), 'disabled', GlobalVariable.Timeout, FailureHandling.OPTIONAL) && isMandatoryComplete != 0)
	{
		GlobalVariable.FlagFailed = 1
		'tulis kondisi gagal'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('IsiSaldo', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed, findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 2) + ';' + 
		GlobalVariable.FailedReasonMandatory)
		
		continue;
	}
	else
	{
		'klik pada tombol lanjut'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/button_Lanjut'))
	}
		
	'klik pada tombol proses isi ulang  saldo'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/button_Ya, proses'))
	
	'ubah fokus ke tab eendigo beta'
	WebUI.switchToWindowIndex(currentTab+1)
	
	'refresh laman web untuk ambil saldo baru'
	WebUI.refresh()
	
	'ambil jumlah saldo pada menu trial'
	Saldoafter = getSaldoforTransaction(findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 14))
	
	'filter saldo sesuai kebutuhan user'
	filterSaldo()
	
	'pindah ke halaman terakhir dari tabel'
	SkiptotheLastPages()
	
	'ambil nomor transaksi terakhir di tabel'
	no_TrxfromUI = getTrxNumber()
	
	'jika perlu cek ke DB'
	if(GlobalVariable.KondisiCekDB == 'Yes')
	{
		'ambil nomor transaksi terbaru dari DB'
		no_TrxfromDB = CustomKeywords.'apikey.checkSaldoAPI.getLatestMutation'(conndevUAT, tenantcode)
		
		'ambil nomor transaksi terbaru tenant lain'
		no_TrxOtherTenant = CustomKeywords.'apikey.checkSaldoAPI.getLatestMutationOtherTenant'(conndevUAT, tenantcode)
		
		'call test case store db'
		 WebUI.callTestCase(findTestCase('IsiSaldo/IsiSaldoStoreDB'), [('tenant') : tenantcode], 
			 FailureHandling.CONTINUE_ON_FAILURE)
		
		'cek apakah transaksi tercatat, memastikan tenant lain tidak memiliki transaksi yang sama'
		if(no_TrxfromDB != no_TrxfromUI || no_TrxfromDB == no_TrxOtherTenant)
		{
			'topup dianggap gagal'
			TopupSaldoCorrectTenant = 0
		}
		else
		{
			'jika ada konten pada tabel yang tidak sesuai dengan DB'
			if(verifyTableContent(conndevUAT, tenantcode) == 0)
			{
				'topup dianggap gagal'
				TopupSaldoCorrectTenant = 0
			}
		}
	}
	
	'ambil jumlah topup yang diinput oleh user dari excel'
	JumlahTopUp = Integer.parseInt(findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 15))
	
	'saldo sekarang harus sama dengan saldo sebelumnya ditambah jumlah topup'
	if(Saldobefore + JumlahTopUp == Saldoafter && GlobalVariable.FlagFailed == 0 && TopupSaldoCorrectTenant == 1)
	{
		'tulis status sukses pada excel'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('IsiSaldo', GlobalVariable.NumOfColumn, GlobalVariable.StatusSuccess, findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 2) + ';' + 
		GlobalVariable.SuccessReason)
				
	}
	else
	{
		GlobalVariable.FlagFailed = 1
		'tulis kondisi gagal'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('IsiSaldo', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed, findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 2) + ';' + 
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
	
	'flag apakah tenant sesuai pada verifikasi'
	int isTenantMatch = 1
	
	'ambil nama vendor dari DB'
	ArrayList<String> namatenantDB = CustomKeywords.'apikey.checkSaldoAPI.getTenantName'(connection)
	
	'nama-nama tipe saldo sedang aktif dari UI'
	ArrayList<String> namatenantUI = new ArrayList<String>()
	
	'ambil hitungan tenant dari DB'
	int countDB = namatenantDB.size()
	
	'jika hitungan di UI dan DB sesuai'
	if(countWeb == countDB) 
	{
//		for(int i=1; i<=countWeb; i++)
//		{
//			'ambil object dari ddl'
//			def modifyNamaTenant = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/TenantList'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-topup-balance/div[2]/div/div/div/div/form/div[1]/div[1]/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div["+(i+1)+"]/span", true)
//				
//			'tambahkan nama tipe saldo ke array'
//			String data = WebUI.getText(modifyNamaTenant)
//			namatenantUI.add(data)
//		}
//			
//		'cek setiap data di UI dengan data di DB sebagai pembanding'
//		for (String tipe : namatenantDB)
//		{
//			'jika ada data yang tidak terdapat pada arraylist yang lain'
//			if (!namatenantUI.contains(tipe))
//			{
//				'ada data yang tidak match'
//				isTenantMatch = 0;
//				'berhentikan loop'
//				break;
//				}
//			'kondisi ini bisa ditemui jika data match'
//			isTenantMatch = 1
//		}
	}
	else if(countWeb != countDB || isTenantMatch == 0)
	{
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and reason topup failed'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('IsiSaldo', GlobalVariable.NumOfColumn,
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
	
	'ambil nama vendor dari DB'
	ArrayList<String> namaVendorDB = CustomKeywords.'apikey.checkSaldoAPI.getVendorName'(connection, tenantcode)
	
	'nama-nama tipe saldo sedang aktif dari UI'
	ArrayList<String> namaVendorUI = new ArrayList<String>()
	
	'hitung banyak data didalam array DB'
	int countDB = namaVendorDB.size()
	
	'jika hitungan di UI dan DB sesuai'
	if(countWeb == countDB)
	{
		for(int i=1; i<=countWeb; i++)
		{
			'ambil object dari ddl'
			def modifyNamaVendor = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/VendorList'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-topup-balance/div[2]/div/div/div/div/form/div[1]/div[2]/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div["+(i+1)+"]/span", true)
				
			'tambahkan nama tipe saldo ke array'
			String data = WebUI.getText(modifyNamaVendor)
			namaVendorUI.add(data)
		}
		
		'cek setiap data di UI dengan data di DB sebagai pembanding'
		for (String tipe : namaVendorDB)
		{
			'jika ada data yang tidak terdapat pada arraylist yang lain'
			if (!namaVendorUI.contains(tipe))
			{
				'ada data yang tidak match'
				isVendorFound = 0;
				'berhentikan loop'
				break;
			}
			'kondisi ini bisa ditemui jika data match'
			isVendorFound = 1
		}
			
	}
	else if(isVendorFound == 0 || countWeb != countDB)
	{
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('IsiSaldo', GlobalVariable.NumOfColumn,
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
	
	'nama-nama tipe saldo yang sedang aktif dari DB'
	ArrayList<String> namaTipefromDB = CustomKeywords.'apikey.checkSaldoAPI.getNamaTipeSaldo'(connection, tenantcode)
	
	'nama-nama tipe saldo sedang aktif dari UI'
	ArrayList<String> namaTipefromUI = new ArrayList<String>()
	
	'ambil ukuran array dari db'
	int countDB = namaTipefromDB.size()
	
	'jika hitungan di UI dan DB sesuai lakukan pengecekan'
	if(countWeb == countDB)
	{
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
	}
	else if(countWeb != countDB || isDataMatch == 0)
	{
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('IsiSaldo', GlobalVariable.NumOfColumn,
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

'fungsi untuk filter saldo berdasarkan input user'
def filterSaldo() {
	'tunggu webpage load'
	WebUI.delay(4)
	
	'isi field input tipe saldo'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'), findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 20))
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'), Keys.chord(Keys.ENTER))
	
	'isi field tipe transaksi'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipetranc'), findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 21))
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipetranc'), Keys.chord(Keys.ENTER))
		
	'klik pada button cari'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/button_Cari'))
}

'fungsi langsung ke laman akhir'
def SkiptotheLastPages() {
	'cari button skip di footer'
	def elementbuttonskip = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance-prod > div.ng-star-inserted > app-msx-paging-v2 > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))
	
	'ambil banyaknya laman footer'
	int lastPage = elementbuttonskip.size()
	
	'ubah path object button skip'
	def modifybuttonskip = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Balance/i_Catatan_datatable-icon-skip'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li["+ (lastPage) +"]", true)

	'cek apakah button enable atau disable'
	if(WebUI.getAttribute(modifybuttonskip, 'class', FailureHandling.CONTINUE_ON_FAILURE) == '')
	{
		'klik button skip to last page'
		WebUI.click(modifybuttonskip)
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
	
	'modifikasi object tanggal transaksi'
	def modifytglTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/TglTrx'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (lastIndex) +"]/datatable-body-row/div[2]/datatable-body-cell[1]/div/p", true)
		
	'modifikasi object kantor'
	def modifyKantor = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/KantorLocation'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (lastIndex) +"]/datatable-body-row/div[2]/datatable-body-cell[2]/div/p", true)

	'modifikasi object tipe transaksi'
	def modifytipeTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/TipeTrx'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (lastIndex) +"]/datatable-body-row/div[2]/datatable-body-cell[3]/div/p", true)

	'modifikasi object sumber transaksi'
	def modifysumberTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/sumberTrx'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (lastIndex) +"]/datatable-body-row/div[2]/datatable-body-cell[4]/div/p", true)

	'modifikasi object tenant transaksi'
	def modifytenantTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/TenantTrx'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (lastIndex) +"]/datatable-body-row/div[2]/datatable-body-cell[5]/div/p", true)

	'modifikasi object trxnumber'
	def modifytrxnumber = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/TrxNumber'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (lastIndex) +"]/datatable-body-row/div[2]/datatable-body-cell[6]/div/p", true)
		
	'modifikasi object reference transaksi'
	def modifyrefTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/refTrx'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (lastIndex) +"]/datatable-body-row/div[2]/datatable-body-cell[7]/div/p", true)

	'modifikasi object quantity transaksi'
	def modifyqtyTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Balance/div_500'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (lastIndex) +"]/datatable-body-row/div[2]/datatable-body-cell[8]/div", true)

	'modifikasi object hasil proses transaksi'
	def modifyprocTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/procTrx'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (lastIndex) +"]/datatable-body-row/div[2]/datatable-body-cell[10]/div/p", true)

	'modifikasi object hasil proses transaksi'
	def modifycatatanTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/catatan'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (lastIndex) +"]/datatable-body-row/div[2]/datatable-body-cell[11]/div/p", true)

	'ambil data table dari db'
	ArrayList<String> result = CustomKeywords.'apikey.checkSaldoAPI.getTrialTableContent'(connection, tenant)
	
	'check status semua match data'
	ArrayList<String> arrayMatch = new ArrayList<String>()
		
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
	if (arrayMatch.contains(false)) 
	{
		'kembalikan flag error'
		flagError = 0
	}
	return flagError
}

'fungsi untuk melakukan pengecekan '
def checkVerifyEqualOrMatch(Boolean isMatch) {
	if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('IsiSaldo', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
			(findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.FailedReasonVerifyEqualorMatch)

		GlobalVariable.FlagFailed = 1
	}
}

'ambil no. transaksi pada tabel'
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