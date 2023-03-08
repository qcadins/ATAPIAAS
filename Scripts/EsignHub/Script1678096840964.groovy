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

WebUI.openBrowser('')

WebUI.navigateToUrl('http://websvr:8000/login')

WebUI.setText(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/input_Selamat datang kembali di Billing Sys_95ee84'), 
    'ADMESIGN')

WebUI.setEncryptedText(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/input_Selamat datang kembali di Billing Sys_0aaa8d'), 
    '8SQVv/p9jVScEs4/2CZsLw==')

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/button_Masuk'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/i_ADMIN ESIGN_ft-chevron-down'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/span_My Profile'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/i_ADMIN ESIGN_ft-chevron-down'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/a_Ubah Kode Akses'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/button_Batal'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/i_ADMIN ESIGN_ft-chevron-down'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/img_Indonesian_avatar d-md-flex d-none'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/i_Indonesian_ft-menu font-medium-3'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/img_Keluar_logo-img ng-tns-c99-1'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/i_Indonesian_ft-menu font-medium-3_1'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/span_Isi Saldo'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/span_Pilih Tenant_ng-arrow-wrapper'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/div_SMS Finance'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/span_ADINS_ng-arrow-wrapper'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/div_ADINS'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/span_Pilih Tipe Saldo_ng-arrow-wrapper'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/div_OCR KTP'))

WebUI.setText(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/input_Tambah Saldo_qty'), 
    '12.000')

WebUI.setText(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/input_Nomor Tagihan_refNo'), 
    '1234')

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/input_Catatan_notes'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/fieldset_Tanggal Pembelian'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/i_Tanggal Pembelian_fa fa-calendar'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/div_6'))

WebUI.setText(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/input_Catatan_notes'), 
    'Tidak ada')

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/button_Lanjut'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/button_Tidak, batalkan'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/i_Indonesian_ft-menu font-medium-3_1'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/span_Tenant'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/a_Aksi_mr-2 text-primary ng-star-inserted'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/button_Add Credit Decision Engine'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/button_Add Verification'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/button_Add Document'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/button_Batal_1'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/em_Aksi_align-middle cursor-pointer font-me_44ff46'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/th_Verifikasi Dukcapil Tanpa Biometrik_ng-s_44e6b0'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/input_Verifikasi Dukcapil Tanpa Biometrik_D_8fb83e'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/input_Verifikasi Dukcapil Tanpa Biometrik_D_0d87e0'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/input_Credit Decision Engine_CDEINDR'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/button_Batal_1'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/div_Pilih statusAll'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/div_Active'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/i_Cari_ft-search'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/button_Cari'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/input'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/div_Inactive'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/button_Cari'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/button_Set Ulang'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/a_Baru'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/button_Generate'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/i_ADMIN ESIGN_ft-chevron-down'))

WebUI.click(findTestObject('Object Repository/Esign/Page_eSignHub - Adicipta Inovasi Teknologi/span_Keluar'))

WebUI.closeBrowser()

