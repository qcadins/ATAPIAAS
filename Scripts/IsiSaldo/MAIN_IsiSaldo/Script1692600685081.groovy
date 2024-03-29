import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import java.sql.Connection
import org.openqa.selenium.JavascriptExecutor as JavascriptExecutor
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writetoexcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Isi Saldo'
int countColumnEdit = findTestData(ExcelPathSaldoAPI).columnNumbers, firstRun = 0

Connection conn

if (GlobalVariable.SettingEnvi == 'Production') {
	'deklarasi koneksi ke Database eendigo_dev'
	conn = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_public'()
} else if (GlobalVariable.SettingEnvi == 'Trial') {
	'deklarasi koneksi ke Database eendigo_dev_uat'
	conn = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_devUat'()
}

for (GlobalVariable.NumOfColumn = 2; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++) {
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).length() == 0) {
		break
	} else if (findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
		'set penanda error menjadi 0'
		GlobalVariable.FlagFailed = 0
		
		'angka untuk menghitung data mandatory yang tidak terpenuhi'
		int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('Mandatory Complete')))
		
		int saldobefore, saldoafter, jumlahTopUp, topupSaldoCorrectTenant
		
		String noTrxfromUI, noTrxfromDB, noTrxOtherTenant
		
		if (firstRun == 0) {
			'panggil fungsi login'
			WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'IsiSaldo', ('SheetName') : sheet,
				('Path') : ExcelPathSaldoAPI, ('Username') : '$Username ', ('Password') : '$Password Login',], FailureHandling.STOP_ON_FAILURE)
			
			'klik tombol masuk'
			WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/button_Masuk'))
			
			'ambil WebDriver untuk menjalankan js executor'
			WebDriver driver = DriverFactory.webDriver
			
			'siapkan js executor'
			JavascriptExecutor js = (driver as JavascriptExecutor)
			
			'buka tab baru'
			js.executeScript('window.open();')
			
			'ganti fokus robot ke tab baru'
			WebUI.switchToWindowIndex(1)
			
			'arahkan tab baru ke url eendigo beta dan lakukan login'
			navigatetoeendigoBeta()
		}
		
		'ganti fokus robot ke tab baru'
		WebUI.switchToWindowIndex(1)
		
		'flag apakah topup masuk ke tenant yang benar'
		topupSaldoCorrectTenant = 1
		
		'ambil kode tenant di DB'
		String tenantcode = CustomKeywords.'ocrtesting.GetParameterfromDB.getTenantCodefromDB'(conn, findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('$Username Login')))
		
		WebUI.refresh()
		
		'ambil saldo sebelum isi ulang'
		saldobefore = getSaldoforTransaction(findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('$Tipe Saldo')))
		
		'ubah ke tab billing system'
		WebUI.switchToWindowIndex(0)
		
		if (firstRun == 0) {
			'ambil nama tenant dari DB'
			ArrayList namatenantDB = CustomKeywords.'apikey.CheckSaldoAPI.getTenantName'(conn)
			
			'ambil nama vendor dari DB'
			ArrayList namaVendorDB = CustomKeywords.'apikey.CheckSaldoAPI.getVendorName'(conn, tenantcode)
			
			'nama-nama tipe saldo yang sedang aktif dari DB'
			ArrayList namaTipefromDB = CustomKeywords.'apikey.CheckSaldoAPI.getNamaTipeSaldo'(conn, tenantcode)
			
			'panggil fungsi check jumlah tenant di DB dan UI'
			checkDDL(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tenant'), namatenantDB, 'DDL Tenant')
			
			'input nama tenant yang akan digunakan'
			inputDDLExact('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tenant', 'PT REAL ESTATE')
			
			'check jumlah vendor di DB dan UI'
			checkDDL(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input vendor'), namaVendorDB, 'DDL Vendor')
			
			'input nama vendor yang akan digunakan'
			inputDDLExact('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input vendor',
				'ADINS')

			'panggil fungsi cek banyak tipe saldo yang bisa diisi ulang'
			checkDDL(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tipe saldo'), namaTipefromDB, 'DDL Tipe saldo')
	
			'input nama saldo yang akan diisi ulang'
			inputDDLExact('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tipe saldo',
				'IDR')
			
			WebUI.refresh()
			
			firstRun = 1
		}
				
		'input nama tenant yang akan digunakan'
		inputDDLExact('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tenant',
			findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('$Tenant')))

		'input nama vendor yang akan digunakan'
		inputDDLExact('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input vendor',
			findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('$Vendor')))

		'input nama saldo yang akan diisi ulang'
		inputDDLExact('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tipe saldo', 
			findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('$Tipe Saldo')))

		'input jumlah saldo yang akan ditambahkan'
		WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input_Tambah Saldo_qty'),
			findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('$Tambah Saldo')))
		
		'input nomor tagihan untuk proses isi ulang saldo'
		WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input_Nomor Tagihan_refNo'),
			findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('$Nomor tagihan')))
		
		'input notes/catatan untuk proses isi ulang saldo'
		WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input_Catatan_notes'),
			findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('$Catatan')))
		
		'input tanggal isi ulang saldo'
		WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input_Tanggal Pembelian_trxDate'),
			findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('$Tanggal Pembelian (YYYY-MM-DD)')))
		
		'klik di luar textbox agar memunculkan tombol lanjut'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/containerForm'))
		
		'verifikasi button tidak di disable'
		if (WebUI.verifyElementHasAttribute(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/button_Lanjut'),
					'disabled', GlobalVariable.Timeout, FailureHandling.OPTIONAL) && isMandatoryComplete != 0) {
			GlobalVariable.FlagFailed = 1
			'tulis kondisi gagal'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet,
				GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed, 
					findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';' +
						GlobalVariable.FailedReasonMandatory)
			
			continue
		} else {
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
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusWarning, (findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
					GlobalVariable.FailedReasonUnknown)
		}
		
		'ambil jumlah saldo pada menu trial'
		saldoafter = getSaldoforTransaction(findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('$Tipe Saldo')))
		
		'filter saldo sesuai kebutuhan user'
		filterSaldo()
		
		'scroll ke bawah halaman'
		WebUI.scrollToElement(findTestObject('Object Repository/API_KEY/Page_Balance/skiptoLast_page'), GlobalVariable.Timeout)
		
		'cek apakah button skip enable atau disable'
		if (WebUI.verifyElementVisible(findTestObject('Object Repository/API_KEY/Page_Balance/skiptoLast_page'), FailureHandling.OPTIONAL)) {
			'klik button skip to last page'
			WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/skiptoLast_page'))
		}
		
		'ambil nomor transaksi terakhir di tabel'
		noTrxfromUI = tableTrxNumber()
		
		'jika perlu cek ke DB'
		if (GlobalVariable.KondisiCekDB == 'Yes') {
			'ambil nomor transaksi terbaru dari DB'
			noTrxfromDB = CustomKeywords.'apikey.CheckSaldoAPI.getLatestMutation'(conn, tenantcode)
			
			'ambil nomor transaksi terbaru tenant lain'
			noTrxOtherTenant = CustomKeywords.'apikey.CheckSaldoAPI.getLatestMutationOtherTenant'(conn, tenantcode)
			
			'call test case store db'
			 WebUI.callTestCase(findTestCase('IsiSaldo/IsiSaldoStoreDB'), [('ExcelPathSaldoAPI') : 'APIAAS/DataSaldoAPIKEY', ('tenant') : tenantcode, ('autoIsiSaldo') : '', ('tipeSaldo') : '', ('sheet') : sheet],
				 FailureHandling.CONTINUE_ON_FAILURE)
			
			'cek apakah transaksi tercatat, memastikan tenant lain tidak memiliki transaksi yang sama'
			if (noTrxfromDB != noTrxfromUI || noTrxfromDB == noTrxOtherTenant) {
				'topup dianggap gagal'
				topupSaldoCorrectTenant = 0
			} else {
				'simpan data flag error dari function verifytable'
				int isError = verifyTableContent(conn, tenantcode)
				
				'jika ada konten pada tabel yang tidak sesuai dengan DB'
				if (isError == 0) {
					'topup dianggap gagal'
					topupSaldoCorrectTenant = 0
				}
			}
		}
		
		'ambil jumlah topup yang diinput oleh user dari excel'
		jumlahTopUp = Integer.parseInt(findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('$Tambah Saldo')))
		
		'saldo sekarang harus sama dengan saldo sebelumnya ditambah jumlah topup'
		if (saldobefore + jumlahTopUp == saldoafter && topupSaldoCorrectTenant == 1) {
			'tulis status sukses pada excel'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0,
				GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
		} else {
			GlobalVariable.FlagFailed = 1
			
			'tulis kondisi gagal'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed, findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';' +
			GlobalVariable.FailedReasonTopUpFailed)
		}
	}	
}

