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
import com.kms.katalon.entity.global.GlobalVariableEntity

import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import groovy.sql.Sql as Sql
import org.openqa.selenium.By as By
import org.openqa.selenium.support.ui.Select as Select
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory

'mendapat jumlah kolom dari sheet Edit Profile'
int CountColumnEdit = findTestData(ExcelPathEditProfile).getColumnNumbers()

'kumpulan string yang menyimpan hasil text dari User Interface APIAAS'
ArrayList<String> hasilgetText = new ArrayList<String>()

'kumpulan string dari data yang diambil langsung dari database'
ArrayList<String> hasilexcel = new ArrayList<String>()

'mengambil text dari field nama perusahaan'
hasilgetText.add(WebUI.getAttribute(findTestObject('Eendigo/Page_Edit Profile/input__tenantName'), 'value'))

'megambil text dari field nama belakang'
hasilgetText.add(WebUI.getAttribute(findTestObject('Eendigo/Page_Edit Profile/input__lastName'), 'value'))

'mengambil text dari field industri'
hasilgetText.add(WebUI.getAttribute(findTestObject('Eendigo/Page_Edit Profile/input__industry'), 'value'))

'mengambil nomor telepon dari field Nomor HP'
hasilgetText.add(WebUI.getAttribute(findTestObject('Eendigo/Page_Edit Profile/input_Wanita_phoneNumber'), 'value'))

'mengambil text dari field jabatan kerja'
hasilgetText.add(WebUI.getAttribute(findTestObject('Eendigo/Page_Edit Profile/input__position'), 'value'))

'mengambil data dari excel'
hasilexcel.add(findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 10))
hasilexcel.add(findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 11))
hasilexcel.add(findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 12))
hasilexcel.add(findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 13))
hasilexcel.add(findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 14))

'verifikasi data pada WEB dan excel sama'
for (int j = 0; j < hasilexcel.size ; j++) {
	WebUI.verifyMatch(hasilgetText[j], hasilexcel[j], false)
	if(WebUI.verifyMatch(hasilgetText[j], hasilexcel[j], false))
	{
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('Edit Profile', GlobalVariable.NumOfColumn, GlobalVariable.Failed,
		(findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.FailedReasonDataNotMatch)
	}
}