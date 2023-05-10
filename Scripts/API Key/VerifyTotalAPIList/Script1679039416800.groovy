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

'deklarasi variabel untuk konek ke Database APIAAS'
def conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_public'()

'kumpulan string dari WEB'
ArrayList<String> totaldata = new ArrayList<String>()

'kumpulan string dari DB'
ArrayList<String> totaldataDB = CustomKeywords.'apikey.CheckAPIKey.getTotalAPIKeyfromDB'(conn)

'masukkan hasil perhitungan jumlah key ke totaldata'
totaldata.add(WebUI.getText(findTestObject('Object Repository/API_KEY/Page_Api Key List/Footer')))

'cek jumlah data dari DB dan WEB adalah sama'
for (int j = 0; j < totaldataDB.size; j++) {
	checkVerifyEqualorMatch(WebUI.verifyMatch(totaldata[j], totaldataDB[j], false, FailureHandling.CONTINUE_ON_FAILURE))
}

def checkVerifyEqualorMatch(Boolean isMatch) {
	if (isMatch == false) {
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		GlobalVariable.FlagFailed = 1
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('API KEY', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
		(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.FailedReasonStoreDB)
	}
}

