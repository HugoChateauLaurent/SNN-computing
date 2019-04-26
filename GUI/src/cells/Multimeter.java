/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cells;

import cells.LIF;
import cells.Node;
import graph.Graph;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 *
 * @author ubuntu
 */
public class Multimeter extends Detector {

    
    private List<Node> targets;
    private double[][] V;
    private int index;

    public Multimeter(List<Node> targets) {
        super(targets);
        this.view = new Rectangle(50, 50);
    }

    public Multimeter() {
        this(new LinkedList());
    }

    public void init(int steps) {
        this.V = new double[steps][targets.size()];
        this.index = 0;
    }

    public void step() {
        LIF lif;
        for (int i = 0; i < targets.size(); i++) {
            lif = (LIF) targets.get(i);
            V[index][i] = lif.getV();
        }
        index++;
    }
    
    
}
