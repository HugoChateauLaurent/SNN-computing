package cells;

import graph.Graph;
import graph.Model;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;

import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

public class InputTrain extends AbstractNode {
    
    private static int count = 1;
    
    private static final long serialVersionUID = 2L;
    
    private boolean toConnect = false;
    
    //parameters
    protected double[] train;
    protected int index;
    protected boolean loop;
    
    //recording parameters for non implemented cell built-in detector
    protected boolean record_V;
    protected boolean record_spikes;

    public InputTrain(Model model, double[] train, boolean loop) {
        super(1, count, model);
        count++;
        
        this.train = train;
        this.loop = loop;
        
        record_V = true;
        record_spikes = true;
        
        init();
                
    }
    
    public void createView() {
        view = new Ellipse(50, 50);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()+"_"+ID+ " = "+getClass().getSimpleName()+"(");
        sb.append(arrayToString(train)+", ");
        sb.append(Boolean.toString(loop)+")");
        
        return sb.toString();
    }
    
    public static String arrayToString(double[] array) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i=0; i<array.length; i++) {
            sb.append(array[i]+",");
        }
        sb.append("]");
        
        return sb.toString();
    }
    
    
    @Override
    public void init() {
        index = 0;
        
    }
    
    public void step() {
        if (index >= train.length) {
            if (loop) {
                index = 0;
                V = train[index];
            } else {
                V = 0;
            }
        } else {
            V = train[index];
        }
        this.out = V;
        
        index ++;
        
        
    }

    public double getV() {
        return V;
    }
    
    public void editProperties() {
        StringBuilder s_train = new StringBuilder();
        for (int i=0; i<train.length; i++) {
            s_train.append(Double.toString(train[i]));
            if (i<train.length-1) {
                s_train.append(", ");
            }
        }
        
        final Pair<double[],Boolean> params = askParameters(s_train.toString(), loop);
        if (params != null) {
            train = params.getKey();
            loop = params.getValue();
        }
    }
        
    public static Pair<double[],Boolean> askParameters() {
        return askParameters("", false); //default values
    }

    public static Pair<double[],Boolean> askParameters(String s_train, boolean loop) {
        // Create the custom dialog.
        Dialog<Pair<double[],Boolean>> dialog = new Dialog<>();
        dialog.setTitle("Input Train parameters");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));
        
        
        gridPane.add(new Label("Input train:"), 0, 0);
        
        TextField text = new TextField(s_train);
        gridPane.add(text, 1, 0);
        
        CheckBox box = new CheckBox("Loop");
        box.setSelected(loop);
        gridPane.add(box, 0, 1);
        

        dialog.getDialogPane().setContent(gridPane);
        Platform.runLater(() -> text.requestFocus());
        
        

        dialog.setResultConverter(dialogButton -> {
            Pair<double[],Boolean> params;
            if (dialogButton == loginButtonType) {
                
                final double[] arr = Stream.of(text.getText().split(","))
                     .mapToDouble (Double::parseDouble)
                     .toArray();
                params = new Pair(arr, box.isSelected());

                return params;
            }
            return null;
        });

        boolean done = false;

        while (!done) {
            try {
                Optional<Pair<double[],Boolean>> result = dialog.showAndWait();

                return result.get();
            } catch (NoSuchElementException e) {
                System.out.println("No value");
                done = true;
            } catch (Exception e) {
                System.out.println("Invalid input");
            }
        }
        return null;

    }
    
    public void increaseCount() {
        count++;
    }
    
    public void decreaseCount() {
        count--;
    }
    
    public static void setCount(int newCount) {
        count = newCount;
    }
    
    public static int getCount() {
        return count;
    }

    
}
