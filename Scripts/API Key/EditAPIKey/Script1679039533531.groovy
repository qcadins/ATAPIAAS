import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import org.openqa.selenium.Keys
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.By as By
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'klik pada tombol edit API'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/buttonEditAPI'))
	
'klik tombol batal'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/button_Batal'))

'search api yang mau di edit'
searchAPIKEY(rowExcel('SearchStatusAPI'))

'klik pada tombol edit API'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/buttonEditAPI'))

'klik pada panah ddl Status API'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/span_Inactive_ng-arrow-wrapper'))

if (GlobalVariable.KondisiCekDB == 'Yes') {
	'kumpulan string dari data yang diambil langsung dari database'
	ArrayList hasildb = CustomKeywords.'apikey.CheckAPIKey.getAPIStatusfromDB'(conn,
		WebUI.getAttribute(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/input__apiKeyName'), 'value'),
			findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('$Username Login')))

	'ambil text dari UI Web APIAAS'
	ArrayList hasilweb = CustomKeywords.'apikey.CheckAPIKey.getAttributeValueAPI'()

	'verifikasi data pada WEB dan DB sama'
	for (int j = 0; j < hasildb.size(); j++) {
	    checkVerifyEqualOrMatch(WebUI.verifyMatch(hasilweb[j], hasildb[j], false, FailureHandling.CONTINUE_ON_FAILURE), 'Data sebelum Edit tidak sesuai DB')
	}
}

if (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('$Edit Status API')).equalsIgnoreCase('')) {
	GlobalVariable.FlagFailed = 1
	
	'tulis adanya error pada sistem web'
	CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
		GlobalVariable.StatusWarning, (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
			GlobalVariable.FailedReasonMandatory)
} else {
	'klik pada panah ddl Status API'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/span_Inactive_ng-arrow-wrapper'))
	
	'input nama API'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/input__apiKeyName'),
		findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('$Edit Nama API')))
	
	'klik pada panah ddl Status API'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/span_Inactive_ng-arrow-wrapper'))
	
	'get id ddl'
	id = WebUI.getAttribute(findTestObject('Object Repository/Top Up/ddlClass'), 'id', FailureHandling.CONTINUE_ON_FAILURE)
	
	'jika status yang diingkan adalah aktif/inaktif'
	if (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('$Edit Status API')) == 'Active') {
		'modify object DDL'
		modifyObjectDDL = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/input'), 'xpath', 'equals', '//*[@id="' + id + '-0"]', true)
	} else if (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('$Edit Status API')) == 'Inactive') {
		'modify object DDL'
		modifyObjectDDL = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/input'), 'xpath', 'equals', '//*[@id="' + id + '-1"]', true)
	}
	
	'klik status yang dipilih'
	WebUI.click(modifyObjectDDL)
	
	'klik tombol untuk simpan ubahan'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/button_Lanjut'))
	
	'cek jika adanya tombol "YA" pada popup'
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/button_Ya'),
			GlobalVariable.Timeout, FailureHandling.CONTINUE_ON_FAILURE)) {
		'klik pada tombol ya'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/button_Ya'))
	}
	
	'cek apakah muncul error setelah edit api key'
	if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'),
			GlobalVariable.Timeout, FailureHandling.OPTIONAL) == false) {
		'tulis adanya error pada sistem web'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusWarning, (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
				GlobalVariable.FailedReasonUnknown)
	}
	
	WebUI.delay(2)
	
	'periksa status edit dan tulis ke excel'
	CustomKeywords.'writetoexcel.CheckSaveProcess.checkAlert'(GlobalVariable.NumOfColumn, sheet)
	
	'kondisi jika tidak ada tombol ok, tc masih bisa dilanjutkan'
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/button_OK_success'),
			GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		'klik tombol ok'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/button_OK_success'))
		
		'cek ke DB jika memang diperlukan'
		if (GlobalVariable.KondisiCekDB == 'Yes') {
			'verifikasi data ke db setelah di edit'
			WebUI.callTestCase(findTestCase('Test Cases/API Key/APIKeyStoreDB'), [('Case'): 'Edit'],
				FailureHandling.STOP_ON_FAILURE)
		}
		
		if (WebUI.verifyElementPresent(findTestObject('API_KEY/Page_Edit Api Key/button_Batal'),
			GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
			'klik pada tombol batal terlebih dahulu'
			WebUI.click(findTestObject('API_KEY/Page_Edit Api Key/button_Batal'))
		}
		
		searchAPIKEY(rowExcel('$Edit Status API'))

		'panggil fungsi search after edit'
		checkVerifyEqualOrMatch(searchAPIKEYafter(isMandatory), " Search after Add")
		
//		'verify nama api key'
//		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/API_KEY/label_NamaAPIKey')),
//			findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('$Edit Nama API')), false, FailureHandling.CONTINUE_ON_FAILURE), ' nama api key')
//	
//		'verify nama tipe api key'
//		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/API_KEY/label_TipeAPIKey')),
//			findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('SearchTipeAPI')), false, FailureHandling.CONTINUE_ON_FAILURE), ' tipe api key')
	//kondisi untuk tombol ok jika edit error
	} else if (WebUI.verifyElementPresent(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/button_OK_gagal'),
			GlobalVariable.Timeout, FailureHandling.CONTINUE_ON_FAILURE)) {
		'klik tombol ok'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/button_OK_gagal'))
	}
}

