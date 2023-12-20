import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection

Connection conn

if(GlobalVariable.SettingEnvi == 'Production') {
	'deklarasi koneksi ke Database eendigo_dev'
	conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_public'()
} else if(GlobalVariable.SettingEnvi == 'Trial') {
	'deklarasi koneksi ke Database eendigo_dev_uat'
	conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_devUat'()
}

'declare arraylist arraymatch'
ArrayList arrayMatch = []

'check if action new/services'
if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 8).equalsIgnoreCase('New') 
	|| findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 8).equalsIgnoreCase('Edit')) {
	
'get data balance mutation dari DB'
ArrayList result = CustomKeywords.'tenant.TenantVerif.getTenantStoreDB'(conn,
	findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 15))

'get data services dari DB'
ArrayList resultServices = CustomKeywords.'tenant.TenantVerif.getTenantServicesDescription'(conn,
	findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 14))

'declare arrayindex'
arrayindex = 0

'verify tenant name'
arrayMatch.add(WebUI.verifyMatch(
	findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 13).toUpperCase(),
	(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 8).equalsIgnoreCase('New')) {
	
	'verify tenant code'
	arrayMatch.add(WebUI.verifyMatch(
		findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 14).toUpperCase(),
		(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
} else {
	
	'skip'
	arrayindex++
}

'verify label ref number'
arrayMatch.add(WebUI.verifyMatch(
	findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 15).toUpperCase(),
	(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify API Key'
arrayMatch.add(WebUI.verifyMatch(
	findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 17).toUpperCase(),
	(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify Email reminder'
arrayMatch.add(WebUI.verifyMatch(
	findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 22).toUpperCase().replace(';',','),
	(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'deklarasi array services'
ArrayList arrayServices = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 19).split(';',-1)

'deklarasi batas saldo tiap service'
ArrayList arrayServicesBatasSaldo = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn,
	20).split(';',-1)

'looping untuk verif services dan batas saldo'
indexServices = 0

for (indexExcel = 0 ; indexExcel < arrayServices.size(); indexExcel++) {
	
	String services = resultServices[indexServices++].toString()
	
	if (services.equalsIgnoreCase(arrayServices[indexExcel])) {
		
		'verify services'
		arrayMatch.add(WebUI.verifyMatch(services.toUpperCase(), arrayServices[indexExcel].toUpperCase(),
				false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'verify service batas saldo'
		arrayMatch.add(WebUI.verifyMatch(resultServices[indexServices++], arrayServicesBatasSaldo[indexExcel],
				false, FailureHandling.CONTINUE_ON_FAILURE))
	}

}

} else if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 8).equalsIgnoreCase('Service')) {
	
	'get data balacne mutation dari DB'
	String result = CustomKeywords.'tenant.TenantVerif.getTenantServices'(conn, 
		findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 10)).replace('{','').replace('}','').replace('"','').replace(',','')
	
	'split result to array'
	ArrayList resultarray = result.split(':0')
	
	'get array Services dari excel'
	ArrayList arrayServices = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 26).split(';', -1)
	
	'verify services'
	arrayMatch.add(resultarray.containsAll(arrayServices))
	
} else if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 8).equalsIgnoreCase('ChargeType')) {
	
	if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 30) != '') {
		
		'get array Services dari excel'
		arrayServices = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 30).split(';', -1)
		
		'looping untuk input services check'
		for (index = 0; index < arrayServices.size(); index++) {
			
			'ambil id pembayaran untuk service pertama yang diubah'
			int IDPaymentType = CustomKeywords.'tenant.TenantVerif.getIDPaymentType'(conn,
				findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 14), arrayServices[index])
			
			'ambil jenis pembayaran untuk service yang terpilih'
			String PaymentType = CustomKeywords.'tenant.TenantVerif.getPaymentType'(conn,
				findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 14), IDPaymentType)
			
			'split result to array'
			if (PaymentType == 'Price') {
				
				'verify services'
				arrayMatch.add(true)
				
			} else {
				
				'verify services'
				arrayMatch.add(false)
			}
		}
	}
}


'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {

	'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
	CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 2) +
		';' + GlobalVariable.FailedReasonStoreDB)
}