'tutup browser'
WebUI.closeBrowser()

def checkDDL(TestObject objectDDL, ArrayList<String> listDB, String reason) {
	'declare array untuk menampung ddl'
	ArrayList<String> list = []

	'click untuk memunculkan ddl'
	WebUI.click(objectDDL)
	
	'get id ddl'
	id = WebUI.getAttribute(findTestObject('Object Repository/Top Up/ddlClass'), 'id', FailureHandling.CONTINUE_ON_FAILURE)
	
	'get row'
	variable = DriverFactory.webDriver.findElements(By.cssSelector(('#' + id) + '> div > div:nth-child(2) div'))
	
	if (variable.size() < 10) {
		'looping untuk get ddl kedalam array'
		for (i = 1; i < variable.size(); i++) {
			'modify object DDL'
			modifyObjectDDL = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/modifyobject'), 'xpath', 'equals', ((('//*[@id=\'' +
				id) + '-') + i) + '\']', true)
	
			'add ddl ke array'
			list.add(WebUI.getText(modifyObjectDDL))
		}
		
		'verify ddl ui = db'
		checkVerifyEqualOrMatch(listDB.containsAll(list), reason)
	
		'verify jumlah ddl ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyEqual(list.size(), listDB.size(), FailureHandling.CONTINUE_ON_FAILURE), ' Jumlah ' + reason)
	} else {
		'verify jumlah ddl ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyEqual(variable.size() - 1, listDB.size(), FailureHandling.CONTINUE_ON_FAILURE), ' Jumlah ' + reason)
	}
		
	'Input enter untuk tutup ddl'
	WebUI.sendKeys(objectDDL, Keys.chord(Keys.ENTER))
}

