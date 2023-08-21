package apikey

import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import java.sql.Connection
import java.sql.Statement

import java.sql.ResultSet
import java.sql.ResultSetMetaData
import org.openqa.selenium.support.ui.Select
import groovy.sql.Sql as Sql

public class CheckAPIKey {

	int columnCount

	//fungsi untuk mengambil data APIKEY dari database
	@Keyword
	getAPINamefromDB(Connection conn, String apiname) {
		String data

		ArrayList listdata = []

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select mk.api_key_name, mlo.description from ms_lov mlo JOIN ms_api_key mk on mlo.id_lov = mk.lov_api_key_type where mk.api_key_name = '"+apiname+"'")

		ResultSetMetaData metadata  = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while(resultSet.next()) {
			for(int i=1; i<=columnCount ; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}

	//fungsi digunakan untuk mengambil text dari attribut website yang memiliki textbox
	@Keyword
	getAttributeValueAPI() {

		String optionLabel

		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/input'))

		optionLabel = WebUI.getAttribute(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/input'), 'aria-activedescendant')

		println(optionLabel)

		'kumpulan string yang menyimpan hasil text dari User Interface APIAAS'
		ArrayList hasilgetText = []

		'mengambil text dari field nama api key'
		hasilgetText.add(WebUI.getAttribute(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/'+
				'input__apiKeyName'), 'value'))

		if (optionLabel.contains('-0')) {

			'mengambil text dari field status api key'
			hasilgetText.add('Active')
		}
		else {

			'mengambil text dari field status api key'
			hasilgetText.add('Inactive')
		}
		'sekelompok data akan dikembalikan dalam bentuk array'
		hasilgetText
	}

	//fungsi untuk mengambil data APIKEY dari database
	@Keyword
	getAPIStatusfromDB(Connection conn, String apiname) {
		String data

		ArrayList listdata = []

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT api_key_name, (CASE WHEN is_active = '1' THEN 'Active' WHEN is_active = '0' THEN 'Inactive' END) AS is_active FROM ms_api_key WHERE api_key_name = '"+apiname+"'")
		ResultSetMetaData metadata  = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while(resultSet.next()) {
			for(int i=1; i<=columnCount ; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}


	//fungsi untuk mengambil jumlah data APIKEY dari database
	@Keyword
	getTotalAPIKeyfromDB(Connection conn, String email) {
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT CONCAT(COUNT(api_key_name), ' total') FROM ms_api_key mak LEFT JOIN ms_useroftenant mot ON mot.id_ms_tenant = mak.id_ms_tenant LEFT JOIN am_msuser amu ON amu.id_ms_user = mot.id_ms_user WHERE amu.login_id = '"+ email +"'")

		while (resultSet.next()) {

			data = resultSet.getObject(1);
		}
		data
	}

	@Keyword
	getTenantCodeName(Connection conn, String email) {
		String data

		ArrayList listdata = []

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select tenant_code, tenant_name from am_msuser amu join ms_useroftenant muot on amu.id_ms_user = muot.id_ms_user join ms_tenant mt on mt.id_ms_tenant = muot.id_ms_tenant where login_id = '"+ email +"'")

		ResultSetMetaData metadata  = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while(resultSet.next()) {
			for(int i=1; i<=columnCount ; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}
}
