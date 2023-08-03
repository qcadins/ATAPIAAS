import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection

'deklarasi koneksi ke DB eendigo_dev'
Connection conndev = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_esign'()

'ambil data coupon dari db'
ArrayList resultDB = CustomKeywords.'coupon.CouponVerif.getAddEditCoupon'(conndev, 
	findTestData(Path).getValue(GlobalVariable.NumOfColumn, 21))

'kembalikan nomor transaksi'
int arrayIndex = 0
	
'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(Path).getValue(GlobalVariable.NumOfColumn, 20),
	resultDB[arrayIndex++], false,FailureHandling.OPTIONAL), 'Tipe Kupon tidak sesuai')

'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(Path).getValue(GlobalVariable.NumOfColumn, 21),
	resultDB[arrayIndex++], false,FailureHandling.OPTIONAL), 'Kode Kupon tidak sesuai')

'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(Path).getValue(GlobalVariable.NumOfColumn, 22),
	resultDB[arrayIndex++], false,FailureHandling.OPTIONAL), 'Tanggal Mulai berlaku Kupon tidak sesuai')

'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(Path).getValue(GlobalVariable.NumOfColumn, 23),
	resultDB[arrayIndex++], false,FailureHandling.OPTIONAL), 'Tanggal Akhir berlaku tidak sesuai')

'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(Path).getValue(GlobalVariable.NumOfColumn, 24),
	resultDB[arrayIndex++], false,FailureHandling.OPTIONAL), 'Tipe Nilai Kupon tidak sesuai')

'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(Path).getValue(GlobalVariable.NumOfColumn, 25),
	resultDB[arrayIndex++], false,FailureHandling.OPTIONAL), 'Nilai Kupon tidak sesuai')

'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(Path).getValue(GlobalVariable.NumOfColumn, 26),
	resultDB[arrayIndex++], false,FailureHandling.OPTIONAL), 'Jumlah Kupon tidak sesuai')

'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(Path).getValue(GlobalVariable.NumOfColumn, 27),
	resultDB[arrayIndex++], false,FailureHandling.OPTIONAL), 'Maksimal Penebusan Kupon tidak sesuai')

'cek hasil db dan excel'
checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(Path).getValue(GlobalVariable.NumOfColumn, 28),
	resultDB[arrayIndex++], false,FailureHandling.OPTIONAL), 'Minimal Pembayaran Kupon tidak sesuai')

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
	if (isMatch == false) {
		
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Coupon', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(Path).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonStoreDB + ' ' + reason)
		
	}
}