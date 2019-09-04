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
    
    private static final long serialVersionUID = 4L;

    
    private boolean[][] spikes; // [target][t]
    private int index;

    public Raster(List<AbstractNode> targets, MainApp app) {
        super(targets, count, app);
        count++;
    }

    public Raster(MainApp app) {
        this(new LinkedList(), app);
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
    
    @Override
    public boolean getToConnect() {
        return toConnect;
    }

    @Override
    public void updateToConnect(boolean toConnect) {
        this.toConnect = toConnect;
    }

    public void createVisualizer() {
        visualizer = new RasterVisualizer(app, this, false);
    }
    
    public static int getCount() {
        return count;
    }
    
    public static void setCount(int newCount) {
        count = newCount;
    }
    
    
}
