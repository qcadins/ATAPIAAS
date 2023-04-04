import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.testobject.ResponseObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import java.awt.Robot
import java.awt.event.KeyEvent
import java.sql.Driver

import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testcase.Variable
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable

import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.writeExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Edit Profile'
int CountColumnEdit = findTestData(ExcelPathOCRTesting).getColumnNumbers()

'deklarasi variabel untuk konek ke Database APIAAS'
def conn = CustomKeywords.'dbConnection.connect.connectDBAPIAAS_public'()

def connProd = CustomKeywords.'dbConnection.connect.connectDBAPIAAS_uatProduction'()

'buka chrome'
WebUI.openBrowser('')

'buka website APIAAS SIT, data diambil dari TestData Login'
WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 2))

'input data email'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_Buat Akun_form-control ng-untouched n_ab9ed8'),
	findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 11))

'input password'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_Buat Akun_form-control ng-untouched n_dd86a2'),
	findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 12))

'ceklis pada reCaptcha'
WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/div_reCAPTCHA_recaptcha-checkbox-border (4)'))

'pada delay, lakukan captcha secara manual'
WebUI.delay(10)

'klik pada button login'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))

WebUI.delay(4)

'ambil kode tenant di DB'
ArrayList<String> tenantcode = CustomKeywords.'ocrTesting.getParameterfromDB.getTenantCodefromDB'(conn, findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 11))

'ambil key trial yang aktif dari DB'
ArrayList<String> thekey = CustomKeywords.'ocrTesting.getParameterfromDB.getAPIKeyfromDB'(conn, tenantcode[0])

