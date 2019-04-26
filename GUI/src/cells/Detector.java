package cells;

import edges.*;
import cells.ICell;
import cells.Node;
import graph.IGraphNode;
import java.util.List;
import visualizer.AbstractVisualizer;

public abstract class Detector extends AbstractCell implements Connectable {

    List<Node> targets;    
    protected boolean toConnect;
    protected AbstractVisualizer visualizer;
    
    public Detector(List<Node> targets) {
        this.targets = targets;
    }
    
    public List<Node> getTargets() {
        return targets;
    }
    
    public abstract void createVisualizer();
    
    public abstract void init(int steps);
    
    @Override
    public void doubleClick() {
        createVisualizer();
    }
    
    public void setVisualizer(AbstractVisualizer visualizer) {
        this.visualizer = visualizer;
    }

}
