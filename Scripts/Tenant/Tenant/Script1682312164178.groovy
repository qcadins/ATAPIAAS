import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import java.sql.Connection as Connection
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'writeToExcel.WriteExcel.getExcelPath'('/Excel/2. APIAAS.xlsx')

//'deklarasi koneksi ke Database adins_apiaas_uat'
//def connProd = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_uatProduction'()

'deklarasi koneksi ke Database adins_apiaas_uat'
def conndevUAT = CustomKeywords.'dbConnection.Connect.connectDBAPIAAS_devUat'()

'mendapat jumlah kolom dari sheet Tenant'
int CountColumnEdit = findTestData(ExcelPathTenant).getColumnNumbers()

'call test case login admin esign'
WebUI.callTestCase(findTestCase('Test Cases/Login/Login'), [('TC') : 'Tenant'], FailureHandling.STOP_ON_FAILURE)

'click menu tenant'
WebUI.click(findTestObject('Tenant/menu_Tenant'))

'call function check paging'
checkPaging(conndevUAT)

'looping tenant'
for (GlobalVariable.NumOfColumn; GlobalVariable.NumOfColumn <= CountColumnEdit; (GlobalVariable.NumOfColumn)++) {
	
	'declare isMmandatory Complete'
	int isMandatoryComplete = Integer.parseInt(findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 4))
	
	if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 1).length() == 0) 
	{
		break
	} 
	else if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 1).equalsIgnoreCase('Unexecuted')) 
	{
	'check if action new/services/edit/balancechargetype'
		if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 7).equalsIgnoreCase('New')) 
		{
			'click menu tenant'
			WebUI.click(findTestObject('Tenant/menu_Tenant'))
			
			'click button Baru'
			WebUI.click(findTestObject('Tenant/Button_Baru'))

			'get total form'
			variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-add-tenant > div.row.match-height > div > div > div > div > form div'))
			
			'input nama tenant'
			WebUI.setText(findTestObject('Tenant/TenantBaru/input_NamaTenant'), findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn,
					12))

			'input tenant code'
			WebUI.setText(findTestObject('Tenant/TenantBaru/input_TenantCode'), findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn,
					13))

			'input label ref number'
			WebUI.setText(findTestObject('Tenant/TenantBaru/input_LabelRefNumber'), findTestData(ExcelPathTenant).getValue(
					GlobalVariable.NumOfColumn, 14))

			'check if ingin menginput api secara manual/generate'
			if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 15) == 'No') 
			{
				'input API Key'
				WebUI.setText(findTestObject('Tenant/TenantBaru/input_APIKEY'), findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn,
						16))
			} 
			else if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 15) == 'Yes') 
			{
				'click button generate api key'
				WebUI.click(findTestObject('Tenant/TenantBaru/button_GenerateAPIKEY'))

				'get api key'
				APIKEY = WebUI.getAttribute(findTestObject('Tenant/TenantBaru/input_APIKEY'), 'value', FailureHandling.CONTINUE_ON_FAILURE)

				'write to excel api key'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Tenant', 15, GlobalVariable.NumOfColumn -
					1, APIKEY)
			}
			
			'get array services dari excel'
			arrayServices = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 18).split(';', -1)
			
			'get array batas saldo dari excel'
			arrayServicesBatasSaldo = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 19).split(';', -1)
			
			'looping untuk input batas saldo'
			for (index = 5; index < variable.size(); index++)
			{
				'modify object button services'
				modifyObjectButtonServices = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'),
					'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' +
					index) + ']/button', true)
				
				'looping untuk array service excel'
				for (indexExcel = 0; indexExcel < arrayServices.size(); indexExcel++)
				{
					if(index > 34)
					{
						break
					}
			
					'check if button contain service name'
					if (WebUI.verifyElementNotPresent(modifyObjectButtonServices, GlobalVariable.Timeout, FailureHandling.OPTIONAL))
					{
						continue
					}
					else if (!(WebUI.getText(modifyObjectButtonServices).contains(arrayServices[indexExcel])))
					{
						continue
					}
					else if (WebUI.getText(modifyObjectButtonServices).contains(arrayServices[indexExcel]))
					{
						'click button add services'
						WebUI.click(modifyObjectButtonServices)
			
						'modify object input services'
						modifyObjectInputServices = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'),
							'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' +
							index) + ']/div/input', true)
			
						'input batas saldo'
						WebUI.setText(modifyObjectInputServices, arrayServicesBatasSaldo[indexExcel])
			
						break
					}
				}
			}
			
			'get array email reminder dari excel'
			arrayEmailReminder = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 21).split(';', -1)
			
			'ambil ukuran dari array email reminder'
			int EmailReminderTotal = arrayEmailReminder.size()
			
			'ubah lokasi xpath dari button simpan'
			modifyobjectbuttonSimpan = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/button_Simpan'), 'xpath',
					'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' +
					(36 + EmailReminderTotal).toString()) + ']/div/button[2]', true)
			
			'ubah lokasi xpath dari button simpan'
			modifyobjectbuttonBatal = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/button_Batal'), 'xpath',
					'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' +
					(36 + EmailReminderTotal).toString()) + ']/div/button[1]', true)

			'looping untuk input email reminder'
			for (index = 1; index <= EmailReminderTotal; index++) 
			{
				'modify object input email'
				modifyObjectInputEmail = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'), 'xpath',
					'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' +
					(34 + index).toString()) + ']/div/input', true)

				'click tambah email'
				WebUI.click(findTestObject('Tenant/TenantBaru/button_TambahEmail'))

				'input email reminder'
				WebUI.setText(modifyObjectInputEmail, arrayEmailReminder[(index - 1)])
			}
			
			'input email user admin'
			WebUI.setText(findTestObject('Tenant/TenantBaru/input_EmailUserAdmin'), findTestData(ExcelPathTenant).getValue(
					GlobalVariable.NumOfColumn, 22))

			'input kode akses user admin'
			WebUI.setText(findTestObject('Tenant/TenantBaru/input_KodeAksesUserAdmin'), findTestData(ExcelPathTenant).getValue(
					GlobalVariable.NumOfColumn, 23))

			'check if mandatory complete dan button simpan clickable'
			if ((isMandatoryComplete == 0) && !(WebUI.verifyElementHasAttribute(modifyobjectbuttonSimpan,
				'disabled', GlobalVariable.Timeout, FailureHandling.OPTIONAL))) 
			{
				'click button simpan'
				WebUI.click(modifyobjectbuttonSimpan)

				'verify pop up message berhasil'
				if (WebUI.verifyElementPresent(findTestObject('Tenant/popUpMsg'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) 
				{
					'click button OK'
					WebUI.click(findTestObject('Tenant/button_OK'))

					'write to excel success'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Tenant', 0,
						GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
				}
				else
				{
					'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonMandatory'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumOfColumn,
						GlobalVariable.StatusFailed, (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 2) +
						';') + GlobalVariable.FailedReasonSubmitError)
					
					continue
				}
			} 
			else if (isMandatoryComplete > 0) 
			{
				'click button Batal'
				WebUI.click(modifyobjectbuttonBatal)

				'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonMandatory'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 2) +
					';') + GlobalVariable.FailedReasonMandatory)
				
				continue
			}
		} 
		else if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 7).equalsIgnoreCase('Service')) 
		{
			'click menu tenant'
			WebUI.click(findTestObject('Tenant/menu_Tenant'))
			
			searchTenant()

			if(WebUI.verifyElementPresent(findTestObject('Tenant/button_ServiceBalance'), GlobalVariable.Timeout, FailureHandling.OPTIONAL))
			{
				'click button services balance'
				WebUI.click(findTestObject('Tenant/button_ServiceBalance'))
			}
			else
			{
				'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 2) +
					';') + GlobalVariable.FailedReasonSearchFailed)
				
				continue
			}

			'get array Services dari excel'
			arrayServices = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 25).split(';', -1)

			'get array Vendor dari excel'
			arrayVendor = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 26).split(';', -1)

			'looping untuk input services check'
			for (index = 0; index < arrayServices.size(); index++) 
			{
				'modify object checkbox'
				modifyObjectCheckbox = WebUI.modifyObjectProperty(findTestObject('Tenant/Services/modifyObject'), 'xpath',
					'equals', ((('//*[@id="' + (arrayServices[index])) + '@') + (arrayVendor[index])) + '"]', true)

				'check if check box is unchecked'
				if (WebUI.verifyElementNotChecked(modifyObjectCheckbox, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) 
				{
					'click checkbox'
					WebUI.click(modifyObjectCheckbox)
				}
			}
			
			'get array Services dari excel'
			arrayServices = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 27).split(';', -1)

			'get array Vendor dari excel'
			arrayVendor = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 28).split(';', -1)

			'looping untuk input services uncheck'
			for (index = 0; index < arrayServices.size(); index++) 
			{
				'modify object checkbox'
				modifyObjectCheckbox = WebUI.modifyObjectProperty(findTestObject('Tenant/Services/modifyObject'), 'xpath',
					'equals', ((('//*[@id="' + (arrayServices[index])) + '@') + (arrayVendor[index])) + '"]', true)

				'check if check box is checked'
				if (WebUI.verifyElementChecked(modifyObjectCheckbox, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) 
				{
					'click checkbox'
					WebUI.click(modifyObjectCheckbox)
				}
			}
			
			'check if mandatory complete dan button simpan clickable'
			if ((isMandatoryComplete == 0) && !(WebUI.verifyElementHasAttribute(findTestObject('Tenant/Services/button_Simpan'),
				'disabled', GlobalVariable.Timeout, FailureHandling.OPTIONAL))) 
			{
				'click button simpan'
				WebUI.click(findTestObject('Tenant/Services/button_Simpan'))

				'check if alert berhasil muncul'
				if (WebUI.getAttribute(findTestObject('Tenant/errorLog'), 'aria-label', FailureHandling.OPTIONAL).contains(
					'berhasil')) 
				{
					'write to excel success'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Tenant', 0,
						GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
				}
			} 
			else if (isMandatoryComplete > 0) 
			{
				'click button Batal'
				WebUI.click(findTestObject('Tenant/Services/button_Batal'))

				'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonMandatory'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 2) +
					';') + GlobalVariable.FailedReasonMandatory)
				
				continue
			}
		} 
		else if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 7).equalsIgnoreCase('Edit')) 
		{
			'click menu tenant'
			WebUI.click(findTestObject('Tenant/menu_Tenant'))
			
			'panggil fungsi search'
			searchTenant()

			'click button edit'
			WebUI.click(findTestObject('Tenant/button_Edit'))

			'input tenant code'
			WebUI.setText(findTestObject('Tenant/TenantBaru/input_TenantCode'), findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn,
					13), FailureHandling.OPTIONAL)

			'input label ref number'
			WebUI.setText(findTestObject('Tenant/TenantBaru/input_LabelRefNumber'), findTestData(ExcelPathTenant).getValue(
					GlobalVariable.NumOfColumn, 14))

			'check if ingin menginput api secara manual/generate'
			if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 15) == 'No') 
			{
				'input API Key'
				WebUI.setText(findTestObject('Tenant/TenantBaru/input_APIKEY'), findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn,
						16))
			} 
			else if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 15) == 'Yes') 
			{
				'click button generate api key'
				WebUI.click(findTestObject('Tenant/TenantBaru/button_GenerateAPIKEY'))

				'get api key'
				APIKEY = WebUI.getAttribute(findTestObject('Tenant/TenantBaru/input_APIKEY'), 'value', FailureHandling.CONTINUE_ON_FAILURE)

				'write to excel api key'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Tenant', 15, GlobalVariable.NumOfColumn -
					1, APIKEY)
			}
			
			'get total form'
			variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-add-tenant > div.row.match-height > div > div > div > div > form div'))
			
			'get array services dari excel'
			arrayServices = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 18).split(';', -1)

			'get array batas saldo dari excel'
			arrayServicesBatasSaldo = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 19).split(';', -1)

			'looping untuk input bata saldo'
			for (index = 5; index < variable.size(); index++) 
			{
				'modify object button Hapus'
				modifyObjectButtonHapus = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'), 'xpath',
					'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' +
					index) + ']/div/button', true)

				'modify object label services'
				modifyObjectLabelServices = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'),
					'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' +
					index) + ']/label', true)

				'modify object button services'
				modifyObjectButtonServices = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'),
					'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' +
					index) + ']/button', true)
				
				'break if index udah lebih dari 34 HARDCODE karena tidak bisa di track objectnya'
				if (index > 34) 
				{
					break
				}
				
				'looping untuk array service excel'
				for (indexExcel = 0; indexExcel < arrayServices.size(); indexExcel++) 
				{
					'check if label present'
					if (WebUI.verifyElementPresent(modifyObjectButtonHapus, GlobalVariable.Timeout, FailureHandling.OPTIONAL) &&
					WebUI.verifyElementPresent(modifyObjectLabelServices, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) 
					{
						'check if button label = service name di excel'
						if (WebUI.getText(modifyObjectLabelServices).equalsIgnoreCase(arrayServices[indexExcel])) 
						{
							'modify object input services'
							modifyObjectInputServices = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'),
								'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' +
								index) + ']/div/input', true)

							'input batas saldo'
							WebUI.setText(modifyObjectInputServices, arrayServicesBatasSaldo[indexExcel])

							break
						} 
						else if (!(WebUI.getText(modifyObjectLabelServices).equalsIgnoreCase(arrayServices[indexExcel]))) 
						{
							if ((indexExcel + 1) == arrayServices.size()) 
							{
								'click button hapus'
								WebUI.click(modifyObjectButtonHapus)

								break
							}
						}
					} 
					else if (WebUI.verifyElementPresent(modifyObjectButtonServices, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) 
					{
						if (WebUI.getText(modifyObjectButtonServices).contains(arrayServices[indexExcel])) 
						{
							'click button add service'
							WebUI.click(modifyObjectButtonServices)

							'modify object input services'
							modifyObjectInputServices = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'),
								'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' +
								index) + ']/div/input', true)

							'input batas saldo'
							WebUI.setText(modifyObjectInputServices, arrayServicesBatasSaldo[indexExcel])

							break
						}
					}
				}
			}
			
			'get array email reminder dari excel'
			arrayEmailReminder = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 21).split(';', -1)
			
			'ambil ukuran dari array email reminder'
			int EmailReminderTotal = arrayEmailReminder.size()
			
			'ubah lokasi xpath dari button simpan'
			modifyobjectbuttonSimpan = WebUI.modifyObjectProperty(findTestObject('Tenant/Edit/button_Simpan'), 'xpath',
					'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' +
					(35 + EmailReminderTotal).toString()) + ']/div/button[2]', true)
			
			'ubah lokasi xpath dari button simpan'
			modifyobjectbuttonBatal = WebUI.modifyObjectProperty(findTestObject('Tenant/Edit/button_Batal'), 'xpath',
					'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' +
					(35 + EmailReminderTotal).toString()) + ']/div/button[1]', true)

			'looping untuk hapus email reminder yang tidak ada di excel'
			for (index = 35; index <= variable.size(); index++) 
			{
				'modify object input email'
				modifyObjectInputEmail = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'), 'xpath',
					'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' +
					index) + ']/div/input', true)

				'modify object button hapus'
				modifyObjectButtonHapus = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'), 'xpath',
					'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' +
					index) + ']/div/button', true)

				if (WebUI.verifyElementPresent(modifyObjectInputEmail, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) 
				{
					'looping untuk input email reminder'
					for (indexexcel = 1; indexexcel <= arrayEmailReminder.size(); indexexcel++) 
					{
						'check if email ui = excel'
						if (WebUI.getAttribute(modifyObjectInputEmail, 'value', FailureHandling.OPTIONAL).equalsIgnoreCase(
							arrayEmailReminder[(indexexcel - 1)])) 
						{
							break
						} 
						else 
						{
							if (indexexcel == arrayEmailReminder.size()) 
							{
								'click tambah email'
								WebUI.click(modifyObjectButtonHapus)

								index--
							}
						}
					}
				} 
				else 
				{
					break
				}
			}
			
			'looping untuk input email reminder yang tidak ada di ui'
			for (indexexcel = 1; indexexcel <= arrayEmailReminder.size(); indexexcel++) 
			{
				'looping untuk input email reminder'
				for (index = 35; index <= variable.size(); index++) 
				{
					'modify object input email'
					modifyObjectInputEmail = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'),
						'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' +
						index) + ']/div/input', true)

					if (WebUI.verifyElementNotPresent(modifyObjectInputEmail, GlobalVariable.Timeout, FailureHandling.OPTIONAL)) 
					{
						'click tambah email'
						WebUI.click(findTestObject('Tenant/TenantBaru/button_TambahEmail'))

						'input email reminder'
						WebUI.setText(modifyObjectInputEmail, arrayEmailReminder[(indexexcel - 1)])

						break
					} 
					else if (WebUI.getAttribute(modifyObjectInputEmail, 'value', FailureHandling.OPTIONAL).equalsIgnoreCase(
						arrayEmailReminder[(indexexcel - 1)])) 
					{
						break
					}
				}
			}
			
			'check if mandatory complete dan button simpan clickable'
			if ((isMandatoryComplete == 0) && !(WebUI.verifyElementHasAttribute(findTestObject('Tenant/Edit/button_Simpan'),
				'disabled', GlobalVariable.Timeout, FailureHandling.OPTIONAL))) 
			{
				'click button simpan'
				WebUI.click(modifyobjectbuttonSimpan)

				'verify pop up message berhasil'
				if (WebUI.verifyElementPresent(findTestObject('Tenant/popUpMsg'), GlobalVariable.Timeout, FailureHandling.OPTIONAL)) 
				{
					'click button OK'
					WebUI.click(findTestObject('Tenant/button_OK'))

					'write to excel success'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Tenant', 0,
						GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
				}
				else
				{
					'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonMandatory'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumOfColumn,
						GlobalVariable.StatusFailed, (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 2) +
						';') + GlobalVariable.FailedReasonSubmitError)
					
					continue
				}
			} 
			else if (isMandatoryComplete > 0) 
			{
				'click button Batal'
				WebUI.click(modifyobjectbuttonBatal)

				'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonMandatory'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 2) +
					';') + GlobalVariable.FailedReasonMandatory)
				
				continue
			}
		}
		else if (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 7).equalsIgnoreCase('ChargeType'))
		{
			'click menu tenant'
			WebUI.click(findTestObject('Tenant/menu_Tenant'))
			
			'panggil fungsi search'
			searchTenant()

			if(WebUI.verifyElementPresent(findTestObject('Tenant/button_chargeType'), GlobalVariable.Timeout, FailureHandling.OPTIONAL))
			{
				'click button services balance'
				WebUI.click(findTestObject('Tenant/button_chargeType'))
			}
			else
			{
				'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
				CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumOfColumn,
					GlobalVariable.StatusFailed, (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 2) +
					';') + GlobalVariable.FailedReasonSearchFailed)
				
				continue
			}
			
			'fungsi untuk cek apakah tenant yang aktif sesuai dengan tenant yang muncul'
			checkActiveTenant(findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 13), conndevUAT)
			
