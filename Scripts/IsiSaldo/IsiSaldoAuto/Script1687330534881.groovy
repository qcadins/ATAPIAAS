import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import org.openqa.selenium.By as By
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writetoexcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

Connection conn

if (GlobalVariable.SettingEnvi == 'Production') {
	'deklarasi koneksi ke Database eendigo_dev'
	conn = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_public'()
} else if (GlobalVariable.SettingEnvi == 'Trial') {
	'deklarasi koneksi ke Database eendigo_dev_uat'
	conn = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_devUat'()
}

'panggil fungsi login'
WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'IsiSaldoAuto', ('SheetName') : sheet, ('Path') : ExcelPath,],
    FailureHandling.STOP_ON_FAILURE)

'ambil kode tenant di DB'
String tenantcode = CustomKeywords.'ocrtesting.GetParameterfromDB.getTenantCodefromDB'(conn, findTestData(ExcelPathOCR).getValue(GlobalVariable.NumOfColumn, rowExcel('UsernameLogin')).toUpperCase())

'call setting balance type function'
settingBalanceType()

'click menu garis tiga atau burger'
WebUI.click(findTestObject('Tenant/menu_Burger'))

'click menu isi saldo'
WebUI.click(findTestObject('Tenant/menu_isiSaldo'))

'input nama tenant yang akan digunakan'
inputDDLExact('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tenant', findTestData(
        ExcelPath).getValue(2, 20))

'klik pada input vendor'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input vendor'))

if (GlobalVariable.SettingEnvi == 'Production') {
	'input nama vendor yang akan digunakan'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input vendor'), 'ESIGN/ADINS')
} else if (GlobalVariable.SettingEnvi == 'Trial') {
	'input nama vendor yang akan digunakan'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input vendor'), 'ADINS')
}

'pencet enter pada textbox'
WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input vendor'), Keys.chord(
        Keys.ENTER))

'klik pada input tipe saldo'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tipe saldo'))

'input nama saldo yang akan diisi ulang'
inputDDLExact('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tipe saldo'), tipeSaldo)

'input jumlah saldo yang akan ditambahkan'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input_Tambah Saldo_qty'),
    findTestData(ExcelPath).getValue(2, 21))

'input nomor tagihan untuk proses isi ulang saldo'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input_Nomor Tagihan_refNo'),
    findTestData(ExcelPath).getValue(2, 22))

'input notes/catatan untuk proses isi ulang saldo'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input_Catatan_notes'),
    'Isi Ulang ' + tipeSaldo)

'get current date'
Date currentDate = new Date().format('yyyy-MM-dd')

'input tanggal isi ulang saldo'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input_Tanggal Pembelian_trxDate'),
    currentDate)

'klik di luar textbox agar memunculkan tombol lanjut'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/containerForm'))

