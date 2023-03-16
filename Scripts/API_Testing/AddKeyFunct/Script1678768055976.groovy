import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.writeExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mendapat jumlah kolom dari sheet Edit Profile'
int CountColumnEdit = findTestData(ExcelPathAPIKey).getColumnNumbers()

'angka untuk menghitung data mandatory yang tidak terpenuhi'
int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 4))

for (GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn <= CountColumnEdit; (GlobalVariable.NumOfColumn)++) 
{
    WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'Key'], FailureHandling.STOP_ON_FAILURE)

    'pada delay, lakukan captcha secara manual'
    WebUI.delay(10)

    'verifikasi button login bisa di klik'
    if (WebUI.verifyElementClickable(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'), 
        FailureHandling.OPTIONAL)) 
	{
        'klik pada button login'
        WebUI.click(findTestObject('Object Repository/API_KEY/Page_Login - eendigo Platform/button_Lanjutkan Perjalanan Anda'))
    } 
	else 
	{
        'penanda ada error, status sukses tidak akan ditulis'
        GlobalVariable.FlagFailed = 1

        'tulis status dan reason error ke excel'
        CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('API KEY', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed, 
            (findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.FailedReasonSubmitError)
    }
    
	'klik tombol +Baru'
    WebUI.click(findTestObject('Object Repository/API_KEY/Page_Api Key List/a_Baru'))

	'input data API KEY name'
    WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Add Api Key/input__apiKeyName'), findTestData(ExcelPathAPIKey).getValue(
            GlobalVariable.NumOfColumn, 10))

	'pilih jenis API KEY'
    WebUI.selectOptionByLabel(findTestObject('Object Repository/API_KEY/Page_Add Api Key/select_Tipe API KeyPRODUCTIONTRIAL'), 
        findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 11), false)

	'cek jika data mandatory di excel tidak lengkap'
	if(isMandatoryComplete > 0)
	{
		'penanda ada error, status sukses tidak akan ditulis'
		GlobalVariable.FlagFailed = 1

		'write status failed karena data mandatory tidak valid'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('API Key', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
		(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.FailedReasonMandatory)
	}
	
	'klik pada tombol simpan'
    WebUI.click(findTestObject('Object Repository/API_KEY/Page_Add Api Key/button_Simpan'))
	
	WebUI.delay(2)
	
	'verifikasi tombol "YA" terdapat di layar'
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/API_KEY/Page_Add Api Key/button_Ya'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		
		'klik pada button YA jika muncul pop-up'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Add Api Key/button_Ya'))
		
		'verifikasi ke database untuk data yang ditambahkan'
		WebUI.callTestCase(findTestCase('Test Cases/API_Testing/VerifyDBAddKey'), [:], FailureHandling.STOP_ON_FAILURE)
	}
	else
	{
		'buat flag failed menjadi 1 agar tidak menulis status sukses pada excel'
		GlobalVariable.FlagFailed = 1

		'tulis error ke excel'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('API KEY', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
			(findTestData(ExcelPathAPIKey).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.FailedReasonSubmitError)
	}
	
	'klik tombol ok pada success alert'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Add Api Key/button_OK'))
	
	'panggil fungsi Edit API KEY'
	WebUI.callTestCase(findTestCase('Test Cases/API_Testing/EditKeyFunct'), [:], FailureHandling.STOP_ON_FAILURE)
	
	'kondisi jika tidak ada error'
	if(GlobalVariable.FlagFailed == 0)
	{
		'tulis status sukses pada excel'
		CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('API KEY', GlobalVariable.NumOfColumn, GlobalVariable.StatusSuccess,
		GlobalVariable.SuccessReason)
	}
}

