import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.testobject.ResponseObject

import java.sql.Connection
import java.sql.Driver

import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable

import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Edit Profile'
int countColumnEdit = findTestData(ExcelPathOCRTesting).columnNumbers

'deklarasi variabel untuk konek ke Database eendigo_dev'
Connection conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_public'()

'deklarasi koneksi ke Database adins_apiaas_uat'
Connection conndevUAT = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_devUat'()

'get base url'
GlobalVariable.BaseUrl =  findTestData('Login/BaseUrl').getValue(2, 3)

'panggil fungsi login'
WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'OCR', ('SheetName') : 'Dukcapil(NonBiom)',
	('Path') : ExcelPathOCRTesting, ('Row') : 28], FailureHandling.STOP_ON_FAILURE)

if (GlobalVariable.SettingEnvi == 'Production') {
	'click pada production'
	WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/button_Production'))
}

'pindah testcase sesuai jumlah di excel'
for(GlobalVariable.NumOfColumn = 2; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++){

	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 1).length() == 0) {
		
		break
		
	} else if (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 1).equalsIgnoreCase('Unexecuted')) {
		
		'ambil kode tenant di DB'
		String tenantcode = CustomKeywords.'ocrTesting.GetParameterfromDB.getTenantCodefromDB'(conn,
			findTestData(ExcelPathOCRTesting).getValue(2, 28))
		
		'ambil key trial yang aktif dari DB'
		String thekey = CustomKeywords.'ocrTesting.GetParameterfromDB.getAPIKeyfromDB'(conn, tenantcode, GlobalVariable.SettingEnvi)
		
		'deklarasi id untuk harga pembayaran OCR'
		int idPayment = CustomKeywords.'ocrTesting.GetParameterfromDB.getIDPaymentType'(conndevUAT,
			tenantcode, findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 14))
		
		'ambil jenis penagihan transaksi (by qty/price)'
		String balanceChargeType = CustomKeywords.'ocrTesting.GetParameterfromDB.getPaymentType'(conndevUAT,
			tenantcode, idPayment)
		
		'deklarasi variable response'
		ResponseObject response
		
		'cek apakah perlu tambah API'
		String useCorrectKey = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 20)
		
		'cek apakah perlu gunakan tenantcode yang salah'
		String useCorrectTenant = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 18)
				
		'deklarasi variabel angka'
		int isSaldoBerkurang, saldobefore, uiSaldoafter, katalonSaldoafter, isTrxIncreased, HitAPITrx
		
		'penanda untuk HIT yang berhasil dan gagal'
		HitAPITrx = 1
		
		'set penanda error menjadi 0'
		GlobalVariable.FlagFailed = 0
		
		'panggil fungsi filter saldo berdasarkan input user'
		filterSaldo()
		
		'cek apakah button skip enable atau disable'
		if (WebUI.verifyElementVisible(
			findTestObject('Object Repository/API_KEY/Page_Balance/skiptoLast_page'), FailureHandling.OPTIONAL)) {
		
			'klik button skip to last page'
			WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/skiptoLast_page'))
		}
		
		'panggil fungsi ambil transaksi terakhir di tabel'
		String no_Trx_before = getTrxNumber()
		
		'variabel yang menyimpan saldo sebelum adanya transaksi'
		saldobefore = getSaldoforTransaction(findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 14))
		
		if (useCorrectKey != 'Yes') {
			
			thekey = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 21)
		}
		if (useCorrectTenant != 'Yes') {
			
			tenantcode = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 19)
		}
		
		'lakukan proses HIT api dengan parameter image, key, dan juga tenant'
		response = WS.sendRequest(findTestObject('Object Repository/OCR Testing/dukcapil UAT - Biometrik',
		[('img'): findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 9),
		('tenant'):tenantcode,
		('key'):thekey,
		('loginId'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 23),
		('refNum'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 24),
		('off_code'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 25),
		('off_name'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 26),
		('phoneNum'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 10),
		('idNo'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 11),
		('fullName'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 12),
		('DOB'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 13),
		('email'):findTestData(ExcelPathOCRTesting).getValue(2, 28)
		]))
			
		'ambil message respon dari HIT tersebut'
		message_ocr = WS.getElementPropertyValue(response, 'message')
		
		'ambil status dari respon HIT tersebut'
		state_ocr = WS.getElementPropertyValue(response, 'status')
		
		'ambil verifStatus dari respon HIT'
		verifState_ocr = WS.getElementPropertyValue(response, 'verifStatus')
	
		'jika kurang saldo hentikan proses testing'
		if (state_ocr == 'FAILED' && message_ocr == 'Insufficient balance') {
			
			'write to excel status failed dan reason'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Dukcapil(NonBiom)', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
			 '<' + message_ocr + '>')
			
			if(GlobalVariable.SettingTopup.equals('IsiSaldo')) {
				
				'call auto isi saldo'
				WebUI.callTestCase(findTestCase('IsiSaldo/IsiSaldoAuto'), [('ExcelPathOCR') : ExcelPathOCRTesting, ('ExcelPath') : 'Login/Login', ('tipeSaldo') : 'Verifikasi Identitas Dukcapil', ('sheet') : 'Dukcapil(NonBiom)', ('idOCR') : 'DUKCAPIL_VIDA'],
					FailureHandling.CONTINUE_ON_FAILURE)
			}
			else if (GlobalVariable.SettingTopup.equals('SelfTopUp')) {
				
				'call isi saldo secara mandiri di Admin Client'
				WebUI.callTestCase(findTestCase('Top Up/TopUpAuto'), [('ExcelPathOCR') : ExcelPathOCRTesting, ('ExcelPath') : 'Login/Login', ('tipeSaldo') : 'Verifikasi Identitas Dukcapil', ('sheet') : 'Dukcapil(NonBiom)', ('idOCR') : 'DUKCAPIL_VIDA'],
					FailureHandling.CONTINUE_ON_FAILURE)
				
				'lakukan approval di transaction history'
				WebUI.callTestCase(findTestCase('Transaction History/TransactionHistoryAuto'), [('ExcelPathOCR') : ExcelPathOCRTesting, ('ExcelPath') : 'Login/Login', ('tipeSaldo') : 'Verifikasi Identitas Dukcapil', ('sheet') : 'Dukcapil(NonBiom)', ('idOCR') : 'DUKCAPIL_VIDA'],
					FailureHandling.CONTINUE_ON_FAILURE)
			}
			
			'panggil fungsi login'
			WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'OCR', ('SheetName') : 'Dukcapil(NonBiom)',
				('Path') : ExcelPathOCRTesting, ('Row') : 28], FailureHandling.STOP_ON_FAILURE)
			
			continue
			
		}
		//jika status sukses dengan key dan kode tenant yang salah, anggap sebagai bug dan lanjutkan ke tc berikutnya
		else if (state_ocr == '0' && useCorrectKey != 'Yes' && useCorrectTenant != 'Yes') {
			
			'write to excel status failed dan reason'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Dukcapil(NonBiom)', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
			GlobalVariable.FailedReasonKeyTenantBypass)
				
			continue;
		}
		
		'refresh halaman web'
		WebUI.refresh()
		
		'cek apakah muncul error unknown setelah login'
		if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'),
			GlobalVariable.Timeout, FailureHandling.OPTIONAL) == false) {
			
			GlobalVariable.FlagFailed = 1
			
			'tulis adanya error pada sistem web'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Dukcapil(NonBiom)', GlobalVariable.NumOfColumn,
				GlobalVariable.StatusWarning, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
					GlobalVariable.FailedReasonUnknown)
		}
		
		'panggil fungsi filter saldo berdasarkan inputan user'
		filterSaldo()
		
		'cek apakah button skip enable atau disable'
		if (WebUI.verifyElementVisible(
			findTestObject('Object Repository/API_KEY/Page_Balance/skiptoLast_page'), FailureHandling.OPTIONAL)) {
		
			'klik button skip to last page'
			WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/skiptoLast_page'))
		}
		
		'variabel yang diharapkan menyimpan number transaksi sesudah hit'
		String no_Trx_after = getTrxNumber()
		
		'jika user ingin cek ke DB hasil HIT API nya'
		if (GlobalVariable.KondisiCekDB == 'Yes') {
			
			'simpan trx number terbaru dari DB'
			String latestMutation = CustomKeywords.'ocrTesting.GetParameterfromDB.getLatestMutationfromDB'(conndevUAT, 
				tenantcode)
	
			'simpan trx number terbaru milik tenant lain dari DB'
			String latestOtherTenantMutation = CustomKeywords.'ocrTesting.GetParameterfromDB.getNotMyLatestMutationfromDB'(conndevUAT, tenantcode)
			
			'jika data transaction number di web dan DB tidak sesuai'
			if (latestMutation != no_Trx_after || latestMutation == latestOtherTenantMutation) {
				
				'anggap HIT Api gagal'
				HitAPITrx = 0
			}
		}
		
		'simpan harga Dukcapil(NonBiom) ke dalam integer'
		int serviceprice = CustomKeywords.'ocrTesting.GetParameterfromDB.getServicePricefromDB'(conndevUAT, idPayment)
		
		'jika HIT API successful'
		if (HitAPITrx == 1) {
			
			'cek apakah jenis penagihan berdasarkan harga'
			if(balanceChargeType == 'Price') {
				
				'input saldo setelah penagihan'
				katalonSaldoafter = saldobefore - serviceprice
			}
			else {
				
				'input saldo setelah penagihan dikurangi qty'
				katalonSaldoafter = saldobefore - 1
			}
		}
		
		'simpan saldo setelah di HIT'
		uiSaldoafter = getSaldoforTransaction(findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 14))
		
		'jika saldoafter match'
		if (katalonSaldoafter == uiSaldoafter) {
			
			isSaldoBerkurang = 1
		}
		else {
			
			isSaldoBerkurang = 0
		}
		
		'jika transaksi bertambah di DB dan di web'
		if (no_Trx_after > no_Trx_before) {
			
			'web mencatat transaksi terbaru'
			isTrxIncreased = 1
		}
		else {
			
			'web tidak mencatat transaksi terbaru'
			isTrxIncreased = 0
		}
		
		'jika tidak ada message error dan kondisi lain terpenuhi'
		if (message_ocr == 'ID has been checked.' && state_ocr == 0 && verifState_ocr == true && isTrxIncreased == 1 
			&& isSaldoBerkurang == 1 && HitAPITrx == 1) {
			
			'tulis status sukses pada excel'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Dukcapil(NonBiom)', 
				GlobalVariable.NumOfColumn, GlobalVariable.StatusSuccess,
					GlobalVariable.SuccessReason)
		
		}
		//kondisi jika transaksi berhasil tapi tidak tercatat/tersimpan di DB
		else if (state_ocr == 0 && isTrxIncreased == 0 && isSaldoBerkurang == 1) {
			
			GlobalVariable.FlagFailed = 1
			'tulis kondisi gagal'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Dukcapil(NonBiom)', 
				GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
					GlobalVariable.FailedReasonTrxNotinDB)
		}
		//kondisi jika transaksi berhasil tapi saldo tidak berkurang
		else if (state_ocr == 0 && isTrxIncreased == 1 && isSaldoBerkurang == 0) {
			
			GlobalVariable.FlagFailed = 1
			'tulis kondisi gagal'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Dukcapil(NonBiom)', 
				GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
					GlobalVariable.FailedReasonBalanceNotChange)
		}
		//kondisi transaksi tidak tampil dan tidak tersimpan di DB
		else if (HitAPITrx == 0 && state_ocr == 0 && isTrxIncreased == 0 && isSaldoBerkurang == 1) {
			
			GlobalVariable.FlagFailed = 1
			'tulis kondisi gagal'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Dukcapil(NonBiom)', 
				GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
					GlobalVariable.FailedReasonSaldoBocor)
		}
		else {
			
			GlobalVariable.FlagFailed = 1
			'write to excel status failed dan reason'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Dukcapil(NonBiom)', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
			'<' + message_ocr + '>')
		}
		
		'refresh halaman web'
		WebUI.refresh()
		
		'cek apakah muncul error unknown setelah login'
		if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'),
			GlobalVariable.Timeout, FailureHandling.OPTIONAL) == false) {
			
			GlobalVariable.FlagFailed = 1
			
			'tulis adanya error pada sistem web'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Dukcapil(NonBiom)', GlobalVariable.NumOfColumn,
				GlobalVariable.StatusWarning, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
					GlobalVariable.FailedReasonUnknown)
		}
	}
}

