/*
 * NDCG.java
 *
 * Created on January 15, 2009, 7:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package uk.ac.surrey.ccsr.ssr.ranking;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.Arrays;

/**
 *
 * @author eyx6ww
 */
public class NDCG {
    
    /** Creates a new instance of NDCG */
    public NDCG() {
    }
    
    public static void main(String args[]) throws Exception{
        //read the precision file
        BufferedReader br = new BufferedReader(new FileReader(new File("data/ndcgData.txt")));
        //get the length of the file
        int num = 0;
        while(br.readLine() != null){
            num++;
        }
        br.close();
        System.out.println("there are " + num + " services to be evaluated");
        
        //read the precision values in the file into an array
        double[] precision = new double[num];
        String item = "";
        num = 0;
        br = new BufferedReader(new FileReader(new File("data/ndcgData.txt")));
        while((item = br.readLine()) != null){
            precision[num] = Double.parseDouble(item);
            num++;            
        }
        br.close();
        
        double[] temp = new double[precision.length];
        for(int i=0; i<precision.length; i++){
            temp[i] = precision[i];
        }
        
        Arrays.sort(temp);
        
        double[] precisionIdeal = new double[precision.length];
        for(int i=0; i<precisionIdeal.length; i++){
            precisionIdeal[i] = temp[temp.length-i-1];
        }
        
        //generate the precision values, generate values for every 5 documents
        double sum = 0.0;
        double sumIdeal = 0.0;
        for(int i=0; i<precision.length; i++){
            for(int j=0; j<(i+1); j++){
                //calculate the cumulative gain
                //System.out.println((Math.pow(2, precision[j])-1)/(Math.log((double)(j+2))));
                sum += (Math.pow(2, precision[j])-1)/(Math.log((double)(j+2)));
                sumIdeal += (Math.pow(2, precisionIdeal[j])-1)/(Math.log(j+2));
            }
            System.out.print(getShortDouble(sum/sumIdeal) + "\t");
            sum = 0.0;
            sumIdeal = 0.0;
        }
        
    }
    
    public static double getShortDouble(double d){
        int decimalPlace = 4;
        BigDecimal bd = new BigDecimal(d);
        bd = bd.setScale(decimalPlace,BigDecimal.ROUND_UP);
        d = bd.doubleValue();
        return d;
    }
    
}
