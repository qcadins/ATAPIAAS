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
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writetoexcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Edit Profile'
int countColumnEdit = findTestData(ExcelPathEditProfile).columnNumbers

'deklarasi variabel untuk konek ke Database eendigo_dev'
Connection conn = CustomKeywords.'dbconnection.Connect.connectDBAPIAAS_public'()

'looping kolom dari testdata'
for (GlobalVariable.NumOfColumn = 2; GlobalVariable.NumOfColumn <= countColumnEdit; GlobalVariable.NumOfColumn++) {
	
    'status kosong berhentikan testing, status selain unexecuted akan dilewat'
    if (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).length() == 0) {
		
        break
		
    } else if (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
		
		GlobalVariable.FlagFailed = 0
		
        'memanggil fungsi untuk login'
        WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'EditProf', ('SheetName') : sheet, ('Path') : ExcelPathEditProfile], 
            FailureHandling.STOP_ON_FAILURE)
		
		if (GlobalVariable.FlagFailed == 1) {
			
			WebUI.closeBrowser()
			
			continue
		}
        
        'angka untuk menghitung data mandatory yang tidak terpenuhi'
        int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, rowExcel('Mandatory Complete')))

        userRole = CustomKeywords.'profile.CheckProfile.getUserRole'(conn,
			findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, rowExcel('$Username Login')))

        'klik garis tiga di kanan atas web'
        WebUI.click(findTestObject('Object Repository/Profile/Page_Balance/dropdownProfile'))

        'klik profil saya'
        WebUI.click(findTestObject('Object Repository/Profile/Page_Balance/a_Profil Saya'))

        'cek apakah muncul error unknown setelah login'
        if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'), GlobalVariable.Timeout, 
            FailureHandling.OPTIONAL) == false) {
            GlobalVariable.FlagFailed = 1

            'tulis adanya error pada sistem web'
            CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, 
                GlobalVariable.StatusWarning, (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + 
                ';') + GlobalVariable.FailedReasonUnknown)
        }
        
        'klik tombol edit profile'
        WebUI.click(findTestObject('Object Repository/Profile/Page_My Profile/button_Edit Profile'))

        WebUI.delay(GlobalVariable.Timeout)

        'panggil fungsi verifikasi jika checkdatabase = yes'
        if (GlobalVariable.KondisiCekDB == 'Yes') {
			
			'jika tenant masih kosong tidak perlu lakukan pengecekan ke DB'
			if (WebUI.getText(findTestObject('Profile/Page_Edit Profile/input__tenantName')) != '')
			
            'verifikasi data yang ada di web dengan di database sebelum diEdit'
			verifyDataEdit(conn, userRole)
        }
		
		'input nama depan pengguna'
		setTextEmptyValidation(findTestObject('Profile/Page_Edit Profile/input__firstName'), '$Nama Depan')

        'klik pada field nama belakang'
        WebUI.click(findTestObject('Object Repository/Profile/Page_Edit Profile/input__lastName'))

        'input data nama belakang'
		setTextEmptyValidation(findTestObject('Profile/Page_Edit Profile/input__lastName'), '$Last Name')

        'pilih jenis kelamin'
        if (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, rowExcel('Jenis Kelamin')) == 'M') {
			
            WebUI.check(findTestObject('Object Repository/Profile/Page_Edit Profile/input__radioMale'))
        } else {
			
            WebUI.check(findTestObject('Object Repository/Profile/Page_Edit Profile/input__radioFemale'))
        }
        
        'input data field nomor telepon'
		setTextEmptyValidation(findTestObject('Profile/Page_Edit Profile/input__PhoneNum'), 'Nomor Telepon')

        'input data field position'
		setTextEmptyValidation(findTestObject('Profile/Page_Edit Profile/input__position'), 'Jabatan')

        'pilih dari dropdownlist +62 Indonesia'
        WebUI.selectOptionByLabel(findTestObject('Object Repository/Profile/Page_Edit Profile/select__country'), findTestData(
                ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, rowExcel('$Kode Negara')), false)

        'pilih tipe akun'
        if (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, rowExcel('Tipe Akun')) == 'Perusahaan') {
			
            'check radio button perusahaan'
            WebUI.check(findTestObject('Object Repository/Profile/Page_Edit Profile/radio_Perusahaan'))
			
        } else if (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, rowExcel('Tipe Akun')) == 'Personal') {
			
            'check radio button personal'
            WebUI.check(findTestObject('Object Repository/Profile/Page_Edit Profile/radio_Personal'))
        }
        
        'check if user role == admin client'
        if (userRole == 'Admin Client') {
			
            'input data nama perusahaan'
			setTextEmptyValidation(findTestObject('Profile/Page_Edit Profile/input__tenantName'), '$Nama Tenant')

            'input data industri'
			setTextEmptyValidation(findTestObject('Profile/Page_Edit Profile/input__industry'), '$Industry')

            'input field website'
			setTextEmptyValidation(findTestObject('Profile/Page_Edit Profile/input__website'), 'Website')
			
            'input no NPWP'
			setTextEmptyValidation(findTestObject('Profile/Page_Edit Profile/input__NPWP'), 'No NPWP')

            if (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, rowExcel('NPWP File Path')).length() > 0) {
				
                'upload file NPWP'
                WebUI.uploadFile(findTestObject('Object Repository/Profile/Page_Edit Profile/button_UploadNPWP'), findTestData(
                        ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, rowExcel('NPWP File Path')), FailureHandling.CONTINUE_ON_FAILURE)
            }
        }
        
        'klik tombol simpan'
        WebUI.click(findTestObject('Object Repository/Profile/Page_Edit Profile/button_Simpan'))
        
        WebUI.delay(3)

        'cek apakah tombol sukses muncul'
        if (WebUI.verifyElementPresent(findTestObject('Object Repository/Profile/Page_Edit Profile/button_OK'), GlobalVariable.Timeout, 
            FailureHandling.OPTIONAL)) {
            'klik pada tombol ok jika muncul'
            WebUI.click(findTestObject('Object Repository/Profile/Page_Edit Profile/button_OK'))
        }
        
		'check if username pada pojok kanan atas tidak match dengan data yang baru di edit'
		if (!WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Profile/Page_Edit Profile/label_UserName')), findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, rowExcel('$Nama Depan')) + ' ' + findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, rowExcel('$Last Name')), false, FailureHandling.CONTINUE_ON_FAILURE)) {
			GlobalVariable.FlagFailed = 1
			
			'tulis adanya error pada sistem web'
			CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) +
							';') + 'Username Pojok Kanan Atas Tidak Sesuai Data Edit')
		}
		
        'panggil fungsi verifikasi jika checkdatabase = yes'
        if (GlobalVariable.KondisiCekDB == 'Yes') {
			
            'verifikasi data yang ada di excel dengan di database sesudah diEdit'
            WebUI.callTestCase(findTestCase('Test Cases/Profile/EditProfileStoreDBVerif'), [('role') : userRole], FailureHandling.CONTINUE_ON_FAILURE)
        }
        
        if (userRole == 'Admin Client') {
			
            'klik pada tombol garis tiga'
            WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/spanMenu'))

            'klik pada tombol balance'
            WebUI.click(findTestObject('Object Repository/Profile/Page_Balance/span_Balance'))

            'klik pada saldo trial'
            WebUI.click(findTestObject('Object Repository/Profile/Page_Balance/label_TRIAL'))

            'verifikasi adanya saldo trial'
            if (!(WebUI.verifyElementPresent(findTestObject('Object Repository/Profile/' + 'Page_Balance/span_IDR'), GlobalVariable.Timeout, 
                FailureHandling.OPTIONAL))) {
                GlobalVariable.FlagFailed = 1

                'Write to excel status failed and reason topup failed'
                CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, 
                    GlobalVariable.StatusFailed, (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 
                        rowExcel('Reason failed')) + ';') + GlobalVariable.FailedReasonTrial)
            }
        }
        
