
import java.io.File;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class Get_Plate_Text {

	/**
	 * Gets the text from the Image - Looking specifically for Chinese characters
	 * 
	 * @param FileNameAndLocation
	 * @return
	 * @throws TesseractException
	 */
	public static String getTextChinese(String FileNameAndLocation) throws TesseractException {

		/**
		 * Creates a new Tesseract Object
		 */
		
		Tesseract tesseractChinese = new Tesseract();
		
		/**
		 * Setting the language to simplified Chinese
		 */
		
		tesseractChinese.setLanguage("chi_sim");
		
		/**
		 * This is where the Tesseract data is saved
		 */
		
		tesseractChinese.setDatapath("Tess4J\\tessdata");

		/**
		 * This says what characters we are looking for in the image - These are all the
		 * characters used in Chinese License Plates and the Alphanumerics
		 */
		
		String whiteListChinese = "京渝沪津皖闽甘粤贵琼冀黑豫鄂湘苏赣吉辽青陕鲁晋川云浙桂蒙宁藏新ABCDEFGHJKLMNPQRSTUVWXYZ0123456789";

		/**
		 * Sets the Tesseract Variables
		 */
		
		tesseractChinese.setTessVariable("tessedit_char_whitelist", whiteListChinese);

		/**
		 * Does the OCR
		 */
		
		String PlateText = tesseractChinese.doOCR(new File(FileNameAndLocation));
		
		/**
		 * Tesseract does OCR and the output is then returned to Find_Plate
		 */

		return PlateText;

	}

	/**
	 * Same as getTextChinese but only looking for Latin characters in the Image
	 * @param FileNameAndLocation
	 * @return
	 * @throws TesseractException
	 */
	
	public static String getTextEnglish(String FileNameAndLocation) throws TesseractException {

		Tesseract tesseractEnglish = new Tesseract();
		tesseractEnglish.setLanguage("eng");
		tesseractEnglish.setDatapath("Tess4J\\tessdata");
		String whiteListEnglish = "ABCDEFGHJKLMNPRSTUVWXYZ0123456789";
		tesseractEnglish.setTessVariable("tessedit_char_whitelist", whiteListEnglish);

		String PlateText = tesseractEnglish.doOCR(new File(FileNameAndLocation));

		/**
		 * Tesseract does OCR and the output is then returned to Find_Plate
		 */
		
		return PlateText;

	}

}