def navigatetoeendigoBeta() {
	'buka website APIAAS SIT, data diambil dari TestData Login'
	WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 2))

	'input data email'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_username'),
		findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('$Username Login')))
	
	'input password'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_password'),
		findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('$Password Login')))
	
	'cek perlukah tunggu agar recaptcha selesai solving'
	if ((findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('CaptchaEnabled')) == 'Yes' ||
		findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('CaptchaEnabled')) == '')) {
		WebUI.delay(1)
	
		String idObject = WebUI.getAttribute(findTestObject('RegisterLogin/Page_Login - eendigo Platform/check_Recaptcha'), 'id', FailureHandling.STOP_ON_FAILURE)
		
		modifyObjectCaptcha = WebUI.modifyObjectProperty(findTestObject('RegisterLogin/Page_Login - eendigo Platform/check_Recaptcha'), 'xpath', 'equals',
			'//*[@id="' + idObject + '"]/div/div[2]', true)
		
		WebUI.waitForElementAttributeValue(modifyObjectCaptcha, 'class', 'antigate_solver recaptcha solved', 120, FailureHandling.OPTIONAL)
	
		WebUI.delay(1)
	//
	//	WebUI.waitForElementAttributeValue(findTestObject('RegisterLogin/Page_Login - eendigo Platform/check_Recaptcha'), 'aria-checked', 'true', 60, FailureHandling.OPTIONAL)
	}
	
	'klik pada button login'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))
	
	'jika ada pilihan role'
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/Change Password/Page_Login - eendigo Platform/Admin Client_3'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		'cari element dengan nama role'
		elementRole = DriverFactory.webDriver.findElements(By.cssSelector('body > ngb-modal-window > div > div > app-multi-role > div > div.row > div > table tr'))
		
		'lakukan loop untuk cari nama role yang ditentukan'
		for (int i = 1; i <= elementRole.size() - 1; i++) {
			'cari nama role yag sesuai di opsi role'
			modifyRole = WebUI.modifyObjectProperty(findTestObject('Object Repository/Change Password/modifyobject'), 'xpath', 'equals', '/html/body/ngb-modal-window/div/div/app-multi-role/div/div[2]/div/table/tr[' + i + 1 + ']/td[1]', true)
	
			'jika nama object sesuai dengan nama role'
			if (findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('Pilih Role')).equalsIgnoreCase(
				WebUI.getAttribute(modifyRole, 'value', FailureHandling.STOP_ON_FAILURE))) {
				'ubah alamat xpath ke role yang dipilih'
				modifyRole = WebUI.modifyObjectProperty(findTestObject('Object Repository/Change Password/modifyobject'), 'xpath', 'equals', '/html/body/ngb-modal-window/div/div/app-multi-role/div/div[2]/div/table/tr[' + i + 1 + ']/td[2]/a', true)
			
				'klik role yang dipilih'
				WebUI.click(findTestObject('Object Repository/Change Password/modifyobject'))
				
				break
			}
		}
	}
	
	'cek apakah muncul error gagal login'
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/ErrorMsg')
		, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		GlobalVariable.FlagFailed = 1
	
		'tulis adanya error pada sistem web'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
				GlobalVariable.FailedReasonLoginIssue)
	}
	
	if (GlobalVariable.SettingEnvi == 'Production' && WebUI.verifyElementPresent(findTestObject('Object Repository/Saldo/Page_Balance/button_Production'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		'click pada production'
		WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/button_Production'))
	}
}

'ambil saldo sesuai testing yang dilakukan'
def getSaldoforTransaction(String namaSaldo) {
	'deklarasi jumlah saldo sekarang'
	int saldoNow
	
	'cari element dengan nama saldo'
	elementNamaSaldo = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance-prod > div.row.match-height > div > lib-balance-summary > div > div'))
	
	'lakukan loop untuk cari nama saldo yang ditentukan'
	for (int i = 1; i <= elementNamaSaldo.size(); i++) {
		'cari nama saldo yang sesuai di list saldo'
		modifyNamaSaldo = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Balance/span_OCR KK'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[1]/div/lib-balance-summary/div/div[' + i + ']/div/div/div/div/div[1]/span', true)

		'jika nama object sesuai dengan nama saldo'
		if (WebUI.getText(modifyNamaSaldo).toUpperCase() == namaSaldo.toUpperCase()) {
			'ubah alamat jumlah saldo ke kotak saldo yang dipilih'
			modifySaldoDipilih = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Balance/h3_45,649'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[1]/div/lib-balance-summary/div/div[' + i + ']/div/div/div/div/div[1]/h3', true)
			
			'simpan jumlah saldo sekarang di variabel'
			 saldoNow = Integer.parseInt(WebUI.getText(modifySaldoDipilih).replace(',', ''))
			 
			 break
		}
	}
	'pakai saldo IDR jika lainnya tidak ada'
	if (saldoNow == 0) {
		modifySaldoDipilih = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Balance/h3_45,649'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[1]/div/lib-balance-summary/div/div[' + elementNamaSaldo.size() + ']/div/div/div/div/div[1]/h3', true)
		
		'simpan jumlah saldo sekarang di variabel'
		 saldoNow = Integer.parseInt(WebUI.getText(modifySaldoDipilih).replace(',', ''))
	}
	'kembalikan nilai saldo sekarang'
	saldoNow
}

