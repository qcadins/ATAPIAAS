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
import java.sql.Connection

'deklarasi variabel untuk konek ke Database eendigo_dev'
def conn = CustomKeywords.'dbConnection.connect.connectDBAPIAAS_public'()

'deklarasi koneksi ke Database adins_apiaas_uat'
def connProd = CustomKeywords.'dbConnection.connect.connectDBAPIAAS_uatProduction'()

'get data balance mutation dari DB'
ArrayList<String> result = CustomKeywords.'apikey.checkSaldoAPI.getIsiSaldoStoreDB'(connProd, tenant)

'declare arraylist arraymatch'
ArrayList<String> arrayMatch = new ArrayList<String>()

'declare arrayindex'
arrayindex = 0

'verify tenant'
arrayMatch.add(WebUI.verifyMatch(findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 12).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify vendor'
arrayMatch.add(WebUI.verifyMatch(findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 13).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tipe saldo'
arrayMatch.add(WebUI.verifyMatch(findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 14).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tambah saldo'
arrayMatch.add(WebUI.verifyMatch(findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 15).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify Nomor tagihan'
arrayMatch.add(WebUI.verifyMatch(findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 16).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify Catatan'
arrayMatch.add(WebUI.verifyMatch(findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 17).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tanggal pembelian'
arrayMatch.add(WebUI.verifyMatch(findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 18).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {

	'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
	CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('APIAAS-Saldo', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed, findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 2) + ';' + GlobalVariable.ReasonFailedStoredDB)
	
}