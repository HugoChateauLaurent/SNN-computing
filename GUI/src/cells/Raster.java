/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cells;

import cells.LIF;
import cells.AbstractNode;
import graph.Graph;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javafx.scene.layout.Region;

/**
 *
 * @author ubuntu
 */
public class Raster extends AbstractDetector {

    private static int count = 1;
    
    private boolean[][] spikes;
    private int index;

    public Raster(List<AbstractNode> targets) {
        super(targets, count);
        count++;
    }

    public Raster() {
        this(new LinkedList());
    }

    public void init(int steps) {
        this.spikes = new boolean[steps][targets.size()];
        this.index = 0;
    }

    public void step() {
        AbstractNode node;
        for (int i = 0; i < targets.size(); i++) {
            node = targets.get(i);
            spikes[index][i] = node.getOut() > 0;
        }
        index++;
    }

    @Override
    public Region getGraphic(Graph graph) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public boolean getToConnect() {
        return toConnect;
    }

    @Override
    public void updateToConnect(boolean toConnect) {
        this.toConnect = toConnect;
    }

    @Override
    public void createVisualizer() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public int getCount() {
        return count;
    }
}
