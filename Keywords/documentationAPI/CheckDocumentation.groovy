package documentationAPI

import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import java.sql.Connection
import java.sql.Statement

import java.sql.ResultSet
import java.sql.ResultSetMetaData
import groovy.sql.Sql as Sql

public class CheckDocumentation {

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
		isDownloaded
	}

	//fungsi untuk mengambil data dokumentasi dari DB
	@Keyword
	getDocumentationAPIName(Connection conn) {
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
		listdata
	}

	//fungsi digunakan untuk mengambil text dari dropdownlist documentation API
	@Keyword
	getValueDDLDocumentationAPI() {

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
		hasilddl
	}
}