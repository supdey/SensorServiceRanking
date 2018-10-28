/**   
* @Title: SensorNode.java
* @Package com.sensorweb.evaluation
* @Description: TODO
* @author A18ccms A18ccms_gmail_com   
* @date 14 Aug 2013 16:47:27
* @version V1.0   
*/
package uk.ac.surrey.ccsr.ssr.path;

/**
 * @ClassName: SensorNode
 * @Description: TODO
 * @author A18ccms a18ccms_gmail_com
 * @date 14 Aug 2013 16:47:27
 * 
 */


public class SensorNode {
	
	/**
	 * 
	* <p>Title: SensorNode</p>
	* <p>Description: Constructor of class SensorNode.</p>
	* @param id
	 */
	public SensorNode(int id){
		
	
	   
	   //Validate the input id. If it is a negative value, exit.
	   if(Utility.isPositiveInteger(id)){
	   this.sensorNodeID = id;
	   }else{
			System.out.println("Unrecognized sensor node id, program exist...");
			System.exit(-1);
	   }
	   this.rtTable        = new RoutingTable(id);
       this.neighbourList = new NeighbourList();
	}

	
	public void updateNeighbourList(int neighbourAddress)
	{
		neighbourList.addNeigborEntry(neighbourAddress, 0, 1);
	}
	
	public void updateNeighbourLinkQuality(int neighbourAddress, int linkQuality)
	{
		neighbourList.updateNeighbourLinkQuality(neighbourAddress,linkQuality);
	}
	/**
	* @Title: getNodeID
	* @Description: Return the sensor node id
	* @param: @return 
	* @return: int 
	* @throws
	*/
	public int getNodeID()
	{
		return this.sensorNodeID;
	}
	
    /**
   * @Title: getRoutingTable
   * @Description: TODO
   * @param: @return 
   * @return: RoutingTable 
   * @throws
   */
   public RoutingTable getRoutingTable()
    {
   	 return rtTable;
    }
	
   public NeighbourList getNeighbourList()
   {
	   return this.neighbourList;
   }
   /**
* @Title: setUpBatteryLevel
* @Description: A randomly generated battery level
* @param: @param battery 
* @return: void 
* @throws
*/
public void setUpBatteryLevel(int battery)
   {
	   if(Utility.isPositiveInteger(battery)){
		   this.batteryLevel = battery;
	   }else{
		   System.out.println("Unrecognized battery level, program exist...");
			System.exit(-1);
	   }
   }

/**
* @Title: getBatteryLevel
* @Description: Get the battery level of current node
* @param: @return 
* @return: int 
* @throws
*/
public int getBatteryLevel()
{
	return this.batteryLevel;
}
/*

protected void updateNeighborLinkQuality(int neighborAddress, int hop, int linkQuality)
{
	this.neighborList.addNeigborEntry(neighborAddress, linkQuality, hop);
}
*/
/**
* @Title: getLinkQuality
* @Description: TODO
* @param: @param neighborAddress
* @param: @param hopNumber
* @param: @return 
* @return: int 
* @throws
*/
/*public int getLinkQuality(int neighborAddress, int hopNumber)
{
	int linkQuality=SensorNodeParameter.INVALID_LINKQUALITY;
	int i=0;
	
	for(i=0; i<neighborList.getNeighborListSize();i++)
	{
		NeighborEntry ne = neighborList.getNeighborEntry(i);
		if(ne!=null){
			if(ne.getNeighborAddress() == neighborAddress && ne.getHop()==hopNumber)
			{
				linkQuality = ne.getLinkQuality();
				break;
			}
		}else{
			log.error("Invalid neighbor node, exit...");
			System.exit(-1);
		}
	}
return linkQuality;
}*/

protected void enableSensorNode(boolean enabled)
{
	this.sensorNodeEnabled = enabled;
}

public boolean isSensorNodeEnabled()
{
	return this.sensorNodeEnabled;
}
    //Local router id. This id can only be accessed through the current class. Any other class can
	//not modify it.
    private int sensorNodeID=-1;
    //Routing table containing the identified path based on shortest-path principle
    RoutingTable rtTable;  
    private int batteryLevel=0;
    private NeighbourList neighbourList;
	
	private boolean sensorNodeEnabled=true;
}