//			'penanda apakah service ditagih by price atau quantity'
//			ArrayList<Integer>isChargedByPrice= new ArrayList<Integer>()
			
			'get array Services dari excel'
			arrayServices = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 29).split(';', -1)

			'looping untuk input services check'
			for (index = 0; index < arrayServices.size(); index++)
			{
				'modify object checkbox'
				modifyObjectCheckbox = WebUI.modifyObjectProperty(findTestObject('Tenant/Services/modifyObject'), 'xpath',
					'equals', ('//*[@id="'+ arrayServices[index] +'"]'), true)
			
				'check if check box is unchecked'
				if (WebUI.verifyElementNotChecked(modifyObjectCheckbox, GlobalVariable.Timeout, FailureHandling.OPTIONAL))
				{
					'click checkbox'
					WebUI.click(modifyObjectCheckbox)
					
//					isChargedByPrice.add(1)
				}
//				else
//				{
//					isChargedByPrice.add(0)
//				}
			}
			
			'get array Services uncheck dari excel'
			arrayServices = findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 30).split(';', -1)

			'looping untuk input services uncheck'
			for (index = 0; index < arrayServices.size(); index++)
			{
				'modify object checkbox'
				modifyObjectCheckbox = WebUI.modifyObjectProperty(findTestObject('Tenant/Services/modifyObject'), 'xpath',
					'equals', ('//*[@id="'+ arrayServices[index] +'"]'), true)
				
				'check if check box is checked'
				if (WebUI.verifyElementChecked(modifyObjectCheckbox, GlobalVariable.Timeout, FailureHandling.OPTIONAL))
				{
					'click checkbox'
					WebUI.click(modifyObjectCheckbox)
				}
			}
		
			'check if mandatory complete dan button simpan clickable'
			if ((isMandatoryComplete == 0) && !(WebUI.verifyElementHasAttribute(findTestObject('Tenant/ChargeType/button_Simpan'),
				'disabled', GlobalVariable.Timeout, FailureHandling.OPTIONAL)))
			{
				'click button simpan'
				WebUI.click(findTestObject('Tenant/ChargeType/button_Simpan'))

				'check if alert berhasil muncul'
				if (WebUI.getAttribute(findTestObject('Tenant/errorLog'), 'aria-label', FailureHandling.OPTIONAL).contains(
					'berhasil'))
				{
					'write to excel success'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Tenant', 0,
						GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
				}
				else
				{
					'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonMandatory'
					CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumOfColumn,
						GlobalVariable.StatusFailed, (findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 2) +
						';') + GlobalVariable.FailedReasonQuantityNotNull)
					
					continue
				}
			}
			else if (isMandatoryComplete > 0)
			{
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
		if (GlobalVariable.KondisiCekDB == 'Yes' && isMandatoryComplete == 0) 
		{
			'call test case tenant store db'
			WebUI.callTestCase(findTestCase('Tenant/TenantStoreDB'), [('ExcelPathTenant') : 'Tenant/DataTestingTenant'], FailureHandling.STOP_ON_FAILURE)
		}
	}
}

