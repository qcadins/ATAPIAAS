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

'mendapat jumlah kolom dari sheet Edit Profile'
int countColumnEdit = findTestData(ExcelPathCoupon).getColumnNumbers()

'deklarasi koneksi ke Database adins_apiaas_uat'
Connection conndev = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_esign'()

'panggil fungsi login'
WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'Coupon', ('SheetName') : 'Coupon',
	('Path') : ExcelPathCoupon], FailureHandling.STOP_ON_FAILURE)

'klik pada tombol untuk span menu'
WebUI.click(findTestObject('Object Repository/Coupon/Page_Balance/Spanmenu'))

'klik pada tombol coupon'
WebUI.click(findTestObject('Object Repository/Coupon/Page_Balance/span_Coupon'))

checkPaging(conndev)

for (GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++) {
	
	'set penanda error menjadi 0'
	GlobalVariable.FlagFailed = 0
		
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 1).length() == 0) {
		
		break
	}
	else if (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 1).equalsIgnoreCase('Unexecuted')) {
		
		'declare isMmandatory Complete'
		int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 5))
		
		'klik pada tombol untuk span menu'
		WebUI.click(findTestObject('Object Repository/Coupon/Page_Balance/Spanmenu'))
		
		'klik pada tombol coupon'
		WebUI.click(findTestObject('Object Repository/Coupon/Page_Balance/span_Coupon'))
		
		'cek apakah tombol menu dalam jangkauan web'
		if (WebUI.verifyElementVisible(findTestObject('Object Repository/Coupon/Page_Edit Coupon/tombolsilang'), 
			FailureHandling.OPTIONAL)) {
			
			'klik pada tombol silang menu'
			WebUI.click(findTestObject('Object Repository/Coupon/Page_Edit Coupon/tombolsilang'))
		}
		
		'check if action new/services/edit/balancechargetype'
		if (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 8).equalsIgnoreCase('New')) {
			
			'klik pada tombol tambah coupon'
			WebUI.click(findTestObject('Object Repository/Coupon/Page_List Coupon/a_Tambah'))
			
			'input tipe kupon'
			WebUI.setText(findTestObject('Object Repository/Coupon/Page_Add Coupon/inputtipekupon'),
					findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 20))
			
			'enter pada tipe kupon'
			WebUI.sendKeys(findTestObject('Object Repository/Coupon/Page_Add Coupon/inputtipekupon'),
				Keys.chord(Keys.ENTER))
			
			'input kode kupon'
			WebUI.setText(findTestObject('Object Repository/Coupon/Page_Add Coupon/input__couponCode'),
					findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 21))
			
			'input tanggal mulai berlaku'
			WebUI.setText(findTestObject('Object Repository/Coupon/Page_Add Coupon/input__couponStartDate'),
					findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 22))
			
			'input tanggal mulai berlaku'
			WebUI.setText(findTestObject('Object Repository/Coupon/Page_Add Coupon/input__couponEndDate'),
					findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 23))
			
			'input tipe nilai kupon'
			WebUI.setText(findTestObject('Object Repository/Coupon/Page_Add Coupon/inputtipenilaikupon'),
					findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 24))
			
			'enter pada tipe nilai kupon'
			WebUI.sendKeys(findTestObject('Object Repository/Coupon/Page_Add Coupon/inputtipenilaikupon'),
				Keys.chord(Keys.ENTER))
			
			'input tipe nilai kupon'
			WebUI.setText(findTestObject('Object Repository/Coupon/Page_Add Coupon/input__nilaikupon'),
					findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 25))
			
			'input jumlah kupon'
			WebUI.setText(findTestObject('Object Repository/Coupon/Page_Add Coupon/input__jmlkupon'),
					findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 26))
			
			'input maksimal redeem kupon'
			WebUI.setText(findTestObject('Object Repository/Coupon/Page_Add Coupon/input__maxredeem'),
					findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 27))
			
			'input minimal pembayaran untuk pakai kupon'
			WebUI.setText(findTestObject('Object Repository/Coupon/Page_Add Coupon/input__minimumPayment'),
					findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 28))
			
			'input tenant'
			WebUI.setText(findTestObject('Object Repository/Coupon/Page_Add Coupon/tenantinput'),
					findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 29))
			
			'enter pada tenant'
			WebUI.sendKeys(findTestObject('Object Repository/Coupon/Page_Add Coupon/tenantinput'),
				Keys.chord(Keys.ENTER))
			
			checkdialogConfirmation(isMandatoryComplete)
		}
		else if (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 8).equalsIgnoreCase('Edit')) {
			
			'cari kupon yang akan dilakukan edit'
			WebUI.setText(findTestObject('Object Repository/Coupon/Page_List Coupon/input_Kode Kupon'),
					findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 15))
			
			'klik tombol cari'
			WebUI.click(findTestObject('Object Repository/Coupon/Page_List Coupon/button_Cari'))
			
			'klik tombol edit'
			WebUI.click(findTestObject('Object Repository/Coupon/Page_List Coupon/editButton'))
			
			'input tipe kupon'
			WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/inputtipekupon'),
					findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 31))
			
			'input kode kupon'
			WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/inputkodekupon'),
					findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 32))
			
			'enter pada tipe kupon'
			WebUI.sendKeys(findTestObject('Object Repository/Coupon/Page_Edit Coupon/inputtipekupon'),
				Keys.chord(Keys.ENTER))
			
			'input tanggal mulai berlaku'
			WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/input__CouponStartDate'),
					findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 33))
			
			'input tanggal mulai berlaku'
			WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/input__CouponEndDate'),
					findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 34))
			
			'input tipe nilai kupon'
			WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/inputtipenilaikupon'),
					findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 35))
			
			'enter pada tipe nilai kupon'
			WebUI.sendKeys(findTestObject('Object Repository/Coupon/Page_Edit Coupon/inputtipenilaikupon'),
				Keys.chord(Keys.ENTER))
			
			'input tipe nilai kupon'
			WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/input__nilaikupon'),
					findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 36))
			
			'input jumlah kupon'
			WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/input__jmlkupon'),
					findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 37))
			
			'input maksimal redeem kupon'
			WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/input__maxredeem'),
					findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 38))
			
			'input minimal pembayaran untuk pakai kupon'
			WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/input__minimumpayment'),
					findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 39))
			
			'input tenant'
			WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/tenantinput'),
					findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 40))
			
			'enter pada tenant'
			WebUI.sendKeys(findTestObject('Object Repository/Coupon/Page_Edit Coupon/tenantinput'),
				Keys.chord(Keys.ENTER))
			
			checkdialogConfirmation(isMandatoryComplete)
		}
		else if (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 8).equalsIgnoreCase('Detail')) {
			
			'cari kupon yang akan dilakukan edit'
			WebUI.setText(findTestObject('Object Repository/Coupon/Page_List Coupon/input_Kode Kupon'),
					findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 15))
			
			'klik tombol cari'
			WebUI.click(findTestObject('Object Repository/Coupon/Page_List Coupon/button_Cari'))
			
			'klik tombol detail'
			WebUI.click(findTestObject('Object Repository/Coupon/Page_List Coupon/detailbutton'))
			
			'inisialisasi array detail dari web'
			ArrayList<String> detailresultWeb = []
			
			'inisialisasi array detail dari DB'
			ArrayList<String> detailresultDB = CustomKeywords.'coupon.couponverif.getDetailCoupon'(conndev, 
				findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 15))
			
			'tambahkan data tenant ke array'
			detailresultWeb.add(WebUI.getText(findTestObject('Object Repository/Coupon/Page_List Coupon/tenant')))
			
			'tambahkan data tenant ke minpayment'
			detailresultWeb.add(WebUI.getText(findTestObject('Object Repository/Coupon/Page_List Coupon/minimalpembayaran')).replace('.', ''))

			'tambahkan data tenant ke maxredeem'
			detailresultWeb.add(WebUI.getText(findTestObject('Object Repository/Coupon/Page_List Coupon/makspenebusan')))
			
			'cek apakah ada data yang tidak sesuai'
			for (int i = 0 ; i < detailresultDB.size; i++) {
				
				'jika ada data yang tidak sesuai'
				if(detailresultWeb[i] != detailresultDB[i]) {
					
					'tulis adanya error pada sistem web'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Coupon', GlobalVariable.NumOfColumn,
						GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
							GlobalVariable.FailedReasonDetailNotMatch)
					
					break
				}
				else
				{
					'write to excel success'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Coupon', 0,
						GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
				}
			}
			
			'klik pada tombol silang untuk mengakhiri sesi detail'
			WebUI.click(findTestObject('Object Repository/Coupon/Page_List Coupon/span_'))
		}
		if (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 42) == 'Yes') {
			
			'klik pada tombol copy kode kupon'
			WebUI.click(findTestObject('Object Repository/Coupon/Page_List Coupon/copybutton'))
			
			if(!WebUI.getText(findTestObject('Object Repository/Coupon/Page_List Coupon/copySuccessnotif'))== 
				' Kode Kupon berhasil disalin ') {
				
				'tulis ada masalah pada button copy'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Coupon', GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 2) +
				';') + GlobalVariable.FailedReasonButtonCopy)
			}
		}
	}
}

