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

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writetoexcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet User'
int countColumnEdit = findTestData(ExcelPathTranx).columnNumbers, flagLoginUsed

'deklarasi koneksi ke Database adins_apiaas_uat'
Connection conndev = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_esign'()

for (GlobalVariable.NumOfColumn = 2; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++) {
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).length() == 0) {
		break
	} else if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).equalsIgnoreCase('Unexecuted') ||
		findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).equalsIgnoreCase('Warning')) {
		'set penanda error menjadi 0'
		GlobalVariable.FlagFailed = 0
		
		if (flagLoginUsed == 0) {
			'panggil fungsi login'
			WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'TranxHist', ('SheetName') : sheet,
					('Path') : ExcelPathTranx], FailureHandling.STOP_ON_FAILURE)
			
			'klik pada menu'
			WebUI.click(
				findTestObject('Object Repository/TransactionHistory/Page_Balance/span_Menu'))
			
			'pilih submenu riwayat transaksi'
			WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_Balance/RiwayatTrxMenu'))
			
			'lakukan check paging'
			checkPaging(conndev)
		}
		
		'angka untuk menghitung data mandatory yang tidak terpenuhi'
		int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Is Mandatory Complete')))
		
		'klik pada menu'
		WebUI.click(
			findTestObject('Object Repository/TransactionHistory/Page_Balance/span_Menu'))
		
		'pilih submenu riwayat transaksi'
		WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_Balance/RiwayatTrxMenu'))
		
		'cek apakah tombol menu dalam jangkauan web'
		if (WebUI.verifyElementVisible(findTestObject('Object Repository/User Management-Role/Page_List Roles/tombolX_menu'), FailureHandling.OPTIONAL)) {
			'klik pada tombol silang menu'
			WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/tombolX_menu'))
		}
		
		if (flagLoginUsed == 0) {
			'ambil nama TipeIsiUlang dari DB'
			ArrayList namaTipeIsiUlangDB = CustomKeywords.'transactionhistory.TransactionVerif.getDDLTipeIsiUlang'(conndev)
		
			'ambil nama Status dari DB'
			ArrayList namaStatusExcel = []
			
			'ambil data status dari excel, karena tidak disimpan ke DB'
			for (int i = 0; i < 5; i++) {
				'tambah data ke array excel'
				namaStatusExcel.add(findTestData(ExcelPathTranx).getValue(1, (rowExcel('Menunggu Pembayaran') + i)))
			}
			
			'ambil nama metodeBayar dari DB'
			ArrayList namametodeBayarDB = CustomKeywords.'transactionhistory.TransactionVerif.getDDLMetodeTrf'(conndev)
	
			'panggil fungsi check ddl di DB dan UI'
			checkDDL(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputTipeIsiUlang'), namaTipeIsiUlangDB, 'DDL Tipe isi Ulang')
			
			'panggil fungsi check ddl di DB dan UI'
			checkDDL(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputStatus'), namaStatusExcel, 'DDL Status')
			
			'panggil fungsi check ddl di DB dan UI'
			checkDDL(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputMetodeBayar'), namametodeBayarDB, 'DDL Metode bayar')
			
			'ubah flag login sudah terpakai = 1'
			flagLoginUsed = 1
		}
				
		'ambil role yang digunakan oleh user'
		String roleUser = CustomKeywords.'transactionhistory.TransactionVerif.getRoleofUser'(conndev,
			findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Username Login')))
		
		'cek apakah role adminclient/admineendigo/adminfinance'
		if (roleUser.equalsIgnoreCase('Admin Client')) {
			'input batas awal transaksi'
			WebUI.setText(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/input_startDate'),
					findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Tanggal Transaksi awal')))
			
			'input batas akhir transaksi'
			WebUI.setText(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/input_endDate'),
					findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Tanggal Transaksi akhir')))
			
			'masukkan data ke filter status'
			inputDDLExact('Object Repository/TransactionHistory/Page_List Transaction History/inputStatus',
				findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('StatusSaldo')))
			
			'klik pada tombol cari'
			WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/button_Cari'))
			
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
			
			'modifikasi alamat object trxnumber'
			modifytrxnumber = WebUI.modifyObjectProperty(findTestObject('Object Repository/TransactionHistory/modifyObject'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[1]/datatable-body-row/div[2]/datatable-body-cell[1]/div/p', true)
			
			'ambil trx num'
			String trxNum = WebUI.getText(modifytrxnumber)
			
			'cek apakah perlu view dan verif detail'
			if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('ViewDetail?')) == 'Yes') {
				functionDetail(conndev, trxNum)
			}
			'cek apakah perlu view dan verif npwp'
			if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('ViewNPWP?')) == 'Yes') {
				functionviewverifNPWP(conndev)
			}
			'cek apakah perlu upload bukti pembayaran'
			if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')) == 'Upload Bukti Bayar') {
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
					findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('$Image')), FailureHandling.OPTIONAL)
				
				'klik batal'
				WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/buttonBatal_upload'))
				
				'klik untuk bagian unggah pembayaran'
				WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/view_UploadBukti'))

				'klik unggah foto bukti pembayaran'
				WebUI.uploadFile(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/uploadBukti_Browse'),
					findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('$Image')), FailureHandling.OPTIONAL)
				
				'klik simpan'
				WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/buttonSimpan_upload'))
				
				if (WebUI.verifyElementPresent(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/div_Success_upload'),
					GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
					'verify layanan detail transaksi ui = db'
					checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/div_Success_upload')),
						'Success', false, FailureHandling.CONTINUE_ON_FAILURE), 'Upload Gagal')
				
					'klik ok pada popup'
					WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/button_OK_upload'))
					
					'cek apakah kondisi cek storeDB aktif'
					if (GlobalVariable.KondisiCekDB == 'Yes') {
						'panggil fungsi storeDB'
						WebUI.callTestCase(findTestCase('Test Cases/Transaction History/TransactionStoreDB'), [('Path') : ExcelPathTranx,
							('TrxType') : 'Upload', ('TrxNum') : trxNum, ('Sheet') : sheet],
							 FailureHandling.CONTINUE_ON_FAILURE)
					}
				} else if (WebUI.verifyElementPresent(findTestObject('Object Repository/TransactionHistory/ErrorTopRight'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
					GlobalVariable.FlagFailed = 1
					
					'ambil error dan get text dari error tersebut'
					'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
					CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
						GlobalVariable.StatusFailed, (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) +
							';') + '<' + WebUI.getText(findTestObject('Object Repository/TransactionHistory/ErrorTopRight')) + '>')
					
					'klik batal'
					WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/buttonBatal_upload'))
				}
			}
		} else if (roleUser.equalsIgnoreCase('Admin Finance Eendigo')) {
			'ambil nama Tenant dari DB'
			ArrayList namaTenantDB = CustomKeywords.'transactionhistory.TransactionVerif.getTenantList'(conndev)
			
			'panggil fungsi check ddl di DB dan UI'
			checkDDL(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputTenant'), namaTenantDB, 'DDL Tenant')
			
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
			modifytrxnumber = WebUI.modifyObjectProperty(findTestObject('Object Repository/TransactionHistory/modifyObject'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[1]/datatable-body-row/div[2]/datatable-body-cell[2]/div/p', true)
			
			'ambil trx num'
			String trxNum = WebUI.getText(modifytrxnumber)
			
			'cek apakah perlu reject pembayaran'
			if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')) == 'Reject') {
				confRejectPayment('Reject', conndev, trxNum)
				
				searchadminEendigoFinance()
				
				'modifikasi alamat object trxnumber'
				modifytrxnumber = WebUI.modifyObjectProperty(findTestObject('Object Repository/TransactionHistory/modifyObject'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[1]/datatable-body-row/div[2]/datatable-body-cell[1]/div/p', true)
				
				'ambil trx num'
				trxNum = WebUI.getText(modifytrxnumber)
			}
			'cek apakah perlu view dan verif detail'
			if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('ViewDetail?')) == 'Yes') {
				functionDetail(conndev, trxNum)
			}
			'cek apakah perlu view dan verif npwp'
			if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('ViewNPWP?')) == 'Yes') {
				functionviewverifNPWP(conndev)
			}
			'cek apakah perlu view bukti pembayaran'
			if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('ViewBuktiPembayaran?')) == 'Yes') {
				'klik pada view bukti pembayaran'
				WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/view_Bukti'))
				
				'klik pada tombol X'
				WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/viewbukti_X'))
			}
			'cek apakah perlu approve pembayaran'
			if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')) == 'Approve') {
				confRejectPayment('Approve', conndev, trxNum)
			}
		} else if (roleUser.equalsIgnoreCase('Admin Eendigo')) {
			'ambil nama Tenant dari DB'
			ArrayList namaTenantDB = CustomKeywords.'transactionhistory.TransactionVerif.getTenantList'(conndev)
			
			'panggil fungsi check ddl di DB dan UI'
			checkDDL(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputTenant'), namaTenantDB, 'DDL Tenant')
			
			searchadminEendigoFinance()
			
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
			
			'modifikasi alamat object trxnumber'
			modifytrxnumber = WebUI.modifyObjectProperty(findTestObject('Object Repository/TransactionHistory/modifyObject'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[1]/datatable-body-row/div[2]/datatable-body-cell[1]/div/p', true)
			
			'ambil trx num'
			String trxNum = WebUI.getText(modifytrxnumber)
			
			'cek apakah perlu view dan verif detail'
			if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('ViewDetail?')) == 'Yes') {
				functionDetail(conndev, trxNum)
			}
			'cek apakah perlu view dan verif npwp'
			if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('ViewNPWP?')) == 'Yes') {
				functionviewverifNPWP(conndev)
			}
		}
		
		'jika tidak ada error tulis sukses'
		if (GlobalVariable.FlagFailed == 0 && isMandatoryComplete == 0) {
			'write to excel success'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0,
				GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
		}
		
		'klik pada profile'
		WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_Balance/span_profile'))
		
		'klik pada logout'
		WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_Balance/span_Logout'))
		
		'cek apakah kolom selanjutnya kosong'
		if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn + 1, rowExcel('Username Login')).length() != 0) {
			'input data email'
			WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_username'),
				findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn + 1, rowExcel('Username Login')))
			
			'input password'
			WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_password'),
				findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn + 1, rowExcel('Password Login')))
						
			'cek perlukah tunggu agar recaptcha selesai solving'
			if ((findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn + 1, rowExcel('CaptchaEnabled')) == 'Yes' ||
				findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn + 1, rowExcel('CaptchaEnabled')) == '')) {
				WebUI.delay(1)
			
				String idObject = WebUI.getAttribute(findTestObject('RegisterLogin/Page_Login - eendigo Platform/check_Recaptcha'), 'id', FailureHandling.STOP_ON_FAILURE)
				
				modifyObjectCaptcha = WebUI.modifyObjectProperty(findTestObject('RegisterLogin/Page_Login - eendigo Platform/check_Recaptcha'), 'xpath', 'equals', 
					'//*[@id="' + idObject + '"]/div/div[2]', true)
				
				WebUI.waitForElementAttributeValue(modifyObjectCaptcha, 'class', 'antigate_solver recaptcha solved', 90, FailureHandling.OPTIONAL)
			
				WebUI.delay(1)
			//	
			//	WebUI.waitForElementAttributeValue(findTestObject('RegisterLogin/Page_Login - eendigo Platform/check_Recaptcha'), 'aria-checked', 'true', 60, FailureHandling.OPTIONAL)
			}
			
			'klik pada button login'
			WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))
		}
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
	String npwpDB = CustomKeywords.'transactionhistory.TransactionVerif.getNPWPnumUser'(conndev,
		findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Username Login')))

	'verify npwp dari DB dan UI sesuai'
	checkVerifyEqualorMatch(WebUI.verifyMatch(npwpUI,
		npwpDB, false, FailureHandling.CONTINUE_ON_FAILURE), ' NPWP Number')
	
	'klik silang'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/npwp_X'))
}

