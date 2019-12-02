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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import visualizer.RasterVisualizer;

/**
 *
 * @author ubuntu
 */
public class Raster extends AbstractDetector {

    private static int count = 1;
    
    private static final long serialVersionUID = 11L;

    
    private boolean[][] spikes; // [target][t]
    private int index;

    public Raster(Model model, List<AbstractNode> targets) {
        super(targets, count, model);
        count++;
    }

    public Raster(Model model) {
        this(model, new LinkedList());
    }

    public void init(int steps) {
        this.spikes = new boolean[targets.size()][steps];
        this.index = 0;
    }

    public void step() {
        AbstractNode node;
        for (int i = 0; i < targets.size(); i++) {
            node = targets.get(i);
            spikes[i][index] = node.getOut() > 0;
        }
        index++;
        
    }
    
    public boolean[][] getSpikes() {
        return spikes;
    }

    public void createVisualizer() {
        visualizer = new RasterVisualizer(model.getGraph().getApp(), this, false);
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
