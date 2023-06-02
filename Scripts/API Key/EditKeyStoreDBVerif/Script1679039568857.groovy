import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import groovy.sql.Sql as Sql

'deklarasi variabel untuk konek ke Database APIAAS'
def conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_public'()

'ambil email dari testdata, disimpan ke string'
String namaAPI = findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 13)

'kumpulan string yang menyimpan hasil data dari DB'
ArrayList<String> hasildb = CustomKeywords.'apikey.CheckAPIKey.getAPIStatusfromDB'(conn, namaAPI)

'kumpulan string dari data yang diambil langsung dari excel'
ArrayList<String> hasilexcel = []

'mengambil data dari excel'
for (int i=12; i<=hasilexcel.size; i++){
	
	hasilexcel.add(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, i))
}

'verifikasi data pada db dan excel sama'
for (int j = 0; j < hasilexcel.size ; j++) {
	if(checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[j], hasilexcel[j], false, 
		FailureHandling.OPTIONAL)) == false){
	
		break;
	}
}
def checkVerifyEqualorMatch(Boolean isMatch) {
	if(isMatch == false){
		
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Edit Profile', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonStoreDB)
		break;
	}
}