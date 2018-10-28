/**   
* @Title: NeighbourList.java
* @Package com.sensorweb.evaluation
* @Description: TODO
* @author A18ccms A18ccms_gmail_com   
* @date 15 Aug 2013 13:24:13
* @version V1.0   
*/
package uk.ac.surrey.ccsr.ssr.path;

import java.util.ArrayList;



/**
 * @ClassName: NeighbourList
 * @Description: TODO
 * @author A18ccms a18ccms_gmail_com
 * @date 15 Aug 2013 13:24:13
 * 
 */
public class NeighbourList {

	public NeighbourList(){
		neighborList = new ArrayList<NeighbourEntry>();
		neighborList.clear();
	}
	
	
	/**
	* @Title: addNeigborEntry
	* @Description: Add, or update a neighbour entry
	* @param: @param address
	* @param: @param linkQuality
	* @param: @param hop 
	* @return: void 
	* @throws
	*/
	public void addNeigborEntry(int address, int linkQuality, int hop){
	
		boolean found=false;
		int i=0;
		for(i=0; i<neighborList.size(); i++)
		{
			NeighbourEntry ne = neighborList.get(i);
			if(ne.getNeighborAddress() == address)
			{
				found=true;
				break;
			}
		}
		
		if(!found){
			NeighbourEntry ne = new NeighbourEntry();
			ne.setUpNeighborAddress(address);
			ne.setUpLinkQuality(linkQuality);
			//ne.setUpHop(hop);
			neighborList.add(ne);
		}else{
			NeighbourEntry ne = neighborList.get(i);
			ne.setUpLinkQuality(linkQuality);
			//ne.setUpHop(hop);
		}
	}
	
	/**
	* @Title: getNeighborListSize
	* @Description: TODO
	* @param: @return 
	* @return: int 
	* @throws
	*/
	public int getNeighborListSize()
	{
		return this.neighborList.size();
	}
	
	
	public void updateNeighbourLinkQuality(int neighbourAddress, int linkQuality)
	{
		boolean found=false;
		for(int i=0; i<neighborList.size();i++)
		{
			NeighbourEntry ne = neighborList.get(i);
			if(ne.getNeighborAddress() == neighbourAddress)
			{
				ne.setUpLinkQuality(linkQuality);
				found=true;
				break;
			}
		}
		
		if(!found)
		{
			NeighbourEntry ne = new NeighbourEntry();
			ne.setUpNeighborAddress(neighbourAddress);
			ne.setUpLinkQuality(linkQuality);
			neighborList.add(ne);
		}
	}
	/**
	* @Title: getNeighborEntry
	* @Description: TODO
	* @param: @param index
	* @param: @return 
	* @return: NeighborEntry 
	* @throws
	*/
	protected NeighbourEntry getNeighborEntry(int index)
	{
		NeighbourEntry ne = null;
		
		if(index < neighborList.size())ne = neighborList.get(index);
		
		return ne;
	}
	
	private ArrayList<NeighbourEntry> neighborList;

}
