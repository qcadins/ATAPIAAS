import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import java.sql.Connection

import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.configuration.RunConfiguration as RunConfiguration
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.WebDriver as WebDriver
import groovy.sql.Sql as Sql
import org.openqa.selenium.By as By
import org.openqa.selenium.support.ui.Select as Select
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.writeExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Edit Profile'
int CountColumnEdit = findTestData(ExcelPathSaldo).getColumnNumbers()

'deklarasi variabel untuk konek ke Database eendigo_dev'
def conn = CustomKeywords.'dbConnection.connect.connectDBAPIAAS_public'()

//'deklarasi koneksi ke Database adins_apiaas_uat'
//def connProd = CustomKeywords.'dbConnection.connect.connectDBAPIAAS_uatProduction'()

'deklarasi koneksi ke Database adins_apiaas_uat'
def conndevUAT = CustomKeywords.'dbConnection.connect.connectDBAPIAAS_devUat'()

'panggil fungsi login'
WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'Saldo'], FailureHandling.STOP_ON_FAILURE)

'ambil kode tenant di DB'
String tenantcode = CustomKeywords.'saldo.verifSaldo.getTenantCodefromDB'(conn, findTestData(ExcelPathSaldo).getValue(2,24))

for(GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn <= CountColumnEdit; (GlobalVariable.NumOfColumn)++)
{
	'set penanda error menjadi 0'
	GlobalVariable.FlagFailed = 0
		
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 1).length() == 0) 
	{
		break
	} 
	else if (!findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 1).equalsIgnoreCase('Unexecuted')) 
	{
		continue
	}
		
	'angka untuk menghitung data mandatory yang tidak terpenuhi'
	int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 4))
	
	'panggil fungsi cek filter saldo'
	filterSaldo()
	
	'panggil fungsi cek table dan paging'
	checkTableandPaging(conndevUAT, tenantcode, findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 8))
	
	'check dropdownlist dari tipe saldo'
	checkddlTipeSaldo(conndevUAT, tenantcode)
	
	'check dropdownlist dari tipe saldo'
	checkddlTipeTransaksi(conndevUAT, findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 8))
	
	'check dropdownlist dari office'
	checkddlOffice(conndevUAT, tenantcode)
	
	'ambil nama saldo tenant yang aktif di DB'
	ArrayList<String> ActiveBalanceDB = CustomKeywords.'saldo.verifSaldo.getListActiveBalance'(conndevUAT, tenantcode)
	
	'ambil nama saldo tenant aktif di UI'
	ArrayList<String> ActiveBalanceUI = new ArrayList<String>()
	
	'cari element dengan nama saldo'
	def elementNamaSaldo = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance-prod > div.row.match-height > div > lib-balance-summary > div > div'))
	
	'lakukan loop untuk cari nama saldo yang ditentukan'
	for(int i=1; i<=elementNamaSaldo.size(); i++)
	{
		'cari nama saldo yang sesuai di list saldo'
		def modifyNamaSaldo = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Balance/span_OCR KK'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[1]/div/lib-balance-summary/div/div["+ (i) +"]/div/div/div/div/div[1]/span", true)
		
		'tambahkan nama saldo ke array'
		ActiveBalanceUI.add(WebUI.getText(modifyNamaSaldo))
	}
	
	'jika hasil UI dan DB tidak sama'
	if(!ActiveBalanceUI.containsAll(ActiveBalanceDB))
	{
		GlobalVariable.FlagFailed = 1
		
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonBalanceUI'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('Saldo', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 2) +
		';') + GlobalVariable.FailedReasonBalanceUI)
	}
	
	'check if mandatory complete dan button simpan clickable'
	if ((isMandatoryComplete == 0) && GlobalVariable.FlagFailed == 0)
	{
		'write to excel success'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Saldo', 0,
			GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
	}
	else if (isMandatoryComplete > 0)
	{
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonMandatory'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('Saldo', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 2) +
			';') + GlobalVariable.FailedReasonMandatory)
	}
	
	WebUI.refresh()
}

WebUI.closeBrowser()

