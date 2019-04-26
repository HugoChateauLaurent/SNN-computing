/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visualizer;

import cells.Detector;
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
    
    public MultimeterVisualizer (MainApp app, Detector detector) {
        super(app, detector);
        visualize();
    }
    
    public void visualize() {
        System.out.println("vis");
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
        LineChart<Number,Number> lineChart;
        XYChart.Series series;
        
        xAxis = new NumberAxis();
        yAxis = new NumberAxis();
        lineChart = new LineChart<Number,Number>(xAxis,yAxis);
        series = new XYChart.Series();
        series.getData().add(new XYChart.Data(1,1));
        series.getData().add(new XYChart.Data(2,7));
        series.getData().add(new XYChart.Data(8,4));
        lineChart.getData().add(series);
        plotBox.getChildren().add(lineChart);
        
        xAxis = new NumberAxis();
        yAxis = new NumberAxis();
        lineChart = new LineChart<Number,Number>(xAxis,yAxis);
        series = new XYChart.Series();
        series.getData().add(new XYChart.Data(1,1));
        series.getData().add(new XYChart.Data(2,7));
        series.getData().add(new XYChart.Data(8,4));
        lineChart.getData().add(series);
        plotBox.getChildren().add(lineChart);
        
        xAxis = new NumberAxis();
        yAxis = new NumberAxis();
        lineChart = new LineChart<Number,Number>(xAxis,yAxis);
        series = new XYChart.Series();
        series.getData().add(new XYChart.Data(1,1));
        series.getData().add(new XYChart.Data(2,7));
        series.getData().add(new XYChart.Data(8,4));
        lineChart.getData().add(series);
        plotBox.getChildren().add(lineChart);
        
        xAxis = new NumberAxis();
        yAxis = new NumberAxis();
        lineChart = new LineChart<Number,Number>(xAxis,yAxis);
        series = new XYChart.Series();
        series.getData().add(new XYChart.Data(1,1));
        series.getData().add(new XYChart.Data(2,7));
        series.getData().add(new XYChart.Data(8,4));
        lineChart.getData().add(series);
        plotBox.getChildren().add(lineChart);
        
        xAxis = new NumberAxis();
        yAxis = new NumberAxis();
        lineChart = new LineChart<Number,Number>(xAxis,yAxis);
        series = new XYChart.Series();
        series.getData().add(new XYChart.Data(1,1));
        series.getData().add(new XYChart.Data(2,7));
        series.getData().add(new XYChart.Data(8,4));
        lineChart.getData().add(series);
        plotBox.getChildren().add(lineChart);
        
        xAxis = new NumberAxis();
        yAxis = new NumberAxis();
        lineChart = new LineChart<Number,Number>(xAxis,yAxis);
        series = new XYChart.Series();
        series.getData().add(new XYChart.Data(1,1));
        series.getData().add(new XYChart.Data(2,7));
        series.getData().add(new XYChart.Data(8,4));
        lineChart.getData().add(series);
        plotBox.getChildren().add(lineChart);
        
        xAxis = new NumberAxis();
        yAxis = new NumberAxis();
        lineChart = new LineChart<Number,Number>(xAxis,yAxis);
        series = new XYChart.Series();
        series.getData().add(new XYChart.Data(1,1));
        series.getData().add(new XYChart.Data(2,7));
        series.getData().add(new XYChart.Data(8,4));
        lineChart.getData().add(series);
        plotBox.getChildren().add(lineChart);
        
        for (int target=0; target<n_targets; target++) {
            xAxis = new NumberAxis();
            yAxis = new NumberAxis();
            lineChart = new LineChart<Number,Number>(xAxis,yAxis);

            series = new XYChart.Series();
            for (int t=0; t<n_targets; t++) {
                if (V != null){
                    series.getData().add(new XYChart.Data(t, V[target][t]));
                }
            }
            lineChart.getData().add(series);
            plotBox.getChildren().add(lineChart);
            
        }
        
        lineChart.prefWidthProperty().bind(plotBox.widthProperty());
        plotBox.prefWidthProperty().bind(plots.widthProperty().subtract(20));

            
        
        
       
        
        
        
    }
    
    
}
