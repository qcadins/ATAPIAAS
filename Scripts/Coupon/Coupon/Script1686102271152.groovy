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
		
		'cek apakah tombol silang terlihat di web'
		if (WebUI.verifyElementVisible(findTestObject('Object Repository/Coupon/Page_Edit Coupon/tombolsilang'), 
			FailureHandling.OPTIONAL)) {
			
			'klik pada tombol silang menu'
			WebUI.click(findTestObject('Object Repository/Coupon/Page_Edit Coupon/tombolsilang'))
		}
		
		'check if action new/edit/detail'
		if (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 8).equalsIgnoreCase('New')) {
			
			'klik pada tombol tambah coupon'
			WebUI.click(findTestObject('Object Repository/Coupon/Page_List Coupon/a_Tambah'))
			
			checkddlTipekupon(conndev)
			
			checkddlTipeNilaikupon(conndev)
			
			checkddlTenant(conndev)
			
			inputparameter(findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 8))
			
			checkdialogConfirmation(isMandatoryComplete)
		}
		else if (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 8).equalsIgnoreCase('Edit')) {
			
			searchfunction()
			
			'klik tombol edit'
			WebUI.click(findTestObject('Object Repository/Coupon/Page_List Coupon/editButton'))
			
			inputparameter(findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 8))
			
			checkdialogConfirmation(isMandatoryComplete)
		}
		else if (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 8).equalsIgnoreCase('Detail')) {
			
			searchfunction()
			
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

def searchfunction() {
	'cari kupon yang akan dilakukan edit'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_List Coupon/input_Kode Kupon'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 15))
	
	'klik tombol cari'
	WebUI.click(findTestObject('Object Repository/Coupon/Page_List Coupon/button_Cari'))
	
	'cek apakah hasil search gagal'
	if(WebUI.verifyElementPresent(findTestObject('Object Repository/Coupon/Page_List Coupon/searchResult')
		, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Coupon', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 2) +
		';') + GlobalVariable.FailedReasonSearchFailed)
		
		continue
	}
}

def inputparameter(String action) {
	
	'deklarasi angka row'
	int rownum
	
	'jika action adalah new coupon'
	if (action == "New") {
		
		'mulai row dari 20'
		rownum = 20
	}
	else
	{
		'mulai row dari 31'
		rownum = 31
	}
	
	'input tipe kupon'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/inputtipekupon'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rownum))
	
	'input kode kupon'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/inputkodekupon'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rownum+1))
	
	'enter pada tipe kupon'
	WebUI.sendKeys(findTestObject('Object Repository/Coupon/Page_Edit Coupon/inputtipekupon'),
		Keys.chord(Keys.ENTER))
	
	'input tanggal mulai berlaku'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/input__CouponStartDate'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rownum+2))
	
	'input tanggal mulai berlaku'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/input__CouponEndDate'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rownum+3))
	
	'input tipe nilai kupon'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/inputtipenilaikupon'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rownum+4))
	
	'enter pada tipe nilai kupon'
	WebUI.sendKeys(findTestObject('Object Repository/Coupon/Page_Edit Coupon/inputtipenilaikupon'),
		Keys.chord(Keys.ENTER))
	
	'input tipe nilai kupon'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/input__nilaikupon'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rownum+5))
	
	'input jumlah kupon'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/input__jmlkupon'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rownum+6))
	
	'input maksimal redeem kupon'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/input__maxredeem'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rownum+7))
	
	'input minimal pembayaran untuk pakai kupon'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/input__minimumpayment'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rownum+8))
	
	'input tenant'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/tenantinput'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rownum+9))
	
	'enter pada tenant'
	WebUI.sendKeys(findTestObject('Object Repository/Coupon/Page_Edit Coupon/tenantinput'),
		Keys.chord(Keys.ENTER))
}

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
	else if (isMandatoryComplete != 0) {
		
		'tulis adanya mandatory tidak lengkap'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Coupon', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
				GlobalVariable.FailedReasonMandatory)
	}
	else {
		'tulis adanya error saat edit'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Coupon', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
				GlobalVariable.FailedReasonPercentage)
	}
}

def checkddlTipekupon(Connection conndev) {
	
	'klik pada tipe kupon'
	WebUI.click(findTestObject('Object Repository/Coupon/Page_Add Coupon/span_tipekupon'))
	
	'ambil list tipekupon'
	def elementCouponType = DriverFactory.getWebDriver().findElements(By.xpath('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-coupon/div[2]/div/div/div/div/form/div[1]/div/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div'))
	
	'ambil hitungan CouponType yang ada'
	int countWeb = (elementCouponType.size()) - 1
	
	'flag CouponType sesuai'
	int isCouponTypeFound = 0
	
	'ambil nama CouponType dari DB'
	ArrayList<String> namaCouponTypeDB = CustomKeywords.'coupon.couponverif.getTipeKuponList'(conndev)
	
	'nama-nama tipe saldo sedang aktif dari UI'
	ArrayList<String> namaCouponTypeUI = []
	
	'hitung banyak data didalam array DB'
	int countDB = namaCouponTypeDB.size()
	
	'jika hitungan di UI dan DB sesuai'
	if(countWeb == countDB){
		
		for(int i=1; i<=countWeb; i++) {
			
			'ambil object dari ddl'
			def modifyNamaCouponType = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/modifyObject'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-coupon/div[2]/div/div/div/div/form/div[1]/div/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div["+(i+1)+"]/span", true)
			
			'tambahkan nama tipe saldo ke array'
			String data = WebUI.getText(modifyNamaCouponType)
			namaCouponTypeUI.add(data)
		}
		
		'cek setiap data di UI dengan data di DB sebagai pembanding'
		for (String tipe : namaCouponTypeDB){
			
			'jika ada data yang tidak terdapat pada arraylist yang lain'
			if (!namaCouponTypeUI.contains(tipe)){
				
				'ada data yang tidak match'
				isCouponTypeFound = 0;
				'berhentikan loop'
				break;
			}
			'kondisi ini bisa ditemui jika data match'
			isCouponTypeFound = 1
		}
			
	}
	else if(isCouponTypeFound == 0 || countWeb != countDB){
		
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Coupon', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonDDL)
	}
	
	'klik pada tipe kupon'
	WebUI.click(findTestObject('Object Repository/Coupon/Page_Add Coupon/span_tipekupon'))
}

