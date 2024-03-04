import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

//login sentani > regis user baru > logout -> login pakai user baru daftar ->
//login gagal -> login pakai sentani -> aktifin user resend -> (query) -> logout -> login user baru -> login sukses
'deklarasi koneksi ke Database adins_apiaas_uat'
Connection conndev = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_esign'()

'deklarasi koneksi ke database eendigo_dev_uat'
Connection conndevUAT = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_devUat'()

'click profile'
WebUI.click(findTestObject('User Management-User/user_Profile'))

'click logout'
WebUI.click(findTestObject('User Management-User/button_LogOut'))

'login dengan user yang baru di tambahkan'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_username'),
		findTestData(excelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$Email')))

'input password'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_password'),
		findTestData(excelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$Password')))

'ceklis pada reCaptcha'
WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/check_Recaptcha'))

'pada delay, lakukan captcha secara manual'
WebUI.delay(10)

'klik pada button login'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))

'check if reason failed tidak present'
if (WebUI.verifyElementNotPresent(findTestObject('User Management-User/label_TextError'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
	'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
	CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(excelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') + 
			'Berhasil Login dengan user yang belum aktivasi')
	
	GlobalVariable.FlagFailed = 1
}

'input data email'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_username'),
		findTestData(excelPathUser).getValue(2, rowExcel('Username Login')))

'input password'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_password'),
		findTestData(excelPathUser).getValue(2, rowExcel('Password Login')))

'delay alert'
WebUI.delay(5)

'klik pada button login'
WebUI.click(
	findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))

'klik pada menu'
WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Balance/span_Menu'))

'pilih submenu manage user'
WebUI.click(findTestObject('Object Repository/User Management-Role/Page_Balance/a_Manage User'))

'klik pada user'
WebUI.click(findTestObject('Object Repository/User Management-User/Page_Balance/span_User'))

'cek apakah tombol menu dalam jangkauan web'
if (WebUI.verifyElementVisible(findTestObject('Object Repository/User Management-Role/Page_List Roles/tombolX_menu'), FailureHandling.OPTIONAL)) {
	'klik pada tombol silang menu'
	WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/tombolX_menu'))
}

'input email'
WebUI.setText(findTestObject('Object Repository/User Management-User/Page_List User/input_Email_email'),
		findTestData(excelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$Email')))

'input status user'
inputDDLExact('Object Repository/User Management-User/Page_List User/inputstatus',
	'Belum Verifikasi')

'input peran user'
inputDDLExact('Object Repository/User Management-User/Page_List User/inputrole',
	findTestData(excelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$Role')))

'klik pada tombol cari'
WebUI.click(findTestObject('Object Repository/User Management-User/Page_List User/button_Search'))

'klik pada tombol resend aktivisi'
WebUI.click(findTestObject('User Management-User/button_ResendLink'))

'klik pada tombol ya Proses'
WebUI.click(findTestObject('User Management-User/button_Confirm'))

'check if success'
if (WebUI.getText(findTestObject('User Management-User/label_PopUp')).equalsIgnoreCase('Success')) {
	'klik pada tombol ya Proses'
	WebUI.click(findTestObject('User Management-User/button_Confirm'))
	
	'aktifkan user yang baru saja didaftarkan di db dev'
	CustomKeywords.'usermanagement.UserVerif.updateIsActiveUser'(conndev,
		findTestData(excelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$Email')))

	'aktifkan user yang baru saja didaftarkan di db dev_uat'
	CustomKeywords.'usermanagement.UserVerif.updateIsActiveUser'(conndevUAT,
		findTestData(excelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$Email')))
	
	'click profile'
	WebUI.click(findTestObject('User Management-User/user_Profile'))
	
	'click logout'
	WebUI.click(findTestObject('User Management-User/button_LogOut'))
	
	'login dengan user yang baru di tambahkan'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_username'),
			findTestData(excelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$Email')))
	
	'input password'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_password'),
			findTestData(excelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('$Password')))
	
	'ceklis pada reCaptcha'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/check_Recaptcha'))
	
	'pada delay, lakukan captcha secara manual'
	WebUI.delay(10)
	
	'klik pada button login'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))
	
	'check if berhasil login'
	if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/User Management-Role/' +
	'Page_Balance/i_SEDARA MANYURA_ft-menu font-medium-3'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(excelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
				'gagal login dengan user yang baru terdaftar dan sudah aktivasi')

		GlobalVariable.FlagFailed = 1
	}
	
	'click profile'
	WebUI.click(findTestObject('User Management-User/user_Profile'))
	
	'click logout'
	WebUI.click(findTestObject('User Management-User/button_LogOut'))
	
	'input data email'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_username'),
		findTestData(excelPathUser).getValue(2, rowExcel('Username Login')))

	'input password'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_password'),
		findTestData(excelPathUser).getValue(2, rowExcel('Password Login')))
	
	'ceklis pada reCaptcha'
	WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/check_Recaptcha'))
	
	'pada delay, lakukan captcha secara manual'
	WebUI.delay(10)
	
	'klik pada button login'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))
} else {
	'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
	CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(excelPathUser).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason Failed')) + ';') +
			'gagal mengirimkan ulang email aktivasi')
	
	GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
	CustomKeywords.'writetoexcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def inputDDLExact(String locationObject, String input) {
	'Input value status'
	WebUI.setText(findTestObject(locationObject), input)

	if (input != '') {
		WebUI.click(findTestObject(locationObject))

		'get token unik'
		tokenUnique = WebUI.getAttribute(findTestObject(locationObject), 'aria-owns')

		'modify object label Value'
		modifyObjectGetDDLFromToken = WebUI.modifyObjectProperty(findTestObject('Saldo/Page_Balance/modifybuttonpage'), 'xpath',
			'equals', ('//*[@id="' + tokenUnique) + '"]/div/div[2]', true)

		DDLFromToken = WebUI.getText(modifyObjectGetDDLFromToken)

		for (i = 0; i < DDLFromToken.split('\n', -1).size(); i++) {
			if ((DDLFromToken.split('\n', -1)[i]).toString().toLowerCase() == input.toString().toLowerCase()) {
				modifyObjectClicked = WebUI.modifyObjectProperty(findTestObject('Saldo/Page_Balance/modifybuttonpage'), 'xpath',
					'equals', ((('//*[@id="' + tokenUnique) + '"]/div/div[2]/div[') + (i + 1)) + ']', true)

				WebUI.click(modifyObjectClicked)

				break
			}
		}
	} else {
		WebUI.click(findTestObject(locationObject))

		WebUI.sendKeys(findTestObject(locationObject), Keys.chord(Keys.ENTER))
	}
}
