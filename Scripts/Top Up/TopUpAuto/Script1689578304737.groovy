import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.By as By

'mencari directory excel'
GlobalVariable.DataFilePath = CustomKeywords.'writetoexcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'deklarasi koneksi ke Database adins_apiaas_uat'
Connection conndev = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_esign'()

'deklarasi koneksi ke database eendigo_dev_uat'
Connection conndevUAT = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_devUat'()

'panggil fungsi login'
WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'IsiSaldoAuto', ('SheetName') : sheet, ('Path') : ExcelPath],
	FailureHandling.STOP_ON_FAILURE)

'call setting balance type function'
settingBalanceType()

'arahkan ke web SIT APIAAS'
WebUI.navigateToUrl(findTestData(ExcelPath).getValue(1, 2))

'input data email'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_username'),
	findTestData(ExcelPath).getValue(2, 27))

'input password'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_password'),
	findTestData(ExcelPath).getValue(2, 28))

'tunggu tombol tidak di disable lagi'
if (WebUI.waitForElementNotHasAttribute(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'),
	'disabled', 100, FailureHandling.OPTIONAL)) {
	'klik pada button login'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))
}

'jika ada pilihan role'
if (WebUI.verifyElementPresent(
	findTestObject('Object Repository/Change Password/Page_Login - eendigo Platform/Admin Client_3'),
		GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
	'pilih admin client'
	WebUI.click(findTestObject('Object Repository/Change Password/Page_Login - eendigo Platform/Admin Client_3'))
}
	
'klik pada tombol menu'
WebUI.click(findTestObject('Object Repository/Top Up/Page_Balance/spanMenu'))

'klik pada menu isi saldo'
WebUI.click(findTestObject('Object Repository/Top Up/Page_Balance/span_Isi Saldo'))

'cek apakah tombol menu dalam jangkauan web'
if (WebUI.verifyElementPresent(findTestObject('Object Repository/User Management-Role/Page_List Roles/tombolX_menu'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
	'klik pada tombol silang menu'
	WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/tombolX_menu'))
}

'deklarasi integer yang akan dipakai'
int totalKatalon, grandTotalafter, hargasatuanUI, hargasatuanDB

'deklarasi array untuk simpan data subtotal'
ArrayList allsubtotal = [], tempDataPrice = [], dataDBInstruction = [], listServices = [], listJumlahisiUlang = []

'ambil nama TipeSaldo dari DB'
ArrayList<String> namaTipeSaldoDB = CustomKeywords.'topup.TopupVerif.getDDLTipeSaldo'(conndev)

'ambil nama TrfMethod dari DB'
ArrayList<String> namaTrfMethodDB = CustomKeywords.'topup.TopupVerif.getDDLMetodeTrf'(conndev)

'ambil nama BankDest dari DB'
ArrayList<String> namaBankDestDB = CustomKeywords.'topup.TopupVerif.getDDLBank'(conndev)

'cek ddl tipesaldo apakah sesuai dengan db'
checkDDL(findTestObject('Object Repository/Top Up/Page_Topup Balance/inputtipesaldo'), namaTipeSaldoDB, 'DDL Tipe Saldo')
		
'cek ddl metode transfer apakah sesuai dengan db'
checkDDL(findTestObject('Object Repository/Top Up/Page_Topup Balance/inputMetodeBayar'), namaTrfMethodDB, 'DDL Metode transfer')
		
'cek ddl bank apakah sesuai dengan db'
checkDDL(findTestObject('Object Repository/Top Up/Page_Topup Balance/inputBank'), namaBankDestDB, 'DDL Bank Destination')

'input data tipe saldo yang diinginkan'
WebUI.setText(findTestObject('Object Repository/Top Up/Page_Topup Balance/inputtipesaldo'),
	findTestData(ExcelPath).getValue(2, 29))

'enter pada ddl tipe saldo'
WebUI.sendKeys(findTestObject('Object Repository/Top Up/Page_Topup Balance/inputtipesaldo'),
	 Keys.chord(Keys.ENTER))

'klik pada luaran form'
WebUI.click(findTestObject('Object Repository/Top Up/ClickForm'))

'input data metode pembayaran'
WebUI.setText(findTestObject('Object Repository/Top Up/Page_Topup Balance/inputMetodeBayar'),
	findTestData(ExcelPath).getValue(2, 30))

'enter pada ddl metode pembayaran'
WebUI.sendKeys(findTestObject('Object Repository/Top Up/Page_Topup Balance/inputMetodeBayar'),
	 Keys.chord(Keys.ENTER))

'input data bank'
WebUI.setText(findTestObject('Object Repository/Top Up/Page_Topup Balance/inputBank'),
	findTestData(ExcelPath).getValue(2, 31))

'enter pada ddl bank'
WebUI.sendKeys(findTestObject('Object Repository/Top Up/Page_Topup Balance/inputBank'),
	 Keys.chord(Keys.ENTER))

'klik pada tambah layanan'
WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/a_Tambah'))

'jika muncul notifikasi'
if (WebUI.verifyElementPresent(findTestObject('Object Repository/Top Up/NotifCatch'),
	GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
	'ambil teks notifikasi'
	String hasilNotif = WebUI.getText(findTestObject('Object Repository/Top Up/NotifCatch'))
	
	'klik tombol ok'
	WebUI.click(findTestObject('Object Repository/Top Up/button_OK'))
	
	'jika hasil notifikasi tidak sama dengan sukses'
	if (hasilNotif != 'Success') {
		GlobalVariable.FlagFailed = 1
						
		'tulis error sesuai reason yang ditampilkan oleh error message'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathOCR).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
				'<' + hasilNotif + '>')
	}
}

'ambil nama ActiveSaldo dari DB'
ArrayList<String> namaActiveSaldoDB = CustomKeywords.'topup.TopupVerif.getDDLSaldoactive'(conndevUAT, findTestData(ExcelPath).getValue(2, 27))

'cek ddl activesaldo sesuai dengan DB'
checkDDL(findTestObject('Object Repository/Top Up/Page_Topup Balance/inputJenisSaldo'), namaActiveSaldoDB, 'DDL Layanan')

'klik tombol cancel'
WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/button_Cancel'))

'cek apakah perlu tambah layanan'
if (findTestData(ExcelPath).getValue(2, 33) == 'Yes') {
	'ambil data services dari excel'
	listServices = []
	
	if (findTestData(ExcelPath).getValue(2, 34) == 'Price') {
		tipeSaldo = 'IDR'
	}
	
	listServices.add(tipeSaldo)
		
	'ambil data jumlah isi ulang dari excel'
	listJumlahisiUlang = findTestData(ExcelPath).getValue(
		2, 32).split(';', -1)
		
	for (int i = 0; i < listServices.size(); i++) {
		'deklarasi string subtotal'
		String subtotal
		
		'deklarasi subtotal convert'
		int subtotalconvert
		
		'klik pada tambah layanan'
		WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/a_Tambah'))
		
		'input data saldo yang dipilih'
		WebUI.setText(findTestObject('Object Repository/Top Up/Page_Topup Balance/inputJenisSaldo'),
			listServices[i])
		
		'enter pada ddl saldo yang dipilih'
		WebUI.sendKeys(findTestObject('Object Repository/Top Up/Page_Topup Balance/inputJenisSaldo'),
			 Keys.chord(Keys.ENTER))
		
		'input data jumlah isi ulang yang dipilih'
		WebUI.setText(findTestObject('Object Repository/Top Up/Page_Topup Balance/inputamount'),
			listJumlahisiUlang[i])
		
		'klik pada form untuk kalkulasi subtotal'
		WebUI.click(findTestObject('Object Repository/Top Up/FormClick'))
		
		'ambil data dari harga satuan'
		hargasatuanUI = Integer.parseInt(WebUI.getAttribute(
			findTestObject('Object Repository/Top Up/Page_Topup Balance/inputunitPrice'), 'value'))
		
		'ambil harga satuan dari DB'
		hargasatuanDB = CustomKeywords.'topup.TopupVerif.getServicePrice'(conndevUAT, listServices[i])
		
		'ambil data dari subtotal'
		subtotal = WebUI.getAttribute(
			findTestObject('Object Repository/Top Up/Page_Topup Balance/inputsubTotal'), 'value').replace('.', '')
			
		'ubah subtotal ke integer'
		subtotalconvert = Integer.parseInt(subtotal)
		
		'jika harga layanan di ui dan db sesuai'
		if (hargasatuanUI == hargasatuanDB) {
			'jika perhitungan subtotal tidak sesuai'
			if (hargasatuanDB * Integer.parseInt(listJumlahisiUlang[i])
				!= subtotalconvert) {
				'tulis penghitungan otomatis error'
				CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathOCR).getValue(GlobalVariable.NumOfColumn, 2) +
						';') + GlobalVariable.FailedReasonSubTotalCalc)
		
				GlobalVariable.FlagFailed = 1
			}
		} else {
			'tulis penghitungan otomatis error'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathOCR).getValue(GlobalVariable.NumOfColumn, 2) +
					';') + GlobalVariable.FailedReasonHargaSatuan)
	
			GlobalVariable.FlagFailed = 1
		}
		
		'cek apakah button save bisa di-klik'
		if (WebUI.verifyElementNotHasAttribute(findTestObject('Object Repository/Top Up/Page_Topup Balance/button_Save'),
			'disabled', GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
			'klik pada objek untuk save'
			WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/button_Save'))
			
			'tambah subtotal ke array'
			allsubtotal.add(subtotalconvert)
			
			tempDataPrice.add(hargasatuanDB.toString())
		} else {
			'klik tombol silang pada services'
			WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/tombolXservices'))
			
			'tulis error penambahan layanan'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathOCR).getValue(GlobalVariable.NumOfColumn, 2) +
					';') + GlobalVariable.FailedReasonAddServices)
	
			GlobalVariable.FlagFailed = 1
		}
	}
}

