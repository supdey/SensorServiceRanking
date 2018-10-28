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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class TestCost {

    public static String locationPrefix = "http://www.surrey.ac.uk/ccsr/ontologies/LocationModel.owl#";
    public static String serviceDataPrefix = "http://iotserver3.ee.surrey.ac.uk/IoTData/BAbuildingUniversityofSurrey_Gateway#";
    public static File mappingFile = new File("data/Room-Node-mapping.txt");
    private static Properties properties = new Properties();
    private static Hashtable<String, Integer> mappingTable = new Hashtable<String, Integer>();
    private static int numberOfQueries = 500;
    private static int numberOfUnansweredQueries = 0;
    private static int queryCount = 0;
    private static double energyConsumption = 0.02;
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
        String folderName = "data/conf-cost/";
        File report = new File(folderName + "report002.txt");
        PrintWriter pw = new PrintWriter(new FileWriter(report, true));

        //for printing double values
        DecimalFormat df = new DecimalFormat("#.####");

        //read all the files
        File adjacencyMatrixFile = new File(folderName + "Matrix.txt");
        File powerFile = new File(folderName + "Energy.txt");
        File linkQualityFile = new File(folderName + "LinkQuality.txt");
        File queryFile = new File("data/queries.txt");

        // FIRST TIME CALCULATE IMPORTANCE VALUES AND GET POWER VALUES
        double[] importance = sn.computeImportanceValues(sn.computeTransitionMatrix(sn.readAdjacencyMatrix(adjacencyMatrixFile)));
        double[] power = sn.getNodePowerFromFile(powerFile);
        double[][] adjacencyMatrix = new double[64][64];
        System.out.println("Importance matrix");
        for (int i = 0; i < importance.length; i++) {
            System.out.println(importance[i] + "\t");
        }
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

        //value of the cost function
        double cost = 0.0;
        double importanceValue = 0.0;
        double powerValue = 0.0;
        //having the maximum cost means that the route is not found using our algorithm
        final double maxCost = 1000.0;

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
            ArrayList<ComputedServiceUsingCost> services = new ArrayList<ComputedServiceUsingCost>();
            boolean needToUpdate = false;

            // send query one by one
            while ((aQuery = br.readLine()) != null) {
                totalQueries++;
                if (totalQueries == 150) {
                    System.out.println("Importance matrix");
                    for (int i = 0; i < importance.length; i++) {
                        System.out.println(importance[i] + "\t");
                    }
                }
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
                        ComputedServiceUsingCost cs = new ComputedServiceUsingCost();
                        cs.setURL(serviceURL.toString());

                        //get the attached sensor node ID
                        URIImpl sensorNodeURL = new URIImpl(dp.getSensorNodeID(serviceURL.toString()));
                        //System.out.println("SENSORNODEURL IS " + sensorNodeURL.getLocalName().toString());
                        cs.setSensorNodeID(getSensorNodeID(sensorNodeURL.getLocalName().toString()));

                        //get the bestPathFromGateway
                        int gatewayID = 0;

                        //The format for parameters is: src node, destination node, and the sensor node list.
                        BestRouteResult bstRoute = new BestRouteResult(gatewayID, cs.getSensorNodeID(), snl);
                        bstRoute.calculateBestRoute();
                        if (bstRoute.getNodesOnRoute().length > 1) {
                            cs.setBestPathFromGateway(bstRoute.getNodesOnRoute());
                            for (int j = 0; j < cs.getBestPathFromGateway().length; j++) {
                                importanceValue = importance[cs.getBestPathFromGateway()[j]] * 100;
                                powerValue = power[cs.getBestPathFromGateway()[j]];
                                //compute the cost
                                cost += importanceValue / powerValue;
                            }
                            double linkQualityFromGateway = 1 / bstRoute.getLinkQualitySummary();
                            cs.setLinkQualityFromGateway(linkQualityFromGateway);

                            //get the bestPathToGateway
                            bstRoute = new BestRouteResult(cs.getSensorNodeID(), gatewayID, snl);
                            bstRoute.calculateBestRoute();
                            cs.setBestPathToGateway(bstRoute.getNodesOnRoute());
                            //pw.append("BEST PATH to GATEWAY: \n");
                            for (int j = 1; j < cs.getBestPathToGateway().length; j++) {
                                importanceValue = importance[cs.getBestPathToGateway()[j]] * 100;
                                powerValue = power[cs.getBestPathToGateway()[j]];

                                //compute the cost
                                cost += importanceValue / powerValue;
                            }
                            cs.setQueryCost(cost);
                            double linkQualityToGateway = 1 / bstRoute.getLinkQualitySummary();
                            //pw.append("\tLink Quality Summary: " + String.valueOf(df.format(linkQualityToGateway)) + "\n");
                            cs.setLinkQualityToGateway(linkQualityToGateway);
                            services.add(cs);
                            cost = 0.0;
                        }
                        /*else {
                         cost = maxCost;
                         //pw.append("ROUTE NOT FOUND \n");
                         }*/
                    }
                    //route actually found
                    if (services.size() > 0) {

                        // find the service to be used
                        ComputedServiceUsingCost serviceChosen = findServiceWithMinCost(services);
                        if (serviceChosen.getBestPathFromGateway().length > 1 && serviceChosen.getBestPathToGateway().length > 1) {
                            queryCount++;
                            answeredQueries++;
                        }
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
                                    //update my adjacency matrix
                                    for (int j = 0; j < adjacencyMatrix[0].length; j++) {
                                        adjacencyMatrix[sensorID][j] = 0;
                                        adjacencyMatrix[j][sensorID] = 0;
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
                                    //update my adjacency matrix
                                    for (int j = 0; j < adjacencyMatrix[0].length; j++) {
                                        adjacencyMatrix[sensorID][j] = 0;
                                        adjacencyMatrix[j][sensorID] = 0;
                                    }
                                    needToUpdate = true;
                                }
                            }
                        }
                        printPower(pw, power);

                    }
                }

                //ONE QUERY PROCESSING IS DONE, DETERMINE IF RANKING PARAMETERS NEED TO BE CHANGED
                if (queryCount >= 10 || needToUpdate) {
                    //establish topology again
                    snl = ant.EastlishTopologyFromArray(matrixArray, linkQualityArray);
                    //recalculate importance
                    importance = sn.computeImportanceValues(adjacencyMatrix);
                    //at the end, reset parameters
                    queryCount = 0;
                    needToUpdate = false;
                }

                //System.out.println("SERVICES LENGTH IS " + services.size());
            }
            pw.close();
            System.out.println("total Queries is: " + totalQueries);
            System.out.println("Answered queries is: " + answeredQueries);
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

    private static ComputedServiceUsingCost findServiceWithMinCost(ArrayList list) {
        System.out.println("LENGTH IS " + list.size());
        System.out.println(list);
        ComputedServiceUsingCost cs = (ComputedServiceUsingCost) list.get(0);
        if (list.size() == 1) {
            return cs;
        } else {
            for (int i = 1; i < list.size(); i++) {
                if (cs.compareTo(((ComputedServiceUsingCost) list.get(i))) > 0) {
                    cs = (ComputedServiceUsingCost) list.get(i);
                }
            }
            return cs;
        }
    }
}

class ComputedServiceUsingCost extends ComputedService implements Comparable {

    public ComputedServiceUsingCost() {
        super();
    }

    public int compareTo(Object o) {
        if (this.getQueryCost() < ((ComputedServiceUsingCost) o).getQueryCost()) {
            return -1;
        } else {
            return 1;
        }
    }
}
