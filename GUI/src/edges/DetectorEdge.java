/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edges;

import cells.AbstractCell;
import cells.Detector;
import cells.Node;
import graph.Graph;
import cells.ICell;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
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


    public DetectorEdge(Node target, Detector detector) {
        super(target, detector);
        detector.getTargets().add(target);
        
    }
}
