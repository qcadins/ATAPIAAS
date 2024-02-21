import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.testobject.ResponseObject
import java.sql.Connection

import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable
import org.openqa.selenium.By
import org.openqa.selenium.Keys

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writetoexcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Edit Profile'
int countColumnEdit = findTestData(ExcelPathOCRTesting).columnNumbers

'deklarasi variabel untuk konek ke Database eendigo_dev'
Connection conn = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_public'()

'deklarasi koneksi ke Database adins_apiaas_uat'
Connection connProd = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_uatProduction'()

'get base url'
GlobalVariable.BaseUrl =  findTestData('Login/BaseUrl').getValue(2, 4)

'panggil fungsi login'
WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'OCR', ('SheetName') : sheet,
	('Path') : ExcelPathOCRTesting, ('Username') : 'UsernameLogin', ('Password') : 'PasswordLogin'], FailureHandling.STOP_ON_FAILURE)

if (GlobalVariable.SettingEnvi == 'Production') {
	'click pada production'
	WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/button_Production'))
}

'pindah testcase sesuai jumlah di excel'
for (GlobalVariable.NumOfColumn = 2; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++) {
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).length() == 0) {
		break
	} else if (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
		'ambil kode tenant di DB'
		String tenantcode = CustomKeywords.'ocrtesting.GetParameterfromDB.getTenantCodefromDB'(conn,
			findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('UsernameLogin')))
		
		'ambil key trial yang aktif dari DB'
		String thekey = CustomKeywords.'ocrtesting.GetParameterfromDB.getAPIKeyfromDB'(conn, tenantcode, GlobalVariable.SettingEnvi)
		
		'deklarasi id untuk harga pembayaran OCR'
		int idPayment = CustomKeywords.'ocrtesting.GetParameterfromDB.getIDPaymentType'(connProd, tenantcode, lovBalanceType)
		
		'ambil jenis penagihan transaksi (by qty/price)'
		String balanceChargeType = CustomKeywords.'ocrtesting.GetParameterfromDB.getPaymentType'(connProd,
			tenantcode, idPayment)
		
		'deklarasi variable response'
		ResponseObject response
		
		'cek apakah perlu tambah API'
		String useCorrectKey = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('UseCorrectKey?(Yes/No)'))
		
		'cek apakah perlu gunakan tenantcode yang salah'
		String useCorrectTenant = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('UseCorrectTenantCode?'))
		
		'deklarasi variabel angka'
		int isSaldoBerkurang, saldobefore, uisaldoafter, katalonSaldoafter, isTrxIncreased, hitAPITrx
		
		'penanda untuk HIT yang berhasil dan gagal'
		hitAPITrx = 1
		
		'set penanda error menjadi 0'
		GlobalVariable.FlagFailed = 0
		
		'panggil fungsi filter saldo berdasarkan input user'
		filterSaldo()
		
		'cek apakah button skip enable atau disable'
		if (WebUI.verifyElementVisible(findTestObject('Object Repository/API_KEY/Page_Balance/skiptoLast_page'), FailureHandling.OPTIONAL)) {
			'klik button skip to last page'
			WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/skiptoLast_page'))
		}
		
		'panggil fungsi ambil transaksi terakhir di tabel'
		String noTrxbefore = tableTrxNumber()
		
		'variabel yang menyimpan saldo sebelum adanya transaksi'
		saldobefore = getSaldoforTransaction(findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('$SearchTipeSaldo')))
		
		if (useCorrectKey != 'Yes') {
			thekey = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Wrong Key'))
		} else if (useCorrectTenant != 'Yes') {
			tenantcode = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Wrong TenantCode'))
		}
			
		'lakukan proses HIT api dengan parameter image, key, dan juga tenant'
		response = WS.sendRequest(findTestObject('Object Repository/OCR Testing/OCR BPKB',
		[
			('page2'): findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('$halaman1')),
			('page3'): findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('$halaman2')),
			('key'):thekey,
			('tenant'):tenantcode,
			('custno'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Customer No.')),
			('off_code'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('OfficeCode')),
			('off_name'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('OfficeName')),
			('question'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Question')),
			('loginId'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('LoginID')),
			('refNum'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('refNumber')),
			('source'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('source')),
		]))
						
		'ambil message respon dari HIT tersebut'
		messageocr = WS.getElementPropertyValue(response, 'message')
					
		'ambil status dari respon HIT tersebut'
		stateocr = WS.getElementPropertyValue(response, 'status')
						
		'jika kurang saldo hentikan proses testing'
		if (stateocr == 'FAILED' && messageocr == 'Insufficient balance') {
			'write to excel status failed dan reason'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
			 '<' + messageocr + '>')
			
			if (GlobalVariable.SettingTopup == 'IsiSaldo') {
				'call auto isi saldo'
				WebUI.callTestCase(findTestCase('IsiSaldo/IsiSaldoAuto'), [('ExcelPathOCR') : ExcelPathOCRTesting, ('ExcelPath') : 'Login/Login', ('tipeSaldo') : lovBalanceType, ('sheet') : sheet, ('idOCR') : idOCR],
					FailureHandling.CONTINUE_ON_FAILURE)
			}
			else if (GlobalVariable.SettingTopup == 'SelfTopUp') {
				'call isi saldo secara mandiri di Admin Client'
				WebUI.callTestCase(findTestCase('Top Up/TopUpAuto'), [('ExcelPathOCR') : ExcelPathOCRTesting, ('ExcelPath') : 'Login/Login', ('tipeSaldo') : lovBalanceType, ('sheet') : sheet, ('idOCR') : idOCR],
					FailureHandling.CONTINUE_ON_FAILURE)
				
				'lakukan approval di transaction history'
				WebUI.callTestCase(findTestCase('Transaction History/TransactionHistoryAuto'), [('ExcelPathOCR') : ExcelPathOCRTesting, ('ExcelPath') : 'Login/Login', ('tipeSaldo') : lovBalanceType, ('sheet') : sheet, ('idOCR') : idOCR],
					FailureHandling.CONTINUE_ON_FAILURE)
			}
			
			'panggil fungsi login'
			WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'OCR', ('SheetName') : sheet,
				('Path') : ExcelPathOCRTesting, ('Username') : 'UsernameLogin', ('Password') : 'PasswordLogin'], FailureHandling.STOP_ON_FAILURE)
			
			continue
		}
		//jika status sukses dengan key dan kode tenant yang salah, anggap sebagai bug dan lanjutkan ke tc berikutnya
		else if (stateocr == 'SUCCESS' && useCorrectKey != 'Yes' && useCorrectTenant != 'Yes') {
			'write to excel status failed dan reason'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
			GlobalVariable.FailedReasonKeyTenantBypass)
							
			continue
		}
		//jika mandatory tidak terpenuhi atau ada error
		else if (messageocr == 'BPKB Hal 2 or 3 not found' || messageocr == 'Unexpected Error' 
				|| messageocr == 'Invalid API key or tenant code') {
			'write to excel status failed dan reason'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
			'<' + messageocr + '>')
			continue
		}
		
		'refresh halaman web'
		WebUI.refresh()
		
		'cek apakah muncul error unknown setelah login'
		if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'),
			GlobalVariable.Timeout, FailureHandling.OPTIONAL) == false) {
			GlobalVariable.FlagFailed = 1
			
			'tulis adanya error pada sistem web'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusWarning, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
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
		String noTrxafter = tableTrxNumber()
		
		'jika user ingin cek ke DB hasil HIT API nya'
		if (GlobalVariable.KondisiCekDB == 'Yes') {
			'simpan trx number terbaru dari DB'
			String latestMutation = CustomKeywords.'ocrtesting.GetParameterfromDB.getLatestMutationfromDB'(connProd, tenantcode)
			
			'simpan trx number terbaru milik tenant lain dari DB'
			String latestOtherTenantMutation = CustomKeywords.'ocrtesting.GetParameterfromDB.getNotMyLatestMutationfromDB'(connProd, tenantcode)
			
			'jika data transaction number di web dan DB tidak sesuai'
			if (latestMutation != noTrxafter || latestMutation == latestOtherTenantMutation) {
				'anggap HIT Api gagal'
				hitAPITrx = 0
			}
		}
		
		'simpan harga OCR BPKB ke dalam integer'
		int serviceprice = CustomKeywords.'ocrtesting.GetParameterfromDB.getServicePricefromDB'(connProd, idPayment)
		
		'jika HIT API successful'
		if (hitAPITrx == 1) {
			'cek apakah jenis penagihan berdasarkan harga'
			if (balanceChargeType == 'Price') {
				'input saldo setelah penagihan'
				katalonSaldoafter = saldobefore - serviceprice
			} else {
				'input saldo setelah penagihan dikurangi qty'
				katalonSaldoafter = saldobefore - 1
			}
		}
		
		'simpan saldo setelah di HIT'
		uisaldoafter = getSaldoforTransaction(findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('$SearchTipeSaldo')))
	
		'jika saldoafter match'
		if (katalonSaldoafter == uisaldoafter) {
			isSaldoBerkurang = 1
		} else {
			isSaldoBerkurang = 0
		}
		
		'jika transaksi bertambah di DB dan di web'
		if (noTrxafter > noTrxbefore) {
			'web mencatat transaksi terbaru'
			isTrxIncreased = 1
		} else {
			'web tidak mencatat transaksi terbaru'
			isTrxIncreased = 0
		}
		
		'jika tidak ada message error dan kondisi lain terpenuhi'
		if (messageocr == '' && stateocr == 'SUCCESS' && isTrxIncreased == 1 && isSaldoBerkurang == 1 
			&& hitAPITrx == 1) {
			'tulis status sukses pada excel'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet,
				GlobalVariable.NumOfColumn, GlobalVariable.StatusSuccess,
					GlobalVariable.SuccessReason)
		}
		//kondisi jika transaksi berhasil tapi tidak tercatat/tersimpan di DB
		else if (stateocr == 'SUCCESS' && isTrxIncreased == 0 && isSaldoBerkurang == 1) {
			GlobalVariable.FlagFailed = 1
			
			'tulis kondisi gagal'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, 
				GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
					GlobalVariable.FailedReasonTrxNotinDB)
		}
		//kondisi jika transaksi berhasil tapi saldo tidak berkurang
		else if (stateocr == 'SUCCESS' && isTrxIncreased == 1 && isSaldoBerkurang == 0) {
			GlobalVariable.FlagFailed = 1
			
			'tulis kondisi gagal'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, 
				GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
					GlobalVariable.FailedReasonBalanceNotChange)
		}
		//kondisi transaksi tidak tampil dan tidak tersimpan di DB
		else if (hitAPITrx == 0 && stateocr == 'FAILED' && isTrxIncreased == 0 && isSaldoBerkurang == 1) {
			GlobalVariable.FlagFailed = 1
			
			'tulis kondisi gagal'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
			GlobalVariable.FailedReasonSaldoBocor)
		}
		else {
			GlobalVariable.FlagFailed = 1
			
			'write to excel status failed dan reason'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
			 '<' + messageocr + '>')
		}
		'refresh laman web sesudah kondisi write to excel'
		WebUI.refresh()
		
		'cek apakah muncul error unknown setelah login'
		if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'), GlobalVariable.Timeout, FailureHandling.OPTIONAL) == false) {
			GlobalVariable.FlagFailed = 1
			
			'tulis adanya error pada sistem web'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusWarning, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
					GlobalVariable.FailedReasonUnknown)
		}
	}
}

