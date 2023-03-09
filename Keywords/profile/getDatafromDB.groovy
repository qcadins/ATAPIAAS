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

import internal.GlobalVariable

public class getDatafromDB {
	@Keyword
	public getDBdata(Connection conn) {
		String data
		ArrayList<String> listdata = new ArrayList<>()
		Statement stm = conn.createStatement()
		ResultSet resultSet = stm.executeQuery("SELECT ms_tenant.tenant_name, a.last_name, ms_tenant.tenant_industry, a.hashed_phone, am_user_personal_data.position from am_msuser a, am_user_personal_data , ms_tenant WHERE a.login_id = am_user_personal_data.email AND a.login_id = ms_tenant.email_reminder_dest AND a.login_id ='WILLIS.WY@AD-INS.COM'")
		ResultSetMetaData metadata  = resultSet.getMetaData()
		int columnCount = metadata.getColumnCount()
		while(resultSet.next()) {
			for(int i=1; i<=columnCount ; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		return listdata
		listdata.removeAll(listdata)
	}
}
