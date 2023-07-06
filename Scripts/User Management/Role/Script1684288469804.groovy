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
import org.openqa.selenium.WebDriver as WebDriver
import org.openqa.selenium.By as By

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Edit Profile'
int countColumnEdit = findTestData(ExcelPathRole).getColumnNumbers()

'deklarasi koneksi ke Database adins_apiaas_uat'
Connection conndevUAT = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_public'()

'panggil fungsi login'
WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'Role', ('SheetName') : 'Role',
	('Path') : ExcelPathRole], FailureHandling.STOP_ON_FAILURE)

'klik pada menu'
WebUI.click(findTestObject('Object Repository/User Management-Role/'+
	'Page_Balance/i_SEDARA MANYURA_ft-menu font-medium-3'))

'pilih submenu manage user'
WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Balance/a_Manage User'))

'klik pada roles'
WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Balance/span_Roles'))

'panggil fungsi check paging'
checkPaging(conndevUAT)

for (GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++) {
	
	'set penanda error menjadi 0'
	GlobalVariable.FlagFailed = 0
		
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 1).length() == 0) {
		
		break
	}
	else if (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 1).equalsIgnoreCase('Unexecuted')) {
		
		'declare isMmandatory Complete'
		int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 5))
		
		'klik pada menu'
		WebUI.click(findTestObject('Object Repository/User Management-Role/'+
			'Page_Balance/i_SEDARA MANYURA_ft-menu font-medium-3'))
		
		'klik pada roles'
		WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Balance/span_Roles'))
		
		'cek apakah tombol menu dalam jangkauan web'
		if(WebUI.verifyElementVisible(findTestObject('Object Repository/User Management-Role/'+
			'Page_List Roles/i_Logout_ft-x ng-tns-c133-2'), FailureHandling.OPTIONAL)) {
			
			'klik pada tombol silang menu'
			WebUI.click(findTestObject('Object Repository/User Management-Role/'+
			'Page_List Roles/i_Logout_ft-x ng-tns-c133-2'))
		}
		
		'check if action new/edit/settings'
		if (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 8).equalsIgnoreCase('New')){
			
			'klik pada tombol New'
			WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/a_New'))
			
			'input nama role baru yang akan ditambahkan'
			WebUI.setText(findTestObject('Object Repository/User Management-Role/Page_Add Role/input__roleName'),
				findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 20))
			
			'panggil fungsi cek konfirmasi dialog'
			if(checkdialogConfirmation(isMandatoryComplete) == true) {
				
				'jika failed mandatory continue testcase'
				continue
			}
			
			'input nama role'
			WebUI.setText(findTestObject('Object Repository/User Management-Role/Page_List Roles/input_Role Name_roleName'),
			findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 20))

			'klik pada tombol cari'
			WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/button_Search'))	
			
			'verify nama depan'
			checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/User Management-Role/label_Role')), findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 20), false, FailureHandling.CONTINUE_ON_FAILURE), ' Nama Role')
		}
		else if (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 8).equalsIgnoreCase('Edit')) {
			
			'panggil fungsi cari role'
			searchRole()
			
			'klik pada edit role'
			WebUI.click(findTestObject('Object Repository/User Management-Role/'+
				'Page_List Roles/em_Action_align-middle cursor-pointer font-_0818c8'))
			
			'panggil fungsi verifikasi sebelum edit'
			beforeEditVerif(conndevUAT)
			
			'input nama role yang akan diubah'
			WebUI.setText(findTestObject('Object Repository/User Management-Role/Page_Edit Role/input__roleName'),
				findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 17))
			
			'input status role yang akan diubah'
			WebUI.setText(findTestObject('Object Repository/User Management-Role/Page_Edit Role/input_status'),
				findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 18))
			
			'enter pada status'
			WebUI.sendKeys(findTestObject('Object Repository/User Management-Role/Page_Edit Role/input_status'),
				Keys.chord(Keys.ENTER))
			
			'panggil fungsi cek konfirmasi dialog'
			if(checkdialogConfirmation(isMandatoryComplete) == true) {
				
				'jika failed mandatory continue testcase'
				continue
			}
			
			'input nama role'
			WebUI.setText(findTestObject('Object Repository/User Management-Role/Page_List Roles/input_Role Name_roleName'),
					findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 17))
		
			'input status role'
			WebUI.setText(findTestObject('Object Repository/User Management-Role/Page_List Roles/input_Status'),
				findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 18))
			
			'enter pada status'
			WebUI.sendKeys(findTestObject('Object Repository/User Management-Role/Page_List Roles/input_Status'),
				 Keys.chord(Keys.ENTER))
			
			'klik pada tombol cari'
			WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/button_Search'))
			
			'verify nama depan'
			checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/User Management-Role/label_Role')), findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 17), false, FailureHandling.CONTINUE_ON_FAILURE), ' Nama Role')
		}
		else if (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 8).equalsIgnoreCase('Settings')) {
			
			'panggil fungsi cari role'
			searchRole()
			
			'klik pada setting role'
			WebUI.click(findTestObject('Object Repository/User Management-Role/'+
				'Page_List Roles/em_Action_align-middle cursor-pointer font-_f80ca2'))
			
			'get array Services dari excel'
			arrayServices = findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 22).split(';', -1)
			
			'looping untuk input services check'
			for (index = 0; index < arrayServices.size(); index++){
				
				'modify object checkbox'
				modifyObjectCheckbox = WebUI.modifyObjectProperty(
					findTestObject('Object Repository/User Management-Role/modifyObject'), 'xpath', 'equals',
				 		('//*[@id="'+ arrayServices[index] +'"]'), true)
			
				'check if check box is unchecked'
				if (WebUI.verifyElementNotChecked(modifyObjectCheckbox, GlobalVariable.Timeout, FailureHandling.OPTIONAL)){
					
					'click checkbox'
					WebUI.click(modifyObjectCheckbox)
				}
			}
			
			'get array Services uncheck dari excel'
			arrayServices = findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 23).split(';', -1)

			'looping untuk input services uncheck'
			for (index = 0; index < arrayServices.size(); index++){
				
				'modify object checkbox'
				modifyObjectCheckbox = WebUI.modifyObjectProperty(
					findTestObject('Object Repository/User Management-Role/modifyObject'), 'xpath', 'equals',
				 		('//*[@id="'+ arrayServices[index] +'"]'), true)
				
				'check if check box is checked'
				if (WebUI.verifyElementChecked(modifyObjectCheckbox, GlobalVariable.Timeout, FailureHandling.OPTIONAL)){
					
					'click checkbox'
					WebUI.click(modifyObjectCheckbox)
				}
			}
			
			'klik pada button save services'
			WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Setting Menu/button_Save'))
			
			'jika muncul alert success'
			if (WebUI.getText(findTestObject('Object Repository/User Management-Role/'+
				'Page_Setting Menu/div_Success')) == 'Success') {
			
				'klik pada tombol OK'
				WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Setting Menu/button_OK'))
				
				'cek apakah muncul error unknown setelah klik pada tombol ok'
				if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'),
					GlobalVariable.Timeout, FailureHandling.OPTIONAL) == false) {
					
					GlobalVariable.FlagFailed = 1
					
					'tulis adanya error pada sistem web'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Role', GlobalVariable.NumOfColumn,
						GlobalVariable.StatusWarning, (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
							GlobalVariable.FailedReasonUnknown)
				}
			
				'jika mandatory lengkap dan tidak ada error'
				if (isMandatoryComplete == 0 && GlobalVariable.FlagFailed == 0) {
					
					'write to excel success'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Role', 0,
						GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
					
					'cek apakah perlu pengecekan ke db'
					if(GlobalVariable.KondisiCekDB == 'Yes') {
						
						'panggil fungsi storeDB'
						WebUI.callTestCase(findTestCase('Test Cases/User Management/RoleStoreDB'), [:], FailureHandling.STOP_ON_FAILURE)
					}
				}
				else {
					
					'klik pada tombol OK'
					WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Setting Menu/button_OK'))
					
					'tulis adanya mandatory tidak lengkap'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Role', GlobalVariable.NumOfColumn,
						GlobalVariable.StatusFailed, (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
							GlobalVariable.FailedReasonMandatory)
					
					'cek apakah muncul error unknown setelah klik pada tombol ok'
					if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'),
						GlobalVariable.Timeout, FailureHandling.OPTIONAL) == false) {
						
						GlobalVariable.FlagFailed = 1
						
						'tulis adanya error pada sistem web'
						CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Role', GlobalVariable.NumOfColumn,
							GlobalVariable.StatusWarning, (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
								GlobalVariable.FailedReasonUnknown)
					}
				}
			}
			else {
				'tulis adanya error saat setting access menu role'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Role', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
						GlobalVariable.FailedReasonMenuRole)
			}
		}
		
