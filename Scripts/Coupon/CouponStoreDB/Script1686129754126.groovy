import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection

'deklarasi koneksi ke DB eendigo_dev'
Connection conndev = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_esign'()

'check if action new/edit'
if (findTestData(Path).getValue(GlobalVariable.NumOfColumn, 8).equalsIgnoreCase('New')) {
	
	'ambil data role dari db'
	ArrayList<String> resultDB = CustomKeywords.'coupon.couponverif.getAddEditCoupon'(conndev, 
		findTestData(Path).getValue(GlobalVariable.NumOfColumn, 21))
	
	'ambil data role dari excel'
	ArrayList<String> resultExcel = []
	
	'cek data untuk tiap alamat di array'
	for (int i = 0; i < resultDB.size ; i++){
		
		'tambahkan data dari excel'
		resultExcel.add(findTestData(Path).getValue(GlobalVariable.NumOfColumn, 20+i))
	}
	
	'jika hasil excel tidak sesuai db'
	if(!resultExcel.containsAll(resultDB)) {
		
		GlobalVariable.FlagFailed = 1
		
		'tulis adanya error pada sistem web'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Coupon', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(Path).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
				GlobalVariable.FailedReasonStoreDB)
	}
}
else if(findTestData(Path).getValue(GlobalVariable.NumOfColumn, 8).equalsIgnoreCase('Edit')) {
	
	'ambil data role dari db'
	ArrayList<String> resultDB = CustomKeywords.'coupon.couponverif.getAddEditCoupon'(conndev, 
		findTestData(Path).getValue(GlobalVariable.NumOfColumn, 31))
	
	'ambil data role dari excel'
	ArrayList<String> resultExcel = []
		
	'cek data untuk tiap alamat di array'
	for (int i = 0; i < resultDB.size ; i++) {
		
		'tambahkan data ke resultExcel'
		resultExcel.add(findTestData(Path).getValue(GlobalVariable.NumOfColumn, (31+i)))
	}
	
	'jika hasil excel tidak sesuai db'
	if (!resultExcel.containsAll(resultDB)) {
		
		GlobalVariable.FlagFailed = 1
		
		'tulis adanya error pada sistem web'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Coupon', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(Path).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
				GlobalVariable.FailedReasonStoreDB)
	}
}