import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.testobject.ResponseObject
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

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.writeExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Edit Profile'
int CountColumnEdit = findTestData(ExcelPathOCRTesting).getColumnNumbers()

'deklarasi variabel untuk konek ke Database APIAAS'
def conn = CustomKeywords.'dbConnection.connect.connectDBAPIAAS_public'()

'buka chrome'
WebUI.openBrowser('')

'buka website APIAAS SIT, data diambil dari TestData Login'
WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 2))

'ambil key trial yang aktif dari DB'
ArrayList<String> thekey = CustomKeywords.'ocrTesting.getParameterfromDB.getAPIKeyfromDB'(conn)

'ambil kode tenant di DB'
ArrayList<String> tenantcode = CustomKeywords.'ocrTesting.getParameterfromDB.getTenantCodefromDB'(conn)

'pindah testcase sesuai jumlah di excel'
for(GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn <= CountColumnEdit; (GlobalVariable.NumOfColumn)++)
{
	'deklarasi variable response'
	ResponseObject response
	
	'cek apakah perlu tambah API'
	String UseCorrectKey = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 14)
	
	'input key yang salah'
	String WrongKey = findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 15)
	
	'set penanda error menjadi 0'
	GlobalVariable.FlagFailed = 0
	
	'angka untuk menghitung data mandatory yang tidak terpenuhi'
	int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 4))
	
	'user pilih untuk input API key yang benar atau salah'
	if(UseCorrectKey == 'Yes')
	{
		'lakukan proses HIT api dengan parameter image, key yang benar, dan juga tenant'
		response = WS.sendRequest(findTestObject('Object Repository/OCR Testing/OCR STNK', [('img'): findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 8),
		('key'):thekey[0], ('tenant'):tenantcode[0]]))
	}
	else
	{
		'lakukan proses HIT api dengan parameter image, key yang salah, dan juga tenant'
		response = WS.sendRequest(findTestObject('Object Repository/OCR Testing/OCR STNK', [('img'): findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 8),
		('key'):WrongKey, ('tenant'):tenantcode[0]]))
		
	}
	
	'ambil message respon dari HIT tersebut'
	message_ocr = WS.getElementPropertyValue(response, 'message')
	
	'ambil status dari respon HIT tersebut'
	state_ocr = WS.getElementPropertyValue(response, 'status')
	
	'jika tidak ada'
	if(message_ocr == '' && state_ocr == 'SUCCESS')
	{
		'tulis status sukses pada excel'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('OCR STNK', GlobalVariable.NumOfColumn, GlobalVariable.StatusSuccess,
		GlobalVariable.SuccessReason)
	}
	//jika gagal memenuhi syarat diatas
	else
	{
		GlobalVariable.FlagFailed = 1
		'write to excel status failed dan reason'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('OCR STNK', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		message_ocr)
	}
}

def checkSaldoUIDB(){
	int Saldo = WebUI.getAttribute(findTestObject('Object Repository/API_KEY/Page_Balance/h3_45,649'), 'value')
	
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/span_Select One_ng-arrow-wrapper'))
	
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipetranc'), findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 9))
	
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipetranc'), Keys.chord(Keys.ENTER))
	
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/span_All_ng-arrow-wrapper'))
	
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'), findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 10))
	
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'), Keys.chord(Keys.ENTER))
	
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/button_Cari'))
	
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/i_Catatan_datatable-icon-skip'))
	
	String totaldata = WebUI.getAttribute(findTestObject('Object Repository/API_KEY/Page_Balance/TotalTrx'), 'value')
}