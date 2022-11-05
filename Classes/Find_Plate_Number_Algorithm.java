

import java.util.ArrayList;

/**
 * This is where you implement your algorithm to find the correct Plate to return
 * You will need to use the database 
 * @author armen
 *
 */

public class Find_Plate_Number_Algorithm {

	public static String getPlateNumber (ArrayList<String[]> ArrayOfPlateResults, Camera_System Cameras) {
		
		if (ArrayOfPlateResults.size() > 0) {
			
			/**
			 * Returns the 1st element in the 1st array in ArrayOfPlateResults
			 * This so you can see your program working
			 */
			
			String[] PlateArray = ArrayOfPlateResults.get(0);
			
			return PlateArray[0];
		}
		
		return null;
		
	}

}