//		WebUI.refresh()
//		
//		'cek apakah muncul error setelah login'
//		if(WebUI.verifyElementPresent(findTestObject('Object Repository/Profile/'+
//			'Page_Balance/div_Unknown Error'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
//			
//			'tulis adanya error pada sistem web'
//			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Role', GlobalVariable.NumOfColumn,
//				GlobalVariable.StatusFailed, (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
//					GlobalVariable.FailedReasonUnknown)
//		}
	}
}

WebUI.closeBrowser()

def searchRole() {
	'input nama role'
	WebUI.setText(findTestObject('Object Repository/User Management-Role/Page_List Roles/input_Role Name_roleName'),
			findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 14))

	'input status role'
	WebUI.setText(findTestObject('Object Repository/User Management-Role/Page_List Roles/input_Status'),
		findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 15))
	
	'enter pada status'
	WebUI.sendKeys(findTestObject('Object Repository/User Management-Role/Page_List Roles/input_Status'),
		 Keys.chord(Keys.ENTER))
	
	'klik pada tombol cari'
	WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/button_Search'))
}

def checkdialogConfirmation(int isMandatoryComplete) {
	
	boolean shouldcontinue = false
	
	if (WebUI.verifyElementHasAttribute(findTestObject('Object Repository/User Management-Role/Page_Add Role/button_Next'), 
		'disabled', GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
			
		'tulis adanya mandatory tidak lengkap'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Role', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
				GlobalVariable.FailedReasonMandatory)
		
		'klik pada tombol batal'
		WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Add Role/button_Cancel'))
		
		shouldcontinue = true
	}
	else {
		
		'klik pada tombol lanjut'
		WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Add Role/button_Next'))
		
		'klik tombol ya untuk menambahkan'
		WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Add Role/button_Ya'))
		
		'jika error muncul'
		if(WebUI.getText(findTestObject('Object Repository/User Management-Role/'+
			'Page_Add Role/KonfirmasiAdd')).contains('Sudah Ada')) {
		
			'klik tombol OK'
			WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Add Role/button_OK'))
		
			'tulis adanya error pada sistem web'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Role', GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
					GlobalVariable.FailedReasonExisted)
			
			shouldcontinue = true
		}
		else {
			
			'klik tombol OK'
			WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Add Role/button_OK'))
			
			'cek apakah muncul error unknown setelah klik pada tombol ok'
			if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'),
				GlobalVariable.Timeout, FailureHandling.OPTIONAL) == false) {
				
				GlobalVariable.FlagFailed = 1
				
				'tulis adanya error pada sistem web'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Role', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusWarning, (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
						GlobalVariable.FailedReasonUnknown)
			}
			
			'jika mandatory lengkap dan tidak ada failure'
			if (isMandatoryComplete == 0 && GlobalVariable.FlagFailed == 0) {
				
				'write to excel success'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Role', 0,
					GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
				
				'cek apakah perlu untuk cek ke DB'
				if (GlobalVariable.KondisiCekDB == 'Yes') {
					
					'panggil fungsi storeDB'
					WebUI.callTestCase(findTestCase('Test Cases/User Management/RoleStoreDB'), [:], FailureHandling.STOP_ON_FAILURE)
		
				}
			}
		}
	}
	
	return shouldcontinue
}

