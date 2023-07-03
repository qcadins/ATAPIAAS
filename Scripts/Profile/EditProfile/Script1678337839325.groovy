import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Edit Profile'
int countColumnEdit = findTestData(ExcelPathEditProfile).getColumnNumbers()

'deklarasi variabel untuk konek ke Database eendigo_dev'
Connection conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_public'()

'looping kolom dari testdata'
for (GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++) {
	
    'status kosong berhentikan testing, status selain unexecuted akan dilewat'
    if (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 1).length() == 0) {
        break
    } 
	else if (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 1).equalsIgnoreCase('Unexecuted')) {
		
        'memanggil fungsi untuk login'
        WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'EditProf', ('SheetName') : 'Edit Profile', ('Path') : ExcelPathEditProfile], 
            FailureHandling.STOP_ON_FAILURE)
        
        'angka untuk menghitung data mandatory yang tidak terpenuhi'
        int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 
                5))

        userRole = CustomKeywords.'profile.CheckProfile.getUserRole'(conn, findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 
                9))

        'klik garis tiga di kanan atas web'
        WebUI.click(findTestObject('Object Repository/Profile/Page_Balance/i_LINA_ft-chevron-down'))

        'klik profil saya'
        WebUI.click(findTestObject('Object Repository/Profile/Page_Balance/a_Profil Saya'))

        'cek apakah muncul error unknown setelah login'
        if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'), GlobalVariable.Timeout, 
            FailureHandling.OPTIONAL) == false) {
            GlobalVariable.FlagFailed = 1

            'tulis adanya error pada sistem web'
            CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Edit Profile', GlobalVariable.NumOfColumn, 
                GlobalVariable.StatusWarning, (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 2) + 
                ';') + GlobalVariable.FailedReasonUnknown)
        }
        
        'klik tombol edit profile'
        WebUI.click(findTestObject('Object Repository/Profile/Page_My Profile/button_Edit Profile'))

        WebUI.delay(GlobalVariable.Timeout)

        'panggil fungsi verifikasi jika checkdatabase = yes'
        if (GlobalVariable.KondisiCekDB == 'Yes') {
            'verifikasi data yang ada di web dengan di database sebelum diEdit'
            WebUI.callTestCase(findTestCase('Profile/VerifyDataEditProfile'), [('ExcelPathEditProfile') : 'APIAAS/DataEditProfile', ('role') : userRole], 
				FailureHandling.CONTINUE_ON_FAILURE)
        }
        
        'input nama depan pengguna'
        WebUI.setText(findTestObject('Object Repository/Profile/Page_Edit Profile/input__firstName'), findTestData(ExcelPathEditProfile).getValue(
                GlobalVariable.NumOfColumn, 11))

        'klik pada field nama belakang'
        WebUI.click(findTestObject('Object Repository/Profile/Page_Edit Profile/input__lastName'))

        'input data nama belakang'
        WebUI.setText(findTestObject('Object Repository/Profile/Page_Edit Profile/input__lastName'), findTestData(ExcelPathEditProfile).getValue(
                GlobalVariable.NumOfColumn, 12))

        'pilih jenis kelamin'
        if (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 15) == 'M') {
            WebUI.check(findTestObject('Object Repository/Profile/Page_Edit Profile/input__radioMale'))
        } else {
            WebUI.check(findTestObject('Object Repository/Profile/Page_Edit Profile/input__radioFemale'))
        }
        
        'input data field nomor telepon'
        WebUI.setText(findTestObject('Object Repository/Profile/Page_Edit Profile/input__PhoneNum'), findTestData(ExcelPathEditProfile).getValue(
                GlobalVariable.NumOfColumn, 17))

        'input data field position'
        WebUI.setText(findTestObject('Object Repository/Profile/Page_Edit Profile/input__position'), findTestData(ExcelPathEditProfile).getValue(
                GlobalVariable.NumOfColumn, 18))

        'pilih dari dropdownlist +62 Indonesia'
        WebUI.selectOptionByLabel(findTestObject('Object Repository/Profile/Page_Edit Profile/select__country'), findTestData(
                ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 19), false)

        'pilih tipe akun'
        if (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 22) == 'Perusahaan') {
            'check radio button perusahaan'
            WebUI.check(findTestObject('Object Repository/Profile/Page_Edit Profile/radio_Perusahaan'))
        } else if (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 22) == 'Personal') {
            'check radio button personal'
            WebUI.check(findTestObject('Object Repository/Profile/Page_Edit Profile/radio_Personal'))
        }
        
        'check if user role == admin client'
        if (userRole == 'Admin Client') {
            'input data nama perusahaan'
            WebUI.setText(findTestObject('Object Repository/Profile/Page_Edit Profile/input__tenantName'), findTestData(
                    ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 13))

            'input data industri'
            WebUI.setText(findTestObject('Object Repository/Profile/Page_Edit Profile/input__industry'), findTestData(ExcelPathEditProfile).getValue(
                    GlobalVariable.NumOfColumn, 14))

            'input field website'
            WebUI.setText(findTestObject('Object Repository/Profile/Page_Edit Profile/input__website'), findTestData(ExcelPathEditProfile).getValue(
                    GlobalVariable.NumOfColumn, 16))

            'input no NPWP'
            WebUI.setText(findTestObject('Object Repository/Profile/Page_Edit Profile/input__NPWP'), findTestData(ExcelPathEditProfile).getValue(
                    GlobalVariable.NumOfColumn, 20))

            if (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 21).length() > 0) {
                'upload file NPWP'
                WebUI.uploadFile(findTestObject('Object Repository/Profile/Page_Edit Profile/button_UploadNPWP'), findTestData(
                        ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 21), FailureHandling.CONTINUE_ON_FAILURE)
            }
        }
        
        'klik tombol simpan'
        WebUI.click(findTestObject('Object Repository/Profile/Page_Edit Profile/button_Simpan'))

        'cek apakah muncul error unknown setelah login'
        if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'), GlobalVariable.Timeout, 
            FailureHandling.OPTIONAL) == false) {
            GlobalVariable.FlagFailed = 1

            'tulis adanya error pada sistem web'
            CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Edit Profile', GlobalVariable.NumOfColumn, 
                GlobalVariable.StatusWarning, (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 2) + 
                ';') + GlobalVariable.FailedReasonUnknown)
        }
        
        WebUI.delay(3)

        'cek apakah tombol sukses muncul'
        if (WebUI.verifyElementPresent(findTestObject('Object Repository/Profile/' + 'Page_Edit Profile/button_OK'), GlobalVariable.Timeout, 
            FailureHandling.OPTIONAL)) {
            'klik pada tombol ok jika muncul'
            WebUI.click(findTestObject('Object Repository/Profile/Page_Edit Profile/button_OK'))
        }
        
        'cek apakah muncul error unknown setelah ubah data edit profile'
        if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'), GlobalVariable.Timeout, 
            FailureHandling.OPTIONAL) == false) {
            GlobalVariable.FlagFailed = 1

            'tulis adanya error pada sistem web'
            CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Edit Profile', GlobalVariable.NumOfColumn, 
                GlobalVariable.StatusWarning, (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 2) + 
                ';') + GlobalVariable.FailedReasonUnknown)
        }
        
		'check if username pada pojok kanan atas tidak match dengan data yang baru di edit'
		if (!WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Profile/Page_Edit Profile/label_UserName')), findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 11) + ' ' + findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 12), false, FailureHandling.CONTINUE_ON_FAILURE)) {
			GlobalVariable.FlagFailed = 1
			
			'tulis adanya error pada sistem web'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Edit Profile', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 2) +
							';') + 'Username Pojok Kanan Atas Tidak Sesuai Data Edit')
		}
		
        'panggil fungsi verifikasi jika checkdatabase = yes'
        if (GlobalVariable.KondisiCekDB == 'Yes') {
            'verifikasi data yang ada di excel dengan di database sesudah diEdit'
            WebUI.callTestCase(findTestCase('Test Cases/Profile/EditProfileStoreDBVerif'), [('role') : userRole], FailureHandling.CONTINUE_ON_FAILURE)
        }
        
        if (userRole == 'Admin Client') {
            'klik pada tombol garis tiga'
            WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/i_KEPIN EDGAR_ft-menu font-medium-3'))

            'klik pada tombol balance'
            WebUI.click(findTestObject('Object Repository/Profile/Page_Balance/span_Balance'))

            'klik pada saldo trial'
            WebUI.click(findTestObject('Object Repository/Profile/Page_Balance/label_TRIAL'))

            'verifikasi adanya saldo trial'
            if (!(WebUI.verifyElementPresent(findTestObject('Object Repository/Profile/' + 'Page_Balance/span_IDR'), GlobalVariable.Timeout, 
                FailureHandling.OPTIONAL))) {
                GlobalVariable.FlagFailed = 1

                'Write to excel status failed and reason topup failed'
                CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Edit Profile', GlobalVariable.NumOfColumn, 
                    GlobalVariable.StatusFailed, (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 
                        2) + ';') + GlobalVariable.FailedReasonTrial)
            }
        }
        
        'klik pada tombol garis tiga'
        WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/i_KEPIN EDGAR_ft-menu font-medium-3'))

        'klik pada tombol API KEY'
        WebUI.click(findTestObject('Object Repository/Profile/Page_Balance/span_API Key'))

        'ambil nama depan tenant'
        String tenantnameExcel = findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 13)

        'nama tenant dari DB'
        String tenantnameDB = CustomKeywords.'profile.CheckProfile.getTenantNamefromDB'(conn, findTestData(ExcelPathEditProfile).getValue(
                GlobalVariable.NumOfColumn, 9))

        'verifikasi adanya tenant code dan name yang sesuai DB'
        if (WebUI.verifyMatch(tenantnameDB, tenantnameExcel, false, FailureHandling.OPTIONAL) == false) {
            GlobalVariable.FlagFailed = 1

            'Write to excel status failed and reason topup failed'
            CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Edit Profile', GlobalVariable.NumOfColumn, 
                GlobalVariable.StatusFailed, (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 2) + 
                ';') + GlobalVariable.FailedReasonTenant)
        }
        
        'kondisi jika tidak ada failed pada bagian lain testcase'
        if (isMandatoryComplete != 0) {
            'Write To Excel GlobalVariable.StatusFailed and gagal karena reason status'
            CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Edit Profile', GlobalVariable.NumOfColumn, 
                GlobalVariable.StatusFailed, (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 2) + 
                ';') + GlobalVariable.FailedReasonMandatory)
        } else if (GlobalVariable.FlagFailed == 0) {
            'write to excel success'
            CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Edit Profile', 0, GlobalVariable.NumOfColumn - 
                1, GlobalVariable.StatusSuccess)
        } else {
            'Write To Excel GlobalVariable.StatusFailed and gagal karena reason status'
            CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Edit Profile', GlobalVariable.NumOfColumn, 
                GlobalVariable.StatusFailed, (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 2) + 
                ';') + GlobalVariable.StatusReasonSystem)
        }
        
        'reset flagging failed ke 0'
        GlobalVariable.FlagFailed = 0
    }
}

WebUI.closeBrowser()

