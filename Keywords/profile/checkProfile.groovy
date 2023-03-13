package profile;

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
import java.sql.Connection
import java.sql.Statement
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.Select
import com.kms.katalon.core.webui.driver.DriverFactory

import internal.GlobalVariable

public class checkProfile {

	int columnCount

	//fungsi untuk mengambil data profile dari database
	@Keyword
	public getProfilefromDB(Connection conn, String email, String country) {
		String data

		ArrayList<String> listdata = new ArrayList<>()

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT a.initial_name, a.last_name, ms_tenant.tenant_name, ms_tenant.tenant_industry, am_user_personal_data.gender, ms_tenant.tenant_website, a.hashed_phone, am_user_personal_data.position, ms_country_code.country_name || ' ' || ms_country_code.country_code from am_msuser a, am_user_personal_data , ms_tenant, ms_country_code WHERE a.login_id = am_user_personal_data.email AND a.login_id = ms_tenant.email_reminder_dest AND a.login_id ='"+ email +"' AND ms_country_code.country_name || ' ' || ms_country_code.country_code = '"+ country +"'")
		ResultSetMetaData metadata  = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while(resultSet.next()) {
			for(int i=1; i<=columnCount ; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		return listdata
	}

	//fungsi digunakan untuk mengambil text dari attribut website yang memiliki textbox
	@Keyword
	public getAttributeValueProfile() {
		Select select
		String optionLabel

		'klik pada frame login'
		WebUI.click(findTestObject('Profile/Page_Edit Profile/framelogin'))

		'fungsi select untuk yang mengarah ke element dropdown list negara'
		select = new Select(DriverFactory.getWebDriver().findElement(By.xpath("//select[@id='countryCodes']")))

		'ambil text yang diselect oleh dropdown list tersebut'
		optionLabel = select.getFirstSelectedOption().getText()

		'kumpulan string yang menyimpan hasil text dari User Interface APIAAS'
		ArrayList<String> hasilgetText = new ArrayList<String>()

		'mengambil first name dari field nama depan'
		hasilgetText.add(WebUI.getAttribute(findTestObject('Profile/Page_Edit Profile/input__firstName'), 'value'))

		'megambil text dari field nama belakang'
		hasilgetText.add(WebUI.getAttribute(findTestObject('Profile/Page_Edit Profile/input__lastName'), 'value'))

		'mengambil text dari field nama perusahaan'
		hasilgetText.add(WebUI.getAttribute(findTestObject('Profile/Page_Edit Profile/input__tenantName'), 'value'))

		'mengambil text dari field industri'
		hasilgetText.add(WebUI.getAttribute(findTestObject('Profile/Page_Edit Profile/input__industry'), 'value'))

		'mengambil value dari gender'
		if (findTestData('APIAAS/DataEditProfile').getValue(GlobalVariable.NumOfColumn, 14) == 'M') {
			hasilgetText.add(WebUI.getAttribute(findTestObject('Object Repository/Profile/Page_Edit Profile/input__ng-untouched ng-pristine ng-valid'), 'value'))
		} else {
			hasilgetText.add(WebUI.getAttribute(findTestObject('Object Repository/Profile/Page_Edit Profile/input_Pria_ng-untouched ng-pristine ng-valid'), 'value'))
		}

		'mengambil value dari field website'
		hasilgetText.add(WebUI.getAttribute(findTestObject('Object Repository/Profile/Page_Edit Profile/input__website'), 'value'))

		'mengambil nomor telepon dari field Nomor HP'
		hasilgetText.add(WebUI.getAttribute(findTestObject('Profile/Page_Edit Profile/input_Wanita_phoneNumber'), 'value'))

		'mengambil text dari field jabatan kerja'
		hasilgetText.add(WebUI.getAttribute(findTestObject('Profile/Page_Edit Profile/input__position'), 'value'))

		'mengambil text dari field negara'
		hasilgetText.add(optionLabel)

		//		'mengambil value dari ddl negara'
		//		hasilgetText.add(WebUI.getAttribute(findTestObject('Object Repository/Profile/Page_Edit Profile/select_Afghanistan 93Albania 355Algeria 213_ddb156'), 'text'))

		'sekelompok data akan dikembalikan dalam bentuk array'
		return hasilgetText
	}
}
