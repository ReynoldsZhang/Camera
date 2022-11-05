

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.lowagie.text.Font;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import com.opencsv.CSVWriter;

public class Camera_System {

	static CSVWriter writer;
	static String DatabaseName = "";

	public Container getContentPane() {
		return null;
	}

	List<Webcam> webcams;
	Webcam webcam;

	/**
	 * Creates all the labels used to show plate information
	 */
	JLabel PlateInformation = new JLabel("↻ Plate Information Here");
	JLabel CarStatusText = new JLabel("↻ Car Status Here");
	JLabel CarReg = new JLabel("↻ Plate Will Appear Here");
	JPanel PlateImagePanel = new JPanel();
	JLabel PlateImagePanelLabel = new JLabel("Plate Image");
	JPanel AlteredImagePlateImagePanel = new JPanel();
	JLabel AlteredImagePlateImagePanelLabel = new JLabel("Altered Plate Image");
	JLabel ProcessingText = new JLabel("Processing Updates Here");
	JLabel CarOwner = new JLabel("Name");
	JLabel CarLocation = new JLabel("Location");
	JLabel ManufacturerAndYear = new JLabel("Manufacturer (Year)");
	JLabel InsurancePurchaseDate = new JLabel("YYYY / MM / DD");
	JLabel textField = new JLabel("");
	ImageIcon imageIcon = new ImageIcon();
	JLabel WholeImageJLabel = new JLabel();
	JLabel Time = new JLabel("Time");
	JLabel NoOfPlateCaptures = new JLabel("Plate Captures");
	JLabel IdentifiedImagePanelPhotoLabel = new JLabel();
	JLabel AlteredIdentifiedImagePanelPhotoLabel = new JLabel();
	JPanel ProcessingPanelLiveUpdates = new JPanel();

	public Camera_System(int Test) {

	}

