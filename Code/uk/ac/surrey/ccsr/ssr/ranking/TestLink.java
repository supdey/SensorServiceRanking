/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ccsr.ssr.ranking;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import org.openrdf.model.impl.URIImpl;
import uk.ac.surrey.ccsr.ssr.path2.AnalysisNodeTopology;
import uk.ac.surrey.ccsr.ssr.path2.BestRouteResult;
import uk.ac.surrey.ccsr.ssr.path2.SensorNodeList;
import uk.ac.surrey.ccsr.ssr.semanticData.DatasetProcessing;
import uk.ac.surrey.ccsr.ssr.semanticData.Parameters;

/**
 *
 * @author ww0004
 */
public class TestLink {
    
    public static String locationPrefix = "http://www.surrey.ac.uk/ccsr/ontologies/LocationModel.owl#";
    public static String serviceDataPrefix = "http://iotserver3.ee.surrey.ac.uk/IoTData/BAbuildingUniversityofSurrey_Gateway#";
    public static File mappingFile = new File("data/Room-Node-mapping.txt");
    private static Properties properties = new Properties();
    private static Hashtable<String, Integer> mappingTable = new Hashtable<String, Integer>();
    private static int numberOfQueries = 500;
    private static int numberOfUnansweredQueries = 0;
    private static int queryCount = 0;
    private static double energyConsumption = 0.05;
    private static Vector<String> goodQueries = new Vector<String>();
    private static int totalQueries = 0;
    private static int answeredQueries = 0;
    
    private static int getSensorNodeID(String sensorNodeName) {
        return mappingTable.get(sensorNodeName);
    }
    
    private static String getSensorNodeURL(int sensorNodeID) {
        String url = properties.getProperty(String.valueOf(sensorNodeID));
        return locationPrefix + url;
    }
    