def confRejectPayment(String choice, Connection conndev, String trxNum) {
	String string1, string2
	
	'deklarasi string yang perlu diubah'
	if (choice == 'Approve') {
		string1 = 'Accept Pembayaran'
		string2 = 'konfirmasiBayarbutton'
	}
	else {
		string1 = 'Reject pembayaran'
		string2 = 'rejectBayarButton'
	}
	
	'ambil list service '
	ArrayList serviceActive = CustomKeywords.'transactionhistory.TransactionVerif.getServiceCheck'(conndev, trxNum)
	
	'ambil harga dari service yang dituju'
	ArrayList serviceSaldobefore = [], serviceSaldoafter = []
	
	'klik pada menu'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_Balance/span_Menu'))
	
	'klik ke menu saldo'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_Balance/SaldoMenu'))
	
	'looping untuk tambahkan saldo ke array'
	for (int i = 0 ; i < serviceActive.size(); i++) {
		serviceSaldobefore.add(getSaldoforTransaction(serviceActive[i]))
	}
	
	searchadminEendigoFinance()
	
	'klik pada tombol approve/reject pembayaran'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/' + string2 + ''))
	
	'klik pada tombol batal'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/button_Tidak, batalkan'))
	
	'klik pada tombol approve/reject pembayaran'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/' + string2 + ''))
	
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
			'Success', false, FailureHandling.CONTINUE_ON_FAILURE), string1)
		
		'klik tombol OK'
		WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/button_OK_upload'))
		
		'jika notif text success'
		if (notifText == 'Success') {
			'klik pada menu'
			WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_Balance/span_Menu'))
	
			'klik ke menu saldo'
			WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_Balance/SaldoMenu'))
			
			'looping untuk tambahkan saldo ke array'
			for (int i = 0 ; i < serviceActive.size(); i++) {
				serviceSaldoafter.add(getSaldoforTransaction(serviceActive[i]))
			}
			
			'loop untuk verifikasi data'
			for (int i = 0; i < serviceActive.size(); i++) {
				if (choice == 'Approve') {
					'verify notifikasi sukses atau tidak'
					checkVerifyEqualorMatch(WebUI.verifyEqual(serviceSaldobefore[i],
						serviceSaldoafter[i], FailureHandling.CONTINUE_ON_FAILURE), 'Saldo tidak Berubah')
				} else {
					'verify notifikasi sukses atau tidak'
					checkVerifyNotEqualorMatch(WebUI.verifyNotEqual(serviceSaldobefore[i],
						serviceSaldoafter[i], FailureHandling.CONTINUE_ON_FAILURE), 'Saldo Berubah')
				}
			}
			
			'cek apakah kondisi cek storeDB aktif'
			if (GlobalVariable.KondisiCekDB == 'Yes') {
				'panggil fungsi storeDB'
				WebUI.callTestCase(findTestCase('Test Cases/Transaction History/TransactionStoreDB'), [('Path') : ExcelPathTranx,
					('TrxType') : choice, ('TrxNum') : trxNum, ('Sheet') : sheet],
					 FailureHandling.CONTINUE_ON_FAILURE)
			}
		}
	} else {
		GlobalVariable.FlagFailed = 1
		
		'tulis adanya error saat melakukan approval'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
				'<' + WebUI.getText(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/div_errorCatch')).toString()) + '>'
	}
}

