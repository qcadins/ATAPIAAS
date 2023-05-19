package userManagement

import com.kms.katalon.core.annotation.Keyword
import java.sql.Connection
import java.sql.Statement
import java.sql.ResultSet
import java.sql.ResultSetMetaData

import groovy.sql.Sql as Sql

import internal.GlobalVariable

public class UserVerif {
	
	//fungsi mengambil jumlah tenant
	@Keyword
	getUserTotal(Connection conn, String email) {

		int data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT count(*) FROM am_msrole WHERE usr_crt = '"+email+"' AND role_name != 'AT-ROLEONE'")

		while(resultSet.next())
		{
			data = resultSet.getObject(1);
		}
		data
	}
}
