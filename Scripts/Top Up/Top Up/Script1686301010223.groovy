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

'mencari directory excel\r\n'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

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
		int totalKatalon, modifyint, totalafter, ppnafter, grandTotalafter, ppnbefore, grandTotalbefore, cashbacknominal
		int cashbackbagian
		
		'deklarasi array untuk simpan data subtotal'
		ArrayList allsubtotal = []
		ArrayList newqty = []
		ArrayList dataDBInstruction = []
		
		'declare isMmandatory Complete'
		int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 5))
		
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
		
		'cek apakah perlu tambah layanan'
		if (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 16) == 'Yes') {
			
			'ambil data services dari excel'
			ArrayList listServices = findTestData(ExcelPathTopUp).getValue(
				GlobalVariable.NumOfColumn, 12).split(';', -1)
				
			'ambil data services dari excel'
			ArrayList listJumlahisiUlang = findTestData(ExcelPathTopUp).getValue(
				GlobalVariable.NumOfColumn, 13).split(';', -1)
				
			for (int i = 0; i <= listServices.size(); i++) {
				
				'deklarasi string subtotal'
				String subtotal
				
				'deklarasi subtotal convert'
				int subtotalconvert
				
				'klik pada tambah layanan'
				WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/a_Tambah'))
				
				'cek ddl activesaldo sesuai dengan DB'
				checkddlActiveSaldo(conndevUAT, findTestData(ExcelPathTopUp).getValue(2, 19))
				
				'input data saldo yang dipilih'
				WebUI.setText(findTestObject('Object Repository/Top Up/Page_Topup Balance/inputJenisSaldo'),
					listServices[i])
				
				'enter pada ddl saldo yang dipilih'
				WebUI.sendKeys(findTestObject('Object Repository/Top Up/Page_Topup Balance/inputJenisSaldo'),
					 Keys.chord(Keys.ENTER))
				
				'input data jumlah isi ulang yang dipilih'
				WebUI.setText(findTestObject('Object Repository/Top Up/Page_Topup Balance/inputJenisSaldo'),
					listJumlahisiUlang[i])
				
				'ambil data dari harga satuan'
				int hargasatuanUI = Integer.parseInt(WebUI.getAttribute(
					findTestObject('Object Repository/Top Up/Page_Topup Balance/inputunitPrice'), 'value'))
				
				int hargasatuanDB = CustomKeywords.'topup.TopupVerif.getServicePrice'(conndev,
					listServices[i])
				
				'ambil data dari subtotal'
				subtotal = WebUI.getAttribute(
					findTestObject('Object Repository/Top Up/Page_Topup Balance/inputsubTotal'), 'value').replace('.', '')
					
				'ubah subtotal ke integer'
				subtotalconvert = Integer.parseInt(subtotal)
				
				'jika harga layanan di ui dan db sesuai'
				if (hargasatuanUI == hargasatuanDB) {
					
					'jika perhitungan subtotal tidak sesuai'
					if (hargasatuanDB * Integer.parseInt(findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 13))
						!= subtotal) {
						
						'tulis penghitungan otomatis error'
						CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('TopUp', GlobalVariable.NumOfColumn,
							GlobalVariable.StatusFailed, (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 2) +
								';') + GlobalVariable.FailedReasonSubTotalCalc)
				
						GlobalVariable.FlagFailed = 1
					}
				}
				else {
					
					'tulis penghitungan otomatis error'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('TopUp', GlobalVariable.NumOfColumn,
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
				}
				else {
					
					'klik tombol silang pada services'
					WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/tombolXservices'))
					
					'tulis error penambahan layanan'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('TopUp', GlobalVariable.NumOfColumn,
						GlobalVariable.StatusFailed, (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 2) +
							';') + GlobalVariable.FailedReasonAddServices)
			
					GlobalVariable.FlagFailed = 1
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
				'value', FailureHandling.OPTIONAL)), 'Total tidak sesuai')
		
		'ambil ppn dari DB'
		int ppnfromDB = CustomKeywords.'topup.TopupVerif.getPPNvalue'(conndev)
		
		'hitung ppn sebelumnya'
		ppnbefore = ppnfromDB * totalKatalon / 100
		
		'hitung grand total'
		grandTotalbefore = ppnbefore + totalKatalon
		
		'panggil fungsi pengecekan data'
		statschecking(conndev, ppnfromDB, totalKatalon, ppnbefore, grandTotalbefore)
		
		'pilihan untuk pakai kupon'
		if (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 17) == 'Yes') {
			
			'ambil detail coupon dari DB'
			ArrayList coupondetail = CustomKeywords.'topup.TopupVerif.getCouponDetail'(conndev,
				findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 14))
			
			'input kode kupon'
			WebUI.setText(findTestObject('Object Repository/Top Up/Page_Topup Balance/input_Kupon_kupon'),
				findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 14))
			
			'cek apakah tipe kupon adalah diskon'
			if (coupondetail[0].equals('Discount')) {
				
				'cek apakah minimum pembayaran terpenuhi'
				if (Integer.parseInt(coupondetail[4]) <= totalKatalon) {
					
					'lihat jenis nilai kupon'
					if (coupondetail[1].equals('Percentage')) {
						
						'hitung persentase discount dari total harga'
						totalafter = totalKatalon - (totalKatalon * coupondetail[2] / 100)
						
						'hitung ppn setelah dikenakan diskon'
						ppnafter = totalafter * ppnfromDB / 100
						
						'hitung grand total setelah diskon'
						grandTotalafter = totalafter + ppnafter
						
						'panggil fungsi pengecekan data'
						statschecking(conndev, ppnfromDB, totalafter, ppnafter, grandTotalafter)
					}
					else if (coupondetail[1].equals('Nominal')) {
						
						'hitung nominal diskon dari total harga'
						totalafter = totalKatalon - coupondetail[2]
						
						'itung ppn setelah dikenakan diskon'
						ppnafter = totalafter * ppnfromDB / 100
						
						'hitung grand total setelah diskon'
						grandTotalafter = totalafter + ppnafter
						
						'panggil fungsi pengecekan data'
						statschecking(conndev, ppnfromDB, totalafter, ppnafter, grandTotalafter)
					}
				}
				else {
					
					GlobalVariable.FlagFailed = 1
					
					'tulis error karena minimum payment tidak terpenuhi'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('TopUp', GlobalVariable.NumOfColumn,
						GlobalVariable.StatusFailed, (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
							GlobalVariable.FailedReasonMinimumPayment)
					
				}
			}
			else if (coupondetail[0].equals('Cashback')) {
				
				'cek apakah minimum pembayaran terpenuhi'
				if (Integer.parseInt(coupondetail[4]) <= totalKatalon) {
					
					'lihat jenis nilai kupon'
					if (coupondetail[1].equals('Percentage')) {
						
						'lakukan loop untuk tambah data ke array qty'
						for (int i = 0; i < allsubtotal.size ; i++) {
							
							'hitung persentase cashback dari total harga'
							newqty.add(allsubtotal[i] + (allsubtotal[i]*coupondetail[2]/100))
						}
						
						'hitung total setelah apply coupon'
						totalafter = totalKatalon
						
						'hitung ppn setelah dikenakan cashback'
						ppnafter = totalafter * ppnfromDB / 100
						
						'hitung grand total setelah cashback'
						grandTotalafter = totalafter + ppnafter
						
						'panggil fungsi pengecekan data'
						statschecking(conndev, ppnfromDB, totalafter, ppnafter, grandTotalafter)
					}
					else if (coupondetail[1].equals('Nominal')) {
						
						for (int i = 0; i < allsubtotal.size(); i++) {
							
							'hitung bagian cashback dari total harga'
							cashbackbagian = allsubtotal[i]/totalKatalon
							
							'simpan data hitungan bagian sementara'
							modifyint = cashbackbagian * coupondetail[2]
							
							'tambahkan ke nominal cashback'
							cashbacknominal = modifyint / cashbackbagian
							
							'masukkan saldo baru ke array'
							newqty.add((cashbacknominal + allsubtotal[i]))
						}
						
						'hitung total setelah pengaplikasian kupon'
						totalafter = totalKatalon
						
						'itung ppn setelah dikenakan cashback'
						ppnafter = totalafter * ppnfromDB / 100
						
						'hitung grand total setelah cashback'
						grandTotalafter = totalafter + ppnafter
						
						'panggil fungsi pengecekan data'
						statschecking(conndev, ppnfromDB, totalafter, ppnafter, grandTotalafter)
					}
				}
				else {
					
					GlobalVariable.FlagFailed = 1
					
					'tulis error karena minimum payment tidak terpenuhi'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('TopUp', GlobalVariable.NumOfColumn,
						GlobalVariable.StatusFailed, (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
							GlobalVariable.FailedReasonMinimumPayment)
					
				}
			}
		}
		
		'periksa apakah button disabled'
		if (WebUI.verifyElementNotHasAttribute(findTestObject('Object Repository/Top Up/Page_Topup Balance/button_Next'),
			'disabled', GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
		
			'klik pada tombol next'
			WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/button_Next'))
			
			'ambil data nomor transaksi'
			String noTrxKatalon = WebUI.getAttribute(
				findTestObject('Object Repository/Top Up/Page_Topup Balance/noTrxReceipt'),
				'value', FailureHandling.OPTIONAL).replace('Nomor Transaksi Anda : ', '')
			
			'ambil data instruction dari DB'
			dataDBInstruction =  CustomKeywords.'topup.TopupVerif.getInstructionDetail'(conndev, noTrxKatalon)
			
			'cek apakah total transaksi sesuai'
			checkVerifyEqualorMatch(WebUI.verifyEqual(grandTotalafter,
				WebUI.getAttribute(findTestObject('Object Repository/Top Up/Page_Topup Balance/totalTranxReceipt'), 
					'value', FailureHandling.OPTIONAL).replace('Rp. ', '').replace('.', ''),
						FailureHandling.OPTIONAL), 'Total transaksi receipt')
			
			'cek apakah nomor transaksi sesuai'
			checkVerifyEqualorMatch(WebUI.verifyEqual(dataDBInstruction[0], noTrxKatalon,
						FailureHandling.OPTIONAL), 'Nomor transaksi receipt')
			
			'cek apakah bank sesuai'
			checkVerifyEqualorMatch(WebUI.verifyEqual(dataDBInstruction[1],
				WebUI.getAttribute(findTestObject('Object Repository/Top Up/Page_Topup Balance/bankNameReceipt'),
					'value', FailureHandling.OPTIONAL),
						FailureHandling.OPTIONAL), 'Nama Bank receipt')
			
			'cek apakah virtual account sesuai'
			checkVerifyEqualorMatch(WebUI.verifyEqual(dataDBInstruction[2],
				WebUI.getAttribute(findTestObject('Object Repository/Top Up/Page_Topup Balance/VANumReceipt'),
					'value', FailureHandling.OPTIONAL),
						FailureHandling.OPTIONAL), 'Virtual Account receipt')
			
			'cek apakah nama akun virtual sesuai'
			checkVerifyEqualorMatch(WebUI.verifyEqual(dataDBInstruction[3],
				WebUI.getAttribute(findTestObject('Object Repository/Top Up/Page_Topup Balance/NameAccReceipt'),
					'value', FailureHandling.OPTIONAL),
						FailureHandling.OPTIONAL), 'VA Name receipt')
			
			'klik pada clickable text'
			WebUI.click(findTestObject('Object Repository/Top Up/Page_Topup Balance/LinkReceipt'))
			
			
		}
	}
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

def statschecking(Connection conndev, int ppnfromDB, int totalkatalon, int ppnkatalon, int grandTotalkatalon) {
	
	'inisialisasi variabel integer'
	int total, ppn, grandTotal
	
	'ambil data total'
	total = Integer.parseInt(WebUI.getAttribute(
		findTestObject('Object Repository/Top Up/Page_Topup Balance/totalprice'),
		'value', FailureHandling.CONTINUE_ON_FAILURE))
	
	'ambil data ppn'
	ppn = Integer.parseInt(WebUI.getAttribute(
		findTestObject('Object Repository/Top Up/Page_Topup Balance/PPN11'),
		'value', FailureHandling.CONTINUE_ON_FAILURE))
	
	'ambil data grandtotal'
	grandTotal = Integer.parseInt(WebUI.getAttribute(
		findTestObject('Object Repository/Top Up/Page_Topup Balance/grandTotal'),
		'value', FailureHandling.CONTINUE_ON_FAILURE))
	
	'cek penghitungan total di katalon dan web'
	checkVerifyEqualorMatch(WebUI.verifyEqual(total, totalkatalon,
		FailureHandling.CONTINUE_ON_FAILURE), 'Penghitungan Total salah')
	
	'cek penghitungan ppn di katalon dan web'
	checkVerifyEqualorMatch(WebUI.verifyEqual(ppn, ppnkatalon,
		FailureHandling.CONTINUE_ON_FAILURE), 'Penghitungan PPN salah')
	
	'cek penghitungan grandtotal di katalon dan web'
	checkVerifyEqualorMatch(WebUI.verifyEqual(grandTotal, grandTotalkatalon,
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
		
		for(int i=1; i<=countWeb; i++) {
			
			'ambil object dari ddl'
			def modifyNamaActiveSaldo = WebUI.modifyObjectProperty(findTestObject('Object Repository/Top Up/modifyObject'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-topup-eendigo/div[2]/div/div/div/div/div/form/div[3]/div/app-select/div/ng-select/ng-dropdown-panel/div/div[2]/div["+(i+1)+"]/span", true)
			
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

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
	if (isMatch == false) {
		
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		GlobalVariable.FlagFailed = 1
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('TopUp', GlobalVariable.NumOfColumn, 
			GlobalVariable.StatusFailed, (findTestData(ExcelPathTopUp).getValue(GlobalVariable.NumOfColumn, 2) + ';') + 
				GlobalVariable.FailedReasonVerifyEqualorMatch + reason)
	}
}