def searchadminEendigoFinance() {
	'klik pada menu'
	WebUI.click(
		findTestObject('Object Repository/TransactionHistory/Page_Balance/span_Menu'))
	
	'pilih submenu riwayat transaksi'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_Balance/RiwayatTrxMenu'))
	
	'cek apakah tombol menu dalam jangkauan web'
	if (WebUI.verifyElementVisible(findTestObject('Object Repository/User Management-Role/Page_List Roles/tombolX_menu'), FailureHandling.OPTIONAL)) {
		'klik pada tombol silang menu'
		WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/tombolX_menu'))
	}
	
	'input batas awal transaksi'
	WebUI.setText(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/input_startDate'),
			findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Tanggal Transaksi awal')))
	
	'input batas akhir transaksi'
	WebUI.setText(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/input_endDate'),
			findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Tanggal Transaksi akhir')))
	
	'masukkan data ke filter status'
	inputDDLExact('Object Repository/TransactionHistory/Page_List Transaction History/inputStatus',
		findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('StatusSaldo')))
	
	'masukkan data ke filter tenant'
	inputDDLExact('Object Repository/TransactionHistory/Page_List Transaction History/inputTenant',
		findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('$Tenant')))
	
	'klik pada tombol cari'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/button_Cari'))
}

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

def functionDetail(Connection conndev, String trxNum) {
	'klik pada tombol detail'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/viewDetail_Client'))
	
	'ambil data table dari db'
	ArrayList result = CustomKeywords.'transactionhistory.TransactionVerif.getRiwayatDetail'(conndev, trxNum)
	
	WebUI.delay(GlobalVariable.Timeout)
	
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
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/detail_X'))
}

