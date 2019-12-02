package graph;

import cells.AbstractCell;
import cells.AbstractDetector;
import cells.ICell;
import cells.LIF;
import cells.Module;
import cells.Multimeter;
import cells.Raster;
import edges.AbstractEdge;
import edges.DetectorEdge;
import edges.IEdge;
import edges.Synapse;
import java.awt.Canvas;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import layout.RandomLayout;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
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
import javafx.util.Pair;
import serialization.INet;
import serialization.Serialization;
import visualizer.AbstractVisualizer;
import visualizer.MultimeterVisualizer;

public class MainApp extends Application {

    private Graph graph = new Graph(this);
    
    private static Pair<String,String> EXTENSION_NAMES = new Pair(".net", ".inet");
    private static FileChooser.ExtensionFilter EXTENSION_net = new FileChooser.ExtensionFilter("GUI files (*"+EXTENSION_NAMES.getKey()+")","*"+EXTENSION_NAMES.getKey());
    private static FileChooser.ExtensionFilter EXTENSION_inet = new FileChooser.ExtensionFilter("Simulator-independent files (*"+EXTENSION_NAMES.getValue()+")","*"+EXTENSION_NAMES.getValue());
    private static File NETWORKS_DIRECTORY = new File("./networks");
    private static File DEFAULT_FILE = new File(NETWORKS_DIRECTORY+"/default.net");
    
    private SplitPane window;
    private MenuBar menu; // top
    private ScrollPane visualizers;
    private File saveFile;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        saveFile = DEFAULT_FILE;
        updateTitle();
        
        // full screen
        //primaryStage.setMaximized(true);
        
        window = new SplitPane();
        window.setOrientation(Orientation.VERTICAL);
        
        menu = makeMenu(primaryStage);
        
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

