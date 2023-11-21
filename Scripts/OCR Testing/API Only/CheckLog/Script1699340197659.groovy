import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.keyword.builtin.VerifyAlertNotPresentKeyword
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.JavascriptExecutor

'lakukan check log bucket standard'
checkProcedure('Standard')

'klik pada button list customer'
WebUI.click(findTestObject('OCR Testing/checkLog/button_ListCustomer'))

'refresh laman web'
WebUI.refresh()

'ganti fokus robot ke tab baru'
WebUI.switchToWindowIndex(1)

'lakukan check log bucket coldline'
checkProcedure('Coldline')

'klik pada button list customer'
WebUI.click(findTestObject('OCR Testing/checkLog/button_ListCustomer'))

'refresh laman web'
WebUI.refresh()

//'navigasi ke url bucket coldline'
//WebUI.navigateToUrl(findTestData(ExcelPathLogin).getValue(8, 8))

'ganti fokus robot ke tab standard'
WebUI.switchToWindowIndex(0)

//'navigasi ke url bucket standard'
//WebUI.navigateToUrl(findTestData(ExcelPathLogin).getValue(8, 7))

def checkProcedure(String bucketType) {
	
	if (GlobalVariable.onlyFirstRun == 0) {
		'input filter OCR'
		WebUI.setText(findTestObject('OCR Testing/checkLog/inputSearchFilter'),
			findTestData(ExcelPathLogin).getValue(8, 11))
		
		WebUI.delay(1)
		
		'klik pada objek paling atas'
		WebUI.click(findTestObject('OCR Testing/checkLog/topResult'))
		
		'input filter OCR'
		WebUI.setText(findTestObject('OCR Testing/checkLog/inputSearchFilter'),
			TenantCode)
		
		WebUI.delay(1)
		
		'klik pada objek paling atas'
		WebUI.click(findTestObject('OCR Testing/checkLog/topResult'))
		
		'input filter OCR'
		WebUI.setText(findTestObject('OCR Testing/checkLog/inputSearchFilter'),
			OCRType)
		
		WebUI.delay(1)
		
		'klik pada objek paling atas'
		WebUI.click(findTestObject('OCR Testing/checkLog/topResult'))
		
		'input filter OCR'
		WebUI.setText(findTestObject('OCR Testing/checkLog/inputSearchFilter'),
			'ListCustomer')
		
		WebUI.delay(1)
		
		'klik pada objek paling atas'
		WebUI.click(findTestObject('OCR Testing/checkLog/topResult'))
		
		'jika sudah coldline, maka tidak perlu input lagi'
		if (bucketType == 'Coldline') {
			
			'ubah status bahwa first run sudah dilakukan'
			GlobalVariable.onlyFirstRun = 1
		}
	}
	
	'input filter OCR'
	WebUI.setText(findTestObject('OCR Testing/checkLog/inputSearchFilter'),
		Tanggal)
	
	WebUI.delay(1)
	
	'cek apakah log by date berhasil masuk'
	if (WebUI.waitForElementPresent(findTestObject('OCR Testing/checkLog/topResult'), 5, FailureHandling.OPTIONAL)) {
		
		'klik pada objek paling atas'
		WebUI.click(findTestObject('OCR Testing/checkLog/topResult'))
	} else {
		
		'tulis kondisi gagal'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet,
			GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
				(findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 2) + ';') + 'Log Not Found di bucket ' + bucketType)
		
		return
	}
	
	if (OCRType == 'BPKBExtractor' && bucketType == 'Coldline') {
		
		for (i = 2; i <= 3; i++) {
			'input filter OCR'
			WebUI.setText(findTestObject('OCR Testing/checkLog/inputSearchFilter'),
				'hal' + (i))
			
			WebUI.delay(1)
			
			'klik pada objek paling atas'
			WebUI.click(findTestObject('OCR Testing/checkLog/topResult'))
			
			'input filter OCR'
			WebUI.setText(findTestObject('OCR Testing/checkLog/inputSearchFilter'),
				TimeOCR)
			
			WebUI.delay(1)
			
			verifyObjectPresent(bucketType, 'Halaman ' + (i.toString()))
			
			'klik tombol kembali'
			WebUI.click(findTestObject('OCR Testing/checkLog/backButton'))
		}
		
	} else {
		for (i = 0 ; i < 2; i++) {
			'input filter OCR'
			WebUI.setText(findTestObject('OCR Testing/checkLog/inputSearchFilter'),
				TimeOCR)
			
			WebUI.delay(1)
		}
		verifyObjectPresent(bucketType, OCRType)
	}
}

def verifyObjectPresent(String bucketType, String inc) {
	if (WebUI.waitForElementPresent(findTestObject('OCR Testing/checkLog/topResultFinal'), 5, FailureHandling.OPTIONAL)) {
		'kalau result null'
		if (!WebUI.getText(findTestObject('OCR Testing/checkLog/topResultFinal'), FailureHandling.OPTIONAL).contains(TimeOCR)) {
			'tulis kondisi gagal'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet,
				GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
					(findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 2) + ';') + 'Nama Log tidak sesuai pada folder halaman' + (inc) + ' di Bucket ' + bucketType)
		}
		
	} else {
		'tulis kondisi gagal'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'(sheet,
			GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
				(findTestData(ExcelPathOCRTesting).getValue(GlobalVariable.NumOfColumn, 2) + ';') + 'Log Not Found di bucket ' + bucketType)
	}
}