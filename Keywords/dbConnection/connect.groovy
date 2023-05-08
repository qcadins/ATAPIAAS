package dbConnection

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
import java.sql.DriverManager

import internal.GlobalVariable

public class connect {

	//fungsi digunakan untuk koneksi dengan database eendigo_prod_test_deploy_modify
	@Keyword
	def connectDBAPIAAS_esign() {

		String servername = findTestData('Data Files/Login/Login').getValue(1, 8)

		String port = findTestData('Data Files/Login/Login').getValue(2, 8)

		String database = findTestData('Data Files/Login/Login').getValue(3, 8)

		String username = findTestData('Data Files/Login/Login').getValue(4, 8)

		String password = findTestData('Data Files/Login/Login').getValue(5, 8)

		String url = servername + ':' + port + '/' + database

		Connection conn = DriverManager.getConnection(url, username, password)

		return conn
	}

	//fungsi digunakan untuk koneksi dengan database eendigo_prod_test_deploy_modify
	@Keyword
	def connectDBAPIAAS_public() {

		String servername = findTestData('Data Files/Login/Login').getValue(1, 8)

		String port = findTestData('Data Files/Login/Login').getValue(2, 8)

		String database = findTestData('Data Files/Login/Login').getValue(3, 8)

		String username = findTestData('Data Files/Login/Login').getValue(4, 8)

		String password = findTestData('Data Files/Login/Login').getValue(5, 8)

		String url = servername + ':' + port + '/' + database

		Connection conn = DriverManager.getConnection(url, username, password)

		return conn
	}

	//fungsi digunakan untuk koneksi dengan database adins apiaas uat tipe production
	@Keyword
	def connectDBAPIAAS_uatProduction() {

		String servername = findTestData('Data Files/Login/Login').getValue(1, 9)

		String port = findTestData('Data Files/Login/Login').getValue(2, 9)

		String database = findTestData('Data Files/Login/Login').getValue(3, 9)

		String username = findTestData('Data Files/Login/Login').getValue(4, 9)

		String password = findTestData('Data Files/Login/Login').getValue(5, 9)

		String url = servername + ':' + port + '/' + database

		Connection conn = DriverManager.getConnection(url, username, password)

		return conn
	}

	//fungsi digunakan untuk koneksi dengan database eendigo dev_uat
	@Keyword
	def connectDBAPIAAS_devUat() {

		String servername = findTestData('Data Files/Login/Login').getValue(1, 10)

		String port = findTestData('Data Files/Login/Login').getValue(2, 10)

		String database = findTestData('Data Files/Login/Login').getValue(3, 10)

		String username = findTestData('Data Files/Login/Login').getValue(4, 10)

		String password = findTestData('Data Files/Login/Login').getValue(5, 10)

		String url = servername + ':' + port + '/' + database

		Connection conn = DriverManager.getConnection(url, username, password)

		return conn
	}
}