'lakukan penghitungan untuk subtotal'
for (int i = 0; i < allsubtotal.size(); i++) {
	'tambahkan hasilnya ke totalkatalon'
	totalKatalon += allsubtotal[i]
}

'cek apakah total di katalon dan UI sesuai'
checkVerifyEqualorMatch(WebUI.verifyEqual(totalKatalon,
	WebUI.getAttribute(findTestObject('Object Repository/Top Up/Page_Topup Balance/totalprice'),
		'value', FailureHandling.OPTIONAL).replace('.', '')), 'Total tidak sesuai')

'periksa apakah button disabled'
if (WebUI.verifyElementNotHasAttribute(findTestObject('Object Repository/Top Up/Page_Topup Balance/button_Next'),
	'disabled', GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
	'ambil data grandtotal'
	grandTotalafter = Integer.parseInt(WebUI.getAttribute(
		findTestObject('Object Repository/Top Up/Page_Topup Balance/grandTotal'),
			'value', FailureHandling.CONTINUE_ON_FAILURE).replaceAll('[^\\d]', ''))
	
	'klik pada tombol next'
	WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/button_Next'))
	
	'verify ada nya error'
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/Top Up/ErrorCatch'),
		GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		GlobalVariable.FlagFailed = 1
	
		'tulis error sesuai reason yang ditampilkan oleh error message'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathOCR).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
				'<' + WebUI.getText(findTestObject('Object Repository/Top Up/ErrorCatch'))  + '>')
	} else if (WebUI.verifyElementPresent(findTestObject('Object Repository/Top Up/NotifCatch'),
			GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		'ambil teks notifikasi'
		String hasilNotif = WebUI.getText(findTestObject('Object Repository/Top Up/NotifCatch'))
		
		'klik tombol ok'
		WebUI.click(findTestObject('Object Repository/Top Up/button_OK'))
		
		'jika hasil notifikasi tidak sama dengan sukses'
		if (hasilNotif != 'Success') {
			GlobalVariable.FlagFailed = 1
							
			'tulis error sesuai reason yang ditampilkan oleh error message'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathOCR).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
					'<' + hasilNotif + '>')
		}
	}
	
	'ambil data nomor transaksi'
	String noTrx = WebUI.getText(findTestObject('Object Repository/Top Up/Page_Topup Balance/noTrxReceipt'),
		FailureHandling.OPTIONAL)
	
	'hilangkan string yang tidak diperlukan'
	String noTrxKatalon = noTrx.substring(noTrx.indexOf('Nomor Transaksi Anda : ') + 'Nomor Transaksi Anda : '.length())
	
	'ambil data instruction dari DB'
	dataDBInstruction =  CustomKeywords.'topup.TopupVerif.getInstructionDetail'(conndev, noTrxKatalon)

	'cek apakah total transaksi sesuai'
	checkVerifyEqualorMatch(WebUI.verifyEqual(grandTotalafter,
		WebUI.getText(findTestObject('Object Repository/Top Up/Page_Topup Balance/totalTranxReceipt'),
			FailureHandling.OPTIONAL).replace('Rp. ', '').replace('.', ''),
				FailureHandling.OPTIONAL), 'Total transaksi receipt')
	
	'cek apakah nomor transaksi sesuai'
	checkVerifyEqualorMatch(WebUI.verifyEqual(dataDBInstruction[0], noTrxKatalon,
				FailureHandling.OPTIONAL), 'Nomor transaksi receipt')
	
	'cek apakah bank sesuai'
	checkVerifyEqualorMatch(WebUI.verifyEqual(dataDBInstruction[1],
		WebUI.getText(findTestObject('Object Repository/Top Up/Page_Topup Balance/bankNameReceipt'),
			FailureHandling.OPTIONAL),
				FailureHandling.OPTIONAL), 'Nama Bank receipt')
	
	'cek apakah virtual account sesuai'
	checkVerifyEqualorMatch(WebUI.verifyEqual(dataDBInstruction[2],
		WebUI.getText(findTestObject('Object Repository/Top Up/Page_Topup Balance/VANumReceipt'),
			FailureHandling.OPTIONAL),
				FailureHandling.OPTIONAL), 'Virtual Account receipt')
	
	'cek apakah nama akun virtual sesuai'
	checkVerifyEqualorMatch(WebUI.verifyEqual(dataDBInstruction[3],
		WebUI.getText(findTestObject('Object Repository/Top Up/Page_Topup Balance/NameAccReceipt'),
			FailureHandling.OPTIONAL),
				FailureHandling.OPTIONAL), 'VA Name receipt')
	
	'klik pada clickable text'
	WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/LinkReceipt'))
		
	WebUI.delay(8)
	
	'klik tombol cari'
	WebUI.click(findTestObject('Object Repository/Top Up/Page_List Transaction History/button_Cari'))
		
	'panggil fungsi cek riwayat terakhir'
	getLastTrx(noTrxKatalon, conndev)
}

