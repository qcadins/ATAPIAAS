package dbconnection

import static com.kms.katalon.core.testdata.TestDataFactory.findTestData

import com.kms.katalon.core.annotation.Keyword

import java.sql.Connection
import java.sql.DriverManager

public class Connect {

	String pathLogin = 'Data Files/Login/Login'

	//fungsi digunakan untuk koneksi dengan database eendigo_prod_test_deploy_modify
	@Keyword
	def connectDBAPIAAS_esign() {
		String servername = findTestData(pathLogin).getValue(1, 8)

		String port = findTestData(pathLogin).getValue(2, 8)

		String database = findTestData(pathLogin).getValue(3, 8)

		String username = findTestData(pathLogin).getValue(4, 8)

		String password = findTestData(pathLogin).getValue(5, 8)

		String url = servername + ':' + port + '/' + database

		Connection conn = DriverManager.getConnection(url, username, password)

		conn
	}

	//fungsi digunakan untuk koneksi dengan database eendigo_prod_test_deploy_modify
	@Keyword
	def connectDBAPIAAS_public() {
		String servername = findTestData(pathLogin).getValue(1, 8)

		String port = findTestData(pathLogin).getValue(2, 8)

		String database = findTestData(pathLogin).getValue(3, 8)

		String username = findTestData(pathLogin).getValue(4, 8)

		String password = findTestData(pathLogin).getValue(5, 8)

		String url = servername + ':' + port + '/' + database

		Connection conn = DriverManager.getConnection(url, username, password)

		conn
	}

	//fungsi digunakan untuk koneksi dengan database adins apiaas uat tipe production (gk ada)
	@Keyword
	def connectDBAPIAAS_uatProduction() {
		String servername = findTestData(pathLogin).getValue(1, 9)

		String port = findTestData(pathLogin).getValue(2, 9)

		String database = findTestData(pathLogin).getValue(3, 9)

		String username = findTestData(pathLogin).getValue(4, 9)

		String password = findTestData(pathLogin).getValue(5, 9)

		String url = servername + ':' + port + '/' + database

		Connection conn = DriverManager.getConnection(url, username, password)

		conn
	}

	//fungsi digunakan untuk koneksi dengan database eendigo dev_uat
	@Keyword
	def connectDBAPIAAS_devUat() {
		String servername = findTestData(pathLogin).getValue(1, 10)

		String port = findTestData(pathLogin).getValue(2, 10)

		String database = findTestData(pathLogin).getValue(3, 10)

		String username = findTestData(pathLogin).getValue(4, 10)

		String password = findTestData(pathLogin).getValue(5, 10)

		String url = servername + ':' + port + '/' + database

		Connection conn = DriverManager.getConnection(url, username, password)

		conn
	}
}
