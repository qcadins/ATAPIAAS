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

'panggil fungsi login'
WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'TranxHist', ('SheetName') : 'RiwayatTransaksi', 
	('Path') : ExcelPathTranx], FailureHandling.STOP_ON_FAILURE)

'klik pada menu'
WebUI.click(
	findTestObject('Object Repository/TransactionHistory/Page_Balance/span_Menu'))

'pilih submenu riwayat transaksi'
WebUI.click(findTestObject('Object Repository/TransactionHistory/Page_Balance/RiwayatTrxMenu'))

checkPaging(conndev)

checkddlTipeIsiUlang(conndev)

checkddlStatus(conndev)

checkddlMetodeTrf(conndev)

checkddlTenant(conndev)

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
		
		'ambil role yang digunakan oleh user'
		String RoleUser = CustomKeywords.'transactionHistory.TransactionVerif.getRoleofUser'(conndev,
			findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 9))
		
		'cek apakah role adminclient/admineendigo/adminfinance'
		if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 11).equalsIgnoreCase('Admin Client')) {
			
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
			checkVerifyNotPresent(WebUI.verifyElementPresent(
				findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/konfirmasiBayarbutton'),
					GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Adm.Client-NPWP')
		}
		else if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 11).equalsIgnoreCase('Admin Finance Eendigo')) {
			
			''
		}
		else if (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 11).equalsIgnoreCase('Admin Eendigo')) {
			
		}
	}
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
			findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 16))
	
	'enter pada metode bayar'
	WebUI.sendKeys(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputTipeIsiUlang'),
		Keys.chord(Keys.ENTER))
	
	'input status pembayaran'
	WebUI.setText(findTestObject('Object Repository/TransactionHistory/Page_List Transaction History/inputStatus'),
			findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 17))
	
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

	'verify total data role'
	checkVerifyPaging(WebUI.verifyEqual(resultTotalData, Integer.parseInt(Total[0]), FailureHandling.CONTINUE_ON_FAILURE))
	
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
	
	'hitung banyak data didalam array DB'
	int countExcel = namaStatusExcel.size()
	
	'ambil data status dari excel, karena tidak disimpan ke DB'
	for (int i = 0; i < 5; i++) {
		
		'tambah data ke array excel'
		namaStatusExcel.add(findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, (29+i)))
	}
	
	'jika hitungan di UI dan DB sesuai'
	if(countWeb == countExcel){
		
		for (int i = 1; i<=countWeb; i++) {
			
			'ambil object dari ddl'
			def modifyNamaStatus = WebUI.modifyObjectProperty(findTestObject('Object Repository/TransactionHistory/modifyObject'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-search-filter/div/div/div/div/div/form/div[1]/div[5]/app-question/app-select/div/div[2]/ng-select/ng-dropdown-panel/div/div[2]/div["+(i+1)+"]/span", true)
			
			'tambahkan nama tipe saldo ke array'
			String data = WebUI.getText(modifyNamaStatus)
			namaStatusUI.add(data)
		}
		
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
	if(countWeb == countDB){
		
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