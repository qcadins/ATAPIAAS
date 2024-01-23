import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection

'deklarasi koneksi ke DB eendigo_dev'
Connection conndev = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_esign'()

'ambil data coupon dari db'
ArrayList resultDB = CustomKeywords.'coupon.CouponVerif.getAddEditCoupon'(conndev, 
	findTestData(Path).getValue(GlobalVariable.NumOfColumn, rowExcel('$KodeKupon')))

'kembalikan nomor transaksi'
int arrayIndex = 0
	
'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(Path).getValue(GlobalVariable.NumOfColumn, rowExcel('$TipeKupon')),
	resultDB[arrayIndex++], false, FailureHandling.OPTIONAL), 'Tipe Kupon tidak sesuai')

'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(Path).getValue(GlobalVariable.NumOfColumn, rowExcel('$KodeKupon')),
	resultDB[arrayIndex++], false, FailureHandling.OPTIONAL), 'Kode Kupon tidak sesuai')

'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(Path).getValue(GlobalVariable.NumOfColumn, rowExcel('$TanggalMulaiBerlaku')),
	resultDB[arrayIndex++], false, FailureHandling.OPTIONAL), 'Tanggal Mulai berlaku Kupon tidak sesuai')

'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(Path).getValue(GlobalVariable.NumOfColumn, rowExcel('$TanggalTerakhirBerlaku')),
	resultDB[arrayIndex++], false, FailureHandling.OPTIONAL), 'Tanggal Akhir berlaku tidak sesuai')

'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(Path).getValue(GlobalVariable.NumOfColumn, rowExcel('$TipeNilaiKupon')),
	resultDB[arrayIndex++], false, FailureHandling.OPTIONAL), 'Tipe Nilai Kupon tidak sesuai')

'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(Path).getValue(GlobalVariable.NumOfColumn, rowExcel('$NilaiKupon')),
	resultDB[arrayIndex++], false, FailureHandling.OPTIONAL), 'Nilai Kupon tidak sesuai')

'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(Path).getValue(GlobalVariable.NumOfColumn, rowExcel('$JumlahKupon')),
	resultDB[arrayIndex++], false, FailureHandling.OPTIONAL), 'Jumlah Kupon tidak sesuai')

'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(Path).getValue(GlobalVariable.NumOfColumn, rowExcel('$MaksimalPenebusan')),
	resultDB[arrayIndex++], false, FailureHandling.OPTIONAL), 'Maksimal Penebusan Kupon tidak sesuai')

'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(Path).getValue(GlobalVariable.NumOfColumn, rowExcel('$MinimalPembayaran')),
	resultDB[arrayIndex++], false, FailureHandling.OPTIONAL), 'Minimal Pembayaran Kupon tidak sesuai')

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
	if (isMatch == false) {
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(Path).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
		GlobalVariable.FailedReasonStoreDB + ' ' + reason)
	}
}

def rowExcel(String cellValue) {
	CustomKeywords.'writetoexcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}
