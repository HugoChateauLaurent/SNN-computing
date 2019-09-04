package cells;

import edges.*;
import cells.ICell;
import cells.AbstractNode;
import graph.Graph;
import graph.IGraphNode;
import graph.MainApp;
import graph.Model;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import visualizer.AbstractVisualizer;

public abstract class AbstractDetector extends AbstractCell implements Connectable {

    List<AbstractNode> targets;    
    protected boolean toConnect;
    protected transient AbstractVisualizer visualizer;

    
    public AbstractDetector(List<AbstractNode> targets, int ID, Model model) {
        this.targets = targets;
        this.ID = ID;
        this.model = model;
        createView();
    }
    
    
    
    public void createView() {
        this.view = new Rectangle(100, 100);
    }
    
    public List<AbstractNode> getTargets() {
        return targets;
    }
    
    public abstract void createVisualizer();
    
    public abstract void init(int steps);
    
    public void setVisualizer(AbstractVisualizer visualizer) {
        this.visualizer = visualizer;
    }
        
    @Override
    public ContextMenu createContextMenu(Graph graph){
        
        Connectable this_connectable = (Connectable) this;
        
        final ContextMenu contextMenu = new ContextMenu();
        /*contextMenu.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                System.out.println("hide");
                contextMenu.hide();
            }
                
        });*/
        
        MenuItem ID_label = new MenuItem("");
        ID_label.setDisable(true);
        ID_label.getStyleClass().add("context-menu-title");
        
        if (this instanceof Multimeter) {
            ID_label.setText("Multimeter " + Integer.toString(ID));
        } else if (this instanceof Raster) {
            ID_label.setText("Raster " + Integer.toString(ID));
        } else {
            System.out.println("Unknown AbstractDetector: cannot ID_label");
        }
        
        MenuItem connect = new MenuItem("Connection mode on/off");
        connect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Connection mode: " + String.valueOf(!toConnect));
                updateToConnect(!toConnect);
                graph.getModel().tryToConnect(this_connectable);
            }
        });
        
        MenuItem openVisualizer = new MenuItem("Show/hide recordings");
        openVisualizer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Showing/hiding visualizer");
                displayVisualizer();
            }
        });
        
        MenuItem properties = new MenuItem("Open/edit properties");
        properties.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Editing properties");
                editProperties();
            }
        });
        
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Delete");
                delete();
            }
        });
        
        
        contextMenu.getItems().addAll(ID_label, connect, openVisualizer, properties, delete);
        
        return contextMenu;
        
    }
    
    public void editProperties(){
        System.out.println("Edit properties not implemented");
    }
    
    public void displayVisualizer() {
        HBox visualizers_hbox = (HBox) model.getGraph().getApp().getVisualizers().getContent();
        if (visualizers_hbox.getChildren().contains(visualizer)) {
            visualizers_hbox.getChildren().remove(visualizer);
        } else {
            visualizers_hbox.getChildren().add(visualizer);
        }
    }

    public AbstractVisualizer getVisualizer() {
        return visualizer;
    }

}
