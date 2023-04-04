package documentationAPI


import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import java.sql.Connection
import java.sql.Statement

import javax.servlet.http.HttpServletRequest
import javax.swing.ComboBoxModel

import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.Select
import com.kms.katalon.core.webui.driver.DriverFactory
import groovy.sql.Sql as Sql
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

import internal.GlobalVariable

public class checkDocumentation {

	def driver = DriverFactory.getWebDriver()

	def js = (JavascriptExecutor)driver

	int columnCount

	@Keyword
	def isFileDownloaded(String deleteFile) {
		boolean isDownloaded = false
		File dir = new File(System.getProperty('user.dir') + "\\Download");
		//Getting the list of all the files in the specific directory
		File[] fList = dir.listFiles();
		for (File f : fList)
		{
			//checking the extension of the file with endsWith method.
			if (f.exists())
			{
				if(deleteFile == 'Yes')
				{
					f.delete();
				}
				isDownloaded = true
			}
		}
		return isDownloaded
	}

	//fungsi untuk mengambil data dokumentasi dari DB
	@Keyword
	public getDocumentationAPIName(Connection conn) {
		String data

		ArrayList<String> listdata = new ArrayList<>()

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT api_name from ms_api")
		ResultSetMetaData metadata  = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while(resultSet.next()) {
			for(int i=1; i<=columnCount ; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		return listdata
	}

	//fungsi digunakan untuk mengambil text dari dropdownlist documentation API
	@Keyword
	public getValueDDLDocumentationAPI() {

		String ariachoice,ariaid

		ariaid = WebUI.getAttribute(findTestObject('Object Repository/API_KEY/Page_API Documentation/input'), 'aria-owns')

		ArrayList<String> hasilddl = new ArrayList<>()

		for(int i = 0; i <10; i++)
		{
			ariachoice = ariaid + "-" + i
			if(ariachoice.contains("-0"))
			{
				hasilddl.add('OCR BPKB')
			}
			else if(ariachoice.contains("-1"))
			{
				hasilddl.add('OCR REK KORAN MANDIRI')
			}
			else if(ariachoice.contains("-2"))
			{
				hasilddl.add('LIVENESS + FACECOMPARE')
			}
			else if(ariachoice.contains("-3"))
			{
				hasilddl.add('OCR KK')
			}
			else if(ariachoice.contains("-4"))
			{
				hasilddl.add('OCR REK KORAN BCA')
			}
			else if(ariachoice.contains("-5"))
			{
				hasilddl.add('OCR STNK')
			}
			else if(ariachoice.contains("-6"))
			{
				hasilddl.add('FACECOMPARE')
			}
			else if(ariachoice.contains("-7"))
			{
				hasilddl.add('OCR KTP')
			}
			else if(ariachoice.contains("-8"))
			{
				hasilddl.add('OCR NPWP')
			}
			else if(ariachoice.contains("-9"))
			{
				hasilddl.add('LIVENESS')
			}
		}
		return hasilddl
	}
}