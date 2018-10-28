package uk.ac.surrey.ccsr.ssr.path;

public class BestRouteResult {

	public BestRouteResult(int src, int dest, SensorNodeList sensorNodeList)
	{
		this.srcAddress = src;
		this.destAddress=dest;
		this.snl = sensorNodeList;
	}
	
	public void calculateBestRoute()
	{
		//Before the calculation, clear up the relevant parameters
		nodeOnRoute=null;
		linkQuality=0.0;
		
		if(this.srcAddress!=-1 && this.destAddress!=-1 && this.snl!=null)
		{
			//1. Locate the src node from the sensor node list
			SensorNode srcNode = snl.getNodeByAddress(this.srcAddress);
			SensorNode destNode = snl.getNodeByAddress(this.destAddress);
			if(srcNode==null || destNode==null)
			{
				//System.out.println("Source node or destination node is not found");
				//System.exit(-1);
				noRouteResult();
			}else{
				//2. Determine if the destination node is reachable from the source node
				RoutingTable rt = srcNode.getRoutingTable();
				boolean found=false;
				RouteEntry bestRouteEntry=null;
				for(int i=0; i<rt.getRoutingTableSize();i++)
				{
					RouteEntry re = rt.getRouteEntry(i);
					if(re.getDest() == this.destAddress)
					{
						bestRouteEntry = re;
						found=true;
						break;
					}
				}
				
				if(!found)
				{
					//System.out.println("No route from the source node to the destination node is found!");
					//System.exit(-1);
					noRouteResult();
				}else{
				
				//3.Record the nodes on the route starting from the source node to the destination node
				linkQuality = bestRouteEntry.getSummaryOfLinkQuality();
				int hop = bestRouteEntry.getHop();
				nodeOnRoute = new int[hop+1];
				nodeOnRoute[0] = this.srcAddress;
				
				SensorNode tmpSrcSensorNode = snl.getNodeByAddress(this.srcAddress);
				for(int m=0; m<hop;m++)
				{
					
					for(int i=0; i<tmpSrcSensorNode.getRoutingTable().getRoutingTableSize();i++)
					{
						RouteEntry re =tmpSrcSensorNode.getRoutingTable().getRouteEntry(i);
						if(re.getDest() == this.destAddress)
						{
							tmpSrcSensorNode = snl.getNodeByAddress(re.getNextHop());
							nodeOnRoute[m+1]=tmpSrcSensorNode.getNodeID();
							break;
						}
					}
				}
				
				}
			}
		}else{
			//System.out.println("Parameters for calculating the best path have not been satisfied" );
			//System.exit(-1);
			noRouteResult();
		}
		
		System.out.println("Result: linkquality "+this.linkQuality);
		for(int i=0; i<nodeOnRoute.length;i++)
		{
			System.out.print("  "+this.nodeOnRoute[i]);
		}
		
	}
	
	public int[] getNodesOnRoute()
	{
		if(this.nodeOnRoute==null) return null;
		return this.nodeOnRoute;
	}
	
	public double getLinkQualitySummary()
	{
	
		return this.linkQuality;
	}
	
	private void noRouteResult()
	{
		nodeOnRoute = new int[1];
		nodeOnRoute[0]=0;
	}
	SensorNodeList snl=null;
	int[] nodeOnRoute=null;
	int srcAddress=-1;
	int destAddress=-1;
	double linkQuality=0.0;
}
