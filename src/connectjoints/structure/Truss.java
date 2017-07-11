/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connectjoints.structure;

import Display.Canvas;
import connectjoints.data.Material;
import connectjoints.math.Vector3d;
import java.util.ArrayList;
import java.util.Random;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;

/**
 *
 * @author Kristau5
 */
public class Truss {
    public ArrayList<Joint> joints;
    public ArrayList<Beam> beams;
    public double cost;
    public double mass;
    public boolean pass;
    public Vector3d posMax;
    public Vector3d posMin;
    
    public Truss(ArrayList<Joint> bjoints, ArrayList<Joint> fjoints,double fineness,ArrayList<Material> m){
        joints = new ArrayList<>();
        beams = new ArrayList<>();
        Vector3d posMin = new Vector3d();
        Vector3d posMax = new Vector3d();
        
        for(Joint j:bjoints){
            joints.add(j);
            if(posMin.x > j.getPosition1().x){
                posMin.x = j.getPosition1().x;
            }
            if(posMin.y > j.getPosition1().y){
                posMin.y = j.getPosition1().y;
            }
            if(posMin.z > j.getPosition1().z){
                posMin.z = j.getPosition1().z;
            }
            if(posMax.x < j.getPosition1().x){
                posMax.x = j.getPosition1().x;
            }
            if(posMax.y < j.getPosition1().y){
                posMax.y = j.getPosition1().y;
            }
            if(posMax.z < j.getPosition1().z){
                posMax.z = j.getPosition1().z;
            }
        }
        for(Joint j:fjoints){
            joints.add(j);
            if(posMin.x > j.getPosition1().x){
                posMin.x = j.getPosition1().x;
            }
            if(posMin.y > j.getPosition1().y){
                posMin.y = j.getPosition1().y;
            }
            if(posMin.z > j.getPosition1().z){
                posMin.z = j.getPosition1().z;
            }
            if(posMax.x < j.getPosition1().x){
                posMax.x = j.getPosition1().x;
            }
            if(posMax.y < j.getPosition1().y){
                posMax.y = j.getPosition1().y;
            }
            if(posMax.z < j.getPosition1().z){
                posMax.z = j.getPosition1().z;
            }
        }
        
        double xInc = (posMax.x - posMin.x)/fineness;
        double yInc = (posMax.y - posMin.y)/fineness;
        double zInc = (posMax.z - posMin.z)/fineness;
        
        System.out.println(xInc + " " + yInc + " " + zInc);
        
        this.posMin = posMin;
        this.posMax = posMax;
        
        for(double x = posMin.x+xInc; x < posMax.x; x+=xInc){
            for(double y = posMin.y+yInc; y < posMax.y; y+=yInc){
                if(!(zInc <=0.0)){
                    for(double z = posMin.z+zInc; z < posMax.z; z+=zInc){
                        Vector3d pos = new Vector3d(x,y,z);
                        Joint j = new Joint(m.get(0),pos,new Vector3d(0,0,0),"con",new Vector3d(.1,.1,.1));
                        joints.add(j);
                    }
                }else{
                    Vector3d pos = new Vector3d(x,y,joints.get(0).getPosition1().z);
                    Joint j = new Joint(m.get(0),pos,new Vector3d(0,0,0),"con",new Vector3d(.1,.1,.1));
                    joints.add(j);
                }
            }
        }
        
        for(int i = 0; i < joints.size();i++){
            for(int j = i+1; j < joints.size();j++){
                Joint j1 = joints.get(i);
                Joint j2 = joints.get(j);
                Beam b = new Beam(m.get(0),j1,j2,.01);
                j1.addBeam(b);
                j2.addBeam(b);
                beams.add(b);
            }
        }
    }
    
    public Truss(ArrayList<Joint> base, ArrayList<Joint> force, ArrayList<Joint> sup, ArrayList<Beam> beam){
        joints = new ArrayList<>();
        beams = new ArrayList<>();
        for(Joint j: base){
            joints.add(j);
        }
        for(Joint j: force){
            joints.add(j);
        }
        for(Joint j: sup){
            joints.add(j);
        }
        for(Beam b: beam){
            this.beams.add(b);
        }
    }
    
