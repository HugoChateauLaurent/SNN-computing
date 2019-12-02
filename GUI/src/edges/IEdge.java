package edges;

import cells.ICell;
import graph.IGraphNode;
import java.io.IOException;
import java.io.ObjectInputStream;

public interface IEdge extends IGraphNode {

    public ICell getSource();

    public ICell getTarget();

    public void step();
    
    public void delete();
    
    public void increaseCount();
    public void decreaseCount();
    public void decreaseID();
    
    public int getID();
    
    public void createView();
}
