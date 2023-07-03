import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.By as By

'mencari directory excel'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

'mencari directory excel lain'
GlobalVariable.DataFilePath2 = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Simulasi/Simulasi Hitung Top Up Using Coupon.xlsx')

'mendapat jumlah kolom dari sheet Edit Profile'
int countColumnEdit = findTestData(ExcelPathTopUp).getColumnNumbers()

'deklarasi koneksi ke Database adins_apiaas_uat'
Connection conndev = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_esign'()

'deklarasi koneksi ke database eendigo_dev_uat'
Connection conndevUAT = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_devUat'()

'panggil fungsi login'
WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'TopUp', ('SheetName') : 'TopUp',
	('Path') : ExcelPathTopUp], FailureHandling.STOP_ON_FAILURE)

for (GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++) {
	
	'set penanda error menjadi 0'
	GlobalVariable.FlagFailed = 0
	
	'status kosong berhentikan testing, status selain unexecuted akan dilewat'
	if (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 1).length() == 0) {
		
		break
	}
	else if (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 1).equalsIgnoreCase('Unexecuted')) {
		
		'angka untuk menghitung data mandatory yang tidak terpenuhi'
		int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 5))
		
		'klik pada tombol menu'
		WebUI.click(findTestObject('Object Repository/Top Up/Page_Balance/spanMenu'))
		
		'klik pada menu isi saldo'
		WebUI.click(findTestObject('Object Repository/Top Up/Page_Balance/span_Isi Saldo'))
		
		'cek apakah tombol menu dalam jangkauan web'
		if (WebUI.verifyElementVisible(findTestObject(TombolSilang), FailureHandling.OPTIONAL)) {
			
			'klik pada tombol silang menu'
			WebUI.click(findTestObject(TombolSilang))
		}
		
		'deklarasi integer yang akan dipakai'
		int totalKatalon, modifyint, totalafter, ppnafter, grandTotalafter, cashbacknominal, cashbackbagian, hargasatuanUI, hargasatuanDB
		
		'deklarasi array untuk simpan data subtotal'
		ArrayList allsubtotal = [], tempDataPrice = [], dataDBInstruction = [], listServices = [], listJumlahisiUlang = []
		
		'cek ddl tipesaldo apakah sesuai dengan db'
		checkddlTipeSaldo(conndev)
		
		'cek ddl metode transfer sesuai dengan db'
		checkddlMetodeTransfer(conndev)
		
		'cek ddl bank sesuai dengan db'
		checkddlBankDestination(conndev)
		
		'input data tipe saldo yang diinginkan'
		WebUI.setText(findTestObject('Object Repository/Top Up/Page_Topup Balance/inputtipesaldo'), 
			findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 9))
		
		'enter pada ddl tipe saldo'
		WebUI.sendKeys(findTestObject('Object Repository/Top Up/Page_Topup Balance/inputtipesaldo'),
			 Keys.chord(Keys.ENTER))
		
		'klik pada luaran form'
		WebUI.click(findTestObject('Object Repository/Top Up/ClickForm'))
		
		'input data metode pembayaran'
		WebUI.setText(findTestObject('Object Repository/Top Up/Page_Topup Balance/inputMetodeBayar'),
			findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 10))
		
		'enter pada ddl metode pembayaran'
		WebUI.sendKeys(findTestObject('Object Repository/Top Up/Page_Topup Balance/inputMetodeBayar'),
			 Keys.chord(Keys.ENTER))
		
		'input data bank'
		WebUI.setText(findTestObject('Object Repository/Top Up/Page_Topup Balance/inputBank'),
			findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 11))
		
		'enter pada ddl bank'
		WebUI.sendKeys(findTestObject('Object Repository/Top Up/Page_Topup Balance/inputBank'),
			 Keys.chord(Keys.ENTER))
		
		'klik pada tambah layanan'
		WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/a_Tambah'))
		
		'jika muncul notifikasi'
		if (WebUI.verifyElementPresent(findTestObject('Object Repository/Top Up/NotifCatch'),
			GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
			
			'ambil teks notifikasi'
			String hasilNotif = WebUI.getText(findTestObject('Object Repository/Top Up/NotifCatch'))
			
			'klik tombol ok'
			WebUI.click(findTestObject('Object Repository/Top Up/button_OK'))
			
			'jika hasil notifikasi tidak sama dengan sukses'
			if (hasilNotif != 'Success') {
								
				'tulis error sesuai reason yang ditampilkan oleh error message'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Top Up', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
						hasilNotif)
				
				WebUI.refresh()
				
				continue
			}
		}
		
		'cek ddl activesaldo sesuai dengan DB'
		checkddlActiveSaldo(conndevUAT, findTestData(ExcelPathTopUp).getValue(2, 19))
		
		'klik tombol cancel'
		WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/button_Cancel'))
		
		'cek apakah perlu tambah layanan'
		if (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 16) == 'Yes') {
			
			'ambil data services dari excel'
			listServices = findTestData(ExcelPathTopUp).getValue(
				GlobalVariable.NumOfColumn, 12).split(';', -1)
				
			'ambil data jumlah isi ulang dari excel'
			listJumlahisiUlang = findTestData(ExcelPathTopUp).getValue(
				GlobalVariable.NumOfColumn, 13).split(';', -1)
				
			for (int i = 0; i < listServices.size(); i++) {
				
				'deklarasi string subtotal'
				String subtotal
				
				'deklarasi subtotal convert'
				int subtotalconvert
				
				'klik pada tambah layanan'
				WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/a_Tambah'))
				
				'input data saldo yang dipilih'
				WebUI.setText(findTestObject('Object Repository/Top Up/Page_Topup Balance/inputJenisSaldo'),
					listServices[i])
				
				'enter pada ddl saldo yang dipilih'
				WebUI.sendKeys(findTestObject('Object Repository/Top Up/Page_Topup Balance/inputJenisSaldo'),
					 Keys.chord(Keys.ENTER))
				
				'input data jumlah isi ulang yang dipilih'
				WebUI.setText(findTestObject('Object Repository/Top Up/Page_Topup Balance/inputamount'),
					listJumlahisiUlang[i])
				
				'klik pada form untuk kalkulasi subtotal'
				WebUI.click(findTestObject('Object Repository/Top Up/FormClick'))
				
				'ambil data dari harga satuan'
				hargasatuanUI = Integer.parseInt(WebUI.getAttribute(
					findTestObject('Object Repository/Top Up/Page_Topup Balance/inputunitPrice'), 'value'))
				
				'ambil harga satuan dari DB'
				hargasatuanDB = CustomKeywords.'topup.TopupVerif.getServicePrice'(conndevUAT, listServices[i])
				
				'ambil data dari subtotal'
				subtotal = WebUI.getAttribute(
					findTestObject('Object Repository/Top Up/Page_Topup Balance/inputsubTotal'), 'value').replace('.', '')
					
				'ubah subtotal ke integer'
				subtotalconvert = Integer.parseInt(subtotal)
				
				'jika harga layanan di ui dan db sesuai'
				if (hargasatuanUI == hargasatuanDB) {
					
					'jika perhitungan subtotal tidak sesuai'
					if (hargasatuanDB * Integer.parseInt(listJumlahisiUlang[i])
						!= subtotalconvert) {
						
						'tulis penghitungan otomatis error'
						CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Top Up', GlobalVariable.NumOfColumn,
							GlobalVariable.StatusFailed, (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 2) +
								';') + GlobalVariable.FailedReasonSubTotalCalc)
				
						GlobalVariable.FlagFailed = 1
					}
				}
				else {
					
					'tulis penghitungan otomatis error'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Top Up', GlobalVariable.NumOfColumn,
						GlobalVariable.StatusFailed, (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 2) +
							';') + GlobalVariable.FailedReasonHargaSatuan)
			
					GlobalVariable.FlagFailed = 1
				}
				
				'cek apakah button save bisa di-klik'
				if (WebUI.verifyElementNotHasAttribute(findTestObject('Object Repository/Top Up/Page_Topup Balance/button_Save'),
					'disabled', GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
				
					'klik pada objek untuk save'
					WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/button_Save'))
					
					'tambah subtotal ke array'
					allsubtotal.add(subtotalconvert)
					
					tempDataPrice.add(hargasatuanDB.toString())
				}
				else {
					
					'klik tombol silang pada services'
					WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/tombolXservices'))
					
					'tulis error penambahan layanan'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Top Up', GlobalVariable.NumOfColumn,
						GlobalVariable.StatusFailed, (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 2) +
							';') + GlobalVariable.FailedReasonAddServices)
			
					GlobalVariable.FlagFailed = 1
					
					continue
				}
			}
		}
		
		'lakukan penghitungan untuk subtotal'
		for (int i = 0; i < allsubtotal.size(); i++) {
			
			'tambahkan hasilnya ke totalkatalon'
			totalKatalon += allsubtotal[i]
			
		}
		
		'cek apakah total di katalon dan UI sesuai'
		checkVerifyEqualorMatch(WebUI.verifyEqual(totalKatalon,
			WebUI.getAttribute(findTestObject('Object Repository/Top Up/Page_Topup Balance/totalprice'),
				'value', FailureHandling.OPTIONAL).replace('.', '')), 'Total tidak sesuai')
		
		'ambil ppn dari DB'
		int ppnfromDB = Integer.parseInt(CustomKeywords.'topup.TopupVerif.getPPNvalue'(conndev))
		
		'pilihan untuk pakai kupon'
		if (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 17) == 'Yes') {
			
			'ambil detail coupon dari DB'
			ArrayList coupondetail = CustomKeywords.'topup.TopupVerif.getCouponDetail'(conndev,
				findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 14))
			
			'input kode kupon'
			WebUI.setText(findTestObject('Object Repository/Top Up/Page_Topup Balance/input_Kupon_kupon'),
				findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 14))
			
			'klik pada apply'
			WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/applyKupon'))
			
			'ambil status setelah apply kupon'
			String statusUI = WebUI.getAttribute(findTestObject('Object Repository/Top Up/DiskonUI'),
				'class', FailureHandling.OPTIONAL)
			
			'cek apakah kupon tidak sesuai'
			if (statusUI == 'help-block mt-1 text-success ng-star-inserted') {
				
				'cek apakah minimum pembayaran terpenuhi'
				if (Integer.parseInt(coupondetail[4]) <= totalKatalon) {
					
					'lihat jenis nilai kupon'
					if (coupondetail[1].equals('Percentage')) {
						
						'panggil fungsi kupon percentage'
						couponPercentage(listServices, tempDataPrice, listJumlahisiUlang, coupondetail, ppnfromDB)
						
					}
					else if (coupondetail[1].equals('Nominal')) {
						
						'panggil fungsi kupon nominal'
						couponNominal(listServices, tempDataPrice, listJumlahisiUlang, coupondetail, ppnfromDB)
					}
				}
				else {
					
					GlobalVariable.FlagFailed = 1
					
					'tulis error karena minimum payment tidak terpenuhi'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Top Up', GlobalVariable.NumOfColumn,
						GlobalVariable.StatusFailed, (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
							GlobalVariable.FailedReasonMinimumPayment)
					
					WebUI.refresh()
					
					continue
				}
			}
			else {
				
				'tulis error sesuai reason yang ditampilkan oleh error message'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Top Up', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
						WebUI.getText(findTestObject('Object Repository/Top Up/DiskonUI')))
				
				WebUI.refresh()
				
				continue
			}
		}
		
		'periksa apakah button disabled'
		if (WebUI.verifyElementNotHasAttribute(findTestObject('Object Repository/Top Up/Page_Topup Balance/button_Next'),
			'disabled', GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		
			'ambil data grandtotal'
			grandTotalafter = Integer.parseInt(WebUI.getAttribute(
				findTestObject('Object Repository/Top Up/Page_Topup Balance/grandTotal'),
					'value', FailureHandling.CONTINUE_ON_FAILURE).replaceAll('[^\\d]', ''))
		
			//kondisi if disini
			
			'klik pada tombol next'
			WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/button_Next'))
			
			'verify ada nya error'
			if (WebUI.verifyElementPresent(findTestObject('Object Repository/Top Up/ErrorCatch'),
				GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
			
				'tulis error sesuai reason yang ditampilkan oleh error message'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Top Up', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
						WebUI.getText(findTestObject('Object Repository/Top Up/ErrorCatch')))
				
				WebUI.refresh()
			
				continue
			}
			else if (WebUI.verifyElementPresent(findTestObject('Object Repository/Top Up/NotifCatch'),
				GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
				
				'ambil teks notifikasi'
				String hasilNotif = WebUI.getText(findTestObject('Object Repository/Top Up/NotifCatch'))
				
				'klik tombol ok'
				WebUI.click(findTestObject('Object Repository/Top Up/button_OK'))
				
				'jika hasil notifikasi tidak sama dengan sukses'
				if (hasilNotif != 'Success') {
									
					'tulis error sesuai reason yang ditampilkan oleh error message'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Top Up', GlobalVariable.NumOfColumn,
						GlobalVariable.StatusFailed, (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
							hasilNotif)
					
					WebUI.refresh()
					
					continue
				}
			}
			
			'ambil data nomor transaksi'
			String noTrx = WebUI.getText(findTestObject('Object Repository/Top Up/Page_Topup Balance/noTrxReceipt'),
				FailureHandling.OPTIONAL)
			
			'hilangkan string yang tidak diperlukan'
			String noTrxKatalon = noTrx.substring(noTrx.indexOf('Nomor Transaksi Anda : ') + 'Nomor Transaksi Anda : '.length())
			
			'ambil data instruction dari DB'
			dataDBInstruction =  CustomKeywords.'topup.TopupVerif.getInstructionDetail'(conndev, noTrxKatalon)
			
			'cek apakah kondisi cek storeDB aktif'
			if (GlobalVariable.KondisiCekDB == 'Yes') {
				
				'panggil fungsi storeDB'
				WebUI.callTestCase(findTestCase('Test Cases/Top Up/TopupStoreDB'), [('Path') : ExcelPathTopUp,  ('NoTrx') : noTrxKatalon],
					 FailureHandling.CONTINUE_ON_FAILURE)
			}
	
			'cek apakah total transaksi sesuai'
			checkVerifyEqualorMatch(WebUI.verifyEqual(grandTotalafter,
				WebUI.getText(findTestObject('Object Repository/Top Up/Page_Topup Balance/totalTranxReceipt'),
					FailureHandling.OPTIONAL).replace('Rp. ', '').replace('.', ''),
						FailureHandling.OPTIONAL), 'Total transaksi receipt')
			
			'cek apakah nomor transaksi sesuai'
			checkVerifyEqualorMatch(WebUI.verifyEqual(dataDBInstruction[0], noTrxKatalon,
						FailureHandling.OPTIONAL), 'Nomor transaksi receipt')
			
			'cek apakah bank sesuai'
			checkVerifyEqualorMatch(WebUI.verifyEqual(dataDBInstruction[1],
				WebUI.getText(findTestObject('Object Repository/Top Up/Page_Topup Balance/bankNameReceipt'),
					FailureHandling.OPTIONAL),
						FailureHandling.OPTIONAL), 'Nama Bank receipt')
			
			'cek apakah virtual account sesuai'
			checkVerifyEqualorMatch(WebUI.verifyEqual(dataDBInstruction[2],
				WebUI.getText(findTestObject('Object Repository/Top Up/Page_Topup Balance/VANumReceipt'),
					FailureHandling.OPTIONAL),
						FailureHandling.OPTIONAL), 'Virtual Account receipt')
			
			'cek apakah nama akun virtual sesuai'
			checkVerifyEqualorMatch(WebUI.verifyEqual(dataDBInstruction[3],
				WebUI.getText(findTestObject('Object Repository/Top Up/Page_Topup Balance/NameAccReceipt'),
					FailureHandling.OPTIONAL),
						FailureHandling.OPTIONAL), 'VA Name receipt')
			
			'klik pada clickable text'
			WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/LinkReceipt'))
		
			'klik tombol cari'
			WebUI.click(findTestObject('Object Repository/Top Up/Page_List Transaction History/button_Cari'))
			
			'panggil fungsi cek riwayat terakhir'
			getLastTrx(noTrxKatalon, conndev)
		}
		
		'jika tidak error dan mandatory lengkap'
		if (isMandatoryComplete == 0 && GlobalVariable.FlagFailed == 0) {
			
			'write to excel success'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Top Up', 0,
				GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
		}
	}
}

