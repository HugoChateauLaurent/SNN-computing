/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cells;

import cells.LIF;
import cells.Node;
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
public class Multimeter extends Detector {

    private double[][] V;
    private int index;
    private MainApp app;

    public Multimeter(List<Node> targets, MainApp app) {
        super(targets);
        this.view = new Rectangle(100, 100);
        this.app = app;
    }

    public Multimeter(MainApp app) {
        this(new LinkedList(), app);
    }

    public void init(int steps) {
        this.V = new double[targets.size()][steps];
        this.index = 0;
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
        visualizer = new MultimeterVisualizer(app, this);
        HBox visualizers_hbox = (HBox) app.getVisualizers().getContent();
        visualizers_hbox.getChildren().add(visualizer);
    }

}
