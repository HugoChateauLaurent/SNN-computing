package edges;

import cells.ICell;
import graph.Graph;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;

public abstract class AbstractEdge implements IEdge {

    private final ICell source;
    private final ICell target;

    public AbstractEdge(ICell source, ICell target) {
        this.source = source;
        this.target = target;

        if (source == null) {
            throw new NullPointerException("Source cannot be null");
        }
        if (target == null) {
            throw new NullPointerException("Target cannot be null");
        }

        source.addCellParent(target);
        target.addCellChild(source);
    }

    @Override
    public ICell getSource() {
        return source;
    }

    @Override
    public ICell getTarget() {
        return target;
    }

    public void step() {

    }

    /*
        View
     */
    
    @Override
    public EdgeGraphic getGraphic(Graph graph) {
        return new EdgeGraphic(graph, this);
    }

    public static class EdgeGraphic extends Pane {

        private final Group group;
        private final Line line;

        public EdgeGraphic(Graph graph, AbstractEdge edge) {
            group = new Group();
            line = new Line();

            line.setStrokeWidth(3);

            final DoubleBinding sourceX = edge.getSource().getXAnchor(graph);
            final DoubleBinding sourceY = edge.getSource().getYAnchor(graph);
            final DoubleBinding targetX = edge.getTarget().getXAnchor(graph);
            final DoubleBinding targetY = edge.getTarget().getYAnchor(graph);

            line.startXProperty().bind(sourceX);
            line.startYProperty().bind(sourceY);

            line.endXProperty().bind(targetX);
            line.endYProperty().bind(targetY);
            line.toBack();
            group.getChildren().add(line);

            Ellipse endpoint = new Ellipse(25, 25);

            final DoubleBinding angle = Bindings.createDoubleBinding(() -> Math.atan2(sourceY.get() - targetY.get(), sourceX.get() - targetX.get()), sourceX, sourceY, targetX, targetY);

            final DoubleBinding vectorX = Bindings.createDoubleBinding(() -> Math.cos(angle.get()), angle).multiply(graph.getGraphic(edge.getTarget()).widthProperty());
            final DoubleBinding vectorY = Bindings.createDoubleBinding(() -> Math.sin(angle.get()), angle).multiply(graph.getGraphic(edge.getTarget()).heightProperty());

            endpoint.centerXProperty().bind(targetX.add(vectorX));
            endpoint.centerYProperty().bind(targetY.add(vectorY));
            group.getChildren().add(endpoint);

            getChildren().add(group);
            group.toBack();

        }

        public Group getGroup() {
            return group;
        }

        public Line getLine() {
            return line;
        }

    }

}
