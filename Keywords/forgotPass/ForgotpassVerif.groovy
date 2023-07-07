package forgotPass

import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import java.sql.Connection
import java.sql.Statement

import java.sql.ResultSet
import java.sql.ResultSetMetaData
import org.openqa.selenium.support.ui.Select
import groovy.sql.Sql as Sql

public class ForgotpassVerif {
	
	@Keyword
	getResetCode(Connection conn, String email) {
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT reset_code FROM am_msuser WHERE login_id = '" + email + "'")

		while (resultSet.next()){

			data = resultSet.getObject(1);
		}

		data
	}
}