'tutup browser'
WebUI.closeBrowser()

def couponPercentage(ArrayList listServices, ArrayList tempDataPrice, ArrayList listJumlahisiUlang,
	ArrayList coupondetail, int ppnfromDB) {
	
	'deklarasi string untuk sheet'
	String SheetChoice
	
	'jika kupon merupakan tipe diskon'
	if (coupondetail[0].equals('Discount')) {
		
		'ambil sheet disc percent'
		SheetChoice = 'TopUp Disc Percent'
	}
	else {
		
		'ambil sheet cashback percent'
		SheetChoice = 'TopUp Cashback Percent'
	}
	
	'ambil diskon dari UI'
	String diskonUI = WebUI.getText(findTestObject('Object Repository/Top Up/DiskonUI'),
			FailureHandling.CONTINUE_ON_FAILURE).replace(coupondetail[2], '').replaceAll('[^\\d]', '')
	
	'looping tulis data ke excel'
	for (int i = 0; i < listServices.size(); i++) {
		
		'tulis layanan ke excel'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath2, SheetChoice, i+1,
			0, listServices[i])
		
		'tulis unit price ke excel'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath2, SheetChoice, i+1,
			1, tempDataPrice[i])
		
		'tulis qty ke excel'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath2, SheetChoice, i+1,
			2, listJumlahisiUlang[i])
		
		'tulis persentase diskon ke excel'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath2, SheetChoice, i+1,
			4, coupondetail[2])
	}
	
	'buka excel untuk refresh testdata'
	CustomKeywords.'customizeKeyword.openCloseExcel.openCloseFileWithRefreshVal'(GlobalVariable.DataFilePath2)
	
	'panggil fungsi pengecekan data'
	statschecking(ppnfromDB, Integer.parseInt(diskonUI), SheetChoice)
	
	'panggil fungsi penghapusan data column service'
	CustomKeywords.'writeToExcel.WriteExcel.emptyCellRange'(GlobalVariable.DataFilePath2, SheetChoice,
		1, 14, 0)
	
	'panggil fungsi penghapusan data column Price'
	CustomKeywords.'writeToExcel.WriteExcel.emptyCellRange'(GlobalVariable.DataFilePath2, SheetChoice,
		1, 14, 1)
	
	'panggil fungsi penghapusan data column Jumlah'
	CustomKeywords.'writeToExcel.WriteExcel.emptyCellRange'(GlobalVariable.DataFilePath2, SheetChoice,
		1, 14, 2)
	
	'panggil fungsi penghapusan data column Discount Percentage'
	CustomKeywords.'writeToExcel.WriteExcel.emptyCellRange'(GlobalVariable.DataFilePath2, SheetChoice,
		1, 14, 4)
	
	WebUI.delay(GlobalVariable.Timeout)
	
	'buka excel untuk refresh testdata'
	CustomKeywords.'customizeKeyword.openCloseExcel.openCloseFileWithRefreshVal'(GlobalVariable.DataFilePath2)
}

