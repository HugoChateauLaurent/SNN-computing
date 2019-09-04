package graph;

import cells.Connectable;
import edges.IEdge;
import cells.ICell;
import java.io.Serializable;
import java.util.List;

import cells.AbstractCell;
import cells.AbstractDetector;
import cells.LIF;
import cells.AbstractNode;
import cells.Multimeter;
import cells.Raster;
import edges.DetectorEdge;
import edges.Synapse;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import javafx.application.Platform;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Shape;
import javafx.util.Pair;

public class Model implements Serializable {

    private static final long serialVersionUID = 1L;

    private ArrayList<ICell> allCellsSerialization;
    private transient ObservableList<ICell> allCells;
    private transient ObservableList<ICell> addedCells;
    private transient ObservableList<ICell> removedCells;

    private ArrayList<IEdge> allEdgesSerialization;
    private transient ObservableList<IEdge> allEdges;
    private transient ObservableList<IEdge> addedEdges;
    private transient ObservableList<IEdge> removedEdges;

    private transient Graph graph;

    public Model() {
        // clear model, create lists
        clear();
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public void clear() {
        allCells = FXCollections.observableArrayList();
        addedCells = FXCollections.observableArrayList();
        removedCells = FXCollections.observableArrayList();

        allEdges = FXCollections.observableArrayList();
        addedEdges = FXCollections.observableArrayList();
        removedEdges = FXCollections.observableArrayList();
    }

    public void clearAddedLists() {
        addedCells.clear();
        addedEdges.clear();
    }

    public void endUpdate() {
        // merge added & removed cells with all cells
        merge();
    }

    public void updateRng() {
        Double seed = askSeed();
        if (seed != null) {
            // update nodes states
            for (ICell cell : this.allCells) {
                cell.updateRng(new Random(seed.longValue()));
            }
        }

    }

    public Double askSeed() {
        // Create the custom dialog.
        Dialog<Double> dialog = new Dialog();
        dialog.setTitle("Seed for random generators");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        gridPane.add(new Label("Seed:"), 0, 0);
        TextField text = new TextField();
        gridPane.add(text, 1, 0);

        dialog.getDialogPane().setContent(gridPane);
        Platform.runLater(() -> text.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return Double.parseDouble(text.getText());
            }
            return null;
        });

        boolean done = false;

