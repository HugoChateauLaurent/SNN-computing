package detectors;

import edges.*;
import cells.ICell;
import cells.Node;
import graph.IGraphNode;
import java.util.List;

public interface IDetector extends IGraphNode {

    public List<Node> getTargets();
    public void init(int steps);
    public void step();

}
