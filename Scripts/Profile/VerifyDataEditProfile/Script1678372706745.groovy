import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'deklarasi variabel untuk konek ke Database APIAAS'
def conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_esign'()

'ambil email dari testdata, disimpan ke string'
String email = WebUI.getAttribute(findTestObject('Profile/Page_Edit Profile/input__email'), 'value')

'kumpulan string dari data yang diambil langsung dari database'
ArrayList<String> hasildb = CustomKeywords.'profile.CheckProfile.getProfilefromDB'(conn, email)

'ambil text dari UI Web APIAAS'
ArrayList<String> hasilweb = CustomKeywords.'profile.CheckProfile.getAttributeValueProfile'()

'verifikasi data pada WEB dan DB sama'
for (int j = 0; j < hasildb.size; j++) {
    checkVerifyEqualorMatch(WebUI.verifyMatch(hasilweb[j], hasildb[j], false, FailureHandling.CONTINUE_ON_FAILURE))
}

def checkVerifyEqualorMatch(Boolean isMatch) {
    if (isMatch == false) {
        'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		GlobalVariable.FlagFailed = 1
        CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Edit Profile', GlobalVariable.NumOfColumn, 
			GlobalVariable.StatusFailed, (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 2) + 
				';') + GlobalVariable.FailedReasonVerifyEqualorMatch)
    }
}

