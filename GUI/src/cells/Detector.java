package cells;

import edges.*;
import cells.ICell;
import cells.Node;
import graph.IGraphNode;
import java.util.List;

public abstract class Detector extends AbstractCell implements Connectable {

    List<Node> targets;    
    protected boolean toConnect;
    
    public Detector(List<Node> targets) {
        this.targets = targets;
    }
    
    public List<Node> getTargets() {
        return targets;
    }
    
    public abstract void init(int steps);

}
