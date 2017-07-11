/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connectjoints.structure;

import connectjoints.data.Material;
import connectjoints.math.Vector3d;
import java.util.ArrayList;

/**
 *
 * @author Kristau5
 */
public class Joint {
    private Material material;
    private Vector3d position1;
    public Vector3d position2;
    private Vector3d force;
    private String type;
    private double crossSectionArea;
    private double volume;
    private Vector3d size;
    private double weight;
    private double cost;
    public ArrayList<Beam> beams;
    
    public static int totalJoints = 0;
    public int index;
    
    public Joint(Material m, Vector3d p, Vector3d f, String t, Vector3d s){
        material = m;
        position1 = p;
        force = f;
        type = t;
        size = s;
        volume = s.x*s.y*s.z;
        weight = material.getRho()*volume;
        cost = weight*material.getSpecificCost();
        beams = new ArrayList<>();
        index = totalJoints;
        totalJoints++;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Vector3d getPosition1() {
        return position1;
    }

    public void setPosition1(Vector3d position1) {
        this.position1 = position1;
    }

    public Vector3d getForce() {
        return force;
    }

    public void setForce(Vector3d force) {
        this.force = force;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getCrossSectionArea() {
        return crossSectionArea;
    }

    public void setCrossSectionArea(double crossSectionArea) {
        this.crossSectionArea = crossSectionArea;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public Vector3d getSize() {
        return size;
    }

    public void setSize(Vector3d size) {
        this.size = size;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
    
    public boolean hasBeam(ArrayList<Beam> beams){
        boolean b = false;
        int count = 0;
        
        for(Beam be: this.beams){
            if(beams.contains(be)){
                count++;
            }
        }
        if(count < this.beams.size()){
            b = true;
        }
        
        return b;
    }
    public void addBeam(Beam b){
        beams.add(b);
    }
    
    public Joint clone(){
        Joint j = new Joint(this.material,this.position1,this.force,this.type,this.size);
        return j;
    }
    
}
