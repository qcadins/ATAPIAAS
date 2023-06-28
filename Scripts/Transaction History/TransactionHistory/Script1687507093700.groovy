import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.By as By

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet User'
int countColumnEdit = findTestData(ExcelPathTranx).getColumnNumbers()

'deklarasi koneksi ke Database adins_apiaas_uat'
Connection conndev = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_esign'()

'deklarasi koneksi ke database eendigo_dev_uat'
Connection conndevUAT = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_devUat'()

for (GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++) {
	
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 1).length() == 0) {
		
		break
	}
	else if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 1).equalsIgnoreCase('Unexecuted') ||
		findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 1).equalsIgnoreCase('Warning')) {
		
		'panggil fungsi login'
		WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'TranxHist', ('SheetName') : 'RiwayatTransaksi',
				('Path') : ExcelPathTranx], FailureHandling.STOP_ON_FAILURE)
		
		'klik pada menu'
		WebUI.click(
			findTestObject('Object Repository/TransactionHistory/Page_Balance/span_Menu'))
		
		'pilih submenu riwayat transaksi'
		WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_Balance/RiwayatTrxMenu'))
		
		checkPaging(conndev)
		
		break
	}
}

for (GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++) {
	
	'deklarasi variable integer'
	int arrayIndex = 0
	
	'set penanda error menjadi 0'
	GlobalVariable.FlagFailed = 0
		
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 1).length() == 0) {
		
		break
	}
	else if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 1).equalsIgnoreCase('Unexecuted') ||
		findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 1).equalsIgnoreCase('Warning')) {
		
		'angka untuk menghitung data mandatory yang tidak terpenuhi'
		int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 5))
		
		'klik pada menu'
		WebUI.click(
			findTestObject('Object Repository/TransactionHistory/Page_Balance/span_Menu'))
		
		'pilih submenu riwayat transaksi'
		WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_Balance/RiwayatTrxMenu'))
		
		'cek apakah tombol menu dalam jangkauan web'
		if (WebUI.verifyElementVisible(findTestObject(TombolSilang), FailureHandling.OPTIONAL)) {
			
			'klik pada tombol silang menu'
			WebUI.click(findTestObject(TombolSilang))
		}
		
		checkddlTipeIsiUlang(conndev)
		
		checkddlStatus(conndev)
		
		checkddlMetodeTrf(conndev)
		
		'ambil role yang digunakan oleh user'
		String RoleUser = CustomKeywords.'transactionHistory.TransactionVerif.getRoleofUser'(conndev,
			findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 9))
		
		'cek apakah role adminclient/admineendigo/adminfinance'
		if (RoleUser.equalsIgnoreCase('Admin Client')) {
			
			'verify object yang muncul sesuai dengan role admin client'
			checkVerifyPresent(WebUI.verifyElementPresent(
				findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/viewDetail_Client'),
					GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Adm.Client-View Detail')
			
			'verify object yang muncul sesuai dengan role admin client'
			checkVerifyPresent(WebUI.verifyElementPresent(
				findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/view_UploadBukti'),
					GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Adm.Client-Upload Bukti')
			
			'verify object yang muncul sesuai dengan role admin client'
			checkVerifyPresent(WebUI.verifyElementPresent(
				findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/view_Bukti'),
					GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Adm.Client-Bukti')
			
			'verify object yang muncul sesuai dengan role admin client'
			checkVerifyNotPresent(WebUI.verifyElementPresent(
				findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputTenant'),
					GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Adm.Client-Tenant')
			
			'verify object yang muncul sesuai dengan role admin client'
			checkVerifyNotPresent(WebUI.verifyElementPresent(
				findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/konfirmasiBayarbutton'),
					GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Adm.Client-Accept Payment')
			
			'verify object yang muncul sesuai dengan role admin client'
			checkVerifyNotPresent(WebUI.verifyElementPresent(
				findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/rejectBayarButton'),
					GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Adm.Client-Reject Payment')
			
			'verify object yang muncul sesuai dengan role admin client'
			checkVerifyPresent(WebUI.verifyElementPresent(
				findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/viewNPWP_Client'),
					GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Adm.Client-NPWP')
			
			'masukkan data ke filter status'
			WebUI.setText(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputStatus'),
				findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 16))
			
			'enter pada filter status'
			WebUI.sendKeys(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputStatus'),
				Keys.chord(Keys.ENTER))
			
			'klik pada tombol cari'
			WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/button_Cari'))
			
			'modifikasi alamat object trxnumber'
			def modifytrxnumber = WebUI.modifyObjectProperty(findTestObject('Object Repository/TransactionHistory/modifyObject'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[1]/datatable-body-row/div[2]/datatable-body-cell[1]/div/p", true)
			
			'ambil trx num'
			String trxNum = WebUI.getText(modifytrxnumber)
			
			'cek apakah perlu view dan verif detail'
			if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 23) == 'Yes') {
				
				functionDetail(conndev, trxNum)
			}
			
			'cek apakah perlu view dan verif npwp'
			if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 24) == 'Yes') {
				
				functionviewverifNPWP(conndev)
			}
			
			'cek apakah perlu upload bukti pembayaran'
			if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 20) == 'Yes') {
				
				'klik untuk bagian unggah pembayaran'
				WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/view_UploadBukti'))
				
				'ambil data nomor transaksi'
				String noTrx = WebUI.getText(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/noTrxReceipt'),
					FailureHandling.OPTIONAL)
				
				'hilangkan string yang tidak diperlukan'
				String noTrxKatalon = noTrx.substring(noTrx.indexOf('Nomor Transaksi Anda : ') + 'Nomor Transaksi Anda : '.length())
	
				'verify layanan detail transaksi tabel == detail'
				checkVerifyEqualorMatch(WebUI.verifyMatch(noTrxKatalon, trxNum, false, FailureHandling.CONTINUE_ON_FAILURE), 'No.Trx Detail tidak sesuai')
				
				'klik unggah foto bukti pembayaran'
				WebUI.uploadFile(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/uploadBukti_Browse'),
					findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 21), FailureHandling.OPTIONAL)
				
				'klik batal'
				WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/buttonBatal_upload'))
				
				'klik untuk bagian unggah pembayaran'
				WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/view_UploadBukti'))

				'klik unggah foto bukti pembayaran'
				WebUI.uploadFile(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/uploadBukti_Browse'),
					findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 21), FailureHandling.OPTIONAL)
				
				'klik simpan'
				WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/buttonSimpan_upload'))
				
				'verify layanan detail transaksi ui = db'
				checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/div_Success_upload')), 
					'Success', false, FailureHandling.CONTINUE_ON_FAILURE), 'Upload Gagal')
				
				'klik ok pada popup'
				WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/button_OK_upload'))
				
				'cek apakah kondisi cek storeDB aktif'
				if (GlobalVariable.KondisiCekDB == 'Yes') {
					
					'panggil fungsi storeDB'
					WebUI.callTestCase(findTestCase('Test Cases/Top Up/TransactionStoreDB'), [('Path') : ExcelPathTranx,
						('TrxType') : 'Upload', ('TrxNum') : trxNum],
						 FailureHandling.CONTINUE_ON_FAILURE)
				}
			}
		}
		else if (RoleUser.equalsIgnoreCase('Admin Finance Eendigo')) {
			
			'check ddl tenant khusus untuk role selain admin client'
			checkddlTenant(conndev)
			
			searchadminEendigoFinance()
			
			'verify object yang muncul sesuai dengan role admin finance'
			checkVerifyPresent(WebUI.verifyElementPresent(
				findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputTenant'),
					GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Adm.Finance-Tenant')
			
			'verify object yang muncul sesuai dengan role admin finance'
			checkVerifyPresent(WebUI.verifyElementPresent(
				findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/viewDetail_Client'),
					GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Adm.Finance-View Detail')
			
			'verify object yang muncul sesuai dengan role admin finance'
			checkVerifyNotPresent(WebUI.verifyElementPresent(
				findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/view_UploadBukti'),
					GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Adm.Finance-Upload Bukti')
			
			'verify object yang muncul sesuai dengan role admin finance'
			checkVerifyPresent(WebUI.verifyElementPresent(
				findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/view_Bukti'),
					GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Adm.Finance-Bukti')
			
			'verify object yang muncul sesuai dengan role admin finance'
			checkVerifyPresent(WebUI.verifyElementPresent(
				findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/konfirmasiBayarbutton'),
					GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Adm.Finance-Accept Payment')
			
			'verify object yang muncul sesuai dengan role admin finance'
			checkVerifyPresent(WebUI.verifyElementPresent(
				findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/rejectBayarButton'),
					GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Adm.Finance-Reject Payment')
			
			'verify object yang muncul sesuai dengan role admin finance'
			checkVerifyPresent(WebUI.verifyElementPresent(
				findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/viewNPWP_Client'),
					GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Adm.Finance-NPWP')
			
			'modifikasi alamat object trxnumber'
			def modifytrxnumber = WebUI.modifyObjectProperty(findTestObject('Object Repository/TransactionHistory/modifyObject'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[1]/datatable-body-row/div[2]/datatable-body-cell[1]/div/p", true)
			
			'ambil trx num'
			String trxNum = WebUI.getText(modifytrxnumber)
			
			'cek apakah perlu reject pembayaran'
			if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 27) == 'Yes') {
				
				confRejectPayment('Reject', conndev, trxNum)
				
				searchadminEendigoFinance()
				
				'modifikasi alamat object trxnumber'
				modifytrxnumber = WebUI.modifyObjectProperty(findTestObject('Object Repository/TransactionHistory/modifyObject'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[1]/datatable-body-row/div[2]/datatable-body-cell[1]/div/p", true)
				
				'ambil trx num'
				trxNum = WebUI.getText(modifytrxnumber)
			}
			
			'cek apakah perlu view dan verif detail'
			if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 23) == 'Yes') {
				
				functionDetail(conndev, trxNum)
			}
			
			'cek apakah perlu view dan verif npwp'
			if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 24) == 'Yes') {
				
				functionviewverifNPWP(conndev)
			}
			
			'cek apakah perlu view bukti pembayaran'
			if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 25) == 'Yes') {
				
				'klik pada view bukti pembayaran'
				WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/view_Bukti'))
				
				'klik pada tombol X'
				WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/viewbukti_X'))
			}
			
			'cek apakah perlu approve pembayaran'
			if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 26) == 'Yes') {
				
				confRejectPayment('Approve', conndev, trxNum)
			}
		}
		else if (RoleUser.equalsIgnoreCase('Admin Eendigo')) {
			
			'check ddl tenant khusus untuk role selain admin client'
			checkddlTenant(conndev)
			
			'verify object yang muncul sesuai dengan role admin eendigo'
			checkVerifyPresent(WebUI.verifyElementPresent(
				findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputTenant'),
					GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Adm.Eendigo-Tenant')
			
			'verify object yang muncul sesuai dengan role admin eendigo'
			checkVerifyPresent(WebUI.verifyElementPresent(
				findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/viewDetail_Client'),
					GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Adm.Eendigo-View Detail')
			
			'verify object yang muncul sesuai dengan role admin eendigo'
			checkVerifyNotPresent(WebUI.verifyElementPresent(
				findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/view_UploadBukti'),
					GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Adm.Eendigo-Upload Bukti')
			
			'verify object yang muncul sesuai dengan role admin eendigo'
			checkVerifyNotPresent(WebUI.verifyElementPresent(
				findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/view_Bukti'),
					GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Adm.Eendigo-Bukti')
			
			'verify object yang muncul sesuai dengan role admin eendigo'
			checkVerifyNotPresent(WebUI.verifyElementPresent(
				findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/konfirmasiBayarbutton'),
					GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Adm.Eendigo-Accept Payment')
			
			'verify object yang muncul sesuai dengan role admin eendigo'
			checkVerifyNotPresent(WebUI.verifyElementPresent(
				findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/rejectBayarButton'),
					GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Adm.Eendigo-Reject Payment')
			
			'verify object yang muncul sesuai dengan role admin eendigo'
			checkVerifyPresent(WebUI.verifyElementPresent(
				findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/viewNPWP_Client'),
					GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Adm.Eendigo-NPWP')
			
			searchadminEendigoFinance()
			
			'modifikasi alamat object trxnumber'
			def modifytrxnumber = WebUI.modifyObjectProperty(findTestObject('Object Repository/TransactionHistory/modifyObject'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[1]/datatable-body-row/div[2]/datatable-body-cell[1]/div/p", true)
			
			'ambil trx num'
			String trxNum = WebUI.getText(modifytrxnumber)
			
			'cek apakah perlu view dan verif detail'
			if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 23) == 'Yes') {
				
				functionDetail(conndev, trxNum)
			}
			
			'cek apakah perlu view dan verif npwp'
			if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 24) == 'Yes') {
				
				functionviewverifNPWP(conndev)
			}
		}
		
		'jika tidak ada error tulis sukses'
		if(GlobalVariable.FlagFailed == 0 && isMandatoryComplete == 0) {
			'write to excel success'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'RiwayatTransaksi', 0,
				GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
		}
		
		'klik pada profile'
		WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_Balance/span_profile'))
		
		'klik pada logout'
		WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_Balance/span_Logout'))
		
		'input data email'
		WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
			'input_Buat Akun_form-control ng-untouched n_ab9ed8'),
			findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn+1, 9))
		
		'input password'
		WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
			'input_Buat Akun_form-control ng-untouched n_dd86a2'),
			findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn+1, 10))
		
		'ceklis pada reCaptcha'
		WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/'+
			'div_reCAPTCHA_recaptcha-checkbox-border (4)'))
		
		'pada delay, lakukan captcha secara manual'
		WebUI.delay(10)
		
		'klik pada button login'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/'+
			'button_Lanjutkan Perjalanan Anda'))
	}
}

WebUI.closeBrowser()

def functionviewverifNPWP(Connection conndev) {
	
	'klik pada view npwp'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/viewNPWP_Client'))
	
	'get value dari nomor NPWP'
	String npwpUI = WebUI.getAttribute(
		findTestObject('Object Repository/TransactionHistory/npwpNum'), 'value', FailureHandling.OPTIONAL)
	
	'ambil npwp dari DB'
	String npwpDB = CustomKeywords.'transactionHistory.TransactionVerif.getNPWPnumUser'(conndev,
		findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 18))

	'verify npwp dari DB dan UI sesuai'
	checkVerifyEqualorMatch(WebUI.verifyMatch(npwpUI,
		npwpDB, false, FailureHandling.CONTINUE_ON_FAILURE), 'NPWP Number')
	
	'klik silang'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/npwp_X'))
}

def confRejectPayment(String choice, Connection conndev, String trxNum) {
	
	String String1, String2
	
	'deklarasi string yang perlu diubah'
	if (choice == 'Approve') {
		
		String1 = 'Accept Pembayaran'
		String2 = 'konfirmasiBayarbutton'
	}
	else {
		
		String1 = 'Reject pembayaran'
		String2 = 'rejectBayarButton'
	}
	
	'ambil list service '
	ArrayList serviceActive = CustomKeywords.'transactionHistory.TransactionVerif.getServiceCheck'(conndev, trxNum)
	
	'ambil harga dari service yang dituju'
	ArrayList serviceSaldobefore = [], serviceSaldoafter = []
	
	'klik pada menu'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_Balance/span_Menu'))
	
	'klik ke menu saldo'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_Balance/SaldoMenu'))
	
	'looping untuk tambahkan saldo ke array'
	for(int i = 0 ; i < serviceActive.size; i++) {
		
		serviceSaldobefore.add(getSaldoforTransaction(serviceActive[i]))
	}
	
	searchadminEendigoFinance()
	
	'klik pada tombol approve pembayaran'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/'+ String2 +''))
	
	'klik pada tombol batal'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/button_Tidak, batalkan'))
	
	'klik pada tombol approve pembayaran'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/'+ String2 +''))
	
	'klik pada tombol ya'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/button_YaKonfirmReject'))
	
	'cek apakah tombol ok muncul'
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/button_OK_upload'),
		GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
	
		'ambil text notif'
		String notifText = WebUI.getText(
			findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/div_Success_upload'))
	
		'verify notifikasi sukses atau tidak'
		checkVerifyEqualorMatch(WebUI.verifyMatch(notifText,
			'Success', false, FailureHandling.CONTINUE_ON_FAILURE), String1)
		
		'klik tombol OK'
		WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/button_OK_upload'))
		
		'jika notif text success'
		if (notifText == 'Success') {
			
			'klik pada menu'
			WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_Balance/span_Menu'))
	
			'klik ke menu saldo'
			WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_Balance/SaldoMenu'))
			
			'looping untuk tambahkan saldo ke array'
			for(int i = 0 ; i < serviceActive.size; i++) {
				
				serviceSaldoafter.add(getSaldoforTransaction(serviceActive[i]))
			}
			
			'loop untuk verifikasi data'
			for (int i = 0; i < serviceActive.size; i++) {
				
				if (choice == 'Approve') {
					
					'verify notifikasi sukses atau tidak'
					checkVerifyEqualorMatch(WebUI.verifyMatch(serviceSaldobefore[i],
						serviceSaldoafter[i], false, FailureHandling.CONTINUE_ON_FAILURE), 'Saldo tidak Berubah')
				}
				else {
					
					'verify notifikasi sukses atau tidak'
					checkVerifyNotEqualorMatch(WebUI.verifyMatch(serviceSaldobefore[i],
						serviceSaldoafter[i], false, FailureHandling.CONTINUE_ON_FAILURE), 'Saldo Berubah')
				}
			}
			
			'cek apakah kondisi cek storeDB aktif'
			if (GlobalVariable.KondisiCekDB == 'Yes') {
				
				'panggil fungsi storeDB'
				WebUI.callTestCase(findTestCase('Test Cases/Top Up/TransactionStoreDB'), [('Path') : ExcelPathTranx,
					('TrxType') : choice, ('TrxNum') : trxNum],
					 FailureHandling.CONTINUE_ON_FAILURE)
			}
		}
	}
	else {
		
		GlobalVariable.FlagFailed = 1
		
		'tulis adanya error saat melakukan approval'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('RiwayatTransaksi', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
				WebUI.getText(
					findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/div_errorCatch')).toString())
	}
}

def searchadminEendigoFinance() {
	
	'klik pada menu'
	WebUI.click(
		findTestObject('Object Repository/TransactionHistory/Page_Balance/span_Menu'))
	
	'pilih submenu riwayat transaksi'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_Balance/RiwayatTrxMenu'))
	
	'cek apakah tombol menu dalam jangkauan web'
	if (WebUI.verifyElementVisible(findTestObject(TombolSilang), FailureHandling.OPTIONAL)) {
		
		'klik pada tombol silang menu'
		WebUI.click(findTestObject(TombolSilang))
	}
	
	'masukkan data ke filter status'
	WebUI.setText(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputStatus'),
		findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 16))
	
	'enter pada filter status'
	WebUI.sendKeys(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputStatus'),
		Keys.chord(Keys.ENTER))
	
	'masukkan data ke filter tenant'
	WebUI.setText(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputTenant'),
		findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 18))
	
	'enter pada filter tenant'
	WebUI.sendKeys(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputTenant'),
		Keys.chord(Keys.ENTER))
	
	'klik pada tombol cari'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/button_Cari'))
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
			def modifySaldoDipilih = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Balance/h3_4,988'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[1]/div/lib-balance-summary/div/div["+ (i) +"]/div/div/div/div/div[1]/h3", true)
			
			'simpan jumlah saldo sekarang di variabel'
			 saldoNow = Integer.parseInt(WebUI.getText(modifySaldoDipilih).replace(',',''))
			 
			 break;
		}
	}
	'pakai saldo IDR jika lainnya tidak ada'
	if (saldoNow == 0) {
		
		'simpan jumlah saldo sekarang di variabel'
		saldoNow = Integer.parseInt(WebUI.getText(findTestObject('Object Repository/API_KEY/Page_Balance/h3_4,988')).replace(',',''))
	}
	'kembalikan nilai saldo sekarang'
	return saldoNow
}

def functionDetail(Connection conndev, String trxNum) {
	'klik pada tombol detail'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/viewDetail_Client'))
	
	'ambil data table dari db'
	ArrayList result = CustomKeywords.'transactionHistory.TransactionVerif.getRiwayatDetail'(conndev, trxNum)
	
	'ambil alamat trxnumber'
	def variabledetail = DriverFactory.getWebDriver().findElements(By.cssSelector('body > ngb-modal-window > div > div > app-transaction-history-detail > div > div.modal-body > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))
	
	'arraylist untuk tampung detail'
	ArrayList detail = []
	
	'lakukan loop untuk ambil data detail'
	for (int i = 1; i <= variabledetail.size(); i++) {
		
		'modifikasi object layanan transaksi'
		def modifylayanandetail = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/Page_List Transaction History/layananDetail'),'xpath','equals', "/html/body/ngb-modal-window/div/div/app-transaction-history-detail/div/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[" + i + "]/datatable-body-row/div[2]/datatable-body-cell[1]/div/p", true)
	
		'modifikasi object unit price transaksi'
		def modifyunitpricedetail = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/Page_List Transaction History/unitpriceDetail'),'xpath','equals', "/html/body/ngb-modal-window/div/div/app-transaction-history-detail/div/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[" + i + "]/datatable-body-row/div[2]/datatable-body-cell[2]/div", true)

		'modifikasi object jumlah transaksi'
		def modifyjumlahdetail = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/Page_List Transaction History/jumlahDetail'),'xpath','equals', "/html/body/ngb-modal-window/div/div/app-transaction-history-detail/div/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[" + i + "]/datatable-body-row/div[2]/datatable-body-cell[3]/div", true)

		'modifikasi object subtotal transaksi'
		def modifysubtotaldetail = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/Page_List Transaction History/subtotalDetail'),'xpath','equals', "/html/body/ngb-modal-window/div/div/app-transaction-history-detail/div/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[" + i + "]/datatable-body-row/div[2]/datatable-body-cell[4]/div", true)
		
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

def checkPaging(Connection conndev) {
	
	'input batas awal transaksi'
	WebUI.setText(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/input_startDate'),
			findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 13))
	
	'input batas akhir transaksi'
	WebUI.setText(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/input_endDate'),
			findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 14))
	
	'input tipe isi ulang'
	WebUI.setText(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputTipeIsiUlang'),
			findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 15))
	
	'enter pada tipe isi ulang'
	WebUI.sendKeys(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputTipeIsiUlang'),
		Keys.chord(Keys.ENTER))
	
	'input metode bayar'
	WebUI.setText(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputMetodeBayar'),
			findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 17))
	
	'enter pada metode bayar'
	WebUI.sendKeys(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputMetodeBayar'),
		Keys.chord(Keys.ENTER))
	
	'input status pembayaran'
	WebUI.setText(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputStatus'),
			findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 16))
	
	'enter pada status pembayaran'
	WebUI.sendKeys(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputStatus'),
		Keys.chord(Keys.ENTER))
	
	'cek apakah dropdown tenant muncul'
	if (WebUI.verifyElementPresent(
		findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputTenant'),
		GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
	
		'input tenant yang dituju'
		WebUI.setText(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputTenant'),
			findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 18))
	
		'enter pada tenant'
		WebUI.sendKeys(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputTenant'),
			Keys.chord(Keys.ENTER))
	}
	
	'klik pada tombol cari'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/button_Cari'))
	
	'cek apakah hasil search gagal'
	if(WebUI.verifyElementPresent(findTestObject('Object Repository/Coupon/Page_List Coupon/searchResult')
		, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		
		GlobalVariable.FlagFailed = 1
		
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('RiwayatTransaksi', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 2) +
		';') + GlobalVariable.FailedReasonSearchFailed)
	
	}
	
	'klik pada button set ulang'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/button_Set Ulang'))
	
	'verify field ke reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/input_startDate'),
			'value', FailureHandling.CONTINUE_ON_FAILURE),'', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field ke reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/input_endDate'),
			'value', FailureHandling.CONTINUE_ON_FAILURE),'', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field ke reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputTipeIsiUlang'),
			'value', FailureHandling.CONTINUE_ON_FAILURE),'', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field ke reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputMetodeBayar'),
			'value', FailureHandling.CONTINUE_ON_FAILURE),'', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field ke reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputStatus'),
			'value', FailureHandling.CONTINUE_ON_FAILURE),'', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'cek apakah dropdown tenant muncul'
	if (WebUI.verifyElementPresent(
		findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputTenant'),
		GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
	
		'verify field ke reset'
		checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputTenant'),
				'value', FailureHandling.CONTINUE_ON_FAILURE),'', false, FailureHandling.CONTINUE_ON_FAILURE))
	}
	
	'cek apakah dropdown tenant muncul'
	if (WebUI.verifyElementPresent(
		findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputTenant'),
		GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
	
		'input tenant yang dituju'
		WebUI.setText(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputTenant'),
			findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 18))
	
		'enter pada tenant'
		WebUI.sendKeys(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputTenant'),
			Keys.chord(Keys.ENTER))
	}
	
	'klik pada tombol cari'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/button_Cari'))
	
	'cek apakah hasil search gagal'
	if(WebUI.verifyElementPresent(findTestObject('Object Repository/Coupon/Page_List Coupon/searchResult')
		, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		
		GlobalVariable.FlagFailed = 1
		
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('RiwayatTransaksi', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 2) +
		';') + GlobalVariable.FailedReasonSearchFailed)
	
	}
	
	'cari button skip di footer'
	def elementbutton = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout >'+
		' div > div.main-panel > div > div.content-wrapper > app-list-transaction-history > app-msx-paging >'+
		' app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))
	
	'ambil banyaknya laman footer'
	int lastPage = elementbutton.size()
	
	'get text total data dari ui'
	Total = WebUI.getText(findTestObject('Object Repository/TransactionHistory/TotalData')).split(' ')
	
	'ambil total data dari db'
	int resultTotalData = CustomKeywords.'transactionHistory.TransactionVerif.getTotalTrx'(conndev,
		findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 9))

