package cells;

import graph.Graph;
import graph.Model;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import java.util.Random;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.control.Cell;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public abstract class AbstractCell implements ICell, Serializable {
    
    protected int ID;
    
    private static final long serialVersionUID = 8L;

    protected boolean toConnect = false;
    protected transient Shape view = null;
    final DragContext dragContext = new DragContext();
    
    protected Model model;
    
    public void setModel(Model model) {
        this.model = model;
    }
    
    public Model getModel() {
        return model;
    }
     
    public Shape getView() {
        return view;
    }

    public void step() {
    }

    public void updateRng(Random rng) {
    }
    
    
    
    @Override
    public int getID() {
        return ID;
    }
    
    public void decreaseID() {
        ID--;
        if (ID<1) {
            System.out.println("ERROR: ID of "+this.getClass().getSimpleName()+" is "+ID);
        }
    }
    
    public abstract void createView();

    @Override
    public Shape getGraphic(Graph graph) {
        this.updateColor();
        view.setStroke(Color.BLACK);
        view.setStrokeWidth(5);

        if (view instanceof Ellipse) {
            Ellipse ellipse = (Ellipse) view;
            ellipse.radiusXProperty().set(30);
            ellipse.radiusYProperty().set(30);
        } else if (view instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) view;
            rectangle.widthProperty().set(35);
            rectangle.heightProperty().set(35);
        }
        
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
    
    public void doubleClick() {
        if (this instanceof AbstractNode || this instanceof AbstractDetector) {
            updateToConnect(!toConnect);
            model.tryToConnect(this);
        }            
    }

    public static class DragContext implements Serializable {
        double x;
        double y;
    }
    
    public void updateColor() {
        Color color;
        if (toConnect) {
            color = Color.GRAY;
        } else {
            color = Color.WHITE;
        }
        view.setFill(new RadialGradient(0, 0, .5, .5, 2.5, true, CycleMethod.NO_CYCLE, new Stop(0, color), new Stop(1, Color.BLACK)));

    }

    public boolean getToConnect() {
        return toConnect;
    }

    public void updateToConnect(boolean newToConnect) {
        this.toConnect = newToConnect;
        this.updateColor();
    }
    
    public abstract ContextMenu createContextMenu(Graph graph);
        
    private void readObject(ObjectInputStream aInputStream)
    throws ClassNotFoundException, IOException {
          aInputStream.defaultReadObject();
          createView();
    
    }
    
    public void delete() {
        model.getGraph().removeCell(this);
    }
    
    

}
