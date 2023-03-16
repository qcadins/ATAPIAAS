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

def conn = CustomKeywords.'dbConnection.connect.connectDBAPIAAS_esign'()

'simpan email ke dalam sebuah variabel'
String email = findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 8)

'simpan data yang diambil dari database'
ArrayList<String> credential = CustomKeywords.'profile.checkRegisterProfile.checkDBafterRegister'(conn, email)

'kumpulan string dari excel'
ArrayList<String> exceldata = new ArrayList<String>()

'data dari excel disimpan ke arraylist'
for (int i=8; i<=credential.size; i++)
{
	exceldata.add(findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, i))
}

'verifikasi data pada WEB dan excel sama'
for (int j = 0; j < exceldata.size ; j++) {
	checkVerifyEqualorMatch(WebUI.verifyMatch(credential[j], exceldata[j], false, FailureHandling.CONTINUE_ON_FAILURE))
}

def checkVerifyEqualorMatch(Boolean isMatch) {
	if(isMatch == false)
	{
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('Register', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathRegisterLogin).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonStoreDB)
	}
}