'verifikasi button tidak di disable'
if (WebUI.verifyElementHasAttribute(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/button_Lanjut'),
    'disabled', GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
    GlobalVariable.FlagFailed = 1

    'tulis kondisi gagal'
    CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
        (findTestData(ExcelPathOCR).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') + GlobalVariable.FailedReasonMandatory)
} else {
    'klik pada tombol lanjut'
    WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/button_Lanjut'))
}

'klik pada tombol proses isi ulang  saldo'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/button_Ya, proses'))

'jika perlu cek ke DB'
if (GlobalVariable.KondisiCekDB == 'Yes') {
	'ambil nomor transaksi terbaru dari DB'
	noTrxfromDB = CustomKeywords.'apikey.CheckSaldoAPI.getLatestMutation'(conn, tenantcode)
			
	'ambil nomor transaksi terbaru tenant lain'
	noTrxOtherTenant = CustomKeywords.'apikey.CheckSaldoAPI.getLatestMutationOtherTenant'(conn, tenantcode)
	
	'call test case store db'
	WebUI.callTestCase(findTestCase('IsiSaldo/IsiSaldoStoreDB'), [('ExcelPathOCR') : ExcelPathOCR, ('ExcelPathSaldoAPI') : ExcelPath, ('tenant') : tenantcode, ('autoIsiSaldo') : 'Yes', ('tipeSaldo') : tipeSaldo, ('sheet') : sheet],
					FailureHandling.CONTINUE_ON_FAILURE)
}

def settingBalanceType() {
	'click menu garis tiga atau burger'
	WebUI.click(findTestObject('Tenant/menu_Burger'))
	
	'click menu tenant'
	WebUI.click(findTestObject('Tenant/menu_Tenant'))
	
	'click button cari'
	WebUI.click(findTestObject('Tenant/button_Cari'))
	
	'input nama tenant'
	WebUI.setText(findTestObject('Tenant/input_NamaTenant'),
		findTestData(ExcelPath).getValue(2, 20))
	
	'click button cari'
	WebUI.click(findTestObject('Tenant/button_Cari'))
	
	'click button services balance'
	WebUI.click(findTestObject('Tenant/button_chargeType'))
	
	'ambil object check box'
	modifyCheckBox = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/VendorList'),
		'xpath', 'equals', '//*[@id="' + idOCR + '"]', true)
	
	'check if balance type quantity'
	if (findTestData(ExcelPath).getValue(2, 23) == 'Quantity') {
		WebUI.uncheck(modifyCheckBox, FailureHandling.CONTINUE_ON_FAILURE)
	} else if (findTestData(ExcelPath).getValue(2, 23) == 'Price') {
		WebUI.check(modifyCheckBox, FailureHandling.CONTINUE_ON_FAILURE)
	}
	
	'click button simpan'
	WebUI.click(findTestObject('Tenant/ChargeType/button_Simpan'))
}

def getSaldoforTransaction(String namaSaldo) {
    'deklarasi jumlah saldo sekarang'
    int saldoNow

    'cari element dengan nama saldo'
    elementNamaSaldo = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance-prod > div.row.match-height > div > lib-balance-summary > div > div'))

    'lakukan loop untuk cari nama saldo yang ditentukan'
    for (int i = 1; i <= elementNamaSaldo.size(); i++) {
        'cari nama saldo yang sesuai di list saldo'
        TestObject modifyNamaSaldo = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Balance/span_OCR KK'), 
            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[1]/div/lib-balance-summary/div/div[' +
            i) + ']/div/div/div/div/div[1]/span', true)

        'jika nama object sesuai dengan nama saldo'
        if (WebUI.getText(modifyNamaSaldo) == namaSaldo) {
            'ubah alamat jumlah saldo ke kotak saldo yang dipilih'
            TestObject modifySaldoDipilih = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Balance/h3_4,988'), 
                'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[1]/div/lib-balance-summary/div/div[' +
                i) + ']/div/div/div/div/div[1]/h3', true)

            'simpan jumlah saldo sekarang di variabel'
            saldoNow = Integer.parseInt(WebUI.getText(modifySaldoDipilih).replace(',', ''))

            break
        }
    }
    
    'pakai saldo IDR jika lainnya tidak ada'
    if (saldoNow == 0) {
        'simpan jumlah saldo sekarang di variabel'
        saldoNow = Integer.parseInt(WebUI.getText(findTestObject('Object Repository/API_KEY/Page_Balance/h3_4,988')).replace(
                ',', ''))
    }
    
    'kembalikan nilai saldo sekarang'
    saldoNow
}

def filterSaldo() {
    'tunggu webpage load'
    WebUI.delay(4)

    'isi field input tipe saldo'
    inputDDLExact('Object Repository/API_KEY/Page_Balance/inputtipesaldo', findTestData(ExcelPathOCR).getValue(
            GlobalVariable.NumOfColumn, 21))

    'isi field tipe transaksi'
    inputDDLExact('Object Repository/API_KEY/Page_Balance/inputtipetranc'), findTestData(ExcelPath).getValue(
            GlobalVariable.NumOfColumn, 22))

    'klik pada button cari'
    WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/button_Cari'))
}

