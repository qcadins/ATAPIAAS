package apikey

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
import groovy.sql.Sql as Sql
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement

import internal.GlobalVariable

public class checkAPIKey {

	def driver = DriverFactory.getWebDriver()

	def js = (JavascriptExecutor)driver

	int columnCount

	//fungsi untuk mengambil data APIKEY dari database
	@Keyword
	public getAPINamefromDB(Connection conn, String apiname) {
		String data

		ArrayList<String> listdata = new ArrayList<>()

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT api_key_name, (CASE WHEN lov_api_key_type = 120 THEN 'PRODUCTION' WHEN lov_api_key_type = 121 THEN 'TRIAL' END) AS lov_api_key_type FROM ms_api_key WHERE api_key_name = '"+apiname+"'")
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
	public getAttributeValueAPI() {

		String optionLabel

		optionLabel = WebUI.getAttribute(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/input'), 'aria-activedescendant')

		'kumpulan string yang menyimpan hasil text dari User Interface APIAAS'
		ArrayList<String> hasilgetText = new ArrayList<String>()

		'mengambil text dari field nama api key'
		hasilgetText.add(WebUI.getAttribute(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/input__apiKeyName'), 'value'))

		if(optionLabel.contains("-0"))
		{
			'mengambil text dari field status api key'
			hasilgetText.add('Active')
		}
		else
		{
			'mengambil text dari field status api key'
			hasilgetText.add('Inactive')
		}
		'sekelompok data akan dikembalikan dalam bentuk array'
		return hasilgetText
	}

	//fungsi untuk mengambil data APIKEY dari database
	@Keyword
	public getAPIStatusfromDB(Connection conn, String apiname) {
		String data

		ArrayList<String> listdata = new ArrayList<>()

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
		return listdata
	}


	//fungsi untuk mengambil jumlah data APIKEY dari database
	@Keyword
	public getTotalAPIKeyfromDB(Connection conn) {
		String data

		ArrayList<String> listdata = new ArrayList<>()

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT CONCAT(COUNT(api_key_name), ' total') AS total_count FROM ms_api_key WHERE id_ms_tenant = 29")
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
}