def checkPaging(Connection conndev) {
	'input batas awal transaksi'
	WebUI.setText(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/input_startDate'),
			findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Tanggal Transaksi awal')))
	
	'input batas akhir transaksi'
	WebUI.setText(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/input_endDate'),
			findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Tanggal Transaksi akhir')))
	
	'input tipe isi ulang'
	inputDDLExact('Object Repository/TransactionHistory/Page_List Transaction History/inputTipeIsiUlang',
			findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Tipe Isi Ulang')))

	'input metode bayar'
	inputDDLExact('Object Repository/TransactionHistory/Page_List Transaction History/inputMetodeBayar',
			findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Metode Pembayaran')))
	
	'input status pembayaran'
	inputDDLExact('Object Repository/TransactionHistory/Page_List Transaction History/inputStatus',
			findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('StatusSaldo')))
	
	'cek apakah dropdown tenant muncul'
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputTenant'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		'input tenant yang dituju'
		inputDDLExact('Object Repository/TransactionHistory/Page_List Transaction History/inputTenant',
			findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('$Tenant')))
	}
	
	'klik pada tombol cari'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/button_Cari'))
	
	'cek apakah hasil search gagal'
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/Coupon/Page_List Coupon/searchResult')
		, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		GlobalVariable.FlagFailed = 1
		
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) +
		';') + GlobalVariable.FailedReasonSearchFailed)
	}
	
	'klik pada button set ulang'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/button_Set Ulang'))
	
	verifyReset()
	
	'cek apakah dropdown tenant muncul'
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputTenant'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		'verify field ke reset'
		checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputTenant'),
				'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))
	}
	'cek apakah dropdown tenant muncul'
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputTenant'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		'input tenant yang dituju'
		inputDDLExact('Object Repository/TransactionHistory/Page_List Transaction History/inputTenant',
			findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('$Tenant')))
	}
	
	'klik pada tombol cari'
	WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/button_Cari'))
	
	'cek apakah hasil search gagal'
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/Coupon/Page_List Coupon/searchResult'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		GlobalVariable.FlagFailed = 1
		
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) +
		';') + GlobalVariable.FailedReasonSearchFailed)
	}
	
	checkPagingDetail(conndev)
}

