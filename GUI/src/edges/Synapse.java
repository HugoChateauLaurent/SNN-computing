/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edges;

import cells.AbstractNode;
import cells.Connectable;
import graph.Graph;
import cells.ICell;
import static cells.LIF.askParameters;
import graph.Model;
import java.util.NoSuchElementException;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Pair;

/**
 *
 * @author ubuntu
 */
public class Synapse extends AbstractEdge {
    
    private static int count = 1;
    private static final long serialVersionUID = 6L;

    protected AbstractNode pre;
    protected AbstractNode post;
    protected double w;
    protected double[] out_pre;

    protected int index = 0;

    public Synapse(Model model, AbstractNode pre, AbstractNode post, double w, int d) {
        super(model, pre, post, count);
        count++;
        this.pre = pre;
        this.post = post;
        this.w = w;
        this.out_pre = new double[d]; // store output of the presynaptic neuron during d timesteps
    }
    
    public static int getCount() {
        return count;
    }
    
    public static void setCount(int newCount) {
        count = newCount;
    }

    public void step() {
        this.out_pre[this.index] = this.pre.getOut(); // store current output of pre
        this.index = (this.index + 1) % this.out_pre.length;
        this.post.setI(this.post.getI() + this.w * this.out_pre[index]); // add w*pre_{t-d} to post 
    }
    
    @Override
    protected Color getColor() {
        if (w < 0) {
            return new Color(30f/255,144f/255,255f/255,1.0); // Blue
        } else {
            return new Color(205f/255,92f/255,92f/255,0.8); // Red
        }
    }
    
    @Override
    public ContextMenu createContextMenu(Graph graph){
                
        final ContextMenu contextMenu = new ContextMenu();
        
        MenuItem ID_label = new MenuItem("");
        ID_label.setDisable(true);
        ID_label.getStyleClass().add("context-menu-title");

        ID_label.setText("Synapse " + Integer.toString(ID));
        
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
        
        contextMenu.getItems().addAll(ID_label, properties, delete);//, front, back);
        
        return contextMenu;
        
    }
    
    public void editProperties() {
        final Pair params = askParameters(Double.toString(w), Integer.toString(out_pre.length));
        if (params != null) {
            int d = (Integer) params.getValue();
            if (d > 0) {
                out_pre = new double[(d)];
                w = (Double) params.getKey();
                updateLineGradient();
            } else {
                System.out.println("Delay must be at least 1");
            }
        }
    }
    
    public static Pair askParameters() {
        return askParameters("1","1");
    }
    
    public static Pair askParameters(String weight, String delay) {
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Connection parameters");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField weight_field = new TextField(weight);
        TextField delay_field = new TextField(delay);

        gridPane.add(new Label("Weight:"), 0, 0);
        gridPane.add(weight_field, 1, 0);
        gridPane.add(new Label("Delay:"), 2, 0);
        gridPane.add(delay_field, 3, 0);

        dialog.getDialogPane().setContent(gridPane);

        Platform.runLater(() -> weight_field.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(weight_field.getText(), delay_field.getText());
            }
            return null;
        });

        boolean done = false;

        while (!done) {
            try {
                Optional<Pair<String, String>> result = dialog.showAndWait();

                result.ifPresent(pair -> {
                    System.out.println("Weight=" + Double.parseDouble(pair.getKey()) + ", Delay=" + (int) Double.parseDouble(pair.getValue()));
                });
                return new Pair(Double.parseDouble(result.get().getKey()), (int) Double.parseDouble(result.get().getValue()));
            } catch (NumberFormatException e) {
                System.out.println("Invalid input");
            } catch (NoSuchElementException e) {
                System.out.println("No value");
                done = true;
            }
        }
        return null;

    }
}
