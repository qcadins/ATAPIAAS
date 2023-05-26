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
int countColumnEdit = findTestData(ExcelPathUser).getColumnNumbers()

'deklarasi koneksi ke Database adins_apiaas_uat'
Connection conndevUAT = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_devUat'()

'panggil fungsi login'
WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'User'], FailureHandling.STOP_ON_FAILURE)

'klik pada bagian admin client'
WebUI.click(findTestObject('Object Repository/User Management-Role/' +
	'Page_Login - eendigo Platform/i_Admin Client_ft-edit'))

'klik pada menu'
WebUI.click(findTestObject('Object Repository/User Management-Role/' +
	'Page_Balance/i_SEDARA MANYURA_ft-menu font-medium-3'))

'pilih submenu manage user'
WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Balance/a_Manage User'))

'klik pada user'
WebUI.click(findTestObject('Object Repository/User Management-User/Page_Balance/span_User'))

'panggil fungsi check paging'
checkPaging(conndevUAT)

for (GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++) {
	
	'set penanda error menjadi 0'
	GlobalVariable.FlagFailed = 0
		
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 1).length() == 0) {
		
		break
	}
	else if (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 1).equalsIgnoreCase('Unexecuted')) {
		
		'declare isMmandatory Complete'
		int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 4))
		
		'klik pada menu'
		WebUI.click(findTestObject('Object Repository/User Management-Role/' +
			'Page_Balance/i_SEDARA MANYURA_ft-menu font-medium-3'))
		
		'klik pada user'
		WebUI.click(findTestObject('Object Repository/User Management-User/Page_Balance/span_User'))
		
		'cek apakah tombol menu dalam jangkauan web'
		if (WebUI.verifyElementVisible(findTestObject(TombolSilang), FailureHandling.OPTIONAL)) {
			
			'klik pada tombol silang menu'
			WebUI.click(findTestObject(TombolSilang))
		}
		
		'check if action new/services/edit/balancechargetype'
		if (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 7).equalsIgnoreCase('New')){
			
			'klik pada tombol New'
			WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/a_New'))
			
			'input email baru yang akan ditambahkan'
			WebUI.setText(findTestObject('Object Repository/User Management-User/Page_Add User/input__email'),
				findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 17))
			
			'input nama depan'
			WebUI.setText(findTestObject('Object Repository/User Management-User/Page_Add User/input__firstName'),
				findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 18))
			
			'input nama belakang'
			WebUI.setText(findTestObject('Object Repository/User Management-User/Page_Add User/input__lastName'),
				findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 19))
			
			'klik pada eyeicon untuk melihat password yang diinput'
			WebUI.click(findTestObject('Object Repository/User Management-User/Page_Add User/eyeicon_1'))
			
			'input pass'
			WebUI.setText(findTestObject('Object Repository/User Management-User/Page_Add User/input__pass'),
				findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 20))
			
			'input konfirmasi pass'
			WebUI.setText(findTestObject('Object Repository/User Management-User/Page_Add User/input__confirmpass'),
				findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 20))
			
			'klik pada eyeicon untuk melihat password yang diinput'
			WebUI.click(findTestObject('Object Repository/User Management-User/Page_Add User/eyeicon_1'))
			
			'input role'
			WebUI.setText(findTestObject('Object Repository/User Management-User/Page_Add User/inputadduser'),
				findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 21))
			
			'klik enter pada role'
			WebUI.sendKeys(findTestObject('Object Repository/User Management-User/Page_Add User/inputadduser'),
				Keys.chord(Keys.ENTER))
			
			'klik pada tombol save'
			WebUI.click(findTestObject('Object Repository/User Management-User/Page_Add User/button_Save'))
			
			'klik pada tombol Ya untuk lanjutkan'
			WebUI.click(findTestObject('Object Repository/User Management-User/Page_Add User/button_Ya'))
			
			'jika error muncul'
			if (WebUI.getText(findTestObject('Object Repository/User Management-User/'+
				'Page_Add User/div_Success')).contains('Success')) {
			
				'klik tombol OK'
				WebUI.click(findTestObject('Object Repository/User Management-User/Page_Add User/button_OK'))
			
				'jika mandatory lengkap dan tidak ada failure'
				if (isMandatoryComplete == 0 && GlobalVariable.FlagFailed == 0) {
					
					'write to excel success'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'User', 0,
						GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
				}
				else {
					
					'tulis adanya mandatory tidak lengkap'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('User', GlobalVariable.NumOfColumn,
						GlobalVariable.StatusFailed, (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
							GlobalVariable.FailedReasonMandatory)
					
					continue;
				}
				
//				'cek apakah perlu untuk cek ke DB'
//				if(GlobalVariable.KondisiCekDB == 'Yes') {
//					
//					'panggil fungsi storeDB'
//					WebUI.callTestCase(findTestCase('Test Cases/User Management/RoleStoreDB'), [:], FailureHandling.STOP_ON_FAILURE)
//	
//				}
			}
			else {
				
				'klik tombol OK'
				WebUI.click(findTestObject('Object Repository/User Management-User/Page_Add User/button_OK'))
				
				'tulis adanya error pada sistem web'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('User', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
						GlobalVariable.FailedReasonExisted)
			}
		}
		else if (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 7).equalsIgnoreCase('Edit')) {
			
			'panggil fungsi cari role'
			searchRole()
			
			'klik pada edit role'
			WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/Edit'))
			
			'input nama depan yang akan diubah'
			WebUI.setText(findTestObject('Object Repository/User Management-User/Page_Edit User/input__firstName'),
				findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 23))
			
			'input nama belakang user yang akan diubah'
			WebUI.setText(findTestObject('Object Repository/User Management-User/Page_Edit User/input__lastName'),
				findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 24))
			
			'input role user yang akan diubah'
			WebUI.setText(findTestObject('Object Repository/User Management-User/Page_Edit User/inputuseredit'),
				findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 25))
			
			'enter pada status'
			WebUI.sendKeys(findTestObject('Object Repository/User Management-User/Page_Edit User/inputuseredit'),
				Keys.chord(Keys.ENTER))
			
			'input role user yang akan diubah'
			WebUI.setText(findTestObject('Object Repository/User Management-User/Page_Edit User/inputstatusUser'),
				findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 26))
			
			'enter pada status'
			WebUI.sendKeys(findTestObject('Object Repository/User Management-User/Page_Edit User/inputstatusUser'),
				Keys.chord(Keys.ENTER))
			
			'klik tombol next untuk melanjutkan proses edit'
			WebUI.click(findTestObject('Object Repository/User Management-User/Page_Edit User/button_Next'))
			
			'klik tombol YA untuk konfirmasi perubahan'
			WebUI.click(findTestObject('Object Repository/User Management-User/Page_Edit User/button_Ya'))
			
			'ambil string dari alert setelah edit'
			String editCondition = WebUI.getText(findTestObject('Object Repository/User Management-User/'+
				'Page_Edit User/div_Success'))
			
			'jika muncul error setelah edit'
			if (editCondition.contains('Success')) {
			
				'klik pada tombol ok'
				WebUI.click(findTestObject('Object Repository/User Management-User/Page_Edit User/button_OK'))
				
				'jika mandatory lengkap dan tidak ada failure'
				if (isMandatoryComplete == 0 && GlobalVariable.FlagFailed == 0) {
					
					'write to excel success'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'User', 0,
						GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
				}
				else {
					
					'tulis adanya mandatory tidak lengkap'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('User', GlobalVariable.NumOfColumn,
						GlobalVariable.StatusFailed, (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
							GlobalVariable.FailedReasonMandatory)
					
					continue;
				}
//				if(GlobalVariable.KondisiCekDB == 'Yes') {
//					
//					'panggil fungsi storeDB'
//					WebUI.callTestCase(findTestCase('Test Cases/User Management/RoleStoreDB'), [:], FailureHandling.STOP_ON_FAILURE)
//				}
			}
			else {
				
				'klik pada tombol OK'
				WebUI.click(findTestObject('Object Repository/User Management-User/Page_Edit User/button_OK'))
				
				'tulis adanya error saat edit'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('User', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
						editCondition)
			}
		}
		'cek apakah perlu resend verif email'
		if(findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 28).equalsIgnoreCase('Yes')) {
			
			'input email'
			WebUI.setText(findTestObject('Object Repository/User Management-User/Page_List User/input_Email_email'),
					findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 13))
			
			'input status user'
			WebUI.setText(findTestObject('Object Repository/User Management-User/Page_List User/inputstatus'),
				findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 14))
			
			'enter pada status'
			WebUI.sendKeys(findTestObject('Object Repository/User Management-User/Page_List User/inputstatus'),
				 Keys.chord(Keys.ENTER))
			
			'klik tombol cari'
			WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/button_Search'))
			
			'cek apakah button resend muncul'
			if(WebUI.verifyElementVisible(findTestObject('Object Repository/User Management-User/'+
				'Page_List User/ResendVerifEmail'), FailureHandling.OPTIONAL)) {
			
				'klik pada resend verif'
				WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/ResendVerifEmail'))
			}
			else{
				
				'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('User', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 2) +
						';') + GlobalVariable.FailedReasonButtonResend)
			}
		}
	}
}

