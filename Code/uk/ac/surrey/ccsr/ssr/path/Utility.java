/**   
* @Title: Utility.java
* @Package com.sensorweb.evaluation
* @Description: TODO
* @author A18ccms A18ccms_gmail_com   
* @date 14 Aug 2013 16:48:24
* @version V1.0   
*/
package uk.ac.surrey.ccsr.ssr.path;

/**
 * @ClassName: Utility
 * @Description: TODO
 * @author A18ccms a18ccms_gmail_com
 * @date 14 Aug 2013 16:48:24
 * 
 */
public class Utility {

	/**
	* @Title: isPositiveInteger
	* @Description: Tell if the input parameter (type int) is positive or negative
	* @param: @param number
	* @param: @return 
	* @return: boolean 
	* 					true:   positive int value
	*                  false:  negative int value
	* @throws
	*/
	public static boolean isPositiveInteger(int number)
	{
		boolean isPositive=false;
		
		if(number>>31!=0)isPositive=false;
		else isPositive=true;
		return isPositive;
	}
}
