import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.main.CustomKeywordDelegatingMetaClass as CustomKeywordDelegatingMetaClass
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import com.kms.katalon.entity.global.GlobalVariableEntity as GlobalVariableEntity
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import groovy.sql.Sql as Sql
import org.openqa.selenium.By as By
import org.openqa.selenium.support.ui.Select as Select
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.writeExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Edit Profile'
int CountColumnEdit = findTestData(ExcelPathEditProfile).getColumnNumbers()

'memanggil fungsi untuk login'
WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'EditProf'], FailureHandling.STOP_ON_FAILURE)

'pada jeda waktu ini, isi captcha secara manual, automation testing dianggap sebagai robot oleh google'
WebUI.delay(10)

'focus pada button login'
WebUI.focus(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))

'Klik Login'
WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))

'looping kolom dari testdata'
for (GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn <= CountColumnEdit; (GlobalVariable.NumOfColumn)++) 
{
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 1).length() == 0)
	{
		break
	}
	else if (!findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 1).equalsIgnoreCase('Unexecuted'))
	{
		continue
	}
	
	int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 4))

    'klik garis tiga di kanan atas web'
    WebUI.click(findTestObject('Object Repository/Profile/Page_Balance/i_LINA_ft-chevron-down'))

    'klik profil saya'
    WebUI.click(findTestObject('Object Repository/Profile/Page_Balance/a_Profil Saya'))

    'klik tombol edit profile'
    WebUI.click(findTestObject('Object Repository/Profile/Page_My Profile/button_Edit Profile'))

    WebUI.delay(GlobalVariable.Timeout)

    'panggil fungsi verifikasi jika checkdatabase = yes'
    if (GlobalVariable.KondisiCekDB == 'Yes') {
        'verifikasi data yang ada di web dengan di database sebelum diEdit'
        WebUI.callTestCase(findTestCase('Test Cases/Profile/VerifyDataEditProfile'), [:], FailureHandling.CONTINUE_ON_FAILURE)
    }
    
    'input nama depan pengguna'
    WebUI.setText(findTestObject('Object Repository/Profile/Page_Edit Profile/input__firstName'), findTestData(ExcelPathEditProfile).getValue(
            GlobalVariable.NumOfColumn, 10))

    'klik pada field nama belakang'
    WebUI.click(findTestObject('Object Repository/Profile/Page_Edit Profile/input__lastName'))

    'input data nama belakang'
    WebUI.setText(findTestObject('Object Repository/Profile/Page_Edit Profile/input__lastName'), findTestData(ExcelPathEditProfile).getValue(
            GlobalVariable.NumOfColumn, 11))

    'input data nama perusahaan'
    WebUI.setText(findTestObject('Object Repository/Profile/Page_Edit Profile/input__tenantName'), findTestData(ExcelPathEditProfile).getValue(
            GlobalVariable.NumOfColumn, 12))

    'input data industri'
    WebUI.setText(findTestObject('Object Repository/Profile/Page_Edit Profile/input__industry'), findTestData(ExcelPathEditProfile).getValue(
            GlobalVariable.NumOfColumn, 13))

    'pilih jenis kelamin'
    if (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 14) == 'M') {
        WebUI.check(findTestObject('Object Repository/Profile/Page_Edit Profile/input__ng-untouched ng-pristine ng-valid'))
    } else {
        WebUI.check(findTestObject('Object Repository/Profile/Page_Edit Profile/input_Pria_ng-untouched ng-pristine ng-valid'))
    }
    
    'input field website'
    WebUI.setText(findTestObject('Object Repository/Profile/Page_Edit Profile/input__website'), findTestData(ExcelPathEditProfile).getValue(
            GlobalVariable.NumOfColumn, 15))

    'input data field nomor telepon'
    WebUI.setText(findTestObject('Object Repository/Profile/Page_Edit Profile/input_Wanita_phoneNumber'), findTestData(ExcelPathEditProfile).getValue(
            GlobalVariable.NumOfColumn, 16))

    'input data field position'
    WebUI.setText(findTestObject('Object Repository/Profile/Page_Edit Profile/input__position'), findTestData(ExcelPathEditProfile).getValue(
            GlobalVariable.NumOfColumn, 17))

    'pilih dari dropdownlist +62 Indonesia'
    WebUI.selectOptionByLabel(findTestObject('Object Repository/Profile/Page_Edit Profile/select_Afghanistan 93Albania 355Algeria 213_ddb156'), 
        findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 18), false)
	
	'klik tombol simpan'
	WebUI.click(findTestObject('Object Repository/Profile/Page_Edit Profile/button_Simpan'))
	
	WebUI.delay(3)

    'panggil fungsi verifikasi jika checkdatabase = yes'
    if (GlobalVariable.KondisiCekDB == 'Yes') {
        'verifikasi data yang ada di excel dengan di database sesudah diEdit'
        WebUI.callTestCase(findTestCase('Test Cases/Profile/EditProfileStoreDBVerif'), [:], FailureHandling.CONTINUE_ON_FAILURE)
    }
	
	'klik pada tombol balance'
	WebUI.click(findTestObject('Object Repository/Profile/Page_Balance/span_Balance'))
	
	'klik pada tombol API KEY'
	WebUI.click(findTestObject('Object Repository/Profile/Page_Balance/span_API Key'))
	
	'klik garis tiga di kanan atas web'
	WebUI.click(findTestObject('Object Repository/Profile/Page_Balance/i_LINA_ft-chevron-down'))

	'klik profil saya'
	WebUI.click(findTestObject('Object Repository/Profile/Page_Balance/a_Profil Saya'))

	'klik tombol edit profile'
	WebUI.click(findTestObject('Object Repository/Profile/Page_My Profile/button_Edit Profile'))
	
    'kondisi jika tidak ada failed pada bagian lain testcase'
    if (GlobalVariable.FlagFailed == 0) 
	{
		'tulis kondisi sukses'
       CustomKeywords.'writeToExcel.checkSaveProcess.checkStatus'(isMandatoryComplete, findTestObject('Profile/Page_Edit Profile/button_OK'), GlobalVariable.NumOfColumn, 'Edit Profile')
    }
	else
	{
		'Write To Excel GlobalVariable.StatusFailed and gagal karena reason status'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('Edit Profile', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 2) +
			';') + GlobalVariable.StatusReasonSystem)
	}
}
WebUI.closeBrowser()
