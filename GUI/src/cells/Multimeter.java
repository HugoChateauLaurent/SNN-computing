/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cells;

import cells.LIF;
import cells.AbstractNode;
import graph.Graph;
import graph.MainApp;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import visualizer.MultimeterVisualizer;

/**
 *
 * @author ubuntu
 */
public class Multimeter extends AbstractDetector {
    
    private static final long serialVersionUID = 3L;

    
    private static int count = 1;

    private double[][] V; // [target][t]
    private int index;

    public Multimeter(List<AbstractNode> targets, MainApp app) {
        super(targets, count, app);
        count++;
    }

    public Multimeter(MainApp app) {
        this(new LinkedList(), app);
    }

    public void init(int steps) {
        this.V = new double[targets.size()][steps];
        this.index = 0;
    }
    
    public MainApp getApp() {
        return app;
    }

    public double[][] getV() {
        return V;
    }

    public void step() {
        LIF lif;
        for (int i = 0; i < targets.size(); i++) {
            lif = (LIF) targets.get(i);
            V[i][index] = lif.getV();
        }
        index++;
    }

    public void createVisualizer() {
        visualizer = new MultimeterVisualizer(app, this, false);
    }
    
    public static int getCount() {
        return count;
    }
    
    public static void setCount(int newCount) {
        count = newCount;
    }

}
