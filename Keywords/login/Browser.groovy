package login

import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.webui.driver.DriverFactory
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.WebDriver
import com.kms.katalon.core.annotation.Keyword

public class Browser {
	
	@Keyword
	settingandOpen(String Paths, int captchaRow) {
		WebDriver driver

		System.setProperty('webdriver.chrome.driver', 'Drivers/chromedriver.exe')

		HashMap<String, ArrayList> chromePrefs = [:] as HashMap<String, ArrayList>

		chromePrefs.put('download.default_directory', System.getProperty('user.dir') + '\\Download')

		ChromeOptions options = new ChromeOptions()

		'jika captcha perlu diaktifkan, maka extension akan ter-load secara otomatis'
		if (findTestData(Paths).getValue(GlobalVariable.NumOfColumn, captchaRow) == 'Yes' ||
		findTestData(Paths).getValue(GlobalVariable.NumOfColumn, captchaRow) == '') {
			//	options.addExtensions(new File("Drivers/nocaptchaai_chrome_1.7.6.crx"))
			options.addExtensions(new File('Drivers/anticaptcha-plugin_v0.65.crx'))
		}

		options.addExtensions(new File('Drivers/Smart_Wait.crx'))

		options.setExperimentalOption('prefs', chromePrefs)

		DesiredCapabilities caps = new DesiredCapabilities()

		caps.setCapability(ChromeOptions.CAPABILITY, options)

		driver = new ChromeDriver(caps)

		DriverFactory.changeWebDriver(driver)

		JavascriptExecutor js = (JavascriptExecutor)driver

		js
	}
}