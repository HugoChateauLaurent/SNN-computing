package cells;

import graph.Graph;
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

    protected boolean toConnect = false;
    protected transient Shape view = null;
    final DragContext dragContext = new DragContext();
    
    public Shape getView() {
        return view;
    }

    public void step() {
    }

    public void updateRng(Random rng) {
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

        Connectable this_connectable = (Connectable) this;
        
        view.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    ContextMenu contextMenu = createContextMenu(graph);
                    contextMenu.setAutoHide(true);
                    contextMenu.show(view, event.getScreenX(), event.getScreenY());
                    event.consume();
        
                    
                }
            }
        });
        
        return view;
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

    public void updateToConnect(boolean toConnect) {
        System.out.println("updatetoconnect"+String.valueOf(toConnect));
        this.toConnect = toConnect;
        this.updateColor();
    }
    
    public abstract ContextMenu createContextMenu(Graph graph);
        
    private void readObject(ObjectInputStream aInputStream)
    throws ClassNotFoundException, IOException {
          aInputStream.defaultReadObject();
          createView();
    
    }
    
    public void delete() {
        System.out.println("Delete not implemented");
    }
    
    

}
