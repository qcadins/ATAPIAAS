import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import org.openqa.selenium.By as By
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writetoexcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Isi Saldo'
int countColumnEdit = findTestData(ExcelPathLayananSaya).columnNumbers

'deklarasi variabel untuk konek ke Database eendigo_dev'
Connection conn = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_public'()

'deklarasi koneksi ke Database adins_apiaas_uat'
Connection conndevUAT = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_devUat'()

'panggil fungsi login'
WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'Layanan', ('SheetName') : sheet, ('Path') : ExcelPathLayananSaya,
	('Username') : '$Username Login', ('Password') : '$Password Login',], FailureHandling.STOP_ON_FAILURE)

'klik pada tombol profil di kanan atas'
WebUI.click(findTestObject('Object Repository/LayananSaya/Page_Balance/span_CHECK FINANCE'))

'pilih layanan saya'
WebUI.click(findTestObject('Object Repository/LayananSaya/Page_Balance/span_Layanan Saya'))

'user memilih perlu cek layanan production atau trial'
if (findTestData(ExcelPathLayananSaya).getValue(GlobalVariable.NumOfColumn, rowExcel('Tipe API KEY')) == 'PRODUCTION') {
    'klik pada api key production'
    WebUI.click(findTestObject('Object Repository/LayananSaya/Page_List Service/label_PRODUCTION'))
} else {
    'klik pada api key trial'
    WebUI.click(findTestObject('Object Repository/LayananSaya/Page_List Service/label_TRIAL'))
}

'ambil kode tenant di DB'
String tenantcode = CustomKeywords.'layanansaya.VerifLayanan.getTenantCodefromDB'(conn, findTestData(ExcelPathLayananSaya).getValue(
        GlobalVariable.NumOfColumn, rowExcel('$Username Login')))

'deklarasi variabel connect'
Connection connect

