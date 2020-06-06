package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import network.Node;
import network.Network;

/**
 * @author Brucce
 */
public class LLGC extends TransductiveClassifier {

    private final ArrayList<Node> listNodes = new ArrayList<>();
    private final double mi;

    public LLGC(Network network, JSONObject json) {
        this(network, json, json.getDouble("mi"));
    }
    public LLGC(Network network, JSONObject json, double mi) {
        super(network, json);
        this.mi = mi;
        for (HashMap<String, Node> nodes : network.layers.values()) {
            for (Node node : nodes.values()) {
                listNodes.add(node);
            }
        }
    }

    @Override
    protected void labelPropagation() {
        for (Node Oi : listNodes) {
            if (mi == 1.0 && Oi.Y != null) {
                continue;
            }
            double f[] = network.createClassVector();
            for (HashMap<Node, Double> adjs : Oi.adjacent.values()) {
                for (Node Oj : adjs.keySet()) {
                    double weight = getWeightRelation(Oi, Oj, adjs.get(Oj));
                    for (int k = 0; k < f.length; k++) {
                        f[k] += Oj.lastF[k] * weight;
                    }
                }
            }
            if (Oi.Y != null) {
                for (int k = 0; k < f.length; k++) {
                    f[k] += mi * Oi.Y[k];
                }
            }
            Oi.F = f;
        }
    }

    protected double getWeightRelation(Node i, Node j, double edge) {
        return edge / (getDegree(i) * getDegree(j));
    }

    @Override
    protected double getDegree(Node obj) {
        double w = 0.0;
        for (HashMap<Node, Double> adjs : obj.adjacent.values()) {
            for (Double weight : adjs.values()) {
                w += weight;
            }
        }
        w = Math.sqrt(w);
        return w;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            throw new Exception("Necessary to inform [JSON file]!");
        }
        JSONObject json = new JSONObject(tools.Useful.readInline(args[0]));
        JSONArray ja = json.getJSONArray("relations");
        String[] layers = new String[ja.length()];
        for (int i = 0; i < layers.length; i++) {
            layers[i] = ja.getString(i);
        }
        Network network = new Network(layers, json.getString("labels"));

        LLGC alg = new LLGC(network, json);
        alg.classifier();
        network.saveNetwork(json.getString("output_file"));
    }
}
