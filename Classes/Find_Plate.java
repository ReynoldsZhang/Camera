import java.util.ArrayList;

//Using OpenCV Library

//Importing required classes
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import net.sourceforge.tess4j.TesseractException;

/**
 * This class is used to find Plates in a photo and returns an Array List of
 * plate reads
 * 
 * @author you
 *
 */
public class Find_Plate {

	static int NumberOfPhotosWithPlate = 0;

	/**
	 * This method finds the plate in the photo, It takes the photo name - String
	 * InputFileName It takes the Camera system object - Camera_System Cameras It
	 * takes a boolean called test - Boolean Test (to know which folder to find the
	 * photo)
	 * 
	 * It Returns a 2D Array List called List of Plates - the reason this is a 2D
	 * array is because a photo can have multiple plates in one photo - each
	 * String[] in the Array List is of length 4
	 * 
	 * [0] = Chinese Read of the unedited photo [1] = English Read of the unedited
	 * photo [2] = Chinese Read of the edited photo [4] = English Read of the edited
	 * photo
	 * 
	 * 
	 * 
	 * @param InputFileName
	 * @param Cameras
	 * @param Test
	 * @return
	 */
	public static ArrayList<String[]> FindCarPlate(String InputFileName, Camera_System Cameras, Boolean Test) {

		/**
		 * This Method returns an Array List, and it's called ListOfPlates
		 */

		ArrayList<String[]> ListOfPlates = new ArrayList<String[]>();

		/**
		 * Core.NATIVE_LIBRARY_NAME must be loaded before calling any of the opencv
		 * methods
		 */

		System.load("C:\\Users\\hggjb2021\\Desktop\\Projects\\To Give to Students\\opencv\\build\\java\\opencv_java460.dll");
//		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		/**
		 * Creates a new Cascade Classifier Object called Plate_Detector which takes the
		 * xml file - this is used to find the plate in the photo
		 */

		CascadeClassifier Plate_Detector = new CascadeClassifier();
		Plate_Detector.load("Chinese_Plates.xml");

		/**
		 * The Mat class of OPENCV is used to store the values of an image
		 */

		Mat image;

		/**
		 * If its a test you want to use the Test Directory, If its a live test then all
		 * photos are saved in Camera Photos
		 */

		if (Test) {

			// Reading the input image
			image = Imgcodecs.imread(Home.getTestDirectory() + InputFileName);

		} else {

			// Reading the input image
			image = Imgcodecs.imread(Home.getDirectory() + "Camera Photos\\" + InputFileName);

		}

		/**
		 * An object of the class MatOfRect called Plate_Detections is used to store the
		 * detected number plates.
		 */

		MatOfRect Plate_Detections = new MatOfRect();

		/**
		 * detectMultiScale finds the number plates The detected number plates are
		 * returned as a list of rectangles. We will loop over the results later
		 */

		Plate_Detector.detectMultiScale(image, Plate_Detections);

		/**
		 * Removes the extension of the Photo
		 */

		String JPGFile = removeExtension(InputFileName);

		/**
		 * If there are no Plates found the Photo null is returned and the GUI is
		 * updated
		 */

		if (Plate_Detections.toArray().length == 0 || Plate_Detections.toArray() == null) {

			Cameras.addProcessingText("No Plate Found");

			image.release();

			return null;
		}

		/**
		 * Saves the Photo where a plate has been found, this is used in the GUI
		 */

		String filenameAndLocation = Home.getDirectory() + "Photos Where Number Plate Found\\" + JPGFile + ".jpg";
		Imgcodecs.imwrite(filenameAndLocation, image);

		/**
		 * Updates the the Image on the GUI (Top left Photo)
		 */

		Cameras.updateWholeImage(filenameAndLocation);

		/**
		 * We now know that there is a plate in the photo so we can Update the GUI
		 */

		NumberOfPhotosWithPlate++;

		Cameras.addPlateCounter("Number of Plates: " + NumberOfPhotosWithPlate);

		/**
		 * We convert the Plate_Detections object to an array so we can loop over each
		 * plate found
		 * 
		 * So this For loop Goes though all the plates found in the photo
		 * 
		 * The length of the array Plate_Detections.toArray() represents the number of
		 * plates found in the photo
		 * 
		 */

		for (Rect rect : Plate_Detections.toArray()) {

			Cameras.addProcessingText("↻ Plate Processing");

			/**
			 * ListOfPlates is an ArrayList, PlateAlgorithm will return the OCR reads from a
			 * Plate PlateAlgorithm returns an Array of Length 4
			 */

			ListOfPlates.add(PlateAlgorithm(Cameras, rect, image, JPGFile));

		}

		Mat imageWithIdentifier = image;

		if (!Test) {

			SavePlateWithIdentifier(Cameras, imageWithIdentifier, JPGFile, Plate_Detections);

		}

		/**
		 * Clears the Image from memory or when you test it will crash your computer (As
		 * I found out)
		 */

		image.release();

		/**
		 * Returns the 2D Array
		 */

		return ListOfPlates;

	}

