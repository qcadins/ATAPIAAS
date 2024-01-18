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

'mendapat jumlah kolom dari sheet User'
int countColumnEdit = findTestData(ExcelPathUser).columnNumbers

'deklarasi koneksi ke Database adins_apiaas_uat'
Connection conndev = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_esign'()

//'deklarasi koneksi ke database eendigo_dev_uat'
//Connection conndevUAT = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_devUat'()

'panggil fungsi login'
WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : sheet, ('SheetName') : sheet, 
	('Path') : ExcelPathUser], FailureHandling.STOP_ON_FAILURE)

'klik pada menu'
WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Balance/span_Menu'))

'pilih submenu manage user'
WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Balance/a_Manage User'))

'klik pada user'
WebUI.click(findTestObject('Object Repository/User Management-User/Page_Balance/span_User'))

'panggil fungsi check paging'
checkPaging(conndev)

for (GlobalVariable.NumOfColumn = 2; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++) {
	'set penanda error menjadi 0'
	GlobalVariable.FlagFailed = 0
		
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).length() == 0) {
		break
	} else if (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).equalsIgnoreCase('Unexecuted') ||
		findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).equalsIgnoreCase('Warning')) {
		'declare isMmandatory Complete'
		int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Is Mandatory Complete')))
		
		'klik pada menu'
		WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Balance/span_Menu'))
		
		if (WebUI.verifyElementNotVisible(findTestObject('Object Repository/User Management-User/Page_Balance/span_User'), FailureHandling.OPTIONAL)) {
			'pilih submenu manage user'
			WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Balance/a_Manage User'))
		}
		
		'klik pada user'
		WebUI.click(findTestObject('Object Repository/User Management-User/Page_Balance/span_User'))
		
		'cek apakah tombol menu dalam jangkauan web'
		if (WebUI.verifyElementVisible(findTestObject('Object Repository/User Management-Role/Page_List Roles/tombolX_menu'), FailureHandling.OPTIONAL)) {
			'klik pada tombol silang menu'
			WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/tombolX_menu'))
		}
		
		'check if action new/edit'
		if (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')).equalsIgnoreCase('New')) {
			'klik pada tombol New'
			WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/a_New'))
			
			'inputcancel check untuk awal testcase'
			inputCancelCheck()
			
			'panggil fungsi menambahkan user'
			inputNewUser()
			
			'panggil fungsi cek dialog'
			checkdialogConfirmation(isMandatoryComplete)
		} else if (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')).equalsIgnoreCase('Edit')) {
			'panggil fungsi cari user'
			searchUser()
			
			'klik pada edit user'
			WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/Edit'))
			
			checkDBbeforedit(conndev)
			
			'ambil status dari user yang dipilih'
			String statususer = CustomKeywords.'usermanagement.UserVerif.getUserStatus'(conndev, 
				findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Email')))
			
			'input nama depan yang akan diubah'
			WebUI.setText(findTestObject('Object Repository/User Management-User/Page_Edit User/input__firstName'),
				findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$Firstname')))
			
			'input nama belakang user yang akan diubah'
			WebUI.setText(findTestObject('Object Repository/User Management-User/Page_Edit User/input__lastName'),
				findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$LastName')))
			
			'input peran user yang akan diubah'
			WebUI.setText(findTestObject('Object Repository/User Management-User/Page_Edit User/inputuseredit'),
				findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$Role')))
			
			'enter pada peran user'
			WebUI.sendKeys(findTestObject('Object Repository/User Management-User/Page_Edit User/inputuseredit'),
				Keys.chord(Keys.ENTER))
	
			'jika status dari user masih unverified, belum bisa ubah status aktivasi'
			if (statususer == 'Belum verifikasi') {
				if (WebUI.verifyElementNotHasAttribute(findTestObject('Object Repository/User Management-User/Page_Edit User/inputstatusUser'), 'readonly',
						GlobalVariable.Timeout, FailureHandling.CONTINUE_ON_FAILURE)) {
					'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
					CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
						GlobalVariable.StatusFailed, (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) +
							';') + GlobalVariable.FailedReasonStatusNotMatch)
			
					GlobalVariable.FlagFailed = 1
				}
				
			} else {
				'klik pada ddl'
				WebUI.click(findTestObject('Object Repository/User Management-User/Page_Edit User/inputstatusUser'))
				
				'cek kondisi status input pada database'
				if (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$Status Aktivasi')) == 'Aktif') {
					'pilih status active'
					WebUI.click(findTestObject('Object Repository/User Management-User/Page_Edit User/Statusactive'))
				} else {
					'pilih status inactive'
					WebUI.click(findTestObject('Object Repository/User Management-User/Page_Edit User/Statusinactive'))
				}
			}
			
			'panggil fungsi lengkapi konfirmasi dialog'
			checkdialogConfirmation(isMandatoryComplete)
		}
		'cek apakah perlu resend verif email'
		if (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('ShouldResendVerif?')).equalsIgnoreCase('Yes')) {
			'input email'
			WebUI.setText(findTestObject('Object Repository/User Management-User/Page_List User/input_Email_email'),
					findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Email')))
			
			'input status user'
			WebUI.setText(findTestObject('Object Repository/User Management-User/Page_List User/inputstatus'),
				findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$Status')))
			
			'enter pada status'
			WebUI.sendKeys(findTestObject('Object Repository/User Management-User/Page_List User/inputstatus'),
				 Keys.chord(Keys.ENTER))
			
			'klik tombol cari'
			WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/button_Search'))
			
			'klik pada resend verif'
			WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/ResendVerifEmail'))
			
			'cek apakah button resend muncul'
			if (WebUI.getText(findTestObject('Object Repository/User Management-User/Page_List User/msgResend'))
				!= 'Tenant tidak ditemukan') {
				'klik pada tombol OK'
				WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/button_OK'))
				
				'write to excel success'
				CustomKeywords.'writetoexcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0,
					GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
			} else {
				'klik pada tombol OK'
				WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/button_OK'))
				
				'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
				CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) +
						';') + GlobalVariable.FailedReasonButtonResend)
			}
		}
	}
}

