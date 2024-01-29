package layanansaya

import com.kms.katalon.core.annotation.Keyword

import java.sql.Connection
import java.sql.Statement

import java.sql.ResultSet
import java.sql.ResultSetMetaData

public class VerifLayanan {

	int columnCount

	//fungsi untuk mengambil tenant code dari database
	@Keyword
	getTenantCodefromDB(Connection conn, String email) {
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT tenant_code FROM ms_tenant mt LEFT JOIN ms_useroftenant mot ON mt.id_ms_tenant = mot.id_ms_tenant LEFT JOIN am_msuser amu ON amu.id_ms_user = mot.id_ms_user WHERE login_id = '" + email + "'")

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getListServiceName(Connection conn, String tenantcode) {
		String data

		ArrayList listdata = []

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT description FROM ms_balancevendoroftenant mbt LEFT JOIN ms_lov mlo ON mlo.id_lov = mbt.lov_balance_type LEFT JOIN ms_tenant mt ON mt.id_ms_tenant = mbt.id_ms_tenant WHERE tenant_code = '" + tenantcode + "' AND mlo.code != 'IDR'")

		ResultSetMetaData metadata  = resultSet.metaData

		columnCount = metadata.columnCount

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

		ArrayList listdata = []

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT CASE WHEN mbt.is_active = '1' THEN 'Active' ELSE 'Inactive' END AS status FROM ms_balancevendoroftenant mbt LEFT JOIN ms_lov mlo ON mlo.id_lov = mbt.lov_balance_type LEFT JOIN ms_tenant mt ON mt.id_ms_tenant = mbt.id_ms_tenant WHERE tenant_code = '" + tenantcode + "'")

		ResultSetMetaData metadata  = resultSet.metaData

		columnCount = metadata.columnCount

		while (resultSet.next()) {
			for (int i = 1; i <= columnCount ; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}

	@Keyword
	getListChargeType(Connection conn, String tenantcode) {
		String data

		ArrayList listdata = []

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT description FROM ms_balancevendoroftenant mbt LEFT JOIN ms_lov mlo ON mlo.id_lov = mbt.lov_balance_charge_type LEFT JOIN ms_tenant mt ON mt.id_ms_tenant = mbt.id_ms_tenant WHERE tenant_code = '" + tenantcode + "'")
		ResultSetMetaData metadata  = resultSet.metaData

		columnCount = metadata.columnCount

		while (resultSet.next()) {
			for (int i = 1; i <= columnCount ; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}
}