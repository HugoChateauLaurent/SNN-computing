/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cells;

import graph.Graph;
import java.util.Random;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author ubuntu
 */
public class SpikeTrain extends Node {
    
    private boolean toConnect = false;
    final Rectangle view = new Rectangle(50, 50);
    
    protected double[] train;
    protected int index = 0;
    
    
    public SpikeTrain(double[] train, double amplitude) {
        super(amplitude);
        this.train = train;
    }
    
    public void step() {
        this.out = this.train[this.index] * this.amplitude;
        this.index = (this.index + 1) % this.train.length;
    }
        
    
    public boolean getToConnect() {
        return toConnect;
    }
    
    public void updateToConnect(boolean toConnect) {
        this.toConnect = toConnect;
        this.updateColor();
    }
    
    
    
    /*
        View
    */
    
    @Override
    public Region getGraphic(Graph graph) {
        this.updateColor();
        view.setStroke(Color.BLACK);

        final Pane pane = new Pane(view);
        pane.setPrefSize(50, 50);
        view.widthProperty().bind(pane.prefWidthProperty());
        view.heightProperty().bind(pane.prefHeightProperty());
        //CellGestures.makeResizable(pane);
        Connectable this_connectable = (Connectable) this;
        
        view.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (e.getButton() == MouseButton.SECONDARY) {
                    updateToConnect(!toConnect); 
                    graph.getModel().tryToConnect(this_connectable);
                }
            }
        });

        return pane;
    }
    
    public void updateColor() {
        if (toConnect) {
            view.setFill(Color.GRAY);
        } else {
            view.setFill(Color.WHITE);
        }
    }
        
        
}
