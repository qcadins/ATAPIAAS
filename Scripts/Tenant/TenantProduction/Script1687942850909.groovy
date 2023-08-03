import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

Connection conn

if(GlobalVariable.SettingEnvi == 'Production') {
	'deklarasi koneksi ke Database eendigo_dev'
	conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_public'()
} else if(GlobalVariable.SettingEnvi == 'Trial') {
	'deklarasi koneksi ke Database eendigo_dev_uat'
	conn = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_devUat'()
}

'mendapat jumlah kolom dari sheet Tenant'
int countColumnEdit = findTestData(ExcelPathTenant).getColumnNumbers()

ArrayList<String> arrayServices, arrayVendor

'looping tenant'
for (GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn <= countColumnEdit; (GlobalVariable.NumOfColumn)++) {
	
	'declare isMmandatory Complete'
	int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 5))
	
	if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 1).length() == 0) {
		
		break
		
	} else if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 1).equalsIgnoreCase('Unexecuted')) {
				
		'call test case login admin esign'
		WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'Tenant', ('SheetName') : 'Tenant',
			('Path') : ExcelPathTenant] , FailureHandling.STOP_ON_FAILURE)
		
		'click menu tenant'
		WebUI.click(findTestObject('Tenant/menu_Tenant'))
		
		if(GlobalVariable.NumOfColumn == 2) {
			'call function check paging'
			checkPaging(conn)
		}
		
		'check if action new/services/edit/balancechargetype'
		if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 8).equalsIgnoreCase('New')) {			
			'click button Baru'
			WebUI.click(findTestObject('Tenant/Button_Baru'))

			'get total form'
			variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div >'+
				' div.main-panel > div > div.content-wrapper > app-add-tenant > div.row.match-height > div > div > div > div >'+
				' form div'))
			
			'cek untuk field awalan sebelum loop'
			if (GlobalVariable.NumOfColumn == 2) {
				
				'input nama tenant'
				WebUI.setText(findTestObject('Tenant/TenantBaru/input_NamaTenant'),
					'TenantTest')
				
				'input tenant code'
				WebUI.setText(findTestObject('Tenant/TenantBaru/input_TenantCode'),
					'TES')
				
				'input label ref number'
				WebUI.setText(findTestObject('Tenant/TenantBaru/input_LabelRefNumber'),
					'20230714')
				
				'click button generate api key'
				WebUI.click(findTestObject('Tenant/TenantBaru/button_GenerateAPIKEY'))
				
				'looping untuk input batas saldo'
				for (index = 5; index < variable.size(); index++){
					
					'modify object button services'
					modifyObjectButtonServices = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'),
						'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/'+
							'div/div/div/form/div[' + index) + ']/button', true)
					
					'looping untuk array service excel'
					for (indexExcel = 0; indexExcel < 1; indexExcel++){
						
						if(index > 7){
							
							break
						}
				
						'check if button contain service name'
						if (WebUI.verifyElementNotPresent(modifyObjectButtonServices, GlobalVariable.Timeout, FailureHandling.OPTIONAL)){
							
							continue
							
						} else if (!(WebUI.getText(modifyObjectButtonServices).contains('OTP'))){
							
							continue
							
						} else if (WebUI.getText(modifyObjectButtonServices).contains('OTP')){
							
							'click button add services'
							WebUI.click(modifyObjectButtonServices)
				
							'modify object input services'
							modifyObjectInputServices = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'),
								'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/'+
									'div/form/div[' + index) + ']/div/input', true)
				
							'input batas saldo'
							WebUI.setText(modifyObjectInputServices, '200')
				
							break
						}
					}
				}
				
				'input email user admin'
				WebUI.setText(findTestObject('Tenant/TenantBaru/input_EmailUserAdmin'),
					findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 23))
	
				'input kode akses user admin'
				WebUI.setText(findTestObject('Tenant/TenantBaru/input_KodeAksesUserAdmin'),
					findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 24))
				
				'mundur satu halaman'
				WebUI.back()
				
				'klik lagi pada tombol Baru'
				WebUI.click(findTestObject('Tenant/Button_Baru'))
				
				'cek apakah field yang baru diisi adalah kosong'
				checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(
					findTestObject('Tenant/TenantBaru/input_NamaTenant'),
					'value', FailureHandling.CONTINUE_ON_FAILURE),'',
						false, FailureHandling.CONTINUE_ON_FAILURE), 'Field nama tenant tidak kosong')
				
				'cek apakah field yang baru diisi adalah kosong'
				checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(
					findTestObject('Tenant/TenantBaru/input_TenantCode'),
					'value', FailureHandling.CONTINUE_ON_FAILURE),'',
						false, FailureHandling.CONTINUE_ON_FAILURE), 'Field kode tenant tidak kosong')
				
				'cek apakah field yang baru diisi adalah kosong'
				checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(
					findTestObject('Tenant/TenantBaru/input_LabelRefNumber'),
					'value', FailureHandling.CONTINUE_ON_FAILURE),'',
						false, FailureHandling.CONTINUE_ON_FAILURE), 'Field ref number tidak kosong')
				
				'cek apakah field yang baru diisi adalah kosong'
				checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(
					findTestObject('Tenant/TenantBaru/input_APIKEY'),
					'value', FailureHandling.CONTINUE_ON_FAILURE),'',
						false, FailureHandling.CONTINUE_ON_FAILURE), 'Field api key tidak kosong')
				
				'cek apakah field yang baru diisi adalah kosong'
				checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(
					findTestObject('Tenant/TenantBaru/input_EmailUserAdmin'),
					'value', FailureHandling.CONTINUE_ON_FAILURE),'',
						false, FailureHandling.CONTINUE_ON_FAILURE), 'Field email user admin tidak kosong')
				
				'cek apakah field yang baru diisi adalah kosong'
				checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(
					findTestObject('Tenant/TenantBaru/input_KodeAksesUserAdmin'),
					'value', FailureHandling.CONTINUE_ON_FAILURE),'',
						false, FailureHandling.CONTINUE_ON_FAILURE), 'Field access code user admin tidak kosong')
			}
			
			'input nama tenant'
			WebUI.setText(findTestObject('Tenant/TenantBaru/input_NamaTenant'), 
				findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 13))

			'input tenant code'
			WebUI.setText(findTestObject('Tenant/TenantBaru/input_TenantCode'), 
				findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 14))

			'input label ref number'
			WebUI.setText(findTestObject('Tenant/TenantBaru/input_LabelRefNumber'), 
				findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 15))

			'check if ingin menginput api secara manual/generate'
			if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 16) == 'No') {
				
				'input API Key'
				WebUI.setText(findTestObject('Tenant/TenantBaru/input_APIKEY'), 
					findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 17))
				
			} else if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 16) == 'Yes') {
				
				'click button generate api key'
				WebUI.click(findTestObject('Tenant/TenantBaru/button_GenerateAPIKEY'))

				'get api key'
				APIKEY = WebUI.getAttribute(findTestObject('Tenant/TenantBaru/input_APIKEY'), 
					'value', FailureHandling.CONTINUE_ON_FAILURE)

				'write to excel api key'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 
					'Tenant', 16, GlobalVariable.NumOfColumn - 1, APIKEY)
			}
			
			'get array services dari excel'
			arrayServices = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 19).split(';', -1)
			
			'get array batas saldo dari excel'
			arrayServicesBatasSaldo = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 20).split(';', -1)
			
			'looping untuk input batas saldo'
			for (index = 5; index < variable.size(); index++) {
				
				'modify object button services'
				modifyObjectButtonServices = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'),
					'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/'+
						'div/div/div/form/div[' + index) + ']/button', true)
				
				'looping untuk array service excel'
				for (indexExcel = 0; indexExcel < arrayServices.size(); indexExcel++) {
					
					if (index > 34) {
						
						break
					}
			
					'check if button contain service name'
					if (WebUI.verifyElementNotPresent(modifyObjectButtonServices, GlobalVariable.Timeout, FailureHandling.OPTIONAL)){
						
						continue
						
					} else if (!(WebUI.getText(modifyObjectButtonServices).contains(arrayServices[indexExcel]))) {
						
						continue
						
					} else if (WebUI.getText(modifyObjectButtonServices).contains(arrayServices[indexExcel])) {
						
						'click button add services'
						WebUI.click(modifyObjectButtonServices)
			
						'modify object input services'
						modifyObjectInputServices = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'),
							'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/'+
								'div/form/div[' + index) + ']/div/input', true)
			
						'input batas saldo'
						WebUI.setText(modifyObjectInputServices, arrayServicesBatasSaldo[indexExcel])
			
						break
					}
				}
			}
			
			'get array email reminder dari excel'
			arrayEmailReminder = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 22).split(';', -1)

			'looping untuk input email reminder'
			for (index = 1; index <= arrayEmailReminder.size(); index++) {
				
				'modify object input email'
				modifyObjectInputEmail = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'), 'xpath',
					'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/'+
						'form/div[' + (19 + index).toString()) + ']/div/input', true)

				'click tambah email'
				WebUI.click(findTestObject('Tenant/TenantBaru/button_TambahEmail'))

				'input email reminder'
				WebUI.setText(modifyObjectInputEmail, arrayEmailReminder[(index - 1)])
			}
			
			'input email user admin'
			WebUI.setText(findTestObject('Tenant/TenantBaru/input_EmailUserAdmin'), 
				findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 23))

			'input kode akses user admin'
			WebUI.setText(findTestObject('Tenant/TenantBaru/input_KodeAksesUserAdmin'), 
				findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 24))

			'check if mandatory complete dan button simpan clickable'
			if ((isMandatoryComplete == 0) && !(WebUI.verifyElementHasAttribute(findTestObject('Tenant/TenantBaru/button_Simpan'),
				'disabled', GlobalVariable.Timeout, FailureHandling.OPTIONAL))) {
			
				'click button simpan'
				WebUI.click(findTestObject('Tenant/TenantBaru/button_Simpan'))

				'verify pop up message berhasil'
				if (WebUI.verifyElementPresent(findTestObject('Tenant/popUpMsg'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
					
					'click button OK'
					WebUI.click(findTestObject('Tenant/button_OK'))
					
					'cek apakah muncul error unknown setelah add new tenant'
					if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'),
						GlobalVariable.Timeout, FailureHandling.OPTIONAL) == false) {
						
						GlobalVariable.FlagFailed = 1
						
						'tulis adanya error pada sistem web'
						CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumOfColumn,
							GlobalVariable.StatusWarning, (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
								GlobalVariable.FailedReasonUnknown)
					}
					else {
						'call function checkAfterAddorEdit'
						checkAfterAddorEdit()
						
						'write to excel success'
						CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Tenant', 0,
							GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
					}
				}
				else{
					
					'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonMandatory'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumOfColumn,
						GlobalVariable.StatusFailed, (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 2) +
						';') + GlobalVariable.FailedReasonSubmitError)
					
					continue
				}
				
			} else if (isMandatoryComplete > 0) {
				
				'click button Batal'
				WebUI.click(findTestObject('Tenant/TenantBaru/button_Batal'))

				'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonMandatory'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 2) +
					';') + GlobalVariable.FailedReasonMandatory)
				
				continue
			}
			
		} else if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 8).equalsIgnoreCase('Service')) {
			'call function search tenant'
			searchTenant()

			if(WebUI.verifyElementPresent(findTestObject('Tenant/button_ServiceBalance'), GlobalVariable.Timeout, 
				FailureHandling.OPTIONAL))
			{
				'click button services balance'
				WebUI.click(findTestObject('Tenant/button_ServiceBalance'))
				
			} else {
				
				'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 2) +
					';') + GlobalVariable.FailedReasonSearchFailed)
				
				continue
			}

			'get array Services dari excel'
			arrayServices = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 26).split(';', -1)

			'get array Vendor dari excel'
			arrayVendor = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 27).split(';', -1)

			'looping untuk input services check'
			for (index = 0; index < arrayServices.size(); index++) {
				
				'modify object checkbox'
				modifyObjectCheckbox = WebUI.modifyObjectProperty(findTestObject('Tenant/Services/modifyObject'), 'xpath',
					'equals', ((('//*[@id="' + (arrayServices[index])) + '@') + (arrayVendor[index])) + '"]', true)

				'check if check box is unchecked'
				if (WebUI.verifyElementNotChecked(modifyObjectCheckbox, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
					
					'click checkbox'
					WebUI.click(modifyObjectCheckbox)
				}
			}
			
			'get array Services dari excel'
			arrayServices = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 28).split(';', -1)

			'get array Vendor dari excel'
			arrayVendor = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 29).split(';', -1)

			'looping untuk input services uncheck'
			for (index = 0; index < arrayServices.size(); index++) {
				
				'modify object checkbox'
				modifyObjectCheckbox = WebUI.modifyObjectProperty(findTestObject('Tenant/Services/modifyObject'), 'xpath',
					'equals', ((('//*[@id="' + (arrayServices[index])) + '@') + (arrayVendor[index])) + '"]', true)

				'check if check box is checked'
				if (WebUI.verifyElementChecked(modifyObjectCheckbox, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
					
					'click checkbox'
					WebUI.click(modifyObjectCheckbox)
				}
			}
			
			'check if mandatory complete dan button simpan clickable'
			if ((isMandatoryComplete == 0) && !(WebUI.verifyElementHasAttribute(findTestObject('Tenant/Services/button_Simpan'),
				'disabled', GlobalVariable.Timeout, FailureHandling.OPTIONAL))) {
			
				'click button simpan'
				WebUI.click(findTestObject('Tenant/Services/button_Simpan'))

				'check if alert berhasil muncul'
				if (WebUI.getAttribute(findTestObject('Tenant/errorLog'), 'aria-label', FailureHandling.OPTIONAL).contains(
					'berhasil')) {
				
					'cek apakah muncul error unknown setelah login'
					if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'),
						GlobalVariable.Timeout, FailureHandling.OPTIONAL) == false) {
						
						GlobalVariable.FlagFailed = 1
						
						'tulis adanya error pada sistem web'
						CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumOfColumn,
							GlobalVariable.StatusWarning, (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
								GlobalVariable.FailedReasonUnknown)
						
					} else {
						
						'write to excel success'
						CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Tenant', 0,
							GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
					}
				}
				
			} else if (isMandatoryComplete > 0) {
				
				'click button Batal'
				WebUI.click(findTestObject('Tenant/Services/button_Batal'))

				'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonMandatory'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 2) +
					';') + GlobalVariable.FailedReasonMandatory)
				
				continue
			}
			
			'call function check saldo setelah setting services'
			checkSaldo()
			
		} else if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 8).equalsIgnoreCase('Edit')) {			
			'panggil fungsi search'
			searchTenant()

			'click button edit'
			WebUI.click(findTestObject('Tenant/button_Edit'))

			'input nama tenant'
			WebUI.setText(findTestObject('Tenant/TenantBaru/input_NamaTenant'), 
				findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 13), FailureHandling.OPTIONAL)
			
			'input tenant code'
			WebUI.setText(findTestObject('Tenant/TenantBaru/input_TenantCode'), 
				findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn,14), FailureHandling.OPTIONAL)

			'input label ref number'
			WebUI.setText(findTestObject('Tenant/TenantBaru/input_LabelRefNumber'), 
				findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 15))

			'check if ingin menginput api secara manual/generate'
			if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 16) == 'No') {
				
				'input API Key'
				WebUI.setText(findTestObject('Tenant/TenantBaru/input_APIKEY'), 
					findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 17))
				
			} else if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 16) == 'Yes') {
				
				'click button generate api key'
				WebUI.click(findTestObject('Tenant/TenantBaru/button_GenerateAPIKEY'))

				'get api key'
				APIKEY = WebUI.getAttribute(findTestObject('Tenant/TenantBaru/input_APIKEY'), 'value', 
					FailureHandling.CONTINUE_ON_FAILURE)

				'write to excel api key'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 
					'Tenant', 16, GlobalVariable.NumOfColumn - 1, APIKEY)
			}
			
			'get total form'
			variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > '+
				' div.main-panel > div > div.content-wrapper > app-add-tenant > div.row.match-height > div > div >'+
				' div > div > form div'))
			
			'get array services dari excel'
			arrayServices = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 19).split(';', -1)

			'get array batas saldo dari excel'
			arrayServicesBatasSaldo = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 20).split(';', -1)

			'looping untuk input bata saldo'
			for (index = 5; index < variable.size(); index++) {
				
				'modify object button Hapus'
				modifyObjectButtonHapus = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'), 'xpath',
					'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' +
					index) + ']/div/button', true)

				'modify object label services'
				modifyObjectLabelServices = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'),
					'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/'+
						'div/form/div[' + index) + ']/label', true)

				'modify object button services'
				modifyObjectButtonServices = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'),
					'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/'+
						'div/form/div[' + index) + ']/button', true)
				
				'break if index udah lebih dari 34 HARDCODE karena tidak bisa di track objectnya'
				if (index > 19) {
					
					break
				}
				
				'looping untuk array service excel'
				for (indexExcel = 0; indexExcel < arrayServices.size(); indexExcel++) {
					
					'check if label present'
					if (WebUI.verifyElementPresent(modifyObjectButtonHapus, GlobalVariable.Timeout, FailureHandling.OPTIONAL) &&
					WebUI.verifyElementPresent(modifyObjectLabelServices, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
					
						'check if button label = service name di excel'
						if (WebUI.getText(modifyObjectLabelServices).equalsIgnoreCase(arrayServices[indexExcel])) {
							
							'modify object input services'
							modifyObjectInputServices = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'),
								'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/'+
									'div/form/div[' + index) + ']/div/input', true)

							'input batas saldo'
							WebUI.setText(modifyObjectInputServices, arrayServicesBatasSaldo[indexExcel])

							break
							
						} else if (!(WebUI.getText(modifyObjectLabelServices).equalsIgnoreCase(arrayServices[indexExcel]))) {
							
							if ((indexExcel + 1) == arrayServices.size()) {
								
								'click button hapus'
								WebUI.click(modifyObjectButtonHapus)

								break
							}
						}
						
					} else if (WebUI.verifyElementPresent(modifyObjectButtonServices, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
						
						if (WebUI.getText(modifyObjectButtonServices).contains(arrayServices[indexExcel])) {
							
							'click button add service'
							WebUI.click(modifyObjectButtonServices)

							'modify object input services'
							modifyObjectInputServices = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'),
								'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/'+
									'div/form/div[' +index) + ']/div/input', true)

							'input batas saldo'
							WebUI.setText(modifyObjectInputServices, arrayServicesBatasSaldo[indexExcel])

							break
						}
					}
				}
			}
			
			'get array email reminder dari excel'
			arrayEmailReminder = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 22).split(';', -1)

			'looping untuk hapus email reminder yang tidak ada di excel'
			for (index = 20; index <= variable.size(); index++) {
				
				'modify object input email'
				modifyObjectInputEmail = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'), 'xpath',
					'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' +
					index) + ']/div/input', true)

				'modify object button hapus'
				modifyObjectButtonHapus = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'), 'xpath',
					'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' +
					index) + ']/div/button', true)

				if (WebUI.verifyElementPresent(modifyObjectInputEmail, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
					
					'looping untuk input email reminder'
					for (indexexcel = 1; indexexcel <= arrayEmailReminder.size(); indexexcel++) {
						
						'check if email ui = excel'
						if (WebUI.getAttribute(modifyObjectInputEmail, 'value', FailureHandling.OPTIONAL).equalsIgnoreCase(
							arrayEmailReminder[(indexexcel - 1)])) {
						
							break
							
						} else {
							
							if (indexexcel == arrayEmailReminder.size()) {
								
								'click tambah email'
								WebUI.click(modifyObjectButtonHapus)

								index--
							}
						}
					}
					
				} else {
					
					break
				}
			}
			
			'looping untuk input email reminder yang tidak ada di ui'
			for (indexexcel = 1; indexexcel <= arrayEmailReminder.size(); indexexcel++) {
				
				'looping untuk input email reminder'
				for (index = 20; index <= variable.size(); index++) {
					
					'modify object input email'
					modifyObjectInputEmail = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'),
						'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/'+
							'div/form/div[' +index) + ']/div/input', true)

					if (WebUI.verifyElementNotPresent(modifyObjectInputEmail, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
						
						'click tambah email'
						WebUI.click(findTestObject('Tenant/TenantBaru/button_TambahEmail'))

						'input email reminder'
						WebUI.setText(modifyObjectInputEmail, arrayEmailReminder[(indexexcel - 1)])

						break
						
					} else if (WebUI.getAttribute(modifyObjectInputEmail, 'value', FailureHandling.OPTIONAL).equalsIgnoreCase(
						arrayEmailReminder[(indexexcel - 1)])) {
					
						break
					}
				}
			}
			
			'check if mandatory complete dan button simpan clickable'
			if ((isMandatoryComplete == 0) && !(WebUI.verifyElementHasAttribute(findTestObject('Tenant/Edit/button_Simpan'),
				'disabled', GlobalVariable.Timeout, FailureHandling.OPTIONAL))){
			
				'click button simpan'
				WebUI.click(findTestObject('Tenant/Edit/button_Simpan'))

				'verify pop up message berhasil'
				if (WebUI.verifyElementPresent(findTestObject('Tenant/popUpMsg'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
					
					'click button OK'
					WebUI.click(findTestObject('Tenant/button_OK'))

					'cek apakah muncul error unknown setelah login'
					if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'),
						GlobalVariable.Timeout, FailureHandling.OPTIONAL) == false) {
						
						GlobalVariable.FlagFailed = 1
						
						'tulis adanya error pada sistem web'
						CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumOfColumn,
							GlobalVariable.StatusWarning, (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
								GlobalVariable.FailedReasonUnknown)
						
					} else {
						
						'call function check afteraddoredit'
						checkAfterAddorEdit()
						
						'write to excel success'
						CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Tenant', 0,
							GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
					}
					
				} else {
					
					'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonMandatory'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumOfColumn,
						GlobalVariable.StatusFailed, (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 2) +
						';') + GlobalVariable.FailedReasonSubmitError)
					
					continue
				}
				
			} else if (isMandatoryComplete > 0) {
				
				'click button Batal'
				WebUI.click(findTestObject('Tenant/TenantBaru/button_Batal'))

				'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonMandatory'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 2) +
					';') + GlobalVariable.FailedReasonMandatory)
				
				continue
			}
			
		} else if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 8).equalsIgnoreCase('ChargeType')){
			'panggil fungsi search'
			searchTenant()

			if (WebUI.verifyElementPresent(findTestObject('Tenant/button_chargeType'), GlobalVariable.Timeout, 
				FailureHandling.OPTIONAL)) {
			
				'click button services balance'
				WebUI.click(findTestObject('Tenant/button_chargeType'))
			} else {
				
				'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 2) +
					';') + GlobalVariable.FailedReasonSearchFailed)
				
				continue
			}
			
			'fungsi untuk cek apakah tenant yang aktif sesuai dengan tenant yang muncul'
			checkActiveTenant(findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 14), conn)
			
