/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visualizer;

import cells.AbstractDetector;
import cells.Raster;
import graph.MainApp;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
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
public class RasterVisualizer extends AbstractVisualizer {

    public RasterVisualizer(MainApp app, AbstractDetector detector, boolean visualize) {
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
        Raster detector = (Raster) this.detector;
        boolean[][] spikes = null;
        try {
            spikes = detector.getSpikes();
            int steps = spikes.length;
        } catch (Exception e) {
            System.out.println("null");
        }

        int n_targets = detector.getTargets().size();

        

        CategoryAxis xAxis;
        NumberAxis yAxis;
        BarChart<String, Number> barChart = null;
        XYChart.Series series;

        for (int target = 0; target < n_targets; target++) {
            xAxis = new CategoryAxis();
            yAxis = new NumberAxis(0,1,1);
            xAxis.labelProperty().set("Steps");
            yAxis.labelProperty().set("Spike");
            
            barChart = new BarChart<String, Number>(xAxis, yAxis);

            series = new XYChart.Series();
            for (int t = 0; t < spikes[target].length; t++) {
                if (spikes != null) {
                    if (spikes[target][t]) {
                        series.getData().add(new XYChart.Data(Integer.toString(t), 1));
                    } else {
                        series.getData().add(new XYChart.Data(Integer.toString(t), 0));
                    }

                }
            }
            barChart.getData().add(series);
            barChart.legendVisibleProperty().set(false);
            plotBox.getChildren().add(barChart);
            barChart.setMaxHeight(70);

        }

    }

}
