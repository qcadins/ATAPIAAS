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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Edit Profile'
int countColumnEdit = findTestData(ExcelPathCoupon).columnNumbers

'deklarasi koneksi ke Database adins_apiaas_uat'
Connection conndev = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_esign'()

'panggil fungsi login'
WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : sheet, ('SheetName') : sheet,
	('Path') : ExcelPathCoupon], FailureHandling.STOP_ON_FAILURE)

'klik pada tombol untuk span menu'
WebUI.click(findTestObject('Object Repository/Coupon/Page_Balance/Spanmenu'))

'klik pada tombol coupon'
WebUI.click(findTestObject('Object Repository/Coupon/Page_Balance/span_Coupon'))

checkPaging(conndev)

for (GlobalVariable.NumOfColumn = 2; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++) {
	
	'set penanda error menjadi 0'
	GlobalVariable.FlagFailed = 0
		
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).length() == 0) {
		
		break
	
	} else if (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
		
		'declare isMmandatory Complete'
		int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Is Mandatory Complete')))
		
		openMenu()
		
		'check if action new/edit/detail'
		if (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')).equalsIgnoreCase('New')) {
			
			'klik pada tombol tambah coupon'
			WebUI.click(findTestObject('Object Repository/Coupon/Page_List Coupon/a_Tambah'))
			
			'ambil nama CouponType dari DB'
			ArrayList namaCouponTypeDB = CustomKeywords.'coupon.CouponVerif.getTipeKuponList'(conndev)
			
			'ambil nama TipeNilaiKupon dari DB'
			ArrayList namaTipeNilaiKuponDB = CustomKeywords.'coupon.CouponVerif.getTipeNilaiKuponList'(conndev)
			
			'ambil nama Tenant dari DB'
			ArrayList namaTenantDB = CustomKeywords.'coupon.CouponVerif.getTenantList'(conndev)
			
			'panggil fungsi check ddl di DB dan UI'
			checkDDL(findTestObject('Object Repository/Coupon/Page_Add Coupon/inputtipekupon'), namaCouponTypeDB, 'DDL Tipe Kupon')
			
			'panggil fungsi check ddl di DB dan UI'
			checkDDL(findTestObject('Object Repository/Coupon/Page_Add Coupon/inputtipenilaikupon'), namaTipeNilaiKuponDB, 'DDL Tipe Nilai Kupon')
			
			'panggil fungsi check ddl di DB dan UI'
			checkDDL(findTestObject('Object Repository/Coupon/Page_Add Coupon/tenantinput'), namaTenantDB, 'DDL Tenant')
			
			'panggil fungsi input data dari excel'
			inputparameter(findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')))
			
			'cek apakah field kosong setelah klik cancel'
			if (GlobalVariable.NumOfColumn == 2) {
				
				'klik pada tombol batal'
				WebUI.click(findTestObject('Object Repository/Coupon/Page_Add Coupon/button_Batal'))
				
				'klik pada tombol tambah kupon kembali'
				WebUI.click(findTestObject('Object Repository/Coupon/Page_List Coupon/a_Tambah'))
				
				'verifikasi field kosong'
				checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(
					findTestObject('Object Repository/Coupon/Page_Add Coupon/inputtipekupon'),
					'value', FailureHandling.CONTINUE_ON_FAILURE),'',
						false, FailureHandling.CONTINUE_ON_FAILURE), 'Field tipe kupon tidak kosong')
				
				'verifikasi field kosong'
				checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(
					findTestObject('Object Repository/Coupon/Page_Add Coupon/input__couponCode'),
					'value', FailureHandling.CONTINUE_ON_FAILURE),'',
						false, FailureHandling.CONTINUE_ON_FAILURE), 'Field kode kupon tidak kosong')
				
				'verifikasi field kosong'
				checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(
					findTestObject('Object Repository/Coupon/Page_Add Coupon/input__couponStartDate'),
					'value', FailureHandling.CONTINUE_ON_FAILURE),'',
						false, FailureHandling.CONTINUE_ON_FAILURE), 'Field coupon start date tidak kosong')
				
				'verifikasi field kosong'
				checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(
					findTestObject('Object Repository/Coupon/Page_Add Coupon/input__couponEndDate'),
					'value', FailureHandling.CONTINUE_ON_FAILURE),'',
						false, FailureHandling.CONTINUE_ON_FAILURE), 'Field coupon end date tidak kosong')
				
				'verifikasi field kosong'
				checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(
					findTestObject('Object Repository/Coupon/Page_Add Coupon/inputtipenilaikupon'),
					'value', FailureHandling.CONTINUE_ON_FAILURE),'',
						false, FailureHandling.CONTINUE_ON_FAILURE), 'Field tipe nilai kupon tidak kosong')
				
				'verifikasi field kosong'
				checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(
					findTestObject('Object Repository/Coupon/Page_Add Coupon/input__nilaikupon'),
					'value', FailureHandling.CONTINUE_ON_FAILURE),'',
						false, FailureHandling.CONTINUE_ON_FAILURE), 'Field nilai kupon tidak kosong')
				
				'verifikasi field kosong'
				checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(
					findTestObject('Object Repository/Coupon/Page_Add Coupon/input__jmlkupon'),
					'value', FailureHandling.CONTINUE_ON_FAILURE),'',
						false, FailureHandling.CONTINUE_ON_FAILURE), 'Field jumlah kupon tidak kosong')
				
				'verifikasi field kosong'
				checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(
					findTestObject('Object Repository/Coupon/Page_Add Coupon/input__maxredeem'),
					'value', FailureHandling.CONTINUE_ON_FAILURE),'',
						false, FailureHandling.CONTINUE_ON_FAILURE), 'Field max redeem tidak kosong')
				
				'verifikasi field kosong'
				checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(
					findTestObject('Object Repository/Coupon/Page_Add Coupon/input__minimumPayment'),
					'value', FailureHandling.CONTINUE_ON_FAILURE),'',
						false, FailureHandling.CONTINUE_ON_FAILURE), 'Field minimum payment tidak kosong')
				
				'verifikasi field kosong'
				checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(
					findTestObject('Object Repository/Coupon/Page_Add Coupon/tenantinput'),
					'value', FailureHandling.CONTINUE_ON_FAILURE),'',
						false, FailureHandling.CONTINUE_ON_FAILURE), 'Field tenant tidak kosong')
				
				'panggil fungsi input data dari excel'
				inputparameter(findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')))
			}
			
			'panggil fungsi menjalankan konfirmasi dialog'
			checkdialogConfirmation(isMandatoryComplete)
			
			'cek apa perlu lakukan copy link'
			copylinkfunction()
			
			if (GlobalVariable.FlagFailed == 0) {
				'check after add new kupon'
				verifyAfterAddorEdit()
			}
			
		} else if (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')).equalsIgnoreCase('Edit')) {
			
			'panggil fungsi search'
			searchfunction()
			
			'cek apakah hasil search gagal'
			if(WebUI.verifyElementPresent(findTestObject('Object Repository/Coupon/Page_List Coupon/searchResult')
				, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
				
				GlobalVariable.FlagFailed = 1
				
				'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) +
				';') + GlobalVariable.FailedReasonSearchFailed)
				
				continue
			}
			
			'klik tombol edit'
			WebUI.click(findTestObject('Object Repository/Coupon/Page_List Coupon/editButton'))
			
			'panggil fungsi cek sebelum edit'
			checkDBbeforeEdit(conndev)
			
			'panggil fungsi input data dari excel'
			inputparameter(findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')))
			
			'panggil fungsi menjalankan konfirmasi dialog'
			checkdialogConfirmation(isMandatoryComplete)
			
			'cek apa perlu lakukan copy link'
			copylinkfunction()
			
			'check after edit'
			verifyAfterAddorEdit()
		}
		else if (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')).equalsIgnoreCase('Detail')) {
			
			'panggil fungsi search'
			searchfunction()
			
			'cek apakah hasil search gagal'
			if (WebUI.verifyElementPresent(findTestObject('Object Repository/Coupon/Page_List Coupon/searchResult')
				, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
				
				GlobalVariable.FlagFailed = 1
			
				'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) +
				';') + GlobalVariable.FailedReasonSearchFailed)
				
				continue
			}
			
			'klik tombol detail'
			WebUI.click(findTestObject('Object Repository/Coupon/Page_List Coupon/detailbutton'))
			
			'inisialisasi array detail dari web'
			ArrayList detailresultWeb = []
			
			'inisialisasi array detail dari DB'
			ArrayList detailresultDB = CustomKeywords.'coupon.CouponVerif.getDetailCoupon'(conndev, 
				findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Kode Kupon')))
			
			'tambahkan data tenant ke array'
			detailresultWeb.add(WebUI.getText(findTestObject('Object Repository/Coupon/Page_List Coupon/tenant')))
			
			'tambahkan data tenant ke minpayment'
			detailresultWeb.add(WebUI.getText(findTestObject('Object Repository/Coupon/Page_List Coupon/minimalpembayaran')).replace('.', ''))

			'tambahkan data tenant ke maxredeem'
			detailresultWeb.add(WebUI.getText(findTestObject('Object Repository/Coupon/Page_List Coupon/makspenebusan')))
			
			'cek apakah ada data yang tidak sesuai'
			for (int i = 0 ; i < detailresultDB.size; i++) {
				
				'jika ada data yang tidak sesuai'
				if (detailresultWeb[i] != detailresultDB[i]) {

					GlobalVariable.FlagFailed = 1
					
					'tulis adanya error pada sistem web'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
						GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
							GlobalVariable.FailedReasonDetailNotMatch)
					
					break
				} else {
					'write to excel success'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Status'),
						GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
				}
			}
			
			'klik pada tombol silang untuk mengakhiri sesi detail'
			WebUI.click(findTestObject('Object Repository/Coupon/Page_List Coupon/span_'))
		}
	}
}

