import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection

'deklarasi variabel untuk konek ke Database APIAAS'
Connection conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_esign'()

'deklarasi untuk array index'
int arrayIndex = 0

'ambil email dari testdata, disimpan ke string'
String email = findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 9)

'kumpulan string yang menyimpan hasil data dari DB'
ArrayList hasildb = CustomKeywords.'profile.CheckProfile.getProfilefromDB'(conn, email, role)

if (role == 'Admin Client') {
		
	'cek hasil db dan excel'
	checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[arrayIndex++], findTestData(ExcelPathEditProfile).getValue(
		GlobalVariable.NumOfColumn, 11), false, FailureHandling.CONTINUE_ON_FAILURE), 'Nama depan tidak sesuai')
	
	'cek hasil db dan excel'
	checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[arrayIndex++], findTestData(ExcelPathEditProfile).getValue(
		GlobalVariable.NumOfColumn, 12), false, FailureHandling.CONTINUE_ON_FAILURE), 'Nama belakang tidak sesuai')

	'cek hasil db dan excel'
	checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[arrayIndex++], findTestData(ExcelPathEditProfile).getValue(
		GlobalVariable.NumOfColumn, 13), false, FailureHandling.CONTINUE_ON_FAILURE), 'Nama tenant tidak sesuai')
	
	'cek hasil db dan excel'
	checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[arrayIndex++], findTestData(ExcelPathEditProfile).getValue(
		GlobalVariable.NumOfColumn, 14), false, FailureHandling.CONTINUE_ON_FAILURE), 'Industry tidak sesuai')
	
	'cek hasil db dan excel'
	checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[arrayIndex++], findTestData(ExcelPathEditProfile).getValue(
		GlobalVariable.NumOfColumn, 15), false, FailureHandling.CONTINUE_ON_FAILURE), 'Jenis Kelamin tidak sesuai')
	
	'cek hasil db dan excel'
	checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[arrayIndex++], findTestData(ExcelPathEditProfile).getValue(
		GlobalVariable.NumOfColumn, 16), false, FailureHandling.CONTINUE_ON_FAILURE), 'Website tidak sesuai')
	
	'cek hasil db dan excel'
	checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[arrayIndex++], findTestData(ExcelPathEditProfile).getValue(
		GlobalVariable.NumOfColumn, 17), false, FailureHandling.CONTINUE_ON_FAILURE), 'Nomor telepon tidak sesuai')
	
	'cek hasil db dan excel'
	checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[arrayIndex++], findTestData(ExcelPathEditProfile).getValue(
		GlobalVariable.NumOfColumn, 18), false, FailureHandling.CONTINUE_ON_FAILURE), 'Position tidak sesuai')
	
	'cek hasil db dan excel'
	checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[arrayIndex++], findTestData(ExcelPathEditProfile).getValue(
		GlobalVariable.NumOfColumn, 19), false, FailureHandling.CONTINUE_ON_FAILURE), 'Negara tidak sesuai')
	
	'cek hasil db dan excel'
	checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[arrayIndex++], findTestData(ExcelPathEditProfile).getValue(
		GlobalVariable.NumOfColumn, 20), false, FailureHandling.CONTINUE_ON_FAILURE), 'Nomor NPWP tidak sesuai')
	
} else {
	
	'cek hasil db dan excel'
	checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[arrayIndex++], findTestData(ExcelPathEditProfile).getValue(
		GlobalVariable.NumOfColumn, 11), false, FailureHandling.CONTINUE_ON_FAILURE), 'Nama depan tidak sesuai')
	
	'cek hasil db dan excel'
	checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[arrayIndex++], findTestData(ExcelPathEditProfile).getValue(
		GlobalVariable.NumOfColumn, 12), false, FailureHandling.CONTINUE_ON_FAILURE), 'Nama belakang tidak sesuai')
	
	'cek hasil db dan excel'
	checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[arrayIndex++], findTestData(ExcelPathEditProfile).getValue(
		GlobalVariable.NumOfColumn, 15), false, FailureHandling.CONTINUE_ON_FAILURE), 'Jenis Kelamin tidak sesuai')
	
	'cek hasil db dan excel'
	checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[arrayIndex++], findTestData(ExcelPathEditProfile).getValue(
		GlobalVariable.NumOfColumn, 17), false, FailureHandling.CONTINUE_ON_FAILURE), 'Nomor telepon tidak sesuai')
	
	'cek hasil db dan excel'
	checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[arrayIndex++], findTestData(ExcelPathEditProfile).getValue(
		GlobalVariable.NumOfColumn, 18), false, FailureHandling.CONTINUE_ON_FAILURE), 'Position tidak sesuai')
	
	'cek hasil db dan excel'
	checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[arrayIndex++], findTestData(ExcelPathEditProfile).getValue(
		GlobalVariable.NumOfColumn, 19), false, FailureHandling.CONTINUE_ON_FAILURE), 'Negara tidak sesuai')
}

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
	if(isMatch == false){
		
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Edit Profile', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
				GlobalVariable.FailedReasonStoreDB + ' ' + reason)
	}
}