package tenant

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

import javax.servlet.http.HttpServletRequest
import javax.swing.ComboBoxModel
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.Sheet


import org.openqa.selenium.By
import org.openqa.selenium.support.ui.Select
import com.kms.katalon.core.webui.driver.DriverFactory
import groovy.sql.Sql as Sql
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

import internal.GlobalVariable

public class tenantVerif {

	int columnCount

	//fungsi mengambil jumlah tenant
	@Keyword
	public getTenantTotal(Connection conn) {

		int data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select count(*) from esign.ms_tenant")

		while(resultSet.next())
		{
			data = resultSet.getObject(1);
		}

		return data
	}

	@Keyword
	public getTenantStoreDB(Connection conn, String refnum){
		String data
		ArrayList<String> listdata = new ArrayList<>()
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select tenant_name, tenant_code, ref_number_label, api_key, email_reminder_dest from esign.ms_tenant where ref_number_label = '"+ refnum +"'")
		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for(int i = 1 ; i <= columnCount ; i++){
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		return listdata
	}

	@Keyword
	public getTenantServicesDescription(Connection conn, String tenantcode){
		String data
		ArrayList<String> listdata = new ArrayList<>()
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT ms_lov.description, value FROM esign.ms_tenant CROSS JOIN LATERAL json_each_text(threshold_balance::json) AS threshold_type JOIN esign.ms_lov ON ms_lov.lov_group = 'BALANCE_TYPE' AND ms_lov.code = threshold_type.key WHERE tenant_code = '"+ tenantcode +"'")
		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for(int i = 1 ; i <= columnCount ; i++){
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		return listdata
	}

	@Keyword
	public getTenantServices(Connection conn, String tenantname){
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select threshold_balance from esign.ms_tenant where tenant_name = '"+ tenantname +"'")
		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		return data
	}
	
	//fungsi mengambil nama sheet yang digunakan
	@Keyword
	public getIDPaymentType(Connection conn, String tenantcode, String testedOCR) {
		int data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("Select ml.id_lov FROM esign.ms_balancevendoroftenant mbt join esign.ms_tenant mt on mt.id_ms_tenant = mbt.id_ms_tenant Join esign.ms_lov ml on ml.id_lov = mbt.lov_balance_type Where mt.tenant_code = '"+ tenantcode +"' AND ml.code = '"+testedOCR+"'")

		while(resultSet.next())
		{
			data = resultSet.getObject(1);
		}

		return data
	}

	//fungsi mengambil jenis penagihan saldo (quantity/price)
	@Keyword
	public getPaymentType(Connection conn, String tenantcode, int idPayment) {
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("Select description From esign.ms_lov ml join esign.ms_balancevendoroftenant mbt on ml.id_lov = mbt.lov_balance_charge_type Join esign.ms_tenant mt on mt.id_ms_tenant = mbt.id_ms_tenant Where ml.lov_group = 'BALANCE_CHARGE_TYPE' and mt.tenant_code = '"+ tenantcode +"' AND mbt.lov_balance_type = "+ idPayment +"")

		while(resultSet.next())
		{
			data = resultSet.getObject(1)
		}
		return data
	}
}