    public Truss(ArrayList<Joint> baseJoints, ArrayList<Joint> forceJoints, ArrayList<Material> mat,double xBound, double yBound, double zBound,double radiusLimit){
        Random r = new Random();
        int countJ = 0;
        int countB = 0;
        joints = new ArrayList<>();
        beams = new ArrayList<>();
        for(Joint j:baseJoints){
            joints.add(j);
        }
        for(Joint j:forceJoints){
            joints.add(j);
        }
        while(!connected(forceJoints)){
            int ran = r.nextInt();
            if(ran%2 == 0){
                Joint j = new Joint(mat.get(r.nextInt(mat.size())),new Vector3d(r.nextDouble()*xBound,
                        r.nextDouble()*yBound,r.nextDouble()*zBound),new Vector3d(),"con",new Vector3d(.1,.1,.1));
                Joint temp = joints.get(r.nextInt(joints.size()));
                joints.add(j);
                Beam b = new Beam(mat.get(r.nextInt(mat.size())),j,temp,r.nextDouble()*radiusLimit);
                j.addBeam(b);
                temp.addBeam(b);
                countJ++;
                System.out.println("added Joint: " + countJ);
            }else{
                if(joints.size()>=2){
                    int ran1 = r.nextInt(joints.size());
                    int ran2 = r.nextInt(joints.size());
                    while(ran1==ran2){
                        ran2 = r.nextInt(joints.size());
                    }
                    Beam b = new Beam(mat.get(r.nextInt(mat.size())),joints.get(ran1),joints.get(ran2),r.nextDouble()*radiusLimit);
                    beams.add(b);
                    joints.get(ran2).addBeam(b);
                    joints.get(ran1).addBeam(b);
                    countB++;
                    System.out.println("Added Beam: " + countB);
                }
            }
        }
        removeSingleJoints();
    }
    
    public boolean connected(ArrayList<Joint> force){
        int counter = 0;
        for(Joint f:force){
            if(checkJointToBase(f)){
                counter++;
            }
        }
        return (counter == force.size());
    }
    
    public void removeSingleJoints(){
        ArrayList<Joint> joins = new ArrayList<>();
        for(Joint j: joints){
            if(j.beams.size() <= 1 && !j.getType().equals("base") && !j.getType().equals("force")){
                joins.add(j);
            }
        }
        for(Joint j:joins){
            joints.remove(j);
        }
        ArrayList<Beam> bem = new ArrayList<>();
        for(Beam b: beams){
            if(!joints.contains(b.getJoint1()) || !joints.contains(b.getJoint2())){
                bem.add(b);
            }
        }
        for(Beam b:bem){
            beams.remove(b);
        }
    }
    
    public boolean checkJointToBase(Joint f){
        boolean base = false;
        ArrayList<Joint> jo = getAllConnectedJoints(f,new ArrayList<Joint>(), new ArrayList<Beam>());
        for(Joint j : jo){
            if(j.getType().equals("base")){
                base = true;
            }
        }
        return base;
    }
    
    public ArrayList<Joint> getAllConnectedJoints(Joint f, ArrayList<Joint> jo, ArrayList<Beam> be){
        ArrayList<Beam> b = f.beams;
        if(!f.hasBeam(be)){
            return jo;
        }
        for(Beam a:b){
            if(!be.contains(a)){
                Joint j = a.getOtherJoint(f);
                if(!jo.contains(j)){
                    jo.add(j);
                    be.add(a);
                    return getAllConnectedJoints(j,jo,be);
                }
            }
        }
        return jo;
    }
    
    public void calcPass(){
        boolean p = true;
        for(Beam b:beams){
            b.calcStress();
            if(b.broke){
                pass = false;
            }
        }
        pass = p;
    }
    