'fungsi untuk menutup browser'
WebUI.closeBrowser()

def searchfunction() {
	
	'cari kupon yang akan dilakukan edit'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_List Coupon/input_Kode Kupon'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Kode Kupon')))
	
	'klik tombol cari'
	WebUI.click(findTestObject('Object Repository/Coupon/Page_List Coupon/button_Cari'))
}

def openMenu() {
	
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
}

def copylinkfunction() {
	
	'panggil fungsi kembali ke menu'
	openMenu()
	
	'panggil fungsi search kupon'
	searchfunction()
	
	'cek apakah perlu copy kode kupon'
	if (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('CopyCouponCode?')) == 'Yes') {
		
		'klik pada tombol copy kode kupon'
		WebUI.click(findTestObject('Object Repository/Coupon/Page_List Coupon/copybutton'))
		
		'jika tidak ada notifikasi sukses'
		if (!WebUI.getText(findTestObject('Object Repository/Coupon/Page_List Coupon/copySuccessnotif')) ==
			' Kode Kupon berhasil disalin ') {
			
			GlobalVariable.FlagFailed = 1
			
			'tulis ada masalah pada button copy'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) +
			';') + GlobalVariable.FailedReasonButtonCopy)
		}
	}
}

def inputparameter(String action) {
	
	'jika melakukan tambah kupon'
	if (action == 'New') {
		
		'input tipe kupon'
		WebUI.setText(findTestObject('Object Repository/Coupon/Page_Add Coupon/inputtipekupon'),
				findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$TipeKupon')))
		
		'enter pada tipe kupon'
		WebUI.sendKeys(findTestObject('Object Repository/Coupon/Page_Add Coupon/inputtipekupon'),
			Keys.chord(Keys.ENTER))
		
		'input kode kupon'
		WebUI.setText(findTestObject('Object Repository/Coupon/Page_Add Coupon/input__couponCode'),
				findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$KodeKupon')))
		
		'input tanggal mulai berlaku'
		WebUI.setText(findTestObject('Object Repository/Coupon/Page_Add Coupon/input__couponStartDate'),
				findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$TanggalMulaiBerlaku')))
		
		'input tanggal mulai berlaku'
		WebUI.setText(findTestObject('Object Repository/Coupon/Page_Add Coupon/input__couponEndDate'),
				findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$TanggalTerakhirBerlaku')))
		
		'input tipe nilai kupon'
		WebUI.setText(findTestObject('Object Repository/Coupon/Page_Add Coupon/inputtipenilaikupon'),
				findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$TipeNilaiKupon')))
		
		'enter pada tipe nilai kupon'
		WebUI.sendKeys(findTestObject('Object Repository/Coupon/Page_Add Coupon/inputtipenilaikupon'),
			Keys.chord(Keys.ENTER))
		
		'input tipe nilai kupon'
		WebUI.setText(findTestObject('Object Repository/Coupon/Page_Add Coupon/input__nilaikupon'),
				findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$NilaiKupon')))
		
		'input jumlah kupon'
		WebUI.setText(findTestObject('Object Repository/Coupon/Page_Add Coupon/input__jmlkupon'),
				findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$JumlahKupon')))
		
		'input maksimal redeem kupon'
		WebUI.setText(findTestObject('Object Repository/Coupon/Page_Add Coupon/input__maxredeem'),
				findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$MaksimalPenebusan')))
		
		'input minimal pembayaran untuk pakai kupon'
		WebUI.setText(findTestObject('Object Repository/Coupon/Page_Add Coupon/input__minimumpayment'),
				findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$MinimalPembayaran')))
		
		'input tenant'
		WebUI.setText(findTestObject('Object Repository/Coupon/Page_Add Coupon/tenantinput'),
				findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$NamaTenant')))
		
		'enter pada tenant'
		WebUI.sendKeys(findTestObject('Object Repository/Coupon/Page_Add Coupon/tenantinput'),
			Keys.chord(Keys.ENTER))
	}
	else {
		
		'input tipe kupon'
		WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/inputtipekupon'),
				findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$TipeKupon')))
		
		'enter pada tipe kupon'
		WebUI.sendKeys(findTestObject('Object Repository/Coupon/Page_Edit Coupon/inputtipekupon'),
			Keys.chord(Keys.ENTER))
		
		'input kode kupon'
		WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/inputkodekupon'),
				findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$KodeKupon')))
		
		'input tanggal mulai berlaku'
		WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/input__CouponStartDate'),
				findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$TanggalMulaiBerlaku')))
		
		'input tanggal mulai berlaku'
		WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/input__CouponEndDate'),
				findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$TanggalTerakhirBerlaku')))
		
		'input tipe nilai kupon'
		WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/inputtipenilaikupon'),
				findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$TipeNilaiKupon')))
		
		'enter pada tipe nilai kupon'
		WebUI.sendKeys(findTestObject('Object Repository/Coupon/Page_Edit Coupon/inputtipenilaikupon'),
			Keys.chord(Keys.ENTER))
		
		'input tipe nilai kupon'
		WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/input__nilaikupon'),
				findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$NilaiKupon')))
		
		'input jumlah kupon'
		WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/input__jmlkupon'),
				findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$JumlahKupon')))
		
		'input maksimal redeem kupon'
		WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/input__maxredeem'),
				findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$MaksimalPenebusan')))
		
		'input minimal pembayaran untuk pakai kupon'
		WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/input__minimumpayment'),
				findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$MinimalPembayaran')))
		
		'input tenant'
		WebUI.setText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/tenantinput'),
				findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$NamaTenant')))
		
		'enter pada tenant'
		WebUI.sendKeys(findTestObject('Object Repository/Coupon/Page_Edit Coupon/tenantinput'),
			Keys.chord(Keys.ENTER))
	}
}

def checkPaging(Connection conndev) {
	
	'input tipe kupon'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_List Coupon/inputtipekupon'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Tipe Kupon')))
	
	'enter pada tipe kupon'
	WebUI.sendKeys(findTestObject('Object Repository/Coupon/Page_List Coupon/inputtipekupon'),
		Keys.chord(Keys.ENTER))
	
	'input tipe nilai kupon'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_List Coupon/inputtipenilaikupon'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Tipe Nilai Kupon')))
	
	'enter pada tipe nilai kupon'
	WebUI.sendKeys(findTestObject('Object Repository/Coupon/Page_List Coupon/inputtipenilaikupon'),
		Keys.chord(Keys.ENTER))
	
	'input tanggal mulai berlaku akhir'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_List Coupon/Tanggal Mulai Berlaku Awal'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Tanggal Mulai berlaku awal')))
	
	'input tanggal mulai berlaku akhir'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_List Coupon/Tanggal Terakhir Berlaku Awal'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Tanggal Terakhir berlaku awal')))
	
	'input tanggal mulai berlaku akhir'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_List Coupon/tanggal Mulai Berlaku Akhir'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Tanggal Mulai berlaku akhir')))
	
	'input tanggal mulai berlaku akhir'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_List Coupon/Tanggal Terakhir Berlaku Akhir'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Tanggal Terakhir berlaku akhir')))
	
	'klik pada tombol cari'
	WebUI.click(findTestObject('Object Repository/Coupon/Page_List Coupon/button_Cari'))
	
	'cek apakah hasil search gagal'
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/Coupon/Page_List Coupon/searchResult')
		, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		
		GlobalVariable.FlagFailed = 1
		
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) +
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
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Tipe Kupon')))
	
	'enter pada tipe kupon'
	WebUI.sendKeys(findTestObject('Object Repository/Coupon/Page_List Coupon/inputtipekupon'),
		Keys.chord(Keys.ENTER))
	
	'input tipe nilai kupon'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_List Coupon/inputtipenilaikupon'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Tipe Nilai Kupon')))
	
	'enter pada tipe nilai kupon'
	WebUI.sendKeys(findTestObject('Object Repository/Coupon/Page_List Coupon/inputtipenilaikupon'),
		Keys.chord(Keys.ENTER))
	
	'klik pada tombol cari'
	WebUI.click(findTestObject('Object Repository/Coupon/Page_List Coupon/button_Cari'))
	
	'cek apakah hasil search gagal'
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/Coupon/Page_List Coupon/searchResult')
		, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		
		GlobalVariable.FlagFailed = 1
		
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) +
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
	
	'ambil waktu start untuk filter hasil qury'
	String thestartDate = callStartDate()
	
	'ambil total data dari db'
	int resultTotalData = CustomKeywords.'coupon.CouponVerif.getCouponTotal'(conndev, thestartDate)

	'verify total data role'
	checkVerifyPaging(WebUI.verifyEqual(resultTotalData, Integer.parseInt(Total[0]), FailureHandling.CONTINUE_ON_FAILURE))
	
	'cek apakah hlm  tersedia'
	if (WebUI.verifyElementVisible(
		findTestObject('Object Repository/User Management-User/Page_List User/gotoLast_page'),
		FailureHandling.OPTIONAL) == true) {
		
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
		WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/next_page'))
		
		'verify paging di page 2'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/Coupon/Page_List Coupon/paging/Page2'),
				'class', FailureHandling.CONTINUE_ON_FAILURE),
					'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'klik prev page'
		WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/prev_page'))
		
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/Coupon/Page_List Coupon/paging/Page1'),
				'class', FailureHandling.CONTINUE_ON_FAILURE),
					'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'klik pada tombol skip'
		WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/gotoLast_page'))
		
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
		WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/gotoFirst_page'))
		
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
			
			'cek apakah result memunculkan hasil sukses'
			if (GlobalVariable.FlagFailed == 0 && isMandatoryComplete == 0 && resultcheck == 'Success') {
					
				'write to excel success'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0,
					GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
				
				if (GlobalVariable.KondisiCekDB == 'Yes') {
					
					'panggil fungsi storeDB'
					WebUI.callTestCase(findTestCase('Test Cases/Coupon/CouponStoreDB'), [('Path') : ExcelPathCoupon],
						 FailureHandling.CONTINUE_ON_FAILURE)
				}
			} else {
				
				GlobalVariable.FlagFailed = 1
				
				'tulis adanya error saat edit'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
						'<' + resultcheck  + '>')
			}
		} else {
			
			GlobalVariable.FlagFailed = 1
			
			'tulis adanya error saat edit'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
					GlobalVariable.FailedReasonSubmitError)
		}	
	}
	else if (isMandatoryComplete != 0) {
		
		GlobalVariable.FlagFailed = 1
		
		'tulis adanya mandatory tidak lengkap'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
				GlobalVariable.FailedReasonMandatory)
	}
	else {
		
		GlobalVariable.FlagFailed = 1
		
		'tulis adanya error saat edit'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
				GlobalVariable.FailedReasonPercentage)
	}
}

