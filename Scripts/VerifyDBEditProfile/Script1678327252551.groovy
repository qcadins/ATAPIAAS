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

'deklarasi variabel untuk konek ke Database APIAAS'
def conn = CustomKeywords.'com.query.connect.connectDBAPIAAS'()

'kumpulan string yang menyimpan hasil text dari User Interface APIAAS'
ArrayList<String> hasilgetText = new ArrayList<String>()

'kumpulan string dari data yang diambil langsung dari database'
ArrayList<String> hasildb = CustomKeywords.'com.query.getDatafromDB.getDBdata'(conn)

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

'verifikasi data pada WEB dan DB sama'
for (int j = 0; j < 5; j++) {
	WebUI.verifyMatch(hasilgetText[j], hasildb[j], false)
}
'kosongkan isi hasilGetText'
hasilgetText.removeAll(hasilgetText)
'kosongkan isi hasildb'
hasildb.removeAll(hasildb)