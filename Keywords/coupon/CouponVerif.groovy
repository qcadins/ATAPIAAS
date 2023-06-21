package coupon

import com.kms.katalon.core.annotation.Keyword
import java.sql.Connection
import java.sql.Statement
import java.sql.ResultSet
import java.sql.ResultSetMetaData

public class CouponVerif {

	int columnCount

	@Keyword
	getCouponTotal(Connection conn, String startDate, String endDate) {

		int data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT count(*) FROM tr_coupon WHERE coupon_end_date <= '" + endDate + "' AND coupon_start_date >= '" + startDate + "'")

		while (resultSet.next()) {

			data = resultSet.getObject(1);
		}
		data
	}

	@Keyword
	getDetailCoupon(Connection conn, String kodekupon) {

		String data
		ArrayList<String> listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT mt.tenant_name, FLOOR(minimum_payment), redemption_limit FROM tr_coupon tc LEFT JOIN ms_tenant mt ON mt.id_ms_tenant = tc.id_ms_tenant WHERE coupon_code = '" + kodekupon + "'")
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
	getTipeNilaiKuponList(Connection conn) {

		String data
		ArrayList<String> listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT description FROM ms_lov WHERE lov_group = 'COUPON_AMOUNT_TYPE'")
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
	getAddEditCoupon(Connection conn, String kodekupon) {

		String data
		ArrayList<String> listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT mlo.description, coupon_code, DATE(coupon_start_date) as date_start, DATE(coupon_end_date) as date_end,(SELECT mlo.description FROM ms_lov mlo LEFT JOIN tr_coupon tco ON tco.lov_coupon_amount_type = mlo.id_lov WHERE tco.coupon_code = '" + kodekupon + "' LIMIT 1) as coupon_amount_type, FLOOR(coupon_amount) as coupon_amount, coupon_qty, redemption_limit, floor(minimum_payment) as minimum_payment FROM tr_coupon tco LEFT JOIN ms_lov mlo ON mlo.id_lov = tco.lov_coupon_type WHERE tco.coupon_code = '" + kodekupon + "'")
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
	getTipeKuponList(Connection conn) {

		String data
		ArrayList<String> listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT description FROM ms_lov WHERE lov_group = 'COUPON_TYPE'")
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
