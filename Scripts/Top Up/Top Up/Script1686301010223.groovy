import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.By as By

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Edit Profile'
int countColumnEdit = findTestData(ExcelPathTopUp).getColumnNumbers()

'deklarasi koneksi ke Database adins_apiaas_uat'
Connection conndev = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_esign'()

'panggil fungsi login'
WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'TopUp', ('SheetName') : 'TopUp',
	('Path') : ExcelPathTopUp], FailureHandling.STOP_ON_FAILURE)

'klik pada tombol menu'
WebUI.click(findTestObject('Object Repository/Top Up/Page_Balance/spanMenu'))

'klik pada menu isi saldo'
WebUI.click(findTestObject('Object Repository/Top Up/Page_Balance/span_Isi Saldo'))

checkddlTipeSaldo()

for (GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++) {
	
	'set penanda error menjadi 0'
	GlobalVariable.FlagFailed = 0
		
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 1).length() == 0) {
		
		break
	}
	else if (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 1).equalsIgnoreCase('Unexecuted')) {
		
		'declare isMmandatory Complete'
		int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 5))
		
	}
}

def checkddlTipeSaldo() {
	
	'klik pada ddl tipe saldo'
	WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/span_tipesaldo'))
	
	'ambil list tipe saldo'
	def elementtipesaldo = DriverFactory.getWebDriver().findElements(By.xpath('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-topup-eendigo/div[2]/div/div/div/div/div/form/div[1]/div/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div'))
	
	'ambil hitungan TipeSaldo yang ada'
	int countWeb = (elementtipesaldo.size()) - 1
	
	'flag TipeSaldo sesuai'
	int isTipeSaldoFound = 0
	
	'ambil nama TipeSaldo dari DB'
	ArrayList<String> namaTipeSaldoDB = 
	
	'nama-nama tipe saldo sedang aktif dari UI'
	ArrayList<String> namaTipeSaldoUI = []
	
	'hitung banyak data didalam array DB'
	int countDB = namaTipeSaldoDB.size()
	
	'jika hitungan di UI dan DB sesuai'
	if(countWeb == countDB){
		
		for(int i=1; i<=countWeb; i++) {
			
			'ambil object dari ddl'
			def modifyNamaTipeSaldo = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/modifyObject'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-topup-eendigo/div[2]/div/div/div/div/div/form/div[1]/div/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div["+(i+1)+"]/span", true)
			
			'tambahkan nama tipe saldo ke array'
			String data = WebUI.getText(modifyNamaTipeSaldo)
			namaTipeSaldoUI.add(data)
		}
		
		'cek setiap data di UI dengan data di DB sebagai pembanding'
		for (String tipe : namaTipeSaldoDB){
			
			'jika ada data yang tidak terdapat pada arraylist yang lain'
			if (!namaTipeSaldoUI.contains(tipe)){
				
				'ada data yang tidak match'
				isTipeSaldoFound = 0;
				'berhentikan loop'
				break;
			}
			'kondisi ini bisa ditemui jika data match'
			isTipeSaldoFound = 1
		}
			
	}
	else if(isTipeSaldoFound == 0 || countWeb != countDB){
		
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Top Up', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonDDL)
	}
	
	'klik pada ddl tipe saldo'
	WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/span_tipesaldo'))
}

def checkddlMetodeTransfer() {
	
	'klik pada ddl metodetransfer'
	WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/span_paymentmethod'))
	
	'ambil list metode transfer'
	def elementTrfMethod = DriverFactory.getWebDriver().findElements(By.xpath('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-topup-eendigo/div[2]/div/div/div/div/div/form/div[2]/div/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div'))
	
	'ambil hitungan TrfMethod yang ada'
	int countWeb = (elementTrfMethod.size()) - 1
	
	'flag TrfMethod sesuai'
	int isTrfMethodFound = 0
	
	'ambil nama TrfMethod dari DB'
	ArrayList<String> namaTrfMethodDB =
	
	'nama-nama tipe saldo sedang aktif dari UI'
	ArrayList<String> namaTrfMethodUI = []
	
	'hitung banyak data didalam array DB'
	int countDB = namaTrfMethodDB.size()
	
	'jika hitungan di UI dan DB sesuai'
	if(countWeb == countDB){
		
		for(int i=1; i<=countWeb; i++) {
			
			'ambil object dari ddl'
			def modifyNamaTrfMethod = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/modifyObject'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-topup-eendigo/div[2]/div/div/div/div/div/form/div[2]/div/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div["+(i+1)+"]/span", true)
			
			'tambahkan nama tipe saldo ke array'
			String data = WebUI.getText(modifyNamaTrfMethod)
			namaTrfMethodUI.add(data)
		}
		
		'cek setiap data di UI dengan data di DB sebagai pembanding'
		for (String tipe : namaTrfMethodDB){
			
			'jika ada data yang tidak terdapat pada arraylist yang lain'
			if (!namaTrfMethodUI.contains(tipe)){
				
				'ada data yang tidak match'
				isTrfMethodFound = 0;
				'berhentikan loop'
				break;
			}
			'kondisi ini bisa ditemui jika data match'
			isTrfMethodFound = 1
		}
			
	}
	else if(isTrfMethodFound == 0 || countWeb != countDB){
		
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Top Up', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonDDL)
	}
	
	'klik pada ddl metodetransfer'
	WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/span_paymentmethod'))
}

def checkddlBankDestination() {
	
	'klik pada ddl bank destination'
	WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/span_bankdestination'))
	
	'ambil list bank destination'
	def elementBankDest = DriverFactory.getWebDriver().findElements(By.xpath('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-topup-eendigo/div[2]/div/div/div/div/div/form/div[3]/div/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div'))
	
	'ambil hitungan BankDest yang ada'
	int countWeb = (elementBankDest.size()) - 1
	
	'flag BankDest sesuai'
	int isBankDestFound = 0
	
	'ambil nama BankDest dari DB'
	ArrayList<String> namaBankDestDB =
	
	'nama-nama tipe saldo sedang aktif dari UI'
	ArrayList<String> namaBankDestUI = []
	
	'hitung banyak data didalam array DB'
	int countDB = namaBankDestDB.size()
	
	'jika hitungan di UI dan DB sesuai'
	if(countWeb == countDB){
		
		for(int i=1; i<=countWeb; i++) {
			
			'ambil object dari ddl'
			def modifyNamaBankDest = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/modifyObject'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-topup-eendigo/div[2]/div/div/div/div/div/form/div[3]/div/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div["+(i+1)+"]/span", true)
			
			'tambahkan nama tipe saldo ke array'
			String data = WebUI.getText(modifyNamaBankDest)
			namaBankDestUI.add(data)
		}
		
		'cek setiap data di UI dengan data di DB sebagai pembanding'
		for (String tipe : namaBankDestDB){
			
			'jika ada data yang tidak terdapat pada arraylist yang lain'
			if (!namaBankDestUI.contains(tipe)){
				
				'ada data yang tidak match'
				isBankDestFound = 0;
				'berhentikan loop'
				break;
			}
			'kondisi ini bisa ditemui jika data match'
			isBankDestFound = 1
		}
			
	}
	else if(isBankDestFound == 0 || countWeb != countDB){
		
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Top Up', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonDDL)
	}
	
	'klik pada ddl bank destination'
	WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/span_bankdestination'))
}