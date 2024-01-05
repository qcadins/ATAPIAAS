package ocrtesting

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdfparser.PDFParser
import org.apache.pdfbox.io.RandomAccessFile
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class Pdfprocessor {

	@Keyword
	countPages() {
		int pageCount
		String pdfPath = 'ImageFolder/RKMandiri/RKMandiri' + (GlobalVariable.NumOfColumn - 1) + '.pdf'
		PDFParser parser = new PDFParser(new RandomAccessFile(new File(pdfPath), 'r'))
		parser.parse()
		PDDocument document = parser.PDDocument
		pageCount = document.numberOfPages
		document.close()

		pageCount
	}
	
}