package cells;

import graph.Graph;
import edges.IEdge;
import graph.IGraphNode;
import java.util.List;
import java.util.Random;

import javafx.beans.binding.DoubleBinding;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public interface ICell extends IGraphNode {

    default DoubleBinding getXAnchor(Graph graph) {
        final Shape graphic = graph.getGraphic(this);
        if (this instanceof AbstractNode) {
            return graphic.layoutXProperty().add(0);
        } else {
            Rectangle rectangle = (Rectangle) graphic;
            return graphic.layoutXProperty().add(rectangle.widthProperty().divide(2));
        }
    }

    default DoubleBinding getYAnchor(Graph graph) {
        final Shape graphic = graph.getGraphic(this);
        if (this instanceof AbstractNode) {
            return graphic.layoutYProperty().add(0);
        } else {
            Rectangle rectangle = (Rectangle) graphic;
            return graphic.layoutYProperty().add(rectangle.widthProperty().divide(2));
        }
    }

    public void step();

    public void updateRng(Random rng);
    
    default void init() {
        // some cells parameters need to be initialized at the beginning of a simulation (e.g. LIF voltage)
    }
    
    public void delete();

}
