package userManagement

import com.kms.katalon.core.annotation.Keyword
import java.sql.Connection
import java.sql.Statement
import java.sql.ResultSet
import java.sql.ResultSetMetaData

public class UserVerif {
	
	int columnCount

	//fungsi mengambil jumlah tenant
	@Keyword
	getUserTotal(Connection conn, String email) {

		int data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT * FROM am_msuser WHERE usr_crt = '" + email + "'")

		while (resultSet.next()) {
			
			data = resultSet.getObject(1);
		}
		data
	}
	
	@Keyword
	getNewUserData(Connection conn, String email) {

		String data
		ArrayList<String> listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT login_id, initial_name, last_name, aro.role_name FROM am_msuser ase LEFT JOIN am_memberofrole amo ON amo.id_ms_user = ase.id_ms_user LEFT JOIN am_msrole aro ON aro.id_ms_role = amo.id_ms_role WHERE login_id = '" + email + "'")
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