	/**
	 * This Finds the text from each plate
	 * 
	 * @param Cameras
	 * @param rect
	 * @param ANPRLocationFolder
	 * @param image
	 * @param JPGFile
	 * @return
	 */
	public static String[] PlateAlgorithm(Camera_System Cameras, Rect rect, Mat image, String JPGFile) {

		/**
		 * Creates a directory string to store the cropped plate image
		 */

		String filenameAndLocation = Home.getDirectory() + "Number Plate Images\\" + "Plate " + JPGFile + ".jpg";

		/**
		 * Creates the area where the plate is, so we can crop it
		 */

		Rect rectCrop = new Rect(rect.x, rect.y, rect.width, rect.height);

		/**
		 * Creates an image of the plate using the rectCrop area
		 */

		Mat image_roi;

		try {

			image_roi = new Mat(image, rectCrop);

		} catch (Exception e) {

			Cameras.addProcessingText("↻ Matt Conversion Error");

			return null;

		}

		/**
		 * Saves the Image
		 */

		Imgcodecs.imwrite(filenameAndLocation, image_roi);

		/**
		 * Updates the Image in the GUI
		 */

		Cameras.updateIdentifiedImagePhoto(filenameAndLocation);

		try {

			Cameras.addProcessingText("↻ Reading Plate");

			/**
			 * Using the cropped image of the plate we do OCR on it
			 * 
			 * text2ChineseOriginal String contains the OCR where we look for Chinese and
			 * Latin characters text2EnglishOriginal String contains the OCR where we look
			 * for Latin characters
			 * 
			 */

			String text2ChineseOriginal = Get_Plate_Text.getTextChinese(filenameAndLocation);
			String text2EnglishOriginal = Get_Plate_Text.getTextEnglish(filenameAndLocation);

			/**
			 * We now process the image (e.g. gray scaling it) so we can improve the OCR,
			 * ProcessedImageLocation saves where the processed image is
			 */

			Cameras.addProcessingText("↻ Photo Processsing");

			String ProcessedImageLocation = Image_Processing.processImage(filenameAndLocation);

			/**
			 * We do OCR again on the cropped image
			 * 
			 * text2ChineseEditedPhoto String contains the OCR where we look for Chinese and
			 * Latin characters text2EnglishEditedPhoto String contains the OCR where we
			 * look for Latin characters
			 * 
			 */

			Cameras.addProcessingText("↻ Reading Plate");

			String text2ChineseEditedPhoto = Get_Plate_Text.getTextChinese(ProcessedImageLocation);
			String text2EnglishEditedPhoto = Get_Plate_Text.getTextEnglish(ProcessedImageLocation);

			Cameras.updateAlteredIdentifiedImagePhoto(ProcessedImageLocation);

			/**
			 * The String array PlateReads contains all the OCR results which is then
			 * returned
			 */

			String[] PlateReads = { text2ChineseOriginal.trim(), text2EnglishOriginal.trim(),
					text2ChineseEditedPhoto.trim(), text2EnglishEditedPhoto.trim() };

			return PlateReads;

		} catch (TesseractException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * This method creates the photo with the green identifiers that show where the
	 * number plate is in the photo You don't need to use this - It's just there if
	 * you want it
	 * 
	 * @param Cameras
	 * @param rect
	 * @param imageToProcess
	 * @param JPGFile
	 * @return
	 */
	public static boolean SavePlateWithIdentifier(Camera_System Cameras, Mat imageToProcess, String JPGFile,
			MatOfRect Plate_Detections) {

		try {

			/**
			 * This creates the new rectangle which will be added to the photo
			 */

			for (Rect rect : Plate_Detections.toArray()) {

				Imgproc.rectangle(imageToProcess, new Point(rect.x, rect.y),
						new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));

			}
			/**
			 * String so that we have a file name
			 */

			String ImageFileName = "NEW WGR " + JPGFile + ".jpg";

			/**
			 * Writes the Image to the folder using the file name above
			 */

			Imgcodecs.imwrite(Home.getDirectory() + "Photos With Green Identifiers\\" + ImageFileName, imageToProcess);

			/**
			 * Updates the Image on the GUI
			 */

			Cameras.updateWholeImage(Home.getDirectory() + "Photos With Green Identifiers\\" + ImageFileName);

			imageToProcess.release();

		} catch (Exception e) {
			System.out.println("Plate_With_Green_Rectangle Error: " + e);
			return false;
		}
		return true;

	}

	/**
	 * Removes the extension (e.g. .png) so we can play with the photo
	 * 
	 * @param s
	 * @return
	 */
	public static String removeExtension(String s) {

		String separator = System.getProperty("file.separator");
		String filename;

		// Remove the path on the filename.
		int lastSeparatorIndex = s.lastIndexOf(separator);
		if (lastSeparatorIndex == -1) {
			filename = s;
		} else {
			filename = s.substring(lastSeparatorIndex + 1);
		}

		// Remove the extension.
		int extensionIndex = filename.lastIndexOf(".");
		if (extensionIndex == -1)
			return filename;

		return filename.substring(0, extensionIndex);
	}

}