'tutup browser'
WebUI.closeBrowser()

def settingBalanceType() {
	'click menu garis tiga atau burger'
	WebUI.click(findTestObject('Tenant/menu_Burger'))
	
	'click menu tenant'
	WebUI.click(findTestObject('Tenant/menu_Tenant'))
	
	'click button cari'
	WebUI.click(findTestObject('Tenant/button_Cari'))
	
	'input nama tenant'
	WebUI.setText(findTestObject('Tenant/input_NamaTenant'),
		findTestData(ExcelPath).getValue(2, 35))
	
	'click button cari'
	WebUI.click(findTestObject('Tenant/button_Cari'))
	
	'click button services balance'
	WebUI.click(findTestObject('Tenant/button_chargeType'))
	
	'ambil object check box'
	modifyCheckBox = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/VendorList'),
		'xpath', 'equals', '//*[@id="' + idOCR + '"]', true)
	
	'check if balance type quantity'
	if (findTestData(ExcelPath).getValue(2, 34) == 'Quantity') {
		WebUI.uncheck(modifyCheckBox, FailureHandling.CONTINUE_ON_FAILURE)
	} else if (findTestData(ExcelPath).getValue(2, 34) == 'Price') {
		WebUI.check(modifyCheckBox, FailureHandling.CONTINUE_ON_FAILURE)
	}
	
	'click button simpan'
	WebUI.click(findTestObject('Tenant/ChargeType/button_Simpan'))
}

