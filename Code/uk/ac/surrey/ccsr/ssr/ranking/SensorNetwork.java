/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ccsr.ssr.ranking;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author ww0004
 */
public class SensorNetwork {
    //totally 64 nodes including gateway

    private static int numberOfNodes = 64;
    //ranking values
    double[] importanceValues = new double[numberOfNodes];

    public SensorNetwork() {
        //initialise the importance values
        importanceValues[0] = 1.0;
        for (int i = 1; i < numberOfNodes; i++) {
            importanceValues[i] = 0.0;
        }
    }

    //get the number of nodes
    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    //compute the node importance
    public double[] computeImportanceValues(double[][] p) {
        double[] newRank = new double[numberOfNodes];
        newRank = importanceValues;
        for (int it = 0; it < 20; it++) {
            /*for (int i = 0; i < numberOfNodes; i++) {
                System.out.print(importanceValues[i] + " ");
            }*/
            for (int i = 0; i < numberOfNodes; i++) {
                for (int j = 0; j < numberOfNodes; j++) {
                    newRank[i] += importanceValues[j] * p[j][i];
                }
            }
            normalise(newRank);
        }
        // Update page ranks.
        importanceValues = newRank;
        return importanceValues;
    }

    private void normalise(double[] p) {
        //get the sum
        double sum = 0.0;
        for (int i = 0; i < p.length; i++) {
            sum += p[i];
        }
        //normalise
        for (int i = 0; i < p.length; i++) {
            p[i] = p[i] / sum;
        }
    }

    //read adjacency matrix from file
    public double[][] readAdjacencyMatrix(File adjacencyMatrixFile) {
        //adjacency matric
        double[][] adjacencyMatrix = new double[numberOfNodes][numberOfNodes];
        try {
            InputStream is = new FileInputStream(adjacencyMatrixFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String values[] = new String[numberOfNodes + 1];
            String line = null;
            int counter = 0;
            br.readLine();
            while ((line = br.readLine()) != null) {
                values = line.split("\\s+");
                for (int i = 1; i < values.length; i++) {
                    adjacencyMatrix[counter][i - 1] = Double.parseDouble(values[i]);
                }
                counter++;
            }
            br.close();
        } catch (Exception e) {
            System.err.println("Error: AdjacencyMatrix File Cannot Be Read");
            e.printStackTrace();
        }
        return adjacencyMatrix;
    }

    public double[][] computeTransitionMatrix(double[][] adjacencyMatrix) {
        double[][] p = new double[numberOfNodes][numberOfNodes];
        double teleport = 0.15;
        //for all the rows
        for (int i = 0; i < numberOfNodes; i++) {
            //count how many non-zero entries
            int count = 0;
            for (int j = 0; j < numberOfNodes; j++) {
                if (adjacencyMatrix[i][j] != 0) {
                    count++;
                }
            }
            double baseP = 1.0 / count;
            for (int j = 0; j < numberOfNodes; j++) {
                if (adjacencyMatrix[i][j] == 0) {
                    p[i][j] = teleport / numberOfNodes;
                } else {
                    p[i][j] = (1 - teleport) / count + teleport / numberOfNodes;
                }
            }
            count = 0;
        }
        return p;
    }

    public double[] getNodePowerFromFile(File nodePowerFile) {
        double[] power = new double[numberOfNodes];
        try {
            InputStream is = new FileInputStream(nodePowerFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String values[] = new String[2];
            String line = null;
            int counter = 0;
            while ((line = br.readLine()) != null) {
                line.trim();
                values = line.split("\\s+");
                power[counter] = Double.parseDouble(values[1].substring(0, values[1].length()-1))/100.0;
                counter++;
            }
            br.close();
        } catch (Exception e) {
            System.err.println("Error: Power Matrix File Cannot Be Read");
            e.printStackTrace();
        }
        return power;
    }

    //for testing
    public static void main(String args[]) {
        File f = new File("data/matrix.txt");
        double[][] m = new double[4][4];
        SensorNetwork sn = new SensorNetwork();
        m = sn.computeTransitionMatrix(sn.readAdjacencyMatrix(f));
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(m[i][j] + " ");
            }
            System.out.println();
        }
        double[] v = new double[numberOfNodes];
        v = sn.computeImportanceValues(m);
        for (int i = 0; i < numberOfNodes; i++) {
            System.out.print(v[i] + " ");
        }

    }
}
