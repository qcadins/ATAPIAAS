import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection

'deklarasi koneksi ke DB eendigo_dev'
Connection conndev = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_esign'()

'ambil data role dari db'
String resultDB = CustomKeywords.'transactionhistory.TransactionVerif.getstatusafterConfirmOrReject'(
	conndev, TrxNum)

'deklarasi string result dari excel'
String resultExcel

'check transaction type and change the resultexcel'
switch (TrxType) {
	case 'Approve':
		'ambil data role dari excel'
		resultExcel = 'Pembayaran Berhasil'
		break
	case 'Reject':
		'ambil data role dari excel'
		resultExcel = 'Pembayaran Ditolak'
		break
	case 'Upload':
		'ambil data role dari excel'
		resultExcel = 'Menunggu Verifikasi Pembayaran'
		break
}

checkVerifyEqualorMatch(WebUI.verifyMatch(resultDB, resultExcel, false,
	FailureHandling.CONTINUE_ON_FAILURE), TrxType)

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
	if (isMatch == false) {
		
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		GlobalVariable.FlagFailed = 1
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(Sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathTranx).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
				GlobalVariable.FailedReasonStoreDB + ' ' + reason)
	}
}

def rowExcel(String cellValue) {
	CustomKeywords.'writetoexcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, Sheet, cellValue)
}
