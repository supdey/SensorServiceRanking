/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ccsr.ssr.semanticData;

import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.manager.RemoteRepositoryManager;
import uk.ac.surrey.ccsr.ssr.ranking.ComputedService;

/**
 *
 * @author ww0004
 */
public class DatasetProcessing {

    private static final String sesameServer = "http://localhost:8080/openrdf-sesame/";
    private Repository rep = null;

    public DatasetProcessing() {
        rep = getRepository();
    }

    public Repository getRepository() {
        Repository repository = null;
        RemoteRepositoryManager manager = new RemoteRepositoryManager(sesameServer);
        //manager.setUsernameAndPassword("unis", "iotest");
        try {
            manager.initialize();
            repository = manager.getRepository("BA_Building");
        } catch (RepositoryConfigException ex) {
            Logger.getLogger(DatasetProcessing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RepositoryException ex) {
            Logger.getLogger(DatasetProcessing.class.getName()).log(Level.SEVERE, null, ex);
        }

        return repository;
    }

    //perform the service discovery
    //return a number of service (represented with the local names)
    public ArrayList getServiceDiscoveryResults(String query) {
        ArrayList services = new ArrayList();
        TupleQueryResult tupleQueryResult = null;
        Set<String> bindingNames = null;
        BindingSet bindingSet;

        try {
            RepositoryConnection connection = (RepositoryConnection) rep.getConnection();
            try {
                TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, query);
                tupleQueryResult = tupleQuery.evaluate();
                while (tupleQueryResult.hasNext()) {

                    bindingSet = tupleQueryResult.next();
                    bindingNames = bindingSet.getBindingNames();
                    for (String name : bindingNames) {
                        services.add(bindingSet.getValue(name));
                    }
                }
                return services;
            } finally {
                connection.close();
            }
        } catch (QueryEvaluationException ex) {
            //Logger.getLogger(DatasetProcessing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RepositoryException ex) {
            //Logger.getLogger(DatasetProcessing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedQueryException ex) {
            //Logger.getLogger(DatasetProcessing.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    //A sensor node is attached with 2-3 sensors, once the sensor service is found
    //this method is to query the repository and get the sensor node ID for later computation
    public String getSensorNodeID(String serviceURL) {
        String id = "";
        String query = "select ?room " + "\n"
                + "where{ " + "\n"
                + "?service rdf:type IoT:IoTService. " + "\n"
                + "?service IoT:serviceArea ?room. " + "\n"
                + "FILTER (?service=<" + serviceURL + ">). " + "\n"
                + "} ";

        ArrayList al = getServiceDiscoveryResults(Parameters.PREFIX + query);
        if(al == null){
            id = null;
        }else{
            URIImpl url = (URIImpl)al.get(0);
            id = url.toString();
        }
        return id;
    }

    public static void main(String[] args) {
        DatasetProcessing dp = new DatasetProcessing();
        ArrayList al = new ArrayList();
        String prefix = Parameters.PREFIX;
        String query = "select ?service" + "\n"
                + "where{" + "\n"
                + "?service rdf:type IoT:IoTService." + "\n"
                + "?service IoT:hasGeohashCode ?geohash." + "\n"
                + "?service IoT:describedBy ?model." + "\n"
                + "?model IoT:hasOperation ?operation." + "\n"
                + "?operation IoT:hasOutput ?output." + "\n"
                + "?output IoT:semanticConceptRef ?type." + "\n"
                + "FILTER ((regex(str(?geohash), \"gcped01\")) && ?type=\"http://purl.oclc.org/NET/ssnx/qu/dim#IndoorTemperature\")." + "\n"
                + "}";
        al = dp.getServiceDiscoveryResults(prefix + query);

        for (int i = 0; i < al.size(); i++) {
            //System.out.println(al.get(i));
            URIImpl url = (URIImpl)(al.get(i));
            System.out.println(url.getLocalName());
        }
        
        System.out.println(dp.getSensorNodeID("http://iotserver3.ee.surrey.ac.uk/IoTData/BAbuildingUniversityofSurrey_Gateway#Service_134"));
    }
}
