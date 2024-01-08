package customizekeyword

import java.awt.Desktop
import com.kms.katalon.core.annotation.Keyword
import java.awt.event.KeyEvent
import java.awt.Robot

public class OpenCloseExcel {

	@Keyword
	def openCloseFileWithRefreshVal(String filePath) {
		File file = new File(filePath)

		//first check if Desktop is supported by Platform or not

		Desktop desktop = Desktop.desktop

		if (file.exists()) {
			desktop.open(file)
		}
		//digunakan close confirmation license activation pada office, jika sudah berlicense dapat dihapus
		Robot robot = new Robot()
		robot.delay(3000)
		robot.keyPress(KeyEvent.VK_CONTROL)
		robot.keyPress(KeyEvent.VK_SHIFT)
		robot.keyPress(KeyEvent.VK_ALT)
		robot.keyPress(KeyEvent.VK_F9)
		robot.keyRelease(KeyEvent.VK_F9)
		robot.keyRelease(KeyEvent.VK_ALT)
		robot.keyRelease(KeyEvent.VK_SHIFT)
		robot.delay(3000)
		robot.keyPress(KeyEvent.VK_S)
		robot.keyRelease(KeyEvent.VK_S)
		robot.keyRelease(KeyEvent.VK_CONTROL)
		robot.delay(5000)
		Runtime.runtime.exec('taskkill /IM EXCEL.EXE')
	}
	
}