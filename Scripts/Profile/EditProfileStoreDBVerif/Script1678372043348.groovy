import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'deklarasi variabel untuk konek ke Database APIAAS'
def conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_esign'()

'ambil email dari testdata, disimpan ke string'
String email = findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 9)

'kumpulan string yang menyimpan hasil data dari DB'
ArrayList<String> hasildb = CustomKeywords.'profile.CheckProfile.getProfilefromDB'(conn, email)

'kumpulan string dari data yang diambil langsung dari excel'
ArrayList<String> hasilexcel = []

'mengambil data dari excel'
for (int i=10; i<=hasilexcel.size; i++){
	
	hasilexcel.add(findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, i))
}

'verifikasi data pada db dan excel sama'
for (int j = 0; j < hasilexcel.size ; j++) {
	
	checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[j], hasilexcel[j], false, FailureHandling.CONTINUE_ON_FAILURE))
}

def checkVerifyEqualorMatch(Boolean isMatch) {
	if(isMatch == false){
		
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Edit Profile', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonStoreDB)
	}
}