//	'verify total data role'
//	checkVerifyPaging(WebUI.verifyEqual(resultTotalData, Integer.parseInt(Total[0]), FailureHandling.CONTINUE_ON_FAILURE))
	
	'cek apakah hlm  tersedia'
	if(WebUI.verifyElementVisible(
		findTestObject('Object Repository/TransactionHistory/i_Action_datatable-icon-skip'),
		FailureHandling.OPTIONAL) == true) {
	
		'klik halaman 2'
		WebUI.click(findTestObject('Object Repository/TransactionHistory/Page2'))
		
		'verify paging di page 2'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/TransactionHistory/Page2'),
				'class', FailureHandling.CONTINUE_ON_FAILURE),
					'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'klik halaman 1'
		WebUI.click(findTestObject('Object Repository/TransactionHistory/Page1'))
		
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/TransactionHistory/Page1'),
				'class', FailureHandling.CONTINUE_ON_FAILURE),
					'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'klik button next page'
		WebUI.click(findTestObject('Object Repository/TransactionHistory/i_Action_datatable-icon-right'))
		
		'verify paging di page 2'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/TransactionHistory/Page2'),
				'class', FailureHandling.CONTINUE_ON_FAILURE),
					'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'klik prev page'
		WebUI.click(findTestObject('Object Repository/TransactionHistory/i_Action_datatable-icon-prev'))
		
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/TransactionHistory/Page1'),
				'class', FailureHandling.CONTINUE_ON_FAILURE),
					'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'klik pada tombol skip'
		WebUI.click(findTestObject('Object Repository/TransactionHistory/i_Action_datatable-icon-skip'))
		
		'modify object laman terakhir'
		def modifyObjectmaxPage = WebUI.modifyObjectProperty(
			findTestObject('Object Repository/TransactionHistory/modifyObject'),
			'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/"+
				"app-msx-paging/app-msx-datatable/section/ngx-datatable/div/"+
					"datatable-footer/div/datatable-pager/ul/li["+ (lastPage - 2) +"]", true)
		
		'verify paging di page terakhir'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(modifyObjectmaxPage,
			'class', FailureHandling.CONTINUE_ON_FAILURE), 'pages active ng-star-inserted',
				false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'click min page'
		WebUI.click(findTestObject('Object Repository/TransactionHistory/i_Action_datatable-icon-prev'))
		
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/TransactionHistory/Page1'),
				'class', FailureHandling.CONTINUE_ON_FAILURE),
					'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
	}
}

