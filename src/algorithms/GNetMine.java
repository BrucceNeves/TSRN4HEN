package algorithms;

import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import network.Node;
import network.Network;

/**
 * @author Brucce
 */
public final class GNetMine extends TransductiveClassifier {

    public final HashMap<String, Double> cacheRelation = new HashMap<>();
    private final double mi;
    private String relation;
    private final HashMap<String, Double> ImportanceRelation;

    public GNetMine(Network network, JSONObject json, HashMap<String, Double> ImportanceRelation) {
        super(network, json);
        mi = json.getDouble("mi");
        this.ImportanceRelation = ImportanceRelation;
        normalizeImportanceRelation();
    }

    public GNetMine(Network network, JSONObject json) {
        super(network, json);
        mi = json.getDouble("mi");
        this.ImportanceRelation = new HashMap<>();
        int total = network.relations_name.size();
        for (String r : network.relations_name) {
            ImportanceRelation.put(r, (1.0 / total));
        }
    }

    private void normalizeImportanceRelation() {
        double sum = 0;
        for (double v : ImportanceRelation.values()) {
            sum += v;
        }
        for (String r : ImportanceRelation.keySet()) {
            double v = ImportanceRelation.get(r);
            ImportanceRelation.put(r, v / sum);
        }
    }

    @Override
    protected void labelPropagation() {
        for (HashMap<String, Node> nodes : network.layers.values()) {
            for (Node Oi : nodes.values()) {
                double f[] = network.createClassVector();
                if (mi == 1.0 && Oi.Y != null) {
                    continue;
                }
                for (String Oi_adjRelacao : Oi.adjacent.keySet()) {
                    relation = Oi_adjRelacao;
                    HashMap<Node, Double> adjs = Oi.adjacent.get(relation);
                    for (Node Oj : adjs.keySet()) {
                        if (ImportanceRelation.get(relation) == null) {
                            System.out.println(relation);
                        }
                        double peso = getWeightRelation(Oi, Oj, adjs.get(Oj)) * ImportanceRelation.get(relation);
                        for (int k = 0; k < f.length; k++) {
                            f[k] += Oj.lastF[k] * peso;
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
    }

    protected double getWeightRelation(Node i, Node j, double edge) {
        return edge / (getDegree(i) * getDegree(j));
    }

    @Override
    protected double getDegree(Node obj) {
        double w = 0.0;
        HashMap<Node, Double> adjs = obj.adjacent.get(relation);
        for (Double weight : adjs.values()) {
            w += weight;
        }
        w = Math.sqrt(w);
        return w;
    }

    @Override
    public String toString() {
        return "GNetMine-" + ImportanceRelation.toString() + "-mi_" + mi;
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
        HashMap<String, Double> importanceRelation = new HashMap<>();
        JSONObject json_importance = json.getJSONObject("weight_relations");
        for (String key : json_importance.keySet()) {
            importanceRelation.put(key, json_importance.getDouble(key));
        }
        Network.setRelationsName(importanceRelation.keySet().toArray());
        Network network = new Network(layers, json.getString("labels"));

        GNetMine alg = importanceRelation.size() > 0 ? new GNetMine(network, json, importanceRelation) : new GNetMine(network, json);
        alg.classifier();
        network.saveNetwork(json.getString("output_file"));
    }
}
