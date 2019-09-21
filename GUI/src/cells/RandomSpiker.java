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

public class RandomSpiker extends AbstractNode {
    
    private static int count = 1;
    
    private static final long serialVersionUID = 2L;
    
    private boolean toConnect = false;
    
    //parameters
    protected double p;
    
    //recording parameters for non implemented cell built-in detector
    protected boolean record_V;
    protected boolean record_spikes;

    public RandomSpiker(Model model, double p, double amplitude) {
        super(amplitude, count, model);
        count++;
        
        this.p = p;
        
        record_V = true;
        record_spikes = true;
    }
    
    public void createView() {
        view = new Ellipse(50, 50);
    }
    
    
    @Override
    public void init() {
        
        
    }
    
    public void step() {
        V = 0;
        if (rng.nextDouble()<p) {
            V = 1;
        }
        this.out = V*amplitude;
    }

    public double getV() {
        return V;
    }
    
    public void editProperties() {
        final Pair<Double,Double> params = askParameters(Double.toString(p), Double.toString(amplitude));
        if (params != null) {
            p = params.getKey();
            amplitude = params.getValue();
        }
    }
        
    public static Pair<Double,Double> askParameters() {
        return askParameters("","1"); //default values
    }

    public static Pair<Double,Double> askParameters(String s_p, String s_amplitude) {
        // Create the custom dialog.
        Dialog<Pair<Double,Double>> dialog = new Dialog<>();
        dialog.setTitle("Random generator parameters");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));
        
        
        gridPane.add(new Label("Probability of firing: "), 0, 0);
        TextField p_field = new TextField(s_p);
        gridPane.add(p_field, 1, 0);
        
        gridPane.add(new Label("Amplitude: "), 0, 1);
        TextField amplitude_field = new TextField(s_amplitude);
        gridPane.add(amplitude_field, 1, 1);
        

        dialog.getDialogPane().setContent(gridPane);
        Platform.runLater(() -> p_field.requestFocus());
        
        

        dialog.setResultConverter(dialogButton -> {
            Pair<Double, Double> params;
            if (dialogButton == loginButtonType) {
                
                params = new Pair(Double.parseDouble(p_field.getText()), Double.parseDouble(amplitude_field.getText()));

                return params;
            }
            return null;
        });

        boolean done = false;

        while (!done) {
            try {
                Optional<Pair<Double,Double>> result = dialog.showAndWait();

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
