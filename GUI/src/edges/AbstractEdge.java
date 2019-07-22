package edges;

import cells.ICell;
import graph.Graph;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

public abstract class AbstractEdge implements IEdge {

    private final ICell source;
    private final ICell target;
    private Shape view;

    public AbstractEdge(ICell source, ICell target) {
        this.source = source;
        this.target = target;
        this.view = null;

        if (source == null) {
            throw new NullPointerException("Source cannot be null");
        }
        if (target == null) {
            throw new NullPointerException("Target cannot be null");
        }
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
    public Shape getGraphic(Graph graph) {
        Line line = new Line();
        view = line;

        final DoubleBinding sourceX = source.getXAnchor(graph);
        final DoubleBinding sourceY = source.getYAnchor(graph);
        final DoubleBinding targetX = target.getXAnchor(graph);
        final DoubleBinding targetY = target.getYAnchor(graph);

        line.startXProperty().bind(sourceX);
        line.startYProperty().bind(sourceY);
        line.endXProperty().bind(targetX);
        line.endYProperty().bind(targetY);
        
        line.startXProperty().addListener(o -> updateLineGradient());
        line.startYProperty().addListener(o -> updateLineGradient());
        line.endXProperty().addListener(o -> updateLineGradient());
        line.endYProperty().addListener(o -> updateLineGradient());
        
        line.setStrokeWidth(5);
        
        updateLineGradient();
                
        return line;
    }
    
    private void updateLineGradient() {
        Line line = (Line) view;
        Stop[] stops = new Stop[] {new Stop(0.5, new Color(0,0,0,1.0)), new Stop(1, getColor())};
        LinearGradient lg = new LinearGradient(line.startXProperty().get(), line.startYProperty().get(), line.endXProperty().get(), line.endYProperty().get(), false, CycleMethod.NO_CYCLE, stops);
        line.setStroke(lg);
    }
    
    protected Color getColor() {
        return Color.BLACK;
    }

}
