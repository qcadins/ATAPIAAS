import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection

'deklarasi variabel untuk konek ke Database APIAAS'
Connection conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_public'()

'ambil nama API dari testdata, disimpan ke string'
String namaAPI = WebUI.getAttribute(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/input__apiKeyName'), 
	'value')

'kumpulan string dari data yang diambil langsung dari database'
ArrayList<String> hasildb = CustomKeywords.'apikey.CheckAPIKey.getAPIStatusfromDB'(conn, namaAPI)

'ambil text dari UI Web APIAAS'
ArrayList<String> hasilweb = CustomKeywords.'apikey.CheckAPIKey.getAttributeValueAPI'()

'verifikasi data pada WEB dan DB sama'
for (int j = 0; j < hasildb.size; j++) {
	
    checkVerifyEqualorMatch(WebUI.verifyMatch(hasilweb[j], hasildb[j], false, FailureHandling.CONTINUE_ON_FAILURE))
}

def checkVerifyEqualorMatch(Boolean isMatch) {
    if (isMatch == false) {
		
        'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		GlobalVariable.FlagFailed = 1
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('API KEY', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
				GlobalVariable.FailedReasonVerifyEqualorMatch)
    }
}