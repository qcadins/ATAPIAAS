import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection

'deklarasi koneksi ke DB eendigo_dev'
Connection conndev = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_esign'()

'deklarasi array index untuk traversing'
int arrayIndex = 0

'ambil data coupon dari db'
ArrayList resultDB = CustomKeywords.'topup.TopupVerif.getStoreDBTopup1'(conndev, NoTrx)

'tambah data dari query lain ke arraylist yang sudah ada'
resultDB.addAll(CustomKeywords.'topup.TopupVerif.getStoreDBTopup2'(conndev, NoTrx))

'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(resultDB[arrayIndex++], findTestData(Path).getValue(
	GlobalVariable.NumOfColumn, rowExcel('$Tipe Saldo')), false, FailureHandling.CONTINUE_ON_FAILURE), 'Tipe Saldo tidak sesuai')

'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(resultDB[arrayIndex++], findTestData(Path).getValue(
	GlobalVariable.NumOfColumn, rowExcel('$Metode Pembayaran')), false, FailureHandling.CONTINUE_ON_FAILURE), 'Metode Pembayaran tidak sesuai')

'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(resultDB[arrayIndex++], findTestData(Path).getValue(
	GlobalVariable.NumOfColumn, rowExcel('$Bank Destinasi')), false, FailureHandling.CONTINUE_ON_FAILURE), 'Bank destinasi tidak sesuai')

'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(resultDB[arrayIndex++], findTestData(Path).getValue(
	GlobalVariable.NumOfColumn, rowExcel('$SaldoYangDipilih')), false, FailureHandling.CONTINUE_ON_FAILURE), 'Saldo yang dipilih tidak sesuai')

'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(resultDB[arrayIndex++], findTestData(Path).getValue(
	GlobalVariable.NumOfColumn, rowExcel('$JumlahisiUlang(quantity)')), false, FailureHandling.CONTINUE_ON_FAILURE), 'Jumlah isi ulang tidak sesuai')

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
	if(isMatch == false){
		
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(Path).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
				GlobalVariable.FailedReasonStoreDB + ' ' + reason)
	}
}

def rowExcel(String cellValue) {
	return CustomKeywords.'writeToExcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}