package graph;

import cells.Connectable;
import edges.IEdge;
import cells.ICell;
import java.io.Serializable;
import java.util.List;

import cells.AbstractCell;
import cells.AbstractDetector;
import cells.LIF;
import cells.Node;
import cells.Multimeter;
import edges.DetectorEdge;
import edges.Synapse;
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
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.util.Pair;

public class Model implements Serializable {

    private static final long serialVersionUID = 172247271876446110L;

    private final ICell root;

    private ObservableList<ICell> allCells;
    private transient ObservableList<ICell> addedCells;
    private transient ObservableList<ICell> removedCells;

    private ObservableList<IEdge> allEdges;
    private transient ObservableList<IEdge> addedEdges;
    private transient ObservableList<IEdge> removedEdges;

    private Graph graph;

    public Model() {
        root = new AbstractCell() {
            @Override
            public Region getGraphic(Graph graph) {
                return null;
            }

            @Override
            public void step() {
                throw new UnsupportedOperationException("Step of root"); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void updateRng(Random rng) {
                throw new UnsupportedOperationException("Update rng of root"); //To change body of generated methods, choose Tools | Templates.
            }
        };
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

    public void beginUpdate() {
        this.root.getCellChildren().clear();
    }

    public void endUpdate() {
        // every cell must have a parent, if it doesn't, then the graphParent is
        // the parent
        attachOrphansToGraphParent(this.addedCells);

        // remove reference to graphParent
        disconnectFromGraphParent(this.removedCells);
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
        final Synapse connection = new Synapse((Node) pre, (Node) post, w, d);
        addEdge((IEdge) connection);
    }
    
    public void addDetectorEdge(Node target, AbstractDetector detector) {
        final DetectorEdge connection = new DetectorEdge((Node) target, detector);
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
            beginUpdate();
            
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
                
            
                Node target = (Node) pre;
                AbstractCell detector = (AbstractCell) post;
                addDetectorEdge((Node) target, (AbstractDetector) detector);
                
                pre.updateToConnect(false);
                post.updateToConnect(false);
                graph.endUpdate();
                return true;
                
            } else if (!(post instanceof AbstractDetector) && pre instanceof AbstractDetector) {
                
            
                Node target = (Node) post;
                AbstractCell detector = (AbstractCell) pre;
                addDetectorEdge((Node) target, (AbstractDetector) detector);
                
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

    /**
     * Attach all cells which don't have a parent to graphParent
     *
     * @param cellList
     */
    public void attachOrphansToGraphParent(List<ICell> cellList) {
        for (final ICell cell : cellList) {
            if (cell.getCellParents().size() == 0) {
                root.addCellChild(cell);
            }
        }
    }

    /**
     * Remove the graphParent reference if it is set
     *
     * @param cellList
     */
    public void disconnectFromGraphParent(List<ICell> cellList) {
        for (final ICell cell : cellList) {
            root.removeCellChild(cell);
        }
    }

    public ICell getRoot() {
        return root;
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

    public Double[] askLIFParameters() {
        // Create the custom dialog.
        Dialog<Double[]> dialog = new Dialog<>();
        dialog.setTitle("LIF parameters");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        String[] params_labels = {"Leakage constant", "V_init", "V_reset", "V_rest", "thr", "amplitude", "I_e", "noise"};
        double[] values = {.9, 0, 0, 0, 1, 1, 0, 0};
        List<TextField> fields = new LinkedList();

        TextField text;
        for (int i = 0; i < params_labels.length; i++) {
            gridPane.add(new Label(params_labels[i] + ":"), (2 * i) % 4, (int) ((2 * i) / 4.0));
            text = new TextField(Double.toString(values[i]));
            fields.add(text);
            gridPane.add(text, (2 * i + 1) % 4, (int) ((2 * i + 1) / 4.0));
        }

        dialog.getDialogPane().setContent(gridPane);
        Platform.runLater(() -> fields.get(0).requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                Double[] params = new Double[params_labels.length];
                for (int i = 0; i < params.length; i++) {
                    params[i] = Double.parseDouble(fields.get(i).getText());
                }
                return params;
            }
            return null;
        });

        boolean done = false;

        while (!done) {
            try {
                Optional<Double[]> result = dialog.showAndWait();

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

    public void run() {
        Integer steps = askSteps();
        if (steps <= 0) {
            System.out.println("Running error: must indicate the number of steps (>0)");
        } else {
            run(steps);
        }
    }

    public void run(int steps) {
        init_detectors(steps);
        for (int i = 0; i < steps; i++) {
            this.step();
        }
    }

    public void init_detectors(int steps) {
        AbstractDetector detector;
        for (ICell cell : allCells) {
            if (cell instanceof AbstractDetector) {
                detector = (AbstractDetector) cell;
                detector.init(steps);
            }
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

    public void createRaster() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void createPoisson() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void createSpikeTrain() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void createLIF() {
        Double[] params = askLIFParameters();
        if (params != null) {
            final ICell lif = new LIF(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7]);
            this.addCell(lif);
        }
    }

    public Multimeter createMultimeter() {
        final Multimeter multimeter = new Multimeter(graph.getApp());
        this.addCell(multimeter);
        return multimeter;
    }

}
