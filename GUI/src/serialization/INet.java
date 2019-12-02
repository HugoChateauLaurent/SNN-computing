/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serialization;

import cells.AbstractCell;
import cells.AbstractDetector;
import cells.AbstractNode;
import cells.ICell;
import edges.AbstractEdge;
import edges.Synapse;
import graph.Graph;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;

/**
 *
 * @author ubuntu
 */
public class INet {

    Graph graph;
    
    private static final long serialVersionUID = 30L;

    public INet(Graph graph) {
        this.graph = graph;

    }

    public void exec(BufferedReader cmds) {

        String cmd;
        String parts[] = new String[2];
        Object o = null;
        Object target;
        HashMap<String, Object> vars = new HashMap();

        try {
            while ((cmd = cmds.readLine()) != null) {
                if (cmd.contains("create")) {
                    if (cmd.contains("=")) { // can be unassigned (e.g. a synapse)
                        parts = cmd.split("=");
                        parts[0] = parts[0].replaceAll("\\s", ""); // remove whitespaces from variable name

                    } else {
                        parts[0] = null;
                        parts[1] = cmd;
                    }
                    
                    o = createObject(vars, parts[1]);

                    if (o != null && parts[0] != null) {
                        vars.put(parts[0], o);
                    }

                } else if (cmd.contains("addTarget")) {
                    parts = cmd.split(".addTarget");
                    parts[0] = parts[0].replaceAll("\\s", ""); // remove whitespaces from variable name
                    parts[1] = parts[1].substring(parts[1].indexOf("(")+1, parts[1].indexOf(")")); // remove parenthesese from target name
                    
                    o = vars.get(parts[0]);
                    if (o instanceof AbstractDetector) {
                        target = vars.get(parts[1]);
                        if (target instanceof AbstractNode) {
                            graph.getModel().addDetectorEdge((AbstractNode) target, (AbstractDetector) o);
                        }
                        
                        else {
                            System.out.println("TypeError: Target must be a node");
                        }
                    } else {
                        System.out.println("TypeError: must be a detector");
                    }
                }
                
                parts[0] = null;
                parts[1] = null;
                
            }
        } catch (IOException ex) {
            Logger.getLogger(INet.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public Object createObject(HashMap<String, Object> vars, String cmd) {
        // get string between function parentheses
        String parameters = cmd.substring(cmd.indexOf("(")+1, cmd.indexOf(")"));
        String[] split_params = parameters.split(",");
        for (int i=0; i<split_params.length; i++) {
            split_params[i] = split_params[i].replaceAll("\\s", ""); // remove whitespaces
        }
        
        
        if (cmd.contains("createSynapse")) {
            return createSynapse(vars, split_params);
        } else if (cmd.contains("createLIF")) {
            return createLIF(split_params);
        } else if (cmd.contains("createRandomSpiker")) {
            return createRandomSpiker(split_params);
        } else if (cmd.contains("createInputTrain")) {
            return createInputTrain(split_params);
        } else if (cmd.contains("createMultimeter")) {
            return graph.getModel().createMultimeter();
        } else if (cmd.contains("createRaster")) {
            return graph.getModel().createRaster();
        } 
        return null;
    }

    public Object createSynapse(HashMap<String, Object> vars, String[] parameters) {
        // get pre post from objects map 
        Object pre = vars.get(parameters[0]);
        Object post = vars.get(parameters[1]);
        
        if (!(pre instanceof AbstractNode)) {
            System.out.println("TypeError: pre must be a node");
            return null;
        }
        if (!(post instanceof AbstractNode)) {
            System.out.println("TypeError: post must be a node");
            return null;
        }
        
        return graph.getModel().addSynapse((ICell) pre, (ICell) post, Double.parseDouble(parameters[2]), Integer.parseInt(parameters[3]));
    }

    private Object createLIF(String[] parameters) {
        Double[] dbl_params = new Double[parameters.length];
        for (int i=0; i<parameters.length; i++) {
            dbl_params[i] = Double.parseDouble(parameters[i]);
        } 
        return graph.getModel().createLIF(dbl_params);
    }

    private Object createRandomSpiker(String[] parameters) {
        return graph.getModel().createRandomSpiker(new Pair(Double.parseDouble(parameters[0]), Double.parseDouble(parameters[1])));
    }

    private Object createInputTrain(String[] parameters) {
        return graph.getModel().createInputTrain(new Pair(Double.parseDouble(parameters[0]), Boolean.parseBoolean(parameters[1])));
    }
    
    public String write() {
        StringBuilder elements = new StringBuilder();
        
        elements.append("inet['network'] = Network()\n" +
            "inet['simulator'] = Simulator(network)\n" +
            "network = inet['network']\n" +
            "simulator = inet['simulator']"
        );
        
        //nodes
        for (AbstractNode node : graph.getModel().getAllNodes()) {
            elements.append(node.to_inet());
            elements.append("\n");
        }

        elements.append("\n\n");
        //detectors
        for (AbstractDetector detector : graph.getModel().getAllDetectors()) {
            elements.append(detector.to_inet());
            elements.append("\n");
        }

        elements.append("\n\n");
        //synapses
        for (Synapse synapse : graph.getModel().getAllSynapses()) {
            elements.append(synapse.to_inet());
            elements.append("\n");
        }

        elements.append("\n\n");
        
        elements.append("inet['network'] = Network([");
        boolean start = true;
        for (AbstractNode node : graph.getModel().getAllNodes()) {
            if (!start) {
                elements.append(", ");
            } else {
                start = false;
            }
            
            elements.append(node.getClassAndID(true));
        }
        
        elements.append("], [");
        start = true;
        for (Synapse synapse : graph.getModel().getAllSynapses()) {
            if (!start) {
                elements.append(", ");
            } else {
                start = false;
            }
            
            elements.append(synapse.getClassAndID(true));
        }
        
        elements.append("])\ninet['simulator'] = Simulator(inet['network'], [");
        start = true;
        for (AbstractDetector detector : graph.getModel().getAllDetectors()) {
            if (!start) {
                elements.append(", ");
            } else {
                start = false;
            }
            
            elements.append(detector.getClassAndID(true));
        }
        elements.append("])");
        
        return elements.toString();
    }
}
