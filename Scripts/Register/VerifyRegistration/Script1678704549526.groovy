import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'siapkan koneksi ke database apiaas lama'
def conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_esign'()

'simpan email ke dalam sebuah variabel'
String email = findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 8)

'simpan data yang diambil dari database'
ArrayList<String> credential = CustomKeywords.'profile.CheckRegisterProfile.checkDBafterRegister'(conn, email)

'kumpulan string dari excel'
ArrayList<String> exceldata = []

'data dari excel disimpan ke arraylist'
for (int i = 7; i < credential.size; i++){
	
	exceldata.add(findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, i+1))
}

'verifikasi data pada WEB dan excel sama'
for (int j = 0; j < exceldata.size ; j++) {
	
	checkVerifyEqualorMatch(WebUI.verifyMatch(credential[j], exceldata[j], false, FailureHandling.CONTINUE_ON_FAILURE))
}

def checkVerifyEqualorMatch(Boolean isMatch) {
	if(isMatch == false){
		
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Register', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonStoreDB)
	}
}