//			'penanda apakah service ditagih by price atau quantity'
//			ArrayList<Integer>isChargedByPrice= new ArrayList<Integer>()
			
			'get array Services dari excel'
			arrayServices = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 30).split(';', -1)

			'looping untuk input services check'
			for (index = 0; index < arrayServices.size(); index++) {
				
				'modify object checkbox'
				modifyObjectCheckbox = WebUI.modifyObjectProperty(findTestObject('Tenant/Services/modifyObject'), 'xpath',
					'equals', ('//*[@id="'+ arrayServices[index] +'"]'), true)
			
				'check if check box is unchecked'
				if (WebUI.verifyElementNotChecked(modifyObjectCheckbox, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
					
					'click checkbox'
					WebUI.click(modifyObjectCheckbox)
					
				}
			}
			
			'get array Services uncheck dari excel'
			arrayServices = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 31).split(';', -1)

			'looping untuk input services uncheck'
			for (index = 0; index < arrayServices.size(); index++) {
				
				'modify object checkbox'
				modifyObjectCheckbox = WebUI.modifyObjectProperty(findTestObject('Tenant/Services/modifyObject'), 'xpath',
					'equals', ('//*[@id="'+ arrayServices[index] +'"]'), true)
				
				'check if check box is checked'
				if (WebUI.verifyElementChecked(modifyObjectCheckbox, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) {
					
					'click checkbox'
					WebUI.click(modifyObjectCheckbox)
				}
			}
		
			'check if mandatory complete dan button simpan clickable'
			if ((isMandatoryComplete == 0) && !(WebUI.verifyElementHasAttribute(findTestObject('Tenant/ChargeType/button_Simpan'),
				'disabled', GlobalVariable.Timeout, FailureHandling.OPTIONAL))) {
			
				'click button simpan'
				WebUI.click(findTestObject('Tenant/ChargeType/button_Simpan'))

				'check if alert berhasil muncul'
				if (WebUI.getAttribute(findTestObject('Tenant/errorLog'), 'aria-label', FailureHandling.OPTIONAL).contains(
					'berhasil')) {
				
					'write to excel success'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Tenant', 0,
						GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
				} else {
					
					'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonMandatory'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumOfColumn,
						GlobalVariable.StatusFailed, (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 2) +
						';') + GlobalVariable.FailedReasonQuantityNotNull)
					
					continue
				}
				
				'cek apakah muncul error unknown setelah ubah chargetype'
				if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'),
					GlobalVariable.Timeout, FailureHandling.OPTIONAL) == false) {
					
					GlobalVariable.FlagFailed = 1
					
					'tulis adanya error pada sistem web'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumOfColumn,
						GlobalVariable.StatusWarning, (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
							GlobalVariable.FailedReasonUnknown)
				}
				
			} else if (isMandatoryComplete > 0) {
				
				'click button Batal'
				WebUI.click(findTestObject('Tenant/ChargeType/button_Batal'))

				'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonMandatory'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 2) +
					';') + GlobalVariable.FailedReasonMandatory)
			
				continue
			}
		}
		
		'check if store db'
		if (GlobalVariable.KondisiCekDB == 'Yes' && isMandatoryComplete == 0) {
			
			'call test case tenant store db'
			WebUI.callTestCase(findTestCase('Tenant/TenantStoreDB'), [('ExcelPathTenant') : 'Tenant/DataTestingTenant'], 
				FailureHandling.STOP_ON_FAILURE)
		}
	}
}