for (GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++) {
    'set penanda error menjadi 0'
    GlobalVariable.FlagFailed = 0

    'status kosong berhentikan testing, status selain unexecuted akan dilewat'
    if (findTestData(ExcelPathLayananSaya).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(ExcelPathLayananSaya).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
        'angka untuk menghitung data mandatory yang tidak terpenuhi'
        int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathLayananSaya).getValue(GlobalVariable.NumOfColumn, rowExcel('Mandatory Complete')))

		'user memilih perlu cek layanan production atau trial'
		if (findTestData(ExcelPathLayananSaya).getValue(GlobalVariable.NumOfColumn, rowExcel('Tipe API KEY')) == 'PRODUCTION') {
			'klik pada api key production'
			WebUI.click(findTestObject('Object Repository/LayananSaya/Page_List Service/label_PRODUCTION'))
			
			'deklarasikan koneksi untuk memakai production db'
			connect = conn
		} else {
			'klik pada api key trial'
			WebUI.click(findTestObject('Object Repository/LayananSaya/Page_List Service/label_TRIAL'))
			
			'deklarasikan koneksi untuk memakai production db'
			connect = conndevUAT
		}
		
        'service name dari DB'
        ArrayList serviceNameDB = CustomKeywords.'layanansaya.VerifLayanan.getListServiceName'(connect, tenantcode)

        'service status dari DB'
        ArrayList<String> serviceStatusDB = CustomKeywords.'layanansaya.VerifLayanan.getListServiceStatus'(connect, tenantcode)

        'service charge type dari DB'
        ArrayList<String> chargeTypeDB = CustomKeywords.'layanansaya.VerifLayanan.getListChargeType'(connect, tenantcode)

        'deklarasi array kosong untuk layanan saya'
        ArrayList<String> serviceNameUI = [], serviceStatusUI = [], chargeTypeUI = []

        'ambil total data pada tabel'
        Total = WebUI.getText(findTestObject('Object Repository/LayananSaya/totalData')).split(' ')

        if (WebUI.verifyEqual(serviceNameDB.size(), Integer.parseInt(Total[0]), FailureHandling.OPTIONAL) == true) {
            for (int row = 0; row < serviceNameDB.size(); row++) {
                if ((row / 10) == 1) {
                    'cek apakah button next enable atau disable'
                    if (WebUI.verifyElementVisible(findTestObject('Object Repository/OCR Testing/Page_Balance/i_Catatan_datatable-icon-right'),
                        FailureHandling.OPTIONAL)) {
                        'klik button next page'
                        WebUI.click(findTestObject('Object Repository/OCR Testing/Page_Balance/i_Catatan_datatable-icon-right'))
                    }

                    'cek apakah muncul error unknown setelah refresh'
                    if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'),
                        GlobalVariable.Timeout, FailureHandling.OPTIONAL) == false) {
                        GlobalVariable.FlagFailed = 1

                        'tulis adanya error pada sistem web'
                        CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
                            GlobalVariable.StatusWarning, (findTestData(ExcelPathLayananSaya).getValue(GlobalVariable.NumOfColumn,
                                rowExcel('Reason failed')) + ';') + GlobalVariable.FailedReasonUnknown)
                    }
                } else {
                    'ambil alamat trxnumber'
                    onepage = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-list-service > div > div > div > div:nth-child(3) > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))

                    'banyaknya row table'
                    int index = onepage.size()

                    'mulai perhitungan data service name'
                    for (int i = 1; i <= index; i++) {
                        'ambil object dari ddl'
                        modifyServiceName = WebUI.modifyObjectProperty(findTestObject('Object Repository/LayananSaya/modifytablecontent'),
                            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-service/div/div/div/div[3]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
                            i) + ']/datatable-body-row/div[2]/datatable-body-cell[1]/div/p', true)

                        'tambahkan nama service name UI ke array'
                        String data = WebUI.getText(modifyServiceName)

                        serviceNameUI.add(data)
                    }
                    
                    'mulai perhitungan data service status'
                    for (int i = 1; i <= index; i++) {
                        'ambil object dari ddl'
                        modifyServiceStatus = WebUI.modifyObjectProperty(findTestObject('Object Repository/LayananSaya/modifytablecontent'),
                            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-service/div/div/div/div[3]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
                            i) + ']/datatable-body-row/div[2]/datatable-body-cell[2]/div/p', true)

                        'tambahkan nama service name UI ke array'
                        String data = WebUI.getText(modifyServiceStatus)

                        serviceStatusUI.add(data)
                    }
                    
                    'mulai perhitungan data charge type'
                    for (int i = 1; i <= index; i++) {
                        'ambil object dari ddl'
                        modifyChargeType = WebUI.modifyObjectProperty(findTestObject('Object Repository/LayananSaya/modifytablecontent'), 
                            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-service/div/div/div/div[3]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                            i) + ']/datatable-body-row/div[2]/datatable-body-cell[3]/div/p', true)

                        'tambahkan nama service name UI ke array'
                        String data = WebUI.getText(modifyChargeType)

                        chargeTypeUI.add(data)
                    }
                }
            }
            
            'jika service name yang tampil UI tidak sesuai dengan DB'
            if (!(serviceNameUI.containsAll(serviceNameDB))) {
                GlobalVariable.FlagFailed = 1

                'Write to excel status failed and reason service tidak sesuai'
                CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
                    (findTestData(ExcelPathLayananSaya).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) +
                    ';') + GlobalVariable.FailedReasonServiceNotMatch)
            } else if (!(serviceStatusUI.containsAll(serviceStatusDB))) {
                'jika service status yang tampil UI tidak sesuai dengan DB'
                GlobalVariable.FlagFailed = 1

                'Write to excel status failed and reason status tidak sesuai'
                CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
                    (findTestData(ExcelPathLayananSaya).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) +
                    ';') + GlobalVariable.FailedReasonStatusNotMatch)
            } else if (!(chargeTypeUI.containsAll(chargeTypeDB))) {
                'jika chargetype yang tampil UI tidak sesuai dengan DB'
                GlobalVariable.FlagFailed = 1

                'Write to excel status failed and reason chargetype'
                CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
                    (findTestData(ExcelPathLayananSaya).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) +
                    ';') + GlobalVariable.FailedReasonChargeTypeNotMatch)
            }
            
            if (isMandatoryComplete != 0) {
                'jika chargetype yang tampil UI tidak sesuai dengan DB'
                GlobalVariable.FlagFailed = 1

                'Write to excel status failed and reason chargetype'
                CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
                    (findTestData(ExcelPathLayananSaya).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) +
                    ';') + GlobalVariable.FailedReasonMandatory)
            }
            
            if ((isMandatoryComplete == 0) && (GlobalVariable.FlagFailed == 0)) {
                'write to excel success'
                CustomKeywords.'writetoexcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumOfColumn - 
                    1, GlobalVariable.StatusSuccess)
            }
        } else {
            'Write to excel status failed karena table bermasalah'
            CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
                (findTestData(ExcelPathLayananSaya).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
                GlobalVariable.FailedReasonTable)
        }
        
        'lakukan refresh laman untuk kembali ke halaman 1'
        WebUI.refresh()

        'cek apakah muncul error unknown setelah refresh'
        if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'), GlobalVariable.Timeout,
            FailureHandling.OPTIONAL) == false) {
            GlobalVariable.FlagFailed = 1

            'tulis adanya error pada sistem web'
            CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, GlobalVariable.StatusWarning,
                (findTestData(ExcelPathLayananSaya).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + ';') +
                GlobalVariable.FailedReasonUnknown)
        }
    }
}

'tutup browser'
WebUI.closeBrowser()

def rowExcel(String cellValue) {
    CustomKeywords.'writetoexcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}
