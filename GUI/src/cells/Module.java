package cells;

import cells.CellGestures.ResizeContext;
//import static com.sun.deploy.util.SessionState.save;
import edges.AbstractEdge;
import edges.IEdge;
import graph.Graph;
import graph.Model;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import java.util.Random;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Cell;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import serialization.INet;
import serialization.Serialization;

public class Module extends AbstractCell {

    private static int count = 1;

    private static final long serialVersionUID = 8L;
    
    protected static final double DEFAULT_SIZE = 200;
    private static File RESIZE_IMAGE_FILE = new File("img/resize.png");
    private static FileChooser.ExtensionFilter EXTENSION = new FileChooser.ExtensionFilter("Module files (*.mod)","*.mod");

    protected transient Rectangle view = null;
    final DragContext dragContext = new DragContext();
    protected transient Button resizeButton;

    protected transient ObservableSet<AbstractCell> cells;
    protected HashSet<AbstractCell> serializableCells;
    protected HashSet<AbstractEdge> serializableInternalEdges; // edges are not stored in modules, except here when serializing

    protected Model model;
    private File saveFile;
    
    private static boolean updateZLayout; // used to prevent from updatingZLayout (and getting NullPointerException) when loading serialization

    public Module(Model model, ObservableSet<AbstractCell> cells) {
        
        ID = count;
        count++;
        zLevel = 0;
        this.cells = cells;
        this.model = model;
        createView();
        saveFile = null;
        updateZLayout = true;
    }
    
    public Module(Model model) {
        this(model, FXCollections.observableSet());
        
    }

    public ObservableSet<AbstractCell> getCells() {
        return cells;
    }

    public HashSet<AbstractCell> getSerializableCells() {
        return serializableCells;
    }

    public HashSet<AbstractEdge> getSerializableInternalEdges() {
        return serializableInternalEdges;
    }
    
    public ObservableSet<AbstractCell> getCellsAndChildren(boolean deserialize_submodules) {
        ObservableSet<AbstractCell> return_cells = FXCollections.observableSet();
        for (AbstractCell cell : cells) {
            return_cells.add(cell);
            if (cell instanceof Module) {
                if (deserialize_submodules) {
                    ((Module) cell).setCells(FXCollections.observableSet(((Module) cell).getSerializableCells()));                    
                }
                return_cells.addAll(((Module) cell).getCellsAndChildren(deserialize_submodules));
            }
        }
        return return_cells;
    }
    
    public HashSet<AbstractCell> findAncestors() {
        HashSet<AbstractCell> ancestors = new HashSet();
        AbstractCell ancestor = this;
        boolean done = false;
        
        while (!done) {
            ancestor = ancestor.getParentModule();
            if (ancestor == null) {
                done = true;
            } else {
                ancestors.add(ancestor);
            }
        }
        return ancestors;
    }
    
    public HashSet<AbstractEdge> findInternalEdges() {
        ObservableList<IEdge> allEdges = this.model.getAllEdges();
        ObservableSet<AbstractCell> children = getCellsAndChildren(false);
        HashSet<AbstractEdge> internalEdges = new HashSet();

        for (IEdge edge : allEdges) {
            if (children.contains(edge.getSource()) && children.contains(edge.getTarget())) {
                internalEdges.add((AbstractEdge) edge);
            }
        }
        return internalEdges;
    }

    public void setCells(ObservableSet<AbstractCell> cells) {
        this.cells = cells;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
    }
    
    public String to_inet() {
        return "";
    }

    public Shape getView() {
        return view;
    }

    public void step() {
    }

    public void updateRng(Random rng) {
    }

    public void createView() {
        Rectangle r = new Rectangle(DEFAULT_SIZE, DEFAULT_SIZE);
        r.setFill(Color.LIGHTGRAY);
        r.setArcHeight(20);
        r.setArcWidth(20);

        view = r;

        createResizeButton();
        cellGestures = new CellGestures(model.getGraph(), this, resizeButton);
        cellGestures.makeDraggable();
        cellGestures.makeResizable();

    }
    
    public static void setUpdateZLayout(boolean newVal) {
        updateZLayout = newVal;
    }