        while (!done) {
            try {
                Optional<Double> result = dialog.showAndWait();
                return result.get();
            } catch (NumberFormatException e) {
                System.out.println("Invalid input");
            } catch (NoSuchElementException e) {
                System.out.println("No value");
                done = true;
            }
        }
        return null;

    }

    public ObservableList<ICell> getAddedCells() {
        return addedCells;
    }

    public ObservableList<ICell> getRemovedCells() {
        return removedCells;
    }

    public ObservableList<ICell> getAllCells() {
        return allCells;
    }
    
    public ObservableList<AbstractDetector> getAllDetectors() {
        ObservableList<AbstractDetector> detectors = FXCollections.observableArrayList();
        for (ICell cell : allCells) {
            if (cell instanceof AbstractDetector) {
                detectors.add((AbstractDetector) cell);
            }
        }
        
        return detectors;
    }

    public ObservableList<IEdge> getAddedEdges() {
        return addedEdges;
    }

    public ObservableList<IEdge> getRemovedEdges() {
        return removedEdges;
    }

    public ObservableList<IEdge> getAllEdges() {
        return allEdges;
    }

    public void addCell(ICell cell) {
        if (cell == null) {
            throw new NullPointerException("Cannot add a null cell");
        }
        addedCells.add(cell);
    }

    public void addSynapse(ICell pre, ICell post, double w, int d) {
        final Synapse connection = new Synapse((AbstractNode) pre, (AbstractNode) post, w, d);
        addEdge((IEdge) connection);
    }
    
    public void addDetectorEdge(AbstractNode target, AbstractDetector detector) {
        final DetectorEdge connection = new DetectorEdge((AbstractNode) target, detector);
        addEdge((IEdge) connection);
    }

    public void addEdge(IEdge edge) {
        if (edge == null) {
            throw new NullPointerException("Cannot add a null edge");
        }
        addedEdges.add(edge);
    }

    public boolean tryToConnect(Connectable post) {
        Connectable pre = null;
        Connectable iCell;

        for (final ICell cell : allCells) {

            if (cell instanceof Connectable && cell != post) {
                iCell = (Connectable) cell;

                if (iCell.getToConnect()) {
                    pre = iCell;
                }
            }
        }
                
                
        if (pre == null) {
            return false;
        } else {
            
            if (!(pre instanceof AbstractDetector) && !(post instanceof AbstractDetector)) {
                
                Pair params = askConnectionParameters();
                if (params != null) {
                    
                    addSynapse((ICell) pre, (ICell) post, (double) params.getKey(), (int) params.getValue());

                    pre.updateToConnect(false);
                    post.updateToConnect(false);
                    graph.endUpdate();

                    return true;
                } else {
                    pre.updateToConnect(false);
                    post.updateToConnect(false);
                    return false;
                }
            } else if (!(pre instanceof AbstractDetector) && post instanceof AbstractDetector) {
                
            
                AbstractNode target = (AbstractNode) pre;
                AbstractCell detector = (AbstractCell) post;
                addDetectorEdge((AbstractNode) target, (AbstractDetector) detector);
                
                pre.updateToConnect(false);
                post.updateToConnect(false);
                graph.endUpdate();
                return true;
                
            } else if (!(post instanceof AbstractDetector) && pre instanceof AbstractDetector) {
                
            
                AbstractNode target = (AbstractNode) post;
                AbstractCell detector = (AbstractCell) pre;
                addDetectorEdge((AbstractNode) target, (AbstractDetector) detector);
                
                pre.updateToConnect(false);
                post.updateToConnect(false);
                graph.endUpdate();
                return true;
                
            } else {
                return false;
            }

        }
    }

    public Pair askConnectionParameters() {
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Connection parameters");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField weight = new TextField();
        TextField delay = new TextField();

        gridPane.add(new Label("Weight:"), 0, 0);
        gridPane.add(weight, 1, 0);
        gridPane.add(new Label("Delay:"), 2, 0);
        gridPane.add(delay, 3, 0);

        dialog.getDialogPane().setContent(gridPane);

        Platform.runLater(() -> weight.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(weight.getText(), delay.getText());
            }
            return null;
        });

        boolean done = false;

        while (!done) {
            try {
                Optional<Pair<String, String>> result = dialog.showAndWait();

                result.ifPresent(pair -> {
                    System.out.println("Weight=" + Double.parseDouble(pair.getKey()) + ", Delay=" + Double.parseDouble(pair.getValue()));
                });
                return new Pair(Double.parseDouble(result.get().getKey()), (int) Integer.parseInt(result.get().getValue()));
            } catch (NumberFormatException e) {
                System.out.println("Invalid input");
            } catch (NoSuchElementException e) {
                System.out.println("No value");
                done = true;
            }
        }
        return null;

    }

    public void merge() {
        // cells
        allCells.addAll(addedCells);
        allCells.removeAll(removedCells);

        addedCells.clear();
        removedCells.clear();

        // edges
        allEdges.addAll(addedEdges);
        allEdges.removeAll(removedEdges);

        addedEdges.clear();
        removedEdges.clear();
    }

    public void run() {
        Integer steps = askSteps();
        if (steps <= 0) {
            System.out.println("Running error: must indicate the number of steps (>0)");
        } else {
            init();
            run(steps);
        }
    }

    public void run(int steps) {
        init_detectors(steps);
        for (int i = 0; i < steps; i++) {
            this.step();
        }
        updateVisualizers();
    }
    
    public void updateVisualizers() {
        for (AbstractDetector detector : getAllDetectors()) {
            detector.getVisualizer().visualize();
        }
    }

    public void init_detectors(int steps) {
        for (AbstractDetector detector : getAllDetectors()) {
            detector.init(steps);
        }
    }

    public void step() {

        // cells step
        for (ICell cell : allCells) {
            cell.step();
        }
        // edges step
        for (IEdge edge : allEdges) {
            edge.step();
        }
    }

    public Integer askSteps() {
        // Create the custom dialog.
        Dialog<Integer> dialog = new Dialog();
        dialog.setTitle("Run simulation");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        gridPane.add(new Label("Number of steps:"), 0, 0);
        TextField text = new TextField();
        gridPane.add(text, 1, 0);

        dialog.getDialogPane().setContent(gridPane);
        Platform.runLater(() -> text.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return Integer.parseInt(text.getText());
            }
            return null;
        });

        boolean done = false;

        while (!done) {
            try {
                Optional<Integer> result = dialog.showAndWait();
                return result.get();
            } catch (NumberFormatException e) {
                System.out.println("Invalid input");
            } catch (NoSuchElementException e) {
                System.out.println("No value");
                done = true;
            }
        }
        return null;
    }

    public Raster createRaster() {
        final Raster raster = new Raster(graph.getApp());
        raster.createVisualizer();
        this.addCell(raster);
        return raster;    }

    public void createPoisson() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void createSpikeTrain() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void createLIF() {
        final Double[] params = LIF.askParameters();
        if (params != null) {
            final ICell lif = new LIF(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7]);
            this.addCell(lif);
        }
    }

    public Multimeter createMultimeter() {
        final Multimeter multimeter = new Multimeter(graph.getApp());
        multimeter.createVisualizer();
        this.addCell(multimeter);
        return multimeter;
    }

    private void init() {
        for (ICell cell : allCells) {
            cell.init();
        }
    }
    
    private void readObject(ObjectInputStream aInputStream)
    throws ClassNotFoundException, IOException {
        aInputStream.defaultReadObject();
        allCells = FXCollections.observableArrayList(allCellsSerialization);
        allEdges = FXCollections.observableArrayList(allEdgesSerialization);

        addedCells = FXCollections.observableArrayList();
        removedCells = FXCollections.observableArrayList();
        addedEdges = FXCollections.observableArrayList();
        removedEdges = FXCollections.observableArrayList();
    
    }

    private void writeObject(ObjectOutputStream aOutputStream)
        throws IOException {
            allCellsSerialization = new ArrayList<ICell>(allCells);
            allEdgesSerialization = new ArrayList<IEdge>(allEdges);
            aOutputStream.defaultWriteObject();
    }
    
    public void setDetectorsApp(MainApp app) {
        for (AbstractDetector detector : getAllDetectors()) {
            detector.setApp(app);
        }
    }
    
    public void createVisualizers() {
        for (AbstractDetector detector : getAllDetectors()) {
            detector.createVisualizer();
        }
    }

}
