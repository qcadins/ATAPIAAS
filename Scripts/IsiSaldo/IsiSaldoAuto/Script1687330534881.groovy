import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import org.openqa.selenium.By as By
import org.openqa.selenium.WebDriver as WebDriver
import java.sql.Connection as Connection
import java.sql.Driver as Driver
import org.openqa.selenium.JavascriptExecutor as JavascriptExecutor
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Isi Saldo'
int countColumnEdit = findTestData(ExcelPath).getColumnNumbers()

'deklarasi variabel untuk konek ke Database eendigo_dev'
Connection conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_public'()

'deklarasi koneksi ke Database adins_apiaas_uat'
Connection conndevUAT = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_devUat'()

'panggil fungsi login'
WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'IsiSaldoAuto', ('SheetName') : sheet, ('Path') : ExcelPath], 
    FailureHandling.STOP_ON_FAILURE)

'declare variable int dan flag apakah topup masuk ke tenant yang benar'
int Saldobefore, Saldoafter, JumlahTopUp, TopupSaldoCorrectTenant = 1

'delcare variable string'
String noTrxfromUI, noTrxfromDB, noTrxOtherTenant

'ambil kode tenant di DB'
String tenantcode = CustomKeywords.'ocrTesting.GetParameterfromDB.getTenantCodefromDB'(conn, findTestData(ExcelPathOCR).getValue(2, 28).toUpperCase())

'call setting balance type function'
settingBalanceType()

'click menu garis tiga atau burger'
WebUI.click(findTestObject('Tenant/menu_Burger'))

'click menu isi saldo'
WebUI.click(findTestObject('Tenant/menu_isiSaldo'))

'input nama tenant yang akan digunakan'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tenant'), findTestData(
        ExcelPath).getValue(2, 20))

'pencet enter pada textbox'
WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tenant'), Keys.chord(
        Keys.ENTER))

'klik pada input vendor'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input vendor'))

'input nama vendor yang akan digunakan'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input vendor'), findTestData(
        ExcelPath).getValue(2, 21))

'pencet enter pada textbox'
WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input vendor'), Keys.chord(
        Keys.ENTER))

'klik pada input tipe saldo'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tipe saldo'))

'input nama saldo yang akan diisi ulang'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tipe saldo'), tipeSaldo)

'pencet enter pada textbox'
WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input tipe saldo'), 
    Keys.chord(Keys.ENTER))

'input jumlah saldo yang akan ditambahkan'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input_Tambah Saldo_qty'), 
    findTestData(ExcelPath).getValue(2, 22))

'input nomor tagihan untuk proses isi ulang saldo'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input_Nomor Tagihan_refNo'), 
    findTestData(ExcelPath).getValue(2, 23))

'input notes/catatan untuk proses isi ulang saldo'
WebUI.setText(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/input_Catatan_notes'), 
    'Isi Ulang ' + tipeSaldo)

'get current date'
def currentDate = new Date().format('yyyy-MM-dd')

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
    CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed, 
        (findTestData(ExcelPathOCR).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.FailedReasonMandatory)

} else {
    'klik pada tombol lanjut'
    WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/button_Lanjut'))
}

'klik pada tombol proses isi ulang  saldo'
WebUI.click(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/button_Ya, proses'))

'cek apakah muncul error unknown setelah login'
if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'),
	GlobalVariable.Timeout, FailureHandling.OPTIONAL) == false) {
	
	GlobalVariable.FlagFailed = 1
	
	'tulis adanya error pada sistem web'
	CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
		GlobalVariable.StatusWarning, (findTestData(ExcelPathOCR).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
			GlobalVariable.FailedReasonUnknown)
}

//'ambil jumlah saldo pada menu trial'
//Saldoafter = getSaldoforTransaction(findTestData(ExcelPath).getValue(GlobalVariable.NumOfColumn, 15))
//
//'filter saldo sesuai kebutuhan user'
//filterSaldo()
//
//'scroll ke bawah halaman'
//WebUI.scrollToElement(findTestObject('Object Repository/API_KEY/Page_Balance/i_Catatan_datatable-icon-skip'), GlobalVariable.Timeout)
//
//'cek apakah button skip enable atau disable'
//if(WebUI.verifyElementVisible(findTestObject('Object Repository/API_KEY/Page_Balance/i_Catatan_datatable-icon-skip'), FailureHandling.OPTIONAL)){
//	
//	'klik button skip to last page'
//	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/i_Catatan_datatable-icon-skip'))
//}
//
//'ambil nomor transaksi terakhir di tabel'
//noTrxfromUI = getTrxNumber()