WebUI.closeBrowser()

def searchRole() {
	'input email'
	WebUI.setText(findTestObject('Object Repository/User Management-User/Page_List User/input_Email_email'),
			findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 13))
	
	'input status user'
	WebUI.setText(findTestObject('Object Repository/User Management-User/Page_List User/inputstatus'),
		findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 14))
	
	'enter pada status'
	WebUI.sendKeys(findTestObject('Object Repository/User Management-User/Page_List User/inputstatus'),
		 Keys.chord(Keys.ENTER))
	
	'input role'
	WebUI.setText(findTestObject('Object Repository/User Management-User/Page_List User/inputrole'),
		findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 15))
	
	'enter pada role'
	WebUI.sendKeys(findTestObject('Object Repository/User Management-User/Page_List User/inputrole'),
		 Keys.chord(Keys.ENTER))
	
	'klik pada tombol cari'
	WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/button_Search'))
}

def checkPaging(Connection connUAT) {
	
	'input nama email'
	WebUI.setText(findTestObject('Object Repository/User Management-User/Page_List User/input_Email_email'),
			findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 13))
	
	'input status user'
	WebUI.setText(findTestObject('Object Repository/User Management-User/Page_List User/inputstatus'),
		findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 14))
	
	'enter pada status'
	WebUI.sendKeys(findTestObject('Object Repository/User Management-User/Page_List User/inputstatus'),
		Keys.chord(Keys.ENTER))
	
	'input role dari user'
	WebUI.setText(findTestObject('Object Repository/User Management-User/Page_List User/inputrole'),
		findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 15))
	
	'klik enter pada role'
	WebUI.sendKeys(findTestObject('Object Repository/User Management-User/Page_List User/inputrole'),
		Keys.chord(Keys.ENTER))
	
	'klik pada tombol cari'
	WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/button_Search'))
	
	'klik pada tombol reset'
	WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/button_Reset'))
	
	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management-User/'+
		'Page_List User/input_Email_email'),
			'value', FailureHandling.CONTINUE_ON_FAILURE),'', false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management-User/'+
		'Page_List User/inputstatus'),
			'value', FailureHandling.CONTINUE_ON_FAILURE),'', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management-User/'+
		'Page_List User/inputrole'),
			'value', FailureHandling.CONTINUE_ON_FAILURE),'', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'klik pada tombol cari'
	WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/button_Search'))
	
	'cari button skip di footer'
	def elementbutton = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout >'+
		' div > div.main-panel > div > div.content-wrapper > app-list-user > app-msx-paging > app-msx-datatable >'+
		' section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))
	
	'ambil banyaknya laman footer'
	int lastPage = elementbutton.size()

	'get data total dari tabel role'
	int resultTotalData = CustomKeywords.'userManagement.RoleVerif.getRoleTotal'(connUAT,
		findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 10))
	
	'get text total data dari ui'
	Total = WebUI.getText(findTestObject('Object Repository/User Management-User/TotalData')).split(' ')

//	'verify total data role'
//	checkVerifyPaging(WebUI.verifyEqual(resultTotalData, Integer.parseInt(Total[0]), FailureHandling.CONTINUE_ON_FAILURE))
	
	'cek apakah hlm  tersedia'
	if(WebUI.verifyElementVisible(findTestObject('Object Repository/User Management-User/'+
		'Page_List User/i_Action_datatable-icon-skip'),FailureHandling.OPTIONAL) == true){
		
		'klik halaman 2'
		WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/Page2'))
		
		'verify paging di page 2'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management-User/'+
			'Page_List User/Page2'),'class', FailureHandling.CONTINUE_ON_FAILURE),
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
		
		'klik pada tombol skip'
		WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/i_Action_datatable-icon-skip'))
		
		'modify object laman terakhir'
		def modifyObjectmaxPage = WebUI.modifyObjectProperty(
			findTestObject('Object Repository/User Management-Role/modifyObject'),
			'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-user/app-msx-paging/"+
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
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('User', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, 2) +
				';') + GlobalVariable.FailedReasonPagingError)

		GlobalVariable.FlagFailed = 1
	}
}

