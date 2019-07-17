/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edges;

import cells.AbstractNode;
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
public class Synapse extends AbstractEdge {

    protected AbstractNode pre;
    protected AbstractNode post;
    protected double w;
    protected double[] out_pre;

    protected int index = 0;

    public Synapse(AbstractNode pre, AbstractNode post, double w, int d) {
        super(pre, post);
        this.pre = pre;
        this.post = post;
        this.w = w;
        this.out_pre = new double[d]; // store output of the presynaptic neuron during d timesteps
    }

    public void step() {
        this.out_pre[this.index] = this.pre.getOut(); // store current output of pre
        this.index = (this.index + 1) % this.out_pre.length;
        this.post.setI(this.post.getI() + this.w * this.out_pre[index]); // add w*pre_{t-d} to post 
    }
}
