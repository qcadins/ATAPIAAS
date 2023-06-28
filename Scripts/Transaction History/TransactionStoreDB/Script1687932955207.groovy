import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection

'deklarasi koneksi ke DB eendigo_dev'
Connection conndev = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_esign'()

'check if action new/edit'
if (TrxType == 'Approve') {
	
	'ambil data role dari db'
	String resultDB = CustomKeywords.'transactionHistory.TransactionVerif.getstatusafterConfirmOrReject'(
		conndev, TrxNum)
	
	'ambil data role dari excel'
	String resultExcel = findTestData(Path).getValue(1, 33)
	
	checkVerifyEqualorMatch(WebUI.verifyMatch(resultDB, resultExcel, false,
		FailureHandling.CONTINUE_ON_FAILURE), TrxType)
}
else if(TrxType == 'Reject') {
	
	'ambil data role dari db'
	String resultDB = CustomKeywords.'transactionHistory.TransactionVerif.getstatusafterConfirmOrReject'(
		conndev, TrxNum)
	
	'ambil data role dari excel'
	String resultExcel = findTestData(Path).getValue(1, 34)
	
	checkVerifyEqualorMatch(WebUI.verifyMatch(resultDB, resultExcel, false,
		FailureHandling.CONTINUE_ON_FAILURE), TrxType)
}
else if(TrxType == 'Upload') {
	
	'ambil data role dari db'
	String resultDB = CustomKeywords.'transactionHistory.TransactionVerif.getstatusafterConfirmOrReject'(
		conndev, TrxNum)
	
	'ambil data role dari excel'
	String resultExcel = findTestData(Path).getValue(1, 31)
	
	checkVerifyEqualorMatch(WebUI.verifyMatch(resultDB, resultExcel, false,
		FailureHandling.CONTINUE_ON_FAILURE), TrxType)
}

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
	if (isMatch == false) {
		
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		GlobalVariable.FlagFailed = 1
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('RiwayatTransaksi', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
				GlobalVariable.FailedReasonStoreDB + ' ' + reason)
	}
}