'jika perlu cek ke DB'
if (GlobalVariable.KondisiCekDB == 'Yes') {
	'ambil nomor transaksi terbaru dari DB'
	noTrxfromDB = CustomKeywords.'apikey.CheckSaldoAPI.getLatestMutation'(conndevUAT, tenantcode)
			
	'ambil nomor transaksi terbaru tenant lain'
	noTrxOtherTenant = CustomKeywords.'apikey.CheckSaldoAPI.getLatestMutationOtherTenant'(conndevUAT, tenantcode)
			
	println(tenantcode)
	
	'call test case store db'
	WebUI.callTestCase(findTestCase('IsiSaldo/IsiSaldoStoreDB'), [('ExcelPathOCR') : ExcelPathOCR, ('ExcelPathSaldoAPI') : ExcelPath, ('tenant') : tenantcode, ('autoIsiSaldo') : 'Yes', ('tipeSaldo') : tipeSaldo, ('sheet') : sheet],
					FailureHandling.CONTINUE_ON_FAILURE)
			
//	'cek apakah transaksi tercatat, memastikan tenant lain tidak memiliki transaksi yang sama'
//	if (noTrxfromDB != noTrxfromUI || noTrxfromDB == noTrxOtherTenant) {
//				
//		'topup dianggap gagal'
//		TopupSaldoCorrectTenant = 0
//	} else {
//		'jika ada konten pada tabel yang tidak sesuai dengan DB'
//		if (verifyTableContent(conndevUAT, tenantcode) == 0) {
//			'topup dianggap gagal'
//			TopupSaldoCorrectTenant = 0
//		}
//	}
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
	if (findTestData(ExcelPath).getValue(2, 24) == 'Quantity') {
		WebUI.uncheck(modifyCheckBox, FailureHandling.CONTINUE_ON_FAILURE)
	} else if (findTestData(ExcelPath).getValue(2, 24) == 'Price') {
		WebUI.check(modifyCheckBox, FailureHandling.CONTINUE_ON_FAILURE)
	}
	
	'click button simpan'
	WebUI.click(findTestObject('Tenant/ChargeType/button_Simpan'))
}

def checkTenantcount(def connection) {
    'ambil list tenant'
    def elementjumlahlisttenant = DriverFactory.getWebDriver().findElements(By.xpath('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-topup-balance/div[2]/div/div/div/div/form/div[1]/div[1]/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div'))

    'ambil hitungan tenant yang ada'
    int countWeb = elementjumlahlisttenant.size() - 1

    'flag apakah tenant sesuai pada verifikasi'
    int isTenantMatch = 1

    'ambil nama vendor dari DB'
    ArrayList<String> namatenantDB = CustomKeywords.'apikey.CheckSaldoAPI.getTenantName'(connection)

    'nama-nama tipe saldo sedang aktif dari UI'
    ArrayList<String> namatenantUI = []

    'ambil hitungan tenant dari DB'
    int countDB = namatenantDB.size()

    'jika hitungan di UI dan DB sesuai'
    if (countWeb == countDB) {
    } else if ((countWeb != countDB) || (isTenantMatch == 0)) {
        GlobalVariable.FlagFailed = 1

        'Write to excel status failed and reason topup failed'
        CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed, 
            (findTestData(ExcelPathOCR).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.FailedReasonVerifyEqualorMatch)
    }
}

def checkVendorcount(def connection, def tenantcode) {
    'ambil list vendor'
    def elementjumlahlistvendor = DriverFactory.getWebDriver().findElements(By.xpath('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-topup-balance/div[2]/div/div/div/div/form/div[1]/div[2]/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div'))

    'ambil hitungan vendor yang ada'
    int countWeb = elementjumlahlistvendor.size() - 1

    'flag vendor sesuai'
    int isVendorFound = 0

    'ambil nama vendor dari DB'
    ArrayList<String> namaVendorDB = CustomKeywords.'apikey.CheckSaldoAPI.getVendorName'(connection, tenantcode)

    'nama-nama tipe saldo sedang aktif dari UI'
    ArrayList<String> namaVendorUI = []

    'hitung banyak data didalam array DB'
    int countDB = namaVendorDB.size()

    'jika hitungan di UI dan DB sesuai'
    if (countWeb == countDB) {
        for (int i = 1; i <= countWeb; i++) {
            'ambil object dari ddl'
            def modifyNamaVendor = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/VendorList'), 
                'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-topup-balance/div[2]/div/div/div/div/form/div[1]/div[2]/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div[' + 
                (i + 1)) + ']/span', true)

            'tambahkan nama tipe saldo ke array'
            String data = WebUI.getText(modifyNamaVendor)

            namaVendorUI.add(data)
        }
        
        'cek setiap data di UI dengan data di DB sebagai pembanding'
        for (String tipe : namaVendorDB) {
            'jika ada data yang tidak terdapat pada arraylist yang lain'
            if (!(namaVendorUI.contains(tipe))) {
                'ada data yang tidak match'
                isVendorFound = 0

                'berhentikan loop'
                break
            }
            
            'kondisi ini bisa ditemui jika data match'
            isVendorFound = 1
        }
    } else if ((isVendorFound == 0) || (countWeb != countDB)) {
        GlobalVariable.FlagFailed = 1

        'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
        CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed, 
            (findTestData(ExcelPathOCR).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.FailedReasonVerifyEqualorMatch)
    }
}

