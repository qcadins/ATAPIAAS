import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection

'deklarasi koneksi ke Database eendigo_dev_uat'
Connection conndevUAT = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_devUat'()

'deklarasi koneksi ke DB eendigo_dev'
Connection conndev = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_esign'()

'ambil data role dan status dari DB'
ArrayList resultDB = []

'inisialisasi array dari Excel'
ArrayList resultExcel = []

'check if action new/edit/settings'
if (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 8).equalsIgnoreCase('New')) {
	
	'ambil data role dari db'
	String resultDB = CustomKeywords.'userManagement.RoleVerif.getNamaRole'(conndevUAT, 
		findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 20))
	
	'ambil data role dari excel'
	String resultExcel = findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 20)
	
	'jika hasil db tidak sesuai excel'
	if (resultDB != resultExcel) {
		
		'tulis adanya error pada sistem web'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Role', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
				GlobalVariable.FailedReasonStoreDB + ' New Role')
	}
	
} else if(findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 8).equalsIgnoreCase('Edit')) {
	
	'ambil data role dan status dari DB'
	resultDB = CustomKeywords.'userManagement.RoleVerif.getRoleEdit'(conndevUAT, 
		findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 17))
	
	'inisialisasi array dari Excel'
	resultExcel = []
	
	'cek data untuk tiap alamat di array'
	for (int i = 0; i < resultDB.size ; i++){
		
		'ambil data dari excel'
		resultExcel.add(findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, (17+i)))
		
		if (resultExcel[i] != resultDB[i]) {
			
			'tulis adanya error pada sistem web'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Role', GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
					GlobalVariable.FailedReasonStoreDB + ' Edit Role')
		}
	}
	
} else if(findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 8).equalsIgnoreCase('Settings')) {
	
	'ambil data result dari DB'
	resultDB = CustomKeywords.'userManagement.RoleVerif.getRoleMenu'(conndev, 
		findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 14), 
			findTestData(ExcelPathRole).getValue(2, 11))
	
	'ambil data menu role pada excel'
	resultExcel = findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 22).split(';', -1)
	
	'jika hasil kedua sumber tidak sesuai'
	if (resultDB != resultExcel) {
		
		'tulis adanya error pada sistem web'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Role', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
				GlobalVariable.FailedReasonStoreDB + ' Setting akses Role')
	}
}