'fungsi untuk filter saldo berdasarkan input user'
def filterSaldo() {
	
	'driver chrome untuk pengalihan proses download'
	WebDriver driver = DriverFactory.getWebDriver()
	
	'isi field input tipe saldo'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'), findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 8))
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'), Keys.chord(Keys.ENTER))
	
	'klik pada button cari'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/button_Cari'))
	
	'jika hasil pencarian tidak memberikan hasil'
	if(WebUI.verifyElementPresent(findTestObject('Object Repository/Saldo/Page_Balance/hasil search'), GlobalVariable.Timeout, FailureHandling.OPTIONAL))
	{
		GlobalVariable.FlagFailed = 1
		
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('Saldo', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 2) +
		';') + GlobalVariable.FailedReasonSearchFailed)
	}
	
	
	'isi field input tipe saldo'
	WebUI.setText(findTestObject('Object Repository/Saldo/Page_Balance/inputtipesaldo'), findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 8))
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/Saldo/Page_Balance/inputtipesaldo'), Keys.chord(Keys.ENTER))
	
	'isi field tipe transaksi'
	WebUI.setText(findTestObject('Object Repository/Saldo/Page_Balance/inputtipetransaksi'), findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 9))
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/Saldo/Page_Balance/inputtipetransaksi'), Keys.chord(Keys.ENTER))
	
	'isi tanggal transaksi awal'
	WebUI.setText(findTestObject('Object Repository/Saldo/Page_Balance/input_Tanggal Transaksi Dari_transactionDateStart'), findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 10))
	
	'input pengguna dari transaksi'
	WebUI.setText(findTestObject('Object Repository/Saldo/Page_Balance/input_Pengguna_user'), findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 11))
	
	'input hasil proses berdasarkan ddl di excel'
	WebUI.setText(findTestObject('Object Repository/Saldo/Page_Balance/inputhasilproses'), findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 12))
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/Saldo/Page_Balance/inputhasilproses'), Keys.chord(Keys.ENTER))
	
	'input reference number transaksi'
	WebUI.setText(findTestObject('Object Repository/Saldo/Page_Balance/input_Ref Number_referenceNo'), findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 13))
	
	'input nama dokumen'
	WebUI.setText(findTestObject('Object Repository/Saldo/Page_Balance/input_Nama Dokumen_documentName'), findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 14))
	
	'input batas tanggal transaksi terakhir'
	WebUI.setText(findTestObject('Object Repository/Saldo/Page_Balance/input_Tanggal Transaksi Sampai_transactionDateEnd'), findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 15))
	
	'input kantor'
	WebUI.setText(findTestObject('Object Repository/Saldo/Page_Balance/inputkantor'), findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 16))
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/Saldo/Page_Balance/inputkantor'), Keys.chord(Keys.ENTER))
	
	'klik pada button cari'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/button_Cari'))
	
	'jika hasil pencarian tidak memberikan hasil'
	if(WebUI.verifyElementPresent(findTestObject('Object Repository/Saldo/Page_Balance/hasil search'), GlobalVariable.Timeout, FailureHandling.OPTIONAL))
	{
		GlobalVariable.FlagFailed = 1
		
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('Saldo', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 2) +
		';') + GlobalVariable.FailedReasonSearchFailed)
	}
	
	'klik pada tombol set ulang'
	WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/button_Set Ulang'))
	
	'verify field tipe saldo ter-reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/Saldo/Page_Balance/inputtipesaldo'), 'value', FailureHandling.CONTINUE_ON_FAILURE),
			'', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field tipe transaksi ter-reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/Saldo/Page_Balance/inputtipetransaksi'), 'value', FailureHandling.CONTINUE_ON_FAILURE),
			'', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field tanggal transaksi awal ter-reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/Saldo/Page_Balance/input_Tanggal Transaksi Dari_transactionDateStart'), 'value', FailureHandling.CONTINUE_ON_FAILURE),
			'', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field pengguna ter-reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/Saldo/Page_Balance/input_Pengguna_user'), 'value', FailureHandling.CONTINUE_ON_FAILURE),
			'', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field hasil proses ter-reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/Saldo/Page_Balance/inputhasilproses'), 'value', FailureHandling.CONTINUE_ON_FAILURE),
			'', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field ref number ter-reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/Saldo/Page_Balance/input_Ref Number_referenceNo'), 'value', FailureHandling.CONTINUE_ON_FAILURE),
			'', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field nama dokumen ter-reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/Saldo/Page_Balance/input_Nama Dokumen_documentName'), 'value', FailureHandling.CONTINUE_ON_FAILURE),
			'', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field tanggal transaksi akhir ter-reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/Saldo/Page_Balance/input_Tanggal Transaksi Sampai_transactionDateEnd'), 'value', FailureHandling.CONTINUE_ON_FAILURE),
			'', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field kantor ter-reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/Saldo/Page_Balance/inputkantor'), 'value', FailureHandling.CONTINUE_ON_FAILURE),
			'', false, FailureHandling.CONTINUE_ON_FAILURE))

	
	'isi field input tipe saldo'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'), findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 8))
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'), Keys.chord(Keys.ENTER))
	
	'isi field tipe transaksi'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipetranc'), findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 9))
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipetranc'), Keys.chord(Keys.ENTER))
	
	'klik pada button cari'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/button_Cari'))
	
	'jika hasil pencarian tidak memberikan hasil'
	if(WebUI.verifyElementPresent(findTestObject('Object Repository/Saldo/Page_Balance/hasil search'), GlobalVariable.Timeout, FailureHandling.OPTIONAL))
	{
		GlobalVariable.FlagFailed = 1
		
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('Saldo', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 2) +
		';') + GlobalVariable.FailedReasonSearchFailed)
	}
	
	'user menentukan apakah file yang didownload langsung dihapus atau tidak lewat excel'
	String DownloadFile = findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 19)
	
	'user menentukan apakah file yang didownload langsung dihapus atau tidak lewat excel'
	String FlagDelete = findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 20)
	
	'mengambil alamat dari project katalon ini'
	String userDir = System.getProperty('user.dir')
	
	'directory tempat file akan didownload'
	String filePath = userDir + '\\Download'
	
	if (DownloadFile == 'Yes')
	{
		'klik pada tombol unduh excel'
		WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/button_Unduh Excel'))
		
		WebUI.delay(GlobalVariable.Timeout)
		
		'pengecekan file yang sudah didownload'
		boolean isDownloaded = CustomKeywords.'documentationAPI.checkDocumentation.isFileDownloaded'(FlagDelete)
		
		'jika file tidak terdeteksi telah terdownload'
		if (!WebUI.verifyEqual(isDownloaded, true, FailureHandling.OPTIONAL))
		{
			GlobalVariable.FlagFailed = 1
			
			'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
			CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('Saldo', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 2) +
			';') + GlobalVariable.FailedReasonDownloadProblem)
		}
	}
}