def checkTipeSaldocount(def connection, def tenantcode) {
    'ambil list tipe saldo'
    def elementjumlahtipe = DriverFactory.getWebDriver().findElements(By.xpath('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-topup-balance/div[2]/div/div/div/div/form/div[1]/div[3]/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div'))

    'flag data yang match'
    int isDataMatch = 0

    'ambil hitungan vendor yang ada'
    int countWeb = elementjumlahtipe.size() - 1

    'nama-nama tipe saldo yang sedang aktif dari DB'
    ArrayList<String> namaTipefromDB = CustomKeywords.'apikey.CheckSaldoAPI.getNamaTipeSaldo'(connection, tenantcode)

    'nama-nama tipe saldo sedang aktif dari UI'
    ArrayList<String> namaTipefromUI = []

    'ambil ukuran array dari db'
    int countDB = namaTipefromDB.size()

    'jika hitungan di UI dan DB sesuai lakukan pengecekan'
    if (countWeb == countDB) {
        'loop untuk tambah data ke array from UI'
        for (int i = 1; i <= countWeb; i++) {
            'ambil object dari ddl'
            def modifyNamaTipe = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_eSignHub - Adicipta Inovasi Teknologi/TipeSaldoList'), 
                'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-topup-balance/div[2]/div/div/div/div/form/div[1]/div[3]/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div[' + 
                (i + 1)) + ']/span', true)

            'tambahkan nama tipe saldo ke array'
            String data = WebUI.getText(modifyNamaTipe)

            namaTipefromUI.add(data)
        }
        
        'cek setiap data di UI dengan data di DB sebagai pembanding'
        for (String tipe : namaTipefromDB) {
            'jika ada data yang tidak terdapat pada arraylist yang lain'
            if (!(namaTipefromUI.contains(tipe))) {
                'ada data yang tidak match'
                isDataMatch = 0

                'berhentikan loop'
                break
            }
            
            'kondisi ini bisa ditemui jika data match'
            isDataMatch = 1
        }
    } else if ((countWeb != countDB) || (isDataMatch == 0)) {
        GlobalVariable.FlagFailed = 1

        'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
        CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed, 
            (findTestData(ExcelPathOCR).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.FailedReasonVerifyEqualorMatch)
    }
}

def navigatetoeendigoBeta() {
    'buka website APIAAS SIT, data diambil dari TestData Login'
    WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 2))

    'isi username dengan email yang terdaftar'
    WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_Buat Akun_form-control ng-untouched n_ab9ed8'), 
        findTestData(ExcelPath).getValue(2, 11))

    'isi password yang sesuai'
    WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/input_Buat Akun_form-control ng-untouched n_dd86a2'), 
        findTestData(ExcelPath).getValue(2, 12))

    'ceklis pada reCaptcha'
    WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/div_reCAPTCHA_recaptcha-checkbox-border (4)'))

    'pada delay, lakukan captcha secara manual'
    WebUI.delay(10)

    'klik pada button login'
    WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))

    'cek apakah muncul error unknown setelah login'
    if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'), GlobalVariable.Timeout, 
        FailureHandling.OPTIONAL) == false) {
        GlobalVariable.FlagFailed = 1

        'tulis adanya error pada sistem web'
        CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, GlobalVariable.StatusWarning, 
            (findTestData(ExcelPathOCR).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.FailedReasonUnknown)
    }
}

