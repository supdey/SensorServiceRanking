/**   
* @Title: AnalysisNodeTopology.java
* @Package com.sensorweb.evaluation
* @Description: TODO
* @author A18ccms A18ccms_gmail_com   
* @date 14 Aug 2013 16:21:24
* @version V1.0   
*/
package uk.ac.surrey.ccsr.ssr.path;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;



/**
 * @ClassName: AnalysisNodeTopology
 * @Description: TODO
 * @author A18ccms a18ccms_gmail_com
 * @date 14 Aug 2013 16:21:24
 * 
 */
public class AnalysisNodeTopology {

	public AnalysisNodeTopology(String _adjacentFileName, String _linkQualityFileName, String _energyFileName )
	{
		 adjacentFileName=_adjacentFileName; 
		 energyFileName =_energyFileName;
		 linkQualityFileName = _linkQualityFileName;
	}
	
	
	public SensorNodeList EstablishTopology() throws IOException
	{
		CreateNodeFromEvaluationFile();
		UpdateLinkQuality();
		calculateBestPath();
		printRoutingTable();
		return this.snl;
	}
	

	
	private void printRoutingTable()
	{
		for(int i=0; i<snl.getSensorNodeListSize();i++)
		{
			SensorNode sn = snl.getNodeByIndex(i);
	       if(sn.getNodeID()==0){
			System.out.println("Sensor id:  "+sn.getNodeID());
			for(int m=0; m<sn.getRoutingTable().getRoutingTableSize();m++)
			{
				RouteEntry re = sn.getRoutingTable().getRouteEntry(m);
				if(re.getHop()==1)
				System.out.println("Dest:  "+re.getDest()+"   NextHop:  "+re.getNextHop()+"  Hop:  "+re.getHop()+"  link quality: "+re.getSummaryOfLinkQuality());
				else System.out.println("Dest:  "+re.getDest()+"   NextHop:  "+re.getNextHop()+"  Hop:  "+re.getHop()+"  LinkQuality  "+re.getSummaryOfLinkQuality());
			}
			System.out.println("--------------------------------------------------------------");
	       }
		}
	}
	private void calculateBestPath()
	{
		//We will calculate out routing table for the src node
		setUpOneHopRoutingTable();
		processFurtherSinglePathRoutingInfo();
	}
	
	private void setUpOneHopRoutingTable()
	{
		//Explore the neighbour table to establish routing table within 1-hop range
		//Locate the address of src router
     
    
        	//Locate src router from caching router list
        	for(int i=0; i<snl.getSensorNodeListSize();i++)
        	{
        		SensorNode sn = snl.getNodeByIndex(i);
        		if(sn !=null)
        		{
        			//Copy the neighbour table to the routing table
        			NeighbourList nl = sn.getNeighbourList();
        			RoutingTable rt = sn.getRoutingTable();
        			//System.out.println("record  "+nt.getNeighborListSize());
        			for(int m=0; m<nl.getNeighborListSize();m++)
        			{
        				NeighbourEntry ne = nl.getNeighborEntry(m);
        				rt.createRouteEntry(ne.getNeighborAddress(), ne.getNeighborAddress(),(double) 100/ne.getLinkQuality(),1);
        				//System.out.println(ne.getLinkQuality()+"   "+(double)1/ne.getLinkQuality());
        			}
        			/*if(sn.getNodeID() == src)
            		{
            			
            			if(!duplicateOneHopChecking(sn,src,Integer.parseInt( info[1]),Integer.parseInt(info[1]),Integer.parseInt(info[3]),hop))
            			{
            				sn.getRoutingTable().createRouteEntry(info[1], info[1], "1", info[2], info[3], info[4], info[5], info[6],1);
            				
            				//Generate a random value representing the link quality from src node to its 1 hop neighbor
            				 Random random = new Random();
            				
            				 int linkquality = Math.abs(random.nextInt())%(SensorNodeParameter.LINK_QUALITY_UPPER_BOUND-SensorNodeParameter.LINK_QUALITY_LOW_BOUND+1)
            						                      +SensorNodeParameter.LINK_QUALITY_LOW_BOUND;
            				 sn.updateNeighborLinkQuality(Integer.parseInt(info[1]), 1, linkquality);
            			}
            			break;
            		}*/
            	}
        		}

	}
	
