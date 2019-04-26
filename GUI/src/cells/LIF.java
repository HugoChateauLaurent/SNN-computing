package cells;

import graph.Graph;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

public class LIF extends Node implements Connectable {
    
    private boolean toConnect = false;
    
    protected double m;
    protected double V;
    protected double V_reset;
    protected double V_rest;
    protected double thr;
    protected double I_e;
    protected double noise;

    public LIF(double m, double V_init, double V_reset, double V_rest, double thr, double amplitude, double I_e, double noise) {
        super(amplitude);
        
        this.m = m;
        this.V = V_init;
        this.V_reset = V_reset;
        this.V_rest = V_rest;
        this.thr = thr;
        this.I_e = I_e;
        this.noise = noise;
        
        this.view = new Ellipse(50, 50);
    }
    
    public void step() {
        this.V = this.V * this.m + this.I + this.rng.nextGaussian() * this.noise; // update V
        if (V < V_rest) {
            V = V_rest;
        }
        this.I = this.I_e; // reset I with I_e
        if (this.V > this.thr) { // check for spike
            this.V = this.V_reset;
            this.out = this.amplitude;
        } else {
            this.out = 0;
        }
    }

    public double getV() {
        return V;
    }

}