'fungsi untuk menutup browser jika testing berjalan mulus'
WebUI.closeBrowser()

def checkPaging(Connection conndev) {
	
	'input tipe kupon'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_List Coupon/inputtipekupon'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 11))
	
	'enter pada tipe kupon'
	WebUI.sendKeys(findTestObject('Object Repository/Coupon/Page_List Coupon/inputtipekupon'),
		Keys.chord(Keys.ENTER))
	
	'input tipe nilai kupon'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_List Coupon/inputtipenilaikupon'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 12))
	
	'enter pada tipe nilai kupon'
	WebUI.sendKeys(findTestObject('Object Repository/Coupon/Page_List Coupon/inputtipenilaikupon'),
		Keys.chord(Keys.ENTER))
	
	'input tanggal mulai berlaku akhir'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_List Coupon/Tanggal Mulai Berlaku Awal'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 13))
	
	'input tanggal mulai berlaku akhir'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_List Coupon/Tanggal Terakhir Berlaku Awal'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 14))
	
	'input tanggal mulai berlaku akhir'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_List Coupon/tanggal Mulai Berlaku Akhir'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 17))
	
	'input tanggal mulai berlaku akhir'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_List Coupon/Tanggal Terakhir Berlaku Akhir'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 18))
	
	'klik pada tombol cari'
	WebUI.click(findTestObject('Object Repository/Coupon/Page_List Coupon/button_Cari'))
	
	'cek apakah hasil search gagal'
	if(WebUI.verifyElementPresent(findTestObject('Object Repository/Coupon/Page_List Coupon/searchResult')
		, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		
		GlobalVariable.FlagFailed = 1
		
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Coupon', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 2) +
		';') + GlobalVariable.FailedReasonSearchFailed)
	
	}
	
	'klik pada button set ulang'
	WebUI.click(findTestObject('Object Repository/Coupon/Page_List Coupon/setUlang'))
	
	'verify field ke reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/Coupon/Page_List Coupon/inputtipekupon'),
			'value', FailureHandling.CONTINUE_ON_FAILURE),'', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field ke reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/Coupon/Page_List Coupon/inputtipenilaikupon'),
			'value', FailureHandling.CONTINUE_ON_FAILURE),'', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field ke reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/Coupon/Page_List Coupon/Tanggal Mulai Berlaku Awal'),
			'value', FailureHandling.CONTINUE_ON_FAILURE),'', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field ke reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/Coupon/Page_List Coupon/Tanggal Terakhir Berlaku Awal'),
			'value', FailureHandling.CONTINUE_ON_FAILURE),'', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field ke reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/Coupon/Page_List Coupon/tanggal Mulai Berlaku Akhir'),
			'value', FailureHandling.CONTINUE_ON_FAILURE),'', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field ke reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/Coupon/Page_List Coupon/Tanggal Terakhir Berlaku Akhir'),
			'value', FailureHandling.CONTINUE_ON_FAILURE),'', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field ke reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/Coupon/Page_List Coupon/inputstatuspemakaian'),
			'value', FailureHandling.CONTINUE_ON_FAILURE),'', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field ke reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/Coupon/Page_List Coupon/input_Kode Kupon'),
			'value', FailureHandling.CONTINUE_ON_FAILURE),'', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'input tipe kupon'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_List Coupon/inputtipekupon'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 11))
	
	'enter pada tipe kupon'
	WebUI.sendKeys(findTestObject('Object Repository/Coupon/Page_List Coupon/inputtipekupon'),
		Keys.chord(Keys.ENTER))
	
	'input tipe nilai kupon'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_List Coupon/inputtipenilaikupon'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 12))
	
	'enter pada tipe nilai kupon'
	WebUI.sendKeys(findTestObject('Object Repository/Coupon/Page_List Coupon/inputtipenilaikupon'),
		Keys.chord(Keys.ENTER))
	
	'klik pada tombol cari'
	WebUI.click(findTestObject('Object Repository/Coupon/Page_List Coupon/button_Cari'))
	
	'cek apakah hasil search gagal'
	if(WebUI.verifyElementPresent(findTestObject('Object Repository/Coupon/Page_List Coupon/searchResult')
		, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		
		GlobalVariable.FlagFailed = 1
		
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Coupon', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 2) +
		';') + GlobalVariable.FailedReasonSearchFailed)
	
	}
	
	'cari button skip di footer'
	def elementbutton = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout >'+
		' div > div.main-panel > div > div.content-wrapper > app-list-coupon > app-msx-paging > app-msx-datatable >'+
		' section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))
	
	'ambil banyaknya laman footer'
	int lastPage = elementbutton.size()
	
	'get text total data dari ui'
	Total = WebUI.getText(findTestObject('Object Repository/Coupon/Page_List Coupon/TotalData')).split(' ')
	
	'ambil total data dari db'
	int resultTotalData = CustomKeywords.'coupon.couponverif.getCouponTotal'(conndev)

	'verify total data role'
	checkVerifyPaging(WebUI.verifyEqual(resultTotalData, Integer.parseInt(Total[0]), FailureHandling.CONTINUE_ON_FAILURE))
	
	'cek apakah hlm  tersedia'
	if(WebUI.verifyElementVisible(findTestObject('Object Repository/User Management-User/'+
		'Page_List User/i_Action_datatable-icon-skip'),FailureHandling.OPTIONAL) == true) {
		
		'klik halaman 2'
		WebUI.click(findTestObject('Object Repository/Coupon/Page_List Coupon/paging/Page2'))
		
		'verify paging di page 2'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/Coupon/Page_List Coupon/paging/Page2'),
				'class', FailureHandling.CONTINUE_ON_FAILURE),
					'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'klik halaman 1'
		WebUI.click(findTestObject('Object Repository/Coupon/Page_List Coupon/paging/Page1'))
		
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/Coupon/Page_List Coupon/paging/Page1'),
				'class', FailureHandling.CONTINUE_ON_FAILURE),
					'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'klik button next page'
		WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/i_Action_datatable-icon-right'))
		
		'verify paging di page 2'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/Coupon/Page_List Coupon/paging/Page2'),
				'class', FailureHandling.CONTINUE_ON_FAILURE),
					'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'klik prev page'
		WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/i_Action_datatable-icon-left'))
		
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/Coupon/Page_List Coupon/paging/Page1'),
				'class', FailureHandling.CONTINUE_ON_FAILURE),
					'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'klik pada tombol skip'
		WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/i_Action_datatable-icon-skip'))
		
		'modify object laman terakhir'
		def modifyObjectmaxPage = WebUI.modifyObjectProperty(
			findTestObject('Object Repository/User Management-Role/modifyObject'),
			'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-coupon/app-msx-paging/"+
				"app-msx-datatable/section/ngx-datatable/div/datatable-footer/"+
					"div/datatable-pager/ul/li["+ (lastPage - 2) +"]", true)
		
		'verify paging di page terakhir'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(modifyObjectmaxPage,
			'class', FailureHandling.CONTINUE_ON_FAILURE), 'pages active ng-star-inserted',
				false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'click min page'
		WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/i_Action_datatable-icon-prev'))
		
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/Coupon/Page_List Coupon/paging/Page1'),
				'class', FailureHandling.CONTINUE_ON_FAILURE),
					'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
	}
}