    public static void main(String args[]) throws Exception {
        //INITIALISATION------------------------------------------
        //get the values from sensor networks for calculation
        SensorNetwork sn = new SensorNetwork();
        int numberOfNodes = sn.getNumberOfNodes();
        String folderName = "data/conf-link/";
        File report = new File(folderName + "report005.txt");
        PrintWriter pw = new PrintWriter(new FileWriter(report, true));

        //for printing double values
        DecimalFormat df = new DecimalFormat("#.####");

        //read all the files
        File adjacencyMatrixFile = new File(folderName + "Matrix.txt");
        File powerFile = new File(folderName + "Energy.txt");
        File linkQualityFile = new File(folderName + "LinkQuality.txt");
        File queryFile = new File("data/queries.txt");

        // FIRST TIME CALCULATE IMPORTANCE VALUES AND GET POWER VALUES
        double[] power = sn.getNodePowerFromFile(powerFile);

        //print initial power distribution to file
        printPower(pw, power);

        //INITIALISE TOPOLOGY
        AnalysisNodeTopology ant = new AnalysisNodeTopology(folderName + "Matrix.txt", folderName + "LinkQuality.txt", folderName + "Energy.txt");
        SensorNodeList snl = ant.EstablishTopology();
        int MAX_ARRAY_LEN = 5000;
        // matrixArray format: matrixArray[0][0]  Source node id,  matrixArray[0][1]   NeighborNode id,  matrixArray[0][2]  link (0=link, 1=no link)
        int matrixArray[][] = new int[MAX_ARRAY_LEN][3];
        // linkQualityArray format: linkQualityArray[0][0] Source node id, linkQualityArray[0][1] NeighboNode id, linkQualityArray[0][3] link quality
        int linkQualityArray[][] = new int[MAX_ARRAY_LEN][3];
        matrixArray = ant.getMatrixInfoArray();
        linkQualityArray = ant.getLinkQualityArray();
        snl = ant.EastlishTopologyFromArray(matrixArray, linkQualityArray);
        //END OF INITIALISE TOPOLOGY

        DatasetProcessing dp = new DatasetProcessing();
        
        double powerValue = 0.0;

        //prepare the semantic query; read from the query file
        String aQuery;
        try {
            //load the mapping file
            properties.load(new FileInputStream(mappingFile));
            //create a hashtable for the mapping file to enable quick search
            Enumeration enu = properties.keys();
            while (enu.hasMoreElements()) {
                String key = enu.nextElement().toString();
                mappingTable.put(properties.getProperty(key), Integer.parseInt(key));
            }

            //prepare to read query file
            BufferedReader br = new BufferedReader(new FileReader(queryFile));
            ArrayList al = new ArrayList();
            ArrayList<ComputedServiceUsingLink> services = new ArrayList<ComputedServiceUsingLink>();
            boolean needToUpdate = false;

            // send query one by one
            while ((aQuery = br.readLine()) != null) {
                //if (!goodQueries.contains(aQuery)) {
                    goodQueries.add(aQuery);
                //}
                totalQueries++;
                
                services.clear();
                String sparql = Parameters.PREFIX + aQuery;
                //send the SPARQL query and get the results    
                al = dp.getServiceDiscoveryResults(sparql);
                //get the services and then look for their sensor node IDs
                if (al != null && al.size() > 0) {
                    //for all the services
                    for (int k = 0; k < al.size(); k++) {
                        //pw.append(al.get(i).toString() + "\n");
                        URIImpl serviceURL = (URIImpl) (al.get(k));
                        //pw.append("------- BEGIN STATISTICS: " + serviceURL.getLocalName() + "--------" + "\n");
                        //once have the service name, then do the computation to initialise an instance of ComputedService
                        ComputedServiceUsingLink cs = new ComputedServiceUsingLink();
                        cs.setURL(serviceURL.toString());

                        //get the attached sensor node ID
                        URIImpl sensorNodeURL = new URIImpl(dp.getSensorNodeID(serviceURL.toString()));
                        //System.out.println("SENSORNODEURL IS " + sensorNodeURL.getLocalName().toString());
                        cs.setSensorNodeID(getSensorNodeID(sensorNodeURL.getLocalName().toString()));
                        cs.setPower(power[cs.getSensorNodeID()]);

                        //get the bestPathFromGateway
                        int gatewayID = 0;

                        //The format for parameters is: src node, destination node, and the sensor node list.
                        BestRouteResult bstRoute = new BestRouteResult(gatewayID, cs.getSensorNodeID(), snl);
                        bstRoute.calculateBestRoute();
                        if (bstRoute.getNodesOnRoute().length > 1) {
                            cs.setBestPathFromGateway(bstRoute.getNodesOnRoute());
                            double linkQualityFromGateway = 1 / bstRoute.getLinkQualitySummary();
                            cs.setLinkQualityFromGateway(linkQualityFromGateway);

                            //get the bestPathToGateway
                            bstRoute = new BestRouteResult(cs.getSensorNodeID(), gatewayID, snl);
                            bstRoute.calculateBestRoute();
                            cs.setBestPathToGateway(bstRoute.getNodesOnRoute());
                            //pw.append("BEST PATH to GATEWAY: \n");                            
                            double linkQualityToGateway = 1 / bstRoute.getLinkQualitySummary();
                            //pw.append("\tLink Quality Summary: " + String.valueOf(df.format(linkQualityToGateway)) + "\n");
                            cs.setLinkQualityToGateway(linkQualityToGateway);
                            services.add(cs);
                        }
                        /*else {
                         cost = maxCost;
                         //pw.append("ROUTE NOT FOUND \n");
                         }*/
                    }
                    //route actually found
                    if (services.size() > 0) {
                        answeredQueries++;
                        // find the service to be used
                        ComputedServiceUsingLink serviceChosen = findServiceWithMaxLinkQuality(services);
                        queryCount++;
                        //update power, gateway' power will not change
                        //update power of the nodes from the gateway
                        for (int i = 0; i < serviceChosen.getBestPathFromGateway().length; i++) {
                            int sensorID = serviceChosen.getBestPathFromGateway()[i];
                            if (sensorID != 0) {
                                power[sensorID] = Double.parseDouble(df.format(power[sensorID] - energyConsumption));
                                //update Fang's matrix and linkQualityMatrix
                                if (power[sensorID] <= energyConsumption) {
                                    for (int j = 0; j < matrixArray.length; j++) {
                                        if (matrixArray[j][0] == sensorID) {
                                            matrixArray[j][2] = 0;
                                        }
                                        if (matrixArray[j][1] == sensorID) {
                                            matrixArray[j][2] = 0;
                                        }
                                        if (linkQualityArray[j][0] == sensorID) {
                                            linkQualityArray[j][2] = 0;
                                        }
                                        if (linkQualityArray[j][1] == sensorID) {
                                            linkQualityArray[j][2] = 0;
                                        }
                                    }
                                    
                                    needToUpdate = true;
                                }
                            }
                        }
                        //update power of the nodes to the gateway
                        for (int i = 0; i < serviceChosen.getBestPathToGateway().length; i++) {
                            int sensorID = serviceChosen.getBestPathToGateway()[i];
                            if (sensorID != 0) {
                                power[sensorID] = Double.parseDouble(df.format(power[sensorID] - energyConsumption));
                                //update Fang's matrix and linkQualityMatrix
                                if (power[sensorID] <= energyConsumption) {
                                    for (int j = 0; j < matrixArray.length; j++) {
                                        if (matrixArray[j][0] == sensorID) {
                                            matrixArray[j][2] = 0;
                                        }
                                        if (matrixArray[j][1] == sensorID) {
                                            matrixArray[j][2] = 0;
                                        }
                                        if (linkQualityArray[j][0] == sensorID) {
                                            linkQualityArray[j][2] = 0;
                                        }
                                        if (linkQualityArray[j][1] == sensorID) {
                                            linkQualityArray[j][2] = 0;
                                        }
                                    }
                                    needToUpdate = true;
                                }
                            }
                        }
                        //printpower
                        printPower(pw, power);
                    }
                }

                //ONE QUERY PROCESSING IS DONE, DETERMINE IF RANKING PARAMETERS NEED TO BE CHANGED
                if (queryCount >= 50 || needToUpdate) {
                    //establish topology again
                    snl = ant.EastlishTopologyFromArray(matrixArray, linkQualityArray);
                    //at the end, reset parameters
                    queryCount = 0;
                    needToUpdate = false;
                }

                //System.out.println("SERVICES LENGTH IS " + services.size());
            }
            
            pw.close();
            System.out.println("total Queries is: " + totalQueries);
            System.out.println("Answered queries is: " + answeredQueries);
            /*Collections.shuffle(goodQueries);
            Collections.shuffle(goodQueries);
            Collections.shuffle(goodQueries);
            for (int i = 0; i < goodQueries.size(); i++) {
                System.out.println(goodQueries.elementAt(i));
            }*/
        } catch (IOException ex) {
            //Logger.getLogger(QueryCost.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void printPower(PrintWriter pw, double[] powerFile) {
        for (int i = 0; i < powerFile.length; i++) {
            pw.print(powerFile[i] + "\t");
        }
        pw.println();
    }
    
    private static ComputedServiceUsingLink findServiceWithMaxLinkQuality(ArrayList list) {
        System.out.println("LENGTH IS " + list.size());
        System.out.println(list);
        ComputedServiceUsingLink cs = (ComputedServiceUsingLink) list.get(0);
        if (list.size() == 1) {
            return cs;
        } else {
            for (int i = 1; i < list.size(); i++) {
                if (cs.compareTo(((ComputedServiceUsingLink) list.get(i))) < 0) {
                    cs = (ComputedServiceUsingLink) list.get(i);
                }
            }
            return cs;
        }
    }
}

class ComputedServiceUsingLink extends ComputedService implements Comparable {
    
    public ComputedServiceUsingLink() {
        super();
    }
    
    public int compareTo(Object o) {
        if (this.getLinkQualityFromGateway() * this.getLinkQualityToGateway() < ((ComputedServiceUsingLink) o).getLinkQualityFromGateway() * ((ComputedServiceUsingLink) o).getLinkQualityToGateway()) {
            return 1;
        } else {
            return -1;
        }
    }
}
