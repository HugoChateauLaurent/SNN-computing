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
public class AbstractVisualizer extends BorderPane {
    
    private MainApp app;
    private Detector detector;
    
    public AbstractVisualizer (MainApp app) {
        super();
        this.app = app;
        Button closeButton = new Button("x");
        closeButton.setOnAction(e -> {
            close();
        });
        
        setBottom(new Rectangle(500,200));
        
        closeButton.prefWidthProperty().bind(this.widthProperty());
        
        
        setTop(closeButton);
    }
    
    public void close() {
        HBox visualizers_hbox = (HBox) app.getVisualizers().getContent();
        visualizers_hbox.getChildren().remove(this);
    }
    
    
}
