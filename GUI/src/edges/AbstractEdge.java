package edges;

import cells.ICell;
import graph.Graph;
import graph.Model;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

public abstract class AbstractEdge implements IEdge, Serializable {
    
    private static final long serialVersionUID = 7L;
    
    protected int ID;

    protected final ICell source;
    protected final ICell target;
    private transient Shape view;
    protected Model model;

    public AbstractEdge(Model model, ICell source, ICell target, int ID) {
        this.source = source;
        this.target = target;
        this.ID = ID;
        this.model = model;
        createView();

        if (source == null) {
            throw new NullPointerException("Source cannot be null");
        }
        if (target == null) {
            throw new NullPointerException("Target cannot be null");
        }
    }
    
    public void createView() {
        view = new Line();
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
        Line line = (Line) view;

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
        
        line.setStrokeWidth(8);
        
        updateLineGradient();
        
        view.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    ContextMenu contextMenu = createContextMenu(graph);
                    contextMenu.setAutoHide(true);
                    contextMenu.show(view, event.getScreenX(), event.getScreenY());
                    event.consume();
                } else if(event.getButton().equals(MouseButton.PRIMARY)){
                    if(event.getClickCount() == 2){
                        doubleClick();
                    }
                }
            }
        });
                
        return view;
    }
    
    public abstract ContextMenu createContextMenu(Graph graph);
    
    protected void updateLineGradient() {
        Line line = (Line) view;
        Stop[] stops = new Stop[] {new Stop(0, Color.WHITE), new Stop(1, getColor())};
        LinearGradient lg = new LinearGradient(line.startXProperty().get(), line.startYProperty().get(), line.endXProperty().get(), line.endYProperty().get(), false, CycleMethod.NO_CYCLE, stops);
        line.setStroke(lg);
    }
    
    protected Color getColor() {
        return Color.BLACK;
    }
    
    public void delete() {
        model.getGraph().removeEdge(this);
    }
    
    private void readObject(ObjectInputStream aInputStream)
    throws ClassNotFoundException, IOException {
          aInputStream.defaultReadObject();
          createView();
    
    }
    
    public void doubleClick() {
        if (this instanceof Synapse) {
            Synapse synapse = (Synapse) this;
            System.out.println("Editing properties");
            synapse.editProperties();
        }
    }
    
    public void decreaseID() {
        ID--;
        System.out.println("Decrease ID to "+ID);
        if (ID<1) {
            System.out.println("ERROR: ID of "+this.getClass().getSimpleName()+" is "+ID);
        }
    }
    
    @Override
    public int getID() {
        return ID;
    }

}