def checkDBbeforeEdit(Connection conndev) {
	
	'deklarasi array list dari UI'
	ArrayList editUI = []
	
	'deklarasi array list dari DB'
	ArrayList editDB = CustomKeywords.'coupon.CouponVerif.getAddEditCoupon'(conndev, 
	findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Kode Kupon')))
	
	'tambahkan data ke array editUI'
	editUI.add(WebUI.getText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/CheckTipeKupon')))

	'tambahkan data ke array editUI'
	editUI.add(WebUI.getAttribute(findTestObject('Object Repository/Coupon/Page_Edit Coupon/inputkodekupon'), 'value'))
	
	'tambahkan data ke array editUI'
	editUI.add(WebUI.getAttribute(findTestObject('Object Repository/Coupon/Page_Edit Coupon/input__CouponStartDate'), 'value'))

	'tambahkan data ke array editUI'
	editUI.add(WebUI.getAttribute(findTestObject('Object Repository/Coupon/Page_Edit Coupon/input__CouponEndDate'), 'value'))
	
	'tambahkan data ke array editUI'
	editUI.add(WebUI.getText(findTestObject('Object Repository/Coupon/Page_Edit Coupon/CheckTipeNilaiKupon')))
	
	'tambahkan data ke array editUI'
	editUI.add(WebUI.getAttribute(findTestObject('Object Repository/Coupon/Page_Edit Coupon/input__nilaikupon'), 'value'))

	'tambahkan data ke array editUI'
	editUI.add(WebUI.getAttribute(findTestObject('Object Repository/Coupon/Page_Edit Coupon/input__jmlkupon'), 'value'))

	'tambahkan data ke array editUI'
	editUI.add(WebUI.getAttribute(findTestObject('Object Repository/Coupon/Page_Edit Coupon/input__maxredeem'), 'value'))

	'tambahkan data ke array editUI'
	editUI.add(WebUI.getAttribute(findTestObject('Object Repository/Coupon/Page_Edit Coupon/input__minimumpayment'), 'value'))
	
	'loop untuk verify data equal'
	for (int i = 0; i < editDB.size(); i++) {
		
		'jika ada data yang tidak sesuai tulis error'
		if (editUI[i] != editDB[i]) {
			
			GlobalVariable.FlagFailed = 1
			
			'tulis adanya error pada sistem web'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
					GlobalVariable.FailedReasonEditVerif)
		}
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
	
	'looping untuk get ddl kedalam array'
	for (i = 1; i < variable.size(); i++) {
		'modify object DDL'
		modifyObjectDDL = WebUI.modifyObjectProperty(findTestObject('Object Repository/Coupon/modifyObject'), 'xpath', 'equals', ((('//*[@id=\'' +
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

def checkVerifyPaging(Boolean isMatch) {
	if (isMatch == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) +
				';') + GlobalVariable.FailedReasonPagingError)

		GlobalVariable.FlagFailed = 1
	}
}

def checkVerifyReset(Boolean isMatch) {
	if (isMatch == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) +
				';') + GlobalVariable.FailedReasonSetFailed)

		GlobalVariable.FlagFailed = 1
	}
}
	
def callStartDate() {
	
	LocalDate currentDate = LocalDate.now()
	
	LocalDate startDate = currentDate.withDayOfMonth(1)
	
	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
	
	String formattedStartDate = startDate.format(dateFormatter)
	
	return formattedStartDate
}

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
	if(isMatch == false){
		GlobalVariable.FlagFailed = 1
		
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
		GlobalVariable.FailedReasonVerifyEqualorMatch + reason)
		
	}
}

