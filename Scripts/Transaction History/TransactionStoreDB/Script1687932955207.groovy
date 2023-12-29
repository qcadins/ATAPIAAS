import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection

'deklarasi koneksi ke DB eendigo_dev'
Connection conndev = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_esign'()

'ambil data role dari db'
String resultDB = CustomKeywords.'transactionHistory.TransactionVerif.getstatusafterConfirmOrReject'(
	conndev, TrxNum)

'deklarasi string result dari excel'
String resultExcel

'check if action new/edit'
if (TrxType == 'Approve') {
	
	'ambil data role dari excel'
	resultExcel = 'Pembayaran Berhasil'
	
} else if(TrxType == 'Reject') {
	
	'ambil data role dari excel'
	resultExcel = 'Pembayaran Ditolak'

} else if(TrxType == 'Upload') {
	
	'ambil data role dari excel'
	resultExcel = 'Menunggu Verifikasi Pembayaran'
}

checkVerifyEqualorMatch(WebUI.verifyMatch(resultDB, resultExcel, false,
	FailureHandling.CONTINUE_ON_FAILURE), TrxType)

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
	if (isMatch == false) {
		
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		GlobalVariable.FlagFailed = 1
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(Sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
				GlobalVariable.FailedReasonStoreDB + ' ' + reason)
	}
}

def rowExcel(String cellValue) {
	return CustomKeywords.'writeToExcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, Sheet, cellValue)
}