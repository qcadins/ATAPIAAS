import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import groovy.sql.Sql as Sql
import java.sql.Connection

'deklarasi variabel untuk konek ke Database APIAAS'
Connection conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_public'()

'deklarasi reason untuk failed'
String reason

'kembalikan nomor transaksi'
int arrayIndex = 0

'kumpulan string yang menyimpan hasil data dari DB'
ArrayList hasilDB = []

if (Case == 'Add') {
	
	'masukkan value untuk reason'
	reason = 'Add API KEY'
	
	'ambil data dari DB sesudah tambah API baru'
	hasilDB = CustomKeywords.'apikey.CheckAPIKey.getAPINamefromDB'(conn, findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('$Nama API KEY')))
	
	'cek hasil db dan excel'
	checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('$Nama API KEY')),
		hasilDB[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), reason)
	
	'cek hasil db dan excel'
	checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('$Tipe API KEY')),
		hasilDB[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), reason)
	
} else if (Case == 'Edit') {
	
	'masukkan value untuk reason'
	reason = 'Edit API KEY'
	
	'kumpulan string yang menyimpan hasil data dari DB'
	hasilDB = CustomKeywords.'apikey.CheckAPIKey.getAPIStatusfromDB'(conn, findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('$Edit Nama API')),
		findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('$Username Login')))
	
	'cek hasil db dan excel'
	checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('$Edit Nama API')),
		hasilDB[arrayIndex++], false,FailureHandling.OPTIONAL), reason)
	
	'cek hasil db dan excel'
	checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('$Edit Status API')),
		hasilDB[arrayIndex++], false,FailureHandling.OPTIONAL), reason)
}

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
	if (isMatch == false) {
		
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
		GlobalVariable.FailedReasonStoreDB + ' ' + reason)
		
	}
}

def rowExcel(String cellValue) {
	return CustomKeywords.'writeToExcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}