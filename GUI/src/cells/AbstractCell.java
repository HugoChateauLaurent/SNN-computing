package cells;

import graph.Graph;
import java.util.ArrayList;
import java.util.List;

import java.util.Random;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public abstract class AbstractCell implements ICell {

    private final List<ICell> children = new ArrayList<>();
    private final List<ICell> parents = new ArrayList<>();
    private boolean toConnect = false;
    protected Shape view = null;
    final DragContext dragContext = new DragContext();

    @Override
    public void addCellChild(ICell cell) {
        children.add(cell);
    }

    @Override
    public List<ICell> getCellChildren() {
        return children;
    }

    @Override
    public void addCellParent(ICell cell) {
        parents.add(cell);
    }

    @Override
    public List<ICell> getCellParents() {
        return parents;
    }

    @Override
    public void removeCellChild(ICell cell) {
        children.remove(cell);
    }

    public void step() {
    }

    public void updateRng(Random rng) {
    }

    @Override
    public Region getGraphic(Graph graph) {
        this.updateColor();
        view.setStroke(Color.BLACK);
        view.setStrokeWidth(3);
        final Pane pane = new Pane(view);
        pane.setPrefSize(50, 50);

        if (view instanceof Ellipse) {
            Ellipse ellipse = (Ellipse) view;
            ellipse.radiusXProperty().bind(pane.prefWidthProperty());
            ellipse.radiusYProperty().bind(pane.prefHeightProperty());
        } else if (view instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) view;
            rectangle.widthProperty().bind(pane.prefWidthProperty());
            rectangle.heightProperty().bind(pane.prefHeightProperty());
        }

        Connectable this_connectable = (Connectable) this;
        view.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (e.getButton() == MouseButton.SECONDARY) {
                    updateToConnect(!toConnect);
                    graph.getModel().tryToConnect(this_connectable);
                }
            }
        });
        view.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
                    updateParameters();
                }
            }
        });

        view.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent mouseEvent) {
                System.out.println("Dragging");
                if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                    
                    double offsetX = mouseEvent.getScreenX() + dragContext.x;
                    double offsetY = mouseEvent.getScreenY() + dragContext.y;
                    
                    // adjust the offset in case we are zoomed
                    final double scale = graph.getScale();

                    offsetX /= scale;
                    offsetY /= scale;
                            
                    view.relocate(offsetX, offsetY);
                    graph.getApp().updateHierarchy();
                } else {
                    System.out.println("right");
                }
            }
        });
        
        view.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                    final double scale = graph.getScale();
                    dragContext.x = view.getBoundsInParent().getMinX() * scale - mouseEvent.getScreenX();
                    dragContext.y = view.getBoundsInParent().getMinY() * scale - mouseEvent.getScreenY();
                }
            }
        });
        
    

        return pane;
    }

    public static class DragContext {
        double x;
        double y;
    }
    
    public void updateColor() {
        if (toConnect) {
            view.setFill(Color.GRAY);
        } else {
            view.setFill(Color.WHITE);
        }
    }

    public void updateParameters() {
        System.out.println("TODO");
    }

    public boolean getToConnect() {
        return toConnect;
    }

    public void updateToConnect(boolean toConnect) {
        this.toConnect = toConnect;
        this.updateColor();
    }

}
