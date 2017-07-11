/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Display;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.universe.SimpleUniverse;
import connectjoints.math.Vector3d;
import connectjoints.structure.Beam;
import connectjoints.structure.Joint;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Material;
import javax.media.j3d.PointLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 *
 * @author Kristau5
 */
public class Canvas extends JPanel implements KeyListener{
    
    public Canvas3D canvas;
    public SimpleUniverse universe;
    public BranchGroup contents;
    public TransformGroup structure;
    public TransformGroup world;
    
    public Vector3f po;
    public Vector3f rot;
    public Canvas(){
        po = new Vector3f(0f,0f,0f);
        rot = new Vector3f(0f,0f,0f);
        setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        canvas = new Canvas3D(config);
        contents = new BranchGroup();
        contents.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND );
        contents.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        contents.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        structure = new TransformGroup();
        
        PointLight light = new PointLight(new Color3f(Color.white),new Point3f(0f,10f,01f), new Point3f(1f,.1f,.01f));
        TransformGroup lights = new TransformGroup();
        Transform3D lightPos = new Transform3D();
        light.setInfluencingBounds(new BoundingSphere());
        lights.setTransform(lightPos);
        lights.addChild(light);
        contents.addChild(lights);
        
        world = new TransformGroup();
        world.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        Transform3D ini = new Transform3D();
        ini.setTranslation(po);
        world.setTransform(ini);
        
