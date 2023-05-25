package ocrTesting

import com.kms.katalon.core.annotation.Keyword
import java.sql.Connection
import java.sql.Statement

import java.sql.ResultSet
import java.sql.ResultSetMetaData
import groovy.sql.Sql as Sql

public class GetParameterfromDB {

	int columnCount

	//fungsi mengambil nama sheet yang digunakan
	@Keyword
	getIDPaymentType(Connection conn, String tenantcode, String testedOCR) {
		int data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("Select ml.id_lov FROM esign.ms_balancevendoroftenant mbt join esign.ms_tenant mt on mt.id_ms_tenant = mbt.id_ms_tenant Join esign.ms_lov ml on ml.id_lov = mbt.lov_balance_type Where mt.tenant_code = '" + tenantcode + "' AND ml.description = '" + testedOCR + "'")

		while (resultSet.next()){
			
			data = resultSet.getObject(1);
		}

		data
	}

	//fungsi mengambil jenis penagihan saldo (quantity/price)
	@Keyword
	getPaymentType(Connection conn, String tenantcode, int idPayment) {
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("Select description From esign.ms_lov ml join esign.ms_balancevendoroftenant mbt on ml.id_lov = mbt.lov_balance_charge_type Join esign.ms_tenant mt on mt.id_ms_tenant = mbt.id_ms_tenant Where ml.lov_group = 'BALANCE_CHARGE_TYPE' and mt.tenant_code = '" + tenantcode + "' AND mbt.lov_balance_type = " + idPayment + "")

		while (resultSet.next()){
			
			data = resultSet.getObject(1)
		}
		data
	}

	//fungsi untuk mengambil KEY dari database
	@Keyword
	getAPIKeyfromDB(Connection conn, String tenantcode) {
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT api_key_code FROM ms_api_key mk JOIN ms_tenant mt ON mk.id_ms_tenant = mt.id_ms_tenant JOIN ms_lov mlo ON mlo.id_lov = mk.lov_api_key_type  WHERE mt.tenant_code = '" + tenantcode + "' AND mk.is_active = '1' AND mlo.description = 'TRIAL'")

		while (resultSet.next()){
			
			data = resultSet.getObject(1)
		}
		data
	}

	//fungsi untuk mengambil jumlah data APIKEY dari database
	@Keyword
	getTenantCodefromDB(Connection conn, String email) {
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT tenant_code FROM ms_tenant WHERE email_reminder_dest = '" + email + "'")

		while (resultSet.next()){
			
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

		while (resultSet.next()){
			
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

		while (resultSet.next()){
			
			data = resultSet.getObject(1)
		}
		data
	}

	//fungsi untuk ambil harga service OCR dari DB
	@Keyword
	getServicePricefromDB(Connection conn, int idPayment) {
		int data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT msp.service_price FROM esign.ms_service_price msp WHERE lov_balance_type = " + idPayment + " AND effective_date >= '2023-03-25'")

		while (resultSet.next()){
			
			data = resultSet.getObject(1);
		}

		data
	}
}