'pindah testcase sesuai jumlah di excel'
for(GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn < 3; (GlobalVariable.NumOfColumn)++)
{
	'deklarasi variable response'
	ResponseObject response
	
	'cek apakah perlu tambah API'
	String UseCorrectKey = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 14)
	
	'input key yang salah'
	String WrongKey = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 15)
	
	String emailuser = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 11)
	
	'angka untuk menghitung data mandatory yang tidak terpenuhi'
	int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 4))
	
	int isSaldoBerkurang, Saldoafter, isTrxIncreased, HitAPITrx
	
	HitAPITrx = 1
	
	'set penanda error menjadi 0'
	GlobalVariable.FlagFailed = 0
	
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'), findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 9))
	
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'), Keys.chord(Keys.ENTER))
	
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipetranc'), findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 10))
	
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipetranc'), Keys.chord(Keys.ENTER))
	
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/button_Cari'))
	
	'panggil fungsi skip ke page terakhir'
	SkiptotheLastPages()
	
	'panggil fungsi ambil transaksi terakhir di tabel'
	String no_Trx_before = getTrxNumber()
	
	int Saldobefore = Integer.parseInt(WebUI.getText(findTestObject('Object Repository/API_KEY/Page_Balance/h3_45,649')).replace(',',''))
	
	if(UseCorrectKey == 'Yes')
	{
		'lakukan proses HIT api dengan parameter image, key, dan juga tenant'
		response = WS.sendRequest(findTestObject('Object Repository/OCR Testing/OCR KTP', [('img'): findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 8),
		('key'):thekey[0], ('tenant'):tenantcode[0]]))
	}
	else
	{
		'lakukan proses HIT api dengan parameter image, key, dan juga tenant'
		response = WS.sendRequest(findTestObject('Object Repository/OCR Testing/OCR KTP', [('img'): findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 8),
		('key'):WrongKey, ('tenant'):tenantcode[0]]))
		
	}
	'ambil message respon dari HIT tersebut'
	message_ocr = WS.getElementPropertyValue(response, 'message')
	
	'ambil status dari respon HIT tersebut'
	state_ocr = WS.getElementPropertyValue(response, 'status')
	
	Robot robot = new Robot()
	
	robot.keyPress(KeyEvent.VK_F5)
	robot.keyRelease(KeyEvent.VK_F5);
	
	WebUI.delay(4)
	
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'), findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 9))
	
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'), Keys.chord(Keys.ENTER))
	
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipetranc'), findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 10))
	
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipetranc'), Keys.chord(Keys.ENTER))
		
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/button_Cari'))
	
	//panggil butto skip dibawah ini
	SkiptotheLastPages()
	
	String no_Trx_after = getTrxNumber()
	
	if(GlobalVariable.KondisiCekDB == 'Yes')
	{
		ArrayList<String> LatestMutation= CustomKeywords.'ocrTesting.getParameterfromDB.getLatestMutationfromDB'(connProd, tenantcode[0])
		
		ArrayList<String> trxnum = new ArrayList<>()
		
		trxnum.add(no_Trx_after)
		
		for (int i = 0; i<LatestMutation.size; i++)
		{
			if(!WebUI.verifyMatch(LatestMutation[i], trxnum[i], false, FailureHandling.CONTINUE_ON_FAILURE))
			{
				HitAPITrx = 0
			}
		}
	}
		
	Saldoafter = Integer.parseInt(WebUI.getText(findTestObject('Object Repository/API_KEY/Page_Balance/h3_45,649')).replace(',',''))
	
	if(Saldobefore - Saldoafter == 500)
	{
		isSaldoBerkurang = 1
	}
	else
	{
		isSaldoBerkurang = 0
	}
	
	if(no_Trx_after > no_Trx_before)
	{
		isTrxIncreased = 1
	}
	else
	{
		isTrxIncreased = 0
	}

	println isSaldoBerkurang
	println isTrxIncreased
	println HitAPITrx
	println no_Trx_before
	println no_Trx_after
	
	'jika tidak ada message error dan kondisi lain terpenuhi'
	if(message_ocr == '' && state_ocr == 'SUCCESS' && isTrxIncreased == 1 && isSaldoBerkurang == 1 && HitAPITrx == 1)
	{
		if(GlobalVariable.NumOfColumn > 3)
		{
			GlobalVariable.FlagFailed = 1
			'tulis kondisi gagal'
			CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('OCR KTP', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
			GlobalVariable.FailedReasonCriteriaBypass)
		}
		'tulis status sukses pada excel'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('OCR KTP', GlobalVariable.NumOfColumn, GlobalVariable.StatusSuccess,
		GlobalVariable.SuccessReason)
	}
	//kondisi jika transaksi berhasil tapi tidak tercatat/tersimpan di DB
	else if(state_ocr == 'SUCCESS' && isTrxIncreased == 0 && isSaldoBerkurang == 1)
	{
		GlobalVariable.FlagFailed = 1
		'tulis kondisi gagal'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('OCR KTP', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
		GlobalVariable.FailedReasonTrxNotinDB)
	}
	//kondisi jika transaksi berhasil tapi saldo tidak berkurang
	else if(state_ocr == 'SUCCESS' && isTrxIncreased == 1 && isSaldoBerkurang == 0)
	{
		GlobalVariable.FlagFailed = 1
		'tulis kondisi gagal'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('OCR KTP', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
		GlobalVariable.FailedReasonBalanceNotChange)
	}
	//kondisi transaksi tidak tampil dan tidak tersimpan di DB
	else if(HitAPITrx == 0 && state_ocr == 'FAILED' && isTrxIncreased == 0 && isSaldoBerkurang == 1)
	{
		GlobalVariable.FlagFailed = 1
		'tulis kondisi gagal'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('OCR KTP', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
		GlobalVariable.FailedReasonSaldoBocor)
	}
	else
	{
		GlobalVariable.FlagFailed = 1
		'write to excel status failed dan reason'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('OCR KTP', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		message_ocr)
	}
}

def SkiptotheLastPages() {
	def elementbuttonskip = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance-prod > div.ng-star-inserted > app-msx-paging-v2 > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))
	
	int lastPage = elementbuttonskip.size()
	
	def modifybuttonskip = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Balance/i_Catatan_datatable-icon-skip'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li["+ (lastPage) +"]", true)

	WebUI.click(modifybuttonskip)
}

def getTrxNumber() {
	def variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance-prod > div.ng-star-inserted > app-msx-paging-v2 > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))
	
	int lastIndex = variable.size()
		
	def modifytrxnumber = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/TrxNumber'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (lastIndex) +"]/datatable-body-row/div[2]/datatable-body-cell[6]/div/p", true)
																			
	String no_Trx = WebUI.getText(modifytrxnumber)
	
	return no_Trx
}