package otp

import com.kms.katalon.core.annotation.Keyword
import java.sql.Connection
import java.sql.Statement
import java.sql.ResultSet
import java.sql.ResultSetMetaData

public class GetOTPfromDB {

	//fungsi digunakan untuk mengambil kode otp secara realtime dengan parameter email
	@Keyword
	getOTPforRegister(Connection conn, String email) {
		String data

		int columnCount

		ArrayList<String> listdata = []

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT otp_code FROM tr_otp WHERE login_id = '"+ email +"'")

		ResultSetMetaData metadata  = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for (int i =1; i <= columnCount ; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}
	
}
