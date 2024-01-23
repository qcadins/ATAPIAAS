package topup

import com.kms.katalon.core.annotation.Keyword
import java.sql.Connection
import java.sql.Statement
import java.sql.ResultSet
import java.sql.ResultSetMetaData

public class TopupVerif {

	int columnCount

	@Keyword
	getDDLTipeSaldo(Connection conn) {
		String data
		ArrayList listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT description FROM ms_lov WHERE lov_group = 'API_KEY_TYPE'")
		ResultSetMetaData metadata = resultSet.metaData

		columnCount = metadata.columnCount

		while (resultSet.next()) {
			for (int i = 1; i <= columnCount; i++) {
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
		ResultSetMetaData metadata = resultSet.metaData

		columnCount = metadata.columnCount

		while (resultSet.next()) {
			for (int i = 1; i <= columnCount; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}

	@Keyword
	getDDLBank(Connection conn) {
		String data
		ArrayList listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT bank_name from ms_account_payment maap LEFT JOIN ms_bank mb ON mb.id_bank = maap.id_bank WHERE maap.is_active = '1'")
		ResultSetMetaData metadata = resultSet.metaData

		columnCount = metadata.columnCount

		while (resultSet.next()) {
			for (int i = 1; i <= columnCount; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}

	@Keyword
	getDDLSaldoactive(Connection conn, String email) {
		String data
		ArrayList listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT ml.description FROM esign.ms_balancevendoroftenant mbt JOIN esign.ms_lov ml ON mbt.lov_balance_type = ml.id_lov JOIN esign.ms_tenant mt ON mbt.id_ms_tenant = mt.id_ms_tenant WHERE email_reminder_dest = '" + email + "' AND mbt.lov_balance_charge_type = 141")
		ResultSetMetaData metadata = resultSet.metaData

		columnCount = metadata.columnCount

		while (resultSet.next()) {
			for (int i = 1; i <= columnCount; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}

	@Keyword
	getServicePrice(Connection conn, String service) {
		int data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT service_price FROM ms_service_price msp LEFT JOIN ms_lov mlo ON mlo.id_lov = msp.lov_balance_type WHERE id_ms_tenant is null AND description = '" + service + "' ORDER BY service_price DESC LIMIT 1")

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getPPNvalue(Connection conn) {
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT gs_value FROM am_generalsetting WHERE gs_code = 'PPN_VALUE'")

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getCouponDetail(Connection conn, String kodekupon) {
		String data
		ArrayList listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT mlo.description,(SELECT mlo.description FROM ms_lov mlo LEFT JOIN tr_coupon tco ON tco.lov_coupon_amount_type = mlo.id_lov WHERE tco.coupon_code = '" + kodekupon + "' LIMIT 1) as coupon_amount_type, FLOOR(coupon_amount) as coupon_amount, redemption_limit, floor(minimum_payment) as minimum_payment FROM tr_coupon tco LEFT JOIN ms_lov mlo ON mlo.id_lov = tco.lov_coupon_type WHERE tco.coupon_code = '" + kodekupon + "'")
		ResultSetMetaData metadata = resultSet.metaData

		columnCount = metadata.columnCount

		while (resultSet.next()) {
			for (int i = 1; i <= columnCount; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}

	@Keyword
	getInstructionDetail(Connection conn, String noTrx) {
		String data
		ArrayList listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT toh.topup_order_number, bank_code, mapt.account_number, mapt.account_name FROM tr_topup_order_d tod LEFT JOIN tr_topup_order_h toh ON toh.id_topup_order_h = tod.id_topup_order_h LEFT JOIN ms_account_payment mapt ON mapt.id_account_payment = toh.id_account_payment LEFT JOIN ms_bank mb ON mb.id_bank = mapt.id_bank WHERE toh.topup_order_number = '" + noTrx + "' ORDER BY topup_order_date DESC LIMIT 1")
		ResultSetMetaData metadata = resultSet.metaData

		columnCount = metadata.columnCount

		while (resultSet.next()) {
			for (int i = 1; i <= columnCount; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}
	@Keyword
	getRiwayatTabelData(Connection conn, String noTrx) {
		String data
		ArrayList listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT toh.topup_order_number, TO_CHAR(tod.dtm_crt::timestamp, 'DD-Mon-YYYY HH24:MI') AS formatted_date, mlo.description, mlov.description,(CASE WHEN toh.status = 0 THEN 'Menunggu Pembayaran' WHEN toh.status = 1 THEN 'Menunggu Verifikasi Pembayaran' WHEN toh.status = 2 THEN 'Transaksi Kadaluarsa' WHEN toh.status = 3 THEN 'Pembayaran berhasil' WHEN toh.status = 4 THEN 'Pembayaran ditolak' END) as status FROM tr_topup_order_d tod LEFT JOIN tr_topup_order_h toh ON toh.id_topup_order_h = tod.id_topup_order_h LEFT JOIN ms_account_payment mapt ON mapt.id_account_payment = toh.id_account_payment LEFT JOIN ms_bank mb ON mb.id_bank = mapt.id_bank LEFT JOIN ms_lov mlo ON mlo.id_lov = toh.lov_api_key_type LEFT JOIN ms_lov mlov ON mlov.id_lov = mapt.lov_payment_method WHERE toh.topup_order_number = '" + noTrx + "' LIMIT 1")
		ResultSetMetaData metadata = resultSet.metaData

		columnCount = metadata.columnCount

		while (resultSet.next()) {
			for (int i = 1; i <= columnCount; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}
	@Keyword
	getRiwayatDetail(Connection conn, String noTrx) {
		String data
		ArrayList listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT ml.description, tod.unit_price, tod.qty, (tod.unit_price * tod.qty) as subtotal FROM tr_topup_order_d tod LEFT JOIN ms_lov ml ON ml.id_lov = tod.lov_balance_type LEFT JOIN tr_topup_order_h toh ON toh.id_topup_order_h = tod.id_topup_order_h WHERE toh.topup_order_number = '" + noTrx + "'")
		ResultSetMetaData metadata = resultSet.metaData

		columnCount = metadata.columnCount

		while (resultSet.next()) {
			for (int i = 1; i <= columnCount; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}
	@Keyword
	getStoreDBTopup1(Connection conn, String noTrx) {
		String data
		ArrayList listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT mlo.description, mlov.description, mb.bank_name FROM tr_topup_order_d tod LEFT JOIN tr_topup_order_h toh ON toh.id_topup_order_h = tod.id_topup_order_h LEFT JOIN ms_account_payment mapt ON mapt.id_account_payment = toh.id_account_payment LEFT JOIN ms_bank mb ON mb.id_bank = mapt.id_bank LEFT JOIN ms_lov mlo ON mlo.id_lov = toh.lov_api_key_type LEFT JOIN ms_lov mlov ON mlov.id_lov = mapt.lov_payment_method LEFT JOIN ms_lov mlove ON mlove.id_lov = tod.lov_balance_type WHERE toh.topup_order_number = '" + noTrx + "' LIMIT 1")
		ResultSetMetaData metadata = resultSet.metaData

		columnCount = metadata.columnCount

		while (resultSet.next()) {
			for (int i = 1; i <= columnCount; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}
	@Keyword
	getStoreDBTopup2(Connection conn, String noTrx) {
		String data
		ArrayList listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT array_to_string(array_agg(ml.description),';') AS descriptions, array_to_string(array_agg(tod.qty),';') AS total_qty FROM tr_topup_order_d tod LEFT JOIN ms_lov ml ON ml.id_lov = tod.lov_balance_type LEFT JOIN tr_topup_order_h toh ON toh.id_topup_order_h = tod.id_topup_order_h WHERE toh.topup_order_number = '" + noTrx + "'")
		ResultSetMetaData metadata = resultSet.metaData

		columnCount = metadata.columnCount

		while (resultSet.next()) {
			for (int i = 1; i <= columnCount; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}
}
