package uk.ac.surrey.ccsr.ssr.path;

import java.util.ArrayList;




public class SensorNodeList {

	public SensorNodeList(){
		//Initialize the array list
		sensorNodeList = new ArrayList<SensorNode>();
		sensorNodeList.clear();
		
	}
	
	public void addNode(SensorNode sn)
	{
		//Explore the stored sensor node to avoid adding duplicate node
		boolean duplicate=false;
		for(int i=0; i<sensorNodeList.size();i++)
		{
			SensorNode tmpSN = sensorNodeList.get(i);
			if(tmpSN.getNodeID() == sn.getNodeID())
			{
				duplicate=true;
				break;
			}
		}
		
		if(!duplicate)sensorNodeList.add(sn);
	}
	
	public SensorNode getNodeByAddress(int address)
	{
		SensorNode sn=null;
		for(int i=0; i<sensorNodeList.size();i++)
		{
			SensorNode tmpSN = sensorNodeList.get(i);
			if(tmpSN.getNodeID() == address)
			{
				sn = tmpSN;
				break;
			}
		}
		
		return sn;
	}
	
	public SensorNode getNodeByIndex(int index)
	{
		SensorNode sn=null;
		
		if(index<sensorNodeList.size())sn = sensorNodeList.get(index);
		return sn;
		
	}
	public int getSensorNodeListSize()
	{
		return sensorNodeList.size();
	}
	private ArrayList<SensorNode> sensorNodeList;
}
