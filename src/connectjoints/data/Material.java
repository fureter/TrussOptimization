/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connectjoints.data;

/**
 *
 * @author Kristau5
 */
public class Material {
    private String name;
    private double rho;
    private double specificCost;
    private double[][] stressLimit = new double[3][3];
    public double youngs;
    public double sigma;

    public Material(String name, double rho, double specificCost) {
        this.name = name;
        this.rho = rho;
        this.specificCost = specificCost;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRho() {
        return rho;
    }

    public void setRho(double rho) {
        this.rho = rho;
    }

    public double getSpecificCost() {
        return specificCost;
    }

    public void setSpecificCost(double specificCost) {
        this.specificCost = specificCost;
    }

    public double[][] getStressLimit() {
        return stressLimit;
    }

    public void setStressLimit(double[][] stressLimit) {
        this.stressLimit = stressLimit;
    }
    
}
