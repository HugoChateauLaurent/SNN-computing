package graph;

import cells.AbstractCell;
import cells.ICell;
import cells.LIF;
import cells.Multimeter;
import cells.Raster;
import edges.DetectorEdge;
import edges.Synapse;
import java.awt.Canvas;
import org.abego.treelayout.Configuration.Location;
import layout.AbegoTreeLayout;
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
                graph.getModel().beginUpdate();
                graph.getModel().createLIF();
                graph.endUpdate();
            }
        });
        menu.getItems().add(item);
        item = new MenuItem("Spike train");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                graph.getModel().beginUpdate();
                graph.getModel().createSpikeTrain();
                graph.endUpdate();
            }
        });
        menu.getItems().add(item);
        item = new MenuItem("Poisson");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                graph.getModel().beginUpdate();
                graph.getModel().createPoisson();
                graph.endUpdate();
            }
        });
        menu.getItems().add(item);
        item = new MenuItem("Multimeter");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                graph.getModel().beginUpdate();
                graph.getModel().createMultimeter();
                graph.endUpdate();
            }
        });
        menu.getItems().add(item);
        item = new MenuItem("Raster");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                graph.getModel().beginUpdate();
                graph.getModel().createRaster();
                graph.endUpdate();
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

        item = new MenuItem("Abego");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                graph.layout(new AbegoTreeLayout());
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
        graph.beginUpdate();
        
        //create neurons with constant input
        LIF neuron = new LIF(1,0,0,0,1,1,0.3,0);
        LIF neuron2 = new LIF(0.8,0,0,0,1,1,0.3,0);
        graph.getModel().addCell(neuron);
        graph.getModel().addCell(neuron2);
        
        //create detectors
        Raster raster = graph.getModel().createRaster();
        Multimeter multi = graph.getModel().createMultimeter();
        
        //connect detector and neurons
        graph.getModel().addEdge(new DetectorEdge(neuron,raster));
        graph.getModel().addEdge(new DetectorEdge(neuron2,raster));
        graph.getModel().addEdge(new DetectorEdge(neuron,multi));
        graph.getModel().addEdge(new DetectorEdge(neuron2,multi));
        
        //open visualizers
        raster.displayVisualizer();
        multi.displayVisualizer();
        
        Group group = new Group();
        group.getChildren().addAll(neuron.getGraphic(graph), neuron2.getGraphic(graph));

        graph.endUpdate();
        graph.layout(new AbegoTreeLayout(200, 200, Location.Top));
        
        
        
        
        
        
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
