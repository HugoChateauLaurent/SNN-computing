package graph;

import cells.AbstractCell;
import edges.IEdge;
import cells.ICell;
import cells.LIF;
import cells.Multimeter;
import cells.Raster;
import edges.AbstractEdge;
import edges.DetectorEdge;
import edges.Synapse;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import layout.Layout;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.shape.Shape;

public class Graph {

    private final MainApp app;
    private final Model model;
    private final PannableCanvas pannableCanvas;
    private final Map<IGraphNode, Shape> graphics;
    private final NodeGestures nodeGestures;
    private final ViewportGestures viewportGestures;
    private final BooleanProperty useNodeGestures;
    private final BooleanProperty useViewportGestures;

    public Graph(MainApp app) {
        this(app, new Model());
    }

    public Graph(MainApp app, Model model) {
        this.app = app;
        this.model = model;
        this.model.setGraph(this);
        

        nodeGestures = new NodeGestures(this);
        useNodeGestures = new SimpleBooleanProperty(true);
        useNodeGestures.addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                model.getAllCells().forEach(cell -> nodeGestures.makeDraggable(getGraphic(cell)));
            } else {
                model.getAllCells().forEach(cell -> nodeGestures.makeUndraggable(getGraphic(cell)));
            }
        });

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
        
        System.out.println("cells in model: "+model.getAllCells().size());
        System.out.println("Edges in model: "+model.getAllEdges().size());
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
        cellsToFront();
    }
    
    public void addEdge(IEdge edge, boolean addToModel, boolean cellsToFront) {
        Shape graphic = getGraphic(edge);
        pannableCanvas.getChildren().add(graphic);
        if (addToModel) {
            model.addEdge(edge);
        }
        
        if (cellsToFront) {
            cellsToFront();
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
        if (useNodeGestures.get()) {
            nodeGestures.makeDraggable(graphic);
        }
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

    public NodeGestures getNodeGestures() {
        return nodeGestures;
    }

    public BooleanProperty getUseNodeGestures() {
        return useNodeGestures;
    }

    public ViewportGestures getViewportGestures() {
        return viewportGestures;
    }

    public BooleanProperty getUseViewportGestures() {
        return useViewportGestures;
    }

    private void cellsToFront() {
        for (Map.Entry<IGraphNode, Shape> graphic : graphics.entrySet()) {
            if (graphic.getKey() instanceof AbstractCell) {
                graphic.getValue().toFront();
            }
        }
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
}
