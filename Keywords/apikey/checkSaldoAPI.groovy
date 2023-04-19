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

public class checkSaldoAPI {

	int columnCount

	//fungsi mengambil jumlah tenant
	@Keyword
	public gettotalTenant(Connection conn) {
		int data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT COUNT(tenant_code) FROM esign.ms_tenant")

		while(resultSet.next())
		{
			data = resultSet.getObject(1);
		}

		return data
	}

	//fungsi mengambil jumlah vendor
	@Keyword
	public gettotalVendor(Connection conn, String tenant) {
		int data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT COUNT(mv.vendor_name) FROM esign.ms_vendor mv JOIN esign.ms_vendoroftenant mot ON mot.id_ms_vendor = mv.id_ms_vendor JOIN esign.ms_tenant mt ON mt.id_ms_tenant = mot.id_ms_tenant WHERE mt.tenant_code = '"+tenant+"'")

		while(resultSet.next())
		{
			data = resultSet.getObject(1);
		}

		return data
	}

	//fungsi mengambil nama vendor dari DB
	@Keyword
	public getVendorName(Connection conn, String tenant) {
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT mv.vendor_name FROM esign.ms_vendor mv JOIN esign.ms_vendoroftenant mot ON mot.id_ms_vendor = mv.id_ms_vendor JOIN esign.ms_tenant mt ON mt.id_ms_tenant = mot.id_ms_tenant WHERE mt.tenant_code = '"+tenant+"'")

		while(resultSet.next())
		{
			data = resultSet.getObject(1);
		}

		return data
	}

	//fungsi mengambil jumlah saldo yang diaktifkan user
	@Keyword
	public gettotalTipeSaldo(Connection conn, String tenant) {
		int data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT COUNT(mb.lov_balance_type) FROM esign.ms_balancevendoroftenant mb JOIN esign.ms_tenant mt ON mb.id_ms_tenant = mt.id_ms_tenant WHERE mt.tenant_code = '"+ tenant +"'")

		while(resultSet.next())
		{
			data = resultSet.getObject(1);
		}

		return data
	}

	//fungsi mengambil nama saldo yang diaktifkan user
	@Keyword
	public getNamaTipeSaldo(Connection conn, String tenant) {
		String data

		ArrayList<String> listdata = new ArrayList<>()

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT mlo.description FROM esign.ms_lov mlo JOIN esign.ms_balancevendoroftenant mb ON mb.lov_balance_type = mlo.id_lov JOIN esign.ms_tenant mt ON mb.id_ms_tenant = mt.id_ms_tenant WHERE mt.tenant_code = '"+tenant+"'")
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

	//fungsi mengambil jumlah saldo yang diaktifkan user
	@Keyword
	public getLatestMutation(Connection conn, String tenant) {
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select trx_no from esign.tr_balance_mutation bm JOIN esign.ms_tenant mt ON mt.id_ms_tenant = bm.id_ms_tenant WHERE tenant_code = '"+tenant+"' ORDER BY id_balance_mutation DESC limit 1;")

		while(resultSet.next())
		{
			data = resultSet.getObject(1);
		}

		return data
	}
	
	//fungsi mengambil jumlah saldo yang diaktifkan user
	@Keyword
	public getLatestMutationOtherTenant(Connection conn, String tenant) {
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select trx_no from esign.tr_balance_mutation bm JOIN esign.ms_tenant mt ON mt.id_ms_tenant = bm.id_ms_tenant WHERE tenant_code != '"+tenant+"' ORDER BY id_balance_mutation DESC limit 1;")

		while(resultSet.next())
		{
			data = resultSet.getObject(1);
		}

		return data
	}
}
