/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cells;

import graph.Graph;
import graph.Model;
import java.io.Serializable;
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
public abstract class AbstractNode extends AbstractCell implements Detectable {
    
    protected double I = 0;
    protected double out = 0;
    protected double V;
    
    protected double amplitude;
    protected Random rng;
    
    public AbstractNode(double amplitude, int ID, Model model) {
        this.amplitude = amplitude;
        this.rng = new Random();
        this.ID = ID;
        this.model = model;
        createView();
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
    
    public double getV() {
        return V;
    }
    
    @Override
    public ContextMenu createContextMenu(Graph graph){
        AbstractNode this_node = this;
                
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

        if (this instanceof LIF) {
            ID_label.setText("LIF " + Integer.toString(ID));
        } else if (this instanceof InputTrain) {
            ID_label.setText("Input Train " + Integer.toString(ID));
        } else {
            System.out.println("Unknown AbstractNode: cannot ID_label");
        }
        
        
        
        
        MenuItem connect = new MenuItem("Connection mode on/off");
        connect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                updateToConnect(!toConnect);
                model.tryToConnect(this_node);
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
                model.beginUpdate();
                lif.getGraphic(graph).toFront();
                graph.endUpdate();
            }
        });
        
        MenuItem back = new MenuItem("Back");
        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                model.beginUpdate();
                lif.getGraphic(graph).toBack();
                graph.endUpdate();
            }
        });*/
        
        
        contextMenu.getItems().addAll(ID_label, connect, properties, delete);//, front, back);
        
        return contextMenu;
        
    }
    
    public abstract void editProperties();
    
}
