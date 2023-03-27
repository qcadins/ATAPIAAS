package writeToExcel

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

import internal.GlobalVariable

public class checkSaveProcess {

	//check status untuk write to excel success / failed + reason failed
	@Keyword
	public checkStatus (int count, TestObject object, int colm, String sheetname){
		if(WebUI.verifyElementPresent(object, 3, FailureHandling.OPTIONAL)){
			if(count==0){
				(new writeToExcel.writeExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
						0, colm - 1, GlobalVariable.StatusSuccess)
			}
			else{
				GlobalVariable.FlagFailed = 1
				(new writeToExcel.writeExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
						0, colm - 1, GlobalVariable.StatusFailed)
				(new writeToExcel.writeExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
						1, colm - 1, GlobalVariable.StatusReasonSystem)
			}
		}else{
			if(count==0){
				GlobalVariable.FlagFailed = 1
				(new writeToExcel.writeExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
						0, colm - 1, GlobalVariable.StatusFailed)
				(new writeToExcel.writeExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
						1, colm - 1, GlobalVariable.FailedReasonSubmitError)
			}
			else{
				GlobalVariable.FlagFailed = 1
				(new writeToExcel.writeExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
						0, colm - 1, GlobalVariable.StatusFailed)
				(new writeToExcel.writeExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
						1, colm - 1, GlobalVariable.FailedReasonMandatory)
			}
		}
	}


	//check alert pojok kanan atas jika failed akan write to excel failed + reason failed
	@Keyword
	public checkAlert(int colm, String sheetname){
		if(WebUI.verifyElementPresent(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/div_Kunci API dengan tipe tersebut sudah aktif'), 1, FailureHandling.OPTIONAL)){
			String erroralert = WebUI.getText(findTestObject('Object Repository/API_KEY/Page_Edit Api Key/div_Kunci API dengan tipe tersebut sudah aktif'), FailureHandling.OPTIONAL)
			if(erroralert!=null){
				if(!erroralert.contains("Success".toUpperCase())){

					String FailedAlertReason = 'Pengubahan API KEY gagal karena sudah aktif'
					(new writeToExcel.writeExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
							0, colm - 1, GlobalVariable.StatusFailed)
					(new writeToExcel.writeExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
							1, colm - 1, FailedAlertReason)
					GlobalVariable.FlagFailed = 1
				}
			}

		}
	}

	//check status untuk write to excel success / failed + reason failed
	@Keyword
	public checkStatusbtnClickable (int count, TestObject object, int colm, String sheetname){
		if(WebUI.verifyElementClickable(object, FailureHandling.OPTIONAL)){
			if(count==0){
				(new writeToExcel.writeExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
						0, colm - 1, GlobalVariable.StatusSuccess)
			}
			else{
				GlobalVariable.FlagFailed = 1
				(new writeToExcel.writeExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
						0, colm - 1, GlobalVariable.StatusFailed)
				(new writeToExcel.writeExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
						1, colm - 1, GlobalVariable.StatusReasonSystem)
			}
		}else{
			if(count==0){
				GlobalVariable.FlagFailed = 1
				(new writeToExcel.writeExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
						0, colm - 1, GlobalVariable.StatusFailed)
				(new writeToExcel.writeExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
						1, colm - 1, GlobalVariable.FailedReasonSubmitError)
			}
			else{
				GlobalVariable.FlagFailed = 1
				(new writeToExcel.writeExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
						0, colm - 1, GlobalVariable.StatusFailed)
				(new writeToExcel.writeExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
						1, colm - 1, GlobalVariable.FailedReasonMandatory)
			}
		}
	}

}
