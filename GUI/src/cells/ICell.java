package cells;

import graph.Graph;
import edges.IEdge;
import graph.IGraphNode;
import java.util.List;
import java.util.Random;

import javafx.beans.binding.DoubleBinding;
import javafx.scene.layout.Region;

public interface ICell extends IGraphNode {

    public void addCellChild(ICell cell);

    public List<ICell> getCellChildren();

    public void addCellParent(ICell cell);

    public List<ICell> getCellParents();

    public void removeCellChild(ICell cell);

    default DoubleBinding getXAnchor(Graph graph) {
        final Region graphic = graph.getGraphic(this);
        return graphic.layoutXProperty().add(0);
        // was:
        //return graphic.layoutXProperty().add(graphic.widthProperty().divide(2));
    }

    default DoubleBinding getYAnchor(Graph graph) {
        final Region graphic = graph.getGraphic(this);
        return graphic.layoutYProperty().add(0);
        // was:
        //return graphic.layoutYProperty().add(graphic.heightProperty().divide(2));
    }

    public void step();

    public void updateRng(Random rng);
    
    default void init() {
        // some cells parameters need to be initialized at the beginning of a simulation (e.g. LIF voltage)
    }

}