def checkddlTipeIsiUlang(Connection conndev) {
	
	'klik pada ddl tipe isi ulang'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/span_TipeIsiUlang'))
	
	'ambil list tipe isi ulang'
	def elementtipeisiulang = DriverFactory.getWebDriver().findElements(By.xpath('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-search-filter/div/div/div/div/div/form/div[1]/div[3]/app-question/app-select/div/div[2]/ng-select/ng-dropdown-panel/div/div[2]/div'))
	
	'ambil hitungan TipeIsiUlang yang ada'
	int countWeb = (elementtipeisiulang.size()) - 1
	
	'flag TipeIsiUlang sesuai'
	int isTipeIsiUlangFound = 0
	
	'ambil nama TipeIsiUlang dari DB'
	ArrayList namaTipeIsiUlangDB = CustomKeywords.'transactionHistory.TransactionVerif.getDDLTipeIsiUlang'(conndev)
	
	'nama-nama tipe saldo sedang aktif dari UI'
	ArrayList namaTipeIsiUlangUI = []
	
	'hitung banyak data didalam array DB'
	int countDB = namaTipeIsiUlangDB.size()
	
	'jika hitungan di UI dan DB sesuai'
	if(countWeb == countDB){
		
		for (int i=1; i<=countWeb; i++) {
			
			'ambil object dari ddl'
			def modifyNamaTipeIsiUlang = WebUI.modifyObjectProperty(findTestObject('Object Repository/TransactionHistory/modifyObject'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-search-filter/div/div/div/div/div/form/div[1]/div[3]/app-question/app-select/div/div[2]/ng-select/ng-dropdown-panel/div/div[2]/div["+(i+1)+"]/span", true)
			
			'tambahkan nama tipe saldo ke array'
			String data = WebUI.getText(modifyNamaTipeIsiUlang)
			namaTipeIsiUlangUI.add(data)
		}
		
		'cek setiap data di UI dengan data di DB sebagai pembanding'
		for (String tipe : namaTipeIsiUlangDB) {
			
			'jika ada data yang tidak terdapat pada arraylist yang lain'
			if (!namaTipeIsiUlangUI.contains(tipe)) {
				
				'ada data yang tidak match'
				isTipeIsiUlangFound = 0;
				'berhentikan loop'
				break;
			}
			'kondisi ini bisa ditemui jika data match'
			isTipeIsiUlangFound = 1
		}
			
	}
	else if (isTipeIsiUlangFound == 0 || countWeb != countDB) {
		
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('RiwayatTransaksi', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonDDL)
	}
	
	'klik pada ddl tipe isi ulang'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/span_TipeIsiUlang'))
}

def checkddlStatus(Connection conndev) {
	
	'klik pada ddl status'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/span_Status'))
	
	'ambil list status'
	def elementstatusddl = DriverFactory.getWebDriver().findElements(By.xpath('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-search-filter/div/div/div/div/div/form/div[1]/div[5]/app-question/app-select/div/div[2]/ng-select/ng-dropdown-panel/div/div[2]/div'))
	
	'ambil hitungan Status yang ada'
	int countWeb = (elementstatusddl.size()) - 1
	
	'flag Status sesuai'
	int isStatusFound = 0
	
	'ambil nama Status dari DB'
	ArrayList<String> namaStatusExcel = []
	
	'nama-nama tipe saldo sedang aktif dari UI'
	ArrayList<String> namaStatusUI = []
	
	'ambil data status dari excel, karena tidak disimpan ke DB'
	for (int i = 0; i < 5; i++) {
		
		'tambah data ke array excel'
		namaStatusExcel.add(findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, (30+i)))
	}
	
	'hitung banyak data didalam array DB'
	int countExcel = namaStatusExcel.size()
	
	for (int i = 1; i<=countExcel; i++) {
		
		'ambil object dari ddl'
		def modifyNamaStatus = WebUI.modifyObjectProperty(findTestObject('Object Repository/TransactionHistory/modifyObject'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-search-filter/div/div/div/div/div/form/div[1]/div[5]/app-question/app-select/div/div[2]/ng-select/ng-dropdown-panel/div/div[2]/div["+(i+1)+"]/span", true)
		
		'tambahkan nama tipe saldo ke array'
		String data = WebUI.getText(modifyNamaStatus)
		namaStatusUI.add(data)
	}
	
	'jika hitungan di UI dan DB sesuai'
	if(countExcel == countWeb){
		
		'cek setiap data di UI dengan data di DB sebagai pembanding'
		for (String tipe : namaStatusExcel) {
			
			'jika ada data yang tidak terdapat pada arraylist yang lain'
			if (!namaStatusUI.contains(tipe)) {
				
				'ada data yang tidak match'
				isStatusFound = 0;
				'berhentikan loop'
				break;
			}
			'kondisi ini bisa ditemui jika data match'
			isStatusFound = 1
		}
			
	}
	else if (isStatusFound == 0 || countWeb != countExcel) {
		
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('RiwayatTransaksi', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonDDL)
	}
	
	'klik pada ddl status'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/span_Status'))
}

def checkddlMetodeTrf(Connection conndev) {
	
	'klik pada ddl metode bayar'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/span_Metode'))
	
	'ambil list metode bayar'
	def elementmetodeBayar = DriverFactory.getWebDriver().findElements(By.xpath('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-search-filter/div/div/div/div/div/form/div[1]/div[4]/app-question/app-select/div/div[2]/ng-select/ng-dropdown-panel/div/div[2]/div'))
	
	'ambil hitungan metodeBayar yang ada'
	int countWeb = (elementmetodeBayar.size()) - 1
	
	'flag metodeBayar sesuai'
	int ismetodeBayarFound = 0
	
	'ambil nama metodeBayar dari DB'
	ArrayList namametodeBayarDB = CustomKeywords.'transactionHistory.TransactionVerif.getDDLMetodeTrf'(conndev)
	
	'nama-nama tipe saldo sedang aktif dari UI'
	ArrayList namametodeBayarUI = []
	
	'hitung banyak data didalam array DB'
	int countDB = namametodeBayarDB.size()
	
	'jika hitungan di UI dan DB sesuai'
	if (countWeb == countDB) {
		
		for (int i=1; i<=countWeb; i++) {
			
			'ambil object dari ddl'
			def modifyNamametodeBayar = WebUI.modifyObjectProperty(findTestObject('Object Repository/TransactionHistory/modifyObject'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-search-filter/div/div/div/div/div/form/div[1]/div[4]/app-question/app-select/div/div[2]/ng-select/ng-dropdown-panel/div/div[2]/div["+(i+1)+"]/span", true)
			
			'tambahkan nama tipe saldo ke array'
			String data = WebUI.getText(modifyNamametodeBayar)
			namametodeBayarUI.add(data)
		}
		
		'cek setiap data di UI dengan data di DB sebagai pembanding'
		for (String tipe : namametodeBayarDB) {
			
			'jika ada data yang tidak terdapat pada arraylist yang lain'
			if (!namametodeBayarUI.contains(tipe)) {
				
				'ada data yang tidak match'
				ismetodeBayarFound = 0;
				'berhentikan loop'
				break;
			}
			'kondisi ini bisa ditemui jika data match'
			ismetodeBayarFound = 1
		}
			
	}
	else if (ismetodeBayarFound == 0 || countWeb != countDB) {
		
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('RiwayatTransaksi', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonDDL)
	}
	
	'klik pada ddl metode bayar'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/span_Metode'))
}

def checkddlTenant(Connection conndev) {
	
	'klik pada dropdownlist tenant'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/span_Tenant'))
	
	'ambil list tenant'
	def elementTenant = DriverFactory.getWebDriver().findElements(By.xpath('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-search-filter/div/div/div/div/div/form/div[1]/div[6]/app-question/app-select/div/div[2]/ng-select/ng-dropdown-panel/div/div[2]/div'))
	
	'ambil hitungan Tenant yang ada'
	int countWeb = (elementTenant.size()) - 1
	
	'flag Tenant sesuai'
	int isTenantFound = 0
	
	'ambil nama Tenant dari DB'
	ArrayList namaTenantDB = CustomKeywords.'transactionHistory.TransactionVerif.getTenantList'(conndev)
	
	'nama-nama tipe saldo sedang aktif dari UI'
	ArrayList namaTenantUI = []
	
	'hitung banyak data didalam array DB'
	int countDB = namaTenantDB.size()
	
	if (countWeb != countDB) {
		
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('RiwayatTransaksi', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonDDL)
	}
	
	'klik pada dropdownlist tenant'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/span_Tenant'))
}

def checkVerifyPaging(Boolean isMatch) {
	if (isMatch == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('RiwayatTransaksi', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 2) +
				';') + GlobalVariable.FailedReasonPagingError)

		GlobalVariable.FlagFailed = 1
	}
}

def checkVerifyReset(Boolean isMatch) {
	if (isMatch == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('RiwayatTransaksi', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 2) +
				';') + GlobalVariable.FailedReasonSetFailed)

		GlobalVariable.FlagFailed = 1
	}
}

def checkVerifyNotPresent(Boolean isPresent, String reason) {
	if (isPresent == true) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('RiwayatTransaksi', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 2) +
				';') + GlobalVariable.FailedReasonRoleFeature + ' ' + reason)

		GlobalVariable.FlagFailed = 1
	}
}

def checkVerifyPresent(Boolean isPresent, String reason) {
	if (isPresent == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('RiwayatTransaksi', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 2) +
				';') + GlobalVariable.FailedReasonRoleFeature + ' Not showing ' + reason)

		GlobalVariable.FlagFailed = 1
	}
}

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
	if (isMatch == false) {
		
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		GlobalVariable.FlagFailed = 1
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('RiwayatTransaksi', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
				GlobalVariable.FailedReasonVerifyEqualorMatch + reason)
	}
}

def checkVerifyNotEqualorMatch(Boolean isMatch, String reason) {
	if (isMatch == true) {
		
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		GlobalVariable.FlagFailed = 1
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('RiwayatTransaksi', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
				GlobalVariable.FailedReasonVerifyEqualorMatch + reason)
	}
}