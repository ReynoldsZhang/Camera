
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Home {

	/**
	 * used to count the number of Photos taken
	 */
	
	public static int PhotoNumber = 1;
	public static String PhotoTime;
	public static boolean Test = false;

	/**
	 * You should know what a main method is lol
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("**********************************************************************");
		System.out.println("***************** STARTING ANPR STUDENT VERSION V1.0 *****************");
		System.out.println("**********************************************************************");
		System.out.println(" ");
		
		/**
		 * If it's a Test, the RunTest will run, if you want to do a Live Test then set the Boolean
		 * Test to False (above)
		 */
		
		if (Test) {

			RunTest();

		} else {

			RunLive(500);

		}

	}

	public static void RunTest() {

		/**
		 * This is to test your Find_Plate_Number_Algorithm, using the photos it tests
		 * the system and not from the camera
		 */

		/**
		 * Gets the photos in a directory
		 */

		File directoryPath = new File(Home.getTestDirectory());

		/**
		 * Creates a String array to hold the Photo Names
		 */
		
		String[] ListOfPhotoNames = directoryPath.list();

		/**
		 * Creates a new camera object but doesn't load the window
		 */
		
		Camera_System Cameras = new Camera_System(1);

		/**
		 * For loop for each photo in the directory
		 */

		for (String TestInputFile : ListOfPhotoNames) {

			try {
				
				/**
				 * Gets the Number Plate read from the Photo
				 */
				
				String TestResult = RunFindCorrectPlateAlgorithm(TestInputFile, Cameras);
				
				/**
				 * Prints the Number Plate out
				 */
				
				System.out.println("Plate Read: " + TestResult);

			} catch (Exception e) {
				System.out.println(e);
			}

		}

	}

	/**
	 * Runs a live test constantly taking photos until it reaches the limit
	 * 
	 * @param NumberOfPhotos
	 */
	public static void RunLive(int NumberOfPhotos) {

		/**
		 * Creates a camera object which loads the GUI and CSV file to save your results
		 */
		Camera_System Cameras = new Camera_System();

		/**
		 * Do while loop will run while PhotoNumber is lower than NumberOfPhotos
		 */
		do {

			String InputFileName = null;

			try {

				System.out.println(">>>>>>>> Geting Photo " + (PhotoNumber) + " <<<<<<<<<<");

				/**
				 * Camera takes a photo
				 */
				
				InputFileName = Cameras.takePhoto(getPhotoTime(true)) + ".png";

				/**
				 * Starts a Timer to time how long it takes to process a plate
				 */
				
				Instant before = Instant.now();

				/**
				 * Using the algorithm a plate number is returned from the photo taken
				 */
				
				String ChosenPlate = RunFindCorrectPlateAlgorithm(InputFileName, Cameras);

				/**
				 * Ends the Timer
				 */
				Instant after = Instant.now();

				/**
				 * Calculates time to process the plate 
				 */
				
				long Secs = (Duration.between(before, after).toMillis() / 1000) % 60;
				String Seconds = Secs + " seconds";
				String ProcessTime = Duration.between(before, after).toMillis() + "";

				/**
				 * Adds the time to the GUI
				 */
				
				Cameras.addProcessingTime(ProcessTime + " (" + Seconds + ")");

				/**
				 * If a plate is returned then if is true
				 */
				
				if (ChosenPlate != null) {
					
					System.out.println("Plate Found: " + ChosenPlate);

					/**
					 * Here you need to update the GUI with the car information, using the database
					 * You update the GUI using the method addAllNumberPlateData
					 * Currently it's just updating the Plate in the GUI using addNumberPlate
					 */
					
					Cameras.addNumberPlate(ChosenPlate);

					Cameras.addProcessingText("Completed Processing");
					
					Cameras.addAllNumberPlateData(ChosenPlate, PhotoNumber + "", Find_Plate.NumberOfPhotosWithPlate + "",
							ProcessTime,"Owner", "Location", "Manufacturer", "2006", "Insurance",
							"Status");
					
				}

				PhotoNumber++;

			} catch (IOException e) {
				e.printStackTrace();

				System.out.println(">>>>>>>> Unable to Take Photo <<<<<<<<<<");
				System.out.println("          ");
			}

		} while (PhotoNumber <= NumberOfPhotos);

		/**
		 * When we are done we close the camera
		 */
		
		Cameras.CloseCamera();

	}

	/**
	 * Using the photo taken this method then tries to find the plate in the photo
	 * 
	 * @param InputFileName
	 * @param Cameras
	 * @param PhotoNumber
	 * @return
	 */
	public static String RunFindCorrectPlateAlgorithm(String InputFileName, Camera_System Cameras) {

		/**
		 * Checks that there's a File name
		 */
		
		if (InputFileName != null) {

			/**
			 * The ArrayOfPlateResults is an array of arrays, but normally there will be 1 array
			 * in it
			 * 
			 * NOTE: Sometimes the plate detection will find multiple plates in one picture!
			 * 
			 * Each plate read will get an array of length 4
			 * 
			 * [0] = Chinese Read of the unedited photo 
			 * [1] = English Read of the unedited photo 
			 * [2] = Chinese Read of the edited photo 
			 * [4] = English Read of the edited photo
			 * 
			 * If it's not null then you want to process it and using your algorithm find the best plate to return
			 * 
			 */
			
			ArrayList<String[]> ArrayOfPlateResults = Find_Plate.FindCarPlate(InputFileName, Cameras, Test);

			if (ArrayOfPlateResults != null) {

				Cameras.addProcessingText("↻ Running Algorithm");
				
				/**
				 * Using your algorithm find the best plate to return
				 */

				return Find_Plate_Number_Algorithm.getPlateNumber(ArrayOfPlateResults, Cameras);

			}

		}

		return null;

	}

	/**
	 * Used to get the directory where all the photos and files are
	 * 
	 * @return
	 */
	
	public static String getDirectory() {
		return "Photos\\";
	}
	
	/**
	 * Used to get all the photos in your test directory
	 * @return
	 */
	
	public static String getTestDirectory() {
		return "Photos\\Test\\";
	}

	/**
	 * This is to standardise all the photo names 
	 * - e.g. so that all photos contain the same Time stamp making it easier to check them
	 * 
	 * @param newPhoto
	 * @return
	 */
	public static String getPhotoTime(boolean NeedPhotoNumber) {

		/**
		 * Gets the current Time and Date
		 */
		
		LocalDateTime now = LocalDateTime.now();

		/**
		 * Uses now to make the Timestamp for the photo name
		 */
		
		if (NeedPhotoNumber) {

			PhotoTime = " " + PhotoNumber + " @ " + now.toString();

		} else {

			PhotoTime = " ANPR Data @ " + now.toString();

		}

		/**
		 * Removes : because it doesn't work with windows file management system
		 * Removes T to make the name simpler
		 */
		
		PhotoTime = PhotoTime.replace(":", "-");
		PhotoTime = PhotoTime.replace("T", " ");
		
		/**
		 * Reduces the number of milliseconds in the time stamp
		 */
		
		PhotoTime = PhotoTime.substring(0, PhotoTime.length() - 7);

		return PhotoTime;
	}


}
