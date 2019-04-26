/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visualizer;

import cells.Detector;
import graph.MainApp;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author ubuntu
 */
public abstract class AbstractVisualizer extends BorderPane {
    
    protected MainApp app;
    protected Detector detector;
    
    public AbstractVisualizer (MainApp app, Detector detector) {
        super();
        System.out.println("super");
        this.app = app;
        this.detector = detector;
        Button closeButton = new Button("x");
        closeButton.setOnAction(e -> {
            close();
        });
        
        
        closeButton.prefWidthProperty().bind(this.widthProperty());
        
        
        setTop(closeButton);
        
        this.setPrefHeight(300);
        this.setPrefWidth(500);
        
    }
    
    public void close() {
        HBox visualizers_hbox = (HBox) app.getVisualizers().getContent();
        visualizers_hbox.getChildren().remove(this);
        detector.setVisualizer(null);
    }
    
    
}
