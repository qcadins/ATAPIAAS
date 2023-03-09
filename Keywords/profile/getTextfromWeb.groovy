package profile

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable

public class getTextfromWeb {
	
	@Keyword
	public getTextfromField() {
		
		'kumpulan string yang menyimpan hasil text dari User Interface APIAAS'
		ArrayList<String> hasilgetText = new ArrayList<String>()
		
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
		
		return hasilgetText
	}
}