//        'klik pada tombol garis tiga'
//        WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/spanMenu'))
//
//        'klik pada tombol API KEY'
//        WebUI.click(findTestObject('Object Repository/Profile/Page_Balance/span_API Key'))
//
//        'ambil nama depan tenant'
//        String tenantnameExcel = findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 13)
//
//        'nama tenant dari DB'
//        String tenantnameDB = CustomKeywords.'profile.CheckProfile.getTenantNamefromDB'(conn, findTestData(ExcelPathEditProfile).getValue(
//                GlobalVariable.NumOfColumn, 9))
//
//        'verifikasi adanya tenant code dan name yang sesuai DB'
//        if (WebUI.verifyMatch(tenantnameDB, tenantnameExcel, false, FailureHandling.OPTIONAL) == false) {
//            GlobalVariable.FlagFailed = 1
//
//            'Write to excel status failed and reason topup failed'
//            CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, 
//                GlobalVariable.StatusFailed, (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 2) + 
//                ';') + GlobalVariable.FailedReasonTenant)
//        }
        
        'kondisi jika tidak ada failed pada bagian lain testcase'
        if (isMandatoryComplete != 0) {
			
            'Write To Excel GlobalVariable.StatusFailed and gagal karena reason status'
            CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, 
                GlobalVariable.StatusFailed, (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + 
                ';') + GlobalVariable.FailedReasonMandatory)
			
        } else if (GlobalVariable.FlagFailed == 0) {
			
            'write to excel success'
            CustomKeywords.'writetoexcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Status') - 1, GlobalVariable.NumOfColumn - 
                1, GlobalVariable.StatusSuccess)
			
        } else {
			
            'Write To Excel GlobalVariable.StatusFailed and gagal karena reason status'
            CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn, 
                GlobalVariable.StatusWarning, (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) + 
                ';') + GlobalVariable.StatusReasonSystem)
        }
        
        'reset flagging failed ke 0'
        GlobalVariable.FlagFailed = 0
		
		WebUI.closeBrowser()
    }
}

