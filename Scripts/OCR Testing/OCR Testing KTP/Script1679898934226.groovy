import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.testobject.ResponseObject
import java.sql.Driver
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Edit Profile'
int countColumnEdit = findTestData(ExcelPathOCRTesting).getColumnNumbers()

'deklarasi variabel untuk konek ke Database eendigo_dev'
def conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_public'()

'deklarasi koneksi ke Database adins_apiaas_uat'
def connProd = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_uatProduction'()

'panggil fungsi login'
WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'OCR', ('Path'): ExcelPathOCRTesting], 
	FailureHandling.STOP_ON_FAILURE)

'pindah testcase sesuai jumlah di excel'
for(GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++){
	
	'ambil kode tenant di DB'
	String tenantcode = CustomKeywords.'ocrTesting.GetParameterfromDB.getTenantCodefromDB'(conn, 
		findTestData(ExcelPathOCRTesting).getValue(2, 25))
	
	'ambil key trial yang aktif dari DB'
	String thekey = CustomKeywords.'ocrTesting.GetParameterfromDB.getAPIKeyfromDB'(conn, tenantcode)
	
	'deklarasi id untuk harga pembayaran OCR'
	int idPayment = CustomKeywords.'ocrTesting.GetParameterfromDB.getIDPaymentType'(connProd, tenantcode, 'OCR KTP')
	
	'ambil jenis penagihan transaksi (by qty/price)'
	String BalanceChargeType = CustomKeywords.'ocrTesting.GetParameterfromDB.getPaymentType'(connProd, 
		tenantcode, idPayment)
	
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 1).length() == 0) {
		
		break
	} 
	else if (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 1).equalsIgnoreCase('Unexecuted')) {
		
		'deklarasi variable response'
		ResponseObject response
		
		'cek apakah perlu tambah API'
		String useCorrectKey = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 14)
		
		'cek apakah perlu gunakan tenantcode yang salah'
		String useCorrectTenant = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 12)
			
		'deklarasi variabel angka'
		int isSaldoBerkurang, saldobefore, uiSaldoafter, katalonSaldoafter, isTrxIncreased, HitAPITrx
		
		'penanda untuk HIT yang berhasil dan gagal'
		HitAPITrx = 1
		
		'set penanda error menjadi 0'
		GlobalVariable.FlagFailed = 0
		
		'panggil fungsi filter saldo berdasarkan input user'
		filterSaldo()
		
		'cek apakah button skip enable atau disable'
		if(WebUI.verifyElementVisible(findTestObject('Object Repository/API_KEY/Page_Balance/'+
			'i_Catatan_datatable-icon-skip'), FailureHandling.OPTIONAL)){
		
			'klik button skip to last page'
			WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/i_Catatan_datatable-icon-skip'))
		}
		
		'panggil fungsi ambil transaksi terakhir di tabel'
		String no_Trx_before = getTrxNumber()
		
		'variabel yang menyimpan saldo sebelum adanya transaksi'
		saldobefore = getSaldoforTransaction('OCR KTP')
		
		if(useCorrectKey != 'Yes'){
			
			thekey = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 15)
		}
		if(useCorrectTenant != 'Yes'){
			
			tenantcode = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 13)
		}
		
		'lakukan proses HIT api dengan parameter image, key, dan juga tenant'
		response = WS.sendRequest(findTestObject('Object Repository/OCR Testing/OCR KTP', 
		[('img'): findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 8),
		('key'):thekey, 
		('tenant'):tenantcode,
		('nik'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 17),
		('loginId'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 18),
		('refNum'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 19),
		('off_code'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 20),
		('off_name'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 21),
		('question'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 22),
		('source'):findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 23)]))
			
		'ambil message respon dari HIT tersebut'
		message_ocr = WS.getElementPropertyValue(response, 'message')
		
		'ambil status dari respon HIT tersebut'
		state_ocr = WS.getElementPropertyValue(response, 'status')
			
		'jika kurang saldo hentikan proses testing'
		if(state_ocr == 'FAILED' && message_ocr == 'Insufficient balance'){
			
			'write to excel status failed dan reason'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('OCR KTP', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
			message_ocr)
			break;
		}
		//jika status sukses dengan key dan kode tenant yang salah, anggap sebagai bug dan lanjutkan ke tc berikutnya
		else if(state_ocr == 'SUCCESS' && useCorrectKey != 'Yes' && useCorrectTenant != 'Yes'){
			
			'write to excel status failed dan reason'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('OCR KTP', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
			GlobalVariable.FailedReasonKeyTenantBypass)
				
			continue;
		}
		//jika mandatory tidak terpenuhi atau ada error
		else if(message_ocr == 'KTP NOT FOUND' || message_ocr == 'Unexpected Error' 
			|| message_ocr == 'Invalid API key or tenant code'){
			
			'write to excel status failed dan reason'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('OCR KTP', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
			message_ocr)
			continue;
		}
		
		'refresh halaman web'
		WebUI.refresh()
		
		'panggil fungsi filter saldo berdasarkan inputan user'
		filterSaldo()
		
		'cek apakah button skip enable atau disable'
		if(WebUI.verifyElementVisible(findTestObject('Object Repository/API_KEY/Page_Balance/'+
			'i_Catatan_datatable-icon-skip'), FailureHandling.OPTIONAL)){
		
			'klik button skip to last page'
			WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/i_Catatan_datatable-icon-skip'))
		}
		
		'variabel yang diharapkan menyimpan number transaksi sesudah hit'
		String no_Trx_after = getTrxNumber()
		
		'jika user ingin cek ke DB hasil HIT API nya'
		if(GlobalVariable.KondisiCekDB == 'Yes'){
			
			'simpan trx number terbaru dari DB'
			String latestMutation= CustomKeywords.'ocrTesting.GetParameterfromDB.getLatestMutationfromDB'(connProd, tenantcode)
			
			'simpan trx number terbaru milik tenant lain dari DB'
			String latestOtherTenantMutation = CustomKeywords.'ocrTesting.GetParameterfromDB.getNotMyLatestMutationfromDB'(connProd, tenantcode)
			
			'jika data transaction number di web dan DB tidak sesuai'
			if(latestMutation != no_Trx_after || latestMutation == latestOtherTenantMutation){
				
				'anggap HIT Api gagal'
				HitAPITrx = 0
			}
		}
		
		'simpan harga OCR KTP ke dalam integer'
		int serviceprice = CustomKeywords.'ocrTesting.GetParameterfromDB.getServicePricefromDB'(connProd, idPayment)
		
		'jika HIT API successful'
		if(HitAPITrx == 1){
			
			'cek apakah jenis penagihan berdasarkan harga'
			if(BalanceChargeType == 'Price'){
				
				'input saldo setelah penagihan'
				katalonSaldoafter = saldobefore - serviceprice
			}
			else{
				
				'input saldo setelah penagihan dikurangi qty'
				katalonSaldoafter = saldobefore - 1
			}
		}
		
		'simpan saldo setelah di HIT'
		uiSaldoafter = getSaldoforTransaction('OCR KTP')
		
		'jika saldoafter match'
		if(katalonSaldoafter == uiSaldoafter){
			
			isSaldoBerkurang = 1
		}
		else{
			
			isSaldoBerkurang = 0
		}
		
		'jika transaksi bertambah di DB dan di web'
		if(no_Trx_after > no_Trx_before){
			
			'web mencatat transaksi terbaru'
			isTrxIncreased = 1
		}
		else{
			
			'web tidak mencatat transaksi terbaru'
			isTrxIncreased = 0
		}
		
		'jika tidak ada message error dan kondisi lain terpenuhi'
		if(message_ocr == '' && state_ocr == 'SUCCESS' && isTrxIncreased == 1 
			&& isSaldoBerkurang == 1 && HitAPITrx == 1){
			
			'tulis status sukses pada excel'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('OCR KTP', 
				GlobalVariable.NumOfColumn, GlobalVariable.StatusSuccess,
					GlobalVariable.SuccessReason)
		
		}
		//kondisi jika transaksi berhasil tapi tidak tercatat/tersimpan di DB
		else if(state_ocr == 'SUCCESS' && isTrxIncreased == 0 && isSaldoBerkurang == 1){
			
			GlobalVariable.FlagFailed = 1
			'tulis kondisi gagal'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('OCR KTP', 
				GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
					GlobalVariable.FailedReasonTrxNotinDB)
		}
		//kondisi jika transaksi berhasil tapi saldo tidak berkurang
		else if(state_ocr == 'SUCCESS' && isTrxIncreased == 1 && isSaldoBerkurang == 0){
			
			GlobalVariable.FlagFailed = 1
			'tulis kondisi gagal'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('OCR KTP', 
				GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
					GlobalVariable.FailedReasonBalanceNotChange)
		}
		//kondisi transaksi tidak tampil dan tidak tersimpan di DB
		else if(HitAPITrx == 0 && state_ocr == 'FAILED' && isTrxIncreased == 0 && isSaldoBerkurang == 1){
			
			GlobalVariable.FlagFailed = 1
			'tulis kondisi gagal'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('OCR KTP', 
				GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
					GlobalVariable.FailedReasonSaldoBocor)
		}
		else{
			
			GlobalVariable.FlagFailed = 1
			'write to excel status failed dan reason'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('OCR KTP', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
			message_ocr)
		}
		'refresh halaman web'
		WebUI.refresh()
	}
}

