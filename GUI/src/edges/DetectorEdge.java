/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edges;

import cells.AbstractCell;
import cells.AbstractDetector;
import cells.AbstractNode;
import cells.Connectable;
import graph.Graph;
import cells.ICell;
import cells.LIF;
import graph.Model;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

/**
 *
 * @author ubuntu
 */
public class DetectorEdge extends AbstractEdge {
    
    private static int count = 1;
    private static final long serialVersionUID = 5L;

    public DetectorEdge(Model model, AbstractNode target, AbstractDetector detector) {
        super(model, target, detector, count);
        count++;
        detector.getTargets().add(target);
        
    }
    
    public static int getCount() {
        return count;
    }
    
    public static void setCount(int newCount) {
        count = newCount;
    }
    
    @Override
    public ContextMenu createContextMenu(Graph graph){
                
        final ContextMenu contextMenu = new ContextMenu();
        
        MenuItem ID_label = new MenuItem("");
        ID_label.setDisable(true);
        ID_label.getStyleClass().add("context-menu-title");

        ID_label.setText("Detector edge " + Integer.toString(ID));
        
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Delete");
                delete();
            }
        });        
        
        contextMenu.getItems().addAll(ID_label, delete);//, front, back);
        
        return contextMenu;
        
    }
}