def couponNominal(ArrayList listServices, ArrayList tempDataPrice, ArrayList listJumlahisiUlang,
	ArrayList coupondetail, int ppnfromDB) {
	
	'deklarasi string untuk sheet'
	String SheetChoice
	
	'jika kupon merupakan tipe diskon'
	if (coupondetail[0].equals('Discount')) {
		
		'ambil sheet disc percent'
		SheetChoice = 'TopUp Disc Nominal'
	}
	else {
		
		'ambil sheet cashback percent'
		SheetChoice = 'TopUp Cashback Nominal'
	}
	
	'ambil diskon dari UI'
	String diskonUI = WebUI.getText(findTestObject('Object Repository/Top Up/DiskonUI'),
			FailureHandling.CONTINUE_ON_FAILURE).replaceAll('[^\\d]', '')
	
	'looping tulis data ke excel'
	for (int i = 0; i < listServices.size(); i++) {
		
		'tulis layanan ke excel'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath2, SheetChoice, i+1,
			0, listServices[i])
		
		'tulis unit price ke excel'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath2, SheetChoice, i+1,
			1, tempDataPrice[i])
		
		'tulis qty ke excel'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath2, SheetChoice, i+1,
			2, listJumlahisiUlang[i])
	}
	
	'tulis total discount ke excel'
	CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath2, SheetChoice, 17,
		1, diskonUI)
	
	'buka excel untuk refresh testdata'
	CustomKeywords.'customizeKeyword.openCloseExcel.openCloseFileWithRefreshVal'(GlobalVariable.DataFilePath2)
	
	'panggil fungsi pengecekan data'
	statschecking(ppnfromDB, Integer.parseInt(diskonUI), SheetChoice)
	
	'panggil fungsi penghapusan data column service'
	CustomKeywords.'writeToExcel.WriteExcel.emptyCellRange'(GlobalVariable.DataFilePath2, SheetChoice,
		1, 14, 0)
	
	'panggil fungsi penghapusan data column Price'
	CustomKeywords.'writeToExcel.WriteExcel.emptyCellRange'(GlobalVariable.DataFilePath2, SheetChoice,
		1, 14, 1)
	
	'panggil fungsi penghapusan data column Jumlah'
	CustomKeywords.'writeToExcel.WriteExcel.emptyCellRange'(GlobalVariable.DataFilePath2, SheetChoice,
		1, 14, 2)
	
	'panggil fungsi penghapusan data column Discount Percentage'
	CustomKeywords.'writeToExcel.WriteExcel.emptyCellRange'(GlobalVariable.DataFilePath2, SheetChoice,
		17, 17, 1)
	
	WebUI.delay(GlobalVariable.Timeout)
	
	'buka excel untuk refresh testdata'
	CustomKeywords.'customizeKeyword.openCloseExcel.openCloseFileWithRefreshVal'(GlobalVariable.DataFilePath2)

}

