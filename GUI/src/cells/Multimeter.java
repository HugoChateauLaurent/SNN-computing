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
import graph.Model;
import java.io.Serializable;
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

    private static final long serialVersionUID = 9L;

    private static int count = 1;

    private double[][] V; // [target][t]
    private int index;

    public Multimeter(Model model, List<AbstractNode> targets) {
        super(targets, count, model);
        count++;
    }

    public Multimeter(Model model) {
        this(model, new LinkedList());
    }

    public void init(int steps) {
        this.V = new double[targets.size()][steps];
        this.index = 0;
    }

    public double[][] getV() {
        return V;
    }

    public void step() {
        AbstractNode node;
        for (int i = 0; i < targets.size(); i++) {
            node = (AbstractNode) targets.get(i);
            V[i][index] = node.getV();
        }
        index++;
    }

    public void createVisualizer() {
        visualizer = new MultimeterVisualizer(model.getGraph().getApp(), this, false);
    }

    public void increaseCount() {
        count++;
    }

    public void decreaseCount() {
        count--;
    }

    public static void setCount(int newCount) {
        count = newCount;
    }

    public static int getCount() {
        return count;
    }
    
    public int getClassCount() {
        return count;
    }

}
