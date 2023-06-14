import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection

'deklarasi variabel untuk konek ke Database APIAAS'
Connection connpublic = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_public'()

'kumpulan string dari WEB'
ArrayList<String> totaldata = []

'kumpulan string dari DB'
ArrayList<String> totaldataDB = CustomKeywords.'apikey.CheckAPIKey.getTotalAPIKeyfromDB'(connpublic, findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 9))

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