'fungsi untuk filter saldo berdasarkan input user'
def filterSaldo() {
	'tunggu webpage load'
	WebUI.delay(4)
	
	'isi field input tipe saldo'
	inputDDLExact('Object Repository/API_KEY/Page_Balance/inputtipesaldo', 
		findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('$SearchTipeSaldo')))

	'isi field tipe transaksi'
	inputDDLExact('Object Repository/API_KEY/Page_Balance/inputtipetranc', 
		findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('$SearchTipeTransaksi')))
	
	'klik pada button cari'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/button_Cari'))
}

'ambil no. transaksi pada tabel'
def verifyTableContent(Connection conn, String tenant) {
	'ambil alamat trxnumber'
	variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance-prod > div.ng-star-inserted > app-msx-paging-v2 > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))
	
	'banyaknya row table'
	int lastIndex = variable.size()
	
	'check status semua match data'
	ArrayList arrayMatch = []
		
	'kembalikan nomor transaksi'
	int arrayIndex = 0
	
	'flag jika ada error pada verifikasi'
	int flagError = 1
	
	'ambil data table dari db'
	ArrayList result = CustomKeywords.'apikey.CheckSaldoAPI.getTrialTableContent'(conn, tenant)
	
	'modifikasi object tanggal transaksi'
	modifytglTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/TglTrx'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[1]/div/p', true)
		
	'modifikasi object kantor'
	modifyKantor = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/KantorLocation'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[2]/div/p', true)

	'modifikasi object tipe transaksi'
	modifytipeTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/TipeTrx'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[3]/div/p', true)

	'modifikasi object sumber transaksi'
	modifysumberTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/sumberTrx'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[4]/div/p', true)

	'modifikasi object tenant transaksi'
	modifytenantTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/TenantTrx'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[5]/div/p', true)

	'modifikasi object trxnumber'
	modifytrxnumber = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/TrxNumber'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[6]/div/p', true)
		
	'modifikasi object reference transaksi'
	modifyrefTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/refTrx'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[7]/div/p', true)
	
	'modifikasi object quantity transaksi'
	modifyqtyTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/modifyobject'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[8]/div', true)

	'modifikasi object hasil proses transaksi'
	modifyprocTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/procTrx'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[10]/div/p', true)

	'modifikasi object hasil proses transaksi'
	modifycatatanTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/catatan'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[11]/div/p', true)
	
	'verify tanggal transaksi ui = db'
	checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifytglTrx), result[arrayIndex++].replace('.0', ''), false, FailureHandling.CONTINUE_ON_FAILURE)), 'Data Tanggal Transaksi')
	
	'verify kantor ui = db'
	checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyKantor), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE)), 'Data Kolom Kantor')
	
	'verify tipe transaksi ui = db'
	checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifytipeTrx), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE)), 'Data Kolom Tipe Transaksi')
	
	'verify sumber transaksi ui = db'
	checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifysumberTrx), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE)), 'Data Kolom Sumber Transaksi')
	
	'verify tenant transaksi ui = db'
	checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifytenantTrx), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE)), 'Data Kolom Tenant')
	
	'verify nomor transaksi ui = db'
	checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifytrxnumber), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE)), 'Data Kolom Nomor Transaksi')
	
	'verify reference number transaksi ui = db'
	checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyrefTrx), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE)), 'Data Kolom Ref Number')
	
	'verify quantity transaksi ui = db'
	checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyqtyTrx) , result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE)), 'Data Kolom Kuantitas')
	
	'verify hasil proses transaksi ui = db'
	checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyprocTrx), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE)), 'Data Kolom Hasil Proses')
	
	'verify catatan transaksi ui = db'
	checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifycatatanTrx), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE)), 'Data Kolom Catatan Transaksi')
	
	'jika ada verifikasi yang gagal'
	if (arrayMatch.contains(false)) {
		'kembalikan flag error'
		flagError = 0
	}
	
	flagError
}

