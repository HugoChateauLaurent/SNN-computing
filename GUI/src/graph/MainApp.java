package graph;

import cells.AbstractCell;
import cells.ICell;
import cells.LIF;
import cells.Multimeter;
import cells.Raster;
import edges.DetectorEdge;
import edges.Synapse;
import java.awt.Canvas;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import layout.RandomLayout;

import javafx.application.Application;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import serialization.Serialization;
import visualizer.AbstractVisualizer;
import visualizer.MultimeterVisualizer;

public class MainApp extends Application {

    private Graph graph = new Graph(this);
    
    private static File defaultFile = new File("default.network");
    
    private SplitPane window;
    private MenuBar menu; // top
    private ScrollPane visualizers;
    private File saveFile;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        saveFile = defaultFile;
        updateTitle();
        
        // full screen
        //primaryStage.setMaximized(true);
        
        window = new SplitPane();
        window.setOrientation(Orientation.VERTICAL);
        
        menu = makeMenu(primaryStage);
        
        System.out.println(graph);
        System.out.println(graph.getCanvas());
        PannableCanvas graph_workspace = graph.getCanvas();
        
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
        
        //exampleElements();
        updateHierarchy();
        
    }

    public Graph getGraph() {
        return graph;
    }
    
    public SplitPane getWindow() {
        return window;
    }
    
    public ScrollPane getVisualizers() {
        return visualizers;
    }
    
    public void updateHierarchy() {
        menu.toFront();
    }

    private MenuBar makeMenu(Stage stage) {
        MainApp app = this;
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("File");
        
        MenuItem item = new MenuItem("New");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                System.out.println("Opening default network");
                Serialization serialization = open_file(stage, new File("default.network"));
                if (serialization != null) {
                    System.out.println("Opening model");
                    load_serialization(serialization);
                }
            }
        });
        menu.getItems().add(item);
        
        item = new MenuItem("Open...");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                System.out.println("Open");
                Serialization serialization = open_file(stage);
                if (serialization != null) {
                    System.out.println("Opening model");
                    load_serialization(serialization);
                }
            }
        });
        menu.getItems().add(item);

        item = new MenuItem("Save");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                System.out.println("Save");
                if (saveFile == null || saveFile.getName()==defaultFile.getName()) {
                    System.out.println("Calling save as");
                    app.saveAs(stage);
                }
                app.save();
            }
        });
        menu.getItems().add(item);
        menuBar.getMenus().add(menu);
        
        item = new MenuItem("Save as");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                System.out.println("Save as");
                app.saveAs(stage);
                app.save();
            }
        });
        menu.getItems().add(item);
        
        menu = new Menu("Create");
        
        item = new MenuItem("Nodes");
        item.setDisable(true);
        item.getStyleClass().add("context-menu-title");
        menu.getItems().add(item);
        
        item = new MenuItem("LIF");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                graph.getModel().createLIF();
                graph.endUpdate();
            }
        });
        menu.getItems().add(item);
        
        item = new MenuItem("Spike train");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                graph.getModel().createSpikeTrain();
                graph.endUpdate();
            }
        });
        item.setDisable(true);
        menu.getItems().add(item);
        
        item = new MenuItem("Poisson");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                graph.getModel().createPoisson();
                graph.endUpdate();
            }
        });
        item.setDisable(true);
        menu.getItems().add(item);
        
        menu.getItems().add(new SeparatorMenuItem());
        
        item = new MenuItem("Detectors");
        item.setDisable(true);
        item.getStyleClass().add("context-menu-title");
        menu.getItems().add(item);
        
        item = new MenuItem("Multimeter");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                graph.getModel().createMultimeter();
                graph.endUpdate();
            }
        });
        menu.getItems().add(item);
        
        item = new MenuItem("Raster");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
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
        
        graph.layout(new RandomLayout());
        
        /*Ellipse ellipse_blue = new Ellipse(30,30);
        ellipse_blue.setStyle("-fx-fill: radial-gradient(center 100% 50%, radius 100%, blue, white 30%);"
                + "-fx-stroke: radial-gradient(center 100% 50%, radius 100%, blue, black 30%);");
        ellipse_blue.strokeWidthProperty().set(5);
        
        Ellipse ellipse_red = new Ellipse(30,30);
        ellipse_red.setStyle("-fx-fill: radial-gradient(center 0% 50%, radius 100%, red, white 30%);"
                + "-fx-stroke: radial-gradient(center 0% 50%, radius 100%, red, black 30%);");
        ellipse_red.strokeWidthProperty().set(5);
        
        ellipse_blue.relocate(0, -20);
        ellipse_red.relocate(100, -20);
        
        
        
        graph.getCanvas().getChildren().addAll(ellipse_blue, ellipse_red);*/
        
        
    }
    
    public static void main(String[] args) {
        launch(args);
    }

    private void saveAs(Stage stage) {
        FileChooser fileChooser = new FileChooser();
 
        //Set extension filter for text files
        //FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        //fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File newSaveFile = fileChooser.showSaveDialog(stage);
        if (newSaveFile != null && saveFile.getName() != defaultFile.getName()) {
            saveFile = newSaveFile;
            updateTitle();
        } else {
            System.out.println("Cannot save as: "+newSaveFile);
        }
            
    }

    private void save() {
        try {
            if (saveFile != null && saveFile.getName() != defaultFile.getName()) {
                System.out.println("Save: "+saveFile.getName());
                FileOutputStream fileOut = new FileOutputStream(saveFile);
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
                Serialization serialization = new Serialization(graph.getModel());
                objectOut.writeObject(serialization);
                objectOut.close();
                System.out.println("The Object was succesfully written to a file");
            } else {
                System.out.println("Cannot save file: "+saveFile.getName());
            }
 
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private Serialization open_file(Stage stage, File openFile) {
 
        try {
            if(openFile != null) {
 
                FileInputStream fileIn = new FileInputStream(openFile);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);

                Serialization serialization = (Serialization) objectIn.readObject();

                System.out.println("The Object has been read from the file");
                objectIn.close();
                saveFile = openFile;
                updateTitle();
                
                return serialization;
            }
 
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    private Serialization open_file(Stage stage) {
 
        try {
            
            FileChooser fileChooser = new FileChooser();
            //Set extension filter for text files
            //FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
            //fileChooser.getExtensionFilters().add(extFilter);
            //Show save file dialog
            File openFile = fileChooser.showOpenDialog(stage);
            
            if(openFile != null) {
 
                open_file(stage, openFile);
            }
 
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public void load_serialization(Serialization serialization) {
        Model model = serialization.getModel();
        graph = new Graph(this, model);
        model.setGraph(graph);
        model.setDetectorsApp(this);
        model.createVisualizers();
        
        graph.endUpdate();
        
        
        PannableCanvas graph_workspace = graph.getCanvas();
        
        HBox visualizers_hbox = new HBox();
        visualizers_hbox.setSpacing(20);
        visualizers = new ScrollPane();
        
        //visualizers.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        visualizers.setContent(visualizers_hbox);
        
        window = new SplitPane();
        window.setOrientation(Orientation.VERTICAL);
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
    }

    private void updateTitle() {
        primaryStage.setTitle("Spiking simulator GUI - "+ saveFile.getName());
    }
}
