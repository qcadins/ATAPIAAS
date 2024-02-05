import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection

Connection conn

if (GlobalVariable.SettingEnvi == 'Production') {
	'deklarasi koneksi ke Database eendigo_dev'
	conn = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_public'()
} else if (GlobalVariable.SettingEnvi == 'Trial') {
	'deklarasi koneksi ke Database eendigo_dev_uat'
	conn = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_devUat'()
}

'declare arraylist arraymatch'
ArrayList arrayMatch = []

'check if action new/services'
if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')).equalsIgnoreCase('New')
	|| findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')).equalsIgnoreCase('Edit')) {
	'get data balance mutation dari DB'
	ArrayList result = CustomKeywords.'tenant.TenantVerif.getTenantStoreDB'(conn,
		findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, rowExcel('$LabelRefNumber')))
	
	'get data services dari DB'
	ArrayList resultServices = CustomKeywords.'tenant.TenantVerif.getTenantServicesDescription'(conn,
		findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, rowExcel('$KodeTenant')))
	
	'declare arrayindex'
	arrayindex = 0
	
	'verify tenant name'
	arrayMatch.add(WebUI.verifyMatch(
		findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, rowExcel('$NamaTenant')).toUpperCase(),
		(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')).equalsIgnoreCase('New')) {
		'verify tenant code'
		arrayMatch.add(WebUI.verifyMatch(
			findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, rowExcel('$KodeTenant')).toUpperCase(),
			(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	} else {
		'skip'
		arrayindex++
	}
	
	'verify label ref number'
	arrayMatch.add(WebUI.verifyMatch(
		findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, rowExcel('$LabelRefNumber')).toUpperCase(),
			(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify API Key'
	arrayMatch.add(WebUI.verifyMatch(
		findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, rowExcel('API Key')).toUpperCase(),
			(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify Email reminder'
	arrayMatch.add(WebUI.verifyMatch(
		findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, rowExcel('Email')).toUpperCase().replace(';', ','),
			(result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'deklarasi array services'
	List<String> arrayServices = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, rowExcel('Services')).split(';', -1)
	
	'deklarasi batas saldo tiap service'
	ArrayList arrayServicesBatasSaldo = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn,
		rowExcel('Batas Saldo')).split(';', -1)
	
	'looping untuk verif services dan batas saldo'
	indexServices = 0
	
	for (indexExcel = 0 ; indexExcel < arrayServices.size(); indexExcel++) {
		String services = resultServices[indexServices++]
		
		if (services.equalsIgnoreCase(arrayServices[indexExcel])) {
			'verify services'
			arrayMatch.add(WebUI.verifyMatch(services.toUpperCase(), arrayServices[indexExcel].toUpperCase(),
					false, FailureHandling.CONTINUE_ON_FAILURE))
			
			'verify service batas saldo'
			arrayMatch.add(WebUI.verifyMatch(resultServices[indexServices++], arrayServicesBatasSaldo[indexExcel],
					false, FailureHandling.CONTINUE_ON_FAILURE))
		}
	}
} else if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')).equalsIgnoreCase('Service')) {
	'get data balacne mutation dari DB'
	String result = CustomKeywords.'tenant.TenantVerif.getTenantServices'(conn,
		findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, rowExcel('$Tenant'))).replace('{', '').replace('}', '').replace('"', '').replace(',', '')
	
	'split result to array'
	ArrayList resultarray = result.split(':0')
	
	'get array Services dari excel'
	ArrayList arrayServices = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, rowExcel('ServicesCheck')).split(';', -1)
	
	'verify services'
	arrayMatch.add(resultarray.containsAll(arrayServices))
} else if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')).equalsIgnoreCase('ChargeType')) {
	if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, rowExcel('Balance ChargeType Check')) != '') {
		'get array Services dari excel'
		arrayServices = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, rowExcel('Balance ChargeType Check')).split(';', -1)
		
		'looping untuk input services check'
		for (index = 0; index < arrayServices.size(); index++) {
			'ambil id pembayaran untuk service pertama yang diubah'
			int idpaymentType = CustomKeywords.'tenant.TenantVerif.getidpaymentType'(conn,
				findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, rowExcel('$KodeTenant')), arrayServices[index])
			
			'ambil jenis pembayaran untuk service yang terpilih'
			String paymentType = CustomKeywords.'tenant.TenantVerif.getpaymentType'(conn,
				findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, rowExcel('$KodeTenant')), idpaymentType)
			
			'split result to array'
			if (paymentType == 'Price') {
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
	GlobalVariable.FlagFailed = 1

	'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
	CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) +
		';' + GlobalVariable.FailedReasonStoreDB)
}

def rowExcel(String cellValue) {
	CustomKeywords.'writetoexcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}
