package topup

import com.kms.katalon.core.annotation.Keyword
import java.sql.Connection
import java.sql.Statement
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import internal.GlobalVariable

public class TopupVerif {

	int columnCount

	@Keyword
	getDDLTipeSaldo(Connection conn) {

		String data
		ArrayList<String> listdata = []
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
		ArrayList<String> listdata = []
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
	getDDLBank(Connection conn) {

		String data
		ArrayList<String> listdata = []
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
	getDDLSaldoactive(Connection conn, String email) {

		String data
		ArrayList<String> listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT ml.description FROM esign.ms_balancevendoroftenant mbt JOIN esign.ms_lov ml ON mbt.lov_balance_type = ml.id_lov JOIN esign.ms_tenant mt ON mbt.id_ms_tenant = mt.id_ms_tenant WHERE email_reminder_dest = '" + email + "'")
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
	getServicePrice(Connection conn, String service) {

		int data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT description, service_price FROM ms_service_price msp LEFT JOIN ms_lov mlo ON mlo.id_lov = msp.lov_balance_type WHERE id_ms_tenant is null AND description = '" + service + "' ORDER BY service_price DESC LIMIT 1")

		while (resultSet.next()) {

			data = resultSet.getObject(1);
		}
		data
	}

	@Keyword
	getPPNvalue(Connection conn) {

		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT gs_value FROM am_generalsetting WHERE gs_code = 'PPN_VALUE'")

		while (resultSet.next()) {

			data = resultSet.getObject(1);
		}
		data
	}

	@Keyword
	getCouponDetail(Connection conn, String kodekupon) {

		String data
		ArrayList<String> listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT mlo.description,(SELECT mlo.description FROM ms_lov mlo LEFT JOIN tr_coupon tco ON tco.lov_coupon_amount_type = mlo.id_lov WHERE tco.coupon_code = '" + kodekupon + "' LIMIT 1) as coupon_amount_type, FLOOR(coupon_amount) as coupon_amount, redemption_limit, floor(minimum_payment) as minimum_payment FROM tr_coupon tco LEFT JOIN ms_lov mlo ON mlo.id_lov = tco.lov_coupon_type WHERE tco.coupon_code = '" + kodekupon + "'")
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