/**   
* @Title: RouteEntry.java
* @Package com.sensorweb.evaluation
* @Description: TODO
* @author A18ccms A18ccms_gmail_com   
* @date 15 Aug 2013 14:37:46
* @version V1.0   
*/
package uk.ac.surrey.ccsr.ssr.path;

/**
 * @ClassName: RouteEntry
 * @Description: TODO
 * @author A18ccms a18ccms_gmail_com
 * @date 15 Aug 2013 14:37:46
 * 
 */
public class RouteEntry {
	public  RouteEntry(){
		this.nexthopAdd =-1;
		this.destAdd       = -1;
		this.hopNum      = -1;
	
	}
	
	

	/**
	* @Title: setRouteEntry
	* @Description: Set a route entry with the next hop address and the corresponding destination
	* @param: @param nextHop : the next hop address
	* @param: @param dest:          the destination address
	* @param: @param hop:          the number of hop to the destination
	* @return: boolean :                   if the setting is successful
	* @throws
	*/
	protected  boolean  setRouteEntry(int nextHop, int dest, double linkquality, int hop)
	 {
		 boolean bSuccess=false;
		 
	    
	    	 nexthopAdd = nextHop;
	    	 destAdd = dest;
	    	 sumOfLinkQuality = linkquality;
	    	 hopNum = hop;
	     //As long as the values of nexthop and destination are valid, the route entry can be thought as valid,
	     //Otherwise, it is invalid
	     if(nexthopAdd!=-1 && destAdd!=-1)
		 bSuccess=true;
	     
	    
		 return bSuccess;
	 }
	
	/**
	* @Title: getNextHop
	* @Description: Return the field of nextHop for this route entry
	* @param: @return 
	* @return: the address of next hop
	* @throws
	*/
	public int getNextHop(){
		
		return nexthopAdd;
	}
	
	/**
	* @Title: getDest
	* @Description: Return the field of destination for this route entry
	* @param: @return 
	* @return: the address of destination 
	* @throws
	*/
	public int getDest(){
		
		return destAdd;
	}
	

	/**
	* @Title: getHop
	* @Description: Return the hop number for this route entry
	* @param: @return 
	* @return:  the number of hop for this route entry 
	* @throws
	*/
	public int getHop(){
		return hopNum;
	}
	
	public void updateLinkQualitySummary(double newLinkQuality)
	{
		sumOfLinkQuality= sumOfLinkQuality+newLinkQuality;
	}
	
	public double getSummaryOfLinkQuality()
	{
		return this.sumOfLinkQuality;
	}

	

	//Only the local class can access it.
	private int nexthopAdd=-1;
	private int destAdd=-1;
	private int hopNum=-1;
    private double sumOfLinkQuality=0.0;
}
