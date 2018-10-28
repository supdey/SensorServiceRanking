/**   
* @Title: RoutingTable.java
* @Package com.sensorweb.evaluation
* @Description: TODO
* @author A18ccms A18ccms_gmail_com   
* @date 15 Aug 2013 14:44:55
* @version V1.0   
*/
package uk.ac.surrey.ccsr.ssr.path;

import java.util.ArrayList;



/**
 * @ClassName: RoutingTable
 * @Description: TODO
 * @author A18ccms a18ccms_gmail_com
 * @date 15 Aug 2013 14:44:55
 * 
 */
public class RoutingTable {

	public RoutingTable(int id){
		routerID = id;
	}
	
	public void createRouteEntry(int nexthop, int dest, double linkquality, int hop)
	{
		RouteEntry route = new RouteEntry();
		
		boolean success = route.setRouteEntry(nexthop, dest,(double)linkquality, hop);
		
		if(success)routingTable.add(route);
	}
	
	public int getRoutingTableSize()
	{
		return routingTable.size();
	}
	
	public RouteEntry getRouteEntry(int index)
	{
		RouteEntry rtEntry=null;
		if(index >=0 && index <routingTable.size())
			rtEntry = routingTable.get(index);
		return rtEntry;
	}
	
	/**
	* @Title: createSingleShortestPath
	* @Description: TODO
	* @param: @param nextHop
	* @param: @param dest
	* @param: @param hop
	* @param: @param multipathIndex
	* @param: @param existingWeight
	* @param: @param addingWeight 
	* @return: void 
	* @throws
	*/
	public void createSingleShortestPath(int nextHop, int dest, int hop, double existingLinkQuality, double addingLinkQuality)
	{
		//1. Explore the local routing table to determine if the specified dest has been discovered
		for(int i=0; i<routingTable.size(); i++)
		{
			RouteEntry rty = routingTable.get(i);
			if(rty.getDest()==dest)
			{
				//If the dest already exists in the local routing table, check if the corresponding weight is less than the newly discovered route
				if(rty.getSummaryOfLinkQuality()< (existingLinkQuality+addingLinkQuality))
				{
					//If it is, no need to change the route
					return;
				}else{
					//Since it is for single path routing table, there should be only one route detected
					//We can stop exploring
					break;
				}
			}
		}
		
		//If program can reach this step, it means 1) the specified dest has not been discovered, or 2)the existing route entry requires more weight, then it can be replaced
		double linkQuality = existingLinkQuality+addingLinkQuality;
		recordRouteEntry(dest,nextHop,hop,linkQuality);
	}
	
	/**
	* @Title: recordRouteEntry
	* @Description: TODO
	* @param: @param dest
	* @param: @param nextHop
	* @param: @param hopNumber
	* @param: @param weight
	* @param: @param multipathIndex 
	* @return: void 
	* @throws
	*/
	private void recordRouteEntry(int dest, int nextHop, int hopNumber, double linkQuality)
	{
		int i=0;
		boolean found=false;

		for(i=0; i<routingTable.size();i++)
		{
			if(routingTable.get(i).getDest()==dest)
			{
				found=true;
				//Overwrite existing entry
				routingTable.get(i).setRouteEntry(nextHop, dest, linkQuality,hopNumber);
						                                                    
				break;
			}
		}
		
		if(!found)
		{
		 //Add a new entry
			RouteEntry re = new RouteEntry();
			re.setRouteEntry(nextHop, dest, linkQuality,hopNumber);
			//System.out.println("RouterID "+routerID+" next hop "+nextHop+" dest "+dest+" hopNumber "+hopNumber);
			routingTable.add(re);
		}
	}
	
	ArrayList<RouteEntry> routingTable = new ArrayList<RouteEntry>();
	protected int routerID=-1;
}
