import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.testdata.TestData as TestData
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection

'deklarasi koneksi ke DB eendigo_dev'
Connection conndev = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_esign'()

'ambil data role dari db'
ArrayList resultDB = []

'ambil data role dari excel'
ArrayList resultExcel = []

'check if action new/edit'
if (findTestData(Path).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')).equalsIgnoreCase('New')) {
	'isi array dari DB'
	resultDB = CustomKeywords.'usermanagement.UserVerif.getNewUserData'(conndev,
		findTestData(Path).getValue(GlobalVariable.NumOfColumn, rowExcel('$Email')),
			findTestData(Path).getValue(GlobalVariable.NumOfColumn, rowExcel('Username Login')))
	
	'cek data untuk tiap alamat di array'
	for (int i = 0; i < resultDB.size ; i++) {
		
		'tambahkan data ke resultExcel'
		resultExcel.add(findTestData(Path).getValue(GlobalVariable.NumOfColumn, (rowExcel('$Email') + i)))
		
		if (resultExcel[i] != resultDB[i]) {
			
			GlobalVariable.FlagFailed = 1
			
			'tulis adanya error pada sistem web'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(Path).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
					GlobalVariable.FailedReasonStoreDB + ' User baru')
		}
	}
} else if (findTestData(Path).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')).equalsIgnoreCase('Edit')) {
	
	'ambil data role dari db'
	resultDB = CustomKeywords.'usermanagement.UserVerif.getEditUserData'(conndev, 
		findTestData(Path).getValue(GlobalVariable.NumOfColumn, rowExcel('Email')))
		
	'cek data untuk tiap alamat di array'
	for (int i = 0; i < resultDB.size ; i++) {
	
		if (i == 0) {
			
			'tambahkan data ke array resultExcel'
			resultExcel.add(findTestData(Path).getValue(GlobalVariable.NumOfColumn, rowExcel('Email')))
			
		} else {
			
			'tambahkan data ke resultExcel'
			resultExcel.add(findTestData(Path).getValue(GlobalVariable.NumOfColumn, (rowExcel('Edit User') + i)))
		}
		
		if (resultExcel[i] != resultDB[i]) {
			
			GlobalVariable.FlagFailed = 1
			
			'tulis adanya error pada sistem web'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(Path).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
					GlobalVariable.FailedReasonStoreDB + ' Edit user')
		}
	}
}

def rowExcel(String cellValue) {
	CustomKeywords.'writetoexcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}
