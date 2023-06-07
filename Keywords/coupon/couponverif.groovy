package coupon

import com.kms.katalon.core.annotation.Keyword
import java.sql.Connection
import java.sql.Statement
import java.sql.ResultSet
import java.sql.ResultSetMetaData

public class couponverif {

	int columnCount

	@Keyword
	getCouponTotal(Connection conn, String email) {

		int data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT * FROM am_msuser WHERE usr_crt = '" + email + "'")

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
	getAddEditCoupon(Connection conn, String kodekupon) {

		String data
		ArrayList<String> listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT mlo.description, DATE(coupon_start_date) as date_start, DATE(coupon_end_date) as date_end, coupon_code,(SELECT mlo.description FROM ms_lov mlo LEFT JOIN tr_coupon tco ON tco.lov_coupon_amount_type = mlo.id_lov WHERE tco.coupon_code = '" + kodekupon + "' LIMIT 1) as coupon_amount_type, coupon_amount, coupon_qty FROM tr_coupon tco LEFT JOIN ms_lov mlo ON mlo.id_lov = tco.lov_coupon_type WHERE tco.coupon_code = '" + kodekupon + "'")
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
