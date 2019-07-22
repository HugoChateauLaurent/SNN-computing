/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visualizer;

import cells.AbstractDetector;
import cells.Multimeter;
import graph.MainApp;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author ubuntu
 */
public class MultimeterVisualizer extends AbstractVisualizer {
    
    public MultimeterVisualizer (MainApp app, AbstractDetector detector, boolean visualize) {
        super(app, detector);
        if (visualize) {
            visualize();
        }
    }
    
    @Override
    public void visualize() {
        VBox plotBox = new VBox(20);
        ScrollPane plots = new ScrollPane();
        plots.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        plots.setContent(plotBox);
        setCenter(plots);
        Multimeter detector = (Multimeter) this.detector; 
        double[][] V=null;
        try {
            V = detector.getV();
            int steps = V.length;
        } catch (Exception e) {
            System.out.println("null");
        }
        
        int n_targets = detector.getTargets().size();
        
        NumberAxis xAxis;
        NumberAxis yAxis;
        LineChart<Number,Number> lineChart = null;
        XYChart.Series series;
        
        
        for (int target=0; target<n_targets; target++) {
            xAxis = new NumberAxis();
            yAxis = new NumberAxis();
            xAxis.labelProperty().set("Steps");
            yAxis.labelProperty().set("Voltage");
            lineChart = new LineChart<Number,Number>(xAxis,yAxis);

            series = new XYChart.Series();
            for (int t=0; t<V[target].length; t++) {
                if (V != null){
                    series.getData().add(new XYChart.Data(t, V[target][t]));
                }
            }
            lineChart.getData().add(series);
            lineChart.legendVisibleProperty().set(false);
            plotBox.getChildren().add(lineChart);
            lineChart.setMaxHeight(70);
            
        }

            
        
        
       
        
        
        
    }
    
    
}
