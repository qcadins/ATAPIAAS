import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.By as By

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writetoexcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Edit Profile'
int countColumnEdit = findTestData(ExcelPathRole).columnNumbers

'deklarasi koneksi ke Database adins_apiaas_uat'
Connection conndevUAT = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_public'()

'panggil fungsi login'
WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : sheet, ('SheetName') : sheet,
	('Path') : ExcelPathRole, ('Username') : 'Username Login', ('Password') : 'Password Login',], FailureHandling.STOP_ON_FAILURE)

'klik pada menu'
WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Balance/span_Menu'))

'pilih submenu manage user'
WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Balance/a_Manage User'))

'klik pada roles'
WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Balance/span_Roles'))

'panggil fungsi check paging'
checkPaging(conndevUAT)

for (GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++) {
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).length() == 0) {
		break
	} else if (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
		'set penanda error menjadi 0'
		GlobalVariable.FlagFailed = 0
		
		'declare isMmandatory Complete'
		int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('Is Mandatory Complete')))
		
		'klik pada menu'
		WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Balance/span_Menu'))
		
		'klik pada roles'
		WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Balance/span_Roles'))
		
		'cek apakah tombol menu dalam jangkauan web'
		if (WebUI.verifyElementVisible(findTestObject('Object Repository/User Management-Role/Page_List Roles/tombolX_menu'), FailureHandling.OPTIONAL)) {
			'klik pada tombol silang menu'
			WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/tombolX_menu'))
		}
		
		'check if action new/edit/settings'
		if (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')).equalsIgnoreCase('New')) {
			'klik pada tombol New'
			WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/a_New'))
			
			'input nama role baru yang akan ditambahkan'
			WebUI.setText(findTestObject('Object Repository/User Management-Role/Page_Add Role/input__roleName'),
				findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('$Add RoleName')))
			
			'panggil fungsi cek konfirmasi dialog'
			if (checkdialogConfirmation(isMandatoryComplete) == true) {
				'jika failed mandatory continue testcase'
				continue
			}
			
			'pengecekan untuk field empty'
			if (GlobalVariable.NumOfColumn == 2) {
				'klik pada tombol batal'
				WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Add Role/button_Cancel'))
				
				'klik pada tombol New'
				WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/a_New'))
			
				'verify element field kosong'
				checkVerifyEqualOrMatch(WebUI.verifyElementAttributeValue(findTestObject('Object Repository/User Management-Role/Page_Add Role/input__roleName'),
					'class', 'form-control ng-untouched ng-pristine ng-invalid',
					GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Field add role tidak kosong')
				
				'input nama role baru yang akan ditambahkan'
				WebUI.setText(findTestObject('Object Repository/User Management-Role/Page_Add Role/input__roleName'),
					findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('$Add RoleName')))
			}

			'klik pada tombol cari'
			WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/button_Search'))	
			
			'verify nama role'
			checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/User Management-Role/label_Role')), findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 20), false, FailureHandling.CONTINUE_ON_FAILURE), ' Nama Role')
		} else if (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')).equalsIgnoreCase('Edit')) {
			'panggil fungsi cari role'
			searchRole()
			
			'klik pada edit role'
			WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/edit_Rolebutton'))
			
			'panggil fungsi verifikasi sebelum edit'
			beforeEditVerif(conndevUAT)
			
			'input nama role yang akan diubah'
			WebUI.setText(findTestObject('Object Repository/User Management-Role/Page_Edit Role/input__roleName'),
				findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('$Nama Role')))
			
			'input status role yang akan diubah'
			WebUI.setText(findTestObject('Object Repository/User Management-Role/Page_Edit Role/input_status'),
				findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('$Edit Status Role')))
			
			'enter pada status'
			WebUI.sendKeys(findTestObject('Object Repository/User Management-Role/Page_Edit Role/input_status'),
				Keys.chord(Keys.ENTER))
			
			'panggil fungsi cek konfirmasi dialog'
			if (checkdialogConfirmation(isMandatoryComplete) == true) {
				'jika failed mandatory continue testcase'
				continue
			}
			
			'input nama role'
			WebUI.setText(findTestObject('Object Repository/User Management-Role/Page_List Roles/input_Role Name_roleName'),
					findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('$Nama Role')))
		
			'input status role'
			WebUI.setText(findTestObject('Object Repository/User Management-Role/Page_List Roles/input_Status'),
				findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('$Edit Status Role')))
			
			'enter pada status'
			WebUI.sendKeys(findTestObject('Object Repository/User Management-Role/Page_List Roles/input_Status'),
				 Keys.chord(Keys.ENTER))
			
			'klik pada tombol cari'
			WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/button_Search'))
			
			'verify nama role'
			checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/User Management-Role/label_Role')), findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 17), false, FailureHandling.CONTINUE_ON_FAILURE), ' Nama Role')
		} else if (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')).equalsIgnoreCase('Settings')) {
			'panggil fungsi cari role'
			searchRole()
			
			'klik pada setting role'
			WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/setting_Rolebutton'))
			
			'fungsi uncheck service'
			checkorUncheckservices(rowExcel('$MenuChecked'))
			
			'fungsi untuk check services'
			checkorUncheckservices(rowExcel('$MenuUnchecked'))
			
			'klik pada button save services'
			WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Setting Menu/button_Save'))
			
			'jika muncul alert success'
			if (WebUI.getText(findTestObject('Object Repository/User Management-Role/Page_Setting Menu/div_Success')) == 'Success') {
				'klik pada tombol OK'
				WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Setting Menu/button_OK'))
				
				'cek apakah muncul error unknown setelah klik pada tombol ok'
				if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'),
					GlobalVariable.Timeout, FailureHandling.OPTIONAL) == false) {
					GlobalVariable.FlagFailed = 1
					
					'tulis adanya error pada sistem web'
					CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
						GlobalVariable.StatusWarning, (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
							GlobalVariable.FailedReasonUnknown)
				}
			
				'jika mandatory lengkap dan tidak ada error'
				if (isMandatoryComplete == 0 && GlobalVariable.FlagFailed == 0) {
					'write to excel success'
					CustomKeywords.'writetoexcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Status') - 1,
						GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
					
					'cek apakah perlu pengecekan ke db'
					if (GlobalVariable.KondisiCekDB == 'Yes') {
						'panggil fungsi storeDB'
						WebUI.callTestCase(findTestCase('Test Cases/User Management/RoleStoreDB'), [:], FailureHandling.STOP_ON_FAILURE)
					}
				} else {
					'klik pada tombol OK'
					WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Setting Menu/button_OK'))
					
					'tulis adanya mandatory tidak lengkap'
					CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
						GlobalVariable.StatusFailed, (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
							GlobalVariable.FailedReasonMandatory)
					
					'cek apakah muncul error unknown setelah klik pada tombol ok'
					if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'),
							GlobalVariable.Timeout, FailureHandling.OPTIONAL) == false) {
						GlobalVariable.FlagFailed = 1
						
						'tulis adanya error pada sistem web'
						CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
							GlobalVariable.StatusWarning, (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
								GlobalVariable.FailedReasonUnknown)
					}
				}
			} else {
				GlobalVariable.FlagFailed = 1
				
				'tulis adanya error saat setting access menu role'
				CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
						GlobalVariable.FailedReasonMenuRole)
			}
		}
	}
}

