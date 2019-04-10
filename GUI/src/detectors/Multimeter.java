/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package detectors;

import cells.LIF;
import cells.Node;
import graph.Connectable;
import graph.Graph;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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
public class Multimeter extends AbstractDetector {

    private List<Node> targets;
    private double[][] V;
    private int index;
    final Rectangle view = new Rectangle(50, 50);

    public Multimeter(List<Node> targets) {
        super();
        this.targets = targets;
        this.V = null;
    }

    public Multimeter() {
        this(new LinkedList());
    }

    public void init(int steps) {
        this.V = new double[steps][targets.size()];
        this.index = 0;
    }

    public void step() {
        LIF lif;
        for (int i = 0; i < targets.size(); i++) {
            lif = (LIF) targets.get(i);
            V[index][i] = lif.getV();
        }
        index++;
    }
    
    @Override
    public List<Node> getTargets() {
        return targets;
    }

    @Override
    public Region getGraphic(Graph graph) {
        if (this.V != null) {
            this.updateColor();
            view.setStroke(Color.BLACK);

            final Pane pane = new Pane(view);
            pane.setPrefSize(50, 50);
            view.widthProperty().bind(pane.prefWidthProperty());
            view.heightProperty().bind(pane.prefHeightProperty());
            //CellGestures.makeResizable(pane);

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
        
        return null;
    }
    
    public void updateColor() {
        if (toConnect) {
            view.setFill(Color.GRAY);
        } else {
            view.setFill(Color.WHITE);
        }
    }
    
    
}