WebUI.closeBrowser()

def checkPaging(Connection conn) {
	'input nama tenant'
	WebUI.setText(findTestObject('Tenant/input_NamaTenant'), 'nama tenant')

	'input status'
	WebUI.setText(findTestObject('Tenant/input_Status'), 'Active')

	'click enter untuk input select ddl'
	WebUI.sendKeys(findTestObject('Tenant/input_Status'), Keys.chord(Keys.ENTER))

	'click button set ulang'
	WebUI.click(findTestObject('Tenant/button_SetUlang'))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/input_NamaTenant'), 
		'value', FailureHandling.CONTINUE_ON_FAILURE),'', false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/input_Status'), 
		'value', FailureHandling.CONTINUE_ON_FAILURE),'', false, FailureHandling.CONTINUE_ON_FAILURE))

	'click button cari'
	WebUI.click(findTestObject('Tenant/button_Cari'))
	
	'cari button skip di footer'
	def elementbutton = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout >'+
		' div > div.main-panel > div > div.content-wrapper > app-tenant > app-msx-paging > app-msx-datatable >'+
		' section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))
	
	'ambil banyaknya laman footer'
	int lastPage = elementbutton.size()

	'get data tenant'
	int resultTotalData = CustomKeywords.'tenant.TenantVerif.getTenantTotal'(conn)

	'get text total data dari ui'
	Total = WebUI.getText(findTestObject('Tenant/label_TotalData')).split(' ')

	'verify total data tenant'
	checkVerifyPaging(WebUI.verifyEqual(resultTotalData, Integer.parseInt(Total[0]), FailureHandling.CONTINUE_ON_FAILURE))

	'click page 2'
	WebUI.click(findTestObject('Tenant/button_Page2'))

	'verify paging di page 2'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/button_Page2'), 
		'class', FailureHandling.CONTINUE_ON_FAILURE), 'pages active ng-star-inserted', 
			false, FailureHandling.CONTINUE_ON_FAILURE))

	'click page 1'
	WebUI.click(findTestObject('Tenant/button_Page1'))

	'verify paging di page 1'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/button_Page1'), 
		'class', FailureHandling.CONTINUE_ON_FAILURE), 'pages active ng-star-inserted', 
			false, FailureHandling.CONTINUE_ON_FAILURE))

	'klik button next page'
	WebUI.click(findTestObject('Object Repository/OCR Testing/Page_Balance/i_Catatan_datatable-icon-right'))

	'verify paging di page 2'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/button_Page2'), 
		'class', FailureHandling.CONTINUE_ON_FAILURE), 'pages active ng-star-inserted', 
			false, FailureHandling.CONTINUE_ON_FAILURE))

	'click prev page'
	WebUI.click(findTestObject('Tenant/button_PrevPage'))

	'verify paging di page 1'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/button_Page1'), 
		'class', FailureHandling.CONTINUE_ON_FAILURE), 'pages active ng-star-inserted', 
			false, FailureHandling.CONTINUE_ON_FAILURE))

	'cek apakah button skip enable atau disable'
	if (WebUI.verifyElementVisible(
		findTestObject('Object Repository/API_KEY/Page_Balance/skiptoLast_page'), FailureHandling.OPTIONAL)) {
	
		'klik button skip to last page'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/i_Catatan_datatable-icon-skip'))
	}
	
	'modify object last Page'
	def modifyObjectmaxPage = WebUI.modifyObjectProperty(findTestObject('Tenant/button_MaxPage'), 
		'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-tenant/app-msx-paging/"+
		"app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li["+ (lastPage - 2) +"]", true)

	'verify paging di page terakhir'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(modifyObjectmaxPage, 
		'class', FailureHandling.CONTINUE_ON_FAILURE), 'pages active ng-star-inserted', 
			false, FailureHandling.CONTINUE_ON_FAILURE))

	'click min page'
	WebUI.click(findTestObject('Tenant/button_MinPage'))

	'verify paging di page 1'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/button_Page1'), 
		'class', FailureHandling.CONTINUE_ON_FAILURE), 'pages active ng-star-inserted', 
			false, FailureHandling.CONTINUE_ON_FAILURE))
}