def beforeEditVerif (Connection connUAT) {
	'siapkan array untuk ambil data role dari UI'
	ArrayList<String> roleUI = []
	
	'siapkan array untuk ambil data role dari DB'
	ArrayList<String> roleDB = CustomKeywords.'userManagement.RoleVerif.getDataRolebeforeVerif'(connUAT, WebUI.getText(findTestObject('Object Repository/User Management-Role/Page_Edit Role/input__roleName')))
	
	'ambil text pada nama role dan tambahkan ke array role UI'
	roleUI.add(WebUI.getText(findTestObject('Object Repository/User Management-Role/Page_Edit Role/input__roleName')))
	
	'ambil text pada status role dan tambah ke array role UI'
	roleUI.add(WebUI.getText(findTestObject('Object Repository/User Management-Role/Page_Edit Role/StatusEditBefore')))

	for (int i = 0; i < roleDB.size(); i++) {
		
		'cek apakah tiap isi dari db sesuai'
		if (roleDB[i] != roleUI[i]) {
			
			'Write To Excel gagal dan bermasalah saat verifikasi data'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Role', GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 2) +
					';') + GlobalVariable.FailedReasonEditVerif)

			GlobalVariable.FlagFailed = 1
		}
	}
}

def checkPaging(Connection connUAT) {
	
	'input nama role'
	WebUI.setText(findTestObject('Object Repository/User Management-Role/Page_List Roles/input_Role Name_roleName'), 
			findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 14))

	'input status role'
	WebUI.setText(findTestObject('Object Repository/User Management-Role/Page_List Roles/input_Status'),
		findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 15))
	
	'enter pada status'
	WebUI.sendKeys(findTestObject('Object Repository/User Management-Role/Page_List Roles/input_Status'),
		 Keys.chord(Keys.ENTER))
	
	'klik pada tombol cari'
	WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/button_Search'))
	
	'klik pada tombol reset'
	WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/button_Reset'))
	
	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management-Role/'+
		'Page_List Roles/input_Role Name_roleName'),
			'value', FailureHandling.CONTINUE_ON_FAILURE),'', false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management-Role/'+
		'Page_List Roles/input_Status'),
			'value', FailureHandling.CONTINUE_ON_FAILURE),'', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'klik pada tombol cari'
	WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/button_Search'))
	
	'cari button skip di footer'
	def elementbutton = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout >'+
		' div > div.main-panel > div > div.content-wrapper > app-list-roles > app-msx-paging > app-msx-datatable >'+
		' section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))
	
	'ambil banyaknya laman footer'
	int lastPage = elementbutton.size()

	'get data total dari tabel role'
	int resultTotalData = CustomKeywords.'userManagement.RoleVerif.getRoleTotal'(connUAT, 
		findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 11))
	
	'get text total data dari ui'
	Total = WebUI.getText(findTestObject('Object Repository/User Management-Role/Page_List Roles/TotalData')).split(' ')

	'verify total data role'
	checkVerifyPaging(WebUI.verifyEqual(resultTotalData, Integer.parseInt(Total[0]), FailureHandling.CONTINUE_ON_FAILURE))
	
	'cek apakah hlm 2 tersedia'
	if (WebUI.verifyElementVisible(findTestObject('Object Repository/User Management-Role/Page_List Roles/a_2')) == true) {
	
		'klik halaman 2'
		WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/a_2'))
		
		'verify paging di page 2'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management-Role/'+
			'Page_List Roles/Page2'),'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'klik halaman 1'
		WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/a_1'))
		
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management-Role/'+
			'Page_List Roles/Page1'),'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'klik button next page'
		WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/i_Action_datatable-icon-right'))
		
		'verify paging di page 2'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/'+
			'User Management-Role/Page_List Roles/Page2'),'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'klik prev page'
		WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/i_Action_datatable-icon-left'))
		
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management-Role/'+
			'Page_List Roles/Page1'),'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'cek apakah button skip disabled atau enabled'
		if (WebUI.verifyElementVisible(
			findTestObject('Object Repository/User Management-Role/Page_List Roles/i_Action_datatable-icon-skip'), FailureHandling.OPTIONAL)) {
		
			'klik pada tombol skip'
			WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/i_Action_datatable-icon-skip'))
		}
		
		'modify object laman terakhir'
		def modifyObjectmaxPage = WebUI.modifyObjectProperty(
			findTestObject('Object Repository/User Management-Role/modifyObject'),
			'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-roles/app-msx-paging/"+
			"app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li["+ (lastPage - 2) +"]", true)
		
		'verify paging di page terakhir'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(modifyObjectmaxPage,
			'class', FailureHandling.CONTINUE_ON_FAILURE), 'pages active ng-star-inserted',
				false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'click min page'
		WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/i_Action_datatable-icon-prev'))
		
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management-Role/'+
			'Page_List Roles/Page1'),'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
	}
}

def checkVerifyPaging(Boolean isMatch) {
	if (isMatch == false) {
		
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Role', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 2) +
				';') + GlobalVariable.FailedReasonPagingError)

		GlobalVariable.FlagFailed = 1
	}
}

'fungsi untuk melakukan pengecekan '
def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
	if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
		
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Role', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
			(findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.FailedReasonVerifyEqualorMatch + reason)

		GlobalVariable.FlagFailed = 1
	}
}