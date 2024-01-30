import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import java.sql.Connection

import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writetoexcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

conndev = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_public'()

'mendapat jumlah kolom dari sheet Edit Profile'
int countColumnEdit = findTestData(ExcelPathAPIKey).columnNumbers, isLoggedin = 0

'pindah testcase sesuai jumlah di excel'
for (GlobalVariable.NumOfColumn = 2; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++) {
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).length() == 0) {
		break
	} else if (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
		'reset failed dari 0'
		GlobalVariable.FlagFailed = 0
		
		'angka untuk menghitung data mandatory yang tidak terpenuhi'
		int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('Mandatory Complete')))
		
		if (isLoggedin == 0) {
			'panggil fungsi login'
			WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'Key', ('SheetName') : sheet,
				('Path') : ExcelPathAPIKey, ('Username') : '$Username Login', ('Password') : '$Password Login',], FailureHandling.STOP_ON_FAILURE)
			
			isLoggedin = 1
		}
		
		WebUI.delay(GlobalVariable.Timeout)
		
		'klik pada tombol garis tiga'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/spanMenu'))
		
		WebUI.delay(1)
		
		'klik pada API KEY'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/span_API Key'))
		
		'cek apakah tombol menu dalam jangkauan web'
		if (WebUI.verifyElementVisible(findTestObject('Object Repository/User Management-Role/Page_List Roles/tombolX_menu'), FailureHandling.OPTIONAL)) {
			'klik pada tombol silang menu'
			WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/tombolX_menu'))
		}
		
		if (GlobalVariable.NumOfColumn == 2) {
			checkpaging(conndev)
		}
		
		'cek perlu nya pemanggilan fungsi add atau edit'
		if (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')) == 'Add') {
			'panggil fungsi Add API KEY'
			WebUI.callTestCase(findTestCase('Test Cases/API Key/AddAPIKey'),
				[:], FailureHandling.CONTINUE_ON_FAILURE)
			
			'cek apakah perlu copy api key yang baru ditambah atau diedit'
			checkCopyLink(isMandatoryComplete)
		} else if (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')) == 'Edit') {
			'panggil fungsi Edit API Key'
			WebUI.callTestCase(findTestCase('Test Cases/API Key/EditAPIKey'),
				[('conn'): conndev], FailureHandling.CONTINUE_ON_FAILURE)
			
			'cek apakah perlu copy api key yang baru ditambah atau diedit'
			checkCopyLink(isMandatoryComplete)
		}

		'kondisi jika tidak ada error'
		if (GlobalVariable.FlagFailed == 0) {
			'tulis status sukses pada excel'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet,
				GlobalVariable.NumOfColumn, GlobalVariable.StatusSuccess, GlobalVariable.SuccessReason)
		}
	}
}

'tutup browser jika hasil sudah sesuai'
WebUI.closeBrowser()

def checkpaging(Connection conn) {
	String optiontipe, optionstatus
	
	int arrayIndex = 0
	
	WebUI.delay(5)
	
	'dapatkan detail tenant dari user yang login'
	ArrayList resultTenant = CustomKeywords.'apikey.CheckAPIKey.getTenantCodeName'(conn, findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('$Username Login')))
	
	'verify tenant code'
	checkVerifyEqualorMatch(WebUI.verifyMatch(resultTenant[arrayIndex++],
		WebUI.getAttribute(findTestObject('API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input_TenantCode'),
			'value', FailureHandling.OPTIONAL), false, FailureHandling.CONTINUE_ON_FAILURE), ' tenant code')
		
	'verify tenant name'
	checkVerifyEqualorMatch(WebUI.verifyMatch(resultTenant[arrayIndex++],
		WebUI.getAttribute(findTestObject('API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input_TenantName'),
			'value', FailureHandling.OPTIONAL), false, FailureHandling.CONTINUE_ON_FAILURE), ' tenant name')
	
	'input tipe API'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Api Key List/input_tipeapi_list'), findTestData(
			ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('SearchTipeAPI')))
	
	'select tipe API'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Api Key List/input_tipeapi_list'), Keys.chord(
			Keys.ENTER))
	
	'input status API'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Api Key List/input_statusapi_list'), findTestData(
			ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('SearchStatusAPI')))
	
	'select status API'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Api Key List/input_statusapi_list'), Keys.chord(
			Keys.ENTER))
	
	'klik pada button cari'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/button_Cari'))
	
	'klik tombol set ulang'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/button_Set Ulang'))
	
	'klik pada ddl tipe API KEY'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/ddlAPIKEYType'))
	
	'simpan pilihan utama dari tipe API KEY'
	optiontipe = WebUI.getAttribute(findTestObject('Object Repository/API_KEY/Page_Api Key List/input tipe'),
		'aria-activedescendant')
	
	'klik pada ddl tipe API KEY'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/ddlAPIKEYType'))
	
	'klik pada ddl Status API KEY'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/ddlAPIKEYStatus'))
	
	'simpan pilihan utama dari status API KEY'
	optionstatus = WebUI.getAttribute(findTestObject('Object Repository/API_KEY/Page_Api Key List/input status'),
		'aria-activedescendant')
	
	'klik pada ddl Status API KEY'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/ddlAPIKEYStatus'))
	
	'klik pada tombol cari'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/button_Cari'))
	
	'jika semua pilihan ddl kembali ke "ALL"'
	if (optiontipe.contains('-0') && optionstatus.contains('-0')) {
		'klik tombol cari'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/button_Cari'))
	}
	
	checkPagingDetail(conn)
}