def checkddlTipeSaldo(Connection conndev) {
	
	'klik pada ddl tipe saldo'
	WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/span_tipesaldo'))
	
	'ambil list tipe saldo'
	def elementtipesaldo = DriverFactory.getWebDriver().findElements(By.xpath('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-topup-eendigo/div[2]/div/div/div/div/div/form/div[1]/div/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div'))
	
	'ambil hitungan TipeSaldo yang ada'
	int countWeb = (elementtipesaldo.size()) - 1
	
	'flag TipeSaldo sesuai'
	int isTipeSaldoFound = 0
	
	'ambil nama TipeSaldo dari DB'
	ArrayList<String> namaTipeSaldoDB = CustomKeywords.'topup.TopupVerif.getDDLTipeSaldo'(conndev)
	
	'nama-nama tipe saldo sedang aktif dari UI'
	ArrayList<String> namaTipeSaldoUI = []
	
	'hitung banyak data didalam array DB'
	int countDB = namaTipeSaldoDB.size()
	
	'jika hitungan di UI dan DB sesuai'
	if(countWeb == countDB){
		
		for (int i=1; i<=countWeb; i++) {
			
			'ambil object dari ddl'
			def modifyNamaTipeSaldo = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/modifyObject'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-topup-eendigo/div[2]/div/div/div/div/div/form/div[1]/div/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div["+(i+1)+"]/span", true)
			
			'tambahkan nama tipe saldo ke array'
			String data = WebUI.getText(modifyNamaTipeSaldo)
			namaTipeSaldoUI.add(data)
		}
		
		'cek setiap data di UI dengan data di DB sebagai pembanding'
		for (String tipe : namaTipeSaldoDB) {
			
			'jika ada data yang tidak terdapat pada arraylist yang lain'
			if (!namaTipeSaldoUI.contains(tipe)) {
				
				'ada data yang tidak match'
				isTipeSaldoFound = 0;
				'berhentikan loop'
				break;
			}
			'kondisi ini bisa ditemui jika data match'
			isTipeSaldoFound = 1
		}
			
	}
	else if (isTipeSaldoFound == 0 || countWeb != countDB) {
		
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Top Up', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonDDL)
	}
	
	'klik pada ddl tipe saldo'
	WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/span_tipesaldo'))
}

