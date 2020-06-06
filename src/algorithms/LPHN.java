package algorithms;

import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import network.Node;
import network.Network;

/**
 * @author Brucce
 */
public class LPHN extends TransductiveClassifier {

    private String relation = "";

    public LPHN(Network network, JSONObject json) {
        super(network, json);
    }

    @Override
    protected void labelPropagation() {
        for (HashMap<String, Node> objects : network.layers.values()) {
            for (Node Oi : objects.values()) {
                if (Oi.Y != null) {
                    continue;
                }
                double f[] = network.createClassVector();
                for (String Oi_adjRelation : Oi.adjacent.keySet()) {
                    this.relation = Oi_adjRelation;
                    HashMap<Node, Double> adjRelation = Oi.adjacent.get(Oi_adjRelation);
                    for (Node Oj : adjRelation.keySet()) {
                        double weight = adjRelation.get(Oj);
                        weight = getDegreeRelation(Oi, Oj, weight);
                        for (int i = 0; i < f.length; i++) {
                            f[i] += Oj.lastF[i] * (weight);
                        }
                    }
                }
                double w = getDegree(Oi);
                if (Double.isNaN(w)) {
                    System.out.println("Error: " + Oi + "\t" + w);
                    System.exit(1);
                }
                for (int i = 0; i < f.length; i++) {
                    f[i] /= w;
                    if (Double.isNaN(f[i])) {
                        System.out.println("Error: " + Oi + "\t" + f[i] + "\t" + w);
                        System.exit(1);
                    }
                }
                Oi.F = f;
            }
        }
    }

    protected double getDegreeRelation(Node i, Node j, double aresta) {
        double aux = getDegree2(i) * getDegree2(j);
        double w = aresta / aux;
        if (aresta > aux) {
            System.out.println("Error-weak: getDegreeRelation(" + i + ", " + j + ", " + aresta + ") degree(i)*degree(j)= " + aux);
        }
        w += 1;
        return w;
    }

    protected double getDegree2(Node obj) {
        Double w = 0.0;
        HashMap<Node, Double> adjs = obj.adjacent.get(relation);
        for (Double weight : adjs.values()) {
            w += weight;
        }
        w = Math.sqrt(w);
        return w;
    }

    @Override
    protected double getDegree(Node obj) {
        Double w = 0.0;
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

        LPHN alg = new LPHN(network, json);
        alg.classifier();
        network.saveNetwork(json.getString("output_file"));
    }
}