    public void calcStructure(){
        DMatrixRMaj globalStiffness = new DMatrixRMaj(Joint.totalJoints*3,Joint.totalJoints*3);
        DMatrixRMaj displacement = new DMatrixRMaj(Joint.totalJoints*3,1);
        DMatrixRMaj forces = new DMatrixRMaj(Joint.totalJoints*3,1);
        
        ArrayList<Integer> dofS = new ArrayList<>();
        ArrayList<Integer> disS = new ArrayList<>();
        
        globalStiffness.zero();     
        for(Beam b:beams){
            //System.out.println(joints.size() + " " + beams.size());
            
            int ind1 = b.getJoint1().index;
            int ind2 = b.getJoint2().index;
            
            double x = (b.getJoint2().getPosition1().x - b.getJoint1().getPosition1().x)/b.getLength();
            double y = (b.getJoint2().getPosition1().y - b.getJoint1().getPosition1().y)/b.getLength();
            double z = (b.getJoint2().getPosition1().z - b.getJoint1().getPosition1().z)/b.getLength();
            double AE = b.getMaterial().youngs*Math.pow(b.getRadius(),2)*Math.PI;
            double L = b.getLength();
            
            for(int i = 0; i < 3; i++){
                double column;
                
                if(i == 0){
                    column = x;
                }else if (i == 1){
                    column = y;
                }else{
                    column = z;
                }
                
                double k1 = x*column*(AE/L); double k2 = -x*column*(AE/L);
                double k3 = y*column*(AE/L); double k4 = -y*column*(AE/L);
                double k5 = z*column*(AE/L); double k6 = -z*column*(AE/L);
                
                double k7 = -x*column*(AE/L); double k8 = x*column*(AE/L);
                double k9 = -y*column*(AE/L); double k10 = y*column*(AE/L);
                double k11 = -z*column*(AE/L); double k12 = z*column*(AE/L);
                
                //System.out.println("K1: " + k1 + "K2: " + k2 + "K3: " + k3 + "K4: " + k4 +
                //        "K5: " + k5 + "K6: " + k6 +
                //        "K7: " + k7 + "K8: " + k8 +
                //        "K9: " + k9 + "K10: " + k10 +
                //        "K11: " + k11 + "K12: " + k12 + " AE/L " + L);
                
                // All of the Entries with Joint 1 values as both indices
                globalStiffness.add(ind1*3,ind1*3+i,k1);
                globalStiffness.add(ind1*3+1,ind1*3+i,k3);
                globalStiffness.add(ind1*3+2,ind1*3+i,k5);
                // All of the Entries with Joint 2 values as both indices
                globalStiffness.add(ind2*3,ind2*3+i,k8);
                globalStiffness.add(ind2*3+1,ind2*3+i,k10);
                globalStiffness.add(ind2*3+2,ind2*3+i,k12);
                // All of the Entries with Joint 1 values as row and Joint 2 as Column
                globalStiffness.add(ind1*3,ind2*3+i,k2);
                globalStiffness.add(ind1*3+1,ind2*3+i,k4);
                globalStiffness.add(ind1*3+2,ind2*3+i,k6);
                // All of the Entries with Joint 2 values as row and Joint 1 as Column
                globalStiffness.add(ind2*3,ind1*3+i,k7);
                globalStiffness.add(ind2*3+1,ind1*3+i,k9);
                globalStiffness.add(ind2*3+2,ind1*3+i,k11); 
            }
            
        }
        for(Joint j:joints){
            if(j.getType().equals("force")){
                int ind = j.index;
                forces.set(ind*3,0,j.getForce().x);
                forces.set(ind*3+1,0,j.getForce().y);
                forces.set(ind*3+2,0,j.getForce().z);
                dofS.add(ind*3);
                dofS.add(ind*3+1);
                dofS.add(ind*3+2);
            }else if(j.getType().equals("base")){
                int ind = j.index;
                displacement.set(ind*3,0,0);
                displacement.set(ind*3+1,0,0);
                displacement.set(ind*3+2,0,0);
                disS.add(ind*3);
                disS.add(ind*3+1);
                disS.add(ind*3+2);
            }else{
                int ind = j.index;
                forces.set(ind*3,0,0);
                forces.set(ind*3+1,0,0);
                forces.set(ind*3+2,0,0);
                dofS.add(ind*3);
                dofS.add(ind*3+1);
                dofS.add(ind*3+2);
            }
        }
        
        DMatrixRMaj Kqq = new DMatrixRMaj(dofS.size(),dofS.size());
        DMatrixRMaj Kqr = new DMatrixRMaj(dofS.size(),disS.size());
        DMatrixRMaj Krq = new DMatrixRMaj(disS.size(),dofS.size());
        DMatrixRMaj Krr = new DMatrixRMaj(disS.size(),disS.size());
        
        for(int i = 0; i < dofS.size();i++){
            for(int j = 0; j < dofS.size();j++){
                Kqq.set(i,j,globalStiffness.get(dofS.get(i),dofS.get(j)));
            }
        }
        
        for(int i = 0; i < disS.size();i++){
            for(int j = 0; j < disS.size();j++){
                Krr.set(i,j,globalStiffness.get(disS.get(i),disS.get(j)));
            }
        }
        
        for(int i = 0; i < dofS.size(); i++){
            for(int j = 0; j < disS.size();j++){
                Kqr.set(i,j,globalStiffness.get(dofS.get(i),disS.get(j)));
            }
        }
        
        for(int i = 0; i < disS.size(); i++){
            for(int j = 0; j < dofS.size();j++){
                Krq.set(i,j,globalStiffness.get(disS.get(i),dofS.get(j)));
            }
        }
        
        DMatrixRMaj Dq = new DMatrixRMaj(dofS.size(),1);
        DMatrixRMaj Dr = new DMatrixRMaj(disS.size(),1);
        DMatrixRMaj Pq = new DMatrixRMaj(dofS.size(),1);
        DMatrixRMaj Rr = new DMatrixRMaj(disS.size(),1);
        
        for(int i = 0; i < disS.size();i++){
            Dr.set(i,0,displacement.get(disS.get(i),0));
        }
        for(int i = 0; i < dofS.size();i++){
            Pq.set(i,0,forces.get(dofS.get(i),0));
        }
        //System.out.println(globalStiffness.toString());
        CommonOps_DDRM.pinv(Kqq, Kqq);
        DMatrixRMaj KqrDr = new DMatrixRMaj(dofS.size(),1);
        DMatrixRMaj subed = new DMatrixRMaj(dofS.size(),1);
        CommonOps_DDRM.mult(Kqr, Dr, KqrDr);
        CommonOps_DDRM.subtract(Pq, KqrDr, subed);
        CommonOps_DDRM.mult(Kqq,subed,Dq);
        
        for(int i = 0; i < Dq.numRows;i++){
            displacement.set(dofS.get(i),0,Dq.get(i,0));
        }
        DMatrixRMaj KrqDq = new DMatrixRMaj(disS.size(),1);
        DMatrixRMaj KrrDr = new DMatrixRMaj(disS.size(),1);
        CommonOps_DDRM.mult(Krq, Dq, KrqDq);
        CommonOps_DDRM.mult(Krr, Dr, KrrDr);
        CommonOps_DDRM.add(KrqDq, KrrDr, Rr);
        
        for(int i = 0; i < Rr.numRows;i++){
            forces.set(disS.get(i),0,Rr.get(i,0));
        }
        for(Joint j:joints){
            int ind = j.index;
            Vector3d pos = j.getPosition1();
            Vector3d position2 = new Vector3d(pos.x+displacement.get(ind*3,0),pos.y+displacement.get(ind*3+1,0),pos.z+displacement.get(ind*3+2,0));
            Vector3d result = new Vector3d(forces.get(ind*3, 0),forces.get(ind*3+1, 0),forces.get(ind*3+2, 0));
            j.position2 = position2;
            j.setForce(result);
        }
        for(Beam b:beams){
            if(!(joints.contains(b.getJoint1()) || joints.contains(b.getJoint2()))){
                //System.out.println(":: Hypo Correct ::");
            }
            double newLength = Vector3d.magnitude(Vector3d.sub(b.getJoint1().position2, b.getJoint2().position2));
            b.strain =  (newLength-b.getLength())/b.getLength();
            b.setForce(b.strain*b.getMaterial().youngs);
        }
        
        //System.out.println(forces.toString());
        //System.out.println(globalStiffness.toString());
        //System.out.println(displacement.toString());
        
        //System.out.println(Kqq.toString());
        //System.out.println(Krr.toString());
        //System.out.println(Kqr.toString());
        //System.out.println(Krq.toString());
        //System.out.println(Dq.toString());
        //System.out.println(Rr.toString());
        
        //globalStiffness = null;
        //forces = null;
        //displacement = null;
        //Kqq = null;
        //Kqr = null;
        //Krq = null;
        //Krr = null;
        //Dr = null;
        //Dq = null;
        
        
        calcPass();
        calcCost();
        calcWeight();
    }
    