def checkddlTipeNilaikupon(Connection conndev) {
	
	'klik pada ddl tipenilai kupon'
	WebUI.click(findTestObject('Object Repository/Coupon/Page_Add Coupon/span_tipenilaikupon'))
	
	'ambil list tipeNilaikupon'
	def elementTipeNilaiKupon = DriverFactory.getWebDriver().findElements(By.xpath('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-coupon/div[2]/div/div/div/div/form/div[5]/div/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div'))
	
	'ambil hitungan TipeNilaiKupon yang ada'
	int countWeb = (elementTipeNilaiKupon.size()) - 1
	
	'flag TipeNilaiKupon sesuai'
	int isTipeNilaiKuponFound = 0
	
	'ambil nama TipeNilaiKupon dari DB'
	ArrayList<String> namaTipeNilaiKuponDB = CustomKeywords.'coupon.couponverif.getTipeNilaiKuponList'(conndev)
	
	'nama-nama tipe saldo sedang aktif dari UI'
	ArrayList<String> namaTipeNilaiKuponUI = []
	
	'hitung banyak data didalam array DB'
	int countDB = namaTipeNilaiKuponDB.size()
	
	'jika hitungan di UI dan DB sesuai'
	if (countWeb == countDB) {
		
		for (int i=1; i<=countWeb; i++) {
			
			'ambil object dari ddl'
			def modifyNamaTipeNilaiKupon = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/modifyObject'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-coupon/div[2]/div/div/div/div/form/div[5]/div/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div["+(i+1)+"]/span", true)
			
			'tambahkan nama tipe saldo ke array'
			String data = WebUI.getText(modifyNamaTipeNilaiKupon)
			namaTipeNilaiKuponUI.add(data)
		}
		
		'cek setiap data di UI dengan data di DB sebagai pembanding'
		for (String tipe : namaTipeNilaiKuponDB) {
			
			'jika ada data yang tidak terdapat pada arraylist yang lain'
			if (!namaTipeNilaiKuponUI.contains(tipe)){
				
				'ada data yang tidak match'
				isTipeNilaiKuponFound = 0;
				'berhentikan loop'
				break;
			}
			'kondisi ini bisa ditemui jika data match'
			isTipeNilaiKuponFound = 1
		}
			
	}
	else if (isTipeNilaiKuponFound == 0 || countWeb != countDB) {
		
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Coupon', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonDDL)
	}
	
	'klik pada ddl tipenilai kupon'
	WebUI.click(findTestObject('Object Repository/Coupon/Page_Add Coupon/span_tipenilaikupon'))
}

def checkddlTenant(Connection conndev) {
	
	'klik pada dropdownlist tenant'
	WebUI.click(findTestObject('Object Repository/Coupon/Page_Add Coupon/span_tenant'))
	
	'ambil list tenant'
	def elementTenant = DriverFactory.getWebDriver().findElements(By.xpath('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-coupon/div[2]/div/div/div/div/form/div[10]/div/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div'))
	
	'ambil hitungan Tenant yang ada'
	int countWeb = (elementTenant.size()) - 1
	
	'flag Tenant sesuai'
	int isTenantFound = 0
	
	'ambil nama Tenant dari DB'
	ArrayList<String> namaTenantDB = CustomKeywords.'coupon.couponverif.getTenantList'(conndev)
	
	'nama-nama tipe saldo sedang aktif dari UI'
	ArrayList<String> namaTenantUI = []
	
	'hitung banyak data didalam array DB'
	int countDB = namaTenantDB.size()
	
	'jika hitungan di UI dan DB sesuai'
	if (countWeb == countDB) {
		
		for (int i=1; i<=countWeb; i++) {
			
			'ambil object dari ddl'
			def modifyNamaTenant = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/modifyObject'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-coupon/div[2]/div/div/div/div/form/div[5]/div/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div["+(i+1)+"]/span", true)
			
			'tambahkan nama tipe saldo ke array'
			String data = WebUI.getText(modifyNamaTenant)
			namaTenantUI.add(data)
		}
		
		'cek setiap data di UI dengan data di DB sebagai pembanding'
		for (String tipe : namaTenantDB) {
			
			'jika ada data yang tidak terdapat pada arraylist yang lain'
			if (!namaTenantUI.contains(tipe)){
				
				'ada data yang tidak match'
				isTenantFound = 0;
				'berhentikan loop'
				break;
			}
			'kondisi ini bisa ditemui jika data match'
			isTenantFound = 1
		}
			
	}
	else if (isTenantFound == 0 || countWeb != countDB) {
		
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Coupon', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonDDL)
	}
	
	'klik pada dropdownlist tenant'
	WebUI.click(findTestObject('Object Repository/Coupon/Page_Add Coupon/span_tenant'))
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