def checkDDL(TestObject objectDDL, ArrayList<String> listDB, String reason) {
	'declare array untuk menampung ddl'
	ArrayList<String> list = []

	'click untuk memunculkan ddl'
	WebUI.click(objectDDL)
	
	'get id ddl'
	id = WebUI.getAttribute(findTestObject('Object Repository/Top Up/ddlClass'), 'id', FailureHandling.CONTINUE_ON_FAILURE)
	
	'get row'
	variable = DriverFactory.webDriver.findElements(By.cssSelector(('#' + id) + '> div > div:nth-child(2) div'))
	
	'looping untuk get ddl kedalam array'
	for (i = 1; i < variable.size(); i++) {
		'modify object DDL'
		modifyObjectDDL = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/modifyObject'), 'xpath', 'equals', ((('//*[@id=\'' +
			id) + '-') + i) + '\']', true)

		'add ddl ke array'
		list.add(WebUI.getText(modifyObjectDDL))
	}
	
	'verify ddl ui = db'
	checkVerifyEqualorMatch(listDB.containsAll(list), reason)

	'verify jumlah ddl ui = db'
	checkVerifyEqualorMatch(WebUI.verifyEqual(list.size(), listDB.size(), FailureHandling.CONTINUE_ON_FAILURE), ' Jumlah ' + reason)
	
	'Input enter untuk tutup ddl'
	WebUI.sendKeys(objectDDL, Keys.chord(Keys.ENTER))
}