def checkddlMetodeTransfer(Connection conndev) {
	
	'klik pada ddl metodetransfer'
	WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/span_paymentmethod'))
	
	'ambil list metode transfer'
	def elementTrfMethod = DriverFactory.getWebDriver().findElements(By.xpath('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-topup-eendigo/div[2]/div/div/div/div/div/form/div[2]/div/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div'))
	
	'ambil hitungan TrfMethod yang ada'
	int countWeb = (elementTrfMethod.size()) - 1
	
	'flag TrfMethod sesuai'
	int isTrfMethodFound = 0
	
	'ambil nama TrfMethod dari DB'
	ArrayList<String> namaTrfMethodDB = CustomKeywords.'topup.TopupVerif.getDDLMetodeTrf'(conndev)
	
	'nama-nama tipe saldo sedang aktif dari UI'
	ArrayList<String> namaTrfMethodUI = []
	
	'hitung banyak data didalam array DB'
	int countDB = namaTrfMethodDB.size()
	
	'jika hitungan di UI dan DB sesuai'
	if (countWeb == countDB) {
		
		for (int i=1; i<=countWeb; i++) {
			
			'ambil object dari ddl'
			def modifyNamaTrfMethod = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/modifyObject'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-topup-eendigo/div[2]/div/div/div/div/div/form/div[2]/div/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div["+(i+1)+"]/span", true)
			
			'tambahkan nama tipe saldo ke array'
			String data = WebUI.getText(modifyNamaTrfMethod)
			namaTrfMethodUI.add(data)
		}
		
		'cek setiap data di UI dengan data di DB sebagai pembanding'
		for (String tipe : namaTrfMethodDB) {
			
			'jika ada data yang tidak terdapat pada arraylist yang lain'
			if (!namaTrfMethodUI.contains(tipe)) {
				
				'ada data yang tidak match'
				isTrfMethodFound = 0;
				'berhentikan loop'
				break;
			}
			'kondisi ini bisa ditemui jika data match'
			isTrfMethodFound = 1
		}
			
	}
	else if (isTrfMethodFound == 0 || countWeb != countDB) {
		
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Top Up', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonDDL)
	}
	
	'klik pada ddl metodetransfer'
	WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/span_paymentmethod'))
}