'fungsi untuk melakukan pengecekan '
def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
	if ((isMatch == false)) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet,
			GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed, (findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
				GlobalVariable.FailedReasonVerifyEqualorMatch + ' ' + reason)

		GlobalVariable.FlagFailed = 1
	}
}

'ambil no. transaksi pada tabel'
def tableTrxNumber() {
	'ambil alamat trxnumber'
	variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance-prod > div.ng-star-inserted > app-msx-paging-v2 > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))
	
	'banyaknya row table'
	int lastIndex = variable.size()
		
	String noTrx = ''
	
	'modifikasi alamat object trxnumber'
	modifytrxnumber = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/TrxNumber'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[6]/div/p', true)
							
	if (WebUI.verifyElementPresent(modifytrxnumber, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		'simpan nomor transaction number ke string'
		noTrx = WebUI.getText(modifytrxnumber)
	}
	
	'kembalikan nomor transaksi'
	noTrx
}

def rowExcel(String cellValue) {
	CustomKeywords.'writetoexcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def inputDDLExact(String locationObject, String input) {
	'Input value status'
	WebUI.setText(findTestObject(locationObject), input)

	if (input != '') {
		WebUI.click(findTestObject(locationObject))

		'get token unik'
		tokenUnique = WebUI.getAttribute(findTestObject(locationObject), 'aria-owns')

		'modify object label Value'
		modifyObjectGetDDLFromToken = WebUI.modifyObjectProperty(findTestObject('Saldo/Page_Balance/modifybuttonpage'), 'xpath',
			'equals', ('//*[@id="' + tokenUnique) + '"]/div/div[2]', true)

		DDLFromToken = WebUI.getText(modifyObjectGetDDLFromToken)

		for (i = 0; i < DDLFromToken.split('\n', -1).size(); i++) {
			if ((DDLFromToken.split('\n', -1)[i]).toString().toLowerCase() == input.toString().toLowerCase()) {
				modifyObjectClicked = WebUI.modifyObjectProperty(findTestObject('Saldo/Page_Balance/modifybuttonpage'), 'xpath',
					'equals', ((('//*[@id="' + tokenUnique) + '"]/div/div[2]/div[') + (i + 1)) + ']', true)

				WebUI.click(modifyObjectClicked)

				break
			}
		}
	} else {
		WebUI.click(findTestObject(locationObject))

		WebUI.sendKeys(findTestObject(locationObject), Keys.chord(Keys.ENTER))
	}
}