'ambil no. transaksi pada tabel'
def getLastTrx(String noTrxKatalon, Connection conn) {
	'periksa apakah tombol skip to last page ada'
	if (WebUI.verifyElementVisible(findTestObject('Object Repository/Top Up/Page_List Transaction History/lastPage'),
		 FailureHandling.OPTIONAL)) {
		 'klik ubah ke halaman terakhir'
		 WebUI.click(findTestObject('Object Repository/Top Up/Page_List Transaction History/lastPage'))
	}
	
	'ambil alamat trxnumber'
	variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-list-transaction-history > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))
	
	'banyaknya row table'
	int lastIndex = variable.size()
	
	'modifikasi alamat object trxnumber'
	modifytrxnumber = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/Page_List Transaction History/noTranx'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[1]/div/p', true)
	
	'modifikasi object tgl transaksi'
	modifytgltrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/Page_List Transaction History/tglTranx'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[2]/div/span', true)

	'modifikasi object tipe saldo'
	modifytipesaldo = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/Page_List Transaction History/tipeSaldoTranx'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[3]/div/p', true)

	'modifikasi object metode transfer'
	modifymetodetrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/Page_List Transaction History/metodeTranx'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[5]/div/p', true)

	'modifikasi object status transaksi'
	modifystatustrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/Page_List Transaction History/statusTranx'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[6]/div/p', true)

	'ambil data table dari db'
	ArrayList result = CustomKeywords.'topup.TopupVerif.getRiwayatTabelData'(conn, noTrxKatalon)
		
	'kembalikan nomor transaksi'
	int arrayIndex = 0
	
	'verify trxnum transaksi ui = db'
	checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifytrxnumber), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), 'TrxNum Riwayat')
	
	'verify tgltrx transaksi ui = db'
	checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifytgltrx), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), 'Tanggal Trx Riwayat')
	
	'verify tipe saldo transaksi ui = db'
	checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifytipesaldo), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), 'TrxTipeSaldo Riwayat')

	'verify metode transaksi ui = db'
	checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifymetodetrx), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), 'MetodeTrx Riwayat')
	
	'verify status transaksi ui = db'
	checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifystatustrx), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), 'StatusTrx Riwayat')
	
	'modifikasi object tombol detail'
	modifytomboldetail = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/modifyObject'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[7]/div/a[1]/em', true)

	'klik pada tombol detail'
	WebUI.click(modifytomboldetail)
	
	'ambil data table dari db'
	result = CustomKeywords.'topup.TopupVerif.getRiwayatDetail'(conn, noTrxKatalon)
	
	'ambil alamat trxnumber'
	variabledetail = DriverFactory.webDriver.findElements(By.cssSelector('body > ngb-modal-window > div > div > app-transaction-history-detail > div > div.modal-body > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))
	
	'arraylist untuk tampung detail'
	ArrayList detail = []
	
	'lakukan loop untuk ambil data detail'
	for (int i = 1; i <= variabledetail.size(); i++) {
		'modifikasi object layanan transaksi'
		modifylayanandetail = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/Page_List Transaction History/layananDetail'), 'xpath', 'equals', '/html/body/ngb-modal-window/div/div/app-transaction-history-detail/div/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + i + ']/datatable-body-row/div[2]/datatable-body-cell[1]/div/p', true)
	
		'modifikasi object unit price transaksi'
		modifyunitpricedetail = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/Page_List Transaction History/unitpriceDetail'), 'xpath', 'equals', '/html/body/ngb-modal-window/div/div/app-transaction-history-detail/div/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + i + ']/datatable-body-row/div[2]/datatable-body-cell[2]/div', true)

		'modifikasi object jumlah transaksi'
		modifyjumlahdetail = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/Page_List Transaction History/jumlahDetail'), 'xpath', 'equals', '/html/body/ngb-modal-window/div/div/app-transaction-history-detail/div/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + i + ']/datatable-body-row/div[2]/datatable-body-cell[3]/div', true)

		'modifikasi object subtotal transaksi'
		modifysubtotaldetail = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/Page_List Transaction History/subtotalDetail'), 'xpath', 'equals', '/html/body/ngb-modal-window/div/div/app-transaction-history-detail/div/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + i + ']/datatable-body-row/div[2]/datatable-body-cell[4]/div', true)
		
		'tambah hasil get text layanan ke array'
		detail.add(WebUI.getText(modifylayanandetail))
		
		'tambah hasil get text harga satuan ke array'
		detail.add(WebUI.getText(modifyunitpricedetail).replaceAll('[^\\d]', ''))
		
		'tambah hasil get text qty ke array'
		detail.add(WebUI.getText(modifyjumlahdetail))
		
		'tambah hasil get text subtotal ke array'
		detail.add(WebUI.getText(modifysubtotaldetail).replaceAll('[^\\d]', ''))
	}
	
	'cek apakah ada data yang tidak sesuai'
	for (int j = 0; j < result.size(); j++) {
		'verify layanan detail transaksi ui = db'
		checkVerifyEqualorMatch(WebUI.verifyMatch(detail[j], result[j], false, FailureHandling.CONTINUE_ON_FAILURE), 'Detail tidak sesuai')
	}
	
	'klik tombol silang'
	WebUI.click(findTestObject('Object Repository/Top Up/Page_List Transaction History/tombolX'))
}

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
	if (isMatch == false) {
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		GlobalVariable.FlagFailed = 1
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathOCR).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
				GlobalVariable.FailedReasonVerifyEqualorMatch + ' ' + reason)
	}
}
