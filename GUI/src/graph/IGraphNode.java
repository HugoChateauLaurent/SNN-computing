package graph;

import cells.ICell;
import cells.Module;
import edges.AbstractEdge;
import java.util.Comparator;
import javafx.scene.shape.Shape;

public interface IGraphNode {

	public Shape getGraphic(Graph graph);
        
        public static final Comparator<IGraphNode> ZLEVEL_COMPARATOR = new Comparator<IGraphNode>() {
            public int compare(IGraphNode a, IGraphNode b) {
                int compare = a.getZLevel() - b.getZLevel();
                if (compare == 0) {
                    if (a instanceof AbstractEdge || b instanceof Module) {
                        compare = -1;
                    } else if (b instanceof AbstractEdge || a instanceof Module) {
                        compare = 1;
                    }
                }
                return compare;
            }
        };
        
        public abstract int getZLevel();

}
