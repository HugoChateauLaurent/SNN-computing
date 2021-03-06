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

public abstract class AbstractDetector extends AbstractCell {

    List<AbstractNode> targets;    
    protected boolean toConnect;
    protected transient AbstractVisualizer visualizer;
    
    private static final long serialVersionUID = 2L;

    
    public AbstractDetector(List<AbstractNode> targets, int ID, Model model) {
        this.targets = targets;
        this.ID = ID;
        this.model = model;
        createView();
    }
    
    
    public void createView() {
        this.view = new Rectangle(100, 100);
        cellGestures = new CellGestures(model.getGraph(), this);
        cellGestures.makeDraggable();
    }
    
    public List<AbstractNode> getTargets() {
        return targets;
    }
    
    public abstract void createVisualizer();
    
    public abstract void init(int steps);
    
    public void setVisualizer(AbstractVisualizer visualizer) {
        this.visualizer = visualizer;
    }
    
    public String to_inet() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()+"_"+ID+ " = network.create"+getClass().getSimpleName()+"()");
        
        for (AbstractNode node : targets) {
            sb.append(getClass().getSimpleName()+"_"+ID+".addTarget("+node.getClassAndID(true)+")\n");
        }
        sb.append("\n");
        
        return sb.toString();
    }
        
    @Override
    public ContextMenu createContextMenu(Graph graph){
        
        AbstractDetector this_node = this;
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
            ID_label.setText("Multimeter " + Integer.toString(ID)+" z"+zLevel);
        } else if (this instanceof Raster) {
            ID_label.setText("Raster " + Integer.toString(ID)+" z"+zLevel);
        } else {
            System.out.println("Unknown AbstractDetector: cannot ID_label");
        }
        
        MenuItem connect = new MenuItem("Connection mode on/off");
        connect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                updateToConnect(!toConnect);
                model.tryToConnect(this_node);
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
        
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Delete");
                delete();
            }
        });
        
        
        contextMenu.getItems().addAll(ID_label, connect, openVisualizer, delete);
        
        return contextMenu;
        
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

    public void removeTarget(AbstractNode target) {
        targets.remove(target);
        tryVisu();
    }
    
    public void tryVisu() {
        if (visualizer != null) {
            visualizer.visualize();
        }
    }
}
