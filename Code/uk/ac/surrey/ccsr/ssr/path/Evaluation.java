/**   
* @Title: Evaluation.java
* @Package com.sensorweb.evaluation
* @Description: TODO
* @author A18ccms A18ccms_gmail_com   
* @date 14 Aug 2013 16:19:02
* @version V1.0   
*/
package uk.ac.surrey.ccsr.ssr.path;

import java.io.IOException;

/**
 * @ClassName: Evaluation
 * @Description: TODO
 * @author A18ccms a18ccms_gmail_com
 * @date 14 Aug 2013 16:19:02
 * 
 */
public class Evaluation {

	/**
	 * @throws IOException 
	 * @Title: main
	 * @Description: TODO
	 * @param: @param args 
	 * @return: void 
	 * @throws
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
     AnalysisNodeTopology ant = new AnalysisNodeTopology("./data/configuration1/Matrix.txt","./data/configuration1/LinkQuality.txt","./data/configuration1/Energy.txt");
	 SensorNodeList snl = ant.EstablishTopology();
	 //The format for parameters is: src node, destination node, and the sensor node list.
	 BestRouteResult bstRoute = new BestRouteResult(0,59,snl);
	 bstRoute.calculateBestRoute();
	}

}
