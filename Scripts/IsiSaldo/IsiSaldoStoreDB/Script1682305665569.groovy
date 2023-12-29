import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection

'get current date'
def currentDate = new Date().format('yyyy-MM-dd')

Connection conn

if(GlobalVariable.SettingEnvi == 'Production') {
	'deklarasi koneksi ke Database eendigo_dev'
	conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_public'()
	
} else if(GlobalVariable.SettingEnvi == 'Trial') {
	'deklarasi koneksi ke Database eendigo_dev_uat'
	conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_devUat'()
	
}

'get data balance mutation dari DB'
ArrayList result = CustomKeywords.'apikey.CheckSaldoAPI.getIsiSaldoStoreDB'(conn, tenant)

'declare arraylist arraymatch'
ArrayList arrayMatch = []

'declare arrayindex'
arrayindex = 0

if (autoIsiSaldo == '') {	
	'verify tenant'
	arrayMatch.add(WebUI.verifyMatch(
		findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('$Tenant')).toUpperCase(), 
		(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify vendor'
	arrayMatch.add(WebUI.verifyMatch(
		findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('$Vendor')).toUpperCase(), 
		(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify tipe saldo'
	arrayMatch.add(WebUI.verifyMatch(
		findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('$Tipe Saldo')).toUpperCase(), 
		(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify tambah saldo'
	arrayMatch.add(WebUI.verifyMatch(
		findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('$Tambah Saldo')).toUpperCase(), 
		(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify Nomor tagihan'
	arrayMatch.add(WebUI.verifyMatch(
		findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('$Nomor tagihan')).toUpperCase(), 
		(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify Catatan'
	arrayMatch.add(WebUI.verifyMatch(
		findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('$Catatan')).toUpperCase(), 
		(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify tanggal pembelian'
	arrayMatch.add(WebUI.verifyMatch(
		findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('$Tanggal Pembelian (YYYY-MM-DD)')).toUpperCase(), 
		(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
} else if (autoIsiSaldo == 'Yes') {
	'verify tenant'
	arrayMatch.add(WebUI.verifyMatch(
		findTestData(ExcelPathSaldoAPI).getValue(2, 20).toUpperCase(),
		(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	if (GlobalVariable.SettingEnvi == 'Production') {
		'verify vendor'
		arrayMatch.add(WebUI.verifyMatch('ESIGN/ADINS',
				(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
		
	} else if (GlobalVariable.SettingEnvi == 'Trial') {
		'verify vendor'
		arrayMatch.add(WebUI.verifyMatch('ADINS',
			(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	}
	
	'verify tipe saldo'
	arrayMatch.add(WebUI.verifyMatch(tipeSaldo.toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify tambah saldo'
	arrayMatch.add(WebUI.verifyMatch(
		findTestData(ExcelPathSaldoAPI).getValue(2, 21).toUpperCase(),
		(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify Nomor tagihan'
	arrayMatch.add(WebUI.verifyMatch(
		findTestData(ExcelPathSaldoAPI).getValue(2, 22).toUpperCase(),
		(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify Catatan'
	arrayMatch.add(WebUI.verifyMatch('ISI ULANG ' + tipeSaldo.toUpperCase(),
		(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify tanggal pembelian'
	arrayMatch.add(WebUI.verifyMatch(currentDate, (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
}

'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {
	
	GlobalVariable.FlagFailed = 1

	'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
	CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, 
		GlobalVariable.StatusFailed, findTestData(ExcelPathSaldoAPI).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';' + 
			GlobalVariable.FailedReasonStoreDB)
	
}

def rowExcel(String cellValue) {
	return CustomKeywords.'writeToExcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}