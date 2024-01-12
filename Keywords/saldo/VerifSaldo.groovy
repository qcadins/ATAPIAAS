package saldo

import com.kms.katalon.core.annotation.Keyword
import java.sql.Connection
import java.sql.Statement

import java.sql.ResultSet
import java.sql.ResultSetMetaData

public class VerifSaldo {

	int columnCount

	//fungsi untuk mengambil tenant code dari database
	@Keyword
	getTenantCodefromDB(Connection conn, String email) {
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT tenant_code FROM ms_tenant mt LEFT JOIN ms_useroftenant mot ON mot.id_ms_tenant = mt.id_ms_tenant LEFT JOIN am_msuser amu ON amu.id_ms_user = mot.id_ms_user WHERE login_id = '" + email + "'")

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	def isFileDownloaded(String deleteFile) {
		boolean isDownloaded = false
		File dir = new File(System.getProperty('user.dir') + '\\Download')
		//Getting the list of all the files in the specific directory
		File[] fList = dir.listFiles()
		for (File f : fList) {
			//checking the extension of the file with endsWith method.
			if (f.exists()) {
				if (deleteFile == 'Yes') {
					f.delete()
				}
				isDownloaded = true
			}
		}
		isDownloaded
	}

	@Keyword
	getListActiveBalance(Connection conn, String tenantcode) {
		String data

		ArrayList listdata = []

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT ml.description FROM esign.ms_balancevendoroftenant mbt LEFT JOIN ms_lov mlo ON mlo.id_lov = mbt.lov_balance_charge_type LEFT JOIN ms_lov ml ON mbt.lov_balance_type = ml.id_lov LEFT JOIN ms_tenant mt ON mbt.id_ms_tenant = mt.id_ms_tenant WHERE tenant_code = '" + tenantcode + "' AND mlo.description = 'Quantity'")
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
	getListTipeTransaksi(Connection conn, String tipeSaldo) {
		String data

		ArrayList listdata = []

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT description FROM ms_lov WHERE description LIKE '%Use " + tipeSaldo + "%' OR description LIKE '%Topup " + tipeSaldo + "%' OR description LIKE '%Top Up " + tipeSaldo + "' AND is_active = '1'")
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
	getListTipeSaldo(Connection conn, String tenantcode) {
		String data

		ArrayList listdata = []

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT ml.description FROM esign.ms_balancevendoroftenant mbt JOIN esign.ms_lov ml ON mbt.lov_balance_type = ml.id_lov JOIN esign.ms_tenant mt ON mbt.id_ms_tenant = mt.id_ms_tenant WHERE tenant_code = '" + tenantcode + "' AND mbt.is_active = '1'")
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
	getListKantor(Connection conn, String tenantcode) {
		String data

		ArrayList listdata = []

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT office_name FROM ms_office mo JOIN ms_tenant mt ON mo.id_ms_tenant = mt.id_ms_tenant WHERE tenant_code = '" + tenantcode + "' AND mo.is_active = '1'")
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

	//fungsi untuk mengambil tenant code dari database
	@Keyword
	getCountTotalData(Connection conn, String tenantcode, String tipeSaldo) {
		int data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT COUNT(*) FROM esign.tr_balance_mutation bm JOIN esign.ms_lov ml ON ml.id_lov = bm.lov_balance_type JOIN esign.ms_tenant mt ON mt.id_ms_tenant = bm.id_ms_tenant WHERE tenant_code = '" + tenantcode + "' AND description = '" + tipeSaldo + "' AND trx_date >= '2023-05-01 00:00:00.0'")

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}
	
}