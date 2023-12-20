package tenant

import com.kms.katalon.core.annotation.Keyword
import java.sql.Connection
import java.sql.Statement
import java.sql.ResultSet
import java.sql.ResultSetMetaData

import groovy.sql.Sql as Sql

public class TenantVerif {

	int columnCount

	//fungsi mengambil jumlah tenant
	@Keyword
	getTenantTotal(Connection conn) {

		int data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select count(*) from esign.ms_tenant")

		while (resultSet.next()) {

			data = resultSet.getObject(1);
		}
		data
	}

	@Keyword
	getTenantStoreDB(Connection conn, String refnum) {
		String data
		ArrayList listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select tenant_name, tenant_code, ref_number_label, api_key, email_reminder_dest from esign.ms_tenant where ref_number_label = '"+ refnum +"'")
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
	getTenantServicesDescription(Connection conn, String tenantcode) {
		String data
		ArrayList listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT ms_lov.description, value FROM esign.ms_tenant CROSS JOIN LATERAL json_each_text(threshold_balance::json) AS threshold_type JOIN esign.ms_lov ON ms_lov.lov_group = 'BALANCE_TYPE' AND ms_lov.code = threshold_type.key WHERE tenant_code = '"+ tenantcode +"'")
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
	getTenantServices(Connection conn, String tenantname) {
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select threshold_balance from esign.ms_tenant where tenant_name = '"+ tenantname +"'")
		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {

			data = resultSet.getObject(1)
		}
		data
	}

	//fungsi mengambil nama sheet yang digunakan
	@Keyword
	getIDPaymentType(Connection conn, String tenantcode, String testedOCR) {
		int data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("Select ml.id_lov FROM esign.ms_balancevendoroftenant mbt join esign.ms_tenant mt on mt.id_ms_tenant = mbt.id_ms_tenant Join esign.ms_lov ml on ml.id_lov = mbt.lov_balance_type Where mt.tenant_code = '"+ tenantcode +"' AND ml.code = '"+testedOCR+"'")

		while (resultSet.next()) {

			data = resultSet.getObject(1);
		}

		data
	}

	//fungsi mengambil jenis penagihan saldo (quantity/price)
	@Keyword
	getPaymentType(Connection conn, String tenantcode, int idPayment) {
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("Select description From esign.ms_lov ml join esign.ms_balancevendoroftenant mbt on ml.id_lov = mbt.lov_balance_charge_type Join esign.ms_tenant mt on mt.id_ms_tenant = mbt.id_ms_tenant Where ml.lov_group = 'BALANCE_CHARGE_TYPE' and mt.tenant_code = '"+ tenantcode +"' AND mbt.lov_balance_type = "+ idPayment +"")

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getActiveTenant(Connection conn, String tenantcode) {
		String data
		ArrayList listdata = []
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT ml.description FROM esign.ms_balancevendoroftenant mbt JOIN esign.ms_lov ml ON mbt.lov_balance_type = ml.id_lov JOIN esign.ms_tenant mt ON mbt.id_ms_tenant = mt.id_ms_tenant JOIN esign.ms_vendor mv ON mv.id_ms_vendor = mbt.id_ms_vendor WHERE tenant_code = '"+ tenantcode +"' AND vendor_name = 'ADINS' AND description != 'IDR'")
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
