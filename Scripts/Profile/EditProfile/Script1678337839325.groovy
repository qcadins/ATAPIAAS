import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Edit Profile'
int countColumnEdit = findTestData(ExcelPathEditProfile).columnNumbers()

'deklarasi variabel untuk konek ke Database eendigo_dev'
def conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_public'()

'memanggil fungsi untuk login'
WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'EditProf'], FailureHandling.STOP_ON_FAILURE)

'pada jeda waktu ini, isi captcha secara manual, automation testing dianggap sebagai robot oleh google'
WebUI.delay(10)

'focus pada button login'
WebUI.focus(findTestObject('Object Repository/RegisterLogin/'+
	'Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))

'Klik Login'
WebUI.click(findTestObject('Object Repository/RegisterLogin/'+
	'Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))

'cek apakah muncul error setelah login'
if(WebUI.verifyElementPresent(findTestObject('Object Repository/Profile/'+
	'Page_Balance/div_Unknown Error'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
	
	GlobalVariable.FlagFailed = 1
	
	'tulis adanya error pada sistem web'
	CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Edit Profile', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
			GlobalVariable.FailedReasonUnknown)
}

'looping kolom dari testdata'
for (GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++) {
	
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 1).length() == 0){
		
		break
	}
	else if (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 1).equalsIgnoreCase('Unexecuted')){
		
		'klik garis tiga di kanan atas web'
		WebUI.click(findTestObject('Object Repository/Profile/Page_Balance/i_LINA_ft-chevron-down'))
		
		'klik profil saya'
		WebUI.click(findTestObject('Object Repository/Profile/Page_Balance/a_Profil Saya'))
		
		'klik tombol edit profile'
		WebUI.click(findTestObject('Object Repository/Profile/Page_My Profile/button_Edit Profile'))
	
		WebUI.delay(GlobalVariable.Timeout)
		
		'panggil fungsi verifikasi jika checkdatabase = yes'
		if (GlobalVariable.KondisiCekDB == 'Yes') 
		{
			'verifikasi data yang ada di web dengan di database sebelum diEdit'
			WebUI.callTestCase(findTestCase('Test Cases/Profile/VerifyDataEditProfile'), 
				[:], FailureHandling.CONTINUE_ON_FAILURE)
		}
			
		'input nama depan pengguna'
		WebUI.setText(findTestObject('Object Repository/Profile/Page_Edit Profile/input__firstName'), 
			findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 10))
		
		'klik pada field nama belakang'
		WebUI.click(findTestObject('Object Repository/Profile/Page_Edit Profile/input__lastName'))
		
		'input data nama belakang'
		WebUI.setText(findTestObject('Object Repository/Profile/Page_Edit Profile/input__lastName'), 
			findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 11))
		
		'input data nama perusahaan'
		WebUI.setText(findTestObject('Object Repository/Profile/Page_Edit Profile/input__tenantName'), 
			findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 12))
		
		'input data industri'
		WebUI.setText(findTestObject('Object Repository/Profile/Page_Edit Profile/input__industry'), 
			findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 13))
		
		'pilih jenis kelamin'
		if (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 14) == 'M') {
			
			WebUI.check(findTestObject('Object Repository/Profile/'+
				'Page_Edit Profile/input__ng-untouched ng-pristine ng-valid'))
		} 
		else {
			
			WebUI.check(findTestObject('Object Repository/Profile/'+
				'Page_Edit Profile/input_Pria_ng-untouched ng-pristine ng-valid'))
		}
			
		'input field website'
		WebUI.setText(findTestObject('Object Repository/Profile/Page_Edit Profile/input__website'), 
			findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 15))
		
		'input data field nomor telepon'
		WebUI.setText(findTestObject('Object Repository/Profile/Page_Edit Profile/input_Wanita_phoneNumber'), 
			findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 16))
		
		'input data field position'
		WebUI.setText(findTestObject('Object Repository/Profile/Page_Edit Profile/input__position'), 
			findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 17))
		
		'pilih dari dropdownlist +62 Indonesia'
		WebUI.selectOptionByLabel(findTestObject('Object Repository/Profile/'+
			'Page_Edit Profile/select_Afghanistan 93Albania 355Algeria 213_ddb156'),
			findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 18), false)
			
		'klik tombol simpan'
		WebUI.click(findTestObject('Object Repository/Profile/Page_Edit Profile/button_Simpan'))
			
		WebUI.delay(3)
		
		'cek apakah tombol sukses muncul'
		if(WebUI.verifyElementPresent(findTestObject('Object Repository/Profile/'+
			'Page_Edit Profile/button_OK'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)){
		
			'klik pada tombol ok jika muncul'
			WebUI.click(findTestObject('Object Repository/Profile/Page_Edit Profile/button_OK'))
		}
		
		'panggil fungsi verifikasi jika checkdatabase = yes'
		if (GlobalVariable.KondisiCekDB == 'Yes') {
			
			'verifikasi data yang ada di excel dengan di database sesudah diEdit'
			WebUI.callTestCase(findTestCase('Test Cases/Profile/EditProfileStoreDBVerif'), 
				[:], FailureHandling.CONTINUE_ON_FAILURE)
		}
			
		'klik pada tombol garis tiga'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/i_KEPIN EDGAR_ft-menu font-medium-3'))
			
		'klik pada tombol balance'
		WebUI.click(findTestObject('Object Repository/Profile/Page_Balance/span_Balance'))
			
		'klik pada saldo trial'
		WebUI.click(findTestObject('Object Repository/Profile/Page_Balance/label_TRIAL'))
			
		'verifikasi adanya saldo trial'
		if(!WebUI.verifyElementPresent(findTestObject('Object Repository/Profile/'+
			'Page_Balance/span_IDR'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)){
		
			GlobalVariable.FlagFailed = 1
			'Write to excel status failed and reason topup failed'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Edit Profile', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
				GlobalVariable.FailedReasonTrial)
		}
			
		'klik pada tombol garis tiga'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/i_KEPIN EDGAR_ft-menu font-medium-3'))
			
		'klik pada tombol API KEY'
		WebUI.click(findTestObject('Object Repository/Profile/Page_Balance/span_API Key'))
			
		'ambil nama depan tenant'
		String tenantnameExcel = findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 10) + " " 
		+ findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 11)
		
		'nama tenant dari DB'
		String tenantnameDB = CustomKeywords.'profile.CheckProfile.getTenantNamefromDB'(conn, 
			findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 8))
		
		'verifikasi adanya tenant code dan name yang sesuai DB'
		if(WebUI.verifyMatch(tenantnameDB, tenantnameExcel, false) == false){
			
			GlobalVariable.FlagFailed = 1
			'Write to excel status failed and reason topup failed'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Edit Profile', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathEditProfile).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
				GlobalVariable.FailedReasonTenant)
		}
			
		'kondisi jika tidak ada failed pada bagian lain testcase'
		if (GlobalVariable.FlagFailed == 0){
			
			'write to excel success'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Edit Profile', 0,
				GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
		}
		else{
			
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
