/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serialization;

import cells.InputTrain;
import cells.LIF;
import cells.Multimeter;
import cells.RandomSpiker;
import cells.Raster;
import edges.DetectorEdge;
import edges.Synapse;
import graph.Model;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ubuntu
 */
public class Serialization implements Serializable {
    
    private Model model;
    private Map<String, Integer> counts;
    
    public Serialization(Model model) {
        this.model = model;
        counts = new HashMap<String, Integer>();
    }

    public Model getModel() {
        return model;
    }
    
    public void readCounts() {
        LIF.setCount(counts.get("LIF"));
        Raster.setCount(counts.get("Raster"));
        Multimeter.setCount(counts.get("Multimeter"));
        RandomSpiker.setCount(counts.get("Random"));
        InputTrain.setCount(counts.get("InputTrain"));
        
        DetectorEdge.setCount(counts.get("DetectorEdge"));
        Synapse.setCount(counts.get("Synapse"));
        
    }
    
    public void writeCounts() {
        counts.put("LIF", LIF.getCount());
        counts.put("Raster", Raster.getCount());
        counts.put("Multimeter", Multimeter.getCount());
        counts.put("Random", RandomSpiker.getCount());
        counts.put("InputTrain", InputTrain.getCount());
        
        counts.put("DetectorEdge", DetectorEdge.getCount());
        counts.put("Synapse", Synapse.getCount());
    }
    
    private void readObject(ObjectInputStream aInputStream)
    throws ClassNotFoundException, IOException {
        aInputStream.defaultReadObject();
        readCounts();
    }

    private void writeObject(ObjectOutputStream aOutputStream)
        throws IOException {
            writeCounts();
            aOutputStream.defaultWriteObject();
    }
    
}
