package graph;

import edges.IEdge;
import cells.ICell;
import java.io.Serializable;
import java.util.List;

import cells.AbstractCell;
import cells.AbstractDetector;
import cells.LIF;
import cells.AbstractNode;
import cells.InputTrain;
import cells.Multimeter;
import cells.RandomSpiker;
import cells.Raster;
import cells.Targetable;
import edges.DetectorEdge;
import edges.Synapse;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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

    private ArrayList<IEdge> allEdgesSerialization;
    private transient ObservableList<IEdge> allEdges;

    private transient Graph graph;

    public Model() {
        // clear model, create lists
        clear();
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public Graph getGraph() {
        return graph;
    }

    public void clear() {
        allCells = FXCollections.observableArrayList();
        allEdges = FXCollections.observableArrayList();
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

    public ObservableList<AbstractNode> getAllNodes() {
        ObservableList<AbstractNode> nodes = FXCollections.observableArrayList();
        for (ICell cell : allCells) {
            if (cell instanceof AbstractNode) {
                nodes.add((AbstractNode) cell);
            }
        }

        return nodes;
    }

    public ObservableList<IEdge> getAllEdges() {
        return allEdges;
    }
    
    public ObservableList<Synapse> getAllSynapses() {
        ObservableList<Synapse> synapses = FXCollections.observableArrayList();
        for (IEdge edge : allEdges) {
            if (edge instanceof Synapse) {
                synapses.add((Synapse) edge);
            }
        }

        return synapses;
    }

    public void addCell(ICell cell) {
        allCells.add(cell);
    }

    public void addSynapse(ICell pre, ICell post, double w, int d) {
        final Synapse connection = new Synapse(graph.getModel(), (AbstractNode) pre, (AbstractNode) post, w, d);
        graph.addEdge((IEdge) connection, true, true);
    }

    public void addDetectorEdge(AbstractNode target, AbstractDetector detector) {
        final DetectorEdge connection = new DetectorEdge(graph.getModel(), (AbstractNode) target, detector);
        graph.addEdge((IEdge) connection, true, true);
    }

    public void addEdge(IEdge edge) {
        allEdges.add(edge);
    }

    public void tryToConnect(AbstractCell c1) {

        AbstractCell c2 = null;

        for (final ICell cell : allCells) {

            if (cell != c1) {

                if (((AbstractCell) cell).getToConnect()) {
                    c2 = (AbstractCell) cell;
                }
            }
        }

        if (c2 != null && c1.getToConnect()) {

            if (!(c2 instanceof AbstractDetector) && !(c1 instanceof AbstractDetector)) {

                if (c1 instanceof Targetable) {
                    Pair params = Synapse.askParameters();
                    if (params != null && (Integer) params.getValue() > 0) {

                        addSynapse((ICell) c2, (ICell) c1, (double) params.getKey(), (int) params.getValue());

                    } else {
                        if ((Integer) params.getValue() <= 0) {
                            System.out.println("Delay must be at least 1");
                        }
                    }

                } else {
                    System.out.println("Target must be targetable (like a LIF)");
                }
                c2.updateToConnect(false);
                c1.updateToConnect(false);

            } else if (!(c2 instanceof AbstractDetector) && c1 instanceof AbstractDetector) {

                AbstractNode target = (AbstractNode) c2;
                AbstractCell detector = (AbstractCell) c1;

                if (!connectionExists(target, detector)) {
                    addDetectorEdge((AbstractNode) target, (AbstractDetector) detector);
                } else {
                    System.out.println("Cannot connect node to the same detector twice");
                }

                c2.updateToConnect(false);
                c1.updateToConnect(false);

            } else if (!(c1 instanceof AbstractDetector) && c2 instanceof AbstractDetector) {

                AbstractNode target = (AbstractNode) c1;
                AbstractCell detector = (AbstractCell) c2;

                if (!connectionExists(target, detector)) {
                    addDetectorEdge((AbstractNode) target, (AbstractDetector) detector);
                } else {
                    System.out.println("Cannot connect node to the same detector twice");
                }

                c2.updateToConnect(false);
                c1.updateToConnect(false);

            } else if (c1 instanceof AbstractDetector && c2 instanceof AbstractDetector) {
                System.out.println("Cannot connect two detectors");
                c1.updateToConnect(false);
                c2.updateToConnect(false);
            }

        }
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

        // nodes step
        for (ICell cell : allCells) {
            if (cell instanceof AbstractNode) {
                cell.step();
            }
        }
        // edges step
        for (IEdge edge : allEdges) {
            edge.step();
        }
        // detectors step
        for (ICell cell : allCells) {
            if (cell instanceof AbstractDetector) {
                cell.step();
            }
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
        final Raster raster = new Raster(this);
        raster.createVisualizer();
        graph.addCell(raster, true);
        return raster;
    }

    public void createRandomSpiker() {
        final Pair<Double, Double> params = RandomSpiker.askParameters();
        if (params != null) {
            final ICell random = new RandomSpiker(this, params.getKey(), params.getValue());
            graph.addCell(random, true);
        }
    }

    public void createLIF() {
        final Double[] params = LIF.askParameters();
        if (params != null) {
            final ICell lif = new LIF(this, params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7]);
            graph.addCell(lif, true);
        }
    }

    public void createInputTrain() {
        final Pair<double[], Boolean> params = InputTrain.askParameters();
        if (params != null) {
            final ICell inputTrain = new InputTrain(this, params.getKey(), params.getValue());
            graph.addCell(inputTrain, true);
        }
    }

    public Multimeter createMultimeter() {
        final Multimeter multimeter = new Multimeter(this);
        multimeter.createVisualizer();
        graph.addCell(multimeter, true);
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

    }

    private void writeObject(ObjectOutputStream aOutputStream)
            throws IOException {
        allCellsSerialization = new ArrayList<ICell>(allCells);
        allEdgesSerialization = new ArrayList<IEdge>(allEdges);
        aOutputStream.defaultWriteObject();
    }

    public void createVisualizers() {
        for (AbstractDetector detector : getAllDetectors()) {
            detector.createVisualizer();
        }
    }

    void removeCell(ICell cell) {
        allCells.remove(cell);

        if (cell instanceof AbstractNode) {
            removeFromDetectors((AbstractNode) cell);
        }

        cell.decreaseCount();
        for (ICell c : allCells) {
            if (cell.getClass().equals(c.getClass()) && cell.getID() < c.getID()) {
                c.decreaseID();
            }
        }

    }

    void removeEdge(IEdge edge) {
        allEdges.remove(edge);
        edge.decreaseCount();
        for (IEdge e : allEdges) {
            if (edge.getClass().equals(e.getClass()) && edge.getID() < e.getID()) {
                e.decreaseID();
            }
        }
    }

    public void removeConnectedEdges(ICell cell) {
        ArrayList<IEdge> toRemove = new ArrayList();
        for (IEdge edge : allEdges) {
            if (edge.getSource() == cell || edge.getTarget() == cell) {
                toRemove.add(edge);
            }
        }
        graph.removeEdges(toRemove);
    }

    public void removeFromDetectors(AbstractNode cell) {
        for (ICell c : allCells) {
            if (c instanceof AbstractDetector) {
                if (((AbstractDetector) c).getTargets().contains(cell)) {
                    ((AbstractDetector) c).removeTarget((AbstractNode) cell);
                }

            }
        }
    }

    private boolean connectionExists(AbstractNode target, AbstractCell detector) {
        for (IEdge edge : allEdges) {
            if (edge.getSource() == target && edge.getTarget() == detector) {
                return true;
            }
        }
        return false;

    }

    public String to_inet() {
        StringBuilder elements = new StringBuilder();
        
        
        //nodes
        for (AbstractNode node : getAllNodes()) {
            elements.append(node.toString());
            elements.append("\n");
        }

        elements.append("\n\n");
        //detectors
        for (AbstractDetector detector : getAllDetectors()) {
            elements.append(detector.toString());
            elements.append("\n");
        }

        elements.append("\n\n");
        //synapses
        for (Synapse synapse : getAllSynapses()) {
            elements.append(synapse.toString());
            elements.append("\n");
        }

        elements.append("\n\n");
        
        elements.append("inet['network'] = Network([");
        boolean start = true;
        for (AbstractNode node : getAllNodes()) {
            if (!start) {
                elements.append(", ");
            } else {
                start = false;
            }
            
            elements.append(node.getClassAndID(true));
        }
        
        elements.append("], [");
        start = true;
        for (Synapse synapse : getAllSynapses()) {
            if (!start) {
                elements.append(", ");
            } else {
                start = false;
            }
            
            elements.append(synapse.getClassAndID(true));
        }
        
        elements.append("])\ninet['simulator'] = Simulator(inet['network'], [");
        start = true;
        for (AbstractDetector detector : getAllDetectors()) {
            if (!start) {
                elements.append(", ");
            } else {
                start = false;
            }
            
            elements.append(detector.getClassAndID(true));
        }
        elements.append("])");
        
        return elements.toString();
    }
}
