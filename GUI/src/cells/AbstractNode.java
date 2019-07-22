/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cells;

import graph.Graph;
import java.util.Random;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author ubuntu
 */
public abstract class AbstractNode extends AbstractCell implements Connectable {
    
    protected double I = 0;
    protected double out = 0;
    
    protected double amplitude;
    protected Random rng;
    
    public AbstractNode(double amplitude, int ID) {
        this.amplitude = amplitude;
        this.rng = new Random();
        this.ID = ID;
    }
    
    public void update_rng(Random rng) {
        this.rng = rng;
    }
    
    public double getOut() {
        return this.out;
    }
    
    public void setOut(double out) {
        this.out = out;
    }

    public double getI() {
        return this.I;
    }

    public void setI(double I) {
        this.I = I;
    }
    
    @Override
    public ContextMenu createContextMenu(MouseEvent event, Graph graph){
        
        Connectable this_connectable = (Connectable) this;
        
        final ContextMenu contextMenu = new ContextMenu();
        
        MenuItem ID_label = new MenuItem("");
        ID_label.setDisable(true);
        ID_label.getStyleClass().add("context-menu-title");

        if (this instanceof LIF) {
            ID_label.setText("LIF " + Integer.toString(ID));
        } else if (this instanceof SpikeTrain) {
            ID_label.setText("SpikeTrain " + Integer.toString(ID));
        } else {
            System.out.println("Unknown AbstractNode: cannot ID_label");
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
        
        /*
        
        LIF lif = (LIF) this;
        
        MenuItem front = new MenuItem("Front");
        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                graph.getModel().beginUpdate();
                lif.getGraphic(graph).toFront();
                graph.endUpdate();

            }
        });
        
        MenuItem back = new MenuItem("Back");
        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                graph.getModel().beginUpdate();
                lif.getGraphic(graph).toBack();
                graph.endUpdate();

            }
        });*/
        
        
        contextMenu.getItems().addAll(ID_label, connect, properties, delete);//, front, back);
        
        return contextMenu;
        
    }
    
    public abstract void editProperties();
    
    public void delete() {
        
    }
    
}
