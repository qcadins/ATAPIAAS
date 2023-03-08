import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

WebUI.openBrowser('')

WebUI.navigateToUrl(GlobalVariable.URLAPIAAS)

WebUI.setText(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/input_Buat Akun_form-control ng-untouched n_ab9ed8'), 
    findTestData('DataRegistLogin').getValue(2, 15))

WebUI.setText(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/input_Buat Akun_form-control ng-untouched n_dd86a2'), 
    findTestData('DataRegistLogin').getValue(2, 16))

WebUI.click(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/div_id(katalon-rec_elementInfoDiv)'))

WebUI.click(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))

WebUI.click(findTestObject('Object Repository/Eendigo/Page_Balance/i_LINA_ft-chevron-down'))

WebUI.click(findTestObject('Object Repository/Eendigo/Page_Balance/a_Profil Saya'))

WebUI.click(findTestObject('Object Repository/Eendigo/Page_My Profile/button_Edit Profile'))

WebUI.setText(findTestObject('Object Repository/Eendigo/Page_Edit Profile/input__tenantName'), findTestData('DataRegistLogin').getValue(
        2, 17))

WebUI.click(findTestObject('Object Repository/Eendigo/Page_Edit Profile/input__lastName'))

WebUI.setText(findTestObject('Object Repository/Eendigo/Page_Edit Profile/input__lastName'), findTestData('DataRegistLogin').getValue(
        2, 18))

WebUI.setText(findTestObject('Object Repository/Eendigo/Page_Edit Profile/input__industry'), findTestData('DataRegistLogin').getValue(
        2, 19))

WebUI.click(findTestObject('Object Repository/Eendigo/Page_Edit Profile/input__ng-untouched ng-pristine ng-valid'))

WebUI.click(findTestObject('Object Repository/Eendigo/Page_Edit Profile/input__website'))

WebUI.setText(findTestObject('Object Repository/Eendigo/Page_Edit Profile/input_Wanita_phoneNumber'), findTestData('DataRegistLogin').getValue(
        2, 20))

WebUI.setText(findTestObject('Object Repository/Eendigo/Page_Edit Profile/input__position'), findTestData('DataRegistLogin').getValue(
        2, 21))

WebUI.selectOptionByValue(findTestObject('Object Repository/Eendigo/Page_Edit Profile/select_Afghanistan 93Albania 355Algeria 213_ddb156'), 
    findTestData('DataRegistLogin').getValue(2, 22), true)

WebUI.click(findTestObject('Object Repository/Eendigo/Page_Edit Profile/button_Simpan'))

WebUI.click(findTestObject('Object Repository/Eendigo/Page_Edit Profile/button_OK'))

WebUI.click(findTestObject('Object Repository/Eendigo/Page_My Profile/i_WILLIS_ft-chevron-down'))

WebUI.click(findTestObject('Object Repository/Eendigo/Page_My Profile/span_Keluar'))

WebUI.closeBrowser()

