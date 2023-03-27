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

'angka untuk menghitung data mandatory yang tidak terpenuhi'
int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 4))

'deklarasi variabel untuk konek ke Database APIAAS'
def conn = CustomKeywords.'dbConnection.connect.connectDBAPIAAS_public'()

'buka chrome'
WebUI.openBrowser('')

'buka website APIAAS SIT, data diambil dari TestData Login'
WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 2))

'ambil key trial yang aktif dari DB'
ArrayList<String> thekey = CustomKeywords.'apikey.checkAPIKey.getAPIKeyfromDB'(conn)

'ambil kode tenant di DB'
ArrayList<String> tenantcode = CustomKeywords.'apikey.checkAPIKey.getTenantCodefromDB'(conn)

'pindah testcase sesuai jumlah di excel'
for(GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn <= CountColumnEdit; (GlobalVariable.NumOfColumn)++)
{
	'set penanda error menjadi 0'
	GlobalVariable.FlagFailed = 0
	
	'lakukan proses HIT api dengan parameter image, key, dan juga tenant'
	ResponseObject response = WS.sendRequest(findTestObject('Object Repository/Postman/OCR KTP Copy', [('img'): findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 8),
		('key'):thekey[0], ('tenant'):tenantcode[0]]))
	
	'ambil message respon dari HIT tersebut'
	message_ocr = WS.getElementPropertyValue(response, 'message')
	
	'ambil status dari respon HIT tersebut'
	state_ocr = WS.getElementPropertyValue(response, 'status')
	
	'jika tidak ada message error dan status success'
	if(message_ocr == '' && state_ocr == 'SUCCESS')
	{	
		'tulis status sukses pada excel'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('OCR KTP', GlobalVariable.NumOfColumn, GlobalVariable.StatusSuccess,
		GlobalVariable.SuccessReason)
	}
	//jika message menunjukkan saldo kurang
	else if(message_ocr  == 'Insufficient balance')
	{
		GlobalVariable.FlagFailed = 1
		'tulis kondisi gagal'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('OCR KTP', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
		GlobalVariable.FailedReasonSaldoAPI)
	}
	//jika message menunjukkan data yang dikirim tidak sesuai
	else if(message_ocr == 'Parameter invalid, please check your request')
	{
		GlobalVariable.FlagFailed = 1
		'tulis kondisi gagal'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('OCR KTP', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
		GlobalVariable.FailedReasonParameterAPI)
	}
	//jika message menunjukkan api key dan kode tenant tidak valid
	else if(message_ocr == 'Invalid API key or tenant code')
	{
		GlobalVariable.FlagFailed = 1
		'tulis kondisi gagal'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('OCR KTP', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
		GlobalVariable.FailedReasonInvalidTenantKey)
	}
	//jika message menunjukkan KTP not found atau error
	else if(message_ocr == 'KTP NOT FOUND')
	{
		GlobalVariable.FlagFailed = 1
		'tulis kondisi gagal'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('OCR KTP', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
		GlobalVariable.FailedReasonKTPInvalid)
	}
	//jika message menunjukkan gambar kurang tajam atau terlalu tajam
	else if(message_ocr == 'Image resolution is below 460 x 350 or above 3800 x 2850 (Document Only).')
	{
		GlobalVariable.FlagFailed = 1
		'tulis kondisi gagal'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('OCR KTP', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
		GlobalVariable.FailedReasonImageSize)
	}
	//jika message menunjukkan gambar tidak fokus dan lainnya
	else if(message_ocr == 'Image is out of focus/blurry; Light reflection detected; Image is noisy; Image is unreadable;')
	{
		GlobalVariable.FlagFailed = 1
		'tulis kondisi gagal'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('OCR KTP', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
		GlobalVariable.FailedReasonBlurryImage)
	}
	//jika ada message error selain diatas
	else
	{
		GlobalVariable.FlagFailed = 1
		'tulis kondisi gagal'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('OCR KTP', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
		GlobalVariable.FailedReasonHITAPI)
	}
}