package layananSaya

import com.kms.katalon.core.annotation.Keyword

import java.sql.Connection
import java.sql.Statement

import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import groovy.sql.Sql as Sql

public class VerifLayanan {

	int columnCount

	//fungsi untuk mengambil tenant code dari database
	@Keyword
	getTenantCodefromDB(Connection conn, String email) {
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT tenant_code FROM ms_tenant WHERE email_reminder_dest = '"+ email +"'")

		while (resultSet.next())
		{
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getListServiceName(Connection conn, String tenantcode) {
		String data

		ArrayList<String> listdata = []

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery(
				"SELECT description " +
				"FROM ms_balancevendoroftenant mbt " +
				"LEFT JOIN ms_lov mlo ON mlo.id_lov = mbt.lov_balance_type " +
				"LEFT JOIN ms_tenant mt ON mt.id_ms_tenant = mbt.id_ms_tenant " +
				"WHERE tenant_code = '" + tenantcode + "'")

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

	@Keyword
	getListServiceStatus(Connection conn, String tenantcode) {
		String data

		ArrayList<String> listdata = []

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT CASE WHEN mbt.is_active = '1' THEN 'Active' ELSE 'Inactive' END AS status FROM ms_balancevendoroftenant mbt LEFT JOIN ms_lov mlo ON mlo.id_lov = mbt.lov_balance_type LEFT JOIN ms_tenant mt ON mt.id_ms_tenant = mbt.id_ms_tenant WHERE tenant_code = '"+tenantcode+"'")
		ResultSetMetaData metadata  = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for(int i = 1; i <= columnCount ; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}

	@Keyword
	getListChargeType(Connection conn, String tenantcode) {
		String data

		ArrayList<String> listdata = []

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT description FROM ms_balancevendoroftenant mbt LEFT JOIN ms_lov mlo ON mlo.id_lov = mbt.lov_balance_charge_type LEFT JOIN ms_tenant mt ON mt.id_ms_tenant = mbt.id_ms_tenant WHERE tenant_code = '"+tenantcode+"'")
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
}
