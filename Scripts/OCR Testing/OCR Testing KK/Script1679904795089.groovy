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
	ResponseObject response = WS.sendRequest(findTestObject('Object Repository/Postman/OCR KK Copy', [('img'): findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 8),
		('key'):thekey[0], ('tenant'):tenantcode[0]]))
	
	'ambil message respon dari HIT tersebut'
	message_ocr = WS.getElementPropertyValue(response, 'message').toString()
	
	'ambil status dari respon HIT tersebut'
	state_ocr = WS.getElementPropertyValue(response, 'status').toString()
	
	'jika tidak ada message error dan status success'
	if(message_ocr == '' && state_ocr == 'SUCCESS')
	{	
		'tulis status sukses pada excel'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('OCR KK', GlobalVariable.NumOfColumn, GlobalVariable.StatusSuccess,
		GlobalVariable.SuccessReason)
	}
	//jika gagal memenuhi syarat diatas
	else
	{
		GlobalVariable.FlagFailed = 1
		'write to excel status failed dan reason'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('OCR KK', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		message_ocr)
	}
}