    public void calcCost(){
        double c = 0.0;
        for(Beam b: beams){
            c += b.getWeight()*b.getMaterial().getSpecificCost();
        }
        for(Joint j: joints){
            c += j.getCost();
        }
        //System.out.println("::" + c);
        cost = c;
    }
    
    public void calcWeight(){
        double w = 0;
        for(Beam b:beams){
            w += b.getWeight();
        }
        for(Joint j: joints){
            w += j.getWeight();
        }
        mass = w;
    }
    
    public void printTruss(){
        for(Joint a: joints){
            System.out.println("Joint: Position" + a.getPosition1());
        }
        for(Beam b : beams){
            System.out.println("Beam " + beams.indexOf(b) + " Stress Fraction: " + b.sigma/b.getMaterial().youngs);
        }
        System.out.println("Mass: " + mass + " Cost: " + cost + " Pass: " + pass);
    }
    
    public void drawStructure(Canvas c){
        c.addBeams(beams);
        c.addJoints(joints);
    }
    
    public Truss copy(){
        ArrayList<Joint> fjoints =  new ArrayList<>();
        ArrayList<Joint> bjoints =  new ArrayList<>();
        ArrayList<Joint> cjoints = new ArrayList<>();
        ArrayList<Beam> beams = new ArrayList<>();
        
        int count = 0;
        
        for(Beam b: this.beams){
            Joint j1 = b.getJoint1().clone();
            Joint j2 = b.getJoint2().clone();
            
            if(isPresent(j1,fjoints)){
                j1 = getFromPos(j1,fjoints);
            }
            if(isPresent(j1,bjoints)){
                j1 = getFromPos(j1,bjoints);
            }
            if(isPresent(j1,cjoints)){
                j1 = getFromPos(j1,cjoints);
            }
            if(isPresent(j2,fjoints)){
                j2 = getFromPos(j2,fjoints);
            }
            if(isPresent(j2,bjoints)){
                j2 = getFromPos(j2,bjoints);
            }
            if(isPresent(j2,cjoints)){
                j2 = getFromPos(j2,cjoints);
            }
            
            Beam c = new Beam(b.getMaterial(),j1,j2,b.getRadius());
            
            j1.addBeam(c);
            j2.addBeam(c);
            beams.add(c);
            
            String j1t = j1.getType();
            String j2t = j2.getType();
            
            switch (j1t){
                case("force"):{
                    if(!fjoints.contains(j1)){
                        fjoints.add(j1);
                    }
                }
                case("base"):{
                    if(!bjoints.contains(j1)){
                        bjoints.add(j1);
                    }
                }
                case("con"):{
                    if(!cjoints.contains(j1)){
                        cjoints.add(j1);
                    }
                }
            }
            switch (j2t){
                case("force"):{
                    if(!fjoints.contains(j2)){
                        fjoints.add(j2);
                    }
                }
                case("base"):{
                    if(!bjoints.contains(j2)){
                        bjoints.add(j2);
                    }
                }
                case("con"):{
                    if(!cjoints.contains(j2)){
                        cjoints.add(j2);
                    }
                }
            }
        }
        
        for(Joint i:fjoints){
            i.index = count;
            count++;
        }
        for(Joint i:bjoints){
            i.index = count;
            count++;
        }
        for(Joint i:cjoints){
            i.index = count;
            count++;
        }
        
        Truss t = new Truss(bjoints,fjoints,cjoints,beams);
        t.posMax = new Vector3d(this.posMax.x,this.posMax.y,this.posMax.z);
        t.posMin = new Vector3d(this.posMin.x,this.posMin.y,this.posMin.z);
        return t;
    }
    
    public Joint getFromPos(Joint j, ArrayList<Joint> joints){
        for(Joint i:joints){
            if(i.getPosition1().equals(j.getPosition1())){
                return i;
            }
        }
        return null;
    }
    
    public boolean isPresent(Joint j, ArrayList<Joint> joints){
        boolean pres = false;
        for(Joint i:joints){
            if(i.getPosition1().equals(j.getPosition1())){
                pres = true;
            }
        }
        return pres;
    }
}