def verifyAfterAddorEdit() {
	'cari kupon yang akan dilakukan edit/add'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_List Coupon/input_Kode Kupon'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$KodeKupon')))
	
	'input tanggal mulai berlaku akhir'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_List Coupon/Tanggal Mulai Berlaku Awal'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$TanggalMulaiBerlaku')))
	
	'input tanggal mulai berlaku akhir'
	WebUI.setText(findTestObject('Object Repository/Coupon/Page_List Coupon/Tanggal Terakhir Berlaku Awal'),
			findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$TanggalTerakhirBerlaku')))
	
	'klik tombol cari'
	WebUI.click(findTestObject('Object Repository/Coupon/Page_List Coupon/button_Cari'))
	
	'verify tipe kupon'
	checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Coupon/label_TipeKupon')), findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$TipeKupon')), false, FailureHandling.CONTINUE_ON_FAILURE), ' Tipe Kupon')
	
	parsedate = CustomKeywords.'customizeKeyword.ParseDate.parseDateFormat'(findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$TanggalMulaiBerlaku')), 'yyyy-MM-dd', 'dd-MMM-yyyy').toUpperCase()
		
	'verify tanggal mulai berlaku'
	checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Coupon/label_TanggalMulai')).toUpperCase(), parsedate, false, FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal mulai berlaku')
	
	parsedate = CustomKeywords.'customizeKeyword.ParseDate.parseDateFormat'(findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$TanggalTerakhirBerlaku')), 'yyyy-MM-dd', 'dd-MMM-yyyy').toUpperCase()
	
	'verify tanggal terakhir berlaku'
	checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Coupon/label_TanggalTerakhir')).toUpperCase(), parsedate, false, FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal terakhir berlaku')
	
	'verify kode kupon'
	checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Coupon/label_KodeKupon')), findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$KodeKupon')), false, FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal kode kupon')
	
	'verify nilai kupon'
	checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Coupon/label_NilaiKupon')).replace('.',''), findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$NilaiKupon')), false, FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal nilai kupon')
	
	'verify tipe nilai kupon'
	checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Coupon/label_TipeNilai')), findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$TipeNilaiKupon')), false, FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal tipe nilai kupon')
	
	'verify kuantitas'
	checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Coupon/label_Kuantitas')), findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$JumlahKupon')) + '/' + findTestData(ExcelPathCoupon).getValue(GlobalVariable.NumOfColumn, rowExcel('$JumlahKupon')), false, FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal terakhir kuantitas')
}

def rowExcel(String cellValue) {
	return CustomKeywords.'writeToExcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}