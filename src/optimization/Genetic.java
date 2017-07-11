/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optimization;

import Display.Canvas;
import Display.Display;
import connectjoints.data.Material;
import connectjoints.math.Vector3d;
import connectjoints.structure.Beam;
import connectjoints.structure.Joint;
import connectjoints.structure.Truss;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Kristau5
 */
public class Genetic {
    private static double costWeight;
    private static double massWeight;
    
    private static double beamRemovale;
    private static double beamAddition;
    private static double jointMove;
    private static double jointRemove;
    private static double jointAddition;
    private static int maxMutation;
    
    private static int populationSize;
    private static int generations;
    
    private static double moveFactor;
    
    private static ArrayList<Truss> population;
    
    private static ArrayList<Material> mat;
    
    public static void initilizeProblem(int pop, int gener, double[] chances, int maxMut, double move){
        populationSize = pop;
        generations = gener;
        population = new ArrayList<>();
        beamRemovale = chances[0];
        beamAddition = chances[1];
        jointMove = chances[2];
        jointRemove = chances[3];
        jointAddition = chances[4];
        maxMutation = maxMut;
        moveFactor = move;
    }
    
    public static void initilizePopulation(ArrayList<Joint> fjoints, ArrayList<Joint> bjoints, double fineness, ArrayList<Material> m){
        mat = m;
        population.clear();
        for(int i = 0; i < populationSize;i++){
            population.add(new Truss(bjoints,fjoints, fineness, m));
            population.get(i).calcStructure();
        }  
    }
    
    public static double calculateFitness(Truss t){
        int factor = 1;
        if(!t.pass){
            factor = 0;
        }
        return ((1/t.cost)*costWeight + (1/t.mass)*massWeight)*factor;
    }
    
    public static void setWeights(double cost, double mass){
        costWeight = cost;
        massWeight = mass;
    }
    
    public static void simpleEvolve(){
        for(int i = 0; i < generations; i++){
            Truss t = getBest();
            population.clear();
            population = new ArrayList<>();
            for(int j = 0; j < populationSize;j++){
                population.add(t.copy());
            }
            mutate();
            calc();
        }
        Truss t = getBest();
        
        Canvas canvas = new Canvas();
        
        Display d = new Display(canvas);
        canvas.addKeyListener(canvas);
        t.drawStructure(canvas);
    }
    
    public static void evolve(){
        for(int i = 0; i < generations;i++){
            mergeBest();
        }
    }
    
    public static Truss getBest(){
        Truss parent1;
        int ind1 = 0;
        double fit1 = calculateFitness(population.get(ind1));
        for(int i = 0; i < population.size();i++){
            if(calculateFitness(population.get(i%populationSize)) > fit1){
                ind1 = i;
            }
        }
        parent1 = population.get(ind1);
        return parent1;
    }
    
    public static void mergeBest(){
        Truss parent1;
        Truss parent2;
        
        int ind1 = 0;
        int ind2 = 1;
        double fit1 = calculateFitness(population.get(ind1));
        double fit2 = calculateFitness(population.get(ind2));
        
        for(int i = 0; i < population.size();i++){
            if(calculateFitness(population.get(i%populationSize)) > fit1){
                ind2 = ind1;
                ind1 = i;
            }
        }
        parent1 = population.get(ind1);
        parent2 = population.get(ind2);
        recombination(parent1,parent2);
        mutate();
        
        
    }
    
