package layout;

import cells.AbstractCell;
import java.util.List;
import java.util.Random;

import graph.Graph;
import cells.ICell;
import cells.Module;

public class RandomLayout implements Layout {

	private final Random rnd = new Random();

	@Override
	public void execute(Graph graph) {
		final List<ICell> cells = graph.getModel().getAllCells();

		for (final ICell cell : cells) {
                    if (cell.getZLevel() == 0) {
			final double x = rnd.nextDouble() * 500;
			final double y = rnd.nextDouble() * 500;

                        double offsetX = x - graph.getGraphic(cell).getLayoutX();
                        double offsetY = y - graph.getGraphic(cell).getLayoutY();
			graph.getGraphic(cell).relocate(x, y);
                        
                        
                        if (cell instanceof Module) {
                            for (AbstractCell child : ((Module) cell).getCellsAndChildren(false)) {
                                graph.getGraphic(child).relocate(graph.getGraphic(child).getLayoutX() + offsetX,graph.getGraphic(child).getLayoutY() + offsetY);
                                if (child instanceof Module) {
                                    ((Module) child).getCellGestures().relocateResizeButton();
                                }
                            }
                            ((Module) cell).getCellGestures().relocateResizeButton();
                        }
                    }
		}
	}

}