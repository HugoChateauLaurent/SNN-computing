package edges;

import cells.ICell;
import graph.IGraphNode;

public interface IEdge extends IGraphNode {

    public ICell getSource();

    public ICell getTarget();

    public void step();
    
    public void delete();

}
