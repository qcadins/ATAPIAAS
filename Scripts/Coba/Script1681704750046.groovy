import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities

import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable

import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject

'deklarasi koneksi ke Database eendigo_dev'
conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_public'()

'get data balacne mutation dari DB'
String result = CustomKeywords.'tenant.TenantVerif.getTenantServices'(conn,
	'PT REAL ESTATE').replace('{','').replace('}','').replace('"','').replace(',','')
	
ArrayList a = result.split(':0')

//ArrayList a = ['LIVENESS_FACECOMPARE:300','FACE_COMPARE','LIVENESS','CDE','OCR_KTP','CREDIT_SCORING']

ArrayList b = ['CDE', 'CREDIT_SCORING']

if (a.containsAll(b)) {
	println 'benar'
} else {
	println 'salah'
}

////'mencari directory excel'
////GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')
////
////'mencari directory excel lain'
////GlobalVariable.DataFilePath2 = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Simulasi/Simulasi Hitung Top Up Using Coupon.xlsx')
////
////'mendapat jumlah kolom dari sheet Edit Profile'
////int countColumnEdit = findTestData(ExcelPathTopUp).columnNumbers
////
////'deklarasi koneksi ke Database adins_apiaas_uat'
////Connection conndev = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_esign'()
////
////'panggil fungsi login'
////WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'TopUp', ('SheetName') : 'TopUp',
////	('Path') : ExcelPathTopUp], FailureHandling.STOP_ON_FAILURE)
////
////'klik pada tombol menu'
////WebUI.click(findTestObject('Object Repository/Top Up/Page_Balance/spanMenu'))
////
////'klik pada menu isi saldo'
////WebUI.click(findTestObject('Object Repository/Top Up/Page_Balance/span_Isi Saldo'))
////
////'cek apakah tombol menu dalam jangkauan web'
////if (WebUI.verifyElementVisible(findTestObject('Object Repository/User Management-Role/Page_List Roles/tombolX_menu'), FailureHandling.OPTIONAL)) {
////	
////	'klik pada tombol silang menu'
////	WebUI.click(findTestObject('Object Repository/User Management-Role/Page_List Roles/tombolX_menu'))
////}
////
////'ambil nama TipeSaldo dari DB'
////ArrayList<String> namaTipeSaldoDB = CustomKeywords.'topup.TopupVerif.getDDLTipeSaldo'(conndev)
////
////'call function check ddl untuk Tipe Saldo'
////checkDDL(findTestObject('Object Repository/Top Up/Page_Topup Balance/inputtipesaldo'), namaTipeSaldoDB, 'DDL Tipe Saldo')
////
////def checkDDL(TestObject objectDDL, ArrayList<String> listDB, String reason) {
////	'declare array untuk menampung ddl'
////	ArrayList<String> list = []
////
////	'click untuk memunculkan ddl'
////	WebUI.click(objectDDL)
////	
////	'get id ddl'
////	id = WebUI.getAttribute(findTestObject('Object Repository/Top Up/ddlClass'), 'id', FailureHandling.CONTINUE_ON_FAILURE)
////
////	'get row'
////	variable = DriverFactory.webDriver.findElements(By.cssSelector(('#' + id) + '> div > div:nth-child(2) div'))
////	
////	'looping untuk get ddl kedalam array'
////	for (i = 1; i < variable.size(); i++) {
////		'modify object DDL'
////		modifyObjectDDL = WebUI.modifyObjectProperty(findTestObject('Object Repository/User Management-Role/modifyObject'), 'xpath', 'equals', ((('//*[@id=\'' +
////			id) + '-') + i) + '\']', true)
////
////		'add ddl ke array'
////		list.add(WebUI.getText(modifyObjectDDL))
////	}
////	
////	'verify ddl ui = db'
////	checkVerifyEqualOrMatch(listDB.containsAll(list), reason)
////
////	'verify jumlah ddl ui = db'
////	checkVerifyEqualOrMatch(WebUI.verifyEqual(list.size(), listDB.size(), FailureHandling.CONTINUE_ON_FAILURE), ' Jumlah ' + reason)
////	
////	'Input enter untuk tutup ddl'
////	WebUI.sendKeys(objectDDL, Keys.chord(Keys.ENTER))
////}
////
////def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
////	if (isMatch == false) {
////		GlobalVariable.FlagFailed = 1
////	}
////}
//
////==========================================================================
//
///*Coba untuk extension otomatis Recaptcha*/
//
////System.setProperty("webdriver.chrome.driver", "Drivers/chromedriver.exe")
////
////ChromeOptions options = new ChromeOptions()
////
////options.addExtensions(new File("Drivers/nocaptchaai_chrome_1.7.6.crx"))
////
////options.addExtensions(new File("Drivers/Smart_Wait.crx"))
////
////DesiredCapabilities caps = new DesiredCapabilities()
////
////caps.setCapability(ChromeOptions.CAPABILITY, options)
////
////WebDriver driver = new ChromeDriver(caps)
////
////DriverFactory.changeWebDriver(driver)
////
////WebUI.navigateToUrl('https://config.nocaptchaai.com/?apikey=kvnedgar9286-35bde35f-e305-699a-0af2-d6fb983c8c4a')
////
////'buka website APIAAS SIT, data diambil dari TestData Login'
////WebUI.navigateToUrl(findTestData(ExcelPath).getValue(1, 2))
////
////'input email'
////WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/inputemail'),
////	'JONAUDRIS23@MAILSAC.COM')
////
////'input password'
////WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/inputpassword'),
////	'P@ssw0rd123')
////
////'pada jeda waktu ini, isi captcha secara manual, automation testing dianggap sebagai robot oleh google'
////WebUI.delay(3)
////
////WebUI.closeBrowser()
////
////driver = new ChromeDriver(caps)
////
////DriverFactory.changeWebDriver(driver)
////
////'buka website APIAAS SIT, data diambil dari TestData Login'
////WebUI.navigateToUrl(findTestData(ExcelPath).getValue(1, 2))
////
////'input email'
////WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/inputemail'),
////	'JONAUDRIS23@MAILSAC.COM')
////
////'input password'
////WebUI.setText(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/inputpassword'),
////	'P@ssw0rd123')
////
////'pada jeda waktu ini, isi captcha secara manual, automation testing dianggap sebagai robot oleh google'
////WebUI.delay(10)
////
////'Klik Login'
////WebUI.click(findTestObject('Object Repository/RegisterLogin/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))
//
////=====================================================================================================
//
///*Coba untuk ambil response dan lakukan beautify secara otomatis ke dalam notepad*/
//
////'mencari directory excel\r\n'
////GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')
////
////'get base url'
////GlobalVariable.BaseUrl =  findTestData('Login/BaseUrl').getValue(2, 14)
////
////'lakukan proses HIT api dengan parameter image, key, dan juga tenant'
////response = WS.sendRequest(findTestObject('Object Repository/OCR Testing/New API/Passport',
////[
////	('img'): findTestData(ExcelPathOCRTesting).getValue(2, 9),
////	('key'): '123',
////	('tenant'):'abc'
////]))
////
////// Check if the request was successful
////if (response.getStatusCode() == 200) {
////	String responseBody = response.getResponseBodyContent()
////	
////	try {
////		// Parse the original JSON string
////		def slurper = new groovy.json.JsonSlurper()
////		def json = slurper.parseText(responseBody)
////	
////		// Beautify the JSON
////		def builder = new groovy.json.JsonBuilder(json)
////		def beautifiedJson = builder.toPrettyString()
////	
////		// Define the path for the beautified JSON file
////		String beautifiedJsonPath = System.getProperty('user.dir') + '\\Download\\beautified_response.json'
////	
////		// Save the beautified JSON to a file
////		new File(beautifiedJsonPath).text = beautifiedJson
////		
////		'write to excel status'
////		CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'OCR Passport', 28, 1, beautifiedJson)
////		
//////		'\n\n File Location : ' + beautifiedJsonPath
////	} catch (Exception e) {
////		println("Failed to beautify the JSON: ${e.getMessage()}")
////	}
////}
//
////=====================================================================================================
//
//import groovy.json.JsonSlurper
//
//// Your first JSON response
//def response1 = '''
//{
//    "message": "",
//    "ocr_date": "2023-11-23T10:39:32+07:00",
//    "read": {
//        "agama": "ISLAM",
//        "alamat": "DUSUN IV MELATI",
//        "berlakuHingga": "SEUMUR HIDUP",
//        "golonganDarah": "",
//        "is_ektp": true,
//        "jenisKelamin": "LAKI-LAKI",
//        "kecamatan": "AIR PUTIH",
//        "kelurahanDesa": "TANAH RENDAH",
//        "kewarganegaraan": "WNI",
//        "kotaKabupaten": "BATU BARA",
//        "nama": "WWW.RIZKYMHD.COM",
//        "nik": "120824130999003",
//        "pekerjaan": "DOKTER",
//        "provinsi": "SUMATERA UTARA",
//        "rtRw": "000/000",
//        "statusPerkawinan": "BELUM KAWIN",
//        "tanggalLahir": "13-09-1999",
//        "tempatLahir": "BANDAR MASILAM"
//    },
//    "read_confidence": {
//        "agama": 0.99,
//        "alamat": 0.95708,
//        "berlakuHingga": 0.99,
//        "golonganDarah": 0.01,
//        "jenisKelamin": 0.99,
//        "kecamatan": 0.99,
//        "kelurahanDesa": 0.99,
//        "kewarganegaraan": 0.99,
//        "kotaKabupaten": 0.99,
//        "nama": 0.90725,
//        "nik": 0.01,
//        "pekerjaan": 0.99,
//        "provinsi": 0.99,
//        "rtRw": 0.98687,
//        "statusPerkawinan": 0.99,
//        "tanggalLahir": 0.99298,
//        "tempatLahir": 0.96626
//    },
//    "status": "SUCCESS"
//}
//'''
//
//// Your second JSON response
//def response2 = '''
//{
//    "message": "",
//    "ocr_date": "2023-11-23T10:48:04+07:00",
//    "read": {
//        "agama": "KRISTEN",
//        "alamat": "JAGA VI",
//        "berlakuHingga": "SEUMUR HIDUP",
//        "golonganDarah": "",
//        "is_ektp": true,
//        "jenisKelamin": "PEREMPUAN",
//        "kecamatan": "MOTOLING",
//        "kelurahanDesa": "MOTOLING",
//        "kewarganegaraan": "WNI",
//        "kotaKabupaten": "MINAHASA SELATAN",
//        "nama": "JELTY JEINE KORDAK",
//        "nik": "7105074205820001",
//        "pekerjaan": "MENGURUS RUMAH TANGGA",
//        "provinsi": "SULAWESI UTARA",
//        "rtRw": "",
//        "statusPerkawinan": "KAWIN",
//        "tanggalLahir": "18-05-1982",
//        "tempatLahir": "MOTOLING"
//    },
//    "read_confidence": {
//        "agama": 0.99,
//        "alamat": 0.97372,
//        "berlakuHingga": 0.99,
//        "golonganDarah": 0.01,
//        "jenisKelamin": 0.99,
//        "kecamatan": 0.99,
//        "kelurahanDesa": 0.01,
//        "kewarganegaraan": 0.99,
//        "kotaKabupaten": 0.99,
//        "nama": 0.97762,
//        "nik": 0.97613,
//        "pekerjaan": 0.99,
//        "provinsi": 0.99,
//        "rtRw": 0.01,
//        "statusPerkawinan": 0.99,
//        "tanggalLahir": 0.97601,
//        "tempatLahir": 0.95777
//    },
//    "status": "SUCCESS"
//}
//'''
//
//def jsonSlurper = new JsonSlurper()
//def json1 = jsonSlurper.parseText(response1)
//def json2 = jsonSlurper.parseText(response2)
//
//def read1 = json1.read
//def read2 = json2.read
//
//read1.each { key, value1 ->
//	if (read2.containsKey(key)) {
//		def value2 = read2[key]
//
//		if (value1 != value2) {
//			println("Terdapat perbedaan di parameter $key: $value1 != $value2")
//		}
//	} else {
//		println("Parameter '$key' tidak ditemukan di respons dari API")
//	}
//}
