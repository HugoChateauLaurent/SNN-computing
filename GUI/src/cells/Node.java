/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cells;

import graph.Connectable;
import java.util.Random;

/**
 *
 * @author ubuntu
 */
public abstract class Node extends AbstractCell implements Connectable {
    
    protected double I = 0;
    protected double out = 0;
    
    protected double amplitude;
    protected Random rng;
    
    public Node(double amplitude) {
        this.amplitude = amplitude;
        this.rng = new Random();
    }
    
    public void update_rng(Random rng) {
        this.rng = rng;
    }
    
    public abstract void step();

    public double getOut() {
        return this.out;
    }

    public double getI() {
        return this.I;
    }

    public void setI(double I) {
        this.I = I;
    }
}