def verifyTableContent(Connection connection, String tenant) {
    'ambil alamat trxnumber'
    variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance-prod > div.ng-star-inserted > app-msx-paging-v2 > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))

    'banyaknya row table'
    int lastIndex = variable.size()

    'flag jika ada error pada verifikasi'
    int flagError = 1

    'modifikasi object tanggal transaksi'
    TestObject modifytglTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/TglTrx'), 'xpath', 'equals', 
        '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
        lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[1]/div/p', true)

    'modifikasi object kantor'
    TestObject modifyKantor = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/KantorLocation'), 'xpath', 
        'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
        lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[2]/div/p', true)

    'modifikasi object tipe transaksi'
    TestObject modifytipeTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/TipeTrx'), 'xpath', 'equals', 
        '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
        lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[3]/div/p', true)

    'modifikasi object sumber transaksi'
    TestObject modifysumberTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/sumberTrx'), 'xpath', 
        'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
        lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[4]/div/p', true)

    'modifikasi object tenant transaksi'
    TestObject modifytenantTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/TenantTrx'), 'xpath', 
        'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
        lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[5]/div/p', true)

    'modifikasi object trxnumber'
    TestObject modifytrxnumber = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/TrxNumber'), 'xpath', 
        'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
        lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[6]/div/p', true)

    'modifikasi object reference transaksi'
    TestObject modifyrefTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/refTrx'), 'xpath', 'equals', 
        '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
        lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[7]/div/p', true)

    'modifikasi object quantity transaksi'
    TestObject modifyqtyTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Balance/div_500'), 'xpath', 
        'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
        lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[8]/div', true)

    'modifikasi object hasil proses transaksi'
    TestObject modifyprocTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/procTrx'), 'xpath', 'equals', 
        '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
        lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[10]/div/p', true)

    'modifikasi object hasil proses transaksi'
    TestObject modifycatatanTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/catatan'), 'xpath', 
        'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
        lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[11]/div/p', true)

    'ambil data table dari db'
    ArrayList<String> result = CustomKeywords.'apikey.CheckSaldoAPI.getTrialTableContent'(connection, tenant)

    'check status semua match data'
    ArrayList<String> arrayMatch = []

    'kembalikan nomor transaksi'
    int arrayIndex = 0

    'verify tanggal transaksi ui = db'
    checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifytglTrx), (result[arrayIndex++]).replace(
                    '.0', ''), false, FailureHandling.CONTINUE_ON_FAILURE)))

    'verify kantor ui = db'
    checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyKantor), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE)))

    'verify tipe transaksi ui = db'
    checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifytipeTrx), result[arrayIndex++], false,
                FailureHandling.CONTINUE_ON_FAILURE)))

    'verify sumber transaksi ui = db'
    checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifysumberTrx), result[arrayIndex++], false,
                FailureHandling.CONTINUE_ON_FAILURE)))

    'verify tenant transaksi ui = db'
    checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifytenantTrx), result[arrayIndex++], false,
                FailureHandling.CONTINUE_ON_FAILURE)))

    'verify nomor transaksi ui = db'
    checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifytrxnumber), result[arrayIndex++], false,
                FailureHandling.CONTINUE_ON_FAILURE)))

    'verify reference number transaksi ui = db'
    checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyrefTrx), result[arrayIndex++], false,
				FailureHandling.CONTINUE_ON_FAILURE)))

    'verify quantity transaksi ui = db'
    checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyqtyTrx), result[arrayIndex++], false,
				FailureHandling.CONTINUE_ON_FAILURE)))

    'verify hasil proses transaksi ui = db'
    checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyprocTrx), result[arrayIndex++], false,
                FailureHandling.CONTINUE_ON_FAILURE)))

    'verify catatan transaksi ui = db'
    checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifycatatanTrx), result[arrayIndex++], false,
                FailureHandling.CONTINUE_ON_FAILURE)))

    'jika ada verifikasi yang gagal'
    if (arrayMatch.contains(false)) {
        'kembalikan flag error'
        flagError = 0
    }
    
	flagError
}

def checkVerifyEqualOrMatch(Boolean isMatch) {
    if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed, 
            (findTestData(ExcelPathOCR).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') + GlobalVariable.FailedReasonVerifyEqualorMatch)

        GlobalVariable.FlagFailed = 1
    }
}

def getTrxNumber() {
    'ambil alamat trxnumber'
    variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance-prod > div.ng-star-inserted > app-msx-paging-v2 > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))

    'banyaknya row table'
    int lastIndex = variable.size()

    'modifikasi alamat object trxnumber'
    modifytrxnumber = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/TrxNumber'), 'xpath', 
        'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
        lastIndex + ']/datatable-body-row/div[2]/datatable-body-cell[6]/div/p', true)

    'simpan nomor transaction number ke string'
    String noTrx = WebUI.getText(modifytrxnumber)

    'kembalikan nomor transaksi'
    noTrx
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
