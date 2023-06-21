package customizeKeyword

import java.awt.Desktop
import com.kms.katalon.core.annotation.Keyword
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.After
import java.awt.event.KeyEvent
import java.lang.String
import java.awt.Robot

import org.openqa.selenium.WebElement
import org.openqa.selenium.WebDriver
import org.openqa.selenium.By

import com.kms.katalon.core.mobile.keyword.internal.MobileDriverFactory
import com.kms.katalon.core.webui.driver.DriverFactory

import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty

import com.kms.katalon.core.mobile.helper.MobileElementCommonHelper
import com.kms.katalon.core.util.KeywordUtil

import com.kms.katalon.core.webui.exception.WebElementNotFoundException

public class openCloseExcel {
	
	@Keyword
	def openCloseFileWithRefreshVal(String FilePath) {

		File file = new File(FilePath)

		//first check if Desktop is supported by Platform or not

		Desktop desktop = Desktop.getDesktop()

		if(file.exists()){
			desktop.open(file)
		}
		//digunakan close confirmation license activation pada office, jika sudah berlicense dapat dihapus
		Robot robot = new Robot();
		robot.delay(3000)
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_SHIFT);
		robot.keyPress(KeyEvent.VK_ALT);
		robot.keyPress(KeyEvent.VK_F9);
		robot.keyRelease(KeyEvent.VK_F9);
		robot.keyRelease(KeyEvent.VK_ALT);
		robot.keyRelease(KeyEvent.VK_SHIFT);
		robot.delay(3000)
		robot.keyPress(KeyEvent.VK_S);
		robot.keyRelease(KeyEvent.VK_S)
		robot.keyRelease(KeyEvent.VK_CONTROL)
		robot.delay(5000)
		Runtime.getRuntime().exec("taskkill /IM EXCEL.EXE")

	}
}
