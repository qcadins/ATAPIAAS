import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection

//'deklarasi koneksi ke Database adins_apiaas_uat'
//def connProd = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_uatProduction'()

'deklarasi koneksi ke Database adins_apiaas_uat'
def conndevUAT = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_devUat'()

'get data balance mutation dari DB'
ArrayList<String> result = CustomKeywords.'apikey.CheckSaldoAPI.getIsiSaldoStoreDB'(conndevUAT, tenant)

'declare arraylist arraymatch'
ArrayList<String> arrayMatch = []

'declare arrayindex'
arrayindex = 0

'verify tenant'
arrayMatch.add(WebUI.verifyMatch(
	findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 12).toUpperCase(), 
	(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify vendor'
arrayMatch.add(WebUI.verifyMatch(
	findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 13).toUpperCase(), 
	(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tipe saldo'
arrayMatch.add(WebUI.verifyMatch(
	findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 14).toUpperCase(), 
	(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tambah saldo'
arrayMatch.add(WebUI.verifyMatch(
	findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 15).toUpperCase(), 
	(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify Nomor tagihan'
arrayMatch.add(WebUI.verifyMatch(
	findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 16).toUpperCase(), 
	(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify Catatan'
arrayMatch.add(WebUI.verifyMatch(
	findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 17).toUpperCase(), 
	(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tanggal pembelian'
arrayMatch.add(WebUI.verifyMatch(
	findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 18).toUpperCase(), 
	(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {

	'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
	CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('IsiSaldo', GlobalVariable.NumOfColumn, 
		GlobalVariable.StatusFailed, findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, 2) + ';' + 
			GlobalVariable.FailedReasonStoreDB)
	
}