WebUI.closeBrowser()

def searchUser() {
	'input email'
	WebUI.setText(findTestObject('Object Repository/User Management-User/Page_List User/input_Email_email'),
			findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Email')))
	
	'input status user'
	WebUI.setText(findTestObject('Object Repository/User Management-User/Page_List User/inputstatus'),
		findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$Status')))
	
	'enter pada status'
	WebUI.sendKeys(findTestObject('Object Repository/User Management-User/Page_List User/inputstatus'),
		 Keys.chord(Keys.ENTER))
	
	'input peran user'
	WebUI.setText(findTestObject('Object Repository/User Management-User/Page_List User/inputrole'),
		findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Role')))
	
	'enter pada peran user'
	WebUI.sendKeys(findTestObject('Object Repository/User Management-User/Page_List User/inputrole'),
		 Keys.chord(Keys.ENTER))
	
	'klik pada tombol cari'
	WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/button_Search'))
}

def checkdialogConfirmation(int isMandatoryComplete) {
	if (WebUI.verifyElementHasAttribute(
		findTestObject('Object Repository/User Management-User/Page_Edit User/button_Next'), 'disabled', 
			GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		if (isMandatoryComplete != 0) {
			'tulis adanya mandatory tidak lengkap'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
					GlobalVariable.FailedReasonMandatory)
		} else {
			'error karena input yang salah'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
					GlobalVariable.FailedReasonSubmitError)
		}
	} else {
		'klik tombol next untuk melanjutkan proses edit'
		WebUI.click(findTestObject('Object Repository/User Management-User/Page_Edit User/button_Next'))
		
		'klik tombol YA untuk konfirmasi perubahan'
		WebUI.click(findTestObject('Object Repository/User Management-User/Page_Edit User/button_Ya'))
		
		'ambil string dari alert setelah edit'
		String editCondition = WebUI.getText(
			findTestObject('Object Repository/User Management-User/Page_Edit User/div_Success'))
		
		'jika muncul error setelah edit'
		if (editCondition.contains('Success')) {
			'klik pada tombol ok'
			WebUI.click(findTestObject('Object Repository/User Management-User/Page_Edit User/button_OK'))
			
			'cek apakah muncul error unknown setelah klik pada tombol ok'
			if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'),
					GlobalVariable.Timeout, FailureHandling.OPTIONAL) == false) {
				GlobalVariable.FlagFailed = 1
				
				'tulis adanya error pada sistem web'
				CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
					GlobalVariable.StatusWarning, (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
						GlobalVariable.FailedReasonUnknown)
			}
			
			'jika mandatory lengkap dan tidak ada failure'
			if (isMandatoryComplete == 0 && GlobalVariable.FlagFailed == 0) {
				'write to excel success'
				CustomKeywords.'writetoexcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Status') - 1,
					GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
				
				'cek apakah muncul error unknown setelah klik pada tombol ok'
				if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'),
						GlobalVariable.Timeout, FailureHandling.OPTIONAL) == false) {
					GlobalVariable.FlagFailed = 1
					
					'tulis adanya error pada sistem web'
					CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
						GlobalVariable.StatusWarning, (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
							GlobalVariable.FailedReasonUnknown)
				}
				
				'jika perlu cek status ke DB'
				if (GlobalVariable.KondisiCekDB == 'Yes') {
					'panggil fungsi storeDB'
					WebUI.callTestCase(findTestCase('Test Cases/User Management/UserStoreDB'), [('Path') : ExcelPathUser],
						 FailureHandling.CONTINUE_ON_FAILURE)
				}
				
				if (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')).equalsIgnoreCase('New')) {
					'input email'
					WebUI.setText(findTestObject('Object Repository/User Management-User/Page_List User/input_Email_email'),
							findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$Email')))
					
				} else if (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')).equalsIgnoreCase('Edit')) {
					'input email'
					WebUI.setText(findTestObject('Object Repository/User Management-User/Page_List User/input_Email_email'),
							findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Email')))
				}
				
				'klik pada tombol cari'
				WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/button_Search'))
				
				if (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')).equalsIgnoreCase('New')) {
					'verify nama depan'
					checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/User Management-User/label_NamaDepan')), findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$FirstName')), false, FailureHandling.CONTINUE_ON_FAILURE), ' nama depan')
					
					'verify email'
					checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/User Management-User/label_Email')), findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$Email')), false, FailureHandling.CONTINUE_ON_FAILURE), ' Email')
					
					'verify peran'
					checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/User Management-User/label_Peran')), findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$Role')), false, FailureHandling.CONTINUE_ON_FAILURE), ' peran')
				} else if (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')).equalsIgnoreCase('Edit')) {
					'verify nama depan'
					checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/User Management-User/label_NamaDepan')), findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$Firstname')), false, FailureHandling.CONTINUE_ON_FAILURE), ' nama depan')
					
					'verify email'
					checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/User Management-User/label_Email')), findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Email')), false, FailureHandling.CONTINUE_ON_FAILURE), ' Email')
					
					'verify peran'
					checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/User Management-User/label_Peran')), findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$Role')), false, FailureHandling.CONTINUE_ON_FAILURE), ' peran')
				}
				
				if (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Action')).equalsIgnoreCase('New')) {
					'call testcase user verif'
					WebUI.callTestCase(findTestCase('User Management/UserVerification'), [('excelPathUser') : 'User Management/DataTestingUser'],
						FailureHandling.CONTINUE_ON_FAILURE)
				}
			}
		} else {
			'klik pada tombol OK'
			WebUI.click(findTestObject('Object Repository/User Management-User/Page_Edit User/button_OK'))
			
			'cek apakah muncul error unknown setelah klik pada tombol ok'
			if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'),
					GlobalVariable.Timeout, FailureHandling.OPTIONAL) == false) {
				GlobalVariable.FlagFailed = 1
				
				'tulis adanya error pada sistem web'
				CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
					GlobalVariable.StatusWarning, (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
						GlobalVariable.FailedReasonUnknown)
			}
			
			'tulis adanya error saat edit'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
					'<' + editCondition + '>')
		}
	}	
}

