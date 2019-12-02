package cells;

import graph.*;
import cells.AbstractCell;
import cells.Module;
import java.io.Serializable;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class CellGestures implements Serializable {

    private final DragContext dragContext = new DragContext();
    private final ResizeContext resizeContext = new ResizeContext();
    private Graph graph;
    private final AbstractCell cell;
    private final Button resizeButton;
    
    private static final long serialVersionUID = 5L;

    public CellGestures(Graph graph, AbstractCell cell) {
        this(graph, cell, null);
    }

    public CellGestures(Graph graph, AbstractCell cell, Button resizeButton) {
        this.graph = graph;
        this.cell = cell;
        this.resizeButton = resizeButton;
    }
    
    public ResizeContext getResizeContext() {
        return resizeContext;
    }

    public void initDrag(double mouseX, double mouseY) {
        Shape view = cell.getView();
        final double scale = graph.getScale();
        dragContext.x = view.getBoundsInParent().getMinX() * scale - mouseX;
        dragContext.y = view.getBoundsInParent().getMinY() * scale - mouseY;

        if (cell instanceof Module) {
            for (AbstractCell child : ((Module) cell).getCells()) {
                child.getCellGestures().initDrag(mouseX, mouseY);
            }
        }
    }

    public void drag(double mouseX, double mouseY) {
        // adjust the offset in case we are zoomed
        final double scale = graph.getScale();
        double offsetX = (mouseX + dragContext.x) / scale;
        double offsetY = (mouseY + dragContext.y) / scale;

        Shape view = cell.getView();
        view.relocate(offsetX, offsetY);
        
        boolean authorize = true;
        
        if (cell.getParentModule() != null) {
            Bounds bounds = cell.getView().getBoundsInParent();
            double offset = 10;
            authorize = true;
            authorize = authorize && cell.getParentModule().getView().getBoundsInParent().contains(bounds.getMaxX() + offset, bounds.getMaxY() + offset);
            authorize = authorize && cell.getParentModule().getView().getBoundsInParent().contains(bounds.getMinX() - offset, bounds.getMinY() - offset);
        }
        
        if (authorize) {
            if (resizeButton != null) {
                relocateResizeButton();
            }
            dragContext.saveX = view.getLayoutX();
            dragContext.saveY = view.getLayoutY();
            
            if (cell instanceof Module) {
                for (AbstractCell child : ((Module) cell).getCells()) {
                    child.getCellGestures().drag(mouseX, mouseY);
                }
            }
        } else {
            double relocate_X = dragContext.saveX;
            double relocate_Y = dragContext.saveY;
            if (cell.getView() instanceof Ellipse) {
                relocate_X -= ((Ellipse) cell.getView()).getRadiusX();
                relocate_Y -= ((Ellipse) cell.getView()).getRadiusY();
            }
            view.relocate(relocate_X, relocate_Y);
        }
    }

    public void makeDraggable() {
        Shape view = cell.getView();
        view.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown()) {
                    initDrag(event.getScreenX(), event.getScreenY());
                }
            }
        });

        view.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown()) {
                    drag(event.getScreenX(), event.getScreenY());
                }
            }
        });
    }

    public void makeUndraggable() {
        Shape view = cell.getView();
        view.setOnMousePressed(null);
        view.setOnMouseDragged(null);
    }

    public void makeResizable() {
        Shape view = cell.getView();
        resizeButton.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown() && view instanceof Rectangle) {
                    Rectangle r = (Rectangle) view;
                    final double scale = graph.getScale();
                    resizeContext.width = r.getWidth() * scale - event.getScreenX();
                    resizeContext.height = r.getHeight() * scale - event.getScreenY();
                }
            }
        });

        resizeButton.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown()) {
                    if (view instanceof Rectangle) {
                        Rectangle r = (Rectangle) view;

                        double offsetX = event.getScreenX() + resizeContext.width;
                        double offsetY = event.getScreenY() + resizeContext.height;

                        // adjust the offset in case we are zoomed
                        final double scale = graph.getScale();

                        offsetX /= scale;
                        offsetY /= scale;

                        if (offsetX > 0 && offsetY > 0) {
                            r.setWidth(offsetX);
                            r.setHeight(offsetY);

                            boolean cancel = false;
                            
                            if (cell.getParentModule() == null || cell.getParentModule().getView().getBoundsInParent().contains(view.getBoundsInParent())) {
                                for (AbstractCell child : ((Module) cell).getCells()) {
                                    if (! view.getBoundsInParent().contains(child.getView().getBoundsInParent())) {
                                        cancel = true;
                                        break;
                                    }
                                }
                                if (!cancel) {
                                    relocateResizeButton();
                                    resizeContext.saveWidth = r.getWidth();
                                    resizeContext.saveHeight = r.getHeight();
                                }

                            } else {
                                cancel = true;
                            }
                                
                            if (cancel) {
                                r.setWidth(resizeContext.saveWidth);
                                r.setHeight(resizeContext.saveHeight);
                                relocateResizeButton();
                            }
                        }

                    }
                }
            }
        });
    }
    
    public void relocateResizeButton() {
        Shape view = cell.getView();
        resizeButton.relocate(view.getBoundsInParent().getMaxX(),
                                        view.getBoundsInParent().getMaxY());
    }
    
    public void updateWidth(double newWidth) {
        if (cell instanceof Module) {
            ((Rectangle) cell.getView()).setWidth(newWidth);
            this.resizeContext.width = newWidth;
            this.resizeContext.saveWidth = newWidth;
        } else {
            System.out.println("Method designed for modules only");
        }
    }
    
    public void updateHeight(double newHeight) {
        if (cell instanceof Module) {
            ((Rectangle) cell.getView()).setHeight(newHeight);
            this.resizeContext.height = newHeight;
            this.resizeContext.saveHeight = newHeight;
        } else {
            System.out.println("Method designed for modules only");
        }
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public class DragContext implements Serializable {

        double x;
        double y;
        double saveX;
        double saveY;
    }

    public class ResizeContext implements Serializable {

        double width;
        double height;
        double saveWidth;
        double saveHeight;
    }
    
    
}