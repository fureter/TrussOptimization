/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TrussOptimization;

import Display.Canvas;
import Display.Display;
import connectjoints.data.Material;
import connectjoints.math.Vector3d;
import connectjoints.structure.Beam;
import connectjoints.structure.Joint;
import connectjoints.structure.Truss;
import java.util.ArrayList;
import optimization.Genetic;

/**
 *
 * @author Kristau5
 */
public class TrussOptimization {

    /**
     * @param args the command line arguments
     */  
    public static void main(String[] args) {
        System.setProperty("sun.awt.noerasebackground", "true");
        
        ArrayList<Material> materials = new ArrayList<>();
        
        Material wood = new Material("wood",630,130.0/630);
        wood.youngs = 1e6;
        wood.sigma = 3.2e6;
        materials.add(wood);
        
        
        double[] chances = {1.0,1.0,1.0,.5,.5,.5};
        
        //int pop, int gener, double[] chances, int maxMut, double move
        Genetic.initilizeProblem(2, 500, chances, 10, .05);
        
        
        
        Joint f1 = new Joint(wood,new Vector3d(1,1,0),new Vector3d(0,-50,0),"force",new Vector3d(.1,.1,.1));
        Joint f2 = new Joint(wood,new Vector3d(3,1,0),new Vector3d(25,0,0),"force",new Vector3d(.1,.1,.1));
        Joint b = new Joint(wood,new Vector3d(0,0,0),new Vector3d(0,0,0),"base",new Vector3d(.1,.1,.1));
        Joint b1 = new Joint(wood,new Vector3d(1.5,0,0),new Vector3d(0,0,0),"base",new Vector3d(.1,.1,.1));
        
        ArrayList<Joint> fjoints = new ArrayList<>();
        ArrayList<Joint> bjoints = new ArrayList<>();
        
        fjoints.add(f1);
        fjoints.add(f2);
        bjoints.add(b);
        bjoints.add(b1);
        
        //ArrayList<Joint> fjoints, ArrayList<Joint> bjoints, double fineness, ArrayList<Material> m
        Genetic.initilizePopulation(fjoints, bjoints, 3, materials);
        
        Genetic.simpleEvolve();
        
        
        /*
        Joint c = new Joint(wood,new Vector3d(1,0,0),new Vector3d(0,0,0),"con",new Vector3d(.1,.1,.1));

        //ArrayList<Joint> fjoints = new ArrayList<>();
        //ArrayList<Joint> bjoints = new ArrayList<>();
        ArrayList<Joint> cjoints = new ArrayList<>();
        ArrayList<Beam> beams = new ArrayList<>();
        
        fjoints.add(f1);
        bjoints.add(b);
        bjoints.add(b1);
        cjoints.add(c);
        
        Beam c1 = new Beam(wood,b,f1,.01);
        Beam c2 = new Beam(wood,b1,f1,.01);
        Beam c3 = new Beam(wood,c,f1,.01);
        Beam c4 = new Beam(wood,c,b,.01);
        Beam c5 = new Beam(wood,c,b1,.01);
        
        f1.addBeam(c3);
        c.addBeam(c3);
        c.addBeam(c4);
        c.addBeam(c5);
        b.addBeam(c4);
        b1.addBeam(c5);
        
        
        f1.addBeam(c2);
        f1.addBeam(c1);
        
        b.addBeam(c1);
        b1.addBeam(c2);
        
        beams.add(c1);
        beams.add(c2);
        beams.add(c3);
        beams.add(c4);
        beams.add(c5);
        
        Truss t = new Truss(bjoints,fjoints,cjoints,beams);
        
        t.calcStructure();
        t.printTruss();
        
        Canvas canvas = new Canvas();
        
        Display d = new Display(canvas);
        canvas.addKeyListener(canvas);
        t.drawStructure(canvas);
        */
    }   
    
}