WebUI.closeBrowser()

def checkPaging(Connection connectProd) {
	'input nama tenant'
	WebUI.setText(findTestObject('Tenant/input_NamaTenant'), 'nama tenant')

	'input status'
	WebUI.setText(findTestObject('Tenant/input_Status'), 'Active')

	'click enter untuk input select ddl'
	WebUI.sendKeys(findTestObject('Tenant/input_Status'), Keys.chord(Keys.ENTER))

	'click button set ulang'
	WebUI.click(findTestObject('Tenant/button_SetUlang'))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/input_NamaTenant'), 'value', FailureHandling.CONTINUE_ON_FAILURE),
			'', false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/input_Status'), 'value', FailureHandling.CONTINUE_ON_FAILURE),
			'', false, FailureHandling.CONTINUE_ON_FAILURE))

	'click button cari'
	WebUI.click(findTestObject('Tenant/button_Cari'))
	
	'cari button skip di footer'
	def elementbutton = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-tenant > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))
	
	'ambil banyaknya laman footer'
	int lastPage = elementbutton.size()

	'get data tenant'
	int resultTotalData = CustomKeywords.'tenant.TenantVerif.getTenantTotal'(connectProd)

	'get text total data dari ui'
	Total = WebUI.getText(findTestObject('Tenant/label_TotalData')).split(' ')

	'verify total data tenant'
	checkVerifyPaging(WebUI.verifyEqual(resultTotalData, Integer.parseInt(Total[0]), FailureHandling.CONTINUE_ON_FAILURE))

	'click page 2'
	WebUI.click(findTestObject('Tenant/button_Page2'))

	'verify paging di page 2'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/button_Page2'), 'class', FailureHandling.CONTINUE_ON_FAILURE),
			'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

	'click page 1'
	WebUI.click(findTestObject('Tenant/button_Page1'))

	'verify paging di page 1'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/button_Page1'), 'class', FailureHandling.CONTINUE_ON_FAILURE),
			'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

	'klik button next page'
	WebUI.click(findTestObject('Object Repository/OCR Testing/Page_Balance/i_Catatan_datatable-icon-right'))

	'verify paging di page 2'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/button_Page2'), 'class', FailureHandling.CONTINUE_ON_FAILURE),
			'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

	'click prev page'
	WebUI.click(findTestObject('Tenant/button_PrevPage'))

	'verify paging di page 1'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/button_Page1'), 'class', FailureHandling.CONTINUE_ON_FAILURE),
			'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

	'cek apakah button skip enable atau disable'
	if(WebUI.verifyElementVisible(findTestObject('Object Repository/API_KEY/Page_Balance/i_Catatan_datatable-icon-skip'), FailureHandling.OPTIONAL))
	{
		'klik button skip to last page'
		WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/i_Catatan_datatable-icon-skip'))
	}
	
	'modify object last Page'
	def modifyObjectmaxPage = WebUI.modifyObjectProperty(findTestObject('Tenant/button_MaxPage'), 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-tenant/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li["+ (lastPage - 2) +"]", true)

	'verify paging di page terakhir'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(modifyObjectmaxPage, 'class', FailureHandling.CONTINUE_ON_FAILURE),
			'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

	'click min page'
	WebUI.click(findTestObject('Tenant/button_MinPage'))

	'verify paging di page 1'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/button_Page1'), 'class', FailureHandling.CONTINUE_ON_FAILURE),
			'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
}

