/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connectjoints.structure;

import connectjoints.data.Material;
import connectjoints.math.Vector3d;

/**
 *
 * @author Kristau5
 */
public class Beam {
    private Material material;
    private Joint joint1;
    private Joint joint2;
    private double force;
    private double radius;
    private double length;
    public double strain;
    private double weight;

    public double sigma;
    public double tau;
    public boolean broke;
    
    public Beam(Material m, Joint a, Joint b,double r){
        material = m;
        joint1 = a;
        joint2 = b;
        length = Vector3d.magnitude(Vector3d.sub(a.getPosition1(),b.getPosition1()));
        radius = r;
        weight = (material.getRho()*(length*Math.PI*Math.pow(r, 2)));
        force = 0;
        broke = false;
    }
    
    public Joint getOtherJoint(Joint j){
        if(joint1 == j){
            return joint2;
        }else{
            return joint1;
        }
    }
    
    public void calcStress(){
        double area = Math.PI*Math.pow(radius, 2);
        sigma = force/area;
        
        if(sigma > material.getStressLimit()[0][0]){
            broke = true;
        }
    }

    public double getForce() {
        return force;
    }

    public void setForce(double force) {
        this.force = force;
    }
    

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Joint getJoint1() {
        return joint1;
    }

    public void setJoint1(Joint joint1) {
        this.joint1 = joint1;
    }

    public Joint getJoint2() {
        return joint2;
    }

    public void setJoint2(Joint joint2) {
        this.joint2 = joint2;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
    
}
