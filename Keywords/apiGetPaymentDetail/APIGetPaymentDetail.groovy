package apiGetPaymentDetail

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class APIGetPaymentDetail {
	
	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []
	
	@Keyword
	getPaymentDetailDB(Connection conn, String bankCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select mb.bank_code, mb.bank_name, account_number, account_name from ms_account_payment map join ms_bank mb on map.id_bank = mb.id_bank where bank_code = '"+ bankCode +"' AND map.is_active = '1'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for (i = 1 ; i <= columnCount ; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}
}