def checkVerifyPaging(Boolean isMatch) {
	if (isMatch == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
			(findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.FailedReasonPaging)

		GlobalVariable.FlagFailed = 1
	}
}

def checkActiveTenant(String tenantcode, Connection conndevUAT) {
	
	'ambil list tenant aktif di DB'
	ArrayList<String> ActiveTenantfromDB = CustomKeywords.'tenant.TenantVerif.getActiveTenant'(conndevUAT, tenantcode)
	
	'ambil list tenant aktif dari UI'
	ArrayList<String> ActiveTenantfromUI = new ArrayList<String>()
	
	for(int i=0; i< ActiveTenantfromDB.size(); i++)
	{
		'modify object checkbox'
		def modifyObjectNamaTenant = WebUI.modifyObjectProperty(findTestObject('Tenant/ChargeType/namaTenant'), 'xpath',
			'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-charge/div[2]/div/div/div/div/table/tr['+ (i+2) +']/th[1]'), true)
		
		'tambahkan nama tenant ke array'
		ActiveTenantfromUI.add(WebUI.getText(modifyObjectNamaTenant))
	}
	
	if(!ActiveTenantfromUI.containsAll(ActiveTenantfromDB))
	{
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumOfColumn, GlobalVariable.StatusFailed,
			(findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn, 2) + ';') + GlobalVariable.FailedReasonServiceNotMatch)

		GlobalVariable.FlagFailed = 1
	}
}

def searchTenant() {
	'click button cari'
	WebUI.click(findTestObject('Tenant/button_Cari'))
	
	'input nama tenant'
	WebUI.setText(findTestObject('Tenant/input_NamaTenant'), findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn,
			9))

	'input status'
	WebUI.setText(findTestObject('Tenant/input_Status'), findTestData(ExcelPathTenant).getValue(GlobalVariable.NumOfColumn,
			10))

	'click enter untuk input select ddl'
	WebUI.sendKeys(findTestObject('Tenant/input_Status'), Keys.chord(Keys.ENTER))

	'click button cari'
	WebUI.click(findTestObject('Tenant/button_Cari'))
}
