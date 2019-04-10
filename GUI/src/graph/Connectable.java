package graph;

import graph.Graph;
import edges.IEdge;
import graph.IGraphNode;
import java.util.List;

import javafx.beans.binding.DoubleBinding;
import javafx.scene.layout.Region;


public interface Connectable extends IGraphNode {

    public boolean getToConnect();
    public void updateToConnect(boolean toConnect);

}
