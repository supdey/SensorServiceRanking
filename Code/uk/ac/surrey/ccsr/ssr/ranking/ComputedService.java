/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ccsr.ssr.ranking;

/**
 *
 * @author ww0004
 */
public class ComputedService {

    private String url;
    private int sensorNodeID;
    private double importance;
    private double power;
    private int[] bestPathFromGateway;
    private int[] bestPathToGateway;
    private double queryCost;
    private double linkQualityFromGateway;
    private double linkQualityToGateway;

    public ComputedService() {
        this.url = "";
        this.sensorNodeID = 0;
        this.importance = 0.0;
        this.power = 0.0;
        /*for (int i = 0; i < this.bestPathFromGateway.length; i++) {
            this.bestPathFromGateway[i] = 0;
        }
        for (int i = 0; i < this.bestPathToGateway.length; i++) {
            this.bestPathToGateway[i] = 0;
        }*/
        this.queryCost = 0.0;
        this.linkQualityFromGateway = 0.0;
        this.linkQualityToGateway = 0.0;
    }

    public String getURL() {
        return url;
    }

    public int getSensorNodeID() {
        return sensorNodeID;
    }

    public double getImportance() {
        return importance;
    }

    public double getPower() {
        return power;
    }

    public int[] getBestPathFromGateway() {
        return bestPathFromGateway;
    }

    public int[] getBestPathToGateway() {
        return bestPathToGateway;
    }
    
    public double getQueryCost(){
        return queryCost;
    }
    
    public double getLinkQualityFromGateway(){
        return linkQualityFromGateway;
    }
    
    public double getLinkQualityToGateway(){
        return linkQualityToGateway;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public void setSensorNodeID(int sensorNodeID) {
        this.sensorNodeID = sensorNodeID;
    }

    public void setImportance(double importance) {
        this.importance = importance;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public void setBestPathFromGateway(int[] bestPathFromGateway) {
        this.bestPathFromGateway = bestPathFromGateway;
    }

    public void setBestPathToGateway(int[] bestPathToGateway) {
        this.bestPathToGateway = bestPathToGateway;
    }
    
    public void setQueryCost(double queryCost){
        this.queryCost = queryCost;
    }
    
    public void setLinkQualityFromGateway(double linkQualityFromGateway){
        this.linkQualityFromGateway = linkQualityFromGateway;
    }
    
    public void setLinkQualityToGateway(double linkQualityToGateway){
        this.linkQualityToGateway = linkQualityToGateway;
    }
}