'tutup browser jika loop sudah selesai'
WebUI.closeBrowser()

'ambil saldo sesuai testing yang dilakukan'
def getSaldoforTransaction(String NamaOCR) {
	
	'deklarasi jumlah saldo sekarang'
	int saldoNow
	
	'cari element dengan nama saldo'
	def elementNamaSaldo = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance-prod > div.row.match-height > div > lib-balance-summary > div > div'))
	
	'lakukan loop untuk cari nama saldo yang ditentukan'
	for(int i=1; i<=elementNamaSaldo.size(); i++){
		
		'cari nama saldo yang sesuai di list saldo'
		def modifyNamaSaldo = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Balance/span_OCR KK'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[1]/div/lib-balance-summary/div/div["+ (i) +"]/div/div/div/div/div[1]/span", true)

		'jika nama object sesuai dengan nama saldo'
		if(WebUI.getText(modifyNamaSaldo) == NamaOCR){
			
			'ubah alamat jumlah saldo ke kotak saldo yang dipilih'
			def modifySaldoDipilih = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Balance/h3_4,988'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[1]/div/lib-balance-summary/div/div["+ (i) +"]/div/div/div/div/div[1]/h3", true)
			
			'simpan jumlah saldo sekarang di variabel'
			 saldoNow = Integer.parseInt(WebUI.getText(modifySaldoDipilih).replace(',',''))
		}
	}
	'pakai saldo IDR jika lainnya tidak ada'
	if(saldoNow == 0){
		
		'simpan jumlah saldo sekarang di variabel'
		saldoNow = Integer.parseInt(WebUI.getText(findTestObject('Object Repository/API_KEY/Page_Balance/h3_4,988')).replace(',',''))
	}
	'kembalikan nilai saldo sekarang'
	return saldoNow
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

'fungsi untuk filter saldo berdasarkan input user'
def filterSaldo() {
	'tunggu webpage load'
	WebUI.delay(4)
	
	'isi field input tipe saldo'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'), 
		findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 9))
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'), Keys.chord(Keys.ENTER))
	
	'isi field tipe transaksi'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipetranc'), 
		findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 10))
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipetranc'), Keys.chord(Keys.ENTER))
		
	'klik pada button cari'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/button_Cari'))
}