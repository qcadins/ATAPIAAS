import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection

'deklarasi variabel untuk konek ke Database APIAAS'
Connection conn = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_esign'()

'deklarasi untuk array index'
int arrayIndex = 0

'ambil email dari testdata, disimpan ke string'
String email = findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, rowExcel('$Username Login'))

'kumpulan string yang menyimpan hasil data dari DB'
ArrayList hasildb = CustomKeywords.'profile.CheckProfile.getProfilefromDB'(conn, email)

'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[arrayIndex++], findTestData(ExcelPathEditProfile).getValue(
	GlobalVariable.NumOfColumn, rowExcel('$Nama Depan')), false, FailureHandling.CONTINUE_ON_FAILURE), 'Nama depan tidak sesuai')

'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[arrayIndex++], findTestData(ExcelPathEditProfile).getValue(
	GlobalVariable.NumOfColumn, rowExcel('$Last Name')), false, FailureHandling.CONTINUE_ON_FAILURE), 'Nama belakang tidak sesuai')

'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[arrayIndex++], findTestData(ExcelPathEditProfile).getValue(
	GlobalVariable.NumOfColumn, rowExcel('Jenis Kelamin')), false, FailureHandling.CONTINUE_ON_FAILURE), 'Jenis Kelamin tidak sesuai')

'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[arrayIndex++], findTestData(ExcelPathEditProfile).getValue(
	GlobalVariable.NumOfColumn, rowExcel('Nomor Telepon')), false, FailureHandling.CONTINUE_ON_FAILURE), 'Nomor telepon tidak sesuai')

'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[arrayIndex++], findTestData(ExcelPathEditProfile).getValue(
	GlobalVariable.NumOfColumn, rowExcel('Jabatan')), false, FailureHandling.CONTINUE_ON_FAILURE), 'Position tidak sesuai')

'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[arrayIndex++], findTestData(ExcelPathEditProfile).getValue(
	GlobalVariable.NumOfColumn, rowExcel('Kode Negara')), false, FailureHandling.CONTINUE_ON_FAILURE), 'Negara tidak sesuai')

if (role == 'ADMIN CLIENT') {
	'cek hasil db dan excel'
	checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[arrayIndex++], findTestData(ExcelPathEditProfile).getValue(
		GlobalVariable.NumOfColumn, rowExcel('Website')), false, FailureHandling.CONTINUE_ON_FAILURE), 'Website tidak sesuai')
	
	'cek hasil db dan excel'
	checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[arrayIndex++], findTestData(ExcelPathEditProfile).getValue(
		GlobalVariable.NumOfColumn, rowExcel('$Nama Tenant')), false, FailureHandling.CONTINUE_ON_FAILURE), 'Nama tenant tidak sesuai')
	
	'cek hasil db dan excel'
	checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[arrayIndex++], findTestData(ExcelPathEditProfile).getValue(
		GlobalVariable.NumOfColumn, rowExcel('Industry')), false, FailureHandling.CONTINUE_ON_FAILURE), 'Industry tidak sesuai')
	
	'cek hasil db dan excel'
	checkVerifyEqualorMatch(WebUI.verifyMatch(hasildb[arrayIndex++], findTestData(ExcelPathEditProfile).getValue(
		GlobalVariable.NumOfColumn, rowExcel('No NPWP')), false, FailureHandling.CONTINUE_ON_FAILURE), 'Nomor NPWP tidak sesuai')
}

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
	if (isMatch == false) {
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
				GlobalVariable.FailedReasonStoreDB + ' ' + reason)
	}
}

def rowExcel(String cellValue) {
	CustomKeywords.'writetoexcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}