def checkPagingDetail(Connection conn) {
	'kumpulan string dari DB'
	String totaldataDB = CustomKeywords.'apikey.CheckAPIKey.getTotalAPIKeyfromDB'(conn,
		findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('$Username Login')))
	
	checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/API_KEY/Page_Api Key List/PageFooter')), totaldataDB, false, FailureHandling.CONTINUE_ON_FAILURE), 'Total data tabel tidak sesuai DB')
	
	if (WebUI.verifyElementVisible(findTestObject('Object Repository/API_KEY/Page_Api Key List/isPagingEnabled'), FailureHandling.OPTIONAL)) {
		'klik panah ke kanan di footer'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/next_page'))
		
		'verifikasi halaman ada di 2'
		checkVerifyFooter()
		
		'klik panah kiri pada footer'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/prev_page'))
		
		'verifikasi halaman ada di 1'
		checkVerifyFooter()
		
		'klik angka halaman 2'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/page2'))
		
		'verifikasi halaman ada di 2'
		checkVerifyFooter()
		
		'klik angka halaman 1'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/page1'))
		
		'verifikasi halaman ada di 1'
		checkVerifyFooter()
		
		'klik skip page ke paling akhir'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/skiptoLast_page'))
		
		'verifikasi halaman'
		checkVerifyFooter()
		
		'klik skip page ke paling awal'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/goToFirstPage'))
		
		'verifikasi halaman'
		checkVerifyFooter()
	}
}

'fungsi cek halaman'
def checkVerifyFooter() {
	'fokus ke halaman yang sedang dipilih'
	int pageCheck = Integer.parseInt(
		WebUI.getAttribute(findTestObject('Object Repository/API_KEY/Page_Api Key List/PageFooter'), 'ng-reflect-page'))
	
	'halaman yang dipilih harus sama dengan yang di sistem'
	if (GlobalVariable.PageNum == pageCheck) {
		GlobalVariable.PageNum -= 1
		if (GlobalVariable.PageNum < 1) {
			GlobalVariable.PageNum = 2
		}
	}
	//tulis halaman error jika tidak sesuai
	else {
		GlobalVariable.FlagFailed = 1
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';')
			+ GlobalVariable.FailedReasonPagingError)
	}
}

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
	if (isMatch == false) {
		GlobalVariable.FlagFailed = 1
		
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
		GlobalVariable.FailedReasonVerifyEqualorMatch + reason)
	}
}

def rowExcel(String cellValue) {
	CustomKeywords.'writetoexcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def checkCopyLink(int isMandatoryComplete) {
	'panggil fungsi copy link'
	if (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('Copy API Link?(Yes/No)')) == 'Yes') {
		'klik tombol COPY LINK'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/buttonCopy'))
			
		'verifikasi copy berhasil'
		CustomKeywords.'writetoexcel.CheckSaveProcess.checkStatus'(isMandatoryComplete,
			findTestObject('Object Repository/API_KEY/Page_Api Key List/notif_CopySuccess'),
				GlobalVariable.NumOfColumn, sheet)
	}
}