'tutup browser jika loop sudah selesai'
WebUI.closeBrowser()

'ambil saldo sesuai testing yang dilakukan'
def getSaldoforTransaction(String namaOCR) {
	'deklarasi jumlah saldo sekarang'
	int saldoNow
	
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/OCR Testing/TrxNumber'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		'cari element dengan nama saldo'
		elementNamaSaldo = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance-prod > div.row.match-height > div > lib-balance-summary > div > div'))
		
		'lakukan loop untuk cari nama saldo yang ditentukan'
		for (int i = 1; i <= elementNamaSaldo.size(); i++) {
			'cari nama saldo yang sesuai di list saldo'
			modifyNamaSaldo = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Balance/span_OCR KK'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[1]/div/lib-balance-summary/div/div[' + i + ']/div/div/div/div/div[1]/span', true)
	
			'jika nama object sesuai dengan nama saldo'
			if (WebUI.getText(modifyNamaSaldo) == namaOCR) {
				'ubah alamat jumlah saldo ke kotak saldo yang dipilih'
				modifySaldoDipilih = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Balance/kotakSaldo'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[1]/div/lib-balance-summary/div/div[' + i + ']/div/div/div/div/div[1]/h3', true)
				
				'simpan jumlah saldo sekarang di variabel'
				 saldoNow = Integer.parseInt(WebUI.getText(modifySaldoDipilih).replace(',', ''))
			}
		}
		'pakai saldo IDR jika lainnya tidak ada'
		if (saldoNow == 0) {
			'simpan jumlah saldo sekarang di variabel'
			saldoNow = Integer.parseInt(WebUI.getText(findTestObject('Object Repository/API_KEY/Page_Balance/kotakSaldo')).replace(',', ''))
		}
	}
	'kembalikan nilai saldo sekarang'
	saldoNow
}

