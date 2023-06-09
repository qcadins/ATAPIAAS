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
	ArrayList<String> resultDB = CustomKeywords.'userManagement.UserVerif.getNewUserData'(conndev, 
		findTestData(Path).getValue(GlobalVariable.NumOfColumn, 18))
	
	'ambil data role dari excel'
	ArrayList<String> resultExcel = []
	
	'cek data untuk tiap alamat di array'
	for (int i = 0; i < resultDB.size ; i++){
		
		'tambahkan data ke resultExcel'
		resultExcel.add(findTestData(Path).getValue(GlobalVariable.NumOfColumn, (18+i)))
		
		if(resultExcel[i] != resultDB[i]) {
			
			'tulis adanya error pada sistem web'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('User', GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(Path).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
					GlobalVariable.FailedReasonStoreDB)
		}
	}
}
else if(findTestData(Path).getValue(GlobalVariable.NumOfColumn, 8).equalsIgnoreCase('Edit')) {
	
	'ambil data role dari db'
	ArrayList<String> resultDB = CustomKeywords.'userManagement.UserVerif.getEditUserData'(conndev, 
		findTestData(Path).getValue(GlobalVariable.NumOfColumn, 14))
	
	'ambil data role dari excel'
	ArrayList<String> resultExcel = []
		
	'cek data untuk tiap alamat di array'
	for (int i = 0; i < resultDB.size ; i++) {
	
		'tambahkan data ke resultExcel'
		resultExcel.add(findTestData(Path).getValue(GlobalVariable.NumOfColumn, (25+i)))
		
		if(resultExcel[i] != resultDB[i]) {
			
			'tulis adanya error pada sistem web'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('User', GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(Path).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
					GlobalVariable.FailedReasonStoreDB)
		}
	}
}