	public Camera_System() {

		DatabaseName = Home.getPhotoTime(false) + ".csv";

		/**
		 * CAMERA VIEW SETUP
		 */

		this.webcams = Webcam.getWebcams();

		if (webcams.size() >= 2) {
			this.webcam = webcams.get(1);

		} else {
			this.webcam = webcams.get(0);
		}

		/**
		 * Used to create the camera resolution
		 */
		Dimension[] nonStandardResolutions = new Dimension[] { WebcamResolution.PAL.getSize(),
				WebcamResolution.HVGA.getSize(), new Dimension(1980, 1080), };

		webcam.setCustomViewSizes(nonStandardResolutions);
//		webcam.setViewSize(WebcamResolution.HD720.getSize());

		/**
		 * Sets the Camera Resolution to 1080p
		 */
		webcam.setViewSize(new Dimension(1980, 1080));
		webcam.open();

		WebcamPanel panel = new WebcamPanel(webcam);
		panel.setFPSDisplayed(true);
		panel.setDisplayDebugInfo(true);
		panel.setImageSizeDisplayed(true);
		panel.setMirrored(false);

		JPanel totalGUI = new JPanel();
		totalGUI.setLayout(new BoxLayout(totalGUI, BoxLayout.Y_AXIS));

		/**
		 * TOP Label GUI
		 */
		JPanel redPanel = new JPanel();
		redPanel.setBackground(Color.decode("#cf2a2a"));
		redPanel.setMinimumSize(new Dimension(450, 50));
		redPanel.setPreferredSize(new Dimension(450, 50));
		JLabel Title = new JLabel("LAOWAI Automatic License Plate Recognition");
		Title.setFont(new java.awt.Font("Century Schoolbook L", Font.BOLD, 26));
		Title.setForeground(Color.WHITE);
		redPanel.add(Title);
		totalGUI.add(redPanel);

		// This is the first spacer. This creates a spacer 10px wide that
		// will never get bigger or smaller.
		totalGUI.add(Box.createRigidArea(new Dimension(10, 0)));

		/**
		 * WHOLE PHOTO GUI
		 */
		JPanel WholePhotoWithPlateID = new JPanel();
		WholePhotoWithPlateID.setBackground(Color.decode("#d1d6db"));
		WholePhotoWithPlateID.setPreferredSize(new Dimension(50, 50));

		/**
		 * Used to show the whole photo taken by the camera
		 */
		JLabel WholePhotoLabel = new JLabel("Photo From ANPR Camera");
		WholePhotoLabel.setForeground(Color.decode("#475e71"));
		WholePhotoLabel.setFont(new java.awt.Font("Century Schoolbook L", Font.BOLD, 30));
		WholePhotoWithPlateID.add(WholePhotoLabel);

		Image img = null;
		try {
			img = ImageIO.read(new File("WaitingWholePhoto.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Image dimg = img.getScaledInstance(550, 280, Image.SCALE_SMOOTH);
		imageIcon.setImage(dimg);
		WholeImageJLabel.setIcon(imageIcon);

		WholePhotoWithPlateID.add(WholeImageJLabel);

		JPanel yellowPanel2 = new JPanel();
		yellowPanel2.setBackground(Color.decode("#d1d6db"));
		yellowPanel2.setPreferredSize(new Dimension(50, 300));
		yellowPanel2.add(WholeImageJLabel);

		totalGUI.add(WholePhotoWithPlateID);
		totalGUI.add(yellowPanel2);

		/**
		 * IDENTIFIED PLATE IMAGE GUI
		 */
		
		int PlateImagesGUIHeight = 50;
		int PlateImagesImageGUIHeight = 90;

		JPanel IdentifiedImagePanel = new JPanel();
		IdentifiedImagePanel.setBackground(Color.decode("#8c9aa6"));
		IdentifiedImagePanel.setPreferredSize(new Dimension(50, PlateImagesGUIHeight));
		JLabel y = new JLabel("Identified Plate Image");
		y.setFont(new java.awt.Font("Century Schoolbook L", Font.BOLD, 30));
		IdentifiedImagePanel.add(y);
		totalGUI.add(IdentifiedImagePanel);

		JPanel IdentifiedImagePanelPhoto = new JPanel();
		IdentifiedImagePanelPhoto.setBackground(Color.decode("#8c9aa6"));
		IdentifiedImagePanelPhoto.setPreferredSize(new Dimension(50, PlateImagesImageGUIHeight));

		/**
		 * Puts the image in the Panel - first photo is a loading image
		 */
		Image IDPlateImage = null;
		try {
			IDPlateImage = ImageIO.read(new File("WaitingWholePhoto.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Image dimgx = IDPlateImage.getScaledInstance(250, 70, Image.SCALE_SMOOTH);
		ImageIcon ImageIcon2 = new ImageIcon();
		ImageIcon2.setImage(dimgx);

		IdentifiedImagePanelPhotoLabel.setIcon(ImageIcon2);
		IdentifiedImagePanelPhoto.add(IdentifiedImagePanelPhotoLabel);

		/**
		 * Adds the identified image to the GUI
		 */
		totalGUI.add(IdentifiedImagePanelPhoto);

		/**
		 * Adds the space between the panels
		 */
		totalGUI.add(Box.createHorizontalGlue());

		/**
		 * ALTERED IMAGE GUI
		 */
		JPanel AlteredImagePanel = new JPanel();
		AlteredImagePanel.setBackground(Color.decode("#475e71"));
		AlteredImagePanel.setPreferredSize(new Dimension(50, PlateImagesGUIHeight));
		JLabel AlteredImagePanelLabel = new JLabel("Altered Plate Image");
		AlteredImagePanelLabel.setFont(new java.awt.Font("Century Schoolbook L", Font.BOLD, 30));
		AlteredImagePanelLabel.setForeground(Color.WHITE);
		AlteredImagePanel.add(AlteredImagePanelLabel);
		totalGUI.add(AlteredImagePanel);

		JPanel AlteredImagePanelWithImage = new JPanel();
		AlteredImagePanelWithImage.setBackground(Color.decode("#475e71"));
		AlteredImagePanelWithImage.setPreferredSize(new Dimension(50, PlateImagesImageGUIHeight));

		Image AlteredIDPlateImage = null;
		try {
			AlteredIDPlateImage = ImageIO.read(new File("WaitingWholePhoto.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Image Altereddimgx = AlteredIDPlateImage.getScaledInstance(250, 70, Image.SCALE_SMOOTH);
		ImageIcon AlteredImageIcon = new ImageIcon();
		AlteredImageIcon.setImage(Altereddimgx);

		AlteredIdentifiedImagePanelPhotoLabel.setIcon(AlteredImageIcon);
		AlteredImagePanelWithImage.add(AlteredIdentifiedImagePanelPhotoLabel);

		totalGUI.add(AlteredImagePanelWithImage);

		/**
		 * FILLER GUI
		 */

		Dimension minSize = new Dimension(5, 18);
		Dimension prefSize = new Dimension(5, 18);
		Dimension maxSize = new Dimension(Short.MAX_VALUE, 25);
		totalGUI.add(new Box.Filler(minSize, prefSize, maxSize));

		/**
		 * PRINTED PLATE GUI
		 */

		JPanel bluePanel = new JPanel();
		bluePanel.setBackground(Color.blue);
		bluePanel.setPreferredSize(new Dimension(100, 80));
		CarReg.setFont(new java.awt.Font("Century Schoolbook L", Font.BOLD, 50));
		CarReg.setForeground(Color.WHITE);
		bluePanel.add(CarReg);
		totalGUI.add(bluePanel);

		totalGUI.add(new Box.Filler(minSize, prefSize, maxSize));

		/**
		 * Car Status GUI
		 */

		JPanel CarStatus = new JPanel();
		CarStatus.setBackground(Color.white);
		CarStatus.setPreferredSize(new Dimension(100, 50));
		CarStatusText.setFont(new java.awt.Font("Century Schoolbook L", Font.BOLD, 30));
		CarStatusText.setForeground(Color.blue);
		CarStatus.add(CarStatusText);
		totalGUI.add(CarStatus);

//        totalGUI.add(new Box.Filler(minSize, prefSize, maxSize));

		/**
		 * Panel to show the car owner
		 */
		
		int StatusHeight = 19;
		int statusFontHeight = 15;

		JPanel CarStatusInformationName = new JPanel();
		CarStatusInformationName.setBackground(Color.white);
		CarStatusInformationName.setPreferredSize(new Dimension(100, StatusHeight));
		CarOwner.setFont(new java.awt.Font("Century Schoolbook L", Font.BOLD, statusFontHeight));
		CarOwner.setForeground(Color.blue);
		CarStatusInformationName.add(CarOwner);
		totalGUI.add(CarStatusInformationName);

		/**
		 * Panel to show car owner location
		 */
		JPanel CarStatusInformationLocation = new JPanel();
		CarStatusInformationLocation.setBackground(Color.white);
		CarStatusInformationLocation.setPreferredSize(new Dimension(100, StatusHeight));
		CarLocation.setFont(new java.awt.Font("Century Schoolbook L", Font.BOLD, statusFontHeight));
		CarLocation.setForeground(Color.blue);
		CarStatusInformationLocation.add(CarLocation);
		totalGUI.add(CarStatusInformationLocation);

		/**
		 * Panel to show car Manufacturer Information
		 */
		JPanel CarStatusInformationManufacturer = new JPanel();
		CarStatusInformationManufacturer.setBackground(Color.white);
		CarStatusInformationManufacturer.setPreferredSize(new Dimension(100, StatusHeight));
		ManufacturerAndYear.setFont(new java.awt.Font("Century Schoolbook L", Font.BOLD, statusFontHeight));
		ManufacturerAndYear.setForeground(Color.blue);
		CarStatusInformationManufacturer.add(ManufacturerAndYear);
		totalGUI.add(CarStatusInformationManufacturer);

		/**
		 * Panel to show Insurance Purchase Date
		 */
		JPanel CarStatusInformationInsurance = new JPanel();
		CarStatusInformationInsurance.setBackground(Color.white);
		CarStatusInformationInsurance.setPreferredSize(new Dimension(100, (StatusHeight + 5)));
		InsurancePurchaseDate.setFont(new java.awt.Font("Century Schoolbook L", Font.BOLD, statusFontHeight));
		InsurancePurchaseDate.setForeground(Color.blue);
		CarStatusInformationInsurance.add(InsurancePurchaseDate);
		totalGUI.add(CarStatusInformationInsurance);

		totalGUI.add(new Box.Filler(minSize, prefSize, maxSize));

		/**
		 * Updates Panel
		 */

		ProcessingPanelLiveUpdates.setBackground(Color.gray);
		ProcessingPanelLiveUpdates.setPreferredSize(new Dimension(100, 43));
		textField.setFont(new java.awt.Font("Century Schoolbook L", Font.BOLD, 25));
		textField.setForeground(Color.black);
		ProcessingPanelLiveUpdates.add(textField);
		totalGUI.add(ProcessingPanelLiveUpdates);

		/**
		 * Panel to show time to process
		 */

		JPanel ProcessingPanelLiveTime = new JPanel();
		ProcessingPanelLiveTime.setBackground(Color.DARK_GRAY);
		ProcessingPanelLiveTime.setPreferredSize(new Dimension(100, 20));
		Time.setFont(new java.awt.Font("Century Schoolbook L", Font.BOLD, 10));
		Time.setForeground(Color.white);
		ProcessingPanelLiveTime.add(Time);
		totalGUI.add(ProcessingPanelLiveTime);

		/**
		 * Panel to show number of plates found
		 */
		JPanel ProcessingPanelLiveTimeAndNumberOfPhotos = new JPanel();
		ProcessingPanelLiveTimeAndNumberOfPhotos.setBackground(Color.DARK_GRAY);
		ProcessingPanelLiveTimeAndNumberOfPhotos.setPreferredSize(new Dimension(100, 25));
		NoOfPlateCaptures.setFont(new java.awt.Font("Century Schoolbook L", Font.BOLD, 10));
		NoOfPlateCaptures.setForeground(Color.white);
		ProcessingPanelLiveTimeAndNumberOfPhotos.add(NoOfPlateCaptures);
		totalGUI.add(ProcessingPanelLiveTimeAndNumberOfPhotos);

		/**
		 * Creates the window using the panels from above
		 */

		JSplitPane sl = new JSplitPane(SwingConstants.VERTICAL, totalGUI, panel);
		sl.setOrientation(SwingConstants.VERTICAL);

		JFrame window = new JFrame("ANPR Live Camera Feed");
		window.add(sl);
		window.setResizable(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setVisible(true);

	}

	/**
	 * Takes a photo with the webcam
	 * 
	 * @param PhotoName
	 * @return
	 * @throws IOException
	 */
	public String takePhoto(String PhotoName) throws IOException {

		BufferedImage image = webcam.getImage();
		ImageIO.write(image, "PNG", new File(Home.getDirectory() + "Camera Photos\\" + PhotoName + ".png"));

		return PhotoName;

	}

	/**
	 * Closes the camera
	 */
	public void CloseCamera() {
		webcam.close();
	}

	/**
	 * adds the number plate to the GUI, you will need to call this in the other
	 * classes
	 * 
	 * @param chosenPlate
	 */
	public void addNumberPlate(String chosenPlate) {
		CarReg.setText(chosenPlate);

	}

	int PhotosSincePlateFound = 3;

	/**
	 * Adds processing information to update the user
	 * 
	 * @param ProcessingTextFromSystem
	 */
	public void addProcessingText(String ProcessingTextFromSystem) {

		if (ProcessingTextFromSystem == "No Plate Found") {

			if (PhotosSincePlateFound > 2) {

				ProcessingPanelLiveUpdates.setBackground(Color.gray);
				ProcessingPanelLiveUpdates.setPreferredSize(new Dimension(100, 50));
				textField.setFont(new java.awt.Font("Century Schoolbook L", Font.DEFAULTSIZE, 30));
				textField.setForeground(Color.white);
				textField.setText(ProcessingTextFromSystem);
				
			}
			
			PhotosSincePlateFound++;
			
		} else {
			ProcessingPanelLiveUpdates.setBackground(Color.yellow);
			ProcessingPanelLiveUpdates.setPreferredSize(new Dimension(100, 50));
			textField.setFont(new java.awt.Font("Century Schoolbook L", Font.BOLD, 30));
			textField.setForeground(Color.black);
			textField.setText(ProcessingTextFromSystem);
			PhotosSincePlateFound = 0;
		}

		System.out.println(" ");
		System.out.println(ProcessingTextFromSystem);
		System.out.println(" ");
		
	}

	boolean plateDataCalled = false;

	public void addAllNumberPlateData(String chosenPlate, String PhotoNumber, String NumberOfPhotosWithPlate,
			String PTime, String Owner, String Location, String Manufacturer, String Year, String Insurance,
			String Status) {
		CarReg.setText(chosenPlate);
		NoOfPlateCaptures.setText("Photo with Plate: " + NumberOfPhotosWithPlate + "(Photos: " + PhotoNumber + ")");
		Time.setText(PTime + " ms");
		CarOwner.setText(Owner);
		CarLocation.setText(Location);
		ManufacturerAndYear.setText(Manufacturer + " (" + Year + ") ");
		InsurancePurchaseDate.setText(Insurance);
		CarStatusText.setText(Status);

		if (plateDataCalled == false) {

			String[] DataToWriteFirst = { "Plate Capture Number", "Photo Number", "License Plate",
					"Processing Time (ms)", "Car Owner", "Location", "Manufacturer", "Manufacturer Year", "Insurance",
					"Car Status" };

			WriteToDatabase(DataToWriteFirst);
			CloseWriter();

			String[] DataToWrite = { NumberOfPhotosWithPlate, PhotoNumber, chosenPlate, PTime, Owner, Location,
					Manufacturer, Year, Insurance, Status };

			WriteToDatabase(DataToWrite);
			CloseWriter();

		} else {
			String[] DataToWrite = { NumberOfPhotosWithPlate, PhotoNumber, chosenPlate, PTime, Owner, Location,
					Manufacturer, Year, Insurance, Status };

			WriteToDatabase(DataToWrite);
			CloseWriter();
		}

		plateDataCalled = true;

	}

	/**
	 * Shows the number of plates found
	 * 
	 * @param PlateCaptures
	 */
	public void addPlateCounter(String PlateCaptures) {
		NoOfPlateCaptures.setText(PlateCaptures);
	}

	/**
	 * Shows the time to process the plate
	 * 
	 * @param PTime
	 */
	public void addProcessingTime(String PTime) {
		Time.setText(PTime);
	}

	/**
	 * Updates the main image
	 * 
	 * @param ImageFileAndLocation
	 */
	public void updateWholeImage(String ImageFileAndLocation) {
		Image img = null;
		try {
			img = ImageIO.read(new File(ImageFileAndLocation));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Image dimg = img.getScaledInstance(600, 350, Image.SCALE_SMOOTH);
		ImageIcon x = new ImageIcon();
		x.setImage(dimg);
		WholeImageJLabel.setIcon(x);
	}

	/**
	 * Updates the plate photo taken
	 * 
	 * @param ImageFileAndLocation
	 */
	public void updateIdentifiedImagePhoto(String ImageFileAndLocation) {

		Image img = null;
		try {
			img = ImageIO.read(new File(ImageFileAndLocation));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Image dimg = img.getScaledInstance(250, 70, Image.SCALE_SMOOTH);
		ImageIcon x = new ImageIcon();
		x.setImage(dimg);
		IdentifiedImagePanelPhotoLabel.setIcon(x);

	}

	/**
	 * Update the altered photo taken
	 * 
	 * @param ImageFileAndLocation
	 */
	public void updateAlteredIdentifiedImagePhoto(String ImageFileAndLocation) {

		Image img = null;
		try {
			img = ImageIO.read(new File(ImageFileAndLocation));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Image dimg = img.getScaledInstance(250, 70, Image.SCALE_SMOOTH);
		ImageIcon x = new ImageIcon();
		x.setImage(dimg);
		AlteredIdentifiedImagePanelPhotoLabel.setIcon(x);

	}

	/**
	 * Writes the ANPR information to the CSV
	 * 
	 * @param DataToWrite
	 * @return
	 */
	public static boolean WriteToDatabase(String[] DataToWrite) {
		createWriter(Home.getDirectory() + DatabaseName);
		writer.writeNext(DataToWrite);
		return true;

	}

	/**
	 * Creates a CSV Writer
	 * 
	 * @param filePath
	 * @return
	 */
	public static CSVWriter createWriter(String filePath) {

		File file = new File(filePath);

		FileWriter outputfile = null;

		try {
			outputfile = new FileWriter(file, true);
			writer = new CSVWriter(outputfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return writer;
	}

	/**
	 * Closes the Writer
	 */
	public static void CloseWriter() {
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
