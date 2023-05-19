import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection

'deklarasi koneksi ke Database adins_apiaas_uat'
Connection conndevUAT = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_devUat'()

'check if action new/services'
if (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 7).equalsIgnoreCase('New')) {
	
	'ambil data role dari db'
	String resultDB = CustomKeywords.'userManagement.RoleVerif.getNamaRole'(conndevUAT, 
		findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 19))
	
	'ambil data role dari excel'
	String resultExcel = findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 19)
	
	'jika hasil db tidak sesuai excel'
	if(resultDB != resultExcel) {
		'tulis adanya error pada sistem web'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Role', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
				GlobalVariable.FailedReasonStoreDB)
	}
}
else if(findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 7).equalsIgnoreCase('Edit')) {
	
	'ambil data role dan status dari DB'
	ArrayList<String> resultDB = CustomKeywords.'userManagement.RoleVerif.getRoleEdit'(conndevUAT, 
		findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 16))
	
	'inisialisasi array dari Excel'
	ArrayList<String> resultExcel = []
	
	'cek data untuk tiap alamat di array'
	for (int i = 0; i < resultDB.size ; i++){
		
		'ambil data dari excel'
		resultExcel.add(findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, (16+i)))
		
		if(resultExcel[i] != resultDB[i]) {
			'tulis adanya error pada sistem web'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Role', GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
					GlobalVariable.FailedReasonStoreDB)
		}
	}
}
else if(findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 7).equalsIgnoreCase('Settings')) {
	
	'ambil data result dari DB'
	ArrayList<String> resultDB = CustomKeywords.'userManagement.RoleVerif.getRoleMenu'(conndevUAT, 
		findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 13), 
			findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 10))
	
	'ambil data menu role pada excel'
	ArrayList<String> resultExcel = findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 21).split(';', -1)
	
	'jika hasil kedua sumber tidak sesuai'
	if(resultDB != resultExcel)
	{
		'tulis adanya error pada sistem web'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Role', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
				GlobalVariable.FailedReasonStoreDB)
	}
}