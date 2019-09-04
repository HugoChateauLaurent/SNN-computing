package cells;

import graph.Graph;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
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

public class LIF extends AbstractNode implements Connectable {
    
    private static int count = 1;
    
    private static final long serialVersionUID = 2L;
    
    private boolean toConnect = false;
    
    //parameters
    protected double m;
    protected double V;
    protected double V_init;
    protected double V_reset;
    protected double V_rest;
    protected double thr;
    protected double I_e;
    protected double noise;
    
    //recording parameters
    protected boolean record_V;
    protected boolean record_spikes;

    public LIF(double m, double V_init, double V_reset, double V_rest, double thr, double amplitude, double I_e, double noise) {
        super(amplitude, count);
        count++;
        
        this.m = m;
        this.V_init = V_init;
        init();
        this.V_reset = V_reset;
        this.V_rest = V_rest;
        this.thr = thr;
        this.I_e = I_e;
        this.noise = noise;
        
        record_V = true;
        record_spikes = true;
        
        
    }
    
    public void createView() {
        this.view = new Ellipse(50, 50);
    }
    
    
    @Override
    public void init() {
        this.V = V_init;
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

    public double getV() {
        return V;
    }
    
    public void editProperties() {
        final Double[] params = askParameters(this.m, this.V_init, this.V_reset, 
                this.V_rest, this.thr, this.amplitude, this.I_e, this.noise);
        if (params != null) {
            this.m = params[0];
            this.V_init = params[1];
            init();
            this.V_reset = params[2];
            this.V_rest = params[3];
            this.thr = params[4];
            this.amplitude = params[5];
            this.I_e = params[6];
            this.noise = params[7];
        }
    }
        
    public static Double[] askParameters() {
        return askParameters(.9, 0, 0, 0, 1, 1, 0, 0); //default values
    }

    public static Double[] askParameters(double m, double V_init, double V_reset, double V_rest, double thr, double amplitude, double I_e, double noise) {
        // Create the custom dialog.
        Dialog<Double[]> dialog = new Dialog<>();
        dialog.setTitle("LIF parameters");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        String[] params_labels = {"Leakage constant", "V_init", "V_reset", "V_rest", "thr", "amplitude", "I_e", "noise"};
        double[] values = {m, V_init, V_reset, V_rest, thr, amplitude, I_e, noise};
        List<TextField> fields = new LinkedList();

        TextField text;
        for (int i = 0; i < params_labels.length; i++) {
            gridPane.add(new Label(params_labels[i] + ":"), (2 * i) % 4, (int) ((2 * i) / 4.0));
            text = new TextField(Double.toString(values[i]));
            fields.add(text);
            gridPane.add(text, (2 * i + 1) % 4, (int) ((2 * i + 1) / 4.0));
        }

        dialog.getDialogPane().setContent(gridPane);
        Platform.runLater(() -> fields.get(0).requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                Double[] params = new Double[params_labels.length];
                for (int i = 0; i < params.length; i++) {
                    params[i] = Double.parseDouble(fields.get(i).getText());
                }
                return params;
            }
            return null;
        });

        boolean done = false;

        while (!done) {
            try {
                Optional<Double[]> result = dialog.showAndWait();

                return result.get();
            } catch (NumberFormatException e) {
                System.out.println("Invalid input");
            } catch (NoSuchElementException e) {
                System.out.println("No value");
                done = true;
            }
        }
        return null;

    }
    
    public static int getCount() {
        return count;
    }
    
    public static void setCount(int newCount) {
        count = newCount;
    }

    
}