def checkdialogConfirmation(isMandatoryComplete) {
	
	'cek apakah button simpan dinonaktifkan'
	if (WebUI.verifyElementNotHasAttribute(findTestObject('Object Repository/Coupon/Page_Add Coupon/button_Simpan'),
		'disabled' , GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
	
		'klik pada tombol simpan'
		WebUI.click(findTestObject('Object Repository/Coupon/Page_Add Coupon/button_Simpan'))
		
		'klik pada tombol ya, proses'
		WebUI.click(findTestObject('Object Repository/Coupon/Page_Add Coupon/button_Ya, proses'))
		
		if (WebUI.verifyElementPresent(findTestObject('Object Repository/Coupon/Page_Add Coupon/div_Success'), 
			GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
			
			'ambil hasil tambah data'
			String resultcheck = WebUI.getText(findTestObject('Object Repository/Coupon/Page_Add Coupon/div_Success'))
			
			'klik pada button ok'
			WebUI.click(findTestObject('Object Repository/Coupon/Page_Add Coupon/button_OK'))
			
			if (GlobalVariable.KondisiCekDB == 'Yes') {
				
				'panggil fungsi storeDB'
				WebUI.callTestCase(findTestCase('Test Cases/Coupon/CouponStoreDB'), [('Path') : ExcelPathCoupon],
					 FailureHandling.CONTINUE_ON_FAILURE)
			}
			
			'cek apakah result memunculkan hasil sukses'
			if (GlobalVariable.FlagFailed == 0 && isMandatoryComplete == 0 && resultcheck == 'Success') {
					
				'write to excel success'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Coupon', 0,
					GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
			}
			else {
				
				'tulis adanya error saat edit'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Coupon', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
						resultcheck)
			}
		}
		else {
			
			'tulis adanya error saat edit'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Coupon', GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
					GlobalVariable.FailedReasonSubmitError)
		}	
	}
	else if(isMandatoryComplete != 0) {
		
		'tulis adanya mandatory tidak lengkap'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Coupon', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
				GlobalVariable.FailedReasonMandatory)
	}
}

def checkVerifyPaging(Boolean isMatch) {
	if (isMatch == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Coupon', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 2) +
				';') + GlobalVariable.FailedReasonPagingError)

		GlobalVariable.FlagFailed = 1
	}
}

def checkVerifyReset(Boolean isMatch) {
	if (isMatch == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Coupon', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 2) +
				';') + GlobalVariable.FailedReasonSetFailed)

		GlobalVariable.FlagFailed = 1
	}
}