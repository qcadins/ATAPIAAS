import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

'cek apakah perlu tambah API'
String WantAddAPI = findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 17)

'cek apakah perlu edit API'
String WantEditAPI = findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 18)

'cek apakah perlu copy link API'
String CopyAPILink = findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 19)

'cek apakah perlu fungsi download dokumentasi API'
String DownloadDocs = findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 20)

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.writeExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Edit Profile'
int CountColumnEdit = findTestData(ExcelPathAPIKey).getColumnNumbers()

'angka untuk menghitung data mandatory yang tidak terpenuhi'
int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 4))

String optiontipe, optionstatus, totaldata

'pindah testcase sesuai jumlah di excel'
for(GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn <= CountColumnEdit; (GlobalVariable.NumOfColumn)++)
{
	'panggil fungsi login'
	WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'Key'], FailureHandling.STOP_ON_FAILURE)
	
	'pada delay, lakukan captcha secara manual'
	WebUI.delay(10)
	
	CustomKeywords.'writeToExcel.checkSaveProcess.checkStatusbtnClickable'(isMandatoryComplete, findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'), GlobalVariable.NumOfColumn, 'API KEY')
		
	'klik pada button login'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))
		
	WebUI.delay(4)
	
	'klik pada tombol garis tiga'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/i_KEPIN EDGAR_ft-menu font-medium-3'))
	
	WebUI.delay(1)
	
	'klik pada API KEY'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/span_API Key'))
	
	WebUI.delay(2)
	
	'input tipe API'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Api Key List/input_tipeapi_list'), findTestData(
			ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 14))
	
	'select tipe API'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Api Key List/input_tipeapi_list'), Keys.chord(
			Keys.ENTER))
	
	'input status API'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Api Key List/input_statusapi_list'), findTestData(
			ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 15))
	
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
	optiontipe = WebUI.getAttribute(findTestObject('Object Repository/API_KEY/Page_Api Key List/input tipe'), 'aria-activedescendant')
	
	'klik pada ddl Status API KEY'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/span_All_ng-arrow-wrapper_1'))
	
	'simpan pilihan utama dari status API KEY'
	optionstatus = WebUI.getAttribute(findTestObject('Object Repository/API_KEY/Page_Api Key List/input status'), 'aria-activedescendant')
	
	'klik pada ddl Status API KEY'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/span_All_ng-arrow-wrapper_1'))
	
	'klik pada tombol cari'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/button_Cari'))
	
	'jika semua pilihan ddl kembali ke "ALL"'
	if (optiontipe.contains('-0') && optionstatus.contains('-0')) {
		'klik tombol cari'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/button_Cari'))
	
		'tulis kondisi success atau failed'
		CustomKeywords.'writeToExcel.checkSaveProcess.checkStatus'(isMandatoryComplete, findTestObject('Object Repository/API_KEY/Page_Api Key List/p_MAMANK'),
			GlobalVariable.NumOfColumn, 'API KEY')
	}
	
	'cek ke DB jika memang diperlukan'
	if(GlobalVariable.KondisiCekDB == 'Yes')
	{
		'verifikasi jumlah baris di DB dan di WEB'
		WebUI.callTestCase(findTestCase('Test Cases/API Key/VerifyTotalAPIList'), [:], FailureHandling.STOP_ON_FAILURE)
	}
	
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
	
	'panggil fungsi copy link'
	if(CopyAPILink == 'Yes')
	{
		'klik tombol COPY LINK'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/em_Aksi_align-middle cursor-pointer font-medium-3 ft-copy'))
			
		'verifikasi copy berhasil'
		CustomKeywords.'writeToExcel.checkSaveProcess.checkStatus'(isMandatoryComplete, findTestObject('Object Repository/API_KEY/Page_Api Key List/div_API Key copied to clipboard'),
		GlobalVariable.NumOfColumn, 'API KEY')
	}
	
	'panggil fungsi tambah API'
	if(WantAddAPI == 'Yes')
	{
		'panggil fungsi Add API KEY'
		WebUI.callTestCase(findTestCase('Test Cases/API Key/AddAPIKey'), [:], FailureHandling.CONTINUE_ON_FAILURE)
	}
	
	'panggil fungsi edit API'
	if(WantEditAPI == 'Yes')
	{
		'panggil fungsi Edit API Key'
		WebUI.callTestCase(findTestCase('Test Cases/API Key/EditAPIKey'), [:], FailureHandling.CONTINUE_ON_FAILURE)
	}
	'panggil fungsi download dokumentasi'
	if(DownloadDocs == 'Yes')
	{
		'panggil fungsi download dokumentasi API'
		WebUI.callTestCase(findTestCase('Test Cases/Dokumentasi API/DocumentationAPI'), [:], FailureHandling.STOP_ON_FAILURE)
	}
	'kondisi jika tidak ada error'
	if(GlobalVariable.FlagFailed == 0)
	{
		'tulis status sukses pada excel'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('API KEY', GlobalVariable.NumOfColumn, GlobalVariable.StatusSuccess,
		GlobalVariable.SuccessReason)
	}
}

'fungsi cek halaman'
def checkVerifyFooter()
{
	'fokus ke halaman yang sedang dipilih'
	int PageCheck = Integer.parseInt(WebUI.getAttribute(findTestObject('Object Repository/API_KEY/Page_Api Key List/PageFooter'), 'ng-reflect-page'))
	
	'halaman yang dipilih harus sama dengan yang di sistem'
	if(GlobalVariable.PageNum == PageCheck)
	{
		GlobalVariable.PageNum -= 1
		if(GlobalVariable.PageNum < 1)
		{
			GlobalVariable.PageNum = 2
		}
	}
	//tulis halaman error jika tidak sesuai
	else
	{
		GlobalVariable.FlagFailed = 1
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('API KEY', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
			(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.FailedReasonPagingError)
	}
}