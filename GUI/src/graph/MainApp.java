package graph;

import cells.AbstractCell;
import cells.ICell;
import cells.LIF;
import cells.Multimeter;
import edges.Synapse;
import java.awt.Canvas;
import org.abego.treelayout.Configuration.Location;
import layout.AbegoTreeLayout;
import layout.RandomLayout;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
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
    
    private BorderPane window;
    private MenuBar menu; // top
    private PannableCanvas graph_workspace;
    private ScrollPane visualizers;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Spiking simulator GUI");
        
        // full screen
        //primaryStage.setMaximized(true);
        
        window = new BorderPane();

        
        menu = makeMenu();
        window.setTop(menu);
        
        graph = new Graph(this);
        graph_workspace = graph.getCanvas();
        window.setCenter(graph_workspace);
        
        
        
        
        HBox visualizers_hbox = new HBox();
        visualizers_hbox.setSpacing(20);
        visualizers = new ScrollPane();
        //visualizers.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        visualizers.setContent(visualizers_hbox);
        
        window.setBottom(visualizers);
        
        final Scene scene = new Scene(window, 1024, 768);
        scene.getStylesheets().add(getClass().getResource("design.css").toExternalForm());

        primaryStage.setScene(scene);
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
                final Model model = graph.getModel();
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
                final Model model = graph.getModel();
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
                final Model model = graph.getModel();
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
                final Model model = graph.getModel();
                graph.getModel().beginUpdate();
                final Multimeter multimeter = graph.getModel().createMultimeter();
                final HBox visualizers_hbox = (HBox) visualizers.getContent();
                    visualizers_hbox.getChildren().add(new MultimeterVisualizer(multimeter.getApp(), multimeter, true));

                graph.endUpdate();
            }
        });
        menu.getItems().add(item);
        item = new MenuItem("Raster");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                final Model model = graph.getModel();
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
                updateVisualizers();
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
        LIF a = new LIF(.9,0,0,0,1,1,0,0);
        LIF b = new LIF(.9,0,0,0,1,1,0,0);
        graph.getModel().addCell(a);
        graph.getModel().addCell(b);
        graph.getModel().addEdge(new Synapse(a,b,.5,1));

        graph.endUpdate();
        graph.layout(new AbegoTreeLayout(200, 200, Location.Top));
        
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
    public void updateVisualizers() {
        System.out.println("Updating visualizers");
        final HBox visualizers_hbox = (HBox) visualizers.getContent();
        for (Object o : visualizers_hbox.getChildren()) {
            AbstractVisualizer visualizer = (AbstractVisualizer) o;
            visualizer.visualize();
        }
    }
}
