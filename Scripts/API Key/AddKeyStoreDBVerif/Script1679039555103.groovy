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

'koneksi untuk ke database APIAAS'
def conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_public'()

'menyimpan nama api ke dalam sebuah variabel'
String api_name = findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 10)

'ambil data dari DB sesudah tambah API baru'
ArrayList<String> hasildb = CustomKeywords.'apikey.CheckAPIKey.getAPINamefromDB'(conn, api_name)

'list nama API dari excel'
ArrayList<String> hasilexcel = new ArrayList<String>()

'tambah nama API ke hasilexcel'
hasilexcel.add(api_name)

'tambah jenis API key(production/trial)'
hasilexcel.add(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 11))

'verifikasi data pada db dan excel sama'
for (int j = 0; j < hasilexcel.size ; j++) {
	checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[j], hasilexcel[j], false, FailureHandling.CONTINUE_ON_FAILURE))
}

def checkVerifyEqualorMatch(Boolean isMatch) {
	if(isMatch == false)
	{
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('API KEY', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonSubmitError)
	}
}