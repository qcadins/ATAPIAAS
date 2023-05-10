package writeToExcel

import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable

public class CheckSaveProcess {
	
	TestObject testerAlert = findTestObject('Object Repository/API_KEY/Page_Edit Api Key/div_Kunci API dengan tipe tersebut sudah aktif')

	//check status untuk write to excel success / failed + reason failed
	@Keyword
	checkStatus(int count, TestObject object, int colm, String sheetname){
		if(WebUI.verifyElementPresent(object, 3, FailureHandling.OPTIONAL)){
			if(count==0){
				(new writeToExcel.WriteExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
						0, colm - 1, GlobalVariable.StatusSuccess)
			}
			else{
				GlobalVariable.FlagFailed = 1
				(new writeToExcel.WriteExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
						0, colm - 1, GlobalVariable.StatusFailed)
				(new writeToExcel.WriteExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
						1, colm - 1, GlobalVariable.StatusReasonSystem)
			}
		}else{
			if(count==0){
				GlobalVariable.FlagFailed = 1
				(new writeToExcel.WriteExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
						0, colm - 1, GlobalVariable.StatusFailed)
				(new writeToExcel.WriteExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
						1, colm - 1, GlobalVariable.FailedReasonSubmitError)
			}
			else{
				GlobalVariable.FlagFailed = 1
				(new writeToExcel.WriteExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
						0, colm - 1, GlobalVariable.StatusFailed)
				(new writeToExcel.WriteExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
						1, colm - 1, GlobalVariable.FailedReasonMandatory)
			}
		}
	}


	//check alert pojok kanan atas jika failed akan write to excel failed + reason failed
	@Keyword
	checkAlert(int colm, String sheetname){
		if(WebUI.verifyElementPresent(testerAlert, 1, FailureHandling.OPTIONAL)){
			String erroralert = WebUI.getText(testerAlert, FailureHandling.OPTIONAL)
			if(erroralert!=null){
				if(!erroralert.contains("Success".toUpperCase())){

					String FailedAlertReason = 'Pengubahan API KEY gagal karena sudah aktif'
					(new writeToExcel.WriteExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
							0, colm - 1, GlobalVariable.StatusFailed)
					(new writeToExcel.WriteExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
							1, colm - 1, FailedAlertReason)
					GlobalVariable.FlagFailed = 1
				}
			}

		}
	}

	//check status untuk write to excel success / failed + reason failed
	@Keyword
	checkStatusbtnClickable(int count, TestObject object, int colm, String sheetname){
		if(WebUI.verifyElementClickable(object, FailureHandling.OPTIONAL)){
			if(count==0){
				(new writeToExcel.WriteExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
						0, colm - 1, GlobalVariable.StatusSuccess)
			}
			else{
				GlobalVariable.FlagFailed = 1
				(new writeToExcel.WriteExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
						0, colm - 1, GlobalVariable.StatusFailed)
				(new writeToExcel.WriteExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
						1, colm - 1, GlobalVariable.StatusReasonSystem)
			}
		}else{
			if(count==0){
				GlobalVariable.FlagFailed = 1
				(new writeToExcel.WriteExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
						0, colm - 1, GlobalVariable.StatusFailed)
				(new writeToExcel.WriteExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
						1, colm - 1, GlobalVariable.FailedReasonSubmitError)
			}
			else{
				GlobalVariable.FlagFailed = 1
				(new writeToExcel.WriteExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
						0, colm - 1, GlobalVariable.StatusFailed)
				(new writeToExcel.WriteExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
						1, colm - 1, GlobalVariable.FailedReasonMandatory)
			}
		}
	}

}