'ambil no. transaksi pada tabel'
def tableTrxNumber() {
	String noTrx
	
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/OCR Testing/TrxNumber'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		'ambil alamat trxnumber'
		variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance-prod > div.ng-star-inserted > app-msx-paging-v2 > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))
		
		'banyaknya row table'
		int lastIndex = variable.size()
			
		'modifikasi alamat object trxnumber'
		modifytrxnumber = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/TrxNumber'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[6]/div/p', true)
								
		'simpan nomor transaction number ke string'
		noTrx = WebUI.getText(modifytrxnumber)
	} else {
		noTrx = ''
	}
	'kembalikan nomor transaksi'
	noTrx
}

'fungsi untuk filter saldo berdasarkan input user'
def filterSaldo() {
	'tunggu webpage load'
	WebUI.delay(4)
	
	'isi field input tipe saldo'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'), 
		findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('$SearchTipeSaldo')))
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'), Keys.chord(Keys.ENTER))
	
	'isi field tipe transaksi'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipetranc'), 
		findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, rowExcel('$SearchTipeTransaksi')))
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipetranc'), Keys.chord(Keys.ENTER))
		
	'klik pada button cari'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/button_Cari'))
}

def rowExcel(String cellValue) {
	CustomKeywords.'writetoexcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}
