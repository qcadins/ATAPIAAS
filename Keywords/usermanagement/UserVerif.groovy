package usermanagement

import com.kms.katalon.core.annotation.Keyword
import java.sql.Connection
import java.sql.Statement
import java.sql.ResultSet
import java.sql.ResultSetMetaData

public class UserVerif {

	int columnCount, updateVariable

	//fungsi mengambil jumlah tenant
	@Keyword
	getUserTotal(Connection conn, String email) {
		int data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT * FROM am_msuser WHERE usr_crt = '" + email + "'")

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}
	@Keyword
	getNewUserData(Connection conn, String email, String emailcreate) {
		String data
		ArrayList listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT login_id, initial_name, last_name, aro.role_name FROM am_msuser ase LEFT JOIN am_memberofrole amo ON amo.id_ms_user = ase.id_ms_user LEFT JOIN am_msrole aro ON aro.id_ms_role = amo.id_ms_role WHERE login_id = '" + email + "' AND ase.usr_crt = '" + emailcreate + "'")
		ResultSetMetaData metadata = resultSet.metaData

		columnCount = metadata.columnCount

		while (resultSet.next()) {
			for (int i = 1 ; i <= columnCount ; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}
	@Keyword
	getEditUserData(Connection conn, String email) {
		String data
		ArrayList listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT ase.login_id, initial_name, last_name, aro.role_name, CASE WHEN ase.is_active = '2' THEN 'Belum verifikasi' WHEN ase.is_active = '1' THEN 'Aktif' Else 'Tidak aktif' END FROM am_msuser ase LEFT JOIN am_memberofrole amo ON amo.id_ms_user = ase.id_ms_user LEFT JOIN am_msrole aro ON aro.id_ms_role = amo.id_ms_role WHERE login_id = '" + email + "'")
		ResultSetMetaData metadata = resultSet.metaData

		columnCount = metadata.columnCount

		while (resultSet.next()) {
			for (int i = 1 ; i <= columnCount ; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}
	@Keyword
	updateIsActiveUser(Connection conn, String email) {
		Statement stm = conn.createStatement()

		updateVariable = stm.executeUpdate("UPDATE am_msuser SET is_active = '1', change_pwd_login = '0', is_verified = '1' WHERE login_id = '" + email + "';")
	}
	@Keyword
	getUserStatus(Connection conn, String email) {
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT case when is_active = '0' THEN 'Tidak Aktif' WHEN is_active = '1' Then 'Aktif' WHEN is_active = '2' Then 'Belum verifikasi' END FROM am_msuser WHERE login_id = '" + email + "'")

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

}
