import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection

'deklarasi koneksi ke DB eendigo_dev'
Connection conndev = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_esign'()

'ambil data coupon dari db'
ArrayList<String> resultDB = CustomKeywords.'topup.TopupVerif.getStoreDBTopup1'(conndev, noTrx)

'tambah data dari query lain ke arraylist yang sudah ada'
resultDB.addAll(CustomKeywords.'topup.TopupVerif.getStoreDBTopup2'(conndev, noTrx))
	
'ambil data coupon dari excel'
ArrayList<String> resultExcel = []
	
'cek data untuk tiap alamat di array'
for (int i = 0; i < resultDB.size ; i++) {
		
	'tambahkan data dari excel'
	resultExcel.add(findTestData(Path).getValue(GlobalVariable.NumOfColumn, 9+i))
}
	
'jika hasil excel tidak sesuai db'
if(!resultExcel.containsAll(resultDB)) {
		
	GlobalVariable.FlagFailed = 1
		
	'tulis adanya error pada proses storeDB'
	CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('RiwayatTransaksi', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(Path).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
			GlobalVariable.FailedReasonStoreDB)
}