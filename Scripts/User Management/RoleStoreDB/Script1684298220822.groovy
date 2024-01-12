import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.testdata.TestData as TestData
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection

'deklarasi koneksi ke Database eendigo_dev_uat'
Connection conndevUAT = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_devUat'()

'deklarasi koneksi ke DB eendigo_dev'
Connection conndev = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_esign'()

'ambil data role dan status dari DB'
ArrayList resultDB = []

'inisialisasi array dari Excel'
ArrayList resultExcel = []

'check if action new/edit/settings'
if (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')).equalsIgnoreCase('New')) {
	'ambil data role dari db'
	String resultDB = CustomKeywords.'usermanagement.RoleVerif.getNamaRole'(conndevUAT, 
		findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('$Add RoleName')))
	
	'ambil data role dari excel'
	String resultExcel = findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('$Add RoleName'))
	
	'jika hasil db tidak sesuai excel'
	if (resultDB != resultExcel) {
		GlobalVariable.FlagFailed = 1
		
		'tulis adanya error pada sistem web'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
				GlobalVariable.FailedReasonStoreDB + ' New Role')
	}
} else if (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')).equalsIgnoreCase('Edit')) {
	'ambil data role dan status dari DB'
	resultDB = CustomKeywords.'usermanagement.RoleVerif.getRoleEdit'(conndevUAT,
		findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('$Nama Role')))
	
	'inisialisasi array dari Excel'
	resultExcel = []
	
	'cek data untuk tiap alamat di array'
	for (int i = 0; i < resultDB.size ; i++) {
		'ambil data dari excel'
		resultExcel.add(findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, (rowExcel('$Nama Role') + i)))
		
		if (resultExcel[i] != resultDB[i]) {
			GlobalVariable.FlagFailed = 1
			
			'tulis adanya error pada sistem web'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
					GlobalVariable.FailedReasonStoreDB + ' Edit Role')
		}
	}
} else if (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')).equalsIgnoreCase('Settings')) {
	'ambil data result dari DB'
	resultDB = CustomKeywords.'usermanagement.RoleVerif.getRoleMenu'(conndev,
		findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('Nama Role')),
			findTestData(ExcelPathRole).getValue(2, rowExcel('Username Login')))
	
	'ambil data menu role pada excel'
	resultExcel = findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('$MenuChecked')).split(';', -1)
	
	'jika hasil kedua sumber tidak sesuai'
	if (resultDB != resultExcel) {
		GlobalVariable.FlagFailed = 1
		
		'tulis adanya error pada sistem web'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
				GlobalVariable.FailedReasonStoreDB + ' Setting akses Role')
	}
}

def rowExcel(String cellValue) {
	CustomKeywords.'writetoexcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}
