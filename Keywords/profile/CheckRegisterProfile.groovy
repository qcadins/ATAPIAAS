package profile

import java.sql.Connection

import com.kms.katalon.core.annotation.Keyword
import java.sql.Statement
import java.sql.ResultSet
import java.sql.ResultSetMetaData

public class CheckRegisterProfile {

	@Keyword
	checkDBafterRegister(Connection conn, String email) {
		String data
		int columnCount

		ArrayList listdata = []

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT login_id, full_name FROM am_msuser WHERE login_id = '" + email + "'")

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