def verifyReset() {
	'verify field ke reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/input_startDate'),
			'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field ke reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/input_endDate'),
			'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field ke reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputTipeIsiUlang'),
			'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field ke reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputMetodeBayar'),
			'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field ke reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputStatus'),
			'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))
}

def checkPagingDetail(Connection conndev) {
	'cari button skip di footer'
	elementbutton = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-list-transaction-history > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))
	
	'ambil banyaknya laman footer'
	int lastPage = elementbutton.size()
	
	'get text total data dari ui'
	Total = WebUI.getText(findTestObject('Object Repository/TransactionHistory/TotalData')).split(' ')
	
	'ambil total data dari db'
	int resultTotalData = CustomKeywords.'transactionhistory.TransactionVerif.getTotalTrx'(conndev,
		findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 9))

	'verify total data role'
	checkVerifyPaging(WebUI.verifyNotEqual(resultTotalData, Integer.parseInt(Total[0]), FailureHandling.CONTINUE_ON_FAILURE))
	
	'cek apakah hlm  tersedia'
	if (WebUI.verifyElementVisible(findTestObject('Object Repository/TransactionHistory/skiptoLast_page'), FailureHandling.OPTIONAL) == true) {
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
		WebUI.click(findTestObject('Object Repository/TransactionHistory/next_page'))
		
		'verify paging di page 2'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/TransactionHistory/Page2'),
				'class', FailureHandling.CONTINUE_ON_FAILURE),
					'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'klik prev page'
		WebUI.click(findTestObject('Object Repository/TransactionHistory/prev_page'))
		
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/TransactionHistory/Page1'),
				'class', FailureHandling.CONTINUE_ON_FAILURE),
					'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'klik pada tombol skip'
		WebUI.click(findTestObject('Object Repository/TransactionHistory/skiptoLast_page'))
		
		'modify object laman terakhir'
		modifyObjectmaxPage = WebUI.modifyObjectProperty(findTestObject('Object Repository/TransactionHistory/modifyObject'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' + (lastPage - 2) + ']', true)
		
		'verify paging di page terakhir'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(modifyObjectmaxPage,
			'class', FailureHandling.CONTINUE_ON_FAILURE), 'pages active ng-star-inserted',
				false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'click min page'
		WebUI.click(findTestObject('Object Repository/TransactionHistory/gotoFirst_page'))
		
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/TransactionHistory/Page1'),
				'class', FailureHandling.CONTINUE_ON_FAILURE),
					'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
	}
}