	   private void processFurtherSinglePathRoutingInfo()
	   {
			int i=2;
			//The hop number starts from 2
			while(i<=MAXIMUM_HOP)
			{
				//The routing table establishment is implemented
				//with each caching router
				for(int m=0; m<snl.getSensorNodeListSize();m++)
				{
					SensorNode sn = snl.getNodeByIndex(m);
					setUpSinglePathRoute(sn,i);
				}
				i++;
			}
	   }
		private void setUpSinglePathRoute(SensorNode sn, int hop)
		{
				//1. Address of node we are currently working on
				int currentCRAddress = sn.getNodeID();
				//System.out.println("SN  "+sn.getNodeID());
				for(int m=0; m<sn.getRoutingTable().getRoutingTableSize();m++)
				{
					//2. Explore all route entries of this router
					RouteEntry rtEntry = sn.getRoutingTable().getRouteEntry(m);
					int currentNextHop=-1;
					int currentHopNumber=-1;
					int currentDest = -1;
					//If it is a valid rtEntry
					if(rtEntry != null)
					{
						//Notice: the value of next hop may needs validation before using
						currentNextHop = rtEntry.getNextHop(); //The value of next hop will be used to locate next router
						currentHopNumber = rtEntry.getHop();
						currentDest = rtEntry.getDest();

						//We only consider the route entry which contains route with (hop-1) hops. Then the following steps are to
						//find out a router which is the destination of current route entry, and extends current route with 1-hop
						if(currentHopNumber == (hop-1) )
						{
							boolean found=false;
						
						//2. Now we need to check the router, whose id is equal to "currentDest", to find out if this router has route entry whose 1 hop neighbor
						// has not been covered by current router
							int n=0;
							//Explore caching router list again
							for(n=0; n<snl.getSensorNodeListSize();n++)
							{
								if(snl.getNodeByIndex(n).getNodeID() == currentDest )
								{
									found=true;
									break;
								}
							}
							//As it is assumed that the principle "shortest path" is employed, only one path can be located
							if(found)
							{
								//Now we need to check each route entry on router "currentDest"
									for(int y=0; y<snl.getNodeByIndex(n).getRoutingTable().getRoutingTableSize(); y++)
									{
										RouteEntry rty = snl.getNodeByIndex(n).getRoutingTable().getRouteEntry(y);
										if(rty.getHop()==1 && rty.getDest()!=currentCRAddress)
										{
											sn.getRoutingTable().createSingleShortestPath(currentNextHop, rty.getDest(), hop, rtEntry.getSummaryOfLinkQuality(),rty.getSummaryOfLinkQuality());
										}
									}
								}
						}
					}
				}
		}
		
		
		
	private void CreateNodeFromEvaluationFile() throws IOException
	{
		if(this.adjacentFileName==null){
			System.out.println("Please specify the adjacent file name");
			System.exit(-1);
		}else{
		initializeSensorNodeFromFile(adjacentFileName);
		}
		
	/*	for(int i=0; i<snl.getSensorNodeListSize();i++)
		{
			SensorNode sn = snl.getNodeByIndex(i);
			System.out.println("Node  "+sn.getNodeID());
		}*/
	}
	
	private void UpdateLinkQuality() throws IOException
	{
		if(this.linkQualityFileName==null){
			System.out.println("Please specify the link quality file name");
			System.exit(-1);
		}else{
		updateLinkQualityFromFile(linkQualityFileName);
		}
	}
	
	
	private void updateLinkQualityFromFile(String linkQualityFileName) throws IOException{
		File file = new File(linkQualityFileName);
		if(!file.exists() || file.isDirectory())throw new FileNotFoundException();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String temp = null;
		temp = br.readLine();
		int rowNumber=1;
		while(temp != null){
			readLinkQuality(temp,rowNumber);
			temp = br.readLine();
			rowNumber++;
		}
        br.close();
	}
	
