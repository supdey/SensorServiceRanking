/**   
* @Title: NeighbourEntry.java
* @Package com.sensorweb.evaluation
* @Description: TODO
* @author A18ccms A18ccms_gmail_com   
* @date 15 Aug 2013 13:21:12
* @version V1.0   
*/
package uk.ac.surrey.ccsr.ssr.path;



/**
 * @ClassName: NeighbourEntry
 * @Description: TODO
 * @author A18ccms a18ccms_gmail_com
 * @date 15 Aug 2013 13:21:12
 * 
 */

public class NeighbourEntry {

	public  NeighbourEntry(){
		this.neighborAddress =-1;
		
		this.linkQuality     = -1;
	}
	
	/**
	* @Title: setUpNeighborAddress
	* @Description: Set up neighbour address
	* @param: @param address 
	* @return: void 
	* @throws
	*/
	public void setUpNeighborAddress(int address)
	{
		if(Utility.isPositiveInteger(address))
		{
			this.neighborAddress = address;
		}else{
			System.out.println("Invalid neighbor address, program exist...");
			System.exit(-1);
		}
	}
	
	/**
	* @Title: getNeighborAddress
	* @Description: Return neighbour address
	* @param: @return 
	* @return: int 
	* @throws
	*/
	public int getNeighborAddress()
	{
		return this.neighborAddress;
	}
	
	/**
	* @Title: setUpHop
	* @Description: Set up hop number for reaching the destination
	* @param: @param hopNumber 
	* @return: void 
	* @throws
	*/
	/*public void setUpHop(int hopNumber)
	{
		if(Utility.isPositiveInteger(hopNumber))
		{
			this.hop = hopNumber;
		}else{
			System.out.println("Invalid hop for neighbor, program exist...");
			System.exit(-1);
		}
	}*/
	
	/**
	* @Title: getHop
	* @Description: TODO
	* @param: @return 
	* @return: int 
	* @throws
	*/
	/*public int getHop()
	{
		return this.hop;
	}*/
	
	/**
	* @Title: setUpLinkQuality
	* @Description: TODO
	* @param: @param link 
	* @return: void 
	* @throws
	*/
	public void setUpLinkQuality(int link)
	{
		if(Utility.isPositiveInteger(link))
		{
			this.linkQuality = link;
		}else{
			System.out.println("Invalid link qualit for neighbor, program exist...");
			System.exit(-1);
		}
	}
	
	/**
	* @Title: getLinkQuality
	* @Description: TODO
	* @param: @return 
	* @return: int 
	* @throws
	*/
	public int getLinkQuality()
	{
		return this.linkQuality;
	}
	
	private int neighborAddress;
	//private int hop;
	private int linkQuality;
}