def checkVerifyPaging(Boolean isMatch) {
	if (isMatch == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumOfColumn, 
			GlobalVariable.StatusFailed, (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 2) + 
				';') + GlobalVariable.FailedReasonPagingError)

		GlobalVariable.FlagFailed = 1
	}
}

def checkActiveTenant(String tenantcode, Connection conn) {
	
	'ambil list tenant aktif di DB'
	ArrayList<String> ActiveTenantfromDB = CustomKeywords.'tenant.TenantVerif.getActiveTenant'(conn, tenantcode)
	
	'ambil list tenant aktif dari UI'
	ArrayList<String> ActiveTenantfromUI = []
	
	for(int i=0; i< ActiveTenantfromDB.size(); i++){
		
		'modify object checkbox'
		def modifyObjectNamaTenant = WebUI.modifyObjectProperty(findTestObject('Tenant/ChargeType/namaTenant'), 'xpath',
			'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-charge/div[2]/'+
				'div/div/div/div/table/tr['+ (i+2) +']/th[1]'), true)
		
		'tambahkan nama tenant ke array'
		ActiveTenantfromUI.add(WebUI.getText(modifyObjectNamaTenant))
	}
	
	if(!ActiveTenantfromUI.containsAll(ActiveTenantfromDB)){
		
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumOfColumn, 
			GlobalVariable.StatusFailed,(findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 2) + 
				';') + GlobalVariable.FailedReasonServiceNotMatch)

		GlobalVariable.FlagFailed = 1
	}
}

