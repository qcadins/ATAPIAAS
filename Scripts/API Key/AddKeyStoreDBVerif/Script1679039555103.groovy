import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection

'koneksi untuk ke database APIAAS'
Connection conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_public'()

'menyimpan nama api ke dalam sebuah variabel'
String apiname = findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 11)

'ambil data dari DB sesudah tambah API baru'
ArrayList<String> hasildb = CustomKeywords.'apikey.CheckAPIKey.getAPINamefromDB'(conn, apiname)

'list nama API dari excel'
ArrayList<String> hasilexcel = []

'tambah nama API ke hasilexcel'
hasilexcel.add(apiname)

'tambah jenis API key(production/trial)'
hasilexcel.add(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 12))

'verifikasi data pada db dan excel sama'
for (int j = 0; j < hasilexcel.size ; j++) {
	
	checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[j], hasilexcel[j], false, FailureHandling.CONTINUE_ON_FAILURE))
}

def checkVerifyEqualorMatch(Boolean isMatch) {
	
	if(isMatch == false){
		
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('API KEY', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonSubmitError + ' Add API')
	}
}