    @Override
    public Shape getGraphic(Graph graph) {
        view.setStroke(Color.BLACK);
        view.setStrokeWidth(5);

        view.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    ContextMenu contextMenu = createContextMenu(graph);
                    contextMenu.setAutoHide(true);
                    contextMenu.show(view, event.getScreenX(), event.getScreenY());
                    event.consume();

                } else if (event.getButton().equals(MouseButton.PRIMARY)) {
                    if (event.getClickCount() == 2) {
                        doubleClick();
                    }
                }
            }
        });

        return view;
    }

    public void doubleClick() {

    }

    public static FileChooser.ExtensionFilter getEXTENSION() {
        return EXTENSION;
    }

    private void updateZ(int z) {
        zLevel = z;
        for (AbstractCell cell : cells) {
            if (cell instanceof Module) {
                ((Module) cell).updateZ(zLevel + 1);
            } else {
                cell.setZLevel(zLevel + 1);
            }
        }
    }

    private void updateSize(AbstractCell added) {
        
        System.out.println(this.toString());
        
        CellGestures thisGestures = this.getCellGestures();
        CellGestures addedGestures = added.getCellGestures();
        double gap = DEFAULT_SIZE;
                
        if (thisGestures.getResizeContext().width < addedGestures.getResizeContext().width + gap) {
            if (added.getView() instanceof Rectangle) {
                thisGestures.updateWidth(((Rectangle) added.getView()).getWidth() + gap);
            } else if (added.getView() instanceof Ellipse) {
                thisGestures.updateWidth(((Ellipse) added.getView()).getRadiusX() * 2 + gap);
            }
        }
        
        if (thisGestures.getResizeContext().height < addedGestures.getResizeContext().height + gap) {
            if (added.getView() instanceof Rectangle) {
                thisGestures.updateHeight(((Rectangle) added.getView()).getHeight() + gap);
            } else if (added.getView() instanceof Ellipse) {
                thisGestures.updateHeight(((Ellipse) added.getView()).getRadiusY() * 2 + gap);
            }
        }
        
        cellGestures.relocateResizeButton();
                
        if (this.parentModule != null) {
            this.parentModule.updateSize(this);
        }
    }

    public void fixLayout(List<AbstractCell> ZOrderedCells) {
        Module module;
            
        if (ZOrderedCells.size() > 0) {
            int maxZ = ZOrderedCells.get(0).zLevel;
            for (AbstractCell cell : ZOrderedCells) {
                if (cell.getZLevel() == maxZ && cell instanceof Module) {
                    cell.getCellGestures().relocateResizeButton();
                } else if (cell.getZLevel() == maxZ - 1) {
                    if (cell instanceof Module) {
                        module = (Module) cell;
                        for (AbstractCell child : module.getCells()) {
                            module.updateSize(child);
                        }
                    }
                } else if (cell.getZLevel() < maxZ - 1) {
                    break;
                }
            }
        } 
    }

    public void fixLayout() {
        List<AbstractCell> sortedCells = new ArrayList<>(this.getCellsAndChildren(false));
        Collections.sort(sortedCells, Collections.reverseOrder());
        sortedCells.add(this);
        for (AbstractCell cell : sortedCells) {
            cell.getView().relocate(view.getLayoutX() + (30*(cell.getZLevel()-zLevel)), view.getLayoutY() + (30*(cell.getZLevel()-zLevel)));
        }
        fixLayout(sortedCells);
        
    }

    public static class DragContext implements Serializable {
        double x;
        double y;
    }

    @Override
    public ContextMenu createContextMenu(Graph graph) {

        final ContextMenu contextMenu = new ContextMenu();

        MenuItem ID_label = new MenuItem("");
        ID_label.setDisable(true);
        ID_label.getStyleClass().add("context-menu-title");

        ID_label.setText("Module " + Integer.toString(ID) + " z" + zLevel);

        MenuItem add = new MenuItem("Add");
        add.setOnAction((ActionEvent event) -> {
            System.out.println("Add cells");
            selectCellsToAdd();
        });

        MenuItem remove = new MenuItem("Remove");
        remove.setOnAction((ActionEvent event) -> {
            System.out.println("Remove cells");
            selectCellsToRemove();
        });

        MenuItem saveAs = new MenuItem("Save as");
        saveAs.setOnAction((ActionEvent event) -> {
            System.out.println("Save as");
            saveAs();
        });
        
        MenuItem save = new MenuItem("Save");
        save.setOnAction((ActionEvent event) -> {
            System.out.println("Save");
            if (saveFile == null) {
                saveAs();
            }
            save();
        });

        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction((ActionEvent event) -> {
            System.out.println("Delete");
            delete();
        });

        contextMenu.getItems().addAll(ID_label, add, remove, saveAs, save, delete);

        return contextMenu;

    }
    
    private boolean saveAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(model.getGraph().getApp().getNETWORKS_DIRECTORY());
        fileChooser.setInitialFileName("my_module.mod");
        fileChooser.getExtensionFilters().add(EXTENSION);

        //Show save file dialog
        File newSaveFile = fileChooser.showSaveDialog(model.getGraph().getApp().getPrimaryStage());
        if (newSaveFile != null && newSaveFile.getName().endsWith(".mod")) {
            saveFile = newSaveFile;
            model.getGraph().getApp().setNETWORKS_DIRECTORY(saveFile, true);
            
            return true;
        } else if (!(newSaveFile.getName().endsWith(".mod"))) {
            System.out.println("Wrong file extension. File name must be *.mod");
        } else {
            System.out.println("Cannot save as: " + newSaveFile);
        }
        return false;
            
    }

    private void save() {
        try {
            
            if (saveFile.getName().endsWith(".mod")) {
                FileOutputStream fileOut = new FileOutputStream(saveFile);
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
                objectOut.writeObject(this);
                objectOut.close();
            } else {
                System.out.println("Wrong file extension. File name must be *.mod");
            }
            
 
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }    

    private void writeObject(ObjectOutputStream aOutputStream)
            throws IOException {
        serializableCells = new HashSet(cells);
        serializableInternalEdges = findInternalEdges();
        aOutputStream.defaultWriteObject();
    }

    public void addCell(AbstractCell cell) {
        double offsetX = view.getLayoutX() + 30 - cell.getView().getLayoutX();
        double offsetY = view.getLayoutY() + 30 - cell.getView().getLayoutY();
        cell.getView().relocate(view.getLayoutX() + 30, view.getLayoutY() + 30);
        updateSize(cell);
        if (cell instanceof Module) {
            ((Module) cell).updateZ(zLevel + 1);
            ((Module) cell).relocateChildren(offsetX, offsetY);
            updateSize((Module) cell);
            cell.getCellGestures().relocateResizeButton();
        } else {
            cell.setZLevel(zLevel + 1);
        }
        cells.add(cell);
        cell.setParentModule(this);

    }
    
    public void relocateChildren(double offsetX, double offsetY) {
        for (AbstractCell child : getCellsAndChildren(false)) {
            child.getView().relocate(child.getView().getLayoutX() + offsetX, child.getView().getLayoutY() + offsetY);
            if (child instanceof Module) {
                child.getCellGestures().relocateResizeButton();
            }
        }
    }

    public void addCells(HashSet<AbstractCell> selected) {
        for (AbstractCell cell : selected) {
            addCell(cell);
        }
    }
    

    private void removeCells(HashSet<AbstractCell> selectedCells) {
        for (AbstractCell cell : selectedCells) {
            removeCell(cell);
        }
    }
    

    public void removeCell(AbstractCell cell) {
        System.out.println("Relocating cell to origin");
        cell.getView().relocate(0,0);
        
        cells.remove(cell);
        if (cell instanceof Module) {
            ((Module) cell).updateZ(zLevel);
            cell.getCellGestures().relocateResizeButton();
        } else {
            cell.setZLevel(zLevel);
        }

        cell.setParentModule(null);
    }

    protected void createResizeButton() {
        resizeButton = new Button();
        Image resize_image = new Image(RESIZE_IMAGE_FILE.toURI().toString(), 15, 15, true, true);
        resizeButton.setGraphic(new ImageView(resize_image));
        
        model.getGraph().addResizeButton(resizeButton);
        if (this.updateZLayout) {
            model.getGraph().updateZLayout();
        }
        
        resizeButton.setPrefSize(15, 15);
        resizeButton.setMinSize(15, 15);
        resizeButton.setMaxSize(15, 15);
        resizeButton.relocate(view.getBoundsInParent().getMaxX() - resizeButton.getLayoutX(), view.getBoundsInParent().getMaxX() - resizeButton.getLayoutX());
    }
    
    public Button getResizeButton() {
        return this.resizeButton;
    }

    @Override
    public void delete() {
        model.getGraph().removeResizeButton(resizeButton);
        for (AbstractCell cell : cells) {
            model.getGraph().removeCell(cell);
        }

        model.getGraph().removeCell(this);
        if (parentModule != null) {
            parentModule.removeCell(this);
        }
    }

    public void selectCellsToAdd() {
        ArrayList<LIF> lif = new ArrayList();
        ArrayList<Module> modules = new ArrayList();
        ArrayList<Raster> rasters = new ArrayList();
        ArrayList<Multimeter> multimeters = new ArrayList();
        ArrayList<RandomSpiker> randoms = new ArrayList();
        ArrayList<InputTrain> inputTrains = new ArrayList();
        
        for (ICell cell : model.getAllCells()) {
            if (((AbstractCell) cell).getParentModule() == null && cell != this && !findAncestors().contains(cell)) {
                if (cell instanceof LIF) {
                    lif.add((LIF) cell);
                } else if (cell instanceof Module) {
                    modules.add((Module) cell);
                } else if (cell instanceof Raster) {
                    rasters.add((Raster) cell);
                } else if (cell instanceof Multimeter) {
                    multimeters.add((Multimeter) cell);
                } else if (cell instanceof InputTrain) {
                    inputTrains.add((InputTrain) cell);
                } else if (cell instanceof RandomSpiker) {
                    randoms.add((RandomSpiker) cell);
                } else {
                    System.out.println("Unknown cell type: " + cell);
                }
            }
        }

        Collections.sort(lif);
        Collections.sort(rasters);
        Collections.sort(multimeters);
        Collections.sort(modules);
        Collections.sort(randoms);
        Collections.sort(inputTrains);

        ArrayList<AbstractCell> selectable = new ArrayList(lif);
        selectable.addAll(rasters);
        selectable.addAll(multimeters);
        selectable.addAll(modules);
        selectable.addAll(randoms);
        selectable.addAll(inputTrains);

        addCells(selectCells(selectable, "add to the module"));
        model.getGraph().updateZLayout();
    }

    public void selectCellsToRemove() {
        ArrayList<LIF> lif = new ArrayList();
        ArrayList<Module> modules = new ArrayList();
        ArrayList<Raster> rasters = new ArrayList();
        ArrayList<Multimeter> multimeters = new ArrayList();
        ArrayList<RandomSpiker> randoms = new ArrayList();
        ArrayList<InputTrain> inputTrains = new ArrayList();

        for (AbstractCell cell : cells) {
            if (cell instanceof LIF) {
                lif.add((LIF) cell);
            } else if (cell instanceof Module) {
                modules.add((Module) cell);
            } else if (cell instanceof Raster) {
                rasters.add((Raster) cell);
            } else if (cell instanceof Multimeter) {
                multimeters.add((Multimeter) cell);
            } else if (cell instanceof RandomSpiker) {
                randoms.add((RandomSpiker) cell);
            } else if (cell instanceof InputTrain) {
                inputTrains.add((InputTrain) cell);
            } else {
                System.out.println("Unknown cell type: " + cell);
            }
        }

        Collections.sort(lif);
        Collections.sort(rasters);
        Collections.sort(multimeters);
        Collections.sort(modules);
        Collections.sort(randoms);
        Collections.sort(inputTrains);

        ArrayList<AbstractCell> selectable = new ArrayList(lif);
        selectable.addAll(rasters);
        selectable.addAll(multimeters);
        selectable.addAll(modules);
        selectable.addAll(randoms);
        selectable.addAll(inputTrains);

        HashSet<AbstractCell> selected = selectCells(selectable, "remove from the module");
        removeCells(selected);
        model.getGraph().updateZLayout();
    }

    public HashSet<AbstractCell> selectCells(ArrayList<AbstractCell> selectable, String action) {

        // Create the custom dialog.
        Dialog<boolean[]> dialog = new Dialog<>();
        dialog.setTitle("Select items to " + action);

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        final List<CheckBox> boxes = new LinkedList();
        CheckBox checkbox;

        for (int i = 0; i < selectable.size(); i++) {
            checkbox = new CheckBox(selectable.get(i).toString());
            gridPane.add(checkbox, 0, i);
            boxes.add(checkbox);
        }

        dialog.getDialogPane().setContent(gridPane);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                boolean[] cells = new boolean[boxes.size()];
                for (int i = 0; i < cells.length; i++) {
                    cells[i] = boxes.get(i).selectedProperty().getValue();
                }
                return cells;
            }
            return null;
        });

        Optional<boolean[]> result = dialog.showAndWait();
        HashSet<AbstractCell> selectedCells = new HashSet();
        
        try {
            boolean[] result_ID = result.get();
            
            AbstractCell cell;

            for (int i = 0; i < selectable.size(); i++) {
                if (result_ID[i]) {
                    selectedCells.add(selectable.get(i));
                }
            }
            
        } catch (NoSuchElementException e) {
            System.out.println("Cancelled");            
        }
        return selectedCells;
    }
    
    public void increaseCount() {
        count++;
    }
    
    public void decreaseCount() {
        count--;
    }
    
    public static void setCount(int newCount) {
        count = newCount;
    }
    
    public static int getCount() {
        return count;
    }
    
    public int getClassCount() {
        return count;
    }

}