'fungsi langsung ke laman akhir'
def checkTableandPaging(Connection connection, String tenantcode, String tipeSaldo) {
	
	'ambil total data yang dicari dari DB'
	int resultTotalData = CustomKeywords.'saldo.verifSaldo.getCountTotalData'(connection, tenantcode, tipeSaldo)
	
	'cek apakah total data di table dan db equal'
	Total = WebUI.getText(findTestObject('Object Repository/Saldo/Page_Balance/totalDataTable')).split(' ')
	
	'verify total data tenant'
	if(WebUI.verifyEqual(resultTotalData, Integer.parseInt(Total[0]), FailureHandling.CONTINUE_ON_FAILURE) == false)
	{
		GlobalVariable.FlagFailed = 1
		
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('Saldo', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 2) +
		';') + GlobalVariable.FailedReasonVerifyEqualorMatch)
	}
	
	'cek apakah button enable atau disable'
	if(WebUI.getAttribute(findTestObject('Object Repository/Saldo/Page_Balance/page2'), 'class', FailureHandling.OPTIONAL) == '')
	{
		'klik button page 2'
		WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/page2'))
		
		'verify paging di page 2'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/Saldo/Page_Balance/page2'), 'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'klik button page 1'
		WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/page1'))
		
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/Saldo/Page_Balance/page1'), 'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'cari button skip di footer'
		def elementbuttonskip = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance-prod > div.ng-star-inserted > app-msx-paging-v2 > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))
		
		'ambil banyaknya laman footer'
		int lastPage = elementbuttonskip.size()
		
		'ubah path object button next page'
		def modifybuttonNextPage = WebUI.modifyObjectProperty(findTestObject('Object Repository/Saldo/Page_Balance/modifybuttonpage'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li["+ (lastPage-1) +"]", true)

		'klik tombol next page'
		WebUI.click(modifybuttonNextPage)
		
		'verify paging di page 2'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/Saldo/Page_Balance/page2'), 'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'klik button previous page'
		WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/previousPage'))
		
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/Saldo/Page_Balance/page1'), 'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'ubah path object button skip'
		def modifybuttonskip = WebUI.modifyObjectProperty(findTestObject('Object Repository/Saldo/Page_Balance/lastPage'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li["+ (lastPage) +"]", true)
	
		'cek apakah button enable atau disable'
		if(WebUI.getAttribute(modifybuttonskip, 'class', FailureHandling.CONTINUE_ON_FAILURE) == '')
		{
			'klik button skip to last page'
			WebUI.click(modifybuttonskip)
		}
		
		'ubah path object button laman terakhir'
		def modifybuttonMaxPage = WebUI.modifyObjectProperty(findTestObject('Object Repository/Saldo/Page_Balance/modifybuttonpage'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li["+ (lastPage-2) +"]", true)
		
		'verify paging di page terakhir'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(modifybuttonMaxPage, 'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'klik tombol kembali ke laman pertama'
		WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/firstPage'))
		
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/Saldo/Page_Balance/page1'), 'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
	}
}

'cek jumlah ddl tipe saldo DB dan UI'
def checkddlTipeSaldo(Connection Conn, String tenantcode) {
	
	'klik pada tipe saldo'
	WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/inputtipesaldo'))
	
	'ambil list tipesaldo'
	def elementjumlahTipeSaldo = DriverFactory.getWebDriver().findElements(By.xpath('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-search-filter-v2/div/div/div/div/div/form/div[1]/div[1]/app-select/div/div[2]/ng-select/ng-dropdown-panel/div/div[2]/div'))
		
	'ambil hitungan tipesaldo yang ada'
	int countWeb = (elementjumlahTipeSaldo.size()) - 1
	
	'flag apakah tipesaldo sesuai pada verifikasi'
	int isTipeSaldoMatch = 1
	
	'ambil nama balance dari DB'
	ArrayList<String> namatipesaldoDB = CustomKeywords.'saldo.verifSaldo.getListTipeSaldo'(Conn, tenantcode)
	
	'nama-nama tipe saldo sedang aktif dari UI'
	ArrayList<String> namatipesaldoUI = new ArrayList<String>()
	
	'ambil hitungan tipesaldo dari DB'
	int countDB = namatipesaldoDB.size()
	
	'jika jumlah data di UI sama dengan DB'
	if(countWeb == countDB)
	{
		'mulai perhitungan data'
		for(int i=1; i<=countWeb; i++)
		{
			'ambil object dari ddl'
			def modifyNamatipesaldo = WebUI.modifyObjectProperty(findTestObject('Object Repository/Saldo/Page_Balance/modifyobjectddl'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-search-filter-v2/div/div/div/div/div/form/div[1]/div[1]/app-select/div/div[2]/ng-select/ng-dropdown-panel/div/div[2]/div["+(i+1)+"]/span", true)
				
			'tambahkan nama tipe saldo ke array'
			String data = WebUI.getText(modifyNamatipesaldo)
			namatipesaldoUI.add(data)
		}
			
		'jika ada data yang tidak terdapat pada arraylist yang lain'
		if (!namatipesaldoUI.containsAll(namatipesaldoDB))
		{
			'ada data yang tidak match'
			isTipeSaldoMatch = 0;
		}
	}
	
	'jika hitungan di UI dan DB tidak sesuai'
	if(countWeb != countDB || isTipeSaldoMatch == 0)
	{
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and reason topup failed'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('Saldo', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonDDL)
	}
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'), Keys.chord(Keys.ENTER))
}

'cek jumlah ddl tipe saldo DB dan UI'
def checkddlTipeTransaksi(Connection Conn, String tipeSaldo) {
	
	'klik pada tipe saldo'
	WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/inputtipetransaksi'))
	
	'ambil list tipesaldo'
	def elementjumlahTipeTransaksi = DriverFactory.getWebDriver().findElements(By.xpath('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-search-filter-v2/div/div/div/div/div/form/div[1]/div[3]/app-select/div/div[2]/ng-select/ng-dropdown-panel/div/div[2]/div'))
		
	'ambil hitungan tipesaldo yang ada'
	int countWeb = (elementjumlahTipeTransaksi.size()) - 1
	
	'flag apakah tipesaldo sesuai pada verifikasi'
	int isTipeTransaksiMatch = 1
	
	'ambil nama balance dari DB'
	ArrayList<String> namatipetransaksiDB = CustomKeywords.'saldo.verifSaldo.getListTipeTransaksi'(Conn, tipeSaldo)
	
	'nama-nama tipe saldo sedang aktif dari UI'
	ArrayList<String> namatipetransaksiUI = new ArrayList<String>()
	
	'ambil hitungan tipesaldo dari DB'
	int countDB = namatipetransaksiDB.size()
	
	'jika jumlah data di UI sama dengan DB'
	if(countWeb == countDB)
	{
		'mulai perhitungan data'
		for(int i=1; i<=countWeb; i++)
		{
			'ambil object dari ddl'
			def modifyNamatipetransaksi = WebUI.modifyObjectProperty(findTestObject('Object Repository/Saldo/Page_Balance/modifyobjectddl'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-search-filter-v2/div/div/div/div/div/form/div[1]/div[3]/app-select/div/div[2]/ng-select/ng-dropdown-panel/div/div[2]/div["+(i+1)+"]/span", true)
				
			'tambahkan nama tipe saldo ke array'
			String data = WebUI.getText(modifyNamatipetransaksi)
			namatipetransaksiUI.add(data)
		}
			
		'jika ada data yang tidak terdapat pada arraylist yang lain'
		if (!namatipetransaksiUI.containsAll(namatipetransaksiDB))
		{
			'ada data yang tidak match'
			isTipeTransaksiMatch = 0;
		}
		
	}
	
	'jika hitungan di UI dan DB tidak sesuai'
	if(countWeb != countDB || isTipeTransaksiMatch == 0)
	{
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and reason topup failed'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('Saldo', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonDDL)
	}
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipetransaksi'), Keys.chord(Keys.ENTER))
}

'cek jumlah ddl tipe saldo DB dan UI'
def checkddlOffice(Connection Conn, String tenantcode) {
	
	'klik pada tipe saldo'
	WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/inputkantor'))
	
	'ambil list tipesaldo'
	def elementjumlahKantor = DriverFactory.getWebDriver().findElements(By.xpath('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-search-filter-v2/div/div/div/div/div/form/div[1]/div[8]/app-question/app-select/div/div[2]/ng-select/ng-dropdown-panel/div/div[2]/div'))
		
	'ambil hitungan tipesaldo yang ada'
	int countWeb = (elementjumlahKantor.size()) - 1
	
	'flag apakah tipesaldo sesuai pada verifikasi'
	int isKantorMatch = 1
	
	'ambil nama balance dari DB'
	ArrayList<String> namaKantorDB = CustomKeywords.'saldo.verifSaldo.getListKantor'(Conn, tenantcode)
	
	'nama-nama tipe saldo sedang aktif dari UI'
	ArrayList<String> namaKantorUI = new ArrayList<String>()
	
	'ambil hitungan tipesaldo dari DB'
	int countDB = namaKantorDB.size()
	
	'jika jumlah data di UI sama dengan DB'
	if(countWeb == countDB)
	{
		'mulai perhitungan data'
		for(int i=1; i<=countWeb; i++)
		{
			'ambil object dari ddl'
			def modifyNamaKantor = WebUI.modifyObjectProperty(findTestObject('Object Repository/Saldo/Page_Balance/modifyobjectddl'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-search-filter-v2/div/div/div/div/div/form/div[1]/div[8]/app-question/app-select/div/div[2]/ng-select/ng-dropdown-panel/div/div["+(i+1)+"]/div/span", true)
																																						
			'tambahkan nama tipe saldo ke array'
			String data = WebUI.getText(modifyNamaKantor)
			namaKantorUI.add(data)
		}
			
		'jika ada data yang tidak terdapat pada arraylist yang lain'
		if (!namaKantorUI.containsAll(namaKantorDB))
		{
			'ada data yang tidak match'
			isKantorMatch = 0;
		}
		
	}
	
	'jika hitungan di UI dan DB tidak sesuai'
	if(countWeb != countDB || isKantorMatch == 0)
	{
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and reason topup failed'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('Saldo', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonDDL)
	}
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputkantor'), Keys.chord(Keys.ENTER))
}

def checkVerifyReset(Boolean isMatch) {
	if (isMatch == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('Saldo', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
			(findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.FailedReasonSetFailed)

		GlobalVariable.FlagFailed = 1
	}
}

def checkVerifyPaging(Boolean isMatch) {
	if (isMatch == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('Saldo', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
			(findTestData(ExcelPathSaldo).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.FailedReasonPagingError)

		GlobalVariable.FlagFailed = 1
	}
}