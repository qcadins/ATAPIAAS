import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import java.sql.Driver
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Isi Saldo'
int countColumnEdit = findTestData(ExcelPathLayananSaya).getColumnNumbers()

'deklarasi variabel untuk konek ke Database eendigo_dev'
def conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_public'()

//'deklarasi koneksi ke Database adins_apiaas_uat'
//def connProd = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_uatProduction'()

'deklarasi koneksi ke Database adins_apiaas_uat'
def conndevUAT = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_devUat'()

'panggil fungsi login'
WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'Layanan'], FailureHandling.STOP_ON_FAILURE)

'klik pada tombol profil di kanan atas'
WebUI.click(findTestObject('Object Repository/LayananSaya/Page_Balance/span_CHECK FINANCE'))

'pilih layanan saya'
WebUI.click(findTestObject('Object Repository/LayananSaya/Page_Balance/span_Layanan Saya'))

'user memilih perlu cek layanan production atau trial'
if(findTestData(ExcelPathLayananSaya).getValue(GlobalVariable.NumOfColumn, 12) == 'PRODUCTION')
{
	'klik pada api key production'
	WebUI.click(findTestObject('Object Repository/LayananSaya/Page_List Service/label_PRODUCTION'))
}
else
{
	'klik pada api key trial'
	WebUI.click(findTestObject('Object Repository/LayananSaya/Page_List Service/label_TRIAL'))
}

'ambil kode tenant di DB'
String tenantcode = CustomKeywords.'layananSaya.VerifLayanan.getTenantCodefromDB'(conn, 
	findTestData(ExcelPathLayananSaya).getValue(GlobalVariable.NumOfColumn, 8))