def statschecking(int ppnfromDB, int diskonUI, String SheetChoice) {
	
	'inisialisasi variabel integer'
	String total, ppn, grandTotal, ppnDB, diskonui, path
	
	'ubah ppn integer ke string'
	ppnDB = ppnfromDB.toString()
	
	'ubah diskon integer ke string'
	diskonui = diskonUI.toString()
	
	if (SheetChoice == 'TopUp Disc Percent') {
		
		'ubah string path menjadi disc percent'
		path = ExcelPathDiscPerc
		
	}
	else if (SheetChoice == 'TopUp Disc Nominal') {
		
		'ubah string path menjadi disc nominal'
		path = ExcelPathDiscNom
		
	}
	else if (SheetChoice == 'TopUp Cashback Percent') {
		
		'ubah string path menjadi cashback percentage'
		path = ExcelPathCashPerc
	
	}
	else if (SheetChoice == 'TopUp Cashback Nominal') {
	
		'ubah string path menjadi disc nominal'
		path = ExcelPathCashNom
		
	}
	
//	'ambil data total'
//	total = Integer.parseInt(WebUI.getAttribute(
//		findTestObject('Object Repository/Top Up/Page_Topup Balance/totalprice'),
//		'value', FailureHandling.CONTINUE_ON_FAILURE))
	
	'ambil data ppn'
	ppn = WebUI.getAttribute(
		findTestObject('Object Repository/Top Up/Page_Topup Balance/PPN11'),
		'value', FailureHandling.CONTINUE_ON_FAILURE).replaceAll('[^\\d]', '')
	
	'ambil data grandtotal'
	grandTotal = WebUI.getAttribute(
		findTestObject('Object Repository/Top Up/Page_Topup Balance/grandTotal'),
		'value', FailureHandling.CONTINUE_ON_FAILURE).replaceAll('[^\\d]', '')
	
//	'cek penghitungan total di katalon dan web'
//	checkVerifyEqualorMatch(WebUI.verifyEqual(total,
//		findTestData(ExcelPathSimulasi).getValue(2, 17),
//			FailureHandling.CONTINUE_ON_FAILURE), 'Penghitungan Total salah')
	
	'cek penghitungan diskon di katalon dan web'
	checkVerifyEqualorMatch(WebUI.verifyEqual(diskonui,
		findTestData(path).getValue(2, 18),
			FailureHandling.CONTINUE_ON_FAILURE), 'Penghitungan akhir diskon salah')
	
	'cek penghitungan ppn di katalon dan web'
	checkVerifyEqualorMatch(WebUI.verifyEqual(ppn,
		findTestData(path).getValue(2, 19),
			FailureHandling.CONTINUE_ON_FAILURE), 'Penghitungan PPN salah')
	
	'cek penghitungan grandtotal di katalon dan web'
	checkVerifyEqualorMatch(WebUI.verifyEqual(grandTotal,
		findTestData(path).getValue(2, 20),
			FailureHandling.CONTINUE_ON_FAILURE), 'Penghitungan GrandTotal salah')
}

