import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

import java.sql.Connection
import java.sql.Driver
import org.openqa.selenium.JavascriptExecutor as JavascriptExecutor
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys

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

'panggil fungsi login'
WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'IsiSaldo', ('SheetName') : 'IsiSaldo', 
	('Path') : ExcelPathSaldoAPI], FailureHandling.STOP_ON_FAILURE)

'ambil index tab yang sedang dibuka di chrome'
int currentTab = WebUI.getWindowIndex()

'ambil WebDriver untuk menjalankan js executor'
WebDriver driver = DriverFactory.getWebDriver()

'siapkan js executor'
JavascriptExecutor js = ((driver) as JavascriptExecutor)

'buka tab baru'
js.executeScript('window.open();')

for (GlobalVariable.NumOfColumn = 2; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++) {
	
	'set penanda error menjadi 0'
	GlobalVariable.FlagFailed = 0
	
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 1).length() == 0) {
		
		break
	}
	else if (findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 1).equalsIgnoreCase('Unexecuted')) {
		
		'angka untuk menghitung data mandatory yang tidak terpenuhi'
		int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 5))
		
		int Saldobefore, Saldoafter, JumlahTopUp, TopupSaldoCorrectTenant
		
		String noTrxfromUI, noTrxfromDB, noTrxOtherTenant
		
		'ganti fokus robot ke tab baru'
		WebUI.switchToWindowIndex(1)
		
		'arahkan tab baru ke url eendigo beta dan lakukan login'
		navigatetoeendigoBeta()
		
		'ambil kode tenant di DB'
		String tenantcode = CustomKeywords.'ocrTesting.GetParameterfromDB.getTenantCodefromDB'(conn,
			findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 11))
		
		'flag apakah topup masuk ke tenant yang benar'
		TopupSaldoCorrectTenant = 1
		
		'ganti fokus robot ke tab baru'
		WebUI.switchToWindowIndex(currentTab + 1)
		
		'arahkan tab baru ke url eendigo beta dan lakukan login'
		navigatetoeendigoBeta()
		
		'ambil kode tenant di DB'
		String tenantcode = CustomKeywords.'ocrTesting.GetParameterfromDB.getTenantCodefromDB'(conn,
			findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 11))
		
		'ambil saldo sebelum isi ulang'
		Saldobefore = getSaldoforTransaction(findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 15))
		
		'ubah ke tab billing system'
		WebUI.switchToWindowIndex(0)
		
		'klik pada input tenant'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tenant'))
		
		'panggil fungsi check jumlah tenant di DB dan UI'
		checkTenantcount(conn)
		
		'input nama tenant yang akan digunakan'
		WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tenant'), 
			findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 13))
		
		'pencet enter pada textbox'
		WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tenant'), 
			Keys.chord(Keys.ENTER))
		
		'klik pada input vendor'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input vendor'))
		
		'check jumlah vendor di DB dan UI'
		checkVendorcount(conn, tenantcode)
		
		'input nama vendor yang akan digunakan'
		WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input vendor'), 
			findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 14))
		
		'pencet enter pada textbox'
		WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input vendor'), 
			Keys.chord(Keys.ENTER))
		
		'klik pada input tipe saldo'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tipe saldo'))
		
		'panggil fungsi cek banyak tipe saldo yang bisa diisi ulang'
		checkTipeSaldocount(conn, tenantcode)
		
		'input nama saldo yang akan diisi ulang'
		WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tipe saldo'), 
			findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 15))
		
		'pencet enter pada textbox'
		WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tipe saldo'), 
			Keys.chord(Keys.ENTER))
		
		'input jumlah saldo yang akan ditambahkan'
		WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input_Tambah Saldo_qty'),
			findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 16))
		
		'input nomor tagihan untuk proses isi ulang saldo'
		WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input_Nomor Tagihan_refNo'),
			findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 17))
		
		'input notes/catatan untuk proses isi ulang saldo'
		WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input_Catatan_notes'),
			findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 18))
		
		'input tanggal isi ulang saldo'
		WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input_Tanggal Pembelian_trxDate'),
			findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 19))
		
		'klik di luar textbox agar memunculkan tombol lanjut'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/containerForm'))
		
		'verifikasi button tidak di disable'
		if (WebUI.verifyElementHasAttribute(
			findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/button_Lanjut'), 
			'disabled', GlobalVariable.Timeout, FailureHandling.OPTIONAL) && isMandatoryComplete != 0) {
		
			GlobalVariable.FlagFailed = 1
			'tulis kondisi gagal'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('IsiSaldo', 
				GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed, 
					findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 2) + ';' +
						GlobalVariable.FailedReasonMandatory)
			
			continue;
		}
		else {
			
			'klik pada tombol lanjut'
			WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/button_Lanjut'))
		}
			
		'klik pada tombol proses isi ulang  saldo'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/button_Ya, proses'))
		
		'ubah fokus ke tab eendigo beta'
		WebUI.switchToWindowIndex(1)
		
		'refresh laman web untuk ambil saldo baru'
		WebUI.refresh()
		
		'cek apakah muncul error unknown setelah login'
		if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'),
			GlobalVariable.Timeout, FailureHandling.OPTIONAL) == false) {
			
			GlobalVariable.FlagFailed = 1
			
			'tulis adanya error pada sistem web'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('IsiSaldo', GlobalVariable.NumOfColumn,
				GlobalVariable.StatusWarning, (findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
					GlobalVariable.FailedReasonUnknown)
		}
		
		'ambil jumlah saldo pada menu trial'
		Saldoafter = getSaldoforTransaction(findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 15))
		
		'filter saldo sesuai kebutuhan user'
		filterSaldo()
		
		'scroll ke bawah halaman'
		WebUI.scrollToElement(findTestObject('Object Repository/API_KEY/Page_Balance/skiptoLast_page'), GlobalVariable.Timeout)
		
		'cek apakah button skip enable atau disable'
		if(WebUI.verifyElementVisible(findTestObject('Object Repository/API_KEY/Page_Balance/skiptoLast_page'), FailureHandling.OPTIONAL)){
			
			'klik button skip to last page'
			WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/skiptoLast_page'))
		}
		
		'ambil nomor transaksi terakhir di tabel'
		noTrxfromUI = getTrxNumber()
		
		'jika perlu cek ke DB'
		if (GlobalVariable.KondisiCekDB == 'Yes') {
			
			'ambil nomor transaksi terbaru dari DB'
			noTrxfromDB = CustomKeywords.'apikey.CheckSaldoAPI.getLatestMutation'(conn, tenantcode)
			
			'ambil nomor transaksi terbaru tenant lain'
			noTrxOtherTenant = CustomKeywords.'apikey.CheckSaldoAPI.getLatestMutationOtherTenant'(conn, tenantcode)
			
			'call test case store db'
			 WebUI.callTestCase(findTestCase('IsiSaldo/IsiSaldoStoreDB'), [('ExcelPathSaldoAPI') : 'APIAAS/DataSaldoAPIKEY', ('tenant') : tenantcode, ('autoIsiSaldo') : '', ('tipeSaldo') : ''], ('sheet') : 'IsiSaldo',
				 FailureHandling.CONTINUE_ON_FAILURE)
			
			'cek apakah transaksi tercatat, memastikan tenant lain tidak memiliki transaksi yang sama'
			if (noTrxfromDB != noTrxfromUI || noTrxfromDB == noTrxOtherTenant) {
				
				'topup dianggap gagal'
				TopupSaldoCorrectTenant = 0
			}
			else {
				
				'jika ada konten pada tabel yang tidak sesuai dengan DB'
				if (verifyTableContent(conn, tenantcode) == 0) {
					
					'topup dianggap gagal'
					TopupSaldoCorrectTenant = 0
				}
			}
		}
		
		'ambil jumlah topup yang diinput oleh user dari excel'
		JumlahTopUp = Integer.parseInt(findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 16))
		
		'saldo sekarang harus sama dengan saldo sebelumnya ditambah jumlah topup'
		if (Saldobefore + JumlahTopUp == Saldoafter && GlobalVariable.FlagFailed == 0 && TopupSaldoCorrectTenant == 1) {
			
			'tulis status sukses pada excel'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('IsiSaldo', GlobalVariable.NumOfColumn, 
				GlobalVariable.StatusSuccess, findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 2) + ';' +
					GlobalVariable.SuccessReason)
					
		}
		else {
			
			GlobalVariable.FlagFailed = 1
			'tulis kondisi gagal'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('IsiSaldo', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed, findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 2) + ';' +
			GlobalVariable.FailedReasonTopUpFailed)
		}
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
	ArrayList<String> namatenantDB = CustomKeywords.'apikey.CheckSaldoAPI.getTenantName'(connection)
	
	'nama-nama tipe saldo sedang aktif dari UI'
	ArrayList<String> namatenantUI = []
	
	'ambil hitungan tenant dari DB'
	int countDB = namatenantDB.size()
	
	'jika hitungan di UI dan DB sesuai'
	if (countWeb == countDB) {
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
	else if (countWeb != countDB || isTenantMatch == 0) {
		
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and reason topup failed'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('IsiSaldo', GlobalVariable.NumOfColumn,
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
	ArrayList<String> namaVendorDB = CustomKeywords.'apikey.CheckSaldoAPI.getVendorName'(connection, tenantcode)
	
	'nama-nama tipe saldo sedang aktif dari UI'
	ArrayList<String> namaVendorUI = []
	
	'hitung banyak data didalam array DB'
	int countDB = namaVendorDB.size()
	
	'jika hitungan di UI dan DB sesuai'
	if (countWeb == countDB) {
		
		for (int i=1; i<=countWeb; i++) {
			
			'ambil object dari ddl'
			def modifyNamaVendor = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/VendorList'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-topup-balance/div[2]/div/div/div/div/form/div[1]/div[2]/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div["+(i+1)+"]/span", true)
				
			'tambahkan nama tipe saldo ke array'
			String data = WebUI.getText(modifyNamaVendor)
			namaVendorUI.add(data)
		}
		
		'cek setiap data di UI dengan data di DB sebagai pembanding'
		for (String tipe : namaVendorDB) {
			
			'jika ada data yang tidak terdapat pada arraylist yang lain'
			if (!namaVendorUI.contains(tipe)) {
				
				'ada data yang tidak match'
				isVendorFound = 0;
				'berhentikan loop'
				break;
			}
			'kondisi ini bisa ditemui jika data match'
			isVendorFound = 1
		}
			
	}
	else if (isVendorFound == 0 || countWeb != countDB) {
		
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('IsiSaldo', GlobalVariable.NumOfColumn,
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
	ArrayList<String> namaTipefromDB = CustomKeywords.'apikey.CheckSaldoAPI.getNamaTipeSaldo'(connection, tenantcode)
	
	'nama-nama tipe saldo sedang aktif dari UI'
	ArrayList<String> namaTipefromUI = []
	
	'ambil ukuran array dari db'
	int countDB = namaTipefromDB.size()
	
	'jika hitungan di UI dan DB sesuai lakukan pengecekan'
	if (countWeb == countDB) {
		
		'loop untuk tambah data ke array from UI'
		for (int i=1; i<=countWeb; i++) {
			
			'ambil object dari ddl'
			def modifyNamaTipe = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/TipeSaldoList'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-topup-balance/div[2]/div/div/div/div/form/div[1]/div[3]/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div["+(i+1)+"]/span", true)
			
			'tambahkan nama tipe saldo ke array'
			String data = WebUI.getText(modifyNamaTipe)
			namaTipefromUI.add(data)
		}
	
		'cek setiap data di UI dengan data di DB sebagai pembanding'
		for (String tipe : namaTipefromDB) {
			
			'jika ada data yang tidak terdapat pada arraylist yang lain'
			if (!namaTipefromUI.contains(tipe)) {
				
				'ada data yang tidak match'
				isDataMatch = 0;
				'berhentikan loop'
				break;
			}
			'kondisi ini bisa ditemui jika data match'
			isDataMatch = 1
		}
	}
	else if (countWeb != countDB || isDataMatch == 0) {
		
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('IsiSaldo', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonVerifyEqualorMatch)
	}
}

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
		
		'tulis adanya error pada sistem web'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('IsiSaldo', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusWarning, (findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
				GlobalVariable.FailedReasonUnknown)
	}
}

'ambil saldo sesuai testing yang dilakukan'
def getSaldoforTransaction(String NamaSaldo) {
	
	'deklarasi jumlah saldo sekarang'
	int saldoNow
	
	'cari element dengan nama saldo'
	def elementNamaSaldo = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance-prod > div.row.match-height > div > lib-balance-summary > div > div'))
	
	'lakukan loop untuk cari nama saldo yang ditentukan'
	for (int i=1; i<=elementNamaSaldo.size(); i++) {
		
		'cari nama saldo yang sesuai di list saldo'
		def modifyNamaSaldo = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Balance/span_OCR KK'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[1]/div/lib-balance-summary/div/div["+ (i) +"]/div/div/div/div/div[1]/span", true)

		'jika nama object sesuai dengan nama saldo'
		if (WebUI.getText(modifyNamaSaldo) == NamaSaldo) {
			
			'ubah alamat jumlah saldo ke kotak saldo yang dipilih'
			def modifySaldoDipilih = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Balance/kotakSaldo'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[1]/div/lib-balance-summary/div/div["+ (i) +"]/div/div/div/div/div[1]/h3", true)
			
			'simpan jumlah saldo sekarang di variabel'
			 saldoNow = Integer.parseInt(WebUI.getText(modifySaldoDipilih).replace(',',''))
			 
			 break;
		}
	}
	'pakai saldo IDR jika lainnya tidak ada'
	if (saldoNow == 0) {
		
		'simpan jumlah saldo sekarang di variabel'
		saldoNow = Integer.parseInt(WebUI.getText(findTestObject('Object Repository/API_KEY/Page_Balance/kotakSaldo')).replace(',',''))
	}
	'kembalikan nilai saldo sekarang'
	return saldoNow
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
	def modifyqtyTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Balance/quantityTrx'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (lastIndex) +"]/datatable-body-row/div[2]/datatable-body-cell[8]/div", true)

	'modifikasi object hasil proses transaksi'
	def modifyprocTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/procTrx'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (lastIndex) +"]/datatable-body-row/div[2]/datatable-body-cell[10]/div/p", true)

	'modifikasi object hasil proses transaksi'
	def modifycatatanTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/catatan'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (lastIndex) +"]/datatable-body-row/div[2]/datatable-body-cell[11]/div/p", true)

	'ambil data table dari db'
	ArrayList result = CustomKeywords.'apikey.CheckSaldoAPI.getTrialTableContent'(connection, tenant)
	
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
	if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
		
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('IsiSaldo', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
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
		
	String no_Trx = ''
	
	'modifikasi alamat object trxnumber'
	def modifytrxnumber = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/TrxNumber'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (lastIndex) +"]/datatable-body-row/div[2]/datatable-body-cell[6]/div/p", true)
							
	if(WebUI.verifyElementPresent(modifytrxnumber, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		'simpan nomor transaction number ke string'
		no_Trx = WebUI.getText(modifytrxnumber)
	}
	
	'kembalikan nomor transaksi'
	return no_Trx
}