def checkDDL(TestObject objectDDL, ArrayList<String> listDB, String reason) {
	'declare array untuk menampung ddl'
	ArrayList list = []

	'click untuk memunculkan ddl'
	WebUI.click(objectDDL)
	
	'get id ddl'
	id = WebUI.getAttribute(findTestObject('Object Repository/Top Up/ddlClass'), 'id', FailureHandling.CONTINUE_ON_FAILURE)
	
	'get row'
	variable = DriverFactory.webDriver.findElements(By.cssSelector(('#' + id) + '> div > div:nth-child(2) div'))
	
	'jika size variable diatas 10, lakukan pengecekan size nya saja'
	if (variable.size() < 10) {
		'looping untuk get ddl kedalam array'
		for (i = 1; i < variable.size(); i++) {
			'modify object DDL'
			modifyObjectDDL = WebUI.modifyObjectProperty(findTestObject('Object Repository/TransactionHistory/modifyObject'), 'xpath', 'equals', ((('//*[@id=\'' +
				id) + '-') + i) + '\']', true)
	
			'add ddl ke array'
			list.add(WebUI.getText(modifyObjectDDL))
		}
		
		'verify ddl ui = db'
		checkVerifyEqualorMatch(listDB.containsAll(list), reason)
		
		'verify jumlah ddl ui = db'
		checkVerifyEqualorMatch(WebUI.verifyEqual(list.size(), listDB.size(), FailureHandling.CONTINUE_ON_FAILURE), ' Jumlah ' + reason)
	} else {
		'verify jumlah ddl ui = db'
		checkVerifyEqualorMatch(WebUI.verifyEqual(variable.size() - 1, listDB.size(), FailureHandling.CONTINUE_ON_FAILURE), ' Jumlah ' + reason)
	}
	
	'Input enter untuk tutup ddl'
	WebUI.sendKeys(objectDDL, Keys.chord(Keys.ENTER))
}

def checkVerifyPaging(Boolean isMatch) {
	if (isMatch == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) +
				';') + GlobalVariable.FailedReasonPagingError)

		GlobalVariable.FlagFailed = 1
	}
}

def checkVerifyReset(Boolean isMatch) {
	if (isMatch == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) +
				';') + GlobalVariable.FailedReasonSetFailed)

		GlobalVariable.FlagFailed = 1
	}
}

def checkVerifyNotPresent(Boolean isPresent, String reason) {
	if (isPresent == true) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) +
				';') + GlobalVariable.FailedReasonRoleFeature + ' ' + reason)

		GlobalVariable.FlagFailed = 1
	}
}

def checkVerifyPresent(Boolean isPresent, String reason) {
	if (isPresent == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) +
				';') + GlobalVariable.FailedReasonRoleFeature + ' Not showing ' + reason)

		GlobalVariable.FlagFailed = 1
	}
}

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
	if (isMatch == false) {
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		GlobalVariable.FlagFailed = 1
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
				GlobalVariable.FailedReasonVerifyEqualorMatch + reason)
	}
}

def checkVerifyNotEqualorMatch(Boolean isMatch, String reason) {
	if (isMatch == true) {
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		GlobalVariable.FlagFailed = 1
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
				GlobalVariable.FailedReasonVerifyEqualorMatch + reason)
	}
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