'tutup browser jika loop sudah selesai'
WebUI.closeBrowser()

'ambil saldo sesuai testing yang dilakukan'
def getSaldoforTransaction(String NamaOCR) {
	
	'deklarasi jumlah saldo sekarang'
	int saldoNow
	
	if(WebUI.verifyElementPresent(findTestObject('Object Repository/OCR Testing/TrxNumber'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		
		'cari element dengan nama saldo'
		def elementNamaSaldo = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance-prod > div.row.match-height > div > lib-balance-summary > div > div'))
		
		'lakukan loop untuk cari nama saldo yang ditentukan'
		for(int i=1; i<=elementNamaSaldo.size(); i++){
			
			'cari nama saldo yang sesuai di list saldo'
			def modifyNamaSaldo = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Balance/span_OCR KK'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[1]/div/lib-balance-summary/div/div["+ (i) +"]/div/div/div/div/div[1]/span", true)
	
			'jika nama object sesuai dengan nama saldo'
			if(WebUI.getText(modifyNamaSaldo) == NamaOCR){
				
				'ubah alamat jumlah saldo ke kotak saldo yang dipilih'
				def modifySaldoDipilih = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Balance/kotakSaldo'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[1]/div/lib-balance-summary/div/div["+ (i) +"]/div/div/div/div/div[1]/h3", true)
				
				'simpan jumlah saldo sekarang di variabel'
				 saldoNow = Integer.parseInt(WebUI.getText(modifySaldoDipilih).replace(',',''))
			}
		}
		'pakai saldo IDR jika lainnya tidak ada'
		if (saldoNow == 0) {
			
			'simpan jumlah saldo sekarang di variabel'
			saldoNow = Integer.parseInt(WebUI.getText(findTestObject('Object Repository/API_KEY/Page_Balance/kotakSaldo')).replace(',',''))
		}
	}
	'kembalikan nilai saldo sekarang'
	return saldoNow
}

'ambil no. transaksi pada tabel'
def getTrxNumber() {
	
	String noTrx
	
	if(WebUI.verifyElementPresent(findTestObject('Object Repository/OCR Testing/TrxNumber'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		
		'ambil alamat trxnumber'
		def variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance-prod > div.ng-star-inserted > app-msx-paging-v2 > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))
		
		'banyaknya row table'
		int lastIndex = variable.size()
			
		'modifikasi alamat object trxnumber'
		def modifytrxnumber = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/TrxNumber'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (lastIndex) +"]/datatable-body-row/div[2]/datatable-body-cell[6]/div/p", true)
								
		'simpan nomor transaction number ke string'
		noTrx = WebUI.getText(modifytrxnumber)
	
	} else {
		noTrx = ''
	}
	'kembalikan nomor transaksi'
	return noTrx
}

'fungsi untuk filter saldo berdasarkan input user'
def filterSaldo() {
	'tunggu webpage load'
	WebUI.delay(4)
	
	'isi field input tipe saldo'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'), 
		findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 14))
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'), Keys.chord(Keys.ENTER))
	
	'isi field tipe transaksi'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipetranc'), 
		findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 15))
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipetranc'), Keys.chord(Keys.ENTER))
		
	'klik pada button cari'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/button_Cari'))
}