def getSaldoforTransaction(String NamaSaldo) {
    'deklarasi jumlah saldo sekarang'
    int saldoNow

    'cari element dengan nama saldo'
    def elementNamaSaldo = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance-prod > div.row.match-height > div > lib-balance-summary > div > div'))

    'lakukan loop untuk cari nama saldo yang ditentukan'
    for (int i = 1; i <= elementNamaSaldo.size(); i++) {
        'cari nama saldo yang sesuai di list saldo'
        def modifyNamaSaldo = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Balance/span_OCR KK'), 
            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[1]/div/lib-balance-summary/div/div[' + 
            i) + ']/div/div/div/div/div[1]/span', true)

        'jika nama object sesuai dengan nama saldo'
        if (WebUI.getText(modifyNamaSaldo) == NamaSaldo) {
            'ubah alamat jumlah saldo ke kotak saldo yang dipilih'
            def modifySaldoDipilih = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Balance/h3_4,988'), 
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
    return saldoNow
}

def filterSaldo() {
    'tunggu webpage load'
    WebUI.delay(4)

    'isi field input tipe saldo'
    WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'), findTestData(ExcelPath).getValue(
            GlobalVariable.NumOfColumn, 21))

    'pencet enter'
    WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'), Keys.chord(Keys.ENTER))

    'isi field tipe transaksi'
    WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipetranc'), findTestData(ExcelPath).getValue(
            GlobalVariable.NumOfColumn, 22))

    'pencet enter'
    WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipetranc'), Keys.chord(Keys.ENTER))

    'klik pada button cari'
    WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/button_Cari'))
}

def verifyTableContent(def connection, String tenant) {
    'ambil alamat trxnumber'
    def variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance-prod > div.ng-star-inserted > app-msx-paging-v2 > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))

    'banyaknya row table'
    int lastIndex = variable.size()

    'flag jika ada error pada verifikasi'
    int flagError = 1

    'modifikasi object tanggal transaksi'
    def modifytglTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/TglTrx'), 'xpath', 'equals', 
        ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        lastIndex) + ']/datatable-body-row/div[2]/datatable-body-cell[1]/div/p', true)

    'modifikasi object kantor'
    def modifyKantor = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/KantorLocation'), 'xpath', 
        'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        lastIndex) + ']/datatable-body-row/div[2]/datatable-body-cell[2]/div/p', true)

    'modifikasi object tipe transaksi'
    def modifytipeTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/TipeTrx'), 'xpath', 'equals', 
        ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        lastIndex) + ']/datatable-body-row/div[2]/datatable-body-cell[3]/div/p', true)

    'modifikasi object sumber transaksi'
    def modifysumberTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/sumberTrx'), 'xpath', 
        'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        lastIndex) + ']/datatable-body-row/div[2]/datatable-body-cell[4]/div/p', true)

    'modifikasi object tenant transaksi'
    def modifytenantTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/TenantTrx'), 'xpath', 
        'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        lastIndex) + ']/datatable-body-row/div[2]/datatable-body-cell[5]/div/p', true)

    'modifikasi object trxnumber'
    def modifytrxnumber = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/TrxNumber'), 'xpath', 
        'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        lastIndex) + ']/datatable-body-row/div[2]/datatable-body-cell[6]/div/p', true)

    'modifikasi object reference transaksi'
    def modifyrefTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/refTrx'), 'xpath', 'equals', 
        ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        lastIndex) + ']/datatable-body-row/div[2]/datatable-body-cell[7]/div/p', true)

    'modifikasi object quantity transaksi'
    def modifyqtyTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Balance/div_500'), 'xpath', 
        'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        lastIndex) + ']/datatable-body-row/div[2]/datatable-body-cell[8]/div', true)

    'modifikasi object hasil proses transaksi'
    def modifyprocTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/procTrx'), 'xpath', 'equals', 
        ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        lastIndex) + ']/datatable-body-row/div[2]/datatable-body-cell[10]/div/p', true)

    'modifikasi object hasil proses transaksi'
    def modifycatatanTrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/catatan'), 'xpath', 
        'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        lastIndex) + ']/datatable-body-row/div[2]/datatable-body-cell[11]/div/p', true)

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
    checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyrefTrx), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE)))

    'verify quantity transaksi ui = db'
    checkVerifyEqualOrMatch(arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyqtyTrx), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE)))

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
    
    return flagError
}

def checkVerifyEqualOrMatch(Boolean isMatch) {
    if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed, 
            (findTestData(ExcelPathOCR).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.FailedReasonVerifyEqualorMatch)

        GlobalVariable.FlagFailed = 1
    }
}

def getTrxNumber() {
    'ambil alamat trxnumber'
    def variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance-prod > div.ng-star-inserted > app-msx-paging-v2 > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))

    'banyaknya row table'
    int lastIndex = variable.size()

    'modifikasi alamat object trxnumber'
    def modifytrxnumber = WebUI.modifyObjectProperty(findTestObject('Object Repository/OCR Testing/TrxNumber'), 'xpath', 
        'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        lastIndex) + ']/datatable-body-row/div[2]/datatable-body-cell[6]/div/p', true)

    'simpan nomor transaction number ke string'
    String no_Trx = WebUI.getText(modifytrxnumber)

    'kembalikan nomor transaksi'
    return no_Trx
}

