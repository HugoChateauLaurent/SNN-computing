package cells;

import graph.Connectable;
import graph.Graph;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

public class LIF extends Node implements Connectable {
    
    private boolean toConnect = false;
    final Ellipse view = new Ellipse(100, 100);
    
    protected double m;
    protected double V;
    protected double V_reset;
    protected double V_rest;
    protected double thr;
    protected double I_e;
    protected double noise;

    public LIF(double m, double V_init, double V_reset, double V_rest, double thr, double amplitude, double I_e, double noise) {
        super(amplitude);
        
        this.m = m;
        this.V = V_init;
        this.V_reset = V_reset;
        this.V_rest = V_rest;
        this.thr = thr;
        this.I_e = I_e;
        this.noise = noise;
    }
    
    public void step() {
        this.V = this.V * this.m + this.I + this.rng.nextGaussian() * this.noise; // update V
        if (V < V_rest) {
            V = V_rest;
        }
        this.I = this.I_e; // reset I with I_e
        if (this.V > this.thr) { // check for spike
            this.V = this.V_reset;
            this.out = this.amplitude;
        } else {
            this.out = 0;
        }
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
        view.setStrokeWidth(3);

        final Pane pane = new Pane(view);
        pane.setPrefSize(100, 100);
        view.radiusXProperty().bind(pane.prefWidthProperty());
        view.radiusYProperty().bind(pane.prefHeightProperty());
        
        view.toFront();
        pane.toFront();
        
        view.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (e.getButton() == MouseButton.SECONDARY) {
                    updateToConnect(!toConnect); 
                    graph.getModel().tryToConnect();
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

    public double getV() {
        return V;
    }

}
