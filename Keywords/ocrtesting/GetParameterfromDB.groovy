package ocrtesting

import com.kms.katalon.core.annotation.Keyword
import java.sql.Connection
import java.sql.Statement

import java.sql.ResultSet

public class GetParameterfromDB {

	int columnCount

	//fungsi mengambil nama sheet yang digunakan
	@Keyword
	getIDPaymentType(Connection conn, String tenantcode, String testedOCR) {
		int data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("Select ml.id_lov FROM esign.ms_balancevendoroftenant mbt join esign.ms_tenant mt on mt.id_ms_tenant = mbt.id_ms_tenant Join esign.ms_lov ml on ml.id_lov = mbt.lov_balance_type Where mt.tenant_code = '" + tenantcode + "' AND ml.description ILIKE '" + testedOCR + "'")

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		data
	}

	//fungsi mengambil jenis penagihan saldo (quantity/price)
	@Keyword
	getPaymentType(Connection conn, String tenantcode, int idPayment) {
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("Select description From esign.ms_lov ml join esign.ms_balancevendoroftenant mbt on ml.id_lov = mbt.lov_balance_charge_type Join esign.ms_tenant mt on mt.id_ms_tenant = mbt.id_ms_tenant Where ml.lov_group = 'BALANCE_CHARGE_TYPE' and mt.tenant_code = '" + tenantcode + "' AND mbt.lov_balance_type = " + idPayment + "")

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	//fungsi untuk mengambil KEY dari database
	@Keyword
	getAPIKeyfromDB(Connection conn, String tenantCode, String envi) {
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select api_key_code from ms_tenant mt join ms_api_key amk on amk.id_ms_tenant = mt.id_ms_tenant JOIN ms_lov mlo ON mlo.id_lov = amk.lov_api_key_type where tenant_code = '" + tenantCode + "' AND amk.is_active = '1' AND mlo.description ILIKE '" + envi + "'")

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	//fungsi untuk mengambil jumlah data APIKEY dari database
	@Keyword
	getTenantCodefromDB(Connection conn, String email) {
		String data

		Statement stm = conn.createStatement()
		//join ms_api_key amk on amk.id_ms_tenant = mt.id_ms_tenant
		ResultSet resultSet = stm.executeQuery("select tenant_code from am_msuser amu join ms_useroftenant muot on amu.id_ms_user = muot.id_ms_user join ms_tenant mt on mt.id_ms_tenant = muot.id_ms_tenant where login_id = '" + email + "' LIMIT 1")

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	//fungsi untuk mengambil jumlah data APIKEY dari database
	@Keyword
	getLatestMutationfromDB(Connection conn, String tenantcode) {
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select trx_no from esign.tr_balance_mutation WHERE usr_crt = '" + tenantcode + "' ORDER BY id_balance_mutation DESC limit 1;")

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	//fungsi untuk mengambil jumlah data APIKEY dari database
	@Keyword
	getNotMyLatestMutationfromDB(Connection conn, String tenantcode) {
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select trx_no from esign.tr_balance_mutation WHERE usr_crt != '" + tenantcode + "' ORDER BY id_balance_mutation DESC limit 1;")

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	//fungsi untuk ambil harga service OCR dari DB
	@Keyword
	getServicePricefromDB(Connection conn, int idPayment) {
		int data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT msp.service_price FROM esign.ms_service_price msp WHERE lov_balance_type = " + idPayment + "")

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		data
	}
}