def checkddlBankDestination(Connection conndev) {
	
	'klik pada ddl bank destination'
	WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/span_bankdestination'))
	
	'ambil list bank destination'
	def elementBankDest = DriverFactory.getWebDriver().findElements(By.xpath('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-topup-eendigo/div[2]/div/div/div/div/div/form/div[3]/div/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div'))
	
	'ambil hitungan BankDest yang ada'
	int countWeb = (elementBankDest.size()) - 1
	
	'flag BankDest sesuai'
	int isBankDestFound = 0
	
	'ambil nama BankDest dari DB'
	ArrayList<String> namaBankDestDB = CustomKeywords.'topup.TopupVerif.getDDLBank'(conndev)
	
	'nama-nama tipe saldo sedang aktif dari UI'
	ArrayList<String> namaBankDestUI = []
	
	'hitung banyak data didalam array DB'
	int countDB = namaBankDestDB.size()
	
	'jika hitungan di UI dan DB sesuai'
	if (countWeb == countDB) {
		
		for (int i=1; i<=countWeb; i++) {
			
			'ambil object dari ddl'
			def modifyNamaBankDest = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/modifyObject'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-topup-eendigo/div[2]/div/div/div/div/div/form/div[3]/div/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div["+(i+1)+"]/span", true)
			
			'tambahkan nama tipe saldo ke array'
			String data = WebUI.getText(modifyNamaBankDest)
			namaBankDestUI.add(data)
		}
		
		'cek setiap data di UI dengan data di DB sebagai pembanding'
		for (String tipe : namaBankDestDB) {
			
			'jika ada data yang tidak terdapat pada arraylist yang lain'
			if (!namaBankDestUI.contains(tipe)) {
				
				'ada data yang tidak match'
				isBankDestFound = 0;
				'berhentikan loop'
				break;
			}
			'kondisi ini bisa ditemui jika data match'
			isBankDestFound = 1
		}
			
	}
	else if (isBankDestFound == 0 || countWeb != countDB) {
		
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Top Up', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonDDL)
	}
	
	'klik pada ddl bank destination'
	WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/span_bankdestination'))
}

def checkddlActiveSaldo(Connection conndevUAT, String email) {
	
	'klik pada ddl saldo pada layanan'
	WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/span_saldodipilih'))
	
	'ambil list saldo yang aktif'
	def elementActiveSaldo = DriverFactory.getWebDriver().findElements(By.xpath('/html/body/ngb-modal-window/div/div/app-modal-add-service/form/div[2]/div[1]/div/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div'))
	
	'ambil hitungan ActiveSaldo yang ada'
	int countWeb = (elementActiveSaldo.size()) - 1
	
	'flag ActiveSaldo sesuai'
	int isActiveSaldoFound = 0
	
	'ambil nama ActiveSaldo dari DB'
	ArrayList<String> namaActiveSaldoDB = CustomKeywords.'topup.TopupVerif.getDDLSaldoactive'(conndevUAT, email)
	
	'nama-nama tipe saldo sedang aktif dari UI'
	ArrayList<String> namaActiveSaldoUI = []
	
	'hitung banyak data didalam array DB'
	int countDB = namaActiveSaldoDB.size()

	'jika hitungan di UI dan DB sesuai'
	if (countWeb == countDB) {
		
		for (int i = 1; i<countWeb; i++) {
			
			'ambil object dari ddl'
			def modifyNamaActiveSaldo = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/modifyObject'), 'xpath', 'equals', "/html/body/ngb-modal-window/div/div/app-modal-add-service/form/div[2]/div[1]/div/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div["+(i+1)+"]/span", true)
			
			'tambahkan nama tipe saldo ke array'
			String data = WebUI.getText(modifyNamaActiveSaldo)
			namaActiveSaldoUI.add(data)
		}
		
		'cek setiap data di UI dengan data di DB sebagai pembanding'
		for (String tipe : namaActiveSaldoDB) {
			
			'jika ada data yang tidak terdapat pada arraylist yang lain'
			if (!namaActiveSaldoUI.contains(tipe)) {
				
				'ada data yang tidak match'
				isActiveSaldoFound = 0;
				'berhentikan loop'
				break;
			}
			'kondisi ini bisa ditemui jika data match'
			isActiveSaldoFound = 1
		}
			
	}
	else if (isActiveSaldoFound == 0 || countWeb != countDB) {
		
		GlobalVariable.FlagFailed = 1
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Top Up', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
		GlobalVariable.FailedReasonDDL)
	}
	
	'klik pada ddl saldo pada layanan'
	WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/span_saldodipilih'))
}


