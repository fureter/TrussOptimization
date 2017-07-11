/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connectjoints.math;

import java.util.ArrayList;

/**
 *
 * @author Kristau5
 */
public class Vector3d {
    public double x,y,z;
    
    public Vector3d(){
        x = 0;
        y = 0;
        z = 0;
    }
    public Vector3d(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public static double dot(Vector3d a, Vector3d b){
        return (a.x*b.x+a.y*b.y+a.z+b.z);
    }
    public static Vector3d cross(Vector3d a, Vector3d b){
        Vector3d v = new Vector3d();
        v.x = (a.y*b.z - a.z*b.y);
        v.y = (a.x*b.z - a.z*b.x);
        v.z = (a.x*b.y - a.y*b.x);
        return v;
    }
    public static Vector3d summation(ArrayList<Vector3d> a){
        Vector3d v = new Vector3d();
        for(Vector3d c: a){
            v.x += c.x;
            v.y += c.y;
            v.z = c.z;
        }
        return v;
    }
    public static double magnitude(Vector3d a){
        return Math.sqrt(Math.pow(a.x,2)+Math.pow(a.y,2)+Math.pow(a.z,2));
    }
    
    public static Vector3d sub(Vector3d a, Vector3d b){
        return new Vector3d(a.x-b.x,a.y-b.y,a.z-b.z);
    }
    
    public static Vector3d normalize(Vector3d a){
        double mag = magnitude(a);
        return new Vector3d(a.x/mag,a.y/mag,a.z/mag);
    }
    
    public String toString(){
        String s = "(" + x + ", " + y + ", " + z + ")";
        return s;
    }
    
    public void add(Vector3d a){
        this.x += a.x;
        this.y += a.y;
        this.z += a.z;
    }
    
    public void multiply(double a){
        this.x *= a;
        this.y *= a;
        this.z *= a;
    }
    
    public boolean equals(Vector3d a){
        return ((a.x == this.x) && (a.y == this.y) && (a.z == this.z));
    }
}
