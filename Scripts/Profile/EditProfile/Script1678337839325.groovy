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

//'mendapat jumlah kolom dari sheet Edit Profile'
//int CountColumnEdit = findTestData(ExcelPathEditProfile).getColumnNumbers()

'memanggil fungsi untuk login'
    WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [:], FailureHandling.STOP_ON_FAILURE)

    'klik garis tiga di kanan atas web'
    WebUI.click(findTestObject('Object Repository/Eendigo/Page_Balance/i_LINA_ft-chevron-down'))

    'klik profil saya'
    WebUI.click(findTestObject('Object Repository/Eendigo/Page_Balance/a_Profil Saya'))

    'klik tombol edit profile'
    WebUI.click(findTestObject('Object Repository/Eendigo/Page_My Profile/button_Edit Profile'))

    'panggil fungsi verifikasi jika checkdatabase = yes'
    if (GlobalVariable.KondisiCekDB == 'Yes') {
        'verifikasi data yang ada di web dengan di database sebelum diEdit'
        WebUI.callTestCase(findTestCase('Test Cases/Profile/VerifyDataEditProfile'), [:], FailureHandling.STOP_ON_FAILURE)
    }
    
    'input data nama perusahaan'
    WebUI.setText(findTestObject('Object Repository/Eendigo/Page_Edit Profile/input__tenantName'), findTestData(ExcelPathEditProfile).getValue(
            GlobalVariable.NumOfColumn, 10))

    'klik pada field nama belakang'
    WebUI.click(findTestObject('Object Repository/Eendigo/Page_Edit Profile/input__lastName'))

    'input data nama belakang'
    WebUI.setText(findTestObject('Object Repository/Eendigo/Page_Edit Profile/input__lastName'), findTestData(ExcelPathEditProfile).getValue(
            GlobalVariable.NumOfColumn, 11))

    'input data industri'
    WebUI.setText(findTestObject('Object Repository/Eendigo/Page_Edit Profile/input__industry'), findTestData(ExcelPathEditProfile).getValue(
            GlobalVariable.NumOfColumn, 12))

    'pilih jenis kelamin'
    WebUI.click(findTestObject('Object Repository/Eendigo/Page_Edit Profile/input__ng-untouched ng-pristine ng-valid'))

    'klik pada field website'
    WebUI.click(findTestObject('Object Repository/Eendigo/Page_Edit Profile/input__website'))

    'input data field webstie'
    WebUI.setText(findTestObject('Object Repository/Eendigo/Page_Edit Profile/input_Wanita_phoneNumber'), findTestData(ExcelPathEditProfile).getValue(
            GlobalVariable.NumOfColumn, 13))

    'input data field position'
    WebUI.setText(findTestObject('Object Repository/Eendigo/Page_Edit Profile/input__position'), findTestData(ExcelPathEditProfile).getValue(
            GlobalVariable.NumOfColumn, 14))

    'pilih dari dropdownlist +62 Indonesia'
    WebUI.selectOptionByValue(findTestObject('Object Repository/Eendigo/Page_Edit Profile/select_Afghanistan 93Albania 355Algeria 213_ddb156'), 
        findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 15), true)

    'klik tombol simpan'
    WebUI.click(findTestObject('Object Repository/Eendigo/Page_Edit Profile/button_Simpan'))

    'klik tombol garis tiga di kanan atas web'
    WebUI.click(findTestObject('Object Repository/Eendigo/Page_Balance/i_LINA_ft-chevron-down'))

    'klik tombol profil saya'
    WebUI.click(findTestObject('Object Repository/Eendigo/Page_Balance/a_Profil Saya'))

    'klik tombol edit profile'
    WebUI.click(findTestObject('Object Repository/Eendigo/Page_My Profile/button_Edit Profile'))

    'panggil fungsi verifikasi jika checkdatabase = yes'
    if (GlobalVariable.KondisiCekDB == 'Yes') {
        'verifikasi data yang ada di excel dengan di database sesudah diEdit'
        WebUI.callTestCase(findTestCase('Test Cases/Profile/EditProfileStoreDBVerif'), [:], FailureHandling.STOP_ON_FAILURE)
    }
    
    'klik pada tombol simpan'
    WebUI.click(findTestObject('Object Repository/Eendigo/Page_Edit Profile/button_Simpan'))

    'verifikasi adanya tombol ok setelah klik simpan'
    if (WebUI.verifyElementNotPresent(findTestObject('Eendigo/Page_Edit Profile/button_OK'), GlobalVariable.Timeout)) {
        'tulis error ke excel'
       CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('Edit Profile', GlobalVariable.NumOfColumn, GlobalVariable.Failed, 
		   (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.FailedReasonSubmitError)
	   WebUI.closeBrowser()
    }
	else
	{
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('Edit Profile', GlobalVariable.NumOfColumn, GlobalVariable.Success,
			GlobalVariable.SuccessReason)
	}
WebUI.closeBrowser()

