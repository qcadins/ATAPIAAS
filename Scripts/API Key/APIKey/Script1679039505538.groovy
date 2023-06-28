import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'deklarasi variabel untuk konek ke Database APIAAS'
Connection conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_public'()

'mendapat jumlah kolom dari sheet Edit Profile'
int countColumnEdit = findTestData(ExcelPathAPIKey).getColumnNumbers()

'pindah testcase sesuai jumlah di excel'
for (GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++) {
	
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 1).length() == 0) {
		
		break
	}
	else if (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 1).equalsIgnoreCase('Unexecuted')) {
		
		String optiontipe, optionstatus
		
		'cek apakah perlu melakukan action'
		String actionAPIKey = findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 15)
		
		'cek apakah perlu copy link API'
		String copyAPILink = findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 16)
		
		'cek apakah perlu fungsi download dokumentasi API'
		String downloadDocs = findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 17)
		
		'angka untuk menghitung data mandatory yang tidak terpenuhi'
		int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 5))
		
		'panggil fungsi login'
		WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'Key', ('SheetName') : 'API KEY', 
			('Path') : ExcelPathAPIKey], FailureHandling.STOP_ON_FAILURE)
		
		'pada delay, lakukan captcha secara manual'
		WebUI.delay(15)
		
		'dapatkan detail tenant dari user yang login'
		ArrayList<String> resultTenant = CustomKeywords.'apikey.CheckAPIKey.getTenantCodeName'(conn, findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 9))
		
		CustomKeywords.'writeToExcel.CheckSaveProcess.checkStatusbtnClickable'(isMandatoryComplete, 
			findTestObject(TombolLogin), 
			GlobalVariable.NumOfColumn, 'API KEY')
			
		'klik pada button login'
		WebUI.click(findTestObject(TombolLogin))
			
		'cek apakah muncul error unknown setelah login'
		if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'),
			GlobalVariable.Timeout, FailureHandling.OPTIONAL) == false) {
			
			GlobalVariable.FlagFailed = 1
			
			'tulis adanya error pada sistem web'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('API KEY', GlobalVariable.NumOfColumn,
				GlobalVariable.StatusWarning, (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
					GlobalVariable.FailedReasonUnknown)
		}
		
		WebUI.delay(GlobalVariable.Timeout)
		
		'klik pada tombol garis tiga'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/i_KEPIN EDGAR_ft-menu font-medium-3'))
		
		WebUI.delay(1)
		
		'klik pada API KEY'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/span_API Key'))
		
		WebUI.delay(2)
		
		arrayIndex = 0
		
		'verify tenant code'
		checkVerifyEqualorMatch(WebUI.verifyMatch(resultTenant[arrayIndex++], WebUI.getAttribute(findTestObject('API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input_TenantCode'), 'value', FailureHandling.OPTIONAL), false, FailureHandling.CONTINUE_ON_FAILURE), ' tenant code') 
			
		'verify tenant name'
		checkVerifyEqualorMatch(WebUI.verifyMatch(resultTenant[arrayIndex++], WebUI.getAttribute(findTestObject('API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input_TenantName'), 'value', FailureHandling.OPTIONAL), false, FailureHandling.CONTINUE_ON_FAILURE), ' tenant name')
		
		'input tipe API'
		WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Api Key List/input_tipeapi_list'), findTestData(
				ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 12))
		
		'select tipe API'
		WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Api Key List/input_tipeapi_list'), Keys.chord(
				Keys.ENTER))
		
		'input status API'
		WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Api Key List/input_statusapi_list'), findTestData(
				ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 13))
		
		'select status API'
		WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Api Key List/input_statusapi_list'), Keys.chord(
				Keys.ENTER))
		
		'klik pada button cari'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/button_Cari'))
		
		'klik tombol set ulang'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/button_Set Ulang'))
		
		'klik pada ddl tipe API KEY'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/span_All_ng-arrow-wrapper'))
		
		'simpan pilihan utama dari tipe API KEY'
		optiontipe = WebUI.getAttribute(findTestObject('Object Repository/API_KEY/Page_Api Key List/input tipe'), 
			'aria-activedescendant')
		
		'klik pada ddl Status API KEY'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/span_All_ng-arrow-wrapper_1'))
		
		'simpan pilihan utama dari status API KEY'
		optionstatus = WebUI.getAttribute(findTestObject('Object Repository/API_KEY/Page_Api Key List/input status'), 
			'aria-activedescendant')
		
		'klik pada ddl Status API KEY'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/span_All_ng-arrow-wrapper_1'))
		
		'klik pada tombol cari'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/button_Cari'))
		
		'jika semua pilihan ddl kembali ke "ALL"'
		if (optiontipe.contains('-0') && optionstatus.contains('-0')) {
			
			'klik tombol cari'
			WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/button_Cari'))
		
			'tulis kondisi success atau failed'
			CustomKeywords.'writeToExcel.CheckSaveProcess.checkStatus'(isMandatoryComplete, 
				findTestObject('Object Repository/API_KEY/Page_Api Key List/p_MAMANK'),
				GlobalVariable.NumOfColumn, 'API KEY')
		}
		
		'cek ke DB jika memang diperlukan'
		if(GlobalVariable.KondisiCekDB == 'Yes'){
			
			'verifikasi jumlah baris di DB dan di WEB'
			WebUI.callTestCase(findTestCase('Test Cases/API Key/VerifyTotalAPIList'), 
				[:], FailureHandling.CONTINUE_ON_FAILURE)
		}
		
		if(WebUI.verifyElementVisible(findTestObject('Object Repository/API_KEY/Page_Api Key List/'+
			'isPagingEnabled'), FailureHandling.OPTIONAL)  == true){
			
			'klik panah ke kanan di footer'
			WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/i_Aksi_datatable-icon-right'))
			
			'verifikasi halaman ada di 2'
			checkVerifyFooter()
			
			'klik panah kiri pada footer'
			WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/i_Aksi_datatable-icon-left'))
			
			'verifikasi halaman ada di 1'
			checkVerifyFooter()
			
			'klik angka halaman 2'
			WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/a_2'))
			
			'verifikasi halaman ada di 2'
			checkVerifyFooter()
			
			'klik angka halaman 1'
			WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/a_1'))
			
			'verifikasi halaman ada di 1'
			checkVerifyFooter()
			
			'klik skip page ke paling akhir'
			WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/i_Aksi_datatable-icon-skip'))
			
			'verifikasi halaman'
			checkVerifyFooter()
			
			'klik skip page ke paling awal'
			WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/i_Aksi_datatable-icon-prev'))
			
			'verifikasi halaman'
			checkVerifyFooter()
		}
		
		'panggil fungsi copy link'
		if(copyAPILink == 'Yes'){
			
			'klik tombol COPY LINK'
			WebUI.click(findTestObject('Object Repository/API_KEY/'+
				'Page_Api Key List/em_Aksi_align-middle cursor-pointer font-medium-3 ft-copy'))
				
			'verifikasi copy berhasil'
			CustomKeywords.'writeToExcel.CheckSaveProcess.checkStatus'(isMandatoryComplete, 
				findTestObject('Object Repository/API_KEY/Page_Api Key List/div_API Key copied to clipboard'),
					GlobalVariable.NumOfColumn, 'API KEY')
		}
		
		'panggil fungsi tambah API'
		if(actionAPIKey == 'Add'){
			
			'panggil fungsi Add API KEY'
			WebUI.callTestCase(findTestCase('Test Cases/API Key/AddAPIKey'), 
				[:], FailureHandling.CONTINUE_ON_FAILURE)
		} else if(actionAPIKey == 'Edit'){
			
			'panggil fungsi Edit API Key'
			WebUI.callTestCase(findTestCase('Test Cases/API Key/EditAPIKey'), 
				[:], FailureHandling.CONTINUE_ON_FAILURE)
		}

		
		'kondisi jika tidak ada error'
		if(GlobalVariable.FlagFailed == 0){
			
			'tulis status sukses pada excel'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('API KEY', 
				GlobalVariable.NumOfColumn, GlobalVariable.StatusSuccess, GlobalVariable.SuccessReason)
		}
	}
}

'fungsi cek halaman'
def checkVerifyFooter(){
	
	'fokus ke halaman yang sedang dipilih'
	int pageCheck = Integer.parseInt(
		WebUI.getAttribute(findTestObject('Object Repository/API_KEY/Page_Api Key List/PageFooter'), 'ng-reflect-page'))
	
	'halaman yang dipilih harus sama dengan yang di sistem'
	if(GlobalVariable.PageNum == pageCheck){
		
		GlobalVariable.PageNum -= 1
		if(GlobalVariable.PageNum < 1){
			
			GlobalVariable.PageNum = 2
		}
	}
	//tulis halaman error jika tidak sesuai
	else{
		
		GlobalVariable.FlagFailed = 1
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('API KEY', GlobalVariable.NumOfColumn, 
			GlobalVariable.StatusFailed, (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 2) + ';') 
			+ GlobalVariable.FailedReasonPagingError)
	}
}

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
	if(isMatch == false){
		GlobalVariable.FlagFailed = 1
		
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('API KEY', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonVerifyEqualorMatch + reason)
		
	}
}