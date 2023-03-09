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

'buka chrome\r\n'
WebUI.openBrowser('')

'buka website APIAAS SIT, data diambil dari TestData Login'
WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 2))

'input email'
WebUI.setText(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/input_Buat Akun_form-control ng-untouched n_ab9ed8'), 
    findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 8))

'input password'
WebUI.setText(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/input_Buat Akun_form-control ng-untouched n_dd86a2'), 
    findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 9))

'ceklis pada reCaptcha'
//WebUI.click(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/div_id(katalon-rec_elementInfoDiv)'))

WebUI.delay(20)

'focus pada button login'
WebUI.focus(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))

'Klik Login'
WebUI.click(findTestObject('Object Repository/Eendigo/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))

