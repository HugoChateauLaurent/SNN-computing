package graph;

import cells.AbstractCell;
import cells.ICell;
import cells.LIF;
import cells.Multimeter;
import cells.Raster;
import edges.DetectorEdge;
import edges.IEdge;
import edges.Synapse;
import java.awt.Canvas;
import layout.RandomLayout;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import visualizer.AbstractVisualizer;
import visualizer.MultimeterVisualizer;

public class MainApp extends Application {

    private Graph graph;
    
    private SplitPane window;
    private MenuBar menu; // top
    private PannableCanvas graph_workspace;
    private ScrollPane visualizers;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Spiking simulator GUI");
        
        
        // full screen
        //primaryStage.setMaximized(true);
        
        window = new SplitPane();
        window.setOrientation(Orientation.VERTICAL);

        
        menu = makeMenu();
        
        graph = new Graph(this);
        graph_workspace = graph.getCanvas();
        
        
        
        
        HBox visualizers_hbox = new HBox();
        visualizers_hbox.setSpacing(20);
        visualizers = new ScrollPane();

        
        //visualizers.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        visualizers.setContent(visualizers_hbox);
        
        
        window.getItems().add(menu);
        window.getItems().add(graph_workspace);
        window.getItems().add(visualizers);
        window.setDividerPosition(0, 0f);
        window.setDividerPosition(1, 0.7f);
        
        final Scene scene = new Scene(window, 1200, 900);
        scene.getStylesheets().add(getClass().getResource("design.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setMinHeight(500);
        primaryStage.setMinWidth(750);
        primaryStage.show();

        exampleElements();        
        updateHierarchy();
    }

    public Graph getGraph() {
        return graph;
    }
    
    public ScrollPane getVisualizers() {
        return visualizers;
    }
    
    public void updateHierarchy() {
        menu.toFront();
    }

    private MenuBar makeMenu() {
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("File");
        MenuItem item = new MenuItem("Open");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                System.out.println("Open");
                System.out.println("Open not implemented");
            }
        });
        menu.getItems().add(item);

        item = new MenuItem("Save as");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                System.out.println("Save as");
                System.out.println("Save as not implemented");
            }
        });
        menu.getItems().add(item);

        item = new MenuItem("Save");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                System.out.println("Save");
                System.out.println("Save not implemented");
            }
        });
        menu.getItems().add(item);
        menuBar.getMenus().add(menu);

        menu = new Menu("Create");
        item = new MenuItem("LIF");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                ICell cell = graph.getModel().createLIF();
                graph.addCell(cell);
            }
        });
        menu.getItems().add(item);
        item = new MenuItem("Spike train");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                ICell cell = graph.getModel().createSpikeTrain();
                graph.addCell(cell);
            }
        });
        menu.getItems().add(item);
        item = new MenuItem("Poisson");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                ICell cell = graph.getModel().createPoisson();
                graph.addCell(cell);
            }
        });
        menu.getItems().add(item);
        item = new MenuItem("Multimeter");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                ICell cell = graph.getModel().createMultimeter();
                graph.addCell(cell);
            }
        });
        menu.getItems().add(item);
        item = new MenuItem("Raster");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                ICell cell = graph.getModel().createRaster();
                graph.addCell(cell);
            }
        });
        menu.getItems().add(item);
        menuBar.getMenus().add(menu);

        menu = new Menu("Layout");
        item = new MenuItem("Random");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                graph.layout(new RandomLayout());
            }
        });
        menu.getItems().add(item);
        
        
        menuBar.getMenus().add(menu);

        menu = new Menu("Controls");
        item = new MenuItem("Run");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                graph.getModel().run();
            }
        });
        menu.getItems().add(item);
        item = new MenuItem("Set seed");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                graph.getModel().updateRng();
            }
        });
        menu.getItems().add(item);
        menuBar.getMenus().add(menu);
        
        return menuBar;
    }
    
    public void exampleElements() {
        
        //create neurons with constant input
        LIF neuron = new LIF(1,0,0,0,1,1,0.3,0);
        graph.getModel().addCell(neuron);
        graph.addCell(neuron);
        
        LIF neuron2 = new LIF(0.8,0,0,0,1,1,0.3,0);
        graph.getModel().addCell(neuron2);
        graph.addCell(neuron2);
        
        //create detectors
        Raster raster = graph.getModel().createRaster();
        Multimeter multi = graph.getModel().createMultimeter();
        
        //connect detector and neurons
        IEdge edge = new DetectorEdge(neuron,raster);
        graph.getModel().addEdge(edge);
        graph.addEdge(edge);
        
        edge = new DetectorEdge(neuron2,raster);
        graph.getModel().addEdge(edge);
        graph.addEdge(edge);
        
        edge = new DetectorEdge(neuron,multi);
        graph.getModel().addEdge(edge);
        graph.addEdge(edge);
        
        edge = new DetectorEdge(neuron2,multi);
        graph.getModel().addEdge(edge);
        graph.addEdge(edge);
        
        //open visualizers
        raster.displayVisualizer();
        multi.displayVisualizer();

        
        graph.addCell(raster);
        graph.addCell(multi);
        graph.layout(new RandomLayout());
        
        
        
        
        
        
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