'fungsi untuk melakukan pengecekan '
def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
	if ((isMatch == false)) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
			(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') + GlobalVariable.FailedReasonVerifyEqualorMatch + reason)

		GlobalVariable.FlagFailed = 1
	}
}

def searchAPIKEY(int row) {
	'input tipe API'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Api Key List/input_tipeapi_list'), findTestData(
			ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('SearchTipeAPI')))
	
	'select tipe API'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Api Key List/input_tipeapi_list'), Keys.chord(
			Keys.ENTER))
	
	'input status API'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Api Key List/input_statusapi_list'), findTestData(
			ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, row))
	
	'select status API'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Api Key List/input_statusapi_list'), Keys.chord(
			Keys.ENTER))
	
	'klik pada button cari'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/button_Cari'))
}

def searchAPIKEYafter(int isMandatoryComplete) {
	boolean isFound = false
	
	'cari size dari page yang ada'
	elementbuttonskip = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-list-api-key > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))

	for (int row = 0; row < elementbuttonskip.size() - 4; row++) {
		'ambil alamat trxnumber'
		onepage = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-list-api-key > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))

		'banyaknya row table'
		int index = onepage.size()
		
		'mulai perhitungan data service name'
		for (int i = 1; i <= index; i++) {
			'ambil object dari ddl'
			modifyAPIName = WebUI.modifyObjectProperty(findTestObject('Object Repository/LayananSaya/modifytablecontent'),
				'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-api-key/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
				i + ']/datatable-body-row/div[2]/datatable-body-cell[1]/div/p', true)

			'jika nama object sesuai dengan nama saldo'
			if (WebUI.getText(modifyAPIName) == findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('$Edit Nama API'))) {
				isFound = true
				
				'panggil fungsi copy link'
				if (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('Copy API Link?(Yes/No)')) == 'Yes') {
					'ambil object dari ddl'
					modifyCopyLink = WebUI.modifyObjectProperty(findTestObject('Object Repository/LayananSaya/modifytablecontent'),
						'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-api-key/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
						i + ']/datatable-body-row/div[2]/datatable-body-cell[3]/div/a/em', true)
					
					'klik tombol COPY LINK'
					WebUI.click(modifyCopyLink)
						
					'verifikasi copy berhasil'
					CustomKeywords.'writetoexcel.CheckSaveProcess.checkStatus'(isMandatoryComplete,
						findTestObject('Object Repository/API_KEY/Page_Api Key List/notif_CopySuccess'),
							GlobalVariable.NumOfColumn, sheet)
				}
				break
			}
		}
		
		'cek apakah button next enable atau disable'
		if (WebUI.verifyElementVisible(findTestObject('Object Repository/OCR Testing/Page_Balance/i_Catatan_datatable-icon-right'),
			FailureHandling.OPTIONAL) && isFound == 0) {
			'klik button next page'
			WebUI.click(findTestObject('Object Repository/OCR Testing/Page_Balance/i_Catatan_datatable-icon-right'))
		}
		
		'cek jika sudah ketemu, hentikan seluruh loop'
		if (isFound == 1) {
			break
		}
	}
	isFound
}

def rowExcel(String cellValue) {
	CustomKeywords.'writetoexcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}
