package cells;

import graph.Graph;
import graph.Model;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
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

public abstract class AbstractCell implements ICell, Comparable<AbstractCell>, Serializable {

    protected int ID;
    protected int zLevel = 0;

    private static final long serialVersionUID = 1L;

    protected boolean toConnect = false;
    protected transient Shape view = null;
    protected transient CellGestures cellGestures;

    protected Model model;
    protected Module parentModule = null;

    public void setModel(Model model) {
        this.model = model;
    }
    
    public Model getModel() {
        return model;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " " + ID;
        
    }
    
    public abstract String to_inet();

    public void setParentModule(Module parentModule) {
        this.parentModule = parentModule;
    }

    public Module getParentModule() {
        return parentModule;
    }

    public Shape getView() {
        return view;
    }

    public void step() {
    }

    public int getZLevel() {
        return zLevel;
    }

    public void setZLevel(int z) {
        zLevel = z;
    }

    public void updateRng(Random rng) {
    }

    @Override
    public int compareTo(AbstractCell other) {
        // compareTo should return < 0 if this is supposed to be
        // less than other, > 0 if this is supposed to be greater than 
        // other and 0 if they are supposed to be equal
        return zLevel - other.getZLevel();
    }

    public String getClassAndID(boolean underscore) {
        if (underscore) {
            return getClass().getSimpleName() + "_" + Integer.toString(ID);
        } else {
            return getClass().getSimpleName() + " " + Integer.toString(ID);
        }
    }

    @Override
    public int getID() {
        return ID;
    }

    public void decreaseID() {
        ID--;
        if (ID < 1) {
            System.out.println("ERROR: ID of " + this.getClass().getSimpleName() + " is " + ID);
        }
    }
    
    public void setID(int newID) {
        this.ID = newID;
    }
    
    public abstract int getClassCount();

    
    @Override
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
                if (!event.isConsumed()) {
                    if (event.getButton().equals(MouseButton.SECONDARY)) {
                        ContextMenu contextMenu = createContextMenu(graph);
                        contextMenu.setAutoHide(true);
                        contextMenu.show(view, event.getScreenX(), event.getScreenY());
                    } else if (event.getButton().equals(MouseButton.PRIMARY)) {
                        if (event.getClickCount() == 2) {
                            doubleClick();
                        }
                    }
                    event.consume();
                }
            }
        });

        return view;
    }

    public CellGestures getCellGestures() {
        return cellGestures;
    }

    public void doubleClick() {
        if (this instanceof AbstractNode || this instanceof AbstractDetector) {
            updateToConnect(!toConnect);
            model.tryToConnect(this);
            System.out.println("end double click");
        }
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
        System.out.println("newToConnect "+newToConnect);
        this.toConnect = newToConnect;
        this.updateColor();
    }

    public abstract ContextMenu createContextMenu(Graph graph);

    public void delete() {
        model.getGraph().removeCell(this);
        if (parentModule != null) {
            parentModule.removeCell(this);
        }
    }

    public static final Comparator<AbstractCell> ID_COMPARATOR = new Comparator<AbstractCell>() {
        public int compare(AbstractCell a, AbstractCell b) {
            return a.ID - b.ID;
        }
    };

}