    public static void recombination(Truss p1, Truss p2){
        population.clear();
        for(int i = 0; i < populationSize;i++){
            ArrayList<Joint> p1J = (ArrayList<Joint>)p1.joints.clone();
            ArrayList<Joint> p2J = (ArrayList<Joint>)p2.joints.clone();
            ArrayList<Beam> p1B = (ArrayList<Beam>)p1.beams.clone();
            ArrayList<Beam> p2B = (ArrayList<Beam>)p2.beams.clone();
            
            ArrayList<Beam> p1Bn = new ArrayList<>();
            ArrayList<Beam> p2Bn = new ArrayList<>();
            
            Random ran = new Random(System.currentTimeMillis());
            
            for(int j = 0; j < p1J.size()/2;j++){
                int rem = ran.nextInt(p1J.size());
                if(!(p1J.get(rem).getType().equals("force") || p1J.get(rem).getType().equals("baes"))){
                    p1J.remove(p1J.get(rem));
                }
                
            }
            for(int j = 0; j < p2J.size()/2;j++){
                int rem = ran.nextInt(p2J.size());
                if(!(p2J.get(rem).getType().equals("force") || p2J.get(rem).getType().equals("baes"))){
                    p2J.remove(p2J.get(rem));
                }
            }
            
            for(Beam b: p1B){
                if(p1J.contains(b.getJoint1()) && p1J.contains(b.getJoint2())){
                    p1Bn.add(b);
                }
            }
            for(Beam b: p2B){
                if(p2J.contains(b.getJoint1()) && p2J.contains(b.getJoint2())){
                    p2Bn.add(b);
                }
            }
        }
    }
    
    public static void mutate(){
        for(Truss t: population){
            Random ran = new Random(System.currentTimeMillis());
            int iter = ran.nextInt(maxMutation);
            
            for(int i = 0; i < iter;i++){
                double jAdd = ran.nextDouble();
                double jRev = ran.nextDouble();
                double jMov = ran.nextDouble();
                double bAdd = ran.nextDouble();
                double bRev = ran.nextDouble();

                /*
                if(jAdd < jointAddition){
                    double x = (ran.nextDouble()*(t.posMax.x-t.posMin.x))+t.posMin.x;
                    double y = (ran.nextDouble()*(t.posMax.y-t.posMin.y))+t.posMin.y;
                    double z = (ran.nextDouble()*(t.posMax.z-t.posMin.z))+t.posMin.z;
                    
                    Vector3d pos = new Vector3d(x,y,z);
                    
                    Joint j1 = new Joint(mat.get(ran.nextInt(mat.size())),pos,new Vector3d(0,0,0),"con", new Vector3d(0.01,.01,.01));
                    
                    Joint j2 = null;
                    Joint j3 = null;
                    for(Joint j: t.joints){
                        if(j2 == null){
                            j2 = j;
                        }else if(j3 == null & j2 != j){
                            j3 = j;
                        }else{
                            if(Vector3d.magnitude(Vector3d.sub(pos, j.getPosition1())) < Vector3d.magnitude(Vector3d.sub(pos, j2.getPosition1()))){
                                j3 = j2;
                                j2 = j;
                            }
                        }
                        
                    }
                    Beam b1 = new Beam(mat.get(ran.nextInt(mat.size())),j1,j2,.01);
                    Beam b2 = new Beam(mat.get(ran.nextInt(mat.size())),j1,j3,.01);
                    
                    j1.addBeam(b2);
                    j1.addBeam(b1);
                    j2.addBeam(b1);
                    j3.addBeam(b2);
                    
                    t.beams.add(b2);
                    t.beams.add(b1);
                }
                */
                
                if(jRev < jointRemove){
                    Joint j = t.joints.get(ran.nextInt(t.joints.size()));
                    if(!(j.getType().equals("force") || j.getType().equals("base"))){
                        ArrayList<Beam> b = j.beams;
                        for(Beam be:b){
                            Joint k = be.getOtherJoint(j);
                            k.beams.remove(be);
                            t.beams.remove(be);
                        }
                        t.joints.remove(j);
                    }
                        
                }
                
                if(jMov < jointMove){
                    Joint j = t.joints.get(ran.nextInt(t.joints.size()));
                    if(!(j.getType().equals("force") || j.getType().equals("base"))){
                        double xChange = (ran.nextDouble()-.5)*moveFactor;
                        double yChange = (ran.nextDouble()-.5)*moveFactor;
                        double zChange = (ran.nextDouble()-.5)*moveFactor;
                        
                        j.getPosition1().x += xChange;
                        j.getPosition1().y += yChange;
                        j.getPosition1().z += zChange;
                    }
                }
                if(bAdd < beamAddition){

                }
                if(bRev < beamRemovale){

                }
            }
        }
    }
    
    public static void calc(){
        for(Truss t:population){
            t.calcStructure();
        }
    }
}
