/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edges;

import cells.Node;
import graph.Graph;
import cells.ICell;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

/**
 *
 * @author ubuntu
 */
public class Connection extends AbstractEdge {

    protected Node pre;
    protected Node post;
    protected double w;
    protected double[] out_pre;

    protected int index = 0;

    public Connection(Node pre, Node post, double w, int d) {
        super(pre, post);
        this.pre = pre;
        this.post = post;
        this.w = w;
        this.out_pre = new double[d+1]; // store output of the presynaptic neuron during d timesteps
    }

    public void step() {
        this.post.setI(this.post.getI() + this.w * this.out_pre[index]); // add w*pre_{t-d} to post 
        this.out_pre[this.index] = this.pre.getOut(); // store current output of pre
        this.index = (this.index + 1) % this.out_pre.length;
    }
    
    
    /*
        View
    */

    @Override
    public EdgeGraphic getGraphic(Graph graph) {
        return new EdgeGraphic(graph, this);
    }

    public static class EdgeGraphic extends Pane {

        private final Group group;
        private final Line line;

        public EdgeGraphic(Graph graph, Connection edge) {
            group = new Group();
            line = new Line();
            
            line.setStrokeWidth(3);

            final DoubleBinding sourceX = edge.getSource().getXAnchor(graph, edge);
            final DoubleBinding sourceY = edge.getSource().getYAnchor(graph, edge);
            final DoubleBinding targetX = edge.getTarget().getXAnchor(graph, edge);
            final DoubleBinding targetY = edge.getTarget().getYAnchor(graph, edge);

            line.startXProperty().bind(sourceX);
            line.startYProperty().bind(sourceY);

            line.endXProperty().bind(targetX);
            line.endYProperty().bind(targetY);
            group.getChildren().add(line);
            
            line.toBack();
            group.toBack();
            
            getChildren().add(group);
        }

        public Group getGroup() {
            return group;
        }

        public Line getLine() {
            return line;
        }

    }
}
