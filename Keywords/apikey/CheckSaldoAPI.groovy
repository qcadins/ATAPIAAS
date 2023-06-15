package apikey

import com.kms.katalon.core.annotation.Keyword
import java.sql.Connection
import java.sql.Statement
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.ResultSetMetaData

import groovy.sql.Sql as Sql
import org.openqa.selenium.WebElement

public class CheckSaldoAPI {

	int columnCount

	//fungsi mengambil jumlah tenant
	@Keyword
	getTenantName(Connection conn) {
		String data

		ArrayList listdata = []

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT tenant_name FROM esign.ms_tenant WHERE is_active = '1'")
		ResultSetMetaData metadata  = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while(resultSet.next()) {
			for(int i=1; i<=columnCount ; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}

	//fungsi mengambil nama vendor dari DB
	@Keyword
	getVendorName(Connection conn, String tenant) {
		String data

		ArrayList listdata = []

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT mv.vendor_name FROM esign.ms_vendor mv JOIN esign.ms_vendoroftenant mot ON mot.id_ms_vendor = mv.id_ms_vendor JOIN esign.ms_tenant mt ON mt.id_ms_tenant = mot.id_ms_tenant WHERE mt.tenant_code = '"+ tenant +"'")
		ResultSetMetaData metadata  = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while(resultSet.next()) {
			for(int i=1; i<=columnCount ; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}

	//fungsi mengambil nama saldo yang diaktifkan user
	@Keyword
	getNamaTipeSaldo(Connection conn, String tenant) {
		String data

		ArrayList listdata = []

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT mlo.description FROM esign.ms_lov mlo JOIN esign.ms_balancevendoroftenant mb ON mb.lov_balance_type = mlo.id_lov JOIN esign.ms_tenant mt ON mb.id_ms_tenant = mt.id_ms_tenant WHERE mt.tenant_code = '"+ tenant +"'")
		ResultSetMetaData metadata  = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while(resultSet.next()) {
			for(int i=1; i<=columnCount ; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}

	//fungsi mengambil jumlah saldo yang diaktifkan user
	@Keyword
	getLatestMutation(Connection conn, String tenant) {
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select trx_no from esign.tr_balance_mutation bm JOIN esign.ms_tenant mt ON mt.id_ms_tenant = bm.id_ms_tenant WHERE tenant_code = '"+ tenant +"' ORDER BY id_balance_mutation DESC limit 1;")

		while(resultSet.next())
		{
			data = resultSet.getObject(1);
		}

		data
	}

	//fungsi mengambil jumlah saldo yang diaktifkan user
	@Keyword
	getLatestMutationOtherTenant(Connection conn, String tenant) {
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select trx_no from esign.tr_balance_mutation bm JOIN esign.ms_tenant mt ON mt.id_ms_tenant = bm.id_ms_tenant WHERE tenant_code != '"+ tenant +"' ORDER BY id_balance_mutation DESC limit 1;")

		while(resultSet.next())
		{
			data = resultSet.getObject(1);
		}

		data
	}

	//fungsi mengambil nama saldo yang diaktifkan user
	@Keyword
	getTrialTableContent(Connection conn, String tenant) {
		String data

		ArrayList listdata = []

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT bm.trx_date, COALESCE(mo.office_name,''), coalesce(ml.description,''), coalesce(bm.trx_source,''), CASE WHEN bm.usr_crt = 'ADMESIGN' THEN 'ADMIN ESIGN' ELSE 'Q' END AS usrtrx, bm.trx_no, coalesce(bm.ref_no,''), ABS(bm.qty), COALESCE(( SELECT ml.code FROM esign.tr_balance_mutation bm2 LEFT JOIN esign.ms_lov ml ON ml.id_lov = bm2.lov_process_result WHERE bm2.id_ms_tenant = bm.id_ms_tenant ORDER BY bm2.id_balance_mutation DESC LIMIT 1), '') AS code, COALESCE(bm.notes, '') AS notes FROM esign.tr_balance_mutation bm JOIN esign.ms_tenant mt ON mt.id_ms_tenant = bm.id_ms_tenant LEFT JOIN esign.ms_office mo ON bm.id_ms_office = mo.id_ms_office JOIN esign.ms_lov ml ON ml.id_lov = bm.lov_trx_type WHERE mt.tenant_code = '"+ tenant +"' ORDER BY bm.id_balance_mutation DESC LIMIT 1;")
		ResultSetMetaData metadata  = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while(resultSet.next()) {
			for(int i=1; i<=columnCount ; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}

	@Keyword
	getIsiSaldoStoreDB(Connection conn, String tenant) {
		String data
		ArrayList listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT tenant_name, vendor_name, description, qty, ref_no, notes, to_char(trx_date, 'yyyy-mm-dd') FROM esign.tr_balance_mutation tbm JOIN esign.ms_tenant mt ON mt.id_ms_tenant = tbm.id_ms_tenant JOIN esign.ms_vendor mv ON mv.id_ms_vendor = tbm.id_ms_vendor JOIN esign.ms_lov ml ON ml.id_lov = tbm.lov_balance_type WHERE tenant_code = '"+ tenant +"' ORDER BY tbm.id_balance_mutation DESC LIMIT 1")
		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for(int i = 1 ; i <= columnCount ; i++){
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}
}
