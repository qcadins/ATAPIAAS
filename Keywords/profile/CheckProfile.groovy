package profile;

import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import java.sql.Connection
import java.sql.Statement
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.Select
import com.kms.katalon.core.webui.driver.DriverFactory

import internal.GlobalVariable

public class CheckProfile {

	int columnCount

	//fungsi untuk mengambil data profile dari database
	@Keyword
	getProfilefromDB(Connection conn, String email, String role) {
		String data

		ArrayList listdata = []

		Statement stm = conn.createStatement()

		ResultSet resultSet
		
		if (role == 'Admin Client') {			
			resultSet = stm.executeQuery("SELECT amu.initial_name, amu.last_name, mt.tenant_name, mt.tenant_industry, aupd.gender, mt.tenant_website, amu.hashed_phone, aupd.position, mcc.country_name || ' ' || mcc.country_code from am_msuser amu join am_user_personal_data aupd on amu.id_ms_user = aupd.id_ms_user join ms_useroftenant mut on mut.id_ms_user = amu.id_ms_user join ms_tenant mt on mt.id_ms_tenant = mut.id_ms_tenant join ms_country_code mcc on mcc.id_country_code = amu.id_country_code WHERE amu.login_id = '" + email + "'")
		} else {
			resultSet = stm.executeQuery("SELECT amu.initial_name, amu.last_name, aupd.gender, amu.hashed_phone, aupd.position, mcc.country_name || ' ' || mcc.country_code from am_msuser amu join am_user_personal_data aupd on amu.id_ms_user = aupd.id_ms_user join ms_useroftenant mut on mut.id_ms_user = amu.id_ms_user join ms_tenant mt on mt.id_ms_tenant = mut.id_ms_tenant join ms_country_code mcc on mcc.id_country_code = amu.id_country_code WHERE amu.login_id = '" + email + "'")
		}
		
		ResultSetMetaData metadata  = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for (int i = 1; i <= columnCount ; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}

	//fungsi digunakan untuk mengambil text dari attribut website yang memiliki textbox
	@Keyword
	getAttributeValueProfile(String role) {
		Select select
		String optionLabel

		'klik pada frame login'
		WebUI.click(findTestObject('Profile/Page_Edit Profile/framelogin'))

		'fungsi select untuk yang mengarah ke element dropdown list negara'
		select = new Select(DriverFactory.getWebDriver().findElement(By.xpath("//select[@id='countryCodes']")))

		'ambil text yang diselect oleh dropdown list tersebut'
		optionLabel = select.getFirstSelectedOption().getText()

		'kumpulan string yang menyimpan hasil text dari User Interface APIAAS'
		ArrayList hasilgetText = []

		'mengambil first name dari field nama depan'
		hasilgetText.add(WebUI.getAttribute(findTestObject('Profile/Page_Edit Profile/input__firstName'), 'value'))

		'megambil text dari field nama belakang'
		hasilgetText.add(WebUI.getAttribute(findTestObject('Profile/Page_Edit Profile/input__lastName'), 'value'))

		if (role == 'Admin Client') {			
			'mengambil text dari field nama perusahaan'
			hasilgetText.add(WebUI.getAttribute(findTestObject('Profile/Page_Edit Profile/input__tenantName'), 'value'))
	
			'mengambil text dari field industri'
			hasilgetText.add(WebUI.getAttribute(findTestObject('Profile/Page_Edit Profile/input__industry'), 'value'))
		}

		'mengambil value dari gender'
		if (findTestData('APIAAS/DataEditProfile').getValue(GlobalVariable.NumOfColumn, 15) == 'M') {
			hasilgetText.add(WebUI.getAttribute(findTestObject('Object Repository/Profile/Page_Edit Profile/input__radioMale'), 'value'))
		}
		else {
			hasilgetText.add(WebUI.getAttribute(findTestObject('Object Repository/Profile/Page_Edit Profile/input__radioFemale'), 'value'))
		}

		if (role == 'Admin Client') {
			'mengambil value dari field website'
			hasilgetText.add(WebUI.getAttribute(findTestObject('Object Repository/Profile/Page_Edit Profile/input__website'), 'value'))
		}
		
		'mengambil nomor telepon dari field Nomor HP'
		hasilgetText.add(WebUI.getAttribute(findTestObject('Profile/Page_Edit Profile/input__PhoneNum'), 'value'))

		'mengambil text dari field jabatan kerja'
		hasilgetText.add(WebUI.getAttribute(findTestObject('Profile/Page_Edit Profile/input__position'), 'value'))

		'mengambil text dari field negara'
		hasilgetText.add(optionLabel)

		//		'mengambil value dari ddl negara'
		//		hasilgetText.add(WebUI.getAttribute(findTestObject('Object Repository/Profile/Page_Edit Profile/select__country'), 'text'))

		'sekelompok data akan dikembalikan dalam bentuk array'
		hasilgetText
	}

	//fungsi untuk mengambil tenant code dari database
	@Keyword
	getTenantNamefromDB(Connection conn, String email) {
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select tenant_name from am_msuser amu join ms_useroftenant muot on amu.id_ms_user = muot.id_ms_user join ms_tenant mt on mt.id_ms_tenant = muot.id_ms_tenant join ms_api_key amk on amk.id_ms_tenant = mt.id_ms_tenant where login_id = '"+ email +"'")

		while (resultSet.next()) {

			data = resultSet.getObject(1)
		}
		data
	}
	
	@Keyword
	getUserRole(Connection conn, String email) {
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT mr.role_name FROM am_msuser am JOIN am_memberofrole amr ON amr.id_ms_user = am.id_ms_user JOIN am_msrole mr ON mr.id_ms_role = amr.id_ms_role WHERE am.login_id = '"+ email +"'")
		
		while (resultSet.next()) {

			data = resultSet.getObject(1)
		}
		data
	}
}