	private void readLinkQuality(String dataLine, int rowNumber)
	{
		String[] neighborInfo = processDataLine(dataLine,rowNumber);
		if(neighborInfo == null)
		{
			//No data is obtained from the current line.
			System.out.println("Unable to read link quality data from file");
			System.exit(-1);
		}
		
		//Ignore the first row
		if(rowNumber>1)
		{
			
			int srcNode=-1;
			try{
			 srcNode = Integer.parseInt(neighborInfo[0]);
			}catch(NumberFormatException nfe)
			{
				System.out.println("Error in abstracting file "+this.linkQualityFileName+"  at row "+rowNumber+"  position "+0);
				System.exit(-1);
			}
			SensorNode srcSensorNode = snl.getNodeByAddress(srcNode);
			
			if(srcSensorNode!=null)
			{
				for(int i=1; i<neighborInfo.length;i++)
				{
					if(!neighborInfo[i].equals("0"))
					{
						try{
							
							int linkQuality = Integer.parseInt(neighborInfo[i]);
							
							if(linkQuality<=0)
							{
								System.out.println("Invalid link quality: "+this.linkQualityFileName+"  at row "+rowNumber+"  position "+i);
							}
							srcSensorNode.updateNeighbourLinkQuality(i-1,linkQuality);
						}catch(NumberFormatException nfe)
						{
							System.out.println("Error in abstracting file "+this.linkQualityFileName+"  at row "+rowNumber+"  position "+i);
							System.exit(-1);
						}
					}
					
				}
				
			}
		}
	}
	
	private void initializeSensorNodeFromFile(String adjacentFileName) throws IOException{
		
		File file = new File(adjacentFileName);
		if(!file.exists() || file.isDirectory())throw new FileNotFoundException();

		BufferedReader br = new BufferedReader(new FileReader(file));
		String temp = null;
		temp = br.readLine();
		int rowNumber=1;
		while(temp != null){
			createSensorNode(temp,rowNumber);
			temp = br.readLine();
			rowNumber++;
		}
        br.close();

	}
	
	/**
	 * 
	* @Title: createSensorNode
	* @Description: Read the data line, and create sensor node
	* @param: @param dataLine 
	* @return: void 
	* @throws
	 */
	private void createSensorNode(String dataLine, int rowNumber){
		String[] neighborInfo = processDataLine(dataLine,rowNumber);
		if(neighborInfo == null)
		{
			//No data is obtained from the current line.
			System.out.println("Unable to analyze the matrix file");
			System.exit(-1);
		}
		
		//If the row number is 1, each element of neighborInfo is a node as the first row of the file 
		//lists all the nodes
		if(rowNumber == 1)
		{
			snl = new SensorNodeList();
			for(int i=0; i<neighborInfo.length;i++)
			{
				int nodeID =-1;
				try{
					nodeID=Integer.parseInt(neighborInfo[i]);
				}catch(NumberFormatException nfe){
					System.out.println("Error in initializing node from file "+this.adjacentFileName);
					System.exit(-1);
					
				}
				
				if(nodeID !=-1)
				{
					SensorNode sn = new SensorNode(nodeID);
					snl.addNode(sn);
				}
			}
		}
		
		//For the rows whose order is greater than 1, we can generate neighbour table for each node locating at the first position of the row
		if(rowNumber>1)
		{
			int srcNode=-1;
			try{
			 srcNode = Integer.parseInt(neighborInfo[0]);
			}catch(NumberFormatException nfe)
			{
				System.out.println("Error in abstracting file "+this.adjacentFileName+"  at row "+rowNumber+"  position "+0);
				System.exit(-1);
			}
			
			SensorNode srcSensorNode = snl.getNodeByAddress(srcNode);
			//We have successfully obtained the 'src' node.
			if(srcSensorNode!=null)
			{
				for(int i=1;i<neighborInfo.length;i++)
				{
					//Start to read each element of the array, 'neighborInfo'. According to the format of adjacent matrix, the position of element, i.e. i, is greater than
					//the node address by 1. Which means, if the current position in the array is 1, the corresponding node address is 0.
					int toNode=i-1;
					
					//The neighbour relationship is established between two different nodes
					if(srcNode != toNode)
					{
						if(neighborInfo.equals("1"))
						{
							srcSensorNode.updateNeighbourList(toNode);
						}
					}
				}
			}
		}
		/*if(neighborInfo.length!=SensorNodeParameter.NUMBER_OF_PARAMETERS_PER_LINE)
		{
			//The number of parameters read from the data line is not equivalent to the specified value
			return;
		}*/
		
	/*	int id1 = -1;
		int id2 = -1;

		try{
			id1 = Integer.parseInt(neighborInfo[0]);
			id2 = Integer.parseInt(neighborInfo[1]);

			}catch (NumberFormatException nfe)
			{
				System.out.println("Error in creating caching router");
				System.exit(-1);
			}
			    SensorNode sensorNodeID1 = new SensorNode(id1);
			    addSensorNode(sensorNodeID1);
			    SensorNode sensorNodeID2 = new SensorNode(id2);
			    addSensorNode(sensorNodeID2);*/
	}
	