WebUI.closeBrowser()

def searchRole() {
	'input nama role'
	WebUI.setText(findTestObject('Object Repository/User Management-Role/Page_List Roles/input_Role Name_roleName'),
			findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('Nama Role')))

	'input status role'
	WebUI.setText(findTestObject('Object Repository/User Management-Role/Page_List Roles/input_Status'),
		findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('Status Role')))
	
	'enter pada status'
	WebUI.sendKeys(findTestObject('Object Repository/User Management-Role/Page_List Roles/input_Status'),
		 Keys.chord(Keys.ENTER))
	
	'klik pada tombol cari'
	WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/button_Search'))
}

def checkorUncheckservices(int row) {
	'get array Services dari excel'
	ArrayList arrayServices = findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, row).split(';', -1)
	
	'looping untuk input services check'
	for (index = 0; index < arrayServices.size(); index++) {
		'modify object checkbox'
		modifyObjectCheckbox = WebUI.modifyObjectProperty(
			findTestObject('Object Repository/User Management-Role/modifyObject'), 'xpath', 'equals',
				 ('//*[@id="' + arrayServices[index] + '"]'), true)
	
		if (row == 22) {
			'check if check box is unchecked'
			if (WebUI.verifyElementNotChecked(modifyObjectCheckbox, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
				'click checkbox'
				WebUI.click(modifyObjectCheckbox)
			}
		} else if (row == 23) {
			'check if check box is checked'
			if (WebUI.verifyElementChecked(modifyObjectCheckbox, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
				'click checkbox'
				WebUI.click(modifyObjectCheckbox)
			}
		}
	}
}

def checkdialogConfirmation(int isMandatoryComplete) {
	boolean shouldcontinue = false
	
	if (WebUI.verifyElementHasAttribute(findTestObject('Object Repository/User Management-Role/Page_Add Role/button_Next'), 
		'disabled', GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		GlobalVariable.FlagFailed = 1
			
		'tulis adanya mandatory tidak lengkap'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
				GlobalVariable.FailedReasonMandatory)
		
		'klik pada tombol batal'
		WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Add Role/button_Cancel'))
		
		shouldcontinue = true
	} else {
		'klik pada tombol lanjut'
		WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Add Role/button_Next'))
		
		'klik tombol ya untuk menambahkan'
		WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Add Role/button_Ya'))
		
		'jika error muncul'
		if (WebUI.getText(findTestObject('Object Repository/User Management-Role/Page_Add Role/KonfirmasiAdd')).contains('Sudah Ada')) {
			'klik tombol OK'
			WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Add Role/button_OK'))
		
			GlobalVariable.FlagFailed = 1
			
			'tulis adanya error pada sistem web'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
					GlobalVariable.FailedReasonExisted)
			
			shouldcontinue = true
		} else {
			'klik tombol OK'
			WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Add Role/button_OK'))
			
			'cek apakah muncul error unknown setelah klik pada tombol ok'
			if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'), GlobalVariable.Timeout, FailureHandling.OPTIONAL) == false) {
				GlobalVariable.FlagFailed = 1
				
				'tulis adanya error pada sistem web'
				CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
					GlobalVariable.StatusWarning, (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
						GlobalVariable.FailedReasonUnknown)
			}
			
			'jika mandatory lengkap dan tidak ada failure'
			if (isMandatoryComplete == 0 && GlobalVariable.FlagFailed == 0) {
				'write to excel success'
				CustomKeywords.'writetoexcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Status') - 1,
					GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
				
				'cek apakah perlu untuk cek ke DB'
				if (GlobalVariable.KondisiCekDB == 'Yes') {
					'panggil fungsi storeDB'
					WebUI.callTestCase(findTestCase('Test Cases/User Management/RoleStoreDB'), [:], FailureHandling.STOP_ON_FAILURE)
				}
			}
		}
	}
	shouldcontinue
}

