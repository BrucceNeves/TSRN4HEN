package network;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Brucce
 */
public class Node implements Serializable, Comparable<Node> {

    private final String label;
    public double F[], lastF[], Y[] = null;
    public HashMap<String, HashMap<Node, Double>> adjacent = new HashMap<>();

    public Node(String label) {
        this.label = label;
    }

    public void add(String relation, Node object, double weight) {
        HashMap<Node, Double> bagLabels = adjacent.get(relation);
        if (bagLabels == null) {
            bagLabels = new HashMap<>();
            adjacent.put(relation, bagLabels);
        }
        bagLabels.put(object, weight);
    }

    public void resetF() {
        for (int i = 0; i < Y.length; i++) {
            lastF[i] = F[i] = Y[i];
        }
    }

    @Override
    public int compareTo(Node o) {
        return label.compareTo(o.label);
    }

    @Override
    public String toString() {
        return label;
    }
}
