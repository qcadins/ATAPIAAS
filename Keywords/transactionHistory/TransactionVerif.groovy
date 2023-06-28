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
	getRoleofUser(Connection conn, String email) {

		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT role_name FROM am_memberofrole amf LEFT JOIN am_msrole amr ON amr.id_ms_role = amf.id_ms_role LEFT JOIN am_msuser ams ON ams.id_ms_user = amf.id_ms_user WHERE ams.login_id = '" + email + "' LIMIT 1")

		while (resultSet.next()) {

			data = resultSet.getObject(1);
		}
		data
	}

	@Keyword
	getRiwayatDetail(Connection conn, String noTrx) {

		String data
		ArrayList listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT ml.description, tod.unit_price, tod.qty, (tod.unit_price * tod.qty) as subtotal FROM tr_topup_order_d tod LEFT JOIN ms_lov ml ON ml.id_lov = tod.lov_balance_type LEFT JOIN tr_topup_order_h toh ON toh.id_topup_order_h = tod.id_topup_order_h WHERE toh.topup_order_number = '" + noTrx + "'")
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
	getNPWPnumUser(Connection conn, String tenant) {

		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT npwp_no FROM am_msuser amu LEFT JOIN ms_useroftenant mot ON mot.id_ms_user = amu.id_ms_user LEFT JOIN ms_tenant mt ON mot.id_ms_tenant = mt.id_ms_tenant WHERE tenant_name = '" + tenant + "' LIMIT 1")

		while (resultSet.next()) {

			data = resultSet.getObject(1);
		}
		data
	}

	@Keyword
	getServiceCheck(Connection conn, String noTrx) {

		String data
		ArrayList listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT ml.description FROM tr_topup_order_d tod LEFT JOIN ms_lov ml ON ml.id_lov = tod.lov_balance_type LEFT JOIN tr_topup_order_h toh ON toh.id_topup_order_h = tod.id_topup_order_h WHERE toh.topup_order_number = '" + noTrx + "'")
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
	getstatusafterConfirmOrReject(Connection conn, String noTrx) {

		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT CASE WHEN status = 0 THEN 'Menunggu Pembyaran' WHEN status = 1 THEN 'Menunggu Verifikasi Pembayaran' WHEN status = 2 THEN 'Transaksi Kadaluarsa' WHEN status = 3 THEN 'Pembayaran Berhasil' WHEN status = 4 THEN 'Pembayaran Ditolak' END FROM tr_topup_order_h WHERE topup_order_number = '" + noTrx + "'")

		while (resultSet.next()) {

			data = resultSet.getObject(1);
		}
		data
	}
}