for(GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++){
	
	'set penanda error menjadi 0'
	GlobalVariable.FlagFailed = 0
		
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathLayananSaya).getValue(GlobalVariable.NumOfColumn, 1).length() == 0) {
		
		break
	} 
	else if (findTestData(ExcelPathLayananSaya).getValue(GlobalVariable.NumOfColumn, 1).equalsIgnoreCase('Unexecuted')) {
		
		'angka untuk menghitung data mandatory yang tidak terpenuhi'
		int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathLayananSaya).getValue(GlobalVariable.NumOfColumn, 4))
		
		'service name dari DB'
		ArrayList<String> serviceNameDB = CustomKeywords.'layananSaya.VerifLayanan.getListServiceName'(conndevUAT, tenantcode)
		
		'service status dari DB'
		ArrayList<String> serviceStatusDB = CustomKeywords.'layananSaya.VerifLayanan.getListServiceStatus'(conndevUAT, tenantcode)
		
		'service charge type dari DB'
		ArrayList<String> chargeTypeDB = CustomKeywords.'layananSaya.VerifLayanan.getListChargeType'(conndevUAT, tenantcode)
		
		'deklarasi service yang aktif di UI'
		ArrayList<String> serviceNameUI = new ArrayList<String>()
		
		'deklarasi status service di UI'
		ArrayList<String> serviceStatusUI = new ArrayList<String>()
		
		'deklarasi charge type di UI'
		ArrayList<String> chargeTypeUI = new ArrayList<String>()
		
		'ambil total data pada tabel'
		Total = WebUI.getText(findTestObject('Object Repository/LayananSaya/totalData')).split(' ')
		
		if(WebUI.verifyEqual(serviceNameDB.size(), Integer.parseInt(Total[0]), FailureHandling.OPTIONAL) == true){
			
			for(int row=0; row<serviceNameDB.size(); row++){
				
				if(row/10 == 1){
					
					'cek apakah button next enable atau disable'
					if(WebUI.verifyElementVisible(findTestObject('Object Repository/OCR Testing/Page_Balance/i_Catatan_datatable-icon-right'), FailureHandling.OPTIONAL)){
						
						'klik button next page'
						WebUI.click(findTestObject('Object Repository/OCR Testing/Page_Balance/i_Catatan_datatable-icon-right'))
					}
				}
				else{
					
					'ambil alamat trxnumber'
					def onepage = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-list-service > div > div > div > div:nth-child(3) > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))
					
					'banyaknya row table'
					int Index = onepage.size()
					
					'mulai perhitungan data service name'
					for(int i=1; i<=Index; i++){
						
						'ambil object dari ddl'
						def modifyServiceName = WebUI.modifyObjectProperty(findTestObject('Object Repository/LayananSaya/modifytablecontent'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-service/div/div/div/div[3]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+(i)+"]/datatable-body-row/div[2]/datatable-body-cell[1]/div/p", true)
							
						'tambahkan nama service name UI ke array'
						String data = WebUI.getText(modifyServiceName)
						serviceNameUI.add(data)
					}
					
					'mulai perhitungan data service status'
					for(int i=1; i<=Index; i++){
						
						'ambil object dari ddl'
						def modifyServiceStatus = WebUI.modifyObjectProperty(findTestObject('Object Repository/LayananSaya/modifytablecontent'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-service/div/div/div/div[3]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+(i)+"]/datatable-body-row/div[2]/datatable-body-cell[2]/div/p", true)
							
						'tambahkan nama service name UI ke array'
						String data = WebUI.getText(modifyServiceStatus)
						serviceStatusUI.add(data)
					}
					
					'mulai perhitungan data charge type'
					for(int i=1; i<=Index; i++){
						
						'ambil object dari ddl'
						def modifyChargeType = WebUI.modifyObjectProperty(findTestObject('Object Repository/LayananSaya/modifytablecontent'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-service/div/div/div/div[3]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+(i)+"]/datatable-body-row/div[2]/datatable-body-cell[3]/div/p", true)
							
						'tambahkan nama service name UI ke array'
						String data = WebUI.getText(modifyChargeType)
						chargeTypeUI.add(data)
					}
				}
			}
			
			'jika service name yang tampil UI tidak sesuai dengan DB'
			if (!serviceNameUI.containsAll(serviceNameDB)){
				
				GlobalVariable.FlagFailed = 1
				'Write to excel status failed and reason service tidak sesuai'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('LayananSaya', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathLayananSaya).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
						GlobalVariable.FailedReasonServiceNotMatch)
			}
			else if (!serviceStatusUI.containsAll(serviceStatusDB)){
				
				'jika service status yang tampil UI tidak sesuai dengan DB'
				GlobalVariable.FlagFailed = 1
				'Write to excel status failed and reason status tidak sesuai'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('LayananSaya', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathLayananSaya).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
						GlobalVariable.FailedReasonStatusNotMatch)
			}
			else if (!chargeTypeUI.containsAll(chargeTypeDB)){
				
				'jika chargetype yang tampil UI tidak sesuai dengan DB'
				GlobalVariable.FlagFailed = 1
				'Write to excel status failed and reason chargetype'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('LayananSaya', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathLayananSaya).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
						GlobalVariable.FailedReasonChargeTypeNotMatch)
			}
			if(isMandatoryComplete != 0){
				
				'jika chargetype yang tampil UI tidak sesuai dengan DB'
				GlobalVariable.FlagFailed = 1
				'Write to excel status failed and reason chargetype'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('LayananSaya', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathLayananSaya).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
						GlobalVariable.FailedReasonMandatory)
			}
			if(isMandatoryComplete  == 0 && GlobalVariable.FlagFailed == 0){
				
				'write to excel success'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'LayananSaya', 0,
					GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
			}
		}
		else{
			
			'Write to excel status failed karena table bermasalah'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('LayananSaya', GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathLayananSaya).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
					GlobalVariable.FailedReasonTable)
		}
		
		'lakukan refresh laman untuk kembali ke halaman 1'
		WebUI.refresh()
	}
}
'tutup browser'
WebUI.closeBrowser()