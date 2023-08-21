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
	hasilDB = CustomKeywords.'apikey.CheckAPIKey.getAPINamefromDB'(conn, findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 11))
	
	'cek hasil db dan excel'
	checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 11),
		hasilDB[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), reason)
	
	'cek hasil db dan excel'
	checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 12),
		hasilDB[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), reason)
	
} else if (Case == 'Edit') {
	
	'masukkan value untuk reason'
	reason = 'Edit API KEY'
	
	'kumpulan string yang menyimpan hasil data dari DB'
	hasilDB = CustomKeywords.'apikey.CheckAPIKey.getAPIStatusfromDB'(conn, findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 13))
	
	'cek hasil db dan excel'
	checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 13),
		hasilDB[arrayIndex++], false,FailureHandling.OPTIONAL), reason)
	
	'cek hasil db dan excel'
	checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 14),
		hasilDB[arrayIndex++], false,FailureHandling.OPTIONAL), reason)
}

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
	if (isMatch == false) {
		
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Edit Profile', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonStoreDB + ' ' + reason)
		
	}
}