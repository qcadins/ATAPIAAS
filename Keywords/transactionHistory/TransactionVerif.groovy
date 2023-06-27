package transactionHistory

import com.kms.katalon.core.annotation.Keyword
import java.sql.Connection
import java.sql.Statement
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import internal.GlobalVariable

public class TransactionVerif {
	
	int columnCount
	
	@Keyword
	getTotalTrx(Connection conn, String email) {

		int data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT count(id_topup_order_h) FROM am_msuser amu LEFT JOIN ms_useroftenant muot on amu.id_ms_user = muot.id_ms_user LEFT join ms_tenant mt on mt.id_ms_tenant = muot.id_ms_tenant LEFT JOIN tr_topup_order_h toh on toh.id_ms_tenant = mt.id_ms_tenant WHERE login_id = '" + email + "'")

		while (resultSet.next()) {

			data = resultSet.getObject(1);
		}
		data
	}
	
	@Keyword
	getTenantList(Connection conn) {

		String data
		ArrayList<String> listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT tenant_name FROM ms_tenant WHERE is_active = '1'")
		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for (int i = 1 ; i <= columnCount ; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}
	
	@Keyword
	getDDLTipeIsiUlang(Connection conn) {

		String data
		ArrayList listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT description FROM ms_lov WHERE lov_group = 'API_KEY_TYPE'")
		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for (int i = 1 ; i <= columnCount ; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}
	
	@Keyword
	getDDLMetodeTrf(Connection conn) {

		String data
		ArrayList listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT description FROM ms_lov WHERE lov_group = 'PAYMENT_METHOD'")
		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for (int i = 1 ; i <= columnCount ; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}
	
	@Keyword
	getRoleUser(Connection conn, String email) {

		String data
		ArrayList listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT role_name FROM am_memberofrole amf LEFT JOIN am_msrole amr ON amr.id_ms_role = amf.id_ms_role LEFT JOIN am_msuser ams ON ams.id_ms_user = amf.id_ms_user WHERE ams.login_id = '" + email + "'")
		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for (int i = 1 ; i <= columnCount ; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}
}