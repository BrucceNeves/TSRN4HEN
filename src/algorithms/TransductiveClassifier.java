package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONObject;
import network.Network;
import network.Node;

/**
 * @author Brucce
 */
public abstract class TransductiveClassifier {

    private int iterations;
    private double countDivergence, previousConv;
    private final double convergenceThreshold;
    private final ArrayList<Double> convThresholdCache = new ArrayList<>();
    protected Network network;

    public TransductiveClassifier() {
        this.convergenceThreshold = 0;
    }

    public TransductiveClassifier(Network network, JSONObject config) {
        this(network, config.getInt("iterations"), config.getDouble("convergenceThreshold"));
    }

    public TransductiveClassifier(Network network, int iterations, double convergenceThreshold) {
        this.network = network;
        this.iterations = iterations;
        this.convergenceThreshold = convergenceThreshold;
    }

    public void classifier() throws Exception {
        int count;
        previousConv = 0;
        countDivergence = 0;
        convThresholdCache.clear();
        for (count = 0; count < iterations; count++) {
            labelPropagation();
            if (converged() && count > 5) {
                break;
            }
        }
    }

    protected abstract double getDegree(Node obj);

    protected abstract void labelPropagation() throws Exception;

    protected boolean converged() {
        double convNew = 0.0;
        for (HashMap<String, Node> layer : network.layers.values()) {
            for (Node node : layer.values()) {
                for (int i = 0; i < node.F.length; i++) {
                    convNew += Math.abs(node.lastF[i] - node.F[i]);
                }
                node.lastF = node.F;
            }
        }
        if (Double.isNaN(convNew) || Double.isInfinite(convNew)) {
            System.out.println("########## convNovo(" + convNew + ")");
            return true;
        } else if (convNew > previousConv) {
            System.out.println("########## convNew(" + convNew + ") > previousConv(" + previousConv + ")");
            countDivergence++;
        } else {
            countDivergence = 0;
        }
        convThresholdCache.add(convNew);
        System.out.println(convNew + " < " + convergenceThreshold);
        if (convNew < convergenceThreshold) {
            return true;
        }

        if (countDivergence >= 4) {
            System.out.println("###### Finished by divergence!");
            return true;
        }
        if (convThresholdCache.size() > 10) {
            convThresholdCache.remove(0);
        }
        if (convThresholdCache.size() >= 10) {
            if (convThresholdCache.get(0) < convThresholdCache.get(9) && convThresholdCache.get(1) < convThresholdCache.get(8)) {
                System.out.println("###### Finished by divergence! (zig-zag)");
                return true;
            }
            for (int i = 0; i < convThresholdCache.size(); i++) {
                if (convNew != convThresholdCache.get(i)) {
                    previousConv = convNew;
                    return false;
                }
            }
            System.out.println("Finished by cache");
            return true;
        }
        previousConv = convNew;
        return false;
    }

}