def searchTenant() {
	'click button cari'
	WebUI.click(findTestObject('Tenant/button_Cari'))
	
	'input nama tenant'
	WebUI.setText(findTestObject('Tenant/input_NamaTenant'), 
		findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 10))

	'input status'
	WebUI.setText(findTestObject('Tenant/input_Status'), 
		findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 11))

	'click enter untuk input select ddl'
	WebUI.sendKeys(findTestObject('Tenant/input_Status'), Keys.chord(Keys.ENTER))

	'click button cari'
	WebUI.click(findTestObject('Tenant/button_Cari'))
}

def checkSaldo() {
	'call test case login admin esign'
	WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'TenantCekServices', ('SheetName') : 'Tenant',
		('Path') : ExcelPathTenant] , FailureHandling.STOP_ON_FAILURE)
	
	'get array service dari excel yang terceklist'
	ArrayList<String> arrayServices = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 26).split(';', -1)
	
	ArrayList<String> servicesNameActive = [], servicesNameUISaldo = []
	
	for (int i = 0 ; i < arrayServices.size ; i++) {
		
		println(arrayServices[i])
		
		int row = CustomKeywords.'writeToExcel.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, 'Tenant', arrayServices[i])
		
		servicesNameActive.add(findTestData(ExcelPathTenant).getValue(2, row))
	}
	
	println(servicesNameActive)
	
	'get total tipe saldo'
	variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance-prod > div.row.match-height > div > lib-balance-summary > div > div'))
	
	println(variable.size)
	
	for (i = 1 ; i <= variable.size ; i++) {
		
		'modify object button services'
		modifyObjectTipeSaldo = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'),
			'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[1]/div/lib-balance-summary/div/div[' 
				+ i) + ']/div/div/div/div/div[1]/span', true)
		
		servicesNameUISaldo.add(WebUI.getText(modifyObjectTipeSaldo))
	}
	
	servicesNameActive.containsAll(servicesNameUISaldo)
}

'fungsi untuk melakukan pengecekan '
def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
	if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
		
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
			(findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.FailedReasonVerifyEqualorMatch + reason)

		GlobalVariable.FlagFailed = 1
	}
}

def checkAfterAddorEdit() {
	'input nama tenant'
	WebUI.setText(findTestObject('Tenant/input_NamaTenant'),
		findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 13))

	'click button cari'
	WebUI.click(findTestObject('Tenant/button_Cari'))
	
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Tenant/label_TenantName')), findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 13), false, FailureHandling.CONTINUE_ON_FAILURE), ' nama tenant setelah add')
	
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Tenant/label_TenantCode')), findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 14), false, FailureHandling.CONTINUE_ON_FAILURE), ' kode tenant setelah add')
}