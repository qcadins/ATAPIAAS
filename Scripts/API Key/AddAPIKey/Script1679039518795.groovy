import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys

'klik pada tombol garis tiga'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/spanMenu'))

WebUI.delay(1)

'klik pada API KEY'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/span_API Key'))

'cek apakah tombol menu dalam jangkauan web'
if (WebUI.verifyElementVisible(findTestObject('Object Repository/User Management-Role/Page_List Roles/tombolX_menu'), FailureHandling.OPTIONAL)) {
	'klik pada tombol silang menu'
	WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/tombolX_menu'))
}

'jika merupakan case awal'
if (GlobalVariable.NumOfColumn == 2) {
	'klik tombol +Baru'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/a_Baru'))
	
	'klik tombol batal'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Add Api Key/button_Batal'))
}

'klik tombol +Baru'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/a_Baru'))

'verify element field kosong'
checkVerifyEqualOrMatch(WebUI.verifyElementAttributeValue(findTestObject('Object Repository/API_KEY/Page_Add Api Key/input__apiKeyName'),
	'class', 'form-control ng-untouched ng-pristine ng-invalid',
	GlobalVariable.Timeout, FailureHandling.OPTIONAL), 'Field add tidak kosong setelah klik cancel')

'input data API KEY name'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Add Api Key/input__apiKeyName'), findTestData(ExcelPathAPIKey).getValue(
        GlobalVariable.NumOfColumn, rowExcel('$Nama API KEY')))

'pilih jenis API KEY'
inputDDLExact('Object Repository/API_KEY/Page_Add Api Key/select_tipeAPI', findTestData(ExcelPathAPIKey).getValue(
        GlobalVariable.NumOfColumn, rowExcel('$Tipe API KEY')))

'jika tombol simpan di disabled'
if (WebUI.verifyElementHasAttribute(findTestObject('Object Repository/API_KEY/Page_Add Api Key/button_Simpan'), 'disabled', GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
	'Write to excel status failed mandatory'
	CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') + GlobalVariable.FailedReasonMandatory)
	
	GlobalVariable.FlagFailed = 1
} else {
	'klik pada tombol simpan'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Add Api Key/button_Simpan'))
	
	WebUI.delay(3)
	
	'verifikasi tombol "YA" terdapat di layar'
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/API_KEY/Page_Add Api Key/button_Ya'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		'klik pada button YA jika muncul pop-up'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Add Api Key/button_Ya'))
	}
	
	String reason
	
	'kondisi jika tidak ada tombol ok, tc masih bisa dilanjutkan'
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/API_KEY/Page_Add Api Key/button_OK'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		'get failed reason'
		reason = WebUI.getText(findTestObject('Object Repository/API_KEY/Page_Add Api Key/label_FailedReason'))
		
		'klik tombol ok pada success alert'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Add Api Key/button_OK'))
	}
	
	if (reason == 'Success') {
		'cek ke DB jika memang diperlukan'
		if (GlobalVariable.KondisiCekDB == 'Yes') {
			'verifikasi ke database untuk data yang ditambahkan'
			WebUI.callTestCase(findTestCase('Test Cases/API Key/APIKeyStoreDB'), [('Case'): 'Add'], FailureHandling.STOP_ON_FAILURE)
		}
		
		'cek apakah muncul error setelah add api key'
		if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'), GlobalVariable.Timeout,
			FailureHandling.OPTIONAL) == false) {
			'tulis adanya error pada sistem web'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, GlobalVariable.StatusWarning,
				(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') + GlobalVariable.FailedReasonUnknown)
		}
		
		checkVerifyEqualOrMatch(searchAPIKEY(isMandatory), " Search after Add")
		
//		'verify nama api key'
//		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/API_KEY/label_NamaAPIKey')), findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('$Nama API KEY')), false, FailureHandling.CONTINUE_ON_FAILURE), ' nama api key')
//	
//		'verify nama tipe api key'
//		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/API_KEY/label_TipeAPIKey')), findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('$Tipe API KEY')), false, FailureHandling.CONTINUE_ON_FAILURE), ' tipe api key')
	} else {
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') + '<' + reason + '>')
		
		GlobalVariable.FlagFailed = 1
	}
}

'fungsi untuk melakukan pengecekan '
def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
	if ((isMatch == false)) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
			(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') + GlobalVariable.FailedReasonVerifyEqualorMatch + + '<' + reason + '>')

		GlobalVariable.FlagFailed = 1
	}
}

def searchAPIKEY(int isMandatoryComplete) {
//	'input tipe API'
//	inputDDLExact('Object Repository/API_KEY/Page_Api Key List/input_tipeapi_list', findTestData(
//			ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('$Tipe API KEY')))
	
//	'input status API'
//	inputDDLExact('Object Repository/API_KEY/Page_Api Key List/input_statusapi_list', findTestData(
//			ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('$SearchStatusAPI')))
//
//	'klik pada button cari'
//	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/button_Cari'))
	
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
			if (WebUI.getText(modifyAPIName) == findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, rowExcel('$Nama API KEY'))) {
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

def inputDDLExact(String locationObject, String input) {
	'Input value status'
	WebUI.setText(findTestObject(locationObject), input)

	if (input != '') {
		WebUI.click(findTestObject(locationObject))

		'get token unik'
		tokenUnique = WebUI.getAttribute(findTestObject(locationObject), 'aria-owns')

		'modify object label Value'
		modifyObjectGetDDLFromToken = WebUI.modifyObjectProperty(findTestObject('Saldo/Page_Balance/modifybuttonpage'), 'xpath',
			'equals', ('//*[@id="' + tokenUnique) + '"]/div/div[2]', true)

		DDLFromToken = WebUI.getText(modifyObjectGetDDLFromToken)

		for (i = 0; i < DDLFromToken.split('\n', -1).size(); i++) {
			if ((DDLFromToken.split('\n', -1)[i]).toString().toLowerCase() == input.toString().toLowerCase()) {
				modifyObjectClicked = WebUI.modifyObjectProperty(findTestObject('Saldo/Page_Balance/modifybuttonpage'), 'xpath',
					'equals', ((('//*[@id="' + tokenUnique) + '"]/div/div[2]/div[') + (i + 1)) + ']', true)

				WebUI.click(modifyObjectClicked)

				break
			}
		}
	} else {
		WebUI.click(findTestObject(locationObject))

		WebUI.sendKeys(findTestObject(locationObject), Keys.chord(Keys.ENTER))
	}
}