'ambil no. transaksi pada tabel'
def getLastTrx(String noTrxKatalon, Connection conndev) {
	
	'periksa apakah tombol skip to last page ada'
	if (WebUI.verifyElementVisible(findTestObject('Object Repository/Top Up/Page_List Transaction History/lastPage'),
		 FailureHandling.OPTIONAL)) {
	
	 	'klik ubah ke halaman terakhir'
		 WebUI.click(findTestObject('Object Repository/Top Up/Page_List Transaction History/lastPage'))
	}
	
	'ambil alamat trxnumber'
	def variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-list-transaction-history > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))
	
	'banyaknya row table'
	int lastIndex = variable.size()
	
	'modifikasi alamat object trxnumber'
	def modifytrxnumber = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/Page_List Transaction History/noTranx'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[" + (lastIndex) + "]/datatable-body-row/div[2]/datatable-body-cell[1]/div/p", true)
	
	'modifikasi object tgl transaksi'
	def modifytgltrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/Page_List Transaction History/tglTranx'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[" + (lastIndex) + "]/datatable-body-row/div[2]/datatable-body-cell[2]/div/span", true)

	'modifikasi object tipe saldo'
	def modifytipesaldo = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/Page_List Transaction History/tipeSaldoTranx'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[" + (lastIndex) + "]/datatable-body-row/div[2]/datatable-body-cell[3]/div/p", true)

	'modifikasi object metode transfer'
	def modifymetodetrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/Page_List Transaction History/metodeTranx'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[" + (lastIndex) + "]/datatable-body-row/div[2]/datatable-body-cell[5]/div/p", true)

	'modifikasi object status transaksi'
	def modifystatustrx = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/Page_List Transaction History/statusTranx'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[" + (lastIndex) + "]/datatable-body-row/div[2]/datatable-body-cell[6]/div/p", true)

	'ambil data table dari db'
	ArrayList result = CustomKeywords.'topup.TopupVerif.getRiwayatTabelData'(conndev, noTrxKatalon)
		
	'kembalikan nomor transaksi'
	int arrayIndex = 0
	
	'verify trxnum transaksi ui = db'
	checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifytrxnumber), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), 'TrxNum Riwayat')
	
	'verify tgltrx transaksi ui = db'
	checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifytgltrx), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), 'Tanggal Trx Riwayat')
	
	'verify tipe saldo transaksi ui = db'
	checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifytipesaldo), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), 'TrxTipeSaldo Riwayat')

	'verify metode transaksi ui = db'
	checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifymetodetrx), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), 'MetodeTrx Riwayat')
	
	'verify status transaksi ui = db'
	checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifystatustrx), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), 'StatusTrx Riwayat')
	
	'modifikasi object tombol detail'
	def modifytomboldetail = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/modifyObject'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-transaction-history/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[" + (lastIndex) + "]/datatable-body-row/div[2]/datatable-body-cell[7]/div/a[1]/em", true)

	'klik pada tombol detail'
	WebUI.click(modifytomboldetail)
	
	'ambil data table dari db'
	result = CustomKeywords.'topup.TopupVerif.getRiwayatDetail'(conndev, noTrxKatalon)
	
	'ambil alamat trxnumber'
	def variabledetail = DriverFactory.getWebDriver().findElements(By.cssSelector('body > ngb-modal-window > div > div > app-transaction-history-detail > div > div.modal-body > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))
	
	'arraylist untuk tampung detail'
	ArrayList detail = []
	
	'lakukan loop untuk ambil data detail'
	for (int i = 1; i <= variabledetail.size(); i++) {
		
		'modifikasi object layanan transaksi'
		def modifylayanandetail = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/Page_List Transaction History/layananDetail'),'xpath','equals', "/html/body/ngb-modal-window/div/div/app-transaction-history-detail/div/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[" + i + "]/datatable-body-row/div[2]/datatable-body-cell[1]/div/p", true)
	
		'modifikasi object unit price transaksi'
		def modifyunitpricedetail = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/Page_List Transaction History/unitpriceDetail'),'xpath','equals', "/html/body/ngb-modal-window/div/div/app-transaction-history-detail/div/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[" + i + "]/datatable-body-row/div[2]/datatable-body-cell[2]/div", true)

		'modifikasi object jumlah transaksi'
		def modifyjumlahdetail = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/Page_List Transaction History/jumlahDetail'),'xpath','equals', "/html/body/ngb-modal-window/div/div/app-transaction-history-detail/div/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[" + i + "]/datatable-body-row/div[2]/datatable-body-cell[3]/div", true)

		'modifikasi object subtotal transaksi'
		def modifysubtotaldetail = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/Page_List Transaction History/subtotalDetail'),'xpath','equals', "/html/body/ngb-modal-window/div/div/app-transaction-history-detail/div/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[" + i + "]/datatable-body-row/div[2]/datatable-body-cell[4]/div", true)
		
		'tambah hasil get text layanan ke array'
		detail.add(WebUI.getText(modifylayanandetail))
		
		'tambah hasil get text harga satuan ke array'
		detail.add(WebUI.getText(modifyunitpricedetail).replaceAll('[^\\d]', ''))
		
		'tambah hasil get text qty ke array'
		detail.add(WebUI.getText(modifyjumlahdetail))
		
		'tambah hasil get text subtotal ke array'
		detail.add(WebUI.getText(modifysubtotaldetail).replaceAll('[^\\d]', ''))
	}
	
	'cek apakah ada data yang tidak sesuai'
	for (int j = 0; j < result.size(); j++) {
		
		'verify layanan detail transaksi ui = db'
		checkVerifyEqualorMatch(WebUI.verifyMatch(detail[j], result[j], false, FailureHandling.CONTINUE_ON_FAILURE), 'Detail tidak sesuai')
	}
	
	'klik tombol silang'
	WebUI.click(findTestObject('Object Repository/Top Up/Page_List Transaction History/tombolX'))
}

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
	if (isMatch == false) {
		
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		GlobalVariable.FlagFailed = 1
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Top Up', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
				GlobalVariable.FailedReasonVerifyEqualorMatch + reason)
	}
}
