package ocrTesting

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

import javax.servlet.http.HttpServletRequest
import javax.swing.ComboBoxModel
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.Sheet

import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.ResultSetMetaData
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

public class getParameterfromDB {

	def driver = DriverFactory.getWebDriver()

	def js = (JavascriptExecutor)driver

	int columnCount

	//fungsi mengambil nama sheet yang digunakan
	@Keyword
	public getIDPaymentType(Connection conn, String tenantcode, String testedOCR) {
		int data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("Select ml.id_lov FROM esign.ms_balancevendoroftenant mbt join esign.ms_tenant mt on mt.id_ms_tenant = mbt.id_ms_tenant Join esign.ms_lov ml on ml.id_lov = mbt.lov_balance_type Where mt.tenant_code = '"+ tenantcode +"' AND ml.description = '"+testedOCR+"'")

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

	//fungsi untuk mengambil KEY dari database
	@Keyword
	public getAPIKeyfromDB(Connection conn, String tenantcode) {
		String data

		ArrayList<String> listdata = new ArrayList<>()

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT api_key_code FROM ms_api_key mk JOIN ms_tenant mt ON mk.id_ms_tenant = mt.id_ms_tenant JOIN ms_lov mlo ON mlo.id_lov = mk.lov_api_key_type  WHERE mt.tenant_code = '"+ tenantcode +"' AND mk.is_active = '1' AND mlo.description = 'TRIAL'")
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
	public getTenantCodefromDB(Connection conn, String email) {
		String data

		ArrayList<String> listdata = new ArrayList<>()

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT tenant_code FROM ms_tenant WHERE email_reminder_dest = '"+ email +"'")
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
	public getLatestMutationfromDB(Connection conn, String tenantcode) {
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select trx_no from esign.tr_balance_mutation WHERE usr_crt = '"+tenantcode+"' ORDER BY trx_no DESC limit 1;")

		while(resultSet.next())
		{
			data = resultSet.getObject(1)
		}
		return data
	}

	//fungsi untuk ambil harga service OCR dari DB
	@Keyword
	public getServicePricefromDB(Connection conn, int idPayment) {
		int data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT msp.service_price FROM esign.ms_service_price msp WHERE lov_balance_type = "+idPayment+" AND effective_date >= '2023-03-25'")

		while(resultSet.next())
		{
			data = resultSet.getObject(1);
		}

		return data
	}
}
