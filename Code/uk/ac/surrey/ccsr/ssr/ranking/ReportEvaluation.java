/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ccsr.ssr.ranking;

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
import uk.ac.surrey.ccsr.ssr.path.AnalysisNodeTopology;
import uk.ac.surrey.ccsr.ssr.path.BestRouteResult;
import uk.ac.surrey.ccsr.ssr.path.SensorNodeList;
import uk.ac.surrey.ccsr.ssr.semanticData.DatasetProcessing;
import uk.ac.surrey.ccsr.ssr.semanticData.Parameters;

/**
 *
 * @author ww0004
 */
public class ReportEvaluation {

    public static String locationPrefix = "http://www.surrey.ac.uk/ccsr/ontologies/LocationModel.owl#";
    public static String serviceDataPrefix = "http://iotserver3.ee.surrey.ac.uk/IoTData/BAbuildingUniversityofSurrey_Gateway#";
    public static File mappingFile = new File("data/Room-Node-mapping.txt");
    private static Properties properties = new Properties();
    private static Hashtable<String, Integer> mappingTable = new Hashtable<String, Integer>();

    private static int getSensorNodeID(String sensorNodeName) {
        System.out.println(sensorNodeName);
        return mappingTable.get(sensorNodeName);
    }

    private static String getSensorNodeURL(int sensorNodeID) {
        String url = properties.getProperty(String.valueOf(sensorNodeID));
        return locationPrefix + url;
    }

    public static void main(String args[]) {
        //get the values from sensor networks for calculation
        SensorNetwork sn = new SensorNetwork();
        int numberOfNodes = sn.getNumberOfNodes();
        String folderName = "data/configuration14/";
        File report = new File("data/report.txt");
        PrintWriter pw = null;

        //for printing double values
        DecimalFormat df = new DecimalFormat("#.####");

        //read all the files
        File adjacencyMatrixFile = new File(folderName + "Matrix.txt");
        File powerFile = new File(folderName + "Energy.txt");
        File linkQualityFile = new File(folderName + "LinkQuality.txt");
        File queryFile = new File("data/queries.txt");

        double[] importance = sn.computeImportanceValues(sn.computeTransitionMatrix(sn.readAdjacencyMatrix(adjacencyMatrixFile)));
        double[] power = sn.getNodePowerFromFile(powerFile);

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

            //prepare writer to create report
            pw = new PrintWriter(new FileWriter(report, true));

            //prepare to read query file
            BufferedReader br = new BufferedReader(new FileReader(queryFile));
            // send query one by one
            while ((aQuery = br.readLine()) != null) {
                String sparql = Parameters.PREFIX + aQuery;
                //send the SPARQL query and get the results
                ArrayList al = new ArrayList();
                al = dp.getServiceDiscoveryResults(sparql);
                //get the services and then look for their sensor node IDs
                pw.append(aQuery + "\n\n\n");
                
                //for all the services
                for (int i = 0; i < al.size(); i++) {
                    //pw.append(al.get(i).toString() + "\n");
                    URIImpl serviceURL = (URIImpl) (al.get(i));
                    pw.append(serviceURL.getLocalName() + ":\t\t");
                    //once have the service name, then do the computation to initialise an instance of ComputedService
                    ComputedService cs = new ComputedService();
                    cs.setURL(serviceURL.toString());

                    //get the attached sensor node ID
                    URIImpl sensorNodeURL = new URIImpl(dp.getSensorNodeID(serviceURL.toString()));
                    cs.setSensorNodeID(getSensorNodeID(sensorNodeURL.getLocalName().toString()));
                    //pw.append("ATTACHED TO SENSOR ID/NAME: " + cs.getSensorNodeID() + "/" + sensorNodeURL.getLocalName() + "\n");

                    //get importance of the sensor node
                    pw.append(df.format(importance[cs.getSensorNodeID()]*100) + "\t\t");

                    //get the power of the sensor node
                    pw.append(power[cs.getSensorNodeID()] + "\t\t");

                    //get the bestPathFromGateway
                    int gatewayID = 0;
                    //AnalysisNodeTopology ant = new AnalysisNodeTopology("./data/configuration1/Matrix.txt", "./data/configuration1/LinkQuality.txt", "./data/configuration1/Energy.txt");
                    AnalysisNodeTopology ant = new AnalysisNodeTopology(adjacencyMatrixFile.getPath(), linkQualityFile.getPath(), powerFile.getPath());
                    SensorNodeList snl = ant.EstablishTopology();
                    //The format for parameters is: src node, destination node, and the sensor node list.
                    BestRouteResult bstRoute = new BestRouteResult(gatewayID, cs.getSensorNodeID(), snl);
                    bstRoute.calculateBestRoute();
                    if(bstRoute.getNodesOnRoute().length > 1){
                    cs.setBestPathFromGateway(bstRoute.getNodesOnRoute());
                    //pw.append("BEST PATH FROM GATEWAY: \n");
                    for (int j = 0; j < cs.getBestPathFromGateway().length; j++) {
                        importanceValue = importance[cs.getBestPathFromGateway()[j]]*100;
                        powerValue = power[cs.getBestPathFromGateway()[j]];
                        //pw.append("\t" + String.valueOf(cs.getBestPathFromGateway()[j]));
                        //pw.append(" (");
                        //pw.append(String.valueOf(df.format(importanceValue)) + ";");
                        //pw.append(String.valueOf(powerValue) + ";");
                        //pw.append(")\n");
                        //compute the cost
                        cost += importanceValue/powerValue;
                    }
                    double linkQualityFromGateway = 1/bstRoute.getLinkQualitySummary();
                    //pw.append("\tLink Quality Summary: " + String.valueOf(df.format(linkQualityFromGateway)) + "\n");

                    //get the bestPathToGateway
                    bstRoute = new BestRouteResult(cs.getSensorNodeID(), gatewayID, snl);
                    bstRoute.calculateBestRoute();
                    cs.setBestPathToGateway(bstRoute.getNodesOnRoute());
                    //pw.append("BEST PATH to GATEWAY: \n");
                    for (int j = 1; j < cs.getBestPathToGateway().length; j++) {
                        importanceValue = importance[cs.getBestPathToGateway()[j]]*100;
                        powerValue = power[cs.getBestPathToGateway()[j]];
                        //pw.append("\t" + String.valueOf(cs.getBestPathToGateway()[j]));
                        //pw.append(" (");
                        //pw.append(String.valueOf(df.format(importanceValue)) + ";");
                        //pw.append(String.valueOf(powerValue) + ";");
                        //pw.append(")\n");
                        //compute the cost
                        cost += importanceValue/powerValue;
                    }
                    double linkQualityToGateway = 1/bstRoute.getLinkQualitySummary();
                    pw.append(String.valueOf(df.format(linkQualityToGateway + linkQualityFromGateway)) + "\t\t");
                    }
                    else{
                        cost = maxCost;
                        //pw.append("ROUTE NOT FOUND \n");
                    }
                    //append cost value
                    pw.append(df.format(cost) + "\n");


                    //pw.append("------- END STATISTICS: " + serviceURL.getLocalName() + "--------" + "\n\n\n");
                    cost = 0.0;
                }
                pw.append("\n\n");
            }

            pw.close();
        } catch (IOException ex) {
            Logger.getLogger(QueryCost.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}