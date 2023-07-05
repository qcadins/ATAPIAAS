package userManagement

import com.kms.katalon.core.annotation.Keyword
import java.sql.Connection
import java.sql.Statement
import java.sql.ResultSet
import java.sql.ResultSetMetaData

import groovy.sql.Sql as Sql

import internal.GlobalVariable

public class RoleVerif {

	int columnCount

	//fungsi mengambil jumlah tenant
	@Keyword
	getRoleTotal(Connection conn, String email) {

		int data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT count(amr.role_name) FROM am_msuser amu JOIN ms_useroftenant mot ON amu.id_ms_user = mot.id_ms_user LEFT JOIN ms_tenant mt ON mt.id_ms_tenant = mot.id_ms_tenant LEFT JOIN am_msrole amr ON amr.id_ms_tenant = mt.id_ms_tenant WHERE login_id = '" + email + "' AND role_name != 'AT-ROLEONE'")

		while(resultSet.next()) {

			data = resultSet.getObject(1)
		}
		data
	}

	//fungsi mengambil jumlah tenant
	@Keyword
	getNamaRole(Connection conn, String namarole) {

		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT role_name FROM am_msrole WHERE role_name = '" + namarole + "' LIMIT 1;")

		while (resultSet.next()) {

			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getRoleEdit(Connection conn, String namarole){
		String data
		ArrayList listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT role_name, CASE WHEN is_active = '1' THEN 'Active' ELSE 'Inactive' END FROM am_msrole WHERE role_name = '" + namarole + "' LIMIT 1;")
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
	getRoleMenu(Connection conn, String roleName, String email){
		String data
		ArrayList listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT menu_code FROM am_msmenu mn LEFT JOIN am_menuofrole mor ON mor.id_ms_menu = mn.id_ms_menu LEFT JOIN am_msrole msr ON msr.id_ms_role = mor.id_ms_role WHERE msr.role_name = '" + roleName + "' AND msr.usr_crt = '" + email + "'")
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
	getDataRolebeforeVerif(Connection conn, String roleName){
		String data
		ArrayList listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT role_name, CASE WHEN is_active = '1' THEN 'Active' ELSE 'Inactive' END FROM am_msrole WHERE role_name = '" + roleName + "'")
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
