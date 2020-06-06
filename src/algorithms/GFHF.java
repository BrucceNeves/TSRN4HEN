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
public class GFHF extends TransductiveClassifier {

    private final ArrayList<Node> listNodes = new ArrayList<>();

    public GFHF() {
    }

    public GFHF(Network network, JSONObject json) {
        super(network, json);
        for (HashMap<String, Node> nodes : network.layers.values()) {
            for (Node node : nodes.values()) {
                listNodes.add(node);
            }
        }
    }

    @Override
    protected void labelPropagation() {
        for (Node Oi : listNodes) {
            if (Oi.Y != null) {
                continue;
            }
            double f[] = network.createClassVector();
            for (HashMap<Node, Double> adjs : Oi.adjacent.values()) {
                for (Node Oj : adjs.keySet()) {
                    double weight = adjs.get(Oj);
                    for (int i = 0; i < f.length; i++) {
                        f[i] += Oj.lastF[i] * weight;
                    }
                }
            }
            double w = getDegree(Oi);
            for (int i = 0; i < f.length; i++) {
                f[i] /= w;
            }
            Oi.F = f;
        }
    }

    @Override
    protected double getDegree(Node obj) {
        double w = 0;
        for (HashMap<Node, Double> adjs : obj.adjacent.values()) {
            for (Double weight : adjs.values()) {
                w += weight;
            }
        }
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

        GFHF alg = new GFHF(network, json);
        alg.classifier();
        network.saveNetwork(json.getString("output_file"));
    }
}