	/**
	* @Title: processDataLine
	* @Description: Analyse the received data line, and separate the parameters
	* @param: @param dataline 
	* @return: string array containg the abstracted parameters 
	* @throws
	*/
	private String[] processDataLine(String dataLine, int rowNumber)
	{
		//A temporal string array used to store the analysed parameter name and value
		String[] inputLine = new String[NUM_PARAMETER_PER_LINE];
		//Initialise the temporal string array
		for(int i=0; i<NUM_PARAMETER_PER_LINE;i++)
		{
			inputLine[i]="";
		}
		//A counter used to count how many data has been stored in the string array, i.e. inputLine
		int counter = -1;
		//An indicator used to indicate if the program is currently storing data (see below)
		boolean receivingData=false;
		//If the passed string has content, i.e. length > 0, and the first content is not equal to '#'
		if(dataLine.length()>0 && dataLine.charAt(0)!='#')
		{
			for(int i=0; i<dataLine.length();i++)
			{
				//If the current char is not a blank or 'Tab'
				if(dataLine.charAt(i)!=' ' && dataLine.charAt(i)!='	')
				{
					//If the program is not receiving data, start to receive data and store it in 
					//the closet string, i.e. inputLine[counter]
					if(!receivingData)
					{
						counter++;
						receivingData=true;
					}
					if(counter<NUM_PARAMETER_PER_LINE)
					{
						inputLine[counter]=inputLine[counter]+dataLine.charAt(i);
						
					}
					else 
					{
						//If no more room left for storing data, just ignore it.
						System.out.println("Too many parameters in topology file.");
						System.exit(-1);
					}
				}else{
					//If we encounter blank ,or tab key, whilst the data receiving is in progress, stop
					//receiving.
					if(dataLine.charAt(i)==' ' || dataLine.charAt(i)=='	')
					{
						if(receivingData)
						{
							receivingData=false;
						}
					}
					
				}
			}
		}else{
			return null;
		}
		String[] abstractedParameter=null;
		if(counter>=0)
		{
			abstractedParameter = new String[counter+1];
			for(int i=0; i<(counter+1);i++)
			{
				abstractedParameter[i] = inputLine[i];
			}
			 
		}
		
		return abstractedParameter;
	}
	
	String adjacentFileName=null;
	String energyFileName=null;
	String linkQualityFileName=null;
	SensorNodeList snl = null;

	static int NUM_PARAMETER_PER_LINE=200;
	static int MAXIMUM_HOP=20;
}