        structure.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND );
        structure.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        structure.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        world.addChild(structure);
        contents.addChild(world);
        universe = new SimpleUniverse(canvas);
        universe.getViewingPlatform().setNominalViewingTransform();
        universe.addBranchGraph(contents);
        setSize(new Dimension(1200,800));
        this.add(BorderLayout.CENTER, canvas);
        setFocusable(true);
        setVisible(true);
        
    }
    
    public void addBeams(ArrayList<Beam> b){
        
        //LineAttributes line = new LineAttributes();
        //line.setLineAntialiasingEnable(true);
        
        //RenderingAttributes rend = new RenderingAttributes();
        //rend.setDepthBufferEnable(true);
        
        //mat.setLightingEnable(true);
        //mat.setDiffuseColor(new Color3f(Color.lightGray));
        //mat.setSpecularColor(new Color3f(Color.WHITE));
        
        
        //app.setRenderingAttributes(rend);
        //app.setLineAttributes(line);
        for(Beam a:b){
            Appearance app = new Appearance();
            Material mat = new Material(new Color3f(Color.LIGHT_GRAY),new Color3f(Color.LIGHT_GRAY),new Color3f(Color.LIGHT_GRAY), new Color3f(Color.LIGHT_GRAY), .1f);
            mat.setCapability(Material.ALLOW_COMPONENT_WRITE);
            app.setMaterial(mat);
            
            mat.setShininess(.4f);
            
            double fraction = Math.abs(a.sigma/a.getMaterial().sigma);
            Color3f col = new Color3f((float)(255*fraction),(float)(255*(1-fraction)),0f);
            System.out.println(col.toString());
            mat.setAmbientColor(col);
            mat.setDiffuseColor(col);
            //mat.setEmissiveColor(col);
            mat.setSpecularColor(col);
            
            BranchGroup gp = new BranchGroup();
            Cylinder c = new Cylinder((float)a.getRadius(),(float)(a.getLength()+a.strain*a.getLength()));
            
            
            c.setAppearance(app);
            Vector3d posi = a.getJoint1().position2;
            Vector3d posi2 = a.getJoint2().position2;
            Vector3f pos = new Vector3f((float)(posi.x+posi2.x)/2,(float)(posi.y+posi2.y)/2,(float)(posi.z+posi2.z)/2);
            Transform3D t = new Transform3D();
            
            Vector3f yaxis = new Vector3f(0f,1f,0f);
            Vector3f axis = new Vector3f();
            Vector3d dir = Vector3d.sub(a.getJoint2().position2,a.getJoint1().position2);
            Vector3f direction = new Vector3f((float)dir.x,(float)dir.y,(float)dir.z);
            axis.cross(yaxis,direction);
            axis.normalize();
            
            if(Float.isNaN(axis.x) && Float.isNaN(axis.y) && Float.isNaN(axis.z)){
                axis.x = 1f;
                axis.y = 0f;
                axis.z = 0f;
            }
            
            float angleX = yaxis.angle(direction);
            float aQ = axis.x * (float)Math.sin(angleX/2f);
            float bQ = axis.y * (float)Math.sin(angleX/2f);
            float cQ = axis.z * (float)Math.sin(angleX/2f);
            float dQ = (float)Math.cos(angleX/2f);
            Quat4f quat = new Quat4f(aQ,bQ,cQ,dQ);
            
            t.set(quat);
            
            t.setTranslation(pos);
            TransformGroup tg = new TransformGroup();
            tg.setTransform(t);
            tg.addChild(c);
            gp.addChild(tg);
            structure.addChild(gp);
            universe.getViewingPlatform().setNominalViewingTransform();
            
        }
    }
    public void addJoints(ArrayList<Joint> j){
        for(Joint a: j){
            Appearance app = new Appearance();
            Material mat = new Material(new Color3f(Color.LIGHT_GRAY),new Color3f(Color.LIGHT_GRAY),new Color3f(Color.LIGHT_GRAY), new Color3f(Color.LIGHT_GRAY), .1f);
            mat.setCapability(Material.ALLOW_COMPONENT_WRITE);
            app.setMaterial(mat);
            System.out.println("Rendering Joint");
            BranchGroup gp = new BranchGroup();
            if(a.getType().equals("base")){
                app.getMaterial().setDiffuseColor(new Color3f(Color.BLUE));
                app.getMaterial().setAmbientColor(new Color3f(Color.BLUE));
                app.getMaterial().setSpecularColor(new Color3f(Color.BLUE));
                //app.getMaterial().setEmissiveColor(new Color3f(Color.BLUE));
            }
            if(a.getType().equals("force")){
                app.getMaterial().setDiffuseColor(new Color3f(Color.RED));
                app.getMaterial().setAmbientColor(new Color3f(Color.RED));
                app.getMaterial().setSpecularColor(new Color3f(Color.RED));
                //app.getMaterial().setEmissiveColor(new Color3f(Color.RED));
            }
            if(a.getType().equals("con")){
                app.getMaterial().setDiffuseColor(new Color3f(Color.GREEN));
                app.getMaterial().setAmbientColor(new Color3f(Color.GREEN));
                app.getMaterial().setSpecularColor(new Color3f(Color.GREEN));
                //app.getMaterial().setEmissiveColor(new Color3f(Color.GREEN));
            }
            Box v = new Box(.015f, .015f, .015f, app);
            Transform3D t = new Transform3D();
            Vector3d vec = a.position2;
            Vector3f position = new Vector3f((float)vec.x,(float)vec.y,(float)vec.z);
            TransformGroup g = new TransformGroup();
            t.setTranslation(position);
            g.setTransform(t);
            g.addChild(v);
            gp.addChild(g);
            structure.addChild(gp);
        }
    }
    
    public void keyPressed(KeyEvent e) {
    if (e.getKeyChar()=='d'){
        po.x -=.1f;
    }
    if (e.getKeyChar()=='a'){
        po.x += .1f;
    }
    if (e.getKeyChar()=='w'){
        po.y -=.1f;
    }
    if (e.getKeyChar()=='s'){
        po.y += .1f;
    }
    if (e.getKeyChar()=='q'){
        po.z -=.1f;
    }
    if (e.getKeyChar()=='e'){
        po.z += .1f;
    }
    if(e.getKeyCode()== KeyEvent.VK_UP){
        rot.z += .1f;
    }
    if(e.getKeyCode()== KeyEvent.VK_DOWN){
        rot.z -= .1f;
    }
    if(e.getKeyCode()== KeyEvent.VK_R){
        rot.y += .1f;
    }
    if(e.getKeyCode()== KeyEvent.VK_F){
        rot.y -= .1f;
    }
    if(e.getKeyCode()== KeyEvent.VK_T){
        rot.x += .1f;
    }
    if(e.getKeyCode()== KeyEvent.VK_G){
        rot.x -= .1f;
    }
    Transform3D ren = new Transform3D();
    ren.setTranslation(po);
    
            
    float alpha = rot.x;
    float beta = rot.y;
    float gamma = rot.x;
    
    Matrix3f rotAlpha = new Matrix3f(1f,0f,0f,0f, (float)Math.cos(alpha),(float)Math.sin(alpha),0f,(float)-Math.sin(alpha), (float)Math.cos(alpha));
    Matrix3f rotBeta = new Matrix3f((float)Math.cos(beta),0f,(float)-Math.sin(beta),0f,1f,0f,(float)Math.sin(beta),0f,(float)Math.cos(beta));
    Matrix3f rotGamma = new Matrix3f((float)Math.cos(gamma),(float)Math.sin(gamma),0f,(float)-Math.sin(gamma),(float)Math.cos(gamma),0f,0f,0f,1f);

    rotAlpha.mul(rotGamma);
    rotAlpha.mul(rotBeta);

    Matrix3f rotationMatrix = rotAlpha;
    ren.setRotation(rotationMatrix);
    world.setTransform(ren);
}

    @Override
    public void keyTyped(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
