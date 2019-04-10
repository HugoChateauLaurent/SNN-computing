/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package detectors;

import cells.LIF;
import cells.Node;
import graph.Connectable;
import java.util.List;
import java.util.Set;

/**
 *
 * @author ubuntu
 */
public abstract class AbstractDetector implements IDetector, Connectable {

    protected int index;
    protected boolean toConnect;

    public AbstractDetector() {
        toConnect = false;
    }

    @Override
    public boolean getToConnect() {
        return toConnect;
    }

    @Override
    public void updateToConnect(boolean toConnect) {
        this.toConnect = toConnect;
    }
}