    private MenuBar makeMenu(Stage stage) {
        MainApp app = this;
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("File");
        
        MenuItem item = new MenuItem("New");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                Serialization serialization = (Serialization) openFile(DEFAULT_FILE);
                if (serialization != null) {
                    load_serialization(serialization);
                } else {
                    System.out.println("Null serialization");
                }
            }
        });
        menu.getItems().add(item);
        
        item = new MenuItem("Open");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                Object openFile = chooseAndOpenFile(stage, false);
                if (openFile instanceof Serialization) {
                    load_serialization((Serialization) openFile);
                } else if (openFile instanceof BufferedReader) {
                    load_inet((BufferedReader) openFile, false);
                } else {
                    System.out.println("Can't open file");
                }
            }
        });
        menu.getItems().add(item);
        
        item = new MenuItem("Append .inet to current network");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                Object openFile = chooseAndOpenFile(stage, true);
                if (openFile instanceof BufferedReader) {
                    load_inet((BufferedReader) openFile, true);
                } else {
                    System.out.println("Can't open file");
                }
            }
        });
        menu.getItems().add(item);

        item = new MenuItem("Save");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                boolean save = true;
                if (saveFile == null || saveFile.getName().equals(DEFAULT_FILE.getName())) {
                    save = app.saveAs(stage);
                }
                if (save) {
                    app.save();
                }
            }
        });
        menu.getItems().add(item);
        
        
        item = new MenuItem("Save as");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                boolean save = app.saveAs(stage);
                if (save) {
                    app.save();
                }
            }
        });
        menu.getItems().add(item);
        
        menuBar.getMenus().add(menu);
        
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
            }
        });
        menu.getItems().add(item);
        
        item = new MenuItem("Input train");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                graph.getModel().createInputTrain();
            }
        });
        menu.getItems().add(item);
        
        item = new MenuItem("Random spiker");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                graph.getModel().createRandomSpiker();
            }
        });
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
            }
        });
        menu.getItems().add(item);
        
        item = new MenuItem("Raster");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                graph.getModel().createRaster();
            }
        });
        menu.getItems().add(item);
        
        item = new MenuItem("Modules");
        item.setDisable(true);
        item.getStyleClass().add("context-menu-title");
        menu.getItems().add(item);
        
        item = new MenuItem("Module");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                graph.getModel().createModule();
            }
        });
        menu.getItems().add(item);
        
        item = new MenuItem("Import module");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                File openFile = (File) chooseModule(primaryStage);
                if (openFile != null) {
                    importModule(openFile);
                }
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
        
        menu = new Menu("About");
        item = new MenuItem("Help");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                Alert help_dialog = new Alert(AlertType.INFORMATION);
                help_dialog.setTitle("Help");
                help_dialog.setHeaderText("Hotkeys and shortcuts");
                help_dialog.setContentText(""
                        + "- Right click on an element to open its menu\n"
                        + "- Scroll to zoom in and out\n"
                        + "- Hold right click and drag to navigate\n"
                        + "- To connect A to B, double click on A first,\n"
                        + "   then double click on B\n");
                help_dialog.showAndWait();
            }
        });
        menu.getItems().add(item);
        
        menuBar.getMenus().add(menu);
        
        return menuBar;
    }
    
    public void exampleElements() {
        graph.beginUpdate();
        
        //create neurons with constant input
        LIF neuron = new LIF(graph.getModel(), 1,0,0,0,1,1,0.3,0);
        LIF neuron2 = new LIF(graph.getModel(), 0.8,0,0,0,1,1,0.3,0);
        graph.getModel().addCell(neuron);
        graph.getModel().addCell(neuron2);
        
        //create detectors
        Raster raster = graph.getModel().createRaster();
        Multimeter multi = graph.getModel().createMultimeter();
        
        //connect detector and neurons
        graph.getModel().addEdge(new DetectorEdge(graph.getModel(), neuron,raster));
        graph.getModel().addEdge(new DetectorEdge(graph.getModel(), neuron2,raster));
        graph.getModel().addEdge(new DetectorEdge(graph.getModel(), neuron,multi));
        graph.getModel().addEdge(new DetectorEdge(graph.getModel(), neuron2,multi));
        
        //open visualizers
        raster.displayVisualizer();
        multi.displayVisualizer();
        
        Group group = new Group();
        group.getChildren().addAll(neuron.getGraphic(graph), neuron2.getGraphic(graph));

        
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

    private boolean saveAs(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(NETWORKS_DIRECTORY);
        fileChooser.setInitialFileName("my_network.net");
        fileChooser.getExtensionFilters().add(EXTENSION_net);
        fileChooser.getExtensionFilters().add(EXTENSION_inet);

        //Show save file dialog
        File newSaveFile = fileChooser.showSaveDialog(stage);
        if (newSaveFile != null && (newSaveFile.getName().endsWith(EXTENSION_NAMES.getKey()) || newSaveFile.getName().endsWith(EXTENSION_NAMES.getValue())) && !newSaveFile.getName().equals(DEFAULT_FILE.getName())) {
            saveFile = newSaveFile;
            updateTitle();
            setNETWORKS_DIRECTORY(saveFile, true);
            
            return true;
        } else if (newSaveFile.getName().equals(DEFAULT_FILE.getName())) {		
                System.out.println("Cannot override default file");		
        } else if (!(newSaveFile.getName().endsWith(EXTENSION_NAMES.getKey()) || newSaveFile.getName().endsWith(EXTENSION_NAMES.getValue()))) {
            System.out.println("Wrong file extension. File name must be *" + EXTENSION_NAMES.getKey()+" or *"+ EXTENSION_NAMES.getValue());
        } else {
            System.out.println("Cannot save as: " + newSaveFile);
        }
        return false;
            
    }

    private void save() {
        try {
            
            if (saveFile.getName().endsWith(EXTENSION_NAMES.getKey())) {
                FileOutputStream fileOut = new FileOutputStream(saveFile);
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
                Serialization serialization = new Serialization(graph.getModel());
                objectOut.writeObject(serialization);
                objectOut.close();
            } else {
                PrintWriter out = new PrintWriter(saveFile);
                INet inet_interface = new INet(graph);
                out.println(inet_interface.write());
                out.close();
            }
            
 
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private Object openFile(File openFile) {
 
        try {
            if(openFile != null) {
                if (openFile.getName().endsWith(EXTENSION_NAMES.getKey())) {

                    FileInputStream fileIn = new FileInputStream(openFile);
                    ObjectInputStream objectIn = new ObjectInputStream(fileIn);

                    Object obj = (Serialization) objectIn.readObject();
                    objectIn.close();

                    if (obj instanceof Serialization) {
                        saveFile = openFile;
                        updateTitle();
                        setNETWORKS_DIRECTORY(saveFile, true);
                        return (Serialization) obj;
                    } else {
                        System.out.println("Cannot load object, not a Serialization: "+obj);
                        return null;
                    }
                } else if (openFile.getName().endsWith(EXTENSION_NAMES.getValue())) {
                    saveFile = openFile;
                    updateTitle();
                    setNETWORKS_DIRECTORY(saveFile, true);
                    
                    BufferedReader br = new BufferedReader(new FileReader(openFile));
                    return br;
                    
                } else {
                    System.out.println("Unknwown file extension");
                }
            }
 
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    private void importModule(File openFile) {
 
        try {

            FileInputStream fileIn = new FileInputStream(openFile);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            Object obj = (Module) objectIn.readObject();
            objectIn.close();
            
                

            if (obj instanceof Module) {
                
                Module module = (Module) obj;
                module.setParentModule(null);
                module.setCells(FXCollections.observableSet(module.getSerializableCells()));
                int moduleZLevel = module.getZLevel();
                int maxZLevel = -1;
                int zLevel;
                
                ArrayList<AbstractCell> cells = new ArrayList(module.getCellsAndChildren(true));
                cells.add(0, module);
                Module.setUpdateZLayout(false);
                
                for (AbstractCell cell : cells) {
                    
                    cell.setModel(this.graph.getModel());
                    
                    //update ID and increase class count
                    cell.setID(cell.getClassCount());
                    cell.increaseCount();
                    
                    zLevel = cell.getZLevel() - moduleZLevel;
                    cell.setZLevel(zLevel);
                    
                    cell.createView();
                    graph.addCell(cell, true);
                    
                    if (cell instanceof AbstractDetector){
                        ((AbstractDetector) cell).createVisualizer();
                    }
                
                    
                }
                
                module.fixLayout();
                
                for (AbstractEdge edge : module.getSerializableInternalEdges()) {
                    
                    edge.setModel(this.graph.getModel());
                    
                    //update ID and increase class count
                    edge.setID(edge.getClassCount());
                    edge.increaseCount();
                    
                    edge.createView();
                    graph.addEdge(edge, true, false);
                    
                }
                
                Module.setUpdateZLayout(true);
                graph.updateZLayout();


                
            } else {
                System.out.println("Cannot load module: "+obj);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void setNETWORKS_DIRECTORY(File file, boolean take_parent) {
        Path path = Paths.get(file.toString());
        if (take_parent) {
            path = path.getParent();
        }
        NETWORKS_DIRECTORY = path.toFile();
    }
    
    public File getNETWORKS_DIRECTORY() {
        return NETWORKS_DIRECTORY;
    }
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    private Object chooseAndOpenFile(Stage stage, boolean append_inet) {
 
        try {
            
            FileChooser fileChooser = new FileChooser();
            if (!append_inet) {
                fileChooser.getExtensionFilters().add(EXTENSION_net);
            }
            fileChooser.getExtensionFilters().add(EXTENSION_inet);
            fileChooser.setInitialDirectory(NETWORKS_DIRECTORY);
            
            File openFile = fileChooser.showOpenDialog(stage);
            
            if(openFile != null) {
                return openFile(openFile);
            } else {
                System.out.println("Null file");
            }
 
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    private Object chooseModule(Stage stage) {
 
        try {
            
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(Module.getEXTENSION());
            
            File openFile = fileChooser.showOpenDialog(stage);
            
            if(openFile != null) {
                return openFile;
            } else {
                System.out.println("Null file");
            }
 
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public void load_serialization(Serialization serialization) {
        
        serialization.readCounts();
        
        Model model = serialization.getModel();
        
        model.setAllCells(FXCollections.observableArrayList(model.getAllCellsSerialization()));
        model.setAllEdges(FXCollections.observableArrayList(model.getAllEdgesSerialization()));
        
        // updating cells of modules manually because Module.readObject() is not working
        for (ICell cell : model.getAllCells()) {
            if (cell instanceof Module) {
                ((Module) cell).setCells(FXCollections.observableSet(((Module) cell).getSerializableCells()));
            }
        }  
        
        graph = new Graph(this, model);
        model.setGraph(graph);
        
        Module.setUpdateZLayout(false);
        for (ICell c : model.getAllCells()) {
            c.createView();
        } for (IEdge e : model.getAllEdges()) {
            e.createView();
        }
        Module.setUpdateZLayout(true);
        graph.updateZLayout();
        
        for (ICell cell : model.getAllCells()) {
            if (cell instanceof Module) {
                if (cell.getZLevel() == 0) {
                    ((Module) cell).fixLayout();
                }
            }
        }  
        
        graph.addCells(model.getAllCells(), false);
        graph.addEdges(model.getAllEdges(), false);
        
        
        
        model.createVisualizers(); 
        
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
        
        graph.layout(new RandomLayout());
    }
    
    public void load_inet(BufferedReader cmds, boolean append) {
        if (!append) {
            graph = new Graph(this);
        }
        Model model = graph.getModel();
        INet inet = new INet(graph);
        inet.exec(cmds);
        
        model.createVisualizers(); 
        
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
        
        graph.layout(new RandomLayout());
    }

    private void updateTitle() {
        primaryStage.setTitle("Spiking simulator GUI - "+ saveFile.getName());
    }
}