def beforeEditVerif(Connection connUAT) {
	'siapkan array untuk ambil data role dari UI'
	ArrayList<String> roleUI = []
	
	'siapkan array untuk ambil data role dari DB'
	ArrayList<String> roleDB = CustomKeywords.'usermanagement.RoleVerif.getDataRolebeforeVerif'(connUAT, WebUI.getText(findTestObject('Object Repository/User Management-Role/Page_Edit Role/input__roleName')))
	
	'ambil text pada nama role dan tambahkan ke array role UI'
	roleUI.add(WebUI.getText(findTestObject('Object Repository/User Management-Role/Page_Edit Role/input__roleName')))
	
	'ambil text pada status role dan tambah ke array role UI'
	roleUI.add(WebUI.getText(findTestObject('Object Repository/User Management-Role/Page_Edit Role/StatusEditBefore')))

	for (int i = 0; i < roleDB.size(); i++) {
		'cek apakah tiap isi dari db sesuai'
		if (roleDB[i] != roleUI[i]) {
			'Write To Excel gagal dan bermasalah saat verifikasi data'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) +
					';') + GlobalVariable.FailedReasonEditVerif)

			GlobalVariable.FlagFailed = 1
		}
	}
}

def checkPaging(Connection connUAT) {
	'input nama role'
	WebUI.setText(findTestObject('Object Repository/User Management-Role/Page_List Roles/input_Role Name_roleName'), 
			findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('Nama Role')))

	'input status role'
	WebUI.setText(findTestObject('Object Repository/User Management-Role/Page_List Roles/input_Status'),
		findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('Status Role')))
	
	'enter pada status'
	WebUI.sendKeys(findTestObject('Object Repository/User Management-Role/Page_List Roles/input_Status'),
		 Keys.chord(Keys.ENTER))
	
	'klik pada tombol cari'
	WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/button_Search'))
	
	'klik pada tombol reset'
	WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/button_Reset'))
	
	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/User Management-Role/Page_List Roles/input_Role Name_roleName'),
			'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/User Management-Role/Page_List Roles/input_Status'),
			'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'klik pada tombol cari'
	WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/button_Search'))
	
	'cari button skip di footer'
	elementbutton = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-list-roles > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))
	
	'ambil banyaknya laman footer'
	int lastPage = elementbutton.size()

	'get data total dari tabel role'
	int resultTotalData = CustomKeywords.'usermanagement.RoleVerif.getRoleTotal'(connUAT, 
		findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('Username Login')))
	
	'get text total data dari ui'
	Total = WebUI.getText(findTestObject('Object Repository/User Management-Role/Page_List Roles/TotalData')).split(' ')

	'verify total data role'
	checkVerifyPaging(WebUI.verifyEqual(resultTotalData, Integer.parseInt(Total[0]), FailureHandling.CONTINUE_ON_FAILURE))
	
	'cek apakah hlm 2 tersedia'
	if (WebUI.verifyElementVisible(findTestObject('Object Repository/User Management-Role/Page_List Roles/a_2')) == true) {
		'klik halaman 2'
		WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/a_2'))
		
		'verify paging di page 2'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/User Management-Role/Page_List Roles/Page2'), 'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'klik halaman 1'
		WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/a_1'))
		
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/User Management-Role/Page_List Roles/Page1'), 'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'klik button next page'
		WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/next_page'))
		
		'verify paging di page 2'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/User Management-Role/Page_List Roles/Page2'), 'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'klik prev page'
		WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/prev_page'))
		
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/User Management-Role/Page_List Roles/Page1'), 'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'cek apakah button skip disabled atau enabled'
		if (WebUI.verifyElementVisible(findTestObject('Object Repository/User Management-Role/Page_List Roles/gotoLast_page'), FailureHandling.OPTIONAL)) {
			'klik pada tombol skip'
			WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/gotoLast_page'))
		}
		
		'modify object laman terakhir'
		modifyObjectmaxPage = WebUI.modifyObjectProperty(
			findTestObject('Object Repository/User Management-Role/modifyObject'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-roles/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' + lastPage - 2 + ']', true)
		
		'verify paging di page terakhir'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(modifyObjectmaxPage,
			'class', FailureHandling.CONTINUE_ON_FAILURE), 'pages active ng-star-inserted',
				false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'click min page'
		WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/gotoFirst_page'))
		
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/User Management-Role/Page_List Roles/Page1'), 'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
	}
}

def checkVerifyPaging(Boolean isMatch) {
	if (isMatch == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) +
				';') + GlobalVariable.FailedReasonPagingError)

		GlobalVariable.FlagFailed = 1
	}
}

'fungsi untuk melakukan pengecekan '
def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
	if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
			(findTestData(ExcelPathRole).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') + GlobalVariable.FailedReasonVerifyEqualorMatch + reason)

		GlobalVariable.FlagFailed = 1
	}
}

def rowExcel(String cellValue) {
	CustomKeywords.'writetoexcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}
