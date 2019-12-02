package graph;

import cells.AbstractCell;
import edges.IEdge;
import cells.ICell;
import cells.LIF;
import cells.Module;
import cells.Multimeter;
import cells.Raster;
import edges.AbstractEdge;
import edges.DetectorEdge;
import edges.Synapse;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import javafx.collections.FXCollections;

import layout.Layout;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.shape.Shape;

public class Graph {

    private final MainApp app;
    private final Model model;
    private final PannableCanvas pannableCanvas;
    private final Map<IGraphNode, Shape> graphics;
    private final ObservableList<Button> resizeButtons;
    private final ViewportGestures viewportGestures;
    private final BooleanProperty useViewportGestures;
    
    private static final long serialVersionUID = 15L;

    public Graph(MainApp app) {
        this(app, new Model());
    }

    public Graph(MainApp app, Model model) {
        this.app = app;
        this.model = model;
        this.model.setGraph(this);
        
        resizeButtons = FXCollections.observableArrayList();

        pannableCanvas = new PannableCanvas();
        
        viewportGestures = new ViewportGestures(pannableCanvas);
        useViewportGestures = new SimpleBooleanProperty(true);
        useViewportGestures.addListener((obs, oldVal, newVal) -> {
            final Parent parent = pannableCanvas.parentProperty().get();
            if (parent == null) {
                return;
            }
            if (newVal) {
                parent.addEventHandler(MouseEvent.MOUSE_PRESSED, viewportGestures.getOnMousePressedEventHandler());
                parent.addEventHandler(MouseEvent.MOUSE_DRAGGED, viewportGestures.getOnMouseDraggedEventHandler());
                parent.addEventHandler(ScrollEvent.ANY, viewportGestures.getOnScrollEventHandler());
            } else {
                parent.removeEventHandler(MouseEvent.MOUSE_PRESSED, viewportGestures.getOnMousePressedEventHandler());
                parent.removeEventHandler(MouseEvent.MOUSE_DRAGGED, viewportGestures.getOnMouseDraggedEventHandler());
                parent.removeEventHandler(ScrollEvent.ANY, viewportGestures.getOnScrollEventHandler());
            }
        });
        pannableCanvas.parentProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal != null) {
                oldVal.removeEventHandler(MouseEvent.MOUSE_PRESSED, viewportGestures.getOnMousePressedEventHandler());
                oldVal.removeEventHandler(MouseEvent.MOUSE_DRAGGED, viewportGestures.getOnMouseDraggedEventHandler());
                oldVal.removeEventHandler(ScrollEvent.ANY, viewportGestures.getOnScrollEventHandler());
            }
            if (newVal != null) {
                newVal.addEventHandler(MouseEvent.MOUSE_PRESSED, viewportGestures.getOnMousePressedEventHandler());
                newVal.addEventHandler(MouseEvent.MOUSE_DRAGGED, viewportGestures.getOnMouseDraggedEventHandler());
                newVal.addEventHandler(ScrollEvent.ANY, viewportGestures.getOnScrollEventHandler());
            }
        });

        graphics = new HashMap<>();
        
    }

    public Map<IGraphNode, Shape> getGraphics() {
        return graphics;
    }

    public MainApp getApp() {
        return app;
    }

    public Model getModel() {
        return model;
    }

    public PannableCanvas getCanvas() {
        return pannableCanvas;
    }

    public void beginUpdate() {
        pannableCanvas.getChildren().clear();
    }

    public void addEdges(List<IEdge> edges, boolean addToModel) {
        for (IEdge edge : edges) {
            addEdge(edge, addToModel, false);
        }
        updateZLayout();
    }

    public void addEdge(IEdge edge, boolean addToModel, boolean updateZLayout) {
        Shape graphic = getGraphic(edge);
        pannableCanvas.getChildren().add(graphic);
        if (addToModel) {
            model.addEdge(edge);
        }

        if (updateZLayout) {
            updateZLayout();
        }
    }

    public void addCells(List<ICell> cells, boolean addToModel) {
        for (ICell cell : cells) {
            addCell(cell, addToModel);
        }
    }
    
    public void addCell(ICell cell, boolean addToModel) {
        Shape graphic = getGraphic(cell);
        pannableCanvas.getChildren().add(graphic);
        
        if (addToModel) {
            model.addCell(cell);
        }
    }

    public Shape getGraphic(IGraphNode node) {
        if (!graphics.containsKey(node)) {            
            graphics.put(node, createGraphic(node));
        }
        return graphics.get(node);
    }

    public Shape createGraphic(IGraphNode node) {
        return node.getGraphic(this);
    }

    public double getScale() {
        return pannableCanvas.getScale();
    }

    public void layout(Layout layout) {
        layout.execute(this);
    }

    public ViewportGestures getViewportGestures() {
        return viewportGestures;
    }

    public BooleanProperty getUseViewportGestures() {
        return useViewportGestures;
    }
    
    public void removeCell(ICell cell) {
        Shape cellGraphic = getGraphic(cell);
        pannableCanvas.getChildren().remove(cellGraphic);
        model.removeConnectedEdges(cell);
        model.removeCell(cell);
        
    }
    
    public void removeEdge(IEdge edge) {
        Shape edgeGraphic = getGraphic(edge);
        pannableCanvas.getChildren().remove(edgeGraphic);
        model.removeEdge(edge);
    }
    
    public void removeEdges(List<IEdge> edges) {
        for (IEdge edge : edges) {
            removeEdge(edge);
        }
    }
    
    public void removeModule(Module aThis) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void addResizeButton(Button resizeButton) {
        resizeButtons.add(resizeButton);
        pannableCanvas.getChildren().add(resizeButton);
    }

    public void removeResizeButton(Button resizeButton) {
        resizeButtons.remove(resizeButton);
        pannableCanvas.getChildren().remove(resizeButton);
    }
    
    public void updateZLayout() {
        ObservableList<ICell> cells = model.getAllCells();
        ObservableList<IEdge> edges = model.getAllEdges();
        List<IGraphNode> graphNodes = new ArrayList();
        graphNodes.addAll(cells);
        graphNodes.addAll(edges);
        
        Collections.sort(graphNodes, IGraphNode.ZLEVEL_COMPARATOR);
        
        for (IGraphNode graphNode : graphNodes) {
            graphNode.getGraphic(this).toFront();
            
            if (graphNode instanceof Module) {
                ((Module) graphNode).getResizeButton().toFront();
            }
            
        }
    }
}


