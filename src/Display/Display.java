/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Display;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;

/**
 *
 * @author Kristau5
 */
public class Display extends JFrame  {
    Canvas canvas;
    public Display(Canvas canvas){
        this.canvas = canvas;
        this.setLayout(new BorderLayout());
        setSize(new Dimension(1200,800));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(BorderLayout.CENTER, canvas);
        this.setVisible(true);
        
    }
    
}