def inputNewUser() {
	'input email baru yang akan ditambahkan'
	WebUI.setText(findTestObject('Object Repository/User Management-User/Page_Add User/input__email'),
		findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$Email')))
	
	'input nama depan'
	WebUI.setText(findTestObject('Object Repository/User Management-User/Page_Add User/input__firstName'),
		findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$FirstName')))
	
	'input nama belakang'
	WebUI.setText(findTestObject('Object Repository/User Management-User/Page_Add User/input__lastName'),
		findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$LastName')))
	
	'input pass'
	WebUI.setText(findTestObject('Object Repository/User Management-User/Page_Add User/input__pass'),
		findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$Password')))
	
	'input konfirmasi pass'
	WebUI.setText(findTestObject('Object Repository/User Management-User/Page_Add User/input__confirmpass'),
		findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$PassConfirm')))
	
	'klik pada eyeicon untuk melihat password yang diinput'
	WebUI.click(findTestObject('Object Repository/User Management-User/Page_Add User/eyeicon_1'))
	
	'ambil string pass'
	String textpass = WebUI.getAttribute(
		findTestObject('Object Repository/User Management-User/Page_Add User/input__pass'), 'value')
	
	'ambil string konfirmasi pass'
	String textconf = WebUI.getAttribute(
		findTestObject('Object Repository/User Management-User/Page_Add User/input__confirmpass'), 'value')
	
	'cek apakah pass yang diinput sudah sesuai dengan excel'
	if (textpass != findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$Password')) ||
			textconf != findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$PassConfirm'))) {
		GlobalVariable.FlagFailed = 1
				
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) +
				';') + GlobalVariable.FailedReasonPassNotMatch)
	}
	
	'klik pada eyeicon untuk melihat password yang diinput'
	WebUI.click(findTestObject('Object Repository/User Management-User/Page_Add User/eyeicon_1'))
	
	'input peran'
	WebUI.setText(findTestObject('Object Repository/User Management-User/Page_Add User/inputadduser'),
		findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$Role')))
	
	'klik enter pada peran'
	WebUI.sendKeys(findTestObject('Object Repository/User Management-User/Page_Add User/inputadduser'),
		Keys.chord(Keys.ENTER))
}

def inputCancelCheck() {
	'loop untuk pengecekan awal testcase'
	if (GlobalVariable.NumOfColumn == 2) {
		inputNewUser()
		
		'klik pada tombol batal'
		WebUI.click(findTestObject('Object Repository/User Management-User/Page_Add User/button_Batal'))
		
		'klik pada tombol New'
		WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/a_New'))
		
		'verify element field kosong'
		checkVerifyEqualOrMatch(WebUI.verifyElementAttributeValue(findTestObject('Object Repository/User Management-User/Page_Add User/input__email'),
			'class', 'form-control ng-untouched ng-pristine ng-invalid',
			GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Field input email tidak kosong')
		
		'verify element field kosong'
		checkVerifyEqualOrMatch(WebUI.verifyElementAttributeValue(findTestObject('Object Repository/User Management-User/Page_Add User/input__firstName'),
			'class', 'form-control mb-2 ng-untouched ng-pristine ng-invalid',
			GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Field nama depan tidak kosong')
		
		'verify element field kosong'
		checkVerifyEqualOrMatch(WebUI.verifyElementAttributeValue(findTestObject('Object Repository/User Management-User/Page_Add User/input__lastName'),
			'class', 'form-control mb-2 ng-untouched ng-pristine ng-invalid',
			GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Field nama belakang tidak kosong')
		
		'verify element field kosong'
		checkVerifyEqualOrMatch(WebUI.verifyElementAttributeValue(findTestObject('Object Repository/User Management-User/Page_Add User/input__pass'),
			'class', 'form-control ng-untouched ng-pristine ng-invalid',
			GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Field password tidak kosong')

		'verify element field kosong'
		checkVerifyEqualOrMatch(WebUI.verifyElementAttributeValue(findTestObject('Object Repository/User Management-User/Page_Add User/input__confirmpass'),
			'class', 'form-control ng-untouched ng-pristine ng-invalid',
			GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Field konfirmasi pass tidak kosong')
		
		'verify element field kosong'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/User Management-User/Page_Add User/inputadduser'),
			'value', FailureHandling.OPTIONAL), '',
				false, FailureHandling.OPTIONAL), 'Field role tidak kosong')
	}
}

def checkPaging(Connection conndev) {
	'input nama email'
	WebUI.setText(findTestObject('Object Repository/User Management-User/Page_List User/input_Email_email'),
		findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Email')))
	
	'input status user'
	WebUI.setText(findTestObject('Object Repository/User Management-User/Page_List User/inputstatus'),
		findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$Status')))
	
	'enter pada status'
	WebUI.sendKeys(findTestObject('Object Repository/User Management-User/Page_List User/inputstatus'),
		Keys.chord(Keys.ENTER))
	
	'input peran dari user'
	WebUI.setText(findTestObject('Object Repository/User Management-User/Page_List User/inputrole'),
		findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Role')))
	
	'klik enter pada peran'
	WebUI.sendKeys(findTestObject('Object Repository/User Management-User/Page_List User/inputrole'),
		Keys.chord(Keys.ENTER))
	
	'klik pada tombol cari'
	WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/button_Search'))
	
	'klik pada tombol reset'
	WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/button_Reset'))
	
	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/User Management-User/Page_List User/input_Email_email'),
			'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/User Management-User/Page_List User/inputstatus'),
			'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/User Management-User/Page_List User/inputrole'),
			'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'klik pada tombol cari'
	WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/button_Search'))
	
	'cari button skip di footer'
	elementbutton = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-list-user > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))
	
	'ambil banyaknya laman footer'
	int lastPage = elementbutton.size()

//	'get data total dari tabel user'
//	int resultTotalData = CustomKeywords.'usermanagement.RoleVerif.getRoleTotal'(conndev,
//		findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Username Login')))
	
	'get text total data dari ui'
	Total = WebUI.getText(findTestObject('Object Repository/User Management-User/TotalData')).split(' ')

	'cek apakah hlm  tersedia'
	if (WebUI.verifyElementVisible(
		findTestObject('Object Repository/User Management-User/Page_List User/gotoLast_page'),
			FailureHandling.OPTIONAL) == true) {
		'klik halaman 2'
		WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/Page2'))
		
		'verify paging di page 2'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/User Management-User/Page_List User/Page2'),
			'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'klik halaman 1'
		WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/a_1'))
		
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management-User/Page_List User/Page1'),
			'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'klik button next page'
		WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/next_page'))
		
		'verify paging di page 2'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management-User/Page_List User/Page2'),
			'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'klik prev page'
		WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/prev_page'))
		
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management-User/Page_List User/Page1'),'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'klik pada tombol skip'
		WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/gotoLast_page'))
		
		'modify object laman terakhir'
		modifyObjectmaxPage = WebUI.modifyObjectProperty(
			findTestObject('Object Repository/User Management-Role/modifyObject'),
			'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-user/app-msx-paging/" +
			"app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[" + lastPage - 2 + "]", true)
		
		'verify paging di page terakhir'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(modifyObjectmaxPage,
			'class', FailureHandling.CONTINUE_ON_FAILURE), 'pages active ng-star-inserted',
				false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'click min page'
		WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/gotoFirst_page'))
		
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management-User/Page_List User/Page1'),'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
	}
}

def checkDBbeforedit(Connection conndev) {
	'buat array untuk ambil data dari UI'
	ArrayList<String> editUI = []
	
	'buat array untuk ambil data dari UI'
	ArrayList<String> editDB = CustomKeywords.'usermanagement.UserVerif.getEditUserData'(conndev, 
		findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Email')))
	
	'tambahkan data ke array editUI'
	editUI.add(WebUI.getAttribute(findTestObject('Object Repository/User Management-User/Page_Edit User/EmailPrefill'), 'value'))
	
	'tambahkan data ke array editUI'
	editUI.add(WebUI.getAttribute(findTestObject('Object Repository/User Management-User/Page_Edit User/input__firstName'), 'value'))
	
	'tambahkan data ke array editUI'
	editUI.add(WebUI.getAttribute(findTestObject('Object Repository/User Management-User/Page_Edit User/input__lastName'), 'value'))
	
	'tambahkan data ke array editUI'
	editUI.add(WebUI.getText(findTestObject('Object Repository/User Management-User/Page_Edit User/roleuser')))
	
	'tambahkan data ke array editUI'
	editUI.add(WebUI.getText(findTestObject('Object Repository/User Management-User/Page_Edit User/Statuschecking')))
	
	'loop untuk verify data equal'
	for (int i = 0; i < editDB.size(); i++) {
		'jika ada data yang tidak sesuai tulis error'
		if (editUI[i] != editDB[i]) {
			GlobalVariable.FlagFailed = 1
			
			'tulis adanya error pada sistem web'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
					GlobalVariable.FailedReasonEditVerif)
		}
	}
}

def checkVerifyPaging(Boolean isMatch) {
	if (isMatch == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) +
				';') + GlobalVariable.FailedReasonPagingError)

		GlobalVariable.FlagFailed = 1
	}
}

'fungsi untuk melakukan pengecekan '
def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
	if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
			(findTestData(ExcelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') + GlobalVariable.FailedReasonVerifyEqualorMatch + reason)

		GlobalVariable.FlagFailed = 1
	}
}

def rowExcel(String cellValue) {
	CustomKeywords.'writetoexcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}