if (GlobalVariable.continueTC == 'Yes') {
	'panggil testcase untuk change password'
	WebUI.callTestCase(findTestCase('Test Cases/Change Password/MAIN_ChangePassword'), [:], FailureHandling.STOP_ON_FAILURE)
}

'panggil testcase untuk change password'
WebUI.callTestCase(findTestCase('Test Cases/Change Password/MAIN_ChangePassword'), [:], FailureHandling.STOP_ON_FAILURE)

def verifyDataEdit(Connection conn, String role) {
	'ambil email dari testdata, disimpan ke string'
	String email = WebUI.getAttribute(findTestObject('Profile/Page_Edit Profile/input__email'), 'value')
	
	'kumpulan string dari data yang diambil langsung dari database'
	ArrayList hasildb = CustomKeywords.'profile.CheckProfile.getProfilefromDB'(conn, email, role)
	
	'ambil text dari UI Web APIAAS'
	ArrayList hasilweb = CustomKeywords.'profile.CheckProfile.getAttributeValueProfile'(role)
	
	'verifikasi data pada WEB dan DB sama'
	for (int j = 0; j < hasildb.size; j++) {
		checkVerifyEqualorMatch(WebUI.verifyMatch(hasilweb[j], hasildb[j], false, FailureHandling.CONTINUE_ON_FAILURE))
	}
}

def checkVerifyEqualorMatch(Boolean isMatch) {
	if (isMatch == false) {
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		GlobalVariable.FlagFailed = 1
		CustomKeywords.'writetoexcel.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, rowExcel('Reason failed')) +
				';') + GlobalVariable.FailedReasonVerifyEqualorMatch + ' Data sebelum edit tidak sesuai')
	}
}

def rowExcel(String cellValue) {
	return CustomKeywords.'writetoexcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def setTextEmptyValidation(TestObject object, String Testdata) {
	
	'jika testdata kosong'
	if (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, rowExcel(Testdata)).equalsIgnoreCase('')) {
		
		'select all text di field tersebut'
		WebUI.sendKeys(object, Keys.chord(Keys.CONTROL + 'a'))
		
		'hapus text tersebut'
		WebUI.sendKeys(object, Keys.chord(Keys.BACK_SPACE))
		
		'input text kosong'
		WebUI.setText(object, '')
	} else {
		
		'input text sesuai testdata'
		WebUI.setText(